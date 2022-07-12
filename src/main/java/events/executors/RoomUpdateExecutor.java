package events.executors;

import items.Item;
import rooms.Coordinates;

public class RoomUpdateExecutor extends Executor
{

    public static void executeRemoveItem(Item it)
    {
        gameScreenPanel.removeItemCurrentRoom(it);
    }

    public static void executeAddItem(Item it, Coordinates coord)
    {
        gameScreenPanel.addItemCurrentRoom(it, coord);
    }

}
