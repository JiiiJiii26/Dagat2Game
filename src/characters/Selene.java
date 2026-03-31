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
    
    
    private int lunarRevealCooldown = 0;
    private int crescentStrikeCooldown = 0;
    private int starfallLinkCooldown = 0;
    
    
    private ArrayList<String> revealedCells = new ArrayList<>();
    
    
    private ArrayList<String> linkedCells = new ArrayList<>();
    private boolean linkActive = false;
    private int linkTurns = 0;
    
    
    private int turnCounter = 0;
    private boolean nightTime = false;

     private boolean nightTimeActive = false;
    private int nightTurnsRemaining = 0;
    
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
    
    
    
    
    
    public boolean useLunarReveal(Board enemyBoard, int centerX, int centerY) {
    System.out.println("🔍 useLunarReveal called");
    System.out.println("   nightTime = " + nightTime);
    System.out.println("   lunarRevealCooldown = " + lunarRevealCooldown);
    System.out.println("   currentMana = " + currentMana);
    
    if (lunarRevealCooldown > 0) {
        System.out.println("❌ On cooldown: " + lunarRevealCooldown);
        return false;
    }
    
    if (!hasEnoughMana(60)) {
        System.out.println("❌ Not enough mana: " + currentMana + "/60");
        return false;
    }
        
        int minX, maxX, minY, maxY;
        String skillName;
        
        if (nightTime) {
            
            minX = Math.max(0, centerX - 2);
            maxX = Math.min(9, centerX + 2);
            minY = Math.max(0, centerY - 2);
            maxY = Math.min(9, centerY + 2);
            skillName = "LUNAR REVELATION (NIGHT)";
            System.out.println("🌙✨ SELENE uses " + skillName + ": \"The full moon reveals and destroys!\"");
        } else {
            
            minX = Math.max(0, centerX - 1);
            maxX = Math.min(9, centerX + 1);
            minY = Math.max(0, centerY - 1);
            maxY = Math.min(9, centerY + 1);
            skillName = "LUNAR REVEAL";
            System.out.println("🔮 SELENE uses " + skillName + ": \"The moon reveals all...\"");
        }
        
        int cellsAffected = 0;
        int shipsFound = 0;
        StringBuilder report = new StringBuilder("🔮 " + skillName + ":\n");
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                String cellKey = x + "," + y;
                Cell cell = enemyBoard.getCell(x, y);
                
                if (nightTime) {
                    
                    if (!cell.isFiredUpon()) {
                        ShotResult result = enemyBoard.fire(x, y);
                        cellsAffected++;
                        if (cell.hasShip()) {
                            shipsFound++;
                            report.append("   • Ship at (" + x + "," + y + ") DESTROYED! " + result + "\n");
                        } else {
                            report.append("   • Cell (" + x + "," + y + ") destroyed\n");
                        }
                    } else {
                        report.append("   • Cell (" + x + "," + y + ") already hit\n");
                    }
                } else {
                    
                    if (!revealedCells.contains(cellKey)) {
                        revealedCells.add(cellKey);
                        cell.setRevealed(true);
                        if (cell.hasShip()) {
                            shipsFound++;
                            report.append("   • Cell (" + x + "," + y + ") contains a SHIP!\n");
                        } else {
                            report.append("   • Cell (" + x + "," + y + ") is empty.\n");
                        }
                    } else {
                        report.append("   • Cell (" + x + "," + y + ") already revealed\n");
                    }
                }
            }
        }
        
        System.out.println(report.toString());
        if (nightTime) {
            System.out.println("🌙✨ Lunar Revelation destroyed " + cellsAffected + " cells!");
            System.out.println("   Hit " + shipsFound + " enemy ships!");
        } else {
            System.out.println("🔮 Found " + shipsFound + " ship segments in the area!");
        }
        
        lunarRevealCooldown = 2;
        return true;
    }
    
    
    
    
    
    public int useCrescentStrike(Board enemyBoard, int centerX, int centerY) {
        if (crescentStrikeCooldown > 0) {
            System.out.println("⏳ Crescent Strike is on cooldown for " + crescentStrikeCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughMana(120)) {
            System.out.println("⚠️ Not enough mana! Need 120 mana, have " + currentMana);
            return 0;
        }
        
        spendMana(120);
        
        int totalDestroyed = 0;
        int shipsHit = 0;
        StringBuilder strikeReport = new StringBuilder();
        
        if (nightTime) {
            
            System.out.println("🌙✨ SELENE uses CRESCENT STORM (NIGHT): \"The moon's wrath rains down!\"");
            strikeReport.append("🌙✨ Crescent Storm hits:\n");
            
            int[][] directions = {
                {-1,0}, {1,0}, {0,-1}, {0,1},  
                {-1,-1}, {-1,1}, {1,-1}, {1,1}  
            };
            
            
            totalDestroyed += hitCell(enemyBoard, centerX, centerY, strikeReport);
            if (enemyBoard.getCell(centerX, centerY).hasShip()) shipsHit++;
            
            
            for (int[] dir : directions) {
                int x = centerX + dir[0];
                int y = centerY + dir[1];
                if (x >= 0 && x < 10 && y >= 0 && y < 10) {
                    totalDestroyed += hitCell(enemyBoard, x, y, strikeReport);
                    if (enemyBoard.getCell(x, y).hasShip()) shipsHit++;
                } else {
                    strikeReport.append("   • Out of bounds: (" + x + "," + y + ")\n");
                }
            }
            
            System.out.println(strikeReport.toString());
            System.out.println("🌙✨ Crescent Storm destroyed " + totalDestroyed + " cells!");
            System.out.println("   Hit " + shipsHit + " enemy ships!");
            
        } else {
            
            System.out.println("🌙 SELENE uses CRESCENT STRIKE: \"Witness the edge of the moon!\"");
            strikeReport.append("🌙 Crescent Strike hits:\n");
            
            
            totalDestroyed += hitCell(enemyBoard, centerX, centerY, strikeReport);
            if (enemyBoard.getCell(centerX, centerY).hasShip()) shipsHit++;
            
            
            int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
            for (int[] dir : directions) {
                int x = centerX + dir[0];
                int y = centerY + dir[1];
                if (x >= 0 && x < 10 && y >= 0 && y < 10) {
                    totalDestroyed += hitCell(enemyBoard, x, y, strikeReport);
                    if (enemyBoard.getCell(x, y).hasShip()) shipsHit++;
                } else {
                    strikeReport.append("   • Out of bounds: (" + x + "," + y + ")\n");
                }
            }
            
            System.out.println(strikeReport.toString());
            System.out.println("🌙 Crescent Strike destroyed " + totalDestroyed + " cells!");
            System.out.println("   Hit " + shipsHit + " enemy ships!");
        }
        
        crescentStrikeCooldown = 3;
        return totalDestroyed;
    }
    
    private int hitCell(Board board, int x, int y, StringBuilder report) {
        Cell cell = board.getCell(x, y);
        
        if (!cell.isFiredUpon()) {
            ShotResult result = board.fire(x, y);
            if (cell.hasShip()) {
                report.append("   • Ship at (" + x + "," + y + ") destroyed! " + result + "\n");
            } else {
                report.append("   • Cell (" + x + "," + y + ") sliced by moonlight\n");
            }
            return 1;
        } else {
            report.append("   • Cell (" + x + "," + y + ") already hit\n");
            return 0;
        }
    }
    
    
    
    
    
    public boolean useStarfallLink(Board enemyBoard) {
        if (starfallLinkCooldown > 0) {
            System.out.println("⏳ Starfall Link is on cooldown for " + starfallLinkCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(300)) {
            System.out.println("⚠️ Not enough mana! Need 300 mana, have " + currentMana);
            return false;
        }
        
        spendMana(300);
        
        linkedCells.clear();
        
        int starsToDrop = nightTime ? 5 : 3;
        int linksToCreate = nightTime ? 2 : 1;  
        
        if (nightTime) {
            System.out.println("⭐🌙 SELENE uses STARFALL ECLIPSE (NIGHT): \"The stars align with the moon!\"");
        } else {
            System.out.println("⭐ SELENE uses STARFALL LINK: \"Stars fall and fate intertwines!\"");
        }
        
        
        int starsDestroyed = 0;
        StringBuilder starReport = new StringBuilder("⭐ Stars fall:\n");
        
        for (int i = 0; i < starsToDrop; i++) {
            int attempts = 0;
            boolean placed = false;
            
            while (!placed && attempts < 50) {
                int x = random.nextInt(10);
                int y = random.nextInt(10);
                Cell cell = enemyBoard.getCell(x, y);
                
                if (!cell.isFiredUpon()) {
                    ShotResult result = enemyBoard.fire(x, y);
                    starsDestroyed++;
                    starReport.append("   • Star at (" + x + "," + y + ") destroyed! " + result + "\n");
                    placed = true;
                }
                attempts++;
            }
        }
        
        System.out.println(starReport.toString());
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
            
            String linkKey1 = cell1[0] + "," + cell1[1];
            String linkKey2 = cell2[0] + "," + cell2[1];
            
            linkedCells.add(linkKey1);
            linkedCells.add(linkKey2);
            
            System.out.println("🔗 Cells (" + cell1[0] + "," + cell1[1] + 
                             ") and (" + cell2[0] + "," + cell2[1] + ") are now LINKED!");
            linksCreated++;
        }
        
        if (linksCreated > 0) {
            System.out.println("🔗 " + linksCreated + " link(s) created!");
            if (nightTime) {
                System.out.println("🌙 NIGHT BONUS: Double links activated!");
            }
            linkActive = true;
            linkTurns = 2;
        } else {
            System.out.println("⚠️ Not enough cells to create links!");
        }
        
        starfallLinkCooldown = 5;
        return true;
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
                    ShotResult result = enemyBoard.fire(x, y);
                    System.out.println("🔗 LINKED CELL DAMAGE! (" + x + "," + y + ") also destroyed! " + result);
                }
            }
        }
        
        linkTurns--;
        if (linkTurns <= 0) {
            linkActive = false;
            linkedCells.clear();
            System.out.println("🔗 The star links have faded.");
        }
    }
    
    public boolean isLinkActive() {
        return linkActive;
    }
    
    
    
    

 public void updateTurnCounter() {
    
    if (lunarRevealCooldown > 0) lunarRevealCooldown--;
    if (crescentStrikeCooldown > 0) crescentStrikeCooldown--;
    if (starfallLinkCooldown > 0) starfallLinkCooldown--;
    
    if (linkActive) {
        linkTurns--;
        if (linkTurns <= 0) {
            linkActive = false;
            linkedCells.clear();
            System.out.println("🔗 The star links have faded.");
        }
    }
    
    
    
    
    
    
    turnCounter++;
    
    
    if (turnCounter >= 3) {
        turnCounter = 0;
        nightTime = true;
        System.out.println("🌙✨ MOON'S BLESSING! NIGHT FALLS! All abilities are ENHANCED!");
    }
    
    
    
    regenerateMana(12);
}


public void endTurn() {
    
    nightTime = false;
    System.out.println("🌙 Night has ended.");
}
    public int getTurnsUntilNight() {
    
    
    if (nightTime) {
        return 0;
    }
    
    return 3 - turnCounter;
}
    public boolean isNightTime() {
    return nightTime;
}
    
    public String getSkillStatus(int skillNum) {
        String nightBonus = nightTime ? " (🌙 ENHANCED!)" : "";
        
        switch(skillNum) {
            case 1:
                if (lunarRevealCooldown > 0) {
                    return "Cooldown: " + lunarRevealCooldown + " turn" + (lunarRevealCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(60)) {
                    return "Need 60 mana";
                } else {
                    return "Ready!" + nightBonus;
                }
            case 2:
                if (crescentStrikeCooldown > 0) {
                    return "Cooldown: " + crescentStrikeCooldown + " turn" + (crescentStrikeCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(120)) {
                    return "Need 120 mana";
                } else {
                    return "Ready!" + nightBonus;
                }
            case 3:
                if (starfallLinkCooldown > 0) {
                    return "Cooldown: " + starfallLinkCooldown + " turn" + (starfallLinkCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(300)) {
                    return "Need 300 mana";
                } else {
                    if (nightTime) {
                        return "ULTIMATE READY! (🌙 5 stars + 2 links!)";
                    }
                    return "ULTIMATE READY!";
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
        System.out.println("Selene's abilities are used through skill buttons!");
    }
}