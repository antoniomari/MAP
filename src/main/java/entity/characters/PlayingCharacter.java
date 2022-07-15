package entity.characters;

import entity.items.PickupableItem;
import events.EventHandler;
import events.InventoryEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayingCharacter extends GameCharacter
{
    public static final int INVENTORY_SIZE = 30;

    List<PickupableItem> inventory;
    private static PlayingCharacter player;

    private PlayingCharacter(String name, String spritePath)
    {
        super(name, spritePath);
        this.inventory = new ArrayList<>();
    }

    public static PlayingCharacter getPlayer()
    {
        if(player == null)
            player = new PlayingCharacter("Schwartz", "/img/personaggi/schwartz.png");

        return player;
    }

    public void addToInventory(PickupableItem i)
    {
        inventory.add(i);
        EventHandler.sendEvent(new InventoryEvent(i, InventoryEvent.Type.ADD_ITEM));
    }

    public void removeFromInventory(PickupableItem i)
    {
        inventory.remove(i);
        EventHandler.sendEvent(new InventoryEvent(i, InventoryEvent.Type.USE_ITEM));
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
