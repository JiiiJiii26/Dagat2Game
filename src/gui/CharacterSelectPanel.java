package gui;

import characters.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CharacterSelectPanel extends JPanel {

    private static final Color BG_BLACK     = new Color(8,   8,  12);
    private static final Color CRIMSON_BG   = new Color(100, 12, 18);
    private static final Color ACCENT_RED   = new Color(200, 40, 40);
    private static final Color GOLD         = new Color(220, 170, 60);
    private static final Color TEXT_WHITE   = new Color(240, 240, 245);
    private static final Color TEXT_DIM     = new Color(155, 155, 175);
    private static final Color PANEL_DARK   = new Color(14,  14,  20);
    private static final Color DEPLOY_GREEN = new Color(45,  160, 80);
    private static final Color BORDER_RED   = new Color(80,  20,  20);

    private final ArrayList<CharacterData> roster = new ArrayList<>();
    private int selectedIndex = 0;
    private final CharacterSelectListener listener;

    private ImageCache    imageCache;
    private FeatureCard   featureCard;
    private CarouselPanel carousel;

    public interface CharacterSelectListener {
        void onCharacterSelected(GameCharacter character);
        void onBackToMenu();
    }

    public CharacterSelectPanel(CharacterSelectListener listener) {
        this.listener = listener;
        buildRoster();
        imageCache = new ImageCache(roster);
        imageCache.loadAll();
        buildUI();
        select(0);
    }

    private void buildRoster() {
        roster.add(new CharacterData(new Jiji(), "jiji",
            "A lazy gamer who procrastinates even in battle. One day his online games granted him the " +
            "powers of a TechnoMancer. He still naps between attacks — the ocean waits for no one, " +
            "but Jiji makes it wait anyway."));

        roster.add(new CharacterData(new Kael(), "kael",
            "Raised on storm-ridden coasts where pirates clashed with navies, Kael mastered stealth " +
            "over strength. Known as the Tide Hunter, he hides entire fleets and strikes before enemies " +
            "even know they've been located."));

        roster.add(new CharacterData(new Valerius(), "valerius",
            "A disgraced military engineer whose city was leveled by naval bombardment. Valerius built " +
            "himself an exoskeleton from sunken warship scrap. Now a one-man fortress, he offers his " +
            "services to the highest bidder — and refuses to sink."));

        roster.add(new CharacterData(new Skye(), "skye",
            "Skye runs the largest cat rescue shelter in the Land of Dawn — over 200 cats. When the " +
            "ocean rose to flood her shelter, her cats didn't flee. They fought back. Now they follow " +
            "her into battle, proving cats really are plotting world domination."));

        roster.add(new CharacterData(new Morgana(), "morgana",
            "Once a Royal Fleet navigator, Morgana was betrayed and left to drown during a mutiny. The " +
            "ocean depths transformed her into a mystical siren who sings to the sea itself. She returns " +
            "not for revenge, but to protect the oceans she now calls home."));

        roster.add(new CharacterData(new Aeris(), "aeris",
            "Born into hardship, Aeris learned to adapt fast and manage pressure. Every setback " +
            "strengthened his resolve. Working long hours while attending class, he turned struggle " +
            "into power — an adaptive strategist forged by necessity, not privilege."));

        roster.add(new CharacterData(new Selene(), "selene",
            "Born during a lunar eclipse, Selene sees fragments of the future. The Moon Goddess granted " +
            "her power to read the stars and predict enemy movements. She serves as the navy's secret " +
            "weapon — though her cryptic warnings often confuse more than they help."));

        roster.add(new CharacterData(new Flue(), "flue",
            "A systems architect who coded the naval command network's foundational logic. Flue built " +
            "an untraceable avatar with unparalleled resilience. In combat he views opponents not as " +
            "threats, but as poorly optimized loops that need to be debugged."));
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_BLACK);
        add(new TopBar(),    BorderLayout.NORTH);
        add(buildCenter(),   BorderLayout.CENTER);
        add(new BottomBar(), BorderLayout.SOUTH);
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(14, 20, 10, 20));
        featureCard = new FeatureCard();
        carousel    = new CarouselPanel();
        center.add(featureCard, BorderLayout.CENTER);
        center.add(carousel,    BorderLayout.SOUTH);
        return center;
    }

    private void select(int index) {
        selectedIndex = index;
        featureCard.update(roster.get(index));
        carousel.refresh(index);
    }

    private static class CharacterData {
        final GameCharacter character;
        final String        imageKey;
        final String        backstory;

        CharacterData(GameCharacter ch, String key, String bio) {
            character = ch;
            imageKey  = key;
            backstory = bio;
        }

        String getName()    { return character.getName(); }
        int    getHP()      { return character.getMaxHealth(); }
        int    getSP()      { return character.getMaxSpecialMeter(); }
        String getAbility() { return character.getAbilityName(); }
    }

    private static class ImageCache {

        private static final String   PATH = "assets/";
        private static final String[] EXTS = {".jpg", ".jpeg", ".png", ".gif"};

        private final ArrayList<CharacterData>                 roster;
        private final java.util.HashMap<String, BufferedImage> colorMap = new java.util.HashMap<>();
        private final java.util.HashMap<String, BufferedImage> grayMap  = new java.util.HashMap<>();
        private final java.util.HashMap<String, BufferedImage> bgMap    = new java.util.HashMap<>();

        ImageCache(ArrayList<CharacterData> roster) { this.roster = roster; }

        void loadAll() {
            for (CharacterData cd : roster) {
                loadInto(cd.imageKey,          colorMap);
                loadInto(cd.imageKey + "_bg",  bgMap);
                if (colorMap.containsKey(cd.imageKey))
                    grayMap.put(cd.imageKey, toGray(colorMap.get(cd.imageKey)));
            }
            System.out.println("ImageCache: portraits=" + colorMap.size()
                + "  bgs=" + bgMap.size() + "/" + roster.size());
        }

        private void loadInto(String key, java.util.HashMap<String, BufferedImage> map) {
            for (String ext : EXTS) {
                File f = new File(PATH + key + ext);
                if (!f.exists()) continue;
                try {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) { map.put(key, img); return; }
                } catch (Exception ignored) {}
            }
        }

        BufferedImage getColor(String key) { return colorMap.get(key); }
        BufferedImage getGray(String key)  { return grayMap.get(key); }
        BufferedImage getBg(String key)    { return bgMap.get(key + "_bg"); }

        private BufferedImage toGray(BufferedImage src) {
            BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < src.getHeight(); y++) {
                for (int x = 0; x < src.getWidth(); x++) {
                    int argb = src.getRGB(x, y);
                    int a   = (argb >> 24) & 0xFF;
                    int r   = (argb >> 16) & 0xFF;
                    int g   = (argb >>  8) & 0xFF;
                    int b   =  argb        & 0xFF;
                    int lum = (int)(0.299*r + 0.587*g + 0.114*b);
                    out.setRGB(x, y, (a << 24) | (lum << 16) | (lum << 8) | lum);
                }
            }
            return out;
        }
    }

    private class FeatureCard extends JPanel {

        private BufferedImage currentBg;

        private final JLabel    nameLabel;
        private final JLabel    roleLabel;
        private final JLabel    hpLabel;
        private final JLabel    spLabel;
        private final JLabel    abilityLabel;
        private final JTextArea bioArea;

        FeatureCard() {
            setLayout(new BorderLayout());
            setOpaque(false);

            JPanel overlay = new JPanel();
            overlay.setLayout(new BoxLayout(overlay, BoxLayout.Y_AXIS));
            overlay.setOpaque(false);
            overlay.setBorder(new EmptyBorder(0, 28, 28, 28));

            nameLabel    = overlayLabel("", new Font("Segoe UI", Font.BOLD, 34), TEXT_WHITE);
            roleLabel    = overlayLabel("", new Font("Segoe UI", Font.ITALIC, 14), new Color(210, 110, 110));
            hpLabel      = overlayLabel("", new Font("Segoe UI", Font.PLAIN, 13), TEXT_DIM);
            spLabel      = overlayLabel("", new Font("Segoe UI", Font.PLAIN, 13), TEXT_DIM);
            abilityLabel = overlayLabel("", new Font("Segoe UI", Font.BOLD, 13), GOLD);

            bioArea = new JTextArea();
            bioArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            bioArea.setForeground(new Color(185, 185, 200));
            bioArea.setOpaque(false);
            bioArea.setEditable(false);
            bioArea.setFocusable(false);
            bioArea.setLineWrap(true);
            bioArea.setWrapStyleWord(true);
            bioArea.setAlignmentX(LEFT_ALIGNMENT);
            bioArea.setMaximumSize(new Dimension(460, 72));

            overlay.add(nameLabel);
            overlay.add(Box.createVerticalStrut(3));
            overlay.add(roleLabel);
            overlay.add(Box.createVerticalStrut(10));
            overlay.add(bioArea);
            overlay.add(Box.createVerticalStrut(12));
            overlay.add(hpLabel);
            overlay.add(Box.createVerticalStrut(3));
            overlay.add(spLabel);
            overlay.add(Box.createVerticalStrut(3));
            overlay.add(abilityLabel);

            add(overlay, BorderLayout.SOUTH);
        }

        private JLabel overlayLabel(String text, Font font, Color color) {
            JLabel l = new JLabel(text);
            l.setFont(font);
            l.setForeground(color);
            l.setAlignmentX(LEFT_ALIGNMENT);
            return l;
        }

        void update(CharacterData cd) {
            currentBg = imageCache.getBg(cd.imageKey);
            nameLabel.setText(cd.getName().toUpperCase());
            roleLabel.setText(cd.character.getDescription());
            hpLabel.setText("HP   " + cd.getHP());
            spLabel.setText("SP   " + cd.getSP());
            abilityLabel.setText("◆  " + cd.getAbility());
            bioArea.setText(cd.backstory);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            int w = getWidth(), h = getHeight();
            Shape roundRect = new RoundRectangle2D.Float(0, 0, w, h, 20, 20);

            g2.setColor(CRIMSON_BG);
            g2.fill(roundRect);

            if (currentBg != null) {
                g2.setClip(roundRect);
                double scale = Math.max((double)w / currentBg.getWidth(), (double)h / currentBg.getHeight());
                int dw = (int)(currentBg.getWidth()  * scale);
                int dh = (int)(currentBg.getHeight() * scale);
                g2.drawImage(currentBg, (w - dw) / 2, (h - dh) / 2 + 170, dw, dh, null);
                g2.setClip(null);
            }

            g2.setClip(roundRect);
            g2.setPaint(new GradientPaint(0, h * 0.28f, new Color(0,0,0,0), 0, h, new Color(6,2,2,238)));
            g2.fillRect(0, 0, w, h);
            g2.setClip(null);

            // Red accent border
            g2.setColor(new Color(180, 35, 35, 160));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, w - 2, h - 2, 20, 20);

            g2.dispose();
        }
    }

    private class CarouselPanel extends JPanel {

        private final ArrayList<CarouselThumb> thumbs = new ArrayList<>();

        CarouselPanel() {
            setLayout(new BorderLayout());
            setOpaque(false);
            setPreferredSize(new Dimension(0, 150));
            setBorder(new EmptyBorder(10, 0, 0, 0));

            JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
            row.setOpaque(false);

            for (int i = 0; i < roster.size(); i++) {
                CarouselThumb thumb = new CarouselThumb(i);
                thumbs.add(thumb);
                row.add(thumb);
            }

            JScrollPane scroll = new JScrollPane(row,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.setBorder(null);
            scroll.getHorizontalScrollBar().setUnitIncrement(30);
            scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 4));

            add(scroll, BorderLayout.CENTER);
        }

        void refresh(int selected) {
            for (CarouselThumb t : thumbs) t.setSelected(t.index == selected);
        }
    }

    private class CarouselThumb extends JPanel {

        final int index;
        private boolean selected = false;
        private boolean hovered  = false;
        private float   tint     = 0f;
        private Timer   anim;

        private final BufferedImage colorImg;
        private final BufferedImage grayImg;

        CarouselThumb(int index) {
            this.index = index;
            CharacterData cd = roster.get(index);
            colorImg = imageCache.getColor(cd.imageKey);
            grayImg  = imageCache.getGray(cd.imageKey);

            setOpaque(false);
            setPreferredSize(new Dimension(118, 138));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { select(index); }
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  animateTo(1f); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; if (!selected) animateTo(0f); }
            });
        }

        void setSelected(boolean sel) {
            selected = sel;
            animateTo(sel ? 1f : (hovered ? 1f : 0f));
        }

        private void animateTo(float target) {
            if (anim != null && anim.isRunning()) anim.stop();
            final float[] cur = { tint };
            anim = new Timer(16, null);
            anim.addActionListener(e -> {
                float diff = target - cur[0];
                if (Math.abs(diff) < 0.02f) { cur[0] = target; tint = target; anim.stop(); }
                else { cur[0] += diff * 0.20f; tint = cur[0]; }
                repaint();
            });
            anim.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            int w = getWidth(), h = getHeight();
            Shape roundClip = new RoundRectangle2D.Float(0, 0, w, h, 14, 14);
            g2.setClip(roundClip);

            g2.setColor(PANEL_DARK);
            g2.fillRect(0, 0, w, h);

            if (grayImg != null) drawFit(g2, grayImg, w, h);

            if (colorImg != null && tint > 0f) {
                Graphics2D gc = (Graphics2D) g2.create();
                gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tint));
                drawFit(gc, colorImg, w, h);
                gc.dispose();
            }

            g2.setPaint(new GradientPaint(0, h * 0.65f, new Color(0,0,0,0), 0, h, new Color(5,5,10,140)));
            g2.fillRect(0, 0, w, h);

            g2.setClip(null);

            int br = 20, bx = w - br - 6, by = h - br - 6;
            g2.setColor(selected ? ACCENT_RED : new Color(30, 30, 45, 180));
            g2.fillOval(bx, by, br, br);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(TEXT_WHITE);
            String arrow = selected ? "✓" : "↗";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(arrow, bx + (br - fm.stringWidth(arrow)) / 2, by + 14);

            // Border
            g2.setStroke(new BasicStroke(selected ? 2.5f : 1f));
            g2.setColor(selected ? ACCENT_RED : new Color(45, 45, 60));
            g2.drawRoundRect(1, 1, w - 2, h - 2, 14, 14);

            g2.dispose();
        }

        private void drawFit(Graphics2D g2, BufferedImage img, int w, int h) {
            double scale = Math.max((double)w / img.getWidth(), (double)h / img.getHeight());
            int dw = (int)(img.getWidth()  * scale);
            int dh = (int)(img.getHeight() * scale);
            g2.drawImage(img, (w - dw) / 2, 0, dw, dh, null);
        }
    }

    private class TopBar extends JPanel {

        TopBar() {
            setLayout(new BorderLayout());
            setBackground(new Color(5, 5, 8));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_RED),
                new EmptyBorder(10, 20, 10, 20)
            ));

            JButton back = new JButton("← BACK");
            back.setFont(new Font("Segoe UI", Font.BOLD, 13));
            back.setForeground(Color.WHITE);
            back.setBackground(new Color(120, 25, 25));
            back.setFocusPainted(false);
            back.setBorderPainted(false);
            back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            back.setPreferredSize(new Dimension(100, 36));
            back.addActionListener(e -> { if (listener != null) listener.onBackToMenu(); });

            JPanel titleBlock = new JPanel();
            titleBlock.setOpaque(false);
            titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

            JLabel game = new JLabel("TIDEBOUND", SwingConstants.CENTER);
            game.setFont(new Font("Segoe UI", Font.BOLD, 24));
            game.setForeground(TEXT_WHITE);
            game.setAlignmentX(CENTER_ALIGNMENT);

            JLabel sub = new JLabel("SELECT YOUR COMMANDER", SwingConstants.CENTER);
            sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            sub.setForeground(new Color(175, 70, 70));
            sub.setAlignmentX(CENTER_ALIGNMENT);

            titleBlock.add(game);
            titleBlock.add(sub);

            JPanel spacer = new JPanel();
            spacer.setOpaque(false);
            spacer.setPreferredSize(new Dimension(100, 36));

            add(back,       BorderLayout.WEST);
            add(titleBlock, BorderLayout.CENTER);
            add(spacer,     BorderLayout.EAST);
        }
    }

    private class BottomBar extends JPanel {

        BottomBar() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
            setBackground(new Color(5, 5, 8));
            setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_RED));

            JButton btn = new JButton("DEPLOY COMMANDER");
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setForeground(Color.WHITE);
            btn.setBackground(DEPLOY_GREEN);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(260, 44));

            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(55, 190, 95)); }
                @Override public void mouseExited(MouseEvent e)  { btn.setBackground(DEPLOY_GREEN); }
            });

            btn.addActionListener(e -> {
                if (listener != null)
                    listener.onCharacterSelected(roster.get(selectedIndex).character);
            });

            add(btn);
        }
    }
}