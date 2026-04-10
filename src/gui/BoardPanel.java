package gui;

import game.ShotResult;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import models.Board;
import models.Cell;
import models.Ship;

public class BoardPanel extends JPanel {
    private JButton[][] gridButtons;
    private Board board;
    private final int SIZE = 10;
    private boolean isPlayerBoard;  
    private boolean showShips;  
    private PlayerClickHandler playerClickHandler;
    private List<Image> oceanFrames;
    private int currentFrameIndex = 0;
    private Timer animationTimer;
    private ImageIcon carrierIcon;
    private Image carrierImageRaw;
    private final int CELL_SIZE = 90;
    private int carrierOffset = 0;
    private int carrierDirection = 1;

    public interface PlayerClickHandler {
        void onPlayerCellClicked(int row, int col);
    }
    
    public void setPlayerClickHandler(PlayerClickHandler handler) {
        this.playerClickHandler = handler;
    }
    
    public BoardPanel(boolean isPlayerBoard, Board board) {
        this(isPlayerBoard, board, isPlayerBoard);
    }
    
    public BoardPanel(boolean isPlayerBoard, boolean showShips) {
        this(isPlayerBoard, new Board(), showShips);
    }
    
    public BoardPanel(boolean isPlayerBoard, Board board, boolean showShips) {
        this.isPlayerBoard = isPlayerBoard;
        this.board = board;
        this.showShips = showShips;  

        // Load ocean floor JPG frames
        oceanFrames = new ArrayList<>();
        String basePath = "D:\\GameProj\\Battleship Game\\assets\\oceanfloor";
        
        for (int i = 1; i <= 3; i++) {
            try {
                String imagePath = basePath + i + ".jpg";
                java.io.File file = new java.io.File(imagePath);
                if (file.exists()) {
                    Image img = javax.imageio.ImageIO.read(file);
                    if (img != null) {
                        oceanFrames.add(img);
                    }
                }
            } catch (Exception e) {
                // Silent fail
            }
        }
        
        // Start animation timer
        if (!oceanFrames.isEmpty()) {
            animationTimer = new Timer(500, e -> {
                currentFrameIndex = (currentFrameIndex + 1) % oceanFrames.size();
                repaint();
            });
            animationTimer.start();
        }

        // Load carrier image
        try {
            java.io.File carrierFile = new java.io.File("D:\\GameProj\\Battleship Game\\assets\\carrier.png");
            if (carrierFile.exists()) {
                Image img = javax.imageio.ImageIO.read(carrierFile);
                if (img != null) {
                    // Pre-scale to fit 90px cells (5 cells = 450px wide)
                    carrierImageRaw = img.getScaledInstance(450, 90, Image.SCALE_SMOOTH);
                    System.out.println("✅ Carrier image loaded and pre-scaled to 450x90");
                } else {
                    System.out.println("⚠️ Carrier image is null after reading");
                }
            } else {
                System.out.println("⚠️ Carrier file not found: " + carrierFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load carrier image: " + e.getMessage());
        }

        // Carrier bobbing animation
        Timer carrierTimer = new Timer(100, e -> {
            carrierOffset += carrierDirection;
            if (carrierOffset > 3 || carrierOffset < -3) {
                carrierDirection *= -1;
            }
            repaint();
        });
        carrierTimer.start();

        setLayout(null);
        setPreferredSize(new Dimension(900, 900));
        setMinimumSize(new Dimension(900, 900));
        setBounds(0, 0, 900, 900);
        
        // Create grid buttons directly in this panel
        gridButtons = new JButton[SIZE][SIZE];
        
        int cellSize = CELL_SIZE;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = board.getCell(row, col);
                JButton button = createCellButton(row, col, cell);
                button.setBounds(col * cellSize, row * cellSize, cellSize, cellSize);
                updateButtonAppearance(button, cell, row, col);
                gridButtons[row][col] = button;
                add(button);
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the ocean background first
        if (!oceanFrames.isEmpty()) {
            Image img = oceanFrames.get(currentFrameIndex);
            if (img != null) {
                // Draw scaled to fill the entire panel (900x900)
                g.drawImage(img, 0, 0, 900, 900, this);
            }
        } else {
            // Fallback
            g.setColor(new Color(20, 40, 60));
            g.fillRect(0, 0, 900, 900);
        }
        
        // Draw carrier image - spanning all 5 cells
        if (showShips && carrierImageRaw != null && board != null && !board.getShips().isEmpty()) {
            for (Ship ship : board.getShips()) {
                if ("Carrier".equals(ship.getName()) && !ship.isSunk()) {
                    List<Ship.Coordinate> positions = ship.getPositions();
                    if (positions.size() >= 2) {
                        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
                        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
                        for (Ship.Coordinate pos : positions) {
                            minX = Math.min(minX, pos.getX());
                            minY = Math.min(minY, pos.getY());
                            maxX = Math.max(maxX, pos.getX());
                            maxY = Math.max(maxY, pos.getY());
                        }
                        int width = (maxY - minY + 1) * CELL_SIZE;
                        int height = (maxX - minX + 1) * CELL_SIZE;
                        int drawX = minY * CELL_SIZE;
                        int drawY = minX * CELL_SIZE + carrierOffset;
                        Image scaledCarrier = carrierImageRaw.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        g.drawImage(scaledCarrier, drawX, drawY, null);
                    }
                    break;
                }
            }
        }
    }
    
    private JButton createCellButton(int row, int col, Cell cell) {
        JButton button = new JButton();
        
        button.putClientProperty("row", row);
        button.putClientProperty("col", col);
        
        // Transparent button so background shows through
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 100), 1));
        button.setFocusPainted(false);
        button.setRolloverEnabled(false);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        final int finalRow = row;
        final int finalCol = col;
        button.addActionListener(e -> handleClick(finalRow, finalCol));
        
        return button;
    }
    
    private void updateButtonAppearance(JButton button, Cell cell, int row, int col) {
        button.setOpaque(false);
        button.setIcon(null);
        button.setText("");
        
        Color cellColor = cell.getColor();
        
        if (cellColor.equals(Cell.HIT_RED) || cellColor.equals(Cell.INFECTED_HIT)) {
            button.setForeground(Color.WHITE);
            if (cell.hasShip() && cell.getShip() != null && cell.getShip().isSunk()) {
                button.setText("💀");
            } else {
                button.setText("💥");
            }
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setBackground(new Color(180, 50, 40, 200));
        } 
        else if (cellColor.equals(Cell.MISS_GRAY)) {
            button.setForeground(Color.WHITE);
            button.setText("•");
            button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setBackground(new Color(80, 100, 120, 180));
        } 
        else {
            if ((showShips || cell.isRevealed()) && cell.hasShip() && !cell.isFiredUpon()) {
                if (cell.getShip() != null && cell.getShip().isShielded()) {
                    button.setOpaque(true);
                    button.setContentAreaFilled(true);
                    button.setBackground(new Color(50, 150, 50, 200));
                    button.setText("🛡️");
                } else if (cell.getShip() != null && cell.getShip().isInfected()) {
                    button.setOpaque(true);
                    button.setContentAreaFilled(true);
                    button.setBackground(new Color(50, 150, 50, 200));
                    button.setText("🦠");
                } else if (cell.getShip() != null && "Carrier".equals(cell.getShip().getName()) && !cell.getShip().isSunk()) {
                    // Make transparent so carrier image shows through from paintComponent
                    button.setOpaque(false);
                    button.setContentAreaFilled(false);
                } else {
                    button.setOpaque(true);
                    button.setContentAreaFilled(true);
                    button.setBackground(new Color(50, 150, 50, 200));
                    button.setText("⛵");
                }
                button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            }
        }
    }
    
    private void handleClick(int row, int col) {
        if (isPlayerBoard) {
            if (playerClickHandler != null) {
                playerClickHandler.onPlayerCellClicked(row, col);
            }
        } else {
            if (enemyClickHandler != null) {
                enemyClickHandler.onEnemyCellClicked(row, col);
            } else {
                ShotResult result = board.fire(row, col);
                updateCell(row, col, result);
            }
        }
    }
    
    public Board getBoard() {
        return board;
    }
    
    public void updateCell(int row, int col, ShotResult result) {
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
            Cell cell = board.getCell(row, col);
            JButton button = gridButtons[row][col];
            updateButtonAppearance(button, cell, row, col);
        }
    }
    
    public interface EnemyClickHandler {
        void onEnemyCellClicked(int row, int col);
    }
    
    private EnemyClickHandler enemyClickHandler;
    
    public void setEnemyClickHandler(EnemyClickHandler handler) {
        this.enemyClickHandler = handler;
    }
    
    public void refreshColors() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = board.getCell(row, col);
                JButton button = gridButtons[row][col];
                updateButtonAppearance(button, cell, row, col);
            }
        }
        repaint();
    }
}