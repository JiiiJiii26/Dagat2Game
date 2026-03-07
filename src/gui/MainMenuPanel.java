package gui;

import java.awt.*;
import javax.swing.*;



public class MainMenuPanel extends JPanel {
    private JButton startButton;
    private JButton vsButton;
    private JButton optionsButton;
    private JButton exitButton;
    private final MenuListener listener;  
    
    public interface MenuListener {
        void onStartGame();
        void on1v1Mode();
        void onOptions();
        void onExit();
    }
    
    public MainMenuPanel(MenuListener listener) {
        this.listener = listener;
        
        
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 112)); 
        
        
        JPanel titlePanel = createTitlePanel();
        
        
        JPanel buttonPanel = createButtonPanel();
        
        
        JPanel footerPanel = createFooterPanel();
        
        
        add(titlePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(25, 25, 112));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        
        
        JLabel titleLabel = new JLabel(" TIDEBOUND ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 64));
        titleLabel.setForeground(new Color(173, 216, 230)); 
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        
        JLabel subtitleLabel = new JLabel("Naval Battle Strategy");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        
        JLabel waveLabel = new JLabel("~ ~ ~ ~ ~ ~ ~ ~ ~ ~");
        waveLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        waveLabel.setForeground(new Color(64, 128, 191));
        waveLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(waveLabel);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(25, 25, 112));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 100, 10, 100);
        
        
        startButton = createMenuButton("🚢 START GAME", new Color(46, 125, 50));
        vsButton = createMenuButton("⚔️ 1v1 MULTIPLAYER", new Color(255, 140, 0));
        optionsButton = createMenuButton("⚙️ OPTIONS", new Color(70, 130, 180));
        exitButton = createMenuButton("🚪 EXIT", new Color(178, 34, 34));
        
        
        startButton.addActionListener(e -> listener.onStartGame());
        vsButton.addActionListener(e -> listener.on1v1Mode());
        optionsButton.addActionListener(e -> listener.onOptions());
        exitButton.addActionListener(e -> listener.onExit());
        
        
        panel.add(startButton, gbc);
        panel.add(Box.createRigidArea(new Dimension(0, 15)), gbc);
        panel.add(vsButton, gbc);
        panel.add(Box.createRigidArea(new Dimension(0, 15)), gbc);
        panel.add(optionsButton, gbc);
        panel.add(Box.createRigidArea(new Dimension(0, 15)), gbc);
        panel.add(exitButton, gbc);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(25, 25, 112));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        
        JLabel versionLabel = new JLabel();
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        versionLabel.setForeground(new Color(173, 216, 230));
        
        JLabel creditLabel = new JLabel("Made by Team Omen");
        creditLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        creditLabel.setForeground(Color.GRAY);
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(versionLabel);
        panel.add(creditLabel);
        
        return panel;
    }
    
    private JButton createMenuButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24)); 
         button.setFont(new Font("Arial Unicode MS", Font.BOLD, 24));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            button.setBackground(bgColor.brighter());
            button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 26));
        }
        
        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            button.setBackground(bgColor);
            button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        }
    });
    
    return button;
}
}

