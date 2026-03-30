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
    private static final int MAX_MANA = 400;
    
    
    private int radarOverloadCooldown = 0;
    private int precisionStrikeCooldown = 0;
    private int fortressModeCooldown = 0;
    
    
    private boolean enemySkillsDisabled = false;
    private int enemySkillsDisabledTurns = 0;
    
    
    private boolean nextShotEnhanced = false;
    
    
    private Ship protectedShip = null;  
    private int protectionRemaining = 0;  
    
    
    private boolean scrapperResolveActive = false;
    private boolean resolveTriggered = false;
    
    
    private Board playerBoardRef;
    
    public Valerius() {
        super(
            "Valerius — The Iron Shoreline",
            "A disgraced engineer turned one-man fortress.",
            2600,
            100,
            new Color(169, 169, 169)
        );
        this.currentMana = MAX_MANA;
        this.abilityName = "Iron Fortification";
        this.abilityDescription = "Uses mana to silence enemies, deliver precision strikes, and protect ships.";
    }
    
    public void setPlayerBoard(Board board) {
        this.playerBoardRef = board;
    }
    
    private Board getPlayerBoard() {
        return playerBoardRef;
    }
    
    
    
    public int getCurrentMana() { return currentMana; }
    public int getMaxMana() { return MAX_MANA; }
    public boolean hasEnoughMana(int cost) { return currentMana >= cost; }
    
    public void spendMana(int cost) {
        if (hasEnoughMana(cost)) {
            currentMana -= cost;
            System.out.println("🛡️ Valerius spent " + cost + " mana. Remaining: " + currentMana);
        }
    }
    
    public void regenerateMana(int amount) {
        currentMana += amount;
        if (currentMana > MAX_MANA) {
            currentMana = MAX_MANA;
        }
        System.out.println("🛡️ Valerius mana: " + currentMana + "/" + MAX_MANA);
    }
    
    
    
    public boolean useRadarOverload() {
        if (radarOverloadCooldown > 0) {
            System.out.println("⏳ Radar Overload is on cooldown for " + radarOverloadCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(50)) {
            System.out.println("⚠️ Not enough mana! Need 50 mana, have " + currentMana);
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
    
    
    
    public boolean usePrecisionStrike() {
        if (precisionStrikeCooldown > 0) {
            System.out.println("⏳ Precision Strike is on cooldown for " + precisionStrikeCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(120)) {
            System.out.println("⚠️ Not enough mana! Need 120 mana, have " + currentMana);
            return false;
        }
        
        System.out.println("🎯 VALERIUS uses PRECISION STRIKE: \"Locked on. Eliminating target.\"");
        spendMana(120);
        
        nextShotEnhanced = true;
        
        System.out.println("🎯 Next attack will destroy 2 cells in a line!");
        
        precisionStrikeCooldown = 2;
        return true;
    }
    
    public boolean isPrecisionStrikeReady() {
        return nextShotEnhanced;
    }
    
    public int applyPrecisionStrike(Board enemyBoard, int x, int y, boolean horizontal) {
        if (!nextShotEnhanced) {
            enemyBoard.fire(x, y);
            return 1;
        }
        
        System.out.println("🎯 PRECISION STRIKE ACTIVE! Destroying 2 cells in a line!");
        nextShotEnhanced = false;
        
        int cellsDestroyed = 0;
        
        enemyBoard.fire(x, y);
        cellsDestroyed++;
        
        if (horizontal) {
            int nextY = y + 1;
            if (nextY < 10) {
                enemyBoard.fire(x, nextY);
                cellsDestroyed++;
            }
        } else {
            int nextX = x + 1;
            if (nextX < 10) {
                enemyBoard.fire(nextX, y);
                cellsDestroyed++;
            }
        }
        
        return cellsDestroyed;
    }
    
    
    
    
  public boolean useFortressMode() {
    if (fortressModeCooldown > 0) {
        System.out.println("⏳ Fortress Mode is on cooldown for " + fortressModeCooldown + " more turns");
        return false;
    }
    
    if (!hasEnoughMana(300)) {
        System.out.println("⚠️ Not enough mana! Need 300 mana, have " + currentMana);
        return false;
    }
    
    Board playerBoard = getPlayerBoard();
    if (playerBoard == null) {
        System.out.println("⚠️ Cannot shield ships - player board reference is null!");
        return false;
    }
    
    System.out.println("🏰 VALERIUS uses FORTRESS MODE: \"I am the Iron Shoreline! I will not fall!\"");
    spendMana(300);
    
    
    int shipsShielded = 0;
    for (Ship ship : playerBoard.getShips()) {
        if (!ship.isSunk()) {
            ship.setShielded(true, 2);  
            shipsShielded++;
            System.out.println("🔵 " + ship.getName() + " is now SHIELDED!");
        }
    }
    
    System.out.println("🏰 FORTRESS MODE ACTIVE for 2 turns!");
    System.out.println("   " + shipsShielded + " ships are now SHIELDED and will block 1 hit each!");
    
    fortressModeCooldown = 5;
    return true;
}
    
    public boolean isShipProtected(Ship ship) {
        return protectedShip != null && protectedShip == ship && protectionRemaining > 0;
    }
    
    public boolean hasProtectedShip() {
        return protectedShip != null && protectionRemaining > 0;
    }
    
    public String getProtectedShipName() {
        return protectedShip != null ? protectedShip.getName() : "None";
    }
    
    public int getProtectionRemaining() {
        return protectionRemaining;
    }
    
    
    public boolean consumeProtection() {
        if (protectionRemaining > 0) {
            protectionRemaining--;
            System.out.println("🛡️ Protection consumed! " + protectionRemaining + " remaining.");
            if (protectionRemaining <= 0) {
                if (protectedShip != null) {
                    protectedShip.setShielded(false, 0);
                    System.out.println("🔵 " + protectedShip.getName() + "'s protection has faded!");
                    protectedShip = null;
                }
            }
            return true;  
        }
        return false;  
    }
    
    
    
    @Override
    public void takeDamage(int damage) {
        int actualDamage = damage;
        if (scrapperResolveActive) {
            actualDamage = (int)(damage * 0.85);
            System.out.println("⚡ Scrapper's Resolve reduced damage from " + damage + " to " + actualDamage);
        }
        
        super.takeDamage(actualDamage);
        
        double healthPercent = (double)currentHealth / maxHealth;
        if (!scrapperResolveActive && healthPercent < 0.2 && !resolveTriggered) {
            activateScrapperResolve();
        }
    }
    
    private void activateScrapperResolve() {
        scrapperResolveActive = true;
        resolveTriggered = true;
        System.out.println("⚡ VALERIUS: \"I've survived worse than you.\"");
        System.out.println("🛡️ Scrapper's Resolve activated! Permanent 15% damage reduction!");
    }
    
    public boolean isScrapperResolveActive() {
        return scrapperResolveActive;
    }
    
    
    
    public void updateTurnCounter() {
        if (radarOverloadCooldown > 0) radarOverloadCooldown--;
        if (precisionStrikeCooldown > 0) precisionStrikeCooldown--;
        if (fortressModeCooldown > 0) fortressModeCooldown--;
        
        if (enemySkillsDisabled) {
            enemySkillsDisabledTurns--;
            if (enemySkillsDisabledTurns <= 0) {
                enemySkillsDisabled = false;
                System.out.println("📡 Enemy skills are no longer disabled.");
            }
        }
        
        
        
        
        regenerateMana(10);
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1:
                if (radarOverloadCooldown > 0) {
                    return "Cooldown: " + radarOverloadCooldown + " turn" + (radarOverloadCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(50)) {
                    return "Need 50 mana";
                } else {
                    return "Ready!";
                }
            case 2:
                if (precisionStrikeCooldown > 0) {
                    return "Cooldown: " + precisionStrikeCooldown + " turn" + (precisionStrikeCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(120)) {
                    return "Need 120 mana";
                } else {
                    return "Ready!";
                }
            case 3:
                if (fortressModeCooldown > 0) {
                    return "Cooldown: " + fortressModeCooldown + " turn" + (fortressModeCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(300)) {
                    return "Need 300 mana";
                } else {
                    if (hasProtectedShip()) {
                        return "Ready! (" + getProtectedShipName() + " protected)";
                    }
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
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        System.out.println("Valerius's abilities are used through skill buttons!");
    }
}