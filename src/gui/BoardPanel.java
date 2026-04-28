package gui;

import game.ShotResult;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.Board;
import models.Cell;
import models.Ship;

public class BoardPanel extends JPanel {
    private JButton[][] gridButtons;
    private Board board;
    private final int SIZE = 10;
    private int cellWidth = 90;
    private int cellHeight = 58;
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
    private Image dmgCellImageRaw;
    private int dmgCellOffset = 0;
    private int dmgCellDirection = 1;
    private List<Image> smokeFrames;
    private int currentSmokeFrame = 0;
    private Timer smokeAnimationTimer;
    private List<Image> subDestroyFrames;
    private int currentSubDestroyFrame = 0;
    private Timer subDestroyTimer;
    private boolean subDestroyAnimationPlayed = false;
    private Image destroyerDmgImage;
    private int currentDestroyerDmgFrame = 0;
    private Timer destroyerDmgTimer;
    private boolean destroyerDmgAnimationPlayed = false;
    private Image cruiserDestroyImage;
    private int currentCruiserDestroyFrame = 0;
    private Timer cruiserDestroyTimer;
    private boolean cruiserDestroyAnimationPlayed = false;
    private Image battleshipDmgImage;
    private int currentBattleshipDmgFrame = 0;
    private Timer battleshipDmgTimer;
    private boolean battleshipDmgAnimationPlayed = false;
    private Image carrierDmgImage;
    private int currentCarrierDmgFrame = 0;
    private Timer carrierDmgTimer;
    private boolean carrierDmgAnimationPlayed = false;
    
    private int holdCounter = 0;
    private int holdDelay = 400;
    private int fadeDelay = 50;
    private float currentAlpha = 0f;
    
    private int smokeHoldCounter = 0;
    private int smokeHoldDelay = 200;
    private int smokeFadeDelay = 40;
    private float smokeAlpha = 0f;

    public interface PlayerClickHandler {
        void onPlayerCellClicked(int row, int col);
    }
    
    public void setPlayerClickHandler(PlayerClickHandler handler) {
        this.playerClickHandler = handler;
    }
    
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

        oceanFrames = new ArrayList<>();
        String basePath = "assets/oceanfloor";
        
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
             }
         }
         
        if (!oceanFrames.isEmpty()) {
            final int fadeSteps = 10;
            final int fadeDelay = 50;
            final int holdDelay = 400;
            final int[] holdCounter = {0};
            final float[] currentAlpha = {0f};
            final int[] targetFrame = {1};
            
            animationTimer = new Timer(fadeDelay, e -> {
                if (holdCounter[0] < holdDelay / fadeDelay) {
                    holdCounter[0]++;
                    currentAlpha[0] = 0f;
                } else {
                    currentAlpha[0] += 1f / fadeSteps;
                    if (currentAlpha[0] >= 1f) {
                        currentAlpha[0] = 0f;
                        currentFrameIndex = targetFrame[0];
                        targetFrame[0] = (targetFrame[0] + 1) % oceanFrames.size();
                        holdCounter[0] = 0;
                    }
                }
                repaint();
            });
 
            // animationTimer.start();  // ocean swap disabled
        }

    try {
            java.io.File carrierFile = new java.io.File("assets/carrier.png");
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

        Timer carrierTimer = new Timer(100, e -> {
            carrierOffset += carrierDirection;
            if (carrierOffset > 3 || carrierOffset < -3) {
                carrierDirection *= -1;
            }
            repaint();
        });
        carrierTimer.start();

        try {
            java.io.File battleshipFile = new java.io.File("assets/battleship.png");
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

        Timer battleshipTimer = new Timer(120, e -> {
            battleshipOffset += battleshipDirection;
            if (battleshipOffset > 2 || battleshipOffset < -2) {
                battleshipDirection *= -1;
            }
            repaint();
        });
        battleshipTimer.start();

        try {
            java.io.File cruiserFile = new java.io.File("assets/cruiser.png");
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

        Timer cruiserTimer = new Timer(110, e -> {
            cruiserOffset += cruiserDirection;
            if (cruiserOffset > 2 || cruiserOffset < -2) {
                cruiserDirection *= -1;
            }
            repaint();
        });
        cruiserTimer.start();

        try {
            java.io.File submarineFile = new java.io.File("assets/submarine.png");
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

        Timer submarineTimer = new Timer(130, e -> {
            submarineOffset += submarineDirection;
            if (submarineOffset > 2 || submarineOffset < -2) {
                submarineDirection *= -1;
            }
            repaint();
        });
        submarineTimer.start();

        try {
            java.io.File destroyerFile = new java.io.File("assets/destroyer.png");
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

        Timer destroyerTimer = new Timer(115, e -> {
            destroyerOffset += destroyerDirection;
            if (destroyerOffset > 2 || destroyerOffset < -2) {
                destroyerDirection *= -1;
            }
            repaint();
        });
        destroyerTimer.start();

        try {
            java.io.File dmgCellFile = new java.io.File("assets/dmg_cell.png");
            if (dmgCellFile.exists()) {
                Image img = javax.imageio.ImageIO.read(dmgCellFile);
                if (img != null) {
                    dmgCellImageRaw = img;
                    System.out.println("✅ Damage cell image loaded");
                } else {
                    System.out.println("⚠️ Damage cell image is null after reading");
                }
            } else {
                System.out.println("⚠️ Damage cell file not found: " + dmgCellFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load damage cell image: " + e.getMessage());
        }

        Timer dmgCellTimer = new Timer(100, e -> {
            dmgCellOffset += dmgCellDirection;
            if (dmgCellOffset > 1 || dmgCellOffset < -1) {
                dmgCellDirection *= -1;
            }
            repaint();
        });
        dmgCellTimer.start();

        smokeFrames = new ArrayList<>();
        String smokeBasePath = "assets/smk";
        
        for (int i = 1; i <= 4; i++) {
            try {
                String imagePath = smokeBasePath + i + ".png";
                java.io.File file = new java.io.File(imagePath);
                if (file.exists()) {
                    Image img = javax.imageio.ImageIO.read(file);
                    if (img != null) {
                        smokeFrames.add(img);
                    }
                }
            } catch (Exception e) {
            }
        }
        
        if (!smokeFrames.isEmpty()) {
            smokeAnimationTimer = new Timer(smokeFadeDelay, e -> {
                if (smokeHoldCounter < smokeHoldDelay / smokeFadeDelay) {
                    smokeHoldCounter++;
                } else {
                    smokeAlpha += 1f / 4;
                    if (smokeAlpha >= 1f) {
                        smokeAlpha = 0f;
                        currentSmokeFrame = (currentSmokeFrame + 1) % smokeFrames.size();
                        smokeHoldCounter = 0;
                    }
                }
                repaint();
            });
            smokeAnimationTimer.start();
        }

        subDestroyFrames = new ArrayList<>();
        String subDestroyBasePath = "assets/subdestroy";
        
        for (int i = 1; i <= 3; i++) {
            try {
                String imagePath = subDestroyBasePath + i + ".png";
                java.io.File file = new java.io.File(imagePath);
                if (file.exists()) {
                    Image img = javax.imageio.ImageIO.read(file);
                    if (img != null) {
                        subDestroyFrames.add(img);
                    }
                }
            } catch (Exception e) {
            }
        }
        
        if (!subDestroyFrames.isEmpty()) {
            subDestroyTimer = new Timer(300, e -> {
                boolean hasSunkSubmarine = false;
                if (board != null) {
                    for (Ship ship : board.getShips()) {
                        if ("Submarine".equals(ship.getName()) && ship.isSunk()) {
                            hasSunkSubmarine = true;
                            break;
                        }
                    }
                }
                
                if (hasSunkSubmarine && !subDestroyAnimationPlayed) {
                    currentSubDestroyFrame++;
                    if (currentSubDestroyFrame >= subDestroyFrames.size()) {
                        subDestroyAnimationPlayed = true;
                        subDestroyTimer.stop();
                    }
                    repaint();
                }
            });
            subDestroyTimer.start();
        }
        
        try {
            String imagePath = "assets/destroyerdmg.jpg";
            java.io.File file = new java.io.File(imagePath);
            if (file.exists()) {
                destroyerDmgImage = javax.imageio.ImageIO.read(file);
            }
        } catch (Exception e) {
        }
        
        if (destroyerDmgImage != null) {
            destroyerDmgTimer = new Timer(300, e -> {
                boolean hasSunkDestroyer = false;
                if (board != null) {
                    for (Ship ship : board.getShips()) {
                        if ("Destroyer".equals(ship.getName()) && ship.isSunk()) {
                            hasSunkDestroyer = true;
                            break;
                        }
                    }
                }
                
                if (hasSunkDestroyer && !destroyerDmgAnimationPlayed) {
                    currentDestroyerDmgFrame++;
                    if (currentDestroyerDmgFrame >= 1) {
                        destroyerDmgAnimationPlayed = true;
                        destroyerDmgTimer.stop();
                    }
                    repaint();
                }
            });
            destroyerDmgTimer.start();
        }
        
        try {
            String imagePath = "assets/cruiserdestroy.png";
            java.io.File file = new java.io.File(imagePath);
            if (file.exists()) {
                cruiserDestroyImage = javax.imageio.ImageIO.read(file);
            }
        } catch (Exception e) {
        }
        
        if (cruiserDestroyImage != null) {
            cruiserDestroyTimer = new Timer(300, e -> {
                boolean hasSunkCruiser = false;
                if (board != null) {
                    for (Ship ship : board.getShips()) {
                        if ("Cruiser".equals(ship.getName()) && ship.isSunk()) {
                            hasSunkCruiser = true;
                            break;
                        }
                    }
                }
                
                if (hasSunkCruiser && !cruiserDestroyAnimationPlayed) {
                    currentCruiserDestroyFrame++;
                    if (currentCruiserDestroyFrame >= 1) {
                        cruiserDestroyAnimationPlayed = true;
                        cruiserDestroyTimer.stop();
                    }
                    repaint();
                }
            });
            cruiserDestroyTimer.start();
        }
        
        try {
            String imagePath = "assets/battleshipdmg.png";
            java.io.File file = new java.io.File(imagePath);
            if (file.exists()) {
                battleshipDmgImage = javax.imageio.ImageIO.read(file);
            }
        } catch (Exception e) {
        }
        
        if (battleshipDmgImage != null) {
            battleshipDmgTimer = new Timer(300, e -> {
                boolean hasSunkBattleship = false;
                if (board != null) {
                    for (Ship ship : board.getShips()) {
                        if ("Battleship".equals(ship.getName()) && ship.isSunk()) {
                            hasSunkBattleship = true;
                            break;
                        }
                    }
                }
                
                if (hasSunkBattleship && !battleshipDmgAnimationPlayed) {
                    currentBattleshipDmgFrame++;
                    if (currentBattleshipDmgFrame >= 1) {
                        battleshipDmgAnimationPlayed = true;
                        battleshipDmgTimer.stop();
                    }
                    repaint();
                }
            });
            battleshipDmgTimer.start();
        }
        
        try {
            String imagePath = "assets/carrierdmg.png";
            java.io.File file = new java.io.File(imagePath);
            if (file.exists()) {
                carrierDmgImage = javax.imageio.ImageIO.read(file);
            }
        } catch (Exception e) {
        }
        
        if (carrierDmgImage != null) {
            carrierDmgTimer = new Timer(300, e -> {
                boolean hasSunkCarrier = false;
                if (board != null) {
                    for (Ship ship : board.getShips()) {
                        if ("Carrier".equals(ship.getName()) && ship.isSunk()) {
                            hasSunkCarrier = true;
                            break;
                        }
                    }
                }
                
                if (hasSunkCarrier && !carrierDmgAnimationPlayed) {
                    currentCarrierDmgFrame++;
                    if (currentCarrierDmgFrame >= 1) {
                        carrierDmgAnimationPlayed = true;
                        carrierDmgTimer.stop();
                    }
                    repaint();
                }
            });
            carrierDmgTimer.start();
        }

        initializeLayout();
    }
    
    private void initializeLayout() {
        setLayout(null);
        int panelWidth = SIZE * cellWidth;
        int panelHeight = SIZE * cellHeight;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        setMinimumSize(new Dimension(panelWidth, panelHeight));
        setBounds(0, 0, panelWidth, panelHeight);
        
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
    
    public int getCellWidth() { return cellWidth; }
    public int getCellHeight() { return cellHeight; }
    public int getBoardWidth() { return SIZE * cellWidth; }
    public int getBoardHeight() { return SIZE * cellHeight; }
    
    private boolean hasAnyRevealedShips() {
        if (board == null) return false;
        for (Ship ship : board.getShips()) {
            if (ship.isFullyRevealed()) return true;
        }
        return false;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

         if (!oceanFrames.isEmpty()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            Image img = oceanFrames.get(0);
        if (img != null) {
            g2d.drawImage(img, 0, 0, getBoardWidth(), getBoardHeight(), this);
    }
}
       
        boolean canShowShips = showShips || (board != null && !board.getShips().isEmpty() && hasAnyRevealedShips());
        
        if (canShowShips && carrierImageRaw != null && board != null && !board.getShips().isEmpty()) {
            for (Ship ship : board.getShips()) {
                if ("Carrier".equals(ship.getName()) && !ship.isSunk() && (showShips || ship.isFullyRevealed())) {
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
            
            if (battleshipImageRaw != null && !board.getShips().isEmpty()) {
                for (Ship ship : board.getShips()) {
                    if ("Battleship".equals(ship.getName()) && !ship.isSunk() && (showShips || ship.isFullyRevealed())) {
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
                    if ("Cruiser".equals(ship.getName()) && !ship.isSunk() && (showShips || ship.isFullyRevealed())) {
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
                    if ("Submarine".equals(ship.getName()) && !ship.isSunk() && (showShips || ship.isFullyRevealed())) {
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
                    if ("Destroyer".equals(ship.getName()) && !ship.isSunk() && (showShips || ship.isFullyRevealed())) {
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
            
            // Draw damage cell image for hit cells with fire/smoke effect
            if (dmgCellImageRaw != null && board != null) {
                for (int row = 0; row < SIZE; row++) {
                    for (int col = 0; col < SIZE; col++) {
                        Cell cell = board.getCell(row, col);
                        
                        boolean isSubmarineSunkWithAnimationDone = false;
                        boolean isDestroyerSunkWithAnimationDone = false;
                        boolean isCruiserSunkWithAnimationDone = false;
                        boolean isBattleshipSunkWithAnimationDone = false;
                        boolean isCarrierSunkWithAnimationDone = false;
                        if (cell.hasShip() && cell.getShip() != null && "Submarine".equals(cell.getShip().getName()) && cell.getShip().isSunk() && subDestroyAnimationPlayed) {
                            isSubmarineSunkWithAnimationDone = true;
                        }
                        if (cell.hasShip() && cell.getShip() != null && "Destroyer".equals(cell.getShip().getName()) && cell.getShip().isSunk() && destroyerDmgAnimationPlayed) {
                            isDestroyerSunkWithAnimationDone = true;
                        }
                        if (cell.hasShip() && cell.getShip() != null && "Cruiser".equals(cell.getShip().getName()) && cell.getShip().isSunk() && cruiserDestroyAnimationPlayed) {
                            isCruiserSunkWithAnimationDone = true;
                        }
                        if (cell.hasShip() && cell.getShip() != null && "Battleship".equals(cell.getShip().getName()) && cell.getShip().isSunk() && battleshipDmgAnimationPlayed) {
                            isBattleshipSunkWithAnimationDone = true;
                        }
                        if (cell.hasShip() && cell.getShip() != null && "Carrier".equals(cell.getShip().getName()) && cell.getShip().isSunk() && carrierDmgAnimationPlayed) {
                            isCarrierSunkWithAnimationDone = true;
                        }
                        
                        if ((!isSubmarineSunkWithAnimationDone && !isDestroyerSunkWithAnimationDone && !isCruiserSunkWithAnimationDone && !isBattleshipSunkWithAnimationDone && !isCarrierSunkWithAnimationDone) && (cell.getColor().equals(Cell.HIT_RED) || cell.getColor().equals(Cell.INFECTED_HIT))) {
                            int cellDrawX = col * cellWidth;
                            int cellDrawY = row * cellHeight + dmgCellOffset;
                            
                            // Draw the damage cell image
                            Image scaledDmgCell = dmgCellImageRaw.getScaledInstance(cellWidth, cellHeight, Image.SCALE_SMOOTH);
                            g.drawImage(scaledDmgCell, cellDrawX, cellDrawY, null);
                            
                            // Draw smoke animation frames
                            if (!smokeFrames.isEmpty()) {
                                Image smokeFrame = smokeFrames.get(currentSmokeFrame);
                                int smokeWidth = cellWidth;
                                int smokeHeight = cellHeight;
                                Image scaledSmoke = smokeFrame.getScaledInstance(smokeWidth, smokeHeight, Image.SCALE_SMOOTH);
                                Graphics2D g2d = (Graphics2D) g;
                                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f + smokeAlpha * 0.4f));
                                g.drawImage(scaledSmoke, cellDrawX, cellDrawY - 5, null);
                                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                            } else {
                                // Fallback: programmatic smoke if frames not loaded
                                Graphics2D g2d = (Graphics2D) g;
                                long time = System.currentTimeMillis();
                                
                                for (int i = 0; i < 3; i++) {
                                    float offset = (float)((time / 500.0 + i * 0.7) % 1.0);
                                    int smokeY = cellDrawY + cellHeight - (int)(offset * cellHeight);
                                    int smokeX = cellDrawX + cellWidth / 2 + (int)(Math.sin(time / 200.0 + i) * cellWidth / 4);
                                    int particleSize = 5 + (int)(offset * 10);
                                    int alpha = (int)(150 * (1 - offset));
                                    
                                    g2d.setColor(new Color(100, 100, 100, alpha));
                                    g2d.fillOval(smokeX - particleSize / 2, smokeY - particleSize / 2, particleSize, particleSize);
                                    
                                    if (offset < 0.3) {
                                        int fireAlpha = (int)(200 * (1 - offset / 0.3));
                                        g2d.setColor(new Color(255, 100 + (int)(offset * 200), 0, fireAlpha));
                                        g2d.fillOval(smokeX - particleSize / 2, smokeY + particleSize / 2, particleSize * 2, particleSize * 2);
                                    }
                                }
                                
                                g2d.setColor(new Color(255, 50, 0, 80));
                                g2d.fillOval(cellDrawX + 5, cellDrawY + cellHeight - 15, cellWidth - 10, 15);
                            }
                        }
                    }
                }
            }
        }
        
        // Draw destruction animation for submarine (works for both player and enemy boards)
        if (!subDestroyFrames.isEmpty() && board != null && !board.getShips().isEmpty()) {
            for (Ship ship : board.getShips()) {
                if ("Submarine".equals(ship.getName()) && ship.isSunk()) {
                    List<Ship.Coordinate> positions = ship.getPositions();
                    if (positions.size() >= 2) {
                        int frameIndex = Math.min(currentSubDestroyFrame, subDestroyFrames.size() - 1);
                        Image currentFrame = subDestroyFrames.get(frameIndex);
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
                        if (isHorizontal) {
                            shipWidth = (maxY - minY + 1) * cellWidth;
                            shipHeight = (int)(cellHeight * 1.7);
                        } else {
                            shipWidth = (int)(cellWidth * 1.7);
                            shipHeight = (maxX - minX + 1) * cellHeight;
                        }
                        
                        Image scaledFrame = currentFrame.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                        
                        if (!isHorizontal) {
                            int srcW = currentFrame.getWidth(null);
                            int srcH = currentFrame.getHeight(null);
                            BufferedImage rotated = new BufferedImage(srcH, srcW, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2d = rotated.createGraphics();
                            g2d.translate(srcH / 2, srcW / 2);
                            g2d.rotate(Math.PI / 2);
                            g2d.drawImage(currentFrame, -srcW / 2, -srcH / 2, null);
                            g2d.dispose();
                            scaledFrame = rotated.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                        }
                        
                        // Add floating movement effect
                        long time = System.currentTimeMillis();
                        float floatOffset = (float) Math.sin(time / 500.0) * 3;
                        
                        int drawX, drawY;
                        if (!isHorizontal) {
                            int extraWidth = shipWidth - cellWidth;
                            int offsetX = extraWidth / 2;
                            drawX = minY * cellWidth - offsetX;
                            drawY = minX * cellHeight + (int) floatOffset;
                        } else {
                            int extraHeight = (int)(cellHeight * 1.7) - cellHeight;
                            int offsetY = extraHeight / 2;
                            drawX = minY * cellWidth;
                            drawY = minX * cellHeight + (int) floatOffset - offsetY;
                        }
                        
                        // Draw smoke and fire effects
                        Graphics2D g2d = (Graphics2D) g;
                        for (int i = 0; i < 5; i++) {
                            float offset = (float) ((time / 400.0 + i * 0.6) % 1.0);
                            int smokeY = drawY + shipHeight - (int)(offset * shipHeight * 0.8f);
                            int smokeX = drawX + shipWidth / 2 + (int)(Math.sin(time / 300.0 + i) * shipWidth / 3);
                            int particleSize = 4 + (int)(offset * 12);
                            int alpha = (int)(180 * (1 - offset));
                            
                            g2d.setColor(new Color(80, 80, 80, alpha));
                            g2d.fillOval(smokeX - particleSize / 2, smokeY - particleSize / 2, particleSize, particleSize);
                            
                            if (offset < 0.4) {
                                int fireAlpha = (int)(255 * (1 - offset / 0.4));
                                g2d.setColor(new Color(255, 80 + (int)(offset * 100), 0, fireAlpha));
                                g2d.fillOval(smokeX - particleSize / 3, smokeY + particleSize / 2, particleSize * 2, particleSize * 2);
                            }
                        }
                        
                        // Add bubbling effect
                        for (int i = 0; i < 3; i++) {
                            float bubbleOffset = (float) ((time / 600.0 + i * 0.8) % 1.0);
                            int bubbleY = drawY + shipHeight - (int)(bubbleOffset * shipHeight);
                            int bubbleX = drawX + (i + 1) * shipWidth / 4 + (int)(Math.sin(time / 400.0 + i * 2) * 10);
                            int bubbleSize = 3 + (int)(bubbleOffset * 5);
                            int bubbleAlpha = (int)(150 * (1 - bubbleOffset));
                            g2d.setColor(new Color(200, 230, 255, bubbleAlpha));
                            g2d.fillOval(bubbleX, bubbleY, bubbleSize, bubbleSize);
                        }
                        
                        g.drawImage(scaledFrame, drawX, drawY, null);
                        
                        // Draw smoke animation frames on top of the destroyed submarine
                        if (!smokeFrames.isEmpty()) {
                            Image smokeFrame = smokeFrames.get(currentSmokeFrame);
                            int smokeWidth = (int)(shipWidth * 0.6f);
                            int smokeHeight = (int)(shipHeight * 0.6f);
                            Image scaledSmoke = smokeFrame.getScaledInstance(smokeWidth, smokeHeight, Image.SCALE_SMOOTH);
                            int smokeX = drawX + (shipWidth - smokeWidth) / 2;
                            int smokeY = drawY - smokeHeight / 4;
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f + smokeAlpha * 0.4f));
                            g.drawImage(scaledSmoke, smokeX, smokeY, null);
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                        }
                    }
                    break;
                }
            }
        }
        
        // Draw destruction animation for destroyer
        if (destroyerDmgImage != null && board != null && !board.getShips().isEmpty()) {
            for (Ship ship : board.getShips()) {
                if ("Destroyer".equals(ship.getName()) && ship.isSunk()) {
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
                        if (isHorizontal) {
                            shipWidth = (maxY - minY + 1) * cellWidth;
                            shipHeight = (int)(cellHeight * 1.7);
                        } else {
                            shipWidth = (int)(cellWidth * 1.7);
                            shipHeight = (maxX - minX + 1) * cellHeight;
                        }
                        
                        Image scaledFrame = destroyerDmgImage.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                        
                        if (!isHorizontal) {
                            int srcW = destroyerDmgImage.getWidth(null);
                            int srcH = destroyerDmgImage.getHeight(null);
                            BufferedImage rotated = new BufferedImage(srcH, srcW, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2d = rotated.createGraphics();
                            g2d.translate(srcH / 2, srcW / 2);
                            g2d.rotate(Math.PI / 2);
                            g2d.drawImage(destroyerDmgImage, -srcW / 2, -srcH / 2, null);
                            g2d.dispose();
                            scaledFrame = rotated.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                        }
                        
                        // Add floating movement effect
                        long time = System.currentTimeMillis();
                        float floatOffset = (float) Math.sin(time / 500.0) * 3;
                        
                        int drawX, drawY;
                        if (!isHorizontal) {
                            int extraWidth = shipWidth - cellWidth;
                            int offsetX = extraWidth / 2;
                            drawX = minY * cellWidth - offsetX;
                            drawY = minX * cellHeight + (int) floatOffset;
                        } else {
                            int extraHeight = (int)(cellHeight * 1.7) - cellHeight;
                            int offsetY = extraHeight / 2;
                            drawX = minY * cellWidth;
                            drawY = minX * cellHeight + (int) floatOffset - offsetY;
                        }
                        
                        // Draw smoke and fire effects
                        Graphics2D g2d = (Graphics2D) g;
                        for (int i = 0; i < 5; i++) {
                            float offset = (float) ((time / 400.0 + i * 0.6) % 1.0);
                            int smokeY = drawY + shipHeight - (int)(offset * shipHeight * 0.8f);
                            int smokeX = drawX + shipWidth / 2 + (int)(Math.sin(time / 300.0 + i) * shipWidth / 3);
                            int particleSize = 4 + (int)(offset * 12);
                            int alpha = (int)(180 * (1 - offset));
                            
                            g2d.setColor(new Color(80, 80, 80, alpha));
                            g2d.fillOval(smokeX - particleSize / 2, smokeY - particleSize / 2, particleSize, particleSize);
                            
                            if (offset < 0.4) {
                                int fireAlpha = (int)(255 * (1 - offset / 0.4));
                                g2d.setColor(new Color(255, 80 + (int)(offset * 100), 0, fireAlpha));
                                g2d.fillOval(smokeX - particleSize / 3, smokeY + particleSize / 2, particleSize * 2, particleSize * 2);
                            }
                        }
                        
                        // Add bubbles
                        for (int i = 0; i < 3; i++) {
                            float bubbleOffset = (float) ((time / 600.0 + i * 0.8) % 1.0);
                            int bubbleY = drawY + shipHeight - (int)(bubbleOffset * shipHeight);
                            int bubbleX = drawX + (i + 1) * shipWidth / 4 + (int)(Math.sin(time / 400.0 + i * 2) * 10);
                            int bubbleSize = 3 + (int)(bubbleOffset * 5);
                            int bubbleAlpha = (int)(150 * (1 - bubbleOffset));
                            g2d.setColor(new Color(200, 230, 255, bubbleAlpha));
                            g2d.fillOval(bubbleX, bubbleY, bubbleSize, bubbleSize);
                        }
                        
                        g.drawImage(scaledFrame, drawX, drawY, null);
                        
                        // Draw smoke animation frames on top of the destroyed destroyer
                        if (!smokeFrames.isEmpty()) {
                            Image smokeFrame = smokeFrames.get(currentSmokeFrame);
                            int smokeWidth = (int)(shipWidth * 0.6f);
                            int smokeHeight = (int)(shipHeight * 0.6f);
                            Image scaledSmoke = smokeFrame.getScaledInstance(smokeWidth, smokeHeight, Image.SCALE_SMOOTH);
                            int smokeX = drawX + (shipWidth - smokeWidth) / 2;
                            int smokeY = drawY - smokeHeight / 4;
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f + smokeAlpha * 0.4f));
                            g.drawImage(scaledSmoke, smokeX, smokeY, null);
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                        }
                    }
                    break;
                }
            }
        }
        
        // Draw destruction animation for cruiser
        if (cruiserDestroyImage != null && board != null && !board.getShips().isEmpty()) {
            for (Ship ship : board.getShips()) {
                if ("Cruiser".equals(ship.getName()) && ship.isSunk()) {
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
                        if (isHorizontal) {
                            shipWidth = (maxY - minY + 1) * cellWidth;
                            shipHeight = (int)(cellHeight * 1.7);
                        } else {
                            shipWidth = (int)(cellWidth * 1.7);
                            shipHeight = (maxX - minX + 1) * cellHeight;
                        }
                        
                        Image scaledFrame = cruiserDestroyImage.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                        
                        if (!isHorizontal) {
                            int srcW = cruiserDestroyImage.getWidth(null);
                            int srcH = cruiserDestroyImage.getHeight(null);
                            BufferedImage rotated = new BufferedImage(srcH, srcW, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2d = rotated.createGraphics();
                            g2d.translate(srcH / 2, srcW / 2);
                            g2d.rotate(Math.PI / 2);
                            g2d.drawImage(cruiserDestroyImage, -srcW / 2, -srcH / 2, null);
                            g2d.dispose();
                            scaledFrame = rotated.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                        }
                        
                        long time = System.currentTimeMillis();
                        float floatOffset = (float) Math.sin(time / 500.0) * 3;
                        
                        int drawX, drawY;
                        if (!isHorizontal) {
                            int extraWidth = shipWidth - cellWidth;
                            int offsetX = extraWidth / 2;
                            drawX = minY * cellWidth - offsetX;
                            drawY = minX * cellHeight + (int) floatOffset;
                        } else {
                            int extraHeight = (int)(cellHeight * 1.7) - cellHeight;
                            int offsetY = extraHeight / 2;
                            drawX = minY * cellWidth;
                            drawY = minX * cellHeight + (int) floatOffset - offsetY;
                        }
                        
                        Graphics2D g2d = (Graphics2D) g;
                        for (int i = 0; i < 5; i++) {
                            float offset = (float) ((time / 400.0 + i * 0.6) % 1.0);
                            int smokeY = drawY + shipHeight - (int)(offset * shipHeight * 0.8f);
                            int smokeX = drawX + shipWidth / 2 + (int)(Math.sin(time / 300.0 + i) * shipWidth / 3);
                            int particleSize = 4 + (int)(offset * 12);
                            int alpha = (int)(180 * (1 - offset));
                            
                            g2d.setColor(new Color(80, 80, 80, alpha));
                            g2d.fillOval(smokeX - particleSize / 2, smokeY - particleSize / 2, particleSize, particleSize);
                            
                            if (offset < 0.4) {
                                int fireAlpha = (int)(255 * (1 - offset / 0.4));
                                g2d.setColor(new Color(255, 80 + (int)(offset * 100), 0, fireAlpha));
                                g2d.fillOval(smokeX - particleSize / 3, smokeY + particleSize / 2, particleSize * 2, particleSize * 2);
                            }
                        }
                        
                        for (int i = 0; i < 3; i++) {
                            float bubbleOffset = (float) ((time / 600.0 + i * 0.8) % 1.0);
                            int bubbleY = drawY + shipHeight - (int)(bubbleOffset * shipHeight);
                            int bubbleX = drawX + (i + 1) * shipWidth / 4 + (int)(Math.sin(time / 400.0 + i * 2) * 10);
                            int bubbleSize = 3 + (int)(bubbleOffset * 5);
                            int bubbleAlpha = (int)(150 * (1 - bubbleOffset));
                            g2d.setColor(new Color(200, 230, 255, bubbleAlpha));
                            g2d.fillOval(bubbleX, bubbleY, bubbleSize, bubbleSize);
                        }
                        
                        g.drawImage(scaledFrame, drawX, drawY, null);
                        
                        // Draw smoke animation frames on top of the destroyed cruiser
                        if (!smokeFrames.isEmpty()) {
                            Image smokeFrame = smokeFrames.get(currentSmokeFrame);
                            int smokeWidth = (int)(shipWidth * 0.6f);
                            int smokeHeight = (int)(shipHeight * 0.6f);
                            Image scaledSmoke = smokeFrame.getScaledInstance(smokeWidth, smokeHeight, Image.SCALE_SMOOTH);
                            int smokeX = drawX + (shipWidth - smokeWidth) / 2;
                            int smokeY = drawY - smokeHeight / 4;
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f + smokeAlpha * 0.4f));
                            g.drawImage(scaledSmoke, smokeX, smokeY, null);
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                        }
                    }
                    break;
                }
            }
        }
        
        // Draw destruction animation for battleship
        if (battleshipDmgImage != null && board != null && !board.getShips().isEmpty()) {
            for (Ship ship : board.getShips()) {
                if ("Battleship".equals(ship.getName()) && ship.isSunk()) {
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
                        if (isHorizontal) {
                            shipWidth = (maxY - minY + 1) * cellWidth;
                            shipHeight = (int)(cellHeight * 1.7);
                        } else {
                            shipWidth = (int)(cellWidth * 1.7);
                            shipHeight = (maxX - minX + 1) * cellHeight;
                        }
                        
                        Image scaledFrame = battleshipDmgImage.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                        
                        if (!isHorizontal) {
                            int srcW = battleshipDmgImage.getWidth(null);
                            int srcH = battleshipDmgImage.getHeight(null);
                            BufferedImage rotated = new BufferedImage(srcH, srcW, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2dRotated = rotated.createGraphics();
                            g2dRotated.translate(srcH / 2, srcW / 2);
                            g2dRotated.rotate(Math.PI / 2);
                            g2dRotated.drawImage(battleshipDmgImage, -srcW / 2, -srcH / 2, null);
                            g2dRotated.dispose();
                            scaledFrame = rotated.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                        }
                        
                        long time = System.currentTimeMillis();
                        float floatOffset = (float) Math.sin(time / 500.0) * 3;
                        
                        int drawX, drawY;
                        if (!isHorizontal) {
                            int extraWidth = shipWidth - cellWidth;
                            int offsetX = extraWidth / 2;
                            drawX = minY * cellWidth - offsetX;
                            drawY = minX * cellHeight + (int) floatOffset;
                        } else {
                            int extraHeight = (int)(cellHeight * 1.7) - cellHeight;
                            int offsetY = extraHeight / 2;
                            drawX = minY * cellWidth;
                            drawY = minX * cellHeight + (int) floatOffset - offsetY;
                        }
                        
                        Graphics2D g2d = (Graphics2D) g;
                        for (int i = 0; i < 5; i++) {
                            float offset = (float) ((time / 400.0 + i * 0.6) % 1.0);
                            int smokeY = drawY + shipHeight - (int)(offset * shipHeight * 0.8f);
                            int smokeX = drawX + shipWidth / 2 + (int)(Math.sin(time / 300.0 + i) * shipWidth / 3);
                            int particleSize = 4 + (int)(offset * 12);
                            int alpha = (int)(180 * (1 - offset));
                            
                            g2d.setColor(new Color(80, 80, 80, alpha));
                            g2d.fillOval(smokeX - particleSize / 2, smokeY - particleSize / 2, particleSize, particleSize);
                            
                            if (offset < 0.4) {
                                int fireAlpha = (int)(255 * (1 - offset / 0.4));
                                g2d.setColor(new Color(255, 80 + (int)(offset * 100), 0, fireAlpha));
                                g2d.fillOval(smokeX - particleSize / 3, smokeY + particleSize / 2, particleSize * 2, particleSize * 2);
                            }
                        }
                        
                        for (int i = 0; i < 3; i++) {
                            float bubbleOffset = (float) ((time / 600.0 + i * 0.8) % 1.0);
                            int bubbleY = drawY + shipHeight - (int)(bubbleOffset * shipHeight);
                            int bubbleX = drawX + (i + 1) * shipWidth / 4 + (int)(Math.sin(time / 400.0 + i * 2) * 10);
                            int bubbleSize = 3 + (int)(bubbleOffset * 5);
                            int bubbleAlpha = (int)(150 * (1 - bubbleOffset));
                            g2d.setColor(new Color(200, 230, 255, bubbleAlpha));
                            g2d.fillOval(bubbleX, bubbleY, bubbleSize, bubbleSize);
                        }
                        
                        g.drawImage(scaledFrame, drawX, drawY, null);
                        
                        // Draw smoke animation frames on top of the destroyed battleship
                        if (!smokeFrames.isEmpty()) {
                            Image smokeFrame = smokeFrames.get(currentSmokeFrame);
                            int smokeWidth = (int)(shipWidth * 0.6f);
                            int smokeHeight = (int)(shipHeight * 0.6f);
                            Image scaledSmoke = smokeFrame.getScaledInstance(smokeWidth, smokeHeight, Image.SCALE_SMOOTH);
                            int smokeX = drawX + (shipWidth - smokeWidth) / 2;
                            int smokeY = drawY - smokeHeight / 4;
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f + smokeAlpha * 0.4f));
                            g.drawImage(scaledSmoke, smokeX, smokeY, null);
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                        }
                    }
                    break;
                }
            }
        }
        
        // Draw destruction animation for carrier
        if (carrierDmgImage != null && board != null && !board.getShips().isEmpty()) {
            for (Ship ship : board.getShips()) {
                if ("Carrier".equals(ship.getName()) && ship.isSunk()) {
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
                        if (isHorizontal) {
                            shipWidth = (maxY - minY + 1) * cellWidth;
                            shipHeight = (int)(cellHeight * 1.7);
                        } else {
                            shipWidth = (int)(cellWidth * 1.7);
                            shipHeight = (maxX - minX + 1) * cellHeight;
                        }
                        
                        Image scaledFrame = carrierDmgImage.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                        
                        if (!isHorizontal) {
                            int srcW = carrierDmgImage.getWidth(null);
                            int srcH = carrierDmgImage.getHeight(null);
                            BufferedImage rotated = new BufferedImage(srcH, srcW, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2dRotated = rotated.createGraphics();
                            g2dRotated.translate(srcH / 2, srcW / 2);
                            g2dRotated.rotate(Math.PI / 2);
                            g2dRotated.drawImage(carrierDmgImage, -srcW / 2, -srcH / 2, null);
                            g2dRotated.dispose();
                            scaledFrame = rotated.getScaledInstance(shipWidth, shipHeight, Image.SCALE_SMOOTH);
                        }
                        
                        long time = System.currentTimeMillis();
                        float floatOffset = (float) Math.sin(time / 500.0) * 3;
                        
                        int drawX, drawY;
                        if (!isHorizontal) {
                            int extraWidth = shipWidth - cellWidth;
                            int offsetX = extraWidth / 2;
                            drawX = minY * cellWidth - offsetX;
                            drawY = minX * cellHeight + (int) floatOffset;
                        } else {
                            int extraHeight = (int)(cellHeight * 1.7) - cellHeight;
                            int offsetY = extraHeight / 2;
                            drawX = minY * cellWidth;
                            drawY = minX * cellHeight + (int) floatOffset - offsetY;
                        }
                        
                        Graphics2D g2d = (Graphics2D) g;
                        for (int i = 0; i < 5; i++) {
                            float offset = (float) ((time / 400.0 + i * 0.6) % 1.0);
                            int smokeY = drawY + shipHeight - (int)(offset * shipHeight * 0.8f);
                            int smokeX = drawX + shipWidth / 2 + (int)(Math.sin(time / 300.0 + i) * shipWidth / 3);
                            int particleSize = 4 + (int)(offset * 12);
                            int alpha = (int)(180 * (1 - offset));
                            
                            g2d.setColor(new Color(80, 80, 80, alpha));
                            g2d.fillOval(smokeX - particleSize / 2, smokeY - particleSize / 2, particleSize, particleSize);
                            
                            if (offset < 0.4) {
                                int fireAlpha = (int)(255 * (1 - offset / 0.4));
                                g2d.setColor(new Color(255, 80 + (int)(offset * 100), 0, fireAlpha));
                                g2d.fillOval(smokeX - particleSize / 3, smokeY + particleSize / 2, particleSize * 2, particleSize * 2);
                            }
                        }
                        
                        for (int i = 0; i < 3; i++) {
                            float bubbleOffset = (float) ((time / 600.0 + i * 0.8) % 1.0);
                            int bubbleY = drawY + shipHeight - (int)(bubbleOffset * shipHeight);
                            int bubbleX = drawX + (i + 1) * shipWidth / 4 + (int)(Math.sin(time / 400.0 + i * 2) * 10);
                            int bubbleSize = 3 + (int)(bubbleOffset * 5);
                            int bubbleAlpha = (int)(150 * (1 - bubbleOffset));
                            g2d.setColor(new Color(200, 230, 255, bubbleAlpha));
                            g2d.fillOval(bubbleX, bubbleY, bubbleSize, bubbleSize);
                        }
                        
                        g.drawImage(scaledFrame, drawX, drawY, null);
                        
                        // Draw smoke animation frames on top of the destroyed carrier
                        if (!smokeFrames.isEmpty()) {
                            Image smokeFrame = smokeFrames.get(currentSmokeFrame);
                            int smokeWidth = (int)(shipWidth * 0.6f);
                            int smokeHeight = (int)(shipHeight * 0.6f);
                            Image scaledSmoke = smokeFrame.getScaledInstance(smokeWidth, smokeHeight, Image.SCALE_SMOOTH);
                            int smokeX = drawX + (shipWidth - smokeWidth) / 2;
                            int smokeY = drawY - smokeHeight / 4;
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f + smokeAlpha * 0.4f));
                            g.drawImage(scaledSmoke, smokeX, smokeY, null);
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                        }
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
            
            boolean isSubmarineSunkWithAnimationDone = false;
            boolean isDestroyerSunkWithAnimationDone = false;
            boolean isCruiserSunkWithAnimationDone = false;
            boolean isBattleshipSunkWithAnimationDone = false;
            boolean isCarrierSunkWithAnimationDone = false;
            if (cell.hasShip() && cell.getShip() != null && "Submarine".equals(cell.getShip().getName()) && cell.getShip().isSunk() && subDestroyAnimationPlayed) {
                isSubmarineSunkWithAnimationDone = true;
            }
            if (cell.hasShip() && cell.getShip() != null && "Destroyer".equals(cell.getShip().getName()) && cell.getShip().isSunk() && destroyerDmgAnimationPlayed) {
                isDestroyerSunkWithAnimationDone = true;
            }
            if (cell.hasShip() && cell.getShip() != null && "Cruiser".equals(cell.getShip().getName()) && cell.getShip().isSunk() && cruiserDestroyAnimationPlayed) {
                isCruiserSunkWithAnimationDone = true;
            }
            if (cell.hasShip() && cell.getShip() != null && "Battleship".equals(cell.getShip().getName()) && cell.getShip().isSunk() && battleshipDmgAnimationPlayed) {
                isBattleshipSunkWithAnimationDone = true;
            }
            if (cell.hasShip() && cell.getShip() != null && "Carrier".equals(cell.getShip().getName()) && cell.getShip().isSunk() && carrierDmgAnimationPlayed) {
                isCarrierSunkWithAnimationDone = true;
            }
            
            if (isSubmarineSunkWithAnimationDone || isDestroyerSunkWithAnimationDone || isCruiserSunkWithAnimationDone || isBattleshipSunkWithAnimationDone || isCarrierSunkWithAnimationDone) {
                button.setBorder(null);
            } else {
                button.setBorder(BorderFactory.createLineBorder(new Color(255, 0, 0), 3));
                if (cell.hasShip() && cell.getShip() != null && cell.getShip().isSunk()) {
                    button.setText("💀");
                } else {
                    button.setText("💥");
                }
            }
            button.setOpaque(false);
            button.setContentAreaFilled(false);
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
                } else if (cell.getShip() != null && "Carrier".equals(cell.getShip().getName()) && !cell.getShip().isSunk() && (showShips || cell.getShip().isFullyRevealed())) {
                    // Make transparent so carrier image shows through from paintComponent
                    button.setOpaque(false);
                    button.setContentAreaFilled(false);
                } else if (cell.getShip() != null && "Battleship".equals(cell.getShip().getName()) && !cell.getShip().isSunk() && (showShips || cell.getShip().isFullyRevealed())) {
                    // Make transparent so battleship image shows through from paintComponent
                    button.setOpaque(false);
                    button.setContentAreaFilled(false);
                } else if (cell.getShip() != null && "Cruiser".equals(cell.getShip().getName()) && !cell.getShip().isSunk() && (showShips || cell.getShip().isFullyRevealed())) {
                    // Make transparent so cruiser image shows through from paintComponent
                    button.setOpaque(false);
                    button.setContentAreaFilled(false);
                } else if (cell.getShip() != null && "Submarine".equals(cell.getShip().getName()) && !cell.getShip().isSunk() && (showShips || cell.getShip().isFullyRevealed())) {
                    // Make transparent so submarine image shows through from paintComponent
                    button.setOpaque(false);
                    button.setContentAreaFilled(false);
                } else if (cell.getShip() != null && "Destroyer".equals(cell.getShip().getName()) && !cell.getShip().isSunk() && (showShips || cell.getShip().isFullyRevealed())) {
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