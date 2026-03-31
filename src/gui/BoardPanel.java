package gui;

import game.ShotResult;
import java.awt.*;
import javax.swing.*;
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
    
    public interface PlayerClickHandler {
        void onPlayerCellClicked(int row, int col);
    }
    
    public void setPlayerClickHandler(PlayerClickHandler handler) {
        this.playerClickHandler = handler;
    }
    
    // Backward compatible constructor for existing code
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
        
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board.getCell(i, j).setPlayerBoard(isPlayerBoard);
            }
        }
        
        setLayout(new GridLayout(SIZE, SIZE));
        setPreferredSize(new Dimension(400, 400));
        
        gridButtons = new JButton[SIZE][SIZE];
        
        initializeButtons();
    }
    
    private void initializeButtons() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JButton button = new JButton();
                button.putClientProperty("row", row);
                button.putClientProperty("col", col);
                
                Cell cell = board.getCell(row, col);
                
                updateButtonAppearance(button, cell);
                
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                
                button.addActionListener(e -> {
                    JButton clicked = (JButton) e.getSource();
                    int r = (int) clicked.getClientProperty("row");
                    int c = (int) clicked.getClientProperty("col");
                    handleClick(r, c);
                });
                
                gridButtons[row][col] = button;
                add(button);
            }
        }
    }
    
    private void updateButtonAppearance(JButton button, Cell cell) {
        Color cellColor = cell.getColor();
        
        // Check if it's a hit (red/purple) or miss (gray) based on color
        if (cellColor.equals(Cell.HIT_RED) || cellColor.equals(Cell.INFECTED_HIT)) {
            button.setBackground(cellColor);
            // Check if it's a sunk ship or just hit
            if (cell.hasShip() && cell.getShip() != null && cell.getShip().isSunk()) {
                button.setText("💀");
            } else {
                button.setText("💥");
            }
        } 
        else if (cellColor.equals(Cell.MISS_GRAY)) {
            button.setBackground(Cell.MISS_GRAY);
            button.setText("•");
        } 
        else {
            // Not hit or miss - show water
            button.setBackground(cellColor);
            button.setText("");
            
            // Only show ships if showShips is true (player's own board)
            if (showShips && cell.hasShip()) {
                // Don't change background color, just add a ship icon or keep the ship color
                if (cell.getShip() != null && cell.getShip().isShielded()) {
                    button.setText("🛡️"); // Shielded ship
                } else if (cell.getShip() != null && cell.getShip().isInfected()) {
                    button.setText("🦠"); // Infected ship
                } else {
                    button.setText("⛵"); // Regular ship
                }
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
    
    public interface EnemyClickHandler {
        void onEnemyCellClicked(int row, int col);
    }
    
    private EnemyClickHandler enemyClickHandler;
    
    public void setEnemyClickHandler(EnemyClickHandler handler) {
        this.enemyClickHandler = handler;
    }
    
    public void updateCell(int row, int col, ShotResult result) {
        Cell cell = board.getCell(row, col);
        JButton button = gridButtons[row][col];
        
        switch(result) {
            case HIT:
                button.setBackground(Cell.HIT_RED);
                button.setText("💥");
                break;
            case MISS:
                button.setBackground(Cell.MISS_GRAY);
                button.setText("•");
                break;
            case SUNK:
                button.setBackground(Cell.HIT_RED);
                button.setText("💀");
                break;
            default:
                break;
        }
    }
    
    public void refreshColors() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = board.getCell(row, col);
                JButton button = gridButtons[row][col];
                updateButtonAppearance(button, cell);
            }
        }
        repaint();
    }
}