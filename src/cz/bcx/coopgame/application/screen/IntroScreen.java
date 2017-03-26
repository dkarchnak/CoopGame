package cz.bcx.coopgame.application.screen;

import cz.bcx.coopgame.graphics.TextureRegion;
import cz.bcx.coopgame.application.Application;
import cz.bcx.coopgame.application.Log;
import cz.bcx.coopgame.application.screen.transition.FadeScreenTransition;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 * Created by BCX on 7/26/2016.
 */
public class IntroScreen extends AbstractScreen {
    private static final int TEXTURE_KEY = 1;
    private static final int TEXTURE_KEY_2 = 2;

    private TextureRegion region;
    private TextureRegion region2;

    private float x, y;
    private float velX = 0;
    private float velY = 0;

    private float moveVelocity = 200;
    private float size = 40;
    private float gravity = 980;
    private float jumpVel = 500;

    public IntroScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    public void loadResources() {
        region  = getApplicationTextureRegion(TEXTURE_KEY);
        region2 = getApplicationTextureRegion(TEXTURE_KEY_2);
    }

    @Override
    public void onUpdate(float delta) {
        //Camera's center (0, 0) is center of the screen.
        getScreenCamera().setCameraPositionByCenter(new Vector3f());

        velY -= gravity * (delta/1000f);

        x += velX * (delta/1000f);
        y += velY * (delta/1000f);

        if(y < 0) {
            y = 0;
            velY = 0;
        }

        getScreenCamera().setCameraPositionByCenter(new Vector3f(x + size/2, 0 + size/2, 0));
    }

    @Override
    public void onDraw() {
        getScreenStandardBatch().begin();
        getScreenStandardBatch().draw(
                region2,
                0,
                0,
                800,
                800
        );

        getScreenStandardBatch().draw(
                region,
                x,
                y,
                size,
                size
        );
        getScreenStandardBatch().end();
    }

    @Override
    public void handleKeyboardEvent(Application.KeyboardEvent keyboardEvent) {
        Log.debug(getClass(), keyboardEvent);

        if(keyboardEvent.key == GLFW.GLFW_KEY_A) {
            if(keyboardEvent.action == Application.KeyAction.PRESSED) {
                velX -= moveVelocity;
            }
            else if(keyboardEvent.action == Application.KeyAction.RELEASED) {
                velX += moveVelocity;
            }
        }
        else if(keyboardEvent.key == GLFW.GLFW_KEY_D) {
            if(keyboardEvent.action == Application.KeyAction.PRESSED) {
                velX += moveVelocity;
            }
            else if(keyboardEvent.action == Application.KeyAction.RELEASED) {
                velX -= moveVelocity;
            }
        }
        else if(keyboardEvent.key == GLFW.GLFW_KEY_SPACE && keyboardEvent.action == Application.KeyAction.PRESSED) {
            velY = jumpVel;
        }

        if((keyboardEvent.key == GLFW.GLFW_KEY_ENTER || keyboardEvent.key == GLFW.GLFW_KEY_KP_ENTER) && keyboardEvent.action == Application.KeyAction.PRESSED) {
            getScreenManager().changeScreen(IntroScreen2.class, new FadeScreenTransition(1000));
        }
    }
}