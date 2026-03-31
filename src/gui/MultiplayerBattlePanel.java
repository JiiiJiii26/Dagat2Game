package gui;

import game.LocalMultiplayer;
import game.ShotResult;
import models.Board;
import models.Cell;
import java.awt.*;
import models.Ship;
import javax.swing.*;

public class MultiplayerBattlePanel extends JPanel {
    
    private LocalMultiplayer game;
    private BoardPanel player1BoardPanel;
    private BoardPanel player2BoardPanel;
    private JLabel turnLabel;
    private JLabel statusLabel;
    private JLabel player1ShipsLabel;
    private JLabel player2ShipsLabel;
    
    public MultiplayerBattlePanel(LocalMultiplayer game) {
        this.game = game;
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 112));
        
        // Top panel with turn indicator
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(25, 25, 112));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        turnLabel = new JLabel("PLAYER 1'S TURN", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 24));
        turnLabel.setForeground(new Color(173, 216, 230));
        topPanel.add(turnLabel, BorderLayout.CENTER);
        
        // Boards panel
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsPanel.setBackground(new Color(25, 25, 112));
        boardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Player 1 board (left) - shows player 2's shots on player 1's board
        JPanel player1Panel = createBoardPanel("PLAYER 1", game.getPlayer1Board(), true);
        player1BoardPanel = (BoardPanel) ((JPanel) player1Panel.getComponent(1));
        
        // Player 2 board (right) - shows player 1's shots on player 2's board
        JPanel player2Panel = createBoardPanel("PLAYER 2", game.getPlayer2Board(), false);
        player2BoardPanel = (BoardPanel) ((JPanel) player2Panel.getComponent(1));
        
        boardsPanel.add(player1Panel);
        boardsPanel.add(player2Panel);
        
        // Ship counters
        JPanel counterPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        counterPanel.setBackground(new Color(25, 25, 112));
        counterPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        
        player1ShipsLabel = new JLabel(getShipCount(game.getPlayer1Board()), SwingConstants.CENTER);
        player1ShipsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        player1ShipsLabel.setForeground(Color.GREEN);
        
        player2ShipsLabel = new JLabel(getShipCount(game.getPlayer2Board()), SwingConstants.CENTER);
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
        add(boardsPanel, BorderLayout.CENTER);
        add(counterPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Set up click handlers
        setupClickHandlers();
    }
    
    private JPanel createBoardPanel(String title, Board board, boolean isPlayer1) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 25, 112));
        
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.NORTH);
        
        // For the board that shows the player's own ships, we show ships
        BoardPanel boardPanel = new BoardPanel(true, board);
        panel.add(boardPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupClickHandlers() {
        // Player 2's board is where Player 1 clicks to attack
        player2BoardPanel.setEnemyClickHandler((row, col) -> {
            if (game.isPlayer1Turn()) {
                handleShot(1, row, col);
            }
        });
        
        // We need a custom handler for Player 1's board for Player 2's attacks
        // This requires adding a player click handler to BoardPanel
        player1BoardPanel.setPlayerClickHandler((row, col) -> {
            if (!game.isPlayer1Turn()) {
                handleShot(2, row, col);
            }
        });
    }
    
    private void handleShot(int playerNumber, int row, int col) {
        ShotResult result = game.fire(playerNumber, row, col);
        
        // Update display based on result
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
            statusLabel.setText(message);
            statusLabel.setForeground(color);
            
            // Reset status after 1 second
            Timer timer = new Timer(1000, e -> {
                statusLabel.setText("Click on enemy waters to fire!");
                statusLabel.setForeground(Color.WHITE);
            });
            timer.setRepeats(false);
            timer.start();
        }
        
        // Update ship counters
        player1ShipsLabel.setText(getShipCount(game.getPlayer1Board()));
        player2ShipsLabel.setText(getShipCount(game.getPlayer2Board()));
        
        // Update turn display
        updateTurnDisplay();
        
        // Refresh boards
        player1BoardPanel.refreshColors();
        player2BoardPanel.refreshColors();
    }
    
    private void updateTurnDisplay() {
        if (game.isPlayer1Turn()) {
            turnLabel.setText("PLAYER 1'S TURN");
            turnLabel.setForeground(new Color(100, 255, 100));
        } else {
            turnLabel.setText("PLAYER 2'S TURN");
            turnLabel.setForeground(new Color(255, 100, 100));
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
}