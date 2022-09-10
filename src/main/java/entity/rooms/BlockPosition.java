package entity.rooms;

/**
 * Classe che rappresenta una posizione, misurata in blocchi.
 */
public class BlockPosition
{
    /** Ascissa. */
    private final int x;
    /** Ordinata. */
    private final int y;

    /**
     * Crea un oggetto BlockPosition.
     *
     * @param x ascissa
     * @param y ordinata
     */
    public BlockPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Restituisce una BlockPosition le cui coordinate
     * {@code offsetX} e {@code offsetY} sono calcolate
     * relativamente alle coordinate di this.
     *
     * @param offsetX ascissa
     * @param offsetY ordinata
     * @return BlockPosition con coordinate relative rispetto
     * a this
     */
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

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof BlockPosition)
            return x == ((BlockPosition) o).x && y == ((BlockPosition) o).y;
        else
            return false;
    }
}
