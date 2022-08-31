package entity.rooms;

import entity.GamePiece;
import entity.characters.PlayingCharacter;
import entity.items.Item;
import events.EventHandler;
import events.RoomEvent;
import general.ActionSequence;
import general.GameException;
import general.GameManager;
import general.ScenarioMethod;
import general.xml.XmlParser;
import graphics.SpriteManager;
import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Room
{
    public enum Cardinal
    {
        NORTH
                {
                    @Override
                    public String toString()
                    {
                        return "north";
                    }
                },
        WEST
                {
                    @Override
                    public String toString()
                    {
                        return "west";
                    }
                },
        EAST
                {
                    @Override
                    public String toString()
                    {
                        return "east";
                    }
                },
        SOUTH
                {
                    @Override
                    public String toString()
                    {
                        return "south";
                    }
                };

        private Cardinal opposite;

        static
        {
            NORTH.setOpposite(SOUTH);
            SOUTH.setOpposite(NORTH);
            WEST.setOpposite(EAST);
            EAST.setOpposite(WEST);
        }

        private void setOpposite(Cardinal opposite)
        {
            this.opposite = opposite;
        }

        public Cardinal getOpposite()
        {
            return opposite;
        }
    }

    private static class Entrance
    {
        private Room adjacentRoom;
        private boolean isLocked;
        private BlockPosition arrowPosition;

        Entrance(BlockPosition arrowPosition)
        {
            this.arrowPosition = arrowPosition;
        }
    }


    private final String roomName;


    /** Dizionario che contiene la posizione delle frecce per il cambio stanza
     * per i punti cardinali disponibili a seconda della stanza.
     * Queste informazioni vengono caricate sulla base del JSON della stanza. */

    private final Map<Cardinal, Entrance> entranceMap = new HashMap<>(Cardinal.values().length);

    /** Path dell'xml della stanza. */
    private String xmlPath;
    /** Path dello scenario da eseguire nel momento in cui si entra nella stanza. */
    private String scenarioOnEnterPath;


    /** Path della musica della stanza. Viene caricato dall'XML della stanza. */
    private final String MUSIC_PATH;
    /** Path dell'immagine di background della stanza. Viene caricato dall'XML della stanza. */
    private final String BACKGROUND_PATH;
    /** Immagine di background della stanza. */
    private BufferedImage backgroundImage;


    /** Dizionario contenente tutti i GamePiece presenti nella stanza, assieme alle loro posizioni (in blocchi). */
    private final Map<GamePiece, BlockPosition> pieceLocationMap;

    /** Pavimento della stanza, viene caricato dal JSON della stanza. */
    private final RoomFloor floor;

    private final int bWidth;  // larghezza in blocchi
    private final int bHeight;  // altezza in blocchi

    // posizione di default all'entrata del protagonista
    private final BlockPosition defaultPosition;


    public Room(String name, String path, String jsonPath, String MUSIC_PATH)
    {
        this.roomName = name;
        pieceLocationMap = new HashMap<>();

        this.MUSIC_PATH = MUSIC_PATH;
        BACKGROUND_PATH = path;
        backgroundImage = SpriteManager.loadSpriteSheet(BACKGROUND_PATH);
        floor = RoomFloor.loadFloorFromJson(jsonPath);
        JSONObject json = SpriteManager.getJsonFromFile(jsonPath);

        bWidth = json.getInt("width");
        bHeight = json.getInt("height");

        JSONObject defaultJson = json.getJSONObject("default position");
        int xDefault = defaultJson.getInt("x");
        int yDefault = defaultJson.getInt("y");

        defaultPosition = new BlockPosition(xDefault, yDefault);


        // recupera posizione delle frecce
        for(Cardinal cardinal : Cardinal.values())
        {
            if(json.has(cardinal + "Arrow"))
            {
                JSONObject cardinalJson = json.getJSONObject(cardinal + "Arrow");
                entranceMap.put(cardinal,
                        new Entrance(new BlockPosition(cardinalJson.getInt("x"), cardinalJson.getInt("y"))));
            }
        }

        GameManager.addRoom(this);
    }

    public String getXmlPath()
    {
        return xmlPath;
    }

    public void setXmlPath(String xmlPath)
    {
        this.xmlPath = xmlPath;
    }

    public String getScenarioOnEnterPath()
    {
        return scenarioOnEnterPath;
    }

    public String getMusicPath()
    {
        return MUSIC_PATH;
    }

    @ScenarioMethod
    public void setScenarioOnEnter(String scenarioPath)
    {
        this.scenarioOnEnterPath = scenarioPath;

        GameManager.continueScenario();
    }


    /**
     * Restituisce la direzione verso la quale la stanza {@code this} è
     * collegata alla stanza {@code adjacentRoom}, {@code null} se non è collegata
     * direttamente.
     *
     * @param adjacentRoom stanza collegata a this
     *
     * @return direzione di collegamento o {@code null} se non c'è collegamento diretto
     */
    public Cardinal getAdjacentDirection(Room adjacentRoom)
    {

        List<Map.Entry<Cardinal, Entrance>> list =  entranceMap
                .entrySet().stream()
                .filter(e -> e.getValue().adjacentRoom.equals(adjacentRoom))
                .collect(Collectors.toList());

        if(list.size() != 0)
            return list.get(0).getKey();
        else
            return null;
    }

    /**
     * Restituisce la stanza collegata al punto cardinale specificato
     * rispetto a this.
     *
     * @param cardinal punto cardinale della stanza adiacente rispetto a this
     * @return la stanza collegata, {@code null} se this non ha alcuna stanza collegata
     * nella direzione di tale punto cardinale
     */
    public Room getAdjacentRoom(Cardinal cardinal)
    {
        Objects.requireNonNull(cardinal);

        return entranceMap.get(cardinal).adjacentRoom;
    }

    public void setAdjacentRoom(Cardinal cardinal, Room room)
    {
        Objects.requireNonNull(cardinal);

        entranceMap.get(cardinal).adjacentRoom = room;

        Cardinal opposite = cardinal.opposite;

        if(room.getAdjacentRoom(opposite) == null || !this.equals(room.getAdjacentRoom(opposite)))
            room.setAdjacentRoom(opposite, this);
    }

    /**
     * Restituisce {@code true} se l'accesso alla stanza adiacente a this nella direzione di
     * {@code cardinal} è bloccato, false altrimenti.
     *
     * @param cardinal direzione verso cui controllare il blocco dell'accesso.
     * @return {@code true} se l'accesso alla stanza adiacente è bloccato, {@code false}
     * se l'accesso non è bloccato o se non vi è alcuna stanza adiacente nella direzione {@code cardinal}.
     *
     */
    public boolean isAdjacentLocked(Cardinal cardinal)
    {
        Objects.requireNonNull(cardinal);

        return entranceMap.containsKey(cardinal) && entranceMap.get(cardinal).isLocked;
    }

    @ScenarioMethod
    public void setAdjacentLocked(Cardinal cardinal, boolean locked)
    {
        Objects.requireNonNull(cardinal);

        entranceMap.get(cardinal).isLocked = locked;

        GameManager.continueScenario();
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


    public BlockPosition getInitialPlayerPosition()
    {
        return defaultPosition;
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

        // Esegui scenario on enter se è entrato il giocatore
        if(p.equals(PlayingCharacter.getPlayer()) && scenarioOnEnterPath != null)
        {
            GameManager.startScenario(XmlParser.loadScenario(scenarioOnEnterPath));
            scenarioOnEnterPath = null;
        }

    }

    private void safePieceInsert(GamePiece p, BlockPosition pos)
    {
        if(canGo(p, pos) || !(p instanceof PlayingCharacter))
            pieceLocationMap.put(p, pos);
        else
            throw new GameException(p + " non può entrare in " + this + " alla posizione " + pos);
    }


    private boolean canGo(GamePiece p, BlockPosition pos)
    {
        // TODO: controllare
        boolean fit = canFit(p, pos);

        if(p instanceof Item)
            return fit;
        else // if(p instanceof GameCharacter)
        {
            BlockPosition nearest = floor.getNearestPlacement(pos, p.getBWidth(), p.getBHeight());

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
        // TODO: controllare

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

    /**
     * Restituisce la lista di tutti i GamePiece presenti nella stanza.
     *
     * Se si combina con getPiecePosition è possibile recuperare tutte le
     * informazioni con GamePiece.
     *
     * @return lista dei GamePiece presenti nella stanza
     */
    public List<GamePiece> getPiecesPresent()
    {
        return new ArrayList<>(pieceLocationMap.keySet());
    }

    public void setPiecePosition(GamePiece p, BlockPosition pos)
    {
        if (!pieceLocationMap.containsKey(p))
            throw new GameException(p + " non presente in " + this);

        safePieceInsert(p, pos);

    }

    public BlockPosition getArrowPosition(Cardinal cardinal)
    {
        Objects.requireNonNull(cardinal);

        if(!entranceMap.containsKey(cardinal))
        {
            throw new GameException("Punto cardinale non valido");
        }
        else
        {
            return entranceMap.get(cardinal).arrowPosition;
        }
    }


    public BufferedImage getBackgroundImage()
    {
        return backgroundImage;
    }

}
