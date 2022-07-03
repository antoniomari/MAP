package rooms;

import items.Item;

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

    private Map<Item, Coordinates> itemMap;

    public Room(String name)
    {
        this.roomName = name;
        itemMap = new HashMap<>();
        itemList = new ArrayList<>();
    }

    public Room(String name, String path)
    {
        this(name);
        backgroundPath = path;
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

    public String getBackgroundPath()
    {
        return backgroundPath;
    }

}
