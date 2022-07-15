package events.executors;

import entity.characters.GameCharacter;
import entity.rooms.BlockPosition;

public class CharacterUpdateExecutor extends Executor
{
    public static void executeMove(GameCharacter ch, BlockPosition pos)
    {
        gameScreenPanel.moveCharacter(ch, pos, true);
    }
}
