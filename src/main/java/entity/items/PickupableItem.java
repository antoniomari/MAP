package entity.items;


import entity.characters.PlayingCharacter;
import events.EventHandler;
import events.InventoryEvent;
import events.RoomEvent;
import entity.rooms.BlockPosition;
import entity.rooms.Room;

public class PickupableItem extends Item
{
    // costruttore che inizializza l'oggetto come presente nell'inventario
    public PickupableItem(String name, String description)
    {
        super(name, description);
    }


    public void pickup()
    {
        // generato evento togliStanza
        removeFromRoom(); // setta a null la stanza

        PlayingCharacter.getPlayer().addToInventory(this);

    }

    public void drop(Room room, BlockPosition pos)
    {
        //aggiungi alla stanza
        addInRoom(room, pos);

        // rimuovi dall'inventario
        PlayingCharacter.getPlayer().removeFromInventory(this);
    }

    // TODO: useWith(Item)
}
