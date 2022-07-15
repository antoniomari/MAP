package entity.rooms;

public class BlockPosition
{
    private int x;
    private int y;

    public BlockPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public BlockPosition relativePosition(int offsetX, int offsetY)
    {
        return new BlockPosition(this.getX() + offsetX, this.getY() + offsetY);
    }

    @Override
    public String toString()
    {
        return "[" + "(Block) x=" + x + ", y=" + y + "]";
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
