package cz.bcx.coopgame.postprocess;

import cz.bcx.coopgame.graphics.FrameBufferObject;
import cz.bcx.coopgame.graphics.Shader;
import cz.bcx.coopgame.graphics.StandardBatch;
import cz.bcx.coopgame.graphics.Texture;
import cz.bcx.coopgame.util.MathUtil;

/**
 * Created by bcx on 8/31/16.
 */
public class GreyScalePostProcessEffect extends PostProcessEffect {
    private static final String  GREY_SCALE_SHADER_NAME      = "StandardBatchGreyScaleShader";

    public  static final String  UNIFORM_GREY_SCALE_STRENGTH = "greyStrength";
    public  static final String  UNIFORM_PROJECTION_MATRIX   = "u_ProjMatrix";
    public  static final String  UNIFORM_COLOR_TEXTURE_UNIT  = "u_TexColor";

    public static final String[] UNIFORMS = new String[] {
            UNIFORM_PROJECTION_MATRIX,
            UNIFORM_COLOR_TEXTURE_UNIT,
            UNIFORM_GREY_SCALE_STRENGTH
    };

    //TODO - Create GreyScaleShader class extending Shader
    private static Shader greyScaleShader;

    private float   strength       = 1f;
    private boolean updateUniforms = true;

    public GreyScalePostProcessEffect(float strength) {
        setStrength(strength);

        if(greyScaleShader == null) initializeShader();
    }

    public void setStrength(float value) {
        this.strength = MathUtil.clamp(value, 0f, 1f);
        updateUniforms = true;
    }

    private void initializeShader() {
        greyScaleShader = new Shader(GREY_SCALE_SHADER_NAME, UNIFORMS, StandardBatch.VERTEX_ATTRIBUTES);
    }

    @Override
    public void apply(StandardBatch batch, Texture sourceTexture) {
        if(updateUniforms) {
            greyScaleShader.use();
            greyScaleShader.setUniformValue1f(UNIFORM_GREY_SCALE_STRENGTH, strength);
            updateUniforms = false;
        }

        getResultFrameBuffer().bindFrameBuffer();
        FrameBufferObject.clearFrameBuffer();

        batch.begin();
        batch.setShader(greyScaleShader);
        batch.draw(sourceTexture, 0, 0, getResultFrameBuffer().getWidth(), getResultFrameBuffer().getHeight(), 0, 0, 1, 1);
        batch.setShader(null);
        batch.end();
        FrameBufferObject.bindDefaultFrameBuffer();
    }
}