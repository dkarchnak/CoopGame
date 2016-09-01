package cz.bcx.coopgame.postprocess;

import com.sun.istack.internal.NotNull;
import cz.bcx.coopgame.FrameBufferObject;
import cz.bcx.coopgame.Shader;
import cz.bcx.coopgame.StandardBatch;
import cz.bcx.coopgame.Texture;
import cz.bcx.coopgame.util.MathUtil;
import org.lwjgl.opengl.GL20;

/**
 * Created by bcx on 8/31/16.
 */
public class GreyScalePostProcessEffect extends PostProcessEffect {
    private static final String GREY_SCALE_SHADER_NAME = "StandardBatchGreyScaleShader";

    private static Shader blackAndWhiteShader;

    private float strength = 1f;
    private boolean increasing = false;

    public GreyScalePostProcessEffect(float strength) {
        this.strength = MathUtil.clamp(strength, 0f, 1f);

        if(blackAndWhiteShader == null) initializeShader();
    }

    private void initializeShader() {
        blackAndWhiteShader = new Shader(GREY_SCALE_SHADER_NAME, StandardBatch.VERTEX_ATTRIBUTES);
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
    public void apply(@NotNull StandardBatch batch, @NotNull Texture sourceTexture) {
        getResultFrameBuffer().bindFrameBuffer();
        batch.begin();
        batch.setShader(blackAndWhiteShader);

        //TODO - Add support for passing uniforms easily
        blackAndWhiteShader.use();
        int loc = GL20.glGetUniformLocation(blackAndWhiteShader.getProgramId(), "greyStrength");
        GL20.glUniform1f(loc, strength);

        batch.draw(sourceTexture, 0, 0, getResultFrameBuffer().getWidth(), getResultFrameBuffer().getHeight(), 0, 0, 1, 1);
        batch.setShader(null);
        batch.end();
        FrameBufferObject.bindDefaultFrameBuffer();
    }
}
