package GUI;

public class AbsPosition
{
    private int x;
    private int y;

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
}
