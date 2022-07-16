package events;

import animation.Animation;
import animation.StillAnimation;
import entity.items.Item;

public class ItemInteractionEvent extends GameEvent
{
    private StillAnimation animation;
    private Type type;

    public enum Type
    {
        OBSERVE
        {
            public String toString()
            {
                return "aggiunto alla stanza";
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

    public ItemInteractionEvent(Item item, String toPrint, StillAnimation animation)
    {
        super(toPrint);
        itemInvolved = item;
        this.animation = animation;
    }

    public boolean hasAnimation()
    {
        return animation != null;
    }

    public StillAnimation getAnimation()
    {
        return animation;
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
