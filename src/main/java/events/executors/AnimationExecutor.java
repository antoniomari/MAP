package events.executors;

import GUI.MainFrame;
import animation.Animation;
import items.Item;

import javax.swing.*;

public class AnimationExecutor //TODO: valutare se creare classe AbstractExecutor da cui gli esecutori derivano
{
    private static MainFrame mainFrame;

    public static void setMainFrame(MainFrame frame)
    {
        mainFrame = frame;
    }

    public static void executeAnimation(Animation animation)
    {
        Item itemToAnimate = animation.getItem();
        JLabel label = mainFrame.getLabelAssociated(itemToAnimate);
        animation.setLabel(label);
        animation.rescaleFrames(mainFrame.getScalingFactor());
        animation.start();

    }
}
