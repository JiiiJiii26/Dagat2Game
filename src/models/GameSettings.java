package models;

public class GameSettings {
    private static GameSettings instance;
    private float musicVolume = 1.0f;
    private float sfxVolume = 1.0f;
    private boolean musicEnabled = true;
    private boolean soundEffectsEnabled = true;
    private boolean fullscreen = false;

    private GameSettings() {}

    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = volume;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
    }

    public boolean isSoundEffectsEnabled() {
        return soundEffectsEnabled;
    }

    public void setSoundEffectsEnabled(boolean enabled) {
        this.soundEffectsEnabled = enabled;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }
}