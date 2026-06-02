package ru.samsung.gamestudio.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.samsung.gamestudio.GameSettings;

public class BackgroundView extends View {

    private Texture texture;

    public BackgroundView(String pathToTexture) {
        super(0, 0);
        texture = new Texture(pathToTexture);
        this.width = GameSettings.SCREEN_WIDTH;
        this.height = GameSettings.SCREEN_HEIGHT;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, 0, 0, width, height);
    }

    @Override
    public void dispose() {
        if (texture != null) texture.dispose();
    }
}
