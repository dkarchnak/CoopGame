package cz.bcx.coopgame;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Created by bcx on 9.5.16.
 */
public class Shader {
    private int     programId;
    private String  vertexShaderSource, fragmentShaderSource;

    public Shader(String vertexShaderSource, String fragmentShaderSource, VertexAttribute... vertexAttributes) {
        this.vertexShaderSource = vertexShaderSource;
        this.fragmentShaderSource = fragmentShaderSource;

        int vertexShader   = compileShader(vertexShaderSource,   GL20.GL_VERTEX_SHADER);
        int fragmentShader = compileShader(fragmentShaderSource, GL20.GL_FRAGMENT_SHADER);

        programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vertexShader);
        GL20.glAttachShader(programId, fragmentShader);

        bindAttributes(vertexAttributes);

        GL20.glLinkProgram(programId);

        if(GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) throw new RuntimeException("Cannot link shader: " + GL20.glGetShaderInfoLog(programId, 1024));

        GL20.glValidateProgram(programId);

        if(GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) throw new RuntimeException("Cannot validate shader: " + GL20.glGetShaderInfoLog(programId, 1024));
    }

    private void bindAttributes(VertexAttribute[] vertexAttributes) {
        for(VertexAttribute vertexAttribute : vertexAttributes) {
            vertexAttribute.bindAttributeLocation(programId);
        }
    }

    private int compileShader(String source, int shaderType) {
        int handle = GL20.glCreateShader(shaderType);

        if(handle == 0) throw new RuntimeException("Cannot create shader of type: " + shaderType);

        GL20.glShaderSource(handle, source);
        GL20.glCompileShader(handle);

        int shaderCompileStatus = GL20.glGetShaderi(handle, GL20.GL_COMPILE_STATUS);

        if(shaderCompileStatus == GL11.GL_FALSE) throw new RuntimeException("Cannot compile shader: " + GL20.glGetShaderInfoLog(handle, 1024));

        return handle;
    }

    public void use() {
        GL20.glUseProgram(programId);
    }

    public int getProgramId() {
        return programId;
    }

    public String getVertexShaderSource() {
        return vertexShaderSource;
    }

    public String getFragmentShaderSource() {
        return fragmentShaderSource;
    }
}
