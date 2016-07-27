package cz.bcx.coopgame.application.screen;

import cz.bcx.coopgame.StandardBatch;
import cz.bcx.coopgame.application.Application;

/**
 * Created by BCX on 7/25/2016.
 */
public abstract class AbstractScreen {
    private final ScreenManager screenManager;

    public AbstractScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    public void loadResources() {};

    //Screen life cycle methods
    public void onEnter() {}
    public void onLeave() {}

    public void onUpdate(float delta) {}

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
}