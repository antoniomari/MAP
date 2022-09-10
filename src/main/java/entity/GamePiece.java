package entity;

import entity.characters.GameCharacter;
import events.EventHandler;
import events.GamePieceEvent;
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
    /** Nome (univoco) del GamePiece. */
    private String name;
    /** Stato utilizzato per far variare il comportamento del GamePiece. */
    protected String state = "init";
    /** Sprite del GamePiece. */
    protected Image sprite;
    /** Larghezza in blocchi del GamePiece. */
    protected int bWidth;
    /** Altezza in blocchi del GamePiece. */
    protected int bHeight;

    /** Stanza in cui il GamePiece è presente. */
    private Room locationRoom;

    // Per bufferizzazione sprite
    /** Fattore di riscalamento salvato per lo sprite bufferizzato. */
    private double scalingFactor;
    /** Sprite riscalato secondo rescalingFactor, salvato per la bufferizzazione. */
    private Icon scaledSpriteIcon;

    /** Lista di frame per l'animazione di movimento verso sinistra. */
    protected List<Image> leftMovingFrames;
    /** Lista di frame per l'animazione di movimento verso destra. */
    protected List<Image> rightMovingFrames;
    /**
     * Lista di frame per l'eventuale animazione personalizzata
     * (metodo {@link GamePiece#animate()} e {@link GamePiece#animateReverse()}
     */
    protected List<Image> animateFrames;
    /** Lista di frame per l'eventuale animazione perpetua personalizzata. */
    protected List<Image> perpetualAnimationFrames;


    // tutti i costruttori sono protetti per far sì che non sia
    // direttamente istanziabile dall'esterno

    /**
     * Costruttore da utilizzare quando viene fornito al GamePiece
     * un unico sprite e non uno sprite-sheet (non è quindi necessario un json).
     *
     * Alla costruzione, il GamePiece è automaticamente registrato nel GameManager.
     *
     * @param name nome da assegnare al GamePiece
     * @param spritePath path dell'immagine (sprite)
     */
    protected GamePiece(String name, String spritePath)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(spritePath);
        pieceInit(name, SpriteManager.loadImage(spritePath));
    }

    /**
     * Costruttore da utilizzare quando l'immagine appartiene a uno
     * sprite-sheet ed esiste un Json con le informazioni sullo sprite
     * da caricare.
     *
     * Alla costruzione, il GamePiece è automaticamente registrato nel GameManager.
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

        pieceInit(name, SpriteManager.loadSpriteByName(spriteSheet, jsonPath, name));

    }

    // inizializzazione per costruttore
    private void pieceInit(String name, Image sprite)
    {
        this.name = name;
        this.sprite = sprite;

        this.bWidth = sprite.getWidth(null) / GameManager.BLOCK_SIZE;
        this.bHeight = sprite.getHeight(null) / GameManager.BLOCK_SIZE;

        initDefaultMovingFrames();

        // aggiungi nel gameManager
        GameManager.addPiece(this);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GamePiece gamePiece = (GamePiece) o;
        return name.equals(gamePiece.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name);
    }


    /**
     * Inizializzazione di default per
     * {@link GamePiece#leftMovingFrames} e
     * {@link GamePiece#rightMovingFrames}.
     *
     * Conterranno solamente lo sprite principale,
     * in modo tale che durante l'animazione di
     * movimento non si percepiranno cambiamenti
     * nello sprite.
     */
    private void initDefaultMovingFrames()
    {
        leftMovingFrames = new ArrayList<>();
        leftMovingFrames.add(sprite);

        rightMovingFrames = leftMovingFrames;
    }

    /**
     * Inizializza i frame dell'animazione personalizzata di this.
     *
     * Cerca gli oggetti json le cui chiavi sono {@code "animate1", "animate2", ...}.
     * Se questi non vengono trovati allora cerca gli oggetti json le cui chiavi sono
     * {@code "[name]animate1", "[name]animate2", ...}, dove {@code [name]} è il nome
     * di this.
     *
     * @param spriteSheetPath path dello sprite-sheet contenente i frame da caricare
     * @param jsonPath path del json contenente i dati sui frame da caricare
     */
    public void initAnimateFrames(String spriteSheetPath, String jsonPath)
    {
        Objects.requireNonNull(spriteSheetPath);
        Objects.requireNonNull(jsonPath);

        BufferedImage spriteSheet = SpriteManager.loadImage(spriteSheetPath);

        animateFrames = SpriteManager.getKeywordOrderedFrames(spriteSheet, jsonPath, "animate");

        if(animateFrames.isEmpty())
        {
            animateFrames = SpriteManager.getKeywordOrderedFrames(spriteSheet, jsonPath, getName() + "animate");
        }
    }

    /**
     * Restituisce {@code true} se this ha frame impostati per l'animazione perpetua.
     * 
     * @return {@code true} se this ha frame impostati per l'animazione perpetua,
     * {@code false} altrimenti.
     */
    public boolean hasPerpetualAnimation()
    {
        return perpetualAnimationFrames != null;
    }

    /**
     * Restituisce i frame di animazione perpetua di this.
     * 
     * @return frame di animazione perpetua di this, {@code null} se non sono stati impostati
     */
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
     * @param spriteSheetPath path dello sprite-sheet contenente i frame da caricare
     * @param jsonPath path del json contenente i dati sui frame da caricare
     */
    public void initPerpetualAnimationFrames(String spriteSheetPath, String jsonPath)
    {
        Objects.requireNonNull(spriteSheetPath);
        Objects.requireNonNull(jsonPath);

        BufferedImage spriteSheet = SpriteManager.loadImage(spriteSheetPath);
        perpetualAnimationFrames = SpriteManager.getOrderedFrames(spriteSheet, jsonPath);
    }

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

    public List<Image> getLeftMovingFrames()
    {
        return leftMovingFrames;
    }

    public List<Image> getRightMovingFrames()
    {
        return rightMovingFrames;
    }

    /**
     * Esegue un effetto animato su this.
     *
     * Sprite-sheet e json dell'effetto animato vengono ricavati dall'animationName
     * tramite {@link SpriteManager#getAnimationPaths(String)}.
     *
     * @param animationName nome dell'animazione
     * @param isPerpetual flag che indica se l'animazione dev'essere perpetua
     */
    public void executeEffectAnimation(String animationName, boolean isPerpetual)
    {
        Objects.requireNonNull(animationName);

        Pair<String, String> animationPaths = SpriteManager.getAnimationPaths(animationName);

        String spriteSheetPath = animationPaths.getObject1();
        String jsonPath = animationPaths.getObject2();

        GamePieceEvent.Type eventType = isPerpetual ?
                GamePieceEvent.Type.PERPETUAL_EFFECT_ANIMATION :
                GamePieceEvent.Type.EFFECT_ANIMATION;

        // genera evento di animazione
        GamePieceEvent effectEvent = new GamePieceEvent( this, eventType);
        effectEvent.setAnimationInfo(spriteSheetPath, jsonPath, animationName);
        EventHandler.sendEvent(effectEvent);
    }

    /**
     * Esegue l'animazione personalizzata di this.
     */
    public void animate()
    {
        EventHandler.sendEvent(GamePieceEvent.makePieceAnimationEvent(this, animateFrames));
    }

    /**
     * Esegue l'animazione personalizzata di this, al contrario.
     */
    public void animateReverse()
    {
        Collections.reverse(animateFrames);
        EventHandler.sendEvent(GamePieceEvent.makePieceAnimationEvent(this, animateFrames));
        Collections.reverse(animateFrames);
    }

    /**
     * Imposta la stanza in cui è presente this.
     *
     * Se this è il giocatore allora come side effect, per la chiamata
     * a {@link Room#addPiece(GamePiece, BlockPosition)} allora viene
     * iniziato lo scenario d'ingresso nella stanza;
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

    /**
     * Muove this nella stanza.
     *
     * @param finalPos posizione finale di this nella stanza
     * @param type "absolute" se finalPos è una posizione assoluta nella stanza,
     *             "relative" se finalPos è una posizione relativa a quella attuale di this
     * @param millisecondWaitEnd millisecondi da attendere alla fine del movimento
     */
    public void move(BlockPosition finalPos, String type, int millisecondWaitEnd)
    {
        Objects.requireNonNull(finalPos);
        Objects.requireNonNull(type);

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
    private void updatePosition(BlockPosition newPosition, int millisecondWaitEnd)
    {
        Objects.requireNonNull(newPosition);

        if (locationRoom == null)
            throw new GameException(this + "non presente in alcuna stanza");

        BlockPosition oldPosition = getPosition();

        // aggiorna posizione nella stanza
        locationRoom.setPiecePosition(this, newPosition);

        GamePieceEvent moveEvent = new GamePieceEvent(this, GamePieceEvent.Type.MOVE);
        moveEvent.setOldPosition(oldPosition);
        moveEvent.setNewPosition(newPosition);
        moveEvent.setMillisecondWaitEnd(millisecondWaitEnd);

        EventHandler.sendEvent(moveEvent);
    }

    /**
     * Restituisce la posizione di this all'interno della stanza.
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
