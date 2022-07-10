package events.executors;

import characters.GameCharacter;
import rooms.Coordinates;

public class CharacterUpdateExecutor extends Executor
{
    public static void executeMove(GameCharacter ch, Coordinates pos)
    {
        mainFrame.moveCharacter(ch, pos);
    }
}
