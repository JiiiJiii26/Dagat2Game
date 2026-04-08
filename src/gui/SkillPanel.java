package gui;

import characters.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;

import models.Board;
import models.Ship;

import javax.swing.border.TitledBorder;    
import game.ShotResult;      

public class SkillPanel extends JPanel {
    
    private GameCharacter character;
    private JLabel resourceLabel;
    private JLabel passiveLabel;
    private JLabel characterNameLabel;
    
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
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        
        
        resourceLabel = new JLabel(getResourceText(), SwingConstants.CENTER);
        resourceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        resourceLabel.setForeground(getResourceColor());
        resourceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resourceLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        mainPanel.add(resourceLabel);
        
        
        passiveLabel = new JLabel(getPassiveText(), SwingConstants.CENTER);
        passiveLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        passiveLabel.setForeground(Color.LIGHT_GRAY);
        passiveLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(passiveLabel);
        
        
        if (character instanceof Jiji) {
            addJijiSkills(mainPanel);
        } else if (character instanceof Kael) {
            addKaelSkills(mainPanel);
        } else if (character instanceof Valerius) {
            addValeriusSkills(mainPanel);
        } else if (character instanceof Skye) {
            addSkyeSkills(mainPanel);
        } else if (character instanceof Aeris) {
            addAerisSkills(mainPanel);
        } else if (character instanceof Morgana) {
            addMorganaSkills(mainPanel);
        } else if (character instanceof Selene) {
            addSeleneSkills(mainPanel);
        } else if (character instanceof Flue) {
            addFlueSkills(mainPanel);
        } else {
            addGenericSkills(mainPanel);
        }
        
        
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(mainPanel);
        
        add(centerWrapper, BorderLayout.CENTER);
        
        updateTimer = new Timer(1000, e -> {
            if (resourceLabel != null) {
                String newText = getResourceText();
                if (!resourceLabel.getText().equals(newText)) {
                    resourceLabel.setText(newText);
                }
            }
            if (passiveLabel != null) {
                String newText = getPassiveText();
                if (!passiveLabel.getText().equals(newText)) {
                    passiveLabel.setText(newText);
                }
            }
        });
    }
    
    
    private void addSkillRow(JPanel panel, String labelText, String buttonText, Color color, String tooltip, ActionListener listener) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.Y_AXIS));
        rowPanel.setOpaque(false);
        rowPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton button = new JButton(buttonText);
        button.setBackground(color);
        button.setForeground(color == Color.BLACK ? Color.WHITE : Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setToolTipText(tooltip);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(listener);
        
        rowPanel.add(label);
        rowPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        rowPanel.add(button);
        
        panel.add(rowPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 8))); 
    }
    
    private void addJijiSkills(JPanel panel) {
        addSkillRow(panel, "🔓 DATA LEECH", "USE (50 mana)", new Color(100, 200, 255),
            "<html>Reveals 2 random enemy cells.<br>Marks them for potential chain reactions.</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(1, "Data Leech", true, false, false); });
        
        addSkillRow(panel, "⚡ OVERCLOCK", "USE (120 mana)", new Color(200, 150, 50),
            "<html>Next shot fires twice.<br>Grants extra turn on hit.</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(2, "Overclock", false, false, false); });
        
        addSkillRow(panel, "💻 SYSTEM OVERLOAD", "USE (400 mana)", new Color(200, 50, 50),
            "<html>Reveals a full enemy ship.<br>If Overclock is active, destroys the ship instead!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(3, "System Overload", true, false, false); });
    }
    
    private void addKaelSkills(JPanel panel) {
        addSkillRow(panel, "🌑 SHADOW STEP", "USE (100 energy)", new Color(75, 0, 130),
            "<html>Teleport one of your ships to an empty cell.<br>Damaged cells become wreckage.</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(1, "Shadow Step", false, false, true); });
        
        addSkillRow(panel, "⚔️ SHADOW BLADE", "USE (150 energy)", new Color(100, 150, 255),
            "<html>Destroy every other cell in a row or column.<br>Choose horizontal or vertical.</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(2, "Shadow Blade", true, true, false); });
        
        addSkillRow(panel, "🌑🌑🌑 SHADOW DOMAIN", "USE (200 energy)", new Color(50, 0, 100),
            "<html>Creates a 3x3 shadow explosion.<br>Destroys all cells in the area!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(3, "Shadow Domain", true, false, false); });
    }
    
    private void addValeriusSkills(JPanel panel) {
        addSkillRow(panel, "📡 RADAR OVERLOAD", "USE (50 mana)", new Color(169, 169, 169),
            "<html>Disables enemy skills for 2 turns.<br>Perfect for setting up combos!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(1, "Radar Overload", false, false, false); });
        
        addSkillRow(panel, "🎯 PRECISION STRIKE", "USE (120 mana)", new Color(200, 100, 0),
            "<html>Next attack destroys 2 cells in a line.<br>Choose horizontal or vertical.</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(2, "Precision Strike", true, true, false); });
        
        addSkillRow(panel, "🏰 FORTRESS MODE", "USE (300 mana)", new Color(100, 50, 0),
            "<html>Shields ALL your ships for 2 turns.<br>Each shield blocks 1 hit!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(3, "Fortress Mode", false, false, false); });
    }
    
    private void addSkyeSkills(JPanel panel) {
        addSkillRow(panel, "🌿 CATNIP EXPLOSION", "USE (70 mana)", new Color(50, 205, 50),
            "<html>Destroys a 2x2 area on enemy board.<br>Distracts enemy ships!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(1, "Catnip Explosion", true, false, false); });
        
        addSkillRow(panel, "🔴 LASER POINTER", "USE (50 mana)", new Color(255, 100, 100),
            "<html>Enemy skips their next turn!<br>Perfect for stalling.</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(2, "Laser Pointer", false, false, false); });
        
        addSkillRow(panel, "😺 NINE LIVES", "USE (200 mana)", new Color(255, 165, 0),
            "<html>Revives a fallen ship on YOUR board!<br>Cats always land on their feet!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(3, "Nine Lives", true, false, true); });
    }
    
    private void addMorganaSkills(JPanel panel) {
        addSkillRow(panel, "🎵 ENCHANTING MELODY", "USE (40 mana)", new Color(64, 224, 208),
            "<html>Confuses enemy for 2 turns.<br>They see fake hit/miss results!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(1, "Enchanting Melody", false, false, false); });
        
        addSkillRow(panel, "🌊 WHIRLPOOL TRAP", "USE (80 mana)", new Color(0, 150, 200),
            "<html>Hits 3 cells in a vertical column!<br>Damages ships in that area.</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(2, "Whirlpool Trap", true, false, false); });
        
        addSkillRow(panel, "🌊 TIDAL WAVE", "USE (300 mana)", new Color(70, 130, 200),
            "<html>🌊 TIDAL WAVE: Summons a devastating wave!<br>Hits 6 random cells for massive damage!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(3, "Tidal Wave", false, false, false); });
    }
    
    private void addAerisSkills(JPanel panel) {
        addSkillRow(panel, "🛡️ ADAPTIVE INSTINCT", "USE (120 mana)", new Color(100, 200, 255),
            "<html>Shields a ship for 2 turns.<br>Blocked hits don't damage the ship!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(1, "Adaptive Instinct", true, false, true); });
        
        addSkillRow(panel, "⚡ MULTITASK OVERDRIVE", "USE (Restores 200 mana)", new Color(255, 215, 0),
            "<html>Restores 200 mana instantly.<br>3 turn cooldown.</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(2, "Multitask Overdrive", false, false, false); });
        
        addSkillRow(panel, "⚔️ RELENTLESS ASCENT", "USE (500 mana)", new Color(200, 50, 50),
            "<html>Destroys an entire column!<br>More damage when low on HP.</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(3, "Relentless Ascent", true, false, false); });
    }
    
    private void addSeleneSkills(JPanel panel) {
        addSkillRow(panel, "🔮 LUNAR REVEAL", "USE (60 mana)", new Color(200, 150, 255),
            "<html>Reveals a 3x3 area on enemy board.<br>During night: reveals 4x4 and damages!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(1, "Lunar Reveal", true, false, false); });
        
        addSkillRow(panel, "🌙 CRESCENT STRIKE", "USE (120 mana)", new Color(150, 100, 200),
            "<html>Destroys a cross pattern (5 cells).<br>During night: destroys 9 cells!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(2, "Crescent Strike", true, false, false); });
        
        addSkillRow(panel, "⭐ STARFALL LINK", "USE (300 mana)", new Color(255, 215, 0),
            "<html>ULTIMATE: Destroys 3 random cells and links 2 cells for 2 turns!<br>During night: 5 stars + 2 links!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(3, "Starfall Link", false, false, false); });
    }
    
    private void addFlueSkills(JPanel panel) {
        addSkillRow(panel, "💻 CORRUPTION.EXE", "USE (100 mana)", new Color(0, 255, 127),
            "<html>Infects a cell with a virus.<br>Spread to adjacent cells every 4 turns!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(1, "Corruption.EXE", true, false, false); });
        
        addSkillRow(panel, "🛡️ FORTIFICATION.GRID", "USE (80 mana)", new Color(0, 200, 100),
            "<html>Repairs a damaged ship segment.<br>Restores 1 health to the ship!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(2, "Fortification.GRID", true, false, true); });
        
        addSkillRow(panel, "💀 KERNEL.DECIMATION.REQ", "USE (300 mana)", new Color(200, 0, 0),
            "<html>Massive damage to target cell.<br>If target is infected, destroys 3x3 area!</html>",
            e -> { if (skillListener != null) skillListener.onSkillUsed(3, "Kernel.Decimation.REQ", true, false, false); });
    }
    
    private void addGenericSkills(JPanel panel) {
        addSkillRow(panel, "⚔️ BASIC ATTACK", "FIRE", Color.WHITE,
            "Standard shot at enemy",
            e -> {});
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
}