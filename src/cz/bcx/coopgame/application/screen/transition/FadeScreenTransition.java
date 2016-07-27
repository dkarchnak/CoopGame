package cz.bcx.coopgame.application.screen.transition;

/**
 * Created by BCX on 7/26/2016.
 */
public class FadeScreenTransition extends AbstractScreenTransition {
    private float alpha = 1f;

    public FadeScreenTransition(int duration) {
        super(duration);
    }

    @Override
    public void onDraw() {
        if(getPercentDone() < 0.5) {
            getPreviousScreen().setScreenAlpha(alpha);
            getPreviousScreen().draw();
        }
        else {
            getNextScreen().setScreenAlpha(alpha);
            getNextScreen().draw();
        }
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