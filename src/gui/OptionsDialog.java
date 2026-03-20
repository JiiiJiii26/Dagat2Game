package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import models.GameSettings;

public class OptionsDialog extends JDialog {

    private final JFrame ownerFrame;
    private Point dragOffset;

    public OptionsDialog(JFrame owner) {
        super(owner, "Settings", false);
        this.ownerFrame = owner;

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setResizable(false);

        GameSettings settings = GameSettings.getInstance();
        boolean[] musicOn = { 
            settings.isMusicEnabled() 
        };

        boolean[] soundOn = { 
            settings.isSoundEffectsEnabled() 
        };

        boolean[] fullscreenOn = { 
            settings.isFullscreen() 
        };

        BufferedImage img = loadImage("assets/settings.png");
        int W = (img != null) ? img.getWidth()  : 500;
        int H = (img != null) ? img.getHeight() : 500;
        setSize(W, H);
        setLocationRelativeTo(owner);

        ImagePanel bg = new ImagePanel(img, W, H, musicOn, soundOn, fullscreenOn);
        bg.setLayout(null);
        bg.setOpaque(false);

        bg.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragOffset = e.getPoint();
            }
        });
        bg.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (e.getSource() == bg) {
                    Point loc = getLocation();
                    setLocation(
                        loc.x + e.getX() - dragOffset.x,
                        loc.y + e.getY() - dragOffset.y
                    );
                }
            }
        });

        JButton closeBtn = makeInvisibleButton();
        closeBtn.setBounds(sc(375,W), sc(90,H), sc(30,W), sc(25,H));
        closeBtn.addActionListener(e -> dispose());
        bg.add(closeBtn);

        JButton musicBtn = makeInvisibleButton();
        musicBtn.setBounds(sc(120,W), sc(160,H), sc(60,W), sc(52,H));
        musicBtn.addActionListener(e -> {
            musicOn[0] = !musicOn[0];
            bg.repaint();
        });
        bg.add(musicBtn);

        JButton soundBtn = makeInvisibleButton();
        soundBtn.setBounds(sc(120,W), sc(245,H), sc(60,W), sc(52,H));
        soundBtn.addActionListener(e -> {
            soundOn[0] = !soundOn[0];
            bg.repaint();
        });
        bg.add(soundBtn);

        JButton fullscreenBtn = makeInvisibleButton();
        fullscreenBtn.setBounds(sc(110,W), sc(295,H), sc(280,W), sc(45,H));
        fullscreenBtn.addActionListener(e -> {
            fullscreenOn[0] = !fullscreenOn[0];
            bg.repaint();
        });
        bg.add(fullscreenBtn);

        JButton cancelBtn = makeInvisibleButton();
        cancelBtn.setBounds(sc(118,W), sc(335,H), sc(98,W), sc(45,H));
        cancelBtn.addActionListener(e -> dispose());
        bg.add(cancelBtn);

        JButton saveBtn = makeInvisibleButton();
        saveBtn.setBounds(sc(253,W), sc(335,H), sc(102,W), sc(45,H));
        saveBtn.addActionListener(e ->
            saveAndClose(settings, musicOn, soundOn, fullscreenOn)
        );
        bg.add(saveBtn);

        setContentPane(bg);
    }

    private int sc(int base, int actual) {
        return base * actual / 500;
    }

    private void saveAndClose(GameSettings s, boolean[] musicOn, boolean[] soundOn, boolean[] fullscreenOn) {
        s.setMusicEnabled(musicOn[0]);
        s.setSoundEffectsEnabled(soundOn[0]);
        s.setFullscreen(fullscreenOn[0]);

        if (s.isFullscreen()) {
            ownerFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            ownerFrame.setExtendedState(JFrame.NORMAL);
            ownerFrame.setSize(1280, 720);
            ownerFrame.setLocationRelativeTo(null);
        }

        System.out.println("Settings saved: " + s);
        dispose();
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (Exception e) {
            System.err.println("Could not load: " + path);
            return null;
        }
    }

    private JButton makeInvisibleButton() {
        JButton btn = new JButton();
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static class ImagePanel extends JPanel {

        private final BufferedImage image;
        private final int W, H;
        private final boolean[] musicOn;
        private final boolean[] soundOn;
        private final boolean[] fullscreenOn;

        private static final Color OFF_COLOR  = new Color(180, 30, 30, 180);

        private static final Color ON_COLOR   = new Color(50, 200, 50, 80);

        private static final Color TICK_COLOR = new Color(80, 220, 80, 220);

        public ImagePanel(BufferedImage image, int W, int H, boolean[] musicOn, boolean[] soundOn, boolean[] fullscreenOn) {
            this.image        = image;
            this.W            = W;
            this.H            = H;
            this.musicOn      = musicOn;
            this.soundOn      = soundOn;
            this.fullscreenOn = fullscreenOn;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            drawToggleOverlay(g2, sc(138), sc(158), sc(44), sc(42), musicOn[0]);
            drawToggleOverlay(g2, sc(138), sc(236), sc(44), sc(42), soundOn[0]);
            drawCheckbox(g2, sc(338), sc(295), sc(18), sc(18), fullscreenOn[0]);
        }

        private void drawToggleOverlay(Graphics2D g2, int x, int y, int w, int h, boolean on) {
            if (!on) {
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRect(x, y, w, h);
                g2.setColor(OFF_COLOR);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(x + 6, y + 6, x + w - 6, y + h - 6);
                g2.drawLine(x + w - 6, y + 6, x + 6, y + h - 6);
            }
        }

        private void drawCheckbox(Graphics2D g2, int x, int y, int w, int h, boolean checked) {
            if (checked) {
                g2.setColor(TICK_COLOR);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(x + 3,     y + h/2,   x + w/2,   y + h - 4);
                g2.drawLine(x + w/2,   y + h - 4, x + w - 3, y + 4);
            } else {

                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRect(x + 2, y + 2, w - 4, h - 4);
            }
        }

        private int sc(int base) {
            return base * getWidth() / 500;
        }
    }
}