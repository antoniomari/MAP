package items;

import events.EventHandler;
import events.ItemInteractionEvent;
import graphics.SpriteManager;
import rooms.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Item implements Observable
{
    private final String name;
    private final String description;

    private final static String DEFAULT_NAME = "Spicoli";
    private final static String DEFAULT_DESCRIPTION = "Un oggetto strano";

    // PATH TILESET OGGETTI
    private final static String OBJECT_SPRITESHEET_PATH = "/img/tileset/oggetti.png";
    // PATH JSON RELATIVO AL TILESET
    private final static String JSON_PATH = "/img/tileset/oggetti.json";

    // SPRITESHEET OGGETTI
    private static final BufferedImage SPRITESHEET;

    // SPRITE OGGETTO
    protected BufferedImage sprite;

    protected int bWidth;
    protected int bHeight;

    // Per bufferizzazione sprite riscalamento
    private double scalingFactor;
    private Icon scaledSpriteIcon;

    private Room locationRoom; // stanza in cui è contenuto, se è null allora vuol dire che è nell'inventario

    // CARICAMENTO SPRITESHEET IN MEMORIA
    static
    {
        SPRITESHEET = SpriteManager.loadSpriteSheet(OBJECT_SPRITESHEET_PATH);
    }


    public Item(String name, String description)
    {
        this(name, description, SPRITESHEET, JSON_PATH);
    }

    protected Item(String name, String description, BufferedImage spriteSheet, String jsonPath)
    {
        int BLOCK_SIZE = 24;

        this.name = name;
        this.description = description;
        extractSprite(spriteSheet, jsonPath);
        bWidth = sprite.getWidth() / BLOCK_SIZE;
        bHeight = sprite.getHeight() / BLOCK_SIZE;

    }

    public void setLocationRoom(Room room)
    {
        this.locationRoom = room;
    }

    public Room getLocationRoom()
    {
        return locationRoom;
    }

    public void observe()
    {
        EventHandler.sendEvent(new ItemInteractionEvent(this, ItemInteractionEvent.Type.OBSERVE));
    }

    public String getName()
    {
        return this.name;
    }

    public String getDescription()
    {
        return description;
    }

    /*
    protected BufferedImage loadSpriteByName(BufferedImage spriteSheet, String jsonPath, String spriteName)
    {
        InputStream is = Item.class.getResourceAsStream(jsonPath);
        JSONTokener tokener = new JSONTokener(is);
        JSONObject json = new JSONObject(tokener);

        JSONObject itemJson = json.getJSONObject(spriteName);

        int x = itemJson.getInt("x");
        int y = itemJson.getInt("y");
        int width = itemJson.getInt("width");
        int height = itemJson.getInt("height");

        return spriteSheet.getSubimage(x, y, width, height);
    }

     */


    private void extractSprite(BufferedImage spriteSheet, String jsonPath)
    {
        /*
        try
        {
            InputStream is = Item.class.getResourceAsStream(jsonPath);
            JSONTokener tokener = new JSONTokener(is);
            JSONObject json = new JSONObject(tokener);

            JSONObject itemJson = json.getJSONObject(this.name);

            int x = itemJson.getInt("x");
            int y = itemJson.getInt("y");
            int width = itemJson.getInt("width");
            int height = itemJson.getInt("height");

            sprite = spriteSheet.getSubimage(x, y, width, height);
        }
        catch(JSONException e)
        {

        }

        */
       sprite = SpriteManager.loadSpriteByName(spriteSheet, jsonPath, name);
    }

    public Image getSprite()
    {
        return sprite;
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

    public int getBWidth()
    {
        return bWidth;
    }

    public int getBHeight()
    {
        return bHeight;
    }
}
