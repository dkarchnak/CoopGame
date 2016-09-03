package cz.bcx.coopgame;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.*;
import java.nio.FloatBuffer;
import java.util.HashMap;

/**
 * Created by bcx on 9.5.16.
 */
public class Shader {
    public  static final String VERTEX_SHADER_EXT      = ".vs";
    public  static final String FRAGMENT_SHADER_EXT    = ".fs";

    private static final String SHADERS_PATH           = "res/shaders/";

    private static final FloatBuffer matrixFloatBuffer = BufferUtils.createFloatBuffer(4*4);

    private int     programId;
    private String  vertexShaderSource, fragmentShaderSource;

    private HashMap<String, Integer> uniforms;

    //TODO - Add some sort of shader manager, which loads all shaders at once
    public Shader(String fileName, String[] uniforms, VertexAttribute[] vertexAttributes) {
        this(loadShaderFromFile(SHADERS_PATH + fileName + VERTEX_SHADER_EXT), loadShaderFromFile(SHADERS_PATH + fileName + FRAGMENT_SHADER_EXT), uniforms, vertexAttributes);
    }

    private Shader(String vertexShaderSource, String fragmentShaderSource, String[] uniforms, VertexAttribute[] vertexAttributes) {
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

        if(uniforms != null) {
            bindUniforms(uniforms);
        }
    }

    public static final String loadShaderFromFile(String fileName) {
        try {
            File file = new File(fileName);
            if(!file.exists()) throw new RuntimeException("Cannot load shader from file: " + fileName);

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();

            String line;
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            bufferedReader.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void bindAttributes(VertexAttribute[] vertexAttributes) {
        for(VertexAttribute vertexAttribute : vertexAttributes) {
            vertexAttribute.bindAttributeLocation(programId);
        }
    }

    private void bindUniforms(String[] uniforms) {
        this.uniforms = new HashMap<>();

        for(String uniform : uniforms) {
            int location = GL20.glGetUniformLocation(programId, uniform);

            if(location == -1) throw new RuntimeException("Uniform \"" + uniform + "\" , which you're trying to bind doesn't exist! ");

            this.uniforms.put(uniform, location);
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

    ///////////// BLUR_SHADER_UNIFORMS SET ////////////////
    public void setUniformValue1f(String uniformName, float value) {
        GL20.glUniform1f(this.uniforms.get(uniformName), value);
    }

    public void setUniformValue2f(String uniformName, float value, float value2) {
        GL20.glUniform2f(this.uniforms.get(uniformName), value, value2);
    }

    public void setUniformValueVec2f(String uniformName, Vector2f vector) {
        GL20.glUniform2f(this.uniforms.get(uniformName), vector.x, vector.y);
    }

    public void setUniformValue3f(String uniformName, float value, float value2, float value3) {
        GL20.glUniform3f(this.uniforms.get(uniformName), value, value2, value3);
    }

    public void setUniformValue3f(String uniformName, Vector3f vector) {
        GL20.glUniform3f(this.uniforms.get(uniformName), vector.x, vector.y, vector.z);
    }

    public void setUniformValueMat4f(String uniformName, Matrix4f matrix) {
        matrix.get(matrixFloatBuffer);
        GL20.glUniformMatrix4fv(this.uniforms.get(uniformName), false, matrixFloatBuffer);
    }

    public void setUniformValue1i(String uniformName, int value) {
        GL20.glUniform1i(this.uniforms.get(uniformName), value);
    }
}