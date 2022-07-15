package events;

import entity.GamePiece;
import entity.characters.GameCharacter;
import entity.items.Item;
import entity.rooms.BlockPosition;
import entity.rooms.Room;

public class RoomEvent extends GameEvent
{
    Type type;

    public enum Type
    {
        ADD_PIECE_IN_ROOM
        {
            public String toString()
            {
                return " aggiunto alla stanza";
            }
        },

        REMOVE_PIECE_FROM_ROOM
        {
            @Override
            public String toString()
            {
                return " rimosso dalla stanza";
            }
        }
    }

    public RoomEvent(Room room, GamePiece p, Type type)
    {
        this(room, p, null, type);
    }

    public RoomEvent(Room room, GamePiece p, BlockPosition pos, Type type)
    {
        super(type.toString());

        if(p instanceof GameCharacter)
            this.characterInvolved = (GameCharacter) p;
        else if(p instanceof Item)
            this.itemInvolved = (Item) p;

        this.roomInvolved = room;
        this.type = type;
        this.pos = pos;

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
        return pos;
    }

    @Override
    public String getEventString()
    {
        String s  = eventTime.toString() + " -> ";
        if(itemInvolved != null)
            s += " [" + roomInvolved + "] " + itemInvolved + type;
        else
            s += " [" + roomInvolved + "] " + characterInvolved + type;

        if(pos != null)
            s = s + " in posizione " + pos;

        return s;
    }
}
