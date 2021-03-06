package cz.bcx.coopgame.application.screen;

import cz.bcx.coopgame.application.game.Camera;
import cz.bcx.coopgame.graphics.FrameBufferObject;
import cz.bcx.coopgame.graphics.StandardBatch;
import cz.bcx.coopgame.application.Application;
import cz.bcx.coopgame.graphics.TextureRegion;

/**
 * Created by BCX on 7/25/2016.
 */
public abstract class AbstractScreen {
    private final ScreenManager screenManager;

    private FrameBufferObject screenFrameBuffer;
    private Camera screenCamera;

    public AbstractScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        this.screenFrameBuffer = new FrameBufferObject(screenManager.getWindowWidth(), screenManager.getWindowHeight());
        this.screenCamera = new Camera(screenManager.getWindowWidth(), screenManager.getWindowHeight());
    }

    public void loadResources() {};
    public void destroyResources() {};

    //Screen life cycle methods
    public void onEntering() {} //Transition start
    public void onEnter() {}    //Transition finish

    public void onLeaving() {} //Transition start
    public void onLeave() {} //Transition finish

    protected void onUpdate(float delta) {}

    protected void onWindowResized(int width, long height) {}

    protected void onDraw() {}

    //Screen input handling methods
    public void handleKeyboardEvent(Application.KeyboardEvent keyboardEvent) {}

    public void update(float delta) {
        onUpdate(delta);
    }

    public void windowResized(int width, int height) {
        screenCamera.setCameraDimensions(width, height);
        onWindowResized(width, height);
    }

    public void draw() {
        getScreenStandardBatch().setViewMatrixByCamera(screenCamera);

        screenFrameBuffer.bindFrameBuffer();
        FrameBufferObject.clearFrameBuffer();
        onDraw();
        screenFrameBuffer.unbindFrameBuffer();
    }

    public TextureRegion getApplicationTextureRegion(int key) {
        return screenManager.getApplicationAtlasManager().getTextureRegion(key);
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }

    public FrameBufferObject getScreenFrameBuffer() {
        return screenFrameBuffer;
    }

    public StandardBatch getScreenStandardBatch() {
        return screenManager.getScreenStandardBatch();
    }

    public Camera getScreenCamera() {
        return screenCamera;
    }

    public void destroy() {
        screenFrameBuffer.destroy();
        destroyResources();
    }
}