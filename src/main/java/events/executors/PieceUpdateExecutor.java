package events.executors;

import entity.GamePiece;

public class PieceUpdateExecutor extends Executor
{
    public static void executeUpdateSprite(GamePiece piece)
    {
        gameScreenPanel.updateSprite(piece);
    }
}
