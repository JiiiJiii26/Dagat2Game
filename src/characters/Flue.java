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

public class Flue extends GameCharacter {
    
    private Random random = new Random();
    private int currentMana;
    private static final int MAX_MANA = 350;
    
    
    private int corruptionCooldown = 0;
    private int fortificationCooldown = 0;
    private int kernelDecimationCooldown = 0;
    
    
    private ArrayList<String> infectedCells = new ArrayList<>();
    private Map<String, Integer> infectionSpreadTimer = new HashMap<>();
    private int infectionSpreadCounter = 0;
    
    
    private ArrayList<String> fortifiedCells = new ArrayList<>();
    private int fortificationActiveTurns = 0;
    
    
    private Map<Ship, Debuff> debuffedShips = new HashMap<>();
    
    
    private boolean loneResolveActive = false;
    
    
    private class Debuff {
        int turnsRemaining;
        double damageReduction;
        double healingReduction;
        double accuracyReduction;
        
        Debuff(double damageRed, double healingRed, double accuracyRed) {
            this.turnsRemaining = Integer.MAX_VALUE; 
            this.damageReduction = damageRed;
            this.healingReduction = healingRed;
            this.accuracyReduction = accuracyRed;
        }
    }
    
    public Flue() {
        super(
            "Flue — The System Bastion",
            "A systems architect who spreads viruses to optimize enemy systems.",
            2200,
            100,
            new Color(0, 255, 127)
        );
        this.currentMana = MAX_MANA;
        this.abilityName = "System Optimization";
        this.abilityDescription = "Uses mana to spread viruses, fortify defenses, and decimate targets.";
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
            System.out.println("💻 Flue spent " + cost + " mana. Remaining: " + currentMana);
        }
    }
    
    public void regenerateMana(int amount) {
        currentMana += amount;
        if (currentMana > MAX_MANA) {
            currentMana = MAX_MANA;
        }
        System.out.println("💻 Flue mana: " + currentMana + "/" + MAX_MANA);
    }
    
    
    
    
    public boolean useCorruption(Board enemyBoard, int targetX, int targetY) {
    if (corruptionCooldown > 0) {
        System.out.println("⏳ Corruption.EXE is on cooldown for " + corruptionCooldown + " more turns");
        return false;
    }
    
    if (!hasEnoughMana(100)) {
        System.out.println("⚠️ Not enough mana! Need 100 mana, have " + currentMana);
        return false;
    }
    
    String cellKey = targetX + "," + targetY;
    
    
    if (infectedCells.contains(cellKey)) {
        System.out.println("⚠️ Cell (" + targetX + "," + targetY + ") is already infected!");
        return false;
    }
    
    System.out.println("🦠 FLUE uses CORRUPTION.EXE: \"Initiating viral spread protocol...\"");
    spendMana(100);
    
    
    infectedCells.add(cellKey);
    infectionSpreadTimer.put(cellKey, 4);
    
    
    ShotResult result = enemyBoard.fire(targetX, targetY);
     Cell cell = enemyBoard.getCell(targetX, targetY);
    if (cell.hasShip()) {
        
        Ship infectedShip = cell.getShip();  
        if (infectedShip != null) {
            infectedShip.setInfected(true);
            System.out.println("🦠 " + infectedShip.getName() + " is INFECTED!");
        }
    }
    System.out.println("🦠 Cell (" + targetX + "," + targetY + ") has been INFECTED and MARKED! Result: " + result);
    System.out.println("   The virus will spread to adjacent cells every 4 turns!");
    
    corruptionCooldown = 2;
    return true;
}
    
    
    
public void updateVirusSpread(Board enemyBoard) {
    if (infectedCells.isEmpty()) return;
    
    infectionSpreadCounter++;
    boolean spreadHappened = false;
    ArrayList<String> newInfections = new ArrayList<>();
    ArrayList<String> cellsToUpdate = new ArrayList<>();
    
    for (Map.Entry<String, Integer> entry : infectionSpreadTimer.entrySet()) {
        String cellKey = entry.getKey();
        int turnsLeft = entry.getValue() - 1;
        
        if (turnsLeft <= 0) {
            
            String[] parts = cellKey.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            
            
            int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
            ArrayList<String> possibleTargets = new ArrayList<>();
            
            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];
                String newKey = nx + "," + ny;
                
                if (nx >= 0 && nx < 10 && ny >= 0 && ny < 10) {
                    if (!infectedCells.contains(newKey)) {
                        possibleTargets.add(newKey);
                    }
                }
            }
            
            
            if (!possibleTargets.isEmpty()) {
                String newInfection = possibleTargets.get(random.nextInt(possibleTargets.size()));
                newInfections.add(newInfection);
                cellsToUpdate.add(newInfection);
                System.out.println("🦠 Virus SPREAD from (" + x + "," + y + ") to (" + newInfection + ")!");
                spreadHappened = true;
                
                
                infectionSpreadTimer.put(cellKey, 4);
            } else {
                
                infectionSpreadTimer.put(cellKey, 4);
            }
        } else {
            infectionSpreadTimer.put(cellKey, turnsLeft);
        }
    }
    
    
    for (String newInfection : newInfections) {
        if (!infectedCells.contains(newInfection)) {
            infectedCells.add(newInfection);
            infectionSpreadTimer.put(newInfection, 4);
            
            
            String[] parts = newInfection.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            
            
            ShotResult result = enemyBoard.fire(x, y);
            System.out.println("   🦠 Cell (" + x + "," + y + ") infected and MARKED! Result: " + result);
        }
    }
    
    if (spreadHappened) {
        System.out.println("🦠 Total infected cells: " + infectedCells.size());
    }
}
    
  
    
    public int getInfectedCellsCount() {
        return infectedCells.size();
    }
    
    
    
   public boolean useFortification(Board playerBoard, int targetX, int targetY) {
    if (fortificationCooldown > 0) {
        System.out.println("⏳ Optimized.Fortification.GRID is on cooldown for " + fortificationCooldown + " more turns");
        return false;
    }
    
    if (!hasEnoughMana(200)) {
        System.out.println("⚠️ Not enough mana! Need 200 mana, have " + currentMana);
        return false;
    }
    
    System.out.println("🛡️ FLUE uses OPTIMIZED.FORTIFICATION.GRID: \"Repairing system integrity...\"");
    spendMana(200);
    
    
    Cell cell = playerBoard.getCell(targetX, targetY);
    
    if (!cell.hasShip()) {
        System.out.println("⚠️ No ship at this location!");
        return false;
    }
    
    if (!cell.isFiredUpon()) {
        System.out.println("⚠️ This cell is not damaged! No repair needed.");
        return false;
    }
    
    
    Ship targetShip = null;
    for (Ship ship : playerBoard.getShips()) {
        if (ship.containsCell(targetX, targetY)) {
            targetShip = ship;
            break;
        }
    }
    
    if (targetShip == null) {
        System.out.println("⚠️ Could not find the ship at this location!");
        return false;
    }
    
    
    cell.setFiredUpon(false);      
    cell.setHasShip(true);         
    cell.setShip(targetShip);      
    
    
    targetShip.repair();
    
    System.out.println("🛡️ Cell (" + targetX + "," + targetY + ") on " + targetShip.getName() + " has been REVIVED!");
    System.out.println("   The damaged segment is now operational again!");
    
    fortificationActiveTurns = 1;
    fortificationCooldown = 3;
    return true;
}
    
    
    
   public boolean useKernelDecimation(Board enemyBoard, int targetX, int targetY) {
    if (kernelDecimationCooldown > 0) {
        System.out.println("⏳ Kernel.Decimation.REQ is on cooldown for " + kernelDecimationCooldown + " more turns");
        return false;
    }
    
    if (!hasEnoughMana(300)) {
        System.out.println("⚠️ Not enough mana! Need 300 mana, have " + currentMana);
        return false;
    }
    
    String cellKey = targetX + "," + targetY;
    boolean isTargetInfected = infectedCells.contains(cellKey);
    
    System.out.println("💀 FLUE uses KERNEL.DECIMATION.REQ: \"Executing kernel-level decimation...\"");
    spendMana(300);
    
    int cellsDestroyed = 0;
    StringBuilder hitReport = new StringBuilder("💀 Kernel Decimation:\n");
    
    if (isTargetInfected) {
        
        hitReport.append("   🦠 TARGET IS INFECTED! Executing 3x3 area decimation!\n");
        
        int minX = Math.max(0, targetX - 1);
        int maxX = Math.min(9, targetX + 1);
        int minY = Math.max(0, targetY - 1);
        int maxY = Math.min(9, targetY + 1);
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Cell cell = enemyBoard.getCell(x, y);
                if (!cell.isFiredUpon()) {
                    ShotResult result = enemyBoard.fire(x, y);
                    cellsDestroyed++;
                    hitReport.append("   • (" + x + "," + y + ") destroyed! Result: " + result + "\n");
                } else {
                    hitReport.append("   • (" + x + "," + y + ") already destroyed\n");
                }
            }
        }
        hitReport.append("   🌟 BONUS: 3x3 area decimated!\n");
        
    } else {
        
        hitReport.append("   ⚡ Target not infected. Destroying 3 cells in a row.\n");
        
        int startCol = Math.max(0, targetY - 1);
        int endCol = Math.min(9, targetY + 1);
        
        for (int col = startCol; col <= endCol; col++) {
            Cell cell = enemyBoard.getCell(targetX, col);
            if (!cell.isFiredUpon()) {
                ShotResult result = enemyBoard.fire(targetX, col);
                cellsDestroyed++;
                hitReport.append("   • (" + targetX + "," + col + ") destroyed! Result: " + result + "\n");
            } else {
                hitReport.append("   • (" + targetX + "," + col + ") already destroyed\n");
            }
        }
    }
    
    System.out.println(hitReport.toString());
    System.out.println("💀 Kernel Decimation destroyed " + cellsDestroyed + " cells!");
    
    
    for (int x = 0; x < 10; x++) {
        for (int y = 0; y < 10; y++) {
            Cell cell = enemyBoard.getCell(x, y);
            if (cell.hasShip() && cell.isFiredUpon()) {
                Ship hitShip = getShipAt(enemyBoard, x, y);
                if (hitShip != null && !debuffedShips.containsKey(hitShip)) {
                    Debuff debuff = new Debuff(0.10, 0.10, 0.10);
                    debuffedShips.put(hitShip, debuff);
                    System.out.println("🔻 " + hitShip.getName() + " is PERMANENTLY DEBUFFED!");
                    break;
                }
            }
        }
    }
    
    kernelDecimationCooldown = 5;
    return true;
}
    
    
    
    public void updatePassive() {
        boolean hasActiveShield = !fortifiedCells.isEmpty() && fortificationActiveTurns > 0;
        
        if (!hasActiveShield && !loneResolveActive) {
            loneResolveActive = true;
            System.out.println("🔧 LONE.RESOLVE.CFG ACTIVATED! 15% damage reduction!");
        } else if (hasActiveShield && loneResolveActive) {
            loneResolveActive = false;
            System.out.println("🔧 LONE.RESOLVE.CFG DEACTIVATED");
        }
    }
    
    public boolean isLoneResolveActive() {
        return loneResolveActive;
    }
    
    public double getDamageReduction() {
        return loneResolveActive ? 0.15 : 0.0;
    }
    
    public int applyDamageReduction(int incomingDamage) {
        if (loneResolveActive) {
            int reduced = (int)(incomingDamage * 0.85);
            System.out.println("🛡️ Lone.Resolve reduced damage by " + (incomingDamage - reduced) + "!");
            return reduced;
        }
        return incomingDamage;
    }
    public boolean isCellInfected(int x, int y) {
    return infectedCells.contains(x + "," + y);
}
    
    
    
    public void updateTurnCounter() {
        
        if (corruptionCooldown > 0) corruptionCooldown--;
        if (fortificationCooldown > 0) fortificationCooldown--;
        if (kernelDecimationCooldown > 0) kernelDecimationCooldown--;
        
        
        
        
        
        
        if (fortificationActiveTurns > 0) {
            fortificationActiveTurns--;
            if (fortificationActiveTurns <= 0) {
                fortifiedCells.clear();
                System.out.println("🛡️ Fortification grid has faded.");
            }
        }
        
        
        updatePassive();
        
        
        regenerateMana(12);
    }
    
    
    public void processVirusSpread(Board enemyBoard) {
        updateVirusSpread(enemyBoard);
    }
    
    private Ship getShipAt(Board board, int x, int y) {
        
        return null;
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1: 
                if (corruptionCooldown > 0) {
                    return "Cooldown: " + corruptionCooldown + " turn" + (corruptionCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(100)) {
                    return "Need 100 mana";
                } else {
                    return "Ready!";
                }
            case 2: 
                if (fortificationCooldown > 0) {
                    return "Cooldown: " + fortificationCooldown + " turn" + (fortificationCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(80)) {
                    return "Need 80 mana";
                } else {
                    return "Ready!";
                }
            case 3: 
                if (kernelDecimationCooldown > 0) {
                    return "Cooldown: " + kernelDecimationCooldown + " turn" + (kernelDecimationCooldown > 1 ? "s" : "");
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
    
    public int getFortifiedCellsCount() {
        return fortifiedCells.size();
    }
    
    
    
    public int getDebuffedShipsCount() {
        return debuffedShips.size();
    }
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        System.out.println("Flue's abilities are used through skill buttons!");
    }
}