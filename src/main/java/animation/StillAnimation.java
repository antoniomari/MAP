package animation;

import general.GameException;
import general.GameManager;
import graphics.SpriteManager;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

// animazione statica
public class StillAnimation extends Animation
{
    private int delayMilliseconds;
    private boolean initialDelay;
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
        setFinalDelay(finalWait);
    }

    public void setDelay(int milliseconds)
    {
        if(milliseconds < 0)
            throw new IllegalArgumentException("tempo di delay negativo");

        this.delayMilliseconds = milliseconds;
    }

    public void setInitialDelay(boolean initialDelay)
    {
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

    /**
     * Permette di impostare del codice da eseguire alla fine
     * dell'animazione.
     *
     * @param action runnable da eseguire alla fine dell'animazione
     */
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


    /**
     * Static factory per la creazione di un'animazione personalizzata.
     *
     * @param spritesheetPath path dello spritesheet da cui ricavare i fotogrammi
     *                        dell'animazione
     * @param jsonPath path del json che contiene i dati dei fotogrammi dell'animazione
     * @param animationLabel JLabel da animare
     * @param finalWait millisecondi da attendere alla fine dell'esecuzione dell'animazione
     *
     * @return StillAnimation creata
     */
    public static StillAnimation createCustomAnimation(String spritesheetPath, String jsonPath,
                                                          JLabel animationLabel, int finalWait)
    {
        BufferedImage spriteSheet = SpriteManager.loadSpriteSheet(spritesheetPath);
        List<Image> frames = SpriteManager.getOrderedFrames(spriteSheet, jsonPath);

        // TODO: personalizzare delay milliseconds
        return new StillAnimation(animationLabel, frames, 100, true, finalWait);
    }
}
