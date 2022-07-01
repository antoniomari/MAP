package items;

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
        if(this.state == CLOSED)
            this.state = OPEN;
    }

    public void close()
    {
        if(this.state == OPEN)
            this.state = CLOSED;
    }

    public int getState()
    {
        return this.state;
    }
}
