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
    
    
    public int lunarRevealCooldown = 0;
    public int crescentStrikeCooldown = 0;
    public int starfallLinkCooldown = 0;
    
    
    private ArrayList<String> revealedCells = new ArrayList<>();
    private ArrayList<String> linkedCells = new ArrayList<>();
    private boolean linkActive = false;
    private int linkTurns = 0;
    
    
    private int turnCounter = 0;
    private boolean nightTime = false;
    private int nightTurnsRemaining = 0;
    private static final int DAY_CYCLE_LENGTH = 3;
    private static final int NIGHT_DURATION = 2;
    
    
    private int moonPowerStacks = 0;
    private static final int MAX_MOON_POWER = 5;
    
    
    private boolean eclipseMode = false;
    private int eclipseTurns = 0;
    
    
    private boolean moonBlessingActive = false;
    private int blessingTurns = 0;
    
    
    private boolean skill1UsedThisTurn = false;
    private boolean skill2UsedThisTurn = false;
    private boolean skill3UsedThisTurn = false;
    
    
    private boolean nightSkillsAvailable = false;
    private boolean nightStartedThisTurn = false;
    
    public Selene() {
        super(
            "Selene — The Moon Oracle",
            "A mystic who reads the stars to reveal secrets and command lunar power.",
            1850,
            100,
            new Color(200, 150, 255)
        );
        this.currentMana = MAX_MANA;
        this.abilityName = "Lunar Prophecy";
        this.abilityDescription = "Uses mana to reveal cells, strike with moonlight, and link stars. Enhanced during NIGHT TIME!";
    }
    
    
    public int getCurrentMana() { return currentMana; }
    public int getMaxMana() { return MAX_MANA; }
    public boolean hasEnoughMana(int cost) { return currentMana >= cost; }
    
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
        System.out.println("🌙 Selene mana: " + currentMana + "/" + MAX_MANA);
    }
    
    public boolean consumeNightJustStarted() {
        if (nightStartedThisTurn) {
            nightStartedThisTurn = false;
            return true;
        }
        return false;
    }
    
    public void resetCooldowns() {
        lunarRevealCooldown = 0;
        crescentStrikeCooldown = 0;
        starfallLinkCooldown = 0;
        System.out.println("🌙 Selene cooldowns reset!");
    }
    
   public void updateMoonPhase() {
    boolean wasNight = nightTime;
    
    turnCounter++;
    
    if (!nightTime) {
        
        if (turnCounter >= DAY_CYCLE_LENGTH) {
            enterNightTime();
        }
    } else {
        
        nightTurnsRemaining--;
        if (nightTurnsRemaining <= 0) {
            exitNightTime();
        }
        System.out.println("🌙 Night remaining: " + nightTurnsRemaining + " turns");
    }
    
    
    int regenAmount = nightTime ? 25 : 12;
    regenerateMana(regenAmount);
    
    
    if (nightTime && moonPowerStacks < MAX_MOON_POWER) {
        moonPowerStacks++;
        System.out.println("🌙 Moon power increased! Stack " + moonPowerStacks + "/" + MAX_MOON_POWER);
    }
    
    
    if (moonPowerStacks >= MAX_MOON_POWER && !eclipseMode && nightTime) {
        enterEclipseMode();
    }
    
    if (eclipseMode) {
        eclipseTurns--;
        if (eclipseTurns <= 0) {
            exitEclipseMode();
        }
    }
    
    if (moonBlessingActive) {
        blessingTurns--;
        if (blessingTurns <= 0) {
            moonBlessingActive = false;
            System.out.println("🌙 Moon's blessing has faded.");
        }
    }
    
    skill1UsedThisTurn = false;
    skill2UsedThisTurn = false;
    skill3UsedThisTurn = false;
    
    System.out.println("🌙 Moon phase - Night: " + nightTime + ", Turns until change: " + 
                       (nightTime ? nightTurnsRemaining : (DAY_CYCLE_LENGTH - turnCounter)));
}
    private void enterNightTime() {
        nightTime = true;
        nightTurnsRemaining = NIGHT_DURATION;
        turnCounter = 0;
        nightStartedThisTurn = true;
        nightSkillsAvailable = true;
        
        
        lunarRevealCooldown = 0;
        crescentStrikeCooldown = 0;
        starfallLinkCooldown = 0;
        
        
        regenerateMana(100);
        
        System.out.println("🌙✨ NIGHT FALLS! All skills (including ultimate) reset!");
        System.out.println("   Lunar Reveal ready!");
        System.out.println("   Crescent Strike ready!");
        System.out.println("   Starfall Link (ULTIMATE) ready!");
        System.out.println("   +100 bonus mana!");
    }
    
    private void exitNightTime() {
        nightTime = false;
        nightSkillsAvailable = false;
        System.out.println("🌅 Night ends. Selene returns to normal.");
        
        if (moonPowerStacks > 0) {
            moonPowerStacks = Math.max(0, moonPowerStacks - 2);
            System.out.println("🌙 Moon power decays to " + moonPowerStacks);
        }
    }
    
    private void enterEclipseMode() {
        eclipseMode = true;
        eclipseTurns = 2;
        moonPowerStacks = 0;
        nightSkillsAvailable = true;
        
        lunarRevealCooldown = 0;
        crescentStrikeCooldown = 0;
        starfallLinkCooldown = 0;
        
        System.out.println("🌑🌑🌑 ECLIPSE MODE ACTIVATED! Selene's ultimate power unleashed!");
        System.out.println("   • All skills are FREE during eclipse!");
        System.out.println("   • Double damage and effects!");
        System.out.println("   • Starfall Link becomes instant kill!");
    }
    
    private void exitEclipseMode() {
        eclipseMode = false;
        System.out.println("🌑 Eclipse mode ends. The moon returns to normal.");
    }
    
    
    public boolean useLunarReveal(Board enemyBoard, int centerX, int centerY) {
        System.out.println("🔍 useLunarReveal called");
        System.out.println("   nightTime = " + nightTime);
        System.out.println("   eclipseMode = " + eclipseMode);
        System.out.println("   lunarRevealCooldown = " + lunarRevealCooldown);
        
        if (skill1UsedThisTurn) {
            System.out.println("❌ Lunar Reveal already used this turn!");
            return false;
        }
        
        if (lunarRevealCooldown > 0 && !nightTime && !eclipseMode) {
            System.out.println("❌ On cooldown: " + lunarRevealCooldown);
            return false;
        }
        
        
        if (nightTime || eclipseMode) {
            System.out.println("🌙 Night/Eclipse mode - ignoring cooldown!");
        }
        
        int manaCost = 60;
        if (eclipseMode) {
            manaCost = 0;
        } else if (nightTime) {
            manaCost = 30; 
        }
        
        if (!eclipseMode && !hasEnoughMana(manaCost)) {
            System.out.println("❌ Not enough mana! Need " + manaCost);
            return false;
        }
        
        if (!eclipseMode) spendMana(manaCost);
        skill1UsedThisTurn = true;
        
        
        int minX, maxX, minY, maxY;
        
        if (eclipseMode) {
            minX = Math.max(0, centerX - 2);
            maxX = Math.min(9, centerX + 2);
            minY = Math.max(0, centerY - 2);
            maxY = Math.min(9, centerY + 2);
            System.out.println("🌑🌑🌑 SELENE uses LUNAR ECLIPSE!");
        } else if (nightTime) {
            minX = Math.max(0, centerX - 1);
            maxX = Math.min(9, centerX + 2);
            minY = Math.max(0, centerY - 1);
            maxY = Math.min(9, centerY + 2);
            System.out.println("🌙✨ SELENE uses LUNAR REVELATION!");
        } else {
            minX = Math.max(0, centerX - 1);
            maxX = Math.min(9, centerX + 1);
            minY = Math.max(0, centerY - 1);
            maxY = Math.min(9, centerY + 1);
            System.out.println("🔮 SELENE uses LUNAR REVEAL!");
        }
        
        int cellsAffected = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Cell cell = enemyBoard.getCell(x, y);
                if (!cell.isFiredUpon()) {
                    enemyBoard.fire(x, y);
                    cellsAffected++;
                }
            }
        }
        
        System.out.println("🔮 Lunar Reveal affected " + cellsAffected + " cells!");
        
        if (!nightTime && !eclipseMode) {
            lunarRevealCooldown = 2;
        }
        
        return true;
    }
    
    
    public int useCrescentStrike(Board enemyBoard, int centerX, int centerY) {
        System.out.println("⚔️ useCrescentStrike called");
        System.out.println("   nightTime = " + nightTime);
        System.out.println("   eclipseMode = " + eclipseMode);
        
        if (skill2UsedThisTurn) {
            System.out.println("❌ Crescent Strike already used this turn!");
            return 0;
        }
        
        if (crescentStrikeCooldown > 0 && !nightTime && !eclipseMode) {
            System.out.println("❌ On cooldown: " + crescentStrikeCooldown);
            return 0;
        }
        
        int manaCost = 120;
        if (eclipseMode) {
            manaCost = 0;
        } else if (nightTime) {
            manaCost = 60; 
        }
        
        if (!eclipseMode && !hasEnoughMana(manaCost)) {
            System.out.println("❌ Not enough mana! Need " + manaCost);
            return 0;
        }
        
        if (!eclipseMode) spendMana(manaCost);
        skill2UsedThisTurn = true;
        
        int totalDestroyed = 0;
        
        if (eclipseMode) {
            
            int[][] directions = {{-2,0}, {2,0}, {0,-2}, {0,2}, {-1,-1}, {-1,1}, {1,-1}, {1,1}, {-1,0}, {1,0}, {0,-1}, {0,1}};
            totalDestroyed++;
            for (int[] dir : directions) {
                int x = centerX + dir[0];
                int y = centerY + dir[1];
                if (x >= 0 && x < 10 && y >= 0 && y < 10 && !enemyBoard.getCell(x, y).isFiredUpon()) {
                    enemyBoard.fire(x, y);
                    totalDestroyed++;
                }
            }
        } else if (nightTime) {
            
            int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}, {-1,-1}, {-1,1}, {1,-1}, {1,1}};
            totalDestroyed++;
            for (int[] dir : directions) {
                int x = centerX + dir[0];
                int y = centerY + dir[1];
                if (x >= 0 && x < 10 && y >= 0 && y < 10 && !enemyBoard.getCell(x, y).isFiredUpon()) {
                    enemyBoard.fire(x, y);
                    totalDestroyed++;
                }
            }
        } else {
            
            int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
            totalDestroyed++;
            for (int[] dir : directions) {
                int x = centerX + dir[0];
                int y = centerY + dir[1];
                if (x >= 0 && x < 10 && y >= 0 && y < 10 && !enemyBoard.getCell(x, y).isFiredUpon()) {
                    enemyBoard.fire(x, y);
                    totalDestroyed++;
                }
            }
        }
        
        System.out.println("⚔️ Crescent Strike destroyed " + totalDestroyed + " cells!");
        
        if (!nightTime && !eclipseMode) {
            crescentStrikeCooldown = 3;
        }
        
        return totalDestroyed;
    }
    
    
    public boolean useStarfallLink(Board enemyBoard) {
        System.out.println("⭐ useStarfallLink called");
        System.out.println("   nightTime = " + nightTime);
        System.out.println("   eclipseMode = " + eclipseMode);
        System.out.println("   currentMana = " + currentMana);
        
        if (skill3UsedThisTurn) {
            System.out.println("❌ Starfall Link already used this turn!");
            return false;
        }
        
        if (starfallLinkCooldown > 0 && !nightTime && !eclipseMode) {
            System.out.println("❌ On cooldown: " + starfallLinkCooldown);
            return false;
        }
        
        
        int actualManaCost = 300;
        if (eclipseMode) {
            actualManaCost = 0;
            System.out.println("🌑 Eclipse mode - FREE!");
        } else if (nightTime) {
            actualManaCost = 150; 
            System.out.println("🌙 Night time - 150 mana (half price!)");
        }
        
        if (!eclipseMode && currentMana < actualManaCost) {
            System.out.println("⚠️ Not enough mana! Need " + actualManaCost + ", have " + currentMana);
            return false;
        }
        
        if (!eclipseMode) spendMana(actualManaCost);
        skill3UsedThisTurn = true;
        
        linkedCells.clear();
        
        int starsToDrop;
        int linksToCreate;
        
        if (eclipseMode) {
            starsToDrop = 8;
            linksToCreate = 4;
            System.out.println("🌑🌑🌑 STARFALL ECLIPSE!");
        } else if (nightTime) {
            starsToDrop = 5;
            linksToCreate = 2;
            System.out.println("⭐🌙 STARFALL LINK (NIGHT)!");
        } else {
            starsToDrop = 3;
            linksToCreate = 1;
            System.out.println("⭐ STARFALL LINK!");
        }
        
        
        int starsDestroyed = 0;
        for (int i = 0; i < starsToDrop; i++) {
            int attempts = 0;
            boolean placed = false;
            
            while (!placed && attempts < 50) {
                int x = random.nextInt(10);
                int y = random.nextInt(10);
                Cell cell = enemyBoard.getCell(x, y);
                
                if (!cell.isFiredUpon()) {
                    enemyBoard.fire(x, y);
                    starsDestroyed++;
                    placed = true;
                }
                attempts++;
            }
        }
        
        System.out.println("⭐ " + starsDestroyed + " cells destroyed by falling stars!");
        
        
        ArrayList<int[]> undestroyedCells = new ArrayList<>();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (!enemyBoard.getCell(x, y).isFiredUpon()) {
                    undestroyedCells.add(new int[]{x, y});
                }
            }
        }
        
        int linksCreated = 0;
        for (int l = 0; l < linksToCreate && undestroyedCells.size() >= 2; l++) {
            int[] cell1 = undestroyedCells.remove(random.nextInt(undestroyedCells.size()));
            int[] cell2 = undestroyedCells.remove(random.nextInt(undestroyedCells.size()));
            
            linkedCells.add(cell1[0] + "," + cell1[1]);
            linkedCells.add(cell2[0] + "," + cell2[1]);
            linksCreated++;
        }
        
        if (linksCreated > 0) {
            linkActive = true;
            linkTurns = eclipseMode ? 4 : (nightTime ? 3 : 2);
        }
        
        if (!nightTime && !eclipseMode) {
            starfallLinkCooldown = 5;
        }
        
        return true;
    }
    
    private void activateMoonBlessing() {
        moonBlessingActive = true;
        blessingTurns = 2;
        System.out.println("🌙 Moon's blessing activated!");
    }
    
    public void checkLinkedCells(Board enemyBoard, int hitX, int hitY) {
        if (!linkActive) return;
        
        String hitKey = hitX + "," + hitY;
        
        int linkedIndex = -1;
        for (int i = 0; i < linkedCells.size(); i++) {
            if (linkedCells.get(i).equals(hitKey)) {
                linkedIndex = i;
                break;
            }
        }
        
        if (linkedIndex >= 0) {
            int pairIndex = (linkedIndex % 2 == 0) ? linkedIndex + 1 : linkedIndex - 1;
            
            if (pairIndex < linkedCells.size()) {
                String pairKey = linkedCells.get(pairIndex);
                String[] parts = pairKey.split(",");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                
                Cell linkedCell = enemyBoard.getCell(x, y);
                if (!linkedCell.isFiredUpon()) {
                    enemyBoard.fire(x, y);
                    System.out.println("🔗 LINKED CELL DAMAGE! (" + x + "," + y + ") destroyed!");
                }
            }
        }
        
        linkTurns--;
        if (linkTurns <= 0) {
            linkActive = false;
            linkedCells.clear();
        }
    }
    
    public void updateTurnCounter() {
        if (!nightTime && !eclipseMode) {
            if (lunarRevealCooldown > 0) lunarRevealCooldown--;
            if (crescentStrikeCooldown > 0) crescentStrikeCooldown--;
            if (starfallLinkCooldown > 0) starfallLinkCooldown--;
        } else {
            
            lunarRevealCooldown = 0;
            crescentStrikeCooldown = 0;
            starfallLinkCooldown = 0;
        }
        
        if (linkActive) {
            linkTurns--;
            if (linkTurns <= 0) {
                linkActive = false;
                linkedCells.clear();
            }
        }
        
        updateMoonPhase();
    }
    
    public void endTurn() {
        System.out.println("🌙 Selene's turn ends.");
        skill1UsedThisTurn = false;
        skill2UsedThisTurn = false;
        skill3UsedThisTurn = false;
    }
    
    public int getTurnsUntilNight() {
        if (nightTime) return 0;
        return DAY_CYCLE_LENGTH - turnCounter;
    }
    
    public boolean isNightTime() { return nightTime; }
    public boolean isEclipseMode() { return eclipseMode; }
    public boolean isLinkActive() { return linkActive; }
    public int getMoonPowerStacks() { return moonPowerStacks; }
    public boolean hasMoonBlessing() { return moonBlessingActive; }
    
    public String getSkillStatus(int skillNum) {
        String nightBonus = nightTime ? " (🌙 ENHANCED!)" : "";
        String eclipseBonus = eclipseMode ? " (🌑 ECLIPSE - FREE!)" : "";
        
        switch(skillNum) {
            case 1:
                if (!nightTime && !eclipseMode && lunarRevealCooldown > 0) {
                    return "Cooldown: " + lunarRevealCooldown;
                } else if (!eclipseMode && !nightTime && !hasEnoughMana(60)) {
                    return "Need 60 mana";
                } else {
                    return "Ready!" + nightBonus + eclipseBonus;
                }
            case 2:
                if (!nightTime && !eclipseMode && crescentStrikeCooldown > 0) {
                    return "Cooldown: " + crescentStrikeCooldown;
                } else if (!eclipseMode && !nightTime && !hasEnoughMana(120)) {
                    return "Need 120 mana";
                } else {
                    return "Ready!" + nightBonus + eclipseBonus;
                }
            case 3:
                if (!nightTime && !eclipseMode && starfallLinkCooldown > 0) {
                    return "Cooldown: " + starfallLinkCooldown;
                } else {
                    int requiredMana = 300;
                    if (eclipseMode) {
                        return "ECLIPSE READY! (FREE!)";
                    } else if (nightTime) {
                        if (currentMana >= 150) {
                            return "ULTIMATE READY! (150 mana)";
                        } else {
                            return "Need 150 mana";
                        }
                    } else {
                        if (currentMana >= 300) {
                            return "ULTIMATE READY!";
                        } else {
                            return "Need 300 mana";
                        }
                    }
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
        
        if (eclipseMode) {
            bar.append(" 🌑 ECLIPSE MODE!");
        } else if (nightTime) {
            bar.append(" 🌙 NIGHT TIME!");
        }
        
        return bar.toString();
    }
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        System.out.println("Selene's abilities are used through skill buttons!");
    }
}