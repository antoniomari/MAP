package events.executors;

import animation.StillAnimation;
import entity.GamePiece;
import entity.rooms.BlockPosition;

import java.security.Signature;
import java.util.List;

import java.awt.*;

public class AnimationExecutor extends Executor
{

    public static void executeAnimation(GamePiece piece, List<Image> frames)
    {
        executeAnimation(piece, frames, 200);
    }

    public static void executeAnimation(GamePiece piece, List<Image> frames, int delayMilliseconds)
    {
        new StillAnimation(gameScreenPanel.getLabelAssociated(piece), frames, delayMilliseconds, true).start();
    }

    /*
    public static void executeNonBlockingAnimation(GamePiece piece, List<Image> frames, int delayMilliseconds)
    {
        StillAnimation.createNonBlockingAnimation(gameScreenPanel.getLabelAssociated(piece), frames, delayMilliseconds, true).start();
    }

     */
    public static void executeEffectAnimation(GamePiece piece, String spritesheetPath, String jsonPath, String whatAnimation, BlockPosition pos, int finalWait)
    {
        gameScreenPanel.effectAnimation(piece, spritesheetPath, jsonPath, whatAnimation, pos, finalWait);
    }
}
