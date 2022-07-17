package GUI;

public class AbsPosition
{
    private final int x;
    private final int y;

    public AbsPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString()
    {
        return "[" + "(Absolute Position) x=" + x + ", y=" + y + "]";
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(o instanceof AbsPosition)
        {
            return x == ((AbsPosition) o).getX() && y == ((AbsPosition) o).getY();
        }

        return false;
    }
}
