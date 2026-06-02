package ru.samsung.gamestudio;

import com.badlogic.gdx.utils.TimeUtils;
import ru.samsung.gamestudio.managers.MemoryManager;
import java.util.ArrayList;

public class GameSession {

    public GameState state;
    private int score;
    private int lives;
    private boolean gameOverTriggered;
    long sessionStartTime;
    long pauseStartTime;

    public GameSession() {
        score = 0;
        lives = GameSettings.START_LIVES;
        state = GameState.PLAYING;
        gameOverTriggered = false;
    }

    public void startGame() {
        state = GameState.PLAYING;
        score = 0;
        lives = GameSettings.START_LIVES;
        gameOverTriggered = false;
        sessionStartTime = TimeUtils.millis();
    }

    public void addScore(int points) {
        if (state == GameState.PLAYING) {
            score += points;
        }
    }

    public boolean decreaseLife() {
        if (state != GameState.PLAYING) return false;
        lives--;
        if (lives <= 0) {
            endGame();
            return false;
        }
        return true;
    }

    public void endGame() {
        if (gameOverTriggered) return;
        gameOverTriggered = true;
        state = GameState.ENDED;

        ArrayList<Integer> table = MemoryManager.loadRecordsTable();
        if (table == null) table = new ArrayList<>();

        int pos = 0;
        while (pos < table.size() && table.get(pos) > score) {
            pos++;
        }
        table.add(pos, score);
        MemoryManager.saveTableOfRecords(table);
    }

    public void updateScore() {
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public boolean isGameOver() {
        return state == GameState.ENDED;
    }

    public boolean isPlaying() {
        return state == GameState.PLAYING;
    }

    public void pauseGame() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
            pauseStartTime = TimeUtils.millis();
        }
    }

    public void resumeGame() {
        if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
            sessionStartTime += TimeUtils.millis() - pauseStartTime;
        }
    }
}
