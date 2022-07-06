package graphics;

import items.Item;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;

public class SpriteManager
{
    /**
     * Carica uno spritesheet.
     *
     * @param spriteSheetPath path dello spritesheet
     * @return BufferedImage corrispondente al file
     */
    public static BufferedImage loadSpriteSheet(String spriteSheetPath)
    {
        try
        {
            return ImageIO.read(SpriteManager.class.getResource(spriteSheetPath));
        }
        catch (IOException e)
        {
            // Errore caricamento background
            throw new IOError(e);
        }
    }


    /**
     * Carica immagine sprite in base al nome contenuto nel json
     * corrispondente allo spritesheet utilizzato.
     *
     * @param spriteSheet immagine completa dello spritesheet, già caricata
     * @param jsonPath path del file json relativo allo spritesheet
     * @param spriteName nome dell'immagine da caricare, contenuto nel json
     * @return
     */
    public static BufferedImage loadSpriteByName(BufferedImage spriteSheet, String jsonPath, String spriteName)
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
}
