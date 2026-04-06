package characters;

import models.Board;
import models.Cell;
import models.Ship;
import game.ShotResult;
import java.awt.Color;
import java.util.Random;

public class Skye extends GameCharacter {
    
    private Random random = new Random();
    private int currentMana;
    private static final int MAX_MANA = 440;
    
    
    private int catnipExplosionCooldown = 0;
    private int laserPointerCooldown = 0;
    private int nineLivesCooldown = 0;
    
    
    private boolean enemyTurnSkipped = false;
    
    
    private boolean enemyShipsDistracted = false;
    private int distractedTurns = 0;
    
    
    private int reviveUses = 0;
    
    
    private String[] catSounds = {
        "Meow! 🐱", "Purrr... 🐈", "Hiss! 🐾", "Mrrow? 😸", 
        "MEEOOW! 🐱", "*knocks something off table*", 
        "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾",
        "*chases laser pointer*", "*falls off chair*", "*ignores you*"
    };
    
    
    private Board playerBoardRef;
    
    public Skye() {
        super(
            "Skye — The Crazy Cat Lady",
            "Runs the largest cat rescue shelter. Her cats fight back!",
            2050,
            100,
            new Color(255, 165, 0)  
        );
        this.currentMana = MAX_MANA;
        this.abilityName = "Cat Chaos";
        this.abilityDescription = "Uses mana to explode catnip, distract enemies, and revive fallen ships.";
    }
    
    public void setPlayerBoard(Board board) {
        this.playerBoardRef = board;
    }
    
    
    
    public int getCurrentMana() { return currentMana; }
    public int getMaxMana() { return MAX_MANA; }
    public boolean hasEnoughMana(int cost) { return currentMana >= cost; }
    
    public void spendMana(int cost) {
        if (hasEnoughMana(cost)) {
            currentMana -= cost;
            System.out.println("🐱 Skye spent " + cost + " mana. Remaining: " + currentMana);
        }
    }
    
    public void regenerateMana(int amount) {
        currentMana += amount;
        if (currentMana > MAX_MANA) {
            currentMana = MAX_MANA;
        }
        System.out.println("🐱 Skye mana: " + currentMana + "/" + MAX_MANA);
    }
    
    
    
    
    public int useCatnipExplosion(Board enemyBoard, int centerX, int centerY) {
        if (catnipExplosionCooldown > 0) {
            System.out.println("⏳ Catnip Explosion is on cooldown for " + catnipExplosionCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughMana(70)) {
            System.out.println("⚠️ Not enough mana! Need 70 mana, have " + currentMana);
            return 0;
        }
        
        System.out.println("🌿 SKYE uses CATNIP EXPLOSION: \"It's just catnip... why are you acting so weird?\"");
        spendMana(70);
        
        
        int startX = centerX;
        int startY = centerY;
        int endX = Math.min(9, startX + 1);
        int endY = Math.min(9, startY + 1);
        
        int cellsDestroyed = 0;
        StringBuilder hitReport = new StringBuilder("🌿 Catnip Explosion hits:\n");
        
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                Cell cell = enemyBoard.getCell(x, y);
                if (!cell.isFiredUpon()) {
                    ShotResult result = enemyBoard.fire(x, y);
                    cellsDestroyed++;
                    if (cell.hasShip()) {
                        hitReport.append("   • Ship at (" + x + "," + y + ") destroyed! " + result + "\n");
                    } else {
                        hitReport.append("   • Cell (" + x + "," + y + ") filled with catnip\n");
                    }
                } else {
                    hitReport.append("   • Cell (" + x + "," + y + ") already hit\n");
                }
            }
        }
        
        System.out.println(hitReport.toString());
        
        
        enemyShipsDistracted = true;
        distractedTurns = 1;
        System.out.println("😵‍💫 Enemy ships are DISTRACTED by catnip!");
        System.out.println("Enemy ships: *purring sounds* *rolling around*");
        
        catnipExplosionCooldown = 3;
        return cellsDestroyed;
    }
    
    
    
    
    public boolean useLaserPointer() {
        if (laserPointerCooldown > 0) {
            System.out.println("⏳ Laser Pointer is on cooldown for " + laserPointerCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(50)) {
            System.out.println("⚠️ Not enough mana! Need 50 mana, have " + currentMana);
            return false;
        }
        
        System.out.println("🔴 SKYE uses LASER POINTER: \"Look! A red dot! ...Silly human.\"");
        spendMana(50);
        
        enemyTurnSkipped = true;
        System.out.println("😺 Enemy is chasing the laser pointer! They will skip their next turn!");
        
        laserPointerCooldown = 2;
        return true;
    }
    
    public boolean shouldSkipEnemyTurn() {
        if (enemyTurnSkipped) {
            enemyTurnSkipped = false;
            System.out.println("🔴 Enemy is still chasing that laser pointer! Turn skipped!");
            return true;
        }
        return false;
    }
    
    
    
    
    public boolean useNineLives(Board playerBoard, int targetX, int targetY) {
    if (nineLivesCooldown > 0) {
        System.out.println("⏳ Nine Lives is on cooldown for " + nineLivesCooldown + " more turns");
        return false;
    }
    
    if (!hasEnoughMana(200)) {
        System.out.println("⚠️ Not enough mana! Need 200 mana, have " + currentMana);
        return false;
    }
    
    Cell targetCell = playerBoard.getCell(targetX, targetY);
    
    if (!targetCell.hasShip()) {
        System.out.println("⚠️ No ship at that location!");
        return false;
    }
    
    Ship targetShip = targetCell.getShip();
    
    
    if (!targetShip.isSunk()) {
        System.out.println("⚠️ This ship is not sunk! It has " + targetShip.getRemainingHealth() + "/" + targetShip.getSize() + " segments remaining.");
        System.out.println("💡 Nine Lives only works on FULLY DESTROYED ships!");
        return false;
    }
    
    System.out.println("😺 SKYE uses NINE LIVES: \"Cats always land on their feet... even battleships?\"");
    spendMana(200);
    
    
    reviveFullShip(playerBoard, targetShip);
    
    System.out.println("😺 " + targetShip.getName() + " has been FULLY REVIVED with all " + targetShip.getSize() + " segments restored!");
    System.out.println(getRandomCatSound());
    System.out.println("✨ Nine Lives used: " + (++reviveUses) + "/9");
    
    nineLivesCooldown = 5;
    return true;
}

private void reviveFullShip(Board playerBoard, Ship ship) {
    
    ship.revive();
    
    
    for (Ship.Coordinate pos : ship.getPositions()) {
        int x = pos.getX();
        int y = pos.getY();
        Cell cell = playerBoard.getCell(x, y);
        
        
        cell.setFiredUpon(false);  
        cell.setHasShip(true);      
        cell.setShip(ship);         
        
        
        cell.setRevealed(false);
        
        System.out.println("😺 Cell (" + x + "," + y + ") has been revived and can be damaged again!");
    }
    
    System.out.println("😺 " + ship.getName() + " has been FULLY REVIVED with all " + ship.getSize() + " segments restored!");
}
    public int getReviveUses() {
    return reviveUses;
}
    
    public void updateTurnCounter() {
        
        if (catnipExplosionCooldown > 0) catnipExplosionCooldown--;
        if (laserPointerCooldown > 0) laserPointerCooldown--;
        if (nineLivesCooldown > 0) nineLivesCooldown--;
        
        
        if (enemyShipsDistracted) {
            distractedTurns--;
            if (distractedTurns <= 0) {
                enemyShipsDistracted = false;
                System.out.println("😺 Enemy ships are no longer distracted by catnip.");
            }
        }
        
        
        regenerateMana(15);
        
        
        if (random.nextInt(5) == 0) {
            System.out.println("🐱 " + getRandomCatSound());
        }
    }
    
    public boolean isEnemyDistracted() {
        return enemyShipsDistracted;
    }
    
    public int applyDamageReduction(int incomingDamage) {
        if (enemyShipsDistracted) {
            int reduced = incomingDamage / 2;
            System.out.println("😵 Catnip distraction reduced damage from " + incomingDamage + " to " + reduced);
            return reduced;
        }
        return incomingDamage;
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1: 
                if (catnipExplosionCooldown > 0) {
                    return "Cooldown: " + catnipExplosionCooldown + " turn" + (catnipExplosionCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(70)) {
                    return "Need 70 mana";
                } else {
                    return "Ready!";
                }
            case 2: 
                if (laserPointerCooldown > 0) {
                    return "Cooldown: " + laserPointerCooldown + " turn" + (laserPointerCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(50)) {
                    return "Need 50 mana";
                } else {
                    return "Ready!";
                }
            case 3: 
                if (nineLivesCooldown > 0) {
                    return "Cooldown: " + nineLivesCooldown + " turn" + (nineLivesCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(200)) {
                    return "Need 200 mana";
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
    
    public String getNineLivesDisplay() {
        return "Nine Lives: " + reviveUses + " used";
    }
    
    public String getRandomCatSound() {
        return catSounds[random.nextInt(catSounds.length)];
    }
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        System.out.println("Skye's abilities are used through skill buttons!");
        System.out.println(getRandomCatSound());
    }
}