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
        void onBackToMenu();
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
        
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(25, 25, 112));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        
        
        JButton backButton = new JButton("← BACK ");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(200, 60, 60));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.addActionListener(e -> {
            if (listener != null) {
                listener.onBackToMenu();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);
        
        
        JLabel titleLabel = new JLabel("CHOOSE YOUR COMMANDER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(173, 216, 230));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        
        JPanel rightPlaceholder = new JPanel();
        rightPlaceholder.setOpaque(false);
        rightPlaceholder.setPreferredSize(new Dimension(150, 40));
        topPanel.add(rightPlaceholder, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        
        cardsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        cardsPanel.setBackground(new Color(25, 25, 112));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        for (GameCharacter character : characters) {
            cardsPanel.add(createCharacterCard(character));
        }
        
        
        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
        
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(25, 25, 112));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 25, 20));
        
        statsLabel = new JLabel("Select a character to view details", SwingConstants.CENTER);
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statsLabel.setForeground(Color.WHITE);
        bottomPanel.add(statsLabel, BorderLayout.CENTER);
        
        selectButton = new JButton("SELECT COMMANDER");
        selectButton.setFont(new Font("Arial", Font.BOLD, 18));
        selectButton.setBackground(new Color(70, 130, 180));
        selectButton.setForeground(Color.WHITE);
        selectButton.setEnabled(false);
        selectButton.setFocusPainted(false);
        selectButton.setPreferredSize(new Dimension(200, 45));
        selectButton.addActionListener(e -> {
            if (selectedCharacter != null) {
                listener.onCharacterSelected(selectedCharacter);
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
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
        card.setPreferredSize(new Dimension(200, 220));
        
        
        JLabel nameLabel = new JLabel("<html><center>" + character.getName() + "</center></html>", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        card.add(nameLabel, BorderLayout.NORTH);
        
        
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statsPanel.setBackground(character.getCharacterColor().darker());
        statsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JPanel healthBar = createStatBar("HP", character.getMaxHealth(), character.getMaxHealth(), Color.RED);
        statsPanel.add(healthBar);
        
        JPanel specialBar = createStatBar("SP", character.getMaxSpecialMeter(), character.getMaxSpecialMeter(), Color.YELLOW);
        statsPanel.add(specialBar);
        
        card.add(statsPanel, BorderLayout.CENTER);
        
        
        JTextArea abilityArea = new JTextArea(character.getAbilityDescription());
        abilityArea.setEditable(false);
        abilityArea.setBackground(character.getCharacterColor());
        abilityArea.setForeground(Color.WHITE);
        abilityArea.setFont(new Font("Arial", Font.PLAIN, 10));
        abilityArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        abilityArea.setLineWrap(true);
        abilityArea.setWrapStyleWord(true);
        abilityArea.setPreferredSize(new Dimension(180, 60));
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
                    card.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (selectedCharacter != character) {
                    card.setBackground(character.getCharacterColor().darker());
                    card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                }
            }
        });
        
        return card;
    }
    
    private JPanel createStatBar(String label, int current, int max, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 0, 0, 100));
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        JLabel labelComp = new JLabel(label + ": " + current + "/" + max);
        labelComp.setFont(new Font("Arial", Font.PLAIN, 10));
        labelComp.setForeground(Color.WHITE);
        panel.add(labelComp, BorderLayout.NORTH);
        
        JProgressBar bar = new JProgressBar(0, max);
        bar.setValue(current);
        bar.setForeground(color);
        bar.setBackground(Color.DARK_GRAY);
        bar.setStringPainted(false);
        bar.setPreferredSize(new Dimension(100, 12));
        panel.add(bar, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void selectCharacter(GameCharacter character, JPanel card) {
        selectedCharacter = character;
        selectButton.setEnabled(true);
        
        
        for (Component comp : cardsPanel.getComponents()) {
            comp.setBackground(((GameCharacter)getCharacterFromCard(comp)).getCharacterColor().darker());
            ((JPanel)comp).setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        }
        card.setBackground(character.getCharacterColor().brighter());
        card.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
        
        statsLabel.setText(String.format("<html><center>%s - %s<br>HP: %d/%d | Special: %d/%d</center></html>",
            character.getName(),
            character.getDescription(),
            character.getCurrentHealth(),
            character.getMaxHealth(),
            character.getSpecialMeter(),
            character.getMaxSpecialMeter()));
    }
    
    private GameCharacter getCharacterFromCard(Component card) {
        int index = cardsPanel.getComponentZOrder(card);
        if (index >= 0 && index < characters.size()) {
            return characters.get(index);
        }
        return null;
    }
}