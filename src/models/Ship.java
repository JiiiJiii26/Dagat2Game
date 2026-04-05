package models;

import java.util.ArrayList;

public class Ship {
    private String name;
    private int size;
    private int hits;
    private boolean isSunk;
    private ArrayList<Coordinate> positions;
    private boolean isShielded = false;
private int shieldedTurns = 0;
private boolean isInfected = false; 
private boolean isFullyRevealed = false;
 private int[] hitSegments;


    
    
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
        this.hitSegments = new int[size]; 
         for (int i = 0; i < size; i++) {
            hitSegments[i] = 0;
        }
    }
    
    public void hit() {
        if (!isSunk) {
            hits++;
            if (hits >= size) {
                isSunk = true;
            }
        }
    }
    public void hitSegment(int segmentIndex) {
    if (!isSunk && segmentIndex >= 0 && segmentIndex < size) {
        
        if (hitSegments[segmentIndex] == 0) {
            hitSegments[segmentIndex] = 1;
            hits++;
            damageTaken++;
            if (hits >= size) {
                isSunk = true;
                System.out.println("💀 " + name + " has been SUNK!");
            } else {
                System.out.println("💥 " + name + " segment " + segmentIndex + " destroyed! (" + hits + "/" + size + " hits)");
            }
        } else {
            System.out.println("⚠️ Segment " + segmentIndex + " already destroyed!");
        }
    }
}
public boolean isFullyDestroyed() {
    return isSunk;
}

public int[] getHitSegments() {
    return hitSegments;
}

public boolean isSegmentDestroyed(int segmentIndex) {
    return segmentIndex >= 0 && segmentIndex < size && hitSegments[segmentIndex] == 1;
}
    
   public void setInfected(boolean infected) {
        this.isInfected = infected;
        if (infected) {
            System.out.println("🦠 " + name + " has been INFECTED by the virus!");
        }
    }
    public boolean isFullyRevealed() {
    return isFullyRevealed;
}

public void revive() {
    this.hits = 0;
    this.isSunk = false;
    this.damageTaken = 0;
    
    
    for (int i = 0; i < size; i++) {
        hitSegments[i] = 0;
    }
    
    
    this.isInfected = false;
    this.isHidden = false;
    this.isRevealed = false;
    this.isReinforced = false;
    
    
    System.out.println("😺 " + name + " has been FULLY REVIVED with all " + size + " segments restored!");
}
public void heal() {
    this.hits = 0;
    this.isSunk = false;
    this.damageTaken = 0;
    System.out.println("🏥 " + name + " has been fully repaired!");
}
public void setFullyRevealed(boolean revealed) {
    this.isFullyRevealed = revealed;
}

    
    public boolean isInfected() {
        return isInfected;
    }

    public void setShielded(boolean shielded, int turns) {
    this.isShielded = shielded;
    this.shieldedTurns = turns;
     System.out.println("🛡️ " + name + " shield set to: " + shielded);
}
public ArrayList<Coordinate> getPositions() {
        return positions;
    }
    
    
    public boolean isAtPosition(int x, int y) {
        for (Coordinate pos : positions) {
            if (pos.getX() == x && pos.getY() == y) {
                return true;
            }
        }
        return false;
    }


public boolean isShielded() {
    return isShielded;
}

public boolean containsCoordinate(int x, int y) {
    for (Coordinate pos : positions) {
        if (pos.getX() == x && pos.getY() == y) {
            return true;
        }
    }
    return false;
}
public boolean containsCell(int x, int y) {
    for (Coordinate pos : positions) {
        if (pos.getX() == x && pos.getY() == y) {
            return true;
        }
    }
    return false;
}

public void decrementShieldTurns() {
    if (shieldedTurns > 0) {
        shieldedTurns--;
        if (shieldedTurns <= 0) {
            isShielded = false;
            System.out.println("🛡️ " + name + "'s shield has faded.");
        }
    }
}

public int getShieldedTurns() {
    return shieldedTurns;
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