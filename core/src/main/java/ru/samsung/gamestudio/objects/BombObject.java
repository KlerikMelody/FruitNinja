package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.samsung.gamestudio.GameResources;
import ru.samsung.gamestudio.GameSettings;

public class BombObject {

    private Texture texture;
    private float x, y;
    private int width, height;
    private float speedY;
    private boolean exploded;

    public BombObject(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = GameSettings.BOMB_WIDTH;
        this.height = GameSettings.BOMB_HEIGHT;
        this.speedY = -GameSettings.BOMB_SPEED;
        this.exploded = false;
        texture = new Texture(GameResources.BOMB_IMG_PATH);
    }

    public void update(float delta) {
        y += speedY * delta;
    }

    public void explode() {
        exploded = true;
    }

    public boolean isExploded() {
        return exploded;
    }

    public boolean isOutOfScreen() {
        return y + height/2 < 0;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x - width/2f, y - height/2f, width, height);
    }

    public void dispose() {
        if (texture != null) texture.dispose();
    }
}
