package events.executors;

import entity.GamePiece;
import entity.rooms.BlockPosition;
import entity.rooms.Room;

import javax.swing.plaf.IconUIResource;

public class RoomUpdateExecutor extends Executor
{

    public static void executeRemovePiece(GamePiece piece)
    {
        if(gameScreenPanel.getLabelAssociated(piece) != null)
            gameScreenPanel.removePieceFromScreen(piece);
    }

    public static void executeAddPiece(Room roomInvolved, GamePiece piece, BlockPosition pos)
    {
        if(roomInvolved.equals(mainFrame.getCurrentRoom()))
            gameScreenPanel.addPieceOnScreen(piece, pos);

    }
}
