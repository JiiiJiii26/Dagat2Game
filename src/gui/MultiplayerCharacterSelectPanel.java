package gui;

import characters.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

public class MultiplayerCharacterSelectPanel extends JPanel {

    private ArrayList<GameCharacter> characters;
    private GameCharacter player1Character;
    private GameCharacter player2Character;
    private CharacterSelectListener listener;

    // ── UI components kept as fields so relayout() can reposition them ──
    private JButton confirmButton;
    private JButton backButton;
    private JLabel player1SelectedLabel;
    private JLabel player2SelectedLabel;
    private JPanel p1Banner;
    private JPanel p2Banner;

    // Parallel lists: p1Cards[i] / p2Cards[i] match characters.get(i)
    private final ArrayList<JPanel> p1Cards = new ArrayList<>();
    private final ArrayList<JPanel> p2Cards = new ArrayList<>();

    private ImageIcon gifIcon;
    private ImageIcon boxIcon;

    private ImageIcon p1PortraitIcon = null;
    private ImageIcon p2PortraitIcon = null;

    // Insets are expressed as fractions of card size; computed each layout pass
    private int dynInsetTop, dynInsetBottom, dynInsetLeft, dynInsetRight;

    private static final float BLUR_STRENGTH = 1f / 25f;
    private static final float[] BLUR_KERNEL = new float[25];
    static {
        for (int i = 0; i < 25; i++) BLUR_KERNEL[i] = BLUR_STRENGTH;
    }
    private final ConvolveOp blurOp = new ConvolveOp(
        new Kernel(5, 5, BLUR_KERNEL),
        ConvolveOp.EDGE_NO_OP,
        null
    );
    private BufferedImage blurredFrame;

    // ── Current dynamic layout values (reused in paintComponent) ──
    private int currentPortraitW = 400;

    public interface CharacterSelectListener {
        void onCharactersSelected(GameCharacter player1, GameCharacter player2);
        void onBackToMenu();
    }

    // ─────────────────────────────────────────────────────────────────────────
    public MultiplayerCharacterSelectPanel(CharacterSelectListener listener) {
        this.listener = listener;
        this.characters = new ArrayList<>();

        characters.add(new Jiji());
        characters.add(new Kael());
        characters.add(new Valerius());
        characters.add(new Skye());
        characters.add(new Morgana());
        characters.add(new Aeris());
        characters.add(new Selene());
        characters.add(new Flue());

        String base = System.getProperty("user.dir") + File.separator + "assets" + File.separator;
        gifIcon = new ImageIcon(base + "pvp.gif");
        gifIcon.setImageObserver(this);
        boxIcon = new ImageIcon(base + "pvpBox.png");

        initializeUI();

        // ── Reflow everything whenever the panel is resized ──
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                relayout();
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Build the component tree once (no sizes yet – relayout() sets them)
    // ─────────────────────────────────────────────────────────────────────────
    private void initializeUI() {
        setLayout(null);

        // Back button
        backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.addActionListener(e -> { if (listener != null) listener.onBackToMenu(); });
        add(backButton);

        // Player banners
        p1Banner = createPlayerBanner("PLAYER 1", new Color(0, 180, 220, 180));
        add(p1Banner);

        p2Banner = createPlayerBanner("PLAYER 2", new Color(220, 100, 0, 180));
        add(p2Banner);

        // Selected-character labels
        player1SelectedLabel = createSelectedLabel();
        add(player1SelectedLabel);

        player2SelectedLabel = createSelectedLabel();
        add(player2SelectedLabel);

        // Confirm button
        confirmButton = new JButton("START BATTLE");
        confirmButton.setEnabled(false);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.addActionListener(e -> {
            if (player1Character != null && player2Character != null)
                listener.onCharactersSelected(player1Character, player2Character);
        });
        add(confirmButton);

        // Character cards (created once; bounds set in relayout)
        for (GameCharacter ch : characters) {
            JPanel c1 = createCharacterCard(ch, 1);
            JPanel c2 = createCharacterCard(ch, 2);
            p1Cards.add(c1);
            p2Cards.add(c2);
            add(c1);
            add(c2);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Recalculate every component's bounds from current panel dimensions
    // ─────────────────────────────────────────────────────────────────────────
    private void relayout() {
        int W = getWidth();
        int H = getHeight();
        if (W <= 0 || H <= 0) return;

        // ── Grid math ──────────────────────────────────────────────────────
        // Side margin: leave room for portraits on both edges
        int sideMargin = (int)(W * 0.14);   // ~14 % each side
        int availW     = W - sideMargin * 2;

        int NUM_CARDS = characters.size();   // 8
        int GAP_X     = Math.max(3, (int)(availW * 0.005));
        int BOX_W     = (availW - (NUM_CARDS - 1) * GAP_X) / NUM_CARDS;
        int BOX_H     = (int)(BOX_W * 1.11);  // keep ~200/180 aspect ratio

        // Insets scale with card size
        dynInsetTop    = (int)(BOX_H * 0.15);
        dynInsetBottom = (int)(BOX_H * 0.20);
        dynInsetLeft   = (int)(BOX_W * 0.10);
        dynInsetRight  = (int)(BOX_W * 0.10);

        // Row positions: p1 at ~55 % height, p2 below with a gap
        int rowGap   = (int)(H * 0.035);
        int bannerH  = Math.max(24, (int)(H * 0.038));
        int p1RowY   = (int)(H * 0.55);
        int p2RowY   = p1RowY + BOX_H + rowGap;

        // Portrait width for paintComponent
        currentPortraitW = sideMargin + (int)(sideMargin * 0.5);

        // Font sizes scale with height
        int bannerFontSize = Math.max(10, (int)(H * 0.018));
        int labelFontSize  = Math.max(9,  (int)(H * 0.015));
        int btnFontSize    = Math.max(10, (int)(H * 0.018));

        // ── Back button ────────────────────────────────────────────────────
        backButton.setBounds(10, 10, (int)(W * 0.07), (int)(H * 0.038));

        // ── P1 banner + label ──────────────────────────────────────────────
        int bannerW = (int)(availW * 0.28);
        int labelW  = availW - bannerW - (int)(availW * 0.02);

        p1Banner.setBounds(sideMargin, p1RowY - bannerH - 4, bannerW, bannerH);
        ((JLabel) p1Banner.getComponent(0)).setFont(new Font("Arial", Font.BOLD, bannerFontSize));

        player1SelectedLabel.setBounds(sideMargin + bannerW + (int)(availW * 0.02),
                                        p1RowY - bannerH - 4, labelW, bannerH);
        player1SelectedLabel.setFont(new Font("Arial", Font.ITALIC, labelFontSize));

        // ── P2 banner + label ──────────────────────────────────────────────
        p2Banner.setBounds(sideMargin, p2RowY - bannerH - 4, bannerW, bannerH);
        ((JLabel) p2Banner.getComponent(0)).setFont(new Font("Arial", Font.BOLD, bannerFontSize));

        player2SelectedLabel.setBounds(sideMargin + bannerW + (int)(availW * 0.02),
                                        p2RowY - bannerH - 4, labelW, bannerH);
        player2SelectedLabel.setFont(new Font("Arial", Font.ITALIC, labelFontSize));

        // ── Cards ──────────────────────────────────────────────────────────
        for (int i = 0; i < characters.size(); i++) {
            int x = sideMargin + i * (BOX_W + GAP_X);
            p1Cards.get(i).setBounds(x, p1RowY, BOX_W, BOX_H);
            p2Cards.get(i).setBounds(x, p2RowY, BOX_W, BOX_H);
        }

        // ── Confirm button — centred below p2 row ──────────────────────────
        int btnW = (int)(availW * 0.16);
        int btnH = (int)(H * 0.050);
        int btnX = sideMargin + availW / 2 - btnW / 2;
        int btnY = Math.min(p2RowY + BOX_H + (int)(H * 0.018), H - btnH - 5);
        confirmButton.setBounds(btnX, btnY, btnW, btnH);
        confirmButton.setFont(new Font("Arial", Font.BOLD, btnFontSize));

        revalidate();
        repaint();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Background + portraits
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gifIcon == null) return;

        int w = getWidth();
        int h = getHeight();

        BufferedImage frame = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D fg = frame.createGraphics();
        fg.drawImage(gifIcon.getImage(), 0, 0, w, h, this);
        fg.dispose();

        blurredFrame = blurOp.filter(frame, null);
        g.drawImage(blurredFrame, 0, 0, this);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRect(0, 0, w, h);

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Portraits scale with panel size
        int portraitH = h;
        int portraitW = currentPortraitW;   // set by relayout()

        if (p1PortraitIcon != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2.drawImage(p1PortraitIcon.getImage(), 0, 0, portraitW, portraitH, this);
        }
        if (p2PortraitIcon != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2.drawImage(p2PortraitIcon.getImage(), w - portraitW, 0, portraitW, portraitH, this);
        }

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Component factories
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel createPlayerBanner(String text, Color color) {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        banner.setOpaque(false);

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        banner.add(label, BorderLayout.CENTER);
        return banner;
    }

    private JLabel createSelectedLabel() {
        JLabel label = new JLabel("— choose a character", SwingConstants.LEFT) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setOpaque(false);
        label.setForeground(new Color(255, 220, 50));
        label.setFont(new Font("Arial", Font.ITALIC, 13));
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        return label;
    }

    private JPanel createCharacterCard(GameCharacter character, int playerNumber) {
        ImageIcon charIcon = loadCharacterImage(character);

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int w = getWidth();
                int h = getHeight();

                if (boxIcon != null && boxIcon.getImage() != null)
                    g2.drawImage(boxIcon.getImage(), 0, 0, w, h, this);

                if (charIcon != null && charIcon.getImage() != null) {
                    // Use dynamic insets updated by relayout()
                    int imgX = dynInsetLeft;
                    int imgY = dynInsetTop;
                    int imgW = w - dynInsetLeft - dynInsetRight;
                    int imgH = h - dynInsetTop  - dynInsetBottom;

                    Shape oldClip = g2.getClip();
                    g2.setClip(imgX, imgY, imgW, imgH);
                    g2.drawImage(charIcon.getImage(), imgX, imgY, imgW, imgH, this);
                    g2.setClip(oldClip);
                }

                // Draw box frame on top again (keeps the border overlay)
                if (boxIcon != null && boxIcon.getImage() != null)
                    g2.drawImage(boxIcon.getImage(), 0, 0, w, h, this);

                g2.dispose();
            }
        };
        card.setOpaque(false);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectCharacter(character, playerNumber);
                // Clear highlights from same-row cards only
                ArrayList<JPanel> sameRow = (playerNumber == 1) ? p1Cards : p2Cards;
                for (JPanel c : sameRow) c.setBorder(null);
                card.setBorder(BorderFactory.createLineBorder(
                    playerNumber == 1 ? new Color(0, 200, 255) : new Color(255, 120, 0), 3));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isCharacterSelected(character, playerNumber))
                    card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isCharacterSelected(character, playerNumber))
                    card.setBorder(null);
            }
        });

        return card;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Asset loaders
    // ─────────────────────────────────────────────────────────────────────────
    private ImageIcon loadPortrait(GameCharacter character) {
        String base = System.getProperty("user.dir") + File.separator + "assets" + File.separator;
        String filename = null;

        if      (character instanceof Flue)     filename = "char1.png";
        else if (character instanceof Jiji)     filename = "char2 1.png";
        else if (character instanceof Skye)     filename = "char3.png";
        else if (character instanceof Kael)     filename = "char4.png";
        else if (character instanceof Aeris)    filename = "char5.png";
        else if (character instanceof Selene)   filename = "char6.png";
        else if (character instanceof Morgana)  filename = "char7.png";
        else if (character instanceof Valerius) filename = "char8.png";

        if (filename == null) return null;
        File f = new File(base + filename);
        return f.exists() ? new ImageIcon(f.getAbsolutePath()) : null;
    }

    private ImageIcon loadCharacterImage(GameCharacter character) {
        String base = System.getProperty("user.dir") + File.separator + "assets" + File.separator;
        String filename = null;

        if      (character instanceof Jiji)     filename = "jiji.jpg";
        else if (character instanceof Kael)     filename = "kael.jpg";
        else if (character instanceof Valerius) filename = "valerius.jpg";
        else if (character instanceof Skye)     filename = "skye.png";
        else if (character instanceof Morgana)  filename = "morgana.jpg";
        else if (character instanceof Aeris)    filename = "aeris.jpg";
        else if (character instanceof Selene)   filename = "selene.jpg";
        else if (character instanceof Flue)     filename = "flue.jpg";

        if (filename == null) return null;
        File f = new File(base + filename);
        return f.exists() ? new ImageIcon(f.getAbsolutePath()) : null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Selection helpers
    // ─────────────────────────────────────────────────────────────────────────
    private void selectCharacter(GameCharacter character, int playerNumber) {
        if (playerNumber == 1) {
            player1Character = character;
            p1PortraitIcon   = loadPortrait(character);
            if (player1SelectedLabel != null)
                player1SelectedLabel.setText("  " + character.getName());
        } else {
            player2Character = character;
            p2PortraitIcon   = loadPortrait(character);
            if (player2SelectedLabel != null)
                player2SelectedLabel.setText("  " + character.getName());
        }
        confirmButton.setEnabled(player1Character != null && player2Character != null);
        repaint();
    }

    private boolean isCharacterSelected(GameCharacter character, int playerNumber) {
        return playerNumber == 1 ? player1Character == character
                                 : player2Character == character;
    }
}