package rooms;

import graphics.SpriteManager;
import items.Item;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
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
    private final RoomFloor floor;

    private final int width;  // larghezza in blocchi
    private final int height;  // altezza in blocchi


    public Room(String name, String path, String jsonPath)
    {
        this.roomName = name;
        itemMap = new HashMap<>();
        itemList = new ArrayList<>();
        backgroundPath = path;
        loadBackgroundImage();
        floor = SpriteManager.loadFloorFromJson(jsonPath);
        JSONObject json = SpriteManager.getJsonFromFile(jsonPath);

        width = json.getInt("width");
        height = json.getInt("height");


    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public RoomFloor getFloor()
    {
        return floor;
    }

    public void addItem(Item item, Coordinates c)
    {
        itemMap.put(item, c);
        itemList.add(item);
    }

    public void removeItem(Item item)
    {
        itemMap.remove(item);
        itemList.remove(item);
    }

    public Item getItem(int i)
    {
        return itemList.get(i);
    }

    private void loadBackgroundImage()
    {
        backgroundImage = SpriteManager.loadSpriteSheet(backgroundPath);
    }

    public BufferedImage getBackgroundImage()
    {
        return backgroundImage;
    }


}
