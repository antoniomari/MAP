package items;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;

public class Item implements Observable
{
    private String name;
    private final static String DEFAULT_NAME = "Spicoli";
    private String description;
    private final static String DEFAULT_DESCRIPTION = "Un oggetto strano";
    private final static String OBJECT_TILESET_PATH = "img/tileset/oggetti.png";
    private final static String JSON_PATH = "img/tileset/oggetti.json";
    private static BufferedImage TILESET;
    private BufferedImage sprite;
    private int x;
    private int y;
    private int width;
    private int height;

    static
    {
        try
        {
            TILESET = ImageIO.read(Item.class.getResource("img/tileset/oggetti.png"));
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

            x = itemJson.getInt("x");
            y = itemJson.getInt("y");
            width = itemJson.getInt("width");
            height = itemJson.getInt("height");

            sprite = TILESET.getSubimage(x, y, width, height);
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

}
