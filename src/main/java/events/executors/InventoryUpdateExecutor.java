package events.executors;

import GUI.InventoryPanel;
import GUI.MainFrame;
import entity.items.PickupableItem;

public class InventoryUpdateExecutor extends Executor
{

    public static void executeAdd(PickupableItem it)
    {
        inventoryPanel.addItem(it);
    }

    public static void executeDrop(PickupableItem it)
    {
        inventoryPanel.dropFromInventory(it);
    }
}
