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
            "A master of adaptation who protects his fleet under pressure.",
            2600,
            100,
            new Color(255, 215, 0)
        );
        this.currentMana = MAX_MANA;
        this.abilityName = "Adaptive Strategy";
        this.abilityDescription = "Uses mana to shield ships, multitask, and grow stronger under pressure.";
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
    System.out.println("🔵 Ship.isShielded() = " + targetShip.isShielded());  // ← Debug
    
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
        
        if (!hasEnoughMana(180)) {
            System.out.println("⚠️ Not enough mana! Need 180 mana, have " + currentMana);
            return false;
        }
        
        System.out.println("⚡ AERIS uses MULTITASK OVERDRIVE: \"I can do both!\"");
        spendMana(180);
        
        overdriveActive = true;
        extraActionAvailable = true;
        
        System.out.println("⚡ Extra action available! You can do two things this turn!");
        
        multitaskOverdriveCooldown = 3;
        return true;
    }
    
    public boolean hasExtraAction() {
        return extraActionAvailable;
    }
    
    public void consumeExtraAction() {
        extraActionAvailable = false;
        overdriveActive = false;
    }
    
    
    
    public int useRelentlessAscent(Board enemyBoard, int centerX, int centerY) {
        if (relentlessAscentCooldown > 0) {
            System.out.println("⏳ Relentless Ascent is on cooldown for " + relentlessAscentCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughMana(250)) {
            System.out.println("⚠️ Not enough mana! Need 250 mana, have " + currentMana);
            return 0;
        }
        
        System.out.println("⚔️ AERIS uses RELENTLESS ASCENT: \"My pain is my power!\"");
        spendMana(250);
        
        int missingHP = maxHealth - currentHealth;
        double missingPercent = (double) missingHP / maxHealth;
        
        int baseDamage = random.nextInt(301) + 500;
        int bonusDamage = (int)(baseDamage * missingPercent);
        int totalDamage = baseDamage + bonusDamage;
        
        System.out.println("💥 Base damage: " + baseDamage);
        System.out.println("📈 Bonus from missing HP (" + missingHP + "/" + maxHealth + "): +" + bonusDamage);
        System.out.println("💥 Total damage: " + totalDamage);
        
        int totalDealt = 0;
        int shipsHit = 0;
        StringBuilder hitReport = new StringBuilder("⚔️ Relentless Ascent hits:\n");
        
        totalDealt += hitCellWithDamage(enemyBoard, centerX, centerY, totalDamage, hitReport);
        if (enemyBoard.getCell(centerX, centerY).hasShip()) shipsHit++;
        
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        for (int[] dir : directions) {
            int x = centerX + dir[0];
            int y = centerY + dir[1];
            if (x >= 0 && x < 10 && y >= 0 && y < 10) {
                totalDealt += hitCellWithDamage(enemyBoard, x, y, totalDamage, hitReport);
                if (enemyBoard.getCell(x, y).hasShip()) shipsHit++;
            } else {
                hitReport.append("   • Out of bounds: (").append(x).append(",").append(y).append(")\n");
            }
        }
        
        System.out.println(hitReport.toString());
        System.out.println("⚔️ Relentless Ascent hit " + shipsHit + " ships for " + totalDealt + " total damage!");
        
        double healthPercent = (double) currentHealth / maxHealth;
        if (healthPercent < 0.4) {
            stunImmuneActive = true;
            stunImmuneTurns = 2;
            System.out.println("⚡ STUN IMMUNE ACTIVATED! Aeris cannot be stunned for 2 turns!");
        }
        
        relentlessAscentCooldown = 4;
        return totalDealt;
    }
    
    private int hitCellWithDamage(Board board, int x, int y, int damage, StringBuilder report) {
        Cell cell = board.getCell(x, y);
        
        if (!cell.isFiredUpon()) {
            ShotResult result = board.fire(x, y);
            
            if (cell.hasShip()) {
                report.append("   • Ship at (").append(x).append(",").append(y)
                       .append(") takes ").append(damage).append(" damage! (").append(result).append(")\n");
                return damage;
            } else {
                report.append("   • Cell (").append(x).append(",").append(y).append(") is empty\n");
                return 0;
            }
        } else {
            report.append("   • Cell (").append(x).append(",").append(y).append(") already hit\n");
            return 0;
        }
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
                } else if (!hasEnoughMana(180)) {
                    return "Need 180 mana";
                } else {
                    return "Ready!";
                }
            case 3:
                if (relentlessAscentCooldown > 0) {
                    return "Cooldown: " + relentlessAscentCooldown + " turn" + (relentlessAscentCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(250)) {
                    return "Need 250 mana";
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
    
    public boolean isDamageReductionActive() {
        return !shieldedShips.isEmpty();
    }
    
    public boolean isStunImmuneActive() {
        return stunImmuneActive;
    }
    
    public boolean isExtraActionAvailable() {
        return extraActionAvailable;
    }
    
    public int getConsecutiveHits() {
        return consecutiveHits;
    }
    
    public int getShieldedShipCount() {
        return shieldedShips.size();
    }
    
    public void recordHit() {
        consecutiveHits++;
        if (consecutiveHits >= 2) {
            nextAttackBonus = true;
            System.out.println("⚔️ ADAPTIVE INSTINCT TRIGGERED! Next attack gains +150 bonus damage!");
            consecutiveHits = 0;
        }
    }
    
    public int applyBonusDamage(int baseDamage) {
        if (nextAttackBonus) {
            nextAttackBonus = false;
            int bonus = 150;
            System.out.println("⚔️ Bonus damage added: +" + bonus + "!");
            return baseDamage + bonus;
        }
        return baseDamage;
    }
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        System.out.println("Aeris's abilities are used through skill buttons!");
    }
}