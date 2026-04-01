package gui;

import game.LocalMultiplayer;
import game.ShotResult;
import models.Board;
import models.Cell;
import models.Ship;
import characters.*;
import java.awt.*;
import javax.swing.*;

public class MultiplayerBattlePanel extends JPanel {
    
    private LocalMultiplayer game;
    private BoardPanel player1BoardPanel;
    private BoardPanel player2BoardPanel;
    private JLabel turnLabel;
    private JLabel statusLabel;
    private JLabel player1ShipsLabel;
    private JLabel player2ShipsLabel;
    private JPanel boardsPanel;
    private JPanel skillsPanel;
    private SkillPanel player1SkillPanel;
    private SkillPanel player2SkillPanel;
    private JPanel currentSkillPanel;
    
    // Skill targeting states
    private boolean waitingForSkillTarget = false;
    private int currentSkillPlayer = 0;
    private int currentSkillNumber = 0;
    private String currentSkillName = "";
    private boolean waitingForHorizontal = false;
    private boolean skillDirectionHorizontal = true;
    
    public MultiplayerBattlePanel(LocalMultiplayer game) {
        this.game = game;
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 112));
        
        createGamePanels();
        updateBoardViews();
    }
    
    private void createGamePanels() {
        // Top panel with turn label
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(25, 25, 112));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        turnLabel = new JLabel("", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 24));
        turnLabel.setForeground(new Color(173, 216, 230));
        topPanel.add(turnLabel, BorderLayout.CENTER);
        
        // Main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(new Color(25, 25, 112));
        
        // Boards panel
        boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsPanel.setBackground(new Color(25, 25, 112));
        boardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Skills panel (will be updated based on turn)
        skillsPanel = new JPanel(new BorderLayout());
        skillsPanel.setBackground(new Color(25, 25, 112));
        skillsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        skillsPanel.setPreferredSize(new Dimension(300, 400));
        
        mainContentPanel.add(boardsPanel, BorderLayout.CENTER);
        mainContentPanel.add(skillsPanel, BorderLayout.EAST);
        
        // Counter panel
        JPanel counterPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        counterPanel.setBackground(new Color(25, 25, 112));
        counterPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        
        player1ShipsLabel = new JLabel("", SwingConstants.CENTER);
        player1ShipsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        player1ShipsLabel.setForeground(Color.GREEN);
        
        player2ShipsLabel = new JLabel("", SwingConstants.CENTER);
        player2ShipsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        player2ShipsLabel.setForeground(Color.RED);
        
        counterPanel.add(player1ShipsLabel);
        counterPanel.add(player2ShipsLabel);
        
        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(25, 25, 112));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        statusLabel = new JLabel("Click on enemy waters to fire!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(Color.WHITE);
        statusPanel.add(statusLabel);
        
        add(topPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        add(counterPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void updateBoardViews() {
        // Clear the boards panel
        boardsPanel.removeAll();
        
        if (game.isPlayer1Turn()) {
            // Player 1's turn
            JPanel player1View = createBoardPanel("YOUR FLEET", game.getPlayer1Board(), true);
            JPanel player2View = createBoardPanel("ENEMY WATERS", game.getPlayer2Board(), false);
            boardsPanel.add(player1View);
            boardsPanel.add(player2View);
            
            // Store references
            player1BoardPanel = (BoardPanel) ((JPanel) player1View.getComponent(1));
            player2BoardPanel = (BoardPanel) ((JPanel) player2View.getComponent(1));
            
            // Set click handlers for Player 1's turn
            player2BoardPanel.setEnemyClickHandler((row, col) -> {
                if (waitingForSkillTarget) {
                    handleSkillTarget(row, col);
                } else {
                    handleShot(1, row, col);
                }
            });
            
            // Show Player 1's skills
            showPlayerSkills(1);
            
            // Disable clicking on Player 1's own board
            player1BoardPanel.setPlayerClickHandler(null);
            
        } else {
            // Player 2's turn
            JPanel player2View = createBoardPanel("YOUR FLEET", game.getPlayer2Board(), true);
            JPanel player1View = createBoardPanel("ENEMY WATERS", game.getPlayer1Board(), false);
            boardsPanel.add(player2View);
            boardsPanel.add(player1View);
            
            // Store references (swap them)
            player2BoardPanel = (BoardPanel) ((JPanel) player2View.getComponent(1));
            player1BoardPanel = (BoardPanel) ((JPanel) player1View.getComponent(1));
            
            // Set click handlers for Player 2's turn
            player1BoardPanel.setEnemyClickHandler((row, col) -> {
                if (waitingForSkillTarget) {
                    handleSkillTarget(row, col);
                } else {
                    handleShot(2, row, col);
                }
            });
            
            // Show Player 2's skills
            showPlayerSkills(2);
            
            // Disable clicking on Player 2's own board
            player2BoardPanel.setPlayerClickHandler(null);
        }
        
        boardsPanel.revalidate();
        boardsPanel.repaint();
        
        // Update the turn display
        updateTurnDisplay();
        
        // Update ship counts
        player1ShipsLabel.setText(getShipCount(game.getPlayer1Board()));
        player2ShipsLabel.setText(getShipCount(game.getPlayer2Board()));
    }
    
    private void showPlayerSkills(int playerNumber) {
        skillsPanel.removeAll();
        
        GameCharacter character = (playerNumber == 1) ? game.getPlayer1Character() : game.getPlayer2Character();
        
        if (character != null) {
            SkillPanel skillPanel = new SkillPanel(character);
            skillPanel.setBoards(
                (playerNumber == 1) ? player1BoardPanel : player2BoardPanel,
                (playerNumber == 1) ? player2BoardPanel : player1BoardPanel
            );
            
            // Set skill button listeners
            setupSkillListeners(skillPanel, playerNumber);
            
            skillsPanel.add(skillPanel, BorderLayout.CENTER);
            
            if (playerNumber == 1) {
                player1SkillPanel = skillPanel;
            } else {
                player2SkillPanel = skillPanel;
            }
        } else {
            JLabel noSkillLabel = new JLabel("No character selected", SwingConstants.CENTER);
            noSkillLabel.setForeground(Color.WHITE);
            noSkillLabel.setFont(new Font("Arial", Font.BOLD, 16));
            skillsPanel.add(noSkillLabel, BorderLayout.CENTER);
        }
        
        skillsPanel.revalidate();
        skillsPanel.repaint();
    }
    
    private void setupSkillListeners(SkillPanel skillPanel, int playerNumber) {
        // This is a bit tricky - we need to override the skill panel's button actions
        // Since SkillPanel has its own buttons, we'll need to modify the SkillPanel class
        // to accept custom callbacks, or we'll create a wrapper
        
        // For now, let's add a note that we need to modify SkillPanel
        System.out.println("Setting up skill listeners for Player " + playerNumber);
    }
    
    private void handleSkillTarget(int row, int col) {
        if (!waitingForSkillTarget) return;
        
        boolean success = false;
        
        if (currentSkillPlayer == 1) {
            success = game.useCharacterSkill(1, currentSkillNumber, row, col, skillDirectionHorizontal);
        } else {
            success = game.useCharacterSkill(2, currentSkillNumber, row, col, skillDirectionHorizontal);
        }
        
        if (success) {
            updateStatusMessage(currentSkillName + " used successfully!", Color.GREEN);
            refreshBoards();
            
            // After skill, refresh the skill panel to show updated cooldowns/mana
            showPlayerSkills(currentSkillPlayer);
        } else {
            updateStatusMessage("Failed to use " + currentSkillName + "!", Color.RED);
        }
        
        waitingForSkillTarget = false;
        currentSkillPlayer = 0;
        currentSkillNumber = 0;
        currentSkillName = "";
    }
    
    private JPanel createBoardPanel(String title, Board board, boolean showShips) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 25, 112));
        
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.NORTH);
        
        BoardPanel boardPanel = new BoardPanel(false, board, showShips);
        panel.add(boardPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void handleShot(int playerNumber, int row, int col) {
        // Store current player to check if turn changes
        boolean wasPlayer1Turn = game.isPlayer1Turn();
        
        ShotResult result = game.fire(playerNumber, row, col);
        
        // Show result message
        String message;
        Color color;
        
        switch(result) {
            case HIT:
                message = "HIT!";
                color = Color.GREEN;
                break;
            case SUNK:
                message = "SHIP SUNK!";
                color = Color.ORANGE;
                break;
            case MISS:
                message = "Miss...";
                color = Color.RED;
                break;
            default:
                message = "";
                color = Color.WHITE;
                break;
        }
        
        if (!message.isEmpty()) {
            updateStatusMessage(message, color);
        }
        
        // Refresh the boards to show the shot result
        refreshBoards();
        
        // Update ship counts
        player1ShipsLabel.setText(getShipCount(game.getPlayer1Board()));
        player2ShipsLabel.setText(getShipCount(game.getPlayer2Board()));
        
        // Check if the turn changed
        if (wasPlayer1Turn != game.isPlayer1Turn()) {
            // Turn changed, update the board views for the next player
            updateBoardViews();
        } else {
            // Same player continues, just update turn display
            updateTurnDisplay();
            // Refresh skill panel for the current player
            showPlayerSkills(game.isPlayer1Turn() ? 1 : 2);
        }
        
        // Check for game over
        if (game.isGameOver()) {
            String winner = game.getWinner();
            updateStatusMessage("GAME OVER! " + winner + " WINS!", Color.YELLOW);
            
            // Disable further clicks
            if (player1BoardPanel != null) {
                player1BoardPanel.setEnemyClickHandler(null);
                player1BoardPanel.setPlayerClickHandler(null);
            }
            if (player2BoardPanel != null) {
                player2BoardPanel.setEnemyClickHandler(null);
                player2BoardPanel.setPlayerClickHandler(null);
            }
        }
    }
    
    public void updateTurnDisplay() {
        if (game.isPlayer1Turn()) {
            turnLabel.setText("PLAYER 1'S TURN");
            turnLabel.setForeground(new Color(100, 255, 100));
        } else {
            turnLabel.setText("PLAYER 2'S TURN");
            turnLabel.setForeground(new Color(255, 100, 100));
        }
    }
    
    public void refreshBoards() {
        if (player1BoardPanel != null) {
            player1BoardPanel.refreshColors();
        }
        if (player2BoardPanel != null) {
            player2BoardPanel.refreshColors();
        }
    }
    
    private String getShipCount(Board board) {
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
    
    public void updateStatusMessage(String message, Color color) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setForeground(color);
            
            Timer timer = new Timer(2000, e -> {
                statusLabel.setText("Click on enemy waters to fire!");
                statusLabel.setForeground(Color.WHITE);
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    // Public method to trigger a skill
    public void useSkill(int playerNumber, int skillNumber, String skillName, boolean requiresTarget, boolean requiresDirection) {
        if (playerNumber == 1 && !game.isPlayer1Turn()) {
            updateStatusMessage("Not your turn!", Color.RED);
            return;
        }
        if (playerNumber == 2 && game.isPlayer1Turn()) {
            updateStatusMessage("Not your turn!", Color.RED);
            return;
        }
        
        if (requiresTarget) {
            waitingForSkillTarget = true;
            currentSkillPlayer = playerNumber;
            currentSkillNumber = skillNumber;
            currentSkillName = skillName;
            
            if (requiresDirection) {
                // Ask for direction first
                String[] options = {"Horizontal (→)", "Vertical (↓)"};
                int choice = JOptionPane.showOptionDialog(this,
                    skillName + "\n\nChoose direction:",
                    "Skill Direction",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
                
                if (choice >= 0) {
                    skillDirectionHorizontal = (choice == 0);
                    updateStatusMessage("Click on enemy board to target " + skillName + "!", Color.YELLOW);
                } else {
                    waitingForSkillTarget = false;
                }
            } else {
                updateStatusMessage("Click on enemy board to target " + skillName + "!", Color.YELLOW);
            }
        } else {
            // Skill doesn't require target (like Overclock, Radar Overload)
            boolean success = game.useCharacterSkill(playerNumber, skillNumber, 0, 0, false);
            if (success) {
                updateStatusMessage(skillName + " used successfully!", Color.GREEN);
                refreshBoards();
                showPlayerSkills(playerNumber);
            } else {
                updateStatusMessage("Failed to use " + skillName + "!", Color.RED);
            }
        }
    }
}