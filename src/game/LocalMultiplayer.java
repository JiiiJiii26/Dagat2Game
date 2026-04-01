package game;

import models.Board;
import models.Ship;
import models.Cell;
import characters.GameCharacter;
import characters.*;
import java.util.ArrayList;

public class LocalMultiplayer {
    
    private Board player1Board;
    private Board player2Board;
    private boolean player1Turn = true;
    private GameCharacter player1Character;
    private GameCharacter player2Character;
    private GameListener listener;
    
    public interface GameListener {
        void onGameStart();
        void onPlayerTurn(int playerNumber);
        void onShotFired(int playerNumber, int x, int y, ShotResult result);
        void onGameEnd(int winnerPlayerNumber);
        void onBoardUpdate(Board board, boolean isPlayer1);
        void onCharacterSkillUsed(int playerNumber, String skillName, boolean success);
        void onCharacterUltimateUsed(int playerNumber, String ultimateName);
    }
    
    public LocalMultiplayer(GameListener listener) {
        this(listener, null, null);
    }
    
    public LocalMultiplayer(GameListener listener, GameCharacter player1Char, GameCharacter player2Char) {
        this.listener = listener;
        this.player1Board = new Board();
        this.player2Board = new Board();
        this.player1Character = player1Char;
        this.player2Character = player2Char;
        
        
        if (player1Char != null) {
            player1Char.setBoard(player1Board);
            if (player1Char instanceof Aeris) {
                ((Aeris) player1Char).setPlayerBoard(player1Board);
            } else if (player1Char instanceof Kael) {
                ((Kael) player1Char).setPlayerBoard(player1Board);
            } else if (player1Char instanceof Skye) {
                ((Skye) player1Char).setPlayerBoard(player1Board);
            }
        }
        
        if (player2Char != null) {
            player2Char.setBoard(player2Board);
            if (player2Char instanceof Aeris) {
                ((Aeris) player2Char).setPlayerBoard(player2Board);
            } else if (player2Char instanceof Kael) {
                ((Kael) player2Char).setPlayerBoard(player2Board);
            } else if (player2Char instanceof Skye) {
                ((Skye) player2Char).setPlayerBoard(player2Board);
            }
        }
    }
    
    public Board getPlayer1Board() {
        return player1Board;
    }
    
    public Board getPlayer2Board() {
        return player2Board;
    }
    
    public GameCharacter getPlayer1Character() {
        return player1Character;
    }
    
    public GameCharacter getPlayer2Character() {
        return player2Character;
    }
    
    public boolean isPlayer1Turn() {
        return player1Turn;
    }
    
    public int getCurrentPlayer() {
        return player1Turn ? 1 : 2;
    }
    
    public boolean placeShip(int playerNumber, Ship ship, int x, int y, boolean horizontal) {
        Board board = (playerNumber == 1) ? player1Board : player2Board;
        return board.placeShip(ship, x, y, horizontal);
    }
    
    public ShotResult fire(int playerNumber, int x, int y) {
        if ((playerNumber == 1 && !player1Turn) || (playerNumber == 2 && player1Turn)) {
            System.out.println("Not your turn!");
            return ShotResult.INVALID;
        }
        
        Board targetBoard = (playerNumber == 1) ? player2Board : player1Board;
        GameCharacter attackingChar = (playerNumber == 1) ? player1Character : player2Character;
        GameCharacter defendingChar = (playerNumber == 1) ? player2Character : player1Character;
        
        
        if (defendingChar instanceof Valerius && ((Valerius) defendingChar).areEnemySkillsDisabled()) {
            System.out.println("🚫 Enemy skills are disabled!");
        }
        
        
        if (defendingChar instanceof Jiji) {
            Jiji jiji = (Jiji) defendingChar;
            Cell targetCell = targetBoard.getCell(x, y);
            
            
            if (targetCell.hasShip() && targetCell.getShip() != null && targetCell.getShip().isShielded()) {
                System.out.println("🔵 Shield blocked the attack!");
                return ShotResult.MISS;
            }
        }
        
        
        if (defendingChar instanceof Morgana && ((Morgana) defendingChar).isEnemyConfusedActive()) {
            System.out.println("🎵 Enemy is confused!");
            
        }
        
        
        ShotResult result = targetBoard.fire(x, y);
        
        if (listener != null) {
            listener.onShotFired(playerNumber, x, y, result);
        }
        
        
        updateCharacterTurn(playerNumber);
        
        
        if (targetBoard.allShipsSunk()) {
            if (listener != null) {
                listener.onGameEnd(playerNumber);
            }
        } else {
            
            if (result == ShotResult.MISS) {
                player1Turn = !player1Turn;
                if (listener != null) {
                    listener.onPlayerTurn(getCurrentPlayer());
                }
            } else {
                
                boolean extraTurn = false;
                
                if (attackingChar instanceof Jiji && ((Jiji) attackingChar).isOverclockActive()) {
                    extraTurn = true;
                    System.out.println("⚡ Overclock grants an extra turn!");
                }
                
                if (!extraTurn) {
                    player1Turn = !player1Turn;
                    if (listener != null) {
                        listener.onPlayerTurn(getCurrentPlayer());
                    }
                }
            }
        }
        
        return result;
    }
    
    private void updateCharacterTurn(int playerNumber) {
        if (playerNumber == 1 && player1Character != null) {
            updateCharacter(player1Character);
        } else if (playerNumber == 2 && player2Character != null) {
            updateCharacter(player2Character);
        }
    }
    
    private void updateCharacter(GameCharacter character) {
        if (character instanceof Jiji) {
            ((Jiji) character).updateTurnCounter();
        } else if (character instanceof Kael) {
            ((Kael) character).updateTurnCounter();
        } else if (character instanceof Valerius) {
            ((Valerius) character).updateTurnCounter();
        } else if (character instanceof Skye) {
            ((Skye) character).updateTurnCounter();
        } else if (character instanceof Morgana) {
            ((Morgana) character).updateTurnCounter();
        } else if (character instanceof Aeris) {
            ((Aeris) character).updateTurnCounter();
        } else if (character instanceof Selene) {
            ((Selene) character).updateTurnCounter();
        } else if (character instanceof Flue) {
            ((Flue) character).updateTurnCounter();
        }
    }
    
    public boolean useCharacterSkill(int playerNumber, int skillNumber, int x, int y, boolean horizontal) {
        if ((playerNumber == 1 && !player1Turn) || (playerNumber == 2 && player1Turn)) {
            System.out.println("Not your turn!");
            return false;
        }
        
        GameCharacter character = (playerNumber == 1) ? player1Character : player2Character;
        Board targetBoard = (playerNumber == 1) ? player2Board : player1Board;
        Board playerBoard = (playerNumber == 1) ? player1Board : player2Board;
        
        boolean success = false;
        String skillName = "";
        
        if (character instanceof Jiji) {
            Jiji jiji = (Jiji) character;
            switch(skillNumber) {
                case 1:
                    skillName = "Data Leech";
                    success = jiji.useDataLeech(targetBoard);
                    break;
                case 2:
                    skillName = "Overclock";
                    success = jiji.useOverclock();
                    break;
                case 3:
                    skillName = "System Overload";
                    success = jiji.useSystemOverload(targetBoard);
                    break;
            }
        } else if (character instanceof Kael) {
            Kael kael = (Kael) character;
            switch(skillNumber) {
                case 1:
                    skillName = "Shadow Step";
                    success = kael.useShadowStep(playerBoard, x, y, x, y);
                    break;
                case 2:
                    skillName = "Shadow Blade";
                    int destroyed = kael.useShadowBlade(targetBoard, x, y, horizontal);
                    success = destroyed > 0;
                    break;
                case 3:
                    skillName = "Shadow Domain";
                    destroyed = kael.useShadowDomain(targetBoard, x, y);
                    success = destroyed > 0;
                    break;
            }
        } else if (character instanceof Valerius) {
            Valerius valerius = (Valerius) character;
            switch(skillNumber) {
                case 1:
                    skillName = "Radar Overload";
                    success = valerius.useRadarOverload();
                    break;
                case 2:
                    skillName = "Precision Strike";
                    if (valerius.usePrecisionStrike()) {
                        int destroyed = valerius.applyPrecisionStrike(targetBoard, x, y, horizontal);
                        success = destroyed > 0;
                    }
                    break;
                case 3:
                    skillName = "Fortress Mode";
                    success = valerius.useFortressMode();
                    break;
            }
        } else if (character instanceof Skye) {
            Skye skye = (Skye) character;
            switch(skillNumber) {
                case 1:
                    skillName = "Catnip Explosion";
                    int destroyed = skye.useCatnipExplosion(targetBoard, x, y);
                    success = destroyed > 0;
                    break;
                case 2:
                    skillName = "Laser Pointer";
                    success = skye.useLaserPointer();
                    break;
                case 3:
                    skillName = "Nine Lives";
                    success = skye.useNineLives(playerBoard, x, y);
                    break;
            }
        } else if (character instanceof Morgana) {
            Morgana morgana = (Morgana) character;
            switch(skillNumber) {
                case 1:
                    skillName = "Enchanting Melody";
                    success = morgana.useEnchantingMelody();
                    break;
                case 2:
                    skillName = "Whirlpool Trap";
                    success = morgana.useWhirlpoolTrap(targetBoard, x, y);
                    break;
                case 3:
                    skillName = "Storm Call";
                    success = morgana.useStormCall(targetBoard) > 0;
                    break;
            }
        } else if (character instanceof Aeris) {
            Aeris aeris = (Aeris) character;
            switch(skillNumber) {
                case 1:
                    skillName = "Adaptive Instinct";
                    success = aeris.useAdaptiveInstinct(playerBoard, -1);
                    break;
                case 2:
                    skillName = "Multitask Overdrive";
                    success = aeris.useMultitaskOverdrive();
                    break;
                case 3:
                    skillName = "Relentless Ascent";
                    int destroyed = aeris.useRelentlessAscent(targetBoard, y);
                    success = destroyed > 0;
                    break;
            }
        } else if (character instanceof Selene) {
            Selene selene = (Selene) character;
            switch(skillNumber) {
                case 1:
                    skillName = "Lunar Reveal";
                    success = selene.useLunarReveal(targetBoard, x, y);
                    break;
                case 2:
                    skillName = "Crescent Strike";
                    int destroyed = selene.useCrescentStrike(targetBoard, x, y);
                    success = destroyed > 0;
                    break;
                case 3:
                    skillName = "Starfall Link";
                    success = selene.useStarfallLink(targetBoard);
                    break;
            }
        } else if (character instanceof Flue) {
            Flue flue = (Flue) character;
            switch(skillNumber) {
                case 1:
                    skillName = "Corruption.EXE";
                    success = flue.useCorruption(targetBoard, x, y);
                    break;
                case 2:
                    skillName = "Fortification.GRID";
                    success = flue.useFortification(playerBoard, x, y);
                    break;
                case 3:
                    skillName = "Kernel.Decimation.REQ";
                    success = flue.useKernelDecimation(targetBoard, x, y);
                    break;
            }
        }
        
        if (listener != null) {
            listener.onCharacterSkillUsed(playerNumber, skillName, success);
        }
        
        
        if (listener != null) {
            listener.onBoardUpdate(playerBoard, playerNumber == 1);
            listener.onBoardUpdate(targetBoard, playerNumber != 1);
        }
        
        return success;
    }
    
    public boolean isGameOver() {
        return player1Board.areAllShipsSunk() || player2Board.areAllShipsSunk();
    }
    
    public String getWinner() {
        if (player1Board.areAllShipsSunk()) {
            return "PLAYER 2";
        } else if (player2Board.areAllShipsSunk()) {
            return "PLAYER 1";
        }
        return "NO WINNER YET";
    }
    
    public void reset() {
        player1Board = new Board();
        player2Board = new Board();
        player1Turn = true;
    }
    
    public void setPlayerBoard(int playerNumber, Board board) {
        if (playerNumber == 1) {
            this.player1Board = board;
        } else {
            this.player2Board = board;
        }
    }
}