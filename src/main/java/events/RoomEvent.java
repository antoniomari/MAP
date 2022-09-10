package events;

import entity.GamePiece;
import entity.rooms.BlockPosition;
import entity.rooms.Room;

/**
 * Rappresenta un evento generato dall'aggiunta
 * o dalla rimozione di un GamePiece in una stanza.
 */
public class RoomEvent extends GameEvent
{
    /** Tipo di evento. */
    private final Type type;
    /** GamePiece coinvolto nell'evento. */
    private final GamePiece pieceInvolved;
    /** Room coinvolta nell'evento. */
    private final Room roomInvolved;
    /** Posizione di riferimento. */
    private final BlockPosition pos;

    /**
     * Tipo di evento RoomEvent.
     */
    public enum Type
    {
        /** Aggiunta di un GamePiece nella stanza. */
        ADD_PIECE_IN_ROOM
        {
            public String toString()
            {
                return " aggiunto alla stanza";
            }
        },

        /** Rimozione di un GamePiece dalla stanza. */
        REMOVE_PIECE_FROM_ROOM
        {
            @Override
            public String toString()
            {
                return " rimosso dalla stanza";
            }
        }
    }


    public static RoomEvent makeRemoveEvent(Room room, GamePiece piece)
    {
        return new RoomEvent(room, piece, null, Type.REMOVE_PIECE_FROM_ROOM);
    }

    public static RoomEvent makeAddEvent(Room room, GamePiece piece, BlockPosition pos)
    {
        return new RoomEvent(room, piece, pos, Type.ADD_PIECE_IN_ROOM);
    }

    private RoomEvent(Room room, GamePiece p, BlockPosition pos, Type type)
    {
        super(type.toString());

        this.pieceInvolved = p;

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

        s += " [" + roomInvolved + "] " + pieceInvolved + type;

        if(pos != null)
            s = s + " in posizione " + pos;

        return s;
    }

    public GamePiece getPieceInvolved()
    {
        return pieceInvolved;
    }
}
