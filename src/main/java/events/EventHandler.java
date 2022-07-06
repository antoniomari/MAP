package events;


import GUI.MainFrame;
import animation.Animation;
import items.Item;

import javax.swing.*;

public class EventHandler
{

    public static void printEvent(GameEvent ge)
    {
        System.out.println(ge.getEventString());

        if(ge instanceof ItemInteractionEvent)
            if (((ItemInteractionEvent) ge).hasAnimation())
                performAnimation((ItemInteractionEvent) ge);

    }


    // nota: servirà spostare questo in un'altra classe
    private static void performAnimation(ItemInteractionEvent e)
    {

        Animation animation = e.getAnimation();
        Item item = e.getItemInvolved();
        JLabel label = MainFrame.getLabelAssociated(item);

        animation.setLabel(label);
        animation.rescaleFrames(MainFrame.getScalingFactor());
        animation.start();

    }
}
