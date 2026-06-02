package ru.samsung.gamestudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Iterator;

import ru.samsung.gamestudio.GameResources;
import ru.samsung.gamestudio.GameSession;
import ru.samsung.gamestudio.GameSettings;
import ru.samsung.gamestudio.GameState;
import ru.samsung.gamestudio.MyGdxGame;
import ru.samsung.gamestudio.components.BackgroundView;
import ru.samsung.gamestudio.components.ButtonView;
import ru.samsung.gamestudio.components.ImageView;
import ru.samsung.gamestudio.components.RecordsListView;
import ru.samsung.gamestudio.components.TextView;
import ru.samsung.gamestudio.managers.MemoryManager;
import ru.samsung.gamestudio.objects.BombObject;
import ru.samsung.gamestudio.objects.FruitObject;

public class GameScreen extends ScreenAdapter {

    private final MyGdxGame myGdxGame;
    private GameSession gameSession;

    private ArrayList<FruitObject> fruits;
    private ArrayList<BombObject> bombs;

    private BackgroundView backgroundView;
    private ImageView topBlackoutView;
    private TextView scoreTextView;
    private TextView livesTextView;
    private ButtonView pauseButton;
    private ImageView fullBlackoutView;
    private TextView pauseTextView;
    private ButtonView homeButton;
    private ButtonView continueButton;
    private TextView recordsTextView;
    private RecordsListView recordsListView;
    private ButtonView homeButton2;
    private long nextSpawnTime;
    private Vector2 swipeStart = null;
    private ArrayList<Vector2> swipePoints;
    private ShapeRenderer shapeRenderer;

    private static final int MAX_POINTS = 15;
    private static final float MIN_DISTANCE = 15f;
    private static final float LINE_THICKNESS = 2f;

    public GameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        gameSession = new GameSession();

        fruits = new ArrayList<>();
        bombs = new ArrayList<>();
        swipePoints = new ArrayList<>();
        shapeRenderer = new ShapeRenderer();

        backgroundView = new BackgroundView(GameResources.BACKGROUND_IMG_PATH);
        topBlackoutView = new ImageView(0, 1180, GameResources.BLACKOUT_TOP_IMG_PATH);

        scoreTextView = new TextView(myGdxGame.commonWhiteFont, 50, 1215);
        livesTextView = new TextView(myGdxGame.commonWhiteFont, 250, 1215);

        pauseButton = new ButtonView(605, 1200, 46, 54, GameResources.PAUSE_IMG_PATH);


        fullBlackoutView = new ImageView(0, 0, GameResources.BLACKOUT_FULL_IMG_PATH);
        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 282, 842, "Pause");
        homeButton = new ButtonView(138, 695, 200, 70, myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        continueButton = new ButtonView(393, 695, 200, 70, myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH, "Continue");


        recordsTextView = new TextView(myGdxGame.largeWhiteFont, 206, 842, "Last records");
        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        homeButton2 = new ButtonView(280, 365, 160, 70, myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");

        setupInput();
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (gameSession.isPlaying()) {
                    Vector3 world = myGdxGame.camera.unproject(new Vector3(screenX, screenY, 0));
                    swipeStart = new Vector2(world.x, world.y);
                    clearSwipe();
                    addSwipePoint(world.x, world.y);
                }
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (swipeStart != null && gameSession.isPlaying()) {
                    Vector3 world = myGdxGame.camera.unproject(new Vector3(screenX, screenY, 0));
                    Vector2 lastPoint = swipePoints.isEmpty() ? null : swipePoints.get(swipePoints.size() - 1);
                    addSwipePoint(world.x, world.y);
                    if (swipePoints.size() >= 2 && lastPoint != null) {
                        Vector2 newPoint = swipePoints.get(swipePoints.size() - 1);
                        if (!newPoint.equals(lastPoint)) {
                            handleSliceSegment(lastPoint, newPoint);
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (swipeStart != null && gameSession.isPlaying()) {
                    Vector3 world = myGdxGame.camera.unproject(new Vector3(screenX, screenY, 0));
                    addSwipePoint(world.x, world.y);
                    if (swipePoints.size() >= 2) {
                        Vector2 last = swipePoints.get(swipePoints.size() - 2);
                        Vector2 newPoint = swipePoints.get(swipePoints.size() - 1);
                        handleSliceSegment(last, newPoint);
                    }
                    swipeStart = null;
                    clearSwipe();
                }
                return true;
            }
        });
    }

    private void handleSliceSegment(Vector2 p1, Vector2 p2) {
        Iterator<FruitObject> fruitIt = fruits.iterator();
        while (fruitIt.hasNext()) {
            FruitObject fruit = fruitIt.next();
            if (!fruit.isSliced() && isLineIntersectingCircle(p1, p2, fruit.getX(), fruit.getY(), fruit.getWidth() / 2f)) {
                fruit.slice();
                gameSession.addScore(10);
                myGdxGame.audioManager.playSliceSound();
                fruitIt.remove();
            }
        }
        Iterator<BombObject> bombIt = bombs.iterator();
        while (bombIt.hasNext()) {
            BombObject bomb = bombIt.next();
            if (!bomb.isExploded() && isLineIntersectingCircle(p1, p2, bomb.getX(), bomb.getY(), bomb.getWidth() / 2f)) {
                bomb.explode();
                myGdxGame.audioManager.playExplosionSound();
                bombIt.remove();
                if (!gameSession.decreaseLife() && gameSession.isGameOver()) {
                    recordsListView.setRecords(MemoryManager.loadRecordsTable());
                }
            }
        }
    }

    private void addSwipePoint(float x, float y) {
        if (swipePoints.isEmpty()) {
            swipePoints.add(new Vector2(x, y));
            return;
        }
        Vector2 last = swipePoints.get(swipePoints.size() - 1);
        float distance = (float) Math.hypot(x - last.x, y - last.y);
        if (distance >= MIN_DISTANCE) {
            swipePoints.add(new Vector2(x, y));
        }
        while (swipePoints.size() > MAX_POINTS) {
            swipePoints.remove(0);
        }
    }

    private void clearSwipe() {
        if (swipePoints != null) swipePoints.clear();
    }

    private void drawSwipeLine() {
        if (swipePoints == null || swipePoints.size() < 2) return;
        myGdxGame.batch.end();
        shapeRenderer.setProjectionMatrix(myGdxGame.camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, 0.7f);
        for (int i = 0; i < swipePoints.size() - 1; i++) {
            Vector2 p1 = swipePoints.get(i);
            Vector2 p2 = swipePoints.get(i + 1);
            float angle = (float) Math.atan2(p2.y - p1.y, p2.x - p1.x);
            float length = (float) Math.hypot(p2.x - p1.x, p2.y - p1.y);
            shapeRenderer.rect(p1.x, p1.y - LINE_THICKNESS / 2,
                0, LINE_THICKNESS / 2,
                length, LINE_THICKNESS,
                1, 1,
                (float) Math.toDegrees(angle));
        }
        shapeRenderer.end();
        myGdxGame.batch.begin();
    }

    private boolean isLineIntersectingCircle(Vector2 p1, Vector2 p2, float cx, float cy, float radius) {
        float dx = p2.x - p1.x;
        float dy = p2.y - p1.y;
        float fx = p1.x - cx;
        float fy = p1.y - cy;
        float a = dx * dx + dy * dy;
        if (a == 0) return false;
        float b = 2 * (fx * dx + fy * dy);
        float c = (fx * fx + fy * fy) - radius * radius;
        float discriminant = b * b - 4 * a * c;
        if (discriminant < 0) return false;
        discriminant = (float) Math.sqrt(discriminant);
        float t1 = (-b - discriminant) / (2 * a);
        float t2 = (-b + discriminant) / (2 * a);
        return (t1 >= 0 && t1 <= 1) || (t2 >= 0 && t2 <= 1);
    }

    @Override
    public void show() {
        restartGame();
    }

    private void restartGame() {
        fruits.clear();
        bombs.clear();
        gameSession.startGame();
        nextSpawnTime = TimeUtils.millis() + 500;
    }

    @Override
    public void render(float delta) {
        handlePauseAndMenuInput();

        if (gameSession.isPlaying()) {
            spawnObjects();

            for (FruitObject f : fruits) f.update(delta);
            for (BombObject b : bombs) b.update(delta);
            updateObjects();
        }

        updateUI();
        draw();
    }

    private void spawnObjects() {
        if (TimeUtils.millis() >= nextSpawnTime) {
            nextSpawnTime = TimeUtils.millis() + GameSettings.SPAWN_INTERVAL;
            int x = (int) (Math.random() * (GameSettings.SCREEN_WIDTH - 100)) + 50;
            int y = GameSettings.SCREEN_HEIGHT + 50; // сверху

            boolean spawnBomb = Math.random() * 100 < GameSettings.BOMB_SPAWN_CHANCE;
            if (spawnBomb) {
                bombs.add(new BombObject(x, y));
            } else {
                fruits.add(new FruitObject(x, y));
            }
        }
    }

    private void updateObjects() {

        Iterator<FruitObject> fruitIt = fruits.iterator();
        while (fruitIt.hasNext()) {
            FruitObject fruit = fruitIt.next();
            if (!fruit.isSliced() && fruit.isOutOfScreen() && !fruit.isHasEscaped()) {
                fruit.setHasEscaped(true);
                gameSession.decreaseLife();
            }
            if (fruit.isOutOfScreen() || fruit.isSliced()) {
                fruit.dispose();
                fruitIt.remove();
            }
        }
        Iterator<BombObject> bombIt = bombs.iterator();
        while (bombIt.hasNext()) {
            BombObject bomb = bombIt.next();
            if (bomb.isOutOfScreen() || bomb.isExploded()) {
                bomb.dispose();
                bombIt.remove();
            }
        }
    }

    private void updateUI() {
        scoreTextView.setText("Score: " + gameSession.getScore());
        livesTextView.setText("Lives: " + gameSession.getLives());
    }

    private void handlePauseAndMenuInput() {
        if (!Gdx.input.isTouched()) return;
        myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        if (gameSession.state == GameState.PLAYING) {
            if (pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                gameSession.pauseGame();
            }
        } else if (gameSession.state == GameState.PAUSED) {
            if (continueButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                gameSession.resumeGame();
            } else if (homeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.menuScreen);
            }
        } else if (gameSession.state == GameState.ENDED) {
            if (homeButton2.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.menuScreen);
            }
        }
    }

    private void draw() {
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);
        myGdxGame.batch.begin();

        backgroundView.draw(myGdxGame.batch);
        for (FruitObject f : fruits) f.draw(myGdxGame.batch);
        for (BombObject b : bombs) b.draw(myGdxGame.batch);
        drawSwipeLine();

        topBlackoutView.draw(myGdxGame.batch);
        scoreTextView.draw(myGdxGame.batch);
        livesTextView.draw(myGdxGame.batch);
        pauseButton.draw(myGdxGame.batch);

        if (gameSession.state == GameState.PAUSED) {
            fullBlackoutView.draw(myGdxGame.batch);
            pauseTextView.draw(myGdxGame.batch);
            homeButton.draw(myGdxGame.batch);
            continueButton.draw(myGdxGame.batch);
        } else if (gameSession.state == GameState.ENDED) {
            fullBlackoutView.draw(myGdxGame.batch);
            recordsTextView.draw(myGdxGame.batch);
            recordsListView.draw(myGdxGame.batch);
            homeButton2.draw(myGdxGame.batch);
        }

        myGdxGame.batch.end();
    }

    @Override
    public void dispose() {
        backgroundView.dispose();
        topBlackoutView.dispose();
        fullBlackoutView.dispose();
        pauseButton.dispose();
        homeButton.dispose();
        continueButton.dispose();
        homeButton2.dispose();
        for (FruitObject f : fruits) f.dispose();
        for (BombObject b : bombs) b.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
