package events;

import characters.PlayingCharacter;
import items.Item;
import items.PickupableItem;

public class InventoryEvent extends GameEvent
{
    public enum Type
    {
        ADD_ITEM {
            public String toString()
            {
                return "aggiunto all'inventario";
            }
        },
        USE_ITEM
        {
            @Override
            public String toString()
            {
                return "utilizzato dall'inventario";
            }
        }
    }

    public InventoryEvent(PlayingCharacter ch, PickupableItem item, String toPrint)
    {
        super(item, toPrint);
    }
}
