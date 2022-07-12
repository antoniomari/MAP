package rooms;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RoomFloor
{
    List<Rectangle> walkableRectangles;

    public RoomFloor()
    {
        walkableRectangles = new ArrayList<>();
    }

    // top left corner
    public void addWalkableRectangle(int leftBlock, int topBlock, int width, int height)
    {
        // controlli su parametri
        if(leftBlock < 0 || topBlock < 0 || width <= 0 || height <= 0)
            throw new IllegalArgumentException();
        // TODO: completare controlli

        walkableRectangles.add(new Rectangle(leftBlock, topBlock, width, height));
    }

    public boolean isWalkable(int xBlock, int yBlock)
    {
        for(Rectangle walkableArea : walkableRectangles)
            if(walkableArea.contains(xBlock, yBlock))
                return true;

        return false;
    }
}
