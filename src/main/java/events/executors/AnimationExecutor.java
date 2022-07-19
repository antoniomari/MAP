package events.executors;

import GUI.MainFrame;
import animation.StillAnimation;
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


    public static void executeAnimation(Item it, List<Image> frames)
    {
        new StillAnimation(gameScreenPanel.getLabelAssociated(it), frames, 200, true).start();
    }

    public static void executeEffectAnimation(String whatAnimation, BlockPosition pos)
    {
        gameScreenPanel.effectAnimation(whatAnimation, pos);
    }


}
