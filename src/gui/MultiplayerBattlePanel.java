package gui;

import game.LocalMultiplayer;
import game.ShotResult;
import models.Board;
import models.Cell;
import models.Ship;
import characters.*;
import java.awt.*;
import javax.swing.*;
import main.Main;

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
    private SkillPanel currentSkillPanel;
    private int currentPlayer = 1;
    
    
    private boolean waitingForSkillTarget = false;
    private int currentSkillPlayer = 0;
    private int currentSkillNumber = 0;
    private String currentSkillName = "";
    private boolean waitingForDirection = false;
    private boolean skillDirectionHorizontal = true;
    private boolean skillTargetsOwnBoard = false;
    
    
    private boolean waitingForShadowStepSource = false;
    private int shadowStepSourceX = -1;
    private int shadowStepSourceY = -1;
    
    public MultiplayerBattlePanel(LocalMultiplayer game) {
        this.game = game;
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 112));
        
        createGamePanels();
        updateBoardViews();
    }
    
    private void createGamePanels() {
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(25, 25, 112));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        
        JButton backButton = new JButton("← BACK TO MENU");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(200, 60, 60));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(140, 35));
        backButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to return to the main menu?\nThe current game will be lost.",
                "Return to Menu",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                Main.showMainMenu();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);
        
        
        turnLabel = new JLabel("", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 24));
        turnLabel.setForeground(new Color(173, 216, 230));
        topPanel.add(turnLabel, BorderLayout.CENTER);
        
        
        JPanel rightPlaceholder = new JPanel();
        rightPlaceholder.setOpaque(false);
        rightPlaceholder.setPreferredSize(new Dimension(140, 35));
        topPanel.add(rightPlaceholder, BorderLayout.EAST);
        
        
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(new Color(25, 25, 112));
        
        
        boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsPanel.setBackground(new Color(25, 25, 112));
        boardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        
        skillsPanel = new JPanel(new BorderLayout());
        skillsPanel.setBackground(new Color(25, 25, 112));
        skillsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        skillsPanel.setPreferredSize(new Dimension(320, 400));
        
        mainContentPanel.add(boardsPanel, BorderLayout.CENTER);
        mainContentPanel.add(skillsPanel, BorderLayout.EAST);
        
        
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
        boardsPanel.removeAll();
        
        if (game.isPlayer1Turn()) {
            currentPlayer = 1;
            JPanel player1View = createBoardPanel("YOUR FLEET", game.getPlayer1Board(), true, true);
            JPanel player2View = createBoardPanel("ENEMY WATERS", game.getPlayer2Board(), false, false);
            boardsPanel.add(player1View);
            boardsPanel.add(player2View);
            
            player1BoardPanel = (BoardPanel) ((JPanel) player1View.getComponent(1));
            player2BoardPanel = (BoardPanel) ((JPanel) player2View.getComponent(1));
            
            player1BoardPanel.setPlayerClickHandler((row, col) -> handleOwnBoardClick(1, row, col));
            player2BoardPanel.setEnemyClickHandler((row, col) -> handleEnemyBoardClick(1, row, col));
            
            showPlayerSkills(1);
            
        } else {
            currentPlayer = 2;
            JPanel player2View = createBoardPanel("YOUR FLEET", game.getPlayer2Board(), true, true);
            JPanel player1View = createBoardPanel("ENEMY WATERS", game.getPlayer1Board(), false, false);
            boardsPanel.add(player2View);
            boardsPanel.add(player1View);
            
            player2BoardPanel = (BoardPanel) ((JPanel) player2View.getComponent(1));
            player1BoardPanel = (BoardPanel) ((JPanel) player1View.getComponent(1));
            
            player2BoardPanel.setPlayerClickHandler((row, col) -> handleOwnBoardClick(2, row, col));
            player1BoardPanel.setEnemyClickHandler((row, col) -> handleEnemyBoardClick(2, row, col));
            
            showPlayerSkills(2);
        }
        
        boardsPanel.revalidate();
        boardsPanel.repaint();
        
        updateTurnDisplay();
        updateShipCounts();
    }
    
    private void showPlayerSkills(int playerNumber) {
        if (skillsPanel == null) {
            System.out.println("ERROR: skillsPanel is null!");
            return;
        }
        
        skillsPanel.removeAll();
        
        GameCharacter character = (playerNumber == 1) ? game.getPlayer1Character() : game.getPlayer2Character();
        
        if (character != null) {
            currentSkillPanel = new SkillPanel(character);
            currentSkillPanel.setBoards(
                (playerNumber == 1) ? player1BoardPanel : player2BoardPanel,
                (playerNumber == 1) ? player2BoardPanel : player1BoardPanel
            );
            
            currentSkillPanel.setSkillListener(new SkillPanel.SkillButtonListener() {
                @Override
                public void onSkillUsed(int skillNumber, String skillName, boolean requiresTarget, boolean requiresDirection, boolean targetsOwnBoard) {
                    useSkill(playerNumber, skillNumber, skillName, requiresTarget, requiresDirection, targetsOwnBoard);
                }
            });
            
            skillsPanel.add(currentSkillPanel, BorderLayout.CENTER);
        } else {
            JLabel noSkillLabel = new JLabel("No character selected", SwingConstants.CENTER);
            noSkillLabel.setForeground(Color.WHITE);
            noSkillLabel.setFont(new Font("Arial", Font.BOLD, 16));
            skillsPanel.add(noSkillLabel, BorderLayout.CENTER);
        }
        
        skillsPanel.revalidate();
        skillsPanel.repaint();
    }
    
    private void useSkill(int playerNumber, int skillNumber, String skillName, boolean requiresTarget, boolean requiresDirection, boolean targetsOwnBoard) {
        if ((playerNumber == 1 && !game.isPlayer1Turn()) || (playerNumber == 2 && game.isPlayer1Turn())) {
            updateStatusMessage("Not your turn!", Color.RED);
            return;
        }
        
        if (skillName.equals("Shadow Step") && requiresTarget && targetsOwnBoard) {
            waitingForShadowStepSource = true;
            currentSkillPlayer = playerNumber;
            currentSkillNumber = skillNumber;
            currentSkillName = skillName;
            skillTargetsOwnBoard = true;
            shadowStepSourceX = -1;
            shadowStepSourceY = -1;
            updateStatusMessage("🌑 Click on a ship on YOUR board to teleport!", Color.YELLOW);
            return;
        }
        
        if (requiresTarget) {
            waitingForSkillTarget = true;
            currentSkillPlayer = playerNumber;
            currentSkillNumber = skillNumber;
            currentSkillName = skillName;
            skillTargetsOwnBoard = targetsOwnBoard;
            
            if (requiresDirection) {
                waitingForDirection = true;
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
                    waitingForDirection = false;
                    String targetBoard = targetsOwnBoard ? "YOUR board" : "ENEMY board";
                    updateStatusMessage("Click on " + targetBoard + " to target " + skillName + "!", Color.YELLOW);
                } else {
                    waitingForSkillTarget = false;
                }
            } else {
                String targetBoard = targetsOwnBoard ? "YOUR board" : "ENEMY board";
                updateStatusMessage("Click on " + targetBoard + " to target " + skillName + "!", Color.YELLOW);
            }
        } else {
            boolean success = game.useCharacterSkill(playerNumber, skillNumber, 0, 0, false);
            if (success) {
                updateStatusMessage(skillName + " used successfully!", Color.GREEN);
                refreshBoards();
                showPlayerSkills(playerNumber);
                if (currentSkillPanel != null) {
                    currentSkillPanel.updateUI();
                }
            } else {
                updateStatusMessage("Failed to use " + skillName + "! Check mana/cooldown.", Color.RED);
            }
        }
    }
    
    private void handleEnemyBoardClick(int playerNumber, int row, int col) {
        if (waitingForSkillTarget && currentSkillPlayer == playerNumber && !skillTargetsOwnBoard) {
            handleSkillTarget(row, col);
            return;
        }
        
        if (waitingForSkillTarget && currentSkillPlayer == playerNumber && skillTargetsOwnBoard) {
            updateStatusMessage("This skill targets YOUR board! Click on your fleet.", Color.RED);
            return;
        }
        
        handleShot(playerNumber, row, col);
    }
    
    private void handleOwnBoardClick(int playerNumber, int row, int col) {
        if (waitingForShadowStepSource && currentSkillPlayer == playerNumber && shadowStepSourceX == -1) {
            shadowStepSourceX = row;
            shadowStepSourceY = col;
            waitingForShadowStepSource = false;
            updateStatusMessage("🌑 Now click on destination on YOUR board!", Color.YELLOW);
            return;
        }
        
        if (waitingForShadowStepSource && currentSkillPlayer == playerNumber && shadowStepSourceX != -1) {
            boolean success = game.useShadowStep(playerNumber, shadowStepSourceX, shadowStepSourceY, row, col);
            if (success) {
                updateStatusMessage("🌑 Shadow Step successful! Ship teleported!", Color.GREEN);
                refreshBoards();
                showPlayerSkills(playerNumber);
            } else {
                updateStatusMessage("Failed to use Shadow Step!", Color.RED);
            }
            waitingForShadowStepSource = false;
            shadowStepSourceX = -1;
            shadowStepSourceY = -1;
            waitingForSkillTarget = false;
            return;
        }
        
        if (waitingForSkillTarget && currentSkillPlayer == playerNumber && skillTargetsOwnBoard) {
            handleSkillTarget(row, col);
            return;
        }
        
        if (waitingForSkillTarget && currentSkillPlayer == playerNumber && !skillTargetsOwnBoard) {
            updateStatusMessage("This skill targets ENEMY board! Click on enemy waters.", Color.RED);
            return;
        }
        
        updateStatusMessage("⚠️ Cannot fire at your own ships! Click on ENEMY waters.", Color.ORANGE);
    }
    
    private void handleSkillTarget(int row, int col) {
        if (!waitingForSkillTarget) return;
        
        boolean success = game.useCharacterSkill(currentSkillPlayer, currentSkillNumber, row, col, skillDirectionHorizontal);
        
        if (success) {
            updateStatusMessage(currentSkillName + " used successfully!", Color.GREEN);
            refreshBoards();
            if (currentSkillPanel != null) {
                currentSkillPanel.updateUI();
            }
            showPlayerSkills(currentSkillPlayer);
        } else {
            updateStatusMessage("Failed to use " + currentSkillName + "! Check mana/cooldown.", Color.RED);
        }
        
        waitingForSkillTarget = false;
        currentSkillPlayer = 0;
        currentSkillNumber = 0;
        currentSkillName = "";
        skillTargetsOwnBoard = false;
    }
    
    private JPanel createBoardPanel(String title, Board board, boolean showShips, boolean isPlayerBoard) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 25, 112));
        
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.NORTH);
        
        BoardPanel boardPanel = new BoardPanel(isPlayerBoard, board, showShips);
        panel.add(boardPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void handleShot(int playerNumber, int row, int col) {
        boolean wasPlayer1Turn = game.isPlayer1Turn();
        
        ShotResult result = game.fire(playerNumber, row, col);
        
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
        
        refreshBoards();
        updateShipCounts();
        
        if (wasPlayer1Turn != game.isPlayer1Turn()) {
            updateBoardViews();
        } else {
            updateTurnDisplay();
            if (currentSkillPanel != null) {
                currentSkillPanel.updateUI();
            }
        }
        
        if (game.isGameOver()) {
            String winner = game.getWinner();
            updateStatusMessage("GAME OVER! " + winner + " WINS!", Color.YELLOW);
            
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
    
    private void updateShipCounts() {
        player1ShipsLabel.setText(getShipCount(game.getPlayer1Board()));
        player2ShipsLabel.setText(getShipCount(game.getPlayer2Board()));
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
                if (game.isGameOver()) return;
                statusLabel.setText("Click on enemy waters to fire!");
                statusLabel.setForeground(Color.WHITE);
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
}