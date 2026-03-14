package characters;

import models.Board;
import models.Cell;
import models.Ship;
import game.ShotResult;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Kael extends GameCharacter {
    
    private Random random = new Random();
    private int currentEnergy;
    private static final int MAX_ENERGY = 500;
    private int speed = 85;
    
    // Skill cooldowns
    private int silentDriftCooldown = 0;
    private int sonarPulseCooldown = 0;
    private int depthChargeCooldown = 0;
    private int tempestLockCooldown = 0;
    
    // Hidden ships tracking
    private ArrayList<Ship> hiddenShips = new ArrayList<>();
    private Map<Ship, Integer> shipHideTurns = new HashMap<>();
    
    // Revealed enemy ships
    private ArrayList<Ship> revealedEnemyShips = new ArrayList<>();
    private Map<Ship, Integer> shipRevealTurns = new HashMap<>();
    
    // Tracking for depth charge bonus
    private boolean lastTargetWasHidden = false;
    private int lastCellsDestroyed = 0;
    
    public Kael() {
        super(
            "Kael - Shadow Navigator",
            "A master of stealth who strikes from the shadows. His fleet is invisible until it's too late.",
            2200, // HP (character health, not ship health)
            100,
            new Color(75, 0, 130)  // Deep purple - stealth color
        );
        this.currentEnergy = MAX_ENERGY;
        this.abilityName = "Shadow Navigation";
        this.abilityDescription = "Uses energy to hide ships, reveal enemies, and destroy ship segments.";
    }
    
    // ===================== ENERGY SYSTEM =====================
    
    public int getCurrentEnergy() {
        return currentEnergy;
    }
    
    public int getMaxEnergy() {
        return MAX_ENERGY;
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public boolean hasEnoughEnergy(int cost) {
        return currentEnergy >= cost;
    }
    
    public void spendEnergy(int cost) {
        if (hasEnoughEnergy(cost)) {
            currentEnergy -= cost;
            System.out.println("⚡ Kael spent " + cost + " energy. Remaining: " + currentEnergy);
        }
    }
    
    public void regenerateEnergy(int amount) {
        currentEnergy += amount;
        if (currentEnergy > MAX_ENERGY) {
            currentEnergy = MAX_ENERGY;
        }
    }
    
    // ===================== SKILL 1: SILENT DRIFT =====================
    // Hide one of your own ships for 2 turns
    
    public boolean useSilentDrift(Board playerBoard) {
        if (silentDriftCooldown > 0) {
            System.out.println("⏳ Silent Drift is on cooldown for " + silentDriftCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughEnergy(80)) {
            System.out.println("⚠️ Not enough energy! Need 80 energy, have " + currentEnergy);
            return false;
        }
        
        System.out.println("🌫️ KAEL uses SILENT DRIFT: \"Hide one boat for 2 turns.\"");
        spendEnergy(80);
        
        // Find a ship that isn't already hidden to hide
        boolean shipHidden = false;
        for (Ship ship : playerBoard.getShips()) {
            if (!ship.isSunk() && !hiddenShips.contains(ship)) {
                // Hide this ship
                hiddenShips.add(ship);
                shipHideTurns.put(ship, 2); // Hidden for 2 turns
                ship.setHidden(true);
                System.out.println("🚢 " + ship.getName() + " is now hidden for 2 turns!");
                shipHidden = true;
                break;
            }
        }
        
        if (!shipHidden) {
            System.out.println("❌ No available ships to hide!");
            return false;
        }
        
        silentDriftCooldown = 2; // 2 turns cooldown
        return true;
    }
    
    // ===================== SKILL 2: SONAR PULSE =====================
    // Reveals a hidden enemy ship and destroys ONE of its segments
    
    public boolean useSonarPulse(Board enemyBoard) {
        if (sonarPulseCooldown > 0) {
            System.out.println("⏳ Sonar Pulse is on cooldown for " + sonarPulseCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughEnergy(120)) {
            System.out.println("⚠️ Not enough energy! Need 120 energy, have " + currentEnergy);
            return false;
        }
        
        System.out.println("📡 KAEL uses SONAR PULSE: \"Reveal and destroy one segment of a hidden ship.\"");
        spendEnergy(120);
        
        // Find a hidden enemy ship to reveal
        boolean shipRevealed = false;
        for (Ship ship : enemyBoard.getShips()) {
            if (!ship.isSunk() && ship.isHidden() && !revealedEnemyShips.contains(ship)) {
                // Reveal the ship
                revealedEnemyShips.add(ship);
                shipRevealTurns.put(ship, 1); // Revealed for 1 turn
                ship.setRevealed(true);
                
                // Destroy ONE random segment of this ship
                int segmentsDestroyed = destroyShipSegments(enemyBoard, ship, 1);
                
                System.out.println("🎯 Enemy " + ship.getName() + " revealed and lost " + 
                                   segmentsDestroyed + " segment(s)!");
                shipRevealed = true;
                break;
            }
        }
        
        if (!shipRevealed) {
            System.out.println("❌ No hidden enemy ships found!");
        }
        
        sonarPulseCooldown = 3; // 3 turns cooldown
        return shipRevealed;
    }
    
    // ===================== SKILL 3: DEPTH CHARGE BARRAGE =====================
    // Destroys a 2x2 area - if it hits a hidden ship, destroys an ADDITIONAL segment
    
    public int useDepthChargeBarrage(Board enemyBoard, int centerX, int centerY) {
        if (depthChargeCooldown > 0) {
            System.out.println("⏳ Depth Charge Barrage is on cooldown for " + depthChargeCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughEnergy(200)) {
            System.out.println("⚠️ Not enough energy! Need 200 energy, have " + currentEnergy);
            return 0;
        }
        
        System.out.println("💣 KAEL uses DEPTH CHARGE BARRAGE! \"Targeting a 2x2 area!\"");
        spendEnergy(200);
        
        // Define the area (2x2 area)
        int minX = Math.max(0, centerX - 1);
        int maxX = Math.min(9, centerX);
        int minY = Math.max(0, centerY - 1);
        int maxY = Math.min(9, centerY);
        
        int cellsDestroyed = 0;
        boolean hitHiddenShip = false;
        
        // Hit all cells in the 2x2 area
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Cell cell = enemyBoard.getCell(x, y);
                if (!cell.isFiredUpon()) {
                    ShotResult result = enemyBoard.fire(x, y);
                    cellsDestroyed++;
                    
                    // Check if this cell belonged to a hidden ship
                    if (cell.hasShip()) {
                        for (Ship ship : enemyBoard.getShips()) {
                            if (ship.isHidden()) {
                                hitHiddenShip = true;
                            }
                        }
                    }
                    
                    System.out.println("💥 Depth Charge destroyed cell (" + x + "," + y + ")");
                }
            }
        }
        lastTargetWasHidden = hitHiddenShip;
        
        // Bonus: If we hit a hidden ship, destroy one additional random cell
        if (hitHiddenShip) {
            System.out.println("🎯 BONUS: Hit a hidden ship! Destroying one more segment!");
            for (int attempt = 0; attempt < 20; attempt++) {
                int x = random.nextInt(10);
                int y = random.nextInt(10);
                Cell cell = enemyBoard.getCell(x, y);
                if (!cell.isFiredUpon()) {
                    enemyBoard.fire(x, y);
                    cellsDestroyed++;
                    System.out.println("💥 Bonus segment at (" + x + "," + y + ") destroyed!");
                    break;
                }
            }
        }
        
        System.out.println("💣 Depth Charge destroyed " + cellsDestroyed + " ship segments!");
        lastCellsDestroyed = cellsDestroyed;
        
        depthChargeCooldown = 4; // 4 turns cooldown
        return cellsDestroyed;
    }
    
    // ===================== ULTIMATE: TEMPEST LOCK =====================
    // Destroys ALL cells in a 3x3 area - devastating area attack
    
    public int useTempestLock(Board enemyBoard, int centerX, int centerY) {
        if (tempestLockCooldown > 0) {
            System.out.println("⏳ Tempest Lock is on cooldown for " + tempestLockCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughEnergy(300)) {
            System.out.println("⚠️ Not enough energy! Need 300 energy, have " + currentEnergy);
            return 0;
        }
        
        System.out.println("🌪️ KAEL uses TEMPEST LOCK - ULTIMATE! \"Total annihilation!\"");
        spendEnergy(300);
        
        // Define the area (3x3 area around target)
        int minX = Math.max(0, centerX - 1);
        int maxX = Math.min(9, centerX + 1);
        int minY = Math.max(0, centerY - 1);
        int maxY = Math.min(9, centerY + 1);
        
        int cellsDestroyed = 0;
        int shipsHit = 0;
        StringBuilder hitReport = new StringBuilder("🌪️ Tempest Lock destroys:\n");
        
        // Destroy ALL cells in the area (cannot miss)
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Cell cell = enemyBoard.getCell(x, y);
                if (!cell.isFiredUpon()) {
                    ShotResult result = enemyBoard.fire(x, y);
                    cellsDestroyed++;
                    hitReport.append("• Cell (").append(x).append(",").append(y).append(") destroyed!\n");
                    
                    if (cell.hasShip()) {
                        shipsHit++;
                    }
                }
            }
        }
        
        if (cellsDestroyed == 0) {
            hitReport.append("• All cells in area already destroyed!");
        } else {
            hitReport.append("💥 Total: ").append(cellsDestroyed).append(" ship segments destroyed, ");
            hitReport.append(shipsHit).append(" ships damaged!");
        }
        
        System.out.println(hitReport.toString());
        lastCellsDestroyed = cellsDestroyed;
        
        tempestLockCooldown = 5; // 5 turns cooldown
        return cellsDestroyed;
    }
    
    // Helper method to destroy a specific number of ship segments
    private int destroyShipSegments(Board board, Ship ship, int segmentsToDestroy) {
        int destroyed = 0;
        int attempts = 0;
        
        while (destroyed < segmentsToDestroy && attempts < 100) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);
            Cell cell = board.getCell(x, y);
            
            // In a real implementation, you'd check if this cell belongs to the target ship
            if (cell.hasShip() && !cell.isFiredUpon()) {
                board.fire(x, y);
                destroyed++;
                System.out.println("   Destroyed segment at (" + x + "," + y + ")");
            }
            attempts++;
        }
        
        return destroyed;
    }
    
    // ===================== TURN MANAGEMENT =====================
    
    public void updateTurnCounter() {
        // Reduce cooldowns
        if (silentDriftCooldown > 0) {
            silentDriftCooldown--;
        }
        if (sonarPulseCooldown > 0) {
            sonarPulseCooldown--;
        }
        if (depthChargeCooldown > 0) {
            depthChargeCooldown--;
        }
        if (tempestLockCooldown > 0) {
            tempestLockCooldown--;
        }
        
        // Regenerate energy (15 per turn - Kael's speed)
        regenerateEnergy(15);
        
        // Update hidden ships duration
        updateHiddenShips();
        
        // Update revealed enemy ships duration
        updateRevealedShips();
    }
    
    private void updateHiddenShips() {
        ArrayList<Ship> toRemove = new ArrayList<>();
        
        for (Ship ship : hiddenShips) {
            int turnsLeft = shipHideTurns.get(ship) - 1;
            if (turnsLeft <= 0) {
                toRemove.add(ship);
                ship.setHidden(false);
                System.out.println("🔓 " + ship.getName() + " is no longer hidden.");
            } else {
                shipHideTurns.put(ship, turnsLeft);
            }
        }
        
        for (Ship ship : toRemove) {
            hiddenShips.remove(ship);
            shipHideTurns.remove(ship);
        }
    }
    
    private void updateRevealedShips() {
        ArrayList<Ship> toRemove = new ArrayList<>();
        
        for (Ship ship : revealedEnemyShips) {
            int turnsLeft = shipRevealTurns.get(ship) - 1;
            if (turnsLeft <= 0) {
                toRemove.add(ship);
                ship.setRevealed(false);
                System.out.println("👁️ " + ship.getName() + " fades back into hiding.");
            } else {
                shipRevealTurns.put(ship, turnsLeft);
            }
        }
        
        for (Ship ship : toRemove) {
            revealedEnemyShips.remove(ship);
            shipRevealTurns.remove(ship);
        }
    }
    
    // ===================== UI HELPER METHODS =====================
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1: // Silent Drift
                if (silentDriftCooldown > 0) {
                    return "Cooldown: " + silentDriftCooldown + " turn" + (silentDriftCooldown > 1 ? "s" : "");
                } else if (!hasEnoughEnergy(80)) {
                    return "Need 80 energy";
                } else {
                    return "Ready!";
                }
            case 2: // Sonar Pulse
                if (sonarPulseCooldown > 0) {
                    return "Cooldown: " + sonarPulseCooldown + " turn" + (sonarPulseCooldown > 1 ? "s" : "");
                } else if (!hasEnoughEnergy(120)) {
                    return "Need 120 energy";
                } else {
                    return "Ready!";
                }
            case 3: // Depth Charge
                if (depthChargeCooldown > 0) {
                    return "Cooldown: " + depthChargeCooldown + " turn" + (depthChargeCooldown > 1 ? "s" : "");
                } else if (!hasEnoughEnergy(200)) {
                    return "Need 200 energy";
                } else {
                    return "Ready!";
                }
            case 4: // Tempest Lock (Ultimate)
                if (tempestLockCooldown > 0) {
                    return "Cooldown: " + tempestLockCooldown + " turn" + (tempestLockCooldown > 1 ? "s" : "");
                } else if (!hasEnoughEnergy(300)) {
                    return "Need 300 energy";
                } else {
                    return "ULTIMATE READY!";
                }
            default:
                return "";
        }
    }
    
    public String getEnergyBar() {
        int percent = (currentEnergy * 100) / MAX_ENERGY;
        int bars = percent / 10;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            if (i < bars) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        bar.append("] " + currentEnergy + "/" + MAX_ENERGY + " energy");
        return bar.toString();
    }
    
    public int getHiddenShipsCount() {
        return hiddenShips.size();
    }
    
    public int getRevealedEnemiesCount() {
        return revealedEnemyShips.size();
    }
    
    public int getLastCellsDestroyed() {
        return lastCellsDestroyed;
    }
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        // Handled by individual skills
        System.out.println("Kael's abilities are used through skill buttons!");
    }
    public boolean wasLastTargetHidden() {
    return lastTargetWasHidden;
}
}