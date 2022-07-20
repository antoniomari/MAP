package animation;

import entity.rooms.BlockPosition;
import general.GameManager;
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
        this(label, frames, delayMilliseconds, initialDelay, 0);
    }

    public StillAnimation(JLabel label, List<Image> frames, int delayMilliseconds, boolean initialDelay, int finalWait)
    {
        super(label, frames);
        this.delayMilliseconds = delayMilliseconds;
        this.initialDelay = initialDelay;
        this.millisecondWaitEnd = finalWait;
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

        GameManager.continueScenario();
    }


    public static StillAnimation createExplosionAnimation(String spritesheetPath, String jsonPath,
                                                          JLabel animationLabel, int finalWait)
    {
        BufferedImage spriteSheet = SpriteManager.loadSpriteSheet(spritesheetPath);
        List<Image> frames = SpriteManager.getOrderedFrames(spriteSheet, jsonPath);

        return new StillAnimation(animationLabel, frames, 100, true, finalWait);
    }
}
