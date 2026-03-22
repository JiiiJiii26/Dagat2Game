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
    private PlayerClickHandler playerClickHandler; 
    
     public interface PlayerClickHandler {
        void onPlayerCellClicked(int row, int col);
    }
    public void setPlayerClickHandler(PlayerClickHandler handler) {
        this.playerClickHandler = handler;
    }
    public BoardPanel(boolean isPlayerBoard) {
      this(isPlayerBoard, new Board());
    }
    public BoardPanel(boolean isPlayerBoard, Board existingBoard) {
    this.isPlayerBoard = isPlayerBoard;
    this.board = existingBoard;
    
    setLayout(new GridLayout(SIZE, SIZE));
    setPreferredSize(new Dimension(400, 400));
    
    gridButtons = new JButton[SIZE][SIZE];
    
    
    if (isPlayerBoard && existingBoard.getShips().isEmpty()) {
        Ship carrier = new Ship("Carrier", 5);
        board.placeShip(carrier, 2, 3, true);
    }
    
    initializeButtons();
}
    
    private void initializeButtons() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JButton button = new JButton();
                button.putClientProperty("row", row);
                button.putClientProperty("col", col);
                
             
                Cell cell = board.getCell(row, col);
                button.setBackground(cell.getColor());
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                
             
                if (isPlayerBoard && cell.hasShip()) {
                    button.setBackground(Cell.SHIP_GREEN);
                }
                
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
    
   private void handleClick(int row, int col) {
        if (isPlayerBoard) {
            // For player's own board (for skills like Aeris shield)
            if (playerClickHandler != null) {
                playerClickHandler.onPlayerCellClicked(row, col);
            }
        } else {
            // For enemy board
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
    switch(result) {
        case HIT:
            gridButtons[row][col].setBackground(Cell.HIT_RED);
            gridButtons[row][col].setText("💥");
            break;
        case MISS:
            gridButtons[row][col].setBackground(Cell.MISS_GRAY);
            gridButtons[row][col].setText("•");
            break;
        case SUNK:
            gridButtons[row][col].setBackground(Cell.HIT_RED);
            gridButtons[row][col].setText("💀");
            break;
        default:
            break;
    }
 }
 public void refreshColors() {
    for (int row = 0; row < SIZE; row++) {
        for (int col = 0; col < SIZE; col++) {
            Cell cell = board.getCell(row, col);
            gridButtons[row][col].setBackground(cell.getColor());
            gridButtons[row][col].repaint();
        }
    }
}
}






