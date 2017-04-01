package cz.bcx.coopgame.graphics;

import cz.bcx.coopgame.application.ResourceDestroyedException;
import cz.bcx.coopgame.util.Color;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

/**
 * Created by bcx on 5/14/16.
 */
public class FrameBufferObject {
    private static final Texture.TextureFilter DEFAULT_TEXTURE_MIN_FILTER = Texture.TextureFilter.Linear;
    private static final Texture.TextureFilter DEFAULT_TEXTURE_MAG_FILTER = Texture.TextureFilter.Linear;
    private static final Color                 DEFAULT_CLEAR_COLOR        = new Color(0.2f, 0.2f, 0.2f, 1.0f);

    private int fboId;
    private Texture colorTexture;

    private int width, height;
    private boolean destroyed = false;

    public FrameBufferObject(int width, int height) {
        this(width, height, DEFAULT_TEXTURE_MIN_FILTER, DEFAULT_TEXTURE_MAG_FILTER);
    }

    public FrameBufferObject(int width, int height, Texture.TextureFilter minFilter, Texture.TextureFilter magFilter) {
        fboId = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, fboId);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        this.width = width;
        this.height = height;

        //TODO Use Texture class
        int colorTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter.getFilter());
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter.getFilter());

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL30.glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexture, 0);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, 0);

        this.colorTexture = new Texture(colorTexture, width, height, 3);
    }

    public static void clearFrameBuffer() {
        clearFrameBuffer(DEFAULT_CLEAR_COLOR);
    }

    public static void clearFrameBuffer(Color color) {
        glClearColor(color.r, color.g, color.b, color.a);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public static void bindDefaultFrameBuffer() {
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bindFrameBuffer() {
        if(isDestroyed()) throw new ResourceDestroyedException("Frame Buffer Object has been destroyed!");
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, fboId);
    }

    public void unbindFrameBuffer() {
        if(isDestroyed()) throw new ResourceDestroyedException("Frame Buffer Object has been destroyed!");
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Texture getColorTexture() {
        if(isDestroyed()) throw new ResourceDestroyedException("Frame Buffer Object has been destroyed!");
        return colorTexture;
    }

    public int getWidth() {
        if(isDestroyed()) throw new ResourceDestroyedException("Frame Buffer Object has been destroyed!");
        return width;
    }

    public int getHeight() {
        if(isDestroyed()) throw new ResourceDestroyedException("Frame Buffer Object has been destroyed!");
        return height;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        GL30.glDeleteFramebuffers(fboId);
        this.colorTexture.destroy();
        destroyed = true;
    }
}