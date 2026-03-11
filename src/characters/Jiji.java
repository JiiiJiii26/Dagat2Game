package characters;

import models.Board;
import models.Cell;
import game.ShotResult;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class Jiji extends GameCharacter {
    
    private Random random = new Random();
    private int turnsSinceLastFirewall = 0;
    private boolean decoyActive = false;
    private ArrayList<String> disabledEnemySkills = new ArrayList<>();
    private boolean nextShotEnhanced = false;
    
    
    private int dataLeechCooldown = 0;
    private int overclockCooldown = 0;
    private int systemOverloadCooldown = 0;
    
    
    private int currentMana;
    private static final int MAX_MANA = 450;
    
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
    
    
    
    public boolean useDataLeech(Board enemyBoard) {
        if (dataLeechCooldown > 0) {
            System.out.println("Data Leech is on cooldown for " + dataLeechCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(50)) {
            System.out.println("Not enough mana! Need 50 mana, have " + currentMana);
            return false;
        }
        
        System.out.println("🔓 Jiji uses DATA LEECH: \"Your secrets are mine... wait, that took effort.\"");
        spendMana(50);
        
        
        int revealed = 0;
        int attempts = 0;
        
        while (revealed < 2 && attempts < 100) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);
            Cell cell = enemyBoard.getCell(x, y);
            
            if (!cell.isFiredUpon()) {
                
                
                System.out.println("📡 Data Leech reveals: Cell (" + x + "," + y + 
                                 ") has " + (cell.hasShip() ? "SHIP" : "nothing"));
                revealed++;
            }
            attempts++;
        }
        
        dataLeechCooldown = 1; 
        System.out.println("✅ Data Leech complete! " + revealed + " cells revealed.");
        return true;
    }
    
    
    
    public boolean useOverclock() {
        if (overclockCooldown > 0) {
            System.out.println("Overclock is on cooldown for " + overclockCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(120)) {
            System.out.println("Not enough mana! Need 120 mana, have " + currentMana);
            return false;
        }
        
        System.out.println("⚡ Jiji uses OVERCLOCK: \"Processing power maximum! ...now I need a nap.\"");
        spendMana(120);
        
        
        nextShotEnhanced = true;
        overclockCooldown = 3; 
        
        System.out.println("✅ Next shot will fire TWICE!");
        return true;
    }
    
    public ShotResult applyOverclock(Board enemyBoard, int x, int y) {
        if (!nextShotEnhanced) {
            return enemyBoard.fire(x, y);
        }
        
        System.out.println("⚡⚡ OVERCLOCK ACTIVE! Firing twice!");
        nextShotEnhanced = false;
        
        
        ShotResult result1 = enemyBoard.fire(x, y);
        
        
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}, {-1,-1}, {-1,1}, {1,-1}, {1,1}};
        int[] dir = directions[random.nextInt(directions.length)];
        int x2 = x + dir[0];
        int y2 = y + dir[1];
        
        
        x2 = Math.max(0, Math.min(9, x2));
        y2 = Math.max(0, Math.min(9, y2));
        
        ShotResult result2 = enemyBoard.fire(x2, y2);
        
        
        int damage1 = random.nextInt(101) + 150; 
        int damage2 = random.nextInt(101) + 150;
        System.out.println("💥 First shot damage: " + damage1);
        System.out.println("💥 Second shot damage: " + damage2);
        System.out.println("💥 Total damage: " + (damage1 + damage2));
        
        
        if (result1 == ShotResult.SUNK || result2 == ShotResult.SUNK) {
            return ShotResult.SUNK;
        } else if (result1 == ShotResult.HIT || result2 == ShotResult.HIT) {
            return ShotResult.HIT;
        } else {
            return ShotResult.MISS;
        }
    }
    
    
    
    public boolean useSystemOverload(Board enemyBoard) {
        if (systemOverloadCooldown > 0) {
            System.out.println("System Overload is on cooldown for " + systemOverloadCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(400)) {
            System.out.println("Not enough mana! Need 400 mana, have " + currentMana);
            return false;
        }
        
        System.out.println("💻 Jiji uses SYSTEM OVERLOAD: \"System failure in 3... 2... 1... yawn\"");
        spendMana(400);
        
        
        String[] enemySkills = {"Radar Scan", "Barrage", "Peek", "Emergency Repair"};
        String disabledSkill = enemySkills[random.nextInt(enemySkills.length)];
        disabledEnemySkills.add(disabledSkill);
        
        System.out.println("🛑 Enemy skill '" + disabledSkill + "' is disabled for 3 turns!");
        
        
        int damage = random.nextInt(151) + 250; 
        System.out.println("💥 System Overload deals " + damage + " damage!");
        
        systemOverloadCooldown = 5; 
        return true;
    }
    
    public boolean isSkillDisabled(String skillName) {
        return disabledEnemySkills.contains(skillName);
    }
    
    
    
    public void updateTurnCounter() {
        turnsSinceLastFirewall++;
        
        
        if (dataLeechCooldown > 0) dataLeechCooldown--;
        if (overclockCooldown > 0) overclockCooldown--;
        if (systemOverloadCooldown > 0) systemOverloadCooldown--;
        
        
        regenerateMana(20);
        
        
        if (turnsSinceLastFirewall >= 4) {
            activateFirewall();
        }
    }
    
    private void activateFirewall() {
        decoyActive = true;
        turnsSinceLastFirewall = 0;
        System.out.println("🛡️ FIREWALL ACTIVE! Next hit will be blocked!");
    }
    
    public boolean checkFirewall(int x, int y, ShotResult incomingShot) {
        if (decoyActive && (incomingShot == ShotResult.HIT || incomingShot == ShotResult.SUNK)) {
            decoyActive = false;
            System.out.println("🛡️ Firewall blocked a hit at (" + x + "," + y + ")! \"You can't hack what you can't find...\"");
            return true; 
        }
        return false; 
    }
    
    
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        
        System.out.println("Jiji doesn't have a single special - use individual skills!");
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1: 
                if (dataLeechCooldown > 0) {
                    return "Cooldown: " + dataLeechCooldown + " turns";
                } else if (!hasEnoughMana(50)) {
                    return "Need 50 mana";
                } else {
                    return "Ready!";
                }
            case 2: 
                if (overclockCooldown > 0) {
                    return "Cooldown: " + overclockCooldown + " turns";
                } else if (!hasEnoughMana(120)) {
                    return "Need 120 mana";
                } else {
                    return "Ready!";
                }
            case 3: 
                if (systemOverloadCooldown > 0) {
                    return "Cooldown: " + systemOverloadCooldown + " turns";
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
}
