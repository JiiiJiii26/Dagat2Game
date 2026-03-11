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
    private Map<String, Boolean> shipsProtected = new HashMap<>();
    
    
    private boolean enemyTurnSkipped = false;
    
    
    private boolean enemyShipsDistracted = false;
    private int distractedTurns = 0;
    
    
    private ArrayList<String> shuffledShipPositions = new ArrayList<>();
    
    
    private String[] catSounds = {
        "Meow! 🐱", "Purrr... 🐈", "Hiss! 🐾", "Mrrow? 😸", 
        "MEEOOW! 🐱", "*knocks something off table*", 
        "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾"
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
        }
    }
    
    public void regenerateMana(int amount) {
        currentMana += amount;
        if (currentMana > MAX_MANA) {
            currentMana = MAX_MANA;
        }
    }
    
    
    
    @Override
    public void takeDamage(int damage) {
        
        
        super.takeDamage(damage);
    }
    
    public boolean tryProtectShip(Ship ship, int damage) {
        String shipName = ship.getName();
        
        if (nineLivesRemaining > 0 && !shipsProtected.containsKey(shipName)) {
            
            
            if (ship.isSunk()) {
                nineLivesRemaining--;
                shipsProtected.put(shipName, true);
                System.out.println("😺 NINE LIVES! " + shipName + " survives with 1 HP!");
                System.out.println(catSounds[random.nextInt(catSounds.length)]);
                System.out.println("Lives remaining: " + nineLivesRemaining);
                return true; 
            }
        }
        return false; 
    }
    
    public int getNineLivesRemaining() {
        return nineLivesRemaining;
    }
    
    
    
    public boolean useCatSwarm(Board enemyBoard) {
        if (catSwarmCooldown > 0) {
            System.out.println("Cat Swarm is on cooldown for " + catSwarmCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(70)) {
            System.out.println("Not enough mana! Need 70 mana, have " + currentMana);
            return false;
        }
        
        System.out.println("🐱 SKYE uses CAT SWARM: \"Cats! Confuse them with your chaotic energy!\"");
        System.out.println(catSounds[random.nextInt(catSounds.length)]);
        spendMana(70);
        
        
        ArrayList<String> shipPositions = new ArrayList<>();
        ArrayList<Ship> ships = enemyBoard.getShips();
        
        for (Ship ship : ships) {
            
            
            System.out.println("🐈 Cats knocking ships around like toys!");
        }
        
        
        System.out.println("🔄 Enemy ships are being RANDOMIZED!");
        
        catSwarmCooldown = 3; 
        return true;
    }
    
    
    
    public boolean useLaserPointer() {
        if (laserPointerCooldown > 0) {
            System.out.println("Laser Pointer is on cooldown for " + laserPointerCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(50)) {
            System.out.println("Not enough mana! Need 50 mana, have " + currentMana);
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
            return true;
        }
        return false;
    }
    
    
    
    public int useCatnipExplosion(Board enemyBoard, int targetX, int targetY) {
        if (catnipExplosionCooldown > 0) {
            System.out.println("Catnip Explosion is on cooldown for " + catnipExplosionCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughMana(380)) {
            System.out.println("Not enough mana! Need 380 mana, have " + currentMana);
            return 0;
        }
        
        System.out.println("🌿 SKYE uses CATNIP EXPLOSION: \"It's just catnip... why are you acting so weird?\"");
        spendMana(380);
        
        
        int damage = random.nextInt(151) + 200; 
        System.out.println("💥 Catnip explosion deals " + damage + " damage!");
        
        
        enemyShipsDistracted = true;
        distractedTurns = 1; 
        System.out.println("😵‍💫 Enemy ships are DISTRACTED! They deal 50% less damage next turn!");
        System.out.println("Enemy ships: *purring sounds*");
        
        catnipExplosionCooldown = 4; 
        return damage;
    }
    
    public int applyDamageReduction(int incomingDamage) {
        if (enemyShipsDistracted) {
            int reduced = incomingDamage / 2;
            System.out.println("😵 Catnip distraction reduced damage from " + incomingDamage + " to " + reduced);
            return reduced;
        }
        return incomingDamage;
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
        
        
        if (random.nextInt(10) == 0) {
            System.out.println(catSounds[random.nextInt(catSounds.length)]);
        }
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1: 
                if (catSwarmCooldown > 0) {
                    return "Cooldown: " + catSwarmCooldown + " turns";
                } else if (!hasEnoughMana(70)) {
                    return "Need 70 mana";
                } else {
                    return "Ready!";
                }
            case 2: 
                if (laserPointerCooldown > 0) {
                    return "Cooldown: " + laserPointerCooldown + " turns";
                } else if (!hasEnoughMana(50)) {
                    return "Need 50 mana";
                } else {
                    return "Ready!";
                }
            case 3: 
                if (catnipExplosionCooldown > 0) {
                    return "Cooldown: " + catnipExplosionCooldown + " turns";
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
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        
        System.out.println("Skye's abilities are used through skill buttons!");
        System.out.println(getRandomCatSound());
    }
}
