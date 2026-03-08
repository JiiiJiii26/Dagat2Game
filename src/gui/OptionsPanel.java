package gui;

import java.awt.*;
import javax.swing.*;

public class OptionsPanel extends JPanel {
    private JCheckBox soundCheckBox;
    private JCheckBox musicCheckBox;
    private JSlider difficultySlider;
    private JComboBox<String> themeComboBox;
    private JButton backButton;
    private OptionsListener listener;
    
    public interface OptionsListener {
        void onBackToMenu();
    }
    
    public OptionsPanel(OptionsListener listener) {
        this.listener = listener;
        
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 112));
        
        
        JLabel titleLabel = new JLabel("âš™ï¸ OPTIONS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(new Color(173, 216, 230));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBackground(new Color(25, 25, 112));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        JLabel soundLabel = new JLabel("Sound Effects:");
        soundLabel.setFont(new Font("Arial", Font.BOLD, 18));
        soundLabel.setForeground(Color.WHITE);
        optionsPanel.add(soundLabel, gbc);
        
        gbc.gridx = 1;
        soundCheckBox = new JCheckBox("Enable");
        soundCheckBox.setSelected(true);
        soundCheckBox.setBackground(new Color(25, 25, 112));
        soundCheckBox.setForeground(Color.WHITE);
        soundCheckBox.setFont(new Font("Arial", Font.PLAIN, 16));
        optionsPanel.add(soundCheckBox, gbc);
        
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel musicLabel = new JLabel("Background Music:");
        musicLabel.setFont(new Font("Arial", Font.BOLD, 18));
        musicLabel.setForeground(Color.WHITE);
        optionsPanel.add(musicLabel, gbc);
        
        gbc.gridx = 1;
        musicCheckBox = new JCheckBox("Enable");
        musicCheckBox.setSelected(true);
        musicCheckBox.setBackground(new Color(25, 25, 112));
        musicCheckBox.setForeground(Color.WHITE);
        musicCheckBox.setFont(new Font("Arial", Font.PLAIN, 16));
        optionsPanel.add(musicCheckBox, gbc);
        
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel difficultyLabel = new JLabel("Default AI:");
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        difficultyLabel.setForeground(Color.WHITE);
        optionsPanel.add(difficultyLabel, gbc);
        
        gbc.gridx = 1;
        String[] difficulties = {"Easy", "Medium", "Hard"};
        themeComboBox = new JComboBox<>(difficulties);
        themeComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        optionsPanel.add(themeComboBox, gbc);
        
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel themeLabel = new JLabel("Theme:");
        themeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        themeLabel.setForeground(Color.WHITE);
        optionsPanel.add(themeLabel, gbc);
        
        gbc.gridx = 1;
        String[] themes = {"Tidebound (Blue)", "Sunset (Orange)", "Night (Dark)"};
        JComboBox<String> themeBox = new JComboBox<>(themes);
        themeBox.setFont(new Font("Arial", Font.PLAIN, 16));
        optionsPanel.add(themeBox, gbc);
        
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(25, 25, 112));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 50, 0));
        
        backButton = new JButton("ðŸ”™ BACK TO MENU");
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> listener.onBackToMenu());
        bottomPanel.add(backButton);
        
        add(titleLabel, BorderLayout.NORTH);
        add(optionsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}

