package entity.items;

import action.ActionSequence;
import animation.StillAnimation;
import events.EventHandler;
import events.ItemInteractionEvent;
import general.GameManager;
import graphics.SpriteManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

public class Door extends Item implements Openable, Lockable
{
    public final static int OPEN = 1;
    public final static int CLOSED = 0;
    public final static int BLOCKED = -1;

    private final Runnable onOpen;

    private int state;

    // PATH SPRITESHEET (png + json)
    private final static String SPRITESHEET_PATH = "/img/tileset/porte.png";
    private final static String JSON_PATH = "/img/tileset/porte.json";

    // SPRITESHEET OGGETTI
    private final static BufferedImage SPRITESHEET = SpriteManager.loadSpriteSheet(SPRITESHEET_PATH);

    // ANIMAZIONI
    private static final List<Image> OPEN_FRAMES;
    private static final List<Image> CLOSE_FRAMES;

    static
    {
        // inizializzazione frames
        Image closed = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "closed");
        Image open1 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open1");
        Image open2 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open2");
        Image open3 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open3");

        OPEN_FRAMES = new ArrayList<>();
        OPEN_FRAMES.add(closed);
        OPEN_FRAMES.add(open1);
        OPEN_FRAMES.add(open2);
        OPEN_FRAMES.add(open3);

        CLOSE_FRAMES = new ArrayList<>();
        CLOSE_FRAMES.add(open3);
        CLOSE_FRAMES.add(open2);
        CLOSE_FRAMES.add(open1);
        CLOSE_FRAMES.add(closed);

    }
    public Door(String name, String description)
    {
        super("Porta", description, SPRITESHEET, JSON_PATH);
        this.state = CLOSED;
        this.onOpen = () ->{};
    }

    public Door(String name, String description, Runnable onOpen)
    {
        super("Porta", description, SPRITESHEET, JSON_PATH);
        this.state = CLOSED;
        this.onOpen = onOpen;
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
            EventHandler.sendEvent(new ItemInteractionEvent(this, "La porta è aperta", OPEN_FRAMES));

            // carica scenario animato
            onOpen.run();

        }

        // TODO: cambio sprite
        // else // if(this.state == BLOCKED)
    }

    //@Override
    //public List<Image> getOpenFrames()
    //{
    //    return OPEN_FRAMES;
    //}

    //@Override
    //public List<Image> getCloseFrames()
    //{
    //    return CLOSE_FRAMES;
    //}


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

            EventHandler.sendEvent(new ItemInteractionEvent(this, result, CLOSE_FRAMES));
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
