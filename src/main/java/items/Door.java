package items;

import animation.Animation;
import events.EventHandler;
import events.ItemInteractionEvent;
import graphics.SpriteManager;

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
    private final Animation OPEN_ANIMATION = createOpenAnimation(); // rendere static

    public Door(String name, int state)
    {
        super("Porta", "Una porta strana", SPRITESHEET, JSON_PATH);
        this.state = state;
    }

    public Door(String name, String description)
    {
        super("Porta", description, SPRITESHEET, JSON_PATH);
        this.state = CLOSED;
    }
    public void open()
    {
        if(this.state == OPEN)
            return;

        String result;
        if(this.state == CLOSED)
        {
            result = "La porta è aperta";
            this.state = OPEN;
            // TODO: aggiorna sprite
        }
        else // if(this.state == BLOCKED)
            result = "La porta è bloccata, non si può aprire";

        EventHandler.printEvent(new ItemInteractionEvent(this, result, OPEN_ANIMATION));

    }

    public void close()
    {
        if(this.state == CLOSED || this.state == BLOCKED)
            return;

        if(this.state == OPEN)
        {
            String result = "La porta è chiusa";
            this.state = CLOSED;

            EventHandler.printEvent(new ItemInteractionEvent(this, result));
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

            EventHandler.printEvent(new ItemInteractionEvent(this, "la porta è sbloccata e aperta"));
        }
        else
        {
            EventHandler.printEvent(new ItemInteractionEvent(this, "chiave non corretta"));
        }
    }

    private Animation createOpenAnimation()
    {
        BufferedImage closed = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "closed");
        BufferedImage open1 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open1");
        BufferedImage open2 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open2");
        BufferedImage open3 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open3");

        Animation openAnimation = new Animation(this, 500);
        openAnimation.addFrame(closed);
        openAnimation.addFrame(open1);
        openAnimation.addFrame(open2);
        openAnimation.addFrame(open3);

        return openAnimation;

    }
}















