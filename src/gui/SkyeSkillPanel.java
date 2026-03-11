package gui;

import characters.Skye;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import javax.swing.border.TitledBorder;

public class SkyeSkillPanel extends JPanel {
    
    private Skye skye;
    private JButton skill1Button;
    private JButton skill2Button;
    private JButton skill3Button;
    private JLabel manaLabel;
    private JLabel nineLivesLabel;
    private JLabel catSoundLabel;
    private Timer catSoundTimer;
    
    public SkyeSkillPanel(Skye skye, ActionListener skill1Listener, 
                          ActionListener skill2Listener, ActionListener skill3Listener) {
        this.skye = skye;
        
        setLayout(new GridBagLayout());
        setBackground(new Color(255, 140, 0, 50)); 
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 165, 0), 3),
            "😺 SKYE - CRAZY CAT LADY 😺",
            TitledBorder.CENTER, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16),
            new Color(255, 200, 0)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        
        catSoundLabel = new JLabel("😺 Meow!", SwingConstants.CENTER);
        catSoundLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        catSoundLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(catSoundLabel, gbc);
        
        
        manaLabel = new JLabel(skye.getManaBar(), SwingConstants.CENTER);
        manaLabel.setFont(new Font("Arial", Font.BOLD, 12));
        manaLabel.setForeground(Color.CYAN);
        gbc.gridy = 1;
        add(manaLabel, gbc);
        
        
        nineLivesLabel = new JLabel(skye.getNineLivesDisplay(), SwingConstants.CENTER);
        nineLivesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nineLivesLabel.setForeground(Color.PINK);
        gbc.gridy = 2;
        add(nineLivesLabel, gbc);
        
        
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(createSkillLabel("🐱 CAT SWARM", new Color(255, 200, 0)), gbc);
        
        JPanel skill1Panel = new JPanel(new FlowLayout());
        skill1Panel.setBackground(new Color(255, 140, 0, 0));
        skill1Button = createSkillButton("USE", new Color(255, 165, 0), skill1Listener);
        skill1Panel.add(skill1Button);
        gbc.gridx = 1;
        add(skill1Panel, gbc);
        
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(createSkillLabel("🔴 LASER POINTER", new Color(255, 100, 100)), gbc);
        
        JPanel skill2Panel = new JPanel(new FlowLayout());
        skill2Panel.setBackground(new Color(255, 140, 0, 0));
        skill2Button = createSkillButton("USE", new Color(255, 69, 0), skill2Listener);
        skill2Panel.add(skill2Button);
        gbc.gridx = 1;
        add(skill2Panel, gbc);
        
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(createSkillLabel("🌿 CATNIP EXPLOSION", new Color(0, 255, 0)), gbc);
        
        JPanel skill3Panel = new JPanel(new FlowLayout());
        skill3Panel.setBackground(new Color(255, 140, 0, 0));
        skill3Button = createSkillButton("USE", new Color(50, 205, 50), skill3Listener);
        skill3Panel.add(skill3Button);
        gbc.gridx = 1;
        add(skill3Panel, gbc);
        
        
        JLabel catFact = new JLabel("Cats have 9 lives... so do Skye's ships!", SwingConstants.CENTER);
        catFact.setFont(new Font("Arial", Font.PLAIN, 10));
        catFact.setForeground(Color.LIGHT_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(catFact, gbc);
        
        
        catSoundTimer = new Timer(8000, e -> {
            catSoundLabel.setText("😺 " + skye.getRandomCatSound());
        });
        catSoundTimer.start();
        
        updateUI();
    }
    
    private JLabel createSkillLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(color);
        return label;
    }
    
    private JButton createSkillButton(String text, Color color, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 10));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.addActionListener(listener);
        button.setPreferredSize(new Dimension(60, 25));
        return button;
    }
    
    public void updateUI() {
        manaLabel.setText(skye.getManaBar());
        nineLivesLabel.setText(skye.getNineLivesDisplay());
        
        
        String status1 = skye.getSkillStatus(1);
        skill1Button.setEnabled(status1.equals("Ready!"));
        skill1Button.setToolTipText(status1);
        
        
        String status2 = skye.getSkillStatus(2);
        skill2Button.setEnabled(status2.equals("Ready!"));
        skill2Button.setToolTipText(status2);
        
        
        String status3 = skye.getSkillStatus(3);
        skill3Button.setEnabled(status3.equals("Ready!"));
        skill3Button.setToolTipText(status3);
        
        repaint();
    }
    
    public void stopCatSounds() {
        if (catSoundTimer != null) {
            catSoundTimer.stop();
        }
    }
}
