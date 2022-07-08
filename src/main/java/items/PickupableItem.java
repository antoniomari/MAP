package items;


import characters.PlayingCharacter;
import events.EventHandler;
import events.InventoryEvent;
import events.RoomEvent;
import rooms.Room;


public class PickupableItem extends Item
{
    Room locationRoom; // stanza in cui è contenuto, se è null allora vuol dire che è nell'inventario

    // costruttore che inizializza l'oggetto come presente nell'inventario
    public PickupableItem(String name, String description)
    {
        this(name, description, null);
    }

    public PickupableItem(String name, String description, Room locationRoom)
    {
        super(name, description);
        this.locationRoom = locationRoom;
    }

    public void setLocationRoom(Room room)
    {
        this.locationRoom = room;
    }

    public Room getLocationRoom()
    {
        return locationRoom;
    }


    public void pickup()
    {
        // generato evento togliStanza
        Room oldRoom = getLocationRoom();
        oldRoom.removeItem(this); // rimuovi this dalla stanza in cui è contenuto
        setLocationRoom(null); // setta a null la stanza
        EventHandler.printEvent(new RoomEvent(oldRoom, this, RoomEvent.Type.REMOVE_ITEM_FROM_ROOM));

        // generato evento aggiungiInventario TODO: togliere spicolo
        PlayingCharacter.SPICOLO.addToInventory(this);
        EventHandler.printEvent(new InventoryEvent(PlayingCharacter.SPICOLO, this, InventoryEvent.Type.ADD_ITEM));
    }
}
