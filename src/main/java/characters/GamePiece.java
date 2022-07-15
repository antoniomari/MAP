package characters;

import graphics.SpriteManager;
import items.Item;
import rooms.BlockPosition;
import rooms.Room;

import javax.swing.Icon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Classe che rappresenta un qualsiasi elemento fisico del gioco, ossia che ha uno sprite
 * e può occupare una posizione (e ha un'estensione) in una stanza.
 *
 * Viene utilizzata come classe base sia per i personaggi {@link GameCharacter} che per gli
 * oggetti {@link Item}
 */
public class GamePiece
{
    public static final int BLOCK_SIZE = 24;

    private final String name;
    protected Image sprite;

    protected int bWidth;  // larghezza in blocchi dell'elemento
    protected int bHeight;  // altezza in blocchi dell'elemento

    private Room locationRoom;  // stanza in cui è contenuto

    // Per bufferizzazione sprite
    private double scalingFactor;
    private Icon scaledSpriteIcon;


    /**
     * Costruttore da utilizzare quando l'immagine appartiene
     * a un unico file.
     *
     * @param name nome da assegnare a this
     * @param spritePath path dell'immagine (sprite)
     */
    public GamePiece(String name, String spritePath)
    {
        this.name = name;
        this.sprite = SpriteManager.loadSpriteSheet(spritePath);

        this.bWidth = sprite.getWidth(null) / BLOCK_SIZE;
        this.bHeight = sprite.getHeight(null) / BLOCK_SIZE;
    }


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
    public GamePiece(String name, BufferedImage spriteSheet, String jsonPath)
    {
        this.name = name;
        sprite = SpriteManager.loadSpriteByName(spriteSheet, jsonPath, name);

        this.bWidth = sprite.getWidth(null) / BLOCK_SIZE;
        this.bHeight = sprite.getHeight(null) / BLOCK_SIZE;
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


    /**
     * Imposta la stanza in cui è presente this.
     *
     * @param room la stanza in cui aggiungere l'oggetto (le coordinate saranno null),
     *             oppure {@code null} se si vuole semplicemente rimuovere l'oggetto
     *             dalla stanza
     */
    public void setLocationRoom(Room room)
    {
        // rimuovi l'oggetto dalla vecchia stanza (se presente)
        if(locationRoom != null)
            locationRoom.removePiece(this);

        // imposta attributo
        this.locationRoom = room;

        // se è stata effettivamente impostata una nuova stanza, aggiungilo in posizione null
        if(room != null)
            locationRoom.addPiece(this, null);

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
     * Imposta la posizione (in blocchi) di this all'interno
     * della stanza in cui è contenuto
     *
     * @param newPosition posizione in blocchi alla quale posizionare
     */
    public void setPosition(BlockPosition newPosition)
    {
        Objects.requireNonNull(newPosition);
        locationRoom.setPiecePosition(this, newPosition);

        // TODO: controllo sui bordi
        // TODO: aggiustare
        //if(this instanceof GameCharacter)
        //    EventHandler.sendEvent(new CharacterEvent((GameCharacter) this, newPosition, CharacterEvent.Type.MOVE));
    }

    /**
     * Restituisce la posizione di this all'interno della stanza
     *
     * @return la posizione nella stanza, {@code null} se this è
     *          presente in una stanza ma la posizione non è stata impostata
     * @throws NullPointerException se l'oggetto non è presente in alcuna stanza
     */
    public BlockPosition getPosition()
    {
        // todo: modificare nullPointerException?
        return locationRoom.getPiecePosition(this);
    }


}




