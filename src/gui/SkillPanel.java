package gui;

import characters.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import models.Board;
import models.Ship;

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
        }else if(character instanceof Aeris) {
            addAerisSkills(mainPanel, gbc);
        }
        else {
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
private void addAerisSkills(JPanel panel, GridBagConstraints gbc) {
    Aeris aeris = (Aeris) character;
    
    
    gbc.gridy++;
    JLabel shieldLabel = new JLabel("🛡️ ADAPTIVE INSTINCT");
    shieldLabel.setFont(new Font("Arial", Font.BOLD, 11));
    shieldLabel.setForeground(new Color(100, 200, 255));
    panel.add(shieldLabel, gbc);
    
    gbc.gridy++;
    JButton shieldBtn = new JButton("USE (120 mana)");
    shieldBtn.setBackground(new Color(100, 200, 255));
    shieldBtn.setForeground(Color.BLACK);
    shieldBtn.addActionListener(e -> {
        String status = aeris.getSkillStatus(1);
        if (status.equals("Ready!")) {
            showShipSelectionDialog(aeris);
        } else {
            showMessage("❌ Cannot use Adaptive Instinct!\n" + status);
        }
    });
    panel.add(shieldBtn, gbc);
    
    gbc.gridy++;
    JLabel shieldDesc = new JLabel("Shield a ship for 2 turns");
    shieldDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    shieldDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(shieldDesc, gbc);
    
    
    gbc.gridy++;
    JLabel overdriveLabel = new JLabel("⚡ MULTITASK OVERDRIVE");
    overdriveLabel.setFont(new Font("Arial", Font.BOLD, 11));
    overdriveLabel.setForeground(new Color(255, 215, 0));
    panel.add(overdriveLabel, gbc);
    
    gbc.gridy++;
    JButton overdriveBtn = new JButton("USE (Restores 200 mana)");
    overdriveBtn.setBackground(new Color(255, 215, 0));
    overdriveBtn.setForeground(Color.BLACK);
    overdriveBtn.addActionListener(e -> {
        String status = aeris.getSkillStatus(2);
        if (status.equals("Ready!") || status.equals("Ready! (+200 mana)")) {
            boolean used = aeris.useMultitaskOverdrive();
            if (used) {
                showMessage("⚡ Multitask Overdrive! Restored 200 mana!");
                updateUI();
            } else {
                showMessage("❌ Cannot use Multitask Overdrive!\n" + aeris.getSkillStatus(2));
            }
        } else {
            showMessage("❌ Cannot use Multitask Overdrive!\n" + status);
        }
    });
    panel.add(overdriveBtn, gbc);
    
    gbc.gridy++;
    JLabel overdriveDesc = new JLabel("Restores 200 mana (3 turn cooldown)");
    overdriveDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    overdriveDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(overdriveDesc, gbc);
    
    
    gbc.gridy++;
    JLabel ascentLabel = new JLabel("⚔️ RELENTLESS ASCENT");
    ascentLabel.setFont(new Font("Arial", Font.BOLD, 11));
    ascentLabel.setForeground(new Color(200, 50, 50));
    panel.add(ascentLabel, gbc);
    
    gbc.gridy++;
    JButton ascentBtn = new JButton("USE (500 mana)");
    ascentBtn.setBackground(new Color(200, 50, 50));
    ascentBtn.setForeground(Color.WHITE);
    ascentBtn.addActionListener(e -> {
        String status = aeris.getSkillStatus(3);
        if (status.equals("Ready!")) {
            showMessage("⚔️ Relentless Ascent: Click on a column (0-9) on enemy board!");
        } else {
            showMessage("❌ Cannot use Relentless Ascent!\n" + status);
        }
    });
    panel.add(ascentBtn, gbc);
    
    gbc.gridy++;
    JLabel ascentDesc = new JLabel("Destroys an entire column (more damage when low HP)");
    ascentDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    ascentDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(ascentDesc, gbc);
    
    
    if (aeris.isStunImmuneActive()) {
        gbc.gridy++;
        JLabel stunLabel = new JLabel("⚡ STUN IMMUNE ACTIVE", SwingConstants.CENTER);
        stunLabel.setForeground(Color.YELLOW);
        stunLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(stunLabel, gbc);
    }
    
    
    gbc.gridy++;
    JLabel manaLabel = new JLabel(aeris.getManaBar(), SwingConstants.CENTER);
    manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
    manaLabel.setForeground(Color.CYAN);
    panel.add(manaLabel, gbc);
}
    
   private void addKaelSkills(JPanel panel, GridBagConstraints gbc) {
    Kael kael = (Kael) character;
    
    
    gbc.gridy++;
    JLabel stepLabel = new JLabel("🌑 SHADOW STEP");
    stepLabel.setFont(new Font("Arial", Font.BOLD, 11));
    stepLabel.setForeground(new Color(75, 0, 130));
    panel.add(stepLabel, gbc);
    
    gbc.gridy++;
    JButton stepBtn = new JButton("USE (100 energy)");
    stepBtn.setBackground(new Color(75, 0, 130));
    stepBtn.setForeground(Color.WHITE);
    stepBtn.setToolTipText("Teleport one of your ships to a new location");
    stepBtn.addActionListener(e -> {
        
        
        showMessage("🌑 Shadow Step: Use this skill from the battle screen!");
    });
    panel.add(stepBtn, gbc);
    
    gbc.gridy++;
    JLabel stepDesc = new JLabel("Teleport one of your ships");
    stepDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    stepDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(stepDesc, gbc);
    
    
    gbc.gridy++;
    JLabel bladeLabel = new JLabel("⚔️ SHADOW BLADE");
    bladeLabel.setFont(new Font("Arial", Font.BOLD, 11));
    bladeLabel.setForeground(new Color(100, 150, 255));
    panel.add(bladeLabel, gbc);
    
    gbc.gridy++;
    JButton bladeBtn = new JButton("USE (150 energy)");
    bladeBtn.setBackground(new Color(100, 150, 255));
    bladeBtn.setForeground(Color.BLACK);
    bladeBtn.setToolTipText("Cut through a row or column, destroying every other cell");
    bladeBtn.addActionListener(e -> {
        showMessage("⚔️ Shadow Blade: Choose direction, then click on enemy board!");
    });
    panel.add(bladeBtn, gbc);
    
    gbc.gridy++;
    JLabel bladeDesc = new JLabel("Destroy every other cell in a row/column");
    bladeDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    bladeDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(bladeDesc, gbc);
    
    
    gbc.gridy++;
    JLabel domainLabel = new JLabel("🌑🌑🌑 SHADOW DOMAIN");
    domainLabel.setFont(new Font("Arial", Font.BOLD, 11));
    domainLabel.setForeground(new Color(50, 0, 100));
    panel.add(domainLabel, gbc);
    
    gbc.gridy++;
    JButton domainBtn = new JButton("USE (200 energy)");
    domainBtn.setBackground(new Color(50, 0, 100));
    domainBtn.setForeground(Color.WHITE);
    domainBtn.setToolTipText("Create a 3x3 shadow explosion!");
    domainBtn.addActionListener(e -> {
        showMessage("🌑🌑🌑 Shadow Domain: Click on enemy board to create explosion!");
    });
    panel.add(domainBtn, gbc);
    
    gbc.gridy++;
    JLabel domainDesc = new JLabel("3x3 area explosion - destroys all cells");
    domainDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    domainDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(domainDesc, gbc);
    
    
    gbc.gridy++;
    JLabel energyLabel = new JLabel(kael.getEnergyBar(), SwingConstants.CENTER);
    energyLabel.setFont(new Font("Arial", Font.BOLD, 10));
    energyLabel.setForeground(new Color(100, 200, 255));
    panel.add(energyLabel, gbc);
}
    
   private void addValeriusSkills(JPanel panel, GridBagConstraints gbc) {
    Valerius valerius = (Valerius) character;
    
    
    gbc.gridy++;
    JLabel radarLabel = new JLabel("📡 RADAR OVERLOAD");
    radarLabel.setFont(new Font("Arial", Font.BOLD, 11));
    radarLabel.setForeground(new Color(169, 169, 169));
    panel.add(radarLabel, gbc);
    
    gbc.gridy++;
    JButton radarBtn = new JButton("USE (50 mana)");
    radarBtn.setBackground(new Color(169, 169, 169));
    radarBtn.setForeground(Color.WHITE);
    radarBtn.setToolTipText("Disable enemy skills for 2 turns");
    radarBtn.addActionListener(e -> {
        boolean used = valerius.useRadarOverload();
        if (used) {
            showMessage("📡 Radar Overload! Enemy skills disabled for 2 turns!");
        } else {
            showMessage("❌ Cannot use Radar Overload!\n" + valerius.getSkillStatus(1));
        }
    });
    panel.add(radarBtn, gbc);
    
    gbc.gridy++;
    JLabel radarDesc = new JLabel("Disable enemy skills for 2 turns");
    radarDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    radarDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(radarDesc, gbc);
    
    
    gbc.gridy++;
    JLabel strikeLabel = new JLabel("🎯 PRECISION STRIKE");
    strikeLabel.setFont(new Font("Arial", Font.BOLD, 11));
    strikeLabel.setForeground(new Color(200, 100, 0));
    panel.add(strikeLabel, gbc);
    
    gbc.gridy++;
    JButton strikeBtn = new JButton("USE (120 mana)");
    strikeBtn.setBackground(new Color(200, 100, 0));
    strikeBtn.setForeground(Color.WHITE);
    strikeBtn.setToolTipText("Next attack destroys 2 cells in a line");
    strikeBtn.addActionListener(e -> {
        String[] options = {"Horizontal (→)", "Vertical (↓)"};
        int choice = JOptionPane.showOptionDialog(null,
            "🎯 PRECISION STRIKE\n\nChoose attack direction:",
            "Precision Strike",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice >= 0) {
            boolean horizontal = (choice == 0);
            boolean used = valerius.usePrecisionStrike();
            if (used) {
                String input = JOptionPane.showInputDialog(null,
                    "Enter target coordinates (row,col):\nExample: 5,5",
                    "Precision Strike Target",
                    JOptionPane.QUESTION_MESSAGE);
                if (input != null) {
                    try {
                        String[] parts = input.split(",");
                        int x = Integer.parseInt(parts[0].trim());
                        int y = Integer.parseInt(parts[1].trim());
                        int destroyed = valerius.applyPrecisionStrike(getEnemyBoard(), x, y, horizontal);
                        showMessage("🎯 Precision Strike destroyed " + destroyed + " cells!");
                    } catch (Exception ex) {
                        showMessage("❌ Invalid coordinates!");
                    }
                }
            } else {
                showMessage("❌ Cannot use Precision Strike!\n" + valerius.getSkillStatus(2));
            }
        }
    });
    panel.add(strikeBtn, gbc);
    
    gbc.gridy++;
    JLabel strikeDesc = new JLabel("Next attack destroys 2 cells in a line");
    strikeDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    strikeDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(strikeDesc, gbc);
    
    
    gbc.gridy++;
    JLabel fortressLabel = new JLabel("🏰 FORTRESS MODE");
    fortressLabel.setFont(new Font("Arial", Font.BOLD, 11));
    fortressLabel.setForeground(new Color(100, 50, 0));
    panel.add(fortressLabel, gbc);
    
    gbc.gridy++;
    JButton fortressBtn = new JButton("USE (300 mana)");
    fortressBtn.setBackground(new Color(100, 50, 0));
    fortressBtn.setForeground(Color.WHITE);
    fortressBtn.setToolTipText("Protect a ship - it will block 1 hit (choose from battle screen)");
    fortressBtn.addActionListener(e -> {
        
        showMessage("🏰 Fortress Mode: Use this skill from the battle screen to select a ship to protect!");
    });
    panel.add(fortressBtn, gbc);
    
    gbc.gridy++;
    JLabel fortressDesc = new JLabel("Protect a ship - blocks 1 hit");
    fortressDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    fortressDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(fortressDesc, gbc);
    
    
    if (valerius.areEnemySkillsDisabled()) {
        gbc.gridy++;
        JLabel disabledLabel = new JLabel("🚫 Enemy skills DISABLED", SwingConstants.CENTER);
        disabledLabel.setForeground(Color.RED);
        disabledLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(disabledLabel, gbc);
    }
    
    
    if (valerius.hasProtectedShip()) {
        gbc.gridy++;
        JLabel protectedLabel = new JLabel("🛡️ " + valerius.getProtectedShipName() + " is PROTECTED", SwingConstants.CENTER);
        protectedLabel.setForeground(Color.CYAN);
        protectedLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(protectedLabel, gbc);
    }
    
    if (valerius.isScrapperResolveActive()) {
        gbc.gridy++;
        JLabel resolveLabel = new JLabel("⚡ Scrapper's Resolve ACTIVE", SwingConstants.CENTER);
        resolveLabel.setForeground(Color.ORANGE);
        resolveLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(resolveLabel, gbc);
    }
    
    
    gbc.gridy++;
    JLabel manaLabel = new JLabel(valerius.getManaBar(), SwingConstants.CENTER);
    manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
    manaLabel.setForeground(Color.CYAN);
    panel.add(manaLabel, gbc);
}
    
  private void addSkyeSkills(JPanel panel, GridBagConstraints gbc) {
    Skye skye = (Skye) character;
    
    
  gbc.gridy++;
JLabel catnipLabel = new JLabel("🌿 CATNIP EXPLOSION");
catnipLabel.setFont(new Font("Arial", Font.BOLD, 11));
catnipLabel.setForeground(new Color(50, 205, 50));
panel.add(catnipLabel, gbc);

gbc.gridy++;
JButton catnipBtn = new JButton("USE (70 mana)");
catnipBtn.setBackground(new Color(50, 205, 50));
catnipBtn.setForeground(Color.BLACK);
catnipBtn.setToolTipText("Destroy a 2x2 area on enemy board");
catnipBtn.addActionListener(e -> {
    String status = skye.getSkillStatus(1);
    if (status.equals("Ready!")) {
        showMessage("🌿 Catnip Explosion: Click on enemy board to select target location!");
    } else {
        showMessage("❌ Cannot use Catnip Explosion!\n" + status);
    }
});
panel.add(catnipBtn, gbc);
    
    gbc.gridy++;
    JLabel catnipDesc = new JLabel("Destroy 2x2 area on enemy board");
    catnipDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    catnipDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(catnipDesc, gbc);
    
    
    gbc.gridy++;
    JLabel laserLabel = new JLabel("🔴 LASER POINTER");
    laserLabel.setFont(new Font("Arial", Font.BOLD, 11));
    laserLabel.setForeground(new Color(255, 100, 100));
    panel.add(laserLabel, gbc);
    
    gbc.gridy++;
    JButton laserBtn = new JButton("USE (50 mana)");
    laserBtn.setBackground(new Color(255, 100, 100));
    laserBtn.setForeground(Color.BLACK);
    laserBtn.setToolTipText("Enemy skips their next turn");
    laserBtn.addActionListener(e -> {
        boolean used = skye.useLaserPointer();
        if (used) {
            showMessage("🔴 Laser Pointer! Enemy will skip their next turn!");
        } else {
            showMessage("❌ Cannot use Laser Pointer!\n" + skye.getSkillStatus(2));
        }
    });
    panel.add(laserBtn, gbc);
    
    gbc.gridy++;
    JLabel laserDesc = new JLabel("Enemy skips their next turn");
    laserDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    laserDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(laserDesc, gbc);
    
    
    gbc.gridy++;
    JLabel reviveLabel = new JLabel("😺 NINE LIVES");
    reviveLabel.setFont(new Font("Arial", Font.BOLD, 11));
    reviveLabel.setForeground(new Color(255, 165, 0));
    panel.add(reviveLabel, gbc);
    
    gbc.gridy++;
    JButton reviveBtn = new JButton("USE (200 mana)");
reviveBtn.setBackground(new Color(255, 165, 0));
reviveBtn.setForeground(Color.BLACK);
reviveBtn.setToolTipText("Revive a fallen ship on your board");
reviveBtn.addActionListener(e -> {
    String status = skye.getSkillStatus(3);
    if (status.equals("Ready!")) {
        showMessage("😺 Nine Lives: Click on your own board to revive a sunk ship!");
    } else {
        showMessage("❌ Cannot use Nine Lives!\n" + status);
    }
});
panel.add(reviveBtn, gbc);
    
    gbc.gridy++;
    JLabel reviveDesc = new JLabel("Revive a fallen ship");
    reviveDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    reviveDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(reviveDesc, gbc);
    
    
    if (skye.isEnemyDistracted()) {
        gbc.gridy++;
        JLabel distractedLabel = new JLabel("🌿 Enemy DISTRACTED", SwingConstants.CENTER);
        distractedLabel.setForeground(Color.GREEN);
        distractedLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(distractedLabel, gbc);
    }
    
    if (skye.getReviveUses() > 0) {
        gbc.gridy++;
        JLabel reviveUsedLabel = new JLabel("😺 Nine Lives used: " + skye.getReviveUses(), SwingConstants.CENTER);
        reviveUsedLabel.setForeground(Color.ORANGE);
        reviveUsedLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(reviveUsedLabel, gbc);
    }
    
    
    gbc.gridy++;
    JLabel manaLabel = new JLabel(skye.getManaBar(), SwingConstants.CENTER);
    manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
    manaLabel.setForeground(Color.CYAN);
    panel.add(manaLabel, gbc);
}
private void addSeleneSkills(JPanel panel, GridBagConstraints gbc) {
    Selene selene = (Selene) character;
    
    
    gbc.gridy++;
    JLabel revealLabel = new JLabel("🔮 LUNAR REVEAL");
    revealLabel.setFont(new Font("Arial", Font.BOLD, 11));
    revealLabel.setForeground(new Color(200, 150, 255));
    panel.add(revealLabel, gbc);
    
    gbc.gridy++;
    JButton revealBtn = new JButton("USE (60 mana)");
    revealBtn.setBackground(new Color(200, 150, 255));
    revealBtn.setForeground(Color.BLACK);
    revealBtn.setToolTipText("Reveal all cells in a 3x3 area");
    revealBtn.addActionListener(e -> {
        showMessage("🔮 Lunar Reveal: Use this skill from the battle screen!");
    });
    panel.add(revealBtn, gbc);
    
    gbc.gridy++;
    JLabel revealDesc = new JLabel("Reveal 3x3 area on enemy board");
    revealDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    revealDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(revealDesc, gbc);
    
    
    gbc.gridy++;
    JLabel strikeLabel = new JLabel("🌙 CRESCENT STRIKE");
    strikeLabel.setFont(new Font("Arial", Font.BOLD, 11));
    strikeLabel.setForeground(new Color(150, 100, 200));
    panel.add(strikeLabel, gbc);
    
    gbc.gridy++;
    JButton strikeBtn = new JButton("USE (120 mana)");
    strikeBtn.setBackground(new Color(150, 100, 200));
    strikeBtn.setForeground(Color.WHITE);
    strikeBtn.setToolTipText("Destroy a cross pattern");
    strikeBtn.addActionListener(e -> {
        showMessage("🌙 Crescent Strike: Use this skill from the battle screen!");
    });
    panel.add(strikeBtn, gbc);
    
    gbc.gridy++;
    JLabel strikeDesc = new JLabel("Destroy cross pattern on enemy board");
    strikeDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    strikeDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(strikeDesc, gbc);
    
    
    gbc.gridy++;
    JLabel starfallLabel = new JLabel("⭐ STARFALL LINK");
    starfallLabel.setFont(new Font("Arial", Font.BOLD, 11));
    starfallLabel.setForeground(new Color(255, 215, 0));
    panel.add(starfallLabel, gbc);
    
    gbc.gridy++;
    JButton starfallBtn = new JButton("USE (300 mana)");
    starfallBtn.setBackground(new Color(255, 215, 0));
    starfallBtn.setForeground(Color.BLACK);
    starfallBtn.setToolTipText("ULTIMATE: Destroy 3 random cells + link 2 cells");
    starfallBtn.addActionListener(e -> {
        showMessage("⭐ Starfall Link: Use this skill from the battle screen!");
    });
    panel.add(starfallBtn, gbc);
    
    gbc.gridy++;
    JLabel starfallDesc = new JLabel("Destroy 3 random cells + link 2 cells for 2 turns");
    starfallDesc.setFont(new Font("Arial", Font.PLAIN, 8));
    starfallDesc.setForeground(Color.LIGHT_GRAY);
    panel.add(starfallDesc, gbc);
    
    
    if (selene.isLinkActive()) {
        gbc.gridy++;
        JLabel linkLabel = new JLabel("🔗 STAR LINK ACTIVE", SwingConstants.CENTER);
        linkLabel.setForeground(Color.CYAN);
        linkLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(linkLabel, gbc);
    }
    
    if (selene.isNightTime()) {
        gbc.gridy++;
        JLabel nightLabel = new JLabel("🌙 MOON'S BLESSING ACTIVE", SwingConstants.CENTER);
        nightLabel.setForeground(Color.YELLOW);
        nightLabel.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(nightLabel, gbc);
    } else {
        gbc.gridy++;
        JLabel nightCountdown = new JLabel("🌙 Night in " + selene.getTurnsUntilNight() + " turns", SwingConstants.CENTER);
        nightCountdown.setForeground(Color.GRAY);
        nightCountdown.setFont(new Font("Arial", Font.BOLD, 10));
        panel.add(nightCountdown, gbc);
    }
    
    
    gbc.gridy++;
    JLabel manaLabel = new JLabel(selene.getManaBar(), SwingConstants.CENTER);
    manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
    manaLabel.setForeground(Color.CYAN);
    panel.add(manaLabel, gbc);
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
    int livesRemaining = 9 - s.getReviveUses();
    return "Passive: Nine Lives - " + livesRemaining + " lives remaining (" + s.getReviveUses() + " used)";
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
    private void showShipSelectionDialog(Aeris aeris) {
    
    ArrayList<Ship> availableShips = new ArrayList<>();
    for (Ship ship : getPlayerBoard().getShips()) {
        if (!ship.isSunk() && !ship.isShielded()) {
            availableShips.add(ship);
        }
    }
    
    if (availableShips.isEmpty()) {
        showMessage("❌ No available ships to shield! All ships are either sunk or already shielded.");
        return;
    }
    
    
    String[] shipNames = new String[availableShips.size()];
    for (int i = 0; i < availableShips.size(); i++) {
        Ship ship = availableShips.get(i);
        shipNames[i] = ship.getName() + " (HP: " + ship.getRemainingHealth() + "/" + ship.getSize() + ")";
    }
    
    
    String selectedShip = (String) JOptionPane.showInputDialog(
        this,
        "Select a ship to shield for 2 turns:",
        "Adaptive Instinct",
        JOptionPane.QUESTION_MESSAGE,
        null,
        shipNames,
        shipNames[0]
    );
    
    if (selectedShip != null) {
        
        for (int i = 0; i < availableShips.size(); i++) {
            if (shipNames[i].equals(selectedShip)) {
                boolean used = aeris.useAdaptiveInstinct(getPlayerBoard(), i);
                if (used) {
                    showMessage("🛡️ " + availableShips.get(i).getName() + " is now SHIELDED for 2 turns!");
                    updateUI();
                } else {
                    showMessage("❌ Failed to shield ship!");
                }
                break;
            }
        }
    }
}
}
