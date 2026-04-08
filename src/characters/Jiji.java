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

public class Jiji extends GameCharacter {
    
    private Random random = new Random();
    private int currentMana;
    private static final int MAX_MANA = 500;
    
    
    private int dataLeechCooldown = 0;
    private int overclockCooldown = 0;
    private int systemOverloadCooldown = 0;
    
    
    private boolean nextShotEnhanced = false;
    private boolean firewallActive = false;
    private int firewallTurns = 0;
    private int turnsSinceLastFirewall = 0;
    
    private boolean isDamaged = false;
private int turnsSinceDamage = 0;
private static final int DAMAGE_DISPLAY_DURATION = 2;
    
    private ArrayList<String> revealedCells = new ArrayList<>();
    
    
    private boolean overclockActive = false;
    private int overclockBonusTurns = 0;
    private ArrayList<String> overclockTargets = new ArrayList<>();
    
    
    private ArrayList<String> disabledEnemySkills = new ArrayList<>();
    private int skillDisableTurns = 0;
    
    public Jiji() {
        super(
            "Jiji - The Lazy Technomancer",
            "A lazy gamer who hacked the sea itself. Procrastinates even in battle.",
            1950,
            100,
            new Color(100, 200, 255)
        );
        this.currentMana = MAX_MANA;
        this.abilityName = "Technomancer Abilities";
        this.abilityDescription = "Uses mana to hack, overclock, and overload enemies.";
    }
    
    
    
    public int getCurrentMana() { return currentMana; }
    public int getMaxMana() { return MAX_MANA; }
    public boolean hasEnoughMana(int cost) { return currentMana >= cost; }
    
    public void spendMana(int cost) {
        if (hasEnoughMana(cost)) {
            currentMana -= cost;
            System.out.println("💰 Jiji spent " + cost + " mana. Remaining: " + currentMana);
        }
    }
    public boolean isDamaged() { return isDamaged; }
public void setDamaged(boolean damaged) { 
    this.isDamaged = damaged;
    if (!damaged) {
        turnsSinceDamage = 0;
    }
}


public void updateDamageState() {
    if (isDamaged) {
        turnsSinceDamage++;
        System.out.println("Jiji damaged turns: " + turnsSinceDamage + "/" + DAMAGE_DISPLAY_DURATION);
        if (turnsSinceDamage >= DAMAGE_DISPLAY_DURATION) {
            isDamaged = false;
            turnsSinceDamage = 0;
            System.out.println("😺 Jiji recovered!");
        }
    }
}



public void onShipDamaged() {
    isDamaged = true;
    turnsSinceDamage = 0;
}
public void onShipSunk() {
    isDamaged = true;
    turnsSinceDamage = 0;
    System.out.println("💢 JIJI's ship was sunk! Entering damaged state!");
}
    
    public void regenerateMana(int amount) {
        currentMana += amount;
        if (currentMana > MAX_MANA) {
            currentMana = MAX_MANA;
        }
    }
    
    
    
    
   public boolean useDataLeech(Board enemyBoard) {
    if (dataLeechCooldown > 0) {
        System.out.println("⏳ Data Leech is on cooldown for " + dataLeechCooldown + " more turns");
        return false;
    }
    
    if (!hasEnoughMana(50)) {
        System.out.println("⚠️ Not enough mana! Need 50 mana, have " + currentMana);
        return false;
    }
    
    
    int cellsToReveal = overclockActive ? 4 : 2;
    
    String synergyMsg = overclockActive ? " (OVERCLOCK SYNERGY: 4 cells!)" : "";
    System.out.println("🔓 Jiji uses DATA LEECH" + synergyMsg + ": \"Your secrets are mine... wait, that took effort.\"");
    spendMana(50);
    
    
    int revealed = 0;
    int attempts = 0;
    StringBuilder resultMessage = new StringBuilder("📡 Data Leech reveals and marks:\n");
    
    while (revealed < cellsToReveal && attempts < 100) {
        int x = random.nextInt(10);
        int y = random.nextInt(10);
        String cellKey = x + "," + y;
        Cell cell = enemyBoard.getCell(x, y);
        
        
        if (!cell.isFiredUpon()) {
            ShotResult result = enemyBoard.fire(x, y);
            revealedCells.add(cellKey);
            
            
            if (overclockActive) {
                overclockTargets.add(cellKey);
                System.out.println("⚡ Overclock synergy: " + cellKey + " marked for chain reaction!");
            }
            
            String content = cell.hasShip() ? "🚢 SHIP" : "🌊 empty";
            resultMessage.append("   • (").append(x).append(",").append(y)
                        .append("): ").append(content).append(" → MARKED! (").append(result).append(")\n");
            revealed++;
        }
        attempts++;
    }
    
    System.out.println(resultMessage.toString());
    System.out.println("✅ Data Leech complete! " + revealed + " cells revealed and marked.");
    if (overclockActive) {
        System.out.println("⚡ Overclock synergy: " + overclockTargets.size() + " cells marked for chain reaction!");
    }
    
    dataLeechCooldown = 1;
    return true;
}
    
    
    
    
    public boolean useOverclock() {
        if (overclockCooldown > 0) {
            System.out.println("⏳ Overclock is on cooldown for " + overclockCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(100)) {
            System.out.println("⚠️ Not enough mana! Need 100 mana, have " + currentMana);
            return false;
        }
        
        System.out.println("⚡ Jiji uses OVERCLOCK: \"Processing power maximum! Synergy mode activated!\"");
        spendMana(100);
        
        
        overclockActive = true;
        overclockBonusTurns = 2;
        overclockTargets.clear();
        
        System.out.println("⚡ OVERCLOCK ACTIVE for 2 turns!");
        System.out.println("   → Data Leech will mark cells for chain reaction!");
        System.out.println("   → System Overload will trigger on ALL marked cells!");
        
        overclockCooldown = 4; 
        return true;
    }
    
    
    public boolean triggerChainReaction(Board enemyBoard) {
        if (!overclockActive || overclockTargets.isEmpty()) {
            return false;
        }
        
        System.out.println("⚡⚡ OVERCLOCK CHAIN REACTION TRIGGERED!");
        int triggered = 0;
        
        for (String cellKey : overclockTargets) {
            String[] parts = cellKey.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            Cell cell = enemyBoard.getCell(x, y);
            
            if (!cell.isFiredUpon()) {
                ShotResult result = enemyBoard.fire(x, y);
                triggered++;
                System.out.println("   💥 Chain reaction at (" + x + "," + y + "): " + result);
            }
        }
        
        overclockTargets.clear();
        System.out.println("⚡ Chain reaction hit " + triggered + " cells!");
        return triggered > 0;
    }
    
    
    
    
    
   public boolean useSystemOverload(Board enemyBoard) {
    if (systemOverloadCooldown > 0) {
        System.out.println("⏳ System Overload is on cooldown for " + systemOverloadCooldown + " more turns");
        return false;
    }
    
    if (!hasEnoughMana(400)) {
        System.out.println("⚠️ Not enough mana! Need 400 mana, have " + currentMana);
        return false;
    }
    
    
    ArrayList<Ship> availableShips = new ArrayList<>();
    for (Ship ship : enemyBoard.getShips()) {
        if (!ship.isSunk() && !ship.isFullyRevealed()) {
            availableShips.add(ship);
        }
    }
    
    if (availableShips.isEmpty()) {
        System.out.println("⚠️ No available ships to target!");
        return false;
    }
    
    
    Ship targetShip = availableShips.get(random.nextInt(availableShips.size()));
    
    
    if (overclockActive) {
        
        System.out.println("💻 Jiji uses SYSTEM OVERLOAD with OVERCLOCK SYNERGY!");
        spendMana(400);
        
        System.out.println("⚡⚡ SYNERGY ACTIVATED! " + targetShip.getName() + " will be DESTROYED!");
        
        
        int destroyedCells = 0;
        StringBuilder destroyReport = new StringBuilder("💀 " + targetShip.getName() + " DESTROYED:\n");
        
        for (Ship.Coordinate pos : targetShip.getPositions()) {
            int x = pos.getX();
            int y = pos.getY();
            Cell cell = enemyBoard.getCell(x, y);
            
            if (!cell.isFiredUpon()) {
                
                ShotResult result = enemyBoard.fire(x, y);
                destroyedCells++;
                destroyReport.append("   • (" + x + "," + y + ") destroyed! Result: " + result + "\n");
            } else {
                destroyReport.append("   • (" + x + "," + y + ") already destroyed\n");
            }
        }
        
        
        targetShip.setFullyRevealed(true);
        
        System.out.println(destroyReport.toString());
        System.out.println("💀 " + targetShip.getName() + " has been COMPLETELY DESTROYED!");
        System.out.println("   " + destroyedCells + " ship segments obliterated!");
        
        
        triggerChainReaction(enemyBoard);
        
        systemOverloadCooldown = 5;
        return true;
        
    } else {
        
        System.out.println("💻 Jiji uses SYSTEM OVERLOAD: \"Revealing enemy ship... yawn\"");
        spendMana(400);
        
        int revealedCells = 0;
        StringBuilder revealReport = new StringBuilder("💻 System Overload reveals " + targetShip.getName() + ":\n");
        
        for (Ship.Coordinate pos : targetShip.getPositions()) {
            int x = pos.getX();
            int y = pos.getY();
            Cell cell = enemyBoard.getCell(x, y);
            
            if (!cell.isFiredUpon() && !cell.isRevealed()) {
                cell.setRevealed(true);
                revealedCells++;
                revealReport.append("   • (" + x + "," + y + ") revealed!\n");
            }
        }
        
        targetShip.setFullyRevealed(true);
        System.out.println(revealReport.toString());
        System.out.println("💻 " + targetShip.getName() + " has been FULLY REVEALED!");
        System.out.println("   " + revealedCells + " ship segments are now visible!");
        
        systemOverloadCooldown = 5;
        return true;
    }
}
    
    
    
    public void updateTurnCounter() {
        
        if (dataLeechCooldown > 0) dataLeechCooldown--;
        if (overclockCooldown > 0) overclockCooldown--;
        if (systemOverloadCooldown > 0) systemOverloadCooldown--;
        
        
        if (overclockActive) {
            overclockBonusTurns--;
            if (overclockBonusTurns <= 0) {
                overclockActive = false;
                overclockTargets.clear();
                System.out.println("⚡ Overclock has ended.");
            }
        }
        
        
        if (skillDisableTurns > 0) {
            skillDisableTurns--;
            if (skillDisableTurns <= 0) {
                disabledEnemySkills.clear();
                System.out.println("✅ Enemy skills are no longer disabled.");
            }
        }
        
        
        turnsSinceLastFirewall++;
        if (turnsSinceLastFirewall >= 4) {
            activateFirewall();
        }
        
        regenerateMana(20);
    }
    
    private void activateFirewall() {
        firewallActive = true;
        firewallTurns = 1;
        turnsSinceLastFirewall = 0;
        System.out.println("🛡️ FIREWALL ACTIVE! Next hit will be blocked!");
    }
    
    public boolean checkFirewall(int x, int y, ShotResult incomingShot) {
        if (firewallActive && (incomingShot == ShotResult.HIT || incomingShot == ShotResult.SUNK)) {
            firewallActive = false;
            System.out.println("🛡️ Firewall blocked a hit at (" + x + "," + y + ")! \"You can't hack what you can't find...\"");
            return true;
        }
        return false;
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1: 
                if (dataLeechCooldown > 0) {
                    return "Cooldown: " + dataLeechCooldown + " turn" + (dataLeechCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(50)) {
                    return "Need 50 mana";
                } else {
                    return "Ready!";
                }
            case 2: 
                if (overclockCooldown > 0) {
                    return "Cooldown: " + overclockCooldown + " turn" + (overclockCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(100)) {
                    return "Need 100 mana";
                } else {
                    return "Ready! (2 turns synergy mode)";
                }
            case 3: 
                if (systemOverloadCooldown > 0) {
                    return "Cooldown: " + systemOverloadCooldown + " turn" + (systemOverloadCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(400)) {
                    return "Need 400 mana";
                } else {
                    if (overclockActive && !overclockTargets.isEmpty()) {
                        return "SYNERGY READY! (" + overclockTargets.size() + " cells)";
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
    
    public boolean isOverclockActive() {
        return overclockActive;
    }
    
    public int getOverclockTargetCount() {
        return overclockTargets.size();
    }
    
    public boolean isFirewallActive() { return firewallActive; }
    public boolean isNextShotEnhanced() { return false; } 
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        System.out.println("Jiji's abilities are used through skill buttons!");
    }
}