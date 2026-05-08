package models;

import java.awt.Color;

public class Cell {
    public static final Color HIT_RED = Color.RED;
    public static final Color INFECTED_HIT = Color.ORANGE; // Placeholder
    public static final Color MISS_GRAY = Color.GRAY;
    private Ship ship;
    private boolean firedUpon;
    private boolean revealed;

    public Cell() {
        this.ship = null;
        this.firedUpon = false;
        this.revealed = false;
    }

    public boolean hasShip() {
        return ship != null;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public void setHasShip(boolean hasShip) {
        if (!hasShip) {
            this.ship = null;
        }
    }

    public boolean isFiredUpon() {
        return firedUpon;
    }

    public void setFiredUpon(boolean firedUpon) {
        this.firedUpon = firedUpon;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public void resetFiredUpon() {
        this.firedUpon = false;
    }

    public java.awt.Color getColor() {
        // Placeholder: return a color based on state
        if (firedUpon && hasShip()) {
            return java.awt.Color.RED; // Hit
        } else if (firedUpon) {
            return java.awt.Color.GRAY; // Miss
        } else if (hasShip() && revealed) {
            return java.awt.Color.BLUE; // Ship revealed
        } else {
            return java.awt.Color.CYAN; // Water
        }
    }
}