package events.executors;

import entity.GamePiece;
import entity.rooms.BlockPosition;

public class RoomUpdateExecutor extends Executor
{

    public static void executeRemovePiece(GamePiece piece)
    {
        gameScreenPanel.removePieceFromScreen(piece);
    }

    public static void executeAddPiece(GamePiece piece, BlockPosition pos)
    {
        gameScreenPanel.addPieceOnScreen(piece, pos);
    }
}
