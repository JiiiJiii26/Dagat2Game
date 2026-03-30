package campaign;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.border.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.Component;


import characters.*;
import gui.BoardPanel;
import gui.PlacementPanel;
import models.Board;
import models.Cell;
import models.Ship;
import game.ShotResult;
import main.Main;

public class CampaignMode {
   
    
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
    
private boolean waitingForTarget = false;
private String currentSkillName = "";
private SkillTargetCallback targetCallback;
private JButton lastClickedSkillButton;
private int extraTurnsRemaining = 0;

  
private boolean waitingForKaelStepSource = false;
private boolean waitingForKaelStepDestination = false;
private int[] stepSourceCoordinates = new int[2];


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
        } 
    } else {
        System.out.println("🤖 Enemy decides to just attack normally.");
    }
}

private void healPlayerShips() {
    System.out.println("🏥 Healing player ships between waves...");
    
    
    for (Ship ship : playerBoard.getShips()) {
        ship.heal();
    }
    
    
    for (int i = 0; i < 10; i++) {
        for (int j = 0; j < 10; j++) {
            Cell cell = playerBoard.getCell(i, j);
            cell.resetFiredUpon();
        }
    }
    
    
    refreshUI();
    
    System.out.println("✅ Player ships healed!");
}
private void useJijiEnemySkill() {
    Jiji enemyJiji = (Jiji) currentEnemy;
    
    
    int skillChoice = enemyRandom.nextInt(3);
    
    switch(skillChoice) {
        case 0: 
            if (enemyJiji.hasEnoughMana(50)) {
                System.out.println("🔓 Enemy Jiji uses DATA LEECH!");
                enemyJiji.useDataLeech(playerBoard);
                showEnemySkillMessage("Jiji uses Data Leech on your fleet!");
            }
            break;
        case 1: 
            if (enemyJiji.hasEnoughMana(120)) {
                System.out.println("⚡ Enemy Jiji uses OVERCLOCK!");
                enemyJiji.useOverclock();
                showEnemySkillMessage("Jiji overclocks! Their next shot will fire twice!");
            }
            break;
        case 2: 
            if (enemyJiji.hasEnoughMana(400)) {
                System.out.println("💻 Enemy Jiji uses SYSTEM OVERLOAD!");
                enemyJiji.useSystemOverload(playerBoard);
                showEnemySkillMessage("Jiji overloads your systems! One of your ships is damaged!");
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
                System.out.println("🌑 Enemy Kael uses SHADOW STEP!");
                showEnemySkillMessage("Kael teleports one of their ships!");
            }
            break;
        case 1: 
            if (enemyKael.hasEnoughEnergy(150)) {
                System.out.println("⚔️ Enemy Kael uses SHADOW BLADE!");
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
                System.out.println("🌑🌑🌑 Enemy Kael uses SHADOW DOMAIN!");
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
                System.out.println("📡 Enemy Valerius uses RADAR OVERLOAD!");
                enemyValerius.useRadarOverload();
                showEnemySkillMessage("Valerius jams your radar! Your skills are disabled for 2 turns!");
            }
            break;
        case 1: 
            if (enemyValerius.hasEnoughMana(120)) {
                System.out.println("🎯 Enemy Valerius uses PRECISION STRIKE!");
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
        System.out.println("🏰 Enemy Valerius uses FORTRESS MODE!");
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
                System.out.println("🐱 Enemy Skye uses CAT SWARM!");
                enemySkye.useCatSwarm(playerBoard);
                showEnemySkillMessage("Skye summons a swarm of cats! Your ships are scattered!");
            }
            break;
        case 1: 
            if (enemySkye.hasEnoughMana(50)) {
                System.out.println("🔴 Enemy Skye uses LASER POINTER!");
                enemySkye.useLaserPointer();
                
                showEnemySkillMessage("Skye distracts you with a laser pointer!");
            }
            break;
        case 2: 
            if (enemySkye.hasEnoughMana(380)) {
                System.out.println("🌿 Enemy Skye uses CATNIP EXPLOSION!");
                int x = enemyRandom.nextInt(8);
                int y = enemyRandom.nextInt(8);
                enemySkye.useCatnipExplosion(playerBoard, x, y);
                showEnemySkillMessage("Skye detonates a catnip bomb near your fleet!");
            }
            break;
    }
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
    }if(!playerCharacter.getName().equals(flue.getName())) {
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
            "🌊 Digital Storm",
            "⚡ Thunderous Assault",
            "🌫️ Misty Encounter",
            "🔥 Burning Tides",
            "❄️ Frozen Depths",
            "💀 Shadow Strike",
            "🌀 Maelstrom",
            "⚓ Naval Clash",
            "🌪️ Tempest Fury",
            "🛡️ Iron Wall"
        };
        
        String[] difficulties = {
            "Skirmish",
            "Encounter",
            "Battle",
            "Clash",
            "Confrontation",
            "Assault",
            "Siege",
            "War"
        };
        
        String randomTitle = titles[random.nextInt(titles.length)];
        String randomDifficulty = difficulties[random.nextInt(difficulties.length)];
        
        return "🌊 WAVE " + waveNumber + ": " + randomTitle + " - " + randomDifficulty;
    }
    
    private Color getRandomWaveColor() {
        Color[] colors = {
            new Color(255, 99, 71),   
            new Color(255, 215, 0),    
            new Color(50, 205, 50),    
            new Color(30, 144, 255),   
            new Color(186, 85, 211),   
            new Color(255, 140, 0),    
            new Color(0, 255, 127),    
            new Color(255, 105, 180)   
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
    
    
    String waveMessage = String.format("🌊 WAVE %d/%d - VS %s", 
        index + 1, waves.size(), currentEnemy.getName());
    updateStatusLabel(waveMessage, Color.YELLOW);
    
    adjustEnemyDifficulty(index + 1);
    placeEnemyShips(currentEnemy, enemyBoard);
    createBattleUI(wave);
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
        
        if (enemy instanceof Jiji) {
            placeRandomShips(board);
        } else if (enemy instanceof Kael) {
            placeRandomShips(board);
        } else if (enemy instanceof Valerius) {
            placeRandomShips(board);
        } else if (enemy instanceof Skye) {
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
    
    private void placeKaelShips(Board board) {
        
        board.placeShip(new Ship("Carrier", 5), 0, 0, true);
        board.placeShip(new Ship("Battleship", 4), 9, 0, false);
        board.placeShip(new Ship("Cruiser", 3), 0, 9, true);
        board.placeShip(new Ship("Submarine", 3), 9, 9, false);
        board.placeShip(new Ship("Destroyer", 2), 4, 4, true);
    }
    
    private void placeValeriusShips(Board board) {
        
        board.placeShip(new Ship("Carrier", 5), 2, 2, true);
        board.placeShip(new Ship("Battleship", 4), 2, 4, true);
        board.placeShip(new Ship("Cruiser", 3), 4, 2, false);
        board.placeShip(new Ship("Submarine", 3), 5, 5, true);
        board.placeShip(new Ship("Destroyer", 2), 7, 7, false);
    }
    
   private void createBattleUI(CampaignWave wave) {
    frame.getContentPane().removeAll();
    frame.setLayout(new BorderLayout());
    
    
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBackground(new Color(25, 25, 112));
    topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    String waveInfo = String.format("%s - Wave %d/%d", 
        wave.title, currentWaveIndex + 1, waves.size());
    
    waveLabel = new JLabel(waveInfo, SwingConstants.CENTER);
    waveLabel.setFont(new Font("Arial", Font.BOLD, 20));
    waveLabel.setForeground(wave.waveColor);
    topPanel.add(waveLabel, BorderLayout.CENTER);
    
    
    JPanel battlePanel = new JPanel(new GridBagLayout());
    battlePanel.setBackground(new Color(25, 25, 112));
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1.0;
    
    
    gbc.gridx = 0;
    gbc.weightx = 0.2;
    battlePanel.add(createPlayerCharacterPanel(), gbc);
    
    
    gbc.gridx = 1;
    gbc.weightx = 0.6;
    battlePanel.add(createBoardsPanel(), gbc);
    
    
    gbc.gridx = 2;
    gbc.weightx = 0.2;
    battlePanel.add(createEnemyCharacterPanel(wave), gbc);
    
    
    JPanel statusPanel = new JPanel();
    statusPanel.setBackground(new Color(25, 25, 112));
    statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
    
    
    statusLabel = new JLabel("YOUR TURN - Click on enemy waters to fire!", SwingConstants.CENTER);
    statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
    statusLabel.setForeground(Color.WHITE);
    statusPanel.add(statusLabel);
    
    
    frame.add(topPanel, BorderLayout.NORTH);
    frame.add(battlePanel, BorderLayout.CENTER);
    frame.add(statusPanel, BorderLayout.SOUTH);
    
    frame.revalidate();
    frame.repaint();
    
    
    System.out.println("✅ statusLabel created: " + (statusLabel != null));

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
    
    
    JPanel skillsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
    skillsPanel.setBackground(new Color(0, 50, 0));
    skillsPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GREEN),
        "ABILITIES",
        TitledBorder.CENTER,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 10),
        Color.GREEN
    ));
    
    if (playerCharacter instanceof Aeris) {
        ((Aeris) playerCharacter).setPlayerBoard(playerBoard);
    }if (playerCharacter instanceof Kael) {
    ((Kael) playerCharacter).setPlayerBoard(playerBoard);
}
if (playerCharacter instanceof Valerius) {
    ((Valerius) playerCharacter).setPlayerBoard(playerBoard);
}


    if (playerCharacter instanceof Jiji) {
        addJijiSkills(skillsPanel, true);
    } else if (playerCharacter instanceof Kael) {
        addKaelSkills(skillsPanel, true);  
    } else if (playerCharacter instanceof Valerius) {
        addValeriusSkills(skillsPanel, true);
    } else if (playerCharacter instanceof Skye) {
        addSkyeSkills(skillsPanel, true);
    }else if (playerCharacter instanceof Morgana) {
        addMorganaSkills(skillsPanel, true);  
    }else if (playerCharacter instanceof Aeris) {
        addAerisSkills(skillsPanel, true); 
    }else if (playerCharacter instanceof Selene) {
        addSeleneSkills(skillsPanel, true); 
    }else if(playerCharacter instanceof Flue) {
        addFlueSkills(skillsPanel, true); 
    }
    
    panel.add(skillsPanel, BorderLayout.WEST);
    
    
    
    JPanel shipCounterPanel = createShipCounterPanel(true);
    panel.add(shipCounterPanel, BorderLayout.SOUTH);
    
    return panel;
}
private void addFlueSkills(JPanel panel, boolean isPlayer) {
    Flue flue = (Flue) playerCharacter;
    
    
    JButton corruptionBtn = new JButton("💻 Corruption.EXE (45)");
    corruptionBtn.setBackground(new Color(0, 255, 127));
    corruptionBtn.setForeground(Color.BLACK);
    corruptionBtn.setToolTipText("Deals damage and silences enemy skills for 2 turns");
    corruptionBtn.setFont(new Font("Arial", Font.BOLD, 11));
    corruptionBtn.setFocusPainted(false);
    
    String status1 = flue.getSkillStatus(1);
    if (!status1.equals("Ready!")) {
        corruptionBtn.setEnabled(false);
        corruptionBtn.setText("💻 Corruption.EXE (" + status1 + ")");
    }
    
    corruptionBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            updateStatusLabel("💻 Click on enemy board to corrupt a cell!", Color.YELLOW);
            waitingForTarget = true;
            currentSkillName = "CORRUPTION.EXE";
            targetCallback = (x, y) -> {
                if (x < 0 || x > 9 || y < 0 || y > 9) {
                    updateStatusLabel("❌ Invalid coordinates!", Color.RED);
                    waitingForTarget = false;
                    return;
                }
                
                boolean used = flue.useCorruption(enemyBoard, x, y);
                if (used) {
                    updateStatusLabel("💻 Corruption.EXE executed at (" + x + "," + y + ")!", Color.CYAN);
                    refreshUI();
                } else {
                    updateStatusLabel("❌ Cannot use Corruption.EXE!", Color.RED);
                }
                waitingForTarget = false;
            };
        }
    });
    panel.add(corruptionBtn);
    
    
    JButton fortificationBtn = new JButton("🛡️ Fortification.GRID (80)");
    fortificationBtn.setBackground(new Color(0, 200, 100));
    fortificationBtn.setForeground(Color.BLACK);
    fortificationBtn.setToolTipText("Shields a 2x2 area (50% chance to block damage for 1 turn)");
    fortificationBtn.setFont(new Font("Arial", Font.BOLD, 11));
    fortificationBtn.setFocusPainted(false);
    
    String status2 = flue.getSkillStatus(2);
    if (!status2.equals("Ready!")) {
        fortificationBtn.setEnabled(false);
        fortificationBtn.setText("🛡️ Fortification.GRID (" + status2 + ")");
    }
    
    fortificationBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            updateStatusLabel("🛡️ Click on YOUR board to place fortification grid!", Color.YELLOW);
            waitingForAerisShield = true; 
            currentAerisShieldCallback = (x, y) -> {
                if (x < 0 || x > 9 || y < 0 || y > 9) {
                    updateStatusLabel("❌ Invalid coordinates!", Color.RED);
                    waitingForAerisShield = false;
                    return;
                }
                
                boolean used = flue.useFortification(playerBoard, x, y);
                if (used) {
                    updateStatusLabel("🛡️ Fortification grid deployed at (" + x + "," + y + ")!", Color.CYAN);
                    refreshUI();
                } else {
                    updateStatusLabel("❌ Cannot place fortification grid!", Color.RED);
                }
                waitingForAerisShield = false;
            };
        }
    });
    panel.add(fortificationBtn);
    
    
    JButton kernelBtn = new JButton("💀 Kernel.Decimation.REQ (300)");
    kernelBtn.setBackground(new Color(200, 0, 0));
    kernelBtn.setForeground(Color.WHITE);
    kernelBtn.setToolTipText("Massive damage + permanent debuff (-10% damage/healing/accuracy)");
    kernelBtn.setFont(new Font("Arial", Font.BOLD, 11));
    kernelBtn.setFocusPainted(false);
    
    String status3 = flue.getSkillStatus(3);
    if (!status3.equals("Ready!")) {
        kernelBtn.setEnabled(false);
        kernelBtn.setText("💀 Kernel.Decimation.REQ (" + status3 + ")");
    }
    
    kernelBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            updateStatusLabel("💀 Click on enemy board to decimate a cell!", Color.YELLOW);
            waitingForTarget = true;
            currentSkillName = "KERNEL.DECIMATION.REQ";
            targetCallback = (x, y) -> {
                if (x < 0 || x > 9 || y < 0 || y > 9) {
                    updateStatusLabel("❌ Invalid coordinates!", Color.RED);
                    waitingForTarget = false;
                    return;
                }
                
                boolean used = flue.useKernelDecimation(enemyBoard, x, y);
                if (used) {
                    updateStatusLabel("💀 Kernel.Decimation executed at (" + x + "," + y + ")!", Color.ORANGE);
                    refreshUI();
                } else {
                    updateStatusLabel("❌ Cannot use Kernel.Decimation!", Color.RED);
                }
                waitingForTarget = false;
            };
        }
    });
    panel.add(kernelBtn);
    
    
    if (flue.getSilencedShipsCount() > 0) {
        JLabel silencedLabel = new JLabel("🔇 " + flue.getSilencedShipsCount() + " ship(s) silenced", SwingConstants.CENTER);
        silencedLabel.setForeground(Color.RED);
        silencedLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(silencedLabel);
    }
    
    if (flue.getFortifiedCellsCount() > 0) {
        JLabel fortifiedLabel = new JLabel("🛡️ " + flue.getFortifiedCellsCount() + " fortified cells", SwingConstants.CENTER);
        fortifiedLabel.setForeground(Color.CYAN);
        fortifiedLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(fortifiedLabel);
    }
    
    if (flue.getDebuffedShipsCount() > 0) {
        JLabel debuffedLabel = new JLabel("💀 " + flue.getDebuffedShipsCount() + " debuffed ships", SwingConstants.CENTER);
        debuffedLabel.setForeground(Color.MAGENTA);
        debuffedLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(debuffedLabel);
    }
    
    if (flue.isLoneResolveActive()) {
        JLabel loneLabel = new JLabel("🔧 Lone.Resolve ACTIVE (15% DR)", SwingConstants.CENTER);
        loneLabel.setForeground(Color.YELLOW);
        loneLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(loneLabel);
    }
    
    
    JLabel manaLabel = new JLabel(flue.getManaBar(), SwingConstants.CENTER);
    manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
    manaLabel.setForeground(Color.CYAN);
    panel.add(manaLabel);
}
private void addSeleneSkills(JPanel panel, boolean isPlayer) {
    Selene selene = (Selene) playerCharacter;  
    
    
    JButton lunarBtn = new JButton("🔮 Lunar Vision (60)");
    lunarBtn.setBackground(new Color(200, 150, 255));
    lunarBtn.setForeground(Color.BLACK);
    lunarBtn.setToolTipText("Reveals all ships in a 3x3 area");
    
    lunarBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            updateStatusLabel("🔮 Click on enemy board to reveal 3x3 area!", Color.YELLOW);
            waitingForSeleneVision = true;
            currentSeleneVisionCallback = (x, y) -> {
                if (x < 0 || x > 9 || y < 0 || y > 9) {
                    updateStatusLabel("❌ Invalid coordinates!", Color.RED);
                    waitingForSeleneVision = false;
                    return;
                }
                
                
                boolean used = selene.useLunarVision(enemyBoard, x, y);
                if (used) {
                    updateStatusLabel("🔮 Lunar Vision revealed area around (" + x + "," + y + ")!", Color.CYAN);
                    refreshUI();
                } else {
                    updateStatusLabel("❌ Cannot use Lunar Vision!", Color.RED);
                }
                waitingForSeleneVision = false;
            };
        }
    });
    panel.add(lunarBtn);
    
    
    JButton eclipseBtn = new JButton("🌑 Eclipse Binding (150)");
eclipseBtn.setBackground(new Color(100, 50, 150));
eclipseBtn.setForeground(Color.WHITE);
eclipseBtn.setToolTipText("Grants 2 extra turns!");
eclipseBtn.setFont(new Font("Arial", Font.BOLD, 11));
eclipseBtn.setFocusPainted(false);

String status2 = selene.getSkillStatus(2);
if (!status2.equals("Ready!")) {
    eclipseBtn.setEnabled(false);
    eclipseBtn.setText("🌑 Eclipse Binding (" + status2 + ")");
}

eclipseBtn.addActionListener(e -> {
    if (isPlayer && playerTurn) {
        boolean used = selene.useEclipseBinding();  
        if (used) {
            updateStatusLabel("🌑 Eclipse Binding! You gain 2 EXTRA TURNS!", Color.MAGENTA);
            refreshUI();
        } else {
            updateStatusLabel("❌ Cannot use Eclipse Binding! " + selene.getSkillStatus(2), Color.RED);
        }
    }
});
panel.add(eclipseBtn);
  
    
    
    JButton crescentBtn = new JButton("🌙 Crescent Blade (400)");
    crescentBtn.setBackground(new Color(150, 100, 200));
    crescentBtn.setForeground(Color.WHITE);
    crescentBtn.setToolTipText("Hits a cross pattern (center + up, down, left, right)");
    crescentBtn.setFont(new Font("Arial", Font.BOLD, 11));
    crescentBtn.setFocusPainted(false);
    
    String status3 = selene.getSkillStatus(3);
    if (!status3.equals("Ready!")) {
        crescentBtn.setEnabled(false);
        crescentBtn.setText("🌙 Crescent Blade (" + status3 + ")");
    }
    
    crescentBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            
            updateStatusLabel("🌙 Click on enemy board to strike a cross pattern!", Color.YELLOW);
            waitingForSeleneCrescent = true;
            currentSeleneCrescentCallback = (x, y) -> {
                if (x < 0 || x > 9 || y < 0 || y > 9) {
                    updateStatusLabel("❌ Invalid coordinates!", Color.RED);
                    waitingForSeleneCrescent = false;
                    return;
                }
                
                int damage = selene.useCrescentBlade(enemyBoard, x, y);
                if (damage > 0) {
                    updateStatusLabel("🌙 Crescent Blade dealt " + damage + " damage!", Color.ORANGE);
                    refreshUI();
                } else {
                    updateStatusLabel("❌ Cannot use Crescent Blade!", Color.RED);
                }
                waitingForSeleneCrescent = false;
            };
        }
    });
    panel.add(crescentBtn);
    
    
    if (selene.areEnemyShipsTrapped()) {
        JLabel trappedLabel = new JLabel("🌑 Enemy Ships BOUND", SwingConstants.CENTER);
        trappedLabel.setForeground(Color.MAGENTA);
        trappedLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(trappedLabel);
    }
    
    if (selene.isNightTime()) {
        JLabel nightLabel = new JLabel("🌙 MOON'S BLESSING ACTIVE", SwingConstants.CENTER);
        nightLabel.setForeground(Color.YELLOW);
        nightLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(nightLabel);
    } else {
        JLabel nightCountdown = new JLabel("🌙 Night in " + selene.getTurnsUntilNight() + " turns", SwingConstants.CENTER);
        nightCountdown.setForeground(Color.GRAY);
        nightCountdown.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(nightCountdown);
    }
    
    if (selene.getTrappedCellsCount() > 0) {
        JLabel boundLabel = new JLabel("🌑 " + selene.getTrappedCellsCount() + " cells bound", SwingConstants.CENTER);
        boundLabel.setForeground(new Color(150, 100, 200));
        boundLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(boundLabel);
    }
    
    
    JLabel manaLabel = new JLabel(selene.getManaBar(), SwingConstants.CENTER);
    manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
    manaLabel.setForeground(Color.CYAN);
    panel.add(manaLabel);
}
private void addAerisSkills(JPanel panel, boolean isPlayer) {
    Aeris aeris = (Aeris) playerCharacter;
    
    
  


JButton adaptiveBtn = new JButton("🛡️ Adaptive Instinct (120)");
adaptiveBtn.setBackground(new Color(255, 215, 0));
adaptiveBtn.setForeground(Color.BLACK);
adaptiveBtn.setToolTipText("Click on YOUR ship to shield it for 2 turns (turns blue, immune to damage)");

String status1 = aeris.getSkillStatus(1);
if (!status1.equals("Ready!")) {
    adaptiveBtn.setEnabled(false);
    adaptiveBtn.setText("🛡️ Adaptive Instinct (" + status1 + ")");
}

adaptiveBtn.addActionListener(e -> {
    if (isPlayer && playerTurn) {
        
        if (!aeris.hasEnoughMana(120)) {
            updateStatusLabel("❌ Not enough mana! Need 120 mana.", Color.RED);
            return;
        }
        if (aeris.getSkillStatus(1).contains("Cooldown")) {
            updateStatusLabel("❌ Adaptive Instinct is on cooldown!", Color.RED);
            return;
        }
        
        
        updateStatusLabel("🛡️ Click on YOUR board to select a ship to shield!", Color.YELLOW);
        waitingForAerisShield = true;
     currentAerisShieldCallback = (x, y) -> {
    System.out.println("🛡️ Aeris callback received: (" + x + "," + y + ")");
    
    
    Ship targetShip = null;
    for (Ship ship : playerBoard.getShips()) {
        if (ship.containsCell(x, y)) {
            targetShip = ship;
            System.out.println("🔵 Found ship: " + ship.getName() + " at (" + x + "," + y + ")");
            break;
        }
    }
    
    if (targetShip == null) {
        updateStatusLabel("❌ No ship at that location!", Color.RED);
        waitingForAerisShield = false;
        return;
    }
    
    if (targetShip.isSunk()) {
        updateStatusLabel("❌ This ship is already sunk!", Color.RED);
        waitingForAerisShield = false;
        return;
    }
    
    if (targetShip.isShielded()) {
        updateStatusLabel("❌ " + targetShip.getName() + " is already shielded!", Color.RED);
        waitingForAerisShield = false;
        return;
    }
    
    
    int shipIndex = -1;
    ArrayList<Ship> ships = playerBoard.getShips();
    for (int i = 0; i < ships.size(); i++) {
        if (ships.get(i) == targetShip) {
            shipIndex = i;
            break;
        }
    }
    
    boolean used = aeris.useAdaptiveInstinct(playerBoard, shipIndex);
    if (used) {
        updateStatusLabel("🔵 " + targetShip.getName() + " is now SHIELDED (blue) for 2 turns!", Color.CYAN);
        playerBoardPanel.refreshColors();
        refreshUI();
    } else {
        updateStatusLabel("❌ Failed to shield " + targetShip.getName() + "!", Color.RED);
    }
    waitingForAerisShield = false;
};
    }
});
panel.add(adaptiveBtn);
    
    
   JButton overdriveBtn = new JButton("⚡ Multitask Overdrive");
overdriveBtn.setBackground(new Color(100, 200, 255));
overdriveBtn.setForeground(Color.BLACK);
overdriveBtn.setToolTipText("Restores 200 mana. 3 turn cooldown.");
overdriveBtn.setFont(new Font("Arial", Font.BOLD, 11));
overdriveBtn.setFocusPainted(false);

String status2 = aeris.getSkillStatus(2);
if (!status2.equals("Ready! (Restores 200 mana)")) {
    overdriveBtn.setEnabled(false);
    overdriveBtn.setText("⚡ Multitask Overdrive (" + status2 + ")");
} else {
    overdriveBtn.setText("⚡ Multitask Overdrive (+200 mana)");
}

overdriveBtn.addActionListener(e -> {
    if (isPlayer && playerTurn) {
        
        if (aeris.getSkillStatus(2).contains("Cooldown")) {
            updateStatusLabel("❌ Multitask Overdrive is on cooldown!", Color.RED);
            return;
        }
        
        boolean used = aeris.useMultitaskOverdrive();
        if (used) {
            updateStatusLabel("⚡ Multitask Overdrive! Restored 200 mana!", Color.GREEN);
            refreshUI();  
        } else {
            updateStatusLabel("❌ Cannot use Multitask Overdrive!", Color.RED);
        }
    }
});
panel.add(overdriveBtn);
    
    
   
JButton relentlessBtn = new JButton("⚔️ Relentless Ascent (500)");
relentlessBtn.setBackground(new Color(200, 100, 0));
relentlessBtn.setForeground(Color.WHITE);
relentlessBtn.setToolTipText("Destroys an entire column! Bonus cells destroyed when low on HP.");
relentlessBtn.setFont(new Font("Arial", Font.BOLD, 11));
relentlessBtn.setFocusPainted(false);

String status3 = aeris.getSkillStatus(3);
if (!status3.equals("Ready!")) {
    relentlessBtn.setEnabled(false);
    relentlessBtn.setText("⚔️ Relentless Ascent (" + status3 + ")");
}

relentlessBtn.addActionListener(e -> {
    if (isPlayer && playerTurn) {
        updateStatusLabel("⚔️ Click on enemy board to select a column to destroy!", Color.YELLOW);
        
        waitingForTarget = true;
        currentSkillName = "RELENTLESS ASCENT";
        targetCallback = (x, y) -> {
            
            if (y < 0 || y > 9) {
                updateStatusLabel("❌ Invalid column!", Color.RED);
                waitingForTarget = false;
                return;
            }
            
            int cellsDestroyed = aeris.useRelentlessAscent(enemyBoard, y);
            if (cellsDestroyed > 0) {
                updateStatusLabel("⚔️ Relentless Ascent destroyed " + cellsDestroyed + " cells in column " + y + "!", Color.ORANGE);
                refreshUI();
            } else {
                updateStatusLabel("❌ Cannot use Relentless Ascent!", Color.RED);
            }
            waitingForTarget = false;
        };
    }
});
panel.add(relentlessBtn);
    
    
    
    
    if (aeris.isStunImmuneActive()) {
        JLabel stunLabel = new JLabel("⚡ Stun Immune ACTIVE", SwingConstants.CENTER);
        stunLabel.setForeground(Color.YELLOW);
        stunLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(stunLabel);
    }
    
   
    

    
    
    JLabel manaLabel = new JLabel(aeris.getManaBar(), SwingConstants.CENTER);
    manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
    manaLabel.setForeground(Color.CYAN);
    panel.add(manaLabel);
}
private void addJijiSkills(JPanel panel, boolean isPlayer) {
    Jiji jiji = (Jiji) playerCharacter;
    
    
    JButton dataLeechBtn = new JButton("🔓 Data Leech (50)");
    dataLeechBtn.setBackground(new Color(100, 200, 255));
    dataLeechBtn.setToolTipText("Reveal 2 random cells");
    dataLeechBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
             
            boolean used = jiji.useDataLeech(enemyBoard);
            if (used) {
                JOptionPane.showMessageDialog(frame, 
                    "📡 Data Leech used! Check console for revealed cells!", 
                    "Skill Used", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshUI();
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "❌ Cannot use Data Leech!\n" + jiji.getSkillStatus(1), 
                    "Skill Not Ready", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    });
    
    
    String status1 = jiji.getSkillStatus(1);
    if (!status1.equals("Ready!")) {
        dataLeechBtn.setEnabled(false);
        dataLeechBtn.setText("🔓 Data Leech (" + status1 + ")");
    }
    panel.add(dataLeechBtn);
    
    
    JButton overclockBtn = new JButton("⚡ Overclock (120)");
    overclockBtn.setBackground(new Color(200, 150, 50));
    overclockBtn.setToolTipText("Next shot fires twice");
    overclockBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            boolean used = jiji.useOverclock();
            if (used) {
                JOptionPane.showMessageDialog(frame, 
                    "⚡ Overclock activated! Next shot fires twice!", 
                    "Skill Used", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshUI();
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "❌ Cannot use Overclock!\n" + jiji.getSkillStatus(2), 
                    "Skill Not Ready", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    });
    
    String status2 = jiji.getSkillStatus(2);
    if (!status2.equals("Ready!")) {
        overclockBtn.setEnabled(false);
        overclockBtn.setText("⚡ Overclock (" + status2 + ")");
    }
    panel.add(overclockBtn);
    
    
    JButton systemBtn = new JButton("💻 System Overload (400)");
    systemBtn.setBackground(new Color(200, 50, 50));
    systemBtn.setToolTipText("Disable enemy skill + damage");
    systemBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            boolean used = jiji.useSystemOverload(enemyBoard);
            if (used) {
                JOptionPane.showMessageDialog(frame, 
                    "💻 System Overload! Enemy skill disabled!", 
                    "ULTIMATE!", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshUI();
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "❌ Cannot use System Overload!\n" + jiji.getSkillStatus(3), 
                    "Skill Not Ready", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    });
    
    String status3 = jiji.getSkillStatus(3);
    if (!status3.equals("Ready!")) {
        systemBtn.setEnabled(false);
        systemBtn.setText("💻 System Overload (" + status3 + ")");
    }
    panel.add(systemBtn);
    
    
    JLabel manaLabel = new JLabel(jiji.getManaBar(), SwingConstants.CENTER);
    manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
    manaLabel.setForeground(Color.CYAN);
    panel.add(manaLabel);
    
    
    if (jiji.isFirewallActive()) {
        JLabel firewallLabel = new JLabel("🛡️ FIREWALL ACTIVE", SwingConstants.CENTER);
        firewallLabel.setForeground(Color.GREEN);
        firewallLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(firewallLabel);
    }
    
    if (jiji.isNextShotEnhanced()) {
        JLabel overclockLabel = new JLabel("⚡ OVERCLOCK READY", SwingConstants.CENTER);
        overclockLabel.setForeground(Color.YELLOW);
        overclockLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(overclockLabel);
    }
}

private void addKaelSkills(JPanel panel, boolean isPlayer) {
    Kael kael = (Kael) playerCharacter;
    
    
    JButton stepBtn = new JButton("🌑 Shadow Step (100)");
    stepBtn.setBackground(new Color(75, 0, 130));
    stepBtn.setForeground(Color.WHITE);
    stepBtn.setToolTipText("Click on YOUR ship, then click on destination");
    stepBtn.setFont(new Font("Arial", Font.BOLD, 11));
    stepBtn.setFocusPainted(false);
    
    String status1 = kael.getSkillStatus(1);
    if (!status1.equals("Ready!")) {
        stepBtn.setEnabled(false);
        stepBtn.setText("🌑 Shadow Step (" + status1 + ")");
    }
    
    stepBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            updateStatusLabel("🌑 Click on YOUR ship to teleport!", Color.YELLOW);
            waitingForKaelStepSource = true;
            waitingForKaelStepDestination = false;
        }
    });
    panel.add(stepBtn);
    
    
    JButton bladeBtn = new JButton("⚔️ Shadow Blade (150)");
    bladeBtn.setBackground(new Color(100, 150, 255));
    bladeBtn.setForeground(Color.BLACK);
    bladeBtn.setToolTipText("Choose direction, then click on enemy board");
    bladeBtn.setFont(new Font("Arial", Font.BOLD, 11));
    bladeBtn.setFocusPainted(false);
    
    String status2 = kael.getSkillStatus(2);
    if (!status2.equals("Ready!")) {
        bladeBtn.setEnabled(false);
        bladeBtn.setText("⚔️ Shadow Blade (" + status2 + ")");
    }
    
    bladeBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            String[] options = {"Horizontal (→)", "Vertical (↓)"};
            int choice = JOptionPane.showOptionDialog(frame,
                "⚔️ SHADOW BLADE\n\nChoose cut direction:",
                "Shadow Blade",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
            
            if (choice >= 0) {
                bladeDirectionHorizontal = (choice == 0);
                updateStatusLabel("⚔️ Click on ENEMY board to cut " + 
                    (bladeDirectionHorizontal ? "HORIZONTALLY" : "VERTICALLY") + "!", Color.YELLOW);
                waitingForKaelBlade = true;
                currentKaelBladeCallback = (x, y) -> {
                    int cellsDestroyed = kael.useShadowBlade(enemyBoard, x, y, bladeDirectionHorizontal);
                    if (cellsDestroyed > 0) {
                        updateStatusLabel("⚔️ Shadow Blade destroyed " + cellsDestroyed + " cells!", Color.ORANGE);
                        refreshUI();
                    } else {
                        updateStatusLabel("❌ Cannot use Shadow Blade!", Color.RED);
                    }
                    waitingForKaelBlade = false;
                };
            }
        }
    });
    panel.add(bladeBtn);
    
    
    JButton domainBtn = new JButton("🌑🌑🌑 Shadow Domain (200)");
    domainBtn.setBackground(new Color(50, 0, 100));
    domainBtn.setForeground(Color.WHITE);
    domainBtn.setToolTipText("ULTIMATE: Click on enemy board to create 3x3 shadow explosion!");
    domainBtn.setFont(new Font("Arial", Font.BOLD, 12));
    domainBtn.setFocusPainted(false);
    
    String status3 = kael.getSkillStatus(3);
    if (!status3.equals("Ready!")) {
        domainBtn.setEnabled(false);
        domainBtn.setText("🌑🌑🌑 Shadow Domain (" + status3 + ")");
    }
    
    domainBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            updateStatusLabel("🌑🌑🌑 Click on ENEMY board to create shadow explosion!", Color.YELLOW);
            waitingForKaelDomain = true;
            currentKaelDomainCallback = (x, y) -> {
                int cellsDestroyed = kael.useShadowDomain(enemyBoard, x, y);
                if (cellsDestroyed > 0) {
                    updateStatusLabel("🌑🌑🌑 Shadow Domain destroyed " + cellsDestroyed + " cells!", Color.MAGENTA);
                    refreshUI();
                } else {
                    updateStatusLabel("❌ Cannot use Shadow Domain!", Color.RED);
                }
                waitingForKaelDomain = false;
            };
        }
    });
    panel.add(domainBtn);
    
    
    JLabel energyLabel = new JLabel(kael.getEnergyBar(), SwingConstants.CENTER);
    energyLabel.setFont(new Font("Arial", Font.BOLD, 10));
    energyLabel.setForeground(new Color(100, 200, 255));
    panel.add(energyLabel);
}
private void addMorganaSkills(JPanel panel, boolean isPlayer) {
    Morgana morgana = (Morgana) playerCharacter;
    
    
    JButton melodyBtn = new JButton("🎵 Enchanting Melody (40)");
    melodyBtn.setBackground(new Color(64, 224, 208));
    melodyBtn.setForeground(Color.BLACK);
    melodyBtn.setToolTipText("Confuse enemy for 2 turns - they see fake hit/miss results");
    melodyBtn.setFont(new Font("Arial", Font.BOLD, 11));
    melodyBtn.setFocusPainted(false);
    
    String status1 = morgana.getSkillStatus(1);
    if (!status1.equals("Ready!")) {
        melodyBtn.setEnabled(false);
        melodyBtn.setText("🎵 Enchanting Melody (" + status1 + ")");
    }
    
    melodyBtn.addActionListener(evt -> {
        if (isPlayer && playerTurn) {
            boolean used = morgana.useEnchantingMelody();
            if (used) {
                updateStatusLabel("🎵 Enchanting Melody! Enemy is confused!", Color.CYAN);
                refreshUI();
            }
        }
    });
    panel.add(melodyBtn);
    
    
    JButton whirlpoolBtn = new JButton("🌊 Whirlpool Trap (80)");
    whirlpoolBtn.setBackground(new Color(0, 150, 200));
    whirlpoolBtn.setForeground(Color.WHITE);
    whirlpoolBtn.setToolTipText("Click a cell - hits 3 cells in a vertical column!");
    whirlpoolBtn.setFont(new Font("Arial", Font.BOLD, 11));
    whirlpoolBtn.setFocusPainted(false);
    
    String status2 = morgana.getSkillStatus(2);
    if (!status2.equals("Ready!")) {
        whirlpoolBtn.setEnabled(false);
        whirlpoolBtn.setText("🌊 Whirlpool Trap (" + status2 + ")");
    }
    
    whirlpoolBtn.addActionListener(event -> {
        if (isPlayer && playerTurn) {
            if (!morgana.hasEnoughMana(80)) {
                updateStatusLabel("❌ Not enough mana! Need 80 mana.", Color.RED);
                return;
            }
            if (morgana.getSkillStatus(2).contains("Cooldown")) {
                updateStatusLabel("❌ Whirlpool Trap is on cooldown!", Color.RED);
                return;
            }
            
            System.out.println("🔴 WHIRLPOOL MODE ACTIVATED! Waiting for click...");
        updateStatusLabel("🌊 Click a cell - will hit 3 cells in a vertical column!", Color.YELLOW);
        waitingForWhirlpoolTarget = true;
        currentWhirlpoolCallback = (x, y) -> {
            System.out.println("🎯 WHIRLPOOL CALLBACK RECEIVED: (" + x + "," + y + ")");
                
                boolean trapUsed = morgana.useWhirlpoolTrap(enemyBoard, x, y);
                if (trapUsed) {
                    updateStatusLabel("🌊 Whirlpool trap hit column " + y + "!", Color.CYAN);
                    refreshUI();
                } else {
                    updateStatusLabel("❌ Failed to place whirlpool!", Color.RED);
                }
                waitingForWhirlpoolTarget = false;
            };
        }
    });
    panel.add(whirlpoolBtn);
    
    
    JButton stormBtn = new JButton("⛈️ Storm Call (300)");
    stormBtn.setBackground(new Color(70, 130, 200));
    stormBtn.setForeground(Color.WHITE);
    stormBtn.setToolTipText("Summon a tempest - floods 4 random cells, damaging ships");
    stormBtn.setFont(new Font("Arial", Font.BOLD, 11));
    stormBtn.setFocusPainted(false);
    
    String status3 = morgana.getSkillStatus(3);
    if (!status3.equals("Ready!")) {
        stormBtn.setEnabled(false);
        stormBtn.setText("⛈️ Storm Call (" + status3 + ")");
    }
    
    stormBtn.addActionListener(ev -> {
        if (isPlayer && playerTurn) {
            int flooded = morgana.useStormCall(enemyBoard);
            if (flooded > 0) {
                updateStatusLabel("⛈️ Storm Call! " + flooded + " cells flooded!", Color.YELLOW);
                refreshUI();
            }
        }
    });
    panel.add(stormBtn);
    
    
    if (morgana.isEnemyConfusedActive()) {
        JLabel confusedLabel = new JLabel("🎵 Enemy CONFUSED!", SwingConstants.CENTER);
        confusedLabel.setForeground(Color.MAGENTA);
        confusedLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(confusedLabel);
    }
    
    if (morgana.getWhirlpoolCount() > 0) {
        JLabel whirlpoolLabel = new JLabel("🌊 " + morgana.getWhirlpoolCount() + " whirlpool(s) active", 
                                           SwingConstants.CENTER);
        whirlpoolLabel.setForeground(Color.CYAN);
        whirlpoolLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(whirlpoolLabel);
    }
    
    if (morgana.getFloodedCount() > 0) {
        JLabel floodedLabel = new JLabel("⛈️ " + morgana.getFloodedCount() + " flooded cells", 
                                         SwingConstants.CENTER);
        floodedLabel.setForeground(new Color(100, 200, 255));
        floodedLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(floodedLabel);
    }
    
    if (!morgana.isOceanEmbraceUsed()) {
        JLabel oceanLabel = new JLabel("🌊 Ocean's Embrace ready", SwingConstants.CENTER);
        oceanLabel.setForeground(new Color(64, 224, 208));
        oceanLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(oceanLabel);
    }
    
    
    JLabel manaLabel = new JLabel(morgana.getManaBar(), SwingConstants.CENTER);
    manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
    manaLabel.setForeground(Color.CYAN);
    panel.add(manaLabel);
} 

private void addValeriusSkills(JPanel panel, boolean isPlayer) {
    Valerius valerius = (Valerius) playerCharacter;
    
    
    JButton radarBtn = new JButton("📡 Radar Overload (50)");
    radarBtn.setBackground(new Color(169, 169, 169));
    radarBtn.setForeground(Color.WHITE);
    radarBtn.setToolTipText("Disable enemy skills for 2 turns");
    radarBtn.setFont(new Font("Arial", Font.BOLD, 11));
    radarBtn.setFocusPainted(false);
    
    String status1 = valerius.getSkillStatus(1);
    if (!status1.equals("Ready!")) {
        radarBtn.setEnabled(false);
        radarBtn.setText("📡 Radar Overload (" + status1 + ")");
    }
    
    radarBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            boolean used = valerius.useRadarOverload();
            if (used) {
                updateStatusLabel("📡 Radar Overload! Enemy skills disabled for 2 turns!", Color.ORANGE);
                refreshUI();
            } else {
                updateStatusLabel("❌ Cannot use Radar Overload!\n" + valerius.getSkillStatus(1), Color.RED);
            }
        }
    });
    panel.add(radarBtn);
    
    
    JButton strikeBtn = new JButton("🎯 Precision Strike (120)");
    strikeBtn.setBackground(new Color(200, 100, 0));
    strikeBtn.setForeground(Color.WHITE);
    strikeBtn.setToolTipText("Next attack destroys 2 cells in a line");
    strikeBtn.setFont(new Font("Arial", Font.BOLD, 11));
    strikeBtn.setFocusPainted(false);
    
    String status2 = valerius.getSkillStatus(2);
    if (!status2.equals("Ready!")) {
        strikeBtn.setEnabled(false);
        strikeBtn.setText("🎯 Precision Strike (" + status2 + ")");
    }
    
    strikeBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            String[] options = {"Horizontal (→)", "Vertical (↓)"};
            int choice = JOptionPane.showOptionDialog(frame,
                "🎯 PRECISION STRIKE\n\nChoose attack direction:",
                "Precision Strike",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
            
            if (choice >= 0) {
                boolean horizontal = (choice == 0);
                valerius.usePrecisionStrike();
                
                updateStatusLabel("🎯 Click on enemy board to strike " + 
                    (horizontal ? "HORIZONTALLY" : "VERTICALLY") + "!", Color.YELLOW);
                waitingForTarget = true;
                currentSkillName = "PRECISION STRIKE";
                targetCallback = (x, y) -> {
                    int destroyed = valerius.applyPrecisionStrike(enemyBoard, x, y, horizontal);
                    if (destroyed > 0) {
                        updateStatusLabel("🎯 Precision Strike destroyed " + destroyed + " cells!", Color.ORANGE);
                        refreshUI();
                    }
                    waitingForTarget = false;
                };
            }
        }
    });
    panel.add(strikeBtn);
    
    
  
JButton fortressBtn = new JButton("🏰 Fortress Mode (300)");
fortressBtn.setBackground(new Color(100, 50, 0));
fortressBtn.setForeground(Color.WHITE);
fortressBtn.setToolTipText("ULTIMATE: Shield ALL your ships for 2 turns (each blocks 1 hit)");
fortressBtn.setFont(new Font("Arial", Font.BOLD, 12));
fortressBtn.setFocusPainted(false);

String status3 = valerius.getSkillStatus(3);
if (!status3.equals("Ready!")) {
    fortressBtn.setEnabled(false);
    fortressBtn.setText("🏰 Fortress Mode (" + status3 + ")");
}

fortressBtn.addActionListener(e -> {
    if (isPlayer && playerTurn) {
        if (!valerius.hasEnoughMana(300)) {
            updateStatusLabel("❌ Not enough mana! Need 300 mana.", Color.RED);
            return;
        }
        if (valerius.getSkillStatus(3).contains("Cooldown")) {
            updateStatusLabel("❌ Fortress Mode is on cooldown!", Color.RED);
            return;
        }
        
        boolean used = valerius.useFortressMode();  
        if (used) {
            updateStatusLabel("🏰 FORTRESS MODE ACTIVATED! All ships are SHIELDED for 2 turns!", Color.GREEN);
            refreshUI();
        } else {
            updateStatusLabel("❌ Cannot use Fortress Mode!", Color.RED);
        }
    }
});
panel.add(fortressBtn);
    
    
    if (valerius.areEnemySkillsDisabled()) {
        JLabel disabledLabel = new JLabel("🚫 Enemy skills DISABLED", SwingConstants.CENTER);
        disabledLabel.setForeground(Color.RED);
        disabledLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(disabledLabel);
    }
    
    if (valerius.hasProtectedShip()) {
        JLabel protectedLabel = new JLabel("🛡️ " + valerius.getProtectedShipName() + " is PROTECTED", SwingConstants.CENTER);
        protectedLabel.setForeground(Color.CYAN);
        protectedLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(protectedLabel);
    }
    
    if (valerius.isScrapperResolveActive()) {
        JLabel resolveLabel = new JLabel("⚡ Scrapper's Resolve ACTIVE", SwingConstants.CENTER);
        resolveLabel.setForeground(Color.ORANGE);
        resolveLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(resolveLabel);
    }
    
    
    JLabel manaLabel = new JLabel(valerius.getManaBar(), SwingConstants.CENTER);
    manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
    manaLabel.setForeground(Color.CYAN);
    panel.add(manaLabel);
}
private void addSkyeSkills(JPanel panel, boolean isPlayer) {
    Skye skye = (Skye) playerCharacter;
    
    
    JButton catSwarmBtn = new JButton("🐱 Cat Swarm (70)");
    catSwarmBtn.setBackground(new Color(255, 165, 0));
    catSwarmBtn.setForeground(Color.BLACK);
    catSwarmBtn.setToolTipText("Summon cats to randomly reposition enemy ships");
    
    String status1 = skye.getSkillStatus(1);
    if (!status1.equals("Ready!")) {
        catSwarmBtn.setEnabled(false);
        catSwarmBtn.setText("🐱 Cat Swarm (" + status1 + ")");
    }
    
    catSwarmBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            boolean used = skye.useCatSwarm(enemyBoard);
            if (used) {
                JOptionPane.showMessageDialog(frame, 
                    "🐱 CAT SWARM!\nEnemy ships are being knocked around!", 
                    "Skill Used", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshUI();
            }
        }
    });
    panel.add(catSwarmBtn);
    
    
    JButton laserBtn = new JButton("🔴 Laser Pointer (50)");
    laserBtn.setBackground(new Color(255, 100, 100));
    laserBtn.setForeground(Color.BLACK);
    laserBtn.setToolTipText("Enemy skips their next turn");
    
    String status2 = skye.getSkillStatus(2);
    if (!status2.equals("Ready!")) {
        laserBtn.setEnabled(false);
        laserBtn.setText("🔴 Laser Pointer (" + status2 + ")");
    }
    
    laserBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            boolean used = skye.useLaserPointer();
            if (used) {
                JOptionPane.showMessageDialog(frame, 
                    "🔴 LASER POINTER!\nEnemy will skip their next turn!", 
                    "Skill Used", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshUI();
            }
        }
    });
    panel.add(laserBtn);
    
    
    JButton catnipBtn = new JButton("🌿 Catnip Explosion (380)");
    catnipBtn.setBackground(new Color(50, 205, 50));
    catnipBtn.setForeground(Color.BLACK);
    catnipBtn.setToolTipText("Click to target center of 2x2 catnip area");
    
    String status3 = skye.getSkillStatus(3);
    if (!status3.equals("Ready!")) {
        catnipBtn.setEnabled(false);
        catnipBtn.setText("🌿 Catnip Explosion (" + status3 + ")");
    }
    
    catnipBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            startTargetSelection("CATNIP EXPLOSION - Click center of 2x2 area", (x, y) -> {
                int cellsDestroyed = skye.useCatnipExplosion(enemyBoard, x, y);
                if (cellsDestroyed > 0) {
                    JOptionPane.showMessageDialog(frame, 
                        "🌿 CATNIP EXPLOSION!\nDestroyed " + cellsDestroyed + " segments!\nEnemy is distracted!", 
                        "Skill Used", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshUI();
                }
            }, catnipBtn);
        }
    });
    panel.add(catnipBtn);
    
    
    JLabel nineLivesLabel = new JLabel(skye.getNineLivesDisplay(), SwingConstants.CENTER);
    nineLivesLabel.setFont(new Font("Arial", Font.BOLD, 12));
    nineLivesLabel.setForeground(Color.PINK);
    panel.add(nineLivesLabel);
    
    JLabel manaLabel = new JLabel(skye.getManaBar(), SwingConstants.CENTER);
    manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
    manaLabel.setForeground(Color.CYAN);
    panel.add(manaLabel);
}

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
    
    
    int totalShips = enemyBoard.getShips().size();
    int remainingShips = 0;
    for (Ship ship : enemyBoard.getShips()) {
        if (!ship.isSunk()) {
            remainingShips++;
        }
    }
    
    JPanel shipCounterPanel = new JPanel(new GridLayout(2, 1));
    shipCounterPanel.setBackground(new Color(50, 0, 0));
    
    JLabel shipsLabel = new JLabel("🚢 ENEMY FLEET", SwingConstants.CENTER);
    shipsLabel.setFont(new Font("Arial", Font.BOLD, 12));
    shipsLabel.setForeground(Color.WHITE);
    shipCounterPanel.add(shipsLabel);
    
    
    JPanel shipIconsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
    shipIconsPanel.setBackground(new Color(50, 0, 0));
    
    for (int i = 0; i < totalShips; i++) {
        JLabel shipIcon;
        if (i < remainingShips) {
            shipIcon = new JLabel("🚢"); 
            shipIcon.setForeground(Color.RED);
        } else {
            shipIcon = new JLabel("💀"); 
            shipIcon.setForeground(Color.GRAY);
        }
        shipIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        shipIconsPanel.add(shipIcon);
    }
    
    
    JLabel countLabel = new JLabel(remainingShips + "/" + totalShips + " ships", SwingConstants.CENTER);
    countLabel.setFont(new Font("Arial", Font.BOLD, 12));
    countLabel.setForeground(Color.WHITE);
    shipCounterPanel.add(shipIconsPanel);
    shipCounterPanel.add(countLabel);
    
    panel.add(shipCounterPanel, BorderLayout.SOUTH);
    
    return panel;
}
private void refreshShipCounters() {
    
    
    createBattleUI(waves.get(currentWaveIndex));
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
    
    playerBoardPanel = new BoardPanel(true, playerBoard);
    enemyBoardPanel = new BoardPanel(false, enemyBoard);
    
    
    playerBoardPanel.setPlayerClickHandler((row, col) -> {

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
            refreshUI();
        } else {
            updateStatusLabel("❌ Cannot use Shadow Step!", Color.RED);
        }
        
        waitingForKaelStepDestination = false;
        return;
    }
        System.out.println("🛡️ Player board clicked at: (" + row + "," + col + ")");
        
        
        if (waitingForAerisShield && currentAerisShieldCallback != null) {
            System.out.println("🛡️ Aeris shield targeting: (" + row + "," + col + ")");
            currentAerisShieldCallback.accept(row, col);
            waitingForAerisShield = false;
            currentAerisShieldCallback = null;
        } else {
            System.out.println("🛡️ No active shield targeting, ignoring click");
        }
    });
    
    
    enemyBoardPanel.setEnemyClickHandler((row, col) -> {
        System.out.println("🎯 Enemy board clicked at: (" + row + "," + col + ")");
        
         if (waitingForKaelBlade && currentKaelBladeCallback != null) {
        System.out.println("⚔️ Kael's SHADOW BLADE targeting: (" + row + "," + col + ")");
        currentKaelBladeCallback.accept(row, col);
        waitingForKaelBlade = false;
        currentKaelBladeCallback = null;
        return;
    }
    
    
    if (waitingForKaelDomain && currentKaelDomainCallback != null) {
        System.out.println("🌑🌑🌑 Kael's SHADOW DOMAIN targeting: (" + row + "," + col + ")");
        currentKaelDomainCallback.accept(row, col);
        waitingForKaelDomain = false;
        currentKaelDomainCallback = null;
        return;
    }
        if (waitingForSeleneVision && currentSeleneVisionCallback != null) {
            System.out.println("🌙 Selene's LUNAR VISION targeting: (" + row + "," + col + ")");
            currentSeleneVisionCallback.accept(row, col);
            waitingForSeleneVision = false;
            currentSeleneVisionCallback = null;
            return;
        }
        
        
        if (waitingForSeleneBinding && currentSeleneBindingCallback != null) {
            System.out.println("🌑 Selene's ECLIPSE BINDING targeting: (" + row + "," + col + ")");
            currentSeleneBindingCallback.accept(row, col);
            waitingForSeleneBinding = false;
            currentSeleneBindingCallback = null;
            return;
        }
        
        
        if (waitingForSeleneCrescent && currentSeleneCrescentCallback != null) {
            System.out.println("🌙 Selene's CRESCENT BLADE targeting: (" + row + "," + col + ")");
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
            handlePlayerAttack(row, col);
        }
    });
    
    panel.add(playerBoardPanel);
    panel.add(enemyBoardPanel);
    
    return panel;
}
    
private void handlePlayerAttack(int row, int col) {
    updateStatusLabel("⚡ FIRING at (" + row + "," + col + ")!", Color.YELLOW);
    
    ShotResult result = ShotResult.MISS;
    
    if (playerCharacter instanceof Kael) {
        Kael kael = (Kael) playerCharacter;
        
        if (playerCharacter instanceof Kael) {
        result = enemyBoard.fire(row, col);
        updateStatusLabel(result == ShotResult.HIT ? "💥 HIT! Enemy ship damaged!" : "💧 Miss...", 
                          result == ShotResult.HIT ? Color.GREEN : Color.CYAN);
    } 
    } else if (playerCharacter instanceof Jiji) {
        
        Jiji jiji = (Jiji) playerCharacter;
        result = enemyBoard.fire(row, col);
        updateStatusLabel(result == ShotResult.HIT ? "💥 HIT! Enemy ship damaged!" : "💧 Miss...", 
                          result == ShotResult.HIT ? Color.GREEN : Color.CYAN);
    } else {
        result = enemyBoard.fire(row, col);
        updateStatusLabel(result == ShotResult.HIT ? "💥 HIT! Enemy ship damaged!" : "💧 Miss...", 
                          result == ShotResult.HIT ? Color.GREEN : Color.CYAN);
    }
    
    enemyBoardPanel.updateCell(row, col, result);
    
    
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
    
    refreshUI();
    
    if (enemyBoard.allShipsSunk()) {
        updateStatusLabel("🎉 VICTORY! All enemy ships destroyed!", Color.ORANGE);
        waveComplete();
        return;
    }
    
    
    if (playerCharacter instanceof Selene) {
        Selene selene = (Selene) playerCharacter;
        if (selene.hasExtraTurn()) {
            selene.consumeExtraTurn();
            updateStatusLabel("🌑 Eclipse Binding grants you an EXTRA TURN!", Color.MAGENTA);
            playerTurn = true;  
            resetStatusLabel();
            refreshUI();
            return;  
        }
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
    
    if (playerCharacter instanceof Skye) {
        Skye skye = (Skye) playerCharacter;
        if (skye.shouldSkipEnemyTurn()) {
            updateStatusLabel("🔴 Enemy chasing laser pointer! Turn skipped!", Color.ORANGE);
            playerTurn = true;
            resetStatusLabel();
            return;
        }
    }
    
    useEnemySkill();
    
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
    
    int x = random.nextInt(10);
    int y = random.nextInt(10);
    
    updateStatusLabel("🎯 Enemy firing at (" + x + "," + y + ")!", Color.ORANGE);
    
    ShotResult result = playerBoard.fire(x, y);
    
    if (playerCharacter instanceof Jiji) {
        Jiji jiji = (Jiji) playerCharacter;
        if (jiji.checkFirewall(x, y, result)) {
            updateStatusLabel("🛡️ FIREWALL blocked the enemy shot!", Color.CYAN);
        } else {
            playerBoardPanel.updateCell(x, y, result);
        }
    } 
    else {
        playerBoardPanel.updateCell(x, y, result);
    }
    
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
    
    refreshUI();
    
    if (playerBoard.allShipsSunk()) {
        updateStatusLabel("💔 GAME OVER - Your fleet destroyed!", Color.RED);
        gameOver();
        return;
    }
    
    playerTurn = true;
    resetStatusLabel();
}
private int getTotalShipSegments(GameCharacter character) {
    Board board = null;
    if (character == currentEnemy) {
        board = enemyBoard;
    } else {
        board = playerBoard;
    }
    
    int total = 0;
    for (Ship ship : board.getShips()) {
        total += ship.getSize();
    }
    return total;
}

private void useStrategicEnemySkill() {
    if (currentEnemy == null) return;
    
    
    double enemyHealthPercent = (double)currentEnemy.getCurrentHealth() / currentEnemy.getMaxHealth();
    
    int playerShipsRemaining = 0;
    for (Ship ship : playerBoard.getShips()) {
        if (!ship.isSunk()) playerShipsRemaining++;
    }
    
   if (currentEnemy instanceof Valerius) {
    
    int remainingSegments = 0;
    for (Ship ship : currentEnemy.getBoard().getShips()) {
        if (!ship.isSunk()) {
            remainingSegments += ship.getRemainingHealth();
        }
    }
    int totalSegments = getTotalShipSegments(currentEnemy);
    double segmentsPercent = (double)remainingSegments / totalSegments;
    
    
    if (segmentsPercent < 0.3) {
        Valerius valerius = (Valerius) currentEnemy;
        if (valerius.hasEnoughMana(300)) {
            valerius.useFortressMode();
            showEnemySkillMessage("Valerius activates Fortress Mode to protect his remaining ships!");
            return;
        }
    }
}
    
    if (currentEnemy instanceof Kael && playerShipsRemaining > 3) {
        Kael kael = (Kael) currentEnemy;
        if (kael.hasEnoughEnergy(200)) {
            int x = random.nextInt(8);
            int y = random.nextInt(8);
            kael.useShadowDomain(playerBoard, x, y);
            showEnemySkillMessage("Kael's shadow domain consumes your fleet!");
            return;
        }
    }
    
    if (currentEnemy instanceof Jiji && playerShipsRemaining < 3) {
        Jiji jiji = (Jiji) currentEnemy;
        if (jiji.hasEnoughMana(400)) {
            jiji.useSystemOverload(playerBoard);
            showEnemySkillMessage("Jiji overloads your remaining ships!");
            return;
        }
    }
    
    if (currentEnemy instanceof Skye && playerShipsRemaining == playerBoard.getShips().size()) {
        Skye skye = (Skye) currentEnemy;
        if (skye.hasEnoughMana(70)) {
            skye.useCatSwarm(playerBoard);
            showEnemySkillMessage("Skye's cats scramble your formation!");
            return;
        }
    }
    
    
    useEnemySkill();
}
private void refreshCharacterPanels() {
    
    
    createBattleUI(waves.get(currentWaveIndex));
}
    private void resetPlayerBoard() {
    
    healPlayerShips();
}

  private void waveComplete() {
    updateStatusLabel("🎉 WAVE CLEAR! Well done!", Color.GREEN);
    currentWaveIndex++;
    
    String message = "🎉 Victory! You defeated " + currentEnemy.getName() + "!\n\n";
    
    if (currentWaveIndex < waves.size()) {
        message += "Next wave: " + waves.get(currentWaveIndex).enemy.getName();
        
        
        healPlayerShips();  
        
        
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
    private void gameOver() {
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
   private void refreshUI() {
    if (playerBoardPanel != null) {
        playerBoardPanel.refreshColors();
    }
    if (enemyBoardPanel != null) {
        enemyBoardPanel.refreshColors();
    }
      if (currentWaveIndex < waves.size()) {
        createBattleUI(waves.get(currentWaveIndex));
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
    statusLabel.getParent().repaint();
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
    }
    return "YOUR TURN - Click on enemy waters to fire!";
}


}

