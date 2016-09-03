package cz.bcx.coopgame.application.screen;

import cz.bcx.coopgame.FrameBufferObject;
import cz.bcx.coopgame.StandardBatch;
import cz.bcx.coopgame.application.Application;
import cz.bcx.coopgame.application.screen.transition.AbstractScreenTransition;
import org.joml.Matrix4f;

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

            throw new RuntimeException("Cannot create a screen for class: " + screenClass);
        }
    }

    private static final Class<? extends AbstractScreen> INITIAL_SCREEN = IntroScreen.class;

    private final Application application;

    private StandardBatch screenStandardBatch;
    private FrameBufferObject screenFrameBuffer;

    private AbstractScreen currentScreen;
    private AbstractScreen nextScreen;

    private AbstractScreenTransition currentScreenTransition;
    private boolean changingScreens = false;

    public ScreenManager(Application application) {
        this.application = application;
        this.screenStandardBatch = new StandardBatch();
        this.screenStandardBatch.setProjectionMatrix(new Matrix4f().setOrtho2D(0, application.getWindowWidth(), 0, application.getWindowHeight()));
        this.screenFrameBuffer = new FrameBufferObject(application.getWindowWidth(), application.getWindowHeight());

        this.currentScreen = ScreenFactory.createScreen(INITIAL_SCREEN, this);
        this.currentScreen.loadResources();
    }

    public void handleKeyboardEvent(Application.KeyboardEvent keyboardEvent) {
        currentScreen.handleKeyboardEvent(keyboardEvent);
    }

    public void changeScreen(Class<? extends AbstractScreen> screen, AbstractScreenTransition screenTransition) {
        if(isChangingScreens()) return;
        changingScreens = true;

        nextScreen = ScreenFactory.createScreen(screen, this);
        nextScreen.loadResources();

        currentScreenTransition = screenTransition;
        currentScreenTransition.initialize(currentScreen, nextScreen);
    }

    public boolean isChangingScreens() {
        return changingScreens;
    }

    public void onWindowResized(int width, long height) {
        this.screenStandardBatch.setProjectionMatrix(new Matrix4f().setOrtho2D(0, width, 0, height));
        currentScreen.onWindowResized(width, height);
        if(nextScreen != null) nextScreen.onWindowResized(width, height);
    }

    public void update(float delta) {
        if(isChangingScreens()) {
            currentScreenTransition.update(delta);

            if(currentScreenTransition.isFinished()) {
                changingScreens = false;
                currentScreen.destroyResources();
                currentScreen = nextScreen;
                currentScreenTransition = null;
                nextScreen = null;
            }
        }
        else {
            currentScreen.update(delta);
        }
    }

    public void draw() {
        screenFrameBuffer.bindFrameBuffer();
        FrameBufferObject.clearFrameBuffer();

        if(isChangingScreens())
            currentScreenTransition.draw();
        else
            currentScreen.draw();

        screenFrameBuffer.unbindFrameBuffer();
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