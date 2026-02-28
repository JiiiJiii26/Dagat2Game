package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoardPanel extends JPanel {
    private JButton[][] gridButtons;  // 10x10 grid of buttons
    private final int SIZE = 10;
    
    public BoardPanel() {
        // Set up the panel with a grid layout
        setLayout(new GridLayout(SIZE, SIZE));
        setPreferredSize(new Dimension(500, 500));  // 500x500 pixels
        
        // Create the 2D array of buttons
        gridButtons = new JButton[SIZE][SIZE];
        
        // Create and add buttons to the grid
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                // Create a new button
                JButton button = new JButton();
                
                // Store row and col in button properties (for click handling)
                button.putClientProperty("row", row);
                button.putClientProperty("col", col);
                
                // Make it look nice
                button.setBackground(new Color(173, 216, 230));  // Light blue
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                
                // Add click handler
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton clickedButton = (JButton) e.getSource();
                        int r = (int) clickedButton.getClientProperty("row");
                        int c = (int) clickedButton.getClientProperty("col");
                        handleButtonClick(r, c);
                    }
                });
                
                // Add to grid
                gridButtons[row][col] = button;
                add(button);
            }
        }
    }
    
    // This method runs when a button is clicked
    private void handleButtonClick(int row, int col) {
        JButton button = gridButtons[row][col];
        
        // If not already clicked
        if (button.getBackground() != Color.GRAY && button.getBackground() != Color.RED) {
            // For now, just change color to show it was clicked
            button.setBackground(Color.GRAY);  // Miss for now
            button.setText("•");  // Add a dot to show it's been hit
            System.out.println("Clicked: (" + row + ", " + col + ")");
        }
    }
    
    // Helper method to change cell color (we'll use this later)
    public void setCellColor(int row, int col, Color color) {
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
            gridButtons[row][col].setBackground(color);
        }
    }
}