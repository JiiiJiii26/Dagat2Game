package ai;

import models.Board;
import models.Ship;
import game.ShotResult;
import java.util.ArrayList;
import java.util.Random;

public class AIPlayer {
    private Board board;
    private String difficulty;
    private Random random;
    //private int lastHitX, lastHitY;
    private boolean huntingMode;
    private ArrayList<int[]> huntTargets;
    private ArrayList<int[]> potentialTargets;

    public AIPlayer(String difficulty) {
        this.board = new Board();
        this.difficulty = difficulty;
        this.random = new Random();
        this.huntingMode = false;
        this.huntTargets = new ArrayList<>();
        this.potentialTargets = new ArrayList<>();

        placeAIShips();
    }

    private void placeAIShips() {

        Ship[] ships = {
                new Ship("Carrier", 5),
                new Ship("Battleship", 4),
                new Ship("Cruiser", 3),
                new Ship("Submarine", 3),
                new Ship("Destroyer", 2)
        };

        for (Ship ship : ships) {
            boolean placed = false;
            int attempts = 0;

            while (!placed && attempts < 100) {
                int x = random.nextInt(10);
                int y = random.nextInt(10);
                boolean horizontal = random.nextBoolean();

                placed = board.placeShip(ship, x, y, horizontal);
                attempts++;
            }
        }
    }

    public int[] getNextMove() {
        switch (difficulty) {
            case "Easy":
                return getRandomMove();
            case "Medium":
                return getSmartMove();
            case "Hard":
                return getHuntingMove();
            default:
                return getRandomMove();
        }
    }

    private int[] getRandomMove() {

        int x = random.nextInt(10);
        int y = random.nextInt(10);
        return new int[] { x, y };
    }

    private int[] getSmartMove() {

        if (potentialTargets.isEmpty()) {

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    potentialTargets.add(new int[] { i, j });
                }
            }
        }

        potentialTargets.removeIf(pos -> board.getCell(pos[0], pos[1]).isFiredUpon());

        if (!potentialTargets.isEmpty()) {
            return potentialTargets.get(random.nextInt(potentialTargets.size()));
        }
        return getRandomMove();
    }

    private int[] getHuntingMove() {
        if (huntingMode && !huntTargets.isEmpty()) {

            int[] target = huntTargets.remove(0);
            return target;
        } else {
            huntingMode = false;
            return getSmartMove();
        }
    }

    public void processResult(int x, int y, ShotResult result) {
        if (result == ShotResult.HIT || result == ShotResult.SUNK) {
            huntingMode = true;
            lastHitX = x;
            lastHitY = y;

            addAdjacentTargets(x, y);

            if (result == ShotResult.SUNK) {

                huntingMode = false;
                huntTargets.clear();
            }
        }
    }

    private void addAdjacentTargets(int x, int y) {
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (newX >= 0 && newX < 10 && newY >= 0 && newY < 10) {
                if (!board.getCell(newX, newY).isFiredUpon()) {
                    huntTargets.add(new int[] { newX, newY });
                }
            }
        }
    }

    public Board getBoard() {
        return board;
    }

    public boolean allShipsSunk() {
        return board.allShipsSunk();
    }
}

