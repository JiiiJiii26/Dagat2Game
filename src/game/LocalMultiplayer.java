package game;

import models.Board;
import models.Ship;
import models.Cell;
import java.util.ArrayList;

public class LocalMultiplayer {
    
    private Board player1Board;
    private Board player2Board;
    private boolean player1Turn = true;
    private GameListener listener;
    
    public interface GameListener {
        void onGameStart();
        void onPlayerTurn(int playerNumber);
        void onShotFired(int playerNumber, int x, int y, ShotResult result);
        void onGameEnd(int winnerPlayerNumber);
        void onBoardUpdate(Board board, boolean isPlayer1);
    }
    public boolean isGameOver() {
    // Check if either player has no ships left
    return player1Board.areAllShipsSunk() || player2Board.areAllShipsSunk();
}

public String getWinner() {
    if (player1Board.areAllShipsSunk()) {
        return "PLAYER 2";
    } else if (player2Board.areAllShipsSunk()) {
        return "PLAYER 1";
    }
    return "NO WINNER YET";
}
    
    public LocalMultiplayer(GameListener listener) {
        this.listener = listener;
        this.player1Board = new Board();
        this.player2Board = new Board();
    }
    
    public Board getPlayer1Board() {
        return player1Board;
    }
    
    public Board getPlayer2Board() {
        return player2Board;
    }
    
    public boolean isPlayer1Turn() {
        return player1Turn;
    }
    
    public int getCurrentPlayer() {
        return player1Turn ? 1 : 2;
    }
    
    public boolean placeShip(int playerNumber, Ship ship, int x, int y, boolean horizontal) {
        Board board = (playerNumber == 1) ? player1Board : player2Board;
        return board.placeShip(ship, x, y, horizontal);
    }
    
    public ShotResult fire(int playerNumber, int x, int y) {
        
        if ((playerNumber == 1 && !player1Turn) || (playerNumber == 2 && player1Turn)) {
            System.out.println("Not your turn!");
            return ShotResult.INVALID;
        }
        
        
        Board targetBoard = (playerNumber == 1) ? player2Board : player1Board;
        
        
        ShotResult result = targetBoard.fire(x, y);
        
        
        if (listener != null) {
            listener.onShotFired(playerNumber, x, y, result);
        }
        
        
        if (targetBoard.allShipsSunk()) {
            if (listener != null) {
                listener.onGameEnd(playerNumber);
            }
        } else {
            
            player1Turn = !player1Turn;
            if (listener != null) {
                listener.onPlayerTurn(getCurrentPlayer());
            }
        }
        
        return result;
    }
    
    public void reset() {
        player1Board = new Board();
        player2Board = new Board();
        player1Turn = true;
    }
    public void setPlayerBoard(int playerNumber, Board board) {
    if (playerNumber == 1) {
        this.player1Board = board;
    } else {
        this.player2Board = board;
    }
}
}