package events;


import GUI.MainFrame;
import animation.Animation;
import events.executors.AnimationExecutor;
import items.Item;

import javax.swing.*;

public class EventHandler
{

    public static void printEvent(GameEvent ge)
    {
        System.out.println(ge.getEventString());

        if(ge instanceof ItemInteractionEvent)
            if (((ItemInteractionEvent) ge).hasAnimation())
                AnimationExecutor.executeAnimation(((ItemInteractionEvent) ge).getAnimation());
    }

}
