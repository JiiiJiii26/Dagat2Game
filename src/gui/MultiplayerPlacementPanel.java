package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import models.Board;
import models.Ship;
import models.Cell;
import java.util.ArrayList;
import java.util.Stack;

public class MultiplayerPlacementPanel extends JPanel {
    
    private JButton[][] gridButtons;
    private Board currentBoard;
    private final int SIZE = 10;
    private int playerNumber;
    private String playerName;
    private ArrayList<Ship> shipsToPlace;
    private ArrayList<Ship> originalShipsToPlace;
    private Ship currentShip;
    private boolean horizontal = true;
    private JLabel instructionLabel;
    private JButton rotateButton;
    private JButton undoButton;
    private JButton resetButton;
    private JButton nextPlayerButton;
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
        void onPlacementComplete(int playerNumber, Board board);
        void onAllPlayersReady();
    }
    
    public MultiplayerPlacementPanel(int playerNumber, String playerName, PlacementListener listener) {
        this.playerNumber = playerNumber;
        this.playerName = playerName;
        this.listener = listener;
        this.currentBoard = new Board();
        this.shipsToPlace = new ArrayList<>();
        this.originalShipsToPlace = new ArrayList<>();
        this.undoStack = new Stack<>();
        
        
        originalShipsToPlace.add(new Ship("Carrier", 5));
        originalShipsToPlace.add(new Ship("Battleship", 4));
        originalShipsToPlace.add(new Ship("Cruiser", 3));
        originalShipsToPlace.add(new Ship("Submarine", 3));
        originalShipsToPlace.add(new Ship("Destroyer", 2));
        
        resetShipList();
        
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 112));
        
        
        JLabel titleLabel = new JLabel(playerName + "'s Fleet Placement", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(173, 216, 230));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        
        JPanel instructionPanel = new JPanel();
        instructionLabel = new JLabel("Place your " + currentShip.getName() + " (" + currentShip.getSize() + " cells)");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        instructionLabel.setForeground(Color.WHITE);
        instructionPanel.add(instructionLabel);
        
        
        JPanel controlPanel = new JPanel();
        
        rotateButton = new JButton("🔄 Rotate");
        rotateButton.addActionListener(e -> toggleRotation());
        controlPanel.add(rotateButton);
        
        
        undoButton = new JButton("↩️ Undo");
        undoButton.setBackground(new Color(255, 165, 0));
        undoButton.setForeground(Color.BLACK);
        undoButton.setFont(new Font("Arial", Font.BOLD, 12));
        undoButton.setEnabled(false);
        undoButton.addActionListener(e -> undoLastPlacement());
        controlPanel.add(undoButton);
        
        
        resetButton = new JButton("🗑️ Reset All");
        resetButton.setBackground(new Color(255, 100, 100));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("Arial", Font.BOLD, 12));
        resetButton.addActionListener(e -> resetAllShips());
        controlPanel.add(resetButton);
        
        nextPlayerButton = new JButton("✅ Complete Placement");
        nextPlayerButton.setEnabled(false);
        nextPlayerButton.addActionListener(e -> {
            if (listener != null) {
                listener.onPlacementComplete(playerNumber, currentBoard);
            }
        });
        controlPanel.add(nextPlayerButton);
        
        JPanel topCenterPanel = new JPanel(new BorderLayout());
        topCenterPanel.add(instructionPanel, BorderLayout.NORTH);
        topCenterPanel.add(controlPanel, BorderLayout.SOUTH);
        centerPanel.add(topCenterPanel, BorderLayout.NORTH);
        
        
        JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE));
        gridPanel.setPreferredSize(new Dimension(400, 400));
        gridButtons = new JButton[SIZE][SIZE];
        
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JButton button = new JButton();
                button.putClientProperty("row", row);
                button.putClientProperty("col", col);
                
                button.setBackground(Cell.OCEAN_BLUE);
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                
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
        
        centerPanel.add(gridPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
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
            Cell cell = currentBoard.getCell(x, y);
            cell.setHasShip(false);
            cell.setShip(null);
            gridButtons[x][y].setBackground(Cell.OCEAN_BLUE);
            gridButtons[x][y].setText("");
        }
        
        
        lastAction.ship.getPositions().clear();
        
        
        currentBoard.getShips().remove(lastAction.ship);
        
        
        shipsToPlace.add(0, lastAction.ship);
        currentShip = shipsToPlace.get(0);
        
        
        instructionLabel.setText("Place your " + currentShip.getName() + " (" + currentShip.getSize() + " cells)");
        
        
        rotateButton.setEnabled(true);
        nextPlayerButton.setEnabled(false);
        
        
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
        
        
        currentBoard = new Board();
        
        
        resetShipList();
        
        
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                gridButtons[row][col].setBackground(Cell.OCEAN_BLUE);
                gridButtons[row][col].setText("");
            }
        }
        
        
        rotateButton.setEnabled(true);
        rotateButton.setText("🔄 Rotate");
        horizontal = true;
        nextPlayerButton.setEnabled(false);
        undoButton.setEnabled(false);
        instructionLabel.setText("Place your " + currentShip.getName() + " (" + currentShip.getSize() + " cells)");
        
        JOptionPane.showMessageDialog(this,
            "All ships have been reset! You can now place them again.",
            "Reset Complete",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void toggleRotation() {
        horizontal = !horizontal;
        rotateButton.setText(horizontal ? "🔄 Rotate (Horizontal)" : "🔄 Rotate (Vertical)");
    }
    
    private void showPreview(int row, int col) {
        if (currentShip == null) return;
        clearPreview();
        
        if (horizontal) {
            if (col + currentShip.getSize() <= SIZE) {
                for (int i = 0; i < currentShip.getSize(); i++) {
                    if (!currentBoard.getCell(row, col + i).hasShip() && 
                        !currentBoard.getCell(row, col + i).isFiredUpon()) {
                        gridButtons[row][col + i].setBackground(new Color(100, 200, 100, 150));
                    }
                }
            }
        } else {
            if (row + currentShip.getSize() <= SIZE) {
                for (int i = 0; i < currentShip.getSize(); i++) {
                    if (!currentBoard.getCell(row + i, col).hasShip() &&
                        !currentBoard.getCell(row + i, col).isFiredUpon()) {
                        gridButtons[row + i][col].setBackground(new Color(100, 200, 100, 150));
                    }
                }
            }
        }
    }
    
    private void clearPreview() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = currentBoard.getCell(row, col);
                if (cell.hasShip()) {
                    gridButtons[row][col].setBackground(Cell.SHIP_GREEN);
                } else {
                    gridButtons[row][col].setBackground(Cell.OCEAN_BLUE);
                }
            }
        }
    }
    
    private void handleGridClick(int row, int col) {
        if (currentShip == null) return;
        
        boolean placed = currentBoard.placeShip(currentShip, row, col, horizontal);
        
        if (placed) {
            
            Ship placedShip = currentShip;
            undoStack.push(new UndoAction(placedShip, row, col, horizontal));
            undoButton.setEnabled(true);
            
            
            for (int i = 0; i < currentShip.getSize(); i++) {
                if (horizontal) {
                    gridButtons[row][col + i].setBackground(Cell.SHIP_GREEN);
                } else {
                    gridButtons[row + i][col].setBackground(Cell.SHIP_GREEN);
                }
            }
            
            if (!shipsToPlace.isEmpty()) {
                currentShip = shipsToPlace.remove(0);
                instructionLabel.setText("Place your " + currentShip.getName() + " (" + currentShip.getSize() + " cells)");
            } else {
                currentShip = null;
                instructionLabel.setText("All ships placed! Click 'Complete Placement' to continue.");
                nextPlayerButton.setEnabled(true);
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