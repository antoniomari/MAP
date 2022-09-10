package animation;

import general.GameManager;
import graphics.SpriteManager;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta un'animazione perpetua di una JLabel.
 * L'animazione data dal susseguirsi dei frames impostati viene ripetuta
 * finché un evento esterno non la ferma.
 */
public class PerpetualAnimation extends StillAnimation
{
    /** Icona da impostare all'interruzione dell'animazione. */
    private Icon endIcon;
    /**
     * {@link GameManager.GameState} in cui l'animazione deve continuare.
     *
     * Nota: se il controllo sul GameState non è abilitato allora l'animazione
     * dev'essere interrotta da una chiamata esterna al metodo {@link PerpetualAnimation#stop()}.
     */
    private GameManager.GameState runningState;
    /** Flag che indica se il controllo sul runningState è abilitato*/
    private boolean stateEnabled = false;

    /**
     * Crea un'animazione perpetua.
     *
     * @param label JLabel da animare
     * @param frames fotogrammi che costituiscono l'animazione
     * @param delayMilliseconds millisecondi da attendere per cambio di fotogramma
     * @param initialDelay flag che indica se all'inizio dell'animazione bisogna attendere delayMilliseconds
     */
    public PerpetualAnimation(JLabel label, List<Image> frames, int delayMilliseconds, boolean initialDelay)
    {
        super(label, frames, delayMilliseconds, initialDelay, DEFAULT_DELAY_MILLISECONDS);
    }

    /**
     * Crea un'animazione perpetua, impostando il fattore di riscalamento per le icone.
     *
     * @param label JLabel da animare
     * @param frames fotogrammi che costituiscono l'animazione
     * @param delayMilliseconds millisecondi da attendere per cambio di fotogramma
     * @param initialDelay flag che indica se all'inizio dell'animazione bisogna attendere delayMilliseconds
     * @param finalWait millisecondi da attendere alla fine (interruzione) dell'animazione
     * @param rescalingFactor fattore di riscalamento per i frames
     */
    private PerpetualAnimation(JLabel label, List<Image> frames, int delayMilliseconds, boolean initialDelay,
                               int finalWait, double rescalingFactor)
    {
        super(label, frames, delayMilliseconds, initialDelay, finalWait, rescalingFactor);
    }

    /**
     * Imposta icona da impostare all'interruzione dell'animazione.
     *
     * @param endIcon icona da impostare all'interruzione dell'animazione
     */
    public void setEndIcon(Icon endIcon)
    {
        Objects.requireNonNull(endIcon);

        this.endIcon = endIcon;
    }

    @Override
    protected void execute()
    {
        boolean delay = initialDelay;

        try
        {
            // itera all'infinito su iterazioni di tutti i frames
            while(true)
                for (Icon frame : frameIcons)
                {
                    // se lo stato è abilitato controlla ed eventualmente fermati
                    if(stateEnabled && GameManager.getState() != runningState)
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
            if(endIcon != null)
                label.setIcon(endIcon);
        }
    }

    @Override
    protected void terminate()
    {
        if(onEndExecute != null)
            onEndExecute.run();
    }

    /**
     * Ferma l'animazione, se questa è iniziata; altrimenti non fa nulla.
     */
    public void stop()
    {
        if(thread != null)
            thread.interrupt();
    }

    /**
     * Static factory che crea una PerpetualAnimation basata sul GameState:
     * finché il gioco si trova nello stato {@code runningState} allora l'animazione
     * continua a essere eseguita, terminando appena cambia lo stato.
     *
     * @param label JLabel da animare
     * @param frames fotogrammi che compongono l'animazione
     * @param delayMilliseconds millisecondi da attendere al cambio di fotogramma
     * @param initialDelay flag se indica se attendere delayMilliseconds all'inizio dell'animazione
     * @param runningState stato in cui l'animazione deve continuare
     * @return PerpetualAnimation costruita secondo i parametri impostati
     */
    public static PerpetualAnimation animateWhileGameState(JLabel label, List<Image> frames,
                                                           int delayMilliseconds, boolean initialDelay,
                                                           GameManager.GameState runningState)
    {
        Objects.requireNonNull(label);
        Objects.requireNonNull(runningState);

        PerpetualAnimation anim = new PerpetualAnimation(label, frames, delayMilliseconds, initialDelay);
        anim.stateEnabled = true;
        anim.runningState = runningState;

        return anim;
    }

    /**
     * Static factory per la creazione di un'animazione personalizzata.
     *
     * Nota: i fotogrammi vengono ricavati dal json cercando oggetti JSON con le
     * keyword {@code "1", "2", ...}
     *
     * @param spriteSheetPath path dello spriteSheet da cui ricavare i fotogrammi
     *                        dell'animazione
     * @param jsonPath path del json che contiene i dati dei fotogrammi dell'animazione
     * @param animationLabel JLabel da animare
     *
     * @return PerpetualAnimation creata
     */
    public static PerpetualAnimation createPerpetualAnimation(String spriteSheetPath,
                                                                String jsonPath,
                                                                JLabel animationLabel)
    {
        Objects.requireNonNull(spriteSheetPath);
        Objects.requireNonNull(jsonPath);
        Objects.requireNonNull(animationLabel);

        BufferedImage spriteSheet = SpriteManager.loadImage(spriteSheetPath);
        List<Image> frames = SpriteManager.getOrderedFrames(spriteSheet, jsonPath);

        // in futuro: personalizzare delayMilliseconds
        return new PerpetualAnimation(animationLabel, frames, 100, true);
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
     * @param jsonPath path del json che contiene i dati dei fotogrammi dell'animazione
     * @param name nome dell'animazione (da ricercare nel json)
     * @param animationLabel JLabel da animare
     * @param rescalingFactor fattore di riscalamento per i frames
     * @return PerpetualAnimation creata
     */
    public static PerpetualAnimation createPerpetualAnimation(String spriteSheetPath, String jsonPath, String name,
                                                                JLabel animationLabel, double rescalingFactor)
    {
        Objects.requireNonNull(spriteSheetPath);
        Objects.requireNonNull(jsonPath);
        Objects.requireNonNull(name);
        Objects.requireNonNull(animationLabel);

        BufferedImage spriteSheet = SpriteManager.loadImage(spriteSheetPath);
        List<Image> frames = SpriteManager.getKeywordOrderedFrames(spriteSheet, jsonPath, name);

        // TODO: personalizzare delay milliseconds
        return new PerpetualAnimation(animationLabel, frames, 100, true, DEFAULT_END_MILLISECONDS,
                rescalingFactor);
    }


}
