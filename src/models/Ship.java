package models;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    private String name;
    private int size;
    private List<Coordinate> positions;
    private boolean sunk;
    private boolean shielded;
    private int shieldTurns;
    private boolean infected;
    private boolean fullyRevealed;

    public Ship(String name, int size) {
        this.name = name;
        this.size = size;
        this.positions = new ArrayList<>();
        this.sunk = false;
        this.shielded = false;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public List<Coordinate> getPositions() {
        return positions;
    }

    public void addPosition(int x, int y) {
        positions.add(new Coordinate(x, y));
    }

    public boolean isSunk() {
        return sunk;
    }

    public void setSunk(boolean sunk) {
        this.sunk = sunk;
    }

    public boolean isShielded() {
        return shielded;
    }

    public void setShielded(boolean shielded, int turns) {
        this.shielded = shielded;
        this.shieldTurns = turns;
    }

    public void decrementShieldTurns() {
        if (shieldTurns > 0) {
            shieldTurns--;
            if (shieldTurns == 0) {
                shielded = false;
            }
        }
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    public boolean isInfected() {
        return infected;
    }

    public boolean isFullyRevealed() {
        return fullyRevealed;
    }

    public void setFullyRevealed(boolean fullyRevealed) {
        this.fullyRevealed = fullyRevealed;
    }

    public boolean containsCell(int x, int y) {
        for (Coordinate pos : positions) {
            if (pos.getX() == x && pos.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public void repair() {
        // Implement repair logic, perhaps reset damage or heal
        heal();
    }

    public int getRemainingHealth() {
        // Assuming health is the number of unhit positions, but since we don't track hits per cell here,
        // perhaps return size if not sunk, 0 if sunk
        return isSunk() ? 0 : size;
    }

    public void revive() {
        setSunk(false);
        // Perhaps reset other states
    }

    public void heal() {
        // Implement healing logic if needed
    }

    public static class Coordinate {
        private int x, y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}