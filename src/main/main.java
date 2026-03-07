package main;

import gui.*;
import models.Board;
import ai.AIPlayer;
import game.ShotResult;
import java.awt.*;
import javax.swing.*;

public class Main {
    private static JFrame frame;
    private static Board playerBoard;
    private static AIPlayer aiPlayer;
    private static BoardPanel playerBoardPanel;
    private static BoardPanel enemyBoardPanel;
    private static JLabel statusLabel;
    private static boolean playerTurn = true;
    
    public static void main(String[] args) {
        frame = new JFrame("🌊 Tidebound - Naval Battle 🌊");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        showPlacementScreen();
    }
    
    private static void showPlacementScreen() {
        PlacementPanel placementPanel = new PlacementPanel(new PlacementPanel.PlacementListener() {
            @Override
            public void onPlacementComplete(Board board) {
                playerBoard = board;
                showDifficultyScreen();
            }
        });
        
        frame.getContentPane().removeAll();
        frame.add(placementPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private static void showDifficultyScreen() {
        DifficultyPanel difficultyPanel = new DifficultyPanel(new DifficultyPanel.DifficultyListener() {
            @Override
            public void onDifficultySelected(String difficulty) {
                aiPlayer = new AIPlayer(difficulty);
                showGameScreen(difficulty);
            }
        });
        
        frame.getContentPane().removeAll();
        frame.add(difficultyPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.revalidate();
        frame.repaint();
    }
    
    private static void showGameScreen(String difficulty) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());
        
        
       JLabel titleLabel = new JLabel("🌊 TIDEBOUND - " + difficulty + " MODE 🌊", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.add(new JLabel("YOUR FLEET", SwingConstants.CENTER), BorderLayout.NORTH);
        playerBoardPanel = new BoardPanel(true, playerBoard);
        playerPanel.add(playerBoardPanel, BorderLayout.CENTER);
        
        
        JPanel enemyPanel = new JPanel(new BorderLayout());
        enemyPanel.add(new JLabel("ENEMY WATERS", SwingConstants.CENTER), BorderLayout.NORTH);
        enemyBoardPanel = new BoardPanel(false, aiPlayer.getBoard());
        
        
        enemyBoardPanel.setEnemyClickHandler(new BoardPanel.EnemyClickHandler() {
            @Override
            public void onEnemyCellClicked(int row, int col) {
                if (playerTurn) {
                    handlePlayerTurn(row, col);
                }
            }
        });
        
        enemyPanel.add(enemyBoardPanel, BorderLayout.CENTER);
        
        boardsPanel.add(playerPanel);
        boardsPanel.add(enemyPanel);
        
        
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        statusLabel = new JLabel("YOUR TURN - Click on enemy waters!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusPanel.add(statusLabel);
        
        frame.add(titleLabel, BorderLayout.NORTH);
        frame.add(boardsPanel, BorderLayout.CENTER);
        frame.add(statusPanel, BorderLayout.SOUTH);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.revalidate();
        frame.repaint();
        
        System.out.println("⚓ Battle started against " + difficulty + " AI!");
    }
    
    private static void handlePlayerTurn(int row, int col) {
        
        ShotResult result = aiPlayer.getBoard().fire(row, col);
        enemyBoardPanel.updateCell(row, col, result);
        
        
        if (aiPlayer.allShipsSunk()) {
            JOptionPane.showMessageDialog(frame, 
                "🎉 VICTORY! The tides are with you! 🎉", 
                "Tidebound Victor", 
                JOptionPane.INFORMATION_MESSAGE);
            showPlacementScreen(); 
            return;
        }
        
        
        playerTurn = false;
        statusLabel.setText("AI'S TURN - Thinking...");
        statusLabel.setForeground(Color.RED);
        
        
        Timer timer = new Timer(1000, e -> {
            aiTurn();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private static void aiTurn() {
        
        int[] move = aiPlayer.getNextMove();
        int x = move[0];
        int y = move[1];
        
        
        ShotResult result = playerBoard.fire(x, y);
        aiPlayer.processResult(x, y, result);
        playerBoardPanel.updateCell(x, y, result);
        
        
        System.out.println("AI fired at (" + x + ", " + y + ") - " + result);
        
        
        if (playerBoard.allShipsSunk()) {
            JOptionPane.showMessageDialog(frame, 
                "💀 The tides have turned against you... 💀", 
                "Tidebound Defeat", 
                JOptionPane.ERROR_MESSAGE);
            showPlacementScreen(); 
            return;
        }
        
        
        playerTurn = true;
        statusLabel.setText("YOUR TURN - Click on enemy waters!");
        statusLabel.setForeground(Color.BLACK);
    }
}
