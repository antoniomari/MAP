package events.executors;

import GUI.GameScreenManager;
import GUI.gamestate.GameState;
import entity.GamePiece;
import entity.characters.GameCharacter;
import entity.rooms.BlockPosition;
import events.GamePieceEvent;

public class CharacterUpdateExecutor extends Executor
{
    public static void executeMove(GamePiece piece, BlockPosition oldPos, BlockPosition newPos, int millisecondWaitEnd)
    {
        gameScreenPanel.movePiece(piece, oldPos, newPos, millisecondWaitEnd, true);
    }
}
