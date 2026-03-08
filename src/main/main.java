package main;

import gui.*;
import models.Board;
import ai.AIPlayer;
import game.ShotResult;
import java.awt.*;
import javax.swing.*;

public class main {
    private static JFrame frame;
    private static Board playerBoard;
    private static AIPlayer aiPlayer;
    private static BoardPanel playerBoardPanel;
    private static BoardPanel enemyBoardPanel;
    private static JLabel statusLabel;
    private static boolean playerTurn = true;
    private static String selectedDifficulty = "Medium";

    public static void main(String[] args) {
        frame = new JFrame("🌊 Tidebound - Naval Battle 🌊");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(900, 700));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);

        showMainMenu();
    }

    private static void showMainMenu() {
        MainMenuPanel menuPanel = new MainMenuPanel(new MainMenuPanel.MenuListener() {
            @Override
            public void onStartGame() {
                showPlacementScreen();
            }

            @Override
            public void on1v1Mode() {
                JOptionPane.showMessageDialog(frame,
                        "🌊 1v1 Multiplayer coming soon!\n\nPlay against AI for now.",
                        "Coming Soon",
                        JOptionPane.INFORMATION_MESSAGE);
                showPlacementScreen();
            }

            @Override
            public void onOptions() {
                // showOptionsScreen(); //
            }

            @Override
            public void onExit() {
                int confirm = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to exit?",
                        "Exit Tidebound",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        frame.getContentPane().removeAll();
        frame.add(menuPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
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
        frame.revalidate();
        frame.repaint();
    }

    private static void showDifficultyScreen() {
        DifficultyPanel difficultyPanel = new DifficultyPanel(new DifficultyPanel.DifficultyListener() {
            @Override
            public void onDifficultySelected(String difficulty) {
                selectedDifficulty = difficulty;
                aiPlayer = new AIPlayer(difficulty);
                showGameScreen(difficulty);
            }
        });

        frame.getContentPane().removeAll();
        frame.add(difficultyPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private static void showGameScreen(String difficulty) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(25, 25, 112));

        JButton menuButton = new JButton("🏠 MENU");
        menuButton.setFont(new Font("Arial", Font.BOLD, 14));
        menuButton.setBackground(new Color(70, 130, 180));
        menuButton.setForeground(Color.WHITE);
        menuButton.addActionListener(e -> showMainMenu());
        topPanel.add(menuButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("🌊 TIDEBOUND - " + difficulty + " MODE 🌊", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(173, 216, 230));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        topPanel.add(new JPanel(), BorderLayout.EAST);

        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsPanel.setBackground(new Color(25, 25, 112));
        boardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.setBackground(new Color(25, 25, 112));
        JLabel playerLabel = new JLabel("YOUR FLEET", SwingConstants.CENTER);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        playerLabel.setForeground(Color.WHITE);
        playerPanel.add(playerLabel, BorderLayout.NORTH);
        playerBoardPanel = new BoardPanel(true, playerBoard);
        playerPanel.add(playerBoardPanel, BorderLayout.CENTER);

        JPanel enemyPanel = new JPanel(new BorderLayout());
        enemyPanel.setBackground(new Color(25, 25, 112));
        JLabel enemyLabel = new JLabel("ENEMY WATERS", SwingConstants.CENTER);
        enemyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        enemyLabel.setForeground(Color.WHITE);
        enemyPanel.add(enemyLabel, BorderLayout.NORTH);
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
        statusPanel.setBackground(new Color(25, 25, 112));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        statusLabel = new JLabel("YOUR TURN - Click on enemy waters!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(Color.WHITE);
        statusPanel.add(statusLabel);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(boardsPanel, BorderLayout.CENTER);
        frame.add(statusPanel, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();

        System.out.println("⚓ Battle started against " + difficulty + " AI!");
    }

    private static void handlePlayerTurn(int row, int col) {

        ShotResult result = aiPlayer.getBoard().fire(row, col);
        enemyBoardPanel.updateCell(row, col, result);

        if (aiPlayer.allShipsSunk()) {
            int playAgain = JOptionPane.showConfirmDialog(frame,
                    "🎉 VICTORY! The tides are with you! 🎉\n\nPlay again?",
                    "Tidebound Victor",
                    JOptionPane.YES_NO_OPTION);
            if (playAgain == JOptionPane.YES_OPTION) {
                showMainMenu();
            } else {
                System.exit(0);
            }
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
            int playAgain = JOptionPane.showConfirmDialog(frame,
                    "💀 The tides have turned against you... 💀\n\nPlay again?",
                    "Tidebound Defeat",
                    JOptionPane.YES_NO_OPTION);
            if (playAgain == JOptionPane.YES_OPTION) {
                showMainMenu();
            } else {
                System.exit(0);
            }
            return;
        }

        playerTurn = true;
        statusLabel.setText("YOUR TURN - Click on enemy waters!");
        statusLabel.setForeground(Color.WHITE);
    }
}
