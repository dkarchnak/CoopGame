package cz.bcx.coopgame.application.screen.transition;

import cz.bcx.coopgame.util.Color;

/**
 * Created by BCX on 7/26/2016.
 */
public class FadeScreenTransition extends AbstractScreenTransition {
    private float alpha = 1f;

    protected FadeScreenTransition(int duration) {
        super(duration);
    }

    @Override
    public void onDraw() {
        getApplicationBatch().setColor(Color.WHITE);
        getApplicationBatch().setAlpha(alpha);

        if(getPercentDone() < 0.5) getPreviousScreen().draw();
        else getNextScreen().draw();
    }

    @Override
    public void onUpdate(float percentDone) {
        if(percentDone < 0.5f) alpha = (0.5f - percentDone) * 2;
        else alpha = percentDone - 0.5f * 2;
    }
}
