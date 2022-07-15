package events;

import items.PickupableItem;

public class InventoryEvent extends GameEvent
{
    private Type type;
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

    public InventoryEvent(PickupableItem item, Type type)
    {
        this(item, type.toString());
        this.type = type;
    }

    public InventoryEvent(PickupableItem item, String toPrint)
    {
        super(item, toPrint);
    }

    public Type getType()
    {
        return this.type;
    }
}
