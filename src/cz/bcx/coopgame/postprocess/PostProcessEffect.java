package cz.bcx.coopgame.postprocess;

import com.sun.istack.internal.NotNull;
import cz.bcx.coopgame.graphics.FrameBufferObject;
import cz.bcx.coopgame.graphics.StandardBatch;
import cz.bcx.coopgame.graphics.Texture;
import cz.bcx.coopgame.graphics.TextureRegion;
import cz.bcx.coopgame.application.Main;

/**
 * Created by BCX on 6/29/16.
 */
public abstract class PostProcessEffect {
    private static final Texture.TextureFiltering DEFAULT_TEXTURE_FILTERING = Texture.TextureFiltering.LinearLinear;

    private FrameBufferObject resultBufferObject;
    private float scale;

    public PostProcessEffect() {
        this(1f);
    }

    PostProcessEffect(float scale) {
        this(scale, DEFAULT_TEXTURE_FILTERING);

    }
    public PostProcessEffect(float scale, Texture.TextureFiltering filtering) {
        resultBufferObject = new FrameBufferObject((int) (Main.WIDTH * scale), (int) (Main.HEIGHT * scale), filtering);
        this.scale = scale;
    }

    protected FrameBufferObject getResultFrameBuffer() {
        return resultBufferObject;
    }

    public Texture getResultTexture() {
        return resultBufferObject.getColorTexture();
    }

    public TextureRegion getResultTextureRegion() {
        return new TextureRegion(resultBufferObject.getColorTexture(), 0f, 0f, 1f, 1f);
    }

    public float getScale() {
        return scale;
    }

    public abstract void apply(@NotNull StandardBatch batch, @NotNull Texture sourceTexture);
}