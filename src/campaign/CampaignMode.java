package campaign;

import javax.swing.*;
import javax.swing.border.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import characters.*;
import gui.BoardPanel;
import audio.MusicManager;
import gui.PlacementPanel;
import gui.SkillPanel;
import gui.TimerPanel;
import models.Board;
import models.Cell;
import models.Ship;
import game.ShotResult;
import main.Main;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.geom.AffineTransform;


public class CampaignMode {
   


    private boolean testMode = false;   
    private String testEnemyName = "Flue";

    private JPanel jijiPortraitContainer;
    private JLabel jijiDamageOverlay;

    private Image oceanBackground;
    private Image scaledOceanBackground;
    private Timer moonPhaseTimer;
    private JFrame frame;
    private List<GameCharacter> possibleEnemies;  
    private List<CampaignWave> waves;
    private int currentWaveIndex = 0;
    private GameCharacter playerCharacter;
    private GameCharacter currentEnemy;
    private Board playerBoard;
    private Board enemyBoard;
    private JLabel statusLabel;
    private boolean waitingForWhirlpoolTarget = false;
    private java.util.function.BiConsumer<Integer, Integer> currentWhirlpoolCallback;
     
    private boolean waitingForAerisShield = false;
    private java.util.function.BiConsumer<Integer, Integer> currentAerisShieldCallback;


    
    private BoardPanel playerBoardPanel;
    private BoardPanel enemyBoardPanel;
    private JLabel waveLabel;
    private boolean playerTurn = true;
    private Random random = new Random();
    private boolean waitingForSkillTarget = false;
    private boolean waitingForTarget = false;
    private String currentSkillName = "";
    private SkillTargetCallback targetCallback;
    private JButton lastClickedSkillButton;
    private int extraTurnsRemaining = 0;

    private int currentSkillNumber = 0;

private boolean currentSkillTargetsOwnBoard = false;
private boolean currentSkillRequiresDirection = false;
private boolean currentSkillDirectionHorizontal = true;

    private TimerPanel turnTimer;
    private TimerPanel enemyTurnTimer;
private boolean timerEnabled = true;

private JLabel playerShipLabel;
private JLabel enemyShipLabel;

  
    private boolean waitingForKaelStepSource = false;
    private boolean waitingForKaelStepDestination = false;
    private int[] stepSourceCoordinates = new int[2];
private Timer skillPanelRefreshTimer;
private SkillPanel currentSkillPanel;

    private boolean waitingForKaelBlade = false;
    private java.util.function.BiConsumer<Integer, Integer> currentKaelBladeCallback;
    private boolean bladeDirectionHorizontal = true;


    private boolean waitingForKaelDomain = false;
    private java.util.function.BiConsumer<Integer, Integer> currentKaelDomainCallback;
    

    private boolean waitingForSeleneVision = false;
    private java.util.function.BiConsumer<Integer, Integer> currentSeleneVisionCallback;
    
    private boolean waitingForSeleneBinding = false;
    private java.util.function.BiConsumer<Integer, Integer> currentSeleneBindingCallback;
    
    
private boolean waitingForSeleneCrescent = false;
private boolean jijiAttackAnimationPlaying = false;
private boolean jijiAttackPlayedThisTurn = false;
private boolean jijiDamagedAnimationPlaying = false;
private ImageIcon[] jijiIdleFrames = new ImageIcon[4];
private Timer idleAnimationTimer;
private int currentCycleSlot = 0;
private int slotCounter = 0;
private static final int[] CYCLE_DURATIONS = {18,36,18,36,18,36,18,36,18,36,54,72};
private static final int[] SLOT_FRAME_MAP = {0,1,0,1,0,1,0,1,0,1,2,3};
// Subtle sway offsets per slot (px) - gentle side-to-side motion
private static final int[] SLOT_OFFSET_X = {0, 1, -1, 1, -1, 1, 0, 1, -1, 1, 0, 0};

// Damaged animation frames (4 PNGs)
private ImageIcon[] jijiDamagedFrames = new ImageIcon[4];
private Timer damagedAnimationTimer;
private int currentDamagedFrame = 0;
private int damagedFrameCounter = 0;
private static final int[] DAMAGED_FRAME_DURATIONS = {6, 6, 6, 12}; // ticks (~0.5s cycle)

// Attack animation frames (4 PNGs)
private ImageIcon[] jijiAttackFrames = new ImageIcon[4];
private Timer attackAnimationTimer;
private int currentAttackFrame = 0;
private int attackFrameCounter = 0;
private static final int[] ATTACK_FRAME_DURATIONS = {4, 4, 4, 8}; // ticks (~0.4s total)
private java.util.function.BiConsumer<Integer, Integer> currentSeleneCrescentCallback;

private Timer seleneUpdateTimer;

private ImageIcon[] kaelIdleFrames = new ImageIcon[5];
private Timer kaelIdleAnimationTimer;
private int kaelIdleSequenceIndex = 0;
private int kaelIdleFrameCounter = 0;
private boolean kaelIdleAnimationPlaying = false;
private static final int[] KAEL_IDLE_SEQUENCE = {0,2,0,2,0,2,0,2,0,2,0,2,0,2,0,2,1,3,4};

private ImageIcon[] enemyKaelIdleFrames = new ImageIcon[5];
private Timer enemyKaelIdleAnimationTimer;
private int enemyKaelIdleSequenceIndex = 0;
private int enemyKaelIdleFrameCounter = 0;
private boolean enemyKaelIdleAnimationPlaying = false;

private ImageIcon[] kaelAttackFrames = new ImageIcon[3];
private Timer kaelAttackAnimationTimer;
private int currentKaelAttackFrame = 0;
private int kaelAttackFrameCounter = 0;
private boolean kaelAttackAnimationPlaying = false;

private ImageIcon[] enemyKaelAttackFrames = new ImageIcon[3];
private Timer enemyKaelAttackAnimationTimer;
private int currentEnemyKaelAttackFrame = 0;
private int enemyKaelAttackFrameCounter = 0;
private boolean enemyKaelAttackAnimationPlaying = false;

private ImageIcon[] kaelDamagedFrames = new ImageIcon[3];
private Timer kaelDamagedAnimationTimer;
private int currentKaelDamagedFrame = 0;
private int kaelDamagedFrameCounter = 0;
private boolean kaelDamagedAnimationPlaying = false;

private ImageIcon[] enemyKaelDamagedFrames = new ImageIcon[3];
private Timer enemyKaelDamagedAnimationTimer;
private int currentEnemyKaelDamagedFrame = 0;
private int enemyKaelDamagedFrameCounter = 0;
private boolean enemyKaelDamagedAnimationPlaying = false;

private ImageIcon[] valeriusIdleFrames = new ImageIcon[4];
private Timer valeriusIdleAnimationTimer;
private int valeriusIdleSequenceIndex = 0;
private int valeriusIdleFrameCounter = 0;
private boolean valeriusIdleAnimationPlaying = false;
private static final int[] VALERIUS_IDLE_SEQUENCE = {0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3};

private JLabel valeriusLargePortraitLabel;
private ImageIcon[] enemyValeriusIdleFrames = new ImageIcon[4];
private Timer enemyValeriusIdleAnimationTimer;
private int enemyValeriusIdleSequenceIndex = 0;
private int enemyValeriusIdleFrameCounter = 0;
private boolean enemyValeriusIdleAnimationPlaying = false;

private ImageIcon[] skyeIdleFrames = new ImageIcon[4];
private Timer skyeIdleAnimationTimer;
private int skyeIdleSequenceIndex = 0;
private int skyeIdleFrameCounter = 0;
private boolean skyeIdleAnimationPlaying = false;
private static final int[] SKYE_IDLE_SEQUENCE = {0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3};

private ImageIcon[] enemySkyeIdleFrames = new ImageIcon[4];
private Timer enemySkyeIdleAnimationTimer;
private int enemySkyeIdleSequenceIndex = 0;
private int enemySkyeIdleFrameCounter = 0;
private boolean enemySkyeIdleAnimationPlaying = false;

private ImageIcon[] skyeAttackFrames = new ImageIcon[3];
private Timer skyeAttackAnimationTimer;
private int currentSkyeAttackFrame = 0;
private int skyeAttackFrameCounter = 0;
private boolean skyeAttackAnimationPlaying = false;
private static final int[] SKYE_ATTACK_FRAME_DURATIONS = {8, 8, 12}; // ticks (~0.6s total)

private ImageIcon[] enemySkyeAttackFrames = new ImageIcon[3];
private Timer enemySkyeAttackAnimationTimer;
private int currentEnemySkyeAttackFrame = 0;
private int enemySkyeAttackFrameCounter = 0;
private boolean enemySkyeAttackAnimationPlaying = false;

private ImageIcon[] skyeDamagedFrames = new ImageIcon[4];
private Timer skyeDamagedAnimationTimer;
private int currentSkyeDamagedFrame = 0;
private int skyeDamagedFrameCounter = 0;
private boolean skyeDamagedAnimationPlaying = false;
private static final int[] SKYE_DAMAGED_FRAME_DURATIONS = {10, 10, 10, 20}; // ticks (~0.5s total)

private ImageIcon[] enemySkyeDamagedFrames = new ImageIcon[4];
private Timer enemySkyeDamagedAnimationTimer;
private int currentEnemySkyeDamagedFrame = 0;
private int enemySkyeDamagedFrameCounter = 0;
private boolean enemySkyeDamagedAnimationPlaying = false;

private ImageIcon[] morganaIdleFrames = new ImageIcon[4];
private Timer morganaIdleAnimationTimer;
private int morganaIdleSequenceIndex = 0;
private int morganaIdleFrameCounter = 0;
private boolean morganaIdleAnimationPlaying = false;
private static final int[] MORGANA_IDLE_SEQUENCE = {0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3};

private ImageIcon[] morganaAttackFrames = new ImageIcon[3];
private Timer morganaAttackAnimationTimer;
private int currentMorganaAttackFrame = 0;
private int morganaAttackFrameCounter = 0;
private boolean morganaAttackAnimationPlaying = false;
private static final int[] MORGANA_ATTACK_FRAME_DURATIONS = {8, 8, 12}; // ticks (~0.6s total)

private ImageIcon[] morganaDamagedFrames = new ImageIcon[3];
private Timer morganaDamagedAnimationTimer;
private int currentMorganaDamagedFrame = 0;
private int morganaDamagedFrameCounter = 0;
private boolean morganaDamagedAnimationPlaying = false;
private static final int[] MORGANA_DAMAGED_FRAME_DURATIONS = {12, 12, 16}; // ticks (~0.4s total)

private ImageIcon[] enemyMorganaIdleFrames = new ImageIcon[4];
private Timer enemyMorganaIdleAnimationTimer;
private int enemyMorganaIdleSequenceIndex = 0;
private int enemyMorganaIdleFrameCounter = 0;
private boolean enemyMorganaIdleAnimationPlaying = false;

private ImageIcon[] enemyMorganaAttackFrames = new ImageIcon[3];
private Timer enemyMorganaAttackAnimationTimer;
private int currentEnemyMorganaAttackFrame = 0;
private int enemyMorganaAttackFrameCounter = 0;
private boolean enemyMorganaAttackAnimationPlaying = false;

private ImageIcon[] enemyMorganaDamagedFrames = new ImageIcon[3];
private Timer enemyMorganaDamagedAnimationTimer;
private int currentEnemyMorganaDamagedFrame = 0;
private int enemyMorganaDamagedFrameCounter = 0;
private boolean enemyMorganaDamagedAnimationPlaying = false;

private ImageIcon[] aerisIdleFrames = new ImageIcon[3];
private Timer aerisIdleAnimationTimer;
private int aerisIdleSequenceIndex = 0;
private int aerisIdleFrameCounter = 0;
private boolean aerisIdleAnimationPlaying = false;
private static final int[] AERIS_IDLE_SEQUENCE = {0,1,2,0,1,2,0,1,2,0,1,2,0,1,2,0,1,2,0,1,2,0,1,2,0,1,2,0,1,2,0,1,2};

private ImageIcon[] enemyAerisIdleFrames = new ImageIcon[3];
private Timer enemyAerisIdleAnimationTimer;
private int enemyAerisIdleSequenceIndex = 0;
private int enemyAerisIdleFrameCounter = 0;
private boolean enemyAerisIdleAnimationPlaying = false;

private ImageIcon[] aerisAttackFrames = new ImageIcon[3];
private Timer aerisAttackAnimationTimer;
private int currentAerisAttackFrame = 0;
private int aerisAttackFrameCounter = 0;
private boolean aerisAttackAnimationPlaying = false;
private static final int[] AERIS_ATTACK_FRAME_DURATIONS = {8, 8, 12}; // ticks (~0.6s total)

private ImageIcon[] enemyAerisAttackFrames = new ImageIcon[3];
private Timer enemyAerisAttackAnimationTimer;
private int currentEnemyAerisAttackFrame = 0;
private int enemyAerisAttackFrameCounter = 0;
private boolean enemyAerisAttackAnimationPlaying = false;

private ImageIcon[] aerisDamagedFrames = new ImageIcon[3];
private Timer aerisDamagedAnimationTimer;
private int currentAerisDamagedFrame = 0;
private int aerisDamagedFrameCounter = 0;
private boolean aerisDamagedAnimationPlaying = false;
private static final int[] AERIS_DAMAGED_FRAME_DURATIONS = {12, 12, 16}; // ticks (~0.4s total)

private ImageIcon[] enemyAerisDamagedFrames = new ImageIcon[3];
private Timer enemyAerisDamagedAnimationTimer;
private int currentEnemyAerisDamagedFrame = 0;
private int enemyAerisDamagedFrameCounter = 0;
private boolean enemyAerisDamagedAnimationPlaying = false;

private ImageIcon[] seleneIdleFrames = new ImageIcon[4];
private Timer seleneIdleAnimationTimer;
private int seleneIdleSequenceIndex = 0;
private int seleneIdleFrameCounter = 0;
private boolean seleneIdleAnimationPlaying = false;
private static final int[] SELENE_IDLE_SEQUENCE = {0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3};

private ImageIcon[] enemySeleneIdleFrames = new ImageIcon[4];
private Timer enemySeleneIdleAnimationTimer;
private int enemySeleneIdleSequenceIndex = 0;
private int enemySeleneIdleFrameCounter = 0;
private boolean enemySeleneIdleAnimationPlaying = false;

private ImageIcon[] flueIdleFrames = new ImageIcon[3];
private Timer flueIdleAnimationTimer;
private int flueIdleSequenceIndex = 0;
private int flueIdleFrameCounter = 0;
private boolean flueIdleAnimationPlaying = false;
private static final int[] FLUE_IDLE_SEQUENCE = {0,1,2,0,1,2,0,1,2,0,1,2,0,1,2,0,1,2,0,1,2,0,1,2,0,1,2,0,1,2,0,1,2};

private ImageIcon[] enemyFlueIdleFrames = new ImageIcon[3];
private Timer enemyFlueIdleAnimationTimer;
private int enemyFlueIdleSequenceIndex = 0;
private int enemyFlueIdleFrameCounter = 0;
private boolean enemyFlueIdleAnimationPlaying = false;

private ImageIcon[] flueAttackFrames = new ImageIcon[3];
private Timer flueAttackAnimationTimer;
private int currentFlueAttackFrame = 0;
private int flueAttackFrameCounter = 0;
private boolean flueAttackAnimationPlaying = false;
private static final int[] FLUE_ATTACK_FRAME_DURATIONS = {8, 8, 12}; // ticks (~0.6s total)

private ImageIcon[] enemyFlueAttackFrames = new ImageIcon[3];
private Timer enemyFlueAttackAnimationTimer;
private int currentEnemyFlueAttackFrame = 0;
private int enemyFlueAttackFrameCounter = 0;
private boolean enemyFlueAttackAnimationPlaying = false;

private ImageIcon[] flueDamagedFrames = new ImageIcon[3];
private Timer flueDamagedAnimationTimer;
private int currentFlueDamagedFrame = 0;
private int flueDamagedFrameCounter = 0;
private boolean flueDamagedAnimationPlaying = false;
private static final int[] FLUE_DAMAGED_FRAME_DURATIONS = {12, 12, 16}; // ticks (~0.4s total)

private ImageIcon[] enemyFlueDamagedFrames = new ImageIcon[3];
private Timer enemyFlueDamagedAnimationTimer;
private int currentEnemyFlueDamagedFrame = 0;
private int enemyFlueDamagedFrameCounter = 0;
private boolean enemyFlueDamagedAnimationPlaying = false;

private ImageIcon[] seleneAttackFrames = new ImageIcon[3];
private Timer seleneAttackAnimationTimer;
private int currentSeleneAttackFrame = 0;
private int seleneAttackFrameCounter = 0;
private boolean seleneAttackAnimationPlaying = false;
private static final int[] SELENE_ATTACK_FRAME_DURATIONS = {8, 8, 12}; // ticks (~0.6s total)

private ImageIcon[] enemySeleneAttackFrames = new ImageIcon[3];
private Timer enemySeleneAttackAnimationTimer;
private int currentEnemySeleneAttackFrame = 0;
private int enemySeleneAttackFrameCounter = 0;
private boolean enemySeleneAttackAnimationPlaying = false;

private ImageIcon[] seleneDamagedFrames = new ImageIcon[3];
private Timer seleneDamagedAnimationTimer;
private int currentSeleneDamagedFrame = 0;
private int seleneDamagedFrameCounter = 0;
private boolean seleneDamagedAnimationPlaying = false;
private static final int[] SELENE_DAMAGED_FRAME_DURATIONS = {12, 12, 16}; // ticks (~0.4s total)

private ImageIcon[] enemySeleneDamagedFrames = new ImageIcon[3];
private Timer enemySeleneDamagedAnimationTimer;
private int currentEnemySeleneDamagedFrame = 0;
private int enemySeleneDamagedFrameCounter = 0;
private boolean enemySeleneDamagedAnimationPlaying = false;

private ImageIcon[] valeriusAttackFrames = new ImageIcon[4];
private Timer valeriusAttackAnimationTimer;
private int currentValeriusAttackFrame = 0;
private int valeriusAttackFrameCounter = 0;
private boolean valeriusAttackAnimationPlaying = false;
private static final int[] VALERIUS_ATTACK_FRAME_DURATIONS = {8, 8, 8, 12}; // ticks (~0.6s total)

private ImageIcon[] valeriusDamagedFrames = new ImageIcon[3];
private Timer valeriusDamagedAnimationTimer;
private int currentValeriusDamagedFrame = 0;
private int valeriusDamagedFrameCounter = 0;
private boolean valeriusDamagedAnimationPlaying = false;
private static final int[] VALERIUS_DAMAGED_FRAME_DURATIONS = {12, 12, 16}; // ticks (~0.4s total)

private ImageIcon[] enemyValeriusAttackFrames = new ImageIcon[4];
private Timer enemyValeriusAttackAnimationTimer;
private int currentEnemyValeriusAttackFrame = 0;
private int enemyValeriusAttackFrameCounter = 0;
private boolean enemyValeriusAttackAnimationPlaying = false;

private ImageIcon[] enemyValeriusDamagedFrames = new ImageIcon[3];
private Timer enemyValeriusDamagedAnimationTimer;
private int currentEnemyValeriusDamagedFrame = 0;
private int enemyValeriusDamagedFrameCounter = 0;
private boolean enemyValeriusDamagedAnimationPlaying = false;

private JLabel enemyValeriusLargePortraitLabel;

private JLabel skyeLargePortraitLabel;
private JLabel enemySkyeLargePortraitLabel;

private JLabel morganaLargePortraitLabel;
private JLabel enemyMorganaLargePortraitLabel;

private JLabel aerisLargePortraitLabel;
private JLabel enemyAerisLargePortraitLabel;

private JLabel seleneLargePortraitLabel;
private JLabel enemySeleneLargePortraitLabel;

private JLabel flueLargePortraitLabel;
private JLabel enemyFlueLargePortraitLabel;

private JLabel jijiLargePortraitLabel;


private JLabel kaelLargePortraitLabel;
private JLabel enemyKaelLargePortraitLabel;

private JLabel enemyJijiLargePortraitLabel;
private ImageIcon[] enemyJijiIdleFrames = new ImageIcon[4];
private Timer enemyIdleAnimationTimer;
private int enemyCurrentIdleFrame = 0;
private int enemyIdleFrameCounter = 0;
private ImageIcon[] enemyJijiDamagedFrames = new ImageIcon[4];
private Timer enemyDamagedAnimationTimer;
private int enemyCurrentDamagedFrame = 0;
private int enemyDamagedFrameCounter = 0;
private ImageIcon[] enemyJijiAttackFrames = new ImageIcon[4];
private Timer enemyAttackAnimationTimer;
private int enemyCurrentAttackFrame = 0;
private int enemyAttackFrameCounter = 0;
private boolean enemyJijiIdleAnimationPlaying = false;
private boolean enemyJijiDamagedAnimationPlaying = false;
private boolean enemyJijiAttackAnimationPlaying = false;
       

    private void loadOceanBackground() {
    try {
       
        ImageIcon oceanIcon = new ImageIcon("assets/oceanfloor.png");
        oceanBackground = oceanIcon.getImage();
        System.out.println("✅ Ocean background loaded!");
    } catch (Exception e) {
        System.out.println("⚠️ Could not load ocean.png, using gradient background instead");
        oceanBackground = null;
    }
}

private void startMoonPhaseTimer() {
    if (moonPhaseTimer != null) {
        moonPhaseTimer.stop();
    }
    
    moonPhaseTimer = new Timer(1000, e -> {
        if (playerCharacter instanceof Selene && !playerTurn) {
            
            Selene selene = (Selene) playerCharacter;
            selene.updateMoonPhase();
            
            
            if (!selene.isNightTime() && selene.getTurnsUntilNight() > 0) {
                System.out.println("🌅 Day time. Night in " + selene.getTurnsUntilNight() + " turns");
            }
        }
    });
    moonPhaseTimer.start();
}

private class WaveBackgroundPanel extends JPanel {
    private Image backgroundImage;
    private float waveOffset = 0;
   private int shakeX = 0;
        private int shakeY = 0;
        private int shakeIntensity = 0;
        private Color flashColor = null;
        private Timer animTimer;
        private boolean animationEnabled = true;
    
    public WaveBackgroundPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        
        try {
            ImageIcon icon = new ImageIcon("assets/battle_background.png");
            backgroundImage = icon.getImage();
            System.out.println("✅ Battle background loaded!");
        } catch (Exception e) {
            System.out.println("⚠️ Could not load background image");
        }

        
        animTimer = new Timer(30, e -> {
            if (!animationEnabled) return;
            
            waveOffset += 0.05f;
            
            if (shakeIntensity > 0) {
                shakeX = (int)((Math.random() - 0.5) * shakeIntensity);
                shakeY = (int)((Math.random() - 0.5) * shakeIntensity);
                shakeIntensity -= 2; 
            } else {
                shakeX = 0;
                shakeY = 0;
            }
            repaint();
        });
        animTimer.start();
    }

    public void triggerShake(int intensity) {
        this.shakeIntensity = intensity;
    }

    public void triggerFlash(Color color) {
        this.flashColor = color;
        Timer t = new Timer(100, e -> {
            flashColor = null;
            repaint();
        });
        t.setRepeats(false);
        t.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        
        g2d.translate(shakeX, shakeY);
        
        int width = getWidth();
        int height = getHeight();
        
        
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, width, height, this);
        } else {
            
            GradientPaint gp = new GradientPaint(0, 0, new Color(20, 40, 80), 
                                                   0, height, new Color(10, 20, 50));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
        
        
        g2d.setColor(new Color(100, 180, 220, 60));
        for (int i = 0; i < 3; i++) {
            int yBase = height - 40 + i * 15;
            for (int x = 0; x < width + 50; x += 60) {
                int y = yBase + (int)(Math.sin(x * 0.04 + waveOffset + i) * 8);
                g2d.fillOval(x - 30, y, 60, 12);
            }
        }
        
        
        if (flashColor != null) {
            g2d.setColor(new Color(flashColor.getRed(), flashColor.getGreen(), flashColor.getBlue(), 100));
            g2d.fillRect(-50, -50, width + 100, height + 100);
        }
        
        g2d.dispose();
    }
    
    public void stopAnimation() {
        if (animTimer != null) {
                animTimer.stop();
        }
    }
}
    private interface SkillTargetCallback {
        void onTargetSelected(int x, int y);
    }

    private int enemySkillChance = 30; 
    private int lastEnemySkillTurn = 0;
    private String[] enemySkillMessages = {
        "Enemy uses a skill against you!",
        "The enemy activates their ability!",
        "Watch out! Enemy special attack!",
        "Enemy commander is using their power!",
        "Incoming enemy skill!"
    };
    private Random enemyRandom = new Random();

    
    
    private void startSeleneUpdateTimer(JPanel skillsPanel, Selene selene) {
        if (seleneUpdateTimer != null) {
            seleneUpdateTimer.stop();
        }
        
        seleneUpdateTimer = new Timer(500, e -> {
            if (playerTurn && playerCharacter instanceof Selene && skillsPanel != null) {
                updateSeleneSkillButtons(skillsPanel, selene);
            }
        });
        seleneUpdateTimer.start();
    }
    
    private void updateSeleneSkillButtons(JPanel skillsPanel, Selene selene) {
        Component[] components = skillsPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String text = btn.getText();
                
                if (text.contains("Lunar Reveal")) {
                    String status = selene.getSkillStatus(1);
                    boolean enabled = status.contains("Ready") || status.contains("ENHANCED");
                    btn.setEnabled(enabled);
                    if (!enabled) {
                        btn.setText("🔮 Lunar Reveal (" + status + ")");
                    } else {
                        int cost = selene.isNightTime() ? 30 : 60;
                        btn.setText("🔮 Lunar Reveal (" + cost + ")" + (selene.isNightTime() ? " 🌙" : ""));
                    }
                } else if (text.contains("Crescent Strike")) {
                    String status = selene.getSkillStatus(2);
                    boolean enabled = status.contains("Ready") || status.contains("ENHANCED");
                    btn.setEnabled(enabled);
                    if (!enabled) {
                        btn.setText("🌙 Crescent Strike (" + status + ")");
                    } else {
                        int cost = selene.isNightTime() ? 60 : 120;
                        btn.setText("🌙 Crescent Strike (" + cost + ")" + (selene.isNightTime() ? " 🌙" : ""));
                    }
                } else if (text.contains("Starfall Link")) {
                    String status = selene.getSkillStatus(3);
                    boolean enabled = status.contains("READY") || status.contains("Ready");
                    btn.setEnabled(enabled);
                    if (!enabled) {
                        btn.setText("⭐ Starfall Link (" + status + ")");
                    } else {
                        int cost = selene.isNightTime() ? 150 : 300;
                        btn.setText("⭐ Starfall Link (" + cost + ")" + (selene.isNightTime() ? " 🌙" : ""));
                    }
                }
            }
        }
        
        
        for (Component comp : components) {
            if (comp instanceof JLabel && ((JLabel) comp).getText().contains("Mana:")) {
                ((JLabel) comp).setText(selene.getManaBar());
            }
        }
    }

    
    
    private void useEnemySkill() {
        if (currentEnemy == null) return;
        
        System.out.println("🤖 Enemy AI considering skill use...");
        
        
        if (playerCharacter instanceof Valerius) {
            Valerius valerius = (Valerius) playerCharacter;
            if (valerius.areEnemySkillsDisabled()) {
                System.out.println("🚫 Enemy skills are disabled by Radar Overload!");
                return;
            }
        }
        
        
        if (enemyRandom.nextInt(100) < enemySkillChance) {
            
            if (currentEnemy instanceof Jiji) {
                useJijiEnemySkill();
            } else if (currentEnemy instanceof Kael) {
                useKaelEnemySkill();
            } else if (currentEnemy instanceof Valerius) {
                useValeriusEnemySkill();
            } else if (currentEnemy instanceof Skye) {
                useSkyeEnemySkill();
            } else if (currentEnemy instanceof Morgana) {
                useMorganaEnemySkill();
            } else if (currentEnemy instanceof Selene) {
                useSeleneEnemySkill();
            } else if (currentEnemy instanceof Aeris) {
                useAerisEnemySkill();
            } else if (currentEnemy instanceof Flue) {
                useFlueEnemySkill();
            }
        } else {
            System.out.println("🤖 Enemy decides to just attack normally.");
        }
    }

    private void useAerisEnemySkill() {
        Aeris aeris = (Aeris) currentEnemy;
        int skillChoice = enemyRandom.nextInt(3);
        
        switch(skillChoice) {
            case 0: 
                if (aeris.hasEnoughMana(120)) {
                    
                    for (Ship s : enemyBoard.getShips()) {
                        if (!s.isSunk() && !s.isShielded()) {
                            Ship.Coordinate pos = s.getPositions().get(0);
                            aeris.useAdaptiveInstinct(enemyBoard, pos.getX(), pos.getY());
                            showEnemySkillMessage("Aeris shields one of his ships!");
                            break;
                        }
                    }
                }
                break;
            case 1: 
                aeris.useMultitaskOverdrive();
                showEnemySkillMessage("Aeris uses Overdrive to restore mana!");
                break;
            case 2: 
                if (aeris.hasEnoughMana(500)) {
                    int targetCol = enemyRandom.nextInt(10);
                    aeris.useRelentlessAscent(playerBoard, targetCol);
                    showEnemySkillMessage("Aeris strikes a full column with Relentless Ascent!");
                }
                break;
        }
    }

    private void useFlueEnemySkill() {
        Flue flue = (Flue) currentEnemy;
        int skillChoice = enemyRandom.nextInt(3);
        
        switch(skillChoice) {
            case 0: 
                if (flue.hasEnoughMana(100)) {
                    int x = enemyRandom.nextInt(10);
                    int y = enemyRandom.nextInt(10);
                    flue.useCorruption(playerBoard, x, y);
                    showEnemySkillMessage("Flue initiates a virus in your fleet!");
                }
                break;
            case 1: 
                if (flue.hasEnoughMana(80)) {
                    
                    flue.useFortification(enemyBoard, enemyRandom.nextInt(10), enemyRandom.nextInt(10));
                    showEnemySkillMessage("Flue is repairing system integrity!");
                }
                break;
            case 2: 
                if (flue.hasEnoughMana(300)) {
                    int x = enemyRandom.nextInt(10);
                    int y = enemyRandom.nextInt(10);
                    flue.useKernelDecimation(playerBoard, x, y);
                    showEnemySkillMessage("Flue executes Kernel Decimation!");
                }
                break;
        }
    }

    private void useMorganaEnemySkill() {
        Morgana morgana = (Morgana) currentEnemy;
        int skillChoice = enemyRandom.nextInt(3);
        
        switch(skillChoice) {
            case 0:
                if (morgana.hasEnoughMana(40)) {
                    morgana.useEnchantingMelody();
                    showEnemySkillMessage("Morgana's song confuses you!");
                }
                break;
            case 1:
                if (morgana.hasEnoughMana(80)) {
                    int x = enemyRandom.nextInt(10);
                    int y = enemyRandom.nextInt(10);
                    morgana.useWhirlpoolTrap(playerBoard, x, y);
                    showEnemySkillMessage("Morgana creates a whirlpool trap!");
                }
                break;
            case 2:
                if (morgana.hasEnoughMana(300)) {
                     morgana.useTidalWave(playerBoard);
                    showEnemySkillMessage("Morgana summons a storm!");
                }
                break;
        }
    }

    private void healPlayerShips() {
        System.out.println("🏥 Healing player ships between waves...");
        
        
        for (Ship ship : playerBoard.getShips()) {
            ship.heal();
            ship.setShielded(false, 0);
        System.out.println("🛡️ Removed shield from " + ship.getName());
        }

        // Reset Jiji's damage state between waves
        if (playerCharacter instanceof Jiji) {
            ((Jiji) playerCharacter).setDamaged(false);
            System.out.println("🔄 Jiji recovered from damage between waves");
        }
        
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Cell cell = playerBoard.getCell(i, j);
                cell.resetFiredUpon();
                cell.setRevealed(false);
            }
        }
        if (playerBoardPanel != null) {
        playerBoardPanel.refreshColors();
    }
        
        refreshBoardsOnly();
         updateShipCounters();

        System.out.println("✅ Player ships healed and shields removed!");
    }

    private void useSeleneEnemySkill() {
        Selene enemySelene = (Selene) currentEnemy;
        
        int skillChoice = enemyRandom.nextInt(3);
        
        switch(skillChoice) {
            case 0: 
                if (enemySelene.hasEnoughMana(60)) {
                    System.out.println("🔮 Enemy Selene uses LUNAR REVEAL!");
                    int x = enemyRandom.nextInt(10);
                    int y = enemyRandom.nextInt(10);
                    enemySelene.useLunarReveal(playerBoard, x, y);
                    showEnemySkillMessage("Selene reveals a 3x3 area of your board!");
                }
                break;
            case 1: 
                if (enemySelene.hasEnoughMana(120)) {
                    System.out.println("🌙 Enemy Selene uses CRESCENT STRIKE!");
                    int x = enemyRandom.nextInt(10);
                    int y = enemyRandom.nextInt(10);
                    int destroyed = enemySelene.useCrescentStrike(playerBoard, x, y);
                    if (destroyed > 0) {
                        showEnemySkillMessage("Selene's Crescent Strike destroyed " + destroyed + " cells!");
                    }
                }
                break;
            case 2: 
                if (enemySelene.hasEnoughMana(300)) {
                    System.out.println("⭐ Enemy Selene uses STARFALL LINK!");
                    enemySelene.useStarfallLink(playerBoard);
                    showEnemySkillMessage("Selene's Starfall Link destroys random cells and links your ships!");
                }
                break;
        }
    }
    
    private void useJijiEnemySkill() {
        Jiji enemyJiji = (Jiji) currentEnemy;
        int skillChoice = enemyRandom.nextInt(3);
        
        switch(skillChoice) {
            case 0: 
                if (enemyJiji.hasEnoughMana(50)) {
                    enemyJiji.useDataLeech(playerBoard);
                    showEnemySkillMessage("Jiji uses Data Leech on your fleet!");
                }
                break;
            case 1: 
                if (enemyJiji.hasEnoughMana(120)) {
                    enemyJiji.useOverclock();
                    showEnemySkillMessage("Jiji overclocks! Their next shot will fire twice!");
                }
                break;
            case 2: 
                if (enemyJiji.hasEnoughMana(400)) {
                    enemyJiji.useSystemOverload(playerBoard);
                    showEnemySkillMessage("Jiji overloads your systems!");
                }
                break;
        }
    }

    private void useKaelEnemySkill() {
        Kael enemyKael = (Kael) currentEnemy;
        int skillChoice = enemyRandom.nextInt(3);
        
        switch(skillChoice) {
            case 0: 
                if (enemyKael.hasEnoughEnergy(100)) {
                    showEnemySkillMessage("Kael teleports one of their ships!");
                }
                break;
            case 1: 
                if (enemyKael.hasEnoughEnergy(150)) {
                    int[] target = getRandomUnfiredCellForEnemySkill();
                    int x = target[0];
                    int y = target[1];
                    boolean horizontal = enemyRandom.nextBoolean();
                    int destroyed = enemyKael.useShadowBlade(playerBoard, x, y, horizontal);
                    if (destroyed > 0) {
                        showEnemySkillMessage("Kael's shadow blade destroyed " + destroyed + " cells!");
                    }
                }
                break;
            case 2: 
                if (enemyKael.hasEnoughEnergy(200)) {
                    // Get a random cell that hasn't been fired upon
                    int[] target = getRandomUnfiredCellForEnemySkill();
                    int x = Math.min(target[0], 8);
                    int y = Math.min(target[1], 8);
                    int destroyed = enemyKael.useShadowDomain(playerBoard, x, y);
                    if (destroyed > 0) {
                        showEnemySkillMessage("Kael's shadow domain destroyed " + destroyed + " cells!");
                    }
                }
                break;
        }
    }

    private void useValeriusEnemySkill() {
        Valerius enemyValerius = (Valerius) currentEnemy;
        int skillChoice = enemyRandom.nextInt(3);
        
        switch(skillChoice) {
            case 0: 
                if (enemyValerius.hasEnoughMana(50)) {
                    enemyValerius.useRadarOverload();
                    showEnemySkillMessage("Valerius jams your radar!");
                }
                break;
            case 1: 
                if (enemyValerius.hasEnoughMana(120)) {
                    boolean horizontal = enemyRandom.nextBoolean();
                    int x = enemyRandom.nextInt(10);
                    int y = enemyRandom.nextInt(10);
                    int destroyed = enemyValerius.applyPrecisionStrike(playerBoard, x, y, horizontal);
                    if (destroyed > 0) {
                        showEnemySkillMessage("Valerius's precision strike destroyed " + destroyed + " cells!");
                    }
                }
                break;
            case 2: 
                if (enemyValerius.hasEnoughMana(300)) {
                    enemyValerius.useFortressMode();  
                    showEnemySkillMessage("Valerius shields ALL his ships for 2 turns!");
                }
                break;
        }
    }

    private void useSkyeEnemySkill() {
        Skye enemySkye = (Skye) currentEnemy;
        int skillChoice = enemyRandom.nextInt(3);  
        
        switch(skillChoice) {
            case 0: 
                if (enemySkye.hasEnoughMana(70)) {
                    int x = enemyRandom.nextInt(8);
                    int y = enemyRandom.nextInt(8);
                    enemySkye.useCatnipExplosion(playerBoard, x, y);
                    showEnemySkillMessage("Skye detonates a catnip bomb near your fleet!");
                }
                break;
            case 1: 
                if (enemySkye.hasEnoughMana(50)) {
                    enemySkye.useLaserPointer();
                    showEnemySkillMessage("Skye distracts you with a laser pointer!");
                }
                break;
            case 2: 
                if (enemySkye.hasEnoughMana(200)) {
                    Ship sunkShip = findSunkShip(enemyBoard);
                    if (sunkShip != null) {
                        Ship.Coordinate pos = sunkShip.getPositions().get(0);
                        enemySkye.useNineLives(enemyBoard, pos.getX(), pos.getY());
                        showEnemySkillMessage("Skye revives one of her ships! Nine Lives!");
                    }
                }
                break;
        }
    }

    private Ship findSunkShip(Board board) {
        for (Ship ship : board.getShips()) {
            if (ship.isSunk()) {
                return ship;
            }
        }
        return null;
    }

    private void showEnemySkillMessage(String message) {
        updateStatusLabel("🤖 " + message, Color.ORANGE);
        
        Timer resetTimer = new Timer(2000, e -> resetStatusLabel());
        resetTimer.setRepeats(false);
        resetTimer.start();
    }

    private void onPlayerSkillUse(String skillName, boolean success) {
        if (success) {
            updateStatusLabel("✨ " + skillName + " activated!", Color.GREEN);
        } else {
            updateStatusLabel("❌ " + skillName + " not ready!", Color.RED);
        }
        
        Timer resetTimer = new Timer(1500, e -> resetStatusLabel());
        resetTimer.setRepeats(false);
        resetTimer.start();
    }
    
    private void startTargetSelection(String skillName, SkillTargetCallback callback, JButton skillButton) {
        waitingForTarget = true;
        currentSkillName = skillName;
        targetCallback = callback;
        lastClickedSkillButton = skillButton;
        
        JOptionPane.showMessageDialog(frame,
            "🎯 " + skillName + "\n\n" +
            "Click on the ENEMY board to select target.\n" +
            "Click CANCEL to abort.",
            "Select Target",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public CampaignMode(JFrame frame, GameCharacter playerCharacter) {
        this.frame = frame;
        this.playerCharacter = playerCharacter;
        this.playerBoard = new Board();
        this.enemyBoard = new Board();
        this.waves = new ArrayList<>();
        this.possibleEnemies = new ArrayList<>();
    
        
        initializePossibleEnemies();
        generateRandomWaves();
        // Pre-load animation frames
        initJijiIdleFrames();
        initKaelIdleFrames();
        initEnemyKaelIdleFrames();
        initValeriusIdleFrames();
        initEnemyValeriusIdleFrames();
        initSkyeIdleFrames();
        initEnemySkyeIdleFrames();
        initSkyeAttackFrames();
        initSkyeDamagedFrames();
        initEnemySkyeAttackFrames();
        initEnemySkyeDamagedFrames();
        initMorganaIdleFrames();
        initEnemyMorganaIdleFrames();
        initMorganaAttackFrames();
        initMorganaDamagedFrames();
        initEnemyMorganaAttackFrames();
        initEnemyMorganaDamagedFrames();
        initAerisIdleFrames();
        initEnemyAerisIdleFrames();
        initAerisAttackFrames();
        initAerisDamagedFrames();
        initEnemyAerisAttackFrames();
        initEnemyAerisDamagedFrames();
        initSeleneIdleFrames();
        initEnemySeleneIdleFrames();
        initFlueIdleFrames();
        initEnemyFlueIdleFrames();
        initFlueAttackFrames();
        initFlueDamagedFrames();
        initEnemyFlueAttackFrames();
        initEnemyFlueDamagedFrames();
        initSeleneAttackFrames();
        initSeleneDamagedFrames();
        initEnemySeleneAttackFrames();
        initEnemySeleneDamagedFrames();
        initAerisDamagedFrames();
        initEnemyAerisAttackFrames();
        initEnemyAerisDamagedFrames();
        initMorganaDamagedFrames();
        initEnemyMorganaAttackFrames();
        initEnemyMorganaDamagedFrames();
        initValeriusAttackFrames();
        initValeriusDamagedFrames();
        initEnemyValeriusAttackFrames();
        initEnemyValeriusDamagedFrames();
        initKaelAttackFrames();
        initEnemyKaelAttackFrames();
        initKaelDamagedFrames();
        initEnemyKaelDamagedFrames();
    }
    
    public void refreshSkillPanels() {
        if (currentWaveIndex < waves.size()) {
            createBattleUI(waves.get(currentWaveIndex));
        }
    }
    
    private void initializePossibleEnemies() {
        Jiji jiji = new Jiji();
        Kael kael = new Kael();
        Valerius valerius = new Valerius();
        Skye skye = new Skye();
        Morgana morgana = new Morgana(); 
        Aeris aeris = new Aeris();
        Selene selene = new Selene(); 
        Flue flue = new Flue();    
        
        if (!playerCharacter.getName().equals(jiji.getName())) {
            possibleEnemies.add(jiji);
        }
        if (!playerCharacter.getName().equals(kael.getName())) {
            possibleEnemies.add(kael);
        }
        if (!playerCharacter.getName().equals(valerius.getName())) {
            possibleEnemies.add(valerius);
        }
        if (!playerCharacter.getName().equals(skye.getName())) {
            possibleEnemies.add(skye);
        }
        if (!playerCharacter.getName().equals(morgana.getName())) {  
            possibleEnemies.add(morgana);
        }  
        if (!playerCharacter.getName().equals(aeris.getName())) {
            possibleEnemies.add(aeris);
        }
        if (!playerCharacter.getName().equals(selene.getName())) {
            possibleEnemies.add(selene);
        }
        if(!playerCharacter.getName().equals(flue.getName())) {
            possibleEnemies.add(flue);
        }

        Collections.shuffle(possibleEnemies);
        
        System.out.println("🎲 Possible enemies: " + possibleEnemies.size());
        for (GameCharacter enemy : possibleEnemies) {
            System.out.println("   - " + enemy.getName());
        }
    }
    
    private void generateRandomWaves() {
        waves.clear();
         if (testMode) {
        System.out.println("🧪 TEST MODE ENABLED - Fighting: " + testEnemyName);

        GameCharacter testEnemy = null;


        switch(testEnemyName) {
            case "Jiji":
                testEnemy = new Jiji();
                break;
            case "Kael":
                testEnemy = new Kael();
                break;
            case "Valerius":
                testEnemy = new Valerius();
                break;
            case "Skye":
                testEnemy = new Skye();
                break;
            case "Morgana":
                testEnemy = new Morgana();
                break;
            case "Aeris":
                testEnemy = new Aeris();
                break;
            case "Selene":
                testEnemy = new Selene();
                break;
            case "Flue":
                testEnemy = new Flue();
                break;
            default:
                testEnemy = new Skye();
                break;
        }

        waves.add(new CampaignWave(
            "🧪 TEST WAVE",
            "Testing: " + testEnemy.getName(),
            testEnemy,
            Color.MAGENTA
        ));
        return;
    }
        int numWaves = random.nextInt(3) + 3; 
        System.out.println("🎲 Generating " + numWaves + " random waves...");
        
        List<GameCharacter> enemyPool = new ArrayList<>(possibleEnemies);
        
        for (int i = 0; i < numWaves; i++) {
            if (enemyPool.isEmpty()) {
                enemyPool = new ArrayList<>(possibleEnemies);
                Collections.shuffle(enemyPool);
            }
            
            GameCharacter randomEnemy = enemyPool.remove(0);
            String waveTitle = getRandomWaveTitle(i + 1);
            Color waveColor = getRandomWaveColor();
            
            waves.add(new CampaignWave(
                waveTitle,
                "Enemy: " + randomEnemy.getName(),
                randomEnemy,
                waveColor
            ));
            
            System.out.println("   Wave " + (i + 1) + ": " + randomEnemy.getName());
        }
    }
    
    private String getRandomWaveTitle(int waveNumber) {
        String[] titles = {
            "🌊 Digital Storm", "⚡ Thunderous Assault", "🌫️ Misty Encounter",
            "🔥 Burning Tides", "❄️ Frozen Depths", "💀 Shadow Strike",
            "🌀 Maelstrom", "⚓ Naval Clash", "🌪️ Tempest Fury", "🛡️ Iron Wall"
        };
        
        String[] difficulties = {
            "Skirmish", "Encounter", "Battle", "Clash", "Confrontation",
            "Assault", "Siege", "War"
        };
        
        String randomTitle = titles[random.nextInt(titles.length)];
        String randomDifficulty = difficulties[random.nextInt(difficulties.length)];
        
        return "🌊 WAVE " + waveNumber + ": " + randomTitle + " - " + randomDifficulty;
    }
    
    private Color getRandomWaveColor() {
        Color[] colors = {
            new Color(255, 99, 71), new Color(255, 215, 0), new Color(50, 205, 50),
            new Color(30, 144, 255), new Color(186, 85, 211), new Color(255, 140, 0),
            new Color(0, 255, 127), new Color(255, 105, 180)
        };
        return colors[random.nextInt(colors.length)];
    }
    
    public void start() {
        System.out.println("🎮 Campaign started with: " + playerCharacter.getName());
        System.out.println("🎲 Random waves generated!");
        showPlacementScreen();
    }
    
    private void showPlacementScreen() {
        PlacementPanel placementPanel = new PlacementPanel(new PlacementPanel.PlacementListener() {
            @Override
            public void onPlacementComplete(Board board) {
                playerBoard = board;
                System.out.println("✅ Ships placed! Starting campaign with " + waves.size() + " waves...");
                loadWave(0);
            }
        });
        
        frame.getContentPane().removeAll();
        frame.add(placementPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
    
    private void loadWave(int index) {
    if (index >= waves.size()) {
        updateStatusLabel("🏆 CAMPAIGN COMPLETE! Victory!", Color.ORANGE);
        showVictoryScreen();
        return;
    }
    
    CampaignWave wave = waves.get(index);
    currentEnemy = wave.enemy;
    enemyBoard = new Board();

    if (playerCharacter instanceof Aeris) {
        ((Aeris) playerCharacter).setPlayerBoard(playerBoard);
    }
    if (playerCharacter instanceof Kael) {
        ((Kael) playerCharacter).setPlayerBoard(playerBoard);
    }
    if (playerCharacter instanceof Valerius) {
        ((Valerius) playerCharacter).setPlayerBoard(playerBoard);
    }
    if (playerCharacter instanceof Skye) {
        ((Skye) playerCharacter).setPlayerBoard(playerBoard);
    }
    
    String waveMessage = String.format("🌊 WAVE %d/%d - VS %s", 
        index + 1, waves.size(), currentEnemy.getName());
    updateStatusLabel(waveMessage, Color.YELLOW);
    
    adjustEnemyDifficulty(index + 1);
    placeEnemyShips(currentEnemy, enemyBoard);
    createBattleUI(wave);
    
    
    if (playerCharacter instanceof Selene) {
        startMoonPhaseTimer();
    }
}
    
    private void adjustEnemyDifficulty(int waveNumber) {
        enemySkillChance = 20 + (waveNumber * 5); 
        if (enemySkillChance > 80) {
            enemySkillChance = 80;
        }
        System.out.println("⚔️ Wave " + waveNumber + " difficulty: " + enemySkillChance + "% skill chance");
        
        if (waveNumber > 2) {
            System.out.println("🧠 Enemy AI is now using STRATEGIC thinking!");
        }
        if (waveNumber >= 4) {
            System.out.println("💀 BOSS WAVE! Enemy is enraged!");
        }
    }
    
    private void placeEnemyShips(GameCharacter enemy, Board board) {
    // All enemies now get random ship placement
    placeRandomShips(board);
}
    
    private void placeDefaultShips(Board board) {
        board.placeShip(new Ship("Carrier", 5), 0, 0, true);
        board.placeShip(new Ship("Battleship", 4), 2, 0, true);
        board.placeShip(new Ship("Cruiser", 3), 4, 0, true);
        board.placeShip(new Ship("Submarine", 3), 6, 0, true);
        board.placeShip(new Ship("Destroyer", 2), 8, 0, true);
    }
    
    private void placeRandomShips(Board board) {
        Random rand = new Random();
        String[] names = {"Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"};
        int[] sizes = {5, 4, 3, 3, 2};
        
        for (int i = 0; i < names.length; i++) {
            Ship ship = new Ship(names[i], sizes[i]);
            boolean placed = false;
            int attempts = 0;
            
            while (!placed && attempts < 100) {
                int x = rand.nextInt(10);
                int y = rand.nextInt(10);
                boolean horizontal = rand.nextBoolean();
                placed = board.placeShip(ship, x, y, horizontal);
                attempts++;
            }
        }
    }
    
 
 // ===================================================================
// REDESIGNED createBattleUI - TideBound aesthetic with preserved animations
// ===================================================================
// 
// INSTRUCTIONS:
// Replace your existing createBattleUI method (line 1114 onwards) with this entire method.
// All animation code is preserved exactly as your teammate built it.
// 
private void createBattleUI(CampaignWave wave) {
    // ===============================================================
    // STEP 1: Stop all animations from previous battle (PRESERVED)
    // ===============================================================
    stopIdleAnimation();
    stopDamagedAnimation();
    stopAttackAnimation();
    stopEnemyIdleAnimation();
    stopEnemyDamagedAnimation();
    stopEnemyAttackAnimation();
    stopKaelIdleAnimation();
    stopEnemyKaelIdleAnimation();
    stopValeriusIdleAnimation();
    stopEnemyValeriusIdleAnimation();
    stopSkyeIdleAnimation();
    stopEnemySkyeIdleAnimation();
    stopSkyeAttackAnimation();
    stopEnemySkyeAttackAnimation();
    stopSkyeDamagedAnimation();
    stopEnemySkyeDamagedAnimation();
    stopMorganaIdleAnimation();
    stopMorganaDamagedAnimation();
    stopEnemyMorganaIdleAnimation();
    stopEnemyMorganaDamagedAnimation();
    stopAerisIdleAnimation();
    stopAerisAttackAnimation();
    stopAerisDamagedAnimation();
    stopSeleneIdleAnimation();
    stopEnemyAerisIdleAnimation();
    stopEnemyAerisAttackAnimation();
    stopEnemyAerisDamagedAnimation();
    stopEnemySeleneIdleAnimation();
    stopFlueIdleAnimation();
    stopEnemyFlueIdleAnimation();
    stopSeleneAttackAnimation();
    stopSeleneDamagedAnimation();
    stopFlueAttackAnimation();
    stopEnemySeleneAttackAnimation();
    stopEnemySeleneDamagedAnimation();
    stopFlueDamagedAnimation();
    stopEnemyFlueDamagedAnimation();
    stopEnemyFlueAttackAnimation();
    stopKaelAttackAnimation();
    stopEnemyKaelAttackAnimation();
    stopValeriusAttackAnimation();
    stopEnemyValeriusAttackAnimation();
    stopKaelDamagedAnimation();
    stopEnemyKaelDamagedAnimation();
    stopValeriusDamagedAnimation();
    stopEnemyValeriusDamagedAnimation();

    frame.getContentPane().removeAll();
    frame.setLayout(new BorderLayout());

    // ===============================================================
    // STEP 2: Create TideBound visual container
    // ===============================================================
    JPanel mainPanel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            
            // Deep ocean gradient background
            g2.setPaint(new GradientPaint(0, 0, new Color(0x0F, 0x23, 0x26), 
                                          0, getHeight(), new Color(0x08, 0x18, 0x1A)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            g2.dispose();
        }
    };
    mainPanel.setLayout(new BorderLayout());
    mainPanel.setOpaque(true);

    // ===============================================================
    // STEP 3: Turn Banner (top)
    // ===============================================================
    JPanel turnBanner = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth(), h = getHeight();
            
            // Metal bar background
            g2.setPaint(new GradientPaint(0, 0, new Color(0x2A, 0x5A, 0x5E), 0, h, new Color(0x1E, 0x45, 0x48)));
            g2.fillRect(0, 0, w, h);
            g2.setColor(new Color(0x3A, 0x7A, 0x7E));
            g2.fillRect(0, h - 3, w, 1);
            g2.setColor(new Color(0x08, 0x18, 0x1A));
            g2.fillRect(0, h - 2, w, 2);
            
            // BACK button (top-left)
            int bw = 110, bh = 36, bx = 16, by = 16;
            g2.setColor(new Color(0x08, 0x18, 0x1A));
            g2.fillRoundRect(bx + 2, by + 3, bw, bh, 6, 6);
            g2.setPaint(new GradientPaint(0, by, new Color(0x2A, 0x5A, 0x5E), 0, by + bh, new Color(0x1E, 0x45, 0x48)));
            g2.fillRoundRect(bx, by, bw, bh, 6, 6);
            g2.setStroke(new BasicStroke(1.6f));
            g2.setColor(new Color(0x3A, 0x7A, 0x7E));
            g2.drawRoundRect(bx, by, bw, bh, 6, 6);
            
            Font smallFont = new Font("Consolas", Font.PLAIN, 11);
            g2.setFont(smallFont);
            g2.setColor(new Color(0x8A, 0xA8, 0xAC));
            String backText = "← BACK";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(backText, bx + (bw - fm.stringWidth(backText)) / 2, by + (bh + fm.getAscent()) / 2 - 2);
            
            // Central engraved nameplate with wave info
            String waveText = String.format("WAVE %d/%d - VS %s — %s", 
                currentWaveIndex + 1, waves.size(), 
                currentEnemy.getName(), currentEnemy.getAbilityName());
            
            Font headerFont = new Font("Consolas", Font.BOLD, 18);
            g2.setFont(headerFont);
            fm = g2.getFontMetrics();
            int plateW = Math.min(fm.stringWidth(waveText) + 80, w - 400);
            int plateH = 56;
            int px = (w - plateW) / 2;
            int py = (h - plateH) / 2;
            
            // Engraved plate
            g2.setColor(new Color(0x08, 0x18, 0x1A));
            g2.fillRoundRect(px + 2, py + 2, plateW, plateH, 8, 8);
            g2.setPaint(new GradientPaint(0, py, new Color(0x12, 0x2A, 0x2E), 0, py + plateH, new Color(0x08, 0x18, 0x1A)));
            g2.fillRoundRect(px, py, plateW, plateH, 8, 8);
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(new Color(0x5F, 0xD4, 0xE0));
            g2.drawRoundRect(px, py, plateW, plateH, 8, 8);
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(new Color(0x5F, 0xD4, 0xE0, 40));
            g2.drawRoundRect(px + 4, py + 4, plateW - 8, plateH - 8, 6, 6);
            
            // Wave text
            int tx = px + (plateW - fm.stringWidth(waveText)) / 2;
            int ty = py + (plateH + fm.getAscent()) / 2 - 4;
            g2.setColor(new Color(0x08, 0x18, 0x1A));
            g2.drawString(waveText, tx + 1, ty + 1);
            g2.setColor(Color.YELLOW);
            g2.drawString(waveText, tx, ty);
            
            // Turn indicator on left of plate
            String turnTag = playerTurn ? "YOU" : "AI";
            Color tagColor = playerTurn ? new Color(0x5F, 0xD4, 0xE0) : new Color(0xE0, 0x5F, 0x5F);
            Font bigFont = new Font("Consolas", Font.BOLD, 24);
            g2.setFont(bigFont);
            fm = g2.getFontMetrics();
            int tagX = px - fm.stringWidth(turnTag) - 24;
            int tagY = py + (plateH + fm.getAscent()) / 2 - 4;
            g2.setColor(new Color(0x08, 0x18, 0x1A));
            g2.drawString(turnTag, tagX + 1, tagY + 1);
            g2.setColor(tagColor);
            g2.drawString(turnTag, tagX, tagY);
            
            g2.dispose();
        }
    };
    turnBanner.setPreferredSize(new Dimension(0, 100));
    turnBanner.setOpaque(false);

    turnBanner.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent e) {
        // Check if click was on BACK button (top-left 110x36 at position 16,16)
        int x = e.getX();
        int y = e.getY();
        if (x >= 16 && x <= 126 && y >= 16 && y <= 52) {
            int confirm = JOptionPane.showConfirmDialog(frame,
                "Return to main menu? Progress will be lost.",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (turnTimer != null) turnTimer.stopTimer();
                if (enemyTurnTimer != null) enemyTurnTimer.stopTimer();
                    stopIdleAnimation();
                    stopDamagedAnimation();
                    stopAttackAnimation();
                    stopEnemyIdleAnimation();
                    stopEnemyDamagedAnimation();
                    stopEnemyAttackAnimation();
                    main.Main.showMainMenu();
            }
        }
    }
});
    
    // Create timer panels (preserved from original)
turnTimer = new TimerPanel(30, () -> {
    System.out.println("⏰ TIME'S UP! Auto-ending turn...");
    updateStatusLabel("⏰ TIME'S UP! Auto-ending turn...", Color.RED);
    endTurn();
});

enemyTurnTimer = new TimerPanel(10, () -> {
    System.out.println("⏰ ENEMY TIME'S UP! Switching to player...");
    updateStatusLabel("⏰ Enemy took too long! Your turn!", Color.GREEN);
    playerTurn = true;
    if (enemyTurnTimer != null) {
        enemyTurnTimer.stopTimer();
        enemyTurnTimer.setVisible(false);
    }
    if (turnTimer != null) {
        turnTimer.setTimerLabel("Your Turn");
        turnTimer.setVisible(true);
        turnTimer.startTimer();
    }
    onPlayerTurnStart();
    cancelAllSkillTargeting();
    if (currentSkillPanel != null) currentSkillPanel.updateUI();
});

// Combine turn banner + timer in top area
JPanel topArea = new JPanel(new BorderLayout());
topArea.setOpaque(false);
topArea.add(turnBanner, BorderLayout.CENTER);

JPanel timerPanel = new JPanel(new GridLayout(1, 1, 0, 5));
timerPanel.setOpaque(false);
turnTimer.setUseCustomLabel(true);
turnTimer.setTimerLabel("Your Turn");
turnTimer.setVisible(true);
enemyTurnTimer.setUseCustomLabel(true);
enemyTurnTimer.setTimerLabel("Enemy Turn");
enemyTurnTimer.setVisible(false);
timerPanel.add(turnTimer);
timerPanel.add(enemyTurnTimer);
topArea.add(timerPanel, BorderLayout.SOUTH);

mainPanel.add(topArea, BorderLayout.NORTH);

    // ===============================================================
    // STEP 4: Board area (center) - tactical ocean + boards
    // ===============================================================
    loadOceanBackground();
    
    playerBoardPanel = new BoardPanel(true, playerBoard, true);
    enemyBoardPanel = new BoardPanel(false, enemyBoard, false);
    
    playerBoardPanel.setCellWidth(92);
playerBoardPanel.setCellHeight(64);
enemyBoardPanel.setCellWidth(92);
enemyBoardPanel.setCellHeight(64);
    if (playerCharacter instanceof Flue) {
        ((Flue) playerCharacter).setEnemyBoard(enemyBoard);
    }
    
    setupClickHandlers();
    
    JPanel boardArea = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth(), h = getHeight();
            
            // Tactical ocean background (calm dark-teal gradient with wave shimmer)
            g2.setPaint(new GradientPaint(0, 0, new Color(0x0E, 0x28, 0x2E), 0, h, new Color(0x08, 0x18, 0x1C)));
            g2.fillRect(0, 0, w, h);
            
            // Wave shimmer bands
            long t = System.currentTimeMillis();
            int offset = (int)((t / 80) % 40);
            g2.setColor(new Color(255, 255, 255, 8));
            for (int i = 0; i < 6; i++) {
                int y = (i * h / 5 + offset) % h;
                g2.fillRect(0, y, w, 2);
            }
            
            // Cyan grid lines
            g2.setColor(new Color(0x5F, 0xD4, 0xE0, 30));
            int gridStep = 60;
            for (int x = 0; x < w; x += gridStep) g2.drawLine(x, 0, x, h);
            for (int y = 0; y < h; y += gridStep) g2.drawLine(0, y, w, y);
            
            g2.dispose();
        }
    };
    boardArea.setLayout(new GridLayout(1, 2, 20, 0));
    boardArea.setOpaque(false);
    boardArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Left board (player)
    JPanel leftBoardFrame = new JPanel(new BorderLayout());
    leftBoardFrame.setOpaque(false);
    leftBoardFrame.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(0, 255, 0, 150), 2),
        "⚓ YOUR FLEET",
        TitledBorder.CENTER,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 16),
        new Color(0, 255, 0, 200)
    ));
    
    JPanel charInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
    charInfoPanel.setOpaque(false);
    JLabel charNameLabel = new JLabel(getCharacterEmoji(playerCharacter) + " " + playerCharacter.getName());
    charNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
    charNameLabel.setForeground(Color.CYAN);
    charInfoPanel.add(charNameLabel);
    leftBoardFrame.add(charInfoPanel, BorderLayout.NORTH);
    leftBoardFrame.add(playerBoardPanel, BorderLayout.CENTER);
    
    playerShipLabel = new JLabel(getShipCountText(playerBoard), SwingConstants.CENTER);
    playerShipLabel.setFont(new Font("Arial", Font.BOLD, 12));
    playerShipLabel.setForeground(Color.WHITE);
    leftBoardFrame.add(playerShipLabel, BorderLayout.SOUTH);
    
    // Right board (enemy)
    JPanel rightBoardFrame = new JPanel(new BorderLayout());
    rightBoardFrame.setOpaque(false);
    rightBoardFrame.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(255, 0, 0, 150), 2),
        "ENEMY WATERS",
        TitledBorder.CENTER,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 16),
        new Color(255, 0, 0, 200)
    ));
    
    JPanel enemyTopPanel = new JPanel(new BorderLayout());
    enemyTopPanel.setOpaque(false);
    JLabel enemyNameLabel = new JLabel(currentEnemy.getName(), SwingConstants.CENTER);
    enemyNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
    enemyNameLabel.setForeground(Color.ORANGE);
    enemyTopPanel.add(enemyNameLabel, BorderLayout.CENTER);
    rightBoardFrame.add(enemyTopPanel, BorderLayout.NORTH);
    rightBoardFrame.add(enemyBoardPanel, BorderLayout.CENTER);
    
    enemyShipLabel = new JLabel(getShipCountText(enemyBoard), SwingConstants.CENTER);
    enemyShipLabel.setFont(new Font("Arial", Font.BOLD, 12));
    enemyShipLabel.setForeground(Color.WHITE);
    rightBoardFrame.add(enemyShipLabel, BorderLayout.SOUTH);
    
    boardArea.add(leftBoardFrame);
    boardArea.add(rightBoardFrame);
    
    mainPanel.add(boardArea, BorderLayout.CENTER);

    // ===============================================================
    // STEP 5: Bottom strip - PORTRAITS + SKILL PANEL (PRESERVED EXACTLY)
    // ===============================================================
    currentSkillPanel = new SkillPanel(playerCharacter);
    currentSkillPanel.setBoards(playerBoardPanel, enemyBoardPanel);
    currentSkillPanel.setPreferredSize(new Dimension(450, 500));
    currentSkillPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    
    // Skill listener (preserved)
    currentSkillPanel.setSkillListener(new SkillPanel.SkillButtonListener() {
        @Override
        public void onSkillUsed(int skillNumber, String skillName, boolean requiresTarget, boolean requiresDirection, boolean targetsOwnBoard) {
            System.out.println("Skill used: " + skillName);
            
            if (turnTimer != null) turnTimer.stopTimer();
            
            if (skillName.equals("Shadow Step")) {
                System.out.println("🌑 Shadow Step detected - using separate handler!");
                waitingForKaelStepSource = true;
                waitingForKaelStepDestination = false;
                updateStatusLabel("🌑 Click on a ship on YOUR board to teleport!", Color.YELLOW);
                return;
            }
            
            currentSkillNumber = skillNumber;
            currentSkillName = skillName;
            currentSkillTargetsOwnBoard = targetsOwnBoard;
            currentSkillRequiresDirection = requiresDirection;
            
            if (requiresDirection) {
                String[] options = {"Horizontal (→)", "Vertical (↓)"};
                int choice = JOptionPane.showOptionDialog(frame,
                    skillName + "\n\nChoose direction:",
                    "Skill Direction",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
                
                if (choice < 0) {
                    if (enemyTurnTimer != null) {
                        enemyTurnTimer.stopTimer();
                        enemyTurnTimer.setVisible(false);
                    }
                    if (turnTimer != null && timerEnabled) {
                        turnTimer.setTimerLabel("Your Turn");
                        turnTimer.setVisible(true);
                        turnTimer.startTimer();
                    }
                    return;
                }
                currentSkillDirectionHorizontal = (choice == 0);
            }
            
            if (requiresTarget) {
                waitingForSkillTarget = true;
                updateStatusLabel("Click on " + (targetsOwnBoard ? "YOUR" : "ENEMY") + " board to target " + skillName + "!", Color.YELLOW);
            } else {
                executeSkill(-1, -1);
            }
        }
    });
    
    JPanel combinedBottomPanel = new JPanel(new BorderLayout(30, 0));
    combinedBottomPanel.setOpaque(false);
    combinedBottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 60, 10, 60));
    combinedBottomPanel.setPreferredSize(new Dimension(800, 180));

    // ===============================================================
    // PLAYER PORTRAIT (bottom-left) - PRESERVED ANIMATION CODE
    // ===============================================================
    if (playerCharacter instanceof Jiji) {
        Icon portrait = getCharacterPortrait(playerCharacter);
        if (portrait != null) {
            jijiLargePortraitLabel = new JLabel(portrait);
            jijiLargePortraitLabel.setToolTipText("Jiji: \"Is the game over yet? I want to nap.\"");
            jijiLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            jijiLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            jijiLargePortraitLabel.setPreferredSize(new Dimension(250, 200));
            
            JPanel westWrapper = new JPanel(new BorderLayout());
            westWrapper.setOpaque(false);
            westWrapper.setPreferredSize(new Dimension(250, 220));
            westWrapper.add(jijiLargePortraitLabel, BorderLayout.CENTER);
            
            JLabel nameTag = new JLabel("💻 JIJI", SwingConstants.CENTER);
            nameTag.setFont(new Font("Arial", Font.BOLD, 12));
            nameTag.setForeground(new Color(100, 200, 255));
            westWrapper.add(nameTag, BorderLayout.SOUTH);
            
            combinedBottomPanel.add(westWrapper, BorderLayout.WEST);
            
            initJijiIdleFrames();
            initJijiDamagedFrames();
            Jiji jiji = (Jiji) playerCharacter;
            if (jiji.isDamaged()) {
                if (jijiDamagedFrames[0] != null) {
                    startDamagedAnimation();
                }
            } else {
                if (jijiIdleFrames[0] != null) {
                    startIdleAnimation();
                }
            }
        }
    } else if (playerCharacter instanceof Kael) {
        Icon portrait = getCharacterPortrait(playerCharacter);
        if (portrait != null) {
            kaelLargePortraitLabel = new JLabel(portrait);
            kaelLargePortraitLabel.setToolTipText("Kael: \"Shadows bend to my will.\"");
            kaelLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            kaelLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            kaelLargePortraitLabel.setPreferredSize(new Dimension(150, 120));

            JPanel westWrapper = new JPanel(new BorderLayout());
            westWrapper.setOpaque(false);
            westWrapper.setPreferredSize(new Dimension(150, 140));
            westWrapper.add(kaelLargePortraitLabel, BorderLayout.CENTER);

            JLabel nameTag = new JLabel("⚔️ KAEL", SwingConstants.CENTER);
            nameTag.setFont(new Font("Arial", Font.BOLD, 12));
            nameTag.setForeground(new Color(100, 200, 255));
            westWrapper.add(nameTag, BorderLayout.SOUTH);

            combinedBottomPanel.add(westWrapper, BorderLayout.WEST);

            if (kaelIdleFrames[0] != null) {
                startKaelIdleAnimation();
            }
        }
    } else if (playerCharacter instanceof Valerius) {
        Icon portrait = getCharacterPortrait(playerCharacter);
        if (portrait != null) {
            valeriusLargePortraitLabel = new JLabel(portrait);
            valeriusLargePortraitLabel.setToolTipText("Valerius: \"I am the Iron Shoreline! I will not fall!\"");
            valeriusLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            valeriusLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            valeriusLargePortraitLabel.setPreferredSize(new Dimension(150, 120));

            JPanel westWrapper = new JPanel(new BorderLayout());
            westWrapper.setOpaque(false);
            westWrapper.setPreferredSize(new Dimension(150, 140));
            westWrapper.add(valeriusLargePortraitLabel, BorderLayout.CENTER);

            JLabel nameTag = new JLabel("🛡️ VALERIUS", SwingConstants.CENTER);
            nameTag.setFont(new Font("Arial", Font.BOLD, 12));
            nameTag.setForeground(new Color(100, 200, 255));
            westWrapper.add(nameTag, BorderLayout.SOUTH);

            combinedBottomPanel.add(westWrapper, BorderLayout.WEST);

            if (valeriusIdleFrames[0] != null) {
                startValeriusIdleAnimation();
            }
        }
    } else if (playerCharacter instanceof Skye) {
        Icon portrait = getCharacterPortrait(playerCharacter);
        if (portrait != null) {
            skyeLargePortraitLabel = new JLabel(portrait);
            skyeLargePortraitLabel.setToolTipText("Skye: \"Cats always land on their feet.\"");
            skyeLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            skyeLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            skyeLargePortraitLabel.setPreferredSize(new Dimension(150, 120));

            JPanel westWrapper = new JPanel(new BorderLayout());
            westWrapper.setOpaque(false);
            westWrapper.setPreferredSize(new Dimension(150, 140));
            westWrapper.add(skyeLargePortraitLabel, BorderLayout.CENTER);

            JLabel nameTag = new JLabel("🐱 SKYE", SwingConstants.CENTER);
            nameTag.setFont(new Font("Arial", Font.BOLD, 12));
            nameTag.setForeground(new Color(100, 200, 255));
            westWrapper.add(nameTag, BorderLayout.SOUTH);

            combinedBottomPanel.add(westWrapper, BorderLayout.WEST);

            if (skyeIdleFrames[0] != null) {
                startSkyeIdleAnimation();
            }
        }
    } else if (playerCharacter instanceof characters.Morgana) {
        Icon portrait = getCharacterPortrait(playerCharacter);
        if (portrait != null) {
            morganaLargePortraitLabel = new JLabel(portrait);
            morganaLargePortraitLabel.setToolTipText("Morgana: \"The ocean's embrace protects me.\"");
            morganaLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            morganaLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            morganaLargePortraitLabel.setPreferredSize(new Dimension(150, 120));

            JPanel westWrapper = new JPanel(new BorderLayout());
            westWrapper.setOpaque(false);
            westWrapper.setPreferredSize(new Dimension(150, 140));
            westWrapper.add(morganaLargePortraitLabel, BorderLayout.CENTER);

            JLabel nameTag = new JLabel("🧜‍♀️ MORGANA", SwingConstants.CENTER);
            nameTag.setFont(new Font("Arial", Font.BOLD, 12));
            nameTag.setForeground(new Color(100, 200, 255));
            westWrapper.add(nameTag, BorderLayout.SOUTH);

            combinedBottomPanel.add(westWrapper, BorderLayout.WEST);

            if (morganaIdleFrames[0] != null) {
                startMorganaIdleAnimation();
            }
        }
    } else if (playerCharacter instanceof Selene) {
        Icon portrait = getCharacterPortrait(playerCharacter);
        if (portrait != null) {
            seleneLargePortraitLabel = new JLabel(portrait);
            seleneLargePortraitLabel.setToolTipText("Selene: \"The moon guides my fate.\"");
            seleneLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            seleneLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            seleneLargePortraitLabel.setPreferredSize(new Dimension(150, 120));

            JPanel westWrapper = new JPanel(new BorderLayout());
            westWrapper.setOpaque(false);
            westWrapper.setPreferredSize(new Dimension(150, 140));
            westWrapper.add(seleneLargePortraitLabel, BorderLayout.CENTER);

            JLabel nameTag = new JLabel("🌙 SELENE", SwingConstants.CENTER);
            nameTag.setFont(new Font("Arial", Font.BOLD, 12));
            nameTag.setForeground(new Color(100, 200, 255));
            westWrapper.add(nameTag, BorderLayout.SOUTH);

            combinedBottomPanel.add(westWrapper, BorderLayout.WEST);

            if (seleneIdleFrames[0] != null) {
                startSeleneIdleAnimation();
            }
        }
    } else if (playerCharacter instanceof Flue) {
        Icon portrait = getCharacterPortrait(playerCharacter);
        if (portrait != null) {
            flueLargePortraitLabel = new JLabel(portrait);
            flueLargePortraitLabel.setToolTipText("Flue: \"Code is my weapon.\"");
            flueLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            flueLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            flueLargePortraitLabel.setPreferredSize(new Dimension(150, 120));

            JPanel westWrapper = new JPanel(new BorderLayout());
            westWrapper.setOpaque(false);
            westWrapper.setPreferredSize(new Dimension(150, 140));
            westWrapper.add(flueLargePortraitLabel, BorderLayout.CENTER);

            JLabel nameTag = new JLabel("💻 FLUE", SwingConstants.CENTER);
            nameTag.setFont(new Font("Arial", Font.BOLD, 12));
            nameTag.setForeground(new Color(100, 200, 255));
            westWrapper.add(nameTag, BorderLayout.SOUTH);

            combinedBottomPanel.add(westWrapper, BorderLayout.WEST);

            if (flueIdleFrames[0] != null) {
                startFlueIdleAnimation();
            }
        }
    } else if (playerCharacter instanceof Aeris) {
        Icon portrait = getCharacterPortrait(playerCharacter);
        if (portrait != null) {
            aerisLargePortraitLabel = new JLabel(portrait);
            aerisLargePortraitLabel.setToolTipText("Aeris: \"Adapt or perish.\"");
            aerisLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            aerisLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            aerisLargePortraitLabel.setPreferredSize(new Dimension(150, 120));

            JPanel westWrapper = new JPanel(new BorderLayout());
            westWrapper.setOpaque(false);
            westWrapper.setPreferredSize(new Dimension(150, 140));
            westWrapper.add(aerisLargePortraitLabel, BorderLayout.CENTER);

            JLabel nameTag = new JLabel("💪 AERIS", SwingConstants.CENTER);
            nameTag.setFont(new Font("Arial", Font.BOLD, 12));
            nameTag.setForeground(new Color(100, 200, 255));
            westWrapper.add(nameTag, BorderLayout.SOUTH);

            combinedBottomPanel.add(westWrapper, BorderLayout.WEST);

            if (aerisIdleFrames[0] != null) {
                startAerisIdleAnimation();
            }
        }
    } else {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setOpaque(false);
        emptyPanel.setPreferredSize(new Dimension(150, 140));
        combinedBottomPanel.add(emptyPanel, BorderLayout.WEST);
    }

    // ===============================================================
    // ENEMY PORTRAIT (bottom-right) - PRESERVED ANIMATION CODE
    // ===============================================================
    if (currentEnemy instanceof Jiji) {
        Icon enemyPortrait = getCharacterPortrait(currentEnemy);
        if (enemyPortrait != null) {
            enemyJijiLargePortraitLabel = new JLabel(enemyPortrait);
            enemyJijiLargePortraitLabel.setToolTipText("Enemy Jiji: \"Time to wake up and fight!\"");
            enemyJijiLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            enemyJijiLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            enemyJijiLargePortraitLabel.setPreferredSize(new Dimension(250, 200));
            
            JPanel eastWrapper = new JPanel(new BorderLayout());
            eastWrapper.setOpaque(false);
            eastWrapper.setPreferredSize(new Dimension(250, 220));
            eastWrapper.add(enemyJijiLargePortraitLabel, BorderLayout.CENTER);
            
            JLabel enemyNameTag = new JLabel("💻 JIJI", SwingConstants.CENTER);
            enemyNameTag.setFont(new Font("Arial", Font.BOLD, 12));
            enemyNameTag.setForeground(new Color(255, 100, 100));
            eastWrapper.add(enemyNameTag, BorderLayout.SOUTH);
            
            combinedBottomPanel.add(eastWrapper, BorderLayout.EAST);
            
            initEnemyJijiIdleFrames();
            initEnemyJijiDamagedFrames();
            initEnemyJijiAttackFrames();
            Jiji enemyJiji = (Jiji) currentEnemy;
            if (enemyJiji.isDamaged()) {
                if (enemyJijiDamagedFrames[0] != null) {
                    startEnemyDamagedAnimation();
                }
            } else {
                if (enemyJijiIdleFrames[0] != null) {
                    startEnemyIdleAnimation();
                }
            }
        }
    } else if (currentEnemy instanceof Kael) {
        Icon enemyPortrait = getCharacterPortrait(currentEnemy);
        if (enemyPortrait != null) {
            enemyKaelLargePortraitLabel = new JLabel(enemyPortrait);
            enemyKaelLargePortraitLabel.setToolTipText("Enemy Kael: \"You cannot escape the shadows.\"");
            enemyKaelLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            enemyKaelLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            enemyKaelLargePortraitLabel.setPreferredSize(new Dimension(150, 120));

            JPanel eastWrapper = new JPanel(new BorderLayout());
            eastWrapper.setOpaque(false);
            eastWrapper.setPreferredSize(new Dimension(150, 140));
            eastWrapper.add(enemyKaelLargePortraitLabel, BorderLayout.CENTER);

            JLabel enemyNameTag = new JLabel("⚔️ KAEL", SwingConstants.CENTER);
            enemyNameTag.setFont(new Font("Arial", Font.BOLD, 12));
            enemyNameTag.setForeground(new Color(255, 100, 100));
            eastWrapper.add(enemyNameTag, BorderLayout.SOUTH);

            combinedBottomPanel.add(eastWrapper, BorderLayout.EAST);

            if (enemyKaelIdleFrames[0] != null) {
                startEnemyKaelIdleAnimation();
            }
        }
    } else if (currentEnemy instanceof Valerius) {
        Icon enemyPortrait = getCharacterPortrait(currentEnemy);
        if (enemyPortrait != null) {
            enemyValeriusLargePortraitLabel = new JLabel(enemyPortrait);
            enemyValeriusLargePortraitLabel.setToolTipText("Enemy Valerius: \"I am the Iron Shoreline! I will not fall!\"");
            enemyValeriusLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            enemyValeriusLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            enemyValeriusLargePortraitLabel.setPreferredSize(new Dimension(150, 120));

            JPanel eastWrapper = new JPanel(new BorderLayout());
            eastWrapper.setOpaque(false);
            eastWrapper.setPreferredSize(new Dimension(150, 140));
            eastWrapper.add(enemyValeriusLargePortraitLabel, BorderLayout.CENTER);

            JLabel enemyNameTag = new JLabel("🛡️ VALERIUS", SwingConstants.CENTER);
            enemyNameTag.setFont(new Font("Arial", Font.BOLD, 12));
            enemyNameTag.setForeground(new Color(255, 100, 100));
            eastWrapper.add(enemyNameTag, BorderLayout.SOUTH);

            combinedBottomPanel.add(eastWrapper, BorderLayout.EAST);

            if (enemyValeriusIdleFrames[0] != null) {
                startEnemyValeriusIdleAnimation();
            }
        }
    } else if (currentEnemy instanceof Skye) {
        Icon enemyPortrait = getCharacterPortrait(currentEnemy);
        if (enemyPortrait != null) {
            enemySkyeLargePortraitLabel = new JLabel(enemyPortrait);
            enemySkyeLargePortraitLabel.setToolTipText("Enemy Skye: \"Cats always land on their feet.\"");
            enemySkyeLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            enemySkyeLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            enemySkyeLargePortraitLabel.setPreferredSize(new Dimension(150, 120));

            JPanel eastWrapper = new JPanel(new BorderLayout());
            eastWrapper.setOpaque(false);
            eastWrapper.setPreferredSize(new Dimension(150, 140));
            eastWrapper.add(enemySkyeLargePortraitLabel, BorderLayout.CENTER);

            JLabel enemyNameTag = new JLabel("🐱 SKYE", SwingConstants.CENTER);
            enemyNameTag.setFont(new Font("Arial", Font.BOLD, 12));
            enemyNameTag.setForeground(new Color(255, 100, 100));
            eastWrapper.add(enemyNameTag, BorderLayout.SOUTH);

            combinedBottomPanel.add(eastWrapper, BorderLayout.EAST);

            if (enemySkyeIdleFrames[0] != null) {
                startEnemySkyeIdleAnimation();
            }
        }
    } else if (currentEnemy instanceof characters.Morgana) {
        Icon enemyPortrait = getCharacterPortrait(currentEnemy);
        if (enemyPortrait != null) {
            enemyMorganaLargePortraitLabel = new JLabel(enemyPortrait);
            enemyMorganaLargePortraitLabel.setToolTipText("Enemy Morgana: \"The ocean's embrace protects me.\"");
            enemyMorganaLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            enemyMorganaLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            enemyMorganaLargePortraitLabel.setPreferredSize(new Dimension(150, 120));

            JPanel eastWrapper = new JPanel(new BorderLayout());
            eastWrapper.setOpaque(false);
            eastWrapper.setPreferredSize(new Dimension(150, 140));
            eastWrapper.add(enemyMorganaLargePortraitLabel, BorderLayout.CENTER);

            JLabel enemyNameTag = new JLabel("🧜‍♀️ MORGANA", SwingConstants.CENTER);
            enemyNameTag.setFont(new Font("Arial", Font.BOLD, 12));
            enemyNameTag.setForeground(new Color(255, 100, 100));
            eastWrapper.add(enemyNameTag, BorderLayout.SOUTH);

            combinedBottomPanel.add(eastWrapper, BorderLayout.EAST);

            if (enemyMorganaIdleFrames[0] != null) {
                startEnemyMorganaIdleAnimation();
            }
        }
    } else if (currentEnemy instanceof Aeris) {
        Icon enemyPortrait = getCharacterPortrait(currentEnemy);
        if (enemyPortrait != null) {
            enemyAerisLargePortraitLabel = new JLabel(enemyPortrait);
            enemyAerisLargePortraitLabel.setToolTipText("Enemy Aeris: \"Adapt or perish.\"");
            enemyAerisLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            enemyAerisLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            enemyAerisLargePortraitLabel.setPreferredSize(new Dimension(150, 120));

            JPanel eastWrapper = new JPanel(new BorderLayout());
            eastWrapper.setOpaque(false);
            eastWrapper.setPreferredSize(new Dimension(150, 140));
            eastWrapper.add(enemyAerisLargePortraitLabel, BorderLayout.CENTER);

            JLabel enemyNameTag = new JLabel("💪 AERIS", SwingConstants.CENTER);
            enemyNameTag.setFont(new Font("Arial", Font.BOLD, 12));
            enemyNameTag.setForeground(new Color(255, 100, 100));
            eastWrapper.add(enemyNameTag, BorderLayout.SOUTH);

            combinedBottomPanel.add(eastWrapper, BorderLayout.EAST);

            if (enemyAerisIdleFrames[0] != null) {
                startEnemyAerisIdleAnimation();
            }
        }
    } else if (currentEnemy instanceof Selene) {
        Icon enemyPortrait = getCharacterPortrait(currentEnemy);
        if (enemyPortrait != null) {
            enemySeleneLargePortraitLabel = new JLabel(enemyPortrait);
            enemySeleneLargePortraitLabel.setToolTipText("Enemy Selene: \"The moon guides my fate.\"");
            enemySeleneLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            enemySeleneLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            enemySeleneLargePortraitLabel.setPreferredSize(new Dimension(150, 120));

            JPanel eastWrapper = new JPanel(new BorderLayout());
            eastWrapper.setOpaque(false);
            eastWrapper.setPreferredSize(new Dimension(150, 140));
            eastWrapper.add(enemySeleneLargePortraitLabel, BorderLayout.CENTER);

            JLabel enemyNameTag = new JLabel("🌙 SELENE", SwingConstants.CENTER);
            enemyNameTag.setFont(new Font("Arial", Font.BOLD, 12));
            enemyNameTag.setForeground(new Color(255, 100, 100));
            eastWrapper.add(enemyNameTag, BorderLayout.SOUTH);

            combinedBottomPanel.add(eastWrapper, BorderLayout.EAST);

            if (enemySeleneIdleFrames[0] != null) {
                startEnemySeleneIdleAnimation();
            }
        }
    } else if (currentEnemy instanceof Flue) {
        Icon enemyPortrait = getCharacterPortrait(currentEnemy);
        if (enemyPortrait != null) {
            enemyFlueLargePortraitLabel = new JLabel(enemyPortrait);
            enemyFlueLargePortraitLabel.setToolTipText("Enemy Flue: \"Code is my weapon.\"");
            enemyFlueLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
            enemyFlueLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
            enemyFlueLargePortraitLabel.setPreferredSize(new Dimension(150, 120));

            JPanel eastWrapper = new JPanel(new BorderLayout());
            eastWrapper.setOpaque(false);
            eastWrapper.setPreferredSize(new Dimension(150, 140));
            eastWrapper.add(enemyFlueLargePortraitLabel, BorderLayout.CENTER);

            JLabel enemyNameTag = new JLabel("💻 FLUE", SwingConstants.CENTER);
            enemyNameTag.setFont(new Font("Arial", Font.BOLD, 12));
            enemyNameTag.setForeground(new Color(255, 100, 100));
            eastWrapper.add(enemyNameTag, BorderLayout.SOUTH);

            combinedBottomPanel.add(eastWrapper, BorderLayout.EAST);

            if (enemyFlueIdleFrames[0] != null) {
                startEnemyFlueIdleAnimation();
            }
        }
    } else {
        JPanel emptyPanelEast = new JPanel();
        emptyPanelEast.setOpaque(false);
        emptyPanelEast.setPreferredSize(new Dimension(150, 140));
        combinedBottomPanel.add(emptyPanelEast, BorderLayout.EAST);
    }

    // Skill panel in center of bottom strip
    combinedBottomPanel.add(currentSkillPanel, BorderLayout.CENTER);
    
    mainPanel.add(combinedBottomPanel, BorderLayout.SOUTH);
    
    // ===============================================================
    // STEP 6: Status label overlay (bottom-center floating)
    // ===============================================================
    frame.setContentPane(mainPanel);
    frame.revalidate();
    frame.repaint();
    
    // ===============================================================
    // STEP 7: Start turn timer (preserved)
    // ===============================================================
    if (playerTurn && timerEnabled && turnTimer != null) {
        if (enemyTurnTimer != null) {
            enemyTurnTimer.stopTimer();
            enemyTurnTimer.setVisible(false);
        }
        turnTimer.setTimerLabel("Your Turn");
        turnTimer.setVisible(true);
        turnTimer.startTimer();
    }
    
    System.out.println("✅ Battle UI created with TideBound design + preserved animations!");

    audio.MusicManager.getInstance().playMusic("battle");
}


private void refreshJijiPortrait() {
    System.out.println("🔄 Refreshing Jiji portrait - Damaged state: " + ((Jiji)playerCharacter).isDamaged());
    
    if (playerCharacter instanceof Jiji && jijiLargePortraitLabel != null) {
        Jiji jiji = (Jiji) playerCharacter;
        int portraitWidth = 250; // both idle and damaged use same base width
        
        Icon newPortrait = getCharacterPortrait(playerCharacter);
        if (newPortrait != null) {
            // Skip re-scaling for pre-rendered Jiji frames (already 250x200)
            boolean isPreRendered = (playerCharacter instanceof Jiji) &&
                (newPortrait == jijiIdleFrames[0] || newPortrait == jijiDamagedFrames[0]);
            if (isPreRendered) {
                jijiLargePortraitLabel.setIcon(newPortrait);
            } else {
                Image img = ((ImageIcon) newPortrait).getImage();
                Image scaled = img.getScaledInstance(portraitWidth, 200, Image.SCALE_SMOOTH);
                jijiLargePortraitLabel.setIcon(new ImageIcon(scaled));
            }
            jijiLargePortraitLabel.repaint();
            jijiLargePortraitLabel.revalidate();
            System.out.println("✅ Jiji portrait refreshed - width: " + portraitWidth);
            
            // Switch animation based on damage state
            if (jiji.isDamaged()) {
                stopIdleAnimation();
                startDamagedAnimation();
            } else {
                stopDamagedAnimation();
                startIdleAnimation();
            }
            
            // Add visual feedback when damaged - only trigger once
            if (jiji.isDamaged() && !jijiDamagedAnimationPlaying) {
                jijiDamagedAnimationPlaying = true;
                System.out.println("💢 Starting damaged animation once!");
                
                // Flash red border when damaged - cast to JComponent to use setBorder
                java.awt.Component parent = jijiLargePortraitLabel.getParent();
                if (parent instanceof JComponent) {
                    JComponent jparent = (JComponent) parent;
                    jparent.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                    // Remove border after 1 second
                    Timer borderTimer = new Timer(1000, e -> {
                        if (jijiLargePortraitLabel != null) {
                            java.awt.Component parent2 = jijiLargePortraitLabel.getParent();
                            if (parent2 instanceof JComponent) {
                                ((JComponent) parent2).setBorder(null);
                            }
                        }
                    });
                    borderTimer.setRepeats(false);
                    borderTimer.start();
                }
                
                // Add a visual flash effect when Jiji gets damaged
                if (frame.getContentPane() instanceof WaveBackgroundPanel) {
                    ((WaveBackgroundPanel) frame.getContentPane()).triggerFlash(Color.RED);
                }
                
                // Reset damaged animation flag when Jiji recovers
                javax.swing.Timer recoveryTimer = new javax.swing.Timer(2500, e -> {
                    if (!jiji.isDamaged()) {
                        jijiDamagedAnimationPlaying = false;
                        System.out.println("🔄 Jiji recovered, damaged animation can play again");
                    }
                });
                recoveryTimer.setRepeats(false);
                recoveryTimer.start();
            }
        } else {
            System.out.println("⚠️ newPortrait is NULL!");
        }
    } else {
        System.out.println("⚠️ Cannot refresh - jijiLargePortraitLabel is null or not Jiji");
    }
}

private void initJijiIdleFrames() {
    // Load all 4 idle frames and scale them centered (no offset) with smooth quality
    for (int i = 0; i < 4; i++) {
        String path = "assets/jiji_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    jijiIdleFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (250x200), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                jijiIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading frame " + (i+1) + ": " + e.getMessage());
                jijiIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Idle frame missing: " + f.getAbsolutePath());
            jijiIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 4; i++) {
        System.out.println("   Frame " + i + " " + (jijiIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void startIdleAnimation() {
    stopDamagedAnimation();
    if (idleAnimationTimer != null && idleAnimationTimer.isRunning()) {
        idleAnimationTimer.stop();
    }
    if (jijiIdleFrames[0] == null || jijiLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start idle - frames:" + (jijiIdleFrames[0]!=null) + " label:" + jijiLargePortraitLabel);
        return;
    }
    currentCycleSlot = 0;
    slotCounter = 0;
    final int tickMs = 16;
    idleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (jijiLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Jiji)) {
                stopIdleAnimation();
                return;
            }
            slotCounter++;
            int slotTicks = CYCLE_DURATIONS[currentCycleSlot];
            if (slotCounter >= slotTicks) {
                slotCounter = 0;
                currentCycleSlot = (currentCycleSlot + 1) % CYCLE_DURATIONS.length;
                int frameIdx = SLOT_FRAME_MAP[currentCycleSlot];
                ImageIcon baseFrame = jijiIdleFrames[frameIdx];
                if (baseFrame != null) {
                    // Simply set the icon – no sway
                    jijiLargePortraitLabel.setIcon(baseFrame);
                } else {
                    jijiLargePortraitLabel.setIcon(jijiIdleFrames[0]);
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Idle timer error: " + ex.getMessage());
            stopIdleAnimation();
        }
    });
    idleAnimationTimer.start();
    jijiLargePortraitLabel.setIcon(jijiIdleFrames[SLOT_FRAME_MAP[0]]);
    System.out.println("▶️ Jiji idle animation started (no sway)");
}


private void stopIdleAnimation() {
    if (idleAnimationTimer != null && idleAnimationTimer.isRunning()) {
        idleAnimationTimer.stop();
        currentCycleSlot = 0;
        slotCounter = 0;
        System.out.println("⏹️ Jiji idle animation stopped");
    }
}

private void initJijiDamagedFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/jiji_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ Damaged frame " + (i+1) + " failed to load");
                    jijiDamagedFrames[i] = null;
                    continue;
                }
                System.out.println("   Damaged frame " + (i+1) + " raw: " + base.getWidth() + "x" + base.getHeight());
                Image scaled = base.getScaledInstance(250, 200, Image.SCALE_SMOOTH);
                jijiDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded damaged frame " + (i + 1) + " → 250x200");
            } catch (Exception e) {
                System.out.println("⚠️ Error loading damaged frame " + (i+1) + ": " + e.getMessage());
                jijiDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Damaged frame missing: " + f.getAbsolutePath());
            jijiDamagedFrames[i] = null;
        }
    }
}

private void startDamagedAnimation() {
    stopIdleAnimation();
    if (damagedAnimationTimer != null && damagedAnimationTimer.isRunning()) {
        damagedAnimationTimer.stop();
    }
    if (jijiDamagedFrames[0] == null || jijiLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start damaged - frames:" + (jijiDamagedFrames[0]!=null));
        return;
    }
    currentDamagedFrame = 0;
    damagedFrameCounter = 0;
    final int tickMs = 16;
    damagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (jijiLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Jiji)) {
                stopDamagedAnimation();
                return;
            }
            damagedFrameCounter++;
            int frameTicks = DAMAGED_FRAME_DURATIONS[currentDamagedFrame];
            if (damagedFrameCounter >= frameTicks) {
                damagedFrameCounter = 0;
                currentDamagedFrame = (currentDamagedFrame + 1) % jijiDamagedFrames.length;
                ImageIcon frame = jijiDamagedFrames[currentDamagedFrame];
                if (frame != null) {
                    jijiLargePortraitLabel.setIcon(frame);
                } else {
                    jijiLargePortraitLabel.setIcon(jijiDamagedFrames[0]);
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Damaged timer error: " + ex.getMessage());
            stopDamagedAnimation();
        }
    });
    damagedAnimationTimer.start();
    // Set initial frame directly (already 250x200 from init)
    jijiLargePortraitLabel.setIcon(jijiDamagedFrames[0]);
    System.out.println("💢 Jiji damaged animation started (250px width)");
}

private void stopDamagedAnimation() {
    if (damagedAnimationTimer != null && damagedAnimationTimer.isRunning()) {
        damagedAnimationTimer.stop();
        currentDamagedFrame = 0;
        damagedFrameCounter = 0;
        System.out.println("⏹️ Jiji damaged animation stopped");
    }
}

private void initJijiAttackFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/jiji_attack" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ Attack frame " + (i+1) + " failed to load");
                    jijiAttackFrames[i] = null;
                    continue;
                }
                Image scaled = base.getScaledInstance(250, 200, Image.SCALE_SMOOTH);
                jijiAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading attack frame " + (i+1) + ": " + e.getMessage());
                jijiAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Attack frame missing: " + f.getAbsolutePath());
            jijiAttackFrames[i] = null;
        }
    }
}

private void startAttackAnimation() {
    // Stop all other Jiji animations
    stopIdleAnimation();
    stopDamagedAnimation();
    if (attackAnimationTimer != null && attackAnimationTimer.isRunning()) {
        attackAnimationTimer.stop();
    }
    if (jijiAttackFrames[0] == null || jijiLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start attack - frames:" + (jijiAttackFrames[0]!=null));
        return;
    }
    currentAttackFrame = 0;
    attackFrameCounter = 0;
    final int tickMs = 16;
    attackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (jijiLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Jiji)) {
                stopAttackAnimation();
                return;
            }
            attackFrameCounter++;
            int frameTicks = ATTACK_FRAME_DURATIONS[currentAttackFrame];
            if (attackFrameCounter >= frameTicks) {
                attackFrameCounter = 0;
                currentAttackFrame = (currentAttackFrame + 1) % jijiAttackFrames.length;
                ImageIcon frame = jijiAttackFrames[currentAttackFrame];
                if (frame != null) {
                    jijiLargePortraitLabel.setIcon(frame);
                } else {
                    jijiLargePortraitLabel.setIcon(jijiAttackFrames[0]);
                }
                // On last frame, schedule return to idle/damaged
                if (currentAttackFrame == jijiAttackFrames.length - 1) {
                    // Stop attack timer before refresh
                    stopAttackAnimation();
                    jijiAttackAnimationPlaying = false;
                    jijiAttackPlayedThisTurn = true;
                    // Small delay before returning to normal portrait
                    javax.swing.Timer returnTimer = new javax.swing.Timer(300, ev -> {
                        refreshJijiPortrait();
                    });
                    returnTimer.setRepeats(false);
                    returnTimer.start();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Attack timer error: " + ex.getMessage());
            stopAttackAnimation();
        }
    });
    attackAnimationTimer.start();
    jijiLargePortraitLabel.setIcon(jijiAttackFrames[0]);
    System.out.println("⚔️ Jiji attack animation started");
}

private void initKaelIdleFrames() {
    for (int i = 0; i < 5; i++) {
        String path = "assets/kael_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    kaelIdleFrames[i] = null;
                    continue;
                }
                int targetW = (i == 0 || i == 2) ? 135 : 150; // Make frames 1 and 3 slimmer
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                kaelIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Kael idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Kael frame " + (i+1) + ": " + e.getMessage());
                kaelIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Kael idle frame missing: " + f.getAbsolutePath());
            kaelIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 5; i++) {
        System.out.println("   Kael Frame " + i + " " + (kaelIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemyKaelIdleFrames() {
    for (int i = 0; i < 5; i++) {
        String path = "assets/kael_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyKaelIdleFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                int targetW = (i == 0 || i == 2) ? 135 : 150; // Make frames 1 and 3 slimmer
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyKaelIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Kael idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Kael frame " + (i+1) + ": " + e.getMessage());
                enemyKaelIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Kael idle frame missing: " + f.getAbsolutePath());
            enemyKaelIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 5; i++) {
        System.out.println("   Enemy Kael Frame " + i + " " + (enemyKaelIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemyValeriusIdleFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/valerius_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyValeriusIdleFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyValeriusIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Valerius idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Valerius frame " + (i+1) + ": " + e.getMessage());
                enemyValeriusIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Valerius idle frame missing: " + f.getAbsolutePath());
            enemyValeriusIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 4; i++) {
        System.out.println("   Enemy Valerius Frame " + i + " " + (enemyValeriusIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initValeriusIdleFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/valerius_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    valeriusIdleFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (250x200), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                valeriusIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Valerius idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Valerius frame " + (i+1) + ": " + e.getMessage());
                valeriusIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Valerius idle frame missing: " + f.getAbsolutePath());
            valeriusIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 4; i++) {
        System.out.println("   Valerius Frame " + i + " " + (valeriusIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initSkyeIdleFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/skye_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    skyeIdleFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                skyeIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Skye idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Skye frame " + (i+1) + ": " + e.getMessage());
                skyeIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Skye idle frame missing: " + f.getAbsolutePath());
            skyeIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 4; i++) {
        System.out.println("   Skye Frame " + i + " " + (skyeIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemySkyeIdleFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/skye_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemySkyeIdleFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemySkyeIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Skye idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Skye frame " + (i+1) + ": " + e.getMessage());
                enemySkyeIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Skye idle frame missing: " + f.getAbsolutePath());
            enemySkyeIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 4; i++) {
        System.out.println("   Enemy Skye Frame " + i + " " + (enemySkyeIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initSkyeAttackFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/skye_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    skyeAttackFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                skyeAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Skye attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Skye attack frame " + (i+1) + ": " + e.getMessage());
                skyeAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Skye attack frame missing: " + f.getAbsolutePath());
            skyeAttackFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Skye Attack Frame " + i + " " + (skyeAttackFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initSkyeDamagedFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/skye_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    skyeDamagedFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                skyeDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Skye damaged frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Skye damaged frame " + (i+1) + ": " + e.getMessage());
                skyeDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Skye damaged frame missing: " + f.getAbsolutePath());
            skyeDamagedFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 4; i++) {
        System.out.println("   Skye Damaged Frame " + i + " " + (skyeDamagedFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemySkyeAttackFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/skye_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemySkyeAttackFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemySkyeAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Skye attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Skye attack frame " + (i+1) + ": " + e.getMessage());
                enemySkyeAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Skye attack frame missing: " + f.getAbsolutePath());
            enemySkyeAttackFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Enemy Skye Attack Frame " + i + " " + (enemySkyeAttackFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemySkyeDamagedFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/skye_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemySkyeDamagedFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemySkyeDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Skye damaged frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Skye damaged frame " + (i+1) + ": " + e.getMessage());
                enemySkyeDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Skye damaged frame missing: " + f.getAbsolutePath());
            enemySkyeDamagedFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 4; i++) {
        System.out.println("   Enemy Skye Damaged Frame " + i + " " + (enemySkyeDamagedFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initMorganaIdleFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/morgana_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    morganaIdleFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                morganaIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Morgana idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Morgana frame " + (i+1) + ": " + e.getMessage());
                morganaIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Morgana idle frame missing: " + f.getAbsolutePath());
            morganaIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 4; i++) {
        System.out.println("   Morgana Frame " + i + " " + (morganaIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemyMorganaIdleFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/morgana_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyMorganaIdleFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyMorganaIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Morgana idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Morgana frame " + (i+1) + ": " + e.getMessage());
                enemyMorganaIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Morgana idle frame missing: " + f.getAbsolutePath());
            enemyMorganaIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 4; i++) {
        System.out.println("   Enemy Morgana Frame " + i + " " + (enemyMorganaIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initMorganaAttackFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/morgana_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    morganaAttackFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                morganaAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Morgana attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Morgana attack frame " + (i+1) + ": " + e.getMessage());
                morganaAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Morgana attack frame missing: " + f.getAbsolutePath());
            morganaAttackFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Morgana Attack Frame " + i + " " + (morganaAttackFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemyMorganaAttackFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/morgana_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyMorganaAttackFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyMorganaAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Morgana attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Morgana attack frame " + (i+1) + ": " + e.getMessage());
                enemyMorganaAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Morgana attack frame missing: " + f.getAbsolutePath());
            enemyMorganaAttackFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Enemy Morgana Attack Frame " + i + " " + (enemyMorganaAttackFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initMorganaDamagedFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/morgana_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    morganaDamagedFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                morganaDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Morgana damaged frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Morgana damaged frame " + (i+1) + ": " + e.getMessage());
                morganaDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Morgana damaged frame missing: " + f.getAbsolutePath());
            morganaDamagedFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Morgana Damaged Frame " + i + " " + (morganaDamagedFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemyMorganaDamagedFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/morgana_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyMorganaDamagedFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyMorganaDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Morgana damaged frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Morgana damaged frame " + (i+1) + ": " + e.getMessage());
                enemyMorganaDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Morgana damaged frame missing: " + f.getAbsolutePath());
            enemyMorganaDamagedFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Enemy Morgana Damaged Frame " + i + " " + (enemyMorganaDamagedFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initAerisIdleFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/aeris_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    aerisIdleFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                aerisIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Aeris idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Aeris idle frame " + (i+1) + ": " + e.getMessage());
                aerisIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Aeris idle frame missing: " + f.getAbsolutePath());
            aerisIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Aeris Idle Frame " + i + " " + (aerisIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemyAerisIdleFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/aeris_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyAerisIdleFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyAerisIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Aeris idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Aeris idle frame " + (i+1) + ": " + e.getMessage());
                enemyAerisIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Aeris idle frame missing: " + f.getAbsolutePath());
            enemyAerisIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Enemy Aeris Idle Frame " + i + " " + (enemyAerisIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initAerisAttackFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/aeris_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    aerisAttackFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                aerisAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Aeris attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Aeris attack frame " + (i+1) + ": " + e.getMessage());
                aerisAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Aeris attack frame missing: " + f.getAbsolutePath());
            aerisAttackFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Aeris Attack Frame " + i + " " + (aerisAttackFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemyAerisAttackFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/aeris_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyAerisAttackFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyAerisAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Aeris attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Aeris attack frame " + (i+1) + ": " + e.getMessage());
                enemyAerisAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Aeris attack frame missing: " + f.getAbsolutePath());
            enemyAerisAttackFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Enemy Aeris Attack Frame " + i + " " + (enemyAerisAttackFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initAerisDamagedFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/aeris_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    aerisDamagedFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                aerisDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Aeris damaged frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Aeris damaged frame " + (i+1) + ": " + e.getMessage());
                aerisDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Aeris damaged frame missing: " + f.getAbsolutePath());
            aerisDamagedFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Aeris Damaged Frame " + i + " " + (aerisDamagedFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemyAerisDamagedFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/aeris_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyAerisDamagedFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyAerisDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Aeris damaged frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Aeris damaged frame " + (i+1) + ": " + e.getMessage());
                enemyAerisDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Aeris damaged frame missing: " + f.getAbsolutePath());
            enemyAerisDamagedFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Enemy Aeris Damaged Frame " + i + " " + (enemyAerisDamagedFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initSeleneIdleFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/selene_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    seleneIdleFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                seleneIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Selene idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Selene idle frame " + (i+1) + ": " + e.getMessage());
                seleneIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Selene idle frame missing: " + f.getAbsolutePath());
            seleneIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 4; i++) {
        System.out.println("   Selene Idle Frame " + i + " " + (seleneIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemySeleneIdleFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/selene_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemySeleneIdleFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemySeleneIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Selene idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Selene idle frame " + (i+1) + ": " + e.getMessage());
                enemySeleneIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Selene idle frame missing: " + f.getAbsolutePath());
            enemySeleneIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 4; i++) {
        System.out.println("   Enemy Selene Idle Frame " + i + " " + (enemySeleneIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initFlueIdleFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/flue_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    flueIdleFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                flueIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Flue idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Flue idle frame " + (i+1) + ": " + e.getMessage());
                flueIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Flue idle frame missing: " + f.getAbsolutePath());
            flueIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Flue Idle Frame " + i + " " + (flueIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemyFlueIdleFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/flue_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyFlueIdleFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyFlueIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Flue idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Flue idle frame " + (i+1) + ": " + e.getMessage());
                enemyFlueIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Flue idle frame missing: " + f.getAbsolutePath());
            enemyFlueIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Enemy Flue Idle Frame " + i + " " + (enemyFlueIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initFlueAttackFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/flue_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    flueAttackFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                flueAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Flue attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Flue attack frame " + (i+1) + ": " + e.getMessage());
                flueAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Flue attack frame missing: " + f.getAbsolutePath());
            flueAttackFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Flue Attack Frame " + i + " " + (flueAttackFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemyFlueAttackFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/flue_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyFlueAttackFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyFlueAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Flue attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Flue attack frame " + (i+1) + ": " + e.getMessage());
                enemyFlueAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Flue attack frame missing: " + f.getAbsolutePath());
            enemyFlueAttackFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Enemy Flue Attack Frame " + i + " " + (enemyFlueAttackFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initFlueDamagedFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/flue_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    flueDamagedFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                flueDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Flue damaged frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Flue damaged frame " + (i+1) + ": " + e.getMessage());
                flueDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Flue damaged frame missing: " + f.getAbsolutePath());
            flueDamagedFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Flue Damaged Frame " + i + " " + (flueDamagedFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemyFlueDamagedFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/flue_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyFlueDamagedFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyFlueDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Flue damaged frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Flue damaged frame " + (i+1) + ": " + e.getMessage());
                enemyFlueDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Flue damaged frame missing: " + f.getAbsolutePath());
            enemyFlueDamagedFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Enemy Flue Damaged Frame " + i + " " + (enemyFlueDamagedFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initSeleneAttackFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/selene_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    seleneAttackFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                seleneAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Selene attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Selene attack frame " + (i+1) + ": " + e.getMessage());
                seleneAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Selene attack frame missing: " + f.getAbsolutePath());
            seleneAttackFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Selene Attack Frame " + i + " " + (seleneAttackFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemySeleneAttackFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/selene_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemySeleneAttackFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemySeleneAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Selene attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Selene attack frame " + (i+1) + ": " + e.getMessage());
                enemySeleneAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Selene attack frame missing: " + f.getAbsolutePath());
            enemySeleneAttackFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Enemy Selene Attack Frame " + i + " " + (enemySeleneAttackFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initSeleneDamagedFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/selene_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    seleneDamagedFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                seleneDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Selene damaged frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Selene damaged frame " + (i+1) + ": " + e.getMessage());
                seleneDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Selene damaged frame missing: " + f.getAbsolutePath());
            seleneDamagedFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Selene Damaged Frame " + i + " " + (seleneDamagedFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemySeleneDamagedFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/selene_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemySeleneDamagedFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemySeleneDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Selene damaged frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Selene damaged frame " + (i+1) + ": " + e.getMessage());
                enemySeleneDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Selene damaged frame missing: " + f.getAbsolutePath());
            enemySeleneDamagedFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Enemy Selene Damaged Frame " + i + " " + (enemySeleneDamagedFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initValeriusAttackFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/valerius_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    valeriusAttackFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                valeriusAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Valerius attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Valerius attack frame " + (i+1) + ": " + e.getMessage());
                valeriusAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Valerius attack frame missing: " + f.getAbsolutePath());
            valeriusAttackFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 4; i++) {
        System.out.println("   Valerius Attack Frame " + i + " " + (valeriusAttackFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemyValeriusAttackFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/valerius_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyValeriusAttackFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyValeriusAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Valerius attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Valerius attack frame " + (i+1) + ": " + e.getMessage());
                enemyValeriusAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Valerius attack frame missing: " + f.getAbsolutePath());
            enemyValeriusAttackFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 4; i++) {
        System.out.println("   Enemy Valerius Attack Frame " + i + " " + (enemyValeriusAttackFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initValeriusDamagedFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/valerius_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    valeriusDamagedFrames[i] = null;
                    continue;
                }
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                valeriusDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Valerius damaged frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Valerius damaged frame " + (i+1) + ": " + e.getMessage());
                valeriusDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Valerius damaged frame missing: " + f.getAbsolutePath());
            valeriusDamagedFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Valerius Damaged Frame " + i + " " + (valeriusDamagedFrames[i] != null ? "OK" : "NULL"));
    }
}

private void initEnemyValeriusDamagedFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/valerius_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyValeriusDamagedFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (150x120), centered
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyValeriusDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Valerius damaged frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Valerius damaged frame " + (i+1) + ": " + e.getMessage());
                enemyValeriusDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Valerius damaged frame missing: " + f.getAbsolutePath());
            enemyValeriusDamagedFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 3; i++) {
        System.out.println("   Enemy Valerius Damaged Frame " + i + " " + (enemyValeriusDamagedFrames[i] != null ? "OK" : "NULL"));
    }
}

private void startEnemyValeriusIdleAnimation() {
    stopEnemyValeriusIdleAnimation();
    if (enemyValeriusIdleFrames[0] == null || enemyValeriusLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Valerius idle - frames:" + (enemyValeriusIdleFrames[0]!=null) + " label:" + enemyValeriusLargePortraitLabel);
        return;
    }
    enemyValeriusIdleSequenceIndex = 0;
    enemyValeriusIdleFrameCounter = 0;
    final int tickMs = 16;
    final int frameDuration = 24; // Ticks per frame - slightly faster than Kael
    enemyValeriusIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyValeriusLargePortraitLabel == null) return;
            enemyValeriusIdleFrameCounter++;
            if (enemyValeriusIdleFrameCounter >= frameDuration) {
                enemyValeriusIdleFrameCounter = 0;
                enemyValeriusIdleSequenceIndex = (enemyValeriusIdleSequenceIndex + 1) % VALERIUS_IDLE_SEQUENCE.length;
                int frameIndex = VALERIUS_IDLE_SEQUENCE[enemyValeriusIdleSequenceIndex];
                if (enemyValeriusIdleFrames[frameIndex] != null) {
                    enemyValeriusLargePortraitLabel.setIcon(enemyValeriusIdleFrames[frameIndex]);
                    enemyValeriusLargePortraitLabel.repaint();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Valerius idle timer error: " + ex.getMessage());
            stopEnemyValeriusIdleAnimation();
        }
    });
    enemyValeriusIdleAnimationTimer.start();
    int initialFrame = VALERIUS_IDLE_SEQUENCE[0];
    enemyValeriusLargePortraitLabel.setIcon(enemyValeriusIdleFrames[initialFrame]);
    System.out.println("▶️ Enemy Valerius idle animation started");
}

private void stopEnemyValeriusIdleAnimation() {
    if (enemyValeriusIdleAnimationTimer != null && enemyValeriusIdleAnimationTimer.isRunning()) {
        enemyValeriusIdleAnimationTimer.stop();
        enemyValeriusIdleSequenceIndex = 0;
        enemyValeriusIdleFrameCounter = 0;
        System.out.println("⏹️ Enemy Valerius idle animation stopped");
    }
}

private void initKaelAttackFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/kael_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    kaelAttackFrames[i] = null;
                    continue;
                }
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                kaelAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Kael attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Kael attack frame " + (i+1) + ": " + e.getMessage());
                kaelAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Kael attack frame missing: " + f.getAbsolutePath());
            kaelAttackFrames[i] = null;
        }
    }
}

private void initEnemyKaelAttackFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/kael_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyKaelAttackFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyKaelAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Kael attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Kael attack frame " + (i+1) + ": " + e.getMessage());
                enemyKaelAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Kael attack frame missing: " + f.getAbsolutePath());
            enemyKaelAttackFrames[i] = null;
        }
    }
}

private void initKaelDamagedFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/kael_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    kaelDamagedFrames[i] = null;
                    continue;
                }
                int targetW = 150;
                int targetH = 120;
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                kaelDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Kael damaged frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading Kael damaged frame " + (i+1) + ": " + e.getMessage());
                kaelDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Kael damaged frame missing: " + f.getAbsolutePath());
            kaelDamagedFrames[i] = null;
        }
    }
}

private void initEnemyKaelDamagedFrames() {
    for (int i = 0; i < 3; i++) {
        String path = "assets/kael_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyKaelDamagedFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                int targetW = 150;
                int targetH = 120;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyKaelDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy Kael damaged frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy Kael damaged frame " + (i+1) + ": " + e.getMessage());
                enemyKaelDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Kael damaged frame missing: " + f.getAbsolutePath());
            enemyKaelDamagedFrames[i] = null;
        }
    }
}

private void startKaelIdleAnimation() {
    stopKaelIdleAnimation(); // Ensure no duplicate timers
    if (kaelIdleFrames[0] == null || kaelLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Kael idle - frames:" + (kaelIdleFrames[0]!=null) + " label:" + kaelLargePortraitLabel);
        return;
    }
    kaelIdleSequenceIndex = 0;
    kaelIdleFrameCounter = 0;
    final int tickMs = 16;
    final int frameDuration = 30; // Ticks per frame
    kaelIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (kaelLargePortraitLabel == null) return;
            kaelIdleFrameCounter++;
            if (kaelIdleFrameCounter >= frameDuration) {
                kaelIdleFrameCounter = 0;
                kaelIdleSequenceIndex = (kaelIdleSequenceIndex + 1) % KAEL_IDLE_SEQUENCE.length;
                int frameIndex = KAEL_IDLE_SEQUENCE[kaelIdleSequenceIndex];
                if (kaelIdleFrames[frameIndex] != null) {
                    kaelLargePortraitLabel.setIcon(kaelIdleFrames[frameIndex]);
                    kaelLargePortraitLabel.repaint();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Kael idle timer error: " + ex.getMessage());
            stopKaelIdleAnimation();
        }
    });
    kaelIdleAnimationTimer.start();
    int initialFrame = KAEL_IDLE_SEQUENCE[0];
    kaelLargePortraitLabel.setIcon(kaelIdleFrames[initialFrame]);
    System.out.println("▶️ Kael idle animation started");
}

private void stopKaelIdleAnimation() {
    if (kaelIdleAnimationTimer != null && kaelIdleAnimationTimer.isRunning()) {
        kaelIdleAnimationTimer.stop();
        kaelIdleSequenceIndex = 0;
        kaelIdleFrameCounter = 0;
        System.out.println("⏹️ Kael idle animation stopped");
    }
}

private void startValeriusIdleAnimation() {
    stopValeriusIdleAnimation(); // Ensure no duplicate timers
    if (valeriusIdleFrames[0] == null || valeriusLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Valerius idle - frames:" + (valeriusIdleFrames[0]!=null) + " label:" + valeriusLargePortraitLabel);
        return;
    }
    valeriusIdleSequenceIndex = 0;
    valeriusIdleFrameCounter = 0;
    final int tickMs = 16;
    final int frameDuration = 24; // Ticks per frame - slightly faster than Kael
    valeriusIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (valeriusLargePortraitLabel == null) return;
            valeriusIdleFrameCounter++;
            if (valeriusIdleFrameCounter >= frameDuration) {
                valeriusIdleFrameCounter = 0;
                valeriusIdleSequenceIndex = (valeriusIdleSequenceIndex + 1) % VALERIUS_IDLE_SEQUENCE.length;
                int frameIndex = VALERIUS_IDLE_SEQUENCE[valeriusIdleSequenceIndex];
                if (valeriusIdleFrames[frameIndex] != null) {
                    valeriusLargePortraitLabel.setIcon(valeriusIdleFrames[frameIndex]);
                    valeriusLargePortraitLabel.repaint();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Valerius idle timer error: " + ex.getMessage());
            stopValeriusIdleAnimation();
        }
    });
    valeriusIdleAnimationTimer.start();
    int initialFrame = VALERIUS_IDLE_SEQUENCE[0];
    valeriusLargePortraitLabel.setIcon(valeriusIdleFrames[initialFrame]);
    System.out.println("▶️ Valerius idle animation started");
}

private void stopValeriusIdleAnimation() {
    if (valeriusIdleAnimationTimer != null && valeriusIdleAnimationTimer.isRunning()) {
        valeriusIdleAnimationTimer.stop();
        valeriusIdleSequenceIndex = 0;
        valeriusIdleFrameCounter = 0;
        System.out.println("⏹️ Valerius idle animation stopped");
    }
}

private void startSkyeIdleAnimation() {
    stopSkyeIdleAnimation(); // Ensure no duplicate timers
    if (skyeIdleFrames[0] == null || skyeLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Skye idle - frames:" + (skyeIdleFrames[0]!=null) + " label:" + skyeLargePortraitLabel);
        return;
    }
    skyeIdleSequenceIndex = 0;
    skyeIdleFrameCounter = 0;
    final int tickMs = 16;
    final int frameDuration = 24; // Ticks per frame - similar to Valerius
    skyeIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (skyeLargePortraitLabel == null) return;
            skyeIdleFrameCounter++;
            if (skyeIdleFrameCounter >= frameDuration) {
                skyeIdleFrameCounter = 0;
                skyeIdleSequenceIndex = (skyeIdleSequenceIndex + 1) % SKYE_IDLE_SEQUENCE.length;
                int frameIndex = SKYE_IDLE_SEQUENCE[skyeIdleSequenceIndex];
                if (skyeIdleFrames[frameIndex] != null) {
                    skyeLargePortraitLabel.setIcon(skyeIdleFrames[frameIndex]);
                    skyeLargePortraitLabel.repaint();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Skye idle timer error: " + ex.getMessage());
            stopSkyeIdleAnimation();
        }
    });
    skyeIdleAnimationTimer.start();
    int initialFrame = SKYE_IDLE_SEQUENCE[0];
    skyeLargePortraitLabel.setIcon(skyeIdleFrames[initialFrame]);
    System.out.println("▶️ Skye idle animation started");
}

private void stopSkyeIdleAnimation() {
    if (skyeIdleAnimationTimer != null && skyeIdleAnimationTimer.isRunning()) {
        skyeIdleAnimationTimer.stop();
        skyeIdleSequenceIndex = 0;
        skyeIdleFrameCounter = 0;
        System.out.println("⏹️ Skye idle animation stopped");
    }
}

private void startEnemySkyeIdleAnimation() {
    stopEnemySkyeIdleAnimation(); // Ensure no duplicate timers
    if (enemySkyeIdleFrames[0] == null || enemySkyeLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Skye idle - frames:" + (enemySkyeIdleFrames[0]!=null) + " label:" + enemySkyeLargePortraitLabel);
        return;
    }
    enemySkyeIdleSequenceIndex = 0;
    enemySkyeIdleFrameCounter = 0;
    final int tickMs = 16;
    final int frameDuration = 24; // Ticks per frame - similar to Valerius
    enemySkyeIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemySkyeLargePortraitLabel == null) return;
            enemySkyeIdleFrameCounter++;
            if (enemySkyeIdleFrameCounter >= frameDuration) {
                enemySkyeIdleFrameCounter = 0;
                enemySkyeIdleSequenceIndex = (enemySkyeIdleSequenceIndex + 1) % SKYE_IDLE_SEQUENCE.length;
                int frameIndex = SKYE_IDLE_SEQUENCE[enemySkyeIdleSequenceIndex];
                if (enemySkyeIdleFrames[frameIndex] != null) {
                    enemySkyeLargePortraitLabel.setIcon(enemySkyeIdleFrames[frameIndex]);
                    enemySkyeLargePortraitLabel.repaint();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Skye idle timer error: " + ex.getMessage());
            stopEnemySkyeIdleAnimation();
        }
    });
    enemySkyeIdleAnimationTimer.start();
    int initialFrame = SKYE_IDLE_SEQUENCE[0];
    enemySkyeLargePortraitLabel.setIcon(enemySkyeIdleFrames[initialFrame]);
    System.out.println("▶️ Enemy Skye idle animation started");
}

private void stopEnemySkyeIdleAnimation() {
    if (enemySkyeIdleAnimationTimer != null && enemySkyeIdleAnimationTimer.isRunning()) {
        enemySkyeIdleAnimationTimer.stop();
        enemySkyeIdleSequenceIndex = 0;
        enemySkyeIdleFrameCounter = 0;
        System.out.println("⏹️ Enemy Skye idle animation stopped");
    }
}

private void startSkyeAttackAnimation() {
    // Stop all other Skye animations
    stopSkyeIdleAnimation();
    if (skyeAttackAnimationTimer != null && skyeAttackAnimationTimer.isRunning()) {
        skyeAttackAnimationTimer.stop();
    }
    if (skyeAttackFrames[0] == null || skyeLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Skye attack - frames:" + (skyeAttackFrames[0]!=null) + " label:" + skyeLargePortraitLabel);
        return;
    }
    currentSkyeAttackFrame = 0;
    skyeAttackFrameCounter = 0;
    final int tickMs = 16;
    skyeAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (skyeLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof characters.Skye)) {
                stopSkyeAttackAnimation();
                return;
            }
            skyeAttackFrameCounter++;
            int frameTicks = SKYE_ATTACK_FRAME_DURATIONS[currentSkyeAttackFrame];
            if (skyeAttackFrameCounter >= frameTicks) {
                skyeAttackFrameCounter = 0;
                currentSkyeAttackFrame = (currentSkyeAttackFrame + 1) % skyeAttackFrames.length;
                ImageIcon frame = skyeAttackFrames[currentSkyeAttackFrame];
                if (frame != null) {
                    skyeLargePortraitLabel.setIcon(frame);
                    skyeLargePortraitLabel.repaint();
                } else {
                    skyeLargePortraitLabel.setIcon(skyeAttackFrames[0]);
                }
                // On last frame, schedule return to idle
                if (currentSkyeAttackFrame == skyeAttackFrames.length - 1) {
                    // Stop attack timer before refresh
                    stopSkyeAttackAnimation();
                    skyeAttackAnimationPlaying = false;
                    // Small delay before returning to normal portrait
                    javax.swing.Timer returnTimer = new javax.swing.Timer(300, ev -> {
                        if (skyeLargePortraitLabel != null) {
                            skyeLargePortraitLabel.setIcon(getCharacterPortrait(playerCharacter));
                            skyeLargePortraitLabel.repaint();
                        }
                    });
                    returnTimer.setRepeats(false);
                    returnTimer.start();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Skye attack timer error: " + ex.getMessage());
            stopSkyeAttackAnimation();
        }
    });
    skyeAttackAnimationTimer.start();
    System.out.println("⚔️ Skye attack animation started");
}

private void stopSkyeAttackAnimation() {
    if (skyeAttackAnimationTimer != null && skyeAttackAnimationTimer.isRunning()) {
        skyeAttackAnimationTimer.stop();
        currentSkyeAttackFrame = 0;
        skyeAttackFrameCounter = 0;
        System.out.println("⏹️ Skye attack animation stopped");
    }
}

private void startEnemySkyeAttackAnimation() {
    // Stop all other enemy Skye animations
    stopEnemySkyeIdleAnimation();
    if (enemySkyeAttackAnimationTimer != null && enemySkyeAttackAnimationTimer.isRunning()) {
        enemySkyeAttackAnimationTimer.stop();
    }
    if (enemySkyeAttackFrames[0] == null || enemySkyeLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Skye attack - frames:" + (enemySkyeAttackFrames[0]!=null) + " label:" + enemySkyeLargePortraitLabel);
        return;
    }
    currentEnemySkyeAttackFrame = 0;
    enemySkyeAttackFrameCounter = 0;
    final int tickMs = 16;
    enemySkyeAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemySkyeLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof characters.Skye)) {
                stopEnemySkyeAttackAnimation();
                return;
            }
            enemySkyeAttackFrameCounter++;
            int frameTicks = SKYE_ATTACK_FRAME_DURATIONS[currentEnemySkyeAttackFrame];
            if (enemySkyeAttackFrameCounter >= frameTicks) {
                enemySkyeAttackFrameCounter = 0;
                currentEnemySkyeAttackFrame = (currentEnemySkyeAttackFrame + 1) % enemySkyeAttackFrames.length;
                ImageIcon frame = enemySkyeAttackFrames[currentEnemySkyeAttackFrame];
                if (frame != null) {
                    enemySkyeLargePortraitLabel.setIcon(frame);
                    enemySkyeLargePortraitLabel.repaint();
                } else {
                    enemySkyeLargePortraitLabel.setIcon(enemySkyeAttackFrames[0]);
                }
                // On last frame, schedule return to idle
                if (currentEnemySkyeAttackFrame == enemySkyeAttackFrames.length - 1) {
                    // Stop attack timer before refresh
                    stopEnemySkyeAttackAnimation();
                    enemySkyeAttackAnimationPlaying = false;
                    // Small delay before returning to normal portrait
                    javax.swing.Timer returnTimer = new javax.swing.Timer(300, ev -> {
                        if (enemySkyeLargePortraitLabel != null) {
                            enemySkyeLargePortraitLabel.setIcon(getCharacterPortrait(currentEnemy));
                            enemySkyeLargePortraitLabel.repaint();
                        }
                    });
                    returnTimer.setRepeats(false);
                    returnTimer.start();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Skye attack timer error: " + ex.getMessage());
            stopEnemySkyeAttackAnimation();
        }
    });
    enemySkyeAttackAnimationTimer.start();
    System.out.println("⚔️ Enemy Skye attack animation started");
}

private void stopEnemySkyeAttackAnimation() {
    if (enemySkyeAttackAnimationTimer != null && enemySkyeAttackAnimationTimer.isRunning()) {
        enemySkyeAttackAnimationTimer.stop();
        currentEnemySkyeAttackFrame = 0;
        enemySkyeAttackFrameCounter = 0;
        System.out.println("⏹️ Enemy Skye attack animation stopped");
    }
}

private void startSkyeDamagedAnimation() {
    // Stop all other Skye animations
    stopSkyeIdleAnimation();
    if (skyeDamagedAnimationTimer != null && skyeDamagedAnimationTimer.isRunning()) {
        skyeDamagedAnimationTimer.stop();
    }
    if (skyeDamagedFrames[0] == null || skyeLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Skye damaged - frames:" + (skyeDamagedFrames[0]!=null) + " label:" + skyeLargePortraitLabel);
        return;
    }
    currentSkyeDamagedFrame = 0;
    skyeDamagedFrameCounter = 0;
    final int tickMs = 16;
    skyeDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (skyeLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof characters.Skye)) {
                stopSkyeDamagedAnimation();
                return;
            }
            skyeDamagedFrameCounter++;
            int frameTicks = SKYE_DAMAGED_FRAME_DURATIONS[currentSkyeDamagedFrame];
            if (skyeDamagedFrameCounter >= frameTicks) {
                skyeDamagedFrameCounter = 0;
                currentSkyeDamagedFrame = (currentSkyeDamagedFrame + 1) % skyeDamagedFrames.length;
                ImageIcon frame = skyeDamagedFrames[currentSkyeDamagedFrame];
                if (frame != null) {
                    skyeLargePortraitLabel.setIcon(frame);
                    skyeLargePortraitLabel.repaint();
                } else {
                    skyeLargePortraitLabel.setIcon(skyeDamagedFrames[0]);
                }
                // On last frame, schedule return to idle
                if (currentSkyeDamagedFrame == skyeDamagedFrames.length - 1) {
                    // Stop damaged timer before refresh
                    stopSkyeDamagedAnimation();
                    skyeDamagedAnimationPlaying = false;
                    // Small delay before returning to normal portrait
                    javax.swing.Timer returnTimer = new javax.swing.Timer(300, ev -> {
                        if (skyeLargePortraitLabel != null) {
                            skyeLargePortraitLabel.setIcon(getCharacterPortrait(playerCharacter));
                            skyeLargePortraitLabel.repaint();
                        }
                    });
                    returnTimer.setRepeats(false);
                    returnTimer.start();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Skye damaged timer error: " + ex.getMessage());
            stopSkyeDamagedAnimation();
        }
    });
    skyeDamagedAnimationTimer.start();
    System.out.println("💥 Skye damaged animation started");
}

private void stopSkyeDamagedAnimation() {
    if (skyeDamagedAnimationTimer != null && skyeDamagedAnimationTimer.isRunning()) {
        skyeDamagedAnimationTimer.stop();
        currentSkyeDamagedFrame = 0;
        skyeDamagedFrameCounter = 0;
        System.out.println("⏹️ Skye damaged animation stopped");
    }
}

private void startEnemySkyeDamagedAnimation() {
    // Stop all other enemy Skye animations
    stopEnemySkyeIdleAnimation();
    if (enemySkyeDamagedAnimationTimer != null && enemySkyeDamagedAnimationTimer.isRunning()) {
        enemySkyeDamagedAnimationTimer.stop();
    }
    if (enemySkyeDamagedFrames[0] == null || enemySkyeLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Skye damaged - frames:" + (enemySkyeDamagedFrames[0]!=null) + " label:" + enemySkyeLargePortraitLabel);
        return;
    }
    currentEnemySkyeDamagedFrame = 0;
    enemySkyeDamagedFrameCounter = 0;
    final int tickMs = 16;
    enemySkyeDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemySkyeLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof characters.Skye)) {
                stopEnemySkyeDamagedAnimation();
                return;
            }
            enemySkyeDamagedFrameCounter++;
            int frameTicks = SKYE_DAMAGED_FRAME_DURATIONS[currentEnemySkyeDamagedFrame];
            if (enemySkyeDamagedFrameCounter >= frameTicks) {
                enemySkyeDamagedFrameCounter = 0;
                currentEnemySkyeDamagedFrame = (currentEnemySkyeDamagedFrame + 1) % enemySkyeDamagedFrames.length;
                ImageIcon frame = enemySkyeDamagedFrames[currentEnemySkyeDamagedFrame];
                if (frame != null) {
                    enemySkyeLargePortraitLabel.setIcon(frame);
                    enemySkyeLargePortraitLabel.repaint();
                } else {
                    enemySkyeLargePortraitLabel.setIcon(enemySkyeDamagedFrames[0]);
                }
                // On last frame, schedule return to idle
                if (currentEnemySkyeDamagedFrame == enemySkyeDamagedFrames.length - 1) {
                    // Stop damaged timer before refresh
                    stopEnemySkyeDamagedAnimation();
                    enemySkyeDamagedAnimationPlaying = false;
                    // Small delay before returning to normal portrait
                    javax.swing.Timer returnTimer = new javax.swing.Timer(300, ev -> {
                        if (enemySkyeLargePortraitLabel != null) {
                            enemySkyeLargePortraitLabel.setIcon(getCharacterPortrait(currentEnemy));
                            enemySkyeLargePortraitLabel.repaint();
                        }
                    });
                    returnTimer.setRepeats(false);
                    returnTimer.start();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Skye damaged timer error: " + ex.getMessage());
            stopEnemySkyeDamagedAnimation();
        }
    });
    enemySkyeDamagedAnimationTimer.start();
    System.out.println("💥 Enemy Skye damaged animation started");
}

private void stopEnemySkyeDamagedAnimation() {
    if (enemySkyeDamagedAnimationTimer != null && enemySkyeDamagedAnimationTimer.isRunning()) {
        enemySkyeDamagedAnimationTimer.stop();
        currentEnemySkyeDamagedFrame = 0;
        enemySkyeDamagedFrameCounter = 0;
        System.out.println("⏹️ Enemy Skye damaged animation stopped");
    }
}

private void startMorganaIdleAnimation() {
    stopMorganaIdleAnimation(); // Ensure no duplicate timers
    if (morganaIdleFrames[0] == null || morganaLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Morgana idle - frames:" + (morganaIdleFrames[0]!=null) + " label:" + morganaLargePortraitLabel);
        return;
    }
    morganaIdleSequenceIndex = 0;
    morganaIdleFrameCounter = 0;
    final int tickMs = 16;
    final int frameDuration = 24; // Ticks per frame - similar to Valerius
    morganaIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (morganaLargePortraitLabel == null) return;
            morganaIdleFrameCounter++;
            if (morganaIdleFrameCounter >= frameDuration) {
                morganaIdleFrameCounter = 0;
                morganaIdleSequenceIndex = (morganaIdleSequenceIndex + 1) % MORGANA_IDLE_SEQUENCE.length;
                int frameIndex = MORGANA_IDLE_SEQUENCE[morganaIdleSequenceIndex];
                if (morganaIdleFrames[frameIndex] != null) {
                    morganaLargePortraitLabel.setIcon(morganaIdleFrames[frameIndex]);
                    morganaLargePortraitLabel.repaint();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Morgana idle timer error: " + ex.getMessage());
            stopMorganaIdleAnimation();
        }
    });
    morganaIdleAnimationTimer.start();
    int initialFrame = MORGANA_IDLE_SEQUENCE[0];
    morganaLargePortraitLabel.setIcon(morganaIdleFrames[initialFrame]);
    System.out.println("▶️ Morgana idle animation started");
}

private void stopMorganaIdleAnimation() {
    if (morganaIdleAnimationTimer != null && morganaIdleAnimationTimer.isRunning()) {
        morganaIdleAnimationTimer.stop();
        morganaIdleSequenceIndex = 0;
        morganaIdleFrameCounter = 0;
        System.out.println("⏹️ Morgana idle animation stopped");
    }
}

private void startEnemyMorganaIdleAnimation() {
    stopEnemyMorganaIdleAnimation(); // Ensure no duplicate timers
    if (enemyMorganaIdleFrames[0] == null || enemyMorganaLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Morgana idle - frames:" + (enemyMorganaIdleFrames[0]!=null) + " label:" + enemyMorganaLargePortraitLabel);
        return;
    }
    enemyMorganaIdleSequenceIndex = 0;
    enemyMorganaIdleFrameCounter = 0;
    final int tickMs = 16;
    final int frameDuration = 24; // Ticks per frame - similar to Valerius
    enemyMorganaIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyMorganaLargePortraitLabel == null) return;
            enemyMorganaIdleFrameCounter++;
            if (enemyMorganaIdleFrameCounter >= frameDuration) {
                enemyMorganaIdleFrameCounter = 0;
                enemyMorganaIdleSequenceIndex = (enemyMorganaIdleSequenceIndex + 1) % MORGANA_IDLE_SEQUENCE.length;
                int frameIndex = MORGANA_IDLE_SEQUENCE[enemyMorganaIdleSequenceIndex];
                if (enemyMorganaIdleFrames[frameIndex] != null) {
                    enemyMorganaLargePortraitLabel.setIcon(enemyMorganaIdleFrames[frameIndex]);
                    enemyMorganaLargePortraitLabel.repaint();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Morgana idle timer error: " + ex.getMessage());
            stopEnemyMorganaIdleAnimation();
        }
    });
    enemyMorganaIdleAnimationTimer.start();
    int initialFrame = MORGANA_IDLE_SEQUENCE[0];
    enemyMorganaLargePortraitLabel.setIcon(enemyMorganaIdleFrames[initialFrame]);
    System.out.println("▶️ Enemy Morgana idle animation started");
}

private void stopEnemyMorganaIdleAnimation() {
    if (enemyMorganaIdleAnimationTimer != null && enemyMorganaIdleAnimationTimer.isRunning()) {
        enemyMorganaIdleAnimationTimer.stop();
        enemyMorganaIdleSequenceIndex = 0;
        enemyMorganaIdleFrameCounter = 0;
        System.out.println("⏹️ Enemy Morgana idle animation stopped");
    }
}

private void startMorganaAttackAnimation() {
    // Stop all other Morgana animations
    stopMorganaIdleAnimation();
    if (morganaAttackAnimationTimer != null && morganaAttackAnimationTimer.isRunning()) {
        morganaAttackAnimationTimer.stop();
    }
    if (morganaAttackFrames[0] == null || morganaLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Morgana attack - frames:" + (morganaAttackFrames[0]!=null) + " label:" + morganaLargePortraitLabel);
        return;
    }
    morganaAttackAnimationPlaying = true;
    currentMorganaAttackFrame = 0;
    morganaAttackFrameCounter = 0;
    final int tickMs = 16;
    morganaAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (morganaLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof characters.Morgana)) {
                stopMorganaAttackAnimation();
                return;
            }
            morganaAttackFrameCounter++;
            int frameTicks = MORGANA_ATTACK_FRAME_DURATIONS[currentMorganaAttackFrame];
            if (morganaAttackFrameCounter >= frameTicks) {
                morganaAttackFrameCounter = 0;
                currentMorganaAttackFrame = (currentMorganaAttackFrame + 1) % morganaAttackFrames.length;
                ImageIcon frame = morganaAttackFrames[currentMorganaAttackFrame];
                if (frame != null) {
                    morganaLargePortraitLabel.setIcon(frame);
                    morganaLargePortraitLabel.repaint();
                } else {
                    morganaLargePortraitLabel.setIcon(morganaAttackFrames[0]);
                }
                // On last frame, schedule return to idle
                if (currentMorganaAttackFrame == morganaAttackFrames.length - 1) {
                    // Stop attack timer before refresh
                    stopMorganaAttackAnimation();
                    morganaAttackAnimationPlaying = false;
                    // Small delay before returning to normal portrait
                    javax.swing.Timer returnTimer = new javax.swing.Timer(300, ev -> {
                        if (morganaLargePortraitLabel != null) {
                            morganaLargePortraitLabel.setIcon(getCharacterPortrait(playerCharacter));
                            morganaLargePortraitLabel.repaint();
                        }
                    });
                    returnTimer.setRepeats(false);
                    returnTimer.start();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Morgana attack timer error: " + ex.getMessage());
            stopMorganaAttackAnimation();
        }
    });
    morganaAttackAnimationTimer.start();
    System.out.println("⚔️ Morgana attack animation started");
}

private void stopMorganaAttackAnimation() {
    if (morganaAttackAnimationTimer != null && morganaAttackAnimationTimer.isRunning()) {
        morganaAttackAnimationTimer.stop();
        currentMorganaAttackFrame = 0;
        morganaAttackFrameCounter = 0;
        System.out.println("⏹️ Morgana attack animation stopped");
    }
}

private void startEnemyMorganaAttackAnimation() {
    // Stop all other enemy Morgana animations
    stopEnemyMorganaIdleAnimation();
    if (enemyMorganaAttackAnimationTimer != null && enemyMorganaAttackAnimationTimer.isRunning()) {
        enemyMorganaAttackAnimationTimer.stop();
    }
    if (enemyMorganaAttackFrames[0] == null || enemyMorganaLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Morgana attack - frames:" + (enemyMorganaAttackFrames[0]!=null) + " label:" + enemyMorganaLargePortraitLabel);
        return;
    }
    enemyMorganaAttackAnimationPlaying = true;
    currentEnemyMorganaAttackFrame = 0;
    enemyMorganaAttackFrameCounter = 0;
    final int tickMs = 16;
    enemyMorganaAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyMorganaLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof characters.Morgana)) {
                stopEnemyMorganaAttackAnimation();
                return;
            }
            enemyMorganaAttackFrameCounter++;
            int frameTicks = MORGANA_ATTACK_FRAME_DURATIONS[currentEnemyMorganaAttackFrame];
            if (enemyMorganaAttackFrameCounter >= frameTicks) {
                enemyMorganaAttackFrameCounter = 0;
                currentEnemyMorganaAttackFrame = (currentEnemyMorganaAttackFrame + 1) % enemyMorganaAttackFrames.length;
                ImageIcon frame = enemyMorganaAttackFrames[currentEnemyMorganaAttackFrame];
                if (frame != null) {
                    enemyMorganaLargePortraitLabel.setIcon(frame);
                    enemyMorganaLargePortraitLabel.repaint();
                } else {
                    enemyMorganaLargePortraitLabel.setIcon(enemyMorganaAttackFrames[0]);
                }
                // On last frame, schedule return to idle
                if (currentEnemyMorganaAttackFrame == enemyMorganaAttackFrames.length - 1) {
                    // Stop attack timer before refresh
                    stopEnemyMorganaAttackAnimation();
                    enemyMorganaAttackAnimationPlaying = false;
                    // Small delay before returning to normal portrait
                    javax.swing.Timer returnTimer = new javax.swing.Timer(300, ev -> {
                        if (enemyMorganaLargePortraitLabel != null) {
                            enemyMorganaLargePortraitLabel.setIcon(getCharacterPortrait(currentEnemy));
                            enemyMorganaLargePortraitLabel.repaint();
                        }
                    });
                    returnTimer.setRepeats(false);
                    returnTimer.start();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Morgana attack timer error: " + ex.getMessage());
            stopEnemyMorganaAttackAnimation();
        }
    });
    enemyMorganaAttackAnimationTimer.start();
    System.out.println("⚔️ Enemy Morgana attack animation started");
}

private void stopEnemyMorganaAttackAnimation() {
    if (enemyMorganaAttackAnimationTimer != null && enemyMorganaAttackAnimationTimer.isRunning()) {
        enemyMorganaAttackAnimationTimer.stop();
        currentEnemyMorganaAttackFrame = 0;
        enemyMorganaAttackFrameCounter = 0;
        System.out.println("⏹️ Enemy Morgana attack animation stopped");
    }
}

private void startMorganaDamagedAnimation() {
    stopMorganaIdleAnimation();
    if (morganaDamagedAnimationTimer != null && morganaDamagedAnimationTimer.isRunning()) {
        morganaDamagedAnimationTimer.stop();
    }
    if (morganaDamagedFrames[0] == null || morganaLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Morgana damaged - frames:" + (morganaDamagedFrames[0]!=null) + " label:" + morganaLargePortraitLabel);
        return;
    }
    currentMorganaDamagedFrame = 0;
    morganaDamagedFrameCounter = 0;
    final int tickMs = 16;
    morganaDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (morganaLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof characters.Morgana)) {
                stopMorganaDamagedAnimation();
                return;
            }
            morganaDamagedFrameCounter++;
            int frameTicks = MORGANA_DAMAGED_FRAME_DURATIONS[currentMorganaDamagedFrame];
            if (morganaDamagedFrameCounter >= frameTicks) {
                morganaDamagedFrameCounter = 0;
                currentMorganaDamagedFrame++;
                if (currentMorganaDamagedFrame >= morganaDamagedFrames.length) {
                    // Animation finished, return to idle
                    stopMorganaDamagedAnimation();
                    morganaDamagedAnimationPlaying = false;
                    if (morganaIdleFrames[0] != null) {
                        startMorganaIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = morganaDamagedFrames[currentMorganaDamagedFrame];
                if (frame != null) {
                    morganaLargePortraitLabel.setIcon(frame);
                } else {
                    morganaLargePortraitLabel.setIcon(morganaDamagedFrames[0]);
                }
                morganaLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Morgana damaged timer error: " + ex.getMessage());
            stopMorganaDamagedAnimation();
        }
    });
    morganaDamagedAnimationTimer.start();
    morganaLargePortraitLabel.setIcon(morganaDamagedFrames[0]);
    morganaDamagedAnimationPlaying = true;
    System.out.println("💢 Morgana damaged animation started");
}

private void stopMorganaDamagedAnimation() {
    if (morganaDamagedAnimationTimer != null && morganaDamagedAnimationTimer.isRunning()) {
        morganaDamagedAnimationTimer.stop();
        currentMorganaDamagedFrame = 0;
        morganaDamagedFrameCounter = 0;
        System.out.println("⏹️ Morgana damaged animation stopped");
    }
}

private void startEnemyMorganaDamagedAnimation() {
    stopEnemyMorganaIdleAnimation();
    if (enemyMorganaDamagedAnimationTimer != null && enemyMorganaDamagedAnimationTimer.isRunning()) {
        enemyMorganaDamagedAnimationTimer.stop();
    }
    if (enemyMorganaDamagedFrames[0] == null || enemyMorganaLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Morgana damaged - frames:" + (enemyMorganaDamagedFrames[0]!=null) + " label:" + enemyMorganaLargePortraitLabel);
        return;
    }
    currentEnemyMorganaDamagedFrame = 0;
    enemyMorganaDamagedFrameCounter = 0;
    final int tickMs = 16;
    enemyMorganaDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyMorganaLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof characters.Morgana)) {
                stopEnemyMorganaDamagedAnimation();
                return;
            }
            enemyMorganaDamagedFrameCounter++;
            int frameTicks = MORGANA_DAMAGED_FRAME_DURATIONS[currentEnemyMorganaDamagedFrame];
            if (enemyMorganaDamagedFrameCounter >= frameTicks) {
                enemyMorganaDamagedFrameCounter = 0;
                currentEnemyMorganaDamagedFrame++;
                if (currentEnemyMorganaDamagedFrame >= enemyMorganaDamagedFrames.length) {
                    // Animation finished, return to idle
                    stopEnemyMorganaDamagedAnimation();
                    enemyMorganaDamagedAnimationPlaying = false;
                    if (enemyMorganaIdleFrames[0] != null) {
                        startEnemyMorganaIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = enemyMorganaDamagedFrames[currentEnemyMorganaDamagedFrame];
                if (frame != null) {
                    enemyMorganaLargePortraitLabel.setIcon(frame);
                } else {
                    enemyMorganaLargePortraitLabel.setIcon(enemyMorganaDamagedFrames[0]);
                }
                enemyMorganaLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Morgana damaged timer error: " + ex.getMessage());
            stopEnemyMorganaDamagedAnimation();
        }
    });
    enemyMorganaDamagedAnimationTimer.start();
    enemyMorganaLargePortraitLabel.setIcon(enemyMorganaDamagedFrames[0]);
    enemyMorganaDamagedAnimationPlaying = true;
    System.out.println("💢 Enemy Morgana damaged animation started");
}

private void showEnemyMorganaDamagedAnimation() {
    System.out.println("💥 showEnemyMorganaDamagedAnimation called!");

    // Only play once per turn and if not already playing
    if (enemyMorganaDamagedAnimationPlaying) {
        System.out.println("⏭️ Enemy Morgana damaged animation already playing, skipping...");
        return;
    }

    if (currentEnemy instanceof characters.Morgana && enemyMorganaLargePortraitLabel != null) {
        if (enemyMorganaDamagedFrames[0] != null) {
            startEnemyMorganaDamagedAnimation();
        } else {
            System.out.println("⚠️ Enemy Morgana damaged frames not loaded, skipping damaged animation");
            enemyMorganaDamagedAnimationPlaying = false;
        }
    }
}

private void showEnemyAerisDamagedAnimation() {
    System.out.println("💥 showEnemyAerisDamagedAnimation called!");

    // Only play once per turn and if not already playing
    if (enemyAerisDamagedAnimationPlaying) {
        System.out.println("⏭️ Enemy Aeris damaged animation already playing, skipping...");
        return;
    }

    if (currentEnemy instanceof Aeris && enemyAerisLargePortraitLabel != null) {
        if (enemyAerisDamagedFrames[0] != null) {
            startEnemyAerisDamagedAnimation();
        } else {
            System.out.println("⚠️ Enemy Aeris damaged frames not loaded, skipping damaged animation");
            enemyAerisDamagedAnimationPlaying = false;
        }
    }
}

private void showEnemySeleneDamagedAnimation() {
    System.out.println("💥 showEnemySeleneDamagedAnimation called!");

    // Only play once per turn and if not already playing
    if (enemySeleneDamagedAnimationPlaying) {
        System.out.println("⏭️ Enemy Selene damaged animation already playing, skipping...");
        return;
    }

    if (currentEnemy instanceof Selene && enemySeleneLargePortraitLabel != null) {
        if (enemySeleneDamagedFrames[0] != null) {
            startEnemySeleneDamagedAnimation();
        } else {
            System.out.println("⚠️ Enemy Selene damaged frames not loaded, skipping damaged animation");
            enemySeleneDamagedAnimationPlaying = false;
        }
    }
}

private void showEnemyFlueDamagedAnimation() {
    System.out.println("💥 showEnemyFlueDamagedAnimation called!");

    // Only play once per turn and if not already playing
    if (enemyFlueDamagedAnimationPlaying) {
        System.out.println("⏭️ Enemy Flue damaged animation already playing, skipping...");
        return;
    }

    if (currentEnemy instanceof Flue && enemyFlueLargePortraitLabel != null) {
        if (enemyFlueDamagedFrames[0] != null) {
            startEnemyFlueDamagedAnimation();
        } else {
            System.out.println("⚠️ Enemy Flue damaged frames not loaded, skipping damaged animation");
            enemyFlueDamagedAnimationPlaying = false;
        }
    }
}

private void stopEnemyMorganaDamagedAnimation() {
    if (enemyMorganaDamagedAnimationTimer != null && enemyMorganaDamagedAnimationTimer.isRunning()) {
        enemyMorganaDamagedAnimationTimer.stop();
        currentEnemyMorganaDamagedFrame = 0;
        enemyMorganaDamagedFrameCounter = 0;
        System.out.println("⏹️ Enemy Morgana damaged animation stopped");
    }
}

private void startAerisIdleAnimation() {
    stopAerisIdleAnimation(); // Ensure no duplicate timers
    if (aerisIdleFrames[0] == null || aerisLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Aeris idle - frames:" + (aerisIdleFrames[0]!=null) + " label:" + aerisLargePortraitLabel);
        return;
    }
    aerisIdleSequenceIndex = 0;
    aerisIdleFrameCounter = 0;
    final int tickMs = 16;
    final int frameDuration = 6; // ticks per frame
    aerisIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (aerisLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Aeris)) {
                stopAerisIdleAnimation();
                return;
            }
            aerisIdleFrameCounter++;
            if (aerisIdleFrameCounter >= frameDuration) {
                aerisIdleFrameCounter = 0;
                aerisIdleSequenceIndex = (aerisIdleSequenceIndex + 1) % AERIS_IDLE_SEQUENCE.length;
                int frameIndex = AERIS_IDLE_SEQUENCE[aerisIdleSequenceIndex];
                if (aerisIdleFrames[frameIndex] != null) {
                    aerisLargePortraitLabel.setIcon(aerisIdleFrames[frameIndex]);
                    aerisLargePortraitLabel.repaint();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Aeris idle timer error: " + ex.getMessage());
            stopAerisIdleAnimation();
        }
    });
    aerisIdleAnimationTimer.start();
    aerisLargePortraitLabel.setIcon(aerisIdleFrames[0]);
    System.out.println("▶️ Aeris idle animation started");
}

private void stopAerisIdleAnimation() {
    if (aerisIdleAnimationTimer != null && aerisIdleAnimationTimer.isRunning()) {
        aerisIdleAnimationTimer.stop();
        aerisIdleSequenceIndex = 0;
        aerisIdleFrameCounter = 0;
        System.out.println("⏹️ Aeris idle animation stopped");
    }
}

private void startEnemyAerisIdleAnimation() {
    stopEnemyAerisIdleAnimation(); // Ensure no duplicate timers
    if (enemyAerisIdleFrames[0] == null || enemyAerisLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Aeris idle - frames:" + (enemyAerisIdleFrames[0]!=null) + " label:" + enemyAerisLargePortraitLabel);
        return;
    }
    enemyAerisIdleSequenceIndex = 0;
    enemyAerisIdleFrameCounter = 0;
    final int tickMs = 16;
    final int frameDuration = 6; // ticks per frame
    enemyAerisIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyAerisLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Aeris)) {
                stopEnemyAerisIdleAnimation();
                return;
            }
            enemyAerisIdleFrameCounter++;
            if (enemyAerisIdleFrameCounter >= frameDuration) {
                enemyAerisIdleFrameCounter = 0;
                enemyAerisIdleSequenceIndex = (enemyAerisIdleSequenceIndex + 1) % AERIS_IDLE_SEQUENCE.length;
                int frameIndex = AERIS_IDLE_SEQUENCE[enemyAerisIdleSequenceIndex];
                if (enemyAerisIdleFrames[frameIndex] != null) {
                    enemyAerisLargePortraitLabel.setIcon(enemyAerisIdleFrames[frameIndex]);
                    enemyAerisLargePortraitLabel.repaint();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Aeris idle timer error: " + ex.getMessage());
            stopEnemyAerisIdleAnimation();
        }
    });
    enemyAerisIdleAnimationTimer.start();
    enemyAerisLargePortraitLabel.setIcon(enemyAerisIdleFrames[0]);
    System.out.println("▶️ Enemy Aeris idle animation started");
}

private void stopEnemyAerisIdleAnimation() {
    if (enemyAerisIdleAnimationTimer != null && enemyAerisIdleAnimationTimer.isRunning()) {
        enemyAerisIdleAnimationTimer.stop();
        enemyAerisIdleSequenceIndex = 0;
        enemyAerisIdleFrameCounter = 0;
        System.out.println("⏹️ Enemy Aeris idle animation stopped");
    }
}

private void startAerisAttackAnimation() {
    stopAerisIdleAnimation();
    if (aerisAttackAnimationTimer != null && aerisAttackAnimationTimer.isRunning()) {
        aerisAttackAnimationTimer.stop();
    }
    if (aerisAttackFrames[0] == null || aerisLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Aeris attack - frames:" + (aerisAttackFrames[0]!=null) + " label:" + aerisLargePortraitLabel);
        return;
    }
    currentAerisAttackFrame = 0;
    aerisAttackFrameCounter = 0;
    final int tickMs = 16;
    aerisAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (aerisLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Aeris)) {
                stopAerisAttackAnimation();
                return;
            }
            aerisAttackFrameCounter++;
            int frameTicks = AERIS_ATTACK_FRAME_DURATIONS[currentAerisAttackFrame];
            if (aerisAttackFrameCounter >= frameTicks) {
                aerisAttackFrameCounter = 0;
                currentAerisAttackFrame++;
                if (currentAerisAttackFrame >= aerisAttackFrames.length) {
                    // Animation finished, return to idle
                    stopAerisAttackAnimation();
                    aerisAttackAnimationPlaying = false;
                    if (aerisIdleFrames[0] != null) {
                        startAerisIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = aerisAttackFrames[currentAerisAttackFrame];
                if (frame != null) {
                    aerisLargePortraitLabel.setIcon(frame);
                } else {
                    aerisLargePortraitLabel.setIcon(aerisAttackFrames[0]);
                }
                aerisLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Aeris attack timer error: " + ex.getMessage());
            stopAerisAttackAnimation();
        }
    });
    aerisAttackAnimationTimer.start();
    aerisLargePortraitLabel.setIcon(aerisAttackFrames[0]);
    aerisAttackAnimationPlaying = true;
    System.out.println("⚔️ Aeris attack animation started");
}

private void stopAerisAttackAnimation() {
    if (aerisAttackAnimationTimer != null && aerisAttackAnimationTimer.isRunning()) {
        aerisAttackAnimationTimer.stop();
        currentAerisAttackFrame = 0;
        aerisAttackFrameCounter = 0;
        System.out.println("⏹️ Aeris attack animation stopped");
    }
}

private void startEnemyAerisAttackAnimation() {
    System.out.println("▶️ startEnemyAerisAttackAnimation called");
    stopEnemyAerisIdleAnimation();
    if (enemyAerisAttackAnimationTimer != null && enemyAerisAttackAnimationTimer.isRunning()) {
        enemyAerisAttackAnimationTimer.stop();
    }
    if (enemyAerisAttackFrames[0] == null || enemyAerisLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Aeris attack - frames:" + (enemyAerisAttackFrames[0]!=null) + " label:" + enemyAerisLargePortraitLabel);
        return;
    }
    System.out.println("✅ Starting enemy Aeris attack animation with frames loaded");
    currentEnemyAerisAttackFrame = 0;
    enemyAerisAttackFrameCounter = 0;
    final int tickMs = 16;
    enemyAerisAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyAerisLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Aeris)) {
                stopEnemyAerisAttackAnimation();
                return;
            }
            enemyAerisAttackFrameCounter++;
            int frameTicks = AERIS_ATTACK_FRAME_DURATIONS[currentEnemyAerisAttackFrame];
            if (enemyAerisAttackFrameCounter >= frameTicks) {
                enemyAerisAttackFrameCounter = 0;
                currentEnemyAerisAttackFrame++;
                if (currentEnemyAerisAttackFrame >= enemyAerisAttackFrames.length) {
                    // Animation finished, return to idle
                    stopEnemyAerisAttackAnimation();
                    enemyAerisAttackAnimationPlaying = false;
                    if (enemyAerisIdleFrames[0] != null) {
                        startEnemyAerisIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = enemyAerisAttackFrames[currentEnemyAerisAttackFrame];
                if (frame != null) {
                    enemyAerisLargePortraitLabel.setIcon(frame);
                } else {
                    enemyAerisLargePortraitLabel.setIcon(enemyAerisAttackFrames[0]);
                }
                enemyAerisLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Aeris attack timer error: " + ex.getMessage());
            stopEnemyAerisAttackAnimation();
        }
    });
    enemyAerisAttackAnimationTimer.start();
    enemyAerisLargePortraitLabel.setIcon(enemyAerisAttackFrames[0]);
    enemyAerisAttackAnimationPlaying = true;
    System.out.println("⚔️ Enemy Aeris attack animation started");
}

private void stopEnemyAerisAttackAnimation() {
    if (enemyAerisAttackAnimationTimer != null && enemyAerisAttackAnimationTimer.isRunning()) {
        enemyAerisAttackAnimationTimer.stop();
        currentEnemyAerisAttackFrame = 0;
        enemyAerisAttackFrameCounter = 0;
        System.out.println("⏹️ Enemy Aeris attack animation stopped");
    }
}

private void startAerisDamagedAnimation() {
    stopAerisIdleAnimation();
    if (aerisDamagedAnimationTimer != null && aerisDamagedAnimationTimer.isRunning()) {
        aerisDamagedAnimationTimer.stop();
    }
    if (aerisDamagedFrames[0] == null || aerisLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Aeris damaged - frames:" + (aerisDamagedFrames[0]!=null) + " label:" + aerisLargePortraitLabel);
        return;
    }
    currentAerisDamagedFrame = 0;
    aerisDamagedFrameCounter = 0;
    final int tickMs = 16;
    aerisDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (aerisLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Aeris)) {
                stopAerisDamagedAnimation();
                return;
            }
            aerisDamagedFrameCounter++;
            int frameTicks = AERIS_DAMAGED_FRAME_DURATIONS[currentAerisDamagedFrame];
            if (aerisDamagedFrameCounter >= frameTicks) {
                aerisDamagedFrameCounter = 0;
                currentAerisDamagedFrame++;
                if (currentAerisDamagedFrame >= aerisDamagedFrames.length) {
                    // Animation finished, return to idle
                    stopAerisDamagedAnimation();
                    aerisDamagedAnimationPlaying = false;
                    if (aerisIdleFrames[0] != null) {
                        startAerisIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = aerisDamagedFrames[currentAerisDamagedFrame];
                if (frame != null) {
                    aerisLargePortraitLabel.setIcon(frame);
                } else {
                    aerisLargePortraitLabel.setIcon(aerisDamagedFrames[0]);
                }
                aerisLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Aeris damaged timer error: " + ex.getMessage());
            stopAerisDamagedAnimation();
        }
    });
    aerisDamagedAnimationTimer.start();
    aerisLargePortraitLabel.setIcon(aerisDamagedFrames[0]);
    aerisDamagedAnimationPlaying = true;
    System.out.println("💢 Aeris damaged animation started");
}

private void stopAerisDamagedAnimation() {
    if (aerisDamagedAnimationTimer != null && aerisDamagedAnimationTimer.isRunning()) {
        aerisDamagedAnimationTimer.stop();
        currentAerisDamagedFrame = 0;
        aerisDamagedFrameCounter = 0;
        System.out.println("⏹️ Aeris damaged animation stopped");
    }
}

private void startEnemyAerisDamagedAnimation() {
    stopEnemyAerisIdleAnimation();
    if (enemyAerisDamagedAnimationTimer != null && enemyAerisDamagedAnimationTimer.isRunning()) {
        enemyAerisDamagedAnimationTimer.stop();
    }
    if (enemyAerisDamagedFrames[0] == null || enemyAerisLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Aeris damaged - frames:" + (enemyAerisDamagedFrames[0]!=null) + " label:" + enemyAerisLargePortraitLabel);
        return;
    }
    currentEnemyAerisDamagedFrame = 0;
    enemyAerisDamagedFrameCounter = 0;
    final int tickMs = 16;
    enemyAerisDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyAerisLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Aeris)) {
                stopEnemyAerisDamagedAnimation();
                return;
            }
            enemyAerisDamagedFrameCounter++;
            int frameTicks = AERIS_DAMAGED_FRAME_DURATIONS[currentEnemyAerisDamagedFrame];
            if (enemyAerisDamagedFrameCounter >= frameTicks) {
                enemyAerisDamagedFrameCounter = 0;
                currentEnemyAerisDamagedFrame++;
                if (currentEnemyAerisDamagedFrame >= enemyAerisDamagedFrames.length) {
                    // Animation finished, return to idle
                    stopEnemyAerisDamagedAnimation();
                    enemyAerisDamagedAnimationPlaying = false;
                    if (enemyAerisIdleFrames[0] != null) {
                        startEnemyAerisIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = enemyAerisDamagedFrames[currentEnemyAerisDamagedFrame];
                if (frame != null) {
                    enemyAerisLargePortraitLabel.setIcon(frame);
                } else {
                    enemyAerisLargePortraitLabel.setIcon(enemyAerisDamagedFrames[0]);
                }
                enemyAerisLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Aeris damaged timer error: " + ex.getMessage());
            stopEnemyAerisDamagedAnimation();
        }
    });
    enemyAerisDamagedAnimationTimer.start();
    enemyAerisLargePortraitLabel.setIcon(enemyAerisDamagedFrames[0]);
    enemyAerisDamagedAnimationPlaying = true;
    System.out.println("💢 Enemy Aeris damaged animation started");
}

private void stopEnemyAerisDamagedAnimation() {
    if (enemyAerisDamagedAnimationTimer != null && enemyAerisDamagedAnimationTimer.isRunning()) {
        enemyAerisDamagedAnimationTimer.stop();
        currentEnemyAerisDamagedFrame = 0;
        enemyAerisDamagedFrameCounter = 0;
        System.out.println("⏹️ Enemy Aeris damaged animation stopped");
    }
}

private void startSeleneIdleAnimation() {
    stopSeleneIdleAnimation(); // Ensure no duplicate timers
    if (seleneIdleFrames[0] == null || seleneLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Selene idle - frames:" + (seleneIdleFrames[0]!=null) + " label:" + seleneLargePortraitLabel);
        return;
    }
    seleneIdleSequenceIndex = 0;
    seleneIdleFrameCounter = 0;
    final int tickMs = 16;
    final int frameDuration = 6; // ticks per frame
    seleneIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (seleneLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Selene)) {
                stopSeleneIdleAnimation();
                return;
            }
            seleneIdleFrameCounter++;
            if (seleneIdleFrameCounter >= frameDuration) {
                seleneIdleFrameCounter = 0;
                seleneIdleSequenceIndex = (seleneIdleSequenceIndex + 1) % SELENE_IDLE_SEQUENCE.length;
                int frameIndex = SELENE_IDLE_SEQUENCE[seleneIdleSequenceIndex];
                if (seleneIdleFrames[frameIndex] != null) {
                    seleneLargePortraitLabel.setIcon(seleneIdleFrames[frameIndex]);
                    seleneLargePortraitLabel.repaint();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Selene idle timer error: " + ex.getMessage());
            stopSeleneIdleAnimation();
        }
    });
    seleneIdleAnimationTimer.start();
    seleneLargePortraitLabel.setIcon(seleneIdleFrames[0]);
    System.out.println("▶️ Selene idle animation started");
}

private void stopSeleneIdleAnimation() {
    if (seleneIdleAnimationTimer != null && seleneIdleAnimationTimer.isRunning()) {
        seleneIdleAnimationTimer.stop();
        seleneIdleSequenceIndex = 0;
        seleneIdleFrameCounter = 0;
        System.out.println("⏹️ Selene idle animation stopped");
    }
}

private void startEnemySeleneIdleAnimation() {
    stopEnemySeleneIdleAnimation(); // Ensure no duplicate timers
    if (enemySeleneIdleFrames[0] == null || enemySeleneLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Selene idle - frames:" + (enemySeleneIdleFrames[0]!=null) + " label:" + enemySeleneLargePortraitLabel);
        return;
    }
    enemySeleneIdleSequenceIndex = 0;
    enemySeleneIdleFrameCounter = 0;
    final int tickMs = 16;
    final int frameDuration = 6; // ticks per frame
    enemySeleneIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemySeleneLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Selene)) {
                stopEnemySeleneIdleAnimation();
                return;
            }
            enemySeleneIdleFrameCounter++;
            if (enemySeleneIdleFrameCounter >= frameDuration) {
                enemySeleneIdleFrameCounter = 0;
                enemySeleneIdleSequenceIndex = (enemySeleneIdleSequenceIndex + 1) % SELENE_IDLE_SEQUENCE.length;
                int frameIndex = SELENE_IDLE_SEQUENCE[enemySeleneIdleSequenceIndex];
                if (enemySeleneIdleFrames[frameIndex] != null) {
                    enemySeleneLargePortraitLabel.setIcon(enemySeleneIdleFrames[frameIndex]);
                    enemySeleneLargePortraitLabel.repaint();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Selene idle timer error: " + ex.getMessage());
            stopEnemySeleneIdleAnimation();
        }
    });
    enemySeleneIdleAnimationTimer.start();
    enemySeleneLargePortraitLabel.setIcon(enemySeleneIdleFrames[0]);
    System.out.println("▶️ Enemy Selene idle animation started");
}

private void stopEnemySeleneIdleAnimation() {
    if (enemySeleneIdleAnimationTimer != null && enemySeleneIdleAnimationTimer.isRunning()) {
        enemySeleneIdleAnimationTimer.stop();
        enemySeleneIdleSequenceIndex = 0;
        enemySeleneIdleFrameCounter = 0;
        System.out.println("⏹️ Enemy Selene idle animation stopped");
    }
}

private void startFlueIdleAnimation() {
    stopFlueIdleAnimation(); // Ensure no duplicate timers
    if (flueIdleFrames[0] == null || flueLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Flue idle - frames:" + (flueIdleFrames[0]!=null) + " label:" + flueLargePortraitLabel);
        return;
    }
    flueIdleSequenceIndex = 0;
    flueIdleFrameCounter = 0;
    final int tickMs = 16;
    final int frameDuration = 6; // ticks per frame
    flueIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (flueLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Flue)) {
                stopFlueIdleAnimation();
                return;
            }
            flueIdleFrameCounter++;
            if (flueIdleFrameCounter >= frameDuration) {
                flueIdleFrameCounter = 0;
                flueIdleSequenceIndex = (flueIdleSequenceIndex + 1) % FLUE_IDLE_SEQUENCE.length;
                int frameIndex = FLUE_IDLE_SEQUENCE[flueIdleSequenceIndex];
                if (flueIdleFrames[frameIndex] != null) {
                    flueLargePortraitLabel.setIcon(flueIdleFrames[frameIndex]);
                    flueLargePortraitLabel.repaint();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Flue idle timer error: " + ex.getMessage());
            stopFlueIdleAnimation();
        }
    });
    flueIdleAnimationTimer.start();
    flueLargePortraitLabel.setIcon(flueIdleFrames[0]);
    System.out.println("▶️ Flue idle animation started");
}

private void stopFlueIdleAnimation() {
    if (flueIdleAnimationTimer != null && flueIdleAnimationTimer.isRunning()) {
        flueIdleAnimationTimer.stop();
        flueIdleSequenceIndex = 0;
        flueIdleFrameCounter = 0;
        System.out.println("⏹️ Flue idle animation stopped");
    }
}

private void startEnemyFlueIdleAnimation() {
    stopEnemyFlueIdleAnimation(); // Ensure no duplicate timers
    if (enemyFlueIdleFrames[0] == null || enemyFlueLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Flue idle - frames:" + (enemyFlueIdleFrames[0]!=null) + " label:" + enemyFlueLargePortraitLabel);
        return;
    }
    enemyFlueIdleSequenceIndex = 0;
    enemyFlueIdleFrameCounter = 0;
    final int tickMs = 16;
    final int frameDuration = 6; // ticks per frame
    enemyFlueIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyFlueLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Flue)) {
                stopEnemyFlueIdleAnimation();
                return;
            }
            enemyFlueIdleFrameCounter++;
            if (enemyFlueIdleFrameCounter >= frameDuration) {
                enemyFlueIdleFrameCounter = 0;
                enemyFlueIdleSequenceIndex = (enemyFlueIdleSequenceIndex + 1) % FLUE_IDLE_SEQUENCE.length;
                int frameIndex = FLUE_IDLE_SEQUENCE[enemyFlueIdleSequenceIndex];
                if (enemyFlueIdleFrames[frameIndex] != null) {
                    enemyFlueLargePortraitLabel.setIcon(enemyFlueIdleFrames[frameIndex]);
                    enemyFlueLargePortraitLabel.repaint();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Flue idle timer error: " + ex.getMessage());
            stopEnemyFlueIdleAnimation();
        }
    });
    enemyFlueIdleAnimationTimer.start();
    enemyFlueLargePortraitLabel.setIcon(enemyFlueIdleFrames[0]);
    System.out.println("▶️ Enemy Flue idle animation started");
}

private void stopEnemyFlueIdleAnimation() {
    if (enemyFlueIdleAnimationTimer != null && enemyFlueIdleAnimationTimer.isRunning()) {
        enemyFlueIdleAnimationTimer.stop();
        enemyFlueIdleSequenceIndex = 0;
        enemyFlueIdleFrameCounter = 0;
        System.out.println("⏹️ Enemy Flue idle animation stopped");
    }
}

private void startFlueAttackAnimation() {
    stopFlueIdleAnimation();
    if (flueAttackAnimationTimer != null && flueAttackAnimationTimer.isRunning()) {
        flueAttackAnimationTimer.stop();
    }
    if (flueAttackFrames[0] == null || flueLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Flue attack - frames:" + (flueAttackFrames[0]!=null) + " label:" + flueLargePortraitLabel);
        return;
    }
    currentFlueAttackFrame = 0;
    flueAttackFrameCounter = 0;
    final int tickMs = 16;
    flueAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (flueLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Flue)) {
                stopFlueAttackAnimation();
                return;
            }
            flueAttackFrameCounter++;
            int frameTicks = FLUE_ATTACK_FRAME_DURATIONS[currentFlueAttackFrame];
            if (flueAttackFrameCounter >= frameTicks) {
                flueAttackFrameCounter = 0;
                currentFlueAttackFrame++;
                if (currentFlueAttackFrame >= flueAttackFrames.length) {
                    // Animation finished, return to idle
                    stopFlueAttackAnimation();
                    flueAttackAnimationPlaying = false;
                    if (flueIdleFrames[0] != null) {
                        startFlueIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = flueAttackFrames[currentFlueAttackFrame];
                if (frame != null) {
                    flueLargePortraitLabel.setIcon(frame);
                } else {
                    flueLargePortraitLabel.setIcon(flueAttackFrames[0]);
                }
                flueLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Flue attack timer error: " + ex.getMessage());
            stopFlueAttackAnimation();
        }
    });
    flueAttackAnimationTimer.start();
    flueLargePortraitLabel.setIcon(flueAttackFrames[0]);
    flueAttackAnimationPlaying = true;
    System.out.println("⚔️ Flue attack animation started");
}

private void stopFlueAttackAnimation() {
    if (flueAttackAnimationTimer != null && flueAttackAnimationTimer.isRunning()) {
        flueAttackAnimationTimer.stop();
        currentFlueAttackFrame = 0;
        flueAttackFrameCounter = 0;
        System.out.println("⏹️ Flue attack animation stopped");
    }
}

private void startEnemyFlueAttackAnimation() {
    stopEnemyFlueIdleAnimation();
    if (enemyFlueAttackAnimationTimer != null && enemyFlueAttackAnimationTimer.isRunning()) {
        enemyFlueAttackAnimationTimer.stop();
    }
    if (enemyFlueAttackFrames[0] == null || enemyFlueLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Flue attack - frames:" + (enemyFlueAttackFrames[0]!=null) + " label:" + enemyFlueLargePortraitLabel);
        return;
    }
    currentEnemyFlueAttackFrame = 0;
    enemyFlueAttackFrameCounter = 0;
    final int tickMs = 16;
    enemyFlueAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyFlueLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Flue)) {
                stopEnemyFlueAttackAnimation();
                return;
            }
            enemyFlueAttackFrameCounter++;
            int frameTicks = FLUE_ATTACK_FRAME_DURATIONS[currentEnemyFlueAttackFrame];
            if (enemyFlueAttackFrameCounter >= frameTicks) {
                enemyFlueAttackFrameCounter = 0;
                currentEnemyFlueAttackFrame++;
                if (currentEnemyFlueAttackFrame >= enemyFlueAttackFrames.length) {
                    // Animation finished, return to idle
                    stopEnemyFlueAttackAnimation();
                    enemyFlueAttackAnimationPlaying = false;
                    if (enemyFlueIdleFrames[0] != null) {
                        startEnemyFlueIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = enemyFlueAttackFrames[currentEnemyFlueAttackFrame];
                if (frame != null) {
                    enemyFlueLargePortraitLabel.setIcon(frame);
                } else {
                    enemyFlueLargePortraitLabel.setIcon(enemyFlueAttackFrames[0]);
                }
                enemyFlueLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Flue attack timer error: " + ex.getMessage());
            stopEnemyFlueAttackAnimation();
        }
    });
    enemyFlueAttackAnimationTimer.start();
    enemyFlueLargePortraitLabel.setIcon(enemyFlueAttackFrames[0]);
    enemyFlueAttackAnimationPlaying = true;
    System.out.println("⚔️ Enemy Flue attack animation started");
}

private void stopEnemyFlueAttackAnimation() {
    if (enemyFlueAttackAnimationTimer != null && enemyFlueAttackAnimationTimer.isRunning()) {
        enemyFlueAttackAnimationTimer.stop();
        currentEnemyFlueAttackFrame = 0;
        enemyFlueAttackFrameCounter = 0;
        System.out.println("⏹️ Enemy Flue attack animation stopped");
    }
}

private void startFlueDamagedAnimation() {
    stopFlueIdleAnimation();
    if (flueDamagedAnimationTimer != null && flueDamagedAnimationTimer.isRunning()) {
        flueDamagedAnimationTimer.stop();
    }
    if (flueDamagedFrames[0] == null || flueLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Flue damaged - frames:" + (flueDamagedFrames[0]!=null) + " label:" + flueLargePortraitLabel);
        return;
    }
    currentFlueDamagedFrame = 0;
    flueDamagedFrameCounter = 0;
    final int tickMs = 16;
    flueDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (flueLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Flue)) {
                stopFlueDamagedAnimation();
                return;
            }
            flueDamagedFrameCounter++;
            int frameTicks = FLUE_DAMAGED_FRAME_DURATIONS[currentFlueDamagedFrame];
            if (flueDamagedFrameCounter >= frameTicks) {
                flueDamagedFrameCounter = 0;
                currentFlueDamagedFrame++;
                if (currentFlueDamagedFrame >= flueDamagedFrames.length) {
                    // Animation finished, return to idle
                    stopFlueDamagedAnimation();
                    flueDamagedAnimationPlaying = false;
                    if (flueIdleFrames[0] != null) {
                        startFlueIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = flueDamagedFrames[currentFlueDamagedFrame];
                if (frame != null) {
                    flueLargePortraitLabel.setIcon(frame);
                } else {
                    flueLargePortraitLabel.setIcon(flueDamagedFrames[0]);
                }
                flueLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Flue damaged timer error: " + ex.getMessage());
            stopFlueDamagedAnimation();
        }
    });
    flueDamagedAnimationTimer.start();
    flueLargePortraitLabel.setIcon(flueDamagedFrames[0]);
    flueDamagedAnimationPlaying = true;
    System.out.println("💢 Flue damaged animation started");
}

private void stopFlueDamagedAnimation() {
    if (flueDamagedAnimationTimer != null && flueDamagedAnimationTimer.isRunning()) {
        flueDamagedAnimationTimer.stop();
        currentFlueDamagedFrame = 0;
        flueDamagedFrameCounter = 0;
        System.out.println("⏹️ Flue damaged animation stopped");
    }
}

private void startEnemyFlueDamagedAnimation() {
    stopEnemyFlueIdleAnimation();
    if (enemyFlueDamagedAnimationTimer != null && enemyFlueDamagedAnimationTimer.isRunning()) {
        enemyFlueDamagedAnimationTimer.stop();
    }
    if (enemyFlueDamagedFrames[0] == null || enemyFlueLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Flue damaged - frames:" + (enemyFlueDamagedFrames[0]!=null) + " label:" + enemyFlueLargePortraitLabel);
        return;
    }
    currentEnemyFlueDamagedFrame = 0;
    enemyFlueDamagedFrameCounter = 0;
    final int tickMs = 16;
    enemyFlueDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyFlueLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Flue)) {
                stopEnemyFlueDamagedAnimation();
                return;
            }
            enemyFlueDamagedFrameCounter++;
            int frameTicks = FLUE_DAMAGED_FRAME_DURATIONS[currentEnemyFlueDamagedFrame];
            if (enemyFlueDamagedFrameCounter >= frameTicks) {
                enemyFlueDamagedFrameCounter = 0;
                currentEnemyFlueDamagedFrame++;
                if (currentEnemyFlueDamagedFrame >= enemyFlueDamagedFrames.length) {
                    // Animation finished, return to idle
                    stopEnemyFlueDamagedAnimation();
                    enemyFlueDamagedAnimationPlaying = false;
                    if (enemyFlueIdleFrames[0] != null) {
                        startEnemyFlueIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = enemyFlueDamagedFrames[currentEnemyFlueDamagedFrame];
                if (frame != null) {
                    enemyFlueLargePortraitLabel.setIcon(frame);
                } else {
                    enemyFlueLargePortraitLabel.setIcon(enemyFlueDamagedFrames[0]);
                }
                enemyFlueLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Flue damaged timer error: " + ex.getMessage());
            stopEnemyFlueDamagedAnimation();
        }
    });
    enemyFlueDamagedAnimationTimer.start();
    enemyFlueLargePortraitLabel.setIcon(enemyFlueDamagedFrames[0]);
    enemyFlueDamagedAnimationPlaying = true;
    System.out.println("💢 Enemy Flue damaged animation started");
}

private void stopEnemyFlueDamagedAnimation() {
    if (enemyFlueDamagedAnimationTimer != null && enemyFlueDamagedAnimationTimer.isRunning()) {
        enemyFlueDamagedAnimationTimer.stop();
        currentEnemyFlueDamagedFrame = 0;
        enemyFlueDamagedFrameCounter = 0;
        System.out.println("⏹️ Enemy Flue damaged animation stopped");
    }
}

private void startSeleneAttackAnimation() {
    stopSeleneIdleAnimation();
    if (seleneAttackAnimationTimer != null && seleneAttackAnimationTimer.isRunning()) {
        seleneAttackAnimationTimer.stop();
    }
    if (seleneAttackFrames[0] == null || seleneLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Selene attack - frames:" + (seleneAttackFrames[0]!=null) + " label:" + seleneLargePortraitLabel);
        return;
    }
    currentSeleneAttackFrame = 0;
    seleneAttackFrameCounter = 0;
    final int tickMs = 16;
    seleneAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (seleneLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Selene)) {
                stopSeleneAttackAnimation();
                return;
            }
            seleneAttackFrameCounter++;
            int frameTicks = SELENE_ATTACK_FRAME_DURATIONS[currentSeleneAttackFrame];
            if (seleneAttackFrameCounter >= frameTicks) {
                seleneAttackFrameCounter = 0;
                currentSeleneAttackFrame++;
                if (currentSeleneAttackFrame >= seleneAttackFrames.length) {
                    // Animation finished, return to idle
                    stopSeleneAttackAnimation();
                    seleneAttackAnimationPlaying = false;
                    if (seleneIdleFrames[0] != null) {
                        startSeleneIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = seleneAttackFrames[currentSeleneAttackFrame];
                if (frame != null) {
                    seleneLargePortraitLabel.setIcon(frame);
                } else {
                    seleneLargePortraitLabel.setIcon(seleneAttackFrames[0]);
                }
                seleneLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Selene attack timer error: " + ex.getMessage());
            stopSeleneAttackAnimation();
        }
    });
    seleneAttackAnimationTimer.start();
    seleneLargePortraitLabel.setIcon(seleneAttackFrames[0]);
    seleneAttackAnimationPlaying = true;
    System.out.println("⚔️ Selene attack animation started");
}

private void stopSeleneAttackAnimation() {
    if (seleneAttackAnimationTimer != null && seleneAttackAnimationTimer.isRunning()) {
        seleneAttackAnimationTimer.stop();
        currentSeleneAttackFrame = 0;
        seleneAttackFrameCounter = 0;
        System.out.println("⏹️ Selene attack animation stopped");
    }
}

private void startEnemySeleneAttackAnimation() {
    stopEnemySeleneIdleAnimation();
    if (enemySeleneAttackAnimationTimer != null && enemySeleneAttackAnimationTimer.isRunning()) {
        enemySeleneAttackAnimationTimer.stop();
    }
    if (enemySeleneAttackFrames[0] == null || enemySeleneLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Selene attack - frames:" + (enemySeleneAttackFrames[0]!=null) + " label:" + enemySeleneLargePortraitLabel);
        return;
    }
    currentEnemySeleneAttackFrame = 0;
    enemySeleneAttackFrameCounter = 0;
    final int tickMs = 16;
    enemySeleneAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemySeleneLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Selene)) {
                stopEnemySeleneAttackAnimation();
                return;
            }
            enemySeleneAttackFrameCounter++;
            int frameTicks = SELENE_ATTACK_FRAME_DURATIONS[currentEnemySeleneAttackFrame];
            if (enemySeleneAttackFrameCounter >= frameTicks) {
                enemySeleneAttackFrameCounter = 0;
                currentEnemySeleneAttackFrame++;
                if (currentEnemySeleneAttackFrame >= enemySeleneAttackFrames.length) {
                    // Animation finished, return to idle
                    stopEnemySeleneAttackAnimation();
                    enemySeleneAttackAnimationPlaying = false;
                    if (enemySeleneIdleFrames[0] != null) {
                        startEnemySeleneIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = enemySeleneAttackFrames[currentEnemySeleneAttackFrame];
                if (frame != null) {
                    enemySeleneLargePortraitLabel.setIcon(frame);
                } else {
                    enemySeleneLargePortraitLabel.setIcon(enemySeleneAttackFrames[0]);
                }
                enemySeleneLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Selene attack timer error: " + ex.getMessage());
            stopEnemySeleneAttackAnimation();
        }
    });
    enemySeleneAttackAnimationTimer.start();
    enemySeleneLargePortraitLabel.setIcon(enemySeleneAttackFrames[0]);
    enemySeleneAttackAnimationPlaying = true;
    System.out.println("⚔️ Enemy Selene attack animation started");
}

private void stopEnemySeleneAttackAnimation() {
    if (enemySeleneAttackAnimationTimer != null && enemySeleneAttackAnimationTimer.isRunning()) {
        enemySeleneAttackAnimationTimer.stop();
        currentEnemySeleneAttackFrame = 0;
        enemySeleneAttackFrameCounter = 0;
        System.out.println("⏹️ Enemy Selene attack animation stopped");
    }
}

private void startSeleneDamagedAnimation() {
    stopSeleneIdleAnimation();
    if (seleneDamagedAnimationTimer != null && seleneDamagedAnimationTimer.isRunning()) {
        seleneDamagedAnimationTimer.stop();
    }
    if (seleneDamagedFrames[0] == null || seleneLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Selene damaged - frames:" + (seleneDamagedFrames[0]!=null) + " label:" + seleneLargePortraitLabel);
        return;
    }
    currentSeleneDamagedFrame = 0;
    seleneDamagedFrameCounter = 0;
    final int tickMs = 16;
    seleneDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (seleneLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Selene)) {
                stopSeleneDamagedAnimation();
                return;
            }
            seleneDamagedFrameCounter++;
            int frameTicks = SELENE_DAMAGED_FRAME_DURATIONS[currentSeleneDamagedFrame];
            if (seleneDamagedFrameCounter >= frameTicks) {
                seleneDamagedFrameCounter = 0;
                currentSeleneDamagedFrame++;
                if (currentSeleneDamagedFrame >= seleneDamagedFrames.length) {
                    // Animation finished, return to idle
                    stopSeleneDamagedAnimation();
                    seleneDamagedAnimationPlaying = false;
                    if (seleneIdleFrames[0] != null) {
                        startSeleneIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = seleneDamagedFrames[currentSeleneDamagedFrame];
                if (frame != null) {
                    seleneLargePortraitLabel.setIcon(frame);
                } else {
                    seleneLargePortraitLabel.setIcon(seleneDamagedFrames[0]);
                }
                seleneLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Selene damaged timer error: " + ex.getMessage());
            stopSeleneDamagedAnimation();
        }
    });
    seleneDamagedAnimationTimer.start();
    seleneLargePortraitLabel.setIcon(seleneDamagedFrames[0]);
    seleneDamagedAnimationPlaying = true;
    System.out.println("💢 Selene damaged animation started");
}

private void stopSeleneDamagedAnimation() {
    if (seleneDamagedAnimationTimer != null && seleneDamagedAnimationTimer.isRunning()) {
        seleneDamagedAnimationTimer.stop();
        currentSeleneDamagedFrame = 0;
        seleneDamagedFrameCounter = 0;
        System.out.println("⏹️ Selene damaged animation stopped");
    }
}

private void startEnemySeleneDamagedAnimation() {
    stopEnemySeleneIdleAnimation();
    if (enemySeleneDamagedAnimationTimer != null && enemySeleneDamagedAnimationTimer.isRunning()) {
        enemySeleneDamagedAnimationTimer.stop();
    }
    if (enemySeleneDamagedFrames[0] == null || enemySeleneLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Selene damaged - frames:" + (enemySeleneDamagedFrames[0]!=null) + " label:" + enemySeleneLargePortraitLabel);
        return;
    }
    currentEnemySeleneDamagedFrame = 0;
    enemySeleneDamagedFrameCounter = 0;
    final int tickMs = 16;
    enemySeleneDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemySeleneLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Selene)) {
                stopEnemySeleneDamagedAnimation();
                return;
            }
            enemySeleneDamagedFrameCounter++;
            int frameTicks = SELENE_DAMAGED_FRAME_DURATIONS[currentEnemySeleneDamagedFrame];
            if (enemySeleneDamagedFrameCounter >= frameTicks) {
                enemySeleneDamagedFrameCounter = 0;
                currentEnemySeleneDamagedFrame++;
                if (currentEnemySeleneDamagedFrame >= enemySeleneDamagedFrames.length) {
                    // Animation finished, return to idle
                    stopEnemySeleneDamagedAnimation();
                    enemySeleneDamagedAnimationPlaying = false;
                    if (enemySeleneIdleFrames[0] != null) {
                        startEnemySeleneIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = enemySeleneDamagedFrames[currentEnemySeleneDamagedFrame];
                if (frame != null) {
                    enemySeleneLargePortraitLabel.setIcon(frame);
                } else {
                    enemySeleneLargePortraitLabel.setIcon(enemySeleneDamagedFrames[0]);
                }
                enemySeleneLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Selene damaged timer error: " + ex.getMessage());
            stopEnemySeleneDamagedAnimation();
        }
    });
    enemySeleneDamagedAnimationTimer.start();
    enemySeleneLargePortraitLabel.setIcon(enemySeleneDamagedFrames[0]);
    enemySeleneDamagedAnimationPlaying = true;
    System.out.println("💢 Enemy Selene damaged animation started");
}

private void stopEnemySeleneDamagedAnimation() {
    if (enemySeleneDamagedAnimationTimer != null && enemySeleneDamagedAnimationTimer.isRunning()) {
        enemySeleneDamagedAnimationTimer.stop();
        currentEnemySeleneDamagedFrame = 0;
        enemySeleneDamagedFrameCounter = 0;
        System.out.println("⏹️ Enemy Selene damaged animation stopped");
    }
}

private void startValeriusAttackAnimation() {
    // Stop all other Valerius animations
    stopValeriusIdleAnimation();
    if (valeriusAttackAnimationTimer != null && valeriusAttackAnimationTimer.isRunning()) {
        valeriusAttackAnimationTimer.stop();
    }
    if (valeriusAttackFrames[0] == null || valeriusLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Valerius attack - frames:" + (valeriusAttackFrames[0]!=null));
        return;
    }
    currentValeriusAttackFrame = 0;
    valeriusAttackFrameCounter = 0;
    final int tickMs = 16;
    valeriusAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (valeriusLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Valerius)) {
                stopValeriusAttackAnimation();
                return;
            }
            valeriusAttackFrameCounter++;
            int frameTicks = VALERIUS_ATTACK_FRAME_DURATIONS[currentValeriusAttackFrame];
            if (valeriusAttackFrameCounter >= frameTicks) {
                valeriusAttackFrameCounter = 0;
                currentValeriusAttackFrame = (currentValeriusAttackFrame + 1) % valeriusAttackFrames.length;
                ImageIcon frame = valeriusAttackFrames[currentValeriusAttackFrame];
                if (frame != null) {
                    valeriusLargePortraitLabel.setIcon(frame);
                } else {
                    valeriusLargePortraitLabel.setIcon(valeriusAttackFrames[0]);
                }
                // On last frame, schedule return to idle
                if (currentValeriusAttackFrame == valeriusAttackFrames.length - 1) {
                    // Stop attack timer before refresh
                    stopValeriusAttackAnimation();
                    valeriusAttackAnimationPlaying = false;
                    // Small delay before returning to normal portrait
                    javax.swing.Timer returnTimer = new javax.swing.Timer(300, ev -> {
                        // Return to idle animation
                        if (valeriusIdleFrames[0] != null) {
                            startValeriusIdleAnimation();
                        }
                    });
                    returnTimer.setRepeats(false);
                    returnTimer.start();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Valerius attack timer error: " + ex.getMessage());
            stopValeriusAttackAnimation();
        }
    });
    valeriusAttackAnimationTimer.start();
    valeriusLargePortraitLabel.setIcon(valeriusAttackFrames[0]);
    System.out.println("⚔️ Valerius attack animation started");
}

private void stopValeriusAttackAnimation() {
    if (valeriusAttackAnimationTimer != null && valeriusAttackAnimationTimer.isRunning()) {
        valeriusAttackAnimationTimer.stop();
        currentValeriusAttackFrame = 0;
        valeriusAttackFrameCounter = 0;
        System.out.println("⏹️ Valerius attack animation stopped");
    }
}

private void startEnemyValeriusAttackAnimation() {
    // Stop all other enemy Valerius animations
    stopEnemyValeriusIdleAnimation();
    if (enemyValeriusAttackAnimationTimer != null && enemyValeriusAttackAnimationTimer.isRunning()) {
        enemyValeriusAttackAnimationTimer.stop();
    }
    if (enemyValeriusAttackFrames[0] == null || enemyValeriusLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Valerius attack - frames:" + (enemyValeriusAttackFrames[0]!=null));
        return;
    }
    currentEnemyValeriusAttackFrame = 0;
    enemyValeriusAttackFrameCounter = 0;
    final int tickMs = 16;
    enemyValeriusAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyValeriusLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Valerius)) {
                stopEnemyValeriusAttackAnimation();
                return;
            }
            enemyValeriusAttackFrameCounter++;
            int frameTicks = VALERIUS_ATTACK_FRAME_DURATIONS[currentEnemyValeriusAttackFrame];
            if (enemyValeriusAttackFrameCounter >= frameTicks) {
                enemyValeriusAttackFrameCounter = 0;
                currentEnemyValeriusAttackFrame = (currentEnemyValeriusAttackFrame + 1) % enemyValeriusAttackFrames.length;
                ImageIcon frame = enemyValeriusAttackFrames[currentEnemyValeriusAttackFrame];
                if (frame != null) {
                    enemyValeriusLargePortraitLabel.setIcon(frame);
                } else {
                    enemyValeriusLargePortraitLabel.setIcon(enemyValeriusAttackFrames[0]);
                }
                // On last frame, schedule return to idle
                if (currentEnemyValeriusAttackFrame == enemyValeriusAttackFrames.length - 1) {
                    // Stop attack timer before refresh
                    stopEnemyValeriusAttackAnimation();
                    enemyValeriusAttackAnimationPlaying = false;
                    // Small delay before returning to normal portrait
                    javax.swing.Timer returnTimer = new javax.swing.Timer(300, ev -> {
                        // Return to idle animation
                        if (enemyValeriusIdleFrames[0] != null) {
                            startEnemyValeriusIdleAnimation();
                        }
                    });
                    returnTimer.setRepeats(false);
                    returnTimer.start();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Valerius attack timer error: " + ex.getMessage());
            stopEnemyValeriusAttackAnimation();
        }
    });
    enemyValeriusAttackAnimationTimer.start();
    enemyValeriusLargePortraitLabel.setIcon(enemyValeriusAttackFrames[0]);
    System.out.println("⚔️ Enemy Valerius attack animation started");
}

private void stopEnemyValeriusAttackAnimation() {
    if (enemyValeriusAttackAnimationTimer != null && enemyValeriusAttackAnimationTimer.isRunning()) {
        enemyValeriusAttackAnimationTimer.stop();
        currentEnemyValeriusAttackFrame = 0;
        enemyValeriusAttackFrameCounter = 0;
        System.out.println("⏹️ Enemy Valerius attack animation stopped");
    }
}

private void startEnemyKaelIdleAnimation() {
    stopEnemyKaelIdleAnimation();
    if (enemyKaelIdleFrames[0] == null || enemyKaelLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Kael idle - frames:" + (enemyKaelIdleFrames[0]!=null) + " label:" + enemyKaelLargePortraitLabel);
        return;
    }
    enemyKaelIdleSequenceIndex = 0;
    enemyKaelIdleFrameCounter = 0;
    final int tickMs = 16;
    final int frameDuration = 30;
    enemyKaelIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyKaelLargePortraitLabel == null) return;
            enemyKaelIdleFrameCounter++;
            if (enemyKaelIdleFrameCounter >= frameDuration) {
                enemyKaelIdleFrameCounter = 0;
                enemyKaelIdleSequenceIndex = (enemyKaelIdleSequenceIndex + 1) % KAEL_IDLE_SEQUENCE.length;
                int frameIndex = KAEL_IDLE_SEQUENCE[enemyKaelIdleSequenceIndex];
                if (enemyKaelIdleFrames[frameIndex] != null) {
                    enemyKaelLargePortraitLabel.setIcon(enemyKaelIdleFrames[frameIndex]);
                    enemyKaelLargePortraitLabel.repaint();
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Kael idle timer error: " + ex.getMessage());
            stopEnemyKaelIdleAnimation();
        }
    });
    enemyKaelIdleAnimationTimer.start();
    int initialFrame = KAEL_IDLE_SEQUENCE[0];
    enemyKaelLargePortraitLabel.setIcon(enemyKaelIdleFrames[initialFrame]);
    System.out.println("▶️ Enemy Kael idle animation started");
}

private void stopEnemyKaelIdleAnimation() {
    if (enemyKaelIdleAnimationTimer != null && enemyKaelIdleAnimationTimer.isRunning()) {
        enemyKaelIdleAnimationTimer.stop();
        enemyKaelIdleSequenceIndex = 0;
        enemyKaelIdleFrameCounter = 0;
        System.out.println("⏹️ Enemy Kael idle animation stopped");
    }
}

private void startKaelAttackAnimation() {
    // Stop all other Kael animations
    stopKaelIdleAnimation();
    if (kaelAttackAnimationTimer != null && kaelAttackAnimationTimer.isRunning()) {
        kaelAttackAnimationTimer.stop();
    }
    if (kaelAttackFrames[0] == null || kaelLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Kael attack - frames:" + (kaelAttackFrames[0]!=null));
        return;
    }
    currentKaelAttackFrame = 0;
    kaelAttackFrameCounter = 0;
    final int tickMs = 16;
    final int[] ATTACK_FRAME_DURATIONS = {4, 4, 8}; // ticks (~0.5s total)
    kaelAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (kaelLargePortraitLabel == null) return;
            if (!kaelAttackAnimationPlaying) return; // Should be set before calling
            kaelAttackFrameCounter++;
            int frameTicks = ATTACK_FRAME_DURATIONS[currentKaelAttackFrame];
            if (kaelAttackFrameCounter >= frameTicks) {
                kaelAttackFrameCounter = 0;
                currentKaelAttackFrame++;
                if (currentKaelAttackFrame >= kaelAttackFrames.length) {
                    // Animation finished
                    stopKaelAttackAnimation();
                    kaelAttackAnimationPlaying = false;
                    // Return to idle
                    if (kaelIdleFrames[0] != null) {
                        startKaelIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = kaelAttackFrames[currentKaelAttackFrame];
                if (frame != null) {
                    kaelLargePortraitLabel.setIcon(frame);
                } else {
                    kaelLargePortraitLabel.setIcon(kaelAttackFrames[0]);
                }
                kaelLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Kael attack timer error: " + ex.getMessage());
            stopKaelAttackAnimation();
        }
    });
    kaelAttackAnimationTimer.start();
    kaelLargePortraitLabel.setIcon(kaelAttackFrames[0]);
    kaelAttackAnimationPlaying = true;
    System.out.println("⚔️ Kael attack animation started");
}

private void stopKaelAttackAnimation() {
    if (kaelAttackAnimationTimer != null && kaelAttackAnimationTimer.isRunning()) {
        kaelAttackAnimationTimer.stop();
        currentKaelAttackFrame = 0;
        kaelAttackFrameCounter = 0;
        System.out.println("⏹️ Kael attack animation stopped");
    }
}

private void startEnemyKaelAttackAnimation() {
    // Stop all other enemy Kael animations
    stopEnemyKaelIdleAnimation();
    if (enemyKaelAttackAnimationTimer != null && enemyKaelAttackAnimationTimer.isRunning()) {
        enemyKaelAttackAnimationTimer.stop();
    }
    if (enemyKaelAttackFrames[0] == null || enemyKaelLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Kael attack - frames:" + (enemyKaelAttackFrames[0]!=null));
        return;
    }
    currentEnemyKaelAttackFrame = 0;
    enemyKaelAttackFrameCounter = 0;
    final int tickMs = 16;
    final int[] ATTACK_FRAME_DURATIONS = {4, 4, 8};
    enemyKaelAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyKaelLargePortraitLabel == null) return;
            if (!enemyKaelAttackAnimationPlaying) return;
            enemyKaelAttackFrameCounter++;
            int frameTicks = ATTACK_FRAME_DURATIONS[currentEnemyKaelAttackFrame];
            if (enemyKaelAttackFrameCounter >= frameTicks) {
                enemyKaelAttackFrameCounter = 0;
                currentEnemyKaelAttackFrame++;
                if (currentEnemyKaelAttackFrame >= enemyKaelAttackFrames.length) {
                    // Animation finished
                    stopEnemyKaelAttackAnimation();
                    enemyKaelAttackAnimationPlaying = false;
                    // Return to idle
                    if (enemyKaelIdleFrames[0] != null) {
                        startEnemyKaelIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = enemyKaelAttackFrames[currentEnemyKaelAttackFrame];
                if (frame != null) {
                    enemyKaelLargePortraitLabel.setIcon(frame);
                } else {
                    enemyKaelLargePortraitLabel.setIcon(enemyKaelAttackFrames[0]);
                }
                enemyKaelLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Kael attack timer error: " + ex.getMessage());
            stopEnemyKaelAttackAnimation();
        }
    });
    enemyKaelAttackAnimationTimer.start();
    enemyKaelLargePortraitLabel.setIcon(enemyKaelAttackFrames[0]);
    enemyKaelAttackAnimationPlaying = true;
    System.out.println("⚔️ Enemy Kael attack animation started");
}

private void stopEnemyKaelAttackAnimation() {
    if (enemyKaelAttackAnimationTimer != null && enemyKaelAttackAnimationTimer.isRunning()) {
        enemyKaelAttackAnimationTimer.stop();
        currentEnemyKaelAttackFrame = 0;
        enemyKaelAttackFrameCounter = 0;
        System.out.println("⏹️ Enemy Kael attack animation stopped");
    }
}

private void startKaelDamagedAnimation() {
    stopKaelIdleAnimation();
    if (kaelDamagedAnimationTimer != null && kaelDamagedAnimationTimer.isRunning()) {
        kaelDamagedAnimationTimer.stop();
    }
    if (kaelDamagedFrames[0] == null || kaelLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Kael damaged - frames:" + (kaelDamagedFrames[0]!=null));
        return;
    }
    currentKaelDamagedFrame = 0;
    kaelDamagedFrameCounter = 0;
    final int tickMs = 16;
    final int[] DAMAGED_FRAME_DURATIONS = {6, 6, 12}; // ticks (~0.3s total)
    kaelDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (kaelLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Kael)) {
                stopKaelDamagedAnimation();
                return;
            }
            kaelDamagedFrameCounter++;
            int frameTicks = DAMAGED_FRAME_DURATIONS[currentKaelDamagedFrame];
            if (kaelDamagedFrameCounter >= frameTicks) {
                kaelDamagedFrameCounter = 0;
                currentKaelDamagedFrame++;
                if (currentKaelDamagedFrame >= kaelDamagedFrames.length) {
                    // Animation finished, return to idle
                    stopKaelDamagedAnimation();
                    kaelDamagedAnimationPlaying = false;
                    if (kaelIdleFrames[0] != null) {
                        startKaelIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = kaelDamagedFrames[currentKaelDamagedFrame];
                if (frame != null) {
                    kaelLargePortraitLabel.setIcon(frame);
                } else {
                    kaelLargePortraitLabel.setIcon(kaelDamagedFrames[0]);
                }
                kaelLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Kael damaged timer error: " + ex.getMessage());
            stopKaelDamagedAnimation();
        }
    });
    kaelDamagedAnimationTimer.start();
    kaelLargePortraitLabel.setIcon(kaelDamagedFrames[0]);
    kaelDamagedAnimationPlaying = true;
    System.out.println("💢 Kael damaged animation started");
}

private void stopKaelDamagedAnimation() {
    if (kaelDamagedAnimationTimer != null && kaelDamagedAnimationTimer.isRunning()) {
        kaelDamagedAnimationTimer.stop();
        currentKaelDamagedFrame = 0;
        kaelDamagedFrameCounter = 0;
        System.out.println("⏹️ Kael damaged animation stopped");
    }
}

private void startValeriusDamagedAnimation() {
    stopValeriusIdleAnimation();
    if (valeriusDamagedAnimationTimer != null && valeriusDamagedAnimationTimer.isRunning()) {
        valeriusDamagedAnimationTimer.stop();
    }
    if (valeriusDamagedFrames[0] == null || valeriusLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Valerius damaged - frames:" + (valeriusDamagedFrames[0]!=null));
        return;
    }
    currentValeriusDamagedFrame = 0;
    valeriusDamagedFrameCounter = 0;
    final int tickMs = 16;
    valeriusDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (valeriusLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Valerius)) {
                stopValeriusDamagedAnimation();
                return;
            }
            valeriusDamagedFrameCounter++;
            int frameTicks = VALERIUS_DAMAGED_FRAME_DURATIONS[currentValeriusDamagedFrame];
            if (valeriusDamagedFrameCounter >= frameTicks) {
                valeriusDamagedFrameCounter = 0;
                currentValeriusDamagedFrame++;
                if (currentValeriusDamagedFrame >= valeriusDamagedFrames.length) {
                    // Animation finished, return to idle
                    stopValeriusDamagedAnimation();
                    valeriusDamagedAnimationPlaying = false;
                    if (valeriusIdleFrames[0] != null) {
                        startValeriusIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = valeriusDamagedFrames[currentValeriusDamagedFrame];
                if (frame != null) {
                    valeriusLargePortraitLabel.setIcon(frame);
                } else {
                    valeriusLargePortraitLabel.setIcon(valeriusDamagedFrames[0]);
                }
                valeriusLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Valerius damaged timer error: " + ex.getMessage());
            stopValeriusDamagedAnimation();
        }
    });
    valeriusDamagedAnimationTimer.start();
    valeriusLargePortraitLabel.setIcon(valeriusDamagedFrames[0]);
    System.out.println("💢 Valerius damaged animation started");
}

private void stopValeriusDamagedAnimation() {
    if (valeriusDamagedAnimationTimer != null && valeriusDamagedAnimationTimer.isRunning()) {
        valeriusDamagedAnimationTimer.stop();
        currentValeriusDamagedFrame = 0;
        valeriusDamagedFrameCounter = 0;
        System.out.println("⏹️ Valerius damaged animation stopped");
    }
}

private void startEnemyValeriusDamagedAnimation() {
    stopEnemyValeriusIdleAnimation();
    if (enemyValeriusDamagedAnimationTimer != null && enemyValeriusDamagedAnimationTimer.isRunning()) {
        enemyValeriusDamagedAnimationTimer.stop();
    }
    if (enemyValeriusDamagedFrames[0] == null || enemyValeriusLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Valerius damaged - frames:" + (enemyValeriusDamagedFrames[0]!=null));
        return;
    }
    currentEnemyValeriusDamagedFrame = 0;
    enemyValeriusDamagedFrameCounter = 0;
    final int tickMs = 16;
    enemyValeriusDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyValeriusLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Valerius)) {
                stopEnemyValeriusDamagedAnimation();
                return;
            }
            enemyValeriusDamagedFrameCounter++;
            int frameTicks = VALERIUS_DAMAGED_FRAME_DURATIONS[currentEnemyValeriusDamagedFrame];
            if (enemyValeriusDamagedFrameCounter >= frameTicks) {
                enemyValeriusDamagedFrameCounter = 0;
                currentEnemyValeriusDamagedFrame++;
                if (currentEnemyValeriusDamagedFrame >= enemyValeriusDamagedFrames.length) {
                    // Animation finished, return to idle
                    stopEnemyValeriusDamagedAnimation();
                    enemyValeriusDamagedAnimationPlaying = false;
                    if (enemyValeriusIdleFrames[0] != null) {
                        startEnemyValeriusIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = enemyValeriusDamagedFrames[currentEnemyValeriusDamagedFrame];
                if (frame != null) {
                    enemyValeriusLargePortraitLabel.setIcon(frame);
                } else {
                    enemyValeriusLargePortraitLabel.setIcon(enemyValeriusDamagedFrames[0]);
                }
                enemyValeriusLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Valerius damaged timer error: " + ex.getMessage());
            stopEnemyValeriusDamagedAnimation();
        }
    });
    enemyValeriusDamagedAnimationTimer.start();
    enemyValeriusLargePortraitLabel.setIcon(enemyValeriusDamagedFrames[0]);
    System.out.println("💢 Enemy Valerius damaged animation started");
}

private void stopEnemyValeriusDamagedAnimation() {
    if (enemyValeriusDamagedAnimationTimer != null && enemyValeriusDamagedAnimationTimer.isRunning()) {
        enemyValeriusDamagedAnimationTimer.stop();
        currentEnemyValeriusDamagedFrame = 0;
        enemyValeriusDamagedFrameCounter = 0;
        System.out.println("⏹️ Enemy Valerius damaged animation stopped");
    }
}

private void startEnemyKaelDamagedAnimation() {
    stopEnemyKaelIdleAnimation();
    if (enemyKaelDamagedAnimationTimer != null && enemyKaelDamagedAnimationTimer.isRunning()) {
        enemyKaelDamagedAnimationTimer.stop();
    }
    if (enemyKaelDamagedFrames[0] == null || enemyKaelLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Kael damaged - frames:" + (enemyKaelDamagedFrames[0]!=null));
        return;
    }
    currentEnemyKaelDamagedFrame = 0;
    enemyKaelDamagedFrameCounter = 0;
    final int tickMs = 16;
    final int[] DAMAGED_FRAME_DURATIONS = {6, 6, 12};
    enemyKaelDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyKaelLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Kael)) {
                stopEnemyKaelDamagedAnimation();
                return;
            }
            enemyKaelDamagedFrameCounter++;
            int frameTicks = DAMAGED_FRAME_DURATIONS[currentEnemyKaelDamagedFrame];
            if (enemyKaelDamagedFrameCounter >= frameTicks) {
                enemyKaelDamagedFrameCounter = 0;
                currentEnemyKaelDamagedFrame++;
                if (currentEnemyKaelDamagedFrame >= enemyKaelDamagedFrames.length) {
                    // Animation finished, return to idle
                    stopEnemyKaelDamagedAnimation();
                    enemyKaelDamagedAnimationPlaying = false;
                    if (enemyKaelIdleFrames[0] != null) {
                        startEnemyKaelIdleAnimation();
                    }
                    return;
                }
                ImageIcon frame = enemyKaelDamagedFrames[currentEnemyKaelDamagedFrame];
                if (frame != null) {
                    enemyKaelLargePortraitLabel.setIcon(frame);
                } else {
                    enemyKaelLargePortraitLabel.setIcon(enemyKaelDamagedFrames[0]);
                }
                enemyKaelLargePortraitLabel.repaint();
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Kael damaged timer error: " + ex.getMessage());
            stopEnemyKaelDamagedAnimation();
        }
    });
    enemyKaelDamagedAnimationTimer.start();
    enemyKaelLargePortraitLabel.setIcon(enemyKaelDamagedFrames[0]);
    enemyKaelDamagedAnimationPlaying = true;
    System.out.println("💢 Enemy Kael damaged animation started");
}

private void stopEnemyKaelDamagedAnimation() {
    if (enemyKaelDamagedAnimationTimer != null && enemyKaelDamagedAnimationTimer.isRunning()) {
        enemyKaelDamagedAnimationTimer.stop();
        currentEnemyKaelDamagedFrame = 0;
        enemyKaelDamagedFrameCounter = 0;
        System.out.println("⏹️ Enemy Kael damaged animation stopped");
    }
}

private void showKaelAttackAnimation() {
    System.out.println("⚔️ showKaelAttackAnimation called!");

    // Only play once per turn and if not already playing
    if (kaelAttackAnimationPlaying) {
        System.out.println("⏭️ Kael attack animation already playing, skipping...");
        return;
    }

    if (playerCharacter instanceof Kael && kaelLargePortraitLabel != null) {
        if (kaelAttackFrames[0] != null) {
            startKaelAttackAnimation();
        } else {
            System.out.println("⚠️ Kael attack frames not loaded, skipping attack animation");
            kaelAttackAnimationPlaying = false;
        }
    }
}

private void showEnemyKaelAttackAnimation() {
    System.out.println("⚔️ showEnemyKaelAttackAnimation called!");

    // Only play once per turn and if not already playing
    if (enemyKaelAttackAnimationPlaying) {
        System.out.println("⏭️ Enemy Kael attack animation already playing, skipping...");
        return;
    }

    if (currentEnemy instanceof Kael && enemyKaelLargePortraitLabel != null) {
        if (enemyKaelAttackFrames[0] != null) {
            startEnemyKaelAttackAnimation();
        } else {
            System.out.println("⚠️ Enemy Kael attack frames not loaded, skipping attack animation");
            enemyKaelAttackAnimationPlaying = false;
        }
    }
}

private void showSkyeAttackAnimation() {
    System.out.println("⚔️ showSkyeAttackAnimation called!");

    // Only play once per turn and if not already playing
    if (skyeAttackAnimationPlaying) {
        System.out.println("⏭️ Skye attack animation already playing, skipping...");
        return;
    }

    if (playerCharacter instanceof characters.Skye && skyeLargePortraitLabel != null) {
        if (skyeAttackFrames[0] != null) {
            startSkyeAttackAnimation();
        } else {
            System.out.println("⚠️ Skye attack frames not loaded, skipping attack animation");
            skyeAttackAnimationPlaying = false;
        }
    }
}

private void showEnemySkyeAttackAnimation() {
    System.out.println("⚔️ showEnemySkyeAttackAnimation called!");

    // Only play once per turn and if not already playing
    if (enemySkyeAttackAnimationPlaying) {
        System.out.println("⏭️ Enemy Skye attack animation already playing, skipping...");
        return;
    }

    if (currentEnemy instanceof characters.Skye && enemySkyeLargePortraitLabel != null) {
        if (enemySkyeAttackFrames[0] != null) {
            startEnemySkyeAttackAnimation();
        } else {
            System.out.println("⚠️ Enemy Skye attack frames not loaded, skipping attack animation");
            enemySkyeAttackAnimationPlaying = false;
        }
    }
}

private void showMorganaAttackAnimation() {
    System.out.println("⚔️ showMorganaAttackAnimation called!");

    // Only play once per turn and if not already playing
    if (morganaAttackAnimationPlaying) {
        System.out.println("⏭️ Morgana attack animation already playing, skipping...");
        return;
    }

    if (playerCharacter instanceof characters.Morgana && morganaLargePortraitLabel != null) {
        if (morganaAttackFrames[0] != null) {
            startMorganaAttackAnimation();
        } else {
            System.out.println("⚠️ Morgana attack frames not loaded, skipping attack animation");
            morganaAttackAnimationPlaying = false;
        }
    }
}

private void showEnemyMorganaAttackAnimation() {
    System.out.println("⚔️ showEnemyMorganaAttackAnimation called!");

    // Only play once per turn and if not already playing
    if (enemyMorganaAttackAnimationPlaying) {
        System.out.println("⏭️ Enemy Morgana attack animation already playing, skipping...");
        return;
    }

    if (currentEnemy instanceof characters.Morgana && enemyMorganaLargePortraitLabel != null) {
        if (enemyMorganaAttackFrames[0] != null) {
            startEnemyMorganaAttackAnimation();
        } else {
            System.out.println("⚠️ Enemy Morgana attack frames not loaded, skipping attack animation");
            enemyMorganaAttackAnimationPlaying = false;
        }
    }
}

private void showAerisAttackAnimation() {
    System.out.println("⚔️ showAerisAttackAnimation called!");

    // Only play once per turn and if not already playing
    if (aerisAttackAnimationPlaying) {
        System.out.println("⏭️ Aeris attack animation already playing, skipping...");
        return;
    }

    if (playerCharacter instanceof Aeris && aerisLargePortraitLabel != null) {
        if (aerisAttackFrames[0] != null) {
            startAerisAttackAnimation();
        } else {
            System.out.println("⚠️ Aeris attack frames not loaded, skipping attack animation");
            aerisAttackAnimationPlaying = false;
        }
    }
}

private void showFlueAttackAnimation() {
    System.out.println("⚔️ showFlueAttackAnimation called!");

    // Only play once per turn and if not already playing
    if (flueAttackAnimationPlaying) {
        System.out.println("⏭️ Flue attack animation already playing, skipping...");
        return;
    }

    if (playerCharacter instanceof Flue && flueLargePortraitLabel != null) {
        if (flueAttackFrames[0] != null) {
            startFlueAttackAnimation();
        } else {
            System.out.println("⚠️ Flue attack frames not loaded, skipping attack animation");
            flueAttackAnimationPlaying = false;
        }
    }
}

private void showSeleneAttackAnimation() {
    System.out.println("⚔️ showSeleneAttackAnimation called!");

    // Only play once per turn and if not already playing
    if (seleneAttackAnimationPlaying) {
        System.out.println("⏭️ Selene attack animation already playing, skipping...");
        return;
    }

    if (playerCharacter instanceof Selene && seleneLargePortraitLabel != null) {
        if (seleneAttackFrames[0] != null) {
            startSeleneAttackAnimation();
        } else {
            System.out.println("⚠️ Selene attack frames not loaded, skipping attack animation");
            seleneAttackAnimationPlaying = false;
        }
    }
}

private void showEnemyAerisAttackAnimation() {
    System.out.println("⚔️ showEnemyAerisAttackAnimation called!");

    // Only play once per turn and if not already playing
    if (enemyAerisAttackAnimationPlaying) {
        System.out.println("⏭️ Enemy Aeris attack animation already playing, skipping...");
        return;
    }

    System.out.println("⚔️ Checking enemy Aeris attack animation conditions:");
    System.out.println("  - currentEnemy instanceof Aeris: " + (currentEnemy instanceof Aeris));
    System.out.println("  - enemyAerisLargePortraitLabel != null: " + (enemyAerisLargePortraitLabel != null));
    System.out.println("  - enemyAerisAttackFrames[0] != null: " + (enemyAerisAttackFrames[0] != null));

    if (currentEnemy instanceof Aeris && enemyAerisLargePortraitLabel != null) {
        if (enemyAerisAttackFrames[0] != null) {
            System.out.println("✅ Starting enemy Aeris attack animation");
            startEnemyAerisAttackAnimation();
        } else {
            System.out.println("⚠️ Enemy Aeris attack frames not loaded, skipping attack animation");
            enemyAerisAttackAnimationPlaying = false;
        }
    } else {
        System.out.println("⚠️ Enemy Aeris attack animation conditions not met");
    }
}

private void showEnemyFlueAttackAnimation() {
    System.out.println("⚔️ showEnemyFlueAttackAnimation called!");

    // Only play once per turn and if not already playing
    if (enemyFlueAttackAnimationPlaying) {
        System.out.println("⏭️ Enemy Flue attack animation already playing, skipping...");
        return;
    }

    if (currentEnemy instanceof Flue && enemyFlueLargePortraitLabel != null) {
        if (enemyFlueAttackFrames[0] != null) {
            startEnemyFlueAttackAnimation();
        } else {
            System.out.println("⚠️ Enemy Flue attack frames not loaded, skipping attack animation");
            enemyFlueAttackAnimationPlaying = false;
        }
    }
}

private void showEnemySeleneAttackAnimation() {
    System.out.println("⚔️ showEnemySeleneAttackAnimation called!");

    // Only play once per turn and if not already playing
    if (enemySeleneAttackAnimationPlaying) {
        System.out.println("⏭️ Enemy Selene attack animation already playing, skipping...");
        return;
    }

    if (currentEnemy instanceof Selene && enemySeleneLargePortraitLabel != null) {
        if (enemySeleneAttackFrames[0] != null) {
            startEnemySeleneAttackAnimation();
        } else {
            System.out.println("⚠️ Enemy Selene attack frames not loaded, skipping attack animation");
            enemySeleneAttackAnimationPlaying = false;
        }
    }
}

private void showSkyeDamagedAnimation() {
    System.out.println("💥 showSkyeDamagedAnimation called!");

    // Only play once per turn and if not already playing
    if (skyeDamagedAnimationPlaying) {
        System.out.println("⏭️ Skye damaged animation already playing, skipping...");
        return;
    }

    if (playerCharacter instanceof characters.Skye && skyeLargePortraitLabel != null) {
        if (skyeDamagedFrames[0] != null) {
            startSkyeDamagedAnimation();
        } else {
            System.out.println("⚠️ Skye damaged frames not loaded, skipping damaged animation");
            skyeDamagedAnimationPlaying = false;
        }
    }
}

private void showEnemySkyeDamagedAnimation() {
    System.out.println("💥 showEnemySkyeDamagedAnimation called!");

    // Only play once per turn and if not already playing
    if (enemySkyeDamagedAnimationPlaying) {
        System.out.println("⏭️ Enemy Skye damaged animation already playing, skipping...");
        return;
    }

    if (currentEnemy instanceof characters.Skye && enemySkyeLargePortraitLabel != null) {
        if (enemySkyeDamagedFrames[0] != null) {
            startEnemySkyeDamagedAnimation();
        } else {
            System.out.println("⚠️ Enemy Skye damaged frames not loaded, skipping damaged animation");
            enemySkyeDamagedAnimationPlaying = false;
        }
    }
}

private void stopAttackAnimation() {
    if (attackAnimationTimer != null && attackAnimationTimer.isRunning()) {
        attackAnimationTimer.stop();
        currentAttackFrame = 0;
        attackFrameCounter = 0;
        System.out.println("⏹️ Jiji attack animation stopped");
    }
}

private void initEnemyJijiIdleFrames() {
    // Load all 4 idle frames, flip horizontally, and scale them centered with smooth quality
    for (int i = 0; i < 4; i++) {
        String path = "assets/jiji_idle" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ ImageIO returned null for: " + path);
                    enemyJijiIdleFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                // Target dimensions for portrait area (250x200), centered
                int targetW = 250;
                int targetH = 200;
                Image scaled = flipped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                enemyJijiIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy idle frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy frame " + (i+1) + ": " + e.getMessage());
                enemyJijiIdleFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy idle frame missing: " + f.getAbsolutePath());
            enemyJijiIdleFrames[i] = null;
        }
    }
    // Verify all frames loaded
    for (int i = 0; i < 4; i++) {
        System.out.println("   Enemy Frame " + i + " " + (enemyJijiIdleFrames[i] != null ? "OK" : "NULL"));
    }
}

private void startEnemyIdleAnimation() {
    // Ensure damaged animation is not running
    stopEnemyDamagedAnimation();
    if (enemyIdleAnimationTimer != null && enemyIdleAnimationTimer.isRunning()) {
        enemyIdleAnimationTimer.stop();
    }
    if (enemyJijiIdleFrames[0] == null || enemyJijiLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy idle - frames:" + (enemyJijiIdleFrames[0]!=null) + " label:" + enemyJijiLargePortraitLabel);
        return;
    }
    enemyCurrentIdleFrame = 0;
    enemyIdleFrameCounter = 0;
    final int tickMs = 16; // ~60 FPS base tick
    enemyIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyJijiLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Jiji)) {
                stopEnemyIdleAnimation();
                return;
            }
            enemyIdleFrameCounter++;
            int slotTicks = CYCLE_DURATIONS[enemyCurrentIdleFrame];
            if (enemyIdleFrameCounter >= slotTicks) {
                enemyIdleFrameCounter = 0;
                int prevSlot = enemyCurrentIdleFrame;
                enemyCurrentIdleFrame = (enemyCurrentIdleFrame + 1) % CYCLE_DURATIONS.length;
                if (enemyCurrentIdleFrame == 0) {
                    System.out.println("🔄 Enemy idle cycle completed, restarting");
                }
                int frameIdx = SLOT_FRAME_MAP[enemyCurrentIdleFrame];
                ImageIcon baseFrame = enemyJijiIdleFrames[frameIdx];
                if (baseFrame != null) {
                    // Apply subtle per-slot horizontal sway
                    int offsetX = SLOT_OFFSET_X[enemyCurrentIdleFrame];
                    if (offsetX != 0) {
                        BufferedImage shifted = new BufferedImage(250, 200, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = shifted.createGraphics();
                        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g.drawImage(baseFrame.getImage(), offsetX, 0, null);
                        g.dispose();
                        enemyJijiLargePortraitLabel.setIcon(new ImageIcon(shifted));
                    } else {
                        enemyJijiLargePortraitLabel.setIcon(baseFrame);
                    }
                } else {
                    enemyJijiLargePortraitLabel.setIcon(enemyJijiIdleFrames[0]);
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy idle timer error: " + ex.getMessage());
            stopEnemyIdleAnimation();
        }
    });
    enemyIdleAnimationTimer.start();
    enemyJijiLargePortraitLabel.setIcon(enemyJijiIdleFrames[SLOT_FRAME_MAP[0]]);
    System.out.println("▶️ Enemy Jiji idle animation started (12-slot pattern)");
}

private void stopEnemyIdleAnimation() {
    if (enemyIdleAnimationTimer != null && enemyIdleAnimationTimer.isRunning()) {
        enemyIdleAnimationTimer.stop();
        enemyCurrentIdleFrame = 0;
        enemyIdleFrameCounter = 0;
        System.out.println("⏹️ Enemy Jiji idle animation stopped");
    }
}

private void initEnemyJijiDamagedFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/jiji_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ Enemy damaged frame " + (i+1) + " failed to load");
                    enemyJijiDamagedFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                System.out.println("   Enemy damaged frame " + (i+1) + " raw: " + base.getWidth() + "x" + base.getHeight());
                Image scaled = flipped.getScaledInstance(250, 200, Image.SCALE_SMOOTH);
                enemyJijiDamagedFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy damaged frame " + (i + 1) + " → 250x200");
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy damaged frame " + (i+1) + ": " + e.getMessage());
                enemyJijiDamagedFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy damaged frame missing: " + f.getAbsolutePath());
            enemyJijiDamagedFrames[i] = null;
        }
    }
}

private void startEnemyDamagedAnimation() {
    stopEnemyIdleAnimation();
    if (enemyDamagedAnimationTimer != null && enemyDamagedAnimationTimer.isRunning()) {
        enemyDamagedAnimationTimer.stop();
    }
    if (enemyJijiDamagedFrames[0] == null || enemyJijiLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy damaged - frames:" + (enemyJijiDamagedFrames[0]!=null));
        return;
    }
    enemyCurrentDamagedFrame = 0;
    enemyDamagedFrameCounter = 0;
    final int tickMs = 16;
    enemyDamagedAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyJijiLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Jiji)) {
                stopEnemyDamagedAnimation();
                return;
            }
            enemyDamagedFrameCounter++;
            int frameTicks = DAMAGED_FRAME_DURATIONS[enemyCurrentDamagedFrame];
            if (enemyDamagedFrameCounter >= frameTicks) {
                enemyDamagedFrameCounter = 0;
                enemyCurrentDamagedFrame++;
                if (enemyCurrentDamagedFrame >= enemyJijiDamagedFrames.length) {
                    // Animation finished, return to idle
                    stopEnemyDamagedAnimation();
                    enemyJijiDamagedAnimationPlaying = false;
                    startEnemyIdleAnimation();
                    return;
                }
                ImageIcon frame = enemyJijiDamagedFrames[enemyCurrentDamagedFrame];
                if (frame != null) {
                    enemyJijiLargePortraitLabel.setIcon(frame);
                } else {
                    enemyJijiLargePortraitLabel.setIcon(enemyJijiDamagedFrames[0]);
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy damaged timer error: " + ex.getMessage());
            stopEnemyDamagedAnimation();
        }
    });
    enemyDamagedAnimationTimer.start();
    // Set initial frame directly (already 250x200 from init)
    enemyJijiLargePortraitLabel.setIcon(enemyJijiDamagedFrames[0]);
    System.out.println("💢 Enemy Jiji damaged animation started (250px width)");
}

private void stopEnemyDamagedAnimation() {
    if (enemyDamagedAnimationTimer != null && enemyDamagedAnimationTimer.isRunning()) {
        enemyDamagedAnimationTimer.stop();
        enemyCurrentDamagedFrame = 0;
        enemyDamagedFrameCounter = 0;
        System.out.println("⏹️ Enemy Jiji damaged animation stopped");
    }
}

private void initEnemyJijiAttackFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/jiji_attack" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ Enemy attack frame " + (i+1) + " failed to load");
                    enemyJijiAttackFrames[i] = null;
                    continue;
                }
                // Flip horizontally
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                Image scaled = flipped.getScaledInstance(250, 200, Image.SCALE_SMOOTH);
                enemyJijiAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded and flipped enemy attack frame " + (i + 1));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading enemy attack frame " + (i+1) + ": " + e.getMessage());
                enemyJijiAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy attack frame missing: " + f.getAbsolutePath());
            enemyJijiAttackFrames[i] = null;
        }
    }
}

private void startEnemyAttackAnimation() {
    // Stop all other enemy animations
    stopEnemyIdleAnimation();
    stopEnemyDamagedAnimation();
    if (enemyAttackAnimationTimer != null && enemyAttackAnimationTimer.isRunning()) {
        enemyAttackAnimationTimer.stop();
    }
    if (enemyJijiAttackFrames[0] == null || enemyJijiLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy attack - frames:" + (enemyJijiAttackFrames[0]!=null));
        return;
    }
    enemyCurrentAttackFrame = 0;
    enemyAttackFrameCounter = 0;
    final int tickMs = 16;
    enemyAttackAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyJijiLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Jiji)) {
                stopEnemyAttackAnimation();
                return;
            }
            enemyAttackFrameCounter++;
            int frameTicks = ATTACK_FRAME_DURATIONS[enemyCurrentAttackFrame];
            if (enemyAttackFrameCounter >= frameTicks) {
                enemyAttackFrameCounter = 0;
                enemyCurrentAttackFrame = (enemyCurrentAttackFrame + 1) % enemyJijiAttackFrames.length;
                ImageIcon frame = enemyJijiAttackFrames[enemyCurrentAttackFrame];
                if (frame != null) {
                    enemyJijiLargePortraitLabel.setIcon(frame);
                } else {
                    enemyJijiLargePortraitLabel.setIcon(enemyJijiAttackFrames[0]);
                }
                // On last frame, schedule return to idle/damaged
                if (enemyCurrentAttackFrame == enemyJijiAttackFrames.length - 1) {
                    // Stop attack timer before refresh
                    stopEnemyAttackAnimation();
                    enemyJijiAttackAnimationPlaying = false;
                    // Small delay before returning to normal portrait
                    javax.swing.Timer returnTimer = new javax.swing.Timer(300, ev -> {
                        refreshEnemyJijiPortrait();
                    });
                    returnTimer.setRepeats(false);
                    returnTimer.start();
                }
            }

        } catch (Exception ex) {
            System.out.println("⚠️ Enemy attack timer error: " + ex.getMessage());
            stopEnemyAttackAnimation();
        }
    });
    enemyAttackAnimationTimer.start();
    enemyJijiLargePortraitLabel.setIcon(enemyJijiAttackFrames[0]);
    System.out.println("⚔️ Enemy Jiji attack animation started");
}

private void stopEnemyAttackAnimation() {
    if (enemyAttackAnimationTimer != null && enemyAttackAnimationTimer.isRunning()) {
        enemyAttackAnimationTimer.stop();
        enemyCurrentAttackFrame = 0;
        enemyAttackFrameCounter = 0;
        System.out.println("⏹️ Enemy Jiji attack animation stopped");
    }
}

private void showEnemyJijiAttackAnimation() {
    System.out.println("⚔️ showEnemyJijiAttackAnimation called!");

    // Only play once per turn and if not already playing
    if (enemyJijiAttackAnimationPlaying) {
        System.out.println("⏭️ Enemy attack animation already playing, skipping...");
        return;
    }

    if (currentEnemy instanceof Jiji && enemyJijiLargePortraitLabel != null) {
        if (enemyJijiAttackFrames[0] != null) {
            enemyJijiAttackAnimationPlaying = true;
            startEnemyAttackAnimation();
        } else {
            System.out.println("⚠️ Enemy attack frames not loaded, skipping attack animation");
            enemyJijiAttackAnimationPlaying = false;
        }
    }
}

private void refreshEnemyJijiPortrait() {
    if (currentEnemy instanceof Jiji && enemyJijiLargePortraitLabel != null) {
        // Check if damaged
        Jiji enemyJiji = (Jiji) currentEnemy;
        if (enemyJiji.isDamaged()) {
            if (enemyJijiDamagedFrames[0] != null) {
                startEnemyDamagedAnimation();
            } else {
                System.out.println("⚠️ Damaged frames not available for enemy, showing static");
            }
        } else {
            if (enemyJijiIdleFrames[0] != null) {
                startEnemyIdleAnimation();
            } else {
                System.out.println("⚠️ Idle frames failed to load for enemy, keeping static portrait");
            }
        }
    }
}



private void showJijiAttackAnimation() {
    System.out.println("⚔️ showJijiAttackAnimation called!");
    
    // Only play once per turn and if not already playing
    if (jijiAttackAnimationPlaying || jijiAttackPlayedThisTurn) {
        System.out.println("⏭️ Attack animation already played this turn or playing, skipping...");
        return;
    }
    
    if (playerCharacter instanceof Jiji && jijiLargePortraitLabel != null) {
        if (jijiAttackFrames[0] != null) {
            jijiAttackAnimationPlaying = true;
            startAttackAnimation();
        } else {
            System.out.println("⚠️ Attack frames not loaded, skipping attack animation");
            jijiAttackAnimationPlaying = false;
            jijiAttackPlayedThisTurn = true;
        }
    }
}

private void showValeriusAttackAnimation() {
    System.out.println("⚔️ showValeriusAttackAnimation called! Frames loaded: " + (valeriusAttackFrames[0] != null));

    // Only play once per turn and if not already playing
    if (valeriusAttackAnimationPlaying) {
        System.out.println("⏭️ Valerius attack animation already playing, skipping...");
        return;
    }

    if (playerCharacter instanceof Valerius && valeriusLargePortraitLabel != null) {
        if (valeriusAttackFrames[0] != null) {
            valeriusAttackAnimationPlaying = true;
            startValeriusAttackAnimation();
        } else {
            System.out.println("⚠️ Valerius attack frames not loaded, skipping attack animation");
            valeriusAttackAnimationPlaying = false;
        }
    }
}

private void animateDamagedShake() {
    if (jijiLargePortraitLabel == null) return;
    
    // Store original position
    java.awt.Point originalPos = jijiLargePortraitLabel.getLocation();
    Timer shakeTimer = new Timer(50, new ActionListener() {
        int shakes = 0;
        int maxShakes = 10;
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (shakes >= maxShakes) {
                // Reset position
                jijiLargePortraitLabel.setLocation(originalPos);
                ((Timer)e.getSource()).stop();
                return;
            }
            
            // Random offset for shaking
            int offsetX = (int)(Math.random() * 8) - 4;
            int offsetY = (int)(Math.random() * 8) - 4;
            jijiLargePortraitLabel.setLocation(originalPos.x + offsetX, originalPos.y + offsetY);
            shakes++;
        }
    });
    shakeTimer.start();
}

// Helper method to get a random cell that hasn't been fired upon yet
private int[] getRandomUnfiredCell() {
    // First, collect all cells that haven't been fired upon yet
    java.util.ArrayList<int[]> availableCells = new java.util.ArrayList<>();
    
    for (int i = 0; i < 10; i++) {
        for (int j = 0; j < 10; j++) {
            if (!playerBoard.isCellFiredUpon(i, j)) {
                availableCells.add(new int[]{i, j});
            }
        }
    }
    
    // If there are no available cells (all have been fired upon), return a random cell as fallback
    if (availableCells.isEmpty()) {
        System.out.println("⚠️ No available cells! All have been fired upon!");
        return new int[]{random.nextInt(10), random.nextInt(10)};
    }
    
    // Pick a random cell from the available ones
    int randomIndex = random.nextInt(availableCells.size());
    int[] selected = availableCells.get(randomIndex);
    System.out.println("🎯 Enemy AI selected cell (" + selected[0] + "," + selected[1] + 
                       ") - " + availableCells.size() + " cells remaining");
    return selected;
}

// Helper method for enemy skills to target unfired cells
private int[] getRandomUnfiredCellForEnemySkill() {
    java.util.ArrayList<int[]> availableCells = new java.util.ArrayList<>();
    
    for (int i = 0; i < 10; i++) {
        for (int j = 0; j < 10; j++) {
            if (!playerBoard.isCellFiredUpon(i, j)) {
                availableCells.add(new int[]{i, j});
            }
        }
    }
    
    if (availableCells.isEmpty()) {
        return new int[]{random.nextInt(10), random.nextInt(10)};
    }
    
    return availableCells.get(random.nextInt(availableCells.size()));
}

private void executeSkill(int targetX, int targetY) {
    System.out.println("Executing skill: " + currentSkillName + " at (" + targetX + "," + targetY + ")");
    boolean success = false;
    boolean shouldEndTurn = true;
    
    try {
        if (playerCharacter instanceof Jiji) {
            Jiji jiji = (Jiji) playerCharacter;
            switch(currentSkillNumber) {
                case 1:
                    System.out.println("Using Data Leech");
                    success = jiji.useDataLeech(enemyBoard);
                    shouldEndTurn = true;
                    break;
                case 2:
                    System.out.println("Using Overclock");
                    success = jiji.useOverclock();
                    shouldEndTurn = false; 
                    break;
                case 3:
                    System.out.println("Using System Overload at (" + targetX + "," + targetY + ")");
                    success = jiji.useSystemOverload(enemyBoard, targetX, targetY);
                    shouldEndTurn = true;
                    break;
                default:
                    System.out.println("⚠️ Unknown Jiji skill number: " + currentSkillNumber);
                    success = false;
                    shouldEndTurn = false;
            }
        } else if (playerCharacter instanceof Kael) {
            Kael kael = (Kael) playerCharacter;
            switch(currentSkillNumber) {
                case 1:
                    
                    
                    System.out.println("⚠️ Shadow Step should not be in executeSkill!");
                    success = false;
                    shouldEndTurn = false;
                    break;
                case 2:
                    System.out.println("Using Shadow Blade at (" + targetX + "," + targetY + ")");
                    int destroyed = kael.useShadowBlade(enemyBoard, targetX, targetY, currentSkillDirectionHorizontal);
                    success = destroyed > 0;
                    shouldEndTurn = true;
                    break;
                case 3:
                    System.out.println("Using Shadow Domain at (" + targetX + "," + targetY + ")");
                    destroyed = kael.useShadowDomain(enemyBoard, targetX, targetY);
                    success = destroyed > 0;
                    shouldEndTurn = true;
                    break;
                default:
                    System.out.println("⚠️ Unknown Kael skill number: " + currentSkillNumber);
                    success = false;
                    shouldEndTurn = false;
            }
        } else if (playerCharacter instanceof Valerius) {
            Valerius valerius = (Valerius) playerCharacter;
            switch(currentSkillNumber) {
                case 1:
                    System.out.println("Using Radar Overload");
                    success = valerius.useRadarOverload();
                    shouldEndTurn = true;
                    break;
                case 2:
                    System.out.println("Using Precision Strike");
                    if (valerius.usePrecisionStrike()) {
                        int destroyed = valerius.applyPrecisionStrike(enemyBoard, targetX, targetY, currentSkillDirectionHorizontal);
                        success = destroyed > 0;
                        shouldEndTurn = true;
                    } else {
                        success = false;
                        shouldEndTurn = false;
                    }
                    break;
                case 3:
                    System.out.println("Using Fortress Mode");
                    success = valerius.useFortressMode();
                    shouldEndTurn = true;
                    break;
                default:
                    success = false;
                    shouldEndTurn = false;
            }
        } else if (playerCharacter instanceof Skye) {
            Skye skye = (Skye) playerCharacter;
            switch(currentSkillNumber) {
                case 1:
                    System.out.println("Using Catnip Explosion at (" + targetX + "," + targetY + ")");
                    int destroyed = skye.useCatnipExplosion(enemyBoard, targetX, targetY);
                    success = destroyed > 0;
                    shouldEndTurn = true;
                    break;
                case 2:
                    System.out.println("Using Laser Pointer");
                    success = skye.useLaserPointer();
                    shouldEndTurn = false; 
                    if (success) {
                        System.out.println("🔴 Laser Pointer used - Enemy will skip their next turn!");
                    }
                    break;
                case 3:
                    System.out.println("Using Nine Lives at (" + targetX + "," + targetY + ")");
                    success = skye.useNineLives(playerBoard, targetX, targetY);
                    shouldEndTurn = true;
                    break;
                default:
                    success = false;
                    shouldEndTurn = false;
            }
        } else if (playerCharacter instanceof Morgana) {
            Morgana morgana = (Morgana) playerCharacter;
            switch(currentSkillNumber) {
                case 1:
                    System.out.println("Using Enchanting Melody");
                    success = morgana.useEnchantingMelody();
                    shouldEndTurn = true;
                    break;
                case 2:
                    System.out.println("Using Whirlpool Trap at (" + targetX + "," + targetY + ")");
                    success = morgana.useWhirlpoolTrap(enemyBoard, targetX, targetY);
                    shouldEndTurn = true;
                    break;
                case 3:
                    System.out.println("Using Tidal Wave");
                    int flooded = morgana.useTidalWave(enemyBoard);
                    success = flooded > 0;
                    shouldEndTurn = true;
                    break;
                default:
                    success = false;
                    shouldEndTurn = false;
            }
        } else if (playerCharacter instanceof Aeris) {
            Aeris aeris = (Aeris) playerCharacter;
            switch(currentSkillNumber) {
                case 1:
                    System.out.println("Using Adaptive Instinct");
                    success = aeris.useAdaptiveInstinct(playerBoard, targetX, targetY);
                    shouldEndTurn = true;
                    break;
                case 2:
                    System.out.println("Using Multitask Overdrive");
                    success = aeris.useMultitaskOverdrive();
                    shouldEndTurn = false; 
                    break;
                case 3:
                    System.out.println("Using Relentless Ascent at column " + targetY);
                    int destroyed = aeris.useRelentlessAscent(enemyBoard, targetY);
                    success = destroyed > 0;
                    shouldEndTurn = true;
                    break;
                default:
                    success = false;
                    shouldEndTurn = false;
            }
        } else if (playerCharacter instanceof Selene) {
            Selene selene = (Selene) playerCharacter;
            switch(currentSkillNumber) {
                case 1:
                    System.out.println("Using Lunar Reveal at (" + targetX + "," + targetY + ")");
                    success = selene.useLunarReveal(enemyBoard, targetX, targetY);
                    shouldEndTurn = true;
                    break;
                case 2:
                    System.out.println("Using Crescent Strike at (" + targetX + "," + targetY + ")");
                    int destroyed = selene.useCrescentStrike(enemyBoard, targetX, targetY);
                    success = destroyed > 0;
                    shouldEndTurn = true;
                    break;
                case 3:
                    System.out.println("Using Starfall Link");
                    success = selene.useStarfallLink(enemyBoard);
                    shouldEndTurn = true;
                    break;
                default:
                    success = false;
                    shouldEndTurn = false;
            }
        } else if (playerCharacter instanceof Flue) {
            Flue flue = (Flue) playerCharacter;
            switch(currentSkillNumber) {
                case 1:
                    System.out.println("Using Corruption.EXE at (" + targetX + "," + targetY + ")");
                    success = flue.useCorruption(enemyBoard, targetX, targetY);
                    shouldEndTurn = true;
                    break;
                case 2:
                    System.out.println("Using Fortification.GRID at (" + targetX + "," + targetY + ")");
                    success = flue.useFortification(playerBoard, targetX, targetY);
                    shouldEndTurn = true;
                    break;
                case 3:
                    System.out.println("Using Kernel.Decimation.REQ at (" + targetX + "," + targetY + ")");
                    success = flue.useKernelDecimation(enemyBoard, targetX, targetY);
                    shouldEndTurn = true;
                    break;
                default:
                    success = false;
                    shouldEndTurn = false;
            }
        } else {
            System.out.println("⚠️ Unknown character type for skill execution");
            success = false;
            shouldEndTurn = false;
        }
    } catch (Exception e) {
        e.printStackTrace();
        updateStatusLabel("❌ Skill error: " + e.getMessage(), Color.RED);
        success = false;
        shouldEndTurn = false;
        
        waitingForSkillTarget = false;
        currentSkillNumber = 0;
        currentSkillName = "";
        currentSkillTargetsOwnBoard = false;
        currentSkillRequiresDirection = false;
    }
    
    if (success) {
        String skillNameCopy = currentSkillName;
        waitingForSkillTarget = false;
        currentSkillNumber = 0;
        currentSkillName = "";
        currentSkillTargetsOwnBoard = false;
        currentSkillRequiresDirection = false;
        
        audio.MusicManager.getInstance().playSound("skill");


        updateStatusLabel("✨ " + skillNameCopy + " used successfully!", Color.GREEN);
        if (frame.getContentPane() instanceof WaveBackgroundPanel) {
            ((WaveBackgroundPanel) frame.getContentPane()).triggerFlash(new Color(255, 255, 255));
            ((WaveBackgroundPanel) frame.getContentPane()).triggerShake(10);
        }
        refreshBoardsOnly();
        updateShipCounters();  
        if (currentSkillPanel != null) {
            currentSkillPanel.updateUI();
        }
        
        if (skillNameCopy.equals("Laser Pointer")) {
            updateStatusLabel("🔴 Enemy will skip their next turn! You get another turn!", Color.GREEN);
        }
        
        if (shouldEndTurn) {
            playerTurn = false;
            
            if (turnTimer != null) {
                turnTimer.stopTimer();
                turnTimer.setVisible(false);
            }
            if (enemyTurnTimer != null) {
                enemyTurnTimer.setTimerLabel("Enemy Turn");
                enemyTurnTimer.setVisible(true);
                enemyTurnTimer.startTimer();
            }
            Timer timer = new Timer(1200, e -> enemyTurn());
            timer.setRepeats(false);
            timer.start();
        } else {
            refreshUI();
            updateStatusLabel("YOUR TURN - You get another action!", Color.GREEN);
            
            if (enemyTurnTimer != null) {
                enemyTurnTimer.stopTimer();
                enemyTurnTimer.setVisible(false);
            }
            if (turnTimer != null && timerEnabled) {
                turnTimer.setTimerLabel("Your Turn");
                turnTimer.setVisible(true);
                turnTimer.stopTimer();
                turnTimer.startTimer();
            }
        }
    } else {
        String failedSkillName = currentSkillName;
        waitingForSkillTarget = false;
        currentSkillNumber = 0;
        currentSkillName = "";
        currentSkillTargetsOwnBoard = false;
        currentSkillRequiresDirection = false;
        
        updateStatusLabel("❌ Failed to use " + failedSkillName + "! Check mana/cooldown.", Color.RED);
        
        if (enemyTurnTimer != null) {
            enemyTurnTimer.stopTimer();
            enemyTurnTimer.setVisible(false);
        }
        if (turnTimer != null && timerEnabled && playerTurn) {
            turnTimer.setTimerLabel("Your Turn");
            turnTimer.setVisible(true);
            turnTimer.stopTimer();
            turnTimer.startTimer();
        }
    }

    // Check if skill killed all enemy ships
        if (success && enemyBoard.allShipsSunk()) {
            updateStatusLabel("🎉 VICTORY! All enemy ships destroyed!", Color.ORANGE);
            waveComplete();
            return;
        }
}

private String getShipCountText(Board board) {
    int total = 0;
    int sunk = 0;
    for (Ship ship : board.getShips()) {
        total++;
        if (ship.isSunk()) {
            sunk++;
        }
    }
    int remaining = total - sunk;
    return "🚢 Ships: " + remaining + "/" + total;
}

private void updateShipCounters() {
    if (playerShipLabel != null) {
        playerShipLabel.setText(getShipCountText(playerBoard));
        playerShipLabel.repaint();
    }
    if (enemyShipLabel != null) {
        enemyShipLabel.setText(getShipCountText(enemyBoard));
        enemyShipLabel.repaint();
    }
}


private void setupClickHandlers() {
    
    playerBoardPanel.setPlayerClickHandler((row, col) -> {
        
        if (waitingForSkillTarget && currentSkillTargetsOwnBoard) {
            if (turnTimer != null) turnTimer.stopTimer();
            executeSkill(row, col);
            return;
        }
        
        if (waitingForKaelStepSource) {
            System.out.println("🌑 Kael's SHADOW STEP source: (" + row + "," + col + ")");
            stepSourceCoordinates[0] = row;
            stepSourceCoordinates[1] = col;
            waitingForKaelStepSource = false;
            waitingForKaelStepDestination = true;
            updateStatusLabel("🌑 Now click on YOUR board for the destination!", Color.YELLOW);
            return;
        }
        
        if (waitingForKaelStepDestination) {
            System.out.println("🌑 Kael's SHADOW STEP destination: (" + row + "," + col + ")");
            Kael kael = (Kael) playerCharacter;
            boolean used = kael.useShadowStep(playerBoard, 
                stepSourceCoordinates[0], stepSourceCoordinates[1], row, col);
            if (used) {
                updateStatusLabel("🌑 Shadow Step! Ship teleported successfully!", Color.CYAN);
                refreshBoardsOnly();
                
                playerTurn = false;
                if (turnTimer != null) {
                    turnTimer.stopTimer();
                    turnTimer.setVisible(false);
                }
                if (enemyTurnTimer != null) {
                    enemyTurnTimer.setTimerLabel("Enemy Turn");
                    enemyTurnTimer.setVisible(true);
                    enemyTurnTimer.startTimer();
                }
                Timer timer = new Timer(1200, e -> enemyTurn());
                timer.setRepeats(false);
                timer.start();
            } else {
                updateStatusLabel("❌ Cannot use Shadow Step!", Color.RED);
            }
            waitingForKaelStepDestination = false;
            return;
        }
        
        if (waitingForAerisShield && currentAerisShieldCallback != null) {
            currentAerisShieldCallback.accept(row, col);
            waitingForAerisShield = false;
            currentAerisShieldCallback = null;
        }
    });
    
    
    enemyBoardPanel.setEnemyClickHandler((row, col) -> {
        System.out.println("🔍 ENEMY BOARD CLICKED at (" + row + "," + col + ") - waitingForSkillTarget=" + waitingForSkillTarget + " targetsOwnBoard=" + currentSkillTargetsOwnBoard);
        
        if (waitingForSkillTarget && !currentSkillTargetsOwnBoard) {
            System.out.println("🎯 Executing skill target!");
            if (turnTimer != null) turnTimer.stopTimer();
            executeSkill(row, col);
            return;
        }
        
        if (waitingForKaelBlade && currentKaelBladeCallback != null) {
            currentKaelBladeCallback.accept(row, col);
            waitingForKaelBlade = false;
            currentKaelBladeCallback = null;
            return;
        }
        
        if (waitingForKaelDomain && currentKaelDomainCallback != null) {
            currentKaelDomainCallback.accept(row, col);
            waitingForKaelDomain = false;
            currentKaelDomainCallback = null;
            return;
        }
        
        if (waitingForSeleneVision && currentSeleneVisionCallback != null) {
            currentSeleneVisionCallback.accept(row, col);
            waitingForSeleneVision = false;
            currentSeleneVisionCallback = null;
            return;
        }
        
        if (waitingForSeleneCrescent && currentSeleneCrescentCallback != null) {
            currentSeleneCrescentCallback.accept(row, col);
            waitingForSeleneCrescent = false;
            currentSeleneCrescentCallback = null;
            return;
        }
        
        if (waitingForTarget && targetCallback != null) {
            targetCallback.onTargetSelected(row, col);
            waitingForTarget = false;
            targetCallback = null;
            return;
        }
        
        if (waitingForWhirlpoolTarget && currentWhirlpoolCallback != null) {
            currentWhirlpoolCallback.accept(row, col);
            waitingForWhirlpoolTarget = false;
            currentWhirlpoolCallback = null;
            return;
        }
        
        if (playerTurn) {
            if (turnTimer != null) turnTimer.stopTimer();
            handlePlayerAttack(row, col);
        }
    });
}


    private JPanel createPlayerCharacterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 50, 0));
        panel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
        
        JLabel nameLabel = new JLabel(playerCharacter.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        panel.add(nameLabel, BorderLayout.NORTH);
        
        JLabel portraitLabel = new JLabel(getCharacterEmoji(playerCharacter), SwingConstants.CENTER);
        portraitLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        portraitLabel.setForeground(Color.WHITE);
        panel.add(portraitLabel, BorderLayout.CENTER);
        
      if (playerCharacter instanceof Aeris) {
        ((Aeris) playerCharacter).setPlayerBoard(playerBoard);
    }
    if (playerCharacter instanceof Kael) {
        ((Kael) playerCharacter).setPlayerBoard(playerBoard);
    }
    if (playerCharacter instanceof Valerius) {
        ((Valerius) playerCharacter).setPlayerBoard(playerBoard);
    }
    if (playerCharacter instanceof Skye) {
        ((Skye) playerCharacter).setPlayerBoard(playerBoard);
    }
    
        
        
        
        return panel;
    }


    
    private void addSeleneSkills(JPanel panel, boolean isPlayer) {
        Selene selene = (Selene) playerCharacter;
        
        if (selene.isNightTime()) {
            selene.resetCooldowns();
            System.out.println("🌙 Night time detected in addSeleneSkills - Resetting cooldowns!");
        }
        
        
        startSeleneUpdateTimer(panel, selene);
        
        JButton revealBtn = new JButton("🔮 Lunar Reveal (" + (selene.isNightTime() ? 30 : 60) + ")");
        revealBtn.setBackground(new Color(200, 150, 255));
        revealBtn.setForeground(Color.BLACK);
        revealBtn.setToolTipText("Reveal all cells in a 3x3 area");
        revealBtn.setFont(new Font("Arial", Font.BOLD, 11));
        revealBtn.setFocusPainted(false);
        
        revealBtn.addActionListener(e -> {
            if (isPlayer && playerTurn) {
                if (turnTimer != null) turnTimer.stopTimer();
                updateStatusLabel("🔮 Click on enemy board to reveal area!", Color.YELLOW);
                waitingForSeleneVision = true;
                currentSeleneVisionCallback = (x, y) -> {
                    boolean used = selene.useLunarReveal(enemyBoard, x, y);
                    if (used) {
                        updateStatusLabel("🔮 Lunar Reveal revealed area around (" + x + "," + y + ")!", Color.CYAN);
                        refreshBoardsOnly();
                    } else {
                        updateStatusLabel("❌ Cannot use Lunar Reveal!", Color.RED);
                    }
                    waitingForSeleneVision = false;
                };
            }
        });
        panel.add(revealBtn);
        
        JButton strikeBtn = new JButton("🌙 Crescent Strike (" + (selene.isNightTime() ? 60 : 120) + ")");
        strikeBtn.setBackground(new Color(150, 100, 200));
        strikeBtn.setForeground(Color.WHITE);
        strikeBtn.setToolTipText("Destroy a cross pattern");
        strikeBtn.setFont(new Font("Arial", Font.BOLD, 11));
        strikeBtn.setFocusPainted(false);
        
        strikeBtn.addActionListener(e -> {
            if (isPlayer && playerTurn) {
                if (turnTimer != null) turnTimer.stopTimer();
                updateStatusLabel("🌙 Click on enemy board to strike a cross pattern!", Color.YELLOW);
                waitingForSeleneCrescent = true;
                currentSeleneCrescentCallback = (x, y) -> {
                    if (x < 0 || x > 9 || y < 0 || y > 9) {
                        updateStatusLabel("❌ Invalid coordinates!", Color.RED);
                        waitingForSeleneCrescent = false;
                        return;
                    }
                    int destroyed = selene.useCrescentStrike(enemyBoard, x, y);
                    if (destroyed > 0) {
                        updateStatusLabel("🌙 Crescent Strike destroyed " + destroyed + " cells!", Color.ORANGE);
                        refreshBoardsOnly();
                    } else {
                        updateStatusLabel("❌ Cannot use Crescent Strike!", Color.RED);
                    }
                    waitingForSeleneCrescent = false;
                };
            }
        });
        panel.add(strikeBtn);
        
        JButton starfallBtn = new JButton("⭐ Starfall Link (" + (selene.isNightTime() ? 150 : 300) + ")" + (selene.isNightTime() ? " 🌙" : ""));
        starfallBtn.setBackground(new Color(255, 215, 0));
        starfallBtn.setForeground(Color.BLACK);
        starfallBtn.setToolTipText("ULTIMATE: Destroy random cells and link them!");
        starfallBtn.setFont(new Font("Arial", Font.BOLD, 12));
        starfallBtn.setFocusPainted(false);
        
        starfallBtn.addActionListener(e -> {
            if (isPlayer && playerTurn) {
                if (turnTimer != null) turnTimer.stopTimer();
                boolean used = selene.useStarfallLink(enemyBoard);
                if (used) {
                    updateStatusLabel("⭐ STARFALL LINK ACTIVATED!", Color.YELLOW);
                    refreshBoardsOnly();
                } else {
                    updateStatusLabel("❌ Cannot use Starfall Link!", Color.RED);
                }
            }
        });
        panel.add(starfallBtn);

        if (selene.isNightTime()) {
            JLabel nightLabel = new JLabel("🌙✨ NIGHT TIME ACTIVE! Skills are ENHANCED! ✨🌙", SwingConstants.CENTER);
            nightLabel.setForeground(Color.YELLOW);
            nightLabel.setFont(new Font("Arial", Font.BOLD, 12));
            nightLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            panel.add(nightLabel);
        }
        
        if (selene.isLinkActive()) {
            JLabel linkLabel = new JLabel("🔗 STAR LINK ACTIVE", SwingConstants.CENTER);
            linkLabel.setForeground(Color.CYAN);
            linkLabel.setFont(new Font("Arial", Font.BOLD, 10));
            panel.add(linkLabel);
        }
        
        JLabel manaLabel = new JLabel(selene.getManaBar(), SwingConstants.CENTER);
        manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
        manaLabel.setForeground(Color.CYAN);
        panel.add(manaLabel);
    }
    
    
    private void addFlueSkills(JPanel panel, boolean isPlayer) {}
    private void addAerisSkills(JPanel panel, boolean isPlayer) {}
   
    private void addKaelSkills(JPanel panel, boolean isPlayer) {}
    private void addMorganaSkills(JPanel panel, boolean isPlayer) {}
    private void addValeriusSkills(JPanel panel, boolean isPlayer) {}
    private void addSkyeSkills(JPanel panel, boolean isPlayer) {}
    
    private JPanel createEnemyCharacterPanel(CampaignWave wave) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(50, 0, 0)); 
        panel.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        
        JLabel nameLabel = new JLabel(currentEnemy.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        panel.add(nameLabel, BorderLayout.NORTH);
        
        JLabel portraitLabel = new JLabel(getCharacterEmoji(currentEnemy), SwingConstants.CENTER);
        portraitLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        portraitLabel.setForeground(wave.waveColor);
        panel.add(portraitLabel, BorderLayout.CENTER);
        
       
        
        return panel;
    }
    
    private String getCharacterEmoji(GameCharacter character) {
        if (character instanceof Jiji) return "💻";
        if (character instanceof Kael) return "🌑";
        if (character instanceof Valerius) return "🛡️";
        if (character instanceof Skye) return "🐱";
        if (character instanceof Morgana) return "🧜‍♀️";
        if (character instanceof Aeris) return "💪";
        if (character instanceof Selene) return "🔮"; 
        if (character instanceof Flue) return "💻";
        return "🎮";
    }

   
    private Icon getCharacterPortrait(GameCharacter character) {
       try {
           String name = character.getName().split(" ")[0].toLowerCase();

        // Check for Jiji's damaged state
        if (character instanceof Jiji) {
            Jiji jiji = (Jiji) character;
            System.out.println("🔍 Jiji damaged state: " + jiji.isDamaged());
            if (jiji.isDamaged()) {
                // Use pre-rendered damaged frames if available
                if (character == currentEnemy) {
                    if (enemyJijiDamagedFrames[0] != null) {
                        System.out.println("💢 Enemy Jiji damaged! Returning flipped damaged frame 0");
                        return enemyJijiDamagedFrames[0];
                    }
                } else {
                    if (jijiDamagedFrames[0] != null) {
                        System.out.println("💢 Jiji damaged! Returning damaged frame 0");
                        return jijiDamagedFrames[0];
                    }
                }
                // Fallback to legacy GIF
                String damagedPath = "assets/jiji_whenDamaged.gif";
                File damagedFile = new File(damagedPath);
                if (damagedFile.exists()) {
                    System.out.println("💢 Fallback: Using damaged GIF");
                    return new ImageIcon(damagedPath);
                } else {
                    System.out.println("⚠️ Damaged GIF not found at: " + damagedFile.getAbsolutePath());
                }
            }
        }

         // Normal idle - Jiji uses pre-rendered 4-frame animation (frame 0 as base)
         if (character instanceof Jiji) {
             if (character == currentEnemy) {
                 if (enemyJijiIdleFrames[0] != null) {
                     System.out.println("✅ Returning pre-rendered enemy Jiji idle frame 0 (flipped)");
                     return enemyJijiIdleFrames[0];
                 }
             } else {
                 if (jijiIdleFrames[0] != null) {
                     System.out.println("✅ Returning pre-rendered Jiji idle frame 0");
                     return jijiIdleFrames[0];
                 }
             }
             // Fallback: try raw PNG or legacy GIF
             String[] fallback = {"assets/jiji_idle1.png", "assets/jiji.gif", "assets/jiji_idle.gif"};
             for (String path : fallback) {
                 File file = new File(path);
                 if (file.exists()) {
                     System.out.println("✅ Fallback: Loading " + path);
                     return new ImageIcon(path);
                 }
             }
          }

         // Kael idle
         if (character instanceof Kael) {
             if (character == currentEnemy) {
                 if (enemyKaelIdleFrames[0] != null) {
                     System.out.println("✅ Returning pre-rendered enemy Kael idle frame 0 (flipped)");
                     return enemyKaelIdleFrames[0];
                 }
             } else {
                 if (kaelIdleFrames[0] != null) {
                     System.out.println("✅ Returning pre-rendered Kael idle frame 0");
                     return kaelIdleFrames[0];
                 }
             }
             // Fallback
             String[] fallback = {"assets/kael_idle1.png", "assets/kael.gif", "assets/kael_idle.gif"};
             for (String path : fallback) {
                 File file = new File(path);
                 if (file.exists()) {
                     System.out.println("✅ Fallback: Loading " + path);
                     return new ImageIcon(path);
                 }
             }
          }

          // Valerius idle
          if (character instanceof Valerius) {
              if (valeriusIdleFrames[0] != null) {
                  return valeriusIdleFrames[0];
              }
              // Fallback
              String[] fallback = {"assets/valerius_idle1.png", "assets/valerius.gif", "assets/valerius_idle.gif"};
              for (String path : fallback) {
                  File file = new File(path);
                  if (file.exists()) {
                      System.out.println("✅ Fallback: Loading " + path);
                      return new ImageIcon(path);
                  }
              }
          }

          // Valerius idle
          if (character instanceof Valerius) {
              if (character == currentEnemy) {
                  if (enemyValeriusIdleFrames[0] != null) {
                      System.out.println("✅ Returning pre-rendered enemy Valerius idle frame 0 (flipped)");
                      return enemyValeriusIdleFrames[0];
                  }
              } else {
                  if (valeriusIdleFrames[0] != null) {
                      System.out.println("✅ Returning pre-rendered Valerius idle frame 0");
                      return valeriusIdleFrames[0];
                  }
              }
              // Fallback
              String[] fallback = {"assets/valerius_idle1.png", "assets/valerius.gif", "assets/valerius_idle.gif"};
              for (String path : fallback) {
                  File file = new File(path);
                  if (file.exists()) {
                      System.out.println("✅ Fallback: Loading " + path);
                      return new ImageIcon(path);
                  }
              }
           }

          // Skye idle
          if (character instanceof Skye) {
              if (character == currentEnemy) {
                  if (enemySkyeIdleFrames[0] != null) {
                      System.out.println("✅ Returning pre-rendered enemy Skye idle frame 0 (flipped)");
                      return enemySkyeIdleFrames[0];
                  }
              } else {
                  if (skyeIdleFrames[0] != null) {
                      System.out.println("✅ Returning pre-rendered Skye idle frame 0");
                      return skyeIdleFrames[0];
                  }
              }
              // Fallback
              String[] fallback = {"assets/skye_idle1.png", "assets/skye.gif", "assets/skye_idle.gif"};
              for (String path : fallback) {
                  File file = new File(path);
                  if (file.exists()) {
                      System.out.println("✅ Fallback: Loading " + path);
                      return new ImageIcon(path);
                  }
              }
          }

           // Morgana idle
           if (character instanceof characters.Morgana) {
               if (character == currentEnemy) {
                   if (enemyMorganaIdleFrames[0] != null) {
                       System.out.println("✅ Returning pre-rendered enemy Morgana idle frame 0 (flipped)");
                       return enemyMorganaIdleFrames[0];
                   }
               } else {
                   if (morganaIdleFrames[0] != null) {
                       System.out.println("✅ Returning pre-rendered Morgana idle frame 0");
                       return morganaIdleFrames[0];
                   }
               }
               // Fallback
               String[] fallback = {"assets/morgana_idle1.png", "assets/morgana.gif", "assets/morgana_idle.gif"};
               for (String path : fallback) {
                   File file = new File(path);
                   if (file.exists()) {
                       System.out.println("✅ Fallback: Loading " + path);
                       return new ImageIcon(path);
                   }
               }
           }

           // Aeris idle
           if (character instanceof Aeris) {
               if (character == currentEnemy) {
                   if (enemyAerisIdleFrames[0] != null) {
                       System.out.println("✅ Returning pre-rendered enemy Aeris idle frame 0 (flipped)");
                       return enemyAerisIdleFrames[0];
                   }
               } else {
                   if (aerisIdleFrames[0] != null) {
                       System.out.println("✅ Returning pre-rendered Aeris idle frame 0");
                       return aerisIdleFrames[0];
                   }
               }
               // Fallback to static portrait
               String[] fallback = {"assets/char5.png", "assets/aeris.jpg"};
               for (String path : fallback) {
                   File file = new File(path);
                   if (file.exists()) {
                       System.out.println("✅ Fallback: Loading " + path);
                       return new ImageIcon(path);
                   }
               }
           }

           // Selene idle
           if (character instanceof Selene) {
               if (character == currentEnemy) {
                   if (enemySeleneIdleFrames[0] != null) {
                       System.out.println("✅ Returning pre-rendered enemy Selene idle frame 0 (flipped)");
                       return enemySeleneIdleFrames[0];
                   }
               } else {
                   if (seleneIdleFrames[0] != null) {
                       System.out.println("✅ Returning pre-rendered Selene idle frame 0");
                       return seleneIdleFrames[0];
                   }
               }
               // Fallback to static portrait
               String[] fallback = {"assets/char1.png", "assets/selene.jpg"};
               for (String path : fallback) {
                   File file = new File(path);
                   if (file.exists()) {
                       System.out.println("✅ Fallback: Loading " + path);
                       return new ImageIcon(path);
                   }
               }
           }

           // Flue idle
           if (character instanceof Flue) {
               if (character == currentEnemy) {
                   if (enemyFlueIdleFrames[0] != null) {
                       System.out.println("✅ Returning pre-rendered enemy Flue idle frame 0 (flipped)");
                       return enemyFlueIdleFrames[0];
                   }
               } else {
                   if (flueIdleFrames[0] != null) {
                       System.out.println("✅ Returning pre-rendered Flue idle frame 0");
                       return flueIdleFrames[0];
                   }
               }
               // Fallback to static portrait
               String[] fallback = {"assets/char2.png", "assets/flue.jpg"};
               for (String path : fallback) {
                   File file = new File(path);
                   if (file.exists()) {
                       System.out.println("✅ Fallback: Loading " + path);
                       return new ImageIcon(path);
                   }
               }
           }

             // Default for other characters
         String[] possiblePaths = {"assets/" + name + ".gif", "assets/" + name + "_idle.gif"};
         for (String path : possiblePaths) {
             File file = new File(path);
             if (file.exists()) {
                 System.out.println("✅ Loading portrait from: " + path);
                 return new ImageIcon(path);
             }
         }
         System.out.println("⚠️ No portrait found for: " + name);
     } catch (Exception e) {
         System.out.println("⚠️ Could not load portrait: " + e.getMessage());
     }
     return null;
 }
    
    private JPanel createBoardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setOpaque(false);
        
        playerBoardPanel = new BoardPanel(true, playerBoard, true);
        enemyBoardPanel = new BoardPanel(false, enemyBoard, false);
     
        
    
        playerBoardPanel.setPlayerClickHandler((row, col) -> {
             if (waitingForKaelStepSource) {
        System.out.println("🌑 Kael's SHADOW STEP source selection at: (" + row + "," + col + ")");
        Cell cell = playerBoard.getCell(row, col);
        if (!cell.hasShip()) {
            updateStatusLabel("❌ No ship at this location! Click on a ship to teleport.", Color.RED);
            waitingForKaelStepSource = false;
            return;
        }
        stepSourceCoordinates[0] = row;
        stepSourceCoordinates[1] = col;
        waitingForKaelStepSource = false;
        waitingForKaelStepDestination = true;
        updateStatusLabel("🌑 Now click on destination on YOUR board!", Color.YELLOW);
        return;
    }
            
           if (waitingForKaelStepDestination) {
        System.out.println("🌑 Kael's SHADOW STEP destination selection at: (" + row + "," + col + ")");
        Cell destCell = playerBoard.getCell(row, col);
        if (destCell.hasShip()) {
            updateStatusLabel("❌ Destination already has a ship! Choose an empty cell.", Color.RED);
            waitingForKaelStepDestination = false;
            return;
        }
        
        Kael kael = (Kael) playerCharacter;
        
        
        System.out.println("Current energy: " + kael.getCurrentEnergy() + "/" + kael.getMaxEnergy());
        System.out.println("ShadowStepCooldown: " + kael.getShadowStepCooldown());
        
        boolean used = kael.useShadowStep(playerBoard, 
            stepSourceCoordinates[0], stepSourceCoordinates[1], row, col);
        
        if (used) {
            updateStatusLabel("🌑 Shadow Step! Ship teleported successfully!", Color.CYAN);
            refreshBoardsOnly();
            
            playerTurn = false;
            if (turnTimer != null) {
                turnTimer.stopTimer();
                turnTimer.setVisible(false);
            }
            if (enemyTurnTimer != null) {
                enemyTurnTimer.setTimerLabel("Enemy Turn");
                enemyTurnTimer.setVisible(true);
                enemyTurnTimer.startTimer();
            }
            Timer timer = new Timer(1200, e -> enemyTurn());
            timer.setRepeats(false);
            timer.start();
        } else {
            updateStatusLabel("❌ Cannot use Shadow Step! Check energy or cooldown.", Color.RED);
        }
        waitingForKaelStepDestination = false;
        return;
    }
    
    
    if (waitingForSkillTarget && currentSkillTargetsOwnBoard) {
        if (turnTimer != null) turnTimer.stopTimer();
        executeSkill(row, col);
        return;
    }
            
            if (waitingForAerisShield && currentAerisShieldCallback != null) {
                currentAerisShieldCallback.accept(row, col);
                waitingForAerisShield = false;
                currentAerisShieldCallback = null;
            }
        });
        
        enemyBoardPanel.setEnemyClickHandler((row, col) -> {
            if (waitingForKaelBlade && currentKaelBladeCallback != null) {
                currentKaelBladeCallback.accept(row, col);
                waitingForKaelBlade = false;
                currentKaelBladeCallback = null;
                return;
            }
            
            if (waitingForKaelDomain && currentKaelDomainCallback != null) {
                currentKaelDomainCallback.accept(row, col);
                waitingForKaelDomain = false;
                currentKaelDomainCallback = null;
                return;
            }
            
            if (waitingForSeleneVision && currentSeleneVisionCallback != null) {
                currentSeleneVisionCallback.accept(row, col);
                waitingForSeleneVision = false;
                currentSeleneVisionCallback = null;
                return;
            }
            
            if (waitingForSeleneCrescent && currentSeleneCrescentCallback != null) {
                currentSeleneCrescentCallback.accept(row, col);
                waitingForSeleneCrescent = false;
                currentSeleneCrescentCallback = null;
                return;
            }
            
            if (waitingForTarget && targetCallback != null) {
                targetCallback.onTargetSelected(row, col);
                waitingForTarget = false;
                targetCallback = null;
                return;
            }
            
            if (waitingForWhirlpoolTarget && currentWhirlpoolCallback != null) {
                currentWhirlpoolCallback.accept(row, col);
                waitingForWhirlpoolTarget = false;
                currentWhirlpoolCallback = null;
                return;
            }
            
            if (playerTurn) {
                if (turnTimer != null) turnTimer.stopTimer();
                handlePlayerAttack(row, col);
            }
        });
        
        panel.add(playerBoardPanel);
        panel.add(enemyBoardPanel);
        
        return panel;
    }
    
 private void handlePlayerAttack(int row, int col) {
    if (enemyBoard.isCellFiredUpon(row, col)) {
        updateStatusLabel("⚠️ You already shot at (" + row + "," + col + ")! Choose another cell!", Color.RED);
        
        
        
        if (playerBoardPanel != null) {
            playerBoardPanel.repaint();
            playerBoardPanel.revalidate();
        }
        if (enemyBoardPanel != null) {
            enemyBoardPanel.refreshColors();
            enemyBoardPanel.repaint();
            enemyBoardPanel.revalidate();
        }

        return; 
    }
    
    
    updateStatusLabel("⚡ FIRING at (" + row + "," + col + ")!", Color.YELLOW);
    
    ShotResult result = ShotResult.MISS;

    if (playerCharacter instanceof Selene) {
        Selene selene = (Selene) playerCharacter;
        selene.checkLinkedCells(enemyBoard, row, col);
    }
    
    result = enemyBoard.fire(row, col);
    
    if (result == ShotResult.HIT || result == ShotResult.SUNK) {
        updateStatusLabel("💥 HIT! Enemy ship damaged!", Color.GREEN);

    if (result == ShotResult.SUNK) {
        audio.MusicManager.getInstance().playSound("sunk");  // SUNK sound
    } else {
        audio.MusicManager.getInstance().playSound("hit");   // HIT sound
    }

        if (frame.getContentPane() instanceof WaveBackgroundPanel) {
            ((WaveBackgroundPanel) frame.getContentPane()).triggerShake(15);
        }
    if (playerCharacter instanceof Jiji && result == ShotResult.SUNK) {
        showJijiAttackAnimation();
    }
    if (playerCharacter instanceof Kael && result == ShotResult.SUNK) {
        showKaelAttackAnimation();
    }
    if (playerCharacter instanceof Aeris && result == ShotResult.SUNK) {
        showAerisAttackAnimation();
    }
    if (playerCharacter instanceof Selene && result == ShotResult.SUNK) {
        showSeleneAttackAnimation();
    }
    if (playerCharacter instanceof Flue && result == ShotResult.SUNK) {
        showFlueAttackAnimation();
    }
    if (playerCharacter instanceof Valerius && result == ShotResult.SUNK) {
        showValeriusAttackAnimation();
    }
    if (playerCharacter instanceof characters.Skye && result == ShotResult.SUNK) {
        showSkyeAttackAnimation();
    }
    if (playerCharacter instanceof characters.Morgana && result == ShotResult.SUNK) {
        showMorganaAttackAnimation();
    }

    if (currentEnemy instanceof Jiji && result == ShotResult.SUNK) {
        Jiji enemyJiji = (Jiji) currentEnemy;
        enemyJiji.onShipSunk();
        refreshEnemyJijiPortrait();
    }
    if (currentEnemy instanceof Kael && result == ShotResult.SUNK) {
        startEnemyKaelDamagedAnimation();
    }
    if (currentEnemy instanceof Valerius && result == ShotResult.SUNK) {
        startEnemyValeriusDamagedAnimation();
    }
    if (currentEnemy instanceof characters.Skye && result == ShotResult.SUNK) {
        showEnemySkyeDamagedAnimation();
    }
    if (currentEnemy instanceof characters.Morgana && result == ShotResult.SUNK) {
        showEnemyMorganaDamagedAnimation();
    }
    if (currentEnemy instanceof Aeris && result == ShotResult.SUNK) {
        showEnemyAerisDamagedAnimation();
    }
    if (currentEnemy instanceof Selene && result == ShotResult.SUNK) {
        showEnemySeleneDamagedAnimation();
    }
    if (currentEnemy instanceof Flue && result == ShotResult.SUNK) {
        showEnemyFlueDamagedAnimation();
    }
    } else {
        updateStatusLabel("💧 Miss...", Color.CYAN);
        audio.MusicManager.getInstance().playSound("miss");
    }
    
    if (playerCharacter instanceof Selene) {
        ((Selene) playerCharacter).endTurn();  
    }

    enemyBoardPanel.updateCell(row, col, result);
    updateShipCounters();

    if (playerCharacter instanceof Jiji) {
        ((Jiji) playerCharacter).updateTurnCounter();
    } else if (playerCharacter instanceof Kael) {
        ((Kael) playerCharacter).updateTurnCounter();
    } else if (playerCharacter instanceof Skye) {
        ((Skye) playerCharacter).updateTurnCounter();
    } else if (playerCharacter instanceof Valerius) {
        ((Valerius) playerCharacter).updateTurnCounter();
    } else if (playerCharacter instanceof Morgana) {  
        ((Morgana) playerCharacter).updateTurnCounter();
    } else if (playerCharacter instanceof Selene) {  
        ((Selene) playerCharacter).updateTurnCounter();
    } else if (playerCharacter instanceof Aeris) {  
        ((Aeris) playerCharacter).updateTurnCounter();
    } else if (playerCharacter instanceof Flue) {  
        ((Flue) playerCharacter).updateTurnCounter();
    }
    
    refreshBoardsOnly();
    
    if (enemyBoard.allShipsSunk()) {
        updateStatusLabel("🎉 VICTORY! All enemy ships destroyed!", Color.ORANGE);
        waveComplete();
        return;
    }
    
    playerTurn = false;
    if (turnTimer != null) {
        turnTimer.stopTimer();
        turnTimer.setVisible(false);
    }
    if (enemyTurnTimer != null) {
        enemyTurnTimer.setTimerLabel("Enemy Turn");
        enemyTurnTimer.setVisible(true);
        enemyTurnTimer.startTimer();
    }
    
    for (int i = 3; i > 0; i--) {
        final int count = i;
        Timer countTimer = new Timer((4-i) * 300, e -> {
            updateStatusLabel("🤖 Enemy attacking in " + count + "...", Color.RED);
        });
        countTimer.setRepeats(false);
        countTimer.start();
    }
    
    Timer timer = new Timer(1200, e -> enemyTurn());
    timer.setRepeats(false);
    timer.start();
}

private void enemyTurn() {
    if (enemyTurnTimer != null) {
        enemyTurnTimer.setVisible(true);
    }
    updateStatusLabel("🤖 ENEMY IS ATTACKING!", Color.RED);
    
    if (playerCharacter instanceof Flue) {
        Flue flue = (Flue) playerCharacter;
        flue.updateVirusSpread(enemyBoard);
    }
    
    if (playerCharacter instanceof Selene) {
        Selene selene = (Selene) playerCharacter;
        selene.updateMoonPhase();
    }
    
    Timer delayTimer = new Timer(500, e -> {
        if (playerCharacter instanceof Skye) {
            Skye skye = (Skye) playerCharacter;
            if (skye.shouldSkipEnemyTurn()) {
                updateStatusLabel("🔴 Enemy chasing laser pointer! Turn skipped!", Color.ORANGE);
                playerTurn = true;
                if (enemyTurnTimer != null) {
                    enemyTurnTimer.stopTimer();
                    enemyTurnTimer.setVisible(false);
                }
                if (turnTimer != null) {
                    turnTimer.setTimerLabel("Your Turn");
                    turnTimer.setVisible(true);
                    turnTimer.startTimer();
                }
                onPlayerTurnStart();
                
                
                if (currentSkillPanel != null) {
                    currentSkillPanel.updateUI();
                }
                return;
            }
        }

        // Get a random unused cell instead of just random
        int[] coordinates = getRandomUnfiredCell();
        int x = coordinates[0];
        int y = coordinates[1];
        
        useEnemySkill();

        if (currentEnemy instanceof Selene) {
            Selene selene = (Selene) currentEnemy;
            selene.checkLinkedCells(playerBoard, x, y);
        }
        
        if (playerCharacter instanceof Jiji) {
            ((Jiji) playerCharacter).updateTurnCounter();
        } else if (playerCharacter instanceof Kael) {
            ((Kael) playerCharacter).updateTurnCounter();
        } else if (playerCharacter instanceof Skye) {
            ((Skye) playerCharacter).updateTurnCounter();
        } else if (playerCharacter instanceof Valerius) {
            ((Valerius) playerCharacter).updateTurnCounter();
        } else if (playerCharacter instanceof Morgana) {  
            ((Morgana) playerCharacter).updateTurnCounter();
        } else if (playerCharacter instanceof Selene) {  
            ((Selene) playerCharacter).updateTurnCounter();
        } else if (playerCharacter instanceof Aeris) {  
            ((Aeris) playerCharacter).updateTurnCounter();
        } else if (playerCharacter instanceof Flue) {  
            ((Flue) playerCharacter).updateTurnCounter();
        }
        
        updateStatusLabel("🎯 Enemy firing at (" + x + "," + y + ")!", Color.ORANGE);
        
        // Track ship count before the attack to detect if a ship gets sunk
        int shipsBeforeAttack = 0;
        for (Ship ship : playerBoard.getShips()) {
            if (!ship.isSunk()) {
                shipsBeforeAttack++;
            }
        }
        System.out.println("Ships before attack: " + shipsBeforeAttack);
        
        ShotResult result = playerBoard.fire(x, y);
        
        // Check if Jiji had a ship sunk by this attack
        if (playerCharacter instanceof Jiji) {
            int shipsAfterAttack = 0;
            for (Ship ship : playerBoard.getShips()) {
                if (!ship.isSunk()) {
                    shipsAfterAttack++;
                }
            }
            System.out.println("Ships after attack: " + shipsAfterAttack);
            

            // If a ship was sunk (ships decreased), trigger damaged animation
            if (shipsAfterAttack < shipsBeforeAttack) {
                Jiji jiji = (Jiji) playerCharacter;
                jiji.onShipSunk();
                refreshJijiPortrait();
                updateStatusLabel("💀 JIJI's ship was SUNK! Jiji is damaged!", Color.RED);
            }
        }
        
        if (playerCharacter instanceof Jiji && ((Jiji) playerCharacter).checkFirewall(x, y, result)) {
            updateStatusLabel("🛡️ FIREWALL blocked the enemy shot!", Color.CYAN);
        } else if (playerCharacter instanceof Morgana && ((Morgana) playerCharacter).tryDodgeHit(x, y, result)) {
            updateStatusLabel("🌊 OCEAN'S EMBRACE blocked the hit!", Color.CYAN);
        } else {
            playerBoardPanel.updateCell(x, y, result);
        }
        
        updateShipCounters();
        
        switch(result) {
            case HIT:
                updateStatusLabel("💥 Enemy hit one of your ships!", Color.RED);
                audio.MusicManager.getInstance().playSound("hit");
                break;
            case SUNK:
                updateStatusLabel("💀 YOUR SHIP WAS SUNK!", Color.RED);
                audio.MusicManager.getInstance().playSound("sunk");
                if (currentEnemy instanceof Jiji) {
                    showEnemyJijiAttackAnimation();
                }
                if (currentEnemy instanceof Kael) {
                    showEnemyKaelAttackAnimation();
                }
                if (currentEnemy instanceof characters.Morgana) {
                    showEnemyMorganaAttackAnimation();
                }
                if (currentEnemy instanceof Aeris) {
                    showEnemyAerisAttackAnimation();
                }
                if (currentEnemy instanceof Flue) {
                    showEnemyFlueAttackAnimation();
                }
                if (currentEnemy instanceof Selene) {
                    showEnemySeleneAttackAnimation();
                }
                if (playerCharacter instanceof Kael) {
                    startKaelDamagedAnimation();
                }
                if (playerCharacter instanceof Valerius) {
                    startValeriusDamagedAnimation();
                }
                if (playerCharacter instanceof characters.Morgana) {
                    startMorganaDamagedAnimation();
                }
                if (playerCharacter instanceof Aeris) {
                    startAerisDamagedAnimation();
                }
                if (playerCharacter instanceof Selene) {
                    startSeleneDamagedAnimation();
                }
                if (playerCharacter instanceof Flue) {
                    startFlueDamagedAnimation();
                }
                break;
            case MISS:
                updateStatusLabel("😅 Enemy missed! Lucky break!", Color.GREEN);
                audio.MusicManager.getInstance().playSound("miss");
                break;
            default:
                break;
        }
        
        refreshBoardsOnly();
        
        if (playerBoard.allShipsSunk()) {
            audio.MusicManager.getInstance().stopMusic();
            audio.MusicManager.getInstance().playSound("defeat");
            updateStatusLabel("💔 GAME OVER - Your fleet destroyed!", Color.RED);
            gameOver();
            return;
    }
        
        playerTurn = true;
        if (turnTimer != null) {
            turnTimer.setTimerLabel("Your Turn");
            turnTimer.setVisible(true);
            turnTimer.startTimer();
        }
        onPlayerTurnStart();
        cancelAllSkillTargeting();
        
        
        if (currentSkillPanel != null) {
            currentSkillPanel.updateUI();
        }
    });
    
    delayTimer.setRepeats(false);
    delayTimer.start();
}
    
    private void refreshBoardsOnly() {
        if (playerBoardPanel != null) {
            playerBoardPanel.refreshColors();
        }
        if (enemyBoardPanel != null) {
            enemyBoardPanel.refreshColors();
        }
        updateShipCounters();
    }
    
    private void refreshUI() {
        refreshBoardsOnly();
        
    }
    
    private void waveComplete() {
         if (moonPhaseTimer != null) {
        moonPhaseTimer.stop();
    }

        audio.MusicManager.getInstance().stopMusic();
        audio.MusicManager.getInstance().playSound("victory");

        updateStatusLabel("🎉 WAVE CLEAR! Well done!", Color.GREEN);
        currentWaveIndex++;
        
        String message = "🎉 Victory! You defeated " + currentEnemy.getName() + "!\n\n";
        
        if (currentWaveIndex < waves.size()) {
            message += "Next wave: " + waves.get(currentWaveIndex).enemy.getName();
            healPlayerShips();  
            updateShipCounters();
        } else {
            message += "You've completed all waves!";
        }
        
        int result = JOptionPane.showConfirmDialog(frame,
            message,
            "Wave Complete",
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION && currentWaveIndex < waves.size()) {
            loadWave(currentWaveIndex);
        } else {
            Main.showMainMenu();
        }
    }

    private void showToastMessage(String message, Color color) {
    
    JLabel toast = new JLabel(message);
    toast.setFont(new Font("Arial", Font.BOLD, 14));
    toast.setForeground(Color.WHITE);
    toast.setBackground(color);
    toast.setOpaque(true);
    toast.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    
    
    JPanel popupPanel = new JPanel(new BorderLayout());
    popupPanel.setBackground(color);
    popupPanel.add(toast, BorderLayout.CENTER);
    popupPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
    
    
    Point location = enemyBoardPanel.getLocationOnScreen();
    int x = location.x + enemyBoardPanel.getWidth() / 2 - 150;
    int y = location.y + enemyBoardPanel.getHeight() / 2 - 30;
    
    
    PopupFactory factory = PopupFactory.getSharedInstance();
    Popup popup = factory.getPopup(frame, popupPanel, x, y);
    popup.show();
    
    
    Timer timer = new Timer(1500, e -> {
        popup.hide();
        
        enemyBoardPanel.repaint();
        playerBoardPanel.repaint();
    });
    timer.setRepeats(false);
    timer.start();
}
    
    private void gameOver() {
         if (moonPhaseTimer != null) {
        moonPhaseTimer.stop();
    }
        JOptionPane.showMessageDialog(frame,
            "💀 Game Over! " + currentEnemy.getName() + " has defeated you.\n\nTry again?",
            "Defeat",
            JOptionPane.ERROR_MESSAGE);
        Main.showMainMenu();
    }
    
    private void showVictoryScreen() {
        StringBuilder victoryMessage = new StringBuilder();
        victoryMessage.append("🏆 CONGRATULATIONS! You completed the campaign! 🏆\n\n");
        victoryMessage.append("You defeated:\n");
        
        for (int i = 0; i < waves.size(); i++) {
            victoryMessage.append("✓ Wave ").append(i + 1).append(": ")
                         .append(waves.get(i).enemy.getName()).append("\n");
        }
        
        victoryMessage.append("\nYou are the true Tidebound Champion!");
        
        JOptionPane.showMessageDialog(frame,
            victoryMessage.toString(),
            "Campaign Victory",
            JOptionPane.INFORMATION_MESSAGE);
        Main.showMainMenu();
    }
    
    private static class CampaignWave {
        String title;
        String description;
        GameCharacter enemy;
        Color waveColor;
        
        CampaignWave(String title, String description, GameCharacter enemy, Color color) {
            this.title = title;
            this.description = description;
            this.enemy = enemy;
            this.waveColor = color;
        }
    }
    
    private JPanel createShipCounterPanel(boolean isPlayer) {
        Board board = isPlayer ? playerBoard : enemyBoard;
        Color bgColor = isPlayer ? new Color(0, 50, 0) : new Color(50, 0, 0);
        
        int totalShips = board.getShips().size();
        int remainingShips = 0;
        for (Ship ship : board.getShips()) {
            if (!ship.isSunk()) {
                remainingShips++;
            }
        }
        
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel titleLabel = new JLabel(isPlayer ? "🚢 YOUR FLEET" : "🚢 ENEMY FLEET", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);
        
        JPanel shipIconsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        shipIconsPanel.setBackground(bgColor);
        
        for (int i = 0; i < totalShips; i++) {
            JLabel shipIcon;
            if (i < remainingShips) {
                shipIcon = new JLabel("🚢");
                shipIcon.setForeground(isPlayer ? Color.GREEN : Color.RED);
            } else {
                shipIcon = new JLabel("💀");
                shipIcon.setForeground(Color.GRAY);
            }
            shipIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            shipIconsPanel.add(shipIcon);
        }
        
        panel.add(shipIconsPanel);
        
        return panel;
    }
    
    private void updateStatusLabel(String message, Color color) {
        System.out.println("🔴 DEBUG: updateStatusLabel called with: " + message);
        
        if (statusLabel == null) {
            System.out.println("❌ ERROR: statusLabel is NULL!");
            return;
        }
        
        System.out.println("✅ Updating statusLabel to: " + message);
        statusLabel.setText(message);
        statusLabel.setForeground(color);
        statusLabel.repaint();
        if (statusLabel.getParent() != null) {
            statusLabel.getParent().repaint();
        }
    }
    
    private void resetStatusLabel() {
        if (statusLabel == null) return;
        
        if (!playerTurn) {
            statusLabel.setText("🤖 ENEMY'S TURN - They're planning...");
            statusLabel.setForeground(Color.RED);
        } else {
            String turnMessage = getCharacterTurnMessage();
            statusLabel.setText(turnMessage);
            statusLabel.setForeground(Color.WHITE);
        }
    }
    
    private String getCharacterTurnMessage() {
        if (playerCharacter instanceof Jiji) {
            return "💻 JIJI'S TURN - Hack the enemy! Click to fire or use skills.";
        } else if (playerCharacter instanceof Kael) {
            return "🌑 KAEL'S TURN - Strike from shadows! Click to fire or use skills.";
        } else if (playerCharacter instanceof Valerius) {
            return "🛡️ VALERIUS'S TURN - Hold the line! Click to fire or use skills.";
        } else if (playerCharacter instanceof Skye) {
            return "🐱 SKYE'S TURN - Cats are ready! Click to fire or use skills.";
        } else if (playerCharacter instanceof Selene) {
            Selene selene = (Selene) playerCharacter;
            if (selene.isNightTime()) {
                return "🌙 SELENE'S TURN - Night time! Enhanced skills ready!";
            }
            return "🔮 SELENE'S TURN - The moon guides you! Click to fire or use skills.";
        }
        return "YOUR TURN - Click on enemy waters to fire!";
    }
    
 private void onPlayerTurnStart() {
    System.out.println("🔄 Player turn started! Checking conditions...");

    // Reset Jiji attack animation flag for new turn
    jijiAttackPlayedThisTurn = false;
    
    if (playerCharacter instanceof Jiji) {
        Jiji jiji = (Jiji) playerCharacter;
        boolean wasDamaged = jiji.isDamaged();
        jiji.updateDamageState();
        
        
        if (wasDamaged && !jiji.isDamaged()) {
            System.out.println("😺 Jiji recovered! Returning to idle animation");
            refreshJijiPortrait();
            updateStatusLabel("😺 Jiji has recovered from the damage!", Color.GREEN);
        }
    }

     if (playerCharacter instanceof Flue) {
        Flue flue = (Flue) playerCharacter;
        flue.updateVirusSpread(enemyBoard);
        System.out.println("🦠 Infected cells count: " + flue.getInfectedCellsCount());
    }
    
    if (playerCharacter instanceof Selene) {
        Selene selene = (Selene) playerCharacter;
        
        
        selene.updateMoonPhase();
        
        
        if (selene.consumeNightJustStarted()) {
            System.out.println("🌙 Night just started! Refreshing UI...");
            updateStatusLabel("🌙✨ NIGHT FALLS! All skills are ready and enhanced!", Color.YELLOW);
            
            if (currentWaveIndex < waves.size()) {
                createBattleUI(waves.get(currentWaveIndex));
                return;
            }
        }
        
        
        if (!selene.isNightTime() && !selene.isEclipseMode()) {
            System.out.println("🌅 Night has ended. Skills return to normal.");
            updateStatusLabel("🌅 Night ends. Skills return to normal.", Color.CYAN);
            if (currentWaveIndex < waves.size()) {
                createBattleUI(waves.get(currentWaveIndex));
                return;
            }
        }
    }
    
    cancelAllSkillTargeting();
    refreshBoardsOnly();
     if (currentSkillPanel != null) {
        currentSkillPanel.updateUI();
    }
    
    String turnMessage = getCharacterTurnMessage();
    updateStatusLabel(turnMessage, Color.GREEN);
    
    

    
    if (timerEnabled && turnTimer != null) {
        if (enemyTurnTimer != null) {
            enemyTurnTimer.stopTimer();
            enemyTurnTimer.setVisible(false);
        }
        turnTimer.setTimerLabel("Your Turn");
        turnTimer.setVisible(true);
        turnTimer.stopTimer(); 
        turnTimer.startTimer();
    }
}
private void endTurn() {
    
    if (turnTimer != null) {
        turnTimer.stopTimer();
    }
    
    if (!playerTurn) {
        updateStatusLabel("It's not your turn!", Color.RED);
        return;
    }
    
    
    if (waitingForTarget || waitingForWhirlpoolTarget || waitingForAerisShield || 
        waitingForKaelStepSource || waitingForKaelBlade || waitingForKaelDomain ||
        waitingForSeleneVision || waitingForSeleneBinding || waitingForSeleneCrescent) {
        
        int confirm = JOptionPane.showConfirmDialog(frame,
            "You have an active skill targeting. End turn anyway?\nThis will cancel your skill.",
            "Cancel Skill",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            
            if (timerEnabled && turnTimer != null) {
                if (enemyTurnTimer != null) {
                    enemyTurnTimer.stopTimer();
                    enemyTurnTimer.setVisible(false);
                }
                turnTimer.setTimerLabel("Your Turn");
                turnTimer.setVisible(true);
                turnTimer.startTimer();
            }
            return;
        }
        
        
        cancelAllSkillTargeting();
        updateStatusLabel("Skill cancelled. Ending turn...", Color.ORANGE);
    }
    
    
    playerTurn = false;
    if (turnTimer != null) {
        turnTimer.stopTimer();
        turnTimer.setVisible(false);
    }
    if (enemyTurnTimer != null) {
        enemyTurnTimer.setTimerLabel("Enemy Turn");
        enemyTurnTimer.setVisible(true);
        enemyTurnTimer.startTimer();
    }
    
    
    if (currentSkillPanel != null) {
        currentSkillPanel.updateUI();
    }
    
    refreshBoardsOnly();
    updateStatusLabel("🤖 ENEMY'S TURN - They're planning...", Color.RED);
    
    Timer timer = new Timer(1200, e -> enemyTurn());
    timer.setRepeats(false);
    timer.start();
}
    
    
 
    
    private void cancelAllSkillTargeting() {
        waitingForTarget = false;
        waitingForWhirlpoolTarget = false;
        waitingForAerisShield = false;
        waitingForKaelStepSource = false;
        waitingForKaelStepDestination = false;
        waitingForKaelBlade = false;
        waitingForKaelDomain = false;
        waitingForSeleneVision = false;
        waitingForSeleneBinding = false;
        waitingForSeleneCrescent = false;
        
        currentSkillName = "";
        targetCallback = null;
        currentWhirlpoolCallback = null;
        currentAerisShieldCallback = null;
        currentKaelBladeCallback = null;
        currentKaelDomainCallback = null;
        currentSeleneVisionCallback = null;
        currentSeleneBindingCallback = null;
        currentSeleneCrescentCallback = null;
    }
}