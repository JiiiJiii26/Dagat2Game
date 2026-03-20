package models;

public class GameSettings {

    private static GameSettings instance;

    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    private boolean soundEffectsEnabled = true;
    private boolean musicEnabled        = true;
    private int     masterVolume        = 80;   
    private int     sfxVolume           = 80;   
    private boolean fullscreen          = true;

    private GameSettings() {}

    public boolean isSoundEffectsEnabled(){ 
        return soundEffectsEnabled; 
    }

    public void setSoundEffectsEnabled(boolean v){ 
        soundEffectsEnabled = v;    
    }

    public boolean isMusicEnabled(){ 
        return musicEnabled;        
    }
    public void setMusicEnabled(boolean v){ 
        musicEnabled = v;           
    }

    public int  getMasterVolume(){ 
        return masterVolume;       
    }

    public void setMasterVolume(int v){ 
        masterVolume = clamp(v);    
    }

    public int  getSfxVolume(){ 
        return sfxVolume;           
    }

    public void setSfxVolume(int v){ 
        sfxVolume = clamp(v);      
    }

    public boolean isFullscreen(){ 
        return fullscreen;          
    }
    public void setFullscreen(boolean v){ 
        fullscreen = v;             
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(100, v));
    }

    @Override
    public String toString() {
        return "GameSettings{" +
               "sfx=" + soundEffectsEnabled +
               ", music=" + musicEnabled +
               ", masterVol=" + masterVolume +
               ", sfxVol=" + sfxVolume +
               ", fullscreen=" + fullscreen + "}";
    }
}