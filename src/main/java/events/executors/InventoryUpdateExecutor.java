package events.executors;

import GUI.InventoryPanel;
import GUI.MainFrame;
import items.PickupableItem;

import javax.swing.*;

public class InventoryUpdateExecutor extends Executor
{
    private static InventoryPanel inventoryPanel;

    public static void setMainFrame(MainFrame frame)
    {
        Executor.setMainFrame(frame);
        inventoryPanel = mainFrame.getInventoryPanel();
    }

    public static void executeAdd(PickupableItem it)
    {
        inventoryPanel.addItem(it.getSprite());
    }
}
