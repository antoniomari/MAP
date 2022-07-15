package rooms;

import characters.GameCharacter;
import characters.GamePiece;
import graphics.SpriteManager;
import items.Item;
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

    private final int width;  // larghezza in blocchi
    private final int height;  // altezza in blocchi


    public Room(String name, String path, String jsonPath)
    {
        this.roomName = name;
        pieceLocationMap = new HashMap<>();
        backgroundPath = path;
        loadBackgroundImage();
        floor = SpriteManager.loadFloorFromJson(jsonPath);
        JSONObject json = SpriteManager.getJsonFromFile(jsonPath);

        width = json.getInt("width");
        height = json.getInt("height");


    }

    // restituisce la larghezza misurata in numero di blocchi
    public int getBWidth()
    {
        return width;
    }

    // restituisce l'altezza misurata in numero di blocchi
    public int getBHeight()
    {
        return height;
    }

    public RoomFloor getFloor()
    {
        return floor;
    }

    public void addItem(Item item, BlockPosition c)
    {
        pieceLocationMap.put(item, c);

        // genera evento

    }

    public void removeItem(Item item)
    {
        pieceLocationMap.remove(item);
    }

    public void addCharacter(GameCharacter ch, BlockPosition pos)
    {
        pieceLocationMap.put(ch, pos);
    }

    public void removeCharacter(Character ch)
    {
        pieceLocationMap.remove(ch);
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
        {
            System.out.println(entry.getKey().getName() + " in posizione " + entry.getValue());
        }
    }

}
