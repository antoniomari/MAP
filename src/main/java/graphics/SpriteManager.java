package graphics;

import general.GameException;
import general.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class SpriteManager
{
    /** Path della cartella delle animazioni. */
    public static final String ANIMATION_FOLDER_PATH = "/img/animazioni/";

    /**
     * Restituisce i dati di sprite-sheet e json relativi a un'animazione.
     *
     * @param animationName nome dell'animazione da cercare
     * @return coppia path sprite-sheet e path json relativi all'animazione
     * cercata
     */
    public static Pair<String, String> getAnimationPaths(String animationName)
    {
        return new Pair<>(ANIMATION_FOLDER_PATH + animationName + ".png",
                ANIMATION_FOLDER_PATH + animationName + ".json");
    }

    /**
     * Carica un'immagine.
     *
     * @param imagePath path dell'immagine da caricare
     * @return BufferedImage corrispondente al file
     * @throws IOError se fallisce la lettura del file dello spritesheet
     */
    public static BufferedImage loadImage(String imagePath)
    {
        try
        {
            imagePath = "src/main/resources" + imagePath;
            return ImageIO.read((new File(imagePath)).toURI().toURL());
        }
        catch (Exception e)
        {
            throw new GameException("Impossibile caricare " + imagePath);
        }
    }

    /**
     * Restituisce una lista ordinata d'immagini, descritte nel json da oggetti
     * json con chiave numerica incrementale {@code "1", "2", ...}.
     *
     * @param spriteSheet sprite-sheet da cui ritagliare le immagini
     * @param jsonPath path del json contenente i dati sulle immagini da ritagliare
     * @return lista ordinata d'immagini ritagliate
     */
    public static List<Image> getOrderedFrames(BufferedImage spriteSheet, String jsonPath)
    {
        JSONObject json = getJsonFromFile(jsonPath);
        return json.keySet()
                .stream().mapToInt(Integer::parseInt).sorted()
                .mapToObj(key -> loadSpriteByName(spriteSheet, jsonPath, String.valueOf(key)))
                .collect(Collectors.toList());
    }

    /**
     * Restituisce una lista ordinata d'immagini, descritte nel json da oggetti
     * json con chiavi {@code "[keyword]1", "[keyword]2", ...}, dove [keyword] è il
     * valore effettivo del parametro.
     *
     * @param spriteSheet sprite-sheet da cui ritagliare le immagini
     * @param jsonPath path del json contenente i dati sulle immagini da ritagliare
     * @param keyword stringa da anteporre al numero incrementale per formare le chiavi
     *                da cercare nel ".json"
     * @return lista ordinata d'immagini ritagliate
     */
    public static List<Image> getKeywordOrderedFrames(BufferedImage spriteSheet, String jsonPath, String keyword)
    {
        JSONObject json = getJsonFromFile(jsonPath);
        return json.keySet().stream()
                .filter(str -> str.startsWith(keyword))
                .mapToInt(str -> Integer.parseInt(str.split(keyword)[1]))
                .sorted()
                .mapToObj(x -> keyword + x)
                .map(key -> loadSpriteByName(spriteSheet, jsonPath, String.valueOf(key)))
                .collect(Collectors.toList());
    }


    /**
     * Carica immagine sprite in base al nome contenuto nel json
     * corrispondente allo sprite-sheet utilizzato.
     *
     * @param spriteSheet immagine completa dello sprite-sheet, già caricata
     * @param jsonPath path del file json relativo allo sprite-sheet
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

    /**
     * Crea un oggetto JSONObject contenente il contenuto di
     * un file ".json".
     *
     * @param jsonPath path del file ".json" da caricare
     * @return JSONObject contenente il contenuto del file
     */
    public static JSONObject getJsonFromFile(String jsonPath)
    {
        try
        {
            jsonPath = "src/main/resources" + jsonPath;
            InputStream is = new FileInputStream(jsonPath);
            // InputStream is = SpriteManager.class.getResourceAsStream(jsonPath);
            JSONTokener tokener = new JSONTokener(is);

            return new JSONObject(tokener);
        }
        catch(FileNotFoundException e)
        {
            throw new GameException("Errore nel caricamento del json " + jsonPath);
        }
    }


    /**
     * Restituisce un'icona riscalata dell'immagine im.
     *
     * @param im immagine della quale creare un'icona riscalata.
     * @param rescalingFactor fattore moltiplicativo di riscalamento delle dimensioni dell'immagine
     * @return icona riscalata dell'immagine
     */
    public static Icon rescaledImageIcon(Image im, double rescalingFactor)
    {
        int newWidth = (int) Math.round(rescalingFactor * im.getWidth(null));
        int newHeight = (int) Math.round(rescalingFactor * im.getHeight(null));
        return rescaledImageIcon(im, newWidth, newHeight);
    }

    /**
     * Restituisce un'icona riscalata dell'immagine im.
     *
     * @param im immagine della quale creare un'icona riscalata.
     * @param newWidth larghezza finale dell'icona
     * @param newHeight altezza final dell'icona
     * @return icona riscalata dell'immagine
     */
    public static Icon rescaledImageIcon(Image im, int newWidth, int newHeight)
    {
        Image newSprite = im.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(newSprite);
    }
}
