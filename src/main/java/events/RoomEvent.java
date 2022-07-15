package events;

import entity.items.PickupableItem;
import entity.rooms.BlockPosition;
import entity.rooms.Room;

public class RoomEvent extends GameEvent
{
    Type type;
    Room roomInvolved;
    BlockPosition coord;
    public enum Type
    {
        ADD_ITEM_IN_ROOM
        {
            public String toString()
            {
                return "aggiunto alla stanza";
            }
        },

        ADD_CHARACTER_IN_ROOM
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
        this(room, item, null, type);
    }

    public RoomEvent(Room room, PickupableItem item, BlockPosition coord, Type type)
    {
        super(item, type.toString());
        this.roomInvolved = room;
        this.type = type;
        this.coord = coord;

    }
    public Room getRoomInvolved()
    {
        return roomInvolved;
    }

    public Type getType()
    {
        return type;
    }

    public BlockPosition getCoordinates()
    {
        return coord;
    }
}
