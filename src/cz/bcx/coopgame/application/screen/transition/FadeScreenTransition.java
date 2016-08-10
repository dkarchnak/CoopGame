package cz.bcx.coopgame.application.screen.transition;

import cz.bcx.coopgame.graphics.StandardBatch;
import cz.bcx.coopgame.graphics.Texture;
import cz.bcx.coopgame.application.Main;

/**
 * Created by BCX on 7/26/2016.
 */
public class FadeScreenTransition extends AbstractScreenTransition {
    private float alpha = 1f;

    public FadeScreenTransition(int duration) {
        super(duration);
    }

    @Override
    public void onDraw(StandardBatch batch, Texture currentScreenTexture, Texture nextScreenTexture) {
        batch.setAlpha(alpha);

        if(getPercentDone() < 0.5)
            batch.draw(currentScreenTexture, 0, 0, Main.WIDTH, Main.HEIGHT, 0, 0, 1, 1);
        else
            batch.draw(nextScreenTexture, 0, 0, Main.WIDTH, Main.HEIGHT, 0, 0, 1, 1);
    }

    @Override
    public void onUpdate(float delta) {
        float percentDone = getPercentDone();
        if(percentDone < 0.5f) alpha = (0.5f - percentDone) * 2;
        else alpha = (percentDone - 0.5f) * 2;

        getPreviousScreen().update(delta);
        getNextScreen().update(delta);
    }
}