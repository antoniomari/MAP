package events.executors;

import items.Item;

public class RoomUpdateExecutor extends Executor
{

    public static void executeRemoveItem(Item it)
    {
        mainFrame.removeItemCurrentRoom(it);
    }
}
