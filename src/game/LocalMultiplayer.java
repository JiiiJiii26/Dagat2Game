package game;

import models.Board;
import characters.GameCharacter;

public class LocalMultiplayer {
    private GameListener listener;

    public LocalMultiplayer(GameListener listener, GameCharacter player, GameCharacter enemy) {
        this.listener = listener;
        this.playerCharacter = player;
        this.enemyCharacter = enemy;
        this.playerBoard = new Board();
        this.enemyBoard = new Board();
    }

    public interface GameListener {
        void onGameStart();
        void onPlayerTurn(int player);
        void onShotFired(int player, int x, int y, game.ShotResult result);
        void onGameEnd(int winner);
        void onBoardUpdate(models.Board board, boolean isPlayerBoard);
        void onCharacterSkillUsed(int player, String skillName, boolean success);
        void onCharacterUltimateUsed(int player, String ultimateName);
    }
    private Board playerBoard;
    private Board enemyBoard;
    private GameCharacter playerCharacter;
    private GameCharacter enemyCharacter;
    private boolean playerTurn = true;



    public Board getPlayerBoard() {
        return playerBoard;
    }

    public Board getEnemyBoard() {
        return enemyBoard;
    }

    public GameCharacter getPlayerCharacter() {
        return playerCharacter;
    }

    public GameCharacter getEnemyCharacter() {
        return enemyCharacter;
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public void switchTurn() {
        playerTurn = !playerTurn;
    }

    public boolean isPlayer1Turn() {
        return playerTurn;
    }

    public void setPlayer1Turn(boolean turn) {
        this.playerTurn = turn;
    }

    public void setPlayerBoard(int player, Board board) {
        if (player == 1) {
            this.playerBoard = board;
        } else {
            this.enemyBoard = board;
        }
    }

    public Board getPlayer1Board() {
        return playerBoard;
    }

    public Board getPlayer2Board() {
        return enemyBoard;
    }

    public GameCharacter getPlayer1Character() {
        return playerCharacter;
    }

    public GameCharacter getPlayer2Character() {
        return enemyCharacter;
    }

    public boolean isGameOver() {
        return playerBoard.allShipsSunk() || enemyBoard.allShipsSunk();
    }

    public String getWinner() {
        if (playerBoard.allShipsSunk()) {
            return "Player 2";
        } else if (enemyBoard.allShipsSunk()) {
            return "Player 1";
        }
        return null;
    }

    public ShotResult fire(int player, int x, int y) {
        Board targetBoard = (player == 1) ? enemyBoard : playerBoard;
        if (targetBoard.isCellFiredUpon(x, y)) {
            return ShotResult.MISS; // Invalid shot, do not switch turn
        }
        ShotResult result = targetBoard.fire(x, y);
        return result;
    }

    public boolean useCharacterSkill(int player, int skillNumber, int x, int y, boolean direction) {
        GameCharacter character = (player == 1) ? playerCharacter : enemyCharacter;
        Board playerBoardLocal = (player == 1) ? this.playerBoard : this.enemyBoard;
        Board enemyBoardLocal = (player == 1) ? this.enemyBoard : this.playerBoard;
        boolean success = character.useSkill(skillNumber, playerBoardLocal, enemyBoardLocal, x, y, direction);
        return success;
    }

    public boolean useShadowStep(int player, int fromX, int fromY, int toX, int toY) {
        // Implement shadow step logic
        System.out.println("Shadow step for player " + player + " from " + fromX + "," + fromY + " to " + toX + "," + toY);
        return true;
    }
}