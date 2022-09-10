package events;

import entity.items.Item;

import java.awt.*;
import java.util.List;

/**
 * Rappresenta un evento generato dall'interazione
 * del giocatore con un oggetto.
 */
public class ItemInteractionEvent extends GameEvent
{
    private final Item itemInvolved;
    private List<Image> frames;
    private Type type;

    public enum Type
    {
        OBSERVE
        {
            public String toString()
            {
                return "osservata";
            }
        }
    }


    public ItemInteractionEvent(Item item, Type type)
    {
        super(type.toString());
        itemInvolved = item;
        this.type = type;

    }

    public ItemInteractionEvent(Item item, String toPrint, List<Image> frames)
    {
        super(toPrint);
        itemInvolved = item;
        this.frames = frames;
    }

    public Item getItemInvolved()
    {
        return itemInvolved;
    }

    public boolean hasAnimation()
    {
        return frames != null;
    }

    public List<Image> getFrames()
    {
        return frames;
    }

    public Type getType()
    {
        return type;
    }

    @Override
    public String getEventString()
    {
        return eventTime + " -> " + "[" + itemInvolved + "] " + type;
    }
}
