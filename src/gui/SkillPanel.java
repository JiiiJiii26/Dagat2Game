package gui;

import characters.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import models.Board;    
import javax.swing.border.TitledBorder;    
import game.ShotResult;      

public class SkillPanel extends JPanel {
    
    private GameCharacter character;
    private JButton skill1Button;
    private JButton skill2Button;
    private JButton skill3Button;
    private JButton ultimateButton; 
    private JLabel resourceLabel;
    private JLabel passiveLabel;
    private JLabel characterNameLabel;
    
    
    
    private JPanel jijiPanel;
    private JPanel kaelPanel;
    private JPanel valeriusPanel;
    private JPanel skyePanel;
    
    private Timer updateTimer;
    
    public SkillPanel(GameCharacter character) {
        this.character = character;
        
        setLayout(new BorderLayout());
       
        
       
       
      
     
     
    
        
        
        characterNameLabel = new JLabel(character.getName(), SwingConstants.CENTER);
        characterNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        characterNameLabel.setForeground(getCharacterColor());
        characterNameLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(characterNameLabel, BorderLayout.NORTH);
        
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(25, 25, 112));
        
        
        resourceLabel = new JLabel(getResourceText(), SwingConstants.CENTER);
        resourceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        resourceLabel.setForeground(getResourceColor());
        resourceLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        mainPanel.add(resourceLabel, gbc);
        
        
        passiveLabel = new JLabel(getPassiveText(), SwingConstants.CENTER);
        passiveLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        passiveLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 1;
        mainPanel.add(passiveLabel, gbc);
        
        
        if (character instanceof Jiji) {
            addJijiSkills(mainPanel, gbc);
        } else if (character instanceof Kael) {
            addKaelSkills(mainPanel, gbc);
        } else if (character instanceof Valerius) {
            addValeriusSkills(mainPanel, gbc);
        } else if (character instanceof Skye) {
            addSkyeSkills(mainPanel, gbc);
        } else {
            addGenericSkills(mainPanel, gbc);
        }
        
        add(mainPanel, BorderLayout.CENTER);
        
        
        updateTimer = new Timer(1000, e -> updateUI());
        updateTimer.start();
    }
    
    
    
    private void addJijiSkills(JPanel panel, GridBagConstraints gbc) {
        Jiji jiji = (Jiji) character;
        
        
        addSkillRow(panel, gbc, 2, 
            "🔓 DATA LEECH", 
            "50 mana - Reveal 2 cells",
            new Color(100, 200, 255),
            e -> {
                if (jiji.useDataLeech(getEnemyBoard())) {
                    showMessage("Data Leech used! Cells revealed!");
                }
            });
        
        
        addSkillRow(panel, gbc, 3,
            "⚡ OVERCLOCK",
            "120 mana - Double shot next turn",
            new Color(200, 150, 50),
            e -> {
                if (jiji.useOverclock()) {
                    showMessage("Overclock activated! Next shot fires twice!");
                }
            });
        
        
        addSkillRow(panel, gbc, 4,
            "💻 SYSTEM OVERLOAD",
            "400 mana - Disable enemy skill",
            new Color(200, 50, 50),
            e -> {
                if (jiji.useSystemOverload(getEnemyBoard())) {
                    showMessage("System Overload! Enemy skill disabled!");
                }
            });
    }
    
   private void addKaelSkills(JPanel panel, GridBagConstraints gbc) {
    Kael kael = (Kael) character;
    
    // Shadow Veil
    gbc.gridy++;
    JLabel veilLabel = new JLabel("🌑 SHADOW VEIL");
    veilLabel.setFont(new Font("Arial", Font.BOLD, 11));
    veilLabel.setForeground(new Color(75, 0, 130));
    panel.add(veilLabel, gbc);
    
    gbc.gridy++;
    JButton veilBtn = new JButton("USE (80 energy)");
    veilBtn.setBackground(new Color(75, 0, 130));
    veilBtn.setForeground(Color.WHITE);
    veilBtn.addActionListener(e -> {
        boolean used = kael.useShadowVeil();
        if (used) {
            showMessage("🌑 Shadow Veil! One ship is hidden!");
        } else {
            showMessage("❌ Cannot use Shadow Veil!");
        }
    });
    panel.add(veilBtn, gbc);
    
    // Shadow Strike
    gbc.gridy++;
    JLabel strikeLabel = new JLabel("⚔️ SHADOW STRIKE");
    strikeLabel.setFont(new Font("Arial", Font.BOLD, 11));
    strikeLabel.setForeground(new Color(100, 150, 255));
    panel.add(strikeLabel, gbc);
    
    gbc.gridy++;
    JButton strikeBtn = new JButton("USE (120 energy)");
    strikeBtn.setBackground(new Color(100, 150, 255));
    strikeBtn.setForeground(Color.BLACK);
    strikeBtn.addActionListener(e -> {
        boolean used = kael.useShadowStrike();
        if (used) {
            showMessage("⚔️ Shadow Strike ready! Next attack destroys 2 cells!");
        } else {
            showMessage("❌ Cannot use Shadow Strike!");
        }
    });
    panel.add(strikeBtn, gbc);
    
    // Shadow Realm
    gbc.gridy++;
    JLabel realmLabel = new JLabel("🌑🌑🌑 SHADOW REALM");
    realmLabel.setFont(new Font("Arial", Font.BOLD, 11));
    realmLabel.setForeground(new Color(50, 0, 100));
    panel.add(realmLabel, gbc);
    
    gbc.gridy++;
    JButton realmBtn = new JButton("USE (250 energy)");
    realmBtn.setBackground(new Color(50, 0, 100));
    realmBtn.setForeground(Color.WHITE);
    realmBtn.addActionListener(e -> {
        boolean used = kael.useShadowRealm();
        if (used) {
            showMessage("🌑🌑🌑 Shadow Realm active! All attacks destroy 2 cells for 2 turns!");
        } else {
            showMessage("❌ Cannot use Shadow Realm!");
        }
    });
    panel.add(realmBtn, gbc);
    
    // Energy display
    gbc.gridy++;
    JLabel energyLabel = new JLabel(kael.getEnergyBar(), SwingConstants.CENTER);
    energyLabel.setFont(new Font("Arial", Font.BOLD, 10));
    energyLabel.setForeground(new Color(100, 200, 255));
    panel.add(energyLabel, gbc);
}
    
    private void addValeriusSkills(JPanel panel, GridBagConstraints gbc) {
        Valerius valerius = (Valerius) character;
        
        
        addSkillRow(panel, gbc, 2,
            "📡 RADAR OVERLOAD",
            "50 mana - Disable enemy skills 2 turns",
            new Color(169, 169, 169),
            e -> {
                if (valerius.useRadarOverload()) {
                    showMessage("Radar Overload! Enemy skills disabled!");
                }
            });
        
        
        addSkillRow(panel, gbc, 3,
            "🛡️ KINETIC BARRIER",
            "90 mana - Shield 3x3 area",
            new Color(0, 255, 255),
            e -> {
                promptForTarget("Kinetic Barrier", (x, y) -> {
                    if (valerius.useKineticBarrier(getPlayerBoard(), x, y)) {
                        showMessage("Barrier deployed!");
                    }
                });
            });
        
        
        addSkillRow(panel, gbc, 4,
            "🎯 ORBITAL RAILGUN",
            "280 mana - Massive damage + scan",
            new Color(255, 69, 0),
            e -> {
                promptForTarget("Orbital Railgun", (x, y) -> {
                    ShotResult result = valerius.useOrbitalRailgun(getEnemyBoard(), x, y);
                    if (result != ShotResult.INVALID) {
                        showMessage("Railgun fired!");
                    }
                });
            });
    }
    
    private void addSkyeSkills(JPanel panel, GridBagConstraints gbc) {
        Skye skye = (Skye) character;
        
        
        JLabel nineLives = new JLabel(skye.getNineLivesDisplay(), SwingConstants.CENTER);
        nineLives.setFont(new Font("Arial", Font.BOLD, 14));
        nineLives.setForeground(Color.PINK);
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(nineLives, gbc);
        
        
        addSkillRow(panel, gbc, 3,
            "🐱 CAT SWARM",
            "70 mana - Shuffle enemy ships",
            new Color(255, 165, 0),
            e -> {
                if (skye.useCatSwarm(getEnemyBoard())) {
                    showMessage("Cat Swarm! Enemy ships scrambled!");
                }
            });
        
        
        addSkillRow(panel, gbc, 4,
            "🔴 LASER POINTER",
            "50 mana - Enemy skips turn",
            new Color(255, 100, 100),
            e -> {
                if (skye.useLaserPointer()) {
                    showMessage("Laser Pointer! Enemy distracted!");
                }
            });
        
        
        addSkillRow(panel, gbc, 5,
            "🌿 CATNIP EXPLOSION",
            "380 mana - Damage + debuff",
            new Color(50, 205, 50),
            e -> {
                promptForTarget("Catnip Explosion", (x, y) -> {
                    int damage = skye.useCatnipExplosion(getEnemyBoard(), x, y);
                    if (damage > 0) {
                        showMessage("Catnip Explosion dealt " + damage + " damage!");
                    }
                });
            });
        
        
        Timer catTimer = new Timer(5000, e -> {
            showMessage("😺 " + skye.getRandomCatSound());
        });
        catTimer.start();
    }
    
    private void addGenericSkills(JPanel panel, GridBagConstraints gbc) {
        
        addSkillRow(panel, gbc, 2,
            "⚔️ BASIC ATTACK",
            "No cost - Standard shot",
            Color.WHITE,
            e -> showMessage("Click on enemy board to fire!"));
    }
    
    private void addSkillRow(JPanel panel, GridBagConstraints gbc, int yPos, 
                            String name, String desc, Color color, ActionListener listener) {
        
        
        gbc.gridy = yPos;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 11));
        nameLabel.setForeground(color);
        panel.add(nameLabel, gbc);
        
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        
        JButton useButton = new JButton("USE");
        useButton.setFont(new Font("Arial", Font.BOLD, 10));
        useButton.setBackground(color);
        useButton.setForeground(Color.WHITE);
        useButton.addActionListener(listener);
        useButton.setPreferredSize(new Dimension(60, 25));
        panel.add(useButton, gbc);
        
        
        gbc.gridy = yPos + 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 8));
        descLabel.setForeground(Color.LIGHT_GRAY);
        panel.add(descLabel, gbc);
    }
    
    
    
    private Color getCharacterColor() {
        if (character instanceof Jiji) return new Color(100, 200, 255);
        if (character instanceof Kael) return new Color(75, 0, 130);
        if (character instanceof Valerius) return new Color(169, 169, 169);
        if (character instanceof Skye) return new Color(255, 165, 0);
        return Color.WHITE;
    }
    
    private Color getResourceColor() {
        if (character instanceof Jiji) return Color.CYAN;
        if (character instanceof Kael) return new Color(100, 200, 255);
        if (character instanceof Valerius) return Color.CYAN;
        if (character instanceof Skye) return Color.CYAN;
        return Color.WHITE;
    }
    
    private String getResourceText() {
        if (character instanceof Jiji) {
            Jiji j = (Jiji) character;
            return "⚡ Mana: " + j.getCurrentMana() + "/" + j.getMaxMana();
        }
        if (character instanceof Kael) {
            Kael k = (Kael) character;
            return "⚡ Energy: " + k.getCurrentEnergy() + "/" + k.getMaxEnergy();
        }
        if (character instanceof Valerius) {
            Valerius v = (Valerius) character;
            return "⚡ Mana: " + v.getCurrentMana() + "/" + v.getMaxMana();
        }
        if (character instanceof Skye) {
            Skye s = (Skye) character;
            return "⚡ Mana: " + s.getCurrentMana() + "/" + s.getMaxMana();
        }
        return "⚡ Ready!";
    }
    
    private String getPassiveText() {
        if (character instanceof Jiji) {
            return "Passive: Firewall - Blocks one hit every 4 turns";
        }
        if (character instanceof Kael) {
            return "Passive: Shadow Walk - Ships can hide";
        }
        if (character instanceof Valerius) {
            Valerius v = (Valerius) character;
            if (v.isScrapperResolveActive()) {
                return "Passive: Scrapper's Resolve ACTIVE - 10% damage reduction";
            }
            return "Passive: Scrapper's Resolve - Damage reduction below 20% HP";
        }
        if (character instanceof Skye) {
            Skye s = (Skye) character;
            return "Passive: Nine Lives - " + s.getNineLivesRemaining() + " ships protected";
        }
        return "";
    }
    
    
    private BoardPanel enemyBoardPanel;
    private BoardPanel playerBoardPanel;
    
    public void setBoards(BoardPanel player, BoardPanel enemy) {
        this.playerBoardPanel = player;
        this.enemyBoardPanel = enemy;
    }
    
    private Board getEnemyBoard() {
        return enemyBoardPanel != null ? enemyBoardPanel.getBoard() : null;
    }
    
    private Board getPlayerBoard() {
        return playerBoardPanel != null ? playerBoardPanel.getBoard() : null;
    }
    
    private void promptForTarget(String skillName, TargetCallback callback) {
        JOptionPane.showMessageDialog(this,
            "Click on the enemy board to target " + skillName,
            "Target Selection",
            JOptionPane.INFORMATION_MESSAGE);
        
        
        this.pendingTargetCallback = callback;
    }
    
    private TargetCallback pendingTargetCallback;
    
    public TargetCallback getPendingTargetCallback() {
        TargetCallback cb = pendingTargetCallback;
        pendingTargetCallback = null;
        return cb;
    }
    
    public interface TargetCallback {
        void onTargetSelected(int x, int y);
    }
    
    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, character.getName(), JOptionPane.INFORMATION_MESSAGE);
    }
    
   @Override
public void updateUI() {
    super.updateUI();
    
    
    if (resourceLabel != null) {
        resourceLabel.setText(getResourceText());
    }
    
    if (passiveLabel != null) {
        passiveLabel.setText(getPassiveText());
    }
    
    
    if (skill1Button != null) {
        
    }
}
    
    public void stopTimers() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
    }
}
