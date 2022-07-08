package events;

import characters.PlayingCharacter;
import items.PickupableItem;
import rooms.Room;

public class RoomEvent extends GameEvent
{
    Type type;
    Room roomInvolved;
    public enum Type
    {
        ADD_ITEM_IN_ROOM
        {
            public String toString()
            {
                return "aggiunto alla stanza";
            }
        },

        REMOVE_ITEM_FROM_ROOM
        {
            @Override
            public String toString()
            {
                return "rimosso dalla stanza";
            }
        }
    }

    public RoomEvent(Room room, PickupableItem item, Type type)
    {
        this(room, item, type.toString());
        this.type = type;
    }

    public RoomEvent(Room room, PickupableItem item, String toPrint)
    {
        super(item, toPrint);
        this.roomInvolved = room;
    }

    public Room getRoomInvolved()
    {
        return roomInvolved;
    }

    public Type getType()
    {
        return type;
    }
}