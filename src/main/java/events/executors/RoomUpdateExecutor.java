package events.executors;

import GUI.MainFrame;
import items.Item;

public class RoomUpdateExecutor
{
    private static MainFrame mainFrame;

    public static void setMainFrame(MainFrame frame)
    {
        mainFrame = frame;
    }

    public static void executeRemoveItem(Item it)
    {
        mainFrame.removeItemCurrentRoom(it);
    }
}
