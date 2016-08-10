package cz.bcx.coopgame.application.screen;

import cz.bcx.coopgame.graphics.FrameBufferObject;
import cz.bcx.coopgame.graphics.StandardBatch;
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
    private FrameBufferObject screenManagerFrameBuffer;

    private AbstractScreen currentScreen;
    private AbstractScreen nextScreen;

    private AbstractScreenTransition currentScreenTransition;
    private boolean changingScreens = false;

    public ScreenManager(Application application) {
        this.application = application;
        this.screenStandardBatch = new StandardBatch();
        this.screenStandardBatch.setProjectionMatrix(new Matrix4f().setOrtho2D(0, application.getWindowWidth(), 0, application.getWindowHeight()));
        this.screenManagerFrameBuffer = new FrameBufferObject(application.getWindowWidth(), application.getWindowHeight());

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
        //TODO resize FBOs
        this.screenStandardBatch.setProjectionMatrix(new Matrix4f().setOrtho2D(0, width, 0, height));
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
        if(isChangingScreens()) {
            nextScreen.draw();

            screenManagerFrameBuffer.bindFrameBuffer();
            FrameBufferObject.clearFrameBuffer();

            getApplicationBatch().begin();
            currentScreenTransition.draw(
                    getApplicationBatch(),
                    currentScreen.getScreenFrameBuffer().getColorTexture(),
                    nextScreen.getScreenFrameBuffer().getColorTexture()
            );
            getApplicationBatch().end();

            screenManagerFrameBuffer.unbindFrameBuffer();
        }
        else {
            currentScreen.draw();

            screenManagerFrameBuffer.bindFrameBuffer();
            FrameBufferObject.clearFrameBuffer();

            getApplicationBatch().begin();
            getApplicationBatch().draw(
                    currentScreen.getScreenFrameBuffer().getColorTexture(),
                    0, 0,
                    getWindowWidth(),
                    getWindowHeight(),
                    0, 0, 1, 1
            );
            getApplicationBatch().end();
            screenManagerFrameBuffer.unbindFrameBuffer();
        }
    }

    public StandardBatch getApplicationBatch() {
        return application.getApplicationBatch();
    }

    public StandardBatch getScreenStandardBatch() {
        return screenStandardBatch;
    }

    public FrameBufferObject getScreenManagerFrameBuffer() {
        return screenManagerFrameBuffer;
    }

    public int getWindowWidth() {
        return application.getWindowWidth();
    }

    public int getWindowHeight() {
        return application.getWindowHeight();
    }
}