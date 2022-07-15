package rooms;

import characters.GamePiece;
import graphics.SpriteManager;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Room
{
    private final String roomName;
    private Room north;
    private Room south;
    private Room west;
    private Room east;

    private final String backgroundPath;
    private BufferedImage backgroundImage;

    private final Map<GamePiece, BlockPosition> pieceLocationMap;
    private final RoomFloor floor;

    private final int bWidth;  // larghezza in blocchi
    private final int bHeight;  // altezza in blocchi


    public Room(String name, String path, String jsonPath)
    {
        this.roomName = name;
        pieceLocationMap = new HashMap<>();
        backgroundPath = path;
        loadBackgroundImage();
        floor = SpriteManager.loadFloorFromJson(jsonPath);
        JSONObject json = SpriteManager.getJsonFromFile(jsonPath);

        bWidth = json.getInt("width");
        bHeight = json.getInt("height");
    }

    // restituisce la larghezza misurata in numero di blocchi
    public int getBWidth()
    {
        return bWidth;
    }

    // restituisce l'altezza misurata in numero di blocchi
    public int getBHeight()
    {
        return bHeight;
    }

    public RoomFloor getFloor()
    {
        return floor;
    }


    public void addPiece(GamePiece p, BlockPosition c)
    {
        pieceLocationMap.put(p, c);

        // genera evento

    }

    public void removePiece(GamePiece p)
    {
        pieceLocationMap.remove(p);
    }


    public BlockPosition getPiecePosition(GamePiece p)
    {
        if(!pieceLocationMap.containsKey(p))
            throw new IllegalArgumentException("Elemento non presente nella stanza");

        return pieceLocationMap.get(p);
    }

    public void setPiecePosition(GamePiece p, BlockPosition pos)
    {
        if (!pieceLocationMap.containsKey(p))
            throw new IllegalArgumentException("Elemento non presente nella stanza");

        pieceLocationMap.put(p, pos);
    }

    private void loadBackgroundImage()
    {
        backgroundImage = SpriteManager.loadSpriteSheet(backgroundPath);
    }

    public BufferedImage getBackgroundImage()
    {
        return backgroundImage;
    }

    //TODO: rimuovere, solo stampa di prova
    public void printPieces()
    {
        for (Map.Entry<GamePiece, BlockPosition> entry : pieceLocationMap.entrySet())
            System.out.println(entry.getKey().getName() + " in posizione " + entry.getValue());
    }

}
