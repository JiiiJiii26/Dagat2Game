package campaign;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Manages character animations for idle, attack, and damaged states.
 * Handles frame sequencing, timing, and automatic transitions.
 */
public class CharacterAnimation {
    public enum State { IDLE, ATTACK, DAMAGED }

    private final JLabel portraitLabel;
    private final ImageIcon[][] frames; // [state][frameIndex]
    private final int[][] durations;    // [state][slotIndex] tick durations
    private final int[][] frameMaps;    // [state][slotIndex] -> frameIndex, optional
    private final Runnable[] onFinishes; // [state] callback when animation finishes
    private Timer timer;
    private int currentSlot, slotCounter;  // renamed from currentFrame/frameCounter to avoid confusion
    private State currentState = State.IDLE;
    private boolean playing;

    /**
     * Creates a CharacterAnimation instance.
     *
     * @param label           The JLabel to display the animation on
     * @param idle            Frames for idle animation
     * @param attack          Frames for attack animation
     * @param damaged         Frames for damaged animation
     * @param idleDur         Durations for idle slots (number of steps). Must match frameMap length if provided, else frame count.
     * @param attackDur       Durations for attack slots
     * @param damagedDur      Durations for damaged slots
     * @param idleFrameMap    Optional mapping from slot index to frame index (e.g. for irregular sequences)
     * @param attackFrameMap  Optional mapping for attack
     * @param damagedFrameMap Optional mapping for damaged
     * @param attackOnFinish  Callback when attack finishes (e.g., return to idle)
     * @param damagedOnFinish Callback when damaged finishes
     */
    public CharacterAnimation(JLabel label, ImageIcon[] idle, ImageIcon[] attack, ImageIcon[] damaged,
                              int[] idleDur, int[] attackDur, int[] damagedDur,
                              int[] idleFrameMap, int[] attackFrameMap, int[] damagedFrameMap,
                              Runnable attackOnFinish, Runnable damagedOnFinish) {
        // Input validation
        if (idleDur != null && idle != null) {
            int expected = idleFrameMap != null ? idleFrameMap.length : idle.length;
            if (idleDur.length != expected) {
                throw new IllegalArgumentException("Idle durations length must match frames or frameMap length");
            }
        }
        if (attackDur != null && attack != null) {
            int expected = attackFrameMap != null ? attackFrameMap.length : attack.length;
            if (attackDur.length != expected) {
                throw new IllegalArgumentException("Attack durations length must match frames or frameMap length");
            }
        }
        if (damagedDur != null && damaged != null) {
            int expected = damagedFrameMap != null ? damagedFrameMap.length : damaged.length;
            if (damagedDur.length != expected) {
                throw new IllegalArgumentException("Damaged durations length must match frames or frameMap length");
            }
        }
        this.portraitLabel = label;
        this.frames = new ImageIcon[3][];
        this.frames[0] = idle;
        this.frames[1] = attack;
        this.frames[2] = damaged;
        this.durations = new int[3][];
        this.durations[0] = idleDur;
        this.durations[1] = attackDur;
        this.durations[2] = damagedDur;
        this.frameMaps = new int[3][];
        this.frameMaps[0] = idleFrameMap;
        this.frameMaps[1] = attackFrameMap;
        this.frameMaps[2] = damagedFrameMap;
        this.onFinishes = new Runnable[3];
        this.onFinishes[0] = null; // idle loops indefinitely
        this.onFinishes[1] = attackOnFinish;
        this.onFinishes[2] = damagedOnFinish;
    }

    /**
     * Starts the animation for the specified state.
     * @param newState The state to animate (IDLE loops, others play once)
     */
    public void start(State newState) {
        stop();
        currentState = newState;
        currentSlot = 0;
        slotCounter = 0;
        playing = true;

        int stateIndex = currentState.ordinal();
        if (frames[stateIndex] == null || frames[stateIndex].length == 0) {
            return; // nothing to animate
        }
        // Show the first frame immediately
        updatePortrait();
        timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (portraitLabel == null) return;
                int idx = currentState.ordinal();
                int[] dur = durations[idx];
                if (dur == null || currentSlot >= dur.length) {
                    // No durations or finished sequence
                    if (currentState == State.IDLE) {
                        currentSlot = 0;
                        slotCounter = 0;
                        updatePortrait();
                    } else {
                        stop();
                        if (onFinishes[idx] != null) {
                            onFinishes[idx].run();
                        }
                    }
                    return;
                }

                slotCounter++;
                if (slotCounter >= dur[currentSlot]) {
                    slotCounter = 0;
                    currentSlot++;
                    if (currentSlot >= dur.length) {
                        // Finished all slots
                        if (currentState == State.IDLE) {
                            currentSlot = 0;
                            updatePortrait();
                        } else {
                            stop();
                            if (onFinishes[idx] != null) {
                                onFinishes[idx].run();
                            }
                        }
                    } else {
                        updatePortrait();
                    }
                }
            }
        });
        timer.start();
    }

    /**
     * Updates the portrait label to the current frame.
     */
    private void updatePortrait() {
        int idx = currentState.ordinal();
        if (frames[idx] == null) return;
        int[] map = frameMaps[idx];
        int frameIndex;
        if (map != null && currentSlot < map.length) {
            frameIndex = map[currentSlot];
        } else {
            frameIndex = currentSlot; // direct indexing
        }
        if (frameIndex >= 0 && frameIndex < frames[idx].length && frames[idx][frameIndex] != null) {
            portraitLabel.setIcon(frames[idx][frameIndex]);
        }
    }

    /**
     * Stops the current animation.
     */
    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        playing = false;
    }

    /**
     * Checks if an animation is currently playing.
     * @return true if playing
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Gets the current animation state.
     * @return the current state
     */
    public State getCurrentState() {
        return currentState;
    }
}