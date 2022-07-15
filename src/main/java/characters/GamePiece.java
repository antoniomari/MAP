package characters;

import events.CharacterEvent;
import events.EventHandler;
import graphics.SpriteManager;
import items.Item;
import rooms.BlockPosition;
import rooms.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class GamePiece
{
    public static final int BLOCK_SIZE = 24;

    protected Image sprite;
    private final String name;

    // larghezza in blocchi dell'elemento
    protected int bWidth;
    // altezza in blocchi dell'elemento
    protected int bHeight;

    // stanza in cui è contenuto
    private Room locationRoom;


    // Per bufferizzazione sprite riscalamento
    private double scalingFactor;
    private Icon scaledSpriteIcon;


    /**
     * Costruttore da utilizzare quando l'immagine appartiene
     * a un unico file.
     *
     * @param name
     * @param spritePath
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
     * @param name
     * @param spriteSheet
     * @param jsonPath
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


    public void setLocationRoom(Room room)
    {
        this.locationRoom = room;
        locationRoom.addPiece(this, null);
    }

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




    // TODO : la posizione dev'essere conosciuta dalla stanza, non dal personaggio
    public void setPosition(BlockPosition newPosition)
    {
        Objects.requireNonNull(newPosition);
        locationRoom.setPiecePosition(this, newPosition);

        // TODO: aggiustare
        //if(this instanceof GameCharacter)
        //    EventHandler.sendEvent(new CharacterEvent((GameCharacter) this, newPosition, CharacterEvent.Type.MOVE));


    }

    public BlockPosition getPosition()
    {
        return locationRoom.getPiecePosition(this);
    }


}




