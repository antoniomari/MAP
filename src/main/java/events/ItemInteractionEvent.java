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
    /** Item coinvolto nell'evento. */
    private final Item itemInvolved;
    /** Frames per eventuale animazione. */
    private List<Image> frames;
    /** Tipo di evento. */
    private Type type;

    /** Tipo di evento ItemInteractionEvent. */
    public enum Type
    {
        /** Evento osserva oggetto. */
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
