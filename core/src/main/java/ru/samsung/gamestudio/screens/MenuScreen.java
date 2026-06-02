package ru.samsung.gamestudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.samsung.gamestudio.GameResources;
import ru.samsung.gamestudio.GameSettings;
import ru.samsung.gamestudio.MyGdxGame;
import ru.samsung.gamestudio.components.BackgroundView;
import ru.samsung.gamestudio.components.ButtonView;

public class MenuScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;
    BackgroundView backgroundView;
    ButtonView startButtonView, settingsButtonView, exitButtonView;

    private String titleText = "Fruit Ninja";
    private float titleX, titleY = 960f;
    private float alpha = 0f;

    public MenuScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        backgroundView = new BackgroundView(GameResources.BACKGROUND_IMG_PATH);


        GlyphLayout layout = new GlyphLayout(myGdxGame.titleFont, titleText);
        titleX = (GameSettings.SCREEN_WIDTH - layout.width) / 2f;

        startButtonView = new ButtonView(140, 646, 440, 70, myGdxGame.commonBlackFont,
            GameResources.BUTTON_LONG_BG_IMG_PATH, "START");
        settingsButtonView = new ButtonView(140, 551, 440, 70, myGdxGame.commonBlackFont,
            GameResources.BUTTON_LONG_BG_IMG_PATH, "SETTINGS");
        exitButtonView = new ButtonView(140, 456, 440, 70, myGdxGame.commonBlackFont,
            GameResources.BUTTON_LONG_BG_IMG_PATH, "EXIT");
    }

    @Override
    public void render(float delta) {
        if (alpha < 1f) alpha += delta * 2f;

        handleInput();

        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.BLACK);

        myGdxGame.batch.begin();

        backgroundView.draw(myGdxGame.batch);

        Color oldColor = myGdxGame.batch.getColor();
        myGdxGame.batch.setColor(1, 1, 1, alpha);


        myGdxGame.titleFont.setColor(Color.BLACK);
        myGdxGame.titleFont.draw(myGdxGame.batch, titleText, titleX + 5, titleY + 5);
        myGdxGame.titleFont.setColor(Color.WHITE);
        myGdxGame.titleFont.draw(myGdxGame.batch, titleText, titleX, titleY);

        startButtonView.draw(myGdxGame.batch);
        settingsButtonView.draw(myGdxGame.batch);
        exitButtonView.draw(myGdxGame.batch);

        myGdxGame.batch.setColor(oldColor);
        myGdxGame.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (startButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y))
                myGdxGame.setScreen(myGdxGame.gameScreen);
            if (exitButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y))
                Gdx.app.exit();
            if (settingsButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y))
                myGdxGame.setScreen(myGdxGame.settingsScreen);
        }
    }

    @Override
    public void dispose() {
        backgroundView.dispose();
        startButtonView.dispose();
        settingsButtonView.dispose();
        exitButtonView.dispose();
    }
}
