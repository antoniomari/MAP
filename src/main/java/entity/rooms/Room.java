package entity.rooms;

import entity.GamePiece;
import entity.characters.GameCharacter;
import entity.items.Item;
import events.EventHandler;
import events.RoomEvent;
import general.GameException;
import general.GameManager;
import graphics.SpriteManager;
import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

        GameManager.addRoom(this);
    }

    public String toString()
    {
        return roomName;
    }

    public String getName()
    {
        return roomName;
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

    // TODO: aggiungere controllo sul pavimento
    public void addPiece(GamePiece p, BlockPosition pos)
    {
        Objects.requireNonNull(p);

        if(pieceLocationMap.containsKey(p))
            throw new GameException(p + " già presente in " + this);

        if(pos == null)
            pieceLocationMap.put(p, pos);
        else
            safePieceInsert(p, pos);

        // Evento dalla prospettiva della stanza: Un GamePiece è stato aggiunto alla stanza
        EventHandler.sendEvent(new RoomEvent(this, p, pos, RoomEvent.Type.ADD_PIECE_IN_ROOM));


    }

    private void safePieceInsert(GamePiece p, BlockPosition pos)
    {
        if(canGo(p, pos))
            pieceLocationMap.put(p, pos);
        else
            throw new GameException(p + " non può entrare in " + this + " alla posizione " + pos);
    }


    private boolean canGo(GamePiece p, BlockPosition pos)
    {
        boolean fit = canFit(p, pos);

        System.out.println("CanFit: " + fit);

        if(p instanceof Item)
            return fit;
        else // if(p instanceof GameCharacter)
        {
            BlockPosition nearest = floor.getNearestPlacement(pos, p.getBWidth(), p.getBHeight());

            System.out.println("Nearest: " + nearest);

            if(nearest == null)
                return false;
            else
                return fit;
        }
    }

    /**
     * Controlla se il pezzo {@code p} può essere stampato interamente in this Room
     * alla posizione pos
     *
     * @param p GamePiece del quale si esegue il controllo
     * @param pos coordinate del blocco più in basso a sinistra di {@code p}
     * @return {@code true} se il pezzo può essere stampato interamente,
     * {@code false} altrimenti
     */
    private boolean canFit(GamePiece p, BlockPosition pos)
    {
        Objects.requireNonNull(p);
        Objects.requireNonNull(pos);

        Rectangle roomRect = new Rectangle(0, 0, bWidth, bHeight);

        Rectangle pieceRect = new Rectangle(pos.getX(), pos.getY() - p.getBHeight(), p.getBWidth(), p.getBHeight());

        return roomRect.contains(pieceRect);

    }


    public void removePiece(GamePiece p)
    {
        Objects.requireNonNull(p);

        pieceLocationMap.remove(p);
        EventHandler.sendEvent(new RoomEvent(this, p, RoomEvent.Type.REMOVE_PIECE_FROM_ROOM));
    }


    public BlockPosition getPiecePosition(GamePiece p)
    {
        if(!pieceLocationMap.containsKey(p))
            throw new GameException(p + " non presente in " + this);

        return pieceLocationMap.get(p);
    }

    public void setPiecePosition(GamePiece p, BlockPosition pos)
    {
        if (!pieceLocationMap.containsKey(p))
            throw new GameException(p + " non presente in " + this);

        safePieceInsert(p, pos);

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
