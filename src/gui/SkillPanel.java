package gui;

import characters.Aeris;
import characters.Flue;
import characters.GameCharacter;
import characters.Jiji;
import characters.Kael;
import characters.Morgana;
import characters.Selene;
import characters.Skye;
import characters.Valerius;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

/**
 * SkillPanel - TideBound naval-themed redesign.
 *
 * Replaces the original 920-line per-character implementation with a
 * data-driven approach: each character has a list of {@link SkillDef}s
 * and the panel renders them all the same way.
 *
 * Visual layout (horizontal strip):
 *   [ MANA BAR  ████░░ 440/440 ]    [ PASSIVE: Firewall ]
 *   [ skill card ] [ skill card ] [ skill card ] [ skill card ]
 *
 * Each skill card is a compact stamped-metal plate that IS the button:
 *   - skill name (top)
 *   - cyan accent strip
 *   - cost / cooldown info
 *   - hover lights up cyan; disabled cards grey out
 *
 * All cards use neutral cyan/copper styling (per user preference).
 * Public API (SkillButtonListener, setBoards, setSkillListener,
 * stopTimers, updateUI) is preserved unchanged.
 */
public class SkillPanel extends JPanel {

    // ===================================================================
    // PUBLIC API (preserved from original)
    // ===================================================================
    public interface SkillButtonListener {
        void onSkillUsed(int skillNumber, String skillName,
                         boolean requiresTarget, boolean requiresDirection,
                         boolean targetsOwnBoard);
    }

    public void setSkillListener(SkillButtonListener listener) {
        this.skillListener = listener;
    }

    public void setBoards(BoardPanel player, BoardPanel enemy) {
        this.playerBoardPanel = player;
        this.enemyBoardPanel = enemy;
    }

    public void stopTimers() {
        if (refreshTimer != null) refreshTimer.stop();
    }

    @Override public void updateUI() {
        super.updateUI();
        if (manaBar != null) manaBar.repaint();
        if (passiveBadge != null) passiveBadge.repaint();
        if (cards != null) {
            for (SkillCard card : cards) card.repaint();
        }
    }

    // ===================================================================
    // FIELDS
    // ===================================================================
    private final GameCharacter character;
    private SkillButtonListener skillListener;
    private BoardPanel enemyBoardPanel;
    private BoardPanel playerBoardPanel;

    private ManaBar manaBar;
    private PassiveBadge passiveBadge;
    private final List<SkillCard> cards = new ArrayList<>();

    private final Timer refreshTimer;

    // ===================================================================
    // PALETTE (matches MultiplayerBattlePanel)
    // ===================================================================
    private static final Color FRAME_FILL     = new Color(0x1E, 0x45, 0x48);
    private static final Color FRAME_FILL_HI  = new Color(0x2A, 0x5A, 0x5E);
    private static final Color FRAME_BORDER   = new Color(0x3A, 0x7A, 0x7E);
    private static final Color FRAME_SHADOW   = new Color(0x08, 0x18, 0x1A);
    private static final Color CYAN_ACCENT    = new Color(0x5F, 0xD4, 0xE0);
    private static final Color COPPER         = new Color(0xC9, 0x7A, 0x3D);
    private static final Color RIVET          = new Color(0x6B, 0x4A, 0x2A);
    private static final Color TEXT_LIGHT     = new Color(0xE8, 0xF4, 0xF6);
    private static final Color TEXT_DIM       = new Color(0x8A, 0xA8, 0xAC);
    private static final Color MANA_FILL      = new Color(0x5F, 0xD4, 0xE0);
    private static final Color MANA_LOW       = new Color(0xE0, 0xC0, 0x5F);

    private static final Font FONT_LABEL  = pickFont(13f, Font.BOLD);
    private static final Font FONT_SMALL  = pickFont(10f, Font.PLAIN);
    private static final Font FONT_TINY   = pickFont(9f,  Font.PLAIN);
    private static final Font FONT_NAME   = pickFont(11f, Font.BOLD);
    private static final Font FONT_BIG    = pickFont(16f, Font.BOLD);

    private static Font pickFont(float size, int style) {
        String[] candidates = {"Press Start 2P", "Consolas", "Monospaced"};
        for (String name : candidates) {
            Font f = new Font(name, style, (int) size);
            if (f.getFamily().equalsIgnoreCase(name) || name.equals("Monospaced")) return f;
        }
        return new Font(Font.MONOSPACED, style, (int) size);
    }

    // ===================================================================
    // SKILL DEFINITIONS — data-driven
    // ===================================================================
    private static final class SkillDef {
        final int slotNumber;
        final String name;
        final int cost;
        final boolean requiresTarget;
        final boolean requiresDirection;
        final boolean targetsOwnBoard;
        final String tooltip;

        SkillDef(int slot, String name, int cost,
                 boolean reqTarget, boolean reqDir, boolean ownBoard,
                 String tooltip) {
            this.slotNumber = slot;
            this.name = name;
            this.cost = cost;
            this.requiresTarget = reqTarget;
            this.requiresDirection = reqDir;
            this.targetsOwnBoard = ownBoard;
            this.tooltip = tooltip;
        }
    }

    /** Skill list per character. Matches the original SkillPanel exactly. */
    private static final Map<String, SkillDef[]> SKILLS = new HashMap<>();
    static {
        SKILLS.put("Jiji", new SkillDef[]{
            new SkillDef(1, "DATA LEECH",       50,  true,  false, false, "Reveals 2 random enemy cells. Marks them for chain reactions."),
            new SkillDef(2, "OVERCLOCK",        120, false, false, false, "Next shot fires twice. Grants extra turn on hit."),
            new SkillDef(3, "SYSTEM OVERLOAD",  400, true,  false, false, "Reveals a full enemy ship. With Overclock, destroys it."),
        });
        SKILLS.put("Kael", new SkillDef[]{
            new SkillDef(1, "SHADOW STEP",   100, false, false, true,  "Teleport one of your ships to an empty cell."),
            new SkillDef(2, "SHADOW BLADE",  150, true,  true,  false, "Destroy every other cell in a row or column."),
            new SkillDef(3, "SHADOW DOMAIN", 200, true,  false, false, "3x3 shadow explosion. Destroys all cells in the area."),
        });
        SKILLS.put("Valerius", new SkillDef[]{
            new SkillDef(1, "RADAR OVERLOAD",  50,  false, false, false, "Disables enemy skills for 2 turns."),
            new SkillDef(2, "PRECISION STRIKE",120, true,  true,  false, "Next attack destroys 2 cells in a line."),
            new SkillDef(3, "FORTRESS MODE",   300, false, false, false, "Shields ALL your ships for 2 turns."),
        });
        SKILLS.put("Skye", new SkillDef[]{
            new SkillDef(1, "CATNIP EXPLOSION", 70,  true,  false, false, "Destroys a 2x2 area on enemy board."),
            new SkillDef(2, "LASER POINTER",    50,  false, false, false, "Enemy skips their next turn."),
            new SkillDef(3, "NINE LIVES",       200, true,  false, true,  "Revives a fallen ship on YOUR board."),
        });
        SKILLS.put("Morgana", new SkillDef[]{
            new SkillDef(1, "ENCHANTING MELODY", 40,  false, false, false, "Confuses enemy for 2 turns. Fake hit/miss results."),
            new SkillDef(2, "WHIRLPOOL TRAP",    80,  true,  false, false, "Hits 3 cells in a vertical column."),
            new SkillDef(3, "TIDAL WAVE",        300, false, false, false, "6 random cells. May trigger Siren's Call & Ocean's Blessing."),
        });
        SKILLS.put("Aeris", new SkillDef[]{
            new SkillDef(1, "ADAPTIVE INSTINCT",   120, true,  false, true,  "Shields a ship for 2 turns. Blocked hits don't damage it."),
            new SkillDef(2, "MULTITASK OVERDRIVE", 0,   false, false, false, "Restores 200 mana instantly. 3 turn cooldown."),
            new SkillDef(3, "RELENTLESS ASCENT",   500, true,  false, false, "Destroys an entire column. Stronger when low HP."),
        });
        SKILLS.put("Selene", new SkillDef[]{
            new SkillDef(1, "LUNAR REVEAL",   60,  true,  false, false, "Reveals 3x3 area. At night: 4x4 and damages."),
            new SkillDef(2, "CRESCENT STRIKE",120, true,  false, false, "Destroys a 5-cell cross. At night: 9 cells."),
            new SkillDef(3, "STARFALL LINK",  300, false, false, false, "ULTIMATE: 3 random cells + 2 linked. At night: 5+2."),
        });
        SKILLS.put("Flue", new SkillDef[]{
            new SkillDef(1, "CORRUPTION.EXE",        100, true, false, false, "Infects a cell. Spreads every 4 turns."),
            new SkillDef(2, "FORTIFICATION.GRID",    80,  true, false, true,  "Repairs a damaged ship segment."),
            new SkillDef(3, "KERNEL.DECIMATION.REQ", 300, true, false, false, "Massive damage. If infected, destroys 3x3."),
        });
    }

    // ===================================================================
    // CONSTRUCTOR
    // ===================================================================
    public SkillPanel(GameCharacter character) {
        this.character = character;

        setOpaque(false);
        setLayout(new BorderLayout(0, 8));

        // Top row: mana + passive
        JPanel topRow = new JPanel(new GridBagLayout());
        topRow.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(0, 4, 0, 4);

        manaBar = new ManaBar();
        passiveBadge = new PassiveBadge();

        gc.gridx = 0; gc.weightx = 0.6; topRow.add(manaBar, gc);
        gc.gridx = 1; gc.weightx = 0.4; topRow.add(passiveBadge, gc);

        add(topRow, BorderLayout.NORTH);

        // Bottom row: skill cards spread evenly
        JPanel cardRow = new JPanel(new GridBagLayout());
        cardRow.setOpaque(false);
        GridBagConstraints cc = new GridBagConstraints();
        cc.fill = GridBagConstraints.BOTH;
        cc.insets = new Insets(0, 6, 0, 6);
        cc.weightx = 1.0;
        cc.weighty = 1.0;

        SkillDef[] defs = (character == null) ? new SkillDef[0]
                        : SKILLS.getOrDefault(character.getClass().getSimpleName(), new SkillDef[0]);
        for (int i = 0; i < defs.length; i++) {
            SkillCard card = new SkillCard(defs[i]);
            cards.add(card);
            cc.gridx = i;
            cardRow.add(card, cc);
        }
        add(cardRow, BorderLayout.CENTER);

        // Refresh mana/passive every 500ms
        refreshTimer = new Timer(500, e -> {
            if (manaBar != null) manaBar.repaint();
            if (passiveBadge != null) passiveBadge.repaint();
            for (SkillCard c : cards) c.repaint();
        });
        refreshTimer.start();
    }

    // ===================================================================
    // MANA BAR
    // ===================================================================
    private class ManaBar extends JPanel {
        ManaBar() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 36));
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // backplate
            g2.setColor(FRAME_SHADOW);
            g2.fillRoundRect(2, 3, w - 4, h - 4, 6, 6);
            g2.setPaint(new GradientPaint(0, 0, FRAME_FILL, 0, h, FRAME_FILL_HI));
            g2.fillRoundRect(0, 0, w - 4, h - 4, 6, 6);

            // label
            g2.setFont(FONT_TINY);
            g2.setColor(TEXT_DIM);
            String label = getResourceLabel();
            g2.drawString(label, 12, 14);

            // numeric value
            int cur = getCurrentMana();
            int max = getMaxMana();
            String val = cur + " / " + max;
            g2.setFont(FONT_LABEL);
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(TEXT_LIGHT);
            g2.drawString(val, w - 8 - fm.stringWidth(val), 16);

            // bar
            int barX = 12, barY = 20;
            int barW = w - 24, barH = h - 28;
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(barX, barY, barW, barH, 4, 4);

            float pct = (max > 0) ? (cur / (float) max) : 0f;
            int fillW = (int)(barW * pct);

            Color fill = pct > 0.3f ? MANA_FILL : MANA_LOW;
            g2.setPaint(new GradientPaint(barX, barY, fill,
                                          barX, barY + barH, fill.darker()));
            g2.fillRoundRect(barX, barY, fillW, barH, 4, 4);

            // shimmer line on top of fill
            if (fillW > 0) {
                g2.setColor(new Color(255, 255, 255, 70));
                g2.fillRect(barX, barY + 1, fillW, 1);
            }

            // border
            g2.setColor(FRAME_BORDER);
            g2.drawRoundRect(barX, barY, barW, barH, 4, 4);

            g2.setStroke(new BasicStroke(1f));
            g2.setColor(FRAME_BORDER);
            g2.drawRoundRect(0, 0, w - 5, h - 5, 6, 6);

            g2.dispose();
        }
    }

    // ===================================================================
    // PASSIVE BADGE
    // ===================================================================
    private class PassiveBadge extends JPanel {
        PassiveBadge() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 36));
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            g2.setColor(FRAME_SHADOW);
            g2.fillRoundRect(2, 3, w - 4, h - 4, 6, 6);
            g2.setPaint(new GradientPaint(0, 0, FRAME_FILL, 0, h, FRAME_FILL_HI));
            g2.fillRoundRect(0, 0, w - 4, h - 4, 6, 6);

            // accent strip on left
            g2.setColor(COPPER);
            g2.fillRect(0, 6, 3, h - 16);

            g2.setFont(FONT_TINY);
            g2.setColor(TEXT_DIM);
            g2.drawString("PASSIVE", 12, 14);

            g2.setFont(FONT_NAME);
            g2.setColor(TEXT_LIGHT);
            String passive = getPassiveText();
            FontMetrics fm = g2.getFontMetrics();
            String fitted = ellipsize(passive, fm, w - 18);
            g2.drawString(fitted, 12, 28);

            g2.setColor(FRAME_BORDER);
            g2.drawRoundRect(0, 0, w - 5, h - 5, 6, 6);

            g2.dispose();
        }
    }

    // ===================================================================
    // SKILL CARD (the button itself)
    // ===================================================================
    private class SkillCard extends JPanel {
        private final SkillDef def;
        private boolean hover = false;
        private boolean pressed = false;

        SkillCard(SkillDef def) {
            this.def = def;
            setOpaque(false);
            setPreferredSize(new Dimension(0, 64));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("<html><b>" + def.name + "</b><br>" + def.tooltip + "</html>");

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; pressed = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                    if (isUsable() && contains(e.getPoint())) {
                        if (skillListener != null) {
                            skillListener.onSkillUsed(def.slotNumber, capName(def.name),
                                                      def.requiresTarget, def.requiresDirection,
                                                      def.targetsOwnBoard);
                        }
                    }
                }
            });
        }

        private boolean isUsable() {
            return getCurrentMana() >= def.cost;
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int offset = pressed ? 2 : 0;
            boolean usable = isUsable();

            // shadow
            g2.setColor(FRAME_SHADOW);
            g2.fillRoundRect(2, 3, w - 4, h - 4, 8, 8);

            // body gradient
            Color top = usable ? FRAME_FILL_HI : FRAME_FILL.darker();
            Color bot = usable ? FRAME_FILL : FRAME_FILL.darker().darker();
            g2.setPaint(new GradientPaint(0, 0, top, 0, h, bot));
            g2.fillRoundRect(0, offset, w - 4, h - 4 - offset, 8, 8);

            // accent strip on left
            g2.setColor(usable ? CYAN_ACCENT : TEXT_DIM);
            g2.fillRect(0, 8 + offset, 3, h - 20);

            // hover halo
            if (hover && usable) {
                g2.setColor(new Color(CYAN_ACCENT.getRed(), CYAN_ACCENT.getGreen(), CYAN_ACCENT.getBlue(), 30));
                g2.fillRoundRect(0, offset, w - 4, h - 4 - offset, 8, 8);
            }

            // border
            g2.setStroke(new BasicStroke(1.6f));
            g2.setColor(hover && usable ? CYAN_ACCENT : FRAME_BORDER);
            g2.drawRoundRect(0, offset, w - 5, h - 5 - offset, 8, 8);
            g2.setStroke(new BasicStroke(1f));

            // rivets
            g2.setColor(RIVET);
            g2.fillOval(6, 6 + offset, 4, 4);
            g2.fillOval(w - 14, 6 + offset, 4, 4);
            g2.fillOval(6, h - 14 - offset, 4, 4);
            g2.fillOval(w - 14, h - 14 - offset, 4, 4);

            // skill name
            g2.setFont(FONT_NAME);
            FontMetrics fmName = g2.getFontMetrics();
            g2.setColor(usable ? TEXT_LIGHT : TEXT_DIM);
            String name = def.name;
            String fitted = ellipsize(name, fmName, w - 18);
            g2.drawString(fitted, 10, 22 + offset);

            // accent line under name
            g2.setColor(usable ? CYAN_ACCENT : TEXT_DIM);
            g2.fillRect(10, 26 + offset, Math.min(40, w - 20), 1);

            // cost on the bottom-left
            g2.setFont(FONT_SMALL);
            FontMetrics fmCost = g2.getFontMetrics();
            String cost = (def.cost == 0) ? "FREE" : (def.cost + " mana");
            g2.setColor(TEXT_DIM);
            g2.drawString(cost, 10, h - 12 - offset);

            // status / use chip on the right
            String chip = usable ? "USE" : "LOW MANA";
            int chipPad = 8;
            int chipW = fmCost.stringWidth(chip) + chipPad * 2;
            int chipH = 16;
            int chipX = w - 12 - chipW;
            int chipY = h - 14 - chipH - offset;

            Color chipColor = usable ? CYAN_ACCENT : TEXT_DIM;
            g2.setColor(usable ? new Color(0x12, 0x2A, 0x2E) : new Color(0, 0, 0, 80));
            g2.fillRoundRect(chipX, chipY, chipW, chipH, 4, 4);
            g2.setColor(chipColor);
            g2.drawRoundRect(chipX, chipY, chipW, chipH, 4, 4);
            g2.setFont(FONT_TINY);
            FontMetrics fmChip = g2.getFontMetrics();
            int cx = chipX + (chipW - fmChip.stringWidth(chip)) / 2;
            int cy = chipY + (chipH + fmChip.getAscent()) / 2 - 3;
            g2.setColor(chipColor);
            g2.drawString(chip, cx, cy);

            g2.dispose();
        }
    }

    // ===================================================================
    // RESOURCE / PASSIVE LOOKUP (matches original)
    // ===================================================================
    private int getCurrentMana() {
        if (character == null) return 0;
        if (character instanceof Jiji)     return ((Jiji)     character).getCurrentMana();
        if (character instanceof Kael)     return ((Kael)     character).getCurrentEnergy();
        if (character instanceof Valerius) return ((Valerius) character).getCurrentMana();
        if (character instanceof Skye)     return ((Skye)     character).getCurrentMana();
        if (character instanceof Morgana)  return ((Morgana)  character).getCurrentMana();
        if (character instanceof Aeris)    return ((Aeris)    character).getCurrentMana();
        if (character instanceof Selene)   return ((Selene)   character).getCurrentMana();
        if (character instanceof Flue)     return ((Flue)     character).getCurrentMana();
        return 0;
    }

    private int getMaxMana() {
        if (character == null) return 1;
        if (character instanceof Jiji)     return ((Jiji)     character).getMaxMana();
        if (character instanceof Kael)     return ((Kael)     character).getMaxEnergy();
        if (character instanceof Valerius) return ((Valerius) character).getMaxMana();
        if (character instanceof Skye)     return ((Skye)     character).getMaxMana();
        if (character instanceof Morgana)  return ((Morgana)  character).getMaxMana();
        if (character instanceof Aeris)    return ((Aeris)    character).getMaxMana();
        if (character instanceof Selene)   return ((Selene)   character).getMaxMana();
        if (character instanceof Flue)     return ((Flue)     character).getMaxMana();
        return 1;
    }

    private String getResourceLabel() {
        return (character instanceof Kael) ? "ENERGY" : "MANA";
    }

    private String getPassiveText() {
        if (character == null) return "—";
        if (character instanceof Jiji)     return "Firewall — blocks 1 hit / 4 turns";
        if (character instanceof Kael)     return "Shadow Walk — ships can hide";
        if (character instanceof Valerius) {
            Valerius v = (Valerius) character;
            return v.isScrapperResolveActive()
                ? "Scrapper's Resolve ACTIVE — 10% DR"
                : "Scrapper's Resolve — DR below 20% HP";
        }
        if (character instanceof Skye) {
            Skye s = (Skye) character;
            int left = 9 - s.getReviveUses();
            return "Nine Lives — " + left + " remaining";
        }
        if (character instanceof Morgana)  return "Ocean's Embrace — first hit blocked";
        if (character instanceof Aeris)    return "Stun Immune — below 40% HP";
        if (character instanceof Selene) {
            Selene s = (Selene) character;
            return s.isNightTime()
                ? "MOON'S BLESSING — skills enhanced"
                : "Night in " + s.getTurnsUntilNight() + " turns";
        }
        if (character instanceof Flue)     return "Lone.Resolve — 15% DR no shields";
        return "—";
    }

    /** Convert "DATA LEECH" → "Data Leech" so skill names match what the
     *  game logic expects (the original used title case for callbacks). */
    private static String capName(String upper) {
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = true;
        for (char ch : upper.toLowerCase().toCharArray()) {
            if (Character.isWhitespace(ch) || ch == '.') {
                sb.append(ch);
                nextUpper = true;
            } else if (nextUpper) {
                sb.append(Character.toUpperCase(ch));
                nextUpper = false;
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private static String ellipsize(String s, FontMetrics fm, int maxW) {
        if (fm.stringWidth(s) <= maxW) return s;
        String ell = "…";
        for (int i = s.length() - 1; i > 0; i--) {
            String trial = s.substring(0, i) + ell;
            if (fm.stringWidth(trial) <= maxW) return trial;
        }
        return ell;
    }
}