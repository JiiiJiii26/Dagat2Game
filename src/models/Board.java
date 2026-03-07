package models;

import game.ShotResult;
import java.util.ArrayList;
public class Board {
    private final Cell[][] grid;
    private final ArrayList<Ship> ships;
    private final int SIZE = 10;
    
    public Board() {
        grid = new Cell[SIZE][SIZE];
        ships = new ArrayList<>();
        
        
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
    }
    
    public ShotResult fire(int x, int y) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
            return ShotResult.INVALID;
        }
        return grid[x][y].fire();
    }
    
    public boolean placeShip(Ship ship, int startX, int startY, boolean horizontal) {
        
        if (horizontal) {
            if (startY + ship.getSize() > SIZE) return false;
            for (int i = 0; i < ship.getSize(); i++) {
                if (grid[startX][startY + i].hasShip()) return false;
            }
        } else {
            if (startX + ship.getSize() > SIZE) return false;
            for (int i = 0; i < ship.getSize(); i++) {
                if (grid[startX + i][startY].hasShip()) return false;
            }
        }
        
        
        if (horizontal) {
            for (int i = 0; i < ship.getSize(); i++) {
                grid[startX][startY + i].placeShip(ship);
            }
        } else {
            for (int i = 0; i < ship.getSize(); i++) {
                grid[startX + i][startY].placeShip(ship);
            }
        }
        
        ships.add(ship);
        return true;
    }
    
    public boolean allShipsSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }
    
    public Cell getCell(int x, int y) {
        return grid[x][y];
    }
    
    public int getSize() {
        return SIZE;
    }
    public ArrayList<Ship> getShips() {
    return ships;
}

}



