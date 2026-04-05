package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import models.Board;
import models.Ship;
import models.Cell;
import java.util.ArrayList;

public class MultiplayerPlacementPanel extends JPanel {
    
    private JButton[][] gridButtons;
    private Board currentBoard;
    private final int SIZE = 10;
    private int playerNumber;
    private String playerName;
    private ArrayList<Ship> shipsToPlace;
    private Ship currentShip;
    private boolean horizontal = true;
    private JLabel instructionLabel;
    private JButton rotateButton;
    private JButton nextPlayerButton;
    private PlacementListener listener;
    
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
        
        
        shipsToPlace.add(new Ship("Carrier", 5));
        shipsToPlace.add(new Ship("Battleship", 4));
        shipsToPlace.add(new Ship("Cruiser", 3));
        shipsToPlace.add(new Ship("Submarine", 3));
        shipsToPlace.add(new Ship("Destroyer", 2));
        
        currentShip = shipsToPlace.remove(0);
        
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
        rotateButton = new JButton("Rotate (Horizontal)");
        rotateButton.addActionListener(e -> toggleRotation());
        controlPanel.add(rotateButton);
        
        nextPlayerButton = new JButton("Complete Placement");
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
    
    private void toggleRotation() {
        horizontal = !horizontal;
        rotateButton.setText(horizontal ? "Rotate (Horizontal)" : "Rotate (Vertical)");
    }
    
    private void showPreview(int row, int col) {
        if (currentShip == null) return;
        clearPreview();
        
        if (horizontal) {
            if (col + currentShip.getSize() <= SIZE) {
                for (int i = 0; i < currentShip.getSize(); i++) {
                    if (!currentBoard.getCell(row, col + i).hasShip() && 
                        !currentBoard.getCell(row, col + i).isFiredUpon()) {
                        gridButtons[row][col + i].setBackground(new Color(100, 200, 100));
                    }
                }
            }
        } else {
            if (row + currentShip.getSize() <= SIZE) {
                for (int i = 0; i < currentShip.getSize(); i++) {
                    if (!currentBoard.getCell(row + i, col).hasShip() &&
                        !currentBoard.getCell(row + i, col).isFiredUpon()) {
                        gridButtons[row + i][col].setBackground(new Color(100, 200, 100));
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