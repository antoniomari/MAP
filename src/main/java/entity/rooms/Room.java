package entity.rooms;

import entity.GamePiece;
import entity.characters.PlayingCharacter;
import entity.items.Item;
import events.EventHandler;
import events.RoomEvent;
import general.GameException;
import general.GameManager;
import general.xml.XmlParser;
import graphics.SpriteManager;
import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe che rappresenta una stanza del gioco.
 */
public class Room
{
    /** Nome della stanza. */
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
    private final String musicPath;
    /** Immagine di background della stanza. */
    private final BufferedImage backgroundImage;


    /** Dizionario contenente tutti i GamePiece presenti nella stanza, assieme alle loro posizioni (in blocchi). */
    private final Map<GamePiece, BlockPosition> pieceLocationMap;

    /** Pavimento della stanza, viene caricato dal JSON della stanza. */
    private final RoomFloor floor;
    /** Larghezza in blocchi. */
    private final int bWidth;
    /** Altezza in blocchi. */
    private final int bHeight;  // altezza in blocchi

    /** Posizione di default della stanza. */
    private final BlockPosition defaultPosition;

    /**
     * Punto cardinale.
     */
    public enum Cardinal
    {
        NORTH,
        WEST,
        EAST,
        SOUTH;

        /** Punto cardinale opposto. */
        private Cardinal opposite;

        /**
         * Restituisce il punto cardinale corrispondente
         * alla stringa (case insensitive).
         *
         * @param cardinal punto cardinale sotto forma di stringa
         * @return punto cardinale corrispondente
         */
        public static Cardinal fromString(String cardinal)
        {
            // ignore spaces
            cardinal = cardinal.trim();

            for(Cardinal c : Cardinal.values())
                if(cardinal.equalsIgnoreCase(String.valueOf(c)))
                    return c;
            throw new IllegalArgumentException("Punto cardinale inesistente: " + cardinal);
        }

        static
        {
            NORTH.setOpposite(SOUTH);
            SOUTH.setOpposite(NORTH);
            WEST.setOpposite(EAST);
            EAST.setOpposite(WEST);
        }

        /**
         * Imposta il punto cardinale opposto.
         *
         * @param opposite punto cardinale opposto
         */
        private void setOpposite(Cardinal opposite)
        {
            this.opposite = opposite;
        }

        /**
         * Restituisce il punto cardinale opposto.
         *
         * @return punto cardinale opposto
         */
        public Cardinal getOpposite()
        {
            return opposite;
        }
    }

    /**
     * Classe che rappresenta l'entrata di una stanza.
     */
    private static class Entrance
    {
        /** Stanza alla quale porta l'entrata. */
        private Room adjacentRoom;
        /** Flag che indica se l'entrata è chiusa. */
        private boolean isLocked;
        /** Posizione della freccia relativa all'entrata (nel background della stanza). */
        private final BlockPosition arrowPosition;

        Entrance(BlockPosition arrowPosition)
        {
            this.arrowPosition = arrowPosition;
        }
    }

    public BlockPosition getDefaultPosition()
    {
        return defaultPosition;
    }


    /**
     * Crea una Room.
     *
     * @param name nome da assegnare alla Room
     * @param path path dell'immagine di background
     * @param jsonPath path del json della Room
     * @param musicPath path della musica di sottofondo
     */
    public Room(String name, String path, String jsonPath, String musicPath)
    {
        this.roomName = name;
        pieceLocationMap = new HashMap<>();

        this.musicPath = musicPath;
        backgroundImage = SpriteManager.loadImage(path);
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
            String tag = cardinal.toString().toLowerCase(Locale.ROOT) + "Arrow";

            if(json.has(tag))
            {
                JSONObject cardinalJson = json.getJSONObject(tag);
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
        return musicPath;
    }

    public BufferedImage getBackgroundImage()
    {
        return backgroundImage;
    }

    public void setScenarioOnEnter(String scenarioPath)
    {
        this.scenarioOnEnterPath = scenarioPath;
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
                .filter(e -> adjacentRoom.equals(e.getValue().adjacentRoom))
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

        Entrance entrance = entranceMap.get(cardinal);

        return entrance == null? null : entrance.adjacentRoom;
    }

    /**
     * Imposta la stanza collegata al punto cardinale specificato rispetto
     * a this.
     *
     * @param cardinal punto cardinale a cui room dev'essere collegata rispetto a this
     * @param room stanza da collegare
     */
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

    /**
     * Imposta il blocco di un'entrata della stanza.
     *
     * @param cardinal punto cardinale dell'entrata
     * @param locked {@code true} per bloccare l'entrata, {@code false} per sbloccarla
     */
    public void setAdjacentLocked(Cardinal cardinal, boolean locked)
    {
        Objects.requireNonNull(cardinal);

        entranceMap.get(cardinal).isLocked = locked;
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

    /**
     * Aggiunge un GamePiece nella stanza.
     *
     * @param p GamePiece da aggiungere
     * @param pos posizione alla quale aggiungere il GamePiece
     */
    public void addPiece(GamePiece p, BlockPosition pos)
    {
        Objects.requireNonNull(p);

        if(pieceLocationMap.containsKey(p))
            throw new GameException(p + " già presente in " + this);

        if(pos == null)
            pieceLocationMap.put(p, null);
        else
            safePieceInsert(p, pos);

        // Evento dalla prospettiva della stanza: Un GamePiece è stato aggiunto alla stanza

        EventHandler.sendEvent(new RoomEvent(this, p, pos, RoomEvent.Type.ADD_PIECE_IN_ROOM));
    }

    /**
     * Inizia lo scenarioOnEnter della stanza, se presente; altrimenti
     * continua lo scenario corrente.
     *
     * Viene invocato nello scenario di movimento che fa cambiare stanza,
     * per cui è necessario mandare avanti lo scenario corrente se non vi è
     * alcun scenarioOnEnter da eseguire.
     */
    public void startScenarioOnEnter()
    {
        if(scenarioOnEnterPath != null)
        {
            GameManager.startScenario(XmlParser.loadScenario(scenarioOnEnterPath));
            scenarioOnEnterPath = null;
        }
        else
        {
            GameManager.continueScenario();
        }
    }

    /**
     * Effettua controllo sulla posizione d'inserimento del GamePiece.
     *
     * @param p GamePiece da posizionare
     * @param pos posizione d'inserimento
     */
    private void safePieceInsert(GamePiece p, BlockPosition pos)
    {
        if(canGo(p, pos) || !(p instanceof PlayingCharacter))
            pieceLocationMap.put(p, pos);
        else
            throw new GameException(p + " non può entrare in " + this + " alla posizione " + pos);
    }


    /**
     * Restituisce {@code true} se il GamePiece può essere posizionato in posizione pos.
     *
     * @param p GamePiece che si sta provando a posizionare
     * @param pos posizione alla quale si vuole posizionare il GamePiece
     * @return {@code true} se il GamePiece può essere posizionato in posizione pos,
     * {@code false} altrimenti.
     */
    private boolean canGo(GamePiece p, BlockPosition pos)
    {
        boolean fit = canFit(p, pos);

        if(p instanceof Item)
            return fit;
        else
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
        Objects.requireNonNull(p);
        Objects.requireNonNull(pos);

        Rectangle roomRect = new Rectangle(0, 0, bWidth, bHeight);

        Rectangle pieceRect = new Rectangle(pos.getX(), pos.getY() - p.getBHeight(), p.getBWidth(), p.getBHeight());

        return roomRect.contains(pieceRect);

    }


    /**
     * Rimuove un GamePiece dalla stanza.
     *
     * @param p GamePiece da rimuovere
     */
    public void removePiece(GamePiece p)
    {
        Objects.requireNonNull(p);

        pieceLocationMap.remove(p);
        EventHandler.sendEvent(new RoomEvent(this, p, RoomEvent.Type.REMOVE_PIECE_FROM_ROOM));
    }


    /**
     * Restituisce la posizione di un GamePiece presente nella stanza.
     *
     * @param p GamePiece di cui restituire la posizione
     * @return posizione del GamePiece nella stanza
     */
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

    /**
     * Restituisce la posizione della freccia (background della stanza)
     * relativa all'entrata della stanza in un punto cardinale.
     *
     * @param cardinal punto cardinale dell'entrata
     * @return posizione in blocchi della freccia
     */
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

}
