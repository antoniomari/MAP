package events.executors;

import animation.PerpetualAnimation;
import animation.StillAnimation;
import entity.GamePiece;
import entity.rooms.BlockPosition;
import general.GameManager;

import java.util.List;

import java.awt.*;

public class AnimationExecutor extends Executor
{

    private static PerpetualAnimation speakAnimation;

    public static void executeAnimation(GamePiece piece, List<Image> frames)
    {
        executeAnimation(piece, frames, 200);
    }

    public static void executeAnimation(GamePiece piece, List<Image> frames, int delayMilliseconds)
    {
        new StillAnimation(
                gameScreenPanel.getLabelAssociated(piece), frames, delayMilliseconds, true).start();
    }

    public static void executeSpeakAnimation(GamePiece piece, List<Image> frames,
                                                           int delayMilliseconds, GameManager.GameState runningState)
    {
        if(speakAnimation != null)
            speakAnimation.stop();

        speakAnimation = PerpetualAnimation.animateWhileGameState(gameScreenPanel.getLabelAssociated(piece), frames, delayMilliseconds, false, runningState);
        speakAnimation.setEndIcon(piece.getScaledIconSprite(mainFrame.getGameScreenPanel().getScalingFactor()));
        speakAnimation.start();
    }

    public static void executeEffectAnimation(GamePiece piece, String spritesheetPath, String jsonPath,
                                              String whatAnimation, BlockPosition pos, int finalWait)
    {
        gameScreenPanel.effectAnimation(piece, spritesheetPath, jsonPath, whatAnimation, pos, finalWait);
    }

    public static void executePerpetualEffectAnimation(GamePiece piece, String spritesheetPath, String jsonPath,
                                                       String whatAnimation, BlockPosition pos, int finalWait)
    {
        gameScreenPanel.perpetualEffectAnimation(piece, spritesheetPath, jsonPath, whatAnimation, pos, finalWait);
    }

}
