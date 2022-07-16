package events.executors;

import GUI.GameScreenManager;
import GUI.gamestate.GameState;
import entity.characters.GameCharacter;
import entity.rooms.BlockPosition;

public class CharacterUpdateExecutor extends Executor
{
    public static void executeMove(GameCharacter ch, BlockPosition oldPos, BlockPosition newPos)
    {
        gameScreenPanel.moveCharacter(ch, oldPos, newPos, true);
    }
}
