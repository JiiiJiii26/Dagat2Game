package gui;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import models.Board;
import models.Ship;
import models.Cell;
import java.util.ArrayList;
import java.util.Stack;
import java.util.List;

public class PlacementPanel extends JPanel {
    private JButton[][] gridButtons;
    private Board playerBoard;
    private final int SIZE = 10;
    private final int CELL_WIDTH = 200;  
    private final int CELL_HEIGHT = 90;  
    private Image placementBackgroundImage;
    private Image carrierImage;
    private Image battleshipImage;
    private Image cruiserImage;
    private Image submarineImage;
    private Image destroyerImage;
    private Image[] rainImages;
    private int currentRainFrame;
    private Timer rainAnimationTimer;
    private ArrayList<Ship> originalShipsToPlace;
    private ArrayList<Ship> shipsToPlace;
    private Ship currentShip;
    private boolean horizontal = true;
    private JLabel instructionLabel;
    private JButton rotateButton;
    private JButton undoButton;
    private JButton resetButton;
    private JButton startGameButton;
    private PlacementListener listener;
    private Stack<UndoAction> undoStack;
    private JPanel gridPanel;
    private JPanel controlPanel;
    
    private java.util.List<int[]> rainDrops;
    private Timer rainTimer;
    private java.util.List<int[]> ripples;
    private Timer rippleTimer;
    
    private class UndoAction {
        Ship ship;
        int startX;
        int startY;
        boolean wasHorizontal;
        
        UndoAction(Ship ship, int x, int y, boolean horizontal) {
            this.ship = ship;
            this.startX = x;
            this.startY = y;
            this.wasHorizontal = horizontal;
        }
    }
    
    public interface PlacementListener {
        void onPlacementComplete(Board playerBoard);
    }
    
    public PlacementPanel(PlacementListener listener) {
        this.listener = listener;
        this.playerBoard = new Board();
        this.originalShipsToPlace = new ArrayList<>();
        this.shipsToPlace = new ArrayList<>();
        this.undoStack = new Stack<>();

        loadImages();
        
        rainDrops = new java.util.ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rainDrops.add(new int[] {(int)(Math.random() * 2000), (int)(Math.random() * 1200), (int)(Math.random() * 5 + 3)});
        }
        rainTimer = new Timer(80, e -> {
            for (int[] drop : rainDrops) {
                drop[1] += drop[2];
                if (drop[1] > getHeight()) {
                    drop[1] = -20;
                    drop[0] = (int)(Math.random() * getWidth());
                }
            }
            repaint();
        });
        rainTimer.start();
        
        currentRainFrame = 0;
        rainAnimationTimer = new Timer(10000, e -> {
            currentRainFrame = (currentRainFrame + 1) % 4;
            repaint();
        });
        rainAnimationTimer.start();
        
        
        ripples = new java.util.ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ripples.add(new int[] {(int)(Math.random() * 1200), (int)(Math.random() * 1200), (int)(Math.random() * 30 + 10), 0});
        }
        rippleTimer = new Timer(60, e -> {
            for (int[] ripple : ripples) {
                ripple[2] += 2;
                ripple[3] += 15;
                if (ripple[3] > 100) {
                    ripple[3] = 0;
                    ripple[2] = (int)(Math.random() * 30 + 10);
                    ripple[0] = (int)(Math.random() * getWidth());
                    ripple[1] = (int)(Math.random() * getHeight());
                }
            }
            repaint();
        });
        rippleTimer.start();
        
        originalShipsToPlace.add(new Ship("Carrier", 5));
        originalShipsToPlace.add(new Ship("Battleship", 4));
        originalShipsToPlace.add(new Ship("Cruiser", 3));
        originalShipsToPlace.add(new Ship("Submarine", 3));
        originalShipsToPlace.add(new Ship("Destroyer", 2));
        
        resetShipList();
        
        setLayout(new BorderLayout());
        
        
        setBackground(new Color(20, 40, 60));
        
        
        controlPanel = createControlPanel();
        createGridPanel();
        
        add(controlPanel, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);
        
        int totalWidth = SIZE * CELL_WIDTH + 100;
        int totalHeight = SIZE * CELL_HEIGHT + 180;
        setPreferredSize(new Dimension(totalWidth, totalHeight));
        setMinimumSize(new Dimension(totalWidth, totalHeight));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        
        if (rainImages != null && rainImages[currentRainFrame] != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(rainImages[currentRainFrame], 0, 0, getWidth(), getHeight(), this);
        } else if (placementBackgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(placementBackgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(20, 40, 60));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        
        
        if (rainDrops != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.setColor(new Color(173, 216, 230, 120));
            for (int[] drop : rainDrops) {
                g2d.drawLine(drop[0], drop[1], drop[0] - 1, drop[1] + 12);
            }
        }
        
        
        if (ripples != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(1));
            for (int[] ripple : ripples) {
                int alpha = 100 - ripple[3];
                if (alpha > 0) {
                    g2d.setColor(new Color(200, 230, 255, alpha));
                    g2d.drawOval(ripple[0] - ripple[2] / 2, ripple[1] - ripple[2] / 2, ripple[2], ripple[2]);
                }
            }
        }
    }
    
    private void loadImages() {
        
        try {
            String imagePath = "D:\\GameProj\\Battleship Game\\assets\\shipPlacementbg.jpg";
            Image image = Toolkit.getDefaultToolkit().getImage(imagePath);
            MediaTracker tracker = new MediaTracker(new JLabel());
            tracker.addImage(image, 0);
            tracker.waitForID(0);
            if (image.getWidth(null) > 0) {
                placementBackgroundImage = image;
                System.out.println("✅ Boat placement background loaded");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not load background: " + e.getMessage());
        }
        
        
        rainImages = new Image[3];
        String[] rainFileNames = { "rain1.jpg", "rain2.jpg", "rain3.jpg"};
        for (int i = 0; i < 3; i++) {
            try {
                java.io.File rainFile = new java.io.File("D:\\GameProj\\Battleship Game\\assets\\" + rainFileNames[i]);
                if (rainFile.exists()) {
                    rainImages[i] = javax.imageio.ImageIO.read(rainFile);
                    System.out.println("✅ Rain frame " + i + " loaded");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Failed to load rain frame " + i + ": " + e.getMessage());
            }
        }
        
        
        try {
            java.io.File carrierFile = new java.io.File("D:\\GameProj\\Battleship Game\\assets\\carrier.png");
            if (carrierFile.exists()) {
                carrierImage = javax.imageio.ImageIO.read(carrierFile);
                System.out.println("✅ Carrier image loaded");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load carrier: " + e.getMessage());
        }
        
        try {
            java.io.File battleshipFile = new java.io.File("D:\\GameProj\\Battleship Game\\assets\\battleship.png");
            if (battleshipFile.exists()) {
                battleshipImage = javax.imageio.ImageIO.read(battleshipFile);
                System.out.println("✅ Battleship image loaded");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load battleship: " + e.getMessage());
        }
        
        try {
            java.io.File cruiserFile = new java.io.File("D:\\GameProj\\Battleship Game\\assets\\cruiser.png");
            if (cruiserFile.exists()) {
                cruiserImage = javax.imageio.ImageIO.read(cruiserFile);
                System.out.println("✅ Cruiser image loaded");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load cruiser: " + e.getMessage());
        }
        
        try {
            java.io.File submarineFile = new java.io.File("D:\\GameProj\\Battleship Game\\assets\\submarine.png");
            if (submarineFile.exists()) {
                submarineImage = javax.imageio.ImageIO.read(submarineFile);
                System.out.println("✅ Submarine image loaded");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load submarine: " + e.getMessage());
        }
        
        try {
            java.io.File destroyerFile = new java.io.File("D:\\GameProj\\Battleship Game\\assets\\destroyer.png");
            if (destroyerFile.exists()) {
                destroyerImage = javax.imageio.ImageIO.read(destroyerFile);
                System.out.println("✅ Destroyer image loaded");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load destroyer: " + e.getMessage());
        }
    }
    
    private JPanel createControlPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false); 
        instructionLabel = new JLabel("Place your Carrier (5 cells) - Click to place");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 22));
        instructionLabel.setForeground(Color.YELLOW);
        instructionLabel.setBackground(new Color(0, 0, 0, 150));
        instructionLabel.setOpaque(true);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.add(instructionLabel);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); 
        
        rotateButton = new JButton("🔄 Rotate");
        rotateButton.setFont(new Font("Arial", Font.BOLD, 18));
        rotateButton.setPreferredSize(new Dimension(160, 50));
        rotateButton.setBackground(new Color(0, 0, 0, 200));
        rotateButton.setForeground(Color.WHITE);
        rotateButton.setFocusPainted(false);
        rotateButton.addActionListener(e -> toggleRotation());
        buttonPanel.add(rotateButton);
        
        undoButton = new JButton("↩️ Undo");
        undoButton.setBackground(new Color(255, 165, 0, 200));
        undoButton.setForeground(Color.BLACK);
        undoButton.setFont(new Font("Arial", Font.BOLD, 18));
        undoButton.setPreferredSize(new Dimension(140, 50));
        undoButton.setFocusPainted(false);
        undoButton.setEnabled(false);
        undoButton.addActionListener(e -> undoLastPlacement());
        buttonPanel.add(undoButton);
        
        resetButton = new JButton("🗑️ Reset All");
        resetButton.setBackground(new Color(255, 100, 100, 200));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("Arial", Font.BOLD, 18));
        resetButton.setPreferredSize(new Dimension(140, 50));
        resetButton.setFocusPainted(false);
        resetButton.addActionListener(e -> resetAllShips());
        buttonPanel.add(resetButton);
        
        startGameButton = new JButton("⚔️ Start Battle!");
        startGameButton.setBackground(new Color(50, 200, 50, 200));
        startGameButton.setForeground(Color.WHITE);
        startGameButton.setFont(new Font("Arial", Font.BOLD, 18));
        startGameButton.setPreferredSize(new Dimension(180, 50));
        startGameButton.setFocusPainted(false);
        startGameButton.setEnabled(false);
        startGameButton.addActionListener(e -> {
            if (listener != null) {
                listener.onPlacementComplete(playerBoard);
            }
        });
        buttonPanel.add(startGameButton);
        
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.setOpaque(false); 
        controlPanel.add(topPanel, BorderLayout.NORTH);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return controlPanel;
    }
    
    private void createGridPanel() {
        int gridWidth = SIZE * CELL_WIDTH;
        int gridHeight = SIZE * CELL_HEIGHT;
        
        
        gridPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setStroke(new BasicStroke(2));
                
                
                g2d.setColor(new Color(255, 255, 255, 200));
                for (int i = 0; i <= SIZE; i++) {
                    g2d.drawLine(0, i * CELL_HEIGHT, gridWidth, i * CELL_HEIGHT);
                    g2d.drawLine(i * CELL_WIDTH, 0, i * CELL_WIDTH, gridHeight);
                }
                
                
                drawPlacedShips(g2d);
            }
        };
        
        gridPanel.setPreferredSize(new Dimension(gridWidth, gridHeight));
        gridPanel.setMinimumSize(new Dimension(gridWidth, gridHeight));
        gridPanel.setMaximumSize(new Dimension(gridWidth, gridHeight));
        gridPanel.setOpaque(false); 
        gridPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 150), 4));
        
        
        gridButtons = new JButton[SIZE][SIZE];
        
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JButton button = new JButton();
                button.setBounds(col * CELL_WIDTH, row * CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT);
                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setBorder(null);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                final int r = row;
                final int c = col;
                button.addActionListener(e -> handleGridClick(r, c));
                
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        showPreview(r, c);
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        clearPreview();
                    }
                });
                
                gridButtons[row][col] = button;
                gridPanel.add(button);
            }
        }
    }
    
    private void drawPlacedShips(Graphics2D g) {
        for (Ship ship : playerBoard.getShips()) {
            List<Ship.Coordinate> positions = ship.getPositions();
            if (positions.isEmpty()) continue;
            
            Image shipImg = getShipImage(ship.getName());
            if (shipImg == null) continue;
            
            int minRow = Integer.MAX_VALUE, minCol = Integer.MAX_VALUE;
            int maxRow = Integer.MIN_VALUE, maxCol = Integer.MIN_VALUE;
            
            for (Ship.Coordinate pos : positions) {
                minRow = Math.min(minRow, pos.getX());
                minCol = Math.min(minCol, pos.getY());
                maxRow = Math.max(maxRow, pos.getX());
                maxCol = Math.max(maxCol, pos.getY());
            }
            
            boolean isHorizontal = (maxCol - minCol) > (maxRow - minRow);
            int shipLength = positions.size();
            
            int width, height;
            if (isHorizontal) {
                width = shipLength * CELL_WIDTH;
                height = CELL_HEIGHT;
            } else {
                width = CELL_WIDTH;
                height = shipLength * CELL_HEIGHT;
            }
            
            int x = minCol * CELL_WIDTH;
            int y = minRow * CELL_HEIGHT;
            
            Image scaledImg;
            if (!isHorizontal) {
                int srcW = shipImg.getWidth(null);
                int srcH = shipImg.getHeight(null);
                if (srcW > 0 && srcH > 0) {
                    BufferedImage rotated = new BufferedImage(srcH, srcW, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = rotated.createGraphics();
                    g2d.translate(srcH / 2, srcW / 2);
                    g2d.rotate(Math.PI / 2);
                    g2d.drawImage(shipImg, -srcW / 2, -srcH / 2, null);
                    g2d.dispose();
                    scaledImg = rotated.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                } else {
                    scaledImg = shipImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                }
            } else {
                scaledImg = shipImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            }
            
            g.drawImage(scaledImg, x, y, width, height, null);
            
            
            g.setColor(new Color(255, 215, 0, 200));
            g.setStroke(new BasicStroke(3));
            g.drawRect(x, y, width, height);
        }
    }
    
    private void showPreview(int row, int col) {
        if (currentShip == null) return;
        
        Graphics g = gridPanel.getGraphics();
        if (g == null) return;
        
        int shipSize = currentShip.getSize();
        
        if (horizontal && col + shipSize > SIZE) return;
        if (!horizontal && row + shipSize > SIZE) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        g2d.setColor(new Color(100, 255, 100));
        
        if (horizontal) {
            g2d.fillRect(col * CELL_WIDTH, row * CELL_HEIGHT, shipSize * CELL_WIDTH, CELL_HEIGHT);
            g2d.setColor(new Color(100, 255, 100, 200));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(col * CELL_WIDTH, row * CELL_HEIGHT, shipSize * CELL_WIDTH, CELL_HEIGHT);
        } else {
            g2d.fillRect(col * CELL_WIDTH, row * CELL_HEIGHT, CELL_WIDTH, shipSize * CELL_HEIGHT);
            g2d.setColor(new Color(100, 255, 100, 200));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(col * CELL_WIDTH, row * CELL_HEIGHT, CELL_WIDTH, shipSize * CELL_HEIGHT);
        }
        
        g2d.dispose();
    }
    
    private void clearPreview() {
        gridPanel.repaint();
    }
    
    private Image getShipImage(String shipName) {
        switch (shipName) {
            case "Carrier": return carrierImage;
            case "Battleship": return battleshipImage;
            case "Cruiser": return cruiserImage;
            case "Submarine": return submarineImage;
            case "Destroyer": return destroyerImage;
            default: return null;
        }
    }
    
    private void resetShipList() {
        shipsToPlace.clear();
        for (Ship ship : originalShipsToPlace) {
            shipsToPlace.add(new Ship(ship.getName(), ship.getSize()));
        }
        if (!shipsToPlace.isEmpty()) {
            currentShip = shipsToPlace.remove(0);
        }
        undoStack.clear();
        if (undoButton != null) {
            undoButton.setEnabled(false);
        }
    }
    
    private void undoLastPlacement() {
        if (undoStack.isEmpty()) {
            undoButton.setEnabled(false);
            return;
        }
        
        UndoAction lastAction = undoStack.pop();
        
        for (Ship.Coordinate pos : lastAction.ship.getPositions()) {
            int x = pos.getX();
            int y = pos.getY();
            Cell cell = playerBoard.getCell(x, y);
            if (cell != null) {
                cell.setHasShip(false);
                cell.setShip(null);
            }
        }
        
        lastAction.ship.getPositions().clear();
        playerBoard.getShips().remove(lastAction.ship);
        shipsToPlace.add(0, lastAction.ship);
        currentShip = shipsToPlace.get(0);
        
        instructionLabel.setText("Place your " + currentShip.getName() + " (" + currentShip.getSize() + " cells) - Click to place");
        rotateButton.setEnabled(true);
        startGameButton.setEnabled(false);
        undoButton.setEnabled(!undoStack.isEmpty());
        
        gridPanel.repaint();
        
        System.out.println("↩️ Undid placement of " + lastAction.ship.getName());
    }
    
    private void resetAllShips() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reset all ship placements?\nThis will clear your current layout.",
            "Reset Placement",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        playerBoard = new Board();
        resetShipList();
        
        rotateButton.setEnabled(true);
        rotateButton.setText("🔄 Rotate");
        horizontal = true;
        startGameButton.setEnabled(false);
        undoButton.setEnabled(false);
        if (currentShip != null) {
            instructionLabel.setText("Place your " + currentShip.getName() + " (" + currentShip.getSize() + " cells) - Click to place");
        }
        
        gridPanel.repaint();
        
        JOptionPane.showMessageDialog(this,
            "All ships have been reset! You can now place them again.",
            "Reset Complete",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void toggleRotation() {
        horizontal = !horizontal;
        rotateButton.setText(horizontal ? "🔄 Rotate (Horizontal)" : "🔄 Rotate (Vertical)");
    }
    
    private void handleGridClick(int row, int col) {
        if (currentShip == null) return;
        
        boolean placed = playerBoard.placeShip(currentShip, row, col, horizontal);
        
        if (placed) {
            Ship placedShip = currentShip;
            undoStack.push(new UndoAction(placedShip, row, col, horizontal));
            undoButton.setEnabled(true);
            
            gridPanel.repaint();
            
            if (!shipsToPlace.isEmpty()) {
                currentShip = shipsToPlace.remove(0);
                instructionLabel.setText("Place your " + currentShip.getName() + 
                    " (" + currentShip.getSize() + " cells) - Click to place");
            } else {
                currentShip = null;
                instructionLabel.setText("All ships placed! Click 'Start Battle!'");
                startGameButton.setEnabled(true);
                rotateButton.setEnabled(false);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Cannot place ship there! Try another position.",
                "Invalid Placement",
                JOptionPane.WARNING_MESSAGE);
        }
    }
}