package events.executors;

import entity.GamePiece;
import general.GameManager;

public class PieceUpdateExecutor extends Executor
{
    public static void executeUpdateSprite(GamePiece piece)
    {
        gameScreenPanel.updateSprite(piece);
        GameManager.continueScenario();
    }
}
