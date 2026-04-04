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
    private static final int MAX_MANA = 450;
    
    
    private int enchantingMelodyCooldown = 0;
    private int whirlpoolTrapCooldown = 0;
    private int tidalWaveCooldown = 0;
    
    
    private boolean enemyConfused = false;
    private int confusionTurns = 0;
    
    
    private ArrayList<String> whirlpoolCells = new ArrayList<>();
    private int whirlpoolActiveTurns = 0;
    
    
    private boolean tidalWaveActive = false;
    private int tidalWaveTurns = 0;
    
    
    private boolean oceanBlessingActive = false;
    private int blessingTurns = 0;
    
    
    private boolean sirenCallActive = false;
    private int sirenCallTurns = 0;
    
    
    private boolean seaMistActive = false;
    private int seaMistTurns = 0;
    
    
    private boolean firstHitDodged = false;
    
    public Morgana() {
        super(
            "Morgana — The Siren Queen",
            "A mystical siren who commands the sea itself. Her songs confuse enemies and summon storms.",
            2200,
            100,
            new Color(64, 224, 208)
        );
        this.currentMana = MAX_MANA;
        this.abilityName = "Ocean's Command";
        this.abilityDescription = "Uses mana to confuse enemies, trap ships, and summon devastating tidal waves.";
    }
    
    
    public int getCurrentMana() { return currentMana; }
    public int getMaxMana() { return MAX_MANA; }
    public boolean hasEnoughMana(int cost) { return currentMana >= cost; }
    
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
        System.out.println("🌊 Morgana mana: " + currentMana + "/" + MAX_MANA);
    }
    
    
    public void stealMana(int amount) {
        currentMana += amount;
        if (currentMana > MAX_MANA) {
            currentMana = MAX_MANA;
        }
        System.out.println("🌊 Morgana stole " + amount + " mana! Total: " + currentMana);
    }
    
    
    private void statusMessage(String message) {
        System.out.println(message);
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
        confusionTurns = 3;
        System.out.println("🎵 Enemy is CONFUSED for 3 turns! They will see fake hit/miss indicators!");
        
        
        if (random.nextInt(100) < 20) {
            System.out.println("🎵 The melody is so powerful it deals 10 damage to the enemy!");
            statusMessage("The melody damages the enemy commander!");
        }
        
        enchantingMelodyCooldown = 3;
        return true;
    }
    
    public boolean isEnemyConfused() {
        return enemyConfused;
    }
    
    public ShotResult applyConfusion(ShotResult realResult) {
        if (!enemyConfused) {
            return realResult;
        }
        
        
        if (random.nextInt(100) < 40) {
            if (realResult == ShotResult.HIT || realResult == ShotResult.SUNK) {
                System.out.println("🎵 Confusion: Enemy thought they missed!");
                return ShotResult.MISS;
            } else if (realResult == ShotResult.MISS) {
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
        
        System.out.println("🌊 MORGANA uses WHIRLPOOL TRAP at (" + centerX + "," + centerY + ")!");
        spendMana(80);
        
        whirlpoolCells.clear();
        
        
        int minX = Math.max(0, centerX - 1);
        int maxX = Math.min(9, centerX + 1);
        int minY = Math.max(0, centerY - 1);
        int maxY = Math.min(9, centerY + 1);
        
        int trappedShips = 0;
        int totalDamage = 0;
        StringBuilder hitReport = new StringBuilder("🌊 Whirlpool Trap hits area:\n");
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                String cellKey = x + "," + y;
                whirlpoolCells.add(cellKey);
                
                Cell cell = enemyBoard.getCell(x, y);
                
                if (!cell.isFiredUpon()) {
                    ShotResult result = enemyBoard.fire(x, y);
                    
                    if (cell.hasShip()) {
                        int damage = random.nextInt(41) + 60; 
                        totalDamage += damage;
                        trappedShips++;
                        hitReport.append("   • Ship at (").append(x).append(",").append(y)
                                 .append(") takes ").append(damage).append(" damage! (").append(result).append(")\n");
                    } else {
                        hitReport.append("   • Cell (").append(x).append(",").append(y)
                                 .append(") churns violently (Miss)\n");
                    }
                } else {
                    hitReport.append("   • Cell (").append(x).append(",").append(y)
                             .append(") already hit\n");
                }
            }
        }
        
        System.out.println(hitReport.toString());
        System.out.println("💧 Whirlpool trap dealt " + totalDamage + " damage to " + 
                           trappedShips + " ship(s)!");
        
        
        whirlpoolActiveTurns = 2;
        whirlpoolTrapCooldown = 3;
        
        
        if (random.nextBoolean()) {
            activateSeaMist();
        }
        
        return true;
    }
    
    public boolean isCellInWhirlpool(int x, int y) {
        return whirlpoolCells.contains(x + "," + y) && whirlpoolActiveTurns > 0;
    }
    
    
    private void activateSeaMist() {
        seaMistActive = true;
        seaMistTurns = 2;
        System.out.println("🌫️ SEA MIST ACTIVATED! Your ships have 30% chance to evade attacks!");
    }
    
    public boolean tryEvade() {
        if (seaMistActive && random.nextInt(100) < 30) {
            System.out.println("🌫️ Sea Mist caused the enemy to miss!");
            return true;
        }
        return false;
    }
    
    
    public int useTidalWave(Board enemyBoard) {
        if (tidalWaveCooldown > 0) {
            System.out.println("⏳ Tidal Wave is on cooldown for " + tidalWaveCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughMana(300)) {
            System.out.println("⚠️ Not enough mana! Need 300 mana, have " + currentMana);
            return 0;
        }
        
        System.out.println("🌊🌊🌊 MORGANA uses TIDAL WAVE: \"Feel the wrath of the sea!\"");
        spendMana(300);
        
        int cellsDestroyed = 0;
        int shipsHit = 0;
        int totalDamage = 0;
        
        
        for (int i = 0; i < 6; i++) {
            int attempts = 0;
            boolean placed = false;
            
            while (!placed && attempts < 50) {
                int x = random.nextInt(10);
                int y = random.nextInt(10);
                Cell cell = enemyBoard.getCell(x, y);
                
                if (!cell.isFiredUpon()) {
                    ShotResult result = enemyBoard.fire(x, y);
                    cellsDestroyed++;
                    
                    if (cell.hasShip()) {
                        int damage = random.nextInt(101) + 150; 
                        totalDamage += damage;
                        shipsHit++;
                        System.out.println("   • Ship at (" + x + "," + y + ") takes " + damage + " damage! " + result);
                    } else {
                        System.out.println("   • Cell (" + x + "," + y + ") flooded by the tidal wave");
                    }
                    placed = true;
                }
                attempts++;
            }
        }
        
        System.out.println("🌊 Tidal Wave destroyed " + cellsDestroyed + " cells!");
        System.out.println("💥 " + shipsHit + " ships hit for " + totalDamage + " total damage!");
        
        
        if (random.nextInt(100) < 30) {
            activateSirenCall();
        }
        
        
        activateOceanBlessing();
        
        tidalWaveCooldown = 4;
        return cellsDestroyed;
    }
    
    
    private void activateOceanBlessing() {
        oceanBlessingActive = true;
        blessingTurns = 2;
        System.out.println("🌊 OCEAN'S BLESSING ACTIVATED! Next skill costs 50% less mana!");
    }
    
    
    private void activateSirenCall() {
        sirenCallActive = true;
        sirenCallTurns = 2;
        System.out.println("🎵🎵🎵 SIREN'S CALL ACTIVATED! Enemy skills are disabled for 2 turns!");
    }
    
    public boolean isSirenCallActive() {
        return sirenCallActive;
    }
    
    public boolean hasOceanBlessing() {
        return oceanBlessingActive;
    }
    
    public int getReducedManaCost(int originalCost) {
        if (oceanBlessingActive) {
            return originalCost / 2;
        }
        return originalCost;
    }
    
    
    public boolean tryDodgeHit(int x, int y, ShotResult incomingShot) {
        if (!firstHitDodged && (incomingShot == ShotResult.HIT || incomingShot == ShotResult.SUNK)) {
            firstHitDodged = true;
            System.out.println("🌊 OCEAN'S EMBRACE! Morgana's mist shields her from the first hit!");
            return true;
        }
        return false;
    }
    
    public boolean isOceanEmbraceUsed() {
        return firstHitDodged;
    }
    
    
    public void updateTurnCounter() {
        
        if (enchantingMelodyCooldown > 0) enchantingMelodyCooldown--;
        if (whirlpoolTrapCooldown > 0) whirlpoolTrapCooldown--;
        if (tidalWaveCooldown > 0) tidalWaveCooldown--;
        
        
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
        
        
        if (oceanBlessingActive) {
            blessingTurns--;
            if (blessingTurns <= 0) {
                oceanBlessingActive = false;
                System.out.println("🌊 Ocean's Blessing has faded.");
            }
        }
        
        
        if (sirenCallActive) {
            sirenCallTurns--;
            if (sirenCallTurns <= 0) {
                sirenCallActive = false;
                System.out.println("🎵 Siren's Call has ended.");
            }
        }
        
        
        if (seaMistActive) {
            seaMistTurns--;
            if (seaMistTurns <= 0) {
                seaMistActive = false;
                System.out.println("🌫️ Sea Mist has faded.");
            }
        }
        
        
        regenerateMana(15);
    }
    
    
    public String getSkillStatus(int skillNum) {
        String blessingBonus = oceanBlessingActive ? " (🌟 50% OFF!)" : "";
        
        switch(skillNum) {
            case 1:
                if (enchantingMelodyCooldown > 0) {
                    return "Cooldown: " + enchantingMelodyCooldown + " turn" + (enchantingMelodyCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(40)) {
                    return "Need 40 mana";
                } else {
                    return "Ready!" + blessingBonus;
                }
            case 2:
                if (whirlpoolTrapCooldown > 0) {
                    return "Cooldown: " + whirlpoolTrapCooldown + " turn" + (whirlpoolTrapCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(80)) {
                    return "Need 80 mana";
                } else {
                    return "Ready!" + blessingBonus;
                }
            case 3:
                if (tidalWaveCooldown > 0) {
                    return "Cooldown: " + tidalWaveCooldown + " turn" + (tidalWaveCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(300)) {
                    return "Need 300 mana";
                } else {
                    if (sirenCallActive) {
                        return "SIREN'S CALL ACTIVE! (Ultimate enhanced!)";
                    }
                    return "ULTIMATE READY!" + blessingBonus;
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
        
        if (oceanBlessingActive) {
            bar.append(" 🌊 BLESSED!");
        }
        if (sirenCallActive) {
            bar.append(" 🎵 SIREN'S CALL!");
        }
        if (seaMistActive) {
            bar.append(" 🌫️ MIST!");
        }
        
        return bar.toString();
    }
    
    public int getWhirlpoolCount() {
        return whirlpoolCells.size();
    }
    
    public boolean isEnemyConfusedActive() {
        return enemyConfused;
    }
    
    public boolean isSeaMistActive() {
        return seaMistActive;
    }
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        System.out.println("Morgana's abilities are used through skill buttons!");
    }
}