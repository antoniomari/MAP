package rooms;

import items.Item;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room
{
    private String roomName;
    private Room north;
    private Room south;
    private Room west;
    private Room east;
    private List<Item> itemList;

    private String backgroundPath;
    private BufferedImage backgroundImage;

    private Map<Item, Coordinates> itemMap;


    public Room(String name, String path)
    {
        this.roomName = name;
        itemMap = new HashMap<>();
        itemList = new ArrayList<>();
        backgroundPath = path;
        loadBackgroundImage();
    }

    public void addItem(Item item, Coordinates c)
    {
        itemMap.put(item, c);
        itemList.add(item);
    }

    public Item getItem(int i)
    {
        return itemList.get(i);
    }

    private void loadBackgroundImage()
    {
        try
        {
            backgroundImage = ImageIO.read(getClass().getResource(backgroundPath));
        }
        catch(IOException e)
        {
            // Errore caricamento background
            throw new IOError(e);
        }
    }
    public BufferedImage getBackgroundImage()
    {
        return backgroundImage;
    }

    /*
    funzione boolean isBloccoCamminabile(int xBlock, int yBlock)
     */

    //TODO: inserire max xBlock e yBlock
}
