package ru.samsung.gamestudio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import ru.samsung.gamestudio.managers.AudioManager;
import ru.samsung.gamestudio.screens.GameScreen;
import ru.samsung.gamestudio.screens.MenuScreen;
import ru.samsung.gamestudio.screens.SettingsScreen;

import static ru.samsung.gamestudio.GameSettings.SCREEN_HEIGHT;
import static ru.samsung.gamestudio.GameSettings.SCREEN_WIDTH;

public class MyGdxGame extends Game {

    public BitmapFont largeWhiteFont;
    public BitmapFont commonWhiteFont;
    public BitmapFont commonBlackFont;
    public BitmapFont titleFont;

    public Vector3 touch;
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public AudioManager audioManager;

    public GameScreen gameScreen;
    public MenuScreen menuScreen;
    public SettingsScreen settingsScreen;

    @Override
    public void create() {
        largeWhiteFont = FontBuilder.generate(48, Color.WHITE, GameResources.FONT_PATH);
        commonWhiteFont = FontBuilder.generate(24, Color.WHITE, GameResources.FONT_PATH);
        commonBlackFont = FontBuilder.generate(24, Color.BLACK, GameResources.FONT_PATH);
        titleFont = FontBuilder.generate(76, Color.WHITE, GameResources.FONT_PATH);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        audioManager = new AudioManager();

        gameScreen = new GameScreen(this);
        menuScreen = new MenuScreen(this);
        settingsScreen = new SettingsScreen(this);

        setScreen(menuScreen);
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (largeWhiteFont != null) largeWhiteFont.dispose();
        if (commonWhiteFont != null) commonWhiteFont.dispose();
        if (commonBlackFont != null) commonBlackFont.dispose();
        if (titleFont != null) titleFont.dispose();
    }
}
