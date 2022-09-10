package events;

import entity.items.PickupableItem;

/**
 * Evento che rappresenta la modifica dell'inventario
 * del giocatore.
 */
public class InventoryEvent extends GameEvent
{
    /** Tipo dell'evento. */
    private final Type type;
    /** PickupableItem coinvolto nell'evento. */
    private final PickupableItem pickupInvolved;

    /**
     * Tipo di evento InventoryEvent
     */
    public enum Type
    {
        /** Aggiunta all'inventario. */
        ADD_ITEM
                {
                    public String toString()
                    {
                        return "aggiunto all'inventario";
                    }
                },
        /** Rimozione dall'inventario. */
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
        super(type.toString());
        this.pickupInvolved = item;
        this.type = type;
    }

    public PickupableItem getPickupInvolved()
    {
        return pickupInvolved;
    }



    public Type getType()
    {
        return this.type;
    }

    @Override
    public String getEventString()
    {
        return eventTime + " -> " + "[" + pickupInvolved + "] " + type;
    }

}
