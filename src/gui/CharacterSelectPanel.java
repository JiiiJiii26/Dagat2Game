package gui;

import characters.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class CharacterSelectPanel extends JPanel {
    
    private ArrayList<GameCharacter> characters;
    private GameCharacter selectedCharacter;
    private CharacterSelectListener listener;
    
    private JPanel cardsPanel;
    private JButton selectButton;
    private JLabel statsLabel;
    
    public interface CharacterSelectListener {
        void onCharacterSelected(GameCharacter character);
    }
    
    public CharacterSelectPanel(CharacterSelectListener listener) {
        this.listener = listener;
        this.characters = new ArrayList<>();
        
        
        characters.add(new Jiji());
         characters.add(new Kael()); 
       characters.add(new Valerius());
        characters.add(new Skye());
        characters.add(new Morgana());
        characters.add(new Aeris());
       characters.add(new Selene());  
       characters.add(new Flue()); 

        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 112));
        
        
        JLabel titleLabel = new JLabel("CHOOSE YOUR COMMANDER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(173, 216, 230));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        
        cardsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        cardsPanel.setBackground(new Color(25, 25, 112));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        for (GameCharacter character : characters) {
            cardsPanel.add(createCharacterCard(character));
        }
        
        add(cardsPanel, BorderLayout.CENTER);
        
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(25, 25, 112));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        statsLabel = new JLabel("Select a character to view details", SwingConstants.CENTER);
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statsLabel.setForeground(Color.WHITE);
        bottomPanel.add(statsLabel, BorderLayout.CENTER);
        
        selectButton = new JButton("SELECT COMMANDER");
        selectButton.setFont(new Font("Arial", Font.BOLD, 20));
        selectButton.setBackground(new Color(70, 130, 180));
        selectButton.setForeground(Color.WHITE);
        selectButton.setEnabled(false);
        selectButton.addActionListener(e -> {
            if (selectedCharacter != null) {
                listener.onCharacterSelected(selectedCharacter);
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(25, 25, 112));
        buttonPanel.add(selectButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createCharacterCard(GameCharacter character) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(character.getCharacterColor().darker());
        card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        
        JLabel nameLabel = new JLabel(character.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        card.add(nameLabel, BorderLayout.NORTH);
        
        
        JPanel statsPanel = new JPanel(new GridLayout(2, 1));
        statsPanel.setBackground(character.getCharacterColor().darker());
        
        JPanel healthBar = createStatBar("HP", character.getMaxHealth(), character.getMaxHealth(), Color.RED);
        statsPanel.add(healthBar);
        
        JPanel specialBar = createStatBar("SP", character.getMaxSpecialMeter(), character.getMaxSpecialMeter(), Color.YELLOW);
        statsPanel.add(specialBar);
        
        card.add(statsPanel, BorderLayout.CENTER);
        
        
        JTextArea abilityArea = new JTextArea(character.getAbilityName() + "\n" + character.getAbilityDescription());
        abilityArea.setEditable(false);
        abilityArea.setBackground(character.getCharacterColor());
        abilityArea.setForeground(Color.WHITE);
        abilityArea.setFont(new Font("Arial", Font.PLAIN, 12));
        abilityArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        abilityArea.setLineWrap(true);
        abilityArea.setWrapStyleWord(true);
        card.add(abilityArea, BorderLayout.SOUTH);
        
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectCharacter(character, card);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (selectedCharacter != character) {
                    card.setBackground(character.getCharacterColor());
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (selectedCharacter != character) {
                    card.setBackground(character.getCharacterColor().darker());
                }
            }
        });
        
        return card;
    }
    
    private JPanel createStatBar(String label, int current, int max, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 25, 112));
        
        JLabel labelComp = new JLabel(label + ": " + current + "/" + max);
        labelComp.setFont(new Font("Arial", Font.PLAIN, 12));
        labelComp.setForeground(Color.WHITE);
        panel.add(labelComp, BorderLayout.NORTH);
        
        JProgressBar bar = new JProgressBar(0, max);
        bar.setValue(current);
        bar.setForeground(color);
        bar.setBackground(Color.DARK_GRAY);
        bar.setStringPainted(false);
        panel.add(bar, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void selectCharacter(GameCharacter character, JPanel card) {
        selectedCharacter = character;
        selectButton.setEnabled(true);
        
        
        for (Component comp : cardsPanel.getComponents()) {
            comp.setBackground(((GameCharacter)getCharacterFromCard(comp)).getCharacterColor().darker());
        }
        card.setBackground(character.getCharacterColor().brighter());
        
        
        statsLabel.setText(String.format("%s - %s | HP: %d/%d | Special: %d/%d",
            character.getName(),
            character.getDescription(),
            character.getCurrentHealth(),
            character.getMaxHealth(),
            character.getSpecialMeter(),
            character.getMaxSpecialMeter()));
    }
    
    private GameCharacter getCharacterFromCard(Component card) {
        
        int index = cardsPanel.getComponentZOrder(card);
        return characters.get(index);
    }
}
