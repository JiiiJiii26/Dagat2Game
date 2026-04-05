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

public class Aeris extends GameCharacter {
    
    private Random random = new Random();
    private int currentMana;
    private static final int MAX_MANA = 600;
    
    
    private int adaptiveInstinctCooldown = 0;
    private int multitaskOverdriveCooldown = 0;
    private int relentlessAscentCooldown = 0;
    
    
    private Map<Ship, Integer> shieldedShips = new HashMap<>();
    
    
    private boolean overdriveActive = false;
    private boolean extraActionAvailable = false;
    
    
    private boolean stunImmuneActive = false;
    private int stunImmuneTurns = 0;
    
    
    private boolean nextAttackBonus = false;
    private int consecutiveHits = 0;
    
    
    private Board playerBoardRef;
    
    public Aeris() {
        super(
            "Aeris — The Adaptive Strategist",
            "A master of adaptation who protects his fleet and destroys enemies.",
            2600,
            100,
            new Color(255, 215, 0)
        );
        this.currentMana = MAX_MANA;
        this.abilityName = "Adaptive Strategy";
        this.abilityDescription = "Uses mana to shield ships, restore mana, and destroy columns of enemy cells.";
    }
    
    public void setPlayerBoard(Board board) {
        this.playerBoardRef = board;
    }
    
    
    
    public int getCurrentMana() {
        return currentMana;
    }
    
    public int getMaxMana() {
        return MAX_MANA;
    }
    
    public boolean hasEnoughMana(int cost) {
        return currentMana >= cost;
    }
    
    public void spendMana(int cost) {
        if (hasEnoughMana(cost)) {
            currentMana -= cost;
            System.out.println("💪 Aeris spent " + cost + " mana. Remaining: " + currentMana);
        }
    }
    
    public void regenerateMana(int amount) {
        int oldMana = currentMana;
        currentMana += amount;
        if (currentMana > MAX_MANA) {
            currentMana = MAX_MANA;
        }
        System.out.println("💪 Aeris mana: " + oldMana + " → " + currentMana + " (+" + (currentMana - oldMana) + ")");
    }
    
    
    
    
    public boolean useAdaptiveInstinct(Board playerBoard, int shipIndex) {
        if (adaptiveInstinctCooldown > 0) {
            System.out.println("⏳ Adaptive Instinct is on cooldown for " + adaptiveInstinctCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(120)) {
            System.out.println("⚠️ Not enough mana! Need 120 mana, have " + currentMana);
            return false;
        }
        
        ArrayList<Ship> availableShips = new ArrayList<>();
        for (Ship ship : playerBoard.getShips()) {
            if (!ship.isSunk() && !ship.isShielded()) {
                availableShips.add(ship);
            }
        }
        
        if (availableShips.isEmpty()) {
            System.out.println("⚠️ No available ships to shield!");
            return false;
        }
        
        Ship targetShip;
        if (shipIndex >= 0 && shipIndex < availableShips.size()) {
            targetShip = availableShips.get(shipIndex);
        } else {
            targetShip = availableShips.get(0);
        }
        
        System.out.println("🛡️ AERIS uses ADAPTIVE INSTINCT: \"" + targetShip.getName() + " will endure!\"");
        spendMana(120);
        
        targetShip.setShielded(true, 2);
        shieldedShips.put(targetShip, 2);
        
        System.out.println("🔵 " + targetShip.getName() + " is now SHIELDED for 2 turns!");
        
        adaptiveInstinctCooldown = 2;
        return true;
    }
    
    public boolean isShipImmune(Ship ship) {
        return ship.isShielded();
    }
    
    
    
    
    public boolean useMultitaskOverdrive() {
        if (multitaskOverdriveCooldown > 0) {
            System.out.println("⏳ Multitask Overdrive is on cooldown for " + multitaskOverdriveCooldown + " more turns");
            return false;
        }
        
        System.out.println("⚡ AERIS uses MULTITASK OVERDRIVE: \"Time to recharge!\"");
        
        int oldMana = currentMana;
        currentMana += 200;
        if (currentMana > MAX_MANA) {
            currentMana = MAX_MANA;
        }
        
        System.out.println("⚡ Mana restored: " + oldMana + " → " + currentMana + " (+" + (currentMana - oldMana) + " mana)");
        
        multitaskOverdriveCooldown = 3;
        return true;
    }
    
    
    
    
    public int useRelentlessAscent(Board enemyBoard, int targetY) {
        if (relentlessAscentCooldown > 0) {
            System.out.println("⏳ Relentless Ascent is on cooldown for " + relentlessAscentCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughMana(500)) {
            System.out.println("⚠️ Not enough mana! Need 500 mana, have " + currentMana);
            return 0;
        }
        
        
        int missingHP = maxHealth - currentHealth;
        double missingPercent = (double) missingHP / maxHealth;
        
        
        int bonusCells = (int)(missingPercent * 3); 
        int cellsToDestroy = 10 + bonusCells; 
        cellsToDestroy = Math.min(cellsToDestroy, 10); 
        
        System.out.println("⚔️ AERIS uses RELENTLESS ASCENT: \"My pain is my power!\"");
        System.out.println("📈 Missing HP: " + missingHP + "/" + maxHealth + " (" + (int)(missingPercent * 100) + "%)");
        System.out.println("💥 Bonus cells: +" + bonusCells);
        spendMana(500);
        
        int cellsDestroyed = 0;
        StringBuilder hitReport = new StringBuilder("⚔️ Relentless Ascent destroys column " + targetY + ":\n");
        
        
        for (int row = 0; row < 10; row++) {
            Cell cell = enemyBoard.getCell(row, targetY);
            
            if (!cell.isFiredUpon()) {
                ShotResult result = enemyBoard.fire(row, targetY);
                cellsDestroyed++;
                
                if (cell.hasShip()) {
                    hitReport.append("   • Ship segment at (").append(row).append(",").append(targetY)
                           .append(") destroyed!\n");
                } else {
                    hitReport.append("   • Cell (").append(row).append(",").append(targetY)
                           .append(") destroyed\n");
                }
            } else {
                hitReport.append("   • Cell (").append(row).append(",").append(targetY)
                       .append(") already destroyed\n");
            }
        }
        
        System.out.println(hitReport.toString());
        System.out.println("⚔️ Relentless Ascent destroyed " + cellsDestroyed + " cells!");
        
        
        double healthPercent = (double) currentHealth / maxHealth;
        if (healthPercent < 0.4) {
            stunImmuneActive = true;
            stunImmuneTurns = 2;
            System.out.println("⚡ STUN IMMUNE ACTIVATED! Aeris cannot be stunned for 2 turns!");
        }
        
        relentlessAscentCooldown = 4;
        return cellsDestroyed;
    }
    
    
    
    public void updateTurnCounter() {
        System.out.println("💪 Aeris updateTurnCounter called! Current mana: " + currentMana);
        
        
        if (adaptiveInstinctCooldown > 0) adaptiveInstinctCooldown--;
        if (multitaskOverdriveCooldown > 0) multitaskOverdriveCooldown--;
        if (relentlessAscentCooldown > 0) relentlessAscentCooldown--;
        
        
        if (playerBoardRef != null) {
            for (Ship ship : playerBoardRef.getShips()) {
                ship.decrementShieldTurns();
            }
        }
        
        
        ArrayList<Ship> toRemove = new ArrayList<>();
        for (Map.Entry<Ship, Integer> entry : shieldedShips.entrySet()) {
            int turnsLeft = entry.getValue() - 1;
            if (turnsLeft <= 0) {
                toRemove.add(entry.getKey());
            } else {
                shieldedShips.put(entry.getKey(), turnsLeft);
            }
        }
        
        for (Ship ship : toRemove) {
            shieldedShips.remove(ship);
        }
        
        
        if (stunImmuneActive) {
            stunImmuneTurns--;
            if (stunImmuneTurns <= 0) {
                stunImmuneActive = false;
                System.out.println("⚡ Stun immunity has faded.");
            }
        }
        
        
        if (overdriveActive && !extraActionAvailable) {
            overdriveActive = false;
        }
        
        
        if (consecutiveHits > 0 && !nextAttackBonus) {
            consecutiveHits = 0;
        }
        
        
        regenerateMana(15);
    }
    
    
    
    public void recordHit() {
        consecutiveHits++;
        if (consecutiveHits >= 2) {
            nextAttackBonus = true;
            System.out.println("⚔️ ADAPTIVE INSTINCT TRIGGERED! Next attack destroys an extra cell!");
            consecutiveHits = 0;
        }
    }
    
    public boolean hasBonusAttack() {
        return nextAttackBonus;
    }
    
    public void consumeBonusAttack() {
        nextAttackBonus = false;
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1: 
                if (adaptiveInstinctCooldown > 0) {
                    return "Cooldown: " + adaptiveInstinctCooldown + " turn" + (adaptiveInstinctCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(120)) {
                    return "Need 120 mana";
                } else {
                    return "Ready!";
                }
            case 2: 
                if (multitaskOverdriveCooldown > 0) {
                    return "Cooldown: " + multitaskOverdriveCooldown + " turn" + (multitaskOverdriveCooldown > 1 ? "s" : "");
                } else {
                    return "Ready! (+200 mana)";
                }
            case 3: 
                if (relentlessAscentCooldown > 0) {
                    return "Cooldown: " + relentlessAscentCooldown + " turn" + (relentlessAscentCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(500)) {
                    return "Need 500 mana";
                } else {
                    return "Ready!";
                }
            default:
                return "";
        }
    }
    
    public String getManaBar() {
        int percent = (currentMana * 100) / MAX_MANA;
        int bars = percent / 10;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            if (i < bars) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        bar.append("] " + currentMana + "/" + MAX_MANA + " mana");
        return bar.toString();
    }
    
    public boolean isStunImmuneActive() {
        return stunImmuneActive;
    }
    
    public int getShieldedShipCount() {
        return shieldedShips.size();
    }
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        System.out.println("Aeris's abilities are used through skill buttons!");
    }
}