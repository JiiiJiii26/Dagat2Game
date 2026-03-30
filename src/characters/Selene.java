package characters;

import models.Board;
import models.Cell;
import models.Ship;
import game.ShotResult;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class Selene extends GameCharacter {
    private int moonBlessingStacks = 0;
    private Random random = new Random();
    private int currentMana;
    private static final int MAX_MANA = 500;
    private int extraTurns = 0;  
    
    
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
    
    
    int startX = Math.max(0, centerX - 1);
    int endX = Math.min(9, centerX + 1);
    int startY = Math.max(0, centerY - 1);
    int endY = Math.min(9, centerY + 1);
    
    System.out.println("🔮 SELENE uses LUNAR VISION at (" + centerX + "," + centerY + ")! \"The moon reveals all...\"");
    spendMana(60);
    
    int shipsFound = 0;
    int cellsRevealed = 0;
    StringBuilder visionReport = new StringBuilder("🔮 Lunar Vision reveals 3x3 area:\n");
    
    for (int x = startX; x <= endX; x++) {
        for (int y = startY; y <= endY; y++) {
            Cell cell = enemyBoard.getCell(x, y);
            String cellKey = x + "," + y;
            
            if (!revealedCells.contains(cellKey)) {
                revealedCells.add(cellKey);
                cellsRevealed++;
                
                if (cell.hasShip() && !cell.isFiredUpon()) {
                    shipsFound++;
                    visionReport.append("   • 🚢 SHIP at (").append(x).append(",").append(y).append(")!\n");
                    
                    enemyBoard.fire(x, y);
                } else if (cell.hasShip()) {
                    visionReport.append("   • 🚢 SHIP at (").append(x).append(",").append(y).append(") (already damaged)\n");
                } else {
                    visionReport.append("   • 💧 Empty at (").append(x).append(",").append(y).append(")\n");
                }
            }
        }
    }
    
    System.out.println(visionReport.toString());
    System.out.println("🔮 Revealed " + cellsRevealed + " cells, found " + shipsFound + " ships!");
    
    lunarVisionCooldown = 3;
    return true;
}
    
    
    
  public boolean useEclipseBinding(Board enemyBoard) {
    if (eclipseBindingCooldown > 0) {
        System.out.println("⏳ Eclipse Binding is on cooldown for " + eclipseBindingCooldown + " more turns");
        return false;
    }
    
    if (!hasEnoughMana(150)) {
        System.out.println("⚠️ Not enough mana! Need 150 mana, have " + currentMana);
        return false;
    }
    
    System.out.println("🌑 SELENE uses ECLIPSE BINDING! \"The moon binds your fleet!\"");
    spendMana(150);
    
    
    extraTurns = 2;
    
    
    enemyShipsTrapped = true;
    trapTurns = 1;
    
    
    trappedShipCells.clear();
    for (Ship ship : enemyBoard.getShips()) {
        if (!ship.isSunk()) {
            for (Ship.Coordinate pos : ship.getPositions()) {
                trappedShipCells.add(pos.getX() + "," + pos.getY());
            }
        }
    }
    
    System.out.println("🌑 Eclipse Binding gives you 2 EXTRA TURNS and TRAPS enemy ships for 1 turn!");
    System.out.println("Trapped ships will deal 50% less damage next turn!");
    
    eclipseBindingCooldown = 5;
    return true;
}


public int applyTrapDamageReduction(int incomingDamage) {
    if (enemyShipsTrapped) {
        int reduced = incomingDamage / 2;
        System.out.println("🌑 Enemy ships are trapped! Damage reduced from " + incomingDamage + " to " + reduced);
        return reduced;
    }
    return incomingDamage;
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
    int cellsDestroyed = 0;
    StringBuilder hitReport = new StringBuilder("🌙 Crescent Blade strikes:\n");
    
    
    for (int x = Math.max(0, centerX - 1); x <= Math.min(9, centerX + 1); x++) {
        for (int y = Math.max(0, centerY - 1); y <= Math.min(9, centerY + 1); y++) {
            int damage = hitCellWithBonus(enemyBoard, x, y, hitReport);
            totalDamage += damage;
            if (damage > 0) {
                shipsHit++;
                cellsDestroyed++;
            }
        }
    }
    
    System.out.println(hitReport.toString());
    System.out.println("🌙 Crescent Blade hit " + shipsHit + " ship segments for " + totalDamage + " damage!");
    
    
    if (nightTime) {
        System.out.println("🌙 MOON'S WRATH! Crescent Blade leaves burning moonlight!");
        
    }
    
    crescentBladeCooldown = 5;
    return totalDamage;
}

private int hitCellWithBonus(Board board, int x, int y, StringBuilder report) {
    Cell cell = board.getCell(x, y);
    
    if (!cell.isFiredUpon()) {
        int damage = random.nextInt(151) + 200;
        
        
        if (nightTime) {
            damage = (int)(damage * 1.3);
        }
        
        ShotResult result = board.fire(x, y);
        
        if (cell.hasShip()) {
            report.append("   • 🚢 Ship at (").append(x).append(",").append(y)
                   .append(") takes ").append(damage).append(" damage! ").append(result).append("\n");
            return damage;
        } else {
            report.append("   • 💧 Cell (").append(x).append(",").append(y)
                   .append(") sliced by moonlight\n");
            return 0;
        }
    } else {
        report.append("   • ⚠️ Cell (").append(x).append(",").append(y).append(") already hit\n");
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
        
        
        int regenAmount = 12 + (moonBlessingStacks * 5);
    regenerateMana(regenAmount);
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
    
    
    
  
public String getMoonPhase() {
    if (nightTime) {
        return "🌙 FULL MOON (Level " + moonBlessingStacks + ")";
    }
    return "🌑 NEW MOON (" + getTurnsUntilNight() + " turns until night)";
}


public String getPassiveText() {
    if (nightTime) {
        return "🌙 Moon's Blessing ACTIVE - +" + (moonBlessingStacks * 5) + "% damage, +" + (moonBlessingStacks * 5) + " mana/turn";
    }
    return "🌑 Moon's Blessing - Night falls every 3 turns";
}


public String getTrapStatus() {
    if (enemyShipsTrapped) {
        return "🌑 Enemy ships TRAPPED (" + trapTurns + " turn remaining)";
    }
    return "";
}


public String getSkillStatus(int skillNum) {
    switch(skillNum) {
        case 1: 
            if (lunarVisionCooldown > 0) {
                return "Cooldown: " + lunarVisionCooldown;
            } else if (!hasEnoughMana(60)) {
                return "Need 60 mana";
            } else {
                return "Ready! (Reveals 3x3 area)";
            }
        case 2: 
            if (eclipseBindingCooldown > 0) {
                return "Cooldown: " + eclipseBindingCooldown;
            } else if (!hasEnoughMana(150)) {
                return "Need 150 mana";
            } else {
                return "Ready! (+2 turns + trap)";
            }
        case 3: 
            if (crescentBladeCooldown > 0) {
                return "Cooldown: " + crescentBladeCooldown;
            } else if (!hasEnoughMana(400)) {
                return "Need 400 mana";
            } else {
                return nightTime ? "Ready! (🌙 Empowered by moonlight!)" : "Ready!";
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