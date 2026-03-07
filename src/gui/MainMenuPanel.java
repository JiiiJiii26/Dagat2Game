package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class MainMenuPanel extends JPanel {

    private JButton startButton;
    private JButton vsButton;
    private JButton optionsButton;
    private JButton exitButton;
    private final MenuListener listener;

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

        JPanel titlePanel = createTitlePanel();
        JPanel buttonPanel = createButtonPanel();
        JPanel footerPanel = createFooterPanel();

        add(titlePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));

        JLabel titleLabel = new JLabel(new ImageIcon(
                new ImageIcon("C:/Users/Nicco/Desktop/JavaProject/Dagat2Game/assets/title.png")
                        .getImage().getScaledInstance(600, 120, Image.SCALE_SMOOTH)));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(8, 100, 8, 100);

        startButton = createPNG3DButton("C:/Users/Nicco/Desktop/JavaProject/Dagat2Game/assets/btn_start.png");
        vsButton = createPNG3DButton("C:/Users/Nicco/Desktop/JavaProject/Dagat2Game/assets/btn_multi.png");
        optionsButton = createPNG3DButton("C:/Users/Nicco/Desktop/JavaProject/Dagat2Game/assets/btn_options.png");
        exitButton = createPNG3DButton("C:/Users/Nicco/Desktop/JavaProject/Dagat2Game/assets/btn_exit.png");

        startButton.addActionListener(e -> listener.onStartGame());
        vsButton.addActionListener(e -> listener.on1v1Mode());
        optionsButton.addActionListener(e -> listener.onOptions());
        exitButton.addActionListener(e -> listener.onExit());

        panel.add(startButton, gbc);
        panel.add(vsButton, gbc);
        panel.add(optionsButton, gbc);
        panel.add(exitButton, gbc);

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

    private JButton createPNG3DButton(String imagePath) {
        // Load and scale the PNG
        ImageIcon icon = new ImageIcon(imagePath);
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(icon.getImage(), 0);
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Scale and create hover version
        Image scaled = icon.getImage().getScaledInstance(380, 65, Image.SCALE_SMOOTH);

        // Wait for scaled image too
        ImageIcon scaledIcon = new ImageIcon(scaled);
        MediaTracker tracker2 = new MediaTracker(this);
        tracker2.addImage(scaledIcon.getImage(), 0);
        try {
            tracker2.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create brighter hover version
        Image hoverImg = makeBrighter(scaled);

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

                int w = getWidth();
                int h = getHeight();
                int shadowSize = 6;
                int offsetY = pressed ? 5 : 0; // press down shift

                // Drop shadow (disappears when pressed)
                if (!pressed) {
                    g2.setColor(new Color(0, 0, 0, 150));
                    g2.fillRoundRect(shadowSize, shadowSize, w - shadowSize, h - shadowSize, 12, 12);
                }

                // Draw PNG image (hover = brighter)
                Image imgToDraw = hovered ? hoverImg : scaled;
                g2.drawImage(imgToDraw, 0, offsetY, w - shadowSize, h - shadowSize, null);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(395, 72);
            }
        };

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // Makes image brighter for hover effect
    private Image makeBrighter(Image img) {
        BufferedImage buffered = new BufferedImage(
                img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffered.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, buffered.getWidth(), buffered.getHeight());
        g2d.dispose();
        return buffered;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageIcon bg = new ImageIcon("C:/Users/Nicco/Desktop/JavaProject/Dagat2Game/assets/battleship.gif");
        g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}