package events;

import animation.StillAnimation;
import entity.items.Item;

import java.awt.*;
import java.util.List;

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
        this(item, type.toString());
        this.type = type;

    }


    public ItemInteractionEvent(Item item, String toPrint)
    {
        super(toPrint);
        itemInvolved = item;


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
