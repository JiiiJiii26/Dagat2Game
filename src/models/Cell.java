package models;

import game.ShotResult;
import java.awt.Color;

public class Cell {
    private final int x, y;
    private boolean hasShip;
    private boolean isFiredUpon;
    private Ship ship;  
      private boolean isRevealed; 
     private boolean isPlayerBoard = false;
    
   public static final Color SHIELD_BLUE = new Color(0, 100, 200);
    public static final Color OCEAN_BLUE = new Color(173, 216, 230);
    public static final Color HIT_RED = new Color(255, 99, 71);
    public static final Color MISS_GRAY = new Color(211, 211, 211);
    public static final Color SHIP_GREEN = new Color(144, 238, 144);
    public static final Color INFECTED_PURPLE = new Color(128, 0, 128); 
    public static final Color INFECTED_HIT = new Color(200, 0, 200);
    
    
    
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.hasShip = false;
        this.isFiredUpon = false;
        this.ship = null;
         this.isPlayerBoard = false; 
    }
        public void setRevealed(boolean revealed) {
        this.isRevealed = revealed;
    }
    
    public boolean isRevealed() {
        return isRevealed;
    }
      public void setPlayerBoard(boolean isPlayer) {
        this.isPlayerBoard = isPlayer;
    }

      public void setFiredUpon(boolean fired) {
        this.isFiredUpon = fired;
    }
    
    public void setHasShip(boolean hasShip) {
        this.hasShip = hasShip;
    }
    
    public void setShip(Ship ship) {
        this.ship = ship;
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
    
    if (isRevealed && !isFiredUpon) {
        if (hasShip) {
            return new Color(255, 200, 100);
        } else {
            return new Color(200, 200, 150);
        }
    }
    
    
    if (isFiredUpon) {
        if (hasShip) {
            if (ship != null && ship.isInfected()) {
                return INFECTED_HIT;
            }
            return HIT_RED;
        } else {
            return MISS_GRAY;
        }
    }
    
    
    if (isPlayerBoard) {
        
        if (ship != null && ship.isShielded()) {
            return SHIELD_BLUE;  
        }
        
        if (hasShip && ship != null && ship.isInfected()) {
            return INFECTED_PURPLE;
        }
        
        if (hasShip) {
            return SHIP_GREEN;
        }
        return OCEAN_BLUE;
    }
    
    
    else {
        return OCEAN_BLUE;
    }
}
public Ship getShip() {
    return ship;
}

public void resetFiredUpon() {
    this.isFiredUpon = false;
}
public void reviveShipSegment() {
    this.isFiredUpon = false;  // Allow the cell to be shown as repaired
    // Don't remove the ship - it's still there, just revived
    System.out.println("😺 Cell (" + x + "," + y + ") has been revived!");
}
}






