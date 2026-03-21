package characters;

import models.Board;
import models.Cell;
import models.Ship;
import game.ShotResult;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class Selene extends GameCharacter {
    
    private Random random = new Random();
    private int currentMana;
    private static final int MAX_MANA = 500;
    private int extraTurns = 0;  // Tracks extra turns from Eclipse Binding
    
    
    private int lunarVisionCooldown = 0;
    private int eclipseBindingCooldown = 0;
    private int crescentBladeCooldown = 0;
    
    
    private ArrayList<String> revealedCells = new ArrayList<>();
    
    
    private boolean enemyShipsTrapped = false;
    private int trapTurns = 0;
    private ArrayList<String> trappedShipCells = new ArrayList<>();
    
    
    private int turnCounter = 0;
    private boolean nightTime = false;
    
    public Selene() {
        super(
            "Selene — The Moon Oracle",
            "A mystic who reads the stars to predict enemy movements.",
            1850, 
            100,
            new Color(200, 150, 255)  
        );
        this.currentMana = MAX_MANA;
        this.abilityName = "Lunar Prophecy";
        this.abilityDescription = "Uses mana to reveal areas, trap ships, and strike with moonlight.";
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
            System.out.println("🌙 Selene spent " + cost + " mana. Remaining: " + currentMana);
        }
    }
    
    public void regenerateMana(int amount) {
        currentMana += amount;
        if (currentMana > MAX_MANA) {
            currentMana = MAX_MANA;
        }
    }
    public int getExtraTurns() {
    return extraTurns;
}

public void consumeExtraTurn() {
    if (extraTurns > 0) {
        extraTurns--;
        System.out.println("🌑 Extra turn remaining: " + extraTurns);
    }
}

public boolean hasExtraTurn() {
    return extraTurns > 0;
}
    
    
    
  








public boolean useLunarVision(Board enemyBoard, int centerX, int centerY) {
    if (lunarVisionCooldown > 0) {
        System.out.println("⏳ Lunar Vision is on cooldown for " + lunarVisionCooldown + " more turns");
        return false;
    }
    
    if (!hasEnoughMana(60)) {
        System.out.println("⚠️ Not enough mana! Need 60 mana, have " + currentMana);
        return false;
    }
    
    
    int row = centerX;
    int startCol = Math.max(0, centerY - 1);
    int endCol = Math.min(9, centerY + 1);
    
    System.out.println("🔮 SELENE uses LUNAR VISION at row " + row + ", columns " + startCol + "-" + endCol + "! \"The moon reveals all...\"");
    spendMana(60);
    
    int shipsFound = 0;
    int emptyCells = 0;
    StringBuilder visionReport = new StringBuilder("🔮 Lunar Vision reveals:\n");
    
    for (int col = startCol; col <= endCol; col++) {
        Cell cell = enemyBoard.getCell(row, col);
        String cellKey = row + "," + col;
        
        if (!revealedCells.contains(cellKey)) {
            revealedCells.add(cellKey);
            
            if (cell.hasShip() && !cell.isFiredUpon()) {
                
                int damage = random.nextInt(51) + 50; 
                ShotResult result = enemyBoard.fire(row, col);
                shipsFound++;
                visionReport.append("   • (").append(row).append(",").append(col)
                           .append(") SHIP! Takes ").append(damage).append(" damage! (").append(result).append(")\n");
            } else if (cell.hasShip() && cell.isFiredUpon()) {
                visionReport.append("   • (").append(row).append(",").append(col)
                           .append(") SHIP (already hit)\n");
            } else {
                
                if (!cell.isFiredUpon()) {
                    enemyBoard.fire(row, col); 
                    emptyCells++;
                    visionReport.append("   • (").append(row).append(",").append(col)
                               .append(") empty\n");
                } else {
                    visionReport.append("   • (").append(row).append(",").append(col)
                               .append(") already revealed\n");
                }
            }
        } else {
            visionReport.append("   • (").append(row).append(",").append(col)
                       .append(") already revealed\n");
        }
    }
    
    System.out.println(visionReport.toString());
    System.out.println("🔮 Revealed " + shipsFound + " ship segments and " + emptyCells + " empty cells!");
    
    lunarVisionCooldown = 2; 
    return true;
}
    
    
    
    
   public boolean useEclipseBinding() {
    if (eclipseBindingCooldown > 0) {
        System.out.println("⏳ Eclipse Binding is on cooldown for " + eclipseBindingCooldown + " more turns");
        return false;
    }
    
    if (!hasEnoughMana(150)) {
        System.out.println("⚠️ Not enough mana! Need 150 mana, have " + currentMana);
        return false;
    }
    
    System.out.println("🌑 SELENE uses ECLIPSE BINDING! \"The moon grants you extra time...\"");
    spendMana(150);
    
    // Set the number of extra turns
    extraTurns = 2;
    
    System.out.println("🌑 Eclipse Binding gives you 2 EXTRA TURNS!");
    System.out.println("You will get to act " + (extraTurns + 1) + " times this turn!");
    
    eclipseBindingCooldown = 4; // 4 turns cooldown
    return true;
}

    
    public boolean isCellBound(int x, int y) {
        return trappedShipCells.contains(x + "," + y) && enemyShipsTrapped;
    }
    
    
    
    
    public int useCrescentBlade(Board enemyBoard, int centerX, int centerY) {
        if (crescentBladeCooldown > 0) {
            System.out.println("⏳ Crescent Blade is on cooldown for " + crescentBladeCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughMana(400)) {
            System.out.println("⚠️ Not enough mana! Need 400 mana, have " + currentMana);
            return 0;
        }
        
        System.out.println("🌙 SELENE uses CRESCENT BLADE at (" + centerX + "," + centerY + ")! \"Witness the edge of the moon!\"");
        spendMana(400);
        
        int totalDamage = 0;
        int shipsHit = 0;
        StringBuilder hitReport = new StringBuilder("🌙 Crescent Blade hits:\n");
        
        
        int centerDamage = hitCell(enemyBoard, centerX, centerY, hitReport);
        totalDamage += centerDamage;
        if (centerDamage > 0) shipsHit++;
        
        
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        for (int[] dir : directions) {
            int x = centerX + dir[0];
            int y = centerY + dir[1];
            if (x >= 0 && x < 10 && y >= 0 && y < 10) {
                int damage = hitCell(enemyBoard, x, y, hitReport);
                totalDamage += damage;
                if (damage > 0) shipsHit++;
            } else {
                hitReport.append("   • Out of bounds: (").append(x).append(",").append(y).append(")\n");
            }
        }
        
        System.out.println(hitReport.toString());
        System.out.println("🌙 Crescent Blade hit " + shipsHit + " ships for " + totalDamage + " damage!");
        
        crescentBladeCooldown = 4; 
        return totalDamage;
    }
    
    private int hitCell(Board board, int x, int y, StringBuilder report) {
        Cell cell = board.getCell(x, y);
        
        if (!cell.isFiredUpon()) {
            int damage = random.nextInt(151) + 200; 
            ShotResult result = board.fire(x, y);
            
            if (cell.hasShip()) {
                report.append("   • Ship at (").append(x).append(",").append(y)
                       .append(") takes ").append(damage).append(" damage! (").append(result).append(")\n");
                return damage;
            } else {
                report.append("   • Cell (").append(x).append(",").append(y)
                       .append(") sliced by moonlight (Miss)\n");
                return 0;
            }
        } else {
            report.append("   • Cell (").append(x).append(",").append(y).append(") already hit\n");
            return 0;
        }
    }
    
    
    
    public void updateTurnCounter() {
        
        if (lunarVisionCooldown > 0) lunarVisionCooldown--;
        if (eclipseBindingCooldown > 0) eclipseBindingCooldown--;
        if (crescentBladeCooldown > 0) crescentBladeCooldown--;
        
        
        if (enemyShipsTrapped) {
            trapTurns--;
            if (trapTurns <= 0) {
                enemyShipsTrapped = false;
                trappedShipCells.clear();
                System.out.println("🌑 Eclipse Binding has faded.");
            }
        }
        
        
        turnCounter++;
        if (turnCounter >= 3) {
            turnCounter = 0;
            nightTime = true;
            System.out.println("🌙 MOON'S BLESSING! Night falls - attacks have 20% chance to deal double damage!");
        } else {
            nightTime = false;
        }
        
        
        regenerateMana(12);
    }
    
    public boolean isNightTime() {
        return nightTime;
    }
    
    public int applyDoubleDamage(int damage) {
        if (nightTime && random.nextInt(100) < 20) {
            int doubled = damage * 2;
            System.out.println("🌙 MOON'S BLESSING! Damage doubled from " + damage + " to " + doubled + "!");
            return doubled;
        }
        return damage;
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1: 
                if (lunarVisionCooldown > 0) {
                    return "Cooldown: " + lunarVisionCooldown + " turn" + (lunarVisionCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(60)) {
                    return "Need 60 mana";
                } else {
                    return "Ready!";
                }
            case 2: 
                if (eclipseBindingCooldown > 0) {
                    return "Cooldown: " + eclipseBindingCooldown + " turn" + (eclipseBindingCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(150)) {
                    return "Need 150 mana";
                } else {
                    return "Ready!";
                }
            case 3: 
                if (crescentBladeCooldown > 0) {
                    return "Cooldown: " + crescentBladeCooldown + " turn" + (crescentBladeCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(400)) {
                    return "Need 400 mana";
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
    
    public boolean areEnemyShipsTrapped() {
        return enemyShipsTrapped;
    }
    
    public int getTurnsUntilNight() {
        return 3 - turnCounter;
    }
    
    public int getTrappedCellsCount() {
        return trappedShipCells.size();
    }
    
    public boolean isCellRevealed(int x, int y) {
        return revealedCells.contains(x + "," + y);
    }
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        System.out.println("Selene's abilities are used through skill buttons!");
    }
}