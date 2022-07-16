package events.executors;

import GUI.MainFrame;
import animation.Animation;
import animation.StillAnimation;
import entity.items.Item;
import javax.swing.*;

public class AnimationExecutor extends Executor
{
    private static double scalingFactor;

    public static void setMainFrame(MainFrame frame)
    {
        Executor.setMainFrame(frame);
        scalingFactor = mainFrame.getScalingFactor();
    }

    public static void executeAnimation(Item it, StillAnimation animation)
    {
        animation.compile(gameScreenPanel.getLabelAssociated(it), scalingFactor);
        animation.start();
    }
}
