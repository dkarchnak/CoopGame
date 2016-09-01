package cz.bcx.coopgame;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.*;

/**
 * Created by bcx on 9.5.16.
 */
public class Shader {
    public  static final String VERTEX_SHADER_EXT   = ".vs";
    public  static final String FRAGMENT_SHADER_EXT = ".fs";

    private static final String SHADERS_PATH        = "res/shaders/";

    private int     programId;
    private String  vertexShaderSource, fragmentShaderSource;

    public Shader(String fileName, VertexAttribute ... vertexAttributes) {
        this(loadShaderFromFile(SHADERS_PATH + fileName + VERTEX_SHADER_EXT), loadShaderFromFile(SHADERS_PATH + fileName + FRAGMENT_SHADER_EXT), vertexAttributes);
    }

    private Shader(String vertexShaderSource, String fragmentShaderSource, VertexAttribute ... vertexAttributes) {
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
