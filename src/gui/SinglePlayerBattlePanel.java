package gui;

import ai.AIPlayer;
import characters.GameCharacter;
import game.ShotResult;
import models.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

/**
 * SinglePlayerBattlePanel - TideBound PvE battle screen.
 * 
 * Identical visual design to MultiplayerBattlePanel but adapted for Player vs AI:
 * - Turn banner: "YOUR TURN — STRIKE ENEMY WATERS" / "AI THINKING..."
 * - Left board: your fleet (dimmed when AI turn)
 * - Right board: enemy waters (targeting when your turn)
 * - Skill panel: horizontal strip at bottom (player character only)
 * - FX overlay: hit/miss/sunk animations, hover brackets, shockwave
 * - Win dialogs: "VICTORY!" / "DEFEAT!"
 * 
 * Turn flow:
 *   Player clicks enemy cell → fire → show FX → check win
 *   → if no win: delay 1s → AI picks cell → fire → show FX → check win → back to player
 */
public class SinglePlayerBattlePanel extends JPanel {

    // ===================================================================
    // GAME STATE
    // ===================================================================
    private final GameCharacter playerChar;
    private final Board playerBoard;
    private final Board aiBoard;
    private final AIPlayer ai;
    private final JFrame parentFrame;
    
    private boolean playerTurn = true;
    private int timerSeconds = 90;
    
    // ===================================================================
    // UI COMPONENTS
    // ===================================================================
    private BoardFrame playerBoardFrame;
    private BoardFrame enemyBoardFrame;
    private SkillPanel skillPanel;
    private FXOverlay fxOverlay;
    private TurnBanner turnBanner;
    private JPanel skillStrip;
    
    private Timer turnTimer;
    private Timer aiDelayTimer;
    
    // ===================================================================
    // PALETTE (matches MultiplayerBattlePanel exactly)
    // ===================================================================
    private static final Color BG_DEEP        = new Color(0x0F, 0x23, 0x26);
    private static final Color FRAME_FILL     = new Color(0x1E, 0x45, 0x48);
    private static final Color FRAME_FILL_HI  = new Color(0x2A, 0x5A, 0x5E);
    private static final Color FRAME_BORDER   = new Color(0x3A, 0x7A, 0x7E);
    private static final Color FRAME_SHADOW   = new Color(0x08, 0x18, 0x1A);
    private static final Color CYAN_ACCENT    = new Color(0x5F, 0xD4, 0xE0);
    private static final Color COPPER         = new Color(0xC9, 0x7A, 0x3D);
    private static final Color TARGET_RED     = new Color(0xE0, 0x5F, 0x5F);
    private static final Color HIT_GOLD       = new Color(0xE0, 0xC0, 0x5F);
    private static final Color MISS_WHITE     = new Color(0xE0, 0xF0, 0xF6);
    private static final Color SUNK_AMBER     = new Color(0xE0, 0x80, 0x3F);
    private static final Color TEXT_LIGHT     = new Color(0xE8, 0xF4, 0xF6);
    private static final Color TEXT_DIM       = new Color(0x8A, 0xA8, 0xAC);
    
    private static final Font FONT_HUGE   = pickFont(24f, Font.BOLD);
    private static final Font FONT_BIG    = pickFont(18f, Font.BOLD);
    private static final Font FONT_LABEL  = pickFont(14f, Font.BOLD);
    private static final Font FONT_SMALL  = pickFont(11f, Font.PLAIN);
    
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
    public SinglePlayerBattlePanel(GameCharacter playerChar, Board playerBoard, AIPlayer ai, JFrame parentFrame) {
        this.playerChar = playerChar;
        this.playerBoard = playerBoard;
        this.aiBoard = ai.getBoard();
        this.ai = ai;
        this.parentFrame = parentFrame;
        
        setLayout(null); // absolute positioning for overlays
        setBackground(BG_DEEP);
        
        buildUI();
        startTurnTimer();
    }
    
    private void buildUI() {
        // Turn banner at top
        turnBanner = new TurnBanner();
        add(turnBanner);
        
        // Board frames
        playerBoardFrame = new BoardFrame(playerBoard, true, "YOUR FLEET");
        enemyBoardFrame = new BoardFrame(aiBoard, false, "ENEMY WATERS");
        
        // Click handlers
        enemyBoardFrame.getBoardPanel().setEnemyClickHandler((row, col) -> {
            if (playerTurn) handlePlayerShot(row, col);
        });
        
        add(playerBoardFrame);
        add(enemyBoardFrame);
        
        // Skill strip at bottom
        skillStrip = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        skillStrip.setOpaque(false);
        skillPanel = new SkillPanel(playerChar);
        skillPanel.setBoards(playerBoardFrame.getBoardPanel(), enemyBoardFrame.getBoardPanel());
        skillPanel.setPreferredSize(new Dimension(900, 120));
        skillStrip.add(skillPanel);
        add(skillStrip);
        
        // FX overlay (must be last so it paints on top)
        fxOverlay = new FXOverlay();
        add(fxOverlay);
        
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { layoutComponents(); }
        });
    }
    
    private void layoutComponents() {
        int w = getWidth(), h = getHeight();
        
        // Turn banner: full width, 100px tall, at top
        turnBanner.setBounds(0, 0, w, 100);
        
        // Skill strip: full width, 130px tall, at bottom
        int stripH = 130;
        skillStrip.setBounds(0, h - stripH, w, stripH);
        
        // Boards: side-by-side in remaining space
        int boardsY = 100;
        int boardsH = h - 100 - stripH;
        int boardW = w / 2;
        
        playerBoardFrame.setBounds(0, boardsY, boardW, boardsH);
        enemyBoardFrame.setBounds(boardW, boardsY, boardW, boardsH);
        
        // FX overlay covers the boards area
        fxOverlay.setBounds(0, boardsY, w, boardsH);
        
        revalidate();
        repaint();
    }
    
    // ===================================================================
    // TURN MANAGEMENT
    // ===================================================================
    private void startTurnTimer() {
        timerSeconds = 90;
        if (turnTimer != null) turnTimer.stop();
        turnTimer = new Timer(1000, e -> {
            timerSeconds--;
            turnBanner.repaint();
            if (timerSeconds <= 0) {
                // Time's up - skip turn
                if (playerTurn) {
                    showBanner("TIME'S UP!", TARGET_RED);
                    switchToAITurn();
                }
            }
        });
        turnTimer.start();
    }
    
    private void handlePlayerShot(int row, int col) {
        if (!playerTurn) return;
        
        ShotResult result = aiBoard.fire(row, col);
        enemyBoardFrame.getBoardPanel().updateCell(row, col, result);
        
        // Show FX
        fxOverlay.addCellFlash(enemyBoardFrame, row, col, result);
        
        // Update skill panel mana
        if (skillPanel != null) skillPanel.updateUI();
        
        // Show status banner
        String msg = switch (result) {
            case HIT -> "DIRECT HIT!";
            case MISS -> "MISS!";
            case SUNK -> "VESSEL DESTROYED!";
            default -> "UNKNOWN";
        };
        Color color = switch (result) {
            case HIT -> HIT_GOLD;
            case MISS -> MISS_WHITE;
            case SUNK -> SUNK_AMBER;
            default -> CYAN_ACCENT;
        };
        showBanner(msg, color);
        
        // Check win
        if (aiBoard.allShipsSunk()) {
            playerWins();
            return;
        }
        
        // Switch to AI turn after delay
        switchToAITurn();
    }
    
    private void switchToAITurn() {
        playerTurn = false;
        turnBanner.repaint();
        updateBoardStates();
        
        if (aiDelayTimer != null) aiDelayTimer.stop();
        aiDelayTimer = new Timer(1500, e -> aiTurn());
        aiDelayTimer.setRepeats(false);
        aiDelayTimer.start();
    }
    
    private void aiTurn() {
        int[] move = ai.getNextMove();
        int row = move[0];
        int col = move[1];
        
        ShotResult result = playerBoard.fire(row, col);
        ai.processResult(row, col, result);
        playerBoardFrame.getBoardPanel().updateCell(row, col, result);
        
        // Show FX
        fxOverlay.addCellFlash(playerBoardFrame, row, col, result);
        
        // Show status banner
        String msg = switch (result) {
            case HIT -> "ENEMY HIT US!";
            case MISS -> "ENEMY MISSED!";
            case SUNK -> "WE LOST A SHIP!";
            default -> "UNKNOWN";
        };
        Color color = switch (result) {
            case HIT -> TARGET_RED;
            case MISS -> CYAN_ACCENT;
            case SUNK -> TARGET_RED;
            default -> CYAN_ACCENT;
        };
        showBanner(msg, color);
        
        // Check loss
        if (playerBoard.allShipsSunk()) {
            aiWins();
            return;
        }
        
        // Back to player turn
        Timer backToPlayer = new Timer(1000, e -> {
            playerTurn = true;
            turnBanner.repaint();
            updateBoardStates();
            startTurnTimer();
        });
        backToPlayer.setRepeats(false);
        backToPlayer.start();
    }
    
    private void updateBoardStates() {
        // Dim/highlight boards based on whose turn it is
        playerBoardFrame.setActive(!playerTurn); // defending when AI turn
        enemyBoardFrame.setActive(playerTurn);    // targeting when player turn
        repaint();
    }
    
    private void showBanner(String msg, Color color) {
        fxOverlay.showStatusBanner(msg, color, enemyBoardFrame);
    }
    
    private void playerWins() {
        if (turnTimer != null) turnTimer.stop();
        if (aiDelayTimer != null) aiDelayTimer.stop();
        
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(parentFrame,
                "🎉 VICTORY! The tides are with you! 🎉\n\nPlay again?",
                "TideBound Victor",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                // Trigger return to main menu (Main.java will handle this)
                parentFrame.dispatchEvent(new WindowEvent(parentFrame, WindowEvent.WINDOW_CLOSING));
            } else {
                System.exit(0);
            }
        });
    }
    
    private void aiWins() {
        if (turnTimer != null) turnTimer.stop();
        if (aiDelayTimer != null) aiDelayTimer.stop();
        
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(parentFrame,
                "💀 The tides have turned against you... 💀\n\nPlay again?",
                "TideBound Defeat",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                parentFrame.dispatchEvent(new WindowEvent(parentFrame, WindowEvent.WINDOW_CLOSING));
            } else {
                System.exit(0);
            }
        });
    }
    
    public void cleanup() {
        if (turnTimer != null) turnTimer.stop();
        if (aiDelayTimer != null) aiDelayTimer.stop();
        if (skillPanel != null) skillPanel.stopTimers();
    }
    
    // ===================================================================
    // TURN BANNER (top bar with engraved nameplate + timer)
    // ===================================================================
    private class TurnBanner extends JPanel {
        TurnBanner() {
            setOpaque(false);
        }
        
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth(), h = getHeight();
            
            // Metal bar background
            g2.setPaint(new GradientPaint(0, 0, FRAME_FILL_HI, 0, h, FRAME_FILL));
            g2.fillRect(0, 0, w, h);
            g2.setColor(FRAME_BORDER);
            g2.fillRect(0, h - 3, w, 1);
            g2.setColor(FRAME_SHADOW);
            g2.fillRect(0, h - 2, w, 2);
            
            // BACK button (top-left)
            paintBackButton(g2, 16, 16);
            
            // Central engraved nameplate
            int plateW = Math.min(700, w - 400);
            int plateH = 56;
            int px = (w - plateW) / 2;
            int py = (h - plateH) / 2;
            paintEngravedPlate(g2, px, py, plateW, plateH);
            
            // Turn indicator huge "YOU" / "AI" on left of plate
            String bigTag = playerTurn ? "YOU" : "AI";
            Color tagColor = playerTurn ? CYAN_ACCENT : TARGET_RED;
            g2.setFont(FONT_HUGE);
            FontMetrics fm = g2.getFontMetrics();
            int tagX = px - fm.stringWidth(bigTag) - 24;
            int tagY = py + (plateH + fm.getAscent()) / 2 - 4;
            g2.setColor(FRAME_SHADOW);
            g2.drawString(bigTag, tagX + 1, tagY + 1);
            g2.setColor(tagColor);
            g2.drawString(bigTag, tagX, tagY);
            
            // Turn text inside plate
            String turnText = playerTurn ? "YOUR TURN — STRIKE ENEMY WATERS" : "AI THINKING...";
            g2.setFont(FONT_BIG);
            fm = g2.getFontMetrics();
            int tx = px + (plateW - fm.stringWidth(turnText)) / 2;
            int ty = py + (plateH + fm.getAscent()) / 2 - 4;
            g2.setColor(FRAME_SHADOW);
            g2.drawString(turnText, tx + 1, ty + 1);
            g2.setColor(TEXT_LIGHT);
            g2.drawString(turnText, tx, ty);
            
            // Timer gauge (circular, right side)
            paintTimerGauge(g2, w - 80, h / 2);
            
            g2.dispose();
        }
        
        private void paintBackButton(Graphics2D g2, int x, int y) {
            int bw = 110, bh = 36;
            g2.setColor(FRAME_SHADOW);
            g2.fillRoundRect(x + 2, y + 3, bw, bh, 6, 6);
            g2.setPaint(new GradientPaint(0, y, FRAME_FILL_HI, 0, y + bh, FRAME_FILL));
            g2.fillRoundRect(x, y, bw, bh, 6, 6);
            g2.setStroke(new BasicStroke(1.6f));
            g2.setColor(FRAME_BORDER);
            g2.drawRoundRect(x, y, bw, bh, 6, 6);
            
            g2.setFont(FONT_SMALL);
            g2.setColor(TEXT_DIM);
            FontMetrics fm = g2.getFontMetrics();
            String text = "← BACK";
            g2.drawString(text, x + (bw - fm.stringWidth(text)) / 2, y + (bh + fm.getAscent()) / 2 - 2);
        }
        
        private void paintEngravedPlate(Graphics2D g2, int x, int y, int w, int h) {
            g2.setColor(FRAME_SHADOW);
            g2.fillRoundRect(x + 2, y + 2, w, h, 8, 8);
            g2.setPaint(new GradientPaint(0, y, new Color(0x12, 0x2A, 0x2E), 0, y + h, new Color(0x08, 0x18, 0x1A)));
            g2.fillRoundRect(x, y, w, h, 8, 8);
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(CYAN_ACCENT);
            g2.drawRoundRect(x, y, w, h, 8, 8);
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(new Color(CYAN_ACCENT.getRed(), CYAN_ACCENT.getGreen(), CYAN_ACCENT.getBlue(), 40));
            g2.drawRoundRect(x + 4, y + 4, w - 8, h - 8, 6, 6);
        }
        
        private void paintTimerGauge(Graphics2D g2, int cx, int cy) {
            int r = 28;
            float pct = timerSeconds / 90f;
            Color fill = pct > 0.5f ? CYAN_ACCENT : (pct > 0.25f ? HIT_GOLD : TARGET_RED);
            
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            
            g2.setStroke(new BasicStroke(4f));
            g2.setColor(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), 80));
            Arc2D arc = new Arc2D.Double(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2, 90, -360 * pct, Arc2D.OPEN);
            g2.draw(arc);
            
            g2.setFont(FONT_SMALL);
            g2.setColor(TEXT_LIGHT);
            String time = timerSeconds + "s";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(time, cx - fm.stringWidth(time) / 2, cy + fm.getAscent() / 2 - 2);
        }
    }
    
    // ===================================================================
    // BOARD FRAME (wraps BoardPanel with tactical ocean background + label)
    // ===================================================================
    private class BoardFrame extends JLayeredPane {
        private final Board board;
        private final boolean showShips;
        private final String label;
        private BoardPanel boardPanel;
        private boolean active = false;
        
        BoardFrame(Board board, boolean showShips, String label) {
            this.board = board;
            this.showShips = showShips;
            this.label = label;
            setOpaque(false);
            buildBoard();
        }
        
        private void buildBoard() {
            boardPanel = new BoardPanel(showShips, board, showShips);
            boardPanel.setOpaque(false);
            add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        }
        
        @Override public void doLayout() {
            int w = getWidth(), h = getHeight();
            int margin = 24;
            int boardSize = Math.min(w - margin * 2, h - margin * 2 - 40);
            int bx = (w - boardSize) / 2;
            int by = (h - boardSize) / 2;
            boardPanel.setBounds(bx, by, boardSize, boardSize);
        }
        
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth(), h = getHeight();
            
            // Tactical ocean background (calm dark-teal gradient with wave shimmer)
            g2.setPaint(new GradientPaint(0, 0, new Color(0x0E, 0x28, 0x2E), 0, h, new Color(0x08, 0x18, 0x1C)));
            g2.fillRect(0, 0, w, h);
            
            // Wave shimmer bands (horizontal, slow drift)
            long t = System.currentTimeMillis();
            int offset = (int)((t / 80) % 40);
            g2.setColor(new Color(255, 255, 255, 8));
            for (int i = 0; i < 6; i++) {
                int y = (i * h / 5 + offset) % h;
                g2.fillRect(0, y, w, 2);
            }
            
            // Cyan grid lines over ocean
            g2.setColor(new Color(CYAN_ACCENT.getRed(), CYAN_ACCENT.getGreen(), CYAN_ACCENT.getBlue(), 30));
            int gridStep = 60;
            for (int x = 0; x < w; x += gridStep) g2.drawLine(x, 0, x, h);
            for (int y = 0; y < h; y += gridStep) g2.drawLine(0, y, w, y);
            
            // Dimming overlay when board is not active
            if (!active) {
                g2.setColor(new Color(0, 0, 0, 130));
                g2.fillRect(0, 0, w, h);
                g2.setColor(new Color(CYAN_ACCENT.getRed(), CYAN_ACCENT.getGreen(), CYAN_ACCENT.getBlue(), 100));
                g2.fillRect(0, 0, w, h);
            }
            
            // Board label at bottom
            g2.setFont(FONT_LABEL);
            FontMetrics fm = g2.getFontMetrics();
            int lx = (w - fm.stringWidth(label)) / 2;
            int ly = h - 12;
            
            if (active && !showShips) {
                // Enemy board active = pulsing red "TARGETING" border
                g2.setColor(TEXT_DIM);
                g2.drawString(label, lx, ly);
                
                float pulse = (float)(0.5 + 0.5 * Math.sin(t / 300.0));
                g2.setStroke(new BasicStroke(3f));
                g2.setColor(new Color(TARGET_RED.getRed(), TARGET_RED.getGreen(), TARGET_RED.getBlue(), (int)(180 * pulse)));
                g2.drawRect(2, 2, w - 5, h - 5);
                
                g2.setFont(FONT_SMALL);
                fm = g2.getFontMetrics();
                String tag = "⚔ TARGETING";
                int tx = w - fm.stringWidth(tag) - 12;
                g2.setColor(TARGET_RED);
                g2.drawString(tag, tx, 24);
            } else if (!active && showShips) {
                // Player board inactive = grey "DEFENDING" label
                g2.setColor(TEXT_DIM);
                g2.drawString(label, lx, ly);
                g2.setFont(FONT_SMALL);
                fm = g2.getFontMetrics();
                g2.drawString("DEFENDING", 12, 24);
            } else {
                // Default state
                g2.setColor(TEXT_LIGHT);
                g2.drawString(label, lx, ly);
            }
            
            g2.dispose();
        }
        
        public BoardPanel getBoardPanel() { return boardPanel; }
        public void setActive(boolean active) { this.active = active; repaint(); }
    }
    
    // ===================================================================
    // FX OVERLAY (hit/miss/sunk animations, status banner, hover brackets)
    // ===================================================================
    private class FXOverlay extends JPanel {
        private final List<CellFlash> flashes = new ArrayList<>();
        private String bannerMsg = "";
        private Color bannerColor = CYAN_ACCENT;
        private BoardFrame bannerOverFrame = null;
        private long bannerShown = 0;
        private static final int BANNER_DURATION = 2500;
        
        private Timer animTimer;
        
        FXOverlay() {
            setOpaque(false);
            animTimer = new Timer(30, e -> {
                flashes.removeIf(f -> System.currentTimeMillis() - f.startTime > 2000);
                repaint();
            });
            animTimer.start();
        }
        
        public void addCellFlash(BoardFrame frame, int row, int col, ShotResult result) {
            BoardPanel bp = frame.getBoardPanel();
            Point origin = SwingUtilities.convertPoint(bp, col * 44 + 22, row * 44 + 22, this);
            flashes.add(new CellFlash(frame, row, col, result, origin, 44, 44, System.currentTimeMillis()));
        }
        
        public void showStatusBanner(String msg, Color color, BoardFrame overFrame) {
            this.bannerMsg = msg;
            this.bannerColor = color;
            this.bannerOverFrame = overFrame;
            this.bannerShown = System.currentTimeMillis();
        }
        
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            long now = System.currentTimeMillis();
            
            // Draw all active cell flashes
            for (CellFlash flash : flashes) {
                paintCellFlash(g2, flash, now);
            }
            
            // Draw status banner
            if (!bannerMsg.isEmpty() && bannerOverFrame != null) {
                drawStatusBanner(g2);
            }
            
            g2.dispose();
        }
        
        private void paintCellFlash(Graphics2D g2, CellFlash flash, long now) {
            long elapsed = now - flash.startTime;
            if (elapsed > 2000) return;
            
            float t = elapsed / 2000f;
            int cx = flash.origin.x;
            int cy = flash.origin.y;
            
            // Cell flash (fades out)
            float alpha = 1f - t;
            Color flashColor = switch (flash.result) {
                case HIT -> HIT_GOLD;
                case MISS -> MISS_WHITE;
                case SUNK -> SUNK_AMBER;
                default -> CYAN_ACCENT;
            };
            g2.setColor(new Color(flashColor.getRed(), flashColor.getGreen(), flashColor.getBlue(), (int)(200 * alpha)));
            g2.fillRect(cx - flash.cellW / 2, cy - flash.cellH / 2, flash.cellW, flash.cellH);
            
            // Expanding shockwave ring
            if (flash.result != ShotResult.MISS) {
                int radius = (int)(80 * t);
                g2.setStroke(new BasicStroke(3f));
                g2.setColor(new Color(flashColor.getRed(), flashColor.getGreen(), flashColor.getBlue(), (int)(150 * (1 - t))));
                g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
            }
            
            // Lock-on brackets snapping in (hit/sunk only)
            if (flash.result != ShotResult.MISS && t < 0.3f) {
                float snap = t / 0.3f;
                int bw = (int)(flash.cellW * 0.4f);
                int gap = (int)(flash.cellW * 0.6f * (1 - snap));
                g2.setStroke(new BasicStroke(2.5f));
                g2.setColor(new Color(TARGET_RED.getRed(), TARGET_RED.getGreen(), TARGET_RED.getBlue(), (int)(220 * (1 - t))));
                
                // Top-left bracket
                g2.drawLine(cx - flash.cellW / 2 - gap, cy - flash.cellH / 2 - gap,
                            cx - flash.cellW / 2 - gap + bw, cy - flash.cellH / 2 - gap);
                g2.drawLine(cx - flash.cellW / 2 - gap, cy - flash.cellH / 2 - gap,
                            cx - flash.cellW / 2 - gap, cy - flash.cellH / 2 - gap + bw);
                
                // Bottom-right bracket
                g2.drawLine(cx + flash.cellW / 2 + gap, cy + flash.cellH / 2 + gap,
                            cx + flash.cellW / 2 + gap - bw, cy + flash.cellH / 2 + gap);
                g2.drawLine(cx + flash.cellW / 2 + gap, cy + flash.cellH / 2 + gap,
                            cx + flash.cellW / 2 + gap, cy + flash.cellH / 2 + gap - bw);
            }
            
            // Floating label (rises and fades)
            String label = switch (flash.result) {
                case HIT -> "HIT!";
                case MISS -> "MISS!";
                case SUNK -> "SUNK!";
                default -> "?";
            };
            g2.setFont(FONT_BIG);
            FontMetrics fm = g2.getFontMetrics();
            int rise = (int)(60 * t);
            int lx = cx - fm.stringWidth(label) / 2;
            int ly = cy - rise;
            g2.setColor(new Color(0, 0, 0, (int)(180 * (1 - t))));
            g2.drawString(label, lx + 2, ly + 2);
            g2.setColor(new Color(flashColor.getRed(), flashColor.getGreen(), flashColor.getBlue(), (int)(255 * (1 - t))));
            g2.drawString(label, lx, ly);
        }
        
        private void drawStatusBanner(Graphics2D g2) {
            long elapsed = System.currentTimeMillis() - bannerShown;
            float alpha;
            if (elapsed < 200) alpha = elapsed / 200f;
            else if (elapsed > BANNER_DURATION - 400) alpha = Math.max(0, (BANNER_DURATION - elapsed) / 400f);
            else alpha = 1f;
            alpha = Math.max(0, Math.min(1f, alpha));
            
            // Position: centered horizontally on the overlay, lower-center
            int overlayW = getWidth();
            int overlayH = getHeight();
            
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setFont(FONT_BIG);
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(bannerMsg);
            int boxW = tw + 80;
            int boxH = fm.getHeight() + 24;
            int bx = (overlayW - boxW) / 2;
            int by = overlayH - boxH - 16;
            
            // Soft drop shadow
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(bx + 3, by + 4, boxW, boxH, 12, 12);
            // Body
            g2.setColor(new Color(0x12, 0x2A, 0x2E, 235));
            g2.fillRoundRect(bx, by, boxW, boxH, 12, 12);
            
            // Double border
            g2.setStroke(new BasicStroke(2.6f));
            g2.setColor(bannerColor);
            g2.drawRoundRect(bx, by, boxW, boxH, 12, 12);
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(new Color(bannerColor.getRed(), bannerColor.getGreen(), bannerColor.getBlue(), 60));
            g2.drawRoundRect(bx + 4, by + 4, boxW - 8, boxH - 8, 8, 8);
            
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
        BoardFrame frame;
        int row, col;
        ShotResult result;
        Point origin;
        int cellW, cellH;
        long startTime;
        
        CellFlash(BoardFrame f, int r, int c, ShotResult res, Point o, int cw, int ch, long t) {
            frame = f; row = r; col = c; result = res; origin = o; cellW = cw; cellH = ch; startTime = t;
        }
    }
}