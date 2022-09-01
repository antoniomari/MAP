package events.executors;

import entity.GamePiece;
import general.GameManager;

public class PieceUpdateExecutor extends Executor
{
    // TODO: capire qua quando dev'essere usato
    public static void executeUpdateSprite(GamePiece piece)
    {
        gameScreenPanel.updateSprite(piece);
        //GameManager.continueScenario();
    }
}
