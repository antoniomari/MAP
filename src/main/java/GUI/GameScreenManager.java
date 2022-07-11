package GUI;

import rooms.Coordinates;


public class GameScreenManager
{

    private GameScreenManager()
    {
    }

    // TODO: calcolare massimi xBlock e yBlock per la stanza
    public static Coordinates calculateCoordinates(int xBlocks, int yBlocks, double rescalingFactor)
    {
        if(xBlocks < 0 || yBlocks < 0)
            throw new IllegalArgumentException();

        final int BLOCK_SIZE = 48;
        int xOffset = (int)(xBlocks * BLOCK_SIZE * rescalingFactor);
        int yOffset = (int) (yBlocks * BLOCK_SIZE * rescalingFactor) + 3; // TODO: controllare

        return new Coordinates(xOffset, yOffset);
    }


    public static Coordinates calculateBlocks(Coordinates coord, double rescalingFactor)
    {
        final int BLOCK_SIZE = 48;
        int xBlocks = (int)(coord.getX() / (BLOCK_SIZE * rescalingFactor));
        int yBlocks = (int)(coord.getY() / (BLOCK_SIZE * rescalingFactor));

        return new Coordinates(xBlocks, yBlocks);
    }
}
