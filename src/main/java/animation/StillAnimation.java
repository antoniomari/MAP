package animation;

import graphics.SpriteManager;
import entity.items.Item;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// animazione statica
public class StillAnimation extends Animation
{
    private final int delayMilliseconds;
    private final boolean initialDelay;


    public StillAnimation(JLabel label, List<Image> frames, int delayMilliseconds, boolean initialDelay)
    {
        super(label, frames);
        this.delayMilliseconds = delayMilliseconds;
        this.initialDelay = initialDelay;
    }

    @Override
    protected void execute()
    {
        boolean delay = initialDelay;

        for(Icon frame : frameIcons)
        {
            try
            {
                if(delay)
                    Thread.sleep(delayMilliseconds);
                else
                    delay = true;

                label.setIcon(frame);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
