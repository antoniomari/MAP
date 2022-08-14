package events.executors;

import GUI.MainFrame;
import animation.StillAnimation;
import entity.GamePiece;
import entity.items.Item;
import entity.rooms.BlockPosition;

import java.util.List;

import java.awt.*;

public class AnimationExecutor extends Executor
{
    private static double scalingFactor;

    public static void setMainFrame(MainFrame frame)
    {
        Executor.setMainFrame(frame);
        scalingFactor = mainFrame.getScalingFactor();
    }


    public static void executeAnimation(GamePiece piece, List<Image> frames)
    {
        executeAnimation(piece, frames, 200);
    }

    public static void executeAnimation(GamePiece piece, List<Image> frames, int delayMilliseconds)
    {
        new StillAnimation(gameScreenPanel.getLabelAssociated(piece), frames, delayMilliseconds, true).start();
    }

    public static void executeEffectAnimation(GamePiece piece, String spritesheetPath, String jsonPath, String whatAnimation, BlockPosition pos, int finalWait)
    {
        gameScreenPanel.effectAnimation(piece, spritesheetPath, jsonPath, whatAnimation, pos, finalWait);
    }


}
