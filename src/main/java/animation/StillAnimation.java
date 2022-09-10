package animation;

import general.GameManager;
import general.ScenarioMethod;
import graphics.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta un'animazione di una JLabel in cui
 * la label cambia icona ma non posizione.
 */
public class StillAnimation extends Animation
{
    /** Delay di default tra un frame e l'altro */
    public static final int DEFAULT_DELAY_MILLISECONDS = 200;
    /** Flag per aspettare delay tra cambi di frame prima del primo cambio. */
    private static final boolean DEFAULT_INITIAL_DELAY = true;

    /** Delay tra cambi di fotogrammi. */
    protected int delayMilliseconds = DEFAULT_DELAY_MILLISECONDS;
    /** Flag per segnalare se c'è delay iniziale. */
    protected boolean initialDelay = DEFAULT_INITIAL_DELAY;
    /** Codice da eseguire alla fine dell'animazione. */
    protected Runnable onEndExecute;


    /**
     * Crea una StillAnimation, impostando il fattore di riscalamento.
     *
     * @param label JLabel da animare
     * @param frames fotogrammi che compongono l'animazione
     * @param delayMilliseconds millisecondi di delay tra cambi di frame
     * @param initialDelay flag se indica se attendere delayMillisecond all'inizio dell'animazione
     * @param finalWait millisecondi di attesa alla fine dell'animazione
     * @param rescalingFactor fattore di riscalamento per le icone
     */
    public StillAnimation(JLabel label, List<Image> frames,
                          int delayMilliseconds, boolean initialDelay,
                          int finalWait, double rescalingFactor)
    {
        super(label, frames, rescalingFactor);
        initDelay(delayMilliseconds, initialDelay, finalWait);
    }

    /**
     * Crea una StillAnimation, non impostando il fattore di riscalamento.
     *
     * @param label JLabel da animare
     * @param frames fotogrammi che compongono l'animazione
     * @param delayMilliseconds millisecondi di delay tra cambi di frame
     * @param initialDelay flag se indica se attendere delayMillisecond all'inizio dell'animazione
     * @param finalWait millisecondi di attesa alla fine dell'animazione
     */
    public StillAnimation(JLabel label, List<Image> frames, int delayMilliseconds, boolean initialDelay, int finalWait)
    {
        super(label, frames);
        initDelay(delayMilliseconds, initialDelay, finalWait);

    }

    private void initDelay(int delay, boolean initialDelay, int finalDelay)
    {
        setDelay(delay);
        setInitialDelay(initialDelay);
        setFinalDelay(finalDelay);
    }

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

        // imposta GameState animation
        if (GameManager.getState() != GameManager.GameState.ANIMATION)
            GameManager.changeState(GameManager.GameState.ANIMATION);

        // itera sui frames e aspetta delayMillisecond ogni cambio di frame
        try
        {
            for (Icon frame : frameIcons)
            {

                    if (delay)
                        Thread.sleep(delayMilliseconds);
                    else
                        delay = true;

                    label.setIcon(frame);
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Permette d'impostare del codice da eseguire alla fine
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
        // esegui runnable impostato
        if (onEndExecute != null)
            onEndExecute.run();

        GameManager.continueScenario();
    }


    /**
     * Static factory per la creazione di un'animazione personalizzata.
     *
     * Nota: i fotogrammi vengono ricavati dal json cercando oggetti JSON con le
     * keyword {@code "[name]1", "[name]2", ...}, dove [name] corrisponde al parametro
     * {@code name}.
     *
     * @param spriteSheetPath path dello spriteSheet da cui ricavare i fotogrammi
     *                        dell'animazione
     * @param jsonPath        path del json che contiene i dati dei fotogrammi dell'animazione
     * @param name nome dell'animazione (da ricercare nel json)
     * @param animationLabel JLabel da animare
     * @param rescalingFactor fattore di riscalamento per i frames
     * @return StillAnimation creata
     */
    public static StillAnimation createCustomAnimation(String spriteSheetPath, String jsonPath, String name,
                                                       JLabel animationLabel, double rescalingFactor)
    {
        Objects.requireNonNull(spriteSheetPath);
        Objects.requireNonNull(jsonPath);
        Objects.requireNonNull(name);
        Objects.requireNonNull(animationLabel);

        BufferedImage spriteSheet = SpriteManager.loadImage(spriteSheetPath);
        List<Image> frames = SpriteManager.getKeywordOrderedFrames(spriteSheet, jsonPath, name);

        // in futuro: dare la possibilità di personalizzare delayMilliseconds
        return new StillAnimation(animationLabel, frames,
                100, true, DEFAULT_END_MILLISECONDS, rescalingFactor);
    }
}
