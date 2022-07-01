package items;

import events.EventHandler;
import events.GameEvent;

public class Door extends Item implements Openable
{
    public static int OPEN = 1;
    public static int CLOSED = 0;
    public static int BLOCKED = -1;

    private int state;

    public Door(int state)
    {
        super("Una porta strana");
        this.state = state;
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

        EventHandler.printEvent(new GameEvent(result));


    }

    public void close()
    {
        if(this.state == CLOSED || this.state == BLOCKED)
            return;

        if(this.state == OPEN)
        {
            String result = "La porta è chiusa";
            this.state = CLOSED;

            EventHandler.printEvent(new GameEvent(result));
        }

    }

    public int getState()
    {
        return this.state;
    }
}
