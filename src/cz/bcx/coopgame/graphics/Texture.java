package cz.bcx.coopgame.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Created by bcx on 5/12/16.
 */
public class Texture {
    private int textureId;

    private int width, height, components;
    private boolean destroyed = false;

    public Texture(String file) {
        textureId = GL11.glGenTextures();
        createTextureFromFile(file);
    }

    public Texture(int textureId, int width, int height, int components) {
        this.textureId = textureId;
        this.width = width;
        this.height = height;
        this.components = components;
    }

    private void createTextureFromFile(String file) {
        IntBuffer width  = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer comps  = BufferUtils.createIntBuffer(1);

        STBImage.stbi_set_flip_vertically_on_load(1);
        ByteBuffer image = STBImage.stbi_load(file, width, height, comps, 0);
        if(image == null) throw new RuntimeException("Couldn't load texture from file: " + file + "! " + STBImage.stbi_failure_reason());

        this.width = width.get();
        this.height = height.get();
        this.components = comps.get();

        //TODO - Refactor texture format and components field
        int format = components == 3 ? GL11.GL_RGB : GL11.GL_RGBA;

        bind();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format, this.width, this.height, 0, format, GL11.GL_UNSIGNED_BYTE, image);
        unbind();
    }

    public void bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    }

    public void unbind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void destroy() {
        GL11.glDeleteTextures(getTextureId());
        destroyed = true;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Texture && textureId != -1 && textureId == ((Texture) obj).getTextureId();
    }

    public int getTextureId() {
        return textureId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getComponents() {
        return components;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}