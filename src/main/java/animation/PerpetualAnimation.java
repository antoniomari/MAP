package animation;

import general.GameException;
import general.GameManager;
import graphics.SpriteManager;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

// animazione statica
public class PerpetualAnimation extends StillAnimation
{

    public PerpetualAnimation(JLabel label, List<Image> frames, int delayMilliseconds, boolean initialDelay)
    {
        super(label, frames, delayMilliseconds, initialDelay);
    }

    public PerpetualAnimation(JLabel label, List<Image> frames, int delayMilliseconds, boolean initialDelay, int finalWait,
                          double rescalingFactor)
    {
        super(label, frames, delayMilliseconds, initialDelay, finalWait, rescalingFactor);
    }

    @Override
    protected void execute()
    {
        boolean delay = initialDelay;

        // todo: rimpiazzare thread.sleep urgente
        while(true)
        {
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

            try
            {
                Thread.sleep(millisecondWaitEnd);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

    }



    /**
     * Static factory per la creazione di un'animazione personalizzata.
     *
     * @param spritesheetPath path dello spritesheet da cui ricavare i fotogrammi
     *                        dell'animazione
     * @param jsonPath path del json che contiene i dati dei fotogrammi dell'animazione
     * @param animationLabel JLabel da animare
     *
     * @return StillAnimation creata
     */
    public static PerpetualAnimation createPerpetualAnimation(String spritesheetPath, String jsonPath,
                                                       JLabel animationLabel)
    {
        BufferedImage spriteSheet = SpriteManager.loadSpriteSheet(spritesheetPath);
        List<Image> frames = SpriteManager.getOrderedFrames(spriteSheet, jsonPath);

        // TODO: personalizzare delay milliseconds
        return new PerpetualAnimation(animationLabel, frames, 100, true);
    }


    public static PerpetualAnimation createPerpetualAnimation(String spritesheetPath, String jsonPath, String name,
                                                       JLabel animationLabel, double rescalingFactor)
    {
        BufferedImage spriteSheet = SpriteManager.loadSpriteSheet(spritesheetPath);
        List<Image> frames = SpriteManager.getKeywordOrderedFrames(spriteSheet, jsonPath, name);

        // TODO: personalizzare delay milliseconds
        return new PerpetualAnimation(animationLabel, frames, 100, true, DEFAULT_END_MILLISECONDS,
                rescalingFactor);
    }


}

