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

    private TextureRegion region;

    public IntroScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    public void loadResources() {
        region = getApplicationTextureRegion(TEXTURE_KEY);

        //Camera's center (0, 0) is center of the screen.
        getScreenCamera().setCameraPositionByCenter(new Vector3f());
    }

    @Override
    public void onDraw() {
        getScreenStandardBatch().begin();
        getScreenStandardBatch().draw(
                region,
                -200,
                -200,
                400,
                400
        );
        getScreenStandardBatch().end();
    }

    @Override
    public void handleKeyboardEvent(Application.KeyboardEvent keyboardEvent) {
        if(keyboardEvent.action == Application.KeyAction.PRESSED) getScreenManager().changeScreen(IntroScreen2.class, new FadeScreenTransition(1000));
        Log.debug(getClass(), keyboardEvent);
    }
}