package events;

import characters.PlayingCharacter;
import items.Item;
import items.PickupableItem;

public class InventoryEvent extends GameEvent
{
    Type type;
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

    public InventoryEvent(PlayingCharacter ch, PickupableItem item, Type type)
    {
        this(ch, item, type.toString());
        this.type = type;
    }

    public InventoryEvent(PlayingCharacter ch, PickupableItem item, String toPrint)
    {
        super(item, toPrint);
    }

    public Type getType()
    {
        return this.type;
    }
}
