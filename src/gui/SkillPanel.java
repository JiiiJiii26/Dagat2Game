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
    
    
    private SkillButtonListener skillListener;
    
    
    public interface SkillButtonListener {
        void onSkillUsed(int skillNumber, String skillName, boolean requiresTarget, boolean requiresDirection, boolean targetsOwnBoard);
    }
    
    public void setSkillListener(SkillButtonListener listener) {
        this.skillListener = listener;
    }
    
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
        } else if (character instanceof Aeris) {
            addAerisSkills(mainPanel, gbc);
        } else if (character instanceof Morgana) {
            addMorganaSkills(mainPanel, gbc);
        } else if (character instanceof Selene) {
            addSeleneSkills(mainPanel, gbc);
        } else if (character instanceof Flue) {
            addFlueSkills(mainPanel, gbc);
        } else {
            addGenericSkills(mainPanel, gbc);
        }
        
        add(mainPanel, BorderLayout.CENTER);
        
        updateTimer = new Timer(1000, e -> updateUI());
        updateTimer.start();
    }
    
    private void addMorganaSkills(JPanel panel, GridBagConstraints gbc) {
        Morgana morgana = (Morgana) character;
        
        
        gbc.gridy++;
        JLabel melodyLabel = new JLabel("🎵 ENCHANTING MELODY");
        melodyLabel.setFont(new Font("Arial", Font.BOLD, 11));
        melodyLabel.setForeground(new Color(64, 224, 208));
        panel.add(melodyLabel, gbc);
        
        gbc.gridy++;
        JButton melodyBtn = new JButton("USE (40 mana)");
        melodyBtn.setBackground(new Color(64, 224, 208));
        melodyBtn.setForeground(Color.BLACK);
        melodyBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(1, "Enchanting Melody", false, false, false);
            } else {
                boolean used = morgana.useEnchantingMelody();
                if (used) {
                    showMessage("🎵 Enchanting Melody! Enemy is confused!");
                } else {
                    showMessage("❌ Cannot use Enchanting Melody!\n" + morgana.getSkillStatus(1));
                }
            }
        });
        panel.add(melodyBtn, gbc);
        
        
        gbc.gridy++;
        JLabel whirlpoolLabel = new JLabel("🌊 WHIRLPOOL TRAP");
        whirlpoolLabel.setFont(new Font("Arial", Font.BOLD, 11));
        whirlpoolLabel.setForeground(new Color(0, 150, 200));
        panel.add(whirlpoolLabel, gbc);
        
        gbc.gridy++;
        JButton whirlpoolBtn = new JButton("USE (80 mana)");
        whirlpoolBtn.setBackground(new Color(0, 150, 200));
        whirlpoolBtn.setForeground(Color.WHITE);
        whirlpoolBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(2, "Whirlpool Trap", true, false, false);
            } else {
                showMessage("🌊 Whirlpool Trap: Click on enemy board!");
            }
        });
        panel.add(whirlpoolBtn, gbc);
        
        
        gbc.gridy++;
        JLabel stormLabel = new JLabel("⛈️ STORM CALL");
        stormLabel.setFont(new Font("Arial", Font.BOLD, 11));
        stormLabel.setForeground(new Color(70, 130, 200));
        panel.add(stormLabel, gbc);
        
        gbc.gridy++;
        JButton stormBtn = new JButton("USE (300 mana)");
        stormBtn.setBackground(new Color(70, 130, 200));
        stormBtn.setForeground(Color.WHITE);
        stormBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(3, "Storm Call", false, false, false);
            } else {
                int flooded = morgana.useStormCall(getEnemyBoard());
                if (flooded > 0) {
                    showMessage("⛈️ Storm Call! " + flooded + " cells flooded!");
                } else {
                    showMessage("❌ Cannot use Storm Call!\n" + morgana.getSkillStatus(3));
                }
            }
        });
        panel.add(stormBtn, gbc);
        
        gbc.gridy++;
        JLabel manaLabel = new JLabel(morgana.getManaBar(), SwingConstants.CENTER);
        manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
        manaLabel.setForeground(Color.CYAN);
        panel.add(manaLabel, gbc);
    }
    
    private void addFlueSkills(JPanel panel, GridBagConstraints gbc) {
        Flue flue = (Flue) character;
        
        
        gbc.gridy++;
        JLabel corruptionLabel = new JLabel("💻 CORRUPTION.EXE");
        corruptionLabel.setFont(new Font("Arial", Font.BOLD, 11));
        corruptionLabel.setForeground(new Color(0, 255, 127));
        panel.add(corruptionLabel, gbc);
        
        gbc.gridy++;
        JButton corruptionBtn = new JButton("USE (100 mana)");
        corruptionBtn.setBackground(new Color(0, 255, 127));
        corruptionBtn.setForeground(Color.BLACK);
        corruptionBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(1, "Corruption.EXE", true, false, false);
            } else {
                showMessage("💻 Corruption.EXE: Click on enemy board to infect a cell!");
            }
        });
        panel.add(corruptionBtn, gbc);
        
        
        gbc.gridy++;
        JLabel fortificationLabel = new JLabel("🛡️ FORTIFICATION.GRID");
        fortificationLabel.setFont(new Font("Arial", Font.BOLD, 11));
        fortificationLabel.setForeground(new Color(0, 200, 100));
        panel.add(fortificationLabel, gbc);
        
        gbc.gridy++;
        JButton fortificationBtn = new JButton("USE (200 mana)");
        fortificationBtn.setBackground(new Color(0, 200, 100));
        fortificationBtn.setForeground(Color.BLACK);
        fortificationBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(2, "Fortification.GRID", true, false, true);
            } else {
                showMessage("🛡️ Fortification.GRID: Click on your board to repair!");
            }
        });
        panel.add(fortificationBtn, gbc);
        
        
        gbc.gridy++;
        JLabel kernelLabel = new JLabel("💀 KERNEL.DECIMATION.REQ");
        kernelLabel.setFont(new Font("Arial", Font.BOLD, 11));
        kernelLabel.setForeground(new Color(200, 0, 0));
        panel.add(kernelLabel, gbc);
        
        gbc.gridy++;
        JButton kernelBtn = new JButton("USE (300 mana)");
        kernelBtn.setBackground(new Color(200, 0, 0));
        kernelBtn.setForeground(Color.WHITE);
        kernelBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(3, "Kernel.Decimation.REQ", true, false, false);
            } else {
                showMessage("💀 Kernel.Decimation.REQ: Click on enemy board to decimate!");
            }
        });
        panel.add(kernelBtn, gbc);
        
        gbc.gridy++;
        JLabel manaLabel = new JLabel(flue.getManaBar(), SwingConstants.CENTER);
        manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
        manaLabel.setForeground(Color.CYAN);
        panel.add(manaLabel, gbc);
    }
    
    private void addJijiSkills(JPanel panel, GridBagConstraints gbc) {
        Jiji jiji = (Jiji) character;
        
        
        gbc.gridy++;
        JLabel leechLabel = new JLabel("🔓 DATA LEECH");
        leechLabel.setFont(new Font("Arial", Font.BOLD, 11));
        leechLabel.setForeground(new Color(100, 200, 255));
        panel.add(leechLabel, gbc);
        
        gbc.gridy++;
        JButton leechBtn = new JButton("USE (50 mana)");
        leechBtn.setBackground(new Color(100, 200, 255));
        leechBtn.setForeground(Color.BLACK);
        leechBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(1, "Data Leech", true, false, false);
            } else {
                if (jiji.useDataLeech(getEnemyBoard())) {
                    showMessage("Data Leech used! Cells revealed!");
                } else {
                    showMessage("❌ Cannot use Data Leech!\n" + jiji.getSkillStatus(1));
                }
            }
        });
        panel.add(leechBtn, gbc);
        
        
        gbc.gridy++;
        JLabel overclockLabel = new JLabel("⚡ OVERCLOCK");
        overclockLabel.setFont(new Font("Arial", Font.BOLD, 11));
        overclockLabel.setForeground(new Color(200, 150, 50));
        panel.add(overclockLabel, gbc);
        
        gbc.gridy++;
        JButton overclockBtn = new JButton("USE (120 mana)");
        overclockBtn.setBackground(new Color(200, 150, 50));
        overclockBtn.setForeground(Color.BLACK);
        overclockBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(2, "Overclock", false, false, false);
            } else {
                if (jiji.useOverclock()) {
                    showMessage("Overclock activated! Next shot fires twice!");
                } else {
                    showMessage("❌ Cannot use Overclock!\n" + jiji.getSkillStatus(2));
                }
            }
        });
        panel.add(overclockBtn, gbc);
        
        
        gbc.gridy++;
        JLabel overloadLabel = new JLabel("💻 SYSTEM OVERLOAD");
        overloadLabel.setFont(new Font("Arial", Font.BOLD, 11));
        overloadLabel.setForeground(new Color(200, 50, 50));
        panel.add(overloadLabel, gbc);
        
        gbc.gridy++;
        JButton overloadBtn = new JButton("USE (400 mana)");
        overloadBtn.setBackground(new Color(200, 50, 50));
        overloadBtn.setForeground(Color.WHITE);
        overloadBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(3, "System Overload", true, false, false);
            } else {
                if (jiji.useSystemOverload(getEnemyBoard())) {
                    showMessage("System Overload! Enemy skill disabled!");
                } else {
                    showMessage("❌ Cannot use System Overload!\n" + jiji.getSkillStatus(3));
                }
            }
        });
        panel.add(overloadBtn, gbc);
        
        gbc.gridy++;
        JLabel manaLabel = new JLabel(jiji.getManaBar(), SwingConstants.CENTER);
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
        stepBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(1, "Shadow Step", true, false, true);
            } else {
                showMessage("🌑 Shadow Step: Click on your ship, then destination!");
            }
        });
        panel.add(stepBtn, gbc);
        
        
        gbc.gridy++;
        JLabel bladeLabel = new JLabel("⚔️ SHADOW BLADE");
        bladeLabel.setFont(new Font("Arial", Font.BOLD, 11));
        bladeLabel.setForeground(new Color(100, 150, 255));
        panel.add(bladeLabel, gbc);
        
        gbc.gridy++;
        JButton bladeBtn = new JButton("USE (150 energy)");
        bladeBtn.setBackground(new Color(100, 150, 255));
        bladeBtn.setForeground(Color.BLACK);
        bladeBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(2, "Shadow Blade", true, true, false);
            } else {
                showMessage("⚔️ Shadow Blade: Choose direction, then click on enemy board!");
            }
        });
        panel.add(bladeBtn, gbc);
        
        
        gbc.gridy++;
        JLabel domainLabel = new JLabel("🌑🌑🌑 SHADOW DOMAIN");
        domainLabel.setFont(new Font("Arial", Font.BOLD, 11));
        domainLabel.setForeground(new Color(50, 0, 100));
        panel.add(domainLabel, gbc);
        
        gbc.gridy++;
        JButton domainBtn = new JButton("USE (200 energy)");
        domainBtn.setBackground(new Color(50, 0, 100));
        domainBtn.setForeground(Color.WHITE);
        domainBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(3, "Shadow Domain", true, false, false);
            } else {
                showMessage("🌑🌑🌑 Shadow Domain: Click on enemy board to create explosion!");
            }
        });
        panel.add(domainBtn, gbc);
        
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
        radarBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(1, "Radar Overload", false, false, false);
            } else {
                if (valerius.useRadarOverload()) {
                    showMessage("📡 Radar Overload! Enemy skills disabled for 2 turns!");
                } else {
                    showMessage("❌ Cannot use Radar Overload!\n" + valerius.getSkillStatus(1));
                }
            }
        });
        panel.add(radarBtn, gbc);
        
        
        gbc.gridy++;
        JLabel strikeLabel = new JLabel("🎯 PRECISION STRIKE");
        strikeLabel.setFont(new Font("Arial", Font.BOLD, 11));
        strikeLabel.setForeground(new Color(200, 100, 0));
        panel.add(strikeLabel, gbc);
        
        gbc.gridy++;
        JButton strikeBtn = new JButton("USE (120 mana)");
        strikeBtn.setBackground(new Color(200, 100, 0));
        strikeBtn.setForeground(Color.WHITE);
        strikeBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(2, "Precision Strike", true, true, false);
            } else {
                showMessage("🎯 Precision Strike: Choose direction, then click on enemy board!");
            }
        });
        panel.add(strikeBtn, gbc);
        
        
        gbc.gridy++;
        JLabel fortressLabel = new JLabel("🏰 FORTRESS MODE");
        fortressLabel.setFont(new Font("Arial", Font.BOLD, 11));
        fortressLabel.setForeground(new Color(100, 50, 0));
        panel.add(fortressLabel, gbc);
        
        gbc.gridy++;
        JButton fortressBtn = new JButton("USE (300 mana)");
        fortressBtn.setBackground(new Color(100, 50, 0));
        fortressBtn.setForeground(Color.WHITE);
        fortressBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(3, "Fortress Mode", false, false, false);
            } else {
                if (valerius.useFortressMode()) {
                    showMessage("🏰 Fortress Mode! All ships shielded!");
                } else {
                    showMessage("❌ Cannot use Fortress Mode!\n" + valerius.getSkillStatus(3));
                }
            }
        });
        panel.add(fortressBtn, gbc);
        
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
        catnipBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(1, "Catnip Explosion", true, false, false);
            } else {
                showMessage("🌿 Catnip Explosion: Click on enemy board!");
            }
        });
        panel.add(catnipBtn, gbc);
        
        
        gbc.gridy++;
        JLabel laserLabel = new JLabel("🔴 LASER POINTER");
        laserLabel.setFont(new Font("Arial", Font.BOLD, 11));
        laserLabel.setForeground(new Color(255, 100, 100));
        panel.add(laserLabel, gbc);
        
        gbc.gridy++;
        JButton laserBtn = new JButton("USE (50 mana)");
        laserBtn.setBackground(new Color(255, 100, 100));
        laserBtn.setForeground(Color.BLACK);
        laserBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(2, "Laser Pointer", false, false, false);
            } else {
                if (skye.useLaserPointer()) {
                    showMessage("🔴 Laser Pointer! Enemy will skip their next turn!");
                } else {
                    showMessage("❌ Cannot use Laser Pointer!\n" + skye.getSkillStatus(2));
                }
            }
        });
        panel.add(laserBtn, gbc);
        
        
        gbc.gridy++;
        JLabel reviveLabel = new JLabel("😺 NINE LIVES");
        reviveLabel.setFont(new Font("Arial", Font.BOLD, 11));
        reviveLabel.setForeground(new Color(255, 165, 0));
        panel.add(reviveLabel, gbc);
        
        gbc.gridy++;
        JButton reviveBtn = new JButton("USE (200 mana)");
        reviveBtn.setBackground(new Color(255, 165, 0));
        reviveBtn.setForeground(Color.BLACK);
        reviveBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(3, "Nine Lives", true, false, true);
            } else {
                showMessage("😺 Nine Lives: Click on your own board to revive a sunk ship!");
            }
        });
        panel.add(reviveBtn, gbc);
        
        gbc.gridy++;
        JLabel manaLabel = new JLabel(skye.getManaBar(), SwingConstants.CENTER);
        manaLabel.setFont(new Font("Arial", Font.BOLD, 10));
        manaLabel.setForeground(Color.CYAN);
        panel.add(manaLabel, gbc);
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
            if (skillListener != null) {
                skillListener.onSkillUsed(1, "Adaptive Instinct", true, false, true);
            } else {
                showShipSelectionDialog(aeris);
            }
        });
        panel.add(shieldBtn, gbc);
        
        
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
            if (skillListener != null) {
                skillListener.onSkillUsed(2, "Multitask Overdrive", false, false, false);
            } else {
                boolean used = aeris.useMultitaskOverdrive();
                if (used) {
                    showMessage("⚡ Multitask Overdrive! Restored 200 mana!");
                    updateUI();
                } else {
                    showMessage("❌ Cannot use Multitask Overdrive!\n" + aeris.getSkillStatus(2));
                }
            }
        });
        panel.add(overdriveBtn, gbc);
        
        
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
            if (skillListener != null) {
                skillListener.onSkillUsed(3, "Relentless Ascent", true, false, false);
            } else {
                showMessage("⚔️ Relentless Ascent: Click on a column (0-9) on enemy board!");
            }
        });
        panel.add(ascentBtn, gbc);
        
        gbc.gridy++;
        JLabel manaLabel = new JLabel(aeris.getManaBar(), SwingConstants.CENTER);
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
        revealBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(1, "Lunar Reveal", true, false, false);
            } else {
                showMessage("🔮 Lunar Reveal: Click on enemy board to reveal 3x3 area!");
            }
        });
        panel.add(revealBtn, gbc);
        
        
        gbc.gridy++;
        JLabel strikeLabel = new JLabel("🌙 CRESCENT STRIKE");
        strikeLabel.setFont(new Font("Arial", Font.BOLD, 11));
        strikeLabel.setForeground(new Color(150, 100, 200));
        panel.add(strikeLabel, gbc);
        
        gbc.gridy++;
        JButton strikeBtn = new JButton("USE (120 mana)");
        strikeBtn.setBackground(new Color(150, 100, 200));
        strikeBtn.setForeground(Color.WHITE);
        strikeBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(2, "Crescent Strike", true, false, false);
            } else {
                showMessage("🌙 Crescent Strike: Click on enemy board to strike cross pattern!");
            }
        });
        panel.add(strikeBtn, gbc);
        
        
        gbc.gridy++;
        JLabel starfallLabel = new JLabel("⭐ STARFALL LINK");
        starfallLabel.setFont(new Font("Arial", Font.BOLD, 11));
        starfallLabel.setForeground(new Color(255, 215, 0));
        panel.add(starfallLabel, gbc);
        
        gbc.gridy++;
        JButton starfallBtn = new JButton("USE (300 mana)");
        starfallBtn.setBackground(new Color(255, 215, 0));
        starfallBtn.setForeground(Color.BLACK);
        starfallBtn.addActionListener(e -> {
            if (skillListener != null) {
                skillListener.onSkillUsed(3, "Starfall Link", false, false, false);
            } else {
                boolean used = selene.useStarfallLink(getEnemyBoard());
                if (used) {
                    showMessage("⭐ Starfall Link activated!");
                } else {
                    showMessage("❌ Cannot use Starfall Link!\n" + selene.getSkillStatus(3));
                }
            }
        });
        panel.add(starfallBtn, gbc);
        
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
        if (character instanceof Morgana) return new Color(64, 224, 208);
        if (character instanceof Aeris) return new Color(255, 215, 0);
        if (character instanceof Selene) return new Color(200, 150, 255);
        if (character instanceof Flue) return new Color(0, 255, 127);
        return Color.WHITE;
    }
    
    private Color getResourceColor() {
        if (character instanceof Jiji) return Color.CYAN;
        if (character instanceof Kael) return new Color(100, 200, 255);
        if (character instanceof Valerius) return Color.CYAN;
        if (character instanceof Skye) return Color.CYAN;
        if (character instanceof Morgana) return Color.CYAN;
        if (character instanceof Aeris) return Color.CYAN;
        if (character instanceof Selene) return Color.CYAN;
        if (character instanceof Flue) return Color.CYAN;
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
        if (character instanceof Morgana) {
            Morgana m = (Morgana) character;
            return "🌊 Mana: " + m.getCurrentMana() + "/" + m.getMaxMana();
        }
        if (character instanceof Aeris) {
            Aeris a = (Aeris) character;
            return "💪 Mana: " + a.getCurrentMana() + "/" + a.getMaxMana();
        }
        if (character instanceof Selene) {
            Selene s = (Selene) character;
            return "🌙 Mana: " + s.getCurrentMana() + "/" + s.getMaxMana();
        }
        if (character instanceof Flue) {
            Flue f = (Flue) character;
            return "💻 Mana: " + f.getCurrentMana() + "/" + f.getMaxMana();
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
        if (character instanceof Morgana) {
            return "Passive: Ocean's Embrace - First hit is blocked";
        }
        if (character instanceof Aeris) {
            return "Passive: Stun Immune - Below 40% HP becomes immune to stuns";
        }
        if (character instanceof Selene) {
            Selene s = (Selene) character;
            if (s.isNightTime()) {
                return "Passive: MOON'S BLESSING ACTIVE - All skills enhanced!";
            }
            return "Passive: Night in " + s.getTurnsUntilNight() + " turns";
        }
        if (character instanceof Flue) {
            return "Passive: Lone.Resolve - 15% DR when no shields active";
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
    public void forceRefreshUI() {
    
    updateUI();
    
    
    if (skill1Button != null) {
        String status = getSkillStatus(1);
        boolean enabled = status.equals("Ready!") || status.contains("Ready") || status.contains("ENHANCED");
        skill1Button.setEnabled(enabled);
    }
    if (skill2Button != null) {
        String status = getSkillStatus(2);
        boolean enabled = status.equals("Ready!") || status.contains("Ready") || status.contains("ENHANCED");
        skill2Button.setEnabled(enabled);
    }
    if (skill3Button != null) {
        String status = getSkillStatus(3);
        boolean enabled = status.equals("Ready!") || status.contains("Ready") || status.contains("ENHANCED") || status.contains("ULTIMATE READY");
        skill3Button.setEnabled(enabled);
    }
    
    
    if (resourceLabel != null) {
        resourceLabel.setText(getResourceText());
    }
    if (passiveLabel != null) {
        passiveLabel.setText(getPassiveText());
    }
    
    repaint();
}


private String getSkillStatus(int skillNum) {
    if (character instanceof Selene) {
        return ((Selene) character).getSkillStatus(skillNum);
    }
    return "Ready!";
}
}