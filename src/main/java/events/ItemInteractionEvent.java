package events;

import animation.Animation;
import entity.items.Item;

public class ItemInteractionEvent extends GameEvent
{
    private Animation animation;
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
        super(item, toPrint);
    }

    public ItemInteractionEvent(Item item, String toPrint, Animation animation)
    {
        super(item, toPrint);
        this.animation = animation;
    }

    public boolean hasAnimation()
    {
        return animation != null;
    }

    public Animation getAnimation()
    {
        return animation;
    }

    public Type getType()
    {
        return type;
    }
}
