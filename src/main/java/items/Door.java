package items;

import events.EventHandler;
import events.GameEvent;
import events.ItemInteractionEvent;

public class Door extends Item implements Openable, Lockable
{
    public static int OPEN = 1;
    public static int CLOSED = 0;
    public static int BLOCKED = -1;

    private int state;

    public Door(String name, int state)
    {
        super(name, "Una porta strana");
        this.state = state;
    }

    public Door(String name, String description)
    {
        super(name, description);
        this.state = BLOCKED;
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
}
