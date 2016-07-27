package cz.bcx.coopgame.application.screen.transition;

import cz.bcx.coopgame.StandardBatch;
import cz.bcx.coopgame.application.screen.AbstractScreen;
import cz.bcx.coopgame.util.MathUtil;

/**
 * Created by BCX on 7/26/2016.
 */
public abstract class AbstractScreenTransition {
    private static final int DEFAULT_DURATION = 1000; //ms

    private StandardBatch  applicationBatch;
    private AbstractScreen previousScreen, nextScreen;

    private boolean initialized = false;
    private boolean started = false;

    private long startTime;
    private int duration;

    public AbstractScreenTransition() {
        this(DEFAULT_DURATION);
    }

    protected AbstractScreenTransition(int duration) {
        this.duration = duration;
    }

    public abstract void onDraw();
    public abstract void onUpdate(float percentDone);

    public void initialize(StandardBatch applicationBatch, AbstractScreen previousScreen, AbstractScreen nextScreen) {
        initialized = true;

        this.applicationBatch = applicationBatch;
        this.previousScreen = previousScreen;
        this.nextScreen = nextScreen;
    }

    public void update(float delta) {
        if(started) start();
        onUpdate(getPercentDone());
    }

    private void start() {
        if(!initialized) throw new RuntimeException("You need to initialize screens transition before starting it!");
        started = true;
        startTime = System.currentTimeMillis();
    }

    public StandardBatch getApplicationBatch() {
        return applicationBatch;
    }

    public AbstractScreen getPreviousScreen() {
        return previousScreen;
    }

    public AbstractScreen getNextScreen() {
        return nextScreen;
    }

    public float getPercentDone() {
        return MathUtil.clamp((startTime + System.currentTimeMillis()) / (float)(duration));
    }

    public int getDuration() {
        return duration;
    }
}