package cz.bcx.coopgame.postprocess;

import cz.bcx.coopgame.FrameBufferObject;
import cz.bcx.coopgame.Shader;
import cz.bcx.coopgame.StandardBatch;
import cz.bcx.coopgame.Texture;
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

    private boolean increasing = false;
    private float   strength   = 1f;

    public GreyScalePostProcessEffect(float strength) {
        this.strength = MathUtil.clamp(strength, 0f, 1f);

        if(greyScaleShader == null) initializeShader();
    }

    private void initializeShader() {
        greyScaleShader = new Shader(GREY_SCALE_SHADER_NAME, UNIFORMS, StandardBatch.VERTEX_ATTRIBUTES);
    }

    public void update(float delta) {
        //TODO remove
        float value = (delta / 1000f) / 3f;

        if(increasing) strength += value;
        else strength -= value;

        if(strength <= 0) {
            increasing = true;
        }
        if(strength >= 1)
            increasing = false;
    }

    @Override
    public void apply(StandardBatch batch, Texture sourceTexture) {
        getResultFrameBuffer().bindFrameBuffer();
        batch.begin();
        batch.setShader(greyScaleShader);

        greyScaleShader.use();
        greyScaleShader.setUniformValue1f(UNIFORM_GREY_SCALE_STRENGTH, strength);

        batch.draw(sourceTexture, 0, 0, getResultFrameBuffer().getWidth(), getResultFrameBuffer().getHeight(), 0, 0, 1, 1);
        batch.setShader(null);
        batch.end();
        FrameBufferObject.bindDefaultFrameBuffer();
    }
}