package models;

import game.ShotResult;
import java.awt.Color;

public class Cell {
    private final int x, y;
    private boolean hasShip;
    private boolean isFiredUpon;
    private Ship ship;  
    
   
    public static final Color OCEAN_BLUE = new Color(173, 216, 230);
    public static final Color HIT_RED = new Color(255, 99, 71);
    public static final Color MISS_GRAY = new Color(211, 211, 211);
    public static final Color SHIP_GREEN = new Color(144, 238, 144);
    
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.hasShip = false;
        this.isFiredUpon = false;
        this.ship = null;
    }
    
    public ShotResult fire() {
        if (isFiredUpon) {
            return ShotResult.ALREADY_FIRED;
        }
        
        isFiredUpon = true;
        
        if (hasShip) {
            ship.hitAt(x, y);
            if (ship.isSunk()) {
                return ShotResult.SUNK;
            }
            return ShotResult.HIT;
        }
        
        return ShotResult.MISS;
    }
    
    public void placeShip(Ship ship) {
        this.hasShip = true;
        this.ship = ship;
        ship.addPosition(x, y);
    }
    
    public boolean hasShip() {
        return hasShip;
    }
    
    public boolean isFiredUpon() {
        return isFiredUpon;
    }
    
    public Color getColor() {
        if (!isFiredUpon) {
            return OCEAN_BLUE;
        } else if (hasShip) {
            return HIT_RED;
        } else {
            return MISS_GRAY;
        }
    }
}






