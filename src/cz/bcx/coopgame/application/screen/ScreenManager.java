package cz.bcx.coopgame.application.screen;

import cz.bcx.coopgame.FrameBufferObject;
import cz.bcx.coopgame.StandardBatch;
import cz.bcx.coopgame.application.Application;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by BCX on 7/25/2016.
 */
public class ScreenManager {
    public static class ScreenFactory {
        public static AbstractScreen createScreen(Class<? extends AbstractScreen> screenClass, ScreenManager screenManager) {
            try {
                return screenClass.getConstructor(ScreenManager.class).newInstance(screenManager);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            //TODO
            return null;
        }
    }

    private static final Class<? extends AbstractScreen> INITIAL_SCREEN = IntroScreen.class;

    private final Application application;

    private StandardBatch screenStandardBatch;
    private FrameBufferObject screenFrameBuffer;

    private AbstractScreen currentScreen;

    public ScreenManager(Application application) {
        this.application = application;
        this.screenStandardBatch = new StandardBatch();
        this.screenFrameBuffer = new FrameBufferObject(application.getWindowWidth(), application.getWindowHeight());

        this.currentScreen = ScreenFactory.createScreen(INITIAL_SCREEN, this);
        this.currentScreen.loadResources();
    }

    public void handleKeyboardEvent(Application.KeyboardEvent keyboardEvent) {
        this.currentScreen.handleKeyboardEvent(keyboardEvent);
    }

    public void update(float delta) {
        this.currentScreen.update(delta);
    }

    public void draw() {
        this.currentScreen.draw();
    }

    public StandardBatch getScreenStandardBatch() {
        return screenStandardBatch;
    }

    public FrameBufferObject getScreenFrameBuffer() {
        return screenFrameBuffer;
    }

    public int getWindowWidth() {
        return application.getWindowWidth();
    }

    public int getWindowHeight() {
        return application.getWindowHeight();
    }
}