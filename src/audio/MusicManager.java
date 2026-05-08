// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package audio;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.FloatControl.Type;

public class MusicManager {
   private static MusicManager instance;
   private Clip musicClip;
   private String currentTrack;
   private float musicVolume = 0.9F;
   private float sfxVolume = 1.0F;

   private MusicManager() {
   }

   public static MusicManager getInstance() {
      if (instance == null) {
         instance = new MusicManager();
      }

      return instance;
   }

    public void playMusic(String var1) {
       try {
          if (this.currentTrack != null && this.currentTrack.equals(var1) && this.musicClip != null && this.musicClip.isRunning()) {
             System.out.println("Music already playing: " + var1);
             return;
          }

          if (this.musicClip != null && this.musicClip.isRunning()) {
             this.musicClip.stop();
             this.musicClip.close();
          }

          String var2 = var1.equals("battle") ? "battle_theme.wav" : (var1.equals("menu") ? "menu_theme.wav" : var1 + ".wav");
          AudioInputStream var3 = AudioSystem.getAudioInputStream(new File("assets/music/" + var2));
          this.musicClip = AudioSystem.getClip();
          this.musicClip.open(var3);
          this.setMusicVolume(this.musicVolume);
          this.musicClip.loop(-1);
          this.currentTrack = var1;
          System.out.println("Playing music: " + var1);
       } catch (IOException | LineUnavailableException | UnsupportedAudioFileException var4) {
          System.err.println("Error playing music: " + ((Exception)var4).getMessage());
       }

    }

   public void playSound(String var1) {
      try {
         AudioInputStream var2 = AudioSystem.getAudioInputStream(new File("assets/music/" + var1 + ".wav"));
         Clip var3 = AudioSystem.getClip();
         var3.open(var2);
         FloatControl var4 = (FloatControl)var3.getControl(Type.MASTER_GAIN);
         float var5 = (float)(Math.log((double)this.sfxVolume) / Math.log((double)10.0F) * (double)20.0F);
         var4.setValue(var5);
         var3.start();
         System.out.println("Playing sound: " + var1);
      } catch (IOException | LineUnavailableException | UnsupportedAudioFileException var6) {
         System.err.println("Error playing sound: " + ((Exception)var6).getMessage());
      }

   }

   public void stopMusic() {
      if (this.musicClip != null && this.musicClip.isRunning()) {
         this.musicClip.stop();
         this.musicClip.close();
         this.musicClip = null;
         this.currentTrack = null;
      }

      System.out.println("Stopping music");
   }

   public void setMusicVolume(float var1) {
      this.musicVolume = var1;
      if (this.musicClip != null) {
         FloatControl var2 = (FloatControl)this.musicClip.getControl(Type.MASTER_GAIN);
         float var3 = (float)(Math.log((double)var1) / Math.log((double)10.0F) * (double)20.0F);
         var2.setValue(var3);
      }

      System.out.println("Setting music volume to " + var1);
   }

   public String getCurrentTrack() {
      return this.currentTrack;
   }

   public void setSfxVolume(float var1) {
      this.sfxVolume = var1;
      System.out.println("Setting SFX volume to " + var1);
   }

   public void playMainMenuMusic() {
      this.playMusic("battle_theme");
   }

   public boolean isPlaying() {
      return this.musicClip != null && this.musicClip.isRunning();
   }
}
