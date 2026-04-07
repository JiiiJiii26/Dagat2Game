package gui;

import java.awt.*;
import javax.swing.*;

public class TimerPanel extends JPanel {
    
    private JProgressBar timerBar;
    private JLabel timerLabel;
    private Timer gameTimer;
    private int timeLeft;
    private int maxTime;
    private TimerCallback callback;
    private boolean isRunning = false;
    
    public interface TimerCallback {
        void onTimeOut();
    }
    
    public TimerPanel(int maxTimeSeconds, TimerCallback callback) {
        this.maxTime = maxTimeSeconds;
        this.timeLeft = maxTimeSeconds;
        this.callback = callback;
        
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(200, 40));
        
        
        timerLabel = new JLabel("Time: " + timeLeft + "s", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerLabel.setForeground(Color.WHITE);
        
        
        timerBar = new JProgressBar(0, maxTime);
        timerBar.setValue(timeLeft);
        timerBar.setStringPainted(false);
        timerBar.setForeground(new Color(50, 200, 50));
        timerBar.setBackground(new Color(80, 80, 80));
        timerBar.setPreferredSize(new Dimension(180, 15));
        
        add(timerLabel, BorderLayout.NORTH);
        add(timerBar, BorderLayout.CENTER);
        
        setVisible(false);
    }
    
    public void startTimer() {
        stopTimer();
        timeLeft = maxTime;
        isRunning = true;
        setVisible(true);
        updateDisplay();
        
        gameTimer = new Timer(1000, e -> {
            if (!isRunning) return;
            
            timeLeft--;
            updateDisplay();
            
            
            if (timeLeft <= 3) {
                timerBar.setForeground(Color.RED);
                timerLabel.setForeground(Color.RED);
                
                if (timeLeft % 2 == 0) {
                    timerLabel.setForeground(Color.YELLOW);
                }
            } else if (timeLeft <= 6) {
                timerBar.setForeground(Color.ORANGE);
                timerLabel.setForeground(Color.ORANGE);
            } else {
                timerBar.setForeground(new Color(50, 200, 50));
                timerLabel.setForeground(Color.WHITE);
            }
            
            if (timeLeft <= 0) {
                stopTimer();
                if (callback != null) {
                    callback.onTimeOut();
                }
            }
        });
        gameTimer.start();
    }
    
    public void stopTimer() {
        isRunning = false;
        if (gameTimer != null) {
            gameTimer.stop();
        }
        setVisible(false);
    }
    
    public void resetTimer() {
        stopTimer();
        timeLeft = maxTime;
        updateDisplay();
        timerBar.setForeground(new Color(50, 200, 50));
        timerLabel.setForeground(Color.WHITE);
    }
    
   private void updateDisplay() {
    String newTimeText = "⏱️ Time: " + timeLeft + "s";
    if (!timerLabel.getText().equals(newTimeText)) {
        timerLabel.setText(newTimeText);
        timerBar.setValue(timeLeft);
        
        repaint();
    }
}
    
    public boolean isTimeRunningOut() {
        return timeLeft <= 3;
    }
    
    public int getTimeLeft() {
        return timeLeft;
    }
}