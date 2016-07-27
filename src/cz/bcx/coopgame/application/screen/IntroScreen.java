package cz.bcx.coopgame.application.screen;

import cz.bcx.coopgame.Texture;
import cz.bcx.coopgame.TextureRegion;
import cz.bcx.coopgame.application.Application;
import cz.bcx.coopgame.application.Log;

/**
 * Created by BCX on 7/26/2016.
 */
public class IntroScreen extends AbstractScreen {

    private Texture texture;
    private TextureRegion region;
    private Texture texture2;
    private TextureRegion region2;

    public IntroScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    public void loadResources() {
        texture = new Texture("res/placeholder.png");
        region  = new TextureRegion(texture, 0f, 0f, 1f, 1f);

        texture2 = new Texture("res/placeholder2.jpg");
        region2  = new TextureRegion(texture2, 0f, 0f, 1f, 1f);
    }

    @Override
    public void onUpdate(float delta) {}

    @Override
    public void onDraw() {
        getScreenStandardBatch().begin();
        getScreenStandardBatch().draw(
                region,
                getScreenManager().getWindowWidth()/2 - 400,
                getScreenManager().getWindowHeight()/2 - 200,
                400,
                400
        );
        getScreenStandardBatch().draw(
                region2,
                getScreenManager().getWindowWidth()/2,
                getScreenManager().getWindowHeight()/2 - 200,
                400,
                400
        );
        getScreenStandardBatch().end();
    }

    @Override
    public void handleKeyboardEvent(Application.KeyboardEvent keyboardEvent) {
        Log.debug(getClass(), keyboardEvent);
    }
}
