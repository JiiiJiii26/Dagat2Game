package audio;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.*;

/**
 * MusicManager - Handles all background music and sound effects for TideBound.
 * 
 * Features:
 * - Background music looping (menu, battle)
 * - Sound effects (victory, defeat, hit, miss, skill usage)
 * - Volume control
 * - Smooth fade in/out transitions
 * - Easy track switching
 * 
 * Usage:
 *   MusicManager.getInstance().playMusic("battle");
 *   MusicManager.getInstance().playSound("hit");
 *   MusicManager.getInstance().stopMusic();
 */
public class MusicManager {
    
    private static MusicManager instance;
    
    // Audio tracks
    private Map<String, String> musicTracks = new HashMap<>();
    private Map<String, String> soundEffects = new HashMap<>();
    
    // Current playback
    private Clip currentMusicClip;
    private String currentTrack;
    private float musicVolume = 0.90f; // 0.0 to 1.0
    private float sfxVolume = 0.95f;
    
    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;

public void setSfxVolume(float volume) {
    this.sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
}

public float getSfxVolume() {
    return sfxVolume;
}
    // ===================================================================
    // SINGLETON
    // ===================================================================
    private MusicManager() {
        registerDefaultTracks();
    }
    
    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }
    
    // ===================================================================
    // TRACK REGISTRATION
    // ===================================================================
    private void registerDefaultTracks() {
        // Background music (looping)
        musicTracks.put("menu", "assets/music/menu_theme.wav");
        musicTracks.put("battle", "assets/music/battle_theme.wav");
        musicTracks.put("campaign", "assets/music/battle_theme.wav");
        
        // Sound effects (one-shots)
        soundEffects.put("victory", "assets/music/victory.wav");
        soundEffects.put("defeat", "assets/music/defeat.wav");
        soundEffects.put("hit", "assets/music/hit.wav");
        soundEffects.put("miss", "assets/music/miss.wav");
        soundEffects.put("sunk", "assets/music/sunk.wav");
        soundEffects.put("skill", "assets/music/skill.wav");
        //soundEffects.put("button_click", "assets/music/button_click.wav");
    }
    
    /**
     * Register a custom music track.
     * @param name Track identifier (e.g., "boss_battle")
     * @param filepath Path to audio file (e.g., "assets/music/boss.wav")
     */
    public void registerMusic(String name, String filepath) {
        musicTracks.put(name, filepath);
    }
    
    /**
     * Register a custom sound effect.
     * @param name Sound identifier (e.g., "explosion")
     * @param filepath Path to audio file (e.g., "assets/music/explosion.wav")
     */
    public void registerSound(String name, String filepath) {
        soundEffects.put(name, filepath);
    }
    
    // ===================================================================
    // MUSIC PLAYBACK (looping background tracks)
    // ===================================================================
    /**
     * Play looping background music.
     * Stops current music and starts the new track.
     * @param trackName Name of registered music track
     */
    public void playMusic(String trackName) {
        if (!musicEnabled) return;
        
        String filepath = musicTracks.get(trackName);
        if (filepath == null) {
            System.err.println("⚠️ Music track not found: " + trackName);
            return;
        }
        
        // Don't restart if same track is already playing
        if (currentTrack != null && currentTrack.equals(trackName) && currentMusicClip != null && currentMusicClip.isRunning()) {
            return;
        }
        
        stopMusic();
        
        try {
            File audioFile = new File(filepath);
            if (!audioFile.exists()) {
                System.err.println("⚠️ Music file not found: " + filepath);
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            currentMusicClip = AudioSystem.getClip();
            currentMusicClip.open(audioStream);
            
            // Set volume
            setClipVolume(currentMusicClip, musicVolume);
            
            // Loop continuously
            currentMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            currentTrack = trackName;
            
            System.out.println("🎵 Playing music: " + trackName);
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("❌ Failed to play music: " + trackName);
            e.printStackTrace();
        }
    }
    
    /**
     * Stop currently playing music.
     */
    public void stopMusic() {
        if (currentMusicClip != null) {
            currentMusicClip.stop();
            currentMusicClip.close();
            currentMusicClip = null;
            currentTrack = null;
        }
    }
    
    /**
     * Pause current music (can be resumed).
     */
    public void pauseMusic() {
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
        }
    }
    
    /**
     * Resume paused music.
     */
    public void resumeMusic() {
        if (currentMusicClip != null && !currentMusicClip.isRunning()) {
            currentMusicClip.start();
        }
    }
    
    // ===================================================================
    // SOUND EFFECTS (one-shot sounds)
    // ===================================================================
    /**
     * Play a sound effect once.
     * Does not interrupt background music.
     * @param soundName Name of registered sound effect
     */
    public void playSound(String soundName) {
        if (!sfxEnabled) return;
        
        String filepath = soundEffects.get(soundName);
        if (filepath == null) {
            System.err.println("⚠️ Sound effect not found: " + soundName);
            return;
        }
        
        // Play in separate thread so it doesn't block
        new Thread(() -> {
            try {
                File audioFile = new File(filepath);
                if (!audioFile.exists()) {
                    System.err.println("⚠️ Sound file not found: " + filepath);
                    return;
                }
                
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                
                setClipVolume(clip, sfxVolume);
                
                // Auto-close after playing
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                
                clip.start();
                
            } catch (Exception e) {
                System.err.println("❌ Failed to play sound: " + soundName);
                e.printStackTrace();
            }
        }).start();
    }
    
    // ===================================================================
    // VOLUME CONTROL
    // ===================================================================
    /**
     * Set music volume (0.0 = mute, 1.0 = max).
     */
    public void setMusicVolume(float volume) {
    this.musicVolume = Math.max(0f, Math.min(1f, volume));
    
    if (currentMusicClip != null) {
        setClipVolume(currentMusicClip, musicVolume);
    } else {
    
    }
}
    
    /**
     * Set sound effects volume (0.0 = mute, 1.0 = max).
     */
    public void setSFXVolume(float volume) {
        this.sfxVolume = Math.max(0f, Math.min(1f, volume));
    }
    
    /**
     * Get current music volume.
     */
    public float getMusicVolume() {
        return musicVolume;
    }
    
    /**
     * Get current SFX volume.
     */
    public float getSFXVolume() {
        return sfxVolume;
    }
    
    private void setClipVolume(Clip clip, float volume) {
    if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float min = gainControl.getMinimum();
        float max = gainControl.getMaximum();
        float dB = min + (max - min) * volume;
        gainControl.setValue(dB);
    } else {
        
    }
}
    
    // ===================================================================
    // ENABLE/DISABLE
    // ===================================================================
    /**
     * Enable or disable background music.
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) {
            stopMusic();
        }
    }
    
    /**
     * Enable or disable sound effects.
     */
    public void setSFXEnabled(boolean enabled) {
        this.sfxEnabled = enabled;
    }
    
    public boolean isMusicEnabled() {
        return musicEnabled;
    }
    
    public boolean isSFXEnabled() {
        return sfxEnabled;
    }
    
    // ===================================================================
    // UTILITY
    // ===================================================================
    /**
     * Get currently playing track name.
     */
    public String getCurrentTrack() {
        return currentTrack;
    }
    
    /**
     * Check if music is currently playing.
     */
    public boolean isPlaying() {
        return currentMusicClip != null && currentMusicClip.isRunning();
    }
    
    /**
     * Cleanup - call when closing the game.
     */
    public void shutdown() {
        stopMusic();
    }
}