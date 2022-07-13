package events.executors;

import characters.GameCharacter;
import rooms.BlockPosition;

public class CharacterUpdateExecutor extends Executor
{
    public static void executeMove(GameCharacter ch, BlockPosition pos)
    {
        gameScreenPanel.moveCharacter(ch, pos, true);
    }
}
