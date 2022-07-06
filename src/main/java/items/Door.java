package items;

import events.EventHandler;
import events.ItemInteractionEvent;

import java.awt.image.BufferedImage;

public class Door extends Item implements Openable, Lockable
{
    public static int OPEN = 1;
    public static int CLOSED = 0;
    public static int BLOCKED = -1;

    private int state;

    private final static String OBJECT_SPRITESHEET_PATH = "/img/tileset/porte.png";
    // PATH JSON RELATIVO AL TILESET
    private final static String JSON_PATH = "/img/tileset/porte.json";

    // SPRITESHEET OGGETTI
    private final static BufferedImage SPRITESHEET;

    static
    {
        SPRITESHEET = loadSpriteSheet(OBJECT_SPRITESHEET_PATH);
    }

    public Door(String name, int state)
    {
        super(name, "Una porta strana", SPRITESHEET, JSON_PATH);
        this.state = state;
    }

    public Door(String name, String description)
    {
        super(name, description, SPRITESHEET, JSON_PATH);
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
            try
            {
                openAnimation();
            }
            catch (InterruptedException e)
            {
               //vaffanculoScemo
            }

        }
        else // if(this.state == BLOCKED)
            result = "La porta è bloccata, non si può aprire";

        EventHandler.printEvent(new ItemInteractionEvent(this, result));

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

    private void openAnimation() throws InterruptedException
    {
        /*
        BufferedImage closed = loadSpriteByName(SPRITESHEET, JSON_PATH, "closed");
        BufferedImage open1 = loadSpriteByName(SPRITESHEET, JSON_PATH, "open1");
        BufferedImage open2 = loadSpriteByName(SPRITESHEET, JSON_PATH, "open2");
        BufferedImage open3 = loadSpriteByName(SPRITESHEET, JSON_PATH, "open3");

        Timer timer = new Timer(1000, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (sprite.equals(closed))
                {
                    sprite = open1;
                } else if (sprite.equals(open1))
                {
                    sprite = open2;
                } else if (sprite.equals(open2))
                {
                    sprite = open3;
                } else if (sprite.equals(open3))
                {
                    sprite = closed;
                }
            }
        });
        timer.start();
        */
        BufferedImage open3 = loadSpriteByName(SPRITESHEET, JSON_PATH, "open3");
        sprite = open3;
        spriteChanged = true;

    }
}















