package entity.items;

import animation.Animation;
import animation.StillAnimation;
import events.EventHandler;
import events.ItemInteractionEvent;
import graphics.SpriteManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Door extends Item implements Openable, Lockable
{
    public final static int OPEN = 1;
    public final static int CLOSED = 0;
    public final static int BLOCKED = -1;

    private int state;

    // PATH SPRITESHEET (png + json)
    private final static String SPRITESHEET_PATH = "/img/tileset/porte.png";
    private final static String JSON_PATH = "/img/tileset/porte.json";

    // SPRITESHEET OGGETTI
    private final static BufferedImage SPRITESHEET = SpriteManager.loadSpriteSheet(SPRITESHEET_PATH);

    // ANIMAZIONI
    private static final StillAnimation OPEN_ANIMATION = createOpenAnimation(); // rendere static
    private static final StillAnimation CLOSE_ANIMATION = createCloseAnimation();

    public Door(String name, String description)
    {
        super("Porta", description, SPRITESHEET, JSON_PATH);
        this.state = CLOSED;
    }

    public void open()
    {
        if(this.state == OPEN)
        {
            return;
        }

        if(this.state == CLOSED)
        {
            this.state = OPEN;
            EventHandler.sendEvent(new ItemInteractionEvent(this, "La porta è aperta", OPEN_ANIMATION));
        }

        // TODO: cambio sprite
        // else // if(this.state == BLOCKED)


    }

    @Override
    public StillAnimation getOpenAnimation()
    {
        return OPEN_ANIMATION;
    }

    @Override
    public StillAnimation getCloseAnimation()
    {
        return CLOSE_ANIMATION;
    }

    private static StillAnimation createOpenAnimation()
    {
        BufferedImage closed = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "closed");
        BufferedImage open1 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open1");
        BufferedImage open2 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open2");
        BufferedImage open3 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open3");

        StillAnimation openAnimation = new StillAnimation( 500, true);
        openAnimation.addFrame(closed);
        openAnimation.addFrame(open1);
        openAnimation.addFrame(open2);
        openAnimation.addFrame(open3);

        return openAnimation;
    }

    private static StillAnimation createCloseAnimation()
    {
        BufferedImage closed = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "closed");
        BufferedImage open1 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open1");
        BufferedImage open2 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open2");
        BufferedImage open3 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open3");

        StillAnimation openAnimation = new StillAnimation( 500, true);
        openAnimation.addFrame(open3);
        openAnimation.addFrame(open2);
        openAnimation.addFrame(open1);
        openAnimation.addFrame(closed);

        return openAnimation;
    }

    @Override
    public boolean isOpen()
    {
        return state == OPEN;
    }

    public void close()
    {
        if(this.state == CLOSED || this.state == BLOCKED)
            return;

        if(this.state == OPEN)
        {
            String result = "La porta è chiusa";
            this.state = CLOSED;

            EventHandler.sendEvent(new ItemInteractionEvent(this, result, CLOSE_ANIMATION));
        }

    }

    public int getState()
    {
        return this.state;
    }

    public void lock()
    {
        this.state = BLOCKED;
    }

    public void unlock(PickupableItem key)
    {
        if (this.state != BLOCKED)
        {
            return;
        }

        if (key.getName().equals("Chiave spicola"))
        {
            this.state = OPEN;

            EventHandler.sendEvent(new ItemInteractionEvent(this, "la porta è sbloccata e aperta"));
        }
        else
        {
            EventHandler.sendEvent(new ItemInteractionEvent(this, "chiave non corretta"));
        }
    }
}
