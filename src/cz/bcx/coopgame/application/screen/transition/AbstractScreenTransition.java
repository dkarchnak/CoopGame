package cz.bcx.coopgame.application.screen.transition;

import cz.bcx.coopgame.graphics.StandardBatch;
import cz.bcx.coopgame.graphics.Texture;
import cz.bcx.coopgame.application.screen.AbstractScreen;
import cz.bcx.coopgame.util.MathUtil;

/**
 * Created by BCX on 7/26/2016.
 */
public abstract class AbstractScreenTransition {
    private static final int DEFAULT_DURATION = 1000; //ms

    private AbstractScreen previousScreen, nextScreen;

    private boolean initialized = false;
    private boolean started = false;
    private boolean finished = false;

    private long startTime;
    private int duration;

    public AbstractScreenTransition() {
        this(DEFAULT_DURATION);
    }

    protected AbstractScreenTransition(int duration) {
        this.duration = duration;
    }

    protected abstract void onDraw(StandardBatch batch, Texture currentScreenTexture, Texture nextScreenTexture);
    protected abstract void onUpdate(float delta);

    public void initialize(AbstractScreen previousScreen, AbstractScreen nextScreen) {
        initialized = true;

        this.previousScreen = previousScreen;
        this.nextScreen = nextScreen;
    }

    public void update(float delta) {
        if(!started) start();

        if(getPercentDone() >= 1 && !finished) finish();
        else onUpdate(getPercentDone());
    }

    public void draw(StandardBatch batch, Texture currentScreenTexture, Texture nextScreenTexure) {
        onDraw(batch, currentScreenTexture, nextScreenTexure);
    }

    private void start() {
        if(!initialized) throw new RuntimeException("You need to initialize screens transition before starting it!");
        started = true;
        startTime = System.currentTimeMillis();

        //TODO Let screen manager handle this
        previousScreen.onLeaving();
        nextScreen.onEntering();
    }

    public void finish() {
        finished = true;

        //TODO Let screen manager handle this
        previousScreen.onLeave();
        nextScreen.onEnter();
    }

    public AbstractScreen getPreviousScreen() {
        return previousScreen;
    }

    public AbstractScreen getNextScreen() {
        return nextScreen;
    }

    public float getPercentDone() {
        return MathUtil.clamp((System.currentTimeMillis() - startTime) / (float)(duration));
    }

    public boolean isFinished() {
        return finished;
    }

    public int getDuration() {
        return duration;
    }
}