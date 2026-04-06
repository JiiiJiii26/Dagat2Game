package campaign;

import javax.swing.*;
import javax.swing.border.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.awt.*;

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

public class CampaignMode {
   
    private boolean testMode = false;  
    private String testEnemyName = "Skye";

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
    private java.util.function.BiConsumer<Integer, Integer> currentSeleneCrescentCallback;
    
    private Timer seleneUpdateTimer; 

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
    private Timer waveTimer;
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
    }
        
        
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
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
        
        g2d.dispose();
    }
    
    public void stopAnimation() {
        if (waveTimer != null) {
            waveTimer.stop();
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
            }
        } else {
            System.out.println("🤖 Enemy decides to just attack normally.");
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
                    int x = enemyRandom.nextInt(10);
                    int y = enemyRandom.nextInt(10);
                    boolean horizontal = enemyRandom.nextBoolean();
                    int destroyed = enemyKael.useShadowBlade(playerBoard, x, y, horizontal);
                    if (destroyed > 0) {
                        showEnemySkillMessage("Kael's shadow blade destroyed " + destroyed + " cells!");
                    }
                }
                break;
            case 2: 
                if (enemyKael.hasEnoughEnergy(200)) {
                    int x = enemyRandom.nextInt(8);
                    int y = enemyRandom.nextInt(8);
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
            Main.showMainMenu();
        }
    });
    topPanel.add(backButton, BorderLayout.WEST);
    
    turnTimer = new TimerPanel(10, () -> {
        System.out.println("⏰ TIME'S UP! Auto-ending turn...");
        updateStatusLabel("⏰ TIME'S UP! Auto-ending turn...", Color.RED);
        endTurn();
    });
    topPanel.add(turnTimer, BorderLayout.CENTER);
    
    waveLabel = new JLabel(String.format("⚔️ WAVE %d/%d - VS %s ⚔️", 
        currentWaveIndex + 1, waves.size(), currentEnemy.getName()));
    waveLabel.setFont(new Font("Arial", Font.BOLD, 18));
    waveLabel.setForeground(Color.YELLOW);
    topPanel.add(waveLabel, BorderLayout.EAST);
    
    JPanel mainContentPanel = new JPanel(new BorderLayout());
    mainContentPanel.setOpaque(false);
    mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    
    
    
    
    JPanel boardsWrapper = new JPanel(new GridBagLayout());
    boardsWrapper.setOpaque(false);
    
    JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
    boardsPanel.setOpaque(false);
    
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.setOpaque(false);
    leftPanel.setBackground(new Color(0, 50, 0, 120));
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
    JLabel charNameLabel = new JLabel(getCharacterEmoji(playerCharacter) + " " + playerCharacter.getName());
    charNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
    charNameLabel.setForeground(Color.CYAN);
    charInfoPanel.add(charNameLabel);
    leftPanel.add(charInfoPanel, BorderLayout.NORTH);
    
    leftPanel.add(playerBoardPanel, BorderLayout.CENTER);
    
    playerShipLabel = new JLabel(getShipCountText(playerBoard), SwingConstants.CENTER);
    playerShipLabel.setFont(new Font("Arial", Font.BOLD, 12));
    playerShipLabel.setForeground(Color.WHITE);
    leftPanel.add(playerShipLabel, BorderLayout.SOUTH);
    
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.setOpaque(false);
    rightPanel.setBackground(new Color(0, 50, 0, 120));
    rightPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(255, 0, 0, 150), 2),
        "🏴‍☠️ ENEMY WATERS",
        TitledBorder.CENTER,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 16),
        new Color(255, 0, 0, 200)
    ));
    
    JPanel enemyCharPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
    enemyCharPanel.setOpaque(false);
    JLabel enemyNameLabel = new JLabel(getCharacterEmoji(currentEnemy) + " " + currentEnemy.getName());
    enemyNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
    enemyNameLabel.setForeground(Color.ORANGE);
    enemyCharPanel.add(enemyNameLabel);
    rightPanel.add(enemyCharPanel, BorderLayout.NORTH);
    
    rightPanel.add(enemyBoardPanel, BorderLayout.CENTER);
    
    enemyShipLabel = new JLabel(getShipCountText(enemyBoard), SwingConstants.CENTER);
    enemyShipLabel.setFont(new Font("Arial", Font.BOLD, 12));
    enemyShipLabel.setForeground(Color.WHITE);
    rightPanel.add(enemyShipLabel, BorderLayout.SOUTH);
    
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

    boardsWrapper.add(boardsPanel, gbc);
    
    
    mainContentPanel.add(boardsWrapper, BorderLayout.CENTER);
    
    
    
    currentSkillPanel = new SkillPanel(playerCharacter);
    currentSkillPanel.setBoards(playerBoardPanel, enemyBoardPanel);
    currentSkillPanel.setPreferredSize(new Dimension(350, 280));
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
                    if (turnTimer != null && timerEnabled) turnTimer.startTimer();
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
    
    mainContentPanel.add(currentSkillPanel, BorderLayout.SOUTH);
    
    JPanel bottomPanel = new JPanel();
    bottomPanel.setOpaque(false);
    bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
    
    statusLabel = new JLabel("YOUR TURN - Click on enemy waters to fire!", SwingConstants.CENTER);
    statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
    statusLabel.setForeground(Color.WHITE);
    statusLabel.setOpaque(true);
    statusLabel.setBackground(new Color(0, 0, 0, 100));
    statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    bottomPanel.add(statusLabel);
    
    contentOverlay.add(topPanel, BorderLayout.NORTH);
    contentOverlay.add(mainContentPanel, BorderLayout.CENTER);
    contentOverlay.add(bottomPanel, BorderLayout.SOUTH);
    backgroundPanel.add(contentOverlay, BorderLayout.CENTER);
    
    frame.setContentPane(backgroundPanel);
    frame.revalidate();
    frame.repaint();
    
    if (playerTurn && timerEnabled && turnTimer != null) {
        turnTimer.startTimer();
    }
    
    System.out.println("✅ Battle UI created with Timer!");
}
private void executeSkill(int targetX, int targetY) {
    System.out.println("Executing skill: " + currentSkillName + " at (" + targetX + "," + targetY + ")");
    boolean success = false;
    boolean shouldEndTurn = true;
    
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
                System.out.println("Using System Overload");
                success = jiji.useSystemOverload(enemyBoard);
                shouldEndTurn = true;
                break;
        }
    } else if (playerCharacter instanceof Kael) {
        Kael kael = (Kael) playerCharacter;
        switch(currentSkillNumber) {
            case 1:
                System.out.println("⚠️ Shadow Step should not be executed through executeSkill!");
                updateStatusLabel("Shadow Step requires clicking on your ships - use the skill button then click on your ships!", Color.YELLOW);
                waitingForSkillTarget = false;
                currentSkillNumber = 0;
                currentSkillName = "";
                return;
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
                }
                break;
            case 3:
                System.out.println("Using Fortress Mode");
                success = valerius.useFortressMode();
                shouldEndTurn = true;
                break;
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
        }
    } else if (playerCharacter instanceof Aeris) {
        Aeris aeris = (Aeris) playerCharacter;
        switch(currentSkillNumber) {
            case 1:
                System.out.println("Using Adaptive Instinct");
                success = aeris.useAdaptiveInstinct(playerBoard, -1);
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
        }
    }
    
    if (success) {
        updateStatusLabel("✨ " + currentSkillName + " used successfully!", Color.GREEN);
        refreshBoardsOnly();
        updateShipCounters();  
        if (currentSkillPanel != null) {
            currentSkillPanel.updateUI();
        }
        
        if (currentSkillName.equals("Laser Pointer")) {
            updateStatusLabel("🔴 Enemy will skip their next turn! You get another turn!", Color.GREEN);
        }
        
        if (shouldEndTurn) {
            playerTurn = false;
            Timer timer = new Timer(1200, e -> enemyTurn());
            timer.setRepeats(false);
            timer.start();
        } else {
            refreshUI();
            updateStatusLabel("YOUR TURN - You get another action!", Color.GREEN);
            
            if (turnTimer != null && timerEnabled) {
                turnTimer.startTimer();
            }
        }
    } else {
        updateStatusLabel("❌ Failed to use " + currentSkillName + "! Check mana/cooldown.", Color.RED);
        
        if (turnTimer != null && timerEnabled && playerTurn) {
            turnTimer.startTimer();
        }
    }
    
    waitingForSkillTarget = false;
    currentSkillNumber = 0;
    currentSkillName = "";
    currentSkillTargetsOwnBoard = false;
    currentSkillRequiresDirection = false;
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
                if (turnTimer != null) turnTimer.stopTimer();
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
        System.out.println("Enemy board clicked at: " + row + "," + col);
        
        if (waitingForSkillTarget && !currentSkillTargetsOwnBoard) {
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
            if (turnTimer != null) turnTimer.stopTimer();
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
    updateStatusLabel(result == ShotResult.HIT ? "💥 HIT! Enemy ship damaged!" : "💧 Miss...", 
                      result == ShotResult.HIT ? Color.GREEN : Color.CYAN);
    
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
                onPlayerTurnStart();
                
                
                if (currentSkillPanel != null) {
                    currentSkillPanel.updateUI();
                }
                return;
            }
        }

        int x = random.nextInt(10);
        int y = random.nextInt(10);
        
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
        
        ShotResult result = playerBoard.fire(x, y);
        
        if (playerCharacter instanceof Jiji) {
            Jiji jiji = (Jiji) playerCharacter;
            if (jiji.checkFirewall(x, y, result)) {
                updateStatusLabel("🛡️ FIREWALL blocked the enemy shot!", Color.CYAN);
            } else {
                playerBoardPanel.updateCell(x, y, result);
            }
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
                turnTimer.startTimer();
            }
            return;
        }
        
        
        cancelAllSkillTargeting();
        updateStatusLabel("Skill cancelled. Ending turn...", Color.ORANGE);
    }
    
    
    playerTurn = false;
    
    
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