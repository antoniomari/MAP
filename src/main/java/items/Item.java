package items;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;

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
    private static BufferedImage SPRITESHEET;

    // SPRITE OGGETTO
    private BufferedImage sprite;
    private double scalingFactor;
    private Icon scaledSpriteIcon;

    // CARICAMENTO SPRITESHEET IN MEMORIA
    static
    {
        try
        {
            SPRITESHEET = ImageIO.read(Item.class.getResource(OBJECT_SPRITESHEET_PATH));
        }
        catch (IOException e)
        {
            // Errore caricamento background
            throw new IOError(e);
        }

    }


    public Item(String name, String description)
    {
        this.name = name;
        this.description = description;
        extractSprite();
    }

    public Item()
    {
        this(DEFAULT_NAME, DEFAULT_DESCRIPTION);
    }

    public String observe()
    {
        return this.description;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDescription()
    {
        return description;
    }

    private void extractSprite()
    {

        try
        {
            InputStream is = Item.class.getResourceAsStream(JSON_PATH);
            JSONTokener tokener = new JSONTokener(is);
            JSONObject json = new JSONObject(tokener);

            JSONObject itemJson = json.getJSONObject(this.name);

            int x = itemJson.getInt("x");
            int y = itemJson.getInt("y");
            int width = itemJson.getInt("width");
            int height = itemJson.getInt("height");

            sprite = SPRITESHEET.getSubimage(x, y, width, height);
        }
        catch(JSONException e)
        {
            //TODO: :)
        }
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
            scaledSpriteIcon = rescaledImageIcon(sprite, scalingFactor);

        return scaledSpriteIcon;
    }


    private static Icon rescaledImageIcon(Image im, double rescalingFactor)
    {
        int newWidth = (int) (rescalingFactor * im.getWidth(null));
        int newHeight = (int)(rescalingFactor * im.getHeight(null));
        Image newSprite = im.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(newSprite);
    }

}
