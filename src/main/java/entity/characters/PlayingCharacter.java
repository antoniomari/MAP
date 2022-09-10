package entity.characters;

import entity.items.PickupableItem;
import entity.rooms.Room;
import events.EventHandler;
import events.InventoryEvent;
import general.GameManager;

import java.util.ArrayList;
import java.util.List;

public class PlayingCharacter extends GameCharacter
{
    /** Dimensione dell'inventario del personaggio giocante. */
    public static final int INVENTORY_SIZE = 16;

    /** Path dello sprite-sheet del personaggio giocante. */
    private static final String SCHWARTZ_SPRITESHEET_PATH = "/img/personaggi/Schwartz spritesheet.png";
    /** Path del json relativo allo sprite-sheet del personaggio giocante. */
    private static final String SCHWARTZ_JSON_PATH = "/img/personaggi/Schwartz.json";
    /** Path dello sprite della forma finale del personaggio giocante. */
    private static final String SCHWARTZ_ROBOT_SPRITE_PATH = "/img/personaggi/Schwartz robot.png";
    private static final String PLAYER_NAME = "Schwartz";

    /**  Pattern singleton: personaggio giocante. */
    private static PlayingCharacter player;

    /** Inventario del personaggio giocante. */
    List<PickupableItem> inventory;

    /**
     * Crea un PlayingCharacter avente uno sprite-sheet e un json.
     *
     * @param name nome da assegnare al PlayingCharacter
     * @param spriteSheetPath path dello sprite-sheet del PlayingCharacter
     * @param jsonPath path del json collegato allo sprite-sheet
     */
    private PlayingCharacter(String name, String spriteSheetPath, String jsonPath)
    {
        super(name, spriteSheetPath, jsonPath);
        this.inventory = new ArrayList<>();
    }

    /**
     * Crea un PlayingCharacter avente un unico sprite.
     *
     * @param name nome da assegnare al PlayingCharacter
     * @param spritePath path dello sprite del PlayingCharacter
     */
    private PlayingCharacter(String name, String spritePath)
    {
        super(name, spritePath);
        this.inventory = new ArrayList<>();
    }

    /**
     * Restituisce il personaggio giocante (pattern singleton).
     *
     * @return personaggio giocante
     */
    public static PlayingCharacter getPlayer()
    {
        if(player == null)
            player = new PlayingCharacter(PLAYER_NAME, SCHWARTZ_SPRITESHEET_PATH, SCHWARTZ_JSON_PATH);

        return player;
    }

    /**
     * Sostituisce il personaggio giocante originale (Scienziato Schwartz)
     * con il nuovo personaggio giocante (Schwartz robot), creando una nuova istanza
     * di PlayingCharacter con lo stesso nome ma utilizzando uno sprite diverso.
     *
     * A tutti gli effetti viene creata la seconda istanza di PlayingCharacter, ma in
     * ogni momento viene mantenuto il riferimento a solo un'istanza.
     *
     * Il personaggio originale viene rimosso dalla stanza per esser posizionato il nuovo
     * personaggio, in posizione di default.
     */
    public static void makePlayerFinalForm()
    {
        if(player == null)
        {
            throw new IllegalStateException("Operazione non possibile, giocatore non creato");
        }

        player.removeFromRoom();
        GameManager.removePiece(player);
        player = new PlayingCharacter(PLAYER_NAME, SCHWARTZ_ROBOT_SPRITE_PATH);
        Room currentRoom = GameManager.getMainFrame().getCurrentRoom();
        player.addInRoom(currentRoom, currentRoom.getDefaultPosition());
    }

    /**
     * Aggiungi all'inventario un PickupableItem.
     *
     * @param it oggetto da aggiungere all'inventario
     */
    public void addToInventory(PickupableItem it)
    {
        inventory.add(it);
        EventHandler.sendEvent(new InventoryEvent(it, InventoryEvent.Type.ADD_ITEM));
    }

    /**
     * Rimuovi un PickupableItem dall'inventario.
     *
     * @param it oggetto da rimuovere dall'inventario
     */
    public void removeFromInventory(PickupableItem it)
    {
        inventory.remove(it);
        EventHandler.sendEvent(new InventoryEvent(it, InventoryEvent.Type.USE_ITEM));
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
