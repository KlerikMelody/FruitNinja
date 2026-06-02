package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.samsung.gamestudio.GameResources;
import ru.samsung.gamestudio.GameSettings;

public class FruitObject {

    private Texture texture;
    private float x, y;
    private int width, height;
    private float speedY;
    private boolean sliced;
    private boolean hasEscaped;

    public FruitObject(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = GameSettings.FRUIT_WIDTH;
        this.height = GameSettings.FRUIT_HEIGHT;
        this.speedY = -GameSettings.FRUIT_SPEED;
        this.sliced = false;
        this.hasEscaped = false;


        int r = (int)(Math.random() * 3);
        String path;
        switch (r) {
            case 0: path = GameResources.APPLE_IMG_PATH; break;
            case 1: path = GameResources.ORANGE_IMG_PATH; break;
            default: path = GameResources.PEAR_IMG_PATH; break;
        }
        texture = new Texture(path);
    }

    public void update(float delta) {
        y += speedY * delta;
    }

    public void slice() {
        sliced = true;
    }

    public boolean isSliced() {
        return sliced;
    }

    public boolean isHasEscaped() {
        return hasEscaped;
    }

    public void setHasEscaped(boolean hasEscaped) {
        this.hasEscaped = hasEscaped;
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
