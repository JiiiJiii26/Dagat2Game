package characters;

import models.Board;
import models.Ship;
import java.awt.Color;

public abstract class GameCharacter {
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
    
    public abstract void useSpecialAbility(Board playerBoard, Board enemyBoard);
    
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
}
