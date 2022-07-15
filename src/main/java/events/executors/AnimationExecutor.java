package events.executors;

import GUI.MainFrame;
import animation.Animation;
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

    public static void executeAnimation(Animation animation)
    {
        Item itemToAnimate = animation.getItem();
        JLabel labelToAnimate = gameScreenPanel.getLabelAssociated(itemToAnimate);
        animation.setLabel(labelToAnimate);
        animation.rescaleFrames(scalingFactor);
        animation.start();
    }
}
