package gui;

import game.LocalMultiplayer;
import game.ShotResult;
import models.Board;
import models.Cell;
import models.Ship;
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
        
        turnLabel = new JLabel("", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 24));
        turnLabel.setForeground(new Color(173, 216, 230));
        topPanel.add(turnLabel, BorderLayout.CENTER);
        
        
        boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsPanel.setBackground(new Color(25, 25, 112));
        boardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        
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
        add(boardsPanel, BorderLayout.CENTER);
        add(counterPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void updateBoardViews() {
        
        boardsPanel.removeAll();
        
        if (game.isPlayer1Turn()) {
            
            
            
            JPanel player1View = createBoardPanel("YOUR FLEET", game.getPlayer1Board(), true);
            JPanel player2View = createBoardPanel("ENEMY WATERS", game.getPlayer2Board(), false);
            boardsPanel.add(player1View);
            boardsPanel.add(player2View);
            
            
            player1BoardPanel = (BoardPanel) ((JPanel) player1View.getComponent(1));
            player2BoardPanel = (BoardPanel) ((JPanel) player2View.getComponent(1));
            
            
            
            player2BoardPanel.setEnemyClickHandler((row, col) -> {
                handleShot(1, row, col); 
            });
            
            
            player1BoardPanel.setPlayerClickHandler(null);
            
        } else {
            
            
            
            JPanel player2View = createBoardPanel("YOUR FLEET", game.getPlayer2Board(), true);
            JPanel player1View = createBoardPanel("ENEMY WATERS", game.getPlayer1Board(), false);
            boardsPanel.add(player2View);
            boardsPanel.add(player1View);
            
            
            player2BoardPanel = (BoardPanel) ((JPanel) player2View.getComponent(1));
            player1BoardPanel = (BoardPanel) ((JPanel) player1View.getComponent(1));
            
            
            
            player1BoardPanel.setEnemyClickHandler((row, col) -> {
                handleShot(2, row, col); 
            });
            
            
            player2BoardPanel.setPlayerClickHandler(null);
        }
        
        boardsPanel.revalidate();
        boardsPanel.repaint();
        
        
        updateTurnDisplay();
        
        
        player1ShipsLabel.setText(getShipCount(game.getPlayer1Board()));
        player2ShipsLabel.setText(getShipCount(game.getPlayer2Board()));
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
            statusLabel.setText(message);
            statusLabel.setForeground(color);
            
            Timer timer = new Timer(1000, e -> {
                statusLabel.setText("Click on enemy waters to fire!");
                statusLabel.setForeground(Color.WHITE);
            });
            timer.setRepeats(false);
            timer.start();
        }
        
        
        if (player1BoardPanel != null) {
            player1BoardPanel.refreshColors();
        }
        if (player2BoardPanel != null) {
            player2BoardPanel.refreshColors();
        }
        
        
        player1ShipsLabel.setText(getShipCount(game.getPlayer1Board()));
        player2ShipsLabel.setText(getShipCount(game.getPlayer2Board()));
        
        
        if (wasPlayer1Turn != game.isPlayer1Turn()) {
            
            updateBoardViews();
        } else {
            
            updateTurnDisplay();
        }
        
        
        if (game.isGameOver()) {
            String winner = game.getWinner();
            statusLabel.setText("GAME OVER! " + winner + " WINS!");
            statusLabel.setForeground(Color.YELLOW);
            
            
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