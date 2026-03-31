package main;

import gui.*;
import models.Board;
import ai.AIPlayer;
import characters.GameCharacter;
import game.LocalMultiplayer;
import game.ShotResult;
import java.awt.*;
import javax.swing.*;
import characters.*;
import campaign.CampaignMode;

public class Main {
    private static JFrame frame;
    private static Board playerBoard;
    private static AIPlayer aiPlayer;
    private static BoardPanel playerBoardPanel;
    private static BoardPanel enemyBoardPanel;
    private static JLabel statusLabel;
    private static boolean playerTurn = true;
    private static String selectedDifficulty = "Medium";
    private static GameCharacter selectedCharacter;  
    private static SkillPanel skillPanel;
 
    public static void main(String[] args) {
        frame = new JFrame("🌊 Tidebound - Naval Battle 🌊");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(900, 700));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);

        showMainMenu();
    }

    public static void showMainMenu() {
        MainMenuPanel menuPanel = new MainMenuPanel(new MainMenuPanel.MenuListener() {
            @Override
            public void onStartGame() {
                showCharacterSelectForCampaign();  
            }

        @Override
public void on1v1Mode() {
    startLocalMultiplayer();
}

private static void startLocalMultiplayer() {
    
    frame.getContentPane().removeAll();
    frame.setLayout(new BorderLayout());
    
    
    showMultiplayerPlacementScreen();
}

private static void showMultiplayerPlacementScreen() {
    
    
    JPanel placementContainer = new JPanel(new CardLayout());
    placementContainer.setBackground(new Color(25, 25, 112));
    
    
    MultiplayerPlacementPanel player1Placement = new MultiplayerPlacementPanel(1, "PLAYER 1", 
        new MultiplayerPlacementPanel.PlacementListener() {
            @Override
            public void onPlacementComplete(int playerNumber, Board board) {
                
                player1Board = board;
                
                CardLayout cl = (CardLayout) placementContainer.getLayout();
                cl.next(placementContainer);
            }
            
            @Override
            public void onAllPlayersReady() {
                
            }
        });
    
    
    MultiplayerPlacementPanel player2Placement = new MultiplayerPlacementPanel(2, "PLAYER 2",
        new MultiplayerPlacementPanel.PlacementListener() {
            @Override
            public void onPlacementComplete(int playerNumber, Board board) {
                
                player2Board = board;
                startMultiplayerBattle();
            }
            
            @Override
            public void onAllPlayersReady() {
                
            }
        });
    
    placementContainer.add(player1Placement, "player1");
    placementContainer.add(player2Placement, "player2");
    
    frame.add(placementContainer, BorderLayout.CENTER);
    frame.revalidate();
    frame.repaint();
}

private static Board player1Board;
private static Board player2Board;
private static LocalMultiplayer multiplayerGame;

private static void startMultiplayerBattle() {
    
    multiplayerGame = new LocalMultiplayer(new LocalMultiplayer.GameListener() {
        @Override
        public void onGameStart() {
            System.out.println("1v1 Game Started!");
        }
        
        @Override
        public void onPlayerTurn(int playerNumber) {
            System.out.println("Player " + playerNumber + "'s turn");
        }
        
        @Override
        public void onShotFired(int playerNumber, int x, int y, ShotResult result) {
            System.out.println("Player " + playerNumber + " fired at (" + x + "," + y + "): " + result);
        }
        
        @Override
        public void onGameEnd(int winnerPlayerNumber) {
            JOptionPane.showMessageDialog(frame,
                "🏆 PLAYER " + winnerPlayerNumber + " WINS! 🏆\n\nPlay again?",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE);
            showMainMenu();
        }
        
        @Override
        public void onBoardUpdate(Board board, boolean isPlayer1) {
            
        }
    });
    
    
    
    
    multiplayerGame.setPlayerBoard(1, player1Board);
    multiplayerGame.setPlayerBoard(2, player2Board);
    
    
    MultiplayerBattlePanel battlePanel = new MultiplayerBattlePanel(multiplayerGame);
    
    frame.getContentPane().removeAll();
    frame.add(battlePanel, BorderLayout.CENTER);
    frame.revalidate();
    frame.repaint();
}
            
            @Override
            public void onOptions() {
                
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

    
    private static void showCharacterSelectForCampaign() {
        CharacterSelectPanel charPanel = new CharacterSelectPanel(character -> {
            selectedCharacter = character;
            System.out.println("🎮 Starting CAMPAIGN with: " + character.getName());
            
            
            CampaignMode campaign = new CampaignMode(frame, selectedCharacter);
            campaign.start();
        });
        
        frame.getContentPane().removeAll();
        frame.add(charPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    
    private static void showCharacterSelectForQuickBattle() {
        CharacterSelectPanel charPanel = new CharacterSelectPanel(character -> {
            selectedCharacter = character;
            System.out.println("⚡ Starting QUICK BATTLE with: " + character.getName());
            showPlacementScreen();
        });
        
        frame.getContentPane().removeAll();
        frame.add(charPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
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
        System.out.println("🎮 Starting game with: " + 
            (selectedCharacter != null ? selectedCharacter.getName() : "NO CHARACTER"));
        
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

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(new Color(25, 25, 112));
        splitPane.setDividerLocation(700); 

        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsPanel.setBackground(new Color(25, 25, 112));
        boardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.setBackground(new Color(25, 25, 112));
        JLabel playerLabel = new JLabel("YOUR FLEET", SwingConstants.CENTER);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        playerLabel.setForeground(Color.WHITE);
        playerPanel.add(playerLabel, BorderLayout.NORTH);
        playerBoardPanel = new BoardPanel(true, playerBoard,true);
        playerPanel.add(playerBoardPanel, BorderLayout.CENTER);

        
        JPanel enemyPanel = new JPanel(new BorderLayout());
        enemyPanel.setBackground(new Color(25, 25, 112));
        JLabel enemyLabel = new JLabel("ENEMY WATERS", SwingConstants.CENTER);
        enemyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        enemyLabel.setForeground(Color.WHITE);
        enemyPanel.add(enemyLabel, BorderLayout.NORTH);
        enemyBoardPanel = new BoardPanel(false, aiPlayer.getBoard(), false);

        enemyBoardPanel.setEnemyClickHandler(new BoardPanel.EnemyClickHandler() {
            @Override
            public void onEnemyCellClicked(int row, int col) {
                if (playerTurn) {
                    if (skillPanel != null && skillPanel.getPendingTargetCallback() != null) {
                        skillPanel.getPendingTargetCallback().onTargetSelected(row, col);
                    } else {
                        handlePlayerTurn(row, col);
                    }
                }
            }
        });

        enemyPanel.add(enemyBoardPanel, BorderLayout.CENTER);
        boardsPanel.add(playerPanel);
        boardsPanel.add(enemyPanel);
        splitPane.setLeftComponent(boardsPanel);

        
        if (selectedCharacter != null) {
            skillPanel = new SkillPanel(selectedCharacter);
            skillPanel.setBoards(playerBoardPanel, enemyBoardPanel);
            skillPanel.setPreferredSize(new Dimension(250, 600));
            splitPane.setRightComponent(skillPanel);
        } else {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(new Color(25, 25, 112));
            emptyPanel.setPreferredSize(new Dimension(250, 600));
            splitPane.setRightComponent(emptyPanel);
        }

        
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(25, 25, 112));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        statusLabel = new JLabel("YOUR TURN - Click on enemy waters!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(Color.WHITE);
        statusPanel.add(statusLabel);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(statusPanel, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();

        System.out.println("⚓ Battle started against " + difficulty + " AI!");
    }

    private static void handlePlayerTurn(int row, int col) {
        ShotResult result = aiPlayer.getBoard().fire(row, col);
        enemyBoardPanel.updateCell(row, col, result);

        if (skillPanel != null) {
            skillPanel.updateUI();
        }

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

        if (skillPanel != null) {
            skillPanel.updateUI();
        }

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
