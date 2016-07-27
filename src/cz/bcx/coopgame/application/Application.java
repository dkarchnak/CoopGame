package cz.bcx.coopgame.application;

import cz.bcx.coopgame.FrameBufferObject;
import cz.bcx.coopgame.StandardBatch;
import cz.bcx.coopgame.application.screen.ScreenManager;
import cz.bcx.coopgame.util.Color;
import org.lwjgl.glfw.GLFW;

public class Application {

/////////////////////////////////////////////////////////////
/////                  INPUT EVENTS                     /////
/////////////////////////////////////////////////////////////
    public enum KeyAction {
        PRESSED  (GLFW.GLFW_PRESS),
        RELEASED (GLFW.GLFW_RELEASE),
        REPEAT   (GLFW.GLFW_REPEAT);

        private int keyAction;

        KeyAction(int keyAction) {
            this.keyAction = keyAction;
        }

        public static KeyAction getKeyAction(int action) {
            for(KeyAction keyAction : KeyAction.values()) {
                if(keyAction.keyAction == action) return keyAction;
            }
            return KeyAction.PRESSED;
        }
    }

    public enum KeyModifier {
        NONE    (0x0000),
        SHIFT   (GLFW.GLFW_MOD_SHIFT),
        CONTROL (GLFW.GLFW_MOD_CONTROL),
        ALT     (GLFW.GLFW_MOD_ALT),
        SUPER   (GLFW.GLFW_MOD_SUPER);

        private int keyModifier;

        KeyModifier(int keyModifier) {
            this.keyModifier = keyModifier;
        }

        public static KeyModifier getModifier(int modifierValue) {
            for(KeyModifier modifier : KeyModifier.values()) {
                if(modifier.keyModifier == modifierValue) return modifier;
            }
            return NONE;
        }
    }

    public static class KeyboardEvent {
        public int          key;
        public KeyAction    action;
        public KeyModifier  modifier;

        public KeyboardEvent(int key, KeyAction action, KeyModifier modifier) {
            this.key = key;
            this.action = action;
            this.modifier = modifier;
        }

        @Override
        public String toString() {
            return "[" + getClass().getSimpleName() + "] Key: " + key + ", KeyAction: " + action + ((modifier != KeyModifier.NONE) ? (", Modifier: " + modifier) : "");
        }
    }

/////////////////////////////////////////////////////////////
/////                   Application                     /////
/////////////////////////////////////////////////////////////
    private static final int APPLICATION_BATCH_MAX_ENTITIES = 16; //TODO - Tweak

    private StandardBatch applicationBatch;
    private ScreenManager screenManager;

    private int windowWidth, windowHeight;

    public Application(int windowWidth, int windowHeight) {
        this.windowWidth  = windowWidth;
        this.windowHeight = windowHeight;

        this.applicationBatch = new StandardBatch(APPLICATION_BATCH_MAX_ENTITIES);
        this.screenManager = new ScreenManager(this);
    }

    public void onWindowResized(int width, int height) {
        this.windowWidth  = width;
        this.windowHeight = height;
    }

    public void handleKeyboardEvent(int key, int action, int mods) {
        KeyboardEvent keyboardEvent = new KeyboardEvent(key, KeyAction.getKeyAction(action), KeyModifier.getModifier(mods));
        //TODO - Input event polling (dont create instance of KeyboardEvent every damn time you fag!

        screenManager.handleKeyboardEvent(keyboardEvent);
    }

    public void update(float delta) {
        screenManager.update(delta);
    }

    public void draw() {
        //Draws screens to screen frame buffer
        screenManager.getScreenFrameBuffer().bindFrameBuffer();
        FrameBufferObject.clearFrameBuffer();
        screenManager.draw();
        screenManager.getScreenFrameBuffer().unbindFrameBuffer();

        //Draws screen frame buffer to the screen
        FrameBufferObject.clearFrameBuffer();
        applicationBatch.begin();
        applicationBatch.setColor(Color.WHITE);
        applicationBatch.draw(screenManager.getScreenFrameBuffer().getColorTexture(), 0, 0, windowWidth, windowHeight, 0, 0, 1, 1);
        applicationBatch.end();
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public StandardBatch getApplicationBatch() {
        return applicationBatch;
    }
}