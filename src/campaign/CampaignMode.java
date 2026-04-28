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
   
    private boolean testMode = true;
    private String testEnemyName = "Valerius";

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

// Valerius idle animation frames
private ImageIcon[] valeriusIdleFrames = new ImageIcon[4];
private Timer valeriusIdleAnimationTimer;
private int valeriusCurrentIdleFrame = 0;
private int valeriusIdleFrameCounter = 0;
private Timer valeriusDamagedAnimationTimer;
private Timer enemyValeriusDamagedAnimationTimer;

private ImageIcon[] valeriusAttackFrames = new ImageIcon[4];
private Timer valeriusAttackAnimationTimer;
private int currentValeriusAttackFrame = 0;
private int valeriusAttackFrameCounter = 0;
private boolean valeriusAttackAnimationPlaying = false;
private static final int[] VALERIUS_ATTACK_FRAME_DURATIONS = {4, 4, 4, 8}; // ticks (~0.4s total)

private ImageIcon[] enemyValeriusAttackFrames = new ImageIcon[4];
private Timer enemyValeriusAttackAnimationTimer;
private int currentEnemyValeriusAttackFrame = 0;
private int enemyValeriusAttackFrameCounter = 0;
private boolean enemyValeriusAttackAnimationPlaying = false;

private ImageIcon[] enemyValeriusIdleFrames = new ImageIcon[4];
private Timer enemyValeriusIdleAnimationTimer;
private int enemyValeriusCurrentIdleFrame = 0;
private int enemyValeriusIdleFrameCounter = 0;

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

private JLabel jijiLargePortraitLabel;


private JLabel kaelLargePortraitLabel;
private JLabel enemyKaelLargePortraitLabel;

private JLabel valeriusLargePortraitLabel;
private JLabel enemyValeriusLargePortraitLabel;
private ImageIcon[] valeriusDamagedFrames = new ImageIcon[4];
private ImageIcon[] enemyValeriusDamagedFrames = new ImageIcon[4];

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
        initJijiDamagedFrames();
        initJijiAttackFrames();
        initKaelIdleFrames();
        initEnemyKaelIdleFrames();
        initKaelAttackFrames();
        initEnemyKaelAttackFrames();
        initKaelDamagedFrames();
        initEnemyKaelDamagedFrames();

        // Initialize Valerius frames (always needed for both player and enemy)
        initValeriusIdleFrames();
        initValeriusDamagedFrames();
        initValeriusAttackFrames();
        initEnemyValeriusIdleFrames();
        initEnemyValeriusDamagedFrames();
        initEnemyValeriusAttackFrames();
        
        if (playerCharacter instanceof Valerius) {
        Valerius valerius = (Valerius) playerCharacter;
        if (valerius.isDamaged()) {
            if (valeriusDamagedFrames[0] != null) {
                // Don't try to set icon here - label doesn't exist yet
                // This will be handled in createBattleUI()
            }
        } else {
            if (valeriusIdleFrames[0] != null) {
                // Don't start animation here - label doesn't exist yet
                // This will be handled in createBattleUI()
            }
        }
    }
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
    
    adjustEnemyDifficulty(index + 1);
    placeEnemyShips(currentEnemy, enemyBoard);
    createBattleUI(wave);
    updateStatusLabel(waveMessage, Color.YELLOW);
    updateStatusLabel(waveMessage, Color.YELLOW);
    
    
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
        if (enemy instanceof Jiji || enemy instanceof Kael || enemy instanceof Valerius || enemy instanceof Skye) {
            placeRandomShips(board);
        } else {
            placeDefaultShips(board);
        }
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
    
 
 private void createBattleUI(CampaignWave wave) {
    // Stop any existing Jiji animations from previous battle
    stopIdleAnimation();
    stopDamagedAnimation();
    stopAttackAnimation();
    stopEnemyIdleAnimation();
    stopEnemyDamagedAnimation();
    stopEnemyAttackAnimation();
    stopKaelIdleAnimation();
    stopEnemyKaelIdleAnimation();
    stopKaelAttackAnimation();
    stopEnemyKaelAttackAnimation();
    stopKaelDamagedAnimation();
    stopEnemyKaelDamagedAnimation();
    stopValeriusAttackAnimation();
    stopEnemyValeriusAttackAnimation();
    
    frame.getContentPane().removeAll();
    frame.setLayout(new BorderLayout());

    WaveBackgroundPanel backgroundPanel = new WaveBackgroundPanel();
    backgroundPanel.setLayout(new BorderLayout());

    JPanel contentOverlay = new JPanel(new BorderLayout());
    contentOverlay.setOpaque(false);
    contentOverlay.setBackground(new Color(0, 0, 0, 30));

    loadOceanBackground();

    playerBoardPanel = new BoardPanel(true, playerBoard, true);
    enemyBoardPanel = new BoardPanel(false, enemyBoard, false);
    
    if (playerCharacter instanceof Flue) {
        ((Flue) playerCharacter).setEnemyBoard(enemyBoard);
    }
    
    setupClickHandlers();
    
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(false);
    topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
    JButton backButton = new JButton("← BACK TO MENU");
    backButton.setFont(new Font("Arial", Font.BOLD, 14));
    backButton.setBackground(new Color(80, 80, 100));
    backButton.setForeground(Color.WHITE);
    backButton.setFocusPainted(false);
    backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    backButton.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(frame,
            "Are you sure you want to return to the main menu?",
            "Return to Menu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (turnTimer != null) turnTimer.stopTimer();
            if (skillPanelRefreshTimer != null) skillPanelRefreshTimer.stop();
            if (moonPhaseTimer != null) moonPhaseTimer.stop();
            if (currentSkillPanel != null) currentSkillPanel.stopTimers();
            stopEnemyIdleAnimation();
            stopEnemyDamagedAnimation();
            stopEnemyAttackAnimation();
            Main.showMainMenu();
        }
    });
    topPanel.add(backButton, BorderLayout.WEST);
    
    turnTimer = new TimerPanel(10, () -> {
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
    
    waveLabel = new JLabel(String.format("WAVE %d/%d - VS %s", 
        currentWaveIndex + 1, waves.size(), currentEnemy.getName()));
    waveLabel.setFont(new Font("Arial", Font.BOLD, 18));
    waveLabel.setForeground(Color.YELLOW);
    
    JPanel waveLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    waveLabelPanel.setOpaque(false);
    waveLabelPanel.add(waveLabel);
    
    JPanel centerTopPanel = new JPanel(new BorderLayout());
    centerTopPanel.setOpaque(false);
    centerTopPanel.add(waveLabelPanel, BorderLayout.NORTH);
    
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
    centerTopPanel.add(timerPanel, BorderLayout.CENTER);
    topPanel.add(centerTopPanel, BorderLayout.CENTER);
    
    JPanel mainContentPanel = new JPanel(new BorderLayout());
    mainContentPanel.setOpaque(false);
    mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    
    JPanel boardsWrapper = new JPanel(new GridBagLayout());
    boardsWrapper.setOpaque(false);
    
    JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
    boardsPanel.setPreferredSize(new Dimension(1500, 0));  // Width fixed, height flexible
    boardsPanel.setOpaque(false);
    
    // ========== LEFT PANEL (Player) - UNCHANGED ==========
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.setOpaque(false);
    leftPanel.setPreferredSize(new Dimension(700, 700));
    leftPanel.setMaximumSize(new Dimension(700, 700));
    leftPanel.setMinimumSize(new Dimension(700, 700));
    leftPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(0, 255, 0, 150), 2),
        "⚓ YOUR FLEET",
        TitledBorder.CENTER,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 16),
        new Color(0, 255, 0, 200)
    ));
    
   JPanel charInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
charInfoPanel.setOpaque(false);

// Show only text (no portrait) for Jiji, Kael, and Valerius
if (playerCharacter instanceof Jiji || playerCharacter instanceof Kael || playerCharacter instanceof Valerius) {
    JLabel charNameLabel = new JLabel(getCharacterEmoji(playerCharacter) + " " + playerCharacter.getName());
    charNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
    charNameLabel.setForeground(Color.CYAN);
    charInfoPanel.add(charNameLabel);
    System.out.println("✅ " + playerCharacter.getName() + " - text only at top, large portrait below");
} else {
    // For other characters (Skye, Morgana, Aeris, Selene, Flue, etc.), show portrait
    JLabel charNameLabel = new JLabel(getCharacterEmoji(playerCharacter) + " " + playerCharacter.getName());
    Icon playerPortrait = getCharacterPortrait(playerCharacter);
    if (playerPortrait != null) {
        charNameLabel.setIcon(playerPortrait);
        charNameLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        charNameLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
    }
    charNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
    charNameLabel.setForeground(Color.CYAN);
    charInfoPanel.add(charNameLabel);
}

leftPanel.add(charInfoPanel, BorderLayout.NORTH);
leftPanel.add(playerBoardPanel, BorderLayout.CENTER);

playerShipLabel = new JLabel(getShipCountText(playerBoard), SwingConstants.CENTER);
playerShipLabel.setFont(new Font("Arial", Font.BOLD, 12));
playerShipLabel.setForeground(Color.WHITE);
leftPanel.add(playerShipLabel, BorderLayout.SOUTH);
    
    // ========== RIGHT PANEL (Enemy) - FIXED ==========
  JPanel rightPanel = new JPanel(new BorderLayout());
rightPanel.setOpaque(false);
rightPanel.setPreferredSize(new Dimension(700, 700));
rightPanel.setMaximumSize(new Dimension(700, 700));
rightPanel.setMinimumSize(new Dimension(700, 700));

// Create FRESH enemy character panel for NORTH
JPanel enemyTopPanel = new JPanel(new BorderLayout());
enemyTopPanel.setOpaque(false);

// Use full name
JLabel enemyNameLabel = new JLabel(currentEnemy.getName(), SwingConstants.CENTER);
enemyNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
enemyNameLabel.setForeground(Color.ORANGE);
enemyTopPanel.add(enemyNameLabel, BorderLayout.CENTER);

// Create a container for the board AND ship counter
JPanel boardContainer = new JPanel(new BorderLayout());
boardContainer.setOpaque(false);

// Add board to CENTER of container
boardContainer.add(enemyBoardPanel, BorderLayout.CENTER);

// Add ship counter to SOUTH of container
JLabel freshEnemyShipLabel = new JLabel(getShipCountText(enemyBoard), SwingConstants.CENTER);
freshEnemyShipLabel.setFont(new Font("Arial", Font.BOLD, 12));
freshEnemyShipLabel.setForeground(Color.WHITE);
freshEnemyShipLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
boardContainer.add(freshEnemyShipLabel, BorderLayout.SOUTH);

this.enemyShipLabel = freshEnemyShipLabel;

// Assemble right panel - ONLY ONCE
rightPanel.add(enemyTopPanel, BorderLayout.NORTH);
rightPanel.add(boardContainer, BorderLayout.CENTER);

// Set the border on rightPanel
rightPanel.setBorder(BorderFactory.createTitledBorder(
    BorderFactory.createLineBorder(new Color(255, 0, 0, 150), 2),
    "ENEMY WATERS",
    TitledBorder.CENTER,
    TitledBorder.TOP,
    new Font("Arial", Font.BOLD, 16),
    new Color(255, 0, 0, 200)
));

boardsPanel.add(leftPanel);
boardsPanel.add(rightPanel); // Board + ship counter together
    
    // Set the border on rightPanel
    rightPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(255, 0, 0, 150), 2),
        String.format("Enemy Waters"),
        TitledBorder.CENTER,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 16),
        new Color(255, 0, 0, 200)
    ));
    
    boardsPanel.add(leftPanel);
    boardsPanel.add(rightPanel);
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(20, 20, 20, 20);
    boardsWrapper.add(boardsPanel, gbc);

    mainContentPanel.add(boardsWrapper, BorderLayout.CENTER);
    
    currentSkillPanel = new SkillPanel(playerCharacter);
    currentSkillPanel.setBoards(playerBoardPanel, enemyBoardPanel);
    currentSkillPanel.setPreferredSize(new Dimension(450, 500));
    currentSkillPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    
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
    combinedBottomPanel.setPreferredSize(new Dimension(800, 220));   

    // LARGE PORTRAIT - Bottom Left
   // LARGE PORTRAIT - Bottom Left
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
        westWrapper.add(jijiLargePortraitLabel, BorderLayout.CENTER);  // ← FIXED: use jijiLargePortraitLabel
        
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
        westWrapper.add(kaelLargePortraitLabel, BorderLayout.CENTER);  // ← FIXED: use kaelLargePortraitLabel
        
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
        valeriusLargePortraitLabel.setToolTipText("Valerius: \"Fortifications hold!\"");
        valeriusLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
        valeriusLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
        valeriusLargePortraitLabel.setPreferredSize(new Dimension(250, 200));
        
        JPanel westWrapper = new JPanel(new BorderLayout());
        westWrapper.setOpaque(false);
        westWrapper.setPreferredSize(new Dimension(250, 220));
        westWrapper.add(valeriusLargePortraitLabel, BorderLayout.CENTER);  // ← FIXED: use valeriusLargePortraitLabel
        
        JLabel nameTag = new JLabel("🛡️ VALERIUS", SwingConstants.CENTER);
        nameTag.setFont(new Font("Arial", Font.BOLD, 12));
        nameTag.setForeground(new Color(100, 200, 255));
        westWrapper.add(nameTag, BorderLayout.SOUTH);
        
        combinedBottomPanel.add(westWrapper, BorderLayout.WEST);
        
        if (valeriusIdleFrames[0] != null) {
            startValeriusIdleAnimation();
        }
    }
} else {
    JPanel emptyPanel = new JPanel();
    emptyPanel.setOpaque(false);
    emptyPanel.setPreferredSize(new Dimension(250, 220));
    combinedBottomPanel.add(emptyPanel, BorderLayout.WEST);
}

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
        enemyValeriusLargePortraitLabel.setToolTipText("Enemy Valerius: \"Your attacks are futile!\"");
        enemyValeriusLargePortraitLabel.setHorizontalAlignment(JLabel.CENTER);
        enemyValeriusLargePortraitLabel.setVerticalAlignment(JLabel.CENTER);
        enemyValeriusLargePortraitLabel.setPreferredSize(new Dimension(250, 200));
        
        JPanel eastWrapper = new JPanel(new BorderLayout());
        eastWrapper.setOpaque(false);
        eastWrapper.setPreferredSize(new Dimension(250, 220));
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
} else {
    JPanel emptyPanelEast = new JPanel();
    emptyPanelEast.setOpaque(false);
    emptyPanelEast.setPreferredSize(new Dimension(250, 220));
    combinedBottomPanel.add(emptyPanelEast, BorderLayout.EAST);
}

    combinedBottomPanel.add(currentSkillPanel, BorderLayout.CENTER);
    mainContentPanel.add(combinedBottomPanel, BorderLayout.SOUTH);
    
    JPanel statusBarPanel = new JPanel();
    statusBarPanel.setOpaque(false);
    statusBarPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
    ((FlowLayout) statusBarPanel.getLayout()).setAlignment(FlowLayout.CENTER);
    
    statusLabel = new JLabel("YOUR TURN - Click on enemy waters to fire!", SwingConstants.CENTER);
    statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
    statusLabel.setForeground(Color.WHITE);
    statusLabel.setOpaque(true);
    statusLabel.setBackground(new Color(0, 0, 0, 100));
    statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    statusBarPanel.add(statusLabel);
    
    contentOverlay.add(topPanel, BorderLayout.NORTH);
    contentOverlay.add(mainContentPanel, BorderLayout.CENTER);
    contentOverlay.add(statusBarPanel, BorderLayout.SOUTH);
    backgroundPanel.add(contentOverlay, BorderLayout.CENTER);
    
    frame.setContentPane(backgroundPanel);
    frame.revalidate();
    frame.repaint();
    
    if (playerTurn && timerEnabled && turnTimer != null) {
        if (enemyTurnTimer != null) {
            enemyTurnTimer.stopTimer();
            enemyTurnTimer.setVisible(false);
        }
        turnTimer.setTimerLabel("Your Turn");
        turnTimer.setVisible(true);
        turnTimer.startTimer();
    }
    
    System.out.println("✅ Battle UI created with Timer!");
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
                int targetW = 250;
                int targetH = 200;
                // Scale proportionally to fit without distortion
                double scaleX = (double) targetW / base.getWidth();
                double scaleY = (double) targetH / base.getHeight();
                double scale = Math.min(scaleX, scaleY);
                int scaledW = (int) (base.getWidth() * scale);
                int scaledH = (int) (base.getHeight() * scale);
                Image tempScaled = base.getScaledInstance(scaledW, scaledH, Image.SCALE_SMOOTH);
                // Create a new image with target size and center the scaled image
                BufferedImage finalImage = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = finalImage.createGraphics();
                int x = (targetW - scaledW) / 2;
                int y = (targetH - scaledH) / 2;
                g2d.drawImage(tempScaled, x, y, null);
                g2d.dispose();
                Image scaled = finalImage;
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
                    // Apply subtle per-slot horizontal sway
                    int offsetX = SLOT_OFFSET_X[currentCycleSlot];
                    if (offsetX != 0) {
                        BufferedImage shifted = new BufferedImage(250, 200, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = shifted.createGraphics();
                        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g.drawImage(baseFrame.getImage(), offsetX, 0, null);
                        g.dispose();
                        jijiLargePortraitLabel.setIcon(new ImageIcon(shifted));
                    } else {
                        jijiLargePortraitLabel.setIcon(baseFrame);
                    }
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
    System.out.println("▶️ Jiji idle animation started");
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

private void startValeriusIdleAnimation() {
    stopValeriusDamagedAnimation();
    if (valeriusIdleAnimationTimer != null && valeriusIdleAnimationTimer.isRunning()) {
        valeriusIdleAnimationTimer.stop();
    }
    if (valeriusIdleFrames[0] == null || valeriusLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Valerius idle - frames:" + (valeriusIdleFrames[0]!=null) + " label:" + valeriusLargePortraitLabel);
        return;
    }
    valeriusCurrentIdleFrame = 0;
    valeriusIdleFrameCounter = 0;
    final int tickMs = 25;
    int[] valeriusSequence = {0,0,0,0,0,0,1,1,1,1,1,1,2,2,2,2,2,2,3,3,3,3,3,3};
    valeriusIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (valeriusLargePortraitLabel == null) return;
            if (!(playerCharacter instanceof Valerius)) {
                stopValeriusIdleAnimation();
                return;
            }
            valeriusIdleFrameCounter++;
            if (valeriusIdleFrameCounter >= valeriusSequence.length) {
                valeriusIdleFrameCounter = 0;
            }
            int frameIdx = valeriusSequence[valeriusIdleFrameCounter];
            if (frameIdx >= 0 && frameIdx < valeriusIdleFrames.length && valeriusIdleFrames[frameIdx] != null) {
                valeriusLargePortraitLabel.setIcon(valeriusIdleFrames[frameIdx]);
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Valerius idle animation error: " + ex.getMessage());
            stopValeriusIdleAnimation();
        }
    });
    valeriusIdleAnimationTimer.start();
}

private void stopValeriusIdleAnimation() {
    if (valeriusIdleAnimationTimer != null && valeriusIdleAnimationTimer.isRunning()) {
        valeriusIdleAnimationTimer.stop();
        valeriusCurrentIdleFrame = 0;
        valeriusIdleFrameCounter = 0;
        System.out.println("⏹️ Valerius idle animation stopped");
    }
}

private void startEnemyValeriusIdleAnimation() {
    stopEnemyValeriusDamagedAnimation();
    if (enemyValeriusIdleAnimationTimer != null && enemyValeriusIdleAnimationTimer.isRunning()) {
        enemyValeriusIdleAnimationTimer.stop();
    }
    if (enemyValeriusIdleFrames[0] == null || enemyValeriusLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start enemy Valerius idle - frames:" + (enemyValeriusIdleFrames[0]!=null) + " label:" + enemyValeriusLargePortraitLabel);
        return;
    }
    enemyValeriusCurrentIdleFrame = 0;
    enemyValeriusIdleFrameCounter = 0;
    final int tickMs = 25;
    int[] valeriusSequence = {0,0,0,0,0,0,1,1,1,1,1,1,2,2,2,2,2,2,3,3,3,3,3,3};
    enemyValeriusIdleAnimationTimer = new Timer(tickMs, e -> {
        try {
            if (enemyValeriusLargePortraitLabel == null) return;
            if (!(currentEnemy instanceof Valerius)) {
                stopEnemyValeriusIdleAnimation();
                return;
            }
            enemyValeriusIdleFrameCounter++;
            if (enemyValeriusIdleFrameCounter >= valeriusSequence.length) {
                enemyValeriusIdleFrameCounter = 0;
            }
            int frameIdx = valeriusSequence[enemyValeriusIdleFrameCounter];
            if (frameIdx >= 0 && frameIdx < enemyValeriusIdleFrames.length && enemyValeriusIdleFrames[frameIdx] != null) {
                enemyValeriusLargePortraitLabel.setIcon(enemyValeriusIdleFrames[frameIdx]);
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Valerius idle animation error: " + ex.getMessage());
            stopEnemyValeriusIdleAnimation();
        }
    });
    enemyValeriusIdleAnimationTimer.start();
}

private void stopEnemyValeriusIdleAnimation() {
    if (enemyValeriusIdleAnimationTimer != null && enemyValeriusIdleAnimationTimer.isRunning()) {
        enemyValeriusIdleAnimationTimer.stop();
        enemyValeriusCurrentIdleFrame = 0;
        enemyValeriusIdleFrameCounter = 0;
        System.out.println("⏹️ Enemy Valerius idle animation stopped");
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
                int targetW = 250;
                int targetH = 200;
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
                int targetW = 250;
                int targetH = 200;
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
                int targetW = 250;
                int targetH = 200;
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

private void initValeriusIdleFrames() {
    // Load player Valerius idle frames - match Jiji size (250x200)
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
                
                // Use the SAME dimensions as Jiji (250x200)
                int targetW = 250;
                int targetH = 200;
                
                // Simple scaling - fill the area
                Image scaled = base.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                valeriusIdleFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Loaded Valerius idle frame " + (i + 1) + " - " + targetW + "x" + targetH);
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
private void initValeriusDamagedFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/valerius_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ Valerius damaged frame " + (i+1) + " failed to load");
                    valeriusDamagedFrames[i] = null;
                    continue;
                }
                int targetW = 250;
                int targetH = 200;
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
}
private void initEnemyValeriusDamagedFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/valerius_dmg" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage base = ImageIO.read(f);
                if (base == null) {
                    System.out.println("⚠️ Enemy Valerius damaged frame " + (i+1) + " failed to load");
                    enemyValeriusDamagedFrames[i] = null;
                    continue;
                }
                // Flip horizontally for enemy
                BufferedImage flipped = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = flipped.createGraphics();
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-base.getWidth(), 0);
                g.setTransform(tx);
                g.drawImage(base, 0, 0, null);
                g.dispose();
                
                int targetW = 250;
                int targetH = 200;
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
}
private void startValeriusDamagedAnimation() {
    stopValeriusIdleAnimation();
    if (valeriusDamagedAnimationTimer != null && valeriusDamagedAnimationTimer.isRunning()) {
        valeriusDamagedAnimationTimer.stop();
    }
    if (valeriusDamagedFrames[0] == null || valeriusLargePortraitLabel == null) {
        System.out.println("⚠️ Cannot start Valerius damaged animation");
        return;
    }
    // Similar to Jiji's damaged animation logic
    // Add if you want damaged animation
}

private void stopValeriusDamagedAnimation() {
    if (valeriusDamagedAnimationTimer != null && valeriusDamagedAnimationTimer.isRunning()) {
        valeriusDamagedAnimationTimer.stop();
    }
}
private void stopEnemyValeriusDamagedAnimation() {
    if (enemyValeriusDamagedAnimationTimer != null && enemyValeriusDamagedAnimationTimer.isRunning()) {
        enemyValeriusDamagedAnimationTimer.stop();
    }
}

private void initValeriusAttackFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/valerius_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage img = ImageIO.read(f);
                BufferedImage scaled = new BufferedImage(250, 200, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaled.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.drawImage(img, 0, 0, 250, 200, null);
                g2d.dispose();
                valeriusAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Valerius attack frame " + (i + 1) + " loaded (250x200)");
            } catch (Exception e) {
                System.out.println("⚠️ Could not load Valerius attack frame " + (i + 1) + ": " + e.getMessage());
                valeriusAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Valerius attack frame missing: " + f.getAbsolutePath());
            valeriusAttackFrames[i] = null;
        }
    }
}

private void initEnemyValeriusAttackFrames() {
    for (int i = 0; i < 4; i++) {
        String path = "assets/valerius_atk" + (i + 1) + ".png";
        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage img = ImageIO.read(f);
                // Flip horizontally for enemy
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-img.getWidth(), 0);
                BufferedImage flipped = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = flipped.createGraphics();
                g2d.setTransform(tx);
                g2d.drawImage(img, 0, 0, null);
                g2d.dispose();
                BufferedImage scaled = new BufferedImage(250, 200, BufferedImage.TYPE_INT_ARGB);
                g2d = scaled.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.drawImage(flipped, 0, 0, 250, 200, null);
                g2d.dispose();
                enemyValeriusAttackFrames[i] = new ImageIcon(scaled);
                System.out.println("✅ Enemy Valerius attack frame " + (i + 1) + " loaded (flipped, 250x200)");
            } catch (Exception e) {
                System.out.println("⚠️ Could not load enemy Valerius attack frame " + (i + 1) + ": " + e.getMessage());
                enemyValeriusAttackFrames[i] = null;
            }
        } else {
            System.out.println("⚠️ Enemy Valerius attack frame missing: " + f.getAbsolutePath());
            enemyValeriusAttackFrames[i] = null;
        }
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
            if (!(playerCharacter instanceof Valerius)) return;
            valeriusAttackFrameCounter++;
            int frameTicks = VALERIUS_ATTACK_FRAME_DURATIONS[currentValeriusAttackFrame];
            if (valeriusAttackFrameCounter >= frameTicks) {
                currentValeriusAttackFrame++;
                valeriusAttackFrameCounter = 0;
                if (currentValeriusAttackFrame >= valeriusAttackFrames.length) {
                    valeriusAttackAnimationPlaying = false;
                    stopValeriusAttackAnimation();
                    startValeriusIdleAnimation();
                    return;
                }
                if (valeriusAttackFrames[currentValeriusAttackFrame] != null) {
                    valeriusLargePortraitLabel.setIcon(valeriusAttackFrames[currentValeriusAttackFrame]);
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Valerius attack timer error: " + ex.getMessage());
            stopValeriusAttackAnimation();
        }
    });
    valeriusAttackAnimationTimer.start();
    valeriusLargePortraitLabel.setIcon(valeriusAttackFrames[0]);
    valeriusAttackAnimationPlaying = true;
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
            if (!(currentEnemy instanceof Valerius)) return;
            enemyValeriusAttackFrameCounter++;
            int frameTicks = VALERIUS_ATTACK_FRAME_DURATIONS[currentEnemyValeriusAttackFrame];
            if (enemyValeriusAttackFrameCounter >= frameTicks) {
                currentEnemyValeriusAttackFrame++;
                enemyValeriusAttackFrameCounter = 0;
                if (currentEnemyValeriusAttackFrame >= enemyValeriusAttackFrames.length) {
                    enemyValeriusAttackAnimationPlaying = false;
                    stopEnemyValeriusAttackAnimation();
                    startEnemyValeriusIdleAnimation();
                    return;
                }
                if (enemyValeriusAttackFrames[currentEnemyValeriusAttackFrame] != null) {
                    enemyValeriusLargePortraitLabel.setIcon(enemyValeriusAttackFrames[currentEnemyValeriusAttackFrame]);
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Enemy Valerius attack timer error: " + ex.getMessage());
            stopEnemyValeriusAttackAnimation();
        }
    });
    enemyValeriusAttackAnimationTimer.start();
    enemyValeriusLargePortraitLabel.setIcon(enemyValeriusAttackFrames[0]);
    enemyValeriusAttackAnimationPlaying = true;
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

private void showValeriusAttackAnimation() {
    System.out.println("⚔️ showValeriusAttackAnimation called!");
    if (valeriusAttackAnimationPlaying) {
        System.out.println("⏭️ Valerius attack animation already playing, skipping...");
        return;
    }
    if (playerCharacter instanceof Valerius && valeriusLargePortraitLabel != null) {
        if (valeriusAttackFrames[0] != null) {
            startValeriusAttackAnimation();
        } else {
            System.out.println("⚠️ Valerius attack frames not loaded, skipping attack animation");
            valeriusAttackAnimationPlaying = false;
        }
    }
}

private void showEnemyValeriusAttackAnimation() {
    System.out.println("⚔️ showEnemyValeriusAttackAnimation called!");
    if (enemyValeriusAttackAnimationPlaying) {
        System.out.println("⏭️ Enemy Valerius attack animation already playing, skipping...");
        return;
    }
    if (currentEnemy instanceof Valerius && enemyValeriusLargePortraitLabel != null) {
        if (enemyValeriusAttackFrames[0] != null) {
            startEnemyValeriusAttackAnimation();
        } else {
            System.out.println("⚠️ Enemy Valerius attack frames not loaded, skipping attack animation");
            enemyValeriusAttackAnimationPlaying = false;
        }
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
                // Flip horizontally to face toward player
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
                int targetW = 250;
                int targetH = 200;
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
                int targetW = 250;
                int targetH = 200;
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
                int targetW = 250;
                int targetH = 200;
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
        System.out.println("🔍 Loading portrait for: " + name);

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

        // Default for other characters
        String[] possiblePaths = {"assets/" + name + ".png", "assets/" + name + "_idle1.png", "assets/" + name + ".gif", "assets/" + name + "_idle.gif"};
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
        if (frame.getContentPane() instanceof WaveBackgroundPanel) {
            ((WaveBackgroundPanel) frame.getContentPane()).triggerShake(15);
        }
    if (playerCharacter instanceof Jiji && result == ShotResult.SUNK) {
        showJijiAttackAnimation();
    }
    if (playerCharacter instanceof Kael && result == ShotResult.SUNK) {
        showKaelAttackAnimation();
    }
    if (playerCharacter instanceof Valerius && result == ShotResult.SUNK) {
        showValeriusAttackAnimation();
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
        showEnemyValeriusAttackAnimation();
    }
    } else {
        updateStatusLabel("💧 Miss...", Color.CYAN);
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
                break;
            case SUNK:
                updateStatusLabel("💀 YOUR SHIP WAS SUNK!", Color.RED);
                if (currentEnemy instanceof Jiji) {
                    showEnemyJijiAttackAnimation();
                }
                if (currentEnemy instanceof Kael) {
                    showEnemyKaelAttackAnimation();
                }
                if (playerCharacter instanceof Kael) {
                    startKaelDamagedAnimation();
                }
                break;
            case MISS:
                updateStatusLabel("😅 Enemy missed! Lucky break!", Color.GREEN);
                break;
            default:
                break;
        }
        
        refreshBoardsOnly();
        
        if (playerBoard.allShipsSunk()) {
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