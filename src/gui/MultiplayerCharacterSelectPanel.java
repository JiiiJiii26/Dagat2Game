package gui;

import characters.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class MultiplayerCharacterSelectPanel extends JPanel {
    
    private ArrayList<GameCharacter> characters;
    private GameCharacter player1Character;
    private GameCharacter player2Character;
    private CharacterSelectListener listener;
    
    private JPanel player1CardsPanel;
    private JPanel player2CardsPanel;
    private JButton confirmButton;
    private JLabel player1SelectedLabel;
    private JLabel player2SelectedLabel;
    private int currentPlayer = 1;
    
    public interface CharacterSelectListener {
        void onCharactersSelected(GameCharacter player1, GameCharacter player2);
        void onBackToMenu();
    }
    
    public MultiplayerCharacterSelectPanel(CharacterSelectListener listener) {
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
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        
        JButton backButton = new JButton("← BACK ");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(200, 60, 60));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(160, 40));
        backButton.addActionListener(e -> {
            if (listener != null) {
                listener.onBackToMenu();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);
        
        
        JLabel titleLabel = new JLabel("CHOOSE YOUR COMMANDERS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(173, 216, 230));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        
        JPanel rightPlaceholder = new JPanel();
        rightPlaceholder.setOpaque(false);
        rightPlaceholder.setPreferredSize(new Dimension(160, 40));
        topPanel.add(rightPlaceholder, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setBackground(new Color(25, 25, 112));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        
        JPanel player1Panel = createPlayerSelectionPanel("PLAYER 1", 1);
        JPanel player2Panel = createPlayerSelectionPanel("PLAYER 2", 2);
        
        centerPanel.add(player1Panel);
        centerPanel.add(player2Panel);
        add(centerPanel, BorderLayout.CENTER);
        
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(25, 25, 112));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 25, 0));
        
        confirmButton = new JButton("START BATTLE!");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 22));
        confirmButton.setBackground(new Color(70, 130, 180));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setEnabled(false);
        confirmButton.setFocusPainted(false);
        confirmButton.setPreferredSize(new Dimension(220, 50));
        confirmButton.addActionListener(e -> {
            if (player1Character != null && player2Character != null) {
                listener.onCharactersSelected(player1Character, player2Character);
            }
        });
        bottomPanel.add(confirmButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPlayerSelectionPanel(String playerName, int playerNumber) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 25, 112));
        panel.setBorder(BorderFactory.createLineBorder(
            playerNumber == 1 ? Color.GREEN : Color.RED, 3));
        
        
        JLabel nameLabel = new JLabel(playerName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(playerNumber == 1 ? Color.GREEN : Color.RED);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(nameLabel, BorderLayout.NORTH);
        
        
        JPanel cardsPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        cardsPanel.setBackground(new Color(25, 25, 112));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (GameCharacter character : characters) {
            JPanel card = createCharacterCard(character, playerNumber);
            cardsPanel.add(card);
        }
        
        
        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        
        JLabel selectedLabel = new JLabel("No character selected", SwingConstants.CENTER);
        selectedLabel.setFont(new Font("Arial", Font.BOLD, 12));
        selectedLabel.setForeground(Color.YELLOW);
        selectedLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        if (playerNumber == 1) {
            player1SelectedLabel = selectedLabel;
        } else {
            player2SelectedLabel = selectedLabel;
        }
        
        panel.add(selectedLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCharacterCard(GameCharacter character, int playerNumber) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(character.getCharacterColor().darker());
        card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        card.setPreferredSize(new Dimension(160, 130));
        
        
        JLabel nameLabel = new JLabel("<html><center>" + character.getName() + "</center></html>", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 10));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        card.add(nameLabel, BorderLayout.NORTH);
        
        
        JLabel emojiLabel = new JLabel(getCharacterEmoji(character), SwingConstants.CENTER);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        card.add(emojiLabel, BorderLayout.CENTER);
        
        
        JLabel abilityLabel = new JLabel("<html><center>" + character.getAbilityName() + "</center></html>", SwingConstants.CENTER);
        abilityLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        abilityLabel.setForeground(Color.LIGHT_GRAY);
        abilityLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        card.add(abilityLabel, BorderLayout.SOUTH);
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectCharacter(character, playerNumber);
                
                
                for (Component comp : card.getParent().getComponents()) {
                    if (comp instanceof JPanel) {
                        JPanel otherCard = (JPanel) comp;
                        otherCard.setBackground(((GameCharacter)getCharacterFromCard(otherCard)).getCharacterColor().darker());
                        otherCard.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                    }
                }
                card.setBackground(character.getCharacterColor().brighter());
                card.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isCharacterSelected(character, playerNumber)) {
                    card.setBackground(character.getCharacterColor());
                    card.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isCharacterSelected(character, playerNumber)) {
                    card.setBackground(character.getCharacterColor().darker());
                    card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                }
            }
        });
        
        return card;
    }
    
    private void selectCharacter(GameCharacter character, int playerNumber) {
        if (playerNumber == 1) {
            player1Character = character;
            if (player1SelectedLabel != null) {
                player1SelectedLabel.setText("Selected: " + character.getName());
                player1SelectedLabel.setForeground(Color.GREEN);
            }
        } else {
            player2Character = character;
            if (player2SelectedLabel != null) {
                player2SelectedLabel.setText("Selected: " + character.getName());
                player2SelectedLabel.setForeground(Color.GREEN);
            }
        }
        
        
        confirmButton.setEnabled(player1Character != null && player2Character != null);
        if (confirmButton.isEnabled()) {
            confirmButton.setBackground(new Color(50, 180, 80));
        }
    }
    
    private boolean isCharacterSelected(GameCharacter character, int playerNumber) {
        if (playerNumber == 1) {
            return player1Character == character;
        } else {
            return player2Character == character;
        }
    }
    
    private GameCharacter getCharacterFromCard(Component card) {
        if (card instanceof JPanel) {
            JPanel panel = (JPanel) card;
            Component[] components = panel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel) {
                    JLabel label = (JLabel) comp;
                    String text = label.getText();
                    if (text != null && !text.isEmpty() && !text.contains("<html>")) {
                        for (GameCharacter character : characters) {
                            if (character.getName().equals(text)) {
                                return character;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private String getCharacterEmoji(GameCharacter character) {
        if (character instanceof Jiji) return "💻";
        if (character instanceof Kael) return "🌑";
        if (character instanceof Valerius) return "🛡️";
        if (character instanceof Skye) return "🐱";
        if (character instanceof Morgana) return "🧜‍♀️";
        if (character instanceof Aeris) return "💪";
        if (character instanceof Selene) return "🔮";
        if (character instanceof Flue) return "💻";
        return "🎮";
    }
}