package graphics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import entity.rooms.RoomFloor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class SpriteManager
{
    /**
     * Carica uno spritesheet.
     *
     * @param spriteSheetPath path dello spritesheet
     * @return BufferedImage corrispondente al file
     * @throws IOError se fallisce la lettura del file dello spritesheet
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

    public static List<Image> getOrderedFrames(BufferedImage spritesheet, String jsonPath)
    {
        JSONObject json = getJsonFromFile(jsonPath);
        return json.keySet()
                .stream().mapToInt(Integer::parseInt).sorted()
                .mapToObj(key -> loadSpriteByName(spritesheet, jsonPath, String.valueOf(key)))
                .collect(Collectors.toList());
    }

    public static List<Image> getKeywordOrderedFrames(BufferedImage spritesheet, String jsonPath, String keyword)
    {
        JSONObject json = getJsonFromFile(jsonPath);
        return json.keySet().stream()
                .filter(str -> str.startsWith(keyword))
                .mapToInt(str -> Integer.parseInt(str.split(keyword)[1]))
                .sorted()
                .mapToObj(x -> keyword + x)
                .map(key -> loadSpriteByName(spritesheet, jsonPath, String.valueOf(key)))
                .collect(Collectors.toList());
    }


    /**
     * Carica immagine sprite in base al nome contenuto nel json
     * corrispondente allo spritesheet utilizzato.
     *
     * @param spriteSheet immagine completa dello spritesheet, già caricata
     * @param jsonPath path del file json relativo allo spritesheet
     * @param spriteName nome dell'immagine da caricare, contenuto nel json
     * @return lo sprite richiesto
     */
    public static BufferedImage loadSpriteByName(BufferedImage spriteSheet, String jsonPath, String spriteName)
    {
        JSONObject json = getJsonFromFile(jsonPath);

        JSONObject itemJson;
        try
        {
            itemJson = json.getJSONObject(spriteName);
        }
        catch (JSONException e)
        {
            itemJson = json.getJSONObject("missing");
        }

        int x = itemJson.getInt("x");
        int y = itemJson.getInt("y");
        int width = itemJson.getInt("width");
        int height = itemJson.getInt("height");

        return spriteSheet.getSubimage(x, y, width, height);
    }

    public static RoomFloor loadFloorFromJson(String jsonPath)
    {
        JSONObject json = getJsonFromFile(jsonPath);
        JSONArray rectangleArray = json.getJSONArray("pavimento");

        int roomWidth = json.getInt("width");
        int roomHeight = json.getInt("height");

        RoomFloor floor = new RoomFloor();

        for (int i = 0; i < rectangleArray.length(); i++)
        {
            JSONObject rectangleJson = rectangleArray.getJSONObject(i);
            if(rectangleJson.getInt("width") > roomWidth
                    || rectangleJson.getInt("height") > roomHeight)
                throw new JSONException("Pavimento non valido");

            floor.addWalkableRectangle(rectangleJson.getInt("left"),
                                        rectangleJson.getInt("top"),
                                        rectangleJson.getInt("width"),
                                        rectangleJson.getInt("height"));
        }

        return floor;
    }

    public static JSONObject getJsonFromFile(String jsonPath)
    {
        InputStream is = SpriteManager.class.getResourceAsStream(jsonPath);
        JSONTokener tokener = new JSONTokener(is);

        return new JSONObject(tokener);
    }


    public static Icon rescaledImageIcon(Image im, double rescalingFactor)
    {
        int newWidth = (int) Math.round(rescalingFactor * im.getWidth(null));
        int newHeight = (int) Math.round(rescalingFactor * im.getHeight(null));
        Image newSprite = im.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(newSprite);
    }
}
