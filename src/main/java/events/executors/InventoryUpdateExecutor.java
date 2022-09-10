package events.executors;

import entity.items.PickupableItem;

/**
 * Esecutore per aggiornare la visualizzazione dell'inventario
 * sullo schermo.
 */
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
