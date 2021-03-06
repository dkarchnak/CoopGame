package cz.bcx.coopgame.application;

import cz.bcx.coopgame.graphics.FrameBufferObject;
import cz.bcx.coopgame.graphics.StandardBatch;
import cz.bcx.coopgame.application.screen.ScreenManager;
import cz.bcx.coopgame.util.Color;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class Application {

/////////////////////////////////////////////////////////////
/////                  INPUT EVENTS                     /////
/////////////////////////////////////////////////////////////
    public enum KeyAction {
        NONE     (-1),
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
            return KeyAction.NONE;
        }
    }

    public enum KeyModifier {
        NONE              (0x0000),
        SHIFT             (GLFW.GLFW_MOD_SHIFT),
        CONTROL           (GLFW.GLFW_MOD_CONTROL),
        ALT               (GLFW.GLFW_MOD_ALT),
        SHIFT_CONTROL     (SHIFT.keyModifier | CONTROL.keyModifier),
        SHIFT_ALT         (SHIFT.keyModifier | ALT.keyModifier),
        SHIFT_CONTROL_ALT (SHIFT.keyModifier | CONTROL.keyModifier | ALT.keyModifier),
        CONTROL_ALT       (CONTROL.keyModifier | ALT.keyModifier);
        //Fuck super key! Who uses super key anyway?
        //Looking at you OSX users!
        //EDIT 1/21/2017 - I'm using super key now =D. I switched to i3WM.

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

        private KeyboardEvent() {}

        public void set(int key, KeyAction action, KeyModifier modifier) {
            this.key = key;
            this.action = action;
            this.modifier = modifier;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + ": [Key: " + key + ", KeyAction: " + action +
                    ((modifier != KeyModifier.NONE) ? (", Modifier: " + modifier) : "") + "]";
        }
    }

    public static class MouseEvent {
        public int       key;
        public KeyAction action;
        public int       x, y;

        private MouseEvent() {}

        public void set(int key, KeyAction keyAction, int x, int y) {
            this.key = key;
            this.action = keyAction;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + ": [Key: " + key + ", KeyAction: " + action +
                    ", x: " + x + ", y " + y + "]";
        }
    }

/////////////////////////////////////////////////////////////
/////                   Application                     /////
/////////////////////////////////////////////////////////////
    private static final String APPLICATION_TEXTURE_ATLAS_PATH = "res/atlas.bcx";

    private static final int APPLICATION_BATCH_MAX_ENTITIES = 16; //TODO - Tweak

    private final KeyboardEvent lastKeyboardEvent = new KeyboardEvent();
    private final MouseEvent    lastMouseEvent    = new MouseEvent();

    private StandardBatch applicationBatch;
    private ScreenManager screenManager;

    private TextureAtlasManager applicationAtlasManager;

    private int windowWidth, windowHeight;

    public Application(int windowWidth, int windowHeight) {
        this.windowWidth  = windowWidth;
        this.windowHeight = windowHeight;

        this.applicationBatch = new StandardBatch(APPLICATION_BATCH_MAX_ENTITIES);
        this.applicationBatch.setProjectionMatrix(new Matrix4f().setOrtho2D(0, windowWidth, 0, windowHeight));

        //TODO Proper loading...
        loadApplicationAssets();

        this.screenManager = new ScreenManager(this);
    }

    //TODO Resizing...
    public void onWindowResized(int width, int height) {
        this.windowWidth  = width;
        this.windowHeight = height;

        applicationBatch.setProjectionMatrix(new Matrix4f().setOrtho2D(0, width, 0, height));
        screenManager.onWindowResized(width, height);
    }

    public void handleKeyboardEvent(int key, int action, int mods) {
        lastKeyboardEvent.set(key, KeyAction.getKeyAction(action), KeyModifier.getModifier(mods));
        screenManager.handleKeyboardEvent(lastKeyboardEvent);
    }

    public void handleMouseEvent(int key, int action, int x, int y) {
        lastMouseEvent.set(key, KeyAction.getKeyAction(action), x, y);
        screenManager.handleMouseEvent(lastMouseEvent);
    }

    public void loadApplicationAssets() {
        this.applicationAtlasManager = new TextureAtlasManager(APPLICATION_TEXTURE_ATLAS_PATH);
    }

    public void update(float delta) {
        screenManager.update(delta);
    }

    public void draw() {
        screenManager.draw();

        //Draws screen frame buffer to the screen
        FrameBufferObject.clearFrameBuffer();
        applicationBatch.begin();
        applicationBatch.setColor(Color.WHITE);
        applicationBatch.draw(screenManager.getScreenManagerFrameBuffer().getColorTexture(), 0, 0, windowWidth, windowHeight);
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

    public TextureAtlasManager getApplicationAtlasManager() {
        return applicationAtlasManager;
    }

    public void destroy() {
        screenManager.destroy();
        applicationAtlasManager.destroy();
        applicationBatch.destroy();
        screenManager.destroy();
    }
}