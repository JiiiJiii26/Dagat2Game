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
    
    
    private int shadowVeilCooldown = 0;
    private int shadowStrikeCooldown = 0;
    private int shadowRealmCooldown = 0;
    
    
    private ArrayList<Ship> hiddenShips = new ArrayList<>();
    private Map<Ship, Integer> shipHideTurns = new HashMap<>();
    
    
    private boolean nextAttackBonus = false;
    private int bonusCells = 0;
    
    
    private boolean shadowRealmActive = false;
    private int shadowRealmTurns = 0;
    private ArrayList<String> revealedEnemyCells = new ArrayList<>();
    
    
    private Board playerBoardRef;
    
    public Kael() {
        super(
            "Kael — Shadow Navigator",
            "A master of stealth who strikes from the shadows.",
            2200,
            100,
            new Color(75, 0, 130)  
        );
        this.currentEnergy = MAX_ENERGY;
        this.abilityName = "Shadow Navigation";
        this.abilityDescription = "Uses energy to hide ships, strike from darkness, and enter the shadow realm.";
    }
    
    public void setPlayerBoard(Board board) {
        this.playerBoardRef = board;
    }
    
    
    
    public int getCurrentEnergy() { return currentEnergy; }
    public int getMaxEnergy() { return MAX_ENERGY; }
    public boolean hasEnoughEnergy(int cost) { return currentEnergy >= cost; }
    
    public void spendEnergy(int cost) {
        if (hasEnoughEnergy(cost)) {
            currentEnergy -= cost;
            System.out.println("🌑 Kael spent " + cost + " energy. Remaining: " + currentEnergy);
        }
    }
    
    public void regenerateEnergy(int amount) {
        currentEnergy += amount;
        if (currentEnergy > MAX_ENERGY) {
            currentEnergy = MAX_ENERGY;
        }
        System.out.println("🌑 Kael energy: " + currentEnergy + "/" + MAX_ENERGY);
    }
    
    
    
    
    public boolean useShadowVeil() {
        if (shadowVeilCooldown > 0) {
            System.out.println("⏳ Shadow Veil is on cooldown for " + shadowVeilCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughEnergy(80)) {
            System.out.println("⚠️ Not enough energy! Need 80 energy, have " + currentEnergy);
            return false;
        }
        
        if (playerBoardRef == null) {
            System.out.println("⚠️ No player board reference!");
            return false;
        }
        
        
        ArrayList<Ship> availableShips = new ArrayList<>();
        for (Ship ship : playerBoardRef.getShips()) {
            if (!ship.isSunk() && !hiddenShips.contains(ship)) {
                availableShips.add(ship);
            }
        }
        
        if (availableShips.isEmpty()) {
            System.out.println("⚠️ No available ships to hide!");
            return false;
        }
        
        Ship targetShip = availableShips.get(random.nextInt(availableShips.size()));
        
        System.out.println("🌑 KAEL uses SHADOW VEIL: \"" + targetShip.getName() + " fades into darkness...\"");
        spendEnergy(80);
        
        hiddenShips.add(targetShip);
        shipHideTurns.put(targetShip, 2);
        targetShip.setHidden(true);
        
        System.out.println("🌑 " + targetShip.getName() + " is now HIDDEN for 2 turns!");
        System.out.println("   Enemy cannot target this ship!");
        
        shadowVeilCooldown = 2;
        return true;
    }
    
    public boolean isShipHidden(Ship ship) {
        return hiddenShips.contains(ship) && shipHideTurns.get(ship) > 0;
    }
    
    
    
    
    public boolean useShadowStrike() {
        if (shadowStrikeCooldown > 0) {
            System.out.println("⏳ Shadow Strike is on cooldown for " + shadowStrikeCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughEnergy(120)) {
            System.out.println("⚠️ Not enough energy! Need 120 energy, have " + currentEnergy);
            return false;
        }
        
        System.out.println("⚔️ KAEL uses SHADOW STRIKE: \"Strike from the darkness!\"");
        spendEnergy(120);
        
        nextAttackBonus = true;
        bonusCells = 2;
        
        System.out.println("⚔️ Next attack will destroy 2 cells!");
        
        shadowStrikeCooldown = 2;
        return true;
    }
    
    public int applyShadowStrike(Board enemyBoard, int x, int y) {
        if (!nextAttackBonus) {
            
            enemyBoard.fire(x, y);
            return 1;
        }
        
        System.out.println("⚔️ SHADOW STRIKE ACTIVE! Destroying 2 cells!");
        nextAttackBonus = false;
        
        int cellsDestroyed = 0;
        
        
        ShotResult result1 = enemyBoard.fire(x, y);
        cellsDestroyed++;
        System.out.println("   • Target cell (" + x + "," + y + "): " + result1);
        
        
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        ArrayList<int[]> validTargets = new ArrayList<>();
        
        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < 10 && ny >= 0 && ny < 10) {
                Cell cell = enemyBoard.getCell(nx, ny);
                if (!cell.isFiredUpon()) {
                    validTargets.add(new int[]{nx, ny});
                }
            }
        }
        
        if (!validTargets.isEmpty()) {
            int[] bonusTarget = validTargets.get(random.nextInt(validTargets.size()));
            ShotResult result2 = enemyBoard.fire(bonusTarget[0], bonusTarget[1]);
            cellsDestroyed++;
            System.out.println("   • Bonus cell (" + bonusTarget[0] + "," + bonusTarget[1] + "): " + result2);
        }
        
        return cellsDestroyed;
    }
    
    
    
    
    public boolean useShadowRealm() {
        if (shadowRealmCooldown > 0) {
            System.out.println("⏳ Shadow Realm is on cooldown for " + shadowRealmCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughEnergy(250)) {
            System.out.println("⚠️ Not enough energy! Need 250 energy, have " + currentEnergy);
            return false;
        }
        
        System.out.println("🌑🌑🌑 KAEL uses SHADOW REALM: \"I am one with the darkness!\"");
        spendEnergy(250);
        
        shadowRealmActive = true;
        shadowRealmTurns = 2;
        
        System.out.println("🌑🌑🌑 Shadow Realm ACTIVE for 2 turns!");
        System.out.println("   Every attack will destroy 2 cells!");
        
        shadowRealmCooldown = 4;
        return true;
    }
    
    public boolean isShadowRealmActive() {
        return shadowRealmActive;
    }
    
    public int applyShadowRealm(Board enemyBoard, int x, int y) {
        if (!shadowRealmActive) {
            
            enemyBoard.fire(x, y);
            return 1;
        }
        
        System.out.println("🌑 SHADOW REALM ACTIVE! Destroying 2 cells!");
        
        int cellsDestroyed = 0;
        
        
        ShotResult result1 = enemyBoard.fire(x, y);
        cellsDestroyed++;
        System.out.println("   • Target cell (" + x + "," + y + "): " + result1);
        
        
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        ArrayList<int[]> validTargets = new ArrayList<>();
        
        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < 10 && ny >= 0 && ny < 10) {
                Cell cell = enemyBoard.getCell(nx, ny);
                if (!cell.isFiredUpon()) {
                    validTargets.add(new int[]{nx, ny});
                }
            }
        }
        
        if (!validTargets.isEmpty()) {
            int[] bonusTarget = validTargets.get(random.nextInt(validTargets.size()));
            ShotResult result2 = enemyBoard.fire(bonusTarget[0], bonusTarget[1]);
            cellsDestroyed++;
            System.out.println("   • Bonus cell (" + bonusTarget[0] + "," + bonusTarget[1] + "): " + result2);
        }
        
        return cellsDestroyed;
    }
    
    
    
    public void updateTurnCounter() {
        
        if (shadowVeilCooldown > 0) shadowVeilCooldown--;
        if (shadowStrikeCooldown > 0) shadowStrikeCooldown--;
        if (shadowRealmCooldown > 0) shadowRealmCooldown--;
        
        
        if (shadowRealmActive) {
            shadowRealmTurns--;
            if (shadowRealmTurns <= 0) {
                shadowRealmActive = false;
                System.out.println("🌑 Shadow Realm has faded.");
            }
        }
        
        
        ArrayList<Ship> toRemove = new ArrayList<>();
        for (Map.Entry<Ship, Integer> entry : shipHideTurns.entrySet()) {
            int turnsLeft = entry.getValue() - 1;
            if (turnsLeft <= 0) {
                toRemove.add(entry.getKey());
                entry.getKey().setHidden(false);
                System.out.println("🔓 " + entry.getKey().getName() + " is no longer hidden.");
            } else {
                shipHideTurns.put(entry.getKey(), turnsLeft);
            }
        }
        
        for (Ship ship : toRemove) {
            hiddenShips.remove(ship);
            shipHideTurns.remove(ship);
        }
        
        
        regenerateEnergy(15);
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1: 
                if (shadowVeilCooldown > 0) {
                    return "Cooldown: " + shadowVeilCooldown + " turn" + (shadowVeilCooldown > 1 ? "s" : "");
                } else if (!hasEnoughEnergy(80)) {
                    return "Need 80 energy";
                } else {
                    return "Ready!";
                }
            case 2: 
                if (shadowStrikeCooldown > 0) {
                    return "Cooldown: " + shadowStrikeCooldown + " turn" + (shadowStrikeCooldown > 1 ? "s" : "");
                } else if (!hasEnoughEnergy(120)) {
                    return "Need 120 energy";
                } else {
                    return "Ready! (2 cells next attack)";
                }
            case 3: 
                if (shadowRealmCooldown > 0) {
                    return "Cooldown: " + shadowRealmCooldown + " turn" + (shadowRealmCooldown > 1 ? "s" : "");
                } else if (!hasEnoughEnergy(250)) {
                    return "Need 250 energy";
                } else {
                    return "Ready!";
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
    
    public int getHiddenShipsCount() { return hiddenShips.size(); }
    public boolean isShadowStrikeReady() { return nextAttackBonus; }
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        System.out.println("Kael's abilities are used through skill buttons!");
    }
}