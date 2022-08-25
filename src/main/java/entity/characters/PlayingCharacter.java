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
    private static final String PLAYER_NAME = "Schwartz";

    private PlayingCharacter(String name, String spritesheetPath, String jsonPath)
    {
        super(name, spritesheetPath, jsonPath);
        this.inventory = new ArrayList<>();
    }

    public static PlayingCharacter getPlayer()
    {
        if(player == null)
            player = new PlayingCharacter("Schwartz", "/img/personaggi/Schwartz spritesheet.png",
                    "/img/personaggi/Schwartz.json");

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


    public static String getPlayerName()
    {
        return PLAYER_NAME;
    }
}
