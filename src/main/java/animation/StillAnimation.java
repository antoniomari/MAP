package animation;

import entity.rooms.BlockPosition;
import graphics.SpriteManager;
import entity.items.Item;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// animazione statica
public class StillAnimation extends Animation
{
    private final int delayMilliseconds;
    private final boolean initialDelay;
    private Runnable onEndExecute;


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

    public void setActionOnEnd(Runnable action)
    {
        onEndExecute = action;
    }

    @Override
    protected void terminate()
    {
        if(onEndExecute != null)
            onEndExecute.run();
    }


    public static StillAnimation createExplosionAnimation(JLabel animationLabel, BlockPosition pos)
    {
        BufferedImage spriteSheet = SpriteManager.loadSpriteSheet("/img/animazioni/esplosione.png");
        List<Image> frames = SpriteManager.getOrderedFrames(spriteSheet, "/img/animazioni/esplosione.json");

        return new StillAnimation(animationLabel, frames, 100, true);

    }

}
