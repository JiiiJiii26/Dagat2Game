package gui;

import game.ShotResult;
import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import models.Board;
import models.Cell;
import models.Ship; 
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentListener;

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

          setDoubleBuffered(true);
           setOpaque(true);  
    setBackground(new Color(20, 40, 60));  
        
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board.getCell(i, j).setPlayerBoard(isPlayerBoard);
            }
        }
        
        setLayout(new GridLayout(SIZE, SIZE, 2, 2));
        setPreferredSize(new Dimension(400, 400));
        setBackground(new Color(20, 40, 60));
        
        gridButtons = new JButton[SIZE][SIZE];
        
        initializeButtons();
    }
    
  private void initializeButtons() {
    for (int row = 0; row < SIZE; row++) {
        for (int col = 0; col < SIZE; col++) {
            final int currentRow = row;
            final int currentCol = col;
            
            JButton button = new JButton();
            button.putClientProperty("row", currentRow);
            button.putClientProperty("col", currentCol);
            
            button.setContentAreaFilled(true);
            button.setBorderPainted(true);
            button.setFocusPainted(false);
             button.setRolloverEnabled(false);  
            button.setContentAreaFilled(true); 
            button.setOpaque(true);   
            
            Cell cell = board.getCell(currentRow, currentCol);
            
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            button.setBorder(BorderFactory.createLineBorder(new Color(60, 120, 160, 100), 1));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setOpaque(true);
            button.setBackground(Cell.OCEAN_BLUE); 
            
            updateButtonAppearance(button, cell);
            
            
            button.addMouseListener(new MouseAdapter() {
                
            });
            
            button.addActionListener(e -> {
                JButton clicked = (JButton) e.getSource();
                int r = (int) clicked.getClientProperty("row");
                int c = (int) clicked.getClientProperty("col");
                handleClick(r, c);
            });
            
            gridButtons[currentRow][currentCol] = button;
            add(button);
        }
    }
}
    
private void updateButtonAppearance(JButton button, Cell cell) {
    
    button.setOpaque(true);
    
    Color cellColor = cell.getColor();
    
    
    button.setText("");
    button.setIcon(null);
    
    
    if (cellColor.equals(Cell.HIT_RED) || cellColor.equals(Cell.INFECTED_HIT)) {
        button.setBackground(new Color(200, 60, 50, 180));
        button.setForeground(Color.WHITE);
        if (cell.hasShip() && cell.getShip() != null && cell.getShip().isSunk()) {
            button.setText("💀");
        } else {
            button.setText("💥");
        }
    } 
    
    else if (cellColor.equals(Cell.MISS_GRAY)) {
        button.setBackground(new Color(80, 100, 120, 150));
        button.setForeground(Color.WHITE);
        button.setText("•");
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
    } 
    
    else {
        int row = (int) button.getClientProperty("row");
        int col = (int) button.getClientProperty("col");
        
        if ((row + col) % 2 == 0) {
            button.setBackground(new Color(30, 80, 120, 100));
        } else {
            button.setBackground(new Color(40, 100, 140, 100));
        }
        
        
        if ((showShips || cell.isRevealed()) && cell.hasShip() && !cell.isFiredUpon()) {
            button.setBackground(new Color(60, 140, 80, 180));
            if (cell.getShip() != null && cell.getShip().isShielded()) {
                button.setText("🛡️");
            } else if (cell.getShip() != null && cell.getShip().isInfected()) {
                button.setText("🦠");
            } else {
                button.setText("⛵");
            }
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        }
    }
    
    
    button.paintImmediately(button.getBounds());
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
                button.setBackground(new Color(220, 70, 60, 200));
                button.setText("💥");
                button.setForeground(Color.WHITE);
                break;
            case MISS:
                button.setBackground(new Color(80, 100, 120, 180));
                button.setText("•");
                button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
                break;
            case SUNK:
                button.setBackground(new Color(150, 40, 30, 200));
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