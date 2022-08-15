package events.executors;

import GUI.GameScreenPanel;
import GUI.InventoryPanel;
import GUI.MainFrame;

import java.util.Objects;

public class Executor
{
    protected static MainFrame mainFrame;
    protected static GameScreenPanel gameScreenPanel;
    protected static InventoryPanel inventoryPanel;
    
    public static void setMainFrame(MainFrame frame)
    {
        Objects.requireNonNull(frame);

        mainFrame = frame;
        gameScreenPanel = frame.getGameScreenPanel();
        inventoryPanel = frame.getInventoryPanel();
    }
}
