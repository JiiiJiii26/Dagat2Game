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

public class Valerius extends GameCharacter {
    
    private Random random = new Random();
    private int currentMana;
    private static final int MAX_MANA = 320;
    
    
    private int radarOverloadCooldown = 0;
    private int kineticBarrierCooldown = 0;
    private int orbitalRailgunCooldown = 0;
    
    
    private boolean enemySkillsDisabled = false;
    private int enemySkillsDisabledTurns = 0;
    
    
    private ArrayList<String> shieldedCells = new ArrayList<>();
    private int barrierActiveTurns = 0;
    
    
    private boolean scrapperResolveActive = false;
    private double damageReduction = 0.0;
    
    public Valerius() {
        super(
            "Valerius — The Iron Shoreline",
            "A disgraced engineer turned one-man fortress. He refuses to sink.",
            2400, 
            100,  
            new Color(169, 169, 169)  
        );
        this.currentMana = MAX_MANA;
        this.abilityName = "Iron Fortification";
        this.abilityDescription = "Uses mana to disable enemies, shield his fleet, and deliver devastating railgun strikes.";
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
        }
    }
    
    public void regenerateMana(int amount) {
        currentMana += amount;
        if (currentMana > MAX_MANA) {
            currentMana = MAX_MANA;
        }
    }
    
    
    
    @Override
    public void takeDamage(int damage) {
        
        int actualDamage = damage;
        if (scrapperResolveActive) {
            actualDamage = (int)(damage * 0.9); 
            System.out.println("🛡️ Scrapper's Resolve reduced damage from " + damage + " to " + actualDamage);
        }
        
        super.takeDamage(actualDamage);
        
        
        double healthPercent = (double)currentHealth / maxHealth;
        if (!scrapperResolveActive && healthPercent < 0.2) {
            activateScrapperResolve();
        }
    }
    
    private void activateScrapperResolve() {
        scrapperResolveActive = true;
        damageReduction = 0.1;
        System.out.println("⚡ VALERIUS: \"I've survived worse than you.\"");
        System.out.println("🛡️ Scrapper's Resolve activated! Permanent 10% damage reduction!");
    }
    
    
    
    public boolean useRadarOverload() {
        if (radarOverloadCooldown > 0) {
            System.out.println("Radar Overload is on cooldown for " + radarOverloadCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(50)) {
            System.out.println("Not enough mana! Need 50 mana, have " + currentMana);
            return false;
        }
        
        System.out.println("📡 VALERIUS uses RADAR OVERLOAD: \"Try finding your way through the noise.\"");
        spendMana(50);
        
        
        enemySkillsDisabled = true;
        enemySkillsDisabledTurns = 2;
        
        System.out.println("🛑 Enemy skills are DISABLED for 2 turns!");
        
        radarOverloadCooldown = 3; 
        return true;
    }
    
    public boolean areEnemySkillsDisabled() {
        return enemySkillsDisabled;
    }
    
    
    
    public boolean useKineticBarrier(Board playerBoard, int centerX, int centerY) {
        if (kineticBarrierCooldown > 0) {
            System.out.println("Kinetic Barrier is on cooldown for " + kineticBarrierCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(90)) {
            System.out.println("Not enough mana! Need 90 mana, have " + currentMana);
            return false;
        }
        
        System.out.println("🛡️ VALERIUS uses KINETIC BARRIER: \"Nothing gets past the Iron Shoreline.\"");
        spendMana(90);
        
        
        int minX = Math.max(0, centerX - 1);
        int maxX = Math.min(9, centerX + 1);
        int minY = Math.max(0, centerY - 1);
        int maxY = Math.min(9, centerY + 1);
        
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                String cellKey = x + "," + y;
                shieldedCells.add(cellKey);
                System.out.println("🛡️ Cell (" + x + "," + y + ") is shielded!");
            }
        }
        
        barrierActiveTurns = 1; 
        kineticBarrierCooldown = 4; 
        return true;
    }
    
    public boolean isCellShielded(int x, int y) {
        String cellKey = x + "," + y;
        return shieldedCells.contains(cellKey) && barrierActiveTurns > 0;
    }
    
    public int applyBarrier(int x, int y, int incomingDamage) {
        if (isCellShielded(x, y)) {
            System.out.println("🛡️ Kinetic Barrier at (" + x + "," + y + ") blocked all damage!");
            return 0; 
        }
        return incomingDamage;
    }
    
    
    
    public ShotResult useOrbitalRailgun(Board enemyBoard, int targetX, int targetY) {
        if (orbitalRailgunCooldown > 0) {
            System.out.println("Orbital Railgun is on cooldown for " + orbitalRailgunCooldown + " more turns");
            return ShotResult.INVALID;
        }
        
        if (!hasEnoughMana(280)) {
            System.out.println("Not enough mana! Need 280 mana, have " + currentMana);
            return ShotResult.INVALID;
        }
        
        System.out.println("🎯 VALERIUS uses ORBITAL RAILGUN: \"Locked on. Eliminating target.\"");
        spendMana(280);
        
        
        ShotResult result = enemyBoard.fire(targetX, targetY);
        
        
        int damage = random.nextInt(201) + 400; 
        System.out.println("💥 Railgun deals " + damage + " damage at (" + targetX + "," + targetY + ")!");
        
        
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        
        for (int[] dir : directions) {
            int nx = targetX + dir[0];
            int ny = targetY + dir[1];
            
            if (nx >= 0 && nx < 10 && ny >= 0 && ny < 10) {
                Cell adjacentCell = enemyBoard.getCell(nx, ny);
                if (adjacentCell.hasShip()) {
                    System.out.println("🔍 Adjacent cell (" + nx + "," + ny + ") contains a SHIP!");
                } else {
                    System.out.println("🔍 Adjacent cell (" + nx + "," + ny + ") is empty.");
                }
            }
        }
        
        orbitalRailgunCooldown = 5; 
        return result;
    }
    
    
    
    public void updateTurnCounter() {
        
        if (radarOverloadCooldown > 0) radarOverloadCooldown--;
        if (kineticBarrierCooldown > 0) kineticBarrierCooldown--;
        if (orbitalRailgunCooldown > 0) orbitalRailgunCooldown--;
        
        
        regenerateMana(10);
        
        
        if (enemySkillsDisabled) {
            enemySkillsDisabledTurns--;
            if (enemySkillsDisabledTurns <= 0) {
                enemySkillsDisabled = false;
                System.out.println("📡 Enemy skills are no longer disabled.");
            }
        }
        
        
        if (barrierActiveTurns > 0) {
            barrierActiveTurns--;
            if (barrierActiveTurns <= 0) {
                shieldedCells.clear();
                System.out.println("🛡️ Kinetic Barrier has faded.");
            }
        }
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1: 
                if (radarOverloadCooldown > 0) {
                    return "Cooldown: " + radarOverloadCooldown + " turns";
                } else if (!hasEnoughMana(50)) {
                    return "Need 50 mana";
                } else {
                    return "Ready!";
                }
            case 2: 
                if (kineticBarrierCooldown > 0) {
                    return "Cooldown: " + kineticBarrierCooldown + " turns";
                } else if (!hasEnoughMana(90)) {
                    return "Need 90 mana";
                } else {
                    return "Ready!";
                }
            case 3: 
                if (orbitalRailgunCooldown > 0) {
                    return "Cooldown: " + orbitalRailgunCooldown + " turns";
                } else if (!hasEnoughMana(280)) {
                    return "Need 280 mana";
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
    
    public boolean isScrapperResolveActive() {
        return scrapperResolveActive;
    }
    
    public double getDamageReduction() {
        return damageReduction;
    }
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        
        System.out.println("Valerius's abilities are used through skill buttons!");
    }
}
