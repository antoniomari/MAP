package entity.characters;

import entity.items.PickupableItem;
import events.EventHandler;
import events.InventoryEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayingCharacter extends GameCharacter
{
    public static final int INVENTORY_SIZE = 16;

    private static final String SCHWARTZ_SPRITESHEET_PATH = "/img/personaggi/Schwartz spritesheet.png";
    private static final String SCHWARTZ_JSON_PATH = "/img/personaggi/Schwartz.json";

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
            player = new PlayingCharacter("Schwartz", SCHWARTZ_SPRITESHEET_PATH, SCHWARTZ_JSON_PATH);

        return player;
    }

    // TODO : addToInventory con l'indice per caricamento da db
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
