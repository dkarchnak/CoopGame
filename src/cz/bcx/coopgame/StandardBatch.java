package cz.bcx.coopgame;

import cz.bcx.coopgame.application.Main;
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
 * TODO: Make Batch as an interface or abstract class with methods like flush, begin, end etc...
 */
public class StandardBatch {
    private static final String DEFAULT_VERTEX_SHADER =
            "#version 130 \n" +
            "in vec2 in_Position; \n" +
            "in vec4 in_Color; \n" +
            "in vec2 in_TexCoords; \n\n" +

            "uniform mat4 u_ProjMatrix; \n" +
            "uniform sampler2D u_TexColor; \n" + //TODO texture id or something

            "out vec2 v_FragPosition; \n" +
            "out vec4 v_Color; \n" +
            "out vec2 v_TexCoords; \n\n" +

            "void main() { \n" +
            "   v_Color = in_Color; \n" +
            "   v_TexCoords = in_TexCoords;\n" +
            "   v_FragPosition = in_Position; \n " +
            "   gl_Position = u_ProjMatrix * vec4(in_Position, 0, 1); \n" +
            "}";

    private static final String DEFAULT_FRAGMENT_SHADER =
            "#version 130 \n" +
            "precision highp float; \n" +

            "uniform mat4 u_ProjMatrix; \n" +
            "uniform sampler2D u_TexColor; \n" +

            "in vec2 v_FragPosition; \n" +
            "in vec4 v_Color; \n" +
            "in vec2 v_TexCoords; \n" +
            "out vec4 out_Color; \n\n" +


            "void main() { \n" +
            "   vec4 texColor = texture2D(u_TexColor, v_TexCoords.xy); \n" +
            "   out_Color = v_Color * texColor; \n" +
            "}";

    private static final int               DEFAULT_MAX_ENTITIES_PER_CALL = 1024;

    private static final VertexAttribute   ATTRIBUTE_POSITION   = new VertexAttribute(0, "in_Position");
    private static final VertexAttribute   ATTRIBUTE_COLOR      = new VertexAttribute(1, "in_Color");
    private static final VertexAttribute   ATTRIBUTE_TEX_COORDS = new VertexAttribute(2, "in_TexCoords");

    private static final VertexAttribute[] VERTEX_ATTRIBUTES    = new VertexAttribute[] {
        ATTRIBUTE_POSITION,
        ATTRIBUTE_COLOR,
        ATTRIBUTE_TEX_COORDS
    };

    private static final int    POSITION_SIZE         = 2; //2 floats per position
    private static final int    COLOR_SIZE            = 4; //4 floats per color
    private static final int    TEX_COORDS_SIZE       = 2; //2 floats per vertex

    private static final int    ENTITY_VERTICES_COUNT = 6;
    private static final int    ENTITY_SIZE           = ENTITY_VERTICES_COUNT * (POSITION_SIZE + COLOR_SIZE);

    private static final int    DRAWING_MODE  = GL11.GL_TRIANGLES;
    private static final Color DEFAULT_COLOR = Color.WHITE;

    /** FIELDS **/
    private Matrix4f projectionMatrix;
    private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4*4);

    private static Shader defaultShader;
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

        projectionMatrix = new Matrix4f().setOrtho2D(0, Main.WIDTH, 0, Main.HEIGHT);
        projectionMatrix.get(matrixBuffer);

        vboId = GL15.glGenBuffers();

        defaultShader = new Shader(DEFAULT_VERTEX_SHADER, DEFAULT_FRAGMENT_SHADER, VERTEX_ATTRIBUTES);
        currentShader = defaultShader;
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
            int projectionMatrixLocation = GL20.glGetUniformLocation(currentShader.getProgramId(), "u_ProjMatrix");
            GL20.glUniformMatrix4fv(projectionMatrixLocation, false, matrixBuffer);

            int colorTextureLocation = GL20.glGetUniformLocation(currentShader.getProgramId(), "u_TexColor");
            GL20.glUniform1i(colorTextureLocation, 0); //Uses only one texture

            if(vaoId == -1)
                prepareVAO();

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