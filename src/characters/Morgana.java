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

public class Morgana extends GameCharacter {
    
    private Random random = new Random();
    private int currentMana;
    private static final int MAX_MANA = 380;
    
    
    private int enchantingMelodyCooldown = 0;
    private int whirlpoolTrapCooldown = 0;
    private int stormCallCooldown = 0;
    
    
    private boolean enemyConfused = false;
    private int confusionTurns = 0;
    
    
    private ArrayList<String> whirlpoolCells = new ArrayList<>();
    private int whirlpoolActiveTurns = 0;
    
    
    private ArrayList<String> floodedCells = new ArrayList<>();
    private int floodedTurns = 0;
    
    
    private boolean firstHitDodged = false;
    
    public Morgana() {
        super(
            "Morgana — The Siren",
            "A mystical siren who commands the sea itself. Her songs confuse enemies and summon storms.",
            2100, 
            100,  
            new Color(64, 224, 208)  
        );
        this.currentMana = MAX_MANA;
        this.abilityName = "Ocean's Command";
        this.abilityDescription = "Uses mana to confuse enemies, trap ships, and summon storms.";
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
            System.out.println("🌊 Morgana spent " + cost + " mana. Remaining: " + currentMana);
        }
    }
    
    public void regenerateMana(int amount) {
        currentMana += amount;
        if (currentMana > MAX_MANA) {
            currentMana = MAX_MANA;
        }
    }
    
    
    
    public boolean tryDodgeHit(int x, int y, ShotResult incomingShot) {
        if (!firstHitDodged && (incomingShot == ShotResult.HIT || incomingShot == ShotResult.SUNK)) {
            firstHitDodged = true;
            System.out.println("🌊 OCEAN'S EMBRACE! Morgana's mist shields her from the first hit!");
            return true; 
        }
        return false; 
    }
    
    
    
    
    public boolean useEnchantingMelody() {
        if (enchantingMelodyCooldown > 0) {
            System.out.println("⏳ Enchanting Melody is on cooldown for " + enchantingMelodyCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(40)) {
            System.out.println("⚠️ Not enough mana! Need 40 mana, have " + currentMana);
            return false;
        }
        
        System.out.println("🎵 MORGANA uses ENCHANTING MELODY: \"My song befuddles even the sharpest minds...\"");
        spendMana(40);
        
        
        enemyConfused = true;
        confusionTurns = 2;
        System.out.println("🎵 Enemy is CONFUSED for 2 turns! They will see fake hit/miss indicators!");
        
        enchantingMelodyCooldown = 2; 
        return true;
    }
    
    public boolean isEnemyConfused() {
        return enemyConfused;
    }
    
    public ShotResult applyConfusion(ShotResult realResult) {
        if (!enemyConfused) {
            return realResult;
        }
        
        
        if (realResult == ShotResult.HIT || realResult == ShotResult.SUNK) {
            
            if (random.nextBoolean()) {
                System.out.println("🎵 Confusion: Enemy thought they missed!");
                return ShotResult.MISS;
            }
        } else if (realResult == ShotResult.MISS) {
            
            if (random.nextBoolean()) {
                System.out.println("🎵 Confusion: Enemy thought they hit!");
                return ShotResult.HIT;
            }
        }
        return realResult;
    }
    
    
    
    








public boolean useWhirlpoolTrap(Board enemyBoard, int centerX, int centerY) {
    if (whirlpoolTrapCooldown > 0) {
        System.out.println("⏳ Whirlpool Trap is on cooldown for " + whirlpoolTrapCooldown + " more turns");
        return false;
    }
    
    if (!hasEnoughMana(80)) {
        System.out.println("⚠️ Not enough mana! Need 80 mana, have " + currentMana);
        return false;
    }
    
    System.out.println("🌊 MORGANA uses WHIRLPOOL TRAP at column " + centerY + "!");
    spendMana(80);
    
    
    whirlpoolCells.clear();
    
    
    int column = centerY;
    int startRow = Math.max(0, centerX - 1);
    int endRow = Math.min(9, centerX + 1);
    
    int trappedShips = 0;
    int totalDamage = 0;
    StringBuilder hitReport = new StringBuilder("🌊 Whirlpool Trap hits column " + column + ":\n");
    
    
    for (int row = startRow; row <= endRow; row++) {
        String cellKey = row + "," + column;
        whirlpoolCells.add(cellKey);
        
        Cell cell = enemyBoard.getCell(row, column);
        
        
        if (!cell.isFiredUpon()) {
            
            ShotResult result = enemyBoard.fire(row, column);
            
            if (cell.hasShip()) {
                int damage = random.nextInt(51) + 50; 
                totalDamage += damage;
                trappedShips++;
                hitReport.append("   • Ship at (").append(row).append(",").append(column)
                         .append(") takes ").append(damage).append(" damage! (").append(result).append(")\n");
            } else {
                hitReport.append("   • Cell (").append(row).append(",").append(column)
                         .append(") churns violently (Miss)\n");
            }
        } else {
            hitReport.append("   • Cell (").append(row).append(",").append(column)
                     .append(") already hit\n");
        }
    }
    
    System.out.println(hitReport.toString());
    System.out.println("💧 Whirlpool trap dealt " + totalDamage + " damage to " + 
                       trappedShips + " ship(s)!");
    
    whirlpoolActiveTurns = 1;
    whirlpoolTrapCooldown = 3;
    
    return true;
}
public String getWhirlpoolAreaString(int x, int y) {
    return "Column " + y + " - hits rows " + Math.max(0, x-1) + " to " + Math.min(9, x+1);
}
    
    
    
    
    


public int useStormCall(Board enemyBoard) {
    if (stormCallCooldown > 0) {
        System.out.println("⏳ Storm Call is on cooldown for " + stormCallCooldown + " more turns");
        return 0;
    }
    
    if (!hasEnoughMana(300)) {
        System.out.println("⚠️ Not enough mana! Need 300 mana, have " + currentMana);
        return 0;
    }
    
    System.out.println("⛈️ MORGANA uses STORM CALL: \"Feel the wrath of the sea!\"");
    spendMana(300);
    
    
    floodedCells.clear();
    
    
    int flooded = 0;
    int attempts = 0;
    int totalDamage = 0;
    int shipsHit = 0;
    StringBuilder hitReport = new StringBuilder("⛈️ Storm Call hits:\n");
    
    while (flooded < 4 && attempts < 100) {
        int x = random.nextInt(10);
        int y = random.nextInt(10);
        String cellKey = x + "," + y;
        
        if (!floodedCells.contains(cellKey)) {
            floodedCells.add(cellKey);
            flooded++;
            
            Cell cell = enemyBoard.getCell(x, y);
            
            
            if (!cell.isFiredUpon()) {
                ShotResult result = enemyBoard.fire(x, y);
                
                if (cell.hasShip()) {
                    int damage = random.nextInt(151) + 200; 
                    totalDamage += damage;
                    shipsHit++;
                    hitReport.append("   • Ship at (").append(x).append(",").append(y)
                             .append(") struck by lightning! ").append(damage).append(" damage! (").append(result).append(")\n");
                } else {
                    hitReport.append("   • Cell (").append(x).append(",").append(y)
                             .append(") flooded by the tempest (Miss)\n");
                }
            } else {
                hitReport.append("   • Cell (").append(x).append(",").append(y)
                         .append(") already hit\n");
            }
        }
        attempts++;
    }
    
    System.out.println(hitReport.toString());
    System.out.println("⛈️ Storm Call flooded " + flooded + " cells!");
    System.out.println("💥 " + shipsHit + " ships hit for " + totalDamage + " total damage!");
    
    floodedTurns = 1; 
    stormCallCooldown = 4; 
    
    return flooded;
}
    
    public boolean isCellFlooded(int x, int y) {
        return floodedCells.contains(x + "," + y) && floodedTurns > 0;
    }
    
    
    
    public void updateTurnCounter() {
        
        if (enchantingMelodyCooldown > 0) {
            enchantingMelodyCooldown--;
        }
        if (whirlpoolTrapCooldown > 0) {
            whirlpoolTrapCooldown--;
        }
        if (stormCallCooldown > 0) {
            stormCallCooldown--;
        }
        
        
        regenerateMana(12);
        
        
        if (enemyConfused) {
            confusionTurns--;
            if (confusionTurns <= 0) {
                enemyConfused = false;
                System.out.println("🎵 Enemy is no longer confused.");
            }
        }
        
        
        if (whirlpoolActiveTurns > 0) {
            whirlpoolActiveTurns--;
            if (whirlpoolActiveTurns <= 0) {
                whirlpoolCells.clear();
                System.out.println("🌊 Whirlpool traps have faded.");
            }
        }
        
        
        if (floodedTurns > 0) {
            floodedTurns--;
            if (floodedTurns <= 0) {
                floodedCells.clear();
                System.out.println("⛈️ The storm has passed.");
            }
        }
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1: 
                if (enchantingMelodyCooldown > 0) {
                    return "Cooldown: " + enchantingMelodyCooldown + " turn" + (enchantingMelodyCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(40)) {
                    return "Need 40 mana";
                } else {
                    return "Ready!";
                }
            case 2: 
                if (whirlpoolTrapCooldown > 0) {
                    return "Cooldown: " + whirlpoolTrapCooldown + " turn" + (whirlpoolTrapCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(80)) {
                    return "Need 80 mana";
                } else {
                    return "Ready!";
                }
            case 3: 
                if (stormCallCooldown > 0) {
                    return "Cooldown: " + stormCallCooldown + " turn" + (stormCallCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(300)) {
                    return "Need 300 mana";
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
    
    public boolean isOceanEmbraceUsed() {
        return firstHitDodged;
    }
    
    public boolean isEnemyConfusedActive() {
        return enemyConfused;
    }
    
    public int getWhirlpoolCount() {
        return whirlpoolCells.size();
    }
    
    public int getFloodedCount() {
        return floodedCells.size();
    }
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        
        System.out.println("Morgana's abilities are used through skill buttons!");
    }
}