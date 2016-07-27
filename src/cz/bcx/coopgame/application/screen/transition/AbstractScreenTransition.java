package cz.bcx.coopgame.application.screen.transition;

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

    protected abstract void onDraw();
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

    public void draw() {
        onDraw();
    }

    private void start() {
        if(!initialized) throw new RuntimeException("You need to initialize screens transition before starting it!");
        started = true;
        startTime = System.currentTimeMillis();

        previousScreen.onLeaving();
        nextScreen.onEntering();
    }

    public void finish() {
        finished = true;

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