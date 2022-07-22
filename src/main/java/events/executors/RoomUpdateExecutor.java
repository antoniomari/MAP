package events.executors;

import entity.GamePiece;
import entity.rooms.BlockPosition;

public class RoomUpdateExecutor extends Executor
{

    public static void executeRemovePiece(GamePiece it)
    {
        gameScreenPanel.removePieceCurrentRoom(it);
    }

    public static void executeAddPiece(GamePiece piece, BlockPosition pos)
    {
        gameScreenPanel.addPieceCurrentRoom(piece, pos);
    }
}
