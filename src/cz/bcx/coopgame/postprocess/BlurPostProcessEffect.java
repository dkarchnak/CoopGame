package cz.bcx.coopgame.postprocess;

import cz.bcx.coopgame.Shader;
import cz.bcx.coopgame.StandardBatch;
import cz.bcx.coopgame.FrameBufferObject;
import cz.bcx.coopgame.Texture;

/**
 * Created by BCX on 6/29/16.
 *
 * First draws horizontally blurred image into midStepFrameBuffer, then
 * takes horizontally blurred image and blurs it vertically as well.
 * Result is stored in resultFrameBuffer, which is obtained by calling getResultFrameBuffer method;
 */
public class BlurPostProcessEffect extends PostProcessEffect {
    private static final int   DEFAULT_BLUR_MATRIX_SIZE   = 3;
    private static final float DEFAULT_FRAME_BUFFER_SCALE = 0.5f;

    public  static final String  UNIFORM_PROJECTION_MATRIX   = "u_ProjMatrix";
    public  static final String  UNIFORM_COLOR_TEXTURE_UNIT  = "u_TexColor";
    public  static final String  UNIFORM_BLUR_MATRIX_SIZE    = "u_BlurMatrixSize";
    public  static final String  UNIFORM_PIXEL_SIZE          = "u_PixelSize";

    public static final String[] BLUR_SHADER_UNIFORMS = new String[] {
            UNIFORM_PROJECTION_MATRIX,
            UNIFORM_COLOR_TEXTURE_UNIT,
            UNIFORM_BLUR_MATRIX_SIZE,
            UNIFORM_PIXEL_SIZE,
    };

    private static Shader horizontalBlurShader;
    private static Shader verticalBlurShader;

    //Carries texture after first phase of blur
    private FrameBufferObject midStepFrameBuffer;

    private int     blurMatrixSize;
    private boolean updateUniforms = true;

    public BlurPostProcessEffect() {
        this(DEFAULT_BLUR_MATRIX_SIZE);
    }

    public BlurPostProcessEffect(int blurMatrixSize) {
        super(DEFAULT_FRAME_BUFFER_SCALE);
        this.midStepFrameBuffer = new FrameBufferObject(getResultFrameBuffer().getWidth(), getResultFrameBuffer().getHeight());
        this.blurMatrixSize = blurMatrixSize;

        if(horizontalBlurShader == null || verticalBlurShader == null) initializeShaders();
    }

    private void initializeShaders() {
        if(horizontalBlurShader == null)
            horizontalBlurShader = new Shader("StandardBatchHorizontalBlurShader", BLUR_SHADER_UNIFORMS, StandardBatch.VERTEX_ATTRIBUTES);

        if(verticalBlurShader == null)
            verticalBlurShader = new Shader("StandardBatchVerticalBlurShader", BLUR_SHADER_UNIFORMS, StandardBatch.VERTEX_ATTRIBUTES);
    }

    @Override
    public void apply(StandardBatch batch, Texture sourceTexture) {
        if (updateUniforms) {
            horizontalBlurShader.use();
            horizontalBlurShader.setUniformValue1i(UNIFORM_BLUR_MATRIX_SIZE, blurMatrixSize);
            horizontalBlurShader.setUniformValue1f(UNIFORM_PIXEL_SIZE, 1f / sourceTexture.getWidth());

            verticalBlurShader.use();
            verticalBlurShader.setUniformValue1i(UNIFORM_BLUR_MATRIX_SIZE, blurMatrixSize);
            verticalBlurShader.setUniformValue1f(UNIFORM_PIXEL_SIZE, 1f / sourceTexture.getHeight());

            updateUniforms = false;
        }

        midStepFrameBuffer.bindFrameBuffer();
        FrameBufferObject.clearFrameBuffer();

        batch.begin();
        batch.setShader(horizontalBlurShader);
        batch.draw(sourceTexture, 0, 0, getResultFrameBuffer().getWidth(), getResultFrameBuffer().getHeight(), 0, 0, 1, 1);
        batch.setShader(null);
        batch.end();

        getResultFrameBuffer().bindFrameBuffer();
        FrameBufferObject.clearFrameBuffer();

        batch.begin();
        batch.setShader(verticalBlurShader);
        batch.draw(sourceTexture, 0, 0, getResultFrameBuffer().getWidth(), getResultFrameBuffer().getHeight(), 0, 0, 1, 1);
        batch.setShader(null);
        batch.end();

        FrameBufferObject.bindDefaultFrameBuffer();
    }
}
