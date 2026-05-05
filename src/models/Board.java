package models;

import game.ShotResult;
import java.util.ArrayList;

public class Board {
    private Cell[][] grid;
    private  ArrayList<Ship> ships;
    private int size;
    private boolean[][] firedUpon;

    // Default constructor (10x10)
    public Board() {
        this(10);
    }
    
    // Constructor with custom size
    public Board(int size) {
        this.size = size;
        this.grid = new Cell[size][size];
        this.ships = new ArrayList<Ship>();
        this.firedUpon = new boolean[size][size];
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = new Cell(i, j);
                firedUpon[i][j] = false;
            }
        }
    }
    
    // Resize the board (preserves ships that fit in new size, clears others)
    public void resize(int newSize) {
        if (newSize == size) {
            System.out.println("Board already size " + size);
            return;
        }
        
        if (newSize < 1) {
            System.out.println("Invalid board size: " + newSize);
            return;
        }
        
        System.out.println("📐 Resizing board from " + size + "x" + size + " to " + newSize + "x" + newSize);
        
        // Create new grid
        Cell[][] newGrid = new Cell[newSize][newSize];
        boolean[][] newFiredUpon = new boolean[newSize][newSize];
        
        // Initialize new grid
        for (int i = 0; i < newSize; i++) {
            for (int j = 0; j < newSize; j++) {
                newGrid[i][j] = new Cell(i, j);
                newFiredUpon[i][j] = false;
            }
        }
        
        // Copy over cells that fit in new size
        int copyLimit = Math.min(size, newSize);
        for (int i = 0; i < copyLimit; i++) {
            for (int j = 0; j < copyLimit; j++) {
                newGrid[i][j] = grid[i][j];
                newFiredUpon[i][j] = firedUpon[i][j];
            }
        }
        
        // Update ships - remove ships that no longer fit or are partially outside
        ArrayList<Ship> validShips = new ArrayList<Ship>();
        for (Ship ship : ships) {
            boolean shipFits = true;
            for (Ship.Coordinate pos : ship.getPositions()) {
                if (pos.getX() >= newSize || pos.getY() >= newSize) {
                    shipFits = false;
                    System.out.println("⚠️ Ship '" + ship.getName() + "' removed during resize (outside bounds)");
                    break;
                }
            }
            if (shipFits) {
                validShips.add(ship);
            }
        }
        
        // Update grid references for remaining ships
        for (Ship ship : validShips) {
            for (Ship.Coordinate pos : ship.getPositions()) {
                newGrid[pos.getX()][pos.getY()].placeShip(ship);
            }
        }
        
        // Apply changes
        this.grid = newGrid;
        this.firedUpon = newFiredUpon;
        this.ships = validShips;
        this.size = newSize;
        
        System.out.println("✅ Board resized to " + size + "x" + size + " with " + ships.size() + " ships remaining");
    }
    
    // Clear and reset board to empty state
    public void clear() {
        this.grid = new Cell[size][size];
        this.ships.clear();
        this.firedUpon = new boolean[size][size];
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = new Cell(i, j);
                firedUpon[i][j] = false;
            }
        }
        
        System.out.println("🧹 Board cleared (size: " + size + "x" + size + ")");
    }
    
    // Check if a ship placement is valid at given position
    public boolean isValidPlacement(Ship ship, int startX, int startY, boolean horizontal) {
        if (horizontal) {
            if (startY + ship.getSize() > size) return false;
            for (int i = 0; i < ship.getSize(); i++) {
                if (grid[startX][startY + i].hasShip()) return false;
            }
        } else {
            if (startX + ship.getSize() > size) return false;
            for (int i = 0; i < ship.getSize(); i++) {
                if (grid[startX + i][startY].hasShip()) return false;
            }
        }
        return true;
    }
    
    public void resetFiredStatus(int x, int y) {
        if (x >= 0 && x < size && y >= 0 && y < size) {
            firedUpon[x][y] = false;
            System.out.println("🔄 Board reset fired status at (" + x + "," + y + ")");
        }
    }
    
    public boolean isCellFiredUpon(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) return true;
        return firedUpon[x][y];
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
        if (x < 0 || x >= size || y < 0 || y >= size) {
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
        
        // No shield, process normal hit
        ShotResult result = cell.fire();
        System.out.println("Fired at (" + x + "," + y + "): " + result);
        return result;
    }
    
    public boolean placeShip(Ship ship, int startX, int startY, boolean horizontal) {
        if (!isValidPlacement(ship, startX, startY, horizontal)) {
            return false;
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
        return size;
    }
    
    public ArrayList<Ship> getShips() {
        return ships;
    }
    
    // Get number of remaining (not sunk) ships
    public int getRemainingShipCount() {
        int count = 0;
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                count++;
            }
        }
        return count;
    }
    
    // Get total ship count
    public int getTotalShipCount() {
        return ships.size();
    }
}