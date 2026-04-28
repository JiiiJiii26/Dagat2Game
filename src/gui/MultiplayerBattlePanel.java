package gui;

import characters.GameCharacter;
import game.LocalMultiplayer;
import game.ShotResult;
import main.Main;
import models.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * MultiplayerBattlePanel - TideBound naval-themed redesign (revision 2).
 *
 * Improvements over rev 1:
 *  - Whose-turn clarity: full-banner color flash on turn change + big P1/P2 indicator
 *  - Cell-snapped hover highlight on the active board (replaces floaty crosshair)
 *  - Hit/Miss/Sunk feedback: cell flash + shockwave + lock-on brackets + screen shake
 *  - Status banner moved to overlay the active board (closer to where player is looking)
 *  - Right sidebar: character portrait + ability dossier + skill panel host
 *  - Bigger permanent hit/miss markers drawn on overlay
 *
 * Preserves all original game logic.
 */
public class MultiplayerBattlePanel extends JPanel {

    // ---------- Game state ----------
    private final LocalMultiplayer game;
    private BoardPanel player1BoardPanel;
    private BoardPanel player2BoardPanel;
    private SkillPanel currentSkillPanel;
    private int currentPlayer = 1;

    // ---------- Skill targeting (preserved from original) ----------
    private boolean waitingForSkillTarget = false;
    private int currentSkillPlayer = 0;
    private int currentSkillNumber = 0;
    private String currentSkillName = "";
    private boolean waitingForDirection = false;
    private boolean skillDirectionHorizontal = true;
    private boolean skillTargetsOwnBoard = false;
    private boolean waitingForShadowStepSource = false;
    private int shadowStepSourceX = -1;
    private int shadowStepSourceY = -1;

    // ---------- UI ----------
    private TurnBanner turnBanner;
    private BoardFrame leftBoardFrame;
    private BoardFrame rightBoardFrame;
    private SkillStrip skillStrip;
    private FxOverlay fxOverlay;
    private TimerGauge timerGauge;
    private NavalButton backButton;

    // ---------- Palette ----------
    private static final Color BG_DEEP        = new Color(0x0F, 0x23, 0x26);
    private static final Color FRAME_FILL     = new Color(0x1E, 0x45, 0x48);
    private static final Color FRAME_FILL_HI  = new Color(0x2A, 0x5A, 0x5E);
    private static final Color FRAME_BORDER   = new Color(0x3A, 0x7A, 0x7E);
    private static final Color FRAME_SHADOW   = new Color(0x08, 0x18, 0x1A);
    private static final Color CYAN_ACCENT    = new Color(0x5F, 0xD4, 0xE0);
    private static final Color COPPER         = new Color(0xC9, 0x7A, 0x3D);
    private static final Color RIVET          = new Color(0x6B, 0x4A, 0x2A);
    private static final Color TEXT_LIGHT     = new Color(0xE8, 0xF4, 0xF6);
    private static final Color TEXT_DIM       = new Color(0x8A, 0xA8, 0xAC);
    private static final Color OCEAN_DEEP     = new Color(0x0A, 0x1A, 0x22);
    private static final Color OCEAN_HI       = new Color(0x1F, 0x4A, 0x52);
    private static final Color GRID_LINE      = new Color(0x2D, 0x5D, 0x6A, 140);
    private static final Color TARGET_RED     = new Color(0xE0, 0x5F, 0x5F);
    private static final Color HIT_GOLD       = new Color(0xE0, 0xC0, 0x5F);
    private static final Color MISS_WHITE     = new Color(0xE0, 0xF0, 0xF6);
    private static final Color SUNK_AMBER     = new Color(0xE0, 0x80, 0x3F);
    private static final Color P1_COLOR       = new Color(0x5F, 0xD4, 0xE0);
    private static final Color P2_COLOR       = new Color(0xE0, 0xA0, 0x5F);

    private static final Font FONT_HEADER  = pickFont(22f, Font.BOLD);
    private static final Font FONT_LABEL   = pickFont(14f, Font.BOLD);
    private static final Font FONT_SMALL   = pickFont(11f, Font.PLAIN);
    private static final Font FONT_TINY    = pickFont(9f,  Font.PLAIN);
    private static final Font FONT_BUTTON  = pickFont(13f, Font.BOLD);
    private static final Font FONT_BIG     = pickFont(28f, Font.BOLD);
    private static final Font FONT_HUGE    = pickFont(40f, Font.BOLD);

    private static Font pickFont(float size, int style) {
        String[] candidates = {"Press Start 2P", "Consolas", "Monospaced"};
        for (String name : candidates) {
            Font f = new Font(name, style, (int) size);
            if (f.getFamily().equalsIgnoreCase(name) || name.equals("Monospaced")) return f;
        }
        return new Font(Font.MONOSPACED, style, (int) size);
    }

    // ===================================================================
    // CONSTRUCTOR
    // ===================================================================
    public MultiplayerBattlePanel(LocalMultiplayer game) {
        this.game = game;
        setLayout(new BorderLayout());
        setBackground(BG_DEEP);
        buildUI();
        updateBoardViews();
    }

    private void buildUI() {
        turnBanner = new TurnBanner();
        add(turnBanner, BorderLayout.NORTH);

        // Bottom: skill strip (full-width)
        skillStrip = new SkillStrip();
        add(skillStrip, BorderLayout.SOUTH);

        // Center: layered pane with 2 boards + FX overlay
        JLayeredPane layered = new JLayeredPane();
        layered.setBackground(BG_DEEP);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        leftBoardFrame  = new BoardFrame("YOUR FLEET");
        rightBoardFrame = new BoardFrame("ENEMY WATERS");

        gbc.gridx = 0; gbc.weightx = 0.5; center.add(leftBoardFrame,  gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; center.add(rightBoardFrame, gbc);

        fxOverlay = new FxOverlay();

        layered.setLayout(new LayoutManager() {
            @Override public void addLayoutComponent(String n, Component c) {}
            @Override public void removeLayoutComponent(Component c) {}
            @Override public Dimension preferredLayoutSize(Container p) { return new Dimension(1280, 700); }
            @Override public Dimension minimumLayoutSize(Container p)   { return new Dimension(800, 500); }
            @Override public void layoutContainer(Container p) {
                int w = p.getWidth(), h = p.getHeight();
                center.setBounds(0, 0, w, h);
                fxOverlay.setBounds(0, 0, w, h);
            }
        });
        layered.add(center,    JLayeredPane.DEFAULT_LAYER);
        layered.add(fxOverlay, JLayeredPane.PALETTE_LAYER);

        add(layered, BorderLayout.CENTER);
    }

    // ===================================================================
    // TURN BANNER
    // ===================================================================
    private class TurnBanner extends JPanel {
        private long flashStart = 0;
        private static final long FLASH_DURATION = 1000;
        private Timer flashTicker;

        TurnBanner() {
            setOpaque(false);
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(0, 96));

            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 22));
            left.setOpaque(false);
            backButton = new NavalButton("\u25C0 BACK", new Color(0xD0, 0x55, 0x55));
            backButton.setPreferredSize(new Dimension(130, 48));
            backButton.addActionListener(e -> confirmBackToMenu());
            left.add(backButton);
            add(left, BorderLayout.WEST);

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 18));
            right.setOpaque(false);
            timerGauge = new TimerGauge(30);
            right.add(timerGauge);
            add(right, BorderLayout.EAST);

            flashTicker = new Timer(40, e -> {
                if (System.currentTimeMillis() - flashStart > FLASH_DURATION) flashTicker.stop();
                repaint();
            });
        }

        public void triggerFlash() {
            flashStart = System.currentTimeMillis();
            flashTicker.restart();
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            paintMetalBar(g2, 0, 0, w, h);

            Color accent = (game != null && game.isPlayer1Turn()) ? P1_COLOR : P2_COLOR;
            String pTag = (game != null && game.isPlayer1Turn()) ? "P1" : "P2";

            long flashElapsed = System.currentTimeMillis() - flashStart;
            if (flashElapsed < FLASH_DURATION) {
                float t = flashElapsed / (float) FLASH_DURATION;
                float alpha = (1f - t) * 0.55f;
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(),
                                      (int)(alpha * 255)));
                g2.fillRect(0, 0, w, h);
            }

            int plateW = Math.min(680, w - 380);
            int plateH = 60;
            int px = (w - plateW) / 2;
            int py = (h - plateH) / 2;

            g2.setFont(FONT_HUGE);
            FontMetrics fmHuge = g2.getFontMetrics();
            int tagX = px - 80;
            int tagY = py + (plateH + fmHuge.getAscent()) / 2 - 8;
            g2.setColor(FRAME_SHADOW);
            g2.drawString(pTag, tagX + 2, tagY + 2);
            g2.setColor(accent);
            g2.drawString(pTag, tagX, tagY);

            paintEngravedPlate(g2, px, py, plateW, plateH, accent);

            String title = (game == null) ? "FLEET DEPLOYMENT"
                : (game.isPlayer1Turn()
                    ? "PLAYER 1'S TURN  \u2014  STRIKE ENEMY WATERS"
                    : "PLAYER 2'S TURN  \u2014  STRIKE ENEMY WATERS");
            g2.setFont(FONT_HEADER);
            FontMetrics fm = g2.getFontMetrics();
            int tx = px + (plateW - fm.stringWidth(title)) / 2;
            int ty = py + (plateH + fm.getAscent()) / 2 - 4;
            g2.setColor(FRAME_SHADOW);
            g2.drawString(title, tx + 1, ty + 1);
            g2.setColor(accent);
            g2.drawString(title, tx, ty);

            paintStatusLight(g2, px - 28, py + plateH / 2, accent);
            paintStatusLight(g2, px + plateW + 28, py + plateH / 2, accent);

            g2.dispose();
        }
    }

    private void confirmBackToMenu() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Return to main menu? Current battle will be lost.",
            "Return to Menu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) Main.showMainMenu();
    }

    // ===================================================================
    // CIRCULAR TIMER
    // ===================================================================
    private class TimerGauge extends JPanel {
        private final int totalSeconds;
        private int secondsLeft;
        private Timer ticker;
        private Runnable onExpire;

        TimerGauge(int total) {
            this.totalSeconds = total;
            this.secondsLeft = total;
            setOpaque(false);
            setPreferredSize(new Dimension(72, 72));
        }

        public void setOnExpire(Runnable r) { this.onExpire = r; }
        public void resetTimer() { secondsLeft = totalSeconds; repaint(); }

        public void startTimer() {
            stopTimer();
            ticker = new Timer(1000, e -> {
                secondsLeft--;
                if (secondsLeft <= 0) {
                    secondsLeft = 0;
                    stopTimer();
                    if (onExpire != null) onExpire.run();
                }
                repaint();
            });
            ticker.start();
        }

        public void stopTimer() {
            if (ticker != null) { ticker.stop(); ticker = null; }
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int d = Math.min(w, h) - 6;
            int cx = (w - d) / 2, cy = (h - d) / 2;

            g2.setColor(FRAME_SHADOW);
            g2.fillOval(cx + 2, cy + 3, d, d);
            g2.setPaint(new GradientPaint(0, cy, FRAME_FILL_HI, 0, cy + d, FRAME_FILL));
            g2.fillOval(cx, cy, d, d);

            float pct = (float) secondsLeft / (float) totalSeconds;
            Color arcColor = (pct > 0.5f) ? CYAN_ACCENT
                           : (pct > 0.2f) ? HIT_GOLD : TARGET_RED;

            int arcAngle = (int) Math.round(360 * pct);
            g2.setStroke(new BasicStroke(4f));
            g2.setColor(arcColor);
            g2.drawArc(cx + 4, cy + 4, d - 8, d - 8, 90, -arcAngle);
            g2.setStroke(new BasicStroke(1f));

            g2.setColor(FRAME_BORDER);
            g2.drawOval(cx, cy, d, d);

            g2.setFont(FONT_LABEL);
            String txt = secondsLeft + "s";
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(txt)) / 2;
            int ty = (h + fm.getAscent()) / 2 - 4;
            g2.setColor(FRAME_SHADOW);
            g2.drawString(txt, tx + 1, ty + 1);
            g2.setColor(TEXT_LIGHT);
            g2.drawString(txt, tx, ty);

            g2.dispose();
        }
    }

    // ===================================================================
    // BOARD FRAME
    // ===================================================================
    private class BoardFrame extends JPanel {
        private String title;
        private BoardPanel boardPanel;
        private boolean isActive = false;
        private boolean isOwnBoard = true;
        private final JPanel boardHost;
        private MouseAdapter trackingAdapter;
        private long animStart = System.currentTimeMillis();
        private final Timer shimmerTimer;
        private long shakeStart = 0;
        private static final long SHAKE_DURATION = 400;

        BoardFrame(String initialTitle) {
            this.title = initialTitle;
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(40, 16, 16, 16));

            boardHost = new JPanel(new GridBagLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    paintTacticalOcean(g2, getWidth(), getHeight());
                    g2.dispose();
                }
            };
            boardHost.setOpaque(false);
            add(boardHost, BorderLayout.CENTER);

            shimmerTimer = new Timer(80, e -> boardHost.repaint());
            shimmerTimer.start();
        }

        public BoardPanel getBoardPanel() { return boardPanel; }
        public boolean isOwn() { return isOwnBoard; }
        public boolean isActive() { return isActive; }
        public void triggerShake() { shakeStart = System.currentTimeMillis(); }

        public Point getShakeOffset() {
            long elapsed = System.currentTimeMillis() - shakeStart;
            if (elapsed >= SHAKE_DURATION) return new Point(0, 0);
            double t = elapsed / (double) SHAKE_DURATION;
            double decay = 1.0 - t;
            int amp = (int)(5 * decay);
            int dx = (int)(Math.sin(elapsed * 0.05) * amp);
            int dy = (int)(Math.cos(elapsed * 0.07) * amp);
            return new Point(dx, dy);
        }

        public void setBoardPanel(BoardPanel bp, boolean isOwn) {
            this.isOwnBoard = isOwn;
            this.boardPanel = bp;
            boardHost.removeAll();

            bp.setCellSize(44, 44);
            bp.setPreferredSize(new Dimension(44 * 10, 44 * 10));

            GridBagConstraints g = new GridBagConstraints();
            g.gridx = 0; g.gridy = 0;
            boardHost.add(bp, g);

            if (trackingAdapter != null) {
                bp.removeMouseMotionListener((MouseMotionListener) trackingAdapter);
                bp.removeMouseListener(trackingAdapter);
            }
            trackingAdapter = new MouseAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    if (!isActive) {
                        fxOverlay.clearHover();
                        return;
                    }
                    int cw = bp.getCellWidth(), ch = bp.getCellHeight();
                    int col = e.getX() / cw;
                    int row = e.getY() / ch;
                    if (row < 0 || row > 9 || col < 0 || col > 9) {
                        fxOverlay.clearHover();
                        return;
                    }
                    Point cellOriginOnPanel = new Point(col * cw, row * ch);
                    Point cellOnOverlay = SwingUtilities.convertPoint(bp, cellOriginOnPanel, fxOverlay);
                    fxOverlay.updateHover(BoardFrame.this, row, col, cellOnOverlay, cw, ch);
                }
                @Override public void mouseExited(MouseEvent e) { fxOverlay.clearHover(); }
            };
            bp.addMouseMotionListener(trackingAdapter);
            bp.addMouseListener(trackingAdapter);

            boardHost.revalidate();
            boardHost.repaint();
        }

        public void setActive(boolean a)  { this.isActive = a; repaint(); }
        public void setTitle(String t)    { this.title = t; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            paintRivetedFrame(g2, 0, 0, w, h);

            g2.setFont(FONT_LABEL);
            FontMetrics fm = g2.getFontMetrics();
            int titleY = 28;

            Color titleColor;
            String fullTitle;
            if (isActive && !isOwnBoard) {
                titleColor = TARGET_RED;
                fullTitle = title + "  \u2014  \u2694 TARGETING";
            } else if (isActive && isOwnBoard) {
                titleColor = CYAN_ACCENT;
                fullTitle = title + "  \u2014  \u25CE TARGET YOUR FLEET";
            } else {
                titleColor = TEXT_DIM;
                fullTitle = title + "  \u2014  DEFENDING";
            }
            int tx = (w - fm.stringWidth(fullTitle)) / 2;
            g2.setColor(FRAME_SHADOW);
            g2.drawString(fullTitle, tx + 1, titleY + 1);
            g2.setColor(titleColor);
            g2.drawString(fullTitle, tx, titleY);

            if (isActive) {
                Color glow = isOwnBoard ? CYAN_ACCENT : TARGET_RED;
                long t = System.currentTimeMillis();
                float pulse = (float)(0.5 + 0.5 * Math.sin(t / 350.0));
                int alpha = (int)(80 + 100 * pulse);
                g2.setColor(new Color(glow.getRed(), glow.getGreen(), glow.getBlue(), alpha));
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(8, 38, w - 16, h - 46, 10, 10);
                g2.setStroke(new BasicStroke(1f));
            }

            g2.dispose();
        }

        @Override protected void paintChildren(Graphics g) {
            super.paintChildren(g);
            if (!isActive) {
                Graphics2D g2 = (Graphics2D) g.create();
                Insets ins = getInsets();
                int x = ins.left, y = ins.top + 2;
                int w = getWidth() - ins.left - ins.right;
                int h = getHeight() - ins.top - ins.bottom;
                g2.setColor(new Color(0, 0, 0, 130));
                g2.fillRoundRect(x, y, w, h, 8, 8);
                g2.setColor(new Color(0x14, 0x30, 0x3A, 100));
                g2.fillRoundRect(x, y, w, h, 8, 8);
                g2.dispose();
            }
        }

        private void paintTacticalOcean(Graphics2D g2, int w, int h) {
            g2.setPaint(new GradientPaint(0, 0, OCEAN_HI, 0, h, OCEAN_DEEP));
            g2.fillRect(0, 0, w, h);

            long t = System.currentTimeMillis() - animStart;
            double phase = (t / 1500.0);
            for (int i = 0; i < 6; i++) {
                double y = h * (i / 6.0) + 20 * Math.sin(phase + i);
                int alpha = 22 + (int)(10 * Math.sin(phase + i * 0.7));
                g2.setColor(new Color(0x5F, 0xD4, 0xE0, Math.max(0, Math.min(40, alpha))));
                g2.fillRect(0, (int) y, w, 2);
            }

            g2.setColor(GRID_LINE);
            for (int i = 0; i <= 10; i++) {
                int x = (int)(w * i / 10.0);
                int y = (int)(h * i / 10.0);
                g2.drawLine(x, 0, x, h);
                g2.drawLine(0, y, w, y);
            }
        }
    }

    // ===================================================================
    // FX OVERLAY
    // ===================================================================
    private class FxOverlay extends JComponent {
        private BoardFrame hoverFrame = null;
        private int hoverRow = -1, hoverCol = -1;
        private Point hoverCellOrigin = null;
        private int hoverCellW = 0, hoverCellH = 0;

        private final List<CellFlash> flashes = new ArrayList<>();
        private final List<PermanentMarker> markers = new ArrayList<>();

        private String bannerMsg = "";
        private Color bannerColor = TEXT_LIGHT;
        private long bannerShown = 0;
        private static final long BANNER_DURATION = 1800;
        private BoardFrame bannerOverFrame = null;

        private final Timer animTimer;

        FxOverlay() {
            setOpaque(false);
            animTimer = new Timer(30, e -> {
                pruneFlashes();
                if (System.currentTimeMillis() - bannerShown > BANNER_DURATION) bannerMsg = "";
                repaint();
            });
            animTimer.start();
        }

        @Override public boolean contains(int x, int y) { return false; }

        public void updateHover(BoardFrame frame, int row, int col, Point cellOrigin, int cw, int ch) {
            this.hoverFrame = frame;
            this.hoverRow = row;
            this.hoverCol = col;
            this.hoverCellOrigin = cellOrigin;
            this.hoverCellW = cw;
            this.hoverCellH = ch;
        }

        public void clearHover() {
            hoverFrame = null;
            hoverCellOrigin = null;
        }

        public void addCellFlash(BoardFrame frame, int row, int col, ShotResult result) {
            BoardPanel bp = frame.getBoardPanel();
            if (bp == null) return;
            int cw = bp.getCellWidth();
            int ch = bp.getCellHeight();
            Point origin = SwingUtilities.convertPoint(bp, new Point(col * cw, row * ch), this);
            flashes.add(new CellFlash(frame, row, col, result, origin, cw, ch, System.currentTimeMillis()));
            markers.add(new PermanentMarker(frame, row, col, result));
        }

        public void clearMarkersFor(BoardFrame frame) {
            markers.removeIf(m -> m.frame == frame);
        }

        public void showBanner(String msg, Color color, BoardFrame overFrame) {
            this.bannerMsg = msg;
            this.bannerColor = color;
            this.bannerShown = System.currentTimeMillis();
            this.bannerOverFrame = overFrame;
        }

        private void pruneFlashes() {
            long now = System.currentTimeMillis();
            Iterator<CellFlash> it = flashes.iterator();
            while (it.hasNext()) {
                if (now - it.next().startTime > 900) it.remove();
            }
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // permanent markers (under flashes)
            for (PermanentMarker m : markers) {
                BoardPanel bp = m.frame.getBoardPanel();
                if (bp == null) continue;
                int cw = bp.getCellWidth(), ch = bp.getCellHeight();
                Point origin = SwingUtilities.convertPoint(bp, new Point(m.col * cw, m.row * ch), this);
                Point shake = m.frame.getShakeOffset();
                drawPermanentMarker(g2, m.result,
                                    origin.x + shake.x, origin.y + shake.y, cw, ch);
            }

            // hover highlight
            if (hoverFrame != null && hoverFrame.isActive() && hoverCellOrigin != null) {
                Point shake = hoverFrame.getShakeOffset();
                drawHoverCell(g2, hoverFrame.isOwn(),
                              hoverCellOrigin.x + shake.x, hoverCellOrigin.y + shake.y,
                              hoverCellW, hoverCellH);
            }

            // cell flashes
            long now = System.currentTimeMillis();
            for (CellFlash f : flashes) {
                long elapsed = now - f.startTime;
                Point shake = f.frame.getShakeOffset();
                drawCellFlash(g2, f, elapsed,
                              f.origin.x + shake.x, f.origin.y + shake.y);
            }

            // status banner
            if (!bannerMsg.isEmpty() && bannerOverFrame != null) {
                drawStatusBanner(g2);
            }

            g2.dispose();
        }

        private void drawHoverCell(Graphics2D g2, boolean own, int x, int y, int cw, int ch) {
            Color c = own ? CYAN_ACCENT : TARGET_RED;
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));
            g2.fillRect(x, y, cw, ch);
            g2.setColor(c);
            g2.setStroke(new BasicStroke(2.4f));
            int b = Math.max(8, cw / 4);
            g2.drawLine(x, y, x + b, y);
            g2.drawLine(x, y, x, y + b);
            g2.drawLine(x + cw, y, x + cw - b, y);
            g2.drawLine(x + cw, y, x + cw, y + b);
            g2.drawLine(x, y + ch, x + b, y + ch);
            g2.drawLine(x, y + ch, x, y + ch - b);
            g2.drawLine(x + cw, y + ch, x + cw - b, y + ch);
            g2.drawLine(x + cw, y + ch, x + cw, y + ch - b);
            long t = System.currentTimeMillis();
            float pulse = (float)(0.5 + 0.5 * Math.sin(t / 250.0));
            int ringAlpha = (int)(60 + 80 * pulse);
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), ringAlpha));
            g2.drawRect(x + 3, y + 3, cw - 6, ch - 6);
            g2.setStroke(new BasicStroke(1f));
        }

        private void drawCellFlash(Graphics2D g2, CellFlash f, long elapsed, int x, int y) {
            float t = Math.min(1f, elapsed / 800f);
            float invT = 1f - t;

            Color base;
            switch (f.result) {
                case HIT:  base = TARGET_RED;  break;
                case SUNK: base = SUNK_AMBER;  break;
                case MISS: base = MISS_WHITE;  break;
                default:   base = TEXT_LIGHT;  break;
            }

            int alpha = (int)(220 * invT);
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha));
            g2.fillRect(x, y, f.cellW, f.cellH);

            float scale = 0.4f + t * 1.8f;
            int ringW = (int)(f.cellW * scale);
            int ringH = (int)(f.cellH * scale);
            int ringX = x + (f.cellW - ringW) / 2;
            int ringY = y + (f.cellH - ringH) / 2;
            int ringAlpha = (int)(180 * invT);
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), ringAlpha));
            g2.setStroke(new BasicStroke(3f * invT + 1f));
            g2.drawOval(ringX, ringY, ringW, ringH);
            g2.setStroke(new BasicStroke(1f));

            if (elapsed < 250) {
                float bt = elapsed / 250f;
                int snap = (int)(20 * (1f - bt));
                g2.setColor(base);
                g2.setStroke(new BasicStroke(3f));
                int b = Math.max(8, f.cellW / 3);
                int ox = x - snap, oy = y - snap;
                int ww = f.cellW + snap * 2, hh = f.cellH + snap * 2;
                g2.drawLine(ox, oy, ox + b, oy);
                g2.drawLine(ox, oy, ox, oy + b);
                g2.drawLine(ox + ww, oy, ox + ww - b, oy);
                g2.drawLine(ox + ww, oy, ox + ww, oy + b);
                g2.drawLine(ox, oy + hh, ox + b, oy + hh);
                g2.drawLine(ox, oy + hh, ox, oy + hh - b);
                g2.drawLine(ox + ww, oy + hh, ox + ww - b, oy + hh);
                g2.drawLine(ox + ww, oy + hh, ox + ww, oy + hh - b);
                g2.setStroke(new BasicStroke(1f));
            }

            String label = f.result == ShotResult.HIT ? "HIT!"
                         : f.result == ShotResult.SUNK ? "SUNK!"
                         : "MISS";
            g2.setFont(FONT_LABEL);
            int rise = (int)(t * 30);
            int textAlpha = (int)(255 * invT);
            FontMetrics fm = g2.getFontMetrics();
            int lw = fm.stringWidth(label);
            int lx = x + (f.cellW - lw) / 2;
            int ly = y - 6 - rise;
            g2.setColor(new Color(0, 0, 0, textAlpha));
            g2.drawString(label, lx + 1, ly + 1);
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), textAlpha));
            g2.drawString(label, lx, ly);
        }

        private void drawPermanentMarker(Graphics2D g2, ShotResult r, int x, int y, int cw, int ch) {
            int cx = x + cw / 2, cy = y + ch / 2;
            int s = (int)(Math.min(cw, ch) * 0.55);
            switch (r) {
                case MISS:
                    g2.setColor(new Color(0, 0, 0, 110));
                    g2.fillOval(cx - s/2, cy - s/2, s, s);
                    g2.setColor(MISS_WHITE);
                    g2.setStroke(new BasicStroke(2.4f));
                    g2.drawOval(cx - s/2, cy - s/2, s, s);
                    g2.fillOval(cx - 3, cy - 3, 6, 6);
                    break;
                case HIT:
                    g2.setColor(new Color(TARGET_RED.getRed(), TARGET_RED.getGreen(), TARGET_RED.getBlue(), 100));
                    g2.fillRect(x + 2, y + 2, cw - 4, ch - 4);
                    g2.setColor(TARGET_RED);
                    g2.setStroke(new BasicStroke(3.2f));
                    g2.drawLine(cx - s/2, cy - s/2, cx + s/2, cy + s/2);
                    g2.drawLine(cx + s/2, cy - s/2, cx - s/2, cy + s/2);
                    break;
                case SUNK:
                    g2.setColor(new Color(SUNK_AMBER.getRed(), SUNK_AMBER.getGreen(), SUNK_AMBER.getBlue(), 130));
                    g2.fillRect(x + 2, y + 2, cw - 4, ch - 4);
                    g2.setColor(SUNK_AMBER);
                    g2.setStroke(new BasicStroke(2.4f));
                    g2.drawOval(cx - s/2, cy - s/2, s, s);
                    g2.fillOval(cx - s/4 - 2, cy - 2, 4, 4);
                    g2.fillOval(cx + s/4 - 2, cy - 2, 4, 4);
                    g2.drawLine(cx - s/3, cy + s/4, cx + s/3, cy + s/4);
                    break;
                default: break;
            }
            g2.setStroke(new BasicStroke(1f));
        }

        private void drawStatusBanner(Graphics2D g2) {
            long elapsed = System.currentTimeMillis() - bannerShown;
            float alpha;
            if (elapsed < 200) alpha = elapsed / 200f;
            else if (elapsed > BANNER_DURATION - 400) alpha = Math.max(0, (BANNER_DURATION - elapsed) / 400f);
            else alpha = 1f;
            alpha = Math.max(0, Math.min(1f, alpha));

            BoardPanel bp = bannerOverFrame.getBoardPanel();
            if (bp == null) return;
            Point bpPos = SwingUtilities.convertPoint(bp, 0, 0, this);
            int boardW = bp.getWidth();
            int boardH = bp.getHeight();

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setFont(FONT_BIG);
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(bannerMsg);
            int boxW = tw + 60;
            int boxH = fm.getHeight() + 20;
            int bx = bpPos.x + (boardW - boxW) / 2;
            int by = bpPos.y + (boardH - boxH) / 2;

            g2.setColor(FRAME_SHADOW);
            g2.fillRoundRect(bx + 2, by + 3, boxW, boxH, 10, 10);
            g2.setColor(new Color(0x12, 0x2A, 0x2E, 230));
            g2.fillRoundRect(bx, by, boxW, boxH, 10, 10);

            g2.setStroke(new BasicStroke(2.4f));
            g2.setColor(bannerColor);
            g2.drawRoundRect(bx, by, boxW, boxH, 10, 10);
            g2.setStroke(new BasicStroke(1f));

            int tx = bx + (boxW - tw) / 2;
            int ty = by + (boxH + fm.getAscent()) / 2 - 4;
            g2.setColor(FRAME_SHADOW);
            g2.drawString(bannerMsg, tx + 1, ty + 1);
            g2.setColor(bannerColor);
            g2.drawString(bannerMsg, tx, ty);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    private static class CellFlash {
        BoardFrame frame; int row, col; ShotResult result;
        Point origin; int cellW, cellH; long startTime;
        CellFlash(BoardFrame f, int r, int c, ShotResult res, Point o, int cw, int ch, long t) {
            frame=f; row=r; col=c; result=res; origin=o; cellW=cw; cellH=ch; startTime=t;
        }
    }

    private static class PermanentMarker {
        BoardFrame frame; int row, col; ShotResult result;
        PermanentMarker(BoardFrame f, int r, int c, ShotResult res) {
            frame=f; row=r; col=c; result=res;
        }
    }


    // ===================================================================
    // SKILL STRIP (bottom): hosts SkillPanel inside naval frame chrome.
    // SkillPanel itself paints mana/passive/skill buttons; we just
    // provide the surrounding frame and a header.
    // ===================================================================
    private class SkillStrip extends JPanel {
        private final JPanel skillHost;

        SkillStrip() {
            setOpaque(false);
            setLayout(new BorderLayout());
            // Reserve enough vertical space for SkillPanel's full content.
            setPreferredSize(new Dimension(0, 220));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

            skillHost = new JPanel();
            skillHost.setOpaque(false);
            skillHost.setLayout(new BorderLayout());
            skillHost.setBorder(BorderFactory.createEmptyBorder(36, 18, 14, 18));
            add(skillHost, BorderLayout.CENTER);
        }

        public void setSkillPanel(SkillPanel sp) {
            skillHost.removeAll();
            if (sp != null) {
                skillHost.add(sp, BorderLayout.CENTER);
            }
            skillHost.revalidate();
            skillHost.repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            paintRivetedFrame(g2, 0, 0, w, h);

            // header
            g2.setFont(FONT_LABEL);
            g2.setColor(CYAN_ACCENT);
            String title = "COMMAND CONSOLE  \u2014  ACTIVE SKILLS";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, (w - fm.stringWidth(title)) / 2, 28);
            g2.setColor(FRAME_BORDER);
            g2.drawLine(20, 36, w - 20, 36);

            g2.dispose();
        }
    }


    // ===================================================================
    // CUSTOM BUTTON
    // ===================================================================
    private class NavalButton extends JButton {
        private final Color accent;
        private boolean hover = false;
        private boolean pressed = false;

        NavalButton(String text, Color accent) {
            super(text);
            this.accent = accent;
            setFont(FONT_BUTTON);
            setForeground(TEXT_LIGHT);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; pressed = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int offset = pressed ? 2 : 0;

            g2.setColor(FRAME_SHADOW);
            g2.fillRoundRect(2, 3, w - 4, h - 4, 8, 8);

            Color top = isEnabled() ? FRAME_FILL_HI : FRAME_FILL.darker();
            Color bot = isEnabled() ? FRAME_FILL : FRAME_FILL.darker().darker();
            g2.setPaint(new GradientPaint(0, 0, top, 0, h, bot));
            g2.fillRoundRect(0, offset, w - 4, h - 4 - offset, 8, 8);

            g2.setColor(isEnabled() ? accent : TEXT_DIM);
            g2.fillRect(8, 4 + offset, w - 20, 2);

            g2.setStroke(new BasicStroke(1.6f));
            g2.setColor(hover && isEnabled() ? accent : FRAME_BORDER);
            g2.drawRoundRect(0, offset, w - 5, h - 5 - offset, 8, 8);

            g2.setColor(RIVET);
            g2.fillOval(5, 5 + offset, 4, 4);
            g2.fillOval(w - 13, 5 + offset, 4, 4);
            g2.fillOval(5, h - 13 - offset, 4, 4);
            g2.fillOval(w - 13, h - 13 - offset, 4, 4);

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(getText())) / 2;
            int ty = (h + fm.getAscent()) / 2 - 4 + offset;
            g2.setColor(FRAME_SHADOW);
            g2.drawString(getText(), tx + 1, ty + 1);
            g2.setColor(isEnabled() ? TEXT_LIGHT : TEXT_DIM);
            g2.drawString(getText(), tx, ty);

            g2.dispose();
        }
    }

    // ===================================================================
    // FRAME PAINT HELPERS
    // ===================================================================
    private void paintRivetedFrame(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(FRAME_SHADOW);
        g2.fillRoundRect(x, y, w, h, 12, 12);
        g2.setPaint(new GradientPaint(0, y, FRAME_FILL_HI, 0, y + h, FRAME_FILL));
        g2.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 12, 12);
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(FRAME_BORDER);
        g2.drawRoundRect(x + 2, y + 2, w - 5, h - 5, 12, 12);
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillRoundRect(x + 8, y + 8, w - 16, h - 16, 8, 8);
        g2.setColor(COPPER);
        int[] rx = {x + 12, x + w - 18, x + 12, x + w - 18};
        int[] ry = {y + 12, y + 12, y + h - 18, y + h - 18};
        for (int i = 0; i < 4; i++) {
            g2.fillOval(rx[i], ry[i], 6, 6);
            g2.setColor(RIVET);
            g2.drawOval(rx[i], ry[i], 6, 6);
            g2.setColor(COPPER);
        }
    }

    private void paintMetalBar(Graphics2D g2, int x, int y, int w, int h) {
        g2.setPaint(new GradientPaint(0, y, FRAME_FILL_HI, 0, y + h, FRAME_FILL));
        g2.fillRect(x, y, w, h);
        g2.setColor(FRAME_BORDER);
        g2.fillRect(x, y + h - 3, w, 1);
        g2.setColor(FRAME_SHADOW);
        g2.fillRect(x, y + h - 2, w, 2);
    }

    private void paintEngravedPlate(Graphics2D g2, int x, int y, int w, int h, Color accent) {
        g2.setColor(FRAME_SHADOW);
        g2.fillRoundRect(x + 2, y + 2, w, h, 8, 8);
        g2.setPaint(new GradientPaint(0, y, new Color(0x12, 0x2A, 0x2E), 0, y + h, new Color(0x08, 0x18, 0x1A)));
        g2.fillRoundRect(x, y, w, h, 8, 8);
        g2.setStroke(new BasicStroke(1.6f));
        g2.setColor(accent);
        g2.drawRoundRect(x, y, w, h, 8, 8);
        g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40));
        g2.drawRoundRect(x + 4, y + 4, w - 8, h - 8, 6, 6);
    }

    private void paintStatusLight(Graphics2D g2, int cx, int cy, Color c) {
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 70));
        g2.fillOval(cx - 14, cy - 14, 28, 28);
        g2.setColor(c);
        g2.fillOval(cx - 6, cy - 6, 12, 12);
        g2.setColor(Color.WHITE);
        g2.fillOval(cx - 2, cy - 2, 4, 4);
    }

    // ===================================================================
    // GAME LOGIC (preserved from original)
    // ===================================================================
    private void updateBoardViews() {
        if (fxOverlay != null) fxOverlay.clearHover();

        if (game.isPlayer1Turn()) {
            currentPlayer = 1;
            BoardPanel ownView   = new BoardPanel(true,  game.getPlayer1Board(), true);
            BoardPanel enemyView = new BoardPanel(false, game.getPlayer2Board(), false);
            player1BoardPanel = ownView;
            player2BoardPanel = enemyView;

            leftBoardFrame.setTitle("YOUR FLEET");
            rightBoardFrame.setTitle("ENEMY WATERS");
            leftBoardFrame.setBoardPanel(ownView, true);
            rightBoardFrame.setBoardPanel(enemyView, false);

            ownView.setPlayerClickHandler((row, col) -> handleOwnBoardClick(1, row, col));
            enemyView.setEnemyClickHandler((row, col) -> handleEnemyBoardClick(1, row, col));

            showPlayerSkills(1);
        } else {
            currentPlayer = 2;
            BoardPanel ownView   = new BoardPanel(true,  game.getPlayer2Board(), true);
            BoardPanel enemyView = new BoardPanel(false, game.getPlayer1Board(), false);
            player2BoardPanel = ownView;
            player1BoardPanel = enemyView;

            leftBoardFrame.setTitle("YOUR FLEET");
            rightBoardFrame.setTitle("ENEMY WATERS");
            leftBoardFrame.setBoardPanel(ownView, true);
            rightBoardFrame.setBoardPanel(enemyView, false);

            ownView.setPlayerClickHandler((row, col) -> handleOwnBoardClick(2, row, col));
            enemyView.setEnemyClickHandler((row, col) -> handleEnemyBoardClick(2, row, col));

            showPlayerSkills(2);
        }

        updateActiveBoardForSkillState();

        timerGauge.setOnExpire(this::endTurnTimer);
        timerGauge.resetTimer();
        timerGauge.startTimer();

        turnBanner.triggerFlash();
        turnBanner.repaint();
        revalidate();
        repaint();
    }

    private void updateActiveBoardForSkillState() {
        boolean ownActive = (waitingForSkillTarget && skillTargetsOwnBoard) || waitingForShadowStepSource;
        leftBoardFrame.setActive(ownActive);
        rightBoardFrame.setActive(!ownActive);
    }

    private BoardFrame getActiveTargetFrame() {
        boolean ownActive = (waitingForSkillTarget && skillTargetsOwnBoard) || waitingForShadowStepSource;
        return ownActive ? leftBoardFrame : rightBoardFrame;
    }

    private void showPlayerSkills(int playerNumber) {
        GameCharacter character = (playerNumber == 1) ? game.getPlayer1Character() : game.getPlayer2Character();
        if (character != null) {
            currentSkillPanel = new SkillPanel(character);
            currentSkillPanel.setBoards(
                (playerNumber == 1) ? player1BoardPanel : player2BoardPanel,
                (playerNumber == 1) ? player2BoardPanel : player1BoardPanel
            );
            currentSkillPanel.setSkillListener(new SkillPanel.SkillButtonListener() {
                @Override
                public void onSkillUsed(int skillNumber, String skillName, boolean requiresTarget,
                                        boolean requiresDirection, boolean targetsOwnBoard) {
                    useSkill(playerNumber, skillNumber, skillName, requiresTarget, requiresDirection, targetsOwnBoard);
                }
            });
            skillStrip.setSkillPanel(currentSkillPanel);
        } else {
            skillStrip.setSkillPanel(null);
        }
    }

    private void useSkill(int playerNumber, int skillNumber, String skillName,
                          boolean requiresTarget, boolean requiresDirection, boolean targetsOwnBoard) {
        if ((playerNumber == 1 && !game.isPlayer1Turn()) || (playerNumber == 2 && game.isPlayer1Turn())) {
            updateStatusMessage("Not your turn!", TARGET_RED);
            return;
        }

        if (skillName.equals("Shadow Step") && requiresTarget && targetsOwnBoard) {
            waitingForShadowStepSource = true;
            currentSkillPlayer = playerNumber;
            currentSkillNumber = skillNumber;
            currentSkillName = skillName;
            skillTargetsOwnBoard = true;
            shadowStepSourceX = -1;
            shadowStepSourceY = -1;
            updateStatusMessage("Pick a ship to teleport!", HIT_GOLD);
            updateActiveBoardForSkillState();
            return;
        }

        if (requiresTarget) {
            waitingForSkillTarget = true;
            currentSkillPlayer = playerNumber;
            currentSkillNumber = skillNumber;
            currentSkillName = skillName;
            skillTargetsOwnBoard = targetsOwnBoard;

            if (requiresDirection) {
                waitingForDirection = true;
                String[] options = {"Horizontal (\u2192)", "Vertical (\u2193)"};
                int choice = JOptionPane.showOptionDialog(this,
                    skillName + "\n\nChoose direction:",
                    "Skill Direction",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

                if (choice >= 0) {
                    skillDirectionHorizontal = (choice == 0);
                    waitingForDirection = false;
                    String t = targetsOwnBoard ? "YOUR board" : "ENEMY board";
                    updateStatusMessage("Click " + t + "!", HIT_GOLD);
                } else {
                    waitingForSkillTarget = false;
                }
            } else {
                String t = targetsOwnBoard ? "YOUR board" : "ENEMY board";
                updateStatusMessage("Click " + t + "!", HIT_GOLD);
            }
            updateActiveBoardForSkillState();
        } else {
            boolean wasPlayer1Turn = game.isPlayer1Turn();
            boolean success = game.useCharacterSkill(playerNumber, skillNumber, 0, 0, false);
            if (success) {
                updateStatusMessage(skillName + " used!", new Color(0x6B, 0xD0, 0x8C));
                postSkillActionUpdate(wasPlayer1Turn, playerNumber);
            } else {
                updateStatusMessage("Failed! Mana/cooldown.", TARGET_RED);
            }
        }
    }

    private void handleEnemyBoardClick(int playerNumber, int row, int col) {
        if (waitingForSkillTarget && currentSkillPlayer == playerNumber && !skillTargetsOwnBoard) {
            handleSkillTarget(row, col);
            return;
        }
        if (waitingForSkillTarget && currentSkillPlayer == playerNumber && skillTargetsOwnBoard) {
            updateStatusMessage("Targets YOUR board!", TARGET_RED);
            return;
        }
        handleShot(playerNumber, row, col);
    }

    private void handleOwnBoardClick(int playerNumber, int row, int col) {
        if (waitingForShadowStepSource && currentSkillPlayer == playerNumber && shadowStepSourceX == -1) {
            shadowStepSourceX = row;
            shadowStepSourceY = col;
            waitingForShadowStepSource = false;
            updateStatusMessage("Pick destination!", HIT_GOLD);
            return;
        }
        if (waitingForShadowStepSource && currentSkillPlayer == playerNumber && shadowStepSourceX != -1) {
            boolean wasPlayer1Turn = game.isPlayer1Turn();
            boolean success = game.useShadowStep(playerNumber, shadowStepSourceX, shadowStepSourceY, row, col);
            if (success) {
                updateStatusMessage("Shadow Step!", new Color(0x6B, 0xD0, 0x8C));
                postSkillActionUpdate(wasPlayer1Turn, playerNumber);
            } else {
                updateStatusMessage("Failed Shadow Step!", TARGET_RED);
            }
            waitingForShadowStepSource = false;
            shadowStepSourceX = -1;
            shadowStepSourceY = -1;
            waitingForSkillTarget = false;
            updateActiveBoardForSkillState();
            return;
        }
        if (waitingForSkillTarget && currentSkillPlayer == playerNumber && skillTargetsOwnBoard) {
            handleSkillTarget(row, col);
            return;
        }
        if (waitingForSkillTarget && currentSkillPlayer == playerNumber && !skillTargetsOwnBoard) {
            updateStatusMessage("Targets ENEMY board!", TARGET_RED);
            return;
        }
        updateStatusMessage("Cannot fire on your own ships!", HIT_GOLD);
    }

    private void handleSkillTarget(int row, int col) {
        if (!waitingForSkillTarget) return;
        boolean wasPlayer1Turn = game.isPlayer1Turn();
        boolean success = game.useCharacterSkill(currentSkillPlayer, currentSkillNumber, row, col, skillDirectionHorizontal);
        if (success) {
            updateStatusMessage(currentSkillName + " used!", new Color(0x6B, 0xD0, 0x8C));
            postSkillActionUpdate(wasPlayer1Turn, currentSkillPlayer);
        } else {
            updateStatusMessage("Failed " + currentSkillName + "!", TARGET_RED);
        }
        waitingForSkillTarget = false;
        currentSkillPlayer = 0;
        currentSkillNumber = 0;
        currentSkillName = "";
        skillTargetsOwnBoard = false;
        updateActiveBoardForSkillState();
    }

    private void postSkillActionUpdate(boolean wasPlayer1Turn, int currentPlayerNumber) {
        refreshBoards();
        timerGauge.resetTimer();
        timerGauge.startTimer();

        if (wasPlayer1Turn != game.isPlayer1Turn()) {
            fxOverlay.clearMarkersFor(leftBoardFrame);
            fxOverlay.clearMarkersFor(rightBoardFrame);
            updateBoardViews();
        } else {
            turnBanner.repaint();
            showPlayerSkills(currentPlayerNumber);
            if (currentSkillPanel != null) currentSkillPanel.updateUI();
        }

        if (game.isGameOver()) {
            String winner = game.getWinner();
            updateStatusMessage("GAME OVER! " + winner + " WINS!", HIT_GOLD);
            disableInteraction();
        }
    }

    private void handleShot(int playerNumber, int row, int col) {
        boolean wasPlayer1Turn = game.isPlayer1Turn();
        ShotResult result = game.fire(playerNumber, row, col);

        BoardFrame targetFrame = rightBoardFrame; // enemy board is always on the right per layout
        fxOverlay.addCellFlash(targetFrame, row, col, result);

        switch (result) {
            case HIT:
                updateStatusMessage("HIT!", TARGET_RED);
                targetFrame.triggerShake();
                break;
            case SUNK:
                updateStatusMessage("SHIP SUNK!", SUNK_AMBER);
                targetFrame.triggerShake();
                break;
            case MISS:
                updateStatusMessage("Miss...", MISS_WHITE);
                break;
            default: break;
        }

        refreshBoards();
        timerGauge.resetTimer();
        timerGauge.startTimer();

        if (wasPlayer1Turn != game.isPlayer1Turn()) {
            fxOverlay.clearMarkersFor(leftBoardFrame);
            fxOverlay.clearMarkersFor(rightBoardFrame);
            updateBoardViews();
        } else {
            turnBanner.repaint();
            if (currentSkillPanel != null) currentSkillPanel.updateUI();
        }

        if (game.isGameOver()) {
            String winner = game.getWinner();
            updateStatusMessage("GAME OVER! " + winner + " WINS!", HIT_GOLD);
            disableInteraction();
        }
    }

    private void disableInteraction() {
        if (player1BoardPanel != null) {
            player1BoardPanel.setEnemyClickHandler(null);
            player1BoardPanel.setPlayerClickHandler(null);
        }
        if (player2BoardPanel != null) {
            player2BoardPanel.setEnemyClickHandler(null);
            player2BoardPanel.setPlayerClickHandler(null);
        }
    }

    public void refreshBoards() {
        if (player1BoardPanel != null) player1BoardPanel.refreshColors();
        if (player2BoardPanel != null) player2BoardPanel.refreshColors();
    }

    /** Kept for compatibility with Main.java - repaints the turn banner. */
    public void updateTurnDisplay() {
        if (turnBanner != null) turnBanner.repaint();
    }

    private void endTurnTimer() {
        timerGauge.stopTimer();
        if (waitingForSkillTarget) {
            waitingForSkillTarget = false;
            currentSkillNumber = 0;
            currentSkillName = "";
            updateBoardViews();
            return;
        }
        boolean isPlayer1Turn = game.isPlayer1Turn();
        game.setPlayer1Turn(!isPlayer1Turn);
        fxOverlay.clearMarkersFor(leftBoardFrame);
        fxOverlay.clearMarkersFor(rightBoardFrame);
        updateBoardViews();
    }

    public void updateStatusMessage(String message, Color color) {
        if (fxOverlay != null) fxOverlay.showBanner(message, color, getActiveTargetFrame());
    }
}