package cz.bcx.coopgame;

import cz.bcx.coopgame.util.Color;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

/**
 * Created by bcx on 5/14/16.
 */
public class FrameBufferObject {
    private static final Texture.TextureFiltering DEFAULT_TEXTURE_FILTERING = Texture.TextureFiltering.LinearLinear;
    private static final Color                    DEFAULT_CLEAR_COLOR       = new Color(0.2f, 0.2f, 0.2f, 1.0f);

    private int fboId;
    private Texture colorTexture;

    private int width, height;
    private boolean destroyed = false;

    public FrameBufferObject(int width, int height) {
        this(width, height, DEFAULT_TEXTURE_FILTERING);
    }

    public FrameBufferObject(int width, int height, Texture.TextureFiltering filtering) {
        fboId = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, fboId);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        this.width = width;
        this.height = height;

        //TODO Use Texture class
        int colorTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filtering.getMinFilter());
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filtering.getMagFilter());

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0 ,GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL30.glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexture, 0);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, 0);

        this.colorTexture = new Texture(colorTexture, width, height, 4);
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
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, fboId);
    }

    public void unbindFrameBuffer() {
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Texture getColorTexture() {
        return colorTexture;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
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