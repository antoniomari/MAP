package events;

import animation.Animation;
import items.Item;

public class ItemInteractionEvent extends GameEvent
{
    private Animation animation;

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
}
