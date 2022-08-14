package events;

import entity.GamePiece;

import java.awt.*;
import java.util.List;

public class AnimationEvent extends GameEvent
{
    private GamePiece pieceToAnimate;
    private List<Image> frames;

    public AnimationEvent(GamePiece pieceToAnimate, List<Image> frames)
    {
        super("still animation");
        this.pieceToAnimate = pieceToAnimate;
        this.frames = frames;
    }

    public GamePiece getPieceInvolved()
    {
        return pieceToAnimate;
    }

    public List<Image> getFrames()
    {
        return frames;
    }
}
