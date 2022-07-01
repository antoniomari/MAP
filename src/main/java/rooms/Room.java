package rooms;

import items.Item;

import java.util.HashMap;
import java.util.Map;

public class Room
{
    private Room north;
    private Room south;
    private Room west;
    private Room east;

    private Map<Item, Coordinates> itemList;

    public Room()
    {
        itemList = new HashMap<>();
    }

    public void addItem(Item item, Coordinates c)
    {
        itemList.put(item, c);
    }


}
