package characters;

import models.Board;
import models.Cell;
import models.Ship;
import game.ShotResult;
import java.awt.Color;
import java.util.Random;

public class Aeris extends GameCharacter {
    
    private Random random = new Random();
    private int currentMana;
    private static final int MAX_MANA = 600;
    
    
    private int adaptiveInstinctCooldown = 0;
    private int multitaskOverdriveCooldown = 0;
    private int relentlessAscentCooldown = 0;
    
    
    private boolean damageReductionActive = false;
    private int damageReductionTurns = 0;
    private int consecutiveHits = 0;
    private boolean nextAttackBonus = false;
    
    
    private boolean overdriveActive = false;
    private int overdriveSpeedBonus = 0;
    private boolean extraActionAvailable = false;
    
    
    private boolean stunImmuneActive = false;
    private int stunImmuneTurns = 0;
    
    
    private int lastHitCount = 0;
    
    public Aeris() {
        super(
            "Aeris — The Adaptive Strategist",
            "A master of adaptation who grows stronger under pressure.",
            2600, 
            100,
            new Color(255, 215, 0)  
        );
        this.currentMana = MAX_MANA;
        this.abilityName = "Adaptive Strategy";
        this.abilityDescription = "Uses mana to adapt, overcome, and grow stronger under pressure.";
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
            System.out.println("💪 Aeris spent " + cost + " mana. Remaining: " + currentMana);
        }
    }
    
    public void regenerateMana(int amount) {
        currentMana += amount;
        if (currentMana > MAX_MANA) {
            currentMana = MAX_MANA;
        }
    }
    
    
    
    public boolean useAdaptiveInstinct() {
        if (adaptiveInstinctCooldown > 0) {
            System.out.println("⏳ Adaptive Instinct is on cooldown for " + adaptiveInstinctCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(120)) {
            System.out.println("⚠️ Not enough mana! Need 120 mana, have " + currentMana);
            return false;
        }
        
        System.out.println("🛡️ AERIS uses ADAPTIVE INSTINCT: \"Pressure only makes me stronger!\"");
        spendMana(120);
        
        
        damageReductionActive = true;
        damageReductionTurns = 3;
        consecutiveHits = 0;
        
        System.out.println("🛡️ Damage reduced by 30% for 3 turns!");
        System.out.println("⚔️ Getting hit twice in a row will grant bonus damage!");
        
        adaptiveInstinctCooldown = 2; 
        return true;
    }
    
    public int applyDamageReduction(int incomingDamage) {
        if (damageReductionActive) {
            int reduced = (int)(incomingDamage * 0.7); 
            int blocked = incomingDamage - reduced;
            System.out.println("🛡️ Adaptive Instinct reduced damage by " + blocked + "!");
            return reduced;
        }
        return incomingDamage;
    }
    
    public void recordHit() {
        consecutiveHits++;
        if (consecutiveHits >= 2) {
            nextAttackBonus = true;
            System.out.println("⚔️ ADAPTIVE INSTINCT TRIGGERED! Next attack gains +150 bonus damage!");
            consecutiveHits = 0;
        }
    }
    
    public void resetConsecutiveHits() {
        consecutiveHits = 0;
    }
    
    public int applyBonusDamage(int baseDamage) {
        if (nextAttackBonus) {
            nextAttackBonus = false;
            int bonus = 150;
            System.out.println("⚔️ Bonus damage added: +" + bonus + "!");
            return baseDamage + bonus;
        }
        return baseDamage;
    }
    
    
    
    public boolean useMultitaskOverdrive() {
        if (multitaskOverdriveCooldown > 0) {
            System.out.println("⏳ Multitask Overdrive is on cooldown for " + multitaskOverdriveCooldown + " more turns");
            return false;
        }
        
        if (!hasEnoughMana(180)) {
            System.out.println("⚠️ Not enough mana! Need 180 mana, have " + currentMana);
            return false;
        }
        
        System.out.println("⚡ AERIS uses MULTITASK OVERDRIVE: \"I can do both!\"");
        spendMana(180);
        
        
        overdriveActive = true;
        overdriveSpeedBonus = 20;
        extraActionAvailable = true;
        
        System.out.println("⚡ Speed increased by 20 for this turn!");
        System.out.println("⚡ Extra action available! You can do two things this turn!");
        
        multitaskOverdriveCooldown = 3; 
        return true;
    }
    
    public boolean hasExtraAction() {
        return extraActionAvailable;
    }
    
    public void consumeExtraAction() {
        extraActionAvailable = false;
        overdriveActive = false;
        overdriveSpeedBonus = 0;
    }
    
    public int getSpeedBonus() {
        return overdriveSpeedBonus;
    }
    
    
    
    public int useRelentlessAscent(Board enemyBoard, int targetX, int targetY) {
        if (relentlessAscentCooldown > 0) {
            System.out.println("⏳ Relentless Ascent is on cooldown for " + relentlessAscentCooldown + " more turns");
            return 0;
        }
        
        if (!hasEnoughMana(250)) {
            System.out.println("⚠️ Not enough mana! Need 250 mana, have " + currentMana);
            return 0;
        }
        
        System.out.println("⚔️ AERIS uses RELENTLESS ASCENT: \"My pain is my power!\"");
        spendMana(250);
        
        
        int missingHP = maxHealth - currentHealth;
        double missingPercent = (double) missingHP / maxHealth;
        
        
        int baseDamage = random.nextInt(301) + 500; 
        int bonusDamage = (int)(baseDamage * missingPercent);
        int totalDamage = baseDamage + bonusDamage;
        
        System.out.println("💥 Base damage: " + baseDamage);
        System.out.println("📈 Bonus from missing HP (" + missingHP + "/" + maxHealth + "): +" + bonusDamage);
        System.out.println("💥 Total damage: " + totalDamage);
        
        
        ShotResult result = enemyBoard.fire(targetX, targetY);
        System.out.println("Result: " + result);
        
        
        double healthPercent = (double) currentHealth / maxHealth;
        if (healthPercent < 0.4) {
            stunImmuneActive = true;
            stunImmuneTurns = 2;
            System.out.println("⚡ STUN IMMUNE ACTIVATED! Aeris cannot be stunned for 2 turns!");
        }
        
        relentlessAscentCooldown = 4; 
        return totalDamage;
    }
    
    public boolean isStunImmune() {
        return stunImmuneActive;
    }
    
    
    
    public void updateTurnCounter() {
        
        if (adaptiveInstinctCooldown > 0) {
            adaptiveInstinctCooldown--;
        }
        if (multitaskOverdriveCooldown > 0) {
            multitaskOverdriveCooldown--;
        }
        if (relentlessAscentCooldown > 0) {
            relentlessAscentCooldown--;
        }
        
        
        if (damageReductionActive) {
            damageReductionTurns--;
            if (damageReductionTurns <= 0) {
                damageReductionActive = false;
                System.out.println("🛡️ Adaptive Instinct has faded.");
            }
        }
        
        
        if (stunImmuneActive) {
            stunImmuneTurns--;
            if (stunImmuneTurns <= 0) {
                stunImmuneActive = false;
                System.out.println("⚡ Stun immunity has faded.");
            }
        }
        
        
        if (overdriveActive && !extraActionAvailable) {
            overdriveActive = false;
            overdriveSpeedBonus = 0;
        }
        
        
        if (consecutiveHits > 0 && !nextAttackBonus) {
            consecutiveHits = 0;
        }
        
        
        regenerateMana(15);
    }
    
    
    
    public String getSkillStatus(int skillNum) {
        switch(skillNum) {
            case 1: 
                if (adaptiveInstinctCooldown > 0) {
                    return "Cooldown: " + adaptiveInstinctCooldown + " turn" + (adaptiveInstinctCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(120)) {
                    return "Need 120 mana";
                } else {
                    return "Ready!";
                }
            case 2: 
                if (multitaskOverdriveCooldown > 0) {
                    return "Cooldown: " + multitaskOverdriveCooldown + " turn" + (multitaskOverdriveCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(180)) {
                    return "Need 180 mana";
                } else {
                    return "Ready!";
                }
            case 3: 
                if (relentlessAscentCooldown > 0) {
                    return "Cooldown: " + relentlessAscentCooldown + " turn" + (relentlessAscentCooldown > 1 ? "s" : "");
                } else if (!hasEnoughMana(250)) {
                    return "Need 250 mana";
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
    
    public boolean isDamageReductionActive() {
        return damageReductionActive;
    }
    
    public boolean isStunImmuneActive() {
        return stunImmuneActive;
    }
    
    public boolean isExtraActionAvailable() {
        return extraActionAvailable;
    }
    
    public int getConsecutiveHits() {
        return consecutiveHits;
    }
    
    @Override
    public void useSpecialAbility(Board playerBoard, Board enemyBoard) {
        
        System.out.println("Aeris's abilities are used through skill buttons!");
    }
}