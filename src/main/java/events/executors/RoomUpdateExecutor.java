package events.executors;

import entity.characters.GameCharacter;
import entity.items.Item;
import entity.rooms.BlockPosition;

public class RoomUpdateExecutor extends Executor
{

    public static void executeRemoveItem(Item it)
    {
        gameScreenPanel.removeItemCurrentRoom(it);
    }

    public static void executeAddItem(Item it, BlockPosition pos)
    {
        gameScreenPanel.addItemCurrentRoom(it, pos);
    }

    public static void executeAddCharacter(GameCharacter ch, BlockPosition pos)
    {
        gameScreenPanel.addCharacterCurrentRoom(ch, pos);
    }

}
