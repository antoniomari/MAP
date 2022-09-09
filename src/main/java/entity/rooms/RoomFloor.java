package entity.rooms;

import entity.GamePiece;
import graphics.SpriteManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RoomFloor
{
    private final List<Rectangle> walkableRectangles;
    private final List<Rectangle> obstacleRectangles;

    public RoomFloor()
    {
        walkableRectangles = new ArrayList<>();
        obstacleRectangles = new ArrayList<>();
    }

    // TODO: valutare se  cambiare classe di questo metodo
    public static RoomFloor loadFloorFromJson(String jsonPath)
    {
        JSONObject json = SpriteManager.getJsonFromFile(jsonPath);
        JSONArray rectangleArray = json.getJSONArray("pavimento");

        int roomWidth = json.getInt("width");
        int roomHeight = json.getInt("height");

        RoomFloor floor = new RoomFloor();

        for (int i = 0; i < rectangleArray.length(); i++)
        {
            JSONObject rectangleJson = rectangleArray.getJSONObject(i);
            if(rectangleJson.getInt("width") > roomWidth
                    || rectangleJson.getInt("height") > roomHeight)
                throw new JSONException("Pavimento non valido");

            floor.addWalkableRectangle(rectangleJson.getInt("left"),
                    rectangleJson.getInt("top"),
                    rectangleJson.getInt("width"),
                    rectangleJson.getInt("height"));
        }

        rectangleArray = json.getJSONArray("ostacoli");

        for (int i = 0; i < rectangleArray.length(); i++)
        {
            JSONObject rectangleJson = rectangleArray.getJSONObject(i);
            if(rectangleJson.getInt("width") > roomWidth
                    || rectangleJson.getInt("height") > roomHeight)
                throw new JSONException("Ostacolo non valido");

            floor.addObstacleRectangle(rectangleJson.getInt("left"),
                    rectangleJson.getInt("top"),
                    rectangleJson.getInt("width"),
                    rectangleJson.getInt("height"));
        }

        return floor;
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

    public void addObstacleRectangle(int leftBlock, int topBlock, int width, int height)
    {
        // controlli su parametri
        if(leftBlock < 0 || topBlock < 0 || width <= 0 || height <= 0)
            throw new IllegalArgumentException();
        // TODO: completare controlli

        obstacleRectangles.add(new Rectangle(leftBlock, topBlock, width, height));
    }

    public boolean isWalkable(int xBlock, int yBlock)
    {
        boolean onFloor = false;

        for(Rectangle walkableArea : walkableRectangles)
            if(walkableArea.contains(xBlock, yBlock))
                onFloor = true;

        // caso sul muro
        if(!onFloor)
           return false;

        // se interseca un ostacolo
        if(intersectsObstacle(xBlock, yBlock))
            return false;

        // è sul pavimento e su nessun ostacolo
        return true;
    }

    private boolean intersectsObstacle(int xBlock, int yBlock)
    {
        // controlla ostacoli
        Rectangle feetArea = new Rectangle(xBlock, yBlock, 4, 1); // pedana
        for(Rectangle obstacleArea : obstacleRectangles)
            if(obstacleArea.intersects(feetArea))
                return true;

        return false;
    }

    public boolean isWalkable(BlockPosition pos)
    {
        return isWalkable(pos.getX(), pos.getY());
    }


    public BlockPosition getNearestPlacement(BlockPosition tryPos, GamePiece piece)
    {
        return getNearestPlacement(tryPos, piece.getBWidth(), piece.getBHeight());
    }

    // TODO: aggiustare codice metodo
    public BlockPosition getNearestPlacement(BlockPosition tryPos, int spriteWidth, int spriteHeight)
    {
        if(isWalkable(tryPos))
        {
            return tryPos;
        }

        if(intersectsObstacle(tryPos.getX(), tryPos.getY()))
            return null;

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
