package characters;

import models.Board;
import models.Cell;
import models.Ship;
import game.ShotResult;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Skye extends GameCharacter {
    
    private Random random = new Random();
    private int currentMana;
    private static final int MAX_MANA = 440;
    
    
    private int catSwarmCooldown = 0;
    private int laserPointerCooldown = 0;
    private int catnipExplosionCooldown = 0;
    
    
    private int nineLivesRemaining = 3;
    private Map<Ship, Boolean> shipsProtected = new HashMap<>();
    
    
    private boolean enemyTurnSkipped = false;
    
    
    private boolean enemyShipsDistracted = false;
    private int distractedTurns = 0;
    
    
    private ArrayList<String> shuffledShipPositions = new ArrayList<>();
    
    
    private String[] catSounds = {
        "Meow! 🐱", "Purrr... 🐈", "Hiss! 🐾", "Mrrow? 😸", 
        "MEEOOW! 🐱", "*knocks something off table*", 
        "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾",
        "*chases laser pointer*", "*falls off chair*", "*ignores you*"
    };
    
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
        this.abilityDescription = "Uses mana to confuse enemies with cats, lasers, and catnip!";
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
            System.out.println("💰 Skye spent " + cost + " mana. Remaining: " + currentMana);
        }
    }
    
    public void regenerateMana(int amount) {
        currentMana += amount;
        if (currentMana > MAX_MANA) {
            currentMana = MAX_MANA;
        }
    }
    
    
    
   public boolean tryProtectShip(Ship ship) {
    if (nineLivesRemaining > 0 && !shipsProtected.containsKey(ship) && ship.isSunk()) {
        
        nineLivesRemaining--;
        shipsProtected.put(ship, true);  
        
        
        
        System.out.println("😺 NINE LIVES! " + ship.getName() + " survives with 1 HP!");
        System.out.println(getRandomCatSound());
        System.out.println("Lives remaining: " + nineLivesRemaining);
        return true; 
    }
    return false; 
}
    
    public int getNineLivesRemaining() {
        return nineLivesRemaining;
    }
    
    
    
    
    public boolean useCatSwarm(Board enemyBoard) {
        if (catSwarmCooldown > 0) {
            System.out.println("⏳ Cat Swarm is on cooldown for " + catSwarmCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(70)) {
            System.out.println("⚠️ Not enough mana! Need 70 mana, have " + currentMana);
            return false;
        }
        
        System.out.println("🐱 SKYE uses CAT SWARM: \"Cats! Confuse them with your chaotic energy!\"");
        System.out.println(getRandomCatSound());
        spendMana(70);
        
        
        ArrayList<Ship> ships = enemyBoard.getShips();
        ArrayList<int[]> allPositions = new ArrayList<>();
        
        
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                
                
                for (int i = 0; i < ship.getSize(); i++) {
                    allPositions.add(new int[]{random.nextInt(10), random.nextInt(10)});
                }
            }
        }
        
        
        Collections.shuffle(allPositions);
        
        System.out.println("🔄 Cats are knocking ships around like toys!");
        System.out.println("🐈 Enemy ships have been randomly repositioned!");
        
        catSwarmCooldown = 3; 
        return true;
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
        
        System.out.println("🔴 SKYE uses LASER POINTER DISTRACTION: \"Look! A red dot! ...Silly human.\"");
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
    
    
    
    
    public int useCatnipExplosion(Board enemyBoard, int centerX, int centerY) {
        if (catnipExplosionCooldown > 0) {
            System.out.println("⏳ Catnip Explosion is on cooldown for " + catnipExplosionCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughMana(380)) {
            System.out.println("⚠️ Not enough mana! Need 380 mana, have " + currentMana);
            return 0;
        }
        
        System.out.println("🌿 SKYE uses CATNIP EXPLOSION: \"It's just catnip... why are you acting so weird?\"");
        spendMana(380);
        
        
        int minX = Math.max(0, centerX - 1);
        int maxX = Math.min(9, centerX);
        int minY = Math.max(0, centerY - 1);
        int maxY = Math.min(9, centerY);
        
        int cellsDestroyed = 0;
        
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Cell cell = enemyBoard.getCell(x, y);
                if (!cell.isFiredUpon()) {
                    ShotResult result = enemyBoard.fire(x, y);
                    cellsDestroyed++;
                    System.out.println("🌿 Catnip explosion destroyed cell (" + x + "," + y + ")");
                }
            }
        }
        
        
        enemyShipsDistracted = true;
        distractedTurns = 1; 
        System.out.println("😵‍💫 Enemy ships are DISTRACTED by catnip!");
        System.out.println("Enemy ships: *purring sounds* *rolling around*");
        
        catnipExplosionCooldown = 4; 
        return cellsDestroyed;
    }
    
    public int applyDamageReduction(int incomingDamage) {
        if (enemyShipsDistracted) {
            int reduced = incomingDamage / 2;
            System.out.println("😵 Catnip distraction reduced damage from " + incomingDamage + " to " + reduced);
            return reduced;
        }
        return incomingDamage;
    }
    
    public boolean isEnemyDistracted() {
        return enemyShipsDistracted;
    }
    
    
    
    public void updateTurnCounter() {
        
        if (catSwarmCooldown > 0) catSwarmCooldown--;
        if (laserPointerCooldown > 0) laserPointerCooldown--;
        if (catnipExplosionCooldown > 0) catnipExplosionCooldown--;
        
        
        regenerateMana(15);
        
        
        if (enemyShipsDistracted) {
            distractedTurns--;
            if (distractedTurns <= 0) {
                enemyShipsDistracted = false;
                System.out.println("😺 Enemy ships are no longer distracted by catnip.");
            }
        }
        
        
        if (random.nextInt(5) == 0) { 
            System.out.println("🐱 " + getRandomCatSound());
        }
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1: 
                if (catSwarmCooldown > 0) {
                    return "Cooldown: " + catSwarmCooldown + " turn" + (catSwarmCooldown > 1 ? "s" : "");
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
                if (catnipExplosionCooldown > 0) {
                    return "Cooldown: " + catnipExplosionCooldown + " turn" + (catnipExplosionCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(380)) {
                    return "Need 380 mana";
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
        StringBuilder lives = new StringBuilder("Nine Lives: ");
        for (int i = 0; i < 3; i++) {
            if (i < nineLivesRemaining) {
                lives.append("😺");
            } else {
                lives.append("💀");
            }
        }
        return lives.toString();
    }
    
    public String getRandomCatSound() {
        return catSounds[random.nextInt(catSounds.length)];
    }
    
    public boolean isEnemyTurnSkipped() {
        return enemyTurnSkipped;
    }
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        
        System.out.println("Skye's abilities are used through skill buttons!");
        System.out.println(getRandomCatSound());
    }
}