package main;

import gui.BoardPanel;
import java.awt.*;
import javax.swing.*;

public class main {
    public static void main(String[] args) {
        // Create the main window
        JFrame frame = new JFrame("⚓ Battleship Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Create title label
        JLabel titleLabel = new JLabel("BATTLESHIP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Create the game board
        BoardPanel boardPanel = new BoardPanel();
        
        // Add everything to the frame
        frame.add(titleLabel, BorderLayout.NORTH);
        frame.add(boardPanel, BorderLayout.CENTER);
        
        // Pack and display
        frame.pack();
        frame.setLocationRelativeTo(null);  // Center on screen
        frame.setVisible(true);
        
        System.out.println("⚓ Battleship Game Started!");
        System.out.println("Click any cell to fire!");
    }
}