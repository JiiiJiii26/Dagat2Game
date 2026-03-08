package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MainMenuPanel extends JPanel {

    private JButton startButton;
    private JButton vsButton;
    private JButton optionsButton;
    private JButton exitButton;
    private final MenuListener listener;

    // --- Particle lists ---
    private final ArrayList<SmokeParticle> smokeList = new ArrayList<>();
    private final ArrayList<SplashParticle> splashList = new ArrayList<>();
    private final ArrayList<CannonFlash> flashList = new ArrayList<>();
    private final Random random = new Random();

    // Title drawn in paintComponent — TITLE_SIZE only affects visuals, never the
    // layout
    private static final int TITLE_SIZE = 300; // visual size of the title image (pixels)
    private static final int TITLE_Y = 20; // y-position of title on screen
    private static final int TITLE_SPACER_H = 280; // FIXED spacer height — completely independent of TITLE_SIZE
    private Image scaledTitle;

    // Cannon fire positions (roughly where the ships are in the image)
    private static final int[][] CANNON_POSITIONS = {
            { 400, 420 }, { 520, 400 }, { 650, 380 }, { 750, 360 }, { 900, 370 }
    };

    // Water splash positions
    private static final int[][] SPLASH_POSITIONS = {
            { 150, 500 }, { 250, 480 }, { 350, 510 }, { 800, 460 }, { 950, 490 }, { 1100, 470 }, { 1200, 500 }
    };

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

        // Load & scale the title image once at startup
        ImageIcon rawTitle = new ImageIcon("C:/Users/Nicco/Desktop/JavaProject/Dagat2Game/assets/title.png");
        scaledTitle = rawTitle.getImage().getScaledInstance(TITLE_SIZE, TITLE_SIZE, Image.SCALE_SMOOTH);

        // Fixed-height spacer reserves space for the painted title; buttons stay in
        // CENTER permanently
        add(createTitleSpacer(), BorderLayout.NORTH);
        add(createButtonPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        // Master animation timer — drives ALL effects at 60fps
        Timer masterTimer = new Timer(16, e -> {
            spawnParticles();
            updateParticles();
            repaint();
        });
        masterTimer.start();
    }

    // ===================== PARTICLE SPAWNING =====================

    private void spawnParticles() {
        // Spawn smoke continuously from ship positions
        if (random.nextInt(3) == 0) {
            int[] pos = CANNON_POSITIONS[random.nextInt(CANNON_POSITIONS.length)];
            smokeList.add(new SmokeParticle(
                    pos[0] + random.nextInt(30) - 15,
                    pos[1] + random.nextInt(10),
                    random.nextInt(8) + 6));
        }

        // Randomly trigger cannon flashes
        if (random.nextInt(60) == 0) {
            int[] pos = CANNON_POSITIONS[random.nextInt(CANNON_POSITIONS.length)];
            flashList.add(new CannonFlash(pos[0], pos[1]));
            // Spawn a burst of smoke with each cannon fire
            for (int i = 0; i < 8; i++) {
                smokeList.add(new SmokeParticle(
                        pos[0] + random.nextInt(40) - 20,
                        pos[1] + random.nextInt(10),
                        random.nextInt(12) + 8));
            }
        }

        // Randomly trigger water splashes
        if (random.nextInt(40) == 0) {
            int[] pos = SPLASH_POSITIONS[random.nextInt(SPLASH_POSITIONS.length)];
            int cx = pos[0] + random.nextInt(60) - 30;
            int cy = pos[1] + random.nextInt(20) - 10;
            for (int i = 0; i < 12; i++) {
                splashList.add(new SplashParticle(cx, cy));
            }
        }
    }

    private void updateParticles() {
        Iterator<SmokeParticle> si = smokeList.iterator();
        while (si.hasNext()) {
            if (si.next().update())
                si.remove();
        }

        Iterator<SplashParticle> sp = splashList.iterator();
        while (sp.hasNext()) {
            if (sp.next().update())
                sp.remove();
        }

        Iterator<CannonFlash> cf = flashList.iterator();
        while (cf.hasNext()) {
            if (cf.next().update())
                cf.remove();
        }
    }

    // ===================== PAINT =====================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        ImageIcon bg = new ImageIcon("C:/Users/Nicco/Desktop/JavaProject/Dagat2Game/assets/naval.jpg");
        g2.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), null);

        // Draw title image centered horizontally — purely visual, never affects layout
        if (scaledTitle != null) {
            int tx = (getWidth() - TITLE_SIZE) / 2;
            g2.drawImage(scaledTitle, tx, TITLE_Y, null);
        }

        // Draw cannon flashes (behind smoke)
        for (CannonFlash f : flashList)
            f.draw(g2);
        // Draw smoke
        for (SmokeParticle s : smokeList)
            s.draw(g2);
        // Draw water splashes
        for (SplashParticle p : splashList)
            p.draw(g2);

        g2.dispose();
    }

    // ===================== PANELS =====================

    /** Fixed-height spacer — height is a constant, NEVER tied to TITLE_SIZE. */
    private JPanel createTitleSpacer() {
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(0, TITLE_SPACER_H));
        return spacer;
    }

    private JPanel createButtonPanel() {
        // BoxLayout respects setMaximumSize, ensuring buttons are EXACTLY the right
        // size
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        startButton = createPNG3DButton("C:/Users/Nicco/Desktop/JavaProject/Dagat2Game/assets/btn_start.png");
        vsButton = createPNG3DButton("C:/Users/Nicco/Desktop/JavaProject/Dagat2Game/assets/btn_multi.png");
        optionsButton = createPNG3DButton("C:/Users/Nicco/Desktop/JavaProject/Dagat2Game/assets/btn_options.png");
        exitButton = createPNG3DButton("C:/Users/Nicco/Desktop/JavaProject/Dagat2Game/assets/btn_exit.png");

        startButton.addActionListener(e -> listener.onStartGame());
        vsButton.addActionListener(e -> listener.on1v1Mode());
        optionsButton.addActionListener(e -> listener.onOptions());
        exitButton.addActionListener(e -> listener.onExit());

        panel.add(Box.createVerticalGlue());
        for (JButton btn : new JButton[] { startButton, vsButton, optionsButton, exitButton }) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(btn);
            panel.add(Box.createVerticalStrut(14));
        }
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        JLabel creditLabel = new JLabel("Made by Team Omen");
        creditLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        creditLabel.setForeground(Color.LIGHT_GRAY);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        creditLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(creditLabel);
        return panel;
    }

    // ===================== BUTTON =====================

    private JButton createPNG3DButton(String imagePath) {
        // ── ImageIO.read is FULLY SYNCHRONOUS — no MediaTracker, no async surprises ──
        BufferedImage original = null;
        try {
            original = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (original == null) {
            JButton fallback = new JButton("?");
            return fallback;
        }

        int origW = original.getWidth();
        int origH = original.getHeight();
        int targetH = 160; // ← change ONLY this to resize all buttons
        int targetW = (origH > 0) ? (targetH * origW / origH) : 300;

        // Synchronous scaling via BufferedImage — exact size guaranteed
        BufferedImage scaled = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gs = scaled.createGraphics();
        gs.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        gs.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        gs.drawImage(original, 0, 0, targetW, targetH, null);
        gs.dispose();

        Image hoverImg = makeBrighter(scaled);
        final int btnW = targetW + 8;
        final int btnH = targetH + 8;
        System.out.println("[Button] " + new File(imagePath).getName() + " → " + btnW + "×" + btnH);

        JButton button = new JButton() {
            private boolean pressed = false;
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        pressed = true;
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        pressed = false;
                        repaint();
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        pressed = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                int shadowSize = 6, offsetY = pressed ? 5 : 0;
                if (!pressed) {
                    g2.setColor(new Color(0, 0, 0, 150));
                    g2.fillRoundRect(shadowSize, shadowSize, w - shadowSize, h - shadowSize, 12, 12);
                }
                g2.drawImage(hovered ? hoverImg : scaled, 0, offsetY, w - shadowSize, h - shadowSize, null);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(btnW, btnH);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(btnW, btnH);
            }

            @Override
            public Dimension getMinimumSize() {
                return new Dimension(btnW, btnH);
            }
        };

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private Image makeBrighter(Image img) {
        BufferedImage buffered = new BufferedImage(img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffered.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, buffered.getWidth(), buffered.getHeight());
        g2d.dispose();
        return buffered;
    }

    // ===================== PARTICLE CLASSES =====================

    class SmokeParticle {
        float x, y, vx, vy, size, alpha;

        SmokeParticle(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.vx = random.nextFloat() * 1.5f - 0.75f;
            this.vy = -(random.nextFloat() * 1.5f + 0.5f);
            this.alpha = 0.6f;
        }

        boolean update() {
            x += vx;
            y += vy;
            size += 0.3f;
            alpha -= 0.008f;
            return alpha <= 0;
        }

        void draw(Graphics2D g2) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, alpha)));
            g2.setColor(new Color(60, 60, 60));
            g2.fillOval((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    class SplashParticle {
        float x, y, vx, vy, alpha;

        SplashParticle(int cx, int cy) {
            this.x = cx;
            this.y = cy;
            float angle = random.nextFloat() * (float) Math.PI;
            float speed = random.nextFloat() * 6f + 2f;
            this.vx = (float) (Math.cos(angle) * speed);
            this.vy = -(float) (Math.sin(angle) * speed) - 2f;
            this.alpha = 0.9f;
        }

        boolean update() {
            x += vx;
            y += vy;
            vy += 0.3f; // gravity
            alpha -= 0.025f;
            return alpha <= 0;
        }

        void draw(Graphics2D g2) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, alpha)));
            g2.setColor(new Color(200, 230, 255));
            g2.fillOval((int) x, (int) y, 4, 4);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    class CannonFlash {
        int x, y;
        float alpha;
        float size;

        CannonFlash(int x, int y) {
            this.x = x;
            this.y = y;
            this.alpha = 1.0f;
            this.size = 40f;
        }

        boolean update() {
            alpha -= 0.08f;
            size += 5f;
            return alpha <= 0;
        }

        void draw(Graphics2D g2) {
            // Outer orange glow
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, alpha * 0.5f)));
            g2.setColor(new Color(255, 120, 0));
            g2.fillOval((int) (x - size), (int) (y - size), (int) (size * 2), (int) (size * 2));
            // Inner bright white/yellow core
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, alpha)));
            g2.setColor(new Color(255, 240, 100));
            g2.fillOval((int) (x - size / 3), (int) (y - size / 3), (int) (size * 0.7f), (int) (size * 0.7f));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }
}