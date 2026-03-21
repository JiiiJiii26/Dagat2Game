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
     
    
    
    private BoardPanel playerBoardPanel;
    private BoardPanel enemyBoardPanel;
    private JLabel waveLabel;
    private boolean playerTurn = true;
    private Random random = new Random();
    
private boolean waitingForTarget = false;
private String currentSkillName = "";
private SkillTargetCallback targetCallback;
private JButton lastClickedSkillButton;


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
    
    
    int skillChoice = enemyRandom.nextInt(4);
    
    switch(skillChoice) {
        case 0: 
            if (enemyKael.hasEnoughEnergy(80)) {
                System.out.println("🌫️ Enemy Kael uses SILENT DRIFT!");
                enemyKael.useSilentDrift(enemyBoard);
                showEnemySkillMessage("Kael hides one of their ships!");
            }
            break;
        case 1: 
            if (enemyKael.hasEnoughEnergy(120)) {
                System.out.println("📡 Enemy Kael uses SONAR PULSE!");
                enemyKael.useSonarPulse(playerBoard);
                showEnemySkillMessage("Kael reveals and damages one of your hidden ships!");
            }
            break;
        case 2: 
            if (enemyKael.hasEnoughEnergy(200)) {
                System.out.println("💣 Enemy Kael uses DEPTH CHARGE!");
                
                int x = enemyRandom.nextInt(8);
                int y = enemyRandom.nextInt(8);
                enemyKael.useDepthChargeBarrage(playerBoard, x, y);
                showEnemySkillMessage("Kael drops a depth charge on your fleet!");
            }
            break;
        case 3: 
            if (enemyKael.hasEnoughEnergy(300)) {
                System.out.println("🌪️ Enemy Kael uses TEMPEST LOCK!");
                int x = enemyRandom.nextInt(7) + 1; 
                int y = enemyRandom.nextInt(7) + 1;
                enemyKael.useTempestLock(playerBoard, x, y);
                showEnemySkillMessage("KAEL UNLEASHES TEMPEST LOCK! Massive area damage!");
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
                showEnemySkillMessage("Valerius jams your radar! Your skills are disabled!");
            }
            break;
        case 1: 
            if (enemyValerius.hasEnoughMana(90)) {
                System.out.println("🛡️ Enemy Valerius uses KINETIC BARRIER!");
                int x = enemyRandom.nextInt(8);
                int y = enemyRandom.nextInt(8);
                enemyValerius.useKineticBarrier(enemyBoard, x, y);
                showEnemySkillMessage("Valerius deploys a kinetic barrier around his ships!");
            }
            break;
        case 2: 
            if (enemyValerius.hasEnoughMana(280)) {
                System.out.println("🎯 Enemy Valerius uses ORBITAL RAILGUN!");
                int x = enemyRandom.nextInt(10);
                int y = enemyRandom.nextInt(10);
                enemyValerius.useOrbitalRailgun(playerBoard, x, y);
                playerBoardPanel.updateCell(x, y, ShotResult.HIT);
                showEnemySkillMessage("Valerius fires his railgun at (" + x + "," + y + ")!");
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
            placeKaelShips(board);
        } else if (enemy instanceof Valerius) {
            placeValeriusShips(board);
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
    }
    
    panel.add(skillsPanel, BorderLayout.WEST);
    
    
    
    JPanel shipCounterPanel = createShipCounterPanel(true);
    panel.add(shipCounterPanel, BorderLayout.SOUTH);
    
    return panel;
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
    
    
    JButton silentBtn = new JButton("🌫️ Silent Drift (80)");
    silentBtn.setBackground(new Color(75, 0, 130));
    silentBtn.setForeground(Color.WHITE);
    silentBtn.setToolTipText("Hide one of your ships for 2 turns");
    silentBtn.setFont(new Font("Arial", Font.BOLD, 11));
    
    String status1 = kael.getSkillStatus(1);
    if (!status1.equals("Ready!")) {
        silentBtn.setEnabled(false);
        silentBtn.setText("🌫️ Silent Drift (" + status1 + ")");
    }
    
    silentBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            boolean used = kael.useSilentDrift(playerBoard);
            if (used) {
                JOptionPane.showMessageDialog(frame, 
                    "🌫️ Silent Drift Activated!\nOne of your ships is now hidden!", 
                    "Skill Used", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshUI();
            }
        }
    });
    panel.add(silentBtn);
    
    
    JButton sonarBtn = new JButton("📡 Sonar Pulse (120)");
    sonarBtn.setBackground(new Color(100, 150, 255));
    sonarBtn.setForeground(Color.BLACK);
    sonarBtn.setToolTipText("Click on a cell to reveal and destroy a hidden ship segment");
    sonarBtn.setFont(new Font("Arial", Font.BOLD, 11));
    
    String status2 = kael.getSkillStatus(2);
    if (!status2.equals("Ready!")) {
        sonarBtn.setEnabled(false);
        sonarBtn.setText("📡 Sonar Pulse (" + status2 + ")");
    }
    
    sonarBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            startTargetSelection("SONAR PULSE - Click on any cell", (x, y) -> {
                boolean used = kael.useSonarPulse(enemyBoard);
                if (used) {
                    JOptionPane.showMessageDialog(frame, 
                        "📡 Sonar Pulse Activated!\nA hidden enemy segment was destroyed!", 
                        "Skill Used", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshUI();
                }
            }, sonarBtn);
        }
    });
    panel.add(sonarBtn);
    
    
    JButton depthBtn = new JButton("💣 Depth Charge (200)");
    depthBtn.setBackground(new Color(200, 100, 0));
    depthBtn.setForeground(Color.WHITE);
    depthBtn.setToolTipText("Click to target center of 2x2 area");
    depthBtn.setFont(new Font("Arial", Font.BOLD, 11));
    
    String status3 = kael.getSkillStatus(3);
    if (!status3.equals("Ready!")) {
        depthBtn.setEnabled(false);
        depthBtn.setText("💣 Depth Charge (" + status3 + ")");
    }
    
    depthBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            startTargetSelection("DEPTH CHARGE - Click center of 2x2 area", (x, y) -> {
                int cellsDestroyed = kael.useDepthChargeBarrage(enemyBoard, x, y);
                if (cellsDestroyed > 0) {
                    String message = "💣 Depth Charge Complete!\nDestroyed " + cellsDestroyed + " ship segments.";
                    if (kael.wasLastTargetHidden()) {
                        message += "\n\n🎯 BONUS: Hit a hidden ship!";
                    }
                    JOptionPane.showMessageDialog(frame, message, "Skill Used", JOptionPane.INFORMATION_MESSAGE);
                    refreshUI();
                }
            }, depthBtn);
        }
    });
    panel.add(depthBtn);
    
    
    JButton tempestBtn = new JButton("🌪️ TEMPEST LOCK (300)");
    tempestBtn.setBackground(Color.YELLOW);
    tempestBtn.setForeground(Color.BLACK);
    tempestBtn.setToolTipText("ULTIMATE: Click to center the 3x3 devastation");
    tempestBtn.setFont(new Font("Arial", Font.BOLD, 12));
    
    String status4 = kael.getSkillStatus(4);
    if (!status4.equals("ULTIMATE READY!")) {
        tempestBtn.setEnabled(false);
        tempestBtn.setText("🌪️ Tempest Lock (" + status4 + ")");
    }
    
    tempestBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            startTargetSelection("🌪️ TEMPEST LOCK - Click center of 3x3 area", (x, y) -> {
                int cellsDestroyed = kael.useTempestLock(enemyBoard, x, y);
                if (cellsDestroyed > 0) {
                    JOptionPane.showMessageDialog(frame, 
                        "🌪️ TEMPEST LOCK ACTIVATED!\nDestroyed " + cellsDestroyed + " ship segments!", 
                        "ULTIMATE!", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshUI();
                }
            }, tempestBtn);
        }
    });
    panel.add(tempestBtn);
    
    
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
    
    String status1 = valerius.getSkillStatus(1);
    if (!status1.equals("Ready!")) {
        radarBtn.setEnabled(false);
        radarBtn.setText("📡 Radar Overload (" + status1 + ")");
    }
    
    radarBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            boolean used = valerius.useRadarOverload();
            if (used) {
                JOptionPane.showMessageDialog(frame, 
                    "📡 RADAR OVERLOAD!\nEnemy skills disabled for 2 turns!", 
                    "Skill Used", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshUI();
            }
        }
    });
    panel.add(radarBtn);
    
    
    JButton barrierBtn = new JButton("🛡️ Kinetic Barrier (90)");
    barrierBtn.setBackground(new Color(100, 149, 237));
    barrierBtn.setForeground(Color.WHITE);
    barrierBtn.setToolTipText("Click on YOUR board to place 3x3 shield");
    barrierBtn.setFont(new Font("Arial", Font.BOLD, 11));
    
    String status2 = valerius.getSkillStatus(2);
    if (!status2.equals("Ready!")) {
        barrierBtn.setEnabled(false);
        barrierBtn.setText("🛡️ Kinetic Barrier (" + status2 + ")");
    }
    
    barrierBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            
            JOptionPane.showMessageDialog(frame,
                "🛡️ KINETIC BARRIER\n\nClick on YOUR board to place the shield.",
                "Select Target",
                JOptionPane.INFORMATION_MESSAGE);
            
            
            
            String input = JOptionPane.showInputDialog(frame, 
                "Enter center coordinates (row,col):", 
                "Barrier Placement", 
                JOptionPane.QUESTION_MESSAGE);
            if (input != null) {
                try {
                    String[] parts = input.split(",");
                    int x = Integer.parseInt(parts[0].trim());
                    int y = Integer.parseInt(parts[1].trim());
                    boolean used = valerius.useKineticBarrier(playerBoard, x, y);
                    if (used) {
                        JOptionPane.showMessageDialog(frame, 
                            "🛡️ Barrier deployed at (" + x + "," + y + ")!", 
                            "Skill Used", 
                            JOptionPane.INFORMATION_MESSAGE);
                        refreshUI();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid coordinates!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    });
    panel.add(barrierBtn);
    
    
    JButton railgunBtn = new JButton("🎯 Orbital Railgun (280)");
    railgunBtn.setBackground(Color.RED);
    railgunBtn.setForeground(Color.WHITE);
    railgunBtn.setToolTipText("Click on a cell to fire the railgun");
    railgunBtn.setFont(new Font("Arial", Font.BOLD, 11));
    
    String status3 = valerius.getSkillStatus(3);
    if (!status3.equals("Ready!")) {
        railgunBtn.setEnabled(false);
        railgunBtn.setText("🎯 Orbital Railgun (" + status3 + ")");
    }
    
    railgunBtn.addActionListener(e -> {
        if (isPlayer && playerTurn) {
            startTargetSelection("ORBITAL RAILGUN - Click target cell", (x, y) -> {
                ShotResult result = valerius.useOrbitalRailgun(enemyBoard, x, y);
                if (result != ShotResult.INVALID) {
                    enemyBoardPanel.updateCell(x, y, result);
                    JOptionPane.showMessageDialog(frame, 
                        "🎯 Railgun fired at (" + x + "," + y + ")!\nResult: " + result, 
                        "Skill Used", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshUI();
                }
            }, railgunBtn);
        }
    });
    panel.add(railgunBtn);
    
    
    if (valerius.areEnemySkillsDisabled()) {
        JLabel disabledLabel = new JLabel("🚫 Enemy skills disabled", SwingConstants.CENTER);
        disabledLabel.setForeground(Color.RED);
        panel.add(disabledLabel);
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
       
    return "🎮";
}

private JPanel createBoardsPanel() {
    JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
    panel.setOpaque(false);
    
    playerBoardPanel = new BoardPanel(true, playerBoard);
    enemyBoardPanel = new BoardPanel(false, enemyBoard);
    
 enemyBoardPanel.setEnemyClickHandler((row, col) -> {
    System.out.println("🖱️ Click at (" + row + "," + col + ")");
    
    
    if (waitingForWhirlpoolTarget && currentWhirlpoolCallback != null) {
        System.out.println("✅ Redirecting to WHIRLPOOL skill");
        currentWhirlpoolCallback.accept(row, col);
        waitingForWhirlpoolTarget = false;
        currentWhirlpoolCallback = null;
        return;
    }
    
    
    if (waitingForTarget && targetCallback != null) {
        System.out.println("✅ Redirecting to skill: " + currentSkillName);
        targetCallback.onTargetSelected(row, col);
        waitingForTarget = false;
        targetCallback = null;
        return;
    }
    
    
    if (playerTurn) {
        System.out.println("✅ Normal attack at (" + row + "," + col + ")");
        handlePlayerAttack(row, col);
    } else {
        System.out.println("⚠️ Not player's turn, ignoring click");
    }
});
    
    panel.add(playerBoardPanel);
    panel.add(enemyBoardPanel);
    
    return panel;
}
    
private void handlePlayerAttack(int row, int col) {
    
    updateStatusLabel("⚡ FIRING at (" + row + "," + col + ")!", Color.YELLOW);
    
    ShotResult result;
    
    if (playerCharacter instanceof Jiji) {
        Jiji jiji = (Jiji) playerCharacter;
        result = jiji.applyOverclock(enemyBoard, row, col);
    } else {
        result = enemyBoard.fire(row, col);
    }
    
    enemyBoardPanel.updateCell(row, col, result);
    
    
    switch(result) {
        case HIT:
            updateStatusLabel("💥 HIT! Enemy ship damaged!", Color.GREEN);
            break;
        case SUNK:
            updateStatusLabel("💀 SHIP SUNK! One enemy down!", Color.GREEN);
            break;
        case MISS:
            updateStatusLabel("💧 Missed... Try again!", Color.CYAN);
            break;
        default:
            break;
    }
    
    
    if (playerCharacter instanceof Jiji) {
        ((Jiji) playerCharacter).updateTurnCounter();
    } else if (playerCharacter instanceof Kael) {
        ((Kael) playerCharacter).updateTurnCounter();
    } else if (playerCharacter instanceof Skye) {
        ((Skye) playerCharacter).updateTurnCounter();
    } else if (playerCharacter instanceof Valerius) {
        ((Valerius) playerCharacter).updateTurnCounter();
    }else if (playerCharacter instanceof Morgana) {  
        ((Morgana) playerCharacter).updateTurnCounter();
    }
    
    refreshUI();
    
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
    }else if (playerCharacter instanceof Morgana) {  
        ((Morgana) playerCharacter).updateTurnCounter();
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
    } else if (playerCharacter instanceof Valerius) {
        Valerius valerius = (Valerius) playerCharacter;
        result = valerius.applyBarrier(x, y, result);
        playerBoardPanel.updateCell(x, y, result);
        if (result == ShotResult.MISS && valerius.isCellShielded(x, y)) {
            updateStatusLabel("🛡️ KINETIC BARRIER protected your ship!", Color.CYAN);
        }
    } else {
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

private void useStrategicEnemySkill() {
    if (currentEnemy == null) return;
    
    
    double enemyHealthPercent = (double)currentEnemy.getCurrentHealth() / currentEnemy.getMaxHealth();
    
    
    int playerShipsRemaining = 0;
    for (Ship ship : playerBoard.getShips()) {
        if (!ship.isSunk()) playerShipsRemaining++;
    }
    
    if (currentEnemy instanceof Valerius && enemyHealthPercent < 0.3) {
        
        Valerius valerius = (Valerius) currentEnemy;
        if (valerius.hasEnoughMana(90)) {
            int x = random.nextInt(8);
            int y = random.nextInt(8);
            valerius.useKineticBarrier(enemyBoard, x, y);
            showEnemySkillMessage("Valerius deploys emergency barrier!");
            return;
        }
    }
    
    if (currentEnemy instanceof Kael && playerShipsRemaining > 3) {
        
        Kael kael = (Kael) currentEnemy;
        if (kael.hasEnoughEnergy(300)) {
            int x = random.nextInt(7) + 1;
            int y = random.nextInt(7) + 1;
            kael.useTempestLock(playerBoard, x, y);
            showEnemySkillMessage("Kael unleashes Tempest Lock on your fleet!");
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
    
    private void waveComplete() {
          updateStatusLabel("🎉 WAVE CLEAR! Well done!", Color.GREEN);
        currentWaveIndex++;
        
        String message = "🎉 Victory! You defeated " + currentEnemy.getName() + "!\n\n";
        
        if (currentWaveIndex < waves.size()) {
            message += "Next wave: " + waves.get(currentWaveIndex).enemy.getName();
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

