package cz.bcx.coopgame.graphics;

import cz.bcx.coopgame.application.game.Camera;
import cz.bcx.coopgame.util.Color;
import cz.bcx.coopgame.util.MathUtil;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

/**
 * Created by bcx on 9.5.16.
 */
public class StandardBatch {
    private static final String            DEFAULT_SHADER_NAME  = "StandardBatchShader";

    private static final int               DEFAULT_MAX_ENTITIES_PER_CALL = 1024;

    protected static  final VertexAttribute   ATTRIBUTE_POSITION   = new VertexAttribute(0, "in_Position");
    protected static  final VertexAttribute   ATTRIBUTE_COLOR      = new VertexAttribute(1, "in_Color");
    protected static  final VertexAttribute   ATTRIBUTE_TEX_COORDS = new VertexAttribute(2, "in_TexCoords");

    public static  final VertexAttribute[]  VERTEX_ATTRIBUTES    = new VertexAttribute[] {
        ATTRIBUTE_POSITION,
        ATTRIBUTE_COLOR,
        ATTRIBUTE_TEX_COORDS
    };

    protected static  final String  UNIFORM_PROJECTION_MATRIX  = "u_ProjMatrix";
    protected static  final String  UNIFORM_VIEW_MATRIX        = "u_ViewMatrix";
    protected static  final String  UNIFORM_COLOR_TEXTURE_UNIT = "u_TexColor";

    protected static final String[] UNIFORMS = new String[] {
        UNIFORM_PROJECTION_MATRIX,
        UNIFORM_VIEW_MATRIX,
        UNIFORM_COLOR_TEXTURE_UNIT
    };

    private static final int    POSITION_SIZE         = 2; //2 floats per position
    private static final int    COLOR_SIZE            = 4; //4 floats per color
    private static final int    TEX_COORDS_SIZE       = 2; //2 floats per vertex

    private static final int    ENTITY_VERTICES_COUNT = 6;
    private static final int    ENTITY_SIZE           = ENTITY_VERTICES_COUNT * (POSITION_SIZE + COLOR_SIZE);

    private static final int   DRAWING_MODE  = GL11.GL_TRIANGLES;
    private static final Color DEFAULT_COLOR = Color.WHITE;

    /** FIELDS **/
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;

    //TODO Pass default shader to constructor as a parameter. Create shader manager.
    private Shader defaultShader;
    private Shader currentShader;

    private Texture currentColorTexture;

    private FloatBuffer buffer;
    private int entitiesToDraw = 0;

    private int vaoId = -1;
    private int vboId = -1;

    private Color color = DEFAULT_COLOR;

    private boolean drawing = false;

    public StandardBatch() {
        this(DEFAULT_MAX_ENTITIES_PER_CALL);
    }

    public StandardBatch(int maxEntitiesPerCall) {
        buffer = BufferUtils.createFloatBuffer(maxEntitiesPerCall * ENTITY_SIZE);

        vboId = GL15.glGenBuffers();

        defaultShader = new Shader(DEFAULT_SHADER_NAME, UNIFORMS, VERTEX_ATTRIBUTES);
        currentShader = defaultShader;

        //Default OGL projection matrix
        projectionMatrix = new Matrix4f().ortho2D(-1, 1, -1, 1);
        viewMatrix = new Matrix4f().identity();
    }


    public void setProjectionMatrix(Matrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    public void setViewMatrixByCamera(Camera camera) {
        this.viewMatrix.identity();
        this.viewMatrix.translate(-camera.getCameraPosition().x, -camera.getCameraPosition().y, -camera.getCameraPosition().z);
    }

    public void begin() {
        buffer.clear();
        entitiesToDraw = 0;
        drawing = true;
    }

    public void end() {
        flush();
        currentColorTexture = null;
        drawing = false;
    }

    private void prepareVAO() {
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);

        GL20.glVertexAttribPointer(0, POSITION_SIZE,   GL11.GL_FLOAT, false, (POSITION_SIZE + COLOR_SIZE + TEX_COORDS_SIZE) << 2, 0);
        GL20.glVertexAttribPointer(1, COLOR_SIZE,      GL11.GL_FLOAT, false, (POSITION_SIZE + COLOR_SIZE + TEX_COORDS_SIZE) << 2, POSITION_SIZE << 2);
        GL20.glVertexAttribPointer(2, TEX_COORDS_SIZE, GL11.GL_FLOAT, false, (POSITION_SIZE + COLOR_SIZE + TEX_COORDS_SIZE) << 2, (POSITION_SIZE + COLOR_SIZE) << 2);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    private void enableVertexAttributes() {
        for(VertexAttribute vertexAttribute : VERTEX_ATTRIBUTES) {
            vertexAttribute.enableVertexAttribute();
        }
    }

    private void disableVertexAttributes() {
        for(VertexAttribute vertexAttribute : VERTEX_ATTRIBUTES) {
            vertexAttribute.disableVertexAttribute();
        }
    }

    private void flush() {
        if(drawing && entitiesToDraw > 0) {
            buffer.flip();

            currentShader.use();

            currentShader.setUniformValueMat4f(UNIFORM_PROJECTION_MATRIX, projectionMatrix);
            currentShader.setUniformValueMat4f(UNIFORM_VIEW_MATRIX, viewMatrix);
            currentShader.setUniformValue1i(UNIFORM_COLOR_TEXTURE_UNIT, 0);

            if(vaoId == -1) prepareVAO();

            GL30.glBindVertexArray(vaoId);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);

            enableVertexAttributes();

            GL11.glDrawArrays(DRAWING_MODE, 0, ENTITY_VERTICES_COUNT * entitiesToDraw);

            disableVertexAttributes();

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL30.glBindVertexArray(0);

            entitiesToDraw = 0;
        }
    }

    public void draw(TextureRegion colorTextureRegion, float x, float y, float width, float height) {
        draw(
            colorTextureRegion.getTexture(),
            x,
            y,
            width,
            height,
            colorTextureRegion.getU1(),
            colorTextureRegion.getV1(),
            colorTextureRegion.getU2(),
            colorTextureRegion.getV2()
        );
    }

    public void draw(Texture colorTexture, float x, float y, float width, float height) {
        draw(colorTexture, x, y, width, height, 0.0f, 0.0f, 1.0f, 1.0f);
    }

    public void draw(Texture colorTexture, float x, float y, float width, float height, float u1, float v1, float u2, float v2) {
        if(!drawing) throw new IllegalStateException("You have to call begin() method before drawing!");

        if(!colorTexture.equals(currentColorTexture)) {
            flush();
            currentColorTexture = colorTexture;
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            currentColorTexture.bind();
        }

        float x2 = x + width;
        float y2 = y + height;

        buffer.put(x).put(y);
        buffer.put(color.r).put(color.g).put(color.b).put(color.a);
        buffer.put(u1).put(v1);

        buffer.put(x2).put(y);
        buffer.put(color.r).put(color.g).put(color.b).put(color.a);
        buffer.put(u2).put(v1);

        buffer.put(x2).put(y2);
        buffer.put(color.r).put(color.g).put(color.b).put(color.a);
        buffer.put(u2).put(v2);

        buffer.put(x).put(y);
        buffer.put(color.r).put(color.g).put(color.b).put(color.a);
        buffer.put(u1).put(v1);

        buffer.put(x2).put(y2);
        buffer.put(color.r).put(color.g).put(color.b).put(color.a);
        buffer.put(u2).put(v2);

        buffer.put(x).put(y2);
        buffer.put(color.r).put(color.g).put(color.b).put(color.a);
        buffer.put(u1).put(v2);

        entitiesToDraw++;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setAlpha(float alpha) {
        this.color.a = MathUtil.clamp(alpha);
    }

    public void setShader(Shader shader) {
        if(!currentShader.equals(shader) && drawing)
            flush();

        if(shader == null)
            currentShader = defaultShader;
        else
            currentShader = shader;
    }

    public Shader getCurrentShader() {
        return currentShader;
    }
}