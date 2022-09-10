package events;

import entity.GamePiece;
import entity.rooms.BlockPosition;

import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta un evento di gioco legato a un {@link entity.GamePiece}.
 */
public class GamePieceEvent extends GameEvent
{
    private final Type type;
    private BlockPosition oldPosition;
    private BlockPosition newPosition;
    private final GamePiece pieceInvolved;
    private int millisecondWaitEnd;

    // dati per mandare informazioni sull'animazione da eseguire (eventuale)
    private String spritesheetPath;
    private String jsonPath;
    private String animationName;

    private List<Image> frames;

    public enum Type
    {
        /**
         * Rappresenta il fatto che un GamePiece si è spostato da una posizione
         * a un'altra nella stanza.
         */
        MOVE
                {
                    @Override
                    public String toString()
                    {
                        return "si è spostato";
                    }
                },
        /** Rappresenta l'aggiornamento di sprite di un GamePiece. */
        UPDATE_SPRITE
                {
                    public String toString()
                    {
                        return " aggiornato sprite";
                    }
                },
        /** Rappresenta il dover eseguire l'animazione personalizzata del GamePiece. */
        PIECE_ANIMATION
                {
                    public String toString()
                    {
                        return " animato";
                    }
                },
        /** Rappresenta il dover eseguire un effetto animato sul GamePiece. */
        EFFECT_ANIMATION
                {
                    public String toString()
                    {
                        return " effetto animato";
                    }
                },
        /** Rappresenta il dover eseguire un effetto animato perpetuo sul GamePiece. */
        PERPETUAL_EFFECT_ANIMATION
                {
                    public String toString()
                    {
                        return " effetto animato continuo";
                    }
                }
    }

    public GamePieceEvent(GamePiece pieceInvolved, Type type)
    {
        super(type.toString());
        this.type = type;
        this.pieceInvolved = pieceInvolved;
    }

    private GamePieceEvent(GamePiece pieceInvolved, List<Image> frames, Type type)
    {
        super(type.toString());
        this.type = type;
        this.pieceInvolved = pieceInvolved;
        this.frames = frames;
    }

    public static GamePieceEvent makePieceAnimationEvent(GamePiece pieceInvolved, List<Image> frames)
    {
        return new GamePieceEvent(pieceInvolved, frames, Type.PIECE_ANIMATION);
    }


    public List<Image> getFrames()
    {
        return frames;
    }

    public void setNewPosition(BlockPosition newPosition)
    {
        this.newPosition = newPosition;
    }

    BlockPosition getNewPosition()
    {
        return newPosition;
    }

    BlockPosition getOldPosition()
    {
        return oldPosition;
    }

    public void setOldPosition(BlockPosition oldPosition)
    {
        this.oldPosition = oldPosition;
    }

    int getMillisecondWaitEnd()
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

    @Override
    public String getEventString()
    {
        String pre = "[" + eventTime.toString() + "] -> " + " [" + pieceInvolved + "] " + type;
        if(type == GamePieceEvent.Type.MOVE)
            return pre + " in posizione " + newPosition;
        else
            return pre;
    }

    /**
     * Imposta parametri dell'animazione.
     *
     * @param spritesheetPath path dello sprite-sheet contenente i frames
     * @param jsonPath path del json relativo allo sprite-sheet
     * @param animationName nome dell'animazione
     */
    public void setAnimationInfo(String spritesheetPath, String jsonPath, String animationName)
    {
        this.spritesheetPath = Objects.requireNonNull(spritesheetPath);
        this.jsonPath = Objects.requireNonNull(jsonPath);
        this.animationName = Objects.requireNonNull(animationName);
    }

    String getAnimationSpritesheet()
    {
        return spritesheetPath;
    }

    String getAnimationJson()
    {
        return jsonPath;
    }

    String getAnimationName()
    {
        return animationName;
    }
}
