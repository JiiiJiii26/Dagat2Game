package models;

import java.util.ArrayList;

public class Ship {
    private String name;
    private int size;
    private int hits;
    private boolean isSunk;
    private ArrayList<Coordinate> positions;
    
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






