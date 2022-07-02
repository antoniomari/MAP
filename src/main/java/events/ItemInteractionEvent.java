package events;

import items.Item;

public class ItemInteractionEvent extends GameEvent
{
    public ItemInteractionEvent(Item item, String toPrint)
    {
        super(item, toPrint);
    }
}
