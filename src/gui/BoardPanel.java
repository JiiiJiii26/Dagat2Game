package gui;

import game.ShotResult;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
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
    private int cellWidth = 90;   // CUSTOMIZABLE
    private int cellHeight = 58;   // CUSTOMIZABLE
    private boolean isPlayerBoard;  
    private boolean showShips;  
    private PlayerClickHandler playerClickHandler;
    private List<Image> oceanFrames;
    private int currentFrameIndex = 0;
    private Timer animationTimer;
    private ImageIcon carrierIcon;
    private Image carrierImageRaw;
    private Image battleshipImageRaw;
    private Image cruiserImageRaw;
    private Image submarineImageRaw;
    private Image destroyerImageRaw;
    private int carrierOffset = 0;
    private int carrierDirection = 1;
    private int battleshipOffset = 0;
    private int battleshipDirection = 1;
    private int cruiserOffset = 0;
    private int cruiserDirection = 1;
    private int submarineOffset = 0;
    private int submarineDirection = 1;
    private int destroyerOffset = 0;
    private int destroyerDirection = 1;

    public interface PlayerClickHandler {
        void onPlayerCellClicked(int row, int col);
    }
    
    public void setPlayerClickHandler(PlayerClickHandler handler) {
        this.playerClickHandler = handler;
    }
    
    // Constructor with custom cell size
    public BoardPanel(boolean isPlayerBoard, Board board, int cellWidth, int cellHeight) {
        this(isPlayerBoard, board, isPlayerBoard);
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        reinitializeLayout();
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
                    carrierImageRaw = img;
                    System.out.println("✅ Carrier image loaded");
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

        // Load battleship image
        try {
            java.io.File battleshipFile = new java.io.File("D:\\GameProj\\Battleship Game\\assets\\battleship.png");
            if (battleshipFile.exists()) {
                Image img = javax.imageio.ImageIO.read(battleshipFile);
                if (img != null) {
                    battleshipImageRaw = img;
                    System.out.println("✅ Battleship image loaded");
                } else {
                    System.out.println("⚠️ Battleship image is null after reading");
                }
            } else {
                System.out.println("⚠️ Battleship file not found: " + battleshipFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load battleship image: " + e.getMessage());
        }

        // Battleship bobbing animation (slightly different timing)
        Timer battleshipTimer = new Timer(120, e -> {
            battleshipOffset += battleshipDirection;
            if (battleshipOffset > 2 || battleshipOffset < -2) {
                battleshipDirection *= -1;
            }
            repaint();
        });
        battleshipTimer.start();

        // Load cruiser image
        try {
            java.io.File cruiserFile = new java.io.File("D:\\GameProj\\Battleship Game\\assets\\cruiser.png");
            if (cruiserFile.exists()) {
                Image img = javax.imageio.ImageIO.read(cruiserFile);
                if (img != null) {
                    cruiserImageRaw = img;
                    System.out.println("✅ Cruiser image loaded");
                } else {
                    System.out.println("⚠️ Cruiser image is null after reading");
                }
            } else {
                System.out.println("⚠️ Cruiser file not found: " + cruiserFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load cruiser image: " + e.getMessage());
        }

        // Cruiser bobbing animation
        Timer cruiserTimer = new Timer(110, e -> {
            cruiserOffset += cruiserDirection;
            if (cruiserOffset > 2 || cruiserOffset < -2) {
                cruiserDirection *= -1;
            }
            repaint();
        });
        cruiserTimer.start();

        // Load submarine image
        try {
            java.io.File submarineFile = new java.io.File("D:\\GameProj\\Battleship Game\\assets\\submarine.png");
            if (submarineFile.exists()) {
                Image img = javax.imageio.ImageIO.read(submarineFile);
                if (img != null) {
                    submarineImageRaw = img;
                    System.out.println("✅ Submarine image loaded");
                } else {
                    System.out.println("⚠️ Submarine image is null after reading");
                }
            } else {
                System.out.println("⚠️ Submarine file not found: " + submarineFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load submarine image: " + e.getMessage());
        }

        // Submarine bobbing animation
        Timer submarineTimer = new Timer(130, e -> {
            submarineOffset += submarineDirection;
            if (submarineOffset > 2 || submarineOffset < -2) {
                submarineDirection *= -1;
            }
            repaint();
        });
        submarineTimer.start();

        // Load destroyer image
        try {
            java.io.File destroyerFile = new java.io.File("D:\\GameProj\\Battleship Game\\assets\\destroyer.png");
            if (destroyerFile.exists()) {
                Image img = javax.imageio.ImageIO.read(destroyerFile);
                if (img != null) {
                    destroyerImageRaw = img;
                    System.out.println("✅ Destroyer image loaded");
                } else {
                    System.out.println("⚠️ Destroyer image is null after reading");
                }
            } else {
                System.out.println("⚠️ Destroyer file not found: " + destroyerFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load destroyer image: " + e.getMessage());
        }

        // Destroyer bobbing animation
        Timer destroyerTimer = new Timer(115, e -> {
            destroyerOffset += destroyerDirection;
            if (destroyerOffset > 2 || destroyerOffset < -2) {
                destroyerDirection *= -1;
            }
            repaint();
        });
        destroyerTimer.start();

        initializeLayout();
    }
    
    private void initializeLayout() {
        setLayout(null);
        int panelWidth = SIZE * cellWidth;
        int panelHeight = SIZE * cellHeight;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        setMinimumSize(new Dimension(panelWidth, panelHeight));
        setBounds(0, 0, panelWidth, panelHeight);
        
        // Create grid buttons
        gridButtons = new JButton[SIZE][SIZE];
        
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = board.getCell(row, col);
                JButton button = createCellButton(row, col, cell);
                button.setBounds(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                updateButtonAppearance(button, cell, row, col);
                gridButtons[row][col] = button;
                add(button);
            }
        }
    }
    
    private void reinitializeLayout() {
        removeAll();
        initializeLayout();
        revalidate();
        repaint();
    }
    
    // Setters for custom sizing after creation
    public void setCellSize(int width, int height) {
        this.cellWidth = width;
        this.cellHeight = height;
        reinitializeLayout();
    }
    
    public void setCellWidth(int width) {
        this.cellWidth = width;
        reinitializeLayout();
    }
    
    public void setCellHeight(int height) {
        this.cellHeight = height;
        reinitializeLayout();
    }
    
    // Getters
    public int getCellWidth() { return cellWidth; }
    public int getCellHeight() { return cellHeight; }
    public int getBoardWidth() { return SIZE * cellWidth; }
    public int getBoardHeight() { return SIZE * cellHeight; }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the ocean background first
        if (!oceanFrames.isEmpty()) {
            Image img = oceanFrames.get(currentFrameIndex);
            if (img != null) {
                // Draw scaled to fill the entire panel
                g.drawImage(img, 0, 0, getBoardWidth(), getBoardHeight(), this);
            }
        } else {
            // Fallback
            g.setColor(new Color(20, 40, 60));
            g.fillRect(0, 0, getBoardWidth(), getBoardHeight());
        }
        
        // Draw carrier image - spanning ship cells
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
                        boolean isHorizontal = (maxY - minY) > (maxX - minX);
                        int shipWidth, shipHeight;
                        // Make horizontal and vertical ships the same size
                        if (isHorizontal) {
                            shipWidth = (maxY - minY + 1) * cellWidth;
                            shipHeight = (int)(cellHeight * 1.7);
                        } else {
                            shipWidth = (int)(cellWidth * 1.7);
                            shipHeight = (maxX - minX + 1) * cellHeight;
                        }
                        int drawX = minY * cellWidth;
                        int drawY = minX * cellHeight + carrierOffset;
                        
                        // For vertical ships, center the image properly
                        if (!isHorizontal && carrierImageRaw != null) {
                            // Calculate offset to center the rotated image
                            int extraWidth = shipWidth - cellWidth;
                            int offsetX = extraWidth / 2;
                            
                            // Create rotated version of the image
                            int srcW = carrierImageRaw.getWidth(null);
                            int srcH = carrierImageRaw.getHeight(null);
                            BufferedImage rotatedCarrier = new BufferedImage(srcH, srcW, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2d = rotatedCarrier.createGraphics();
                            g2d.translate(srcH / 2, srcW / 2);
                            g2d.rotate(Math.PI / 2);
                            g2d.drawImage(carrierImageRaw, -srcW / 2, -srcH / 2, null);
                            g2d.dispose();
                            
                            // Scale to fit
                            Image scaledRotated = rotatedCarrier.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                            g.drawImage(scaledRotated, minY * cellWidth - offsetX, drawY, null);
                        } else if (carrierImageRaw != null) {
                            // For horizontal ships, center vertically
                            int extraHeight = (int)(cellHeight * 1.7) - cellHeight;
                            int offsetY = extraHeight / 2;
                            int centeredDrawY = drawY - offsetY;
                            
                            Image scaledCarrier = carrierImageRaw.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                            g.drawImage(scaledCarrier, drawX, centeredDrawY, null);
                        }
                    }
                    break;
                }
            }
            
            // Draw battleship image
            if (battleshipImageRaw != null && !board.getShips().isEmpty()) {
                for (Ship ship : board.getShips()) {
                    if ("Battleship".equals(ship.getName()) && !ship.isSunk()) {
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
                            // Determine ship orientation
                            boolean isHorizontal = (maxY - minY) > (maxX - minX);
                            int shipWidth, shipHeight;
                            // Make horizontal and vertical ships the same size
                            if (isHorizontal) {
                                shipWidth = (maxY - minY + 1) * cellWidth;
                                shipHeight = (int)(cellHeight * 1.7);
                            } else {
                                shipWidth = (int)(cellWidth * 1.7);
                                shipHeight = (maxX - minX + 1) * cellHeight;
                            }
                            int drawX = minY * cellWidth;
                            int drawY = minX * cellHeight + battleshipOffset;
                            
                            // For vertical ships, center the image properly
                            if (!isHorizontal && battleshipImageRaw != null) {
                                // Calculate offset to center the rotated image
                                int extraWidth = shipWidth - cellWidth;
                                int offsetX = extraWidth / 2;
                                
                                // Create rotated version of the image
                                int srcW = battleshipImageRaw.getWidth(null);
                                int srcH = battleshipImageRaw.getHeight(null);
                                BufferedImage rotatedBattleship = new BufferedImage(srcH, srcW, BufferedImage.TYPE_INT_ARGB);
                                Graphics2D g2d = rotatedBattleship.createGraphics();
                                g2d.translate(srcH / 2, srcW / 2);
                                g2d.rotate(Math.PI / 2);
                                g2d.drawImage(battleshipImageRaw, -srcW / 2, -srcH / 2, null);
                                g2d.dispose();
                                
                                // Scale to fit
                                Image scaledRotated = rotatedBattleship.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                                g.drawImage(scaledRotated, minY * cellWidth - offsetX, drawY, null);
                            } else if (battleshipImageRaw != null) {
                                // For horizontal ships, center vertically
                                int extraHeight = (int)(cellHeight * 1.7) - cellHeight;
                                int offsetY = extraHeight / 2;
                                int centeredDrawY = drawY - offsetY;
                                
                                Image scaledBattleship = battleshipImageRaw.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                                g.drawImage(scaledBattleship, drawX, centeredDrawY, null);
                            }
                        }
                        break;
                    }
                }
            }
            
            // Draw cruiser image
            if (cruiserImageRaw != null && board != null && !board.getShips().isEmpty()) {
                for (Ship ship : board.getShips()) {
                    if ("Cruiser".equals(ship.getName()) && !ship.isSunk()) {
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
                            // Determine ship orientation
                            boolean isHorizontal = (maxY - minY) > (maxX - minX);
                            int shipWidth, shipHeight;
                            // Make horizontal and vertical ships the same size
                            if (isHorizontal) {
                                shipWidth = (maxY - minY + 1) * cellWidth;
                                shipHeight = (int)(cellHeight * 1.7);
                            } else {
                                shipWidth = (int)(cellWidth * 1.7);
                                shipHeight = (maxX - minX + 1) * cellHeight;
                            }
                            int drawX = minY * cellWidth;
                            int drawY = minX * cellHeight + cruiserOffset;
                            
                            // For vertical ships, center the image properly
                            if (!isHorizontal && cruiserImageRaw != null) {
                                // Calculate offset to center the rotated image
                                int extraWidth = shipWidth - cellWidth;
                                int offsetX = extraWidth / 2;
                                
                                // Create rotated version of the image
                                int srcW = cruiserImageRaw.getWidth(null);
                                int srcH = cruiserImageRaw.getHeight(null);
                                BufferedImage rotatedCruiser = new BufferedImage(srcH, srcW, BufferedImage.TYPE_INT_ARGB);
                                Graphics2D g2d = rotatedCruiser.createGraphics();
                                g2d.translate(srcH / 2, srcW / 2);
                                g2d.rotate(Math.PI / 2);
                                g2d.drawImage(cruiserImageRaw, -srcW / 2, -srcH / 2, null);
                                g2d.dispose();
                                
                                // Scale to fit
                                Image scaledRotated = rotatedCruiser.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                                g.drawImage(scaledRotated, minY * cellWidth - offsetX, drawY, null);
                            } else if (cruiserImageRaw != null) {
                                // For horizontal ships, center vertically
                                int extraHeight = (int)(cellHeight * 1.7) - cellHeight;
                                int offsetY = extraHeight / 2;
                                int centeredDrawY = drawY - offsetY;
                                
                                Image scaledCruiser = cruiserImageRaw.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                                g.drawImage(scaledCruiser, drawX, centeredDrawY, null);
                            }
                        }
                        break;
                    }
                }
            }
            
            // Draw submarine image
            if (submarineImageRaw != null && board != null && !board.getShips().isEmpty()) {
                for (Ship ship : board.getShips()) {
                    if ("Submarine".equals(ship.getName()) && !ship.isSunk()) {
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
                            // Determine ship orientation
                            boolean isHorizontal = (maxY - minY) > (maxX - minX);
                            int shipWidth, shipHeight;
                            // Make horizontal and vertical ships the same size
                            if (isHorizontal) {
                                shipWidth = (maxY - minY + 1) * cellWidth;
                                shipHeight = (int)(cellHeight * 1.7);
                            } else {
                                shipWidth = (int)(cellWidth * 1.7);
                                shipHeight = (maxX - minX + 1) * cellHeight;
                            }
                            int drawX = minY * cellWidth;
                            int drawY = minX * cellHeight + submarineOffset;
                            
                            // For vertical ships, center the image properly
                            if (!isHorizontal && submarineImageRaw != null) {
                                // Calculate offset to center the rotated image
                                int extraWidth = shipWidth - cellWidth;
                                int offsetX = extraWidth / 2;
                                
                                // Create rotated version of the image
                                int srcW = submarineImageRaw.getWidth(null);
                                int srcH = submarineImageRaw.getHeight(null);
                                BufferedImage rotatedSubmarine = new BufferedImage(srcH, srcW, BufferedImage.TYPE_INT_ARGB);
                                Graphics2D g2d = rotatedSubmarine.createGraphics();
                                g2d.translate(srcH / 2, srcW / 2);
                                g2d.rotate(Math.PI / 2);
                                g2d.drawImage(submarineImageRaw, -srcW / 2, -srcH / 2, null);
                                g2d.dispose();
                                
                                // Scale to fit
                                Image scaledRotated = rotatedSubmarine.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                                g.drawImage(scaledRotated, minY * cellWidth - offsetX, drawY, null);
                            } else if (submarineImageRaw != null) {
                                // For horizontal ships, center vertically
                                int extraHeight = (int)(cellHeight * 1.7) - cellHeight;
                                int offsetY = extraHeight / 2;
                                int centeredDrawY = drawY - offsetY;
                                
                                Image scaledSubmarine = submarineImageRaw.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                                g.drawImage(scaledSubmarine, drawX, centeredDrawY, null);
                            }
                        }
                        break;
                    }
                }
            }
            
            // Draw destroyer image
            if (destroyerImageRaw != null && board != null && !board.getShips().isEmpty()) {
                for (Ship ship : board.getShips()) {
                    if ("Destroyer".equals(ship.getName()) && !ship.isSunk()) {
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
                            // Determine ship orientation
                            boolean isHorizontal = (maxY - minY) > (maxX - minX);
                            int shipWidth, shipHeight;
                            // Make horizontal and vertical ships the same size
                            if (isHorizontal) {
                                shipWidth = (maxY - minY + 1) * cellWidth;
                                shipHeight = (int)(cellHeight * 1.7);
                            } else {
                                shipWidth = (int)(cellWidth * 1.7);
                                shipHeight = (maxX - minX + 1) * cellHeight;
                            }
                            int drawX = minY * cellWidth;
                            int drawY = minX * cellHeight + destroyerOffset;
                            
                            // For vertical ships, center the image properly
                            if (!isHorizontal && destroyerImageRaw != null) {
                                // Calculate offset to center the rotated image
                                int extraWidth = shipWidth - cellWidth;
                                int offsetX = extraWidth / 2;
                                
                                // Create rotated version of the image
                                int srcW = destroyerImageRaw.getWidth(null);
                                int srcH = destroyerImageRaw.getHeight(null);
                                BufferedImage rotatedDestroyer = new BufferedImage(srcH, srcW, BufferedImage.TYPE_INT_ARGB);
                                Graphics2D g2d = rotatedDestroyer.createGraphics();
                                g2d.translate(srcH / 2, srcW / 2);
                                g2d.rotate(Math.PI / 2);
                                g2d.drawImage(destroyerImageRaw, -srcW / 2, -srcH / 2, null);
                                g2d.dispose();
                                
                                // Scale to fit
                                Image scaledRotated = rotatedDestroyer.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                                g.drawImage(scaledRotated, minY * cellWidth - offsetX, drawY, null);
                            } else if (destroyerImageRaw != null) {
                                // For horizontal ships, center vertically
                                int extraHeight = (int)(cellHeight * 1.7) - cellHeight;
                                int offsetY = extraHeight / 2;
                                int centeredDrawY = drawY - offsetY;
                                
                                Image scaledDestroyer = destroyerImageRaw.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                                g.drawImage(scaledDestroyer, drawX, centeredDrawY, null);
                            }
                        }
                        break;
                    }
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
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, Math.min(cellWidth, cellHeight) / 3));
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
            button.setFont(new Font("Segoe UI Emoji", Font.BOLD, Math.min(cellWidth, cellHeight) / 2));
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
                } else if (cell.getShip() != null && "Battleship".equals(cell.getShip().getName()) && !cell.getShip().isSunk()) {
                    // Make transparent so battleship image shows through from paintComponent
                    button.setOpaque(false);
                    button.setContentAreaFilled(false);
                } else if (cell.getShip() != null && "Cruiser".equals(cell.getShip().getName()) && !cell.getShip().isSunk()) {
                    // Make transparent so cruiser image shows through from paintComponent
                    button.setOpaque(false);
                    button.setContentAreaFilled(false);
                } else if (cell.getShip() != null && "Submarine".equals(cell.getShip().getName()) && !cell.getShip().isSunk()) {
                    // Make transparent so submarine image shows through from paintComponent
                    button.setOpaque(false);
                    button.setContentAreaFilled(false);
                } else if (cell.getShip() != null && "Destroyer".equals(cell.getShip().getName()) && !cell.getShip().isSunk()) {
                    // Make transparent so destroyer image shows through from paintComponent
                    button.setOpaque(false);
                    button.setContentAreaFilled(false);
                } else {
                    button.setOpaque(true);
                    button.setContentAreaFilled(true);
                    button.setBackground(new Color(50, 150, 50, 200));
                    button.setText("⛵");
                }
                button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, Math.min(cellWidth, cellHeight) / 4));
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