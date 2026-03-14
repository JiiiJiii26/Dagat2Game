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
    
    
    private BoardPanel playerBoardPanel;
    private BoardPanel enemyBoardPanel;
    private JLabel waveLabel;
    private boolean playerTurn = true;
    private Random random = new Random();
    
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
            showVictoryScreen();
            return;
        }
        
        CampaignWave wave = waves.get(index);
        currentEnemy = wave.enemy;
        enemyBoard = new Board();
        
        System.out.println("⚔️ Wave " + (index + 1) + "/" + waves.size() + 
                          ": Fighting " + currentEnemy.getName());
        
        placeEnemyShips(currentEnemy, enemyBoard);
        createBattleUI(wave);
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
    
    // Top panel with wave info
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBackground(new Color(25, 25, 112));
    topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    String waveInfo = String.format("%s - Wave %d/%d", 
        wave.title, currentWaveIndex + 1, waves.size());
    
    waveLabel = new JLabel(waveInfo, SwingConstants.CENTER);
    waveLabel.setFont(new Font("Arial", Font.BOLD, 20));
    waveLabel.setForeground(wave.waveColor);
    topPanel.add(waveLabel, BorderLayout.CENTER);
    
    // === MAIN BATTLE PANEL WITH 3 COLUMNS ===
    JPanel battlePanel = new JPanel(new GridBagLayout());
    battlePanel.setBackground(new Color(25, 25, 112));
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1.0;
    
    // === LEFT COLUMN: PLAYER CHARACTER ===
    gbc.gridx = 0;
    gbc.weightx = 0.2;
    battlePanel.add(createPlayerCharacterPanel(), gbc);
    
    // === CENTER COLUMN: GAME BOARDS ===
    gbc.gridx = 1;
    gbc.weightx = 0.6;
    battlePanel.add(createBoardsPanel(), gbc);
    
    // === RIGHT COLUMN: ENEMY CHARACTER ===
    gbc.gridx = 2;
    gbc.weightx = 0.2;
    battlePanel.add(createEnemyCharacterPanel(wave), gbc);
    
    // Status panel at bottom
    JPanel statusPanel = new JPanel();
    statusPanel.setBackground(new Color(25, 25, 112));
    statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
    
    JLabel statusLabel = new JLabel("YOUR TURN - Click on enemy waters to fire!", SwingConstants.CENTER);
    statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
    statusLabel.setForeground(Color.WHITE);
    statusPanel.add(statusLabel);
    
    frame.add(topPanel, BorderLayout.NORTH);
    frame.add(battlePanel, BorderLayout.CENTER);
    frame.add(statusPanel, BorderLayout.SOUTH);
    
    frame.revalidate();
    frame.repaint();
}
private JPanel createPlayerCharacterPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(0, 50, 0)); // Dark green for player
    panel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
    
    // Character name
    JLabel nameLabel = new JLabel(playerCharacter.getName(), SwingConstants.CENTER);
    nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
    nameLabel.setForeground(Color.WHITE);
    panel.add(nameLabel, BorderLayout.NORTH);
    
    // Character portrait/emoji
    JLabel portraitLabel = new JLabel(getCharacterEmoji(playerCharacter), SwingConstants.CENTER);
    portraitLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
    portraitLabel.setForeground(Color.WHITE);
    panel.add(portraitLabel, BorderLayout.CENTER);
    
    // Health bar
    JProgressBar healthBar = new JProgressBar(0, playerCharacter.getMaxHealth());
    healthBar.setValue(playerCharacter.getCurrentHealth());
    healthBar.setForeground(Color.GREEN);
    healthBar.setStringPainted(true);
    healthBar.setString(playerCharacter.getCurrentHealth() + "/" + playerCharacter.getMaxHealth() + " HP");
    panel.add(healthBar, BorderLayout.SOUTH);
    
    return panel;
}

private JPanel createEnemyCharacterPanel(CampaignWave wave) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(50, 0, 0)); // Dark red for enemy
    panel.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
    
    // Enemy name
    JLabel nameLabel = new JLabel(currentEnemy.getName(), SwingConstants.CENTER);
    nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
    nameLabel.setForeground(Color.WHITE);
    panel.add(nameLabel, BorderLayout.NORTH);
    
    // Enemy portrait/emoji
    JLabel portraitLabel = new JLabel(getCharacterEmoji(currentEnemy), SwingConstants.CENTER);
    portraitLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
    portraitLabel.setForeground(wave.waveColor);
    panel.add(portraitLabel, BorderLayout.CENTER);
    
    // Health bar
    JProgressBar healthBar = new JProgressBar(0, currentEnemy.getMaxHealth());
    healthBar.setValue(currentEnemy.getCurrentHealth());
    healthBar.setForeground(Color.RED);
    healthBar.setStringPainted(true);
    healthBar.setString(currentEnemy.getCurrentHealth() + "/" + currentEnemy.getMaxHealth() + " HP");
    panel.add(healthBar, BorderLayout.SOUTH);
    
    return panel;
}

private String getCharacterEmoji(GameCharacter character) {
    if (character instanceof Jiji) return "💻";
    if (character instanceof Kael) return "🌑";
    if (character instanceof Valerius) return "🛡️";
    if (character instanceof Skye) return "🐱";
       
    return "🎮";
}

private JPanel createBoardsPanel() {
    JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
    panel.setOpaque(false);
    
    playerBoardPanel = new BoardPanel(true, playerBoard);
    enemyBoardPanel = new BoardPanel(false, enemyBoard);
    
    enemyBoardPanel.setEnemyClickHandler((row, col) -> {
        if (playerTurn) {
            handlePlayerAttack(row, col);
        }
    });
    
    panel.add(playerBoardPanel);
    panel.add(enemyBoardPanel);
    
    return panel;
}
    
   private void handlePlayerAttack(int row, int col) {
    ShotResult result = enemyBoard.fire(row, col);
    enemyBoardPanel.updateCell(row, col, result);
    
    // Update enemy health (simulated - you'll need actual health logic)
    if (result == ShotResult.HIT || result == ShotResult.SUNK) {
        // Reduce enemy health - you'll need to implement this properly
        // currentEnemy.takeDamage(calculateDamage());
    }
    
    if (enemyBoard.allShipsSunk()) {
        waveComplete();
        return;
    }
    
    playerTurn = false;
    
    Timer timer = new Timer(1000, e -> enemyTurn());
    timer.setRepeats(false);
    timer.start();
}
    
   private void enemyTurn() {
    Random rand = new Random();
    int x = rand.nextInt(10);
    int y = rand.nextInt(10);
    
    ShotResult result = playerBoard.fire(x, y);
    playerBoardPanel.updateCell(x, y, result);
    
    // Update player health (simulated)
    if (result == ShotResult.HIT || result == ShotResult.SUNK) {
        // playerCharacter.takeDamage(calculateDamage());
    }
    
    System.out.println(currentEnemy.getName() + " fired at (" + x + ", " + y + ") - " + result);
    
    if (playerBoard.allShipsSunk()) {
        gameOver();
        return;
    }
    
    playerTurn = true;
    
    // Refresh the UI to show updated health
    refreshCharacterPanels();
}

private void refreshCharacterPanels() {
    // This will recreate the battle UI to show updated health
    // You could also update just the health bars directly
    createBattleUI(waves.get(currentWaveIndex));
}
    
    private void waveComplete() {
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
}
