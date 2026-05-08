package models;

import java.util.ArrayList;
import java.util.List;
import game.ShotResult;

public class Board {
    private static final int SIZE = 10;
    private Cell[][] cells;
    private List<Ship> ships;

    public Board() {
        cells = new Cell[SIZE][SIZE];
        ships = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    public boolean placeShip(Ship ship, int x, int y, boolean horizontal) {
        int length = ship.getSize();
        if (horizontal) {
            if (y + length > SIZE) return false;
            for (int i = 0; i < length; i++) {
                if (cells[x][y + i].hasShip()) return false;
            }
            for (int i = 0; i < length; i++) {
                cells[x][y + i].setShip(ship);
                ship.addPosition(x, y + i);
            }
        } else {
            if (x + length > SIZE) return false;
            for (int i = 0; i < length; i++) {
                if (cells[x + i][y].hasShip()) return false;
            }
            for (int i = 0; i < length; i++) {
                cells[x + i][y].setShip(ship);
                ship.addPosition(x + i, y);
            }
        }
        ships.add(ship);
        return true;
    }

    public List<Ship> getShips() {
        return ships;
    }

    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    public ShotResult fire(int x, int y) {
        Cell cell = cells[x][y];
        if (cell.isFiredUpon()) {
            return ShotResult.MISS; // Already fired
        }
        cell.setFiredUpon(true);
        if (cell.hasShip()) {
            Ship ship = cell.getShip();
            // Check if ship is sunk
            boolean sunk = true;
            for (Ship.Coordinate pos : ship.getPositions()) {
                if (!cells[pos.getX()][pos.getY()].isFiredUpon()) {
                    sunk = false;
                    break;
                }
            }
            if (sunk) {
                ship.setSunk(true);
                return ShotResult.SUNK;
            }
            return ShotResult.HIT;
        }
        return ShotResult.MISS;
    }

    public boolean allShipsSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

    public boolean isCellFiredUpon(int x, int y) {
        return cells[x][y].isFiredUpon();
    }

    public void resetFiredStatus(int x, int y) {
        cells[x][y].resetFiredUpon();
    }

    // Other methods as needed
}