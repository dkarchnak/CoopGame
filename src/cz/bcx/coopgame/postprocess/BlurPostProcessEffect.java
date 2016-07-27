package cz.bcx.coopgame.postprocess;

import cz.bcx.coopgame.StandardBatch;
import cz.bcx.coopgame.FrameBufferObject;
import cz.bcx.coopgame.PostProcessEffect;
import cz.bcx.coopgame.Texture;

/**
 * Created by BCX on 6/29/16.
 *
 * Gaussian Blur done in two stages for performance reasons.
 * First draws horizontally blurred image into midStepFrameBuffer, then
 * takes horizontally blurred image and blurs it vertically as well.
 * Result is stored in resultFrameBuffer, which is obtained by calling getResultFrameBuffer method;
 */
public class BlurPostProcessEffect extends PostProcessEffect {
    //Gaussian matrix 5x5 TODO - Add support for various sized matrices
    private static final float[] GAUSSIAN_BLUR_MATRIX = new float[] {0.000003f, 0.000229f, 0.005977f, 0.06136f, 0.24477f, 0.382925f};

    //TODO - Actual blur!
    //Carries texture after first phase of blur
    private FrameBufferObject midStepFrameBuffer;

    //TODO - Add shader manager ???
//    private static HorizontalBlurShader horizontalBlurShader;
//    private static VerticalBlurShader verticalBlurShader;

    public BlurPostProcessEffect() {
        super();
    }

    public BlurPostProcessEffect(float scale) {
        super(scale);
    }

    @Override
    public void apply(StandardBatch batch, Texture sourceTexture) {
        batch.draw(sourceTexture, 0, 0, getResultFrameBuffer().getWidth(), getResultFrameBuffer().getHeight(), 0, 0, 1, 1);
    }
}
