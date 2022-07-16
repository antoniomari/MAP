package entity.rooms;

import GUI.debug.MotionDebugger;

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

    public boolean isWalkable(BlockPosition pos)
    {
        return isWalkable(pos.getX(), pos.getY());
    }


    // TODO: aggiustare codice metodo
    public BlockPosition getNearestPlacement(BlockPosition tryPos, int spriteWidth, int spriteHeight)
    {

        if(isWalkable(tryPos) && isWalkable(tryPos.relativePosition(spriteWidth - 1, 0)))
        {
            return tryPos;
        }

        for(Rectangle walkableArea : walkableRectangles)
        {
            int leftBorder = (int) walkableArea.getX();
            int rightBorder = leftBorder + (int) walkableArea.getWidth() - 1;
            int topBorder = (int) walkableArea.getY();

            int finalX = tryPos.getX();
            int finalY = -1;


            // controllo bordo sopra
            if(tryPos.getY() == topBorder - 1 && tryPos.getY() - spriteHeight >= 0)
                finalY = tryPos.getY() + 1;
            else if (tryPos.getY() >= topBorder)
                finalY = tryPos.getY();

            // controllo bordo laterale
             if(tryPos.getX() < leftBorder)
                 finalX = leftBorder;
             else if (tryPos.getX() + spriteWidth - 1 > rightBorder)
                 finalX = rightBorder - spriteWidth + 1;

             // TODO: aggiustare
             if(finalY == -1)
                 return null;

             return new BlockPosition(finalX, finalY);
        }

        // TODO: attenzione qua
        return null;
    }
}
