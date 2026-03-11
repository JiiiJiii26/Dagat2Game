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
    
    
    private int silentDriftCooldown = 0;
    private int sonarPulseCooldown = 0;
    private int depthChargeCooldown = 0;
    private int tempestLockCooldown = 0;
    
    
    private ArrayList<String> hiddenShips = new ArrayList<>();
    private Map<String, Integer> shipHideTurns = new HashMap<>();
    
    
    private ArrayList<String> revealedEnemyShips = new ArrayList<>();
    
    public Kael() {
        super(
            "Kael - Shadow Navigator",
            "A master of stealth who strikes from the shadows. His fleet is invisible until it's too late.",
            2200, 
            100,  
            new Color(75, 0, 130)  
        );
        this.currentEnergy = MAX_ENERGY;
        this.abilityName = "Shadow Navigation";
        this.abilityDescription = "Uses energy to hide ships, reveal enemies, and strike from the shadows.";
    }
    
    
    
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
        }
    }
    
    public void regenerateEnergy(int amount) {
        currentEnergy += amount;
        if (currentEnergy > MAX_ENERGY) {
            currentEnergy = MAX_ENERGY;
        }
    }
    
    
    
    public boolean useSilentDrift(Board playerBoard) {
        if (silentDriftCooldown > 0) {
            System.out.println("Silent Drift is on cooldown for " + silentDriftCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughEnergy(80)) {
            System.out.println("Not enough energy! Need 80 energy, have " + currentEnergy);
            return false;
        }
        
        System.out.println("🌫️ KAEL uses SILENT DRIFT: \"Hide one boat for 2 turns (cannot be targeted unless revealed).\"");
        spendEnergy(80);
        
        
        for (Ship ship : playerBoard.getShips()) {
            String shipName = ship.getName();
            if (!ship.isSunk() && !hiddenShips.contains(shipName)) {
                
                hiddenShips.add(shipName);
                shipHideTurns.put(shipName, 2); 
                ship.setHidden(true);
                System.out.println("🚢 " + shipName + " is now hidden for 2 turns!");
                
                silentDriftCooldown = 2; 
                return true;
            }
        }
        
        System.out.println("No available ships to hide!");
        return false;
    }
    
    
    
    public boolean useSonarPulse(Board enemyBoard) {
        if (sonarPulseCooldown > 0) {
            System.out.println("Sonar Pulse is on cooldown for " + sonarPulseCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughEnergy(120)) {
            System.out.println("Not enough energy! Need 120 energy, have " + currentEnergy);
            return false;
        }
        
        System.out.println("📡 KAEL uses SONAR PULSE: \"Reveal one hidden enemy boat for 1 turn.\"");
        spendEnergy(120);
        
        
        
        for (Ship ship : enemyBoard.getShips()) {
            if (!ship.isSunk() && ship.isHidden()) {
                String shipName = ship.getName();
                revealedEnemyShips.add(shipName);
                ship.setRevealed(true);
                
                
                int damage = random.nextInt(101) + 150; 
                System.out.println("🎯 Enemy " + shipName + " revealed! Taking " + damage + " damage!");
                
                sonarPulseCooldown = 3; 
                return true;
            }
        }
        
        System.out.println("No hidden enemy ships found!");
        sonarPulseCooldown = 3; 
        return false;
    }
    
    
    
    public int useDepthChargeBarrage(Board enemyBoard, int targetX, int targetY) {
        if (depthChargeCooldown > 0) {
            System.out.println("Depth Charge Barrage is on cooldown for " + depthChargeCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughEnergy(200)) {
            System.out.println("Not enough energy! Need 200 energy, have " + currentEnergy);
            return 0;
        }
        
        System.out.println("💣 KAEL uses DEPTH CHARGE BARRAGE!");
        spendEnergy(200);
        
        
        Cell targetCell = enemyBoard.getCell(targetX, targetY);
        int baseDamage = random.nextInt(201) + 400; 
        int totalDamage = baseDamage;
        
        if (targetCell.hasShip()) {
            Ship targetShip = getShipAt(enemyBoard, targetX, targetY);
            if (targetShip != null && targetShip.isHidden()) {
                
                totalDamage += 200;
                System.out.println("🎯 HIDDEN SHIP BONUS! +200 damage!");
            }
        }
        
        
        System.out.println("💥 Depth Charge deals " + totalDamage + " damage at (" + targetX + "," + targetY + ")!");
        
        depthChargeCooldown = 4; 
        return totalDamage;
    }
    
    
    
    public int useTempestLock(Board enemyBoard, int centerX, int centerY) {
        if (tempestLockCooldown > 0) {
            System.out.println("Tempest Lock is on cooldown for " + tempestLockCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughEnergy(300)) {
            System.out.println("Not enough energy! Need 300 energy, have " + currentEnergy);
            return 0;
        }
        
        System.out.println("🌪️ KAEL uses TEMPEST LOCK - ULTIMATE!");
        spendEnergy(300);
        
        
        int minX = Math.max(0, centerX - 1);
        int maxX = Math.min(9, centerX + 1);
        int minY = Math.max(0, centerY - 1);
        int maxY = Math.min(9, centerY + 1);
        
        int totalDamage = 0;
        int shipsHit = 0;
        
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Cell cell = enemyBoard.getCell(x, y);
                if (cell.hasShip()) {
                    int damage = random.nextInt(201) + 700; 
                    totalDamage += damage;
                    shipsHit++;
                    System.out.println("⚡ Ship at (" + x + "," + y + ") hit for " + damage + "!");
                }
            }
        }
        
        System.out.println("🌪️ Tempest Lock hit " + shipsHit + " ships for total " + totalDamage + " damage!");
        
        tempestLockCooldown = 5; 
        return totalDamage;
    }
    
    
    private Ship getShipAt(Board board, int x, int y) {
        Cell cell = board.getCell(x, y);
        
        
        return null;
    }
    
    
    
    public void updateTurnCounter() {
        
        if (silentDriftCooldown > 0) silentDriftCooldown--;
        if (sonarPulseCooldown > 0) sonarPulseCooldown--;
        if (depthChargeCooldown > 0) depthChargeCooldown--;
        if (tempestLockCooldown > 0) tempestLockCooldown--;
        
        
        regenerateEnergy(15);
        
        
        updateHiddenShips();
        
        
        updateRevealedShips();
    }
    
    private void updateHiddenShips() {
        ArrayList<String> toRemove = new ArrayList<>();
        
        for (String shipName : hiddenShips) {
            int turnsLeft = shipHideTurns.get(shipName) - 1;
            if (turnsLeft <= 0) {
                toRemove.add(shipName);
                System.out.println("🔓 " + shipName + " is no longer hidden!");
            } else {
                shipHideTurns.put(shipName, turnsLeft);
            }
        }
        
        for (String shipName : toRemove) {
            hiddenShips.remove(shipName);
            shipHideTurns.remove(shipName);
        }
    }
    
    private void updateRevealedShips() {
        
        revealedEnemyShips.clear();
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1: 
                if (silentDriftCooldown > 0) {
                    return "Cooldown: " + silentDriftCooldown + " turns";
                } else if (!hasEnoughEnergy(80)) {
                    return "Need 80 energy";
                } else {
                    return "Ready!";
                }
            case 2: 
                if (sonarPulseCooldown > 0) {
                    return "Cooldown: " + sonarPulseCooldown + " turns";
                } else if (!hasEnoughEnergy(120)) {
                    return "Need 120 energy";
                } else {
                    return "Ready!";
                }
            case 3: 
                if (depthChargeCooldown > 0) {
                    return "Cooldown: " + depthChargeCooldown + " turns";
                } else if (!hasEnoughEnergy(200)) {
                    return "Need 200 energy";
                } else {
                    return "Ready!";
                }
            case 4: 
                if (tempestLockCooldown > 0) {
                    return "Cooldown: " + tempestLockCooldown + " turns";
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
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        
        System.out.println("Kael's abilities are used through skill buttons!");
    }
}
