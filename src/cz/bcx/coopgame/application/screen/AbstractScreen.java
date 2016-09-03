package cz.bcx.coopgame.application.screen;

import cz.bcx.coopgame.StandardBatch;
import cz.bcx.coopgame.application.Application;

/**
 * Created by BCX on 7/25/2016.
 */
public abstract class AbstractScreen {
    private final ScreenManager screenManager;

    private float screenAlpha = 1f;

    public AbstractScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    public void loadResources() {};
    public void destroyResources() {};

    //Screen life cycle methods
    public void onEntering() {} //Transition start
    public void onEnter() {}    //Transition finish

    public void onLeaving() {} //Transition start
    public void onLeave() {} //Transition finish

    public void onUpdate(float delta) {}

    public void onWindowResized(int width, long height) {}

    public void onDraw() {}

    //Screen input handling methods
    public void handleKeyboardEvent(Application.KeyboardEvent keyboardEvent) {}

    public void update(float delta) {
        onUpdate(delta);
    }

    public void draw() {
        onDraw();
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }

    public StandardBatch getScreenStandardBatch() {
        return screenManager.getScreenStandardBatch();
    }

    public void setScreenAlpha(float screenAlpha) {
        this.screenAlpha = screenAlpha;
    }

    public float getScreenAlpha() {
        return screenAlpha;
    }
}