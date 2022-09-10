package entity.rooms;

import entity.GamePiece;
import graphics.SpriteManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta il pavimento di una stanza.
 *
 * Nota: la presenza di ostacoli è solo una bozza per sviluppi futuri,
 * infatti non è presente un algoritmo che calcola un percorso nel movimento
 * per aggirare gli ostacoli.
 */
public class RoomFloor
{
    /** Lista di rettangoli in cui è possibile camminare. */
    private final List<Rectangle> walkableRectangles;
    /** Lista di rettangoli di ostacoli, in cui non è possibile camminare */
    private final List<Rectangle> obstacleRectangles;

    /**
     * Crea un pavimento vuoto.
     */
    public RoomFloor()
    {
        walkableRectangles = new ArrayList<>();
        obstacleRectangles = new ArrayList<>();
    }

    /**
     * Crea un pavimento partendo dal JSON di una stanza.
     *
     * @param jsonPath path del file json di una stanza
     * @return Pavimento corrispondente al contenuto del json
     */
    public static RoomFloor loadFloorFromJson(String jsonPath)
    {
        JSONObject json = SpriteManager.getJsonFromFile(jsonPath);
        JSONArray rectangleArray = json.getJSONArray("pavimento");

        // recupera width e height della stanza
        int roomWidth = json.getInt("width");
        int roomHeight = json.getInt("height");

        RoomFloor floor = new RoomFloor();

        // recupera tutti i rettangoli di pavimento e controlla se rientrano
        // nella stanza, considerandone le dimensioni
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

        // recupera tutti i rettangoli degli ostacoli, controllandone le dimensioni
        // e verificando che rientrino tutti nella stanza
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
    private void addWalkableRectangle(int leftBlock, int topBlock, int width, int height)
    {
        // controlli su parametri
        if(leftBlock < 0 || topBlock < 0 || width <= 0 || height <= 0)
            throw new IllegalArgumentException();

        walkableRectangles.add(new Rectangle(leftBlock, topBlock, width, height));
    }

    private void addObstacleRectangle(int leftBlock, int topBlock, int width, int height)
    {
        // controlli su parametri
        if(leftBlock < 0 || topBlock < 0 || width <= 0 || height <= 0)
            throw new IllegalArgumentException();

        obstacleRectangles.add(new Rectangle(leftBlock, topBlock, width, height));
    }

    /**
     * Restituisce {@code true} se è possibile camminare in posizione
     * {@code (xBlock, yBlock)}, {@code false} altrimenti.
     *
     * @param xBlock ascissa della posizione da controllare
     * @param yBlock ordinata della posizione da controllare
     * @return {@code true} se è possibile camminare in posizione
     * {@code (xBlock, yBlock)}, {@code false} altrimenti
     */
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

    /**
     * Restituisce {@code true} se la posizione
     * {@code (xBlock, yBlock)} interseca un ostacolo,
     * {@code false} altrimenti.
     *
     * @param xBlock ascissa della posizione da controllare
     * @param yBlock ordinata della posizione da controllare
     * @return {@code true} se la posizione
     * {@code (xBlock, yBlock)} interseca un ostacolo,
     * {@code false} altrimenti
     */
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

    /**
     * Restituisce la posizione più vicina a {@code tryPos} in cui è possibile
     * posizionare lo sprite.
     *
     * @param tryPos posizione di riferimento
     * @param spriteWidth larghezza dello sprite da posizionare
     * @param spriteHeight altezza dello sprite da posizionare
     * @return la posizione più vicina a {@code tryPos} in cui è possibile
     * posizionare lo sprite, {@code null} se {@code tryPos} interseca un ostacolo o se
     * non è possibile trovarne una
     */
    public BlockPosition getNearestPlacement(BlockPosition tryPos, int spriteWidth, int spriteHeight)
    {
        // se puoi camminare allora va bene tryPos
        if(isWalkable(tryPos))
        {
            return tryPos;
        }

        // caso intersezione ostacolo
        if(intersectsObstacle(tryPos.getX(), tryPos.getY()))
            return null;

        for(Rectangle walkableArea : walkableRectangles)
        {
            int leftBorder = (int) walkableArea.getX();
            int rightBorder = leftBorder + (int) walkableArea.getWidth() - 1;
            int topBorder = (int) walkableArea.getY();

            int finalX = tryPos.getX();
            int finalY = -1; // valore sentinella per riconoscere il fatto di non aver trovato una posizione


            // controllo bordo sopra (se tryPos è su un blocco immediatamente sopra al pavimento)
            if(tryPos.getY() == topBorder - 1 && tryPos.getY() - spriteHeight >= 0)
                finalY = tryPos.getY() + 1;
            else if (tryPos.getY() >= topBorder)
                finalY = tryPos.getY();

            // controllo bordo laterale (se tryPos è vicina ai bordi laterali)
             if(tryPos.getX() < leftBorder)
                 finalX = leftBorder;
             else if (tryPos.getX() + spriteWidth - 1 > rightBorder)
                 finalX = rightBorder - spriteWidth + 1;

             if(finalY == -1)
                 return null;

             return new BlockPosition(finalX, finalY);
        }
        return null;
    }
}
