package animation;

import general.GameManager;
import general.ScenarioMethod;
import graphics.SpriteManager;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Classe che rappresenta un'animazione di una JLabel in cui
 * la label cambia icona ma non posizione.
 */
public class StillAnimation extends Animation
{
    /** Delay di default tra un frame e l'altro */
    private static final int DEFAULT_DELAY_MILLISECONDS = 200;
    /** Flag per aspettare delay tra cambi di frame prima del primo cambio. */
    private static final boolean DEFAULT_INITIAL_DELAY = true;

    /**
     * Delay tra cambi di fotogrammi.
     */
    protected int delayMilliseconds = DEFAULT_DELAY_MILLISECONDS;

    /**
     * Flag per segnalare se c'è delay iniziale.
     */
    protected boolean initialDelay = DEFAULT_INITIAL_DELAY;

    /**
     * Codice da eseguire alla fine dell'animazione.
     */
    protected Runnable onEndExecute;


    @Deprecated
    public StillAnimation(JLabel label, List<Image> frames, int delayMilliseconds, boolean initialDelay)
    {
        this(label, frames, delayMilliseconds, initialDelay, DEFAULT_END_MILLISECONDS);
    }

    @Deprecated
    public StillAnimation(JLabel label, List<Image> frames, int delayMilliseconds, boolean initialDelay, int finalWait,
                          double rescalingFactor)
    {
        super(label, frames, rescalingFactor);
        setDelay(delayMilliseconds);
        setInitialDelay(initialDelay);
        setFinalDelay(finalWait);
    }

    public StillAnimation(JLabel label, List<Image> frames, int delayMilliseconds, boolean initialDelay, int finalWait)
    {
        super(label, frames);
        setDelay(delayMilliseconds);
        setInitialDelay(initialDelay);
        setFinalDelay(finalWait);
    }

    /*

    private void setBlocking(boolean b)
    {
        blocking = b;
    }

     */


    public void setDelay(int milliseconds)
    {
        if (milliseconds < 0)
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

        if (GameManager.getState() != GameManager.GameState.MOVING)
            GameManager.changeState(GameManager.GameState.MOVING);

        for (Icon frame : frameIcons)
        {
            try
            {
                if (delay)
                    Thread.sleep(delayMilliseconds);
                else
                    delay = true;

                label.setIcon(frame);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        //GameState.changeState(GameState.State.PLAYING);
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
    @ScenarioMethod
    protected void terminate()
    {
        if (onEndExecute != null)
            onEndExecute.run();

        GameManager.continueScenario();
    }


    /**
     * Static factory per la creazione di un'animazione personalizzata.
     *
     * @param spritesheetPath path dello spritesheet da cui ricavare i fotogrammi
     *                        dell'animazione
     * @param jsonPath        path del json che contiene i dati dei fotogrammi dell'animazione
     * @param animationLabel  JLabel da animare
     * @return StillAnimation creata
     */
    public static StillAnimation createCustomAnimation(String spritesheetPath, String jsonPath, String name,
                                                       JLabel animationLabel, double rescalingFactor)
    {
        BufferedImage spriteSheet = SpriteManager.loadSpriteSheet(spritesheetPath);
        List<Image> frames = SpriteManager.getKeywordOrderedFrames(spriteSheet, jsonPath, name);

        // TODO: personalizzare delay milliseconds
        return new StillAnimation(animationLabel, frames, 100, true, DEFAULT_END_MILLISECONDS,
                rescalingFactor);
    }
}
