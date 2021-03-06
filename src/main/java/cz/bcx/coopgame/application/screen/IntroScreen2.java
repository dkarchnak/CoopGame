package cz.bcx.coopgame.application.screen;

import cz.bcx.coopgame.graphics.TextureRegion;
import cz.bcx.coopgame.application.Application;
import cz.bcx.coopgame.application.Log;
import cz.bcx.coopgame.application.screen.transition.FadeScreenTransition;

/**
 * Created by BCX on 7/26/2016.
 */
public class IntroScreen2 extends AbstractScreen {
    private static final int TEXTURE_KEY = 2;

    private TextureRegion region;

    public IntroScreen2(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    public void loadResources() {
        region = getApplicationTextureRegion(TEXTURE_KEY);
    }

    @Override
    public void onUpdate(float delta) {}

    @Override
    public void onDraw() {
        getScreenStandardBatch().begin();
            getScreenStandardBatch().draw(
                    region,
                    getScreenManager().getWindowWidth() / 2 - 200,
                    getScreenManager().getWindowHeight() / 2 - 200,
                    400,
                    400
            );
        getScreenStandardBatch().end();
    }

    @Override
    public void handleKeyboardEvent(Application.KeyboardEvent keyboardEvent) {
        if(keyboardEvent.action == Application.KeyAction.PRESSED) getScreenManager().changeScreen(IntroScreen.class, new FadeScreenTransition(1000));
        Log.debug(getClass(), keyboardEvent);
    }
}
