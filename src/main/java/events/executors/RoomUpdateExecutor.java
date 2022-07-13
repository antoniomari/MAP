package events.executors;

import items.Item;
import rooms.BlockPosition;

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

}
