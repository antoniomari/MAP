package items;


import characters.PlayingCharacter;
import events.EventHandler;
import events.InventoryEvent;
import events.RoomEvent;
import rooms.BlockPosition;
import rooms.Room;

public class PickupableItem extends Item
{
    // costruttore che inizializza l'oggetto come presente nell'inventario
    public PickupableItem(String name, String description)
    {
        this(name, description, null);
    }

    public PickupableItem(String name, String description, Room locationRoom)
    {
        super(name, description);
        setLocationRoom(locationRoom);
    }


    public void pickup()
    {
        // generato evento togliStanza
        Room oldRoom = getLocationRoom();
        oldRoom.removePiece(this); // rimuovi this dalla stanza in cui è contenuto
        setLocationRoom(null); // setta a null la stanza
        EventHandler.sendEvent(new RoomEvent(oldRoom, this, RoomEvent.Type.REMOVE_ITEM_FROM_ROOM));

        // generato evento aggiungiInventario TODO: togliere spicolo
        PlayingCharacter.getPlayer().addToInventory(this);
        EventHandler.sendEvent(new InventoryEvent(this, InventoryEvent.Type.ADD_ITEM));
    }

    public void drop(Room room, BlockPosition coord)
    {
        //aggiungi alla stanza
        room.addPiece(this, coord);
        setLocationRoom(room);
        EventHandler.sendEvent(new RoomEvent(room, this, coord, RoomEvent.Type.ADD_ITEM_IN_ROOM));

        // rimuovi dall'inventario
        PlayingCharacter.getPlayer().removeFromInventory(this);
        EventHandler.sendEvent(new InventoryEvent(this, InventoryEvent.Type.USE_ITEM));
    }
}
