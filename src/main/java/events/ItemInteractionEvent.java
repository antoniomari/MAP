package events;

import animation.StillAnimation;
import entity.items.Item;

import java.awt.*;
import java.util.List;

public class ItemInteractionEvent extends GameEvent
{
    private List<Image> frames;
    private Type type;
    private String whatAnimation;

    public enum Type
    {
        OBSERVE
        {
            public String toString()
            {
                return "osservata";
            }
        },
        UPDATE_SPRITE
                {
                    public String toString()
                    {
                        return " aggiornato sprite";
                    }
                },
        EFFECT_ANIMATION
                {
                    public String toString()
                    {
                        return " eseguito effetto animato";
                    }
                }
    }


    public ItemInteractionEvent(Item item, Type type)
    {
        this(item, type.toString());
        this.type = type;

    }

    public String getAnimationName()
    {
        return whatAnimation;
    }

    public ItemInteractionEvent(Item item, String whatAnimation, Type type)
    {
        this(item, type);
        this.whatAnimation = whatAnimation;
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
