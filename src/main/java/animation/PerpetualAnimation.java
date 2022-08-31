package animation;

import GUI.gamestate.GameState;
import general.GameException;
import general.GameManager;
import graphics.SpriteManager;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

// animazione statica
public class PerpetualAnimation extends StillAnimation
{

    private boolean canRun = true;
    private GameState.State runningState;
    private boolean stateEnabled = false;

    public PerpetualAnimation(JLabel label, List<Image> frames, int delayMilliseconds, boolean initialDelay)
    {
        super(label, frames, delayMilliseconds, initialDelay);
    }

    private PerpetualAnimation(JLabel label, List<Image> frames, int delayMilliseconds, boolean initialDelay, int finalWait,
                          double rescalingFactor)
    {
        super(label, frames, delayMilliseconds, initialDelay, finalWait, rescalingFactor);
    }

    @Override
    protected void execute()
    {
        boolean delay = initialDelay;

        // todo: rimpiazzare thread.sleep urgente
        try
        {
            while(canRun)
                for (Icon frame : frameIcons)
                {
                    // se lo stato è abilitato controlla ed eventualmente fermati
                    if(stateEnabled && GameState.getState() != runningState)
                        return;

                    if (delay)
                        Thread.sleep(delayMilliseconds);
                    else
                        delay = true;

                    label.setIcon(frame);
                }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            System.out.println("Animazione interrotta");
        }

    }

    @Override
    protected void terminate()
    {
        if(onEndExecute != null)
            onEndExecute.run();
    }

    /**
     * Ferma l'animazione, se questa è iniziata;
     * altrimento non fa nulla.
     */
    public void stop()
    {
        if(thread != null)
        {
            canRun = false;
            try
            {
                thread.join();
                canRun = true;
            }
            catch(InterruptedException e)
            {
                throw new GameException("Errore in thread animazione");
            }
        }
    }

    public static PerpetualAnimation animateWhileGameState(JLabel label, List<Image> frames, int delayMilliseconds, boolean initialDelay, GameState.State runningState)
    {
        Objects.requireNonNull(runningState);

        PerpetualAnimation anim = new PerpetualAnimation(label, frames, delayMilliseconds, initialDelay);
        anim.stateEnabled = true;
        anim.runningState = runningState;

        return anim;

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

