package entity.characters;

import entity.GamePiece;
import entity.items.PickupableItem;
import entity.rooms.Room;
import events.EventHandler;
import events.InventoryEvent;
import general.GameManager;

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

    private PlayingCharacter(String name, String spritePath)
    {
        super(name, spritePath);
        this.inventory = new ArrayList<>();
    }

    public static PlayingCharacter getPlayer()
    {
        if(player == null)
            player = new PlayingCharacter("Schwartz", SCHWARTZ_SPRITESHEET_PATH, SCHWARTZ_JSON_PATH);

        return player;
    }

    public static void makePlayerFinalForm()
    {
        if(player == null)
        {
            throw new IllegalStateException("Operazione non possibile, giocatore non creato");
        }

        player.removeFromRoom();
        GameManager.removePiece(player);
        player = new PlayingCharacter("Schwartz", "/img/personaggi/Schwartz robot.png");
        Room currentRoom = GameManager.getMainFrame().getCurrentRoom();
        player.addInRoom(currentRoom, currentRoom.getDefaultPosition());
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
