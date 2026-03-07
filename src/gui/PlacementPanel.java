package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import models.Board;
import models.Ship;
import models.Cell;
import java.util.ArrayList;

public class PlacementPanel extends JPanel {
    private JButton[][] gridButtons;
    private Board playerBoard;
    private final int SIZE = 10;
    private ArrayList<Ship> shipsToPlace;
    private Ship currentShip;
    private boolean horizontal = true;
    private JLabel instructionLabel;
    private JButton rotateButton;
    private JButton startGameButton;
    private PlacementListener listener;
    JFrame frame = new JFrame("Tidebound - Fleet Placement");
    
    public interface PlacementListener {
        void onPlacementComplete(Board playerBoard);
    }
    
    public PlacementPanel(PlacementListener listener) {
        this.listener = listener;
        this.playerBoard = new Board();
        this.shipsToPlace = new ArrayList<>();
        
        
        shipsToPlace.add(new Ship("Carrier", 5));
        shipsToPlace.add(new Ship("Battleship", 4));
        shipsToPlace.add(new Ship("Cruiser", 3));
        shipsToPlace.add(new Ship("Submarine", 3));
        shipsToPlace.add(new Ship("Destroyer", 2));
        
        currentShip = shipsToPlace.remove(0);
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 700));
        
        
        JPanel topPanel = new JPanel();
        instructionLabel = new JLabel("Place your Carrier (5 cells) - Click to place");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(instructionLabel);
        
        
        JPanel buttonPanel = new JPanel();
        rotateButton = new JButton("Rotate (Horizontal)");
        rotateButton.addActionListener(e -> toggleRotation());
        buttonPanel.add(rotateButton);
        
        startGameButton = new JButton("Start Battle!");
        startGameButton.setEnabled(false);
        startGameButton.addActionListener(e -> {
            listener.onPlacementComplete(playerBoard);
        });
        buttonPanel.add(startGameButton);
        
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(topPanel, BorderLayout.NORTH);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        
        JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE));
        gridPanel.setPreferredSize(new Dimension(500, 500));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
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
                    public void mouseEntered(MouseEvent e) {
                        showPreview(r, c);
                    }
                    public void mouseExited(MouseEvent e) {
                        clearPreview();
                    }
                });
                
                gridButtons[row][col] = button;
                gridPanel.add(button);
            }
        }
        
        add(controlPanel, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);
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
                    if (!playerBoard.getCell(row, col + i).hasShip()) {
                        gridButtons[row][col + i].setBackground(new Color(200, 255, 200)); 
                    }
                }
            }
        } else {
            if (row + currentShip.getSize() <= SIZE) {
                for (int i = 0; i < currentShip.getSize(); i++) {
                    if (!playerBoard.getCell(row + i, col).hasShip()) {
                        gridButtons[row + i][col].setBackground(new Color(200, 255, 200)); 
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
                    gridButtons[row][col].setBackground(Cell.SHIP_GREEN);
                } else {
                    gridButtons[row][col].setBackground(Cell.OCEAN_BLUE);
                }
            }
        }
    }
    
    private void handleGridClick(int row, int col) {
        if (currentShip == null) return;
        
      
        boolean placed = playerBoard.placeShip(currentShip, row, col, horizontal);
        
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





