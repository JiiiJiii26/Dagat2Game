package characters;

import models.Board;
import models.Ship;
import java.awt.Color;

/**
 * ABSTRACT CLASS - OOP CONCEPT DEMONSTRATION:
 * - ABSTRACTION: Defines common interface and behavior for all characters
 * - INHERITANCE: Base class that concrete character classes extend
 * - POLYMORPHISM: Abstract methods ensure consistent interface across different implementations
 * - ENCAPSULATION: Protected fields accessible only to subclasses, public methods for external access
 */
public abstract class GameCharacter {
    // ENCAPSULATION: Protected fields - accessible to subclasses but not external classes
    protected String name;
    protected String description;
    protected int maxHealth;
    protected int currentHealth;
    protected int specialMeter;
    protected int maxSpecialMeter;
    protected Color characterColor;
    protected String abilityName;
    protected String abilityDescription;
    protected Board board;
    
    public GameCharacter(String name, String description, int maxHealth, int maxSpecialMeter, Color color) {
        this.name = name;
        this.description = description;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.maxSpecialMeter = maxSpecialMeter;
        this.specialMeter = 0;
        this.characterColor = color;
    }

    /**
     * ABSTRACT METHOD - ABSTRACTION & POLYMORPHISM:
     * - ABSTRACTION: Hides implementation details, defines "what" should be done
     * - POLYMORPHISM: Each character subclass provides different implementation
     * - INTERFACE: Ensures all characters have this capability
     */
    public abstract void useSpecialAbility(Board playerBoard, Board enemyBoard);

    /**
     * ABSTRACT METHOD - POLYMORPHISM:
     * Each character implements skill usage differently (Jiji's tech skills vs Kael's shadow skills)
     * CampaignMode calls this on any GameCharacter reference - runtime binding determines behavior
     */
    public abstract boolean useSkill(int skillNumber, Board playerBoard, Board enemyBoard, int x, int y, boolean direction);

    /**
     * ABSTRACT METHOD - POLYMORPHISM:
     * Turn counter updates vary by character (cooldowns, passive effects, etc.)
     * Allows characters to have unique per-turn mechanics
     */
    public abstract void updateTurnCounter();

    /**
     * ABSTRACT METHOD - POLYMORPHISM:
     * Restores character-specific resources (mana, energy, etc.) to full between waves
     * Each character implements this differently based on their resource type
     */
    public abstract void restoreResources();
    
    public void chargeSpecial() {
        if (specialMeter < maxSpecialMeter) {
            specialMeter += 10;
            if (specialMeter > maxSpecialMeter) {
                specialMeter = maxSpecialMeter;
            }
        }
    }
    public void setBoard(Board board) {
    this.board = board;
}

public Board getBoard() {
    return board;
}
    
    public boolean canUseSpecial() {
        return specialMeter >= maxSpecialMeter;
    }
    
    public void resetSpecial() {
        specialMeter = 0;
    }
    
    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth < 0) currentHealth = 0;
    }
    
    public void heal(int amount) {
        currentHealth += amount;
        if (currentHealth > maxHealth) currentHealth = maxHealth;
    }
    
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public int getSpecialMeter() { return specialMeter; }
    public int getMaxSpecialMeter() { return maxSpecialMeter; }
    public Color getCharacterColor() { return characterColor; }
    public String getAbilityName() { return abilityName; }
    public String getAbilityDescription() { return abilityDescription; }
    
    public double getHealthPercentage() {
        return (double) currentHealth / maxHealth;
    }
    
    public double getSpecialPercentage() {
        return (double) specialMeter / maxSpecialMeter;
    }

    public boolean isDamaged() {
        return currentHealth < maxHealth;
    }
}
