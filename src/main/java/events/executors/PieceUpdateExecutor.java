package events.executors;

import entity.GamePiece;
import entity.rooms.BlockPosition;
import general.GameManager;

/**
 * Esecutore per aggiornare la visualizzazione
 * dei GamePiece sullo schermo.
 */
public class PieceUpdateExecutor extends Executor
{
    public static void executeUpdateSprite(GamePiece piece)
    {
        gameScreenPanel.updateSprite(piece);
    }

    public static void executeMove(GamePiece piece, BlockPosition oldPos, BlockPosition newPos, int millisecondWaitEnd)
    {
        gameScreenPanel.movePiece(piece, oldPos, newPos, millisecondWaitEnd, true);
    }
}
