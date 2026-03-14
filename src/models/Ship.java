package models;

import java.util.ArrayList;

public class Ship {
    private String name;
    private int size;
    private int hits;
    private boolean isSunk;
    private ArrayList<Coordinate> positions;
    
    
    private boolean isHidden = false;
    private boolean isRevealed = false;
    private boolean isReinforced = false;
    private int damageTaken = 0;
    
    public Ship(String name, int size) {
        this.name = name;
        this.size = size;
        this.hits = 0;
        this.isSunk = false;
        this.positions = new ArrayList<>();
    }
    
    public void addPosition(int x, int y) {
        positions.add(new Coordinate(x, y));
    }
    
    public boolean hitAt(int x, int y) {
        for (Coordinate pos : positions) {
            if (pos.getX() == x && pos.getY() == y) {
                hits++;
                damageTaken++;
                if (hits >= size) {
                    isSunk = true;
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean isSunk() {
        return isSunk;
    }
    
    public String getName() {
        return name;
    }
    
    public int getSize() {
        return size;
    }
    
    public int getHits() {
        return hits;
    }
    
    public int getRemainingHealth() {
        return size - hits;
    }
    
    
    
    public void setHidden(boolean hidden) {
        this.isHidden = hidden;
        if (hidden) {
            System.out.println("🌫️ " + name + " is now hidden!");
        } else {
            System.out.println("🔓 " + name + " is no longer hidden.");
        }
    }
    
    public boolean isHidden() {
        return isHidden;
    }
    
    public void setRevealed(boolean revealed) {
        this.isRevealed = revealed;
        if (revealed) {
            System.out.println("👁️ " + name + " has been revealed!");
        }
    }
    
    public boolean isRevealed() {
        return isRevealed;
    }
    
    public void setReinforced(boolean reinforced) {
        this.isReinforced = reinforced;
    }
    
    public boolean isReinforced() {
        return isReinforced;
    }
    
    public boolean isDamaged() {
        return hits > 0 && hits < size;
    }
    
    public void repair() {
        if (hits > 0) {
            hits--;
            damageTaken--;
            if (hits < size) {
                isSunk = false;
            }
            System.out.println("🔧 " + name + " was repaired!");
        }
    }
    
    public int getDamageTaken() {
        return damageTaken;
    }
    
    
    
    public static class Coordinate {
        private int x, y;
        
        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
    }
}