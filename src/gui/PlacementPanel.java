package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.*;
import models.Board;
import models.Cell;
import models.Ship;

/**
 * PlacementPanel - TideBound naval-themed redesign.
 * Matches the visual language of MainMenuPanel and CharacterSelectPanel:
 * weathered industrial frames, cyan accent lights, copper rivets, pixel typography.
 *
 * Layout:
 *   [ Header bar - "PLAYER X // FLEET DEPLOYMENT" ]
 *   [ Fleet Roster | Tactical Grid | Status Panel ]
 *   [ Rotate | Undo | Reset | Deploy ]
 */
public class PlacementPanel extends JPanel {

    // ---------- Game state (preserved from original) ----------
    private final int SIZE = 10;
    private GridCell[][] gridCells;
    private Board currentBoard;
    private ArrayList<Ship> shipsToPlace;
    private ArrayList<Ship> originalShipsToPlace;
    private Ship currentShip;
    private boolean horizontal = true;
    private PlacementListener listener;
    private Stack<UndoAction> undoStack;

    // ---------- UI references ----------
    private FleetRosterPanel rosterPanel;
    private StatusPanel statusPanel;
    private TacticalGridPanel gridPanel;
    private NavalButton rotateButton;
    private NavalButton undoButton;
    private NavalButton resetButton;
    private NavalButton deployButton;

    // ---------- Sprite cache ----------
    private static final Map<String, BufferedImage> SPRITE_CACHE = new HashMap<>();
    private BufferedImage backgroundImage;

    // ---------- TideBound palette ----------
    private static final Color BG_DEEP        = new Color(0x0F, 0x23, 0x26);
    private static final Color FRAME_FILL     = new Color(0x1E, 0x45, 0x48);
    private static final Color FRAME_FILL_HI  = new Color(0x2A, 0x5A, 0x5E);
    private static final Color FRAME_BORDER   = new Color(0x3A, 0x7A, 0x7E);
    private static final Color FRAME_SHADOW   = new Color(0x08, 0x18, 0x1A);
    private static final Color CYAN_ACCENT    = new Color(0x5F, 0xD4, 0xE0);
    private static final Color CYAN_GLOW      = new Color(0x5F, 0xD4, 0xE0, 90);
    private static final Color COPPER         = new Color(0xC9, 0x7A, 0x3D);
    private static final Color RIVET          = new Color(0x6B, 0x4A, 0x2A);
    private static final Color TEXT_LIGHT     = new Color(0xE8, 0xF4, 0xF6);
    private static final Color TEXT_DIM       = new Color(0x8A, 0xA8, 0xAC);
    private static final Color OCEAN_TILE     = new Color(0x1A, 0x3D, 0x4A);
    private static final Color OCEAN_TILE_LO  = new Color(0x14, 0x30, 0x3A);
    private static final Color GRID_LINE      = new Color(0x2D, 0x5D, 0x6A);
    private static final Color PREVIEW_OK     = new Color(0x5F, 0xD4, 0xE0, 110);
    private static final Color PREVIEW_BAD    = new Color(0xE0, 0x5F, 0x5F, 130);

    // Pixel font fallback. Swap "Press Start 2P" / your bundled pixel TTF here.
    private static final Font FONT_HEADER  = pickFont(22f, Font.BOLD);
    private static final Font FONT_LABEL   = pickFont(14f, Font.BOLD);
    private static final Font FONT_SMALL   = pickFont(11f, Font.PLAIN);
    private static final Font FONT_BUTTON  = pickFont(13f, Font.BOLD);

    private static Font pickFont(float size, int style) {
        // Prefer bundled pixel font if available; otherwise fall back gracefully.
        String[] candidates = {"Press Start 2P", "Consolas", "Monospaced"};
        for (String name : candidates) {
            Font f = new Font(name, style, (int) size);
            if (f.getFamily().equalsIgnoreCase(name) || name.equals("Monospaced")) return f;
        }
        return new Font(Font.MONOSPACED, style, (int) size);
    }

    // ---------- Inner types ----------
    private class UndoAction {
        Ship ship;
        int startX;
        int startY;
        boolean wasHorizontal;
        UndoAction(Ship ship, int x, int y, boolean horizontal) {
            this.ship = ship; this.startX = x; this.startY = y; this.wasHorizontal = horizontal;
        }
    }

    public interface PlacementListener {
    void onPlacementComplete(Board board);
}

    /** Single grid cell record (replaces JButton[][] for full custom rendering). */
    private static class GridCell {
        boolean hasShip = false;
        boolean previewOk = false;
        boolean previewBad = false;
        String shipName = null;     // for sprite lookup
        int shipIndexInRow = -1;    // 0 = head, used for sprite slicing
        int shipSize = 0;
        boolean shipHorizontal = true;
    }

    // ===================================================================
    // CONSTRUCTOR
    // ===================================================================
    public PlacementPanel(PlacementListener listener) {
        
        
        this.listener = listener;
        this.currentBoard = new Board();
        this.shipsToPlace = new ArrayList<>();
        this.originalShipsToPlace = new ArrayList<>();
        this.undoStack = new Stack<>();

        originalShipsToPlace.add(new Ship("Carrier", 5));
        originalShipsToPlace.add(new Ship("Battleship", 4));
        originalShipsToPlace.add(new Ship("Cruiser", 3));
        originalShipsToPlace.add(new Ship("Submarine", 3));
        originalShipsToPlace.add(new Ship("Destroyer", 2));

        // pre-cache ship sprites
        for (Ship s : originalShipsToPlace) loadSprite(s.getName().toLowerCase());

        // load background (graceful fallback)
        try {
            File bg = new File("assets/shipPlacementbg.jpg");
            if (bg.exists()) backgroundImage = ImageIO.read(bg);
        } catch (Exception ignored) {}

        resetShipList();
        buildUI();
    }

    // ===================================================================
    // UI CONSTRUCTION
    // ===================================================================
    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DEEP);
        setPreferredSize(new Dimension(1280, 820));

        // HEADER
        add(new HeaderPanel(), BorderLayout.NORTH);

        // CENTER (roster | grid | status)
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        rosterPanel = new FleetRosterPanel();
        gridPanel = new TacticalGridPanel();
        statusPanel = new StatusPanel();

        gbc.gridx = 0; gbc.weightx = 0.22; center.add(rosterPanel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.56; center.add(gridPanel, gbc);
        gbc.gridx = 2; gbc.weightx = 0.22; center.add(statusPanel, gbc);

        add(center, BorderLayout.CENTER);

        // FOOTER (action buttons)
        add(buildFooter(), BorderLayout.SOUTH);

        // initialize cell records
        gridCells = new GridCell[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                gridCells[r][c] = new GridCell();

        refreshAll();
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 14)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                paintMetalBar(g2, 0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        footer.setOpaque(false);
        footer.setPreferredSize(new Dimension(0, 78));

        rotateButton = new NavalButton("ROTATE", CYAN_ACCENT);
        undoButton   = new NavalButton("UNDO",   COPPER);
        resetButton  = new NavalButton("RESET",  new Color(0xD0, 0x55, 0x55));
        deployButton = new NavalButton("DEPLOY", new Color(0x6B, 0xD0, 0x8C));

        rotateButton.addActionListener(e -> toggleRotation());
        undoButton.addActionListener(e -> undoLastPlacement());
        resetButton.addActionListener(e -> resetAllShips());
        deployButton.addActionListener(e -> {
            if (listener != null) listener.onPlacementComplete(currentBoard);
        });
        undoButton.setEnabled(false);
        deployButton.setEnabled(false);

        footer.add(rotateButton);
        footer.add(undoButton);
        footer.add(resetButton);
        footer.add(deployButton);
        return footer;
    }

    // ===================================================================
    // HEADER
    // ===================================================================
    private class HeaderPanel extends JPanel {
        HeaderPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 90));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            paintMetalBar(g2, 0, 0, w, h);

            // central engraved nameplate
            int plateW = 560, plateH = 56;
            int px = (w - plateW) / 2, py = (h - plateH) / 2;
            paintEngravedPlate(g2, px, py, plateW, plateH);

            // text
            g2.setFont(FONT_HEADER);
            String text = "FLEET DEPLOYMENT";
            FontMetrics fm = g2.getFontMetrics();
            int tx = px + (plateW - fm.stringWidth(text)) / 2;
            int ty = py + (plateH + fm.getAscent()) / 2 - 4;
            g2.setColor(FRAME_SHADOW);
            g2.drawString(text, tx + 1, ty + 1);
            g2.setColor(TEXT_LIGHT);
            g2.drawString(text, tx, ty);

            // cyan status lights flanking the plate
            paintStatusLight(g2, px - 36, py + plateH / 2, true);
            paintStatusLight(g2, px + plateW + 36, py + plateH / 2, true);

            // vents
            paintVent(g2, 24, py + 6, 110, plateH - 12);
            paintVent(g2, w - 134, py + 6, 110, plateH - 12);

            g2.dispose();
        }
    }

    // ===================================================================
    // FLEET ROSTER (left sidebar)
    // ===================================================================
    private class FleetRosterPanel extends JPanel {
        FleetRosterPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(260, 0));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            paintRivetedFrame(g2, 0, 0, w, h);

            // sub-header
            g2.setFont(FONT_LABEL);
            g2.setColor(CYAN_ACCENT);
            String title = "FLEET ROSTER";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, (w - fm.stringWidth(title)) / 2, 32);

            // separator line
            g2.setColor(FRAME_BORDER);
            g2.drawLine(20, 44, w - 20, 44);

            // ship cards
            int y = 60;
            int cardH = 84;
            int cardW = w - 30;
            int cardX = 15;

            for (Ship s : originalShipsToPlace) {
                boolean placed = isShipPlaced(s.getName());
                boolean isCurrent = currentShip != null && currentShip.getName().equals(s.getName());
                paintShipCard(g2, cardX, y, cardW, cardH, s, placed, isCurrent);
                y += cardH + 10;
            }

            g2.dispose();
        }

        private boolean isShipPlaced(String name) {
            for (Ship s : currentBoard.getShips()) if (s.getName().equals(name)) return true;
            return false;
        }
    }

    private void paintShipCard(Graphics2D g2, int x, int y, int w, int h, Ship ship,
                               boolean placed, boolean isCurrent) {
        // card background
        Color fill = isCurrent ? FRAME_FILL_HI : FRAME_FILL;
        g2.setColor(FRAME_SHADOW);
        g2.fillRoundRect(x + 2, y + 2, w, h, 6, 6);
        g2.setColor(fill);
        g2.fillRoundRect(x, y, w, h, 6, 6);

        // border (cyan glow when current)
        if (isCurrent) {
            g2.setStroke(new BasicStroke(2.4f));
            g2.setColor(CYAN_ACCENT);
        } else {
            g2.setStroke(new BasicStroke(1.4f));
            g2.setColor(FRAME_BORDER);
        }
        g2.drawRoundRect(x, y, w, h, 6, 6);
        g2.setStroke(new BasicStroke(1f));

        // ship sprite
        BufferedImage sprite = loadSprite(ship.getName().toLowerCase());
        int spriteH = h - 30;
        int spriteW = (int)(spriteH * 2.4);
        if (spriteW > w - 80) spriteW = w - 80;
        int sx = x + 12, sy = y + 8;
        if (sprite != null) {
            g2.drawImage(sprite, sx, sy, spriteW, spriteH, null);
        } else {
            g2.setColor(GRID_LINE);
            g2.fillRect(sx, sy, spriteW, spriteH);
        }

        // ship name + size
        g2.setFont(FONT_LABEL);
        g2.setColor(TEXT_LIGHT);
        g2.drawString(ship.getName().toUpperCase(), x + 12, y + h - 22);
        g2.setFont(FONT_SMALL);
        g2.setColor(TEXT_DIM);
        g2.drawString(ship.getSize() + " CELLS", x + 12, y + h - 8);

        // status indicator (right side)
        int dotX = x + w - 22, dotY = y + 14;
        if (placed) {
            g2.setColor(new Color(0x6B, 0xD0, 0x8C));
            g2.fillOval(dotX, dotY, 12, 12);
            g2.setFont(FONT_SMALL);
            g2.drawString("OK", dotX - 18, dotY + 26);
        } else if (isCurrent) {
            g2.setColor(CYAN_ACCENT);
            g2.fillOval(dotX, dotY, 12, 12);
            g2.setFont(FONT_SMALL);
            g2.drawString("NOW", dotX - 22, dotY + 26);
        } else {
            g2.setColor(TEXT_DIM);
            g2.drawOval(dotX, dotY, 12, 12);
        }

        // size pips along the bottom
        int pipY = y + h - 4;
        int pipX = x + w - 12 - ship.getSize() * 8;
        for (int i = 0; i < ship.getSize(); i++) {
            g2.setColor(placed ? new Color(0x6B, 0xD0, 0x8C) : TEXT_DIM);
            g2.fillRect(pipX + i * 8, pipY - 4, 6, 3);
        }
    }

    // ===================================================================
    // STATUS PANEL (right sidebar)
    // ===================================================================
    private class StatusPanel extends JPanel {
        StatusPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(260, 0));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            paintRivetedFrame(g2, 0, 0, w, h);

            // header
            g2.setFont(FONT_LABEL);
            g2.setColor(CYAN_ACCENT);
            String title = "DEPLOYMENT STATUS";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, (w - fm.stringWidth(title)) / 2, 32);
            g2.setColor(FRAME_BORDER);
            g2.drawLine(20, 44, w - 20, 44);

            int y = 60;

            if (currentShip != null) {
                // current ship label
                g2.setFont(FONT_SMALL);
                g2.setColor(TEXT_DIM);
                g2.drawString("DEPLOYING:", 20, y); y += 18;

                g2.setFont(FONT_HEADER.deriveFont(18f));
                g2.setColor(TEXT_LIGHT);
                g2.drawString(currentShip.getName().toUpperCase(), 20, y); y += 16;

                // sprite preview box
                y += 8;
                int boxX = 20, boxW = w - 40, boxH = 90;
                g2.setColor(OCEAN_TILE_LO);
                g2.fillRoundRect(boxX, y, boxW, boxH, 4, 4);
                g2.setColor(FRAME_BORDER);
                g2.drawRoundRect(boxX, y, boxW, boxH, 4, 4);

                BufferedImage sprite = loadSprite(currentShip.getName().toLowerCase());
                if (sprite != null) {
                    int sH = boxH - 20;
                    int sW = Math.min(boxW - 20, sH * 3);
                    int sx = boxX + (boxW - sW) / 2;
                    int sy = y + (boxH - sH) / 2;
                    g2.drawImage(sprite, sx, sy, sW, sH, null);
                }
                y += boxH + 16;

                // size
                g2.setFont(FONT_SMALL);
                g2.setColor(TEXT_DIM);
                g2.drawString("LENGTH:", 20, y);
                g2.setColor(TEXT_LIGHT);
                g2.drawString(currentShip.getSize() + " CELLS", 100, y);
                y += 22;

                // orientation
                g2.setColor(TEXT_DIM);
                g2.drawString("ORIENTATION:", 20, y);
                g2.setColor(CYAN_ACCENT);
                String arrow = horizontal ? "\u25C4 \u25BA" : "\u25B2 \u25BC";
                g2.drawString(arrow, 130, y);
                g2.setColor(TEXT_LIGHT);
                g2.drawString(horizontal ? "HORIZONTAL" : "VERTICAL", 20, y + 16);
                y += 38;

                // deployed count
                g2.setColor(TEXT_DIM);
                g2.drawString("DEPLOYED:", 20, y);
                int deployed = currentBoard.getShips().size();
                g2.setColor(TEXT_LIGHT);
                g2.drawString(deployed + " / " + originalShipsToPlace.size(), 100, y);
                y += 28;

                // hint box
                paintHintBox(g2, 14, y, w - 28,
                    "Click grid to place. Use ROTATE to flip orientation.");
            } else {
                // all ships placed
                g2.setFont(FONT_HEADER.deriveFont(16f));
                g2.setColor(new Color(0x6B, 0xD0, 0x8C));
                g2.drawString("FLEET READY", 20, y); y += 24;
                g2.setFont(FONT_SMALL);
                g2.setColor(TEXT_LIGHT);
                g2.drawString("All vessels deployed.", 20, y); y += 16;
                g2.drawString("Press DEPLOY to confirm.", 20, y); y += 30;
                paintHintBox(g2, 14, y, w - 28, "You may RESET to redeploy your fleet.");
            }

            g2.dispose();
        }
    }

    private void paintHintBox(Graphics2D g2, int x, int y, int w, String text) {
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(x, y, w, 60, 4, 4);
        g2.setColor(FRAME_BORDER);
        g2.drawRoundRect(x, y, w, 60, 4, 4);

        g2.setFont(FONT_SMALL);
        g2.setColor(TEXT_DIM);
        // crude wrap
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int ly = y + 18;
        for (String word : words) {
            String trial = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(trial) > w - 16) {
                g2.drawString(line.toString(), x + 8, ly);
                ly += 14;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(trial);
            }
        }
        if (line.length() > 0) g2.drawString(line.toString(), x + 8, ly);
    }

    // ===================================================================
    // TACTICAL GRID (center)
    // ===================================================================
    private class TacticalGridPanel extends JPanel {
        TacticalGridPanel() {
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int[] rc = pickCell(e.getX(), e.getY());
                    if (rc != null) handleGridClick(rc[0], rc[1]);
                }
                @Override public void mouseExited(MouseEvent e) {
                    clearPreview();
                    repaint();
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    int[] rc = pickCell(e.getX(), e.getY());
                    if (rc != null) {
                        showPreview(rc[0], rc[1]);
                        repaint();
                    } else {
                        clearPreview();
                        repaint();
                    }
                }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            paintRivetedFrame(g2, 0, 0, w, h);

            Rectangle gridRect = computeGridRect();

            // background image inside grid area
            if (backgroundImage != null) {
                g2.drawImage(backgroundImage, gridRect.x, gridRect.y, gridRect.width, gridRect.height, null);
                g2.setColor(new Color(0, 0, 0, 90));
                g2.fillRect(gridRect.x, gridRect.y, gridRect.width, gridRect.height);
            } else {
                g2.setColor(OCEAN_TILE);
                g2.fillRect(gridRect.x, gridRect.y, gridRect.width, gridRect.height);
            }

            // coordinate labels
            int cellSize = gridRect.width / SIZE;
            g2.setFont(FONT_SMALL);
            g2.setColor(CYAN_ACCENT);
            for (int c = 0; c < SIZE; c++) {
                String lbl = String.valueOf((char)('A' + c));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(lbl, gridRect.x + c * cellSize + (cellSize - fm.stringWidth(lbl)) / 2,
                              gridRect.y - 6);
            }
            for (int r = 0; r < SIZE; r++) {
                String lbl = String.valueOf(r + 1);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(lbl, gridRect.x - 18,
                              gridRect.y + r * cellSize + (cellSize + fm.getAscent()) / 2 - 2);
            }

            // cells
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    int cx = gridRect.x + c * cellSize;
                    int cy = gridRect.y + r * cellSize;
                    GridCell gc = gridCells[r][c];

                    // checkerboard tinting on water
                    if (((r + c) & 1) == 0) {
                        g2.setColor(new Color(0, 0, 0, 30));
                        g2.fillRect(cx, cy, cellSize, cellSize);
                    }

                    // grid lines
                    g2.setColor(GRID_LINE);
                    g2.drawRect(cx, cy, cellSize, cellSize);

                    // ship sprite (drawn per-ship so we can slice across cells)
                    // see ship draw pass below
                    if (gc.previewOk) {
                        g2.setColor(PREVIEW_OK);
                        g2.fillRect(cx + 1, cy + 1, cellSize - 1, cellSize - 1);
                        g2.setColor(CYAN_ACCENT);
                        g2.drawRect(cx + 1, cy + 1, cellSize - 3, cellSize - 3);
                    } else if (gc.previewBad) {
                        g2.setColor(PREVIEW_BAD);
                        g2.fillRect(cx + 1, cy + 1, cellSize - 1, cellSize - 1);
                    }
                }
            }

            // draw placed ship sprites stretched across their cells
            for (Ship s : currentBoard.getShips()) {
                drawPlacedShip(g2, s, gridRect, cellSize);
            }

            // outer cyan glow border around the grid
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(CYAN_ACCENT);
            g2.drawRect(gridRect.x - 1, gridRect.y - 1, gridRect.width + 2, gridRect.height + 2);
            g2.setStroke(new BasicStroke(1f));

            g2.dispose();
        }

        private Rectangle computeGridRect() {
            int w = getWidth(), h = getHeight();
            int margin = 36;
            int available = Math.min(w - margin * 2, h - margin * 2);
            available -= available % SIZE; // ensure cells divide evenly
            int gx = (w - available) / 2;
            int gy = (h - available) / 2;
            return new Rectangle(gx, gy, available, available);
        }

        private int[] pickCell(int mx, int my) {
            Rectangle r = computeGridRect();
            if (!r.contains(mx, my)) return null;
            int cs = r.width / SIZE;
            int col = (mx - r.x) / cs;
            int row = (my - r.y) / cs;
            if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return null;
            return new int[]{row, col};
        }
    }

    private void drawPlacedShip(Graphics2D g2, Ship s, Rectangle gridRect, int cellSize) {
        if (s.getPositions().isEmpty()) return;
        BufferedImage sprite = loadSprite(s.getName().toLowerCase());

        // In Board.placeShip, positions are stored as Coordinate(row, col).
        // So getX() == row, getY() == col.
        Ship.Coordinate first = s.getPositions().get(0);
        int row = first.getX();
        int col = first.getY();

        // Determine orientation: if the second cell's row differs, ship runs vertically.
        boolean horiz = true;
        if (s.getPositions().size() > 1) {
            Ship.Coordinate second = s.getPositions().get(1);
            horiz = (second.getX() == row); // same row -> horizontal
        }

        // Pixel coordinates of the ship's bounding box on the grid.
        int px = gridRect.x + col * cellSize;
        int py = gridRect.y + row * cellSize;
        int w  = horiz ? cellSize * s.getSize() : cellSize;
        int h  = horiz ? cellSize : cellSize * s.getSize();

        if (sprite != null) {
            if (horiz) {
                g2.drawImage(sprite, px + 2, py + 4, w - 4, h - 8, null);
            } else {
                // Rotate sprite 90deg clockwise around the bounding box's center.
                Graphics2D rg = (Graphics2D) g2.create();
                rg.translate(px + w / 2, py + h / 2);
                rg.rotate(Math.PI / 2);
                // After rotation, sprite's "width" maps to ship's vertical span (h)
                // and sprite's "height" maps to the cell width (w).
                int drawW = h - 8;
                int drawH = w - 4;
                rg.drawImage(sprite, -drawW / 2, -drawH / 2, drawW, drawH, null);
                rg.dispose();
            }
        } else {
            g2.setColor(new Color(0x6B, 0xD0, 0x8C));
            g2.fillRect(px + 2, py + 2, w - 4, h - 4);
        }

        // subtle cyan tint outline
        g2.setColor(new Color(CYAN_ACCENT.getRed(), CYAN_ACCENT.getGreen(), CYAN_ACCENT.getBlue(), 80));
        g2.drawRect(px + 1, py + 1, w - 2, h - 2);
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
            setPreferredSize(new Dimension(150, 48));
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

            // shadow
            g2.setColor(FRAME_SHADOW);
            g2.fillRoundRect(2, 3, w - 4, h - 4, 8, 8);

            // body gradient
            Color top = isEnabled() ? FRAME_FILL_HI : FRAME_FILL.darker();
            Color bot = isEnabled() ? FRAME_FILL : FRAME_FILL.darker().darker();
            g2.setPaint(new GradientPaint(0, 0, top, 0, h, bot));
            g2.fillRoundRect(0, offset, w - 4, h - 4 - offset, 8, 8);

            // accent strip on top
            g2.setColor(isEnabled() ? accent : TEXT_DIM);
            g2.fillRect(8, 4 + offset, w - 20, 2);

            // border
            g2.setStroke(new BasicStroke(1.6f));
            g2.setColor(hover && isEnabled() ? accent : FRAME_BORDER);
            g2.drawRoundRect(0, offset, w - 5, h - 5 - offset, 8, 8);

            // rivets
            g2.setColor(RIVET);
            g2.fillOval(5, 5 + offset, 4, 4);
            g2.fillOval(w - 13, 5 + offset, 4, 4);
            g2.fillOval(5, h - 13 - offset, 4, 4);
            g2.fillOval(w - 13, h - 13 - offset, 4, 4);

            // text
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
    // FRAME PAINTING HELPERS
    // ===================================================================
    private void paintRivetedFrame(Graphics2D g2, int x, int y, int w, int h) {
        // outer shadow
        g2.setColor(FRAME_SHADOW);
        g2.fillRoundRect(x, y, w, h, 12, 12);
        // body
        g2.setPaint(new GradientPaint(0, y, FRAME_FILL_HI, 0, y + h, FRAME_FILL));
        g2.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 12, 12);
        // border
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(FRAME_BORDER);
        g2.drawRoundRect(x + 2, y + 2, w - 5, h - 5, 12, 12);
        // inner darker area
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillRoundRect(x + 8, y + 8, w - 16, h - 16, 8, 8);
        // corner rivets
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
        // corner rivets
        g2.setColor(COPPER);
        for (int rx : new int[]{x + 14, x + w - 22}) {
            g2.fillOval(rx, y + 14, 7, 7);
            g2.fillOval(rx, y + h - 25, 7, 7);
        }
    }

    private void paintEngravedPlate(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(FRAME_SHADOW);
        g2.fillRoundRect(x + 2, y + 2, w, h, 8, 8);
        g2.setPaint(new GradientPaint(0, y, new Color(0x12, 0x2A, 0x2E), 0, y + h, new Color(0x08, 0x18, 0x1A)));
        g2.fillRoundRect(x, y, w, h, 8, 8);
        g2.setStroke(new BasicStroke(1.6f));
        g2.setColor(CYAN_ACCENT);
        g2.drawRoundRect(x, y, w, h, 8, 8);
        // inner highlight
        g2.setColor(new Color(CYAN_ACCENT.getRed(), CYAN_ACCENT.getGreen(), CYAN_ACCENT.getBlue(), 40));
        g2.drawRoundRect(x + 4, y + 4, w - 8, h - 8, 6, 6);
    }

    private void paintStatusLight(Graphics2D g2, int cx, int cy, boolean on) {
        Color c = on ? CYAN_ACCENT : TEXT_DIM;
        // glow
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 70));
        g2.fillOval(cx - 14, cy - 14, 28, 28);
        // body
        g2.setColor(c);
        g2.fillOval(cx - 6, cy - 6, 12, 12);
        // hot center
        g2.setColor(Color.WHITE);
        g2.fillOval(cx - 2, cy - 2, 4, 4);
    }

    private void paintVent(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(FRAME_SHADOW);
        g2.fillRoundRect(x, y, w, h, 4, 4);
        g2.setColor(new Color(0x10, 0x22, 0x24));
        int slats = 6;
        int slatH = (h - 8) / slats;
        for (int i = 0; i < slats; i++) {
            g2.fillRect(x + 4, y + 4 + i * slatH, w - 8, slatH - 2);
        }
        g2.setStroke(new BasicStroke(1.2f));
        g2.setColor(FRAME_BORDER);
        g2.drawRoundRect(x, y, w, h, 4, 4);
    }

    // ===================================================================
    // GAME LOGIC (preserved + adapted)
    // ===================================================================
    private void resetShipList() {
        shipsToPlace.clear();
        for (Ship ship : originalShipsToPlace) {
            shipsToPlace.add(new Ship(ship.getName(), ship.getSize()));
        }
        currentShip = shipsToPlace.remove(0);
        undoStack.clear();
        if (undoButton != null) undoButton.setEnabled(false);
    }

    private void undoLastPlacement() {
        if (undoStack.isEmpty()) { undoButton.setEnabled(false); return; }
        UndoAction last = undoStack.pop();

        for (Ship.Coordinate pos : last.ship.getPositions()) {
            Cell cell = currentBoard.getCell(pos.getX(), pos.getY());
            cell.setHasShip(false);
            cell.setShip(null);
        }
        last.ship.getPositions().clear();
        currentBoard.getShips().remove(last.ship);

        shipsToPlace.add(0, last.ship);
        currentShip = shipsToPlace.get(0);

        rotateButton.setEnabled(true);
        deployButton.setEnabled(false);
        undoButton.setEnabled(!undoStack.isEmpty());

        clearPreview();
        refreshAll();
    }

    private void resetAllShips() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Reset all ship placements?\nThis will clear your current layout.",
            "Reset Fleet",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        currentBoard = new Board();
        resetShipList();
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                gridCells[r][c] = new GridCell();

        rotateButton.setEnabled(true);
        horizontal = true;
        deployButton.setEnabled(false);
        undoButton.setEnabled(false);
        refreshAll();
    }

    private void toggleRotation() {
        horizontal = !horizontal;
        refreshAll();
    }

    private void showPreview(int row, int col) {
        clearPreview();
        if (currentShip == null) return;

        boolean valid = true;
        int len = currentShip.getSize();
        if (horizontal) {
            if (col + len > SIZE) valid = false;
            else for (int i = 0; i < len; i++) {
                Cell c = currentBoard.getCell(row, col + i);
                if (c.hasShip() || c.isFiredUpon()) { valid = false; break; }
            }
        } else {
            if (row + len > SIZE) valid = false;
            else for (int i = 0; i < len; i++) {
                Cell c = currentBoard.getCell(row + i, col);
                if (c.hasShip() || c.isFiredUpon()) { valid = false; break; }
            }
        }

        for (int i = 0; i < len; i++) {
            int r = horizontal ? row : Math.min(row + i, SIZE - 1);
            int c = horizontal ? Math.min(col + i, SIZE - 1) : col;
            if (horizontal && col + i >= SIZE) break;
            if (!horizontal && row + i >= SIZE) break;
            if (valid) gridCells[r][c].previewOk = true;
            else gridCells[r][c].previewBad = true;
        }
    }

    private void clearPreview() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++) {
                gridCells[r][c].previewOk = false;
                gridCells[r][c].previewBad = false;
            }
    }

    private void handleGridClick(int row, int col) {
        if (currentShip == null) return;

        boolean placed = currentBoard.placeShip(currentShip, row, col, horizontal);
        if (placed) {
            Ship placedShip = currentShip;
            undoStack.push(new UndoAction(placedShip, row, col, horizontal));
            undoButton.setEnabled(true);

            if (!shipsToPlace.isEmpty()) {
                currentShip = shipsToPlace.remove(0);
            } else {
                currentShip = null;
                deployButton.setEnabled(true);
                rotateButton.setEnabled(false);
            }
            clearPreview();
            refreshAll();
        } else {
            // Clear preview state before showing the dialog so we don't leave
            // red/cyan ghost cells on screen after the user dismisses it.
            clearPreview();
            refreshAll();
            JOptionPane.showMessageDialog(this,
                "Cannot deploy vessel there. Try another position.",
                "Invalid Placement",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshAll() {
        if (rosterPanel != null) rosterPanel.repaint();
        if (statusPanel != null) statusPanel.repaint();
        if (gridPanel  != null) gridPanel.repaint();
    }

    // ===================================================================
    // SPRITE LOADING
    // ===================================================================
    private static BufferedImage loadSprite(String name) {
        if (SPRITE_CACHE.containsKey(name)) return SPRITE_CACHE.get(name);
        BufferedImage img = null;
        try {
            File f = new File("assets/" + name + ".png");
            if (f.exists()) img = ImageIO.read(f);
        } catch (Exception ignored) {}
        SPRITE_CACHE.put(name, img); // null cached too (avoid retry)
        return img;
    }
}