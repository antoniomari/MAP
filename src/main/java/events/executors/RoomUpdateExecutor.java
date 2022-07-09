package events.executors;

import items.Item;
import rooms.Coordinates;

public class RoomUpdateExecutor extends Executor
{

    public static void executeRemoveItem(Item it)
    {
        mainFrame.removeItemCurrentRoom(it);
    }

    public static void executeAddItem(Item it, Coordinates coord)
    {
        mainFrame.addItemCurrentRoom(it, coord);
    }

}
