package characters;

import items.PickupableItem;
import java.util.ArrayList;
import java.util.List;

public class PlayingCharacter extends GameCharacter
{
    public static final int INVENTORY_SIZE = 30;

    List<PickupableItem> inventory;
    private static PlayingCharacter player;

    private PlayingCharacter(String name)
    {
        super(name);
        this.inventory = new ArrayList<>();
    }

    public static void initPlayer(String name)
    {
        if (player == null)
            player = new PlayingCharacter(name);
    }

    public static PlayingCharacter getPlayer()
    {
        return player;
    }

    public void addToInventory(PickupableItem i)
    {
        inventory.add(i);
    }

    public void removeFromInventory(PickupableItem i)
    {
        inventory.remove(i);
    }

    public List<PickupableItem> getInventory()
    {
        return inventory;
    }

    public void flushInventory()
    {
        for(PickupableItem i : inventory)
        {
            inventory.remove(i);
        }
    }
}
