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
    
    
    private int shadowStepCooldown = 0;
    private int shadowBladeCooldown = 0;
    private int shadowDomainCooldown = 0;
    
    
    private ArrayList<String> shadowCells = new ArrayList<>();
    private int domainTurns = 0;
    
    
    private Board playerBoardRef;
    
    public Kael() {
        super(
            "Kael — Shadow Navigator",
            "A master of stealth who manipulates shadows to control the battlefield.",
            2200,
            100,
            new Color(75, 0, 130)
        );
        this.currentEnergy = MAX_ENERGY;
        this.abilityName = "Shadow Navigation";
        this.abilityDescription = "Uses energy to teleport ships, cut through defenses, and create shadow explosions.";
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
    
    
  public boolean useShadowStep(Board playerBoard, int fromX, int fromY, int toX, int toY) {
    if (shadowStepCooldown > 0) {
        System.out.println("⏳ Shadow Step is on cooldown for " + shadowStepCooldown + " more turns");
        return false;
    }
    
    if (!hasEnoughEnergy(100)) {
        System.out.println("⚠️ Not enough energy! Need 100 energy, have " + currentEnergy);
        return false;
    }
    
    
    Cell sourceCell = playerBoard.getCell(fromX, fromY);
    if (!sourceCell.hasShip()) {
        System.out.println("⚠️ No ship at source location!");
        return false;
    }
    
    
    Cell destCell = playerBoard.getCell(toX, toY);
    if (destCell.hasShip() || destCell.isFiredUpon()) {
        System.out.println("⚠️ Destination is occupied or already damaged!");
        return false;
    }
    
    
    Ship targetShip = null;
    for (Ship ship : playerBoard.getShips()) {
        if (ship.containsCell(fromX, fromY)) {
            targetShip = ship;
            break;
        }
    }
    
    if (targetShip == null) {
        System.out.println("⚠️ Could not find the ship!");
        return false;
    }
    
    System.out.println("🌑 KAEL uses SHADOW STEP: \"" + targetShip.getName() + " shifts through the shadows!\"");
    spendEnergy(100);
    
    
    ArrayList<int[]> damagedCells = new ArrayList<>();
    ArrayList<int[]> undamagedCells = new ArrayList<>();
    
    for (Ship.Coordinate pos : targetShip.getPositions()) {
        int x = pos.getX();
        int y = pos.getY();
        Cell cell = playerBoard.getCell(x, y);
        
        if (cell.isFiredUpon()) {
            
            damagedCells.add(new int[]{x, y});
            System.out.println("💀 Damaged cell at (" + x + "," + y + ") will remain as wreckage.");
        } else {
            
            undamagedCells.add(new int[]{x, y});
        }
    }
    
    
    for (Ship.Coordinate pos : targetShip.getPositions()) {
        int x = pos.getX();
        int y = pos.getY();
        Cell cell = playerBoard.getCell(x, y);
        cell.setHasShip(false);
        cell.setShip(null);
    }
    
    
    targetShip.getPositions().clear();
    
    
    for (int[] damaged : damagedCells) {
        int x = damaged[0];
        int y = damaged[1];
        Cell cell = playerBoard.getCell(x, y);
        
        
        cell.setHasShip(false);
        cell.setShip(null);
        
        System.out.println("💀 Wreckage left at (" + x + "," + y + ")");
    }
    
    
    if (undamagedCells.isEmpty()) {
        System.out.println("⚠️ No healthy cells to teleport! Ship is completely destroyed!");
        return false;
    }
    
    
    boolean horizontal = (toY + undamagedCells.size() <= 10);
    boolean vertical = (toX + undamagedCells.size() <= 10);
    
    if (horizontal) {
        for (int i = 0; i < undamagedCells.size(); i++) {
            int newX = toX;
            int newY = toY + i;
            targetShip.addPosition(newX, newY);
            Cell newCell = playerBoard.getCell(newX, newY);
            newCell.setHasShip(true);
            newCell.setShip(targetShip);
        }
        System.out.println("🌑 Teleported " + undamagedCells.size() + " healthy cells to (" + toX + "," + toY + ")");
    } else if (vertical) {
        for (int i = 0; i < undamagedCells.size(); i++) {
            int newX = toX + i;
            int newY = toY;
            targetShip.addPosition(newX, newY);
            Cell newCell = playerBoard.getCell(newX, newY);
            newCell.setHasShip(true);
            newCell.setShip(targetShip);
        }
        System.out.println("🌑 Teleported " + undamagedCells.size() + " healthy cells vertically to (" + toX + "," + toY + ")");
    } else {
        System.out.println("⚠️ Not enough space to place ship!");
        
        for (int[] cell : undamagedCells) {
            int x = cell[0];
            int y = cell[1];
            targetShip.addPosition(x, y);
            Cell restoreCell = playerBoard.getCell(x, y);
            restoreCell.setHasShip(true);
            restoreCell.setShip(targetShip);
        }
        for (int[] cell : damagedCells) {
            int x = cell[0];
            int y = cell[1];
            targetShip.addPosition(x, y);
            Cell restoreCell = playerBoard.getCell(x, y);
            restoreCell.setHasShip(true);
            restoreCell.setShip(targetShip);
        }
        return false;
    }
    
    System.out.println("🌑 " + targetShip.getName() + " teleported with " + undamagedCells.size() + " cells!");
    System.out.println("💀 " + damagedCells.size() + " damaged cells left behind as wreckage!");
    
    shadowStepCooldown = 3;
    return true;
}
    
    
    
    public int useShadowBlade(Board enemyBoard, int targetX, int targetY, boolean horizontal) {
        if (shadowBladeCooldown > 0) {
            System.out.println("⏳ Shadow Blade is on cooldown for " + shadowBladeCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughEnergy(150)) {
            System.out.println("⚠️ Not enough energy! Need 150 energy, have " + currentEnergy);
            return 0;
        }
        
        System.out.println("⚔️ KAEL uses SHADOW BLADE: \"" + (horizontal ? "Horizontal" : "Vertical") + " cut through the darkness!\"");
        spendEnergy(150);
        
        int cellsDestroyed = 0;
        StringBuilder hitReport = new StringBuilder("⚔️ Shadow Blade cuts:\n");
        
        if (horizontal) {
            
            for (int col = 0; col < 10; col++) {
                if (col % 2 == targetY % 2) {
                    Cell cell = enemyBoard.getCell(targetX, col);
                    if (!cell.isFiredUpon()) {
                        ShotResult result = enemyBoard.fire(targetX, col);
                        cellsDestroyed++;
                        hitReport.append("   • (" + targetX + "," + col + ") destroyed! " + result + "\n");
                    } else {
                        hitReport.append("   • (" + targetX + "," + col + ") already hit\n");
                    }
                }
            }
        } else {
            
            for (int row = 0; row < 10; row++) {
                if (row % 2 == targetX % 2) {
                    Cell cell = enemyBoard.getCell(row, targetY);
                    if (!cell.isFiredUpon()) {
                        ShotResult result = enemyBoard.fire(row, targetY);
                        cellsDestroyed++;
                        hitReport.append("   • (" + row + "," + targetY + ") destroyed! " + result + "\n");
                    } else {
                        hitReport.append("   • (" + row + "," + targetY + ") already hit\n");
                    }
                }
            }
        }
        
        System.out.println(hitReport.toString());
        System.out.println("⚔️ Shadow Blade destroyed " + cellsDestroyed + " cells!");
        
        shadowBladeCooldown = 3;
        return cellsDestroyed;
    }
    
    
    
    public int useShadowDomain(Board enemyBoard, int centerX, int centerY) {
        if (shadowDomainCooldown > 0) {
            System.out.println("⏳ Shadow Domain is on cooldown for " + shadowDomainCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughEnergy(200)) {
            System.out.println("⚠️ Not enough energy! Need 200 energy, have " + currentEnergy);
            return 0;
        }
        
        System.out.println("🌑🌑🌑 KAEL uses SHADOW DOMAIN: \"The shadows consume all!\"");
        spendEnergy(200);
        
        
        int minX = Math.max(0, centerX - 1);
        int maxX = Math.min(9, centerX + 1);
        int minY = Math.max(0, centerY - 1);
        int maxY = Math.min(9, centerY + 1);
        
        int cellsDestroyed = 0;
        int shipsHit = 0;
        StringBuilder domainReport = new StringBuilder("🌑 Shadow Domain consumes:\n");
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Cell cell = enemyBoard.getCell(x, y);
                
                if (!cell.isFiredUpon()) {
                    ShotResult result = enemyBoard.fire(x, y);
                    cellsDestroyed++;
                    
                    if (cell.hasShip()) {
                        shipsHit++;
                        domainReport.append("   • Ship at (" + x + "," + y + ") destroyed! " + result + "\n");
                    } else {
                        domainReport.append("   • Cell (" + x + "," + y + ") consumed\n");
                    }
                } else {
                    domainReport.append("   • Cell (" + x + "," + y + ") already destroyed\n");
                }
            }
        }
        
        System.out.println(domainReport.toString());
        System.out.println("🌑🌑🌑 Shadow Domain destroyed " + cellsDestroyed + " cells!");
        System.out.println("   Hit " + shipsHit + " enemy ships!");
        
        shadowDomainCooldown = 4;
        return cellsDestroyed;
    }
    
    
    
    public void updateTurnCounter() {
        if (shadowStepCooldown > 0) shadowStepCooldown--;
        if (shadowBladeCooldown > 0) shadowBladeCooldown--;
        if (shadowDomainCooldown > 0) shadowDomainCooldown--;
        
        regenerateEnergy(12);
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1:
                if (shadowStepCooldown > 0) {
                    return "Cooldown: " + shadowStepCooldown + " turn" + (shadowStepCooldown > 1 ? "s" : "");
                } else if (!hasEnoughEnergy(100)) {
                    return "Need 100 energy";
                } else {
                    return "Ready!";
                }
            case 2:
                if (shadowBladeCooldown > 0) {
                    return "Cooldown: " + shadowBladeCooldown + " turn" + (shadowBladeCooldown > 1 ? "s" : "");
                } else if (!hasEnoughEnergy(150)) {
                    return "Need 150 energy";
                } else {
                    return "Ready!";
                }
            case 3:
                if (shadowDomainCooldown > 0) {
                    return "Cooldown: " + shadowDomainCooldown + " turn" + (shadowDomainCooldown > 1 ? "s" : "");
                } else if (!hasEnoughEnergy(200)) {
                    return "Need 200 energy";
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
    
    public boolean isDomainActive() {
        return domainTurns > 0;
    }
    
    public int getDomainCellsCount() {
        return shadowCells.size();
    }
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        System.out.println("Kael's abilities are used through skill buttons!");
    }
}