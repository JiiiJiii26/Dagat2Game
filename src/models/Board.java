package models;

import game.ShotResult;
import java.util.ArrayList;

public class Board {
    private final Cell[][] grid;
    private final ArrayList<Ship> ships;
    private final int SIZE = 10;
    private boolean[][] firedUpon;

      public void resetFiredStatus(int x, int y) {
        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE) {
            firedUpon[x][y] = false;
            System.out.println("🔄 Board reset fired status at (" + x + "," + y + ")");
        }
    }
      public boolean isCellFiredUpon(int x, int y) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) return true;
        return firedUpon[x][y];
    }
    
    public Board() {
        grid = new Cell[SIZE][SIZE];
        ships = new ArrayList<Ship>();
        firedUpon = new boolean[SIZE][SIZE];
        
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
        
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                firedUpon[i][j] = false;
            }
        }
    }
    
   
    
    public boolean areAllShipsSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }
    
    public String getDuplicateShotMessage(int x, int y) {
        return "⚠️ You already shot at (" + x + "," + y + ")!\nChoose another cell!";
    }
    
    public ShotResult fire(int x, int y) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
            return ShotResult.INVALID;
        }
        
        if (firedUpon[x][y]) {
            System.out.println("Already fired at (" + x + "," + y + ")");
            return ShotResult.ALREADY_FIRED;
        }
        
        firedUpon[x][y] = true;
        Cell cell = grid[x][y];
        
        // Check shield FIRST - blocks the hit and consumes the shield
        if (cell.hasShip() && cell.getShip() != null && cell.getShip().isShielded()) {
            System.out.println("🔵 Fortress Mode blocked the attack at (" + x + "," + y + ")!");
            cell.getShip().consumeShield();
            return ShotResult.MISS;
        }
        
        // No shield, process normal hit (removed the duplicate cell.isFiredUpon check)
        ShotResult result = cell.fire();
        System.out.println("Fired at (" + x + "," + y + "): " + result);
        return result;
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