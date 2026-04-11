package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import models.Board;
import models.Ship;
import models.Cell;
import java.util.ArrayList;
import java.util.Stack;

public class PlacementPanel extends JPanel {
    private JButton[][] gridButtons;
    private Board playerBoard;
    private final int SIZE = 10;
    private Image placementBackgroundImage;
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

        try {
            String imagePath = "D:\\GameProj\\Battleship Game\\assets\\boatPlacementbg.jpg";
            Image image = Toolkit.getDefaultToolkit().getImage(imagePath);
            MediaTracker tracker = new MediaTracker(new JLabel());
            tracker.addImage(image, 0);
            tracker.waitForID(0);
            if (image.getWidth(null) > 0) {
                placementBackgroundImage = image;
                System.out.println("✅ Boat placement background loaded: " + image.getWidth(null) + "x" + image.getHeight(null));
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not load boatPlacementbg.jpg: " + e.getMessage());
        }
        
        
        originalShipsToPlace.add(new Ship("Carrier", 5));
        originalShipsToPlace.add(new Ship("Battleship", 4));
        originalShipsToPlace.add(new Ship("Cruiser", 3));
        originalShipsToPlace.add(new Ship("Submarine", 3));
        originalShipsToPlace.add(new Ship("Destroyer", 2));
        
        resetShipList();
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(700, 800));
        setMinimumSize(new Dimension(700, 800));
        setMaximumSize(new Dimension(700, 800));
        
        
        JPanel topPanel = new JPanel();
        instructionLabel = new JLabel("Place your Carrier (5 cells) - Click to place");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(instructionLabel);
        
        
        JPanel buttonPanel = new JPanel();
        
        rotateButton = new JButton("🔄 Rotate");
        rotateButton.addActionListener(e -> toggleRotation());
        buttonPanel.add(rotateButton);
        
        
        undoButton = new JButton("↩️ Undo");
        undoButton.setBackground(new Color(255, 165, 0));
        undoButton.setForeground(Color.BLACK);
        undoButton.setFont(new Font("Arial", Font.BOLD, 12));
        undoButton.setEnabled(false);
        undoButton.addActionListener(e -> undoLastPlacement());
        buttonPanel.add(undoButton);
        
        
        resetButton = new JButton("🗑️ Reset All");
        resetButton.setBackground(new Color(255, 100, 100));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("Arial", Font.BOLD, 12));
        resetButton.addActionListener(e -> resetAllShips());
        buttonPanel.add(resetButton);
        
        startGameButton = new JButton("⚔️ Start Battle!");
        startGameButton.setEnabled(false);
        startGameButton.addActionListener(e -> {
            listener.onPlacementComplete(playerBoard);
        });
        buttonPanel.add(startGameButton);
        
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(topPanel, BorderLayout.NORTH);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        
        JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE, 0, 0));
        gridPanel.setPreferredSize(new Dimension(700, 800));
        gridPanel.setMaximumSize(new Dimension(700, 800));
        gridPanel.setMinimumSize(new Dimension(700, 800));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (placementBackgroundImage != null) {
            final Image bgImage = placementBackgroundImage.getScaledInstance(700, 800, Image.SCALE_SMOOTH);
            gridPanel = new JPanel(new GridLayout(SIZE, SIZE, 0, 0)) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                }
            };
            gridPanel.setPreferredSize(new Dimension(700, 800));
            gridPanel.setMaximumSize(new Dimension(700, 800));
            gridPanel.setMinimumSize(new Dimension(700, 800));
            gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }
        
        gridButtons = new JButton[SIZE][SIZE];
        
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JPanel cellPanel = new JPanel(new BorderLayout());
                cellPanel.setPreferredSize(new Dimension(70, 80));
                cellPanel.setMaximumSize(new Dimension(70, 70));
                cellPanel.setMinimumSize(new Dimension(70, 70));
                cellPanel.setOpaque(false);
                
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(70, 80));
                button.putClientProperty("row", row);
                button.putClientProperty("col", col);

                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 80), 1));
                
                final int r = row;
                final int c = col;
                button.addActionListener(e -> handleGridClick(r, c));
                
                button.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        showPreview(r, c);
                    }
                    public void mouseExited(MouseEvent e) {
                        clearPreview();
                    }
                });
                
                cellPanel.add(button, BorderLayout.CENTER);
                gridButtons[row][col] = button;
                gridPanel.add(cellPanel);
            }
        }
        
        add(controlPanel, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);
    }
    
    private void resetShipList() {
        shipsToPlace.clear();
        for (Ship ship : originalShipsToPlace) {
            shipsToPlace.add(new Ship(ship.getName(), ship.getSize()));
        }
        currentShip = shipsToPlace.remove(0);
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
            cell.setHasShip(false);
            cell.setShip(null);
            gridButtons[x][y].setText("");
            gridButtons[x][y].setOpaque(false);
            gridButtons[x][y].setContentAreaFilled(false);
            gridButtons[x][y].setBackground(null);
        }
        
        
        lastAction.ship.getPositions().clear();
        
        
        playerBoard.getShips().remove(lastAction.ship);
        
        
        shipsToPlace.add(0, lastAction.ship);
        currentShip = shipsToPlace.get(0);
        
        
        instructionLabel.setText("Place your " + currentShip.getName() + " (" + currentShip.getSize() + " cells) - Click to place");
        
        
        rotateButton.setEnabled(true);
        startGameButton.setEnabled(false);
        
        
        undoButton.setEnabled(!undoStack.isEmpty());
        
        
        clearPreview();
        
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
        
        
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                gridButtons[row][col].setText("");
                gridButtons[row][col].setOpaque(false);
                gridButtons[row][col].setContentAreaFilled(false);
                gridButtons[row][col].setBackground(null);
            }
        }
        
        
        rotateButton.setEnabled(true);
        rotateButton.setText("🔄 Rotate");
        horizontal = true;
        startGameButton.setEnabled(false);
        undoButton.setEnabled(false);
        instructionLabel.setText("Place your " + currentShip.getName() + " (" + currentShip.getSize() + " cells) - Click to place");
        
        JOptionPane.showMessageDialog(this,
            "All ships have been reset! You can now place them again.",
            "Reset Complete",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void toggleRotation() {
        horizontal = !horizontal;
        rotateButton.setText(horizontal ? "🔄 Rotate (Horizontal)" : "🔄 Rotate (Vertical)");
    }
    
    private void setButtonBackground(JButton button) {
        if (placementBackgroundImage != null) {
            Image scaled = placementBackgroundImage.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaled));
        } else {
            button.setBackground(Cell.OCEAN_BLUE);
        }
    }
    
    private void clearCell(JButton button) {
        setButtonBackground(button);
        button.setText("");
    }
    
    private void showPreview(int row, int col) {
        if (currentShip == null) return;
        clearPreview();
        
        if (horizontal) {
            if (col + currentShip.getSize() <= SIZE) {
                for (int i = 0; i < currentShip.getSize(); i++) {
                    if (!playerBoard.getCell(row, col + i).hasShip()) {
                        gridButtons[row][col + i].setOpaque(true);
                        gridButtons[row][col + i].setContentAreaFilled(true);
                        gridButtons[row][col + i].setBackground(new Color(100, 200, 100, 150));
                    }
                }
            }
        } else {
            if (row + currentShip.getSize() <= SIZE) {
                for (int i = 0; i < currentShip.getSize(); i++) {
                    if (!playerBoard.getCell(row + i, col).hasShip()) {
                        gridButtons[row + i][col].setOpaque(true);
                        gridButtons[row + i][col].setContentAreaFilled(true);
                        gridButtons[row + i][col].setBackground(new Color(100, 200, 100, 150));
                    }
                }
            }
        }
    }
    
    private void clearPreview() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = playerBoard.getCell(row, col);
                if (cell.hasShip()) {
                    gridButtons[row][col].setOpaque(true);
                    gridButtons[row][col].setContentAreaFilled(true);
                    gridButtons[row][col].setBackground(new Color(50, 150, 50, 200));
                    gridButtons[row][col].setText("⛵");
                } else {
                    gridButtons[row][col].setOpaque(false);
                    gridButtons[row][col].setContentAreaFilled(false);
                    gridButtons[row][col].setBackground(null);
                    gridButtons[row][col].setText("");
                }
            }
        }
    }

    private void handleGridClick(int row, int col) {
        if (currentShip == null) return;
        
        boolean placed = playerBoard.placeShip(currentShip, row, col, horizontal);
        
        if (placed) {
            
            Ship placedShip = currentShip;
            undoStack.push(new UndoAction(placedShip, row, col, horizontal));
            undoButton.setEnabled(true);
            
            
            for (int i = 0; i < currentShip.getSize(); i++) {
                if (horizontal) {
                    gridButtons[row][col + i].setOpaque(true);
                    gridButtons[row][col + i].setContentAreaFilled(true);
                    gridButtons[row][col + i].setBackground(Cell.SHIP_GREEN);
                } else {
                    gridButtons[row + i][col].setOpaque(true);
                    gridButtons[row + i][col].setContentAreaFilled(true);
                    gridButtons[row + i][col].setBackground(Cell.SHIP_GREEN);
                }
            }
            
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