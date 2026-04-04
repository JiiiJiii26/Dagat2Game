package game;

import models.Board;
import models.Ship;
import models.Cell;
import characters.*;
import java.util.ArrayList;
import java.awt.Color;

public class LocalMultiplayer {
    
    private Board player1Board;
    private Board player2Board;
    private boolean player1Turn = true;
    private GameListener listener;
    private GameCharacter player1Character;
    private GameCharacter player2Character;
    
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
            
            boolean extraTurn = false;
            
            
            GameCharacter attackingChar = (playerNumber == 1) ? player1Character : player2Character;
            if (attackingChar instanceof Jiji && ((Jiji) attackingChar).isOverclockActive() && result == ShotResult.HIT) {
                extraTurn = true;
                System.out.println("⚡ Overclock grants an extra turn!");
            }
            
            
            if (!extraTurn) {
                player1Turn = !player1Turn;
                System.out.println("Turn switched to Player " + (player1Turn ? 1 : 2));
                if (listener != null) {
                    listener.onPlayerTurn(getCurrentPlayer());
                }
            } else {
                System.out.println("Extra turn! Player " + playerNumber + " goes again.");
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
            System.out.println("Not your turn for skill!");
            return false;
        }
        
        GameCharacter character = (playerNumber == 1) ? player1Character : player2Character;
        Board targetBoard = (playerNumber == 1) ? player2Board : player1Board;
        Board playerBoard = (playerNumber == 1) ? player1Board : player2Board;
        
        if (character == null) {
            System.out.println("No character for player " + playerNumber);
            return false;
        }
        
        boolean success = false;
        boolean shouldSwitchTurn = true; 
        
        System.out.println("useCharacterSkill called - Player: " + playerNumber + 
                           ", Skill: " + skillNumber + 
                           ", Coordinates: (" + x + "," + y + 
                           "), Horizontal: " + horizontal);
        
        
        if (character instanceof Jiji) {
            Jiji jiji = (Jiji) character;
            switch(skillNumber) {
                case 1: 
                    success = jiji.useDataLeech(targetBoard);
                    shouldSwitchTurn = true;
                    break;
                case 2: 
                    success = jiji.useOverclock();
                    shouldSwitchTurn = false; 
                    break;
                case 3: 
                    success = jiji.useSystemOverload(targetBoard);
                    shouldSwitchTurn = true;
                    break;
            }
        } else if (character instanceof Kael) {
            Kael kael = (Kael) character;
            switch(skillNumber) {
                case 1: 
                    
                    success = false;
                    shouldSwitchTurn = false;
                    break;
                case 2: 
                    int destroyed = kael.useShadowBlade(targetBoard, x, y, horizontal);
                    success = destroyed > 0;
                    shouldSwitchTurn = true;
                    System.out.println("Shadow Blade destroyed " + destroyed + " cells");
                    break;
                case 3: 
                    destroyed = kael.useShadowDomain(targetBoard, x, y);
                    success = destroyed > 0;
                    shouldSwitchTurn = true;
                    System.out.println("Shadow Domain destroyed " + destroyed + " cells");
                    break;
            }
        } else if (character instanceof Valerius) {
            Valerius valerius = (Valerius) character;
            switch(skillNumber) {
                case 1: 
                    success = valerius.useRadarOverload();
                    shouldSwitchTurn = true;
                    break;
                case 2: 
                    if (valerius.usePrecisionStrike()) {
                        int destroyed = valerius.applyPrecisionStrike(targetBoard, x, y, horizontal);
                        success = destroyed > 0;
                        shouldSwitchTurn = true;
                    }
                    break;
                case 3: 
                    success = valerius.useFortressMode();
                    shouldSwitchTurn = true;
                    break;
            }
        } else if (character instanceof Skye) {
            Skye skye = (Skye) character;
            switch(skillNumber) {
                case 1: 
                    int destroyed = skye.useCatnipExplosion(targetBoard, x, y);
                    success = destroyed > 0;
                    shouldSwitchTurn = true;
                    break;
                case 2: 
                    success = skye.useLaserPointer();
                    shouldSwitchTurn = true;
                    break;
                case 3: 
                    success = skye.useNineLives(playerBoard, x, y);
                    shouldSwitchTurn = true;
                    break;
            }
        } else if (character instanceof Morgana) {
            Morgana morgana = (Morgana) character;
            switch(skillNumber) {
                case 1: 
                    success = morgana.useEnchantingMelody();
                    shouldSwitchTurn = true;
                    break;
                case 2: 
                    success = morgana.useWhirlpoolTrap(targetBoard, x, y);
                    shouldSwitchTurn = true;
                    break;
                case 3: 
                    int flooded = morgana.useTidalWave(targetBoard);
                    success = flooded > 0;
                    shouldSwitchTurn = true;
                    break;
            }
        } else if (character instanceof Aeris) {
            Aeris aeris = (Aeris) character;
            switch(skillNumber) {
                case 1: 
                    success = aeris.useAdaptiveInstinct(playerBoard, -1);
                    shouldSwitchTurn = true;
                    break;
                case 2: 
                    success = aeris.useMultitaskOverdrive();
                    shouldSwitchTurn = false; 
                    break;
                case 3: 
                    int destroyed = aeris.useRelentlessAscent(targetBoard, y);
                    success = destroyed > 0;
                    shouldSwitchTurn = true;
                    break;
            }
        } else if (character instanceof Selene) {
            Selene selene = (Selene) character;
            switch(skillNumber) {
                case 1: 
                    success = selene.useLunarReveal(targetBoard, x, y);
                    shouldSwitchTurn = true;
                    break;
                case 2: 
                    int destroyed = selene.useCrescentStrike(targetBoard, x, y);
                    success = destroyed > 0;
                    shouldSwitchTurn = true;
                    break;
                case 3: 
                    success = selene.useStarfallLink(targetBoard);
                    shouldSwitchTurn = true;
                    break;
            }
        } else if (character instanceof Flue) {
            Flue flue = (Flue) character;
            switch(skillNumber) {
                case 1: 
                    success = flue.useCorruption(targetBoard, x, y);
                    shouldSwitchTurn = true;
                    break;
                case 2: 
                    success = flue.useFortification(playerBoard, x, y);
                    shouldSwitchTurn = true;
                    break;
                case 3: 
                    success = flue.useKernelDecimation(targetBoard, x, y);
                    shouldSwitchTurn = true;
                    break;
            }
        }
        
        if (listener != null) {
            listener.onCharacterSkillUsed(playerNumber, getSkillName(character, skillNumber), success);
        }
        
        
        updateCharacter(character);
        
        
        if (success && shouldSwitchTurn) {
            player1Turn = !player1Turn;
            System.out.println("Skill used - Turn switched to Player " + (player1Turn ? 1 : 2));
            if (listener != null) {
                listener.onPlayerTurn(getCurrentPlayer());
            }
        } else if (success && !shouldSwitchTurn) {
            System.out.println("Skill used - Turn remains with Player " + playerNumber);
        } else {
            System.out.println("Skill failed - Turn remains with Player " + playerNumber);
        }
        
        return success;
    }
    
    public boolean useShadowStep(int playerNumber, int sourceX, int sourceY, int destX, int destY) {
        if ((playerNumber == 1 && !player1Turn) || (playerNumber == 2 && player1Turn)) {
            System.out.println("Not your turn for Shadow Step!");
            return false;
        }
        
        GameCharacter character = (playerNumber == 1) ? player1Character : player2Character;
        Board playerBoard = (playerNumber == 1) ? player1Board : player2Board;
        
        if (character instanceof Kael) {
            Kael kael = (Kael) character;
            boolean success = kael.useShadowStep(playerBoard, sourceX, sourceY, destX, destY);
            
            if (listener != null) {
                listener.onCharacterSkillUsed(playerNumber, "Shadow Step", success);
            }
            
            
            updateCharacter(character);
            
            
            if (success) {
                player1Turn = !player1Turn;
                System.out.println("Shadow Step - Turn switched to Player " + (player1Turn ? 1 : 2));
                if (listener != null) {
                    listener.onPlayerTurn(getCurrentPlayer());
                }
            } else {
                System.out.println("Shadow Step failed - Turn remains with Player " + playerNumber);
            }
            
            return success;
        }
        
        return false;
    }
    
    private String getSkillName(GameCharacter character, int skillNumber) {
        if (character instanceof Jiji) {
            switch(skillNumber) {
                case 1: return "Data Leech";
                case 2: return "Overclock";
                case 3: return "System Overload";
            }
        } else if (character instanceof Kael) {
            switch(skillNumber) {
                case 1: return "Shadow Step";
                case 2: return "Shadow Blade";
                case 3: return "Shadow Domain";
            }
        } else if (character instanceof Valerius) {
            switch(skillNumber) {
                case 1: return "Radar Overload";
                case 2: return "Precision Strike";
                case 3: return "Fortress Mode";
            }
        } else if (character instanceof Skye) {
            switch(skillNumber) {
                case 1: return "Catnip Explosion";
                case 2: return "Laser Pointer";
                case 3: return "Nine Lives";
            }
        } else if (character instanceof Morgana) {
            switch(skillNumber) {
                case 1: return "Enchanting Melody";
                case 2: return "Whirlpool Trap";
                case 3: return "Storm Call";
            }
        } else if (character instanceof Aeris) {
            switch(skillNumber) {
                case 1: return "Adaptive Instinct";
                case 2: return "Multitask Overdrive";
                case 3: return "Relentless Ascent";
            }
        } else if (character instanceof Selene) {
            switch(skillNumber) {
                case 1: return "Lunar Reveal";
                case 2: return "Crescent Strike";
                case 3: return "Starfall Link";
            }
        } else if (character instanceof Flue) {
            switch(skillNumber) {
                case 1: return "Corruption.EXE";
                case 2: return "Fortification.GRID";
                case 3: return "Kernel.Decimation.REQ";
            }
        }
        return "Unknown Skill";
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
public void setPlayer1Turn(boolean turn) {
    this.player1Turn = turn;
}

public GameListener getListener() {
    return this.listener;
}

}