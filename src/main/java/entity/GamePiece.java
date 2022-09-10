package entity;

import entity.characters.GameCharacter;
import events.EventHandler;
import events.GamePieceEvent;
import general.GameError;
import general.GameException;
import general.GameManager;
import general.Pair;
import graphics.SpriteManager;
import entity.items.Item;
import entity.rooms.BlockPosition;
import entity.rooms.Room;

import javax.swing.Icon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta un qualsiasi elemento fisico del gioco, ossia che ha uno sprite
 * e può occupare una posizione (e ha un'estensione) in una stanza.
 *
 * Viene utilizzata come classe base sia per i personaggi {@link GameCharacter} che per gli
 * oggetti {@link Item}
 */
public abstract class GamePiece
{

    private final String name;

    /** Stato utilizzato per associare il comportamento del GamePiece. */
    protected String state = "init";
    protected final Image sprite;

    protected int bWidth;  // larghezza in blocchi dell'elemento
    protected int bHeight;  // altezza in blocchi dell'elemento

    private Room locationRoom;  // stanza in cui è contenuto

    // Per bufferizzazione sprite
    private double scalingFactor;
    private Icon scaledSpriteIcon;

    private BufferedImage spriteSheet;
    private String jsonPath;

    protected List<Image> leftMovingFrames;
    protected List<Image> rightMovingFrames;
    protected List<Image> animateFrames;
    protected List<Image> perpetualAnimationFrames;


    // tutti i costruttori sono protetti per far sì che non sia
    // direttamente istanziabile dall'esterno

    /**
     * Costruttore da utilizzare quando l'immagine appartiene
     * a un unico file.
     *
     * @param name nome da assegnare a this
     * @param spritePath path dell'immagine (sprite)
     */
    protected GamePiece(String name, String spritePath)
    {
        this.name = name;
        this.sprite = SpriteManager.loadSpriteSheet(spritePath);

        this.bWidth = sprite.getWidth(null) / GameManager.BLOCK_SIZE;
        this.bHeight = sprite.getHeight(null) / GameManager.BLOCK_SIZE;

        fakeInitMovingFrames();

        // aggiungi nel gameManager
        GameManager.addPiece(this);
    }


    // TODO : unificare costruttori

    /**
     * Costruttore da utilizzare quando l'immagine appartiene a uno
     * spritesheet ed esiste un Json con le informazioni sullo sprite
     * da caricare.
     *
     * @param name nome da assegnare a this
     * @param spriteSheet lo spriteSheet intero
     * @param jsonPath path del json che contiene informazioni
     *                 (ricavate tramite {@code name})
     */
    protected GamePiece(String name, BufferedImage spriteSheet, String jsonPath)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(spriteSheet);
        Objects.requireNonNull(jsonPath);

        this.name = name;
        this.spriteSheet = spriteSheet;
        this.jsonPath = jsonPath;
        sprite = SpriteManager.loadSpriteByName(spriteSheet, jsonPath, name);

        this.bWidth = sprite.getWidth(null) / GameManager.BLOCK_SIZE;
        this.bHeight = sprite.getHeight(null) / GameManager.BLOCK_SIZE;

        fakeInitMovingFrames();

        // aggiungi nel gameManager
        GameManager.addPiece(this);
    }

    public void initAnimateFrames(String spritesheetPath, String jsonPath)
    {
        BufferedImage spritesheet = SpriteManager.loadSpriteSheet(spritesheetPath);

        animateFrames = SpriteManager.getKeywordOrderedFrames(spritesheet, jsonPath, "animate");

        if(animateFrames.isEmpty())
        {
            animateFrames = SpriteManager.getKeywordOrderedFrames(spritesheet, jsonPath, getName() + "animate");
        }

        // animateFrames.add(0, getSprite());
    }

    public boolean hasPerpetualAnimation()
    {
        return perpetualAnimationFrames != null;
    }

    public List<Image> getPerpetualAnimationFrames()
    {
        return perpetualAnimationFrames;
    }

    /**
     * Inizializza i frame di animazione perpetua, ricavati dallo sprite-sheet
     * indicato.
     *
     * Nel json i frame devono essere indicati seguendo chiavi numerate incrementali e
     * con valori come nel seguente esempio:
     *
     * {@code
     * "1":
     *   {
     *     "x": 0,
     *     "y": 0,
     *     "width": 120,
     *     "height": 96
     *   }
     *   }
     * @param spriteSheetPath
     * @param jsonPath
     */
    public void initPerpetualAnimationFrame(String spriteSheetPath, String jsonPath)
    {
        BufferedImage spritesheet = SpriteManager.loadSpriteSheet(spriteSheetPath);
        perpetualAnimationFrames = SpriteManager.getOrderedFrames(spritesheet, jsonPath);
    }

    /**
     * Imposta lo stato di this.
     *
     * @param state nuovo stato
     */
    public void setState(String state)
    {
        Objects.requireNonNull(state);

        this.state = state;
    }

    public String getState()
    {
        return state;
    }

    public String toString()
    {
        return name;
    }

    public Image getSprite()
    {
        return this.sprite;
    }

    public String getName()
    {
        return this.name;
    }

    public int getBWidth()
    {
        return bWidth;
    }

    public int getBHeight()
    {
        return bHeight;
    }


    @Deprecated
    public void executeEffectAnimation(String animationName, int finalWait, boolean isPerpetual)
    {
        Pair<String, String> animationPaths = SpriteManager.getAnimationPaths(animationName);

        String spritesheetPath = animationPaths.getObject1();
        String jsonPath = animationPaths.getObject2();

        GamePieceEvent.Type eventType = isPerpetual ?
                GamePieceEvent.Type.PERPETUAL_EFFECT_ANIMATION :
                GamePieceEvent.Type.EFFECT_ANIMATION;

        GamePieceEvent effectEvent = new GamePieceEvent( this, eventType);
        effectEvent.setAnimationInfo(spritesheetPath, jsonPath, animationName);
        EventHandler.sendEvent(effectEvent);
    }

    public void animate()
    {
        EventHandler.sendEvent(new GamePieceEvent(this, animateFrames, GamePieceEvent.Type.PIECE_ANIMATION));
    }

    public void animateReverse()
    {
        Collections.reverse(animateFrames);
        EventHandler.sendEvent(new GamePieceEvent(this, animateFrames, GamePieceEvent.Type.PIECE_ANIMATION));
        Collections.reverse(animateFrames);
    }

    public List<Image> getLeftMovingFrames()
    {
        return leftMovingFrames;
    }

    public List<Image> getRightMovingFrames()
    {
        return rightMovingFrames;
    }


    private void fakeInitMovingFrames()
    {
        leftMovingFrames = new ArrayList<>();
        leftMovingFrames.add(sprite);

        rightMovingFrames = leftMovingFrames;
    }


    /**
     * Imposta la stanza in cui è presente this.
     *
     * Se this è il giocatore allora come side effect, per la chiamata
     * a {@link Room#addPiece(GamePiece, BlockPosition)} allora viene
     * iniziato lo scenario di ingresso nella stanza;
     * altrimenti viene continuato lo scenario in corso (se presente).
     *
     * @param room la stanza in cui aggiungere l'oggetto
     * @param pos la posizione in cui aggiungere l'oggetto
     */
    public void addInRoom(Room room, BlockPosition pos)
    {
        Objects.requireNonNull(room);
        Objects.requireNonNull(pos);

        // imposta attributo
        this.locationRoom = room;

        // aggiungilo nella stanza
        room.addPiece(this, pos);
    }

    /**
     * Rimuove this dalla stanza in cui è contenuto,
     * settando la {@link GamePiece#locationRoom} a {@code null}.
     */
    public void removeFromRoom()
    {
        if(locationRoom != null)
        {
            locationRoom.removePiece(this);
            this.locationRoom = null;
        }
    }


    /**
     * Restituisce la stanza in cui è presente l'oggetto, {@code null} se non è presente in alcuna.
     *
     * @return stanza in cui è presente l'oggetto, oppure {@code null}
     */
    public Room getLocationRoom()
    {
        return locationRoom;
    }



    /**
     * Restituisce una copia modificata (rescaled) dello sprite dell'oggetto.
     *
     * L'ultima immagine richiesta viene salvata internamente in modo tale che
     * finché questo metodo viene chiamato nuovamente con lo stesso scalingFactor
     * non crea un'ulteriore immagine.
     *
     * @param scalingFactor fattore di riscalamento dell'immagine
     * @return l'icona modificata
     */
    public Icon getScaledIconSprite(double scalingFactor)
    {
        if(scaledSpriteIcon == null || scalingFactor != this.scalingFactor)
        {
            scaledSpriteIcon = SpriteManager.rescaledImageIcon(sprite, scalingFactor);
            this.scalingFactor = scalingFactor;
        }

        return scaledSpriteIcon;
    }

    public void move(BlockPosition finalPos, String type, int millisecondWaitEnd)
    {
        if(this.getPosition() == null)
            throw new GameException("Personaggio non posizionato");

        if(type.equals("absolute"))
            updatePosition(finalPos, millisecondWaitEnd);
        else if(type.equals("relative"))
            updatePosition(getPosition().relativePosition(finalPos.getX(), finalPos.getY()), millisecondWaitEnd);
        else
            throw new IllegalArgumentException("Valore type non valido");
    }

    /**
     * Imposta la posizione (in blocchi) di this all'interno
     * della stanza in cui è contenuto
     *
     * @param newPosition posizione del blocco in basso a sinistra del GamePiece
     */
    public void updatePosition(BlockPosition newPosition, int millisecondWaitEnd)
    {
        Objects.requireNonNull(newPosition);

        if (locationRoom == null)
            throw new GameException(this + "non presente in alcuna stanza");

        BlockPosition oldPosition = getPosition();

        // aggiorna posizione nella stanza
        try
        {
            // TODO: invalidare l'animazione sbagliata
            locationRoom.setPiecePosition(this, newPosition);

            GamePieceEvent moveEvent = new GamePieceEvent(this, GamePieceEvent.Type.MOVE);
            moveEvent.setOldPosition(oldPosition);
            moveEvent.setNewPosition(newPosition);
            moveEvent.setMillisecondWaitEnd(millisecondWaitEnd);

            EventHandler.sendEvent(moveEvent);
        }
        catch(GameException e)
        {
            // TODO : controllare
            throw new GameError(e);
        }
    }

    /**
     * Restituisce la posizione di this all'interno della stanza
     *
     * @return la posizione nella stanza, {@code null} se this è
     *          presente in una stanza ma la posizione non è stata impostata
     * @throws GameException se l'oggetto non è presente in alcuna stanza
     */
    public BlockPosition getPosition()
    {
        if(locationRoom == null)
            throw new GameException(this + " non presente in alcuna stanza");

        return locationRoom.getPiecePosition(this);
    }


}




