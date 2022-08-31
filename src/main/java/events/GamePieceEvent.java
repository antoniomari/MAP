package events;

import entity.GamePiece;
import entity.rooms.BlockPosition;

/**
 * Classe che rappresenta un evento di gioco legato a un {@link entity.GamePiece},
 * inviato da un metodo di tale classe all'{@link EventHandler}.
 */
public class GamePieceEvent extends GameEvent
{
    private final Type type;
    private BlockPosition oldPosition;
    private BlockPosition newPosition;
    private final GamePiece pieceInvolved;
    private int millisecondWaitEnd;

    public enum Type
    {
        /**
         * Rappresenta il fatto che un GamePiece si è spostato da una posizione
         * a un'altra nella stanza.
         *
         * Un evento di questo tipo utilizza:
         * <ul>
         *     <li>{@link GamePieceEvent#pieceInvolved}</li>
         *     <li>{@link GamePieceEvent#newPosition}</li>
         * </ul>
         */
        MOVE
                {
                    @Override
                    public String toString()
                    {
                        return "si è spostato";
                    }
                },
        UPDATE_SPRITE
                {
                    public String toString()
                    {
                        return " aggiornato sprite";
                    }
                },
    }

    public GamePieceEvent(GamePiece pieceInvolved, Type type)
    {
        super(type.toString());
        this.type = type;
        this.pieceInvolved = pieceInvolved;
    }

    public void setNewPosition(BlockPosition newPosition)
    {
        this.newPosition = newPosition;
    }

    public BlockPosition getNewPosition()
    {
        return newPosition;
    }

    public BlockPosition getOldPosition()
    {
        return oldPosition;
    }

    public void setOldPosition(BlockPosition oldPosition)
    {
        this.oldPosition = oldPosition;
    }

    public int getMillisecondWaitEnd()
    {
        return millisecondWaitEnd;
    }

    public void setMillisecondWaitEnd(int millisecondWaitEnd)
    {
        this.millisecondWaitEnd = millisecondWaitEnd;
    }

    public Type getType()
    {
        return type;
    }

    public GamePiece getPieceInvolved()
    {
        return pieceInvolved;
    }

    public String getEventString()
    {
        if(type == GamePieceEvent.Type.MOVE)
            return eventTime.toString() + " -> " + " [" + pieceInvolved + "] "+ type
                    + " in posizione " + newPosition;
        else
            return "NON PRESENTE";
    }
}
