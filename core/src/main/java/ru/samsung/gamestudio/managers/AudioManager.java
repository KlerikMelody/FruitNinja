package ru.samsung.gamestudio.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import ru.samsung.gamestudio.GameResources;

public class AudioManager {

    public boolean isSoundOn;
    public boolean isMusicOn;

    public Music backgroundMusic;
    public Sound explosionSound;
    public Sound sliceSound;

    public AudioManager() {
        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(GameResources.BACKGROUND_MUSIC_PATH));
            explosionSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.EXPLOSION_SOUND_PATH));
            sliceSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.SLICE_SOUND_PATH));
        } catch (Exception e) {
            Gdx.app.error("AudioManager", "Failed to load sounds", e);
        }

        if (backgroundMusic != null) {
            backgroundMusic.setVolume(0.2f);
            backgroundMusic.setLooping(true);
        }

        updateSoundFlag();
        updateMusicFlag();
    }

    public void playSliceSound() {
        if (isSoundOn && sliceSound != null) {
            sliceSound.play();
        }
    }

    public void playExplosionSound() {
        if (isSoundOn && explosionSound != null) {
            explosionSound.play();
        }
    }

    public void updateSoundFlag() {
        isSoundOn = MemoryManager.loadIsSoundOn();
    }

    public void updateMusicFlag() {
        isMusicOn = MemoryManager.loadIsMusicOn();
        if (backgroundMusic != null) {
            if (isMusicOn) backgroundMusic.play();
            else backgroundMusic.stop();
        }
    }
}
