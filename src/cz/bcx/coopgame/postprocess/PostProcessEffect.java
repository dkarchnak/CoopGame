package cz.bcx.coopgame.postprocess;

import com.sun.istack.internal.NotNull;
import cz.bcx.coopgame.FrameBufferObject;
import cz.bcx.coopgame.StandardBatch;
import cz.bcx.coopgame.Texture;
import cz.bcx.coopgame.TextureRegion;
import cz.bcx.coopgame.application.Main;

/**
 * Created by BCX on 6/29/16.
 */
public abstract class PostProcessEffect {
    private FrameBufferObject resultBufferObject;
    private float scale;

    public PostProcessEffect() {
        this(1f);
    }

    public PostProcessEffect(float scale) {
        resultBufferObject = new FrameBufferObject((int) (Main.WIDTH * scale), (int) (Main.HEIGHT * scale));
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