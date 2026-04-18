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

    private JButton confirmButton;
    private JLabel player1SelectedLabel;
    private JLabel player2SelectedLabel;
    private ImageIcon gifIcon;
    private ImageIcon boxIcon;

    private ImageIcon p1PortraitIcon = null;
    private ImageIcon p2PortraitIcon = null;

    private static final int INSET_TOP    = 30;
    private static final int INSET_BOTTOM = 40;
    private static final int INSET_LEFT   = 18;
    private static final int INSET_RIGHT  = 18;

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

    public interface CharacterSelectListener {
        void onCharactersSelected(GameCharacter player1, GameCharacter player2);
        void onBackToMenu();
    }

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
    }

    private ImageIcon loadPortrait(GameCharacter character) {
        String base = System.getProperty("user.dir") + File.separator + "assets" + File.separator;
        String filename = null;

        if (character instanceof Flue)          filename = "char1.png";
        else if (character instanceof Jiji)     filename = "char2 1.png";
        else if (character instanceof Skye)     filename = "char3.png";
        else if (character instanceof Kael)     filename = "char4.png";
        else if (character instanceof Aeris)    filename = "char5.png";
        else if (character instanceof Selene)   filename = "char6.png";
        else if (character instanceof Morgana)  filename = "char7.png";
        else if (character instanceof Valerius) filename = "char8.png";

        if (filename == null) return null;
        File f = new File(base + filename);
        if (!f.exists()) return null;
        return new ImageIcon(f.getAbsolutePath());
    }

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

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int portraitH = h;
        int portraitW = 800;

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

    private void initializeUI() {
        setLayout(null);

        // Back button — top left, small and unobtrusive
        JButton backButton = new JButton("← Back");
        backButton.setBounds(10, 10, 90, 28);
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.addActionListener(e -> {
            if (listener != null) listener.onBackToMenu();
        });
        add(backButton);

        int BOX_W  = 180;
        int BOX_H  = 200;
        int GAP_X  = 6;
        int startX = 200;
        int p1RowY = 500;
        int p2RowY = p1RowY + BOX_H + 30;

        // P1 header banner — sits directly above P1 row
        JPanel p1Banner = createPlayerBanner("PLAYER 1", new Color(0, 180, 220, 180));
        p1Banner.setBounds(startX, p1RowY - 38, 300, 32);
        add(p1Banner);

        // P1 selected name — right of the P1 banner
        player1SelectedLabel = createSelectedLabel();
        player1SelectedLabel.setBounds(startX + 310, p1RowY - 38, 400, 32);
        add(player1SelectedLabel);

        // P2 header banner — sits directly above P2 row
        JPanel p2Banner = createPlayerBanner("PLAYER 2", new Color(220, 100, 0, 180));
        p2Banner.setBounds(startX, p2RowY - 38, 300, 32);
        add(p2Banner);

        // P2 selected name — right of the P2 banner
        player2SelectedLabel = createSelectedLabel();
        player2SelectedLabel.setBounds(startX + 310, p2RowY - 38, 400, 32);
        add(player2SelectedLabel);

        // Confirm button — bottom center, styled
        int totalW = 8 * BOX_W + 7 * GAP_X;
        confirmButton = new JButton("START BATTLE");
        confirmButton.setEnabled(false);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.setBounds(startX + totalW / 2 - 90, p2RowY + BOX_H + 14, 180, 38);
        confirmButton.addActionListener(e -> {
            if (player1Character != null && player2Character != null) {
                listener.onCharactersSelected(player1Character, player2Character);
            }
        });
        add(confirmButton);

        // Cards
        for (int i = 0; i < characters.size(); i++) {
            int x = startX + i * (BOX_W + GAP_X);

            JPanel card1 = createCharacterCard(characters.get(i), 1);
            card1.setBounds(x, p1RowY, BOX_W, BOX_H);
            add(card1);

            JPanel card2 = createCharacterCard(characters.get(i), 2);
            card2.setBounds(x, p2RowY, BOX_W, BOX_H);
            add(card2);
        }
    }

    // Semi-transparent colored banner with player label
    private JPanel createPlayerBanner(String text, Color color) {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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

    // Semi-transparent dark label for selected character name
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

    private ImageIcon loadCharacterImage(GameCharacter character) {
        String base = System.getProperty("user.dir") + File.separator + "assets" + File.separator;
        String filename = null;

        if (character instanceof Jiji)          filename = "jiji.jpg";
        else if (character instanceof Kael)     filename = "kael.jpg";
        else if (character instanceof Valerius) filename = "valerius.jpg";
        else if (character instanceof Skye)     filename = "skye.png";
        else if (character instanceof Morgana)  filename = "morgana.jpg";
        else if (character instanceof Aeris)    filename = "aeris.jpg";
        else if (character instanceof Selene)   filename = "selene.jpg";
        else if (character instanceof Flue)     filename = "flue.jpg";

        if (filename == null) return null;
        File f = new File(base + filename);
        if (!f.exists()) return null;
        return new ImageIcon(f.getAbsolutePath());
    }

    private JPanel createCharacterCard(GameCharacter character, int playerNumber) {
        ImageIcon charIcon = loadCharacterImage(character);

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int w = getWidth();
                int h = getHeight();

                if (boxIcon != null && boxIcon.getImage() != null) {
                    g2.drawImage(boxIcon.getImage(), 0, 0, w, h, this);
                }

                if (charIcon != null && charIcon.getImage() != null) {
                    int imgX = INSET_LEFT;
                    int imgY = INSET_TOP;
                    int imgW = w - INSET_LEFT - INSET_RIGHT;
                    int imgH = h - INSET_TOP - INSET_BOTTOM;

                    Shape oldClip = g2.getClip();
                    g2.setClip(imgX, imgY, imgW, imgH);
                    g2.drawImage(charIcon.getImage(), imgX, imgY, imgW, imgH, this);
                    g2.setClip(oldClip);
                }

                if (boxIcon != null && boxIcon.getImage() != null) {
                    g2.drawImage(boxIcon.getImage(), 0, 0, w, h, this);
                }

                g2.dispose();
            }
        };
        card.setOpaque(false);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectCharacter(character, playerNumber);
                for (Component comp : card.getParent().getComponents()) {
                    if (comp instanceof JPanel) {
                        ((JPanel) comp).setBorder(null);
                    }
                }
                card.setBorder(BorderFactory.createLineBorder(
                    playerNumber == 1 ? new Color(0, 200, 255) : new Color(255, 120, 0), 3));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isCharacterSelected(character, playerNumber)) {
                    card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isCharacterSelected(character, playerNumber)) {
                    card.setBorder(null);
                }
            }
        });

        return card;
    }

    private void selectCharacter(GameCharacter character, int playerNumber) {
        if (playerNumber == 1) {
            player1Character = character;
            p1PortraitIcon = loadPortrait(character);
            if (player1SelectedLabel != null)
                player1SelectedLabel.setText("  " + character.getName());
        } else {
            player2Character = character;
            p2PortraitIcon = loadPortrait(character);
            if (player2SelectedLabel != null)
                player2SelectedLabel.setText("  " + character.getName());
        }
        confirmButton.setEnabled(player1Character != null && player2Character != null);
        repaint();
    }

    private boolean isCharacterSelected(GameCharacter character, int playerNumber) {
        return playerNumber == 1
            ? player1Character == character
            : player2Character == character;
    }

    private String getCharacterEmoji(GameCharacter character) {
        if (character instanceof Jiji)     return "💻";
        if (character instanceof Kael)     return "🌑";
        if (character instanceof Valerius) return "🛡️";
        if (character instanceof Skye)     return "🐱";
        if (character instanceof Morgana)  return "🧜‍♀️";
        if (character instanceof Aeris)    return "💪";
        if (character instanceof Selene)   return "🔮";
        if (character instanceof Flue)     return "💻";
        return "🎮";
    }
}