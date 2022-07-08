package events.executors;

import GUI.InventoryPanel;
import GUI.MainFrame;
import items.PickupableItem;

import javax.swing.*;

public class InventoryUpdateExecutor
{
    private static MainFrame mainFrame;
    private static InventoryPanel inventoryPanel;

    public static void setMainFrame(MainFrame frame)
    {
        mainFrame = frame;
        inventoryPanel = mainFrame.getInventoryPanel();
    }

    public static void executeAdd(PickupableItem it)
    {
        inventoryPanel.addItem(new ImageIcon(it.getSprite()));
    }
}
