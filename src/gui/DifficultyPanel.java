package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DifficultyPanel extends JPanel {
    private JButton easyButton, mediumButton, hardButton;
    private String selectedDifficulty;
    private DifficultyListener listener;
    Frame frame = new JFrame("Tidebound - Choose Your Challenge");
    public interface DifficultyListener {
        void onDifficultySelected(String difficulty);
    }
    
    public DifficultyPanel(DifficultyListener listener) {
        this.listener = listener;
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 300));
        
        
        JLabel titleLabel = new JLabel("SELECT DIFFICULTY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        easyButton = createDifficultyButton(
            "EASY", 
            "ðŸŒŠ Random shots - Perfect for beginners",
            new Color(144, 238, 144)
        );
        
        mediumButton = createDifficultyButton(
            "MEDIUM", 
            "ðŸŽ¯ Smart targeting - Avoids repeats",
            new Color(255, 215, 0)
        );
        
        hardButton = createDifficultyButton(
            "HARD", 
            "ðŸ’€ Hunting mode - Chases hits",
            new Color(255, 99, 71)
        );
        
        buttonPanel.add(easyButton);
        buttonPanel.add(mediumButton);
        buttonPanel.add(hardButton);
        
        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }
    
    private JButton createDifficultyButton(String text, String tooltip, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setToolTipText(tooltip);
        button.setBackground(color);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedDifficulty = text;
                listener.onDifficultySelected(text);
            }
        });
        
        return button;
    }
}





