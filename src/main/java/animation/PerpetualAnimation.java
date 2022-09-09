package animation;

import GUI.gamestate.GameState;
import general.GameException;
import general.GameManager;
import general.LogOutputManager;
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

    private Icon endIcon;
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

    public void setEndIcon(Icon endIcon)
    {
        this.endIcon = endIcon;
    }

    @Override
    protected void execute()
    {
        boolean delay = initialDelay;

        // todo: rimpiazzare thread.sleep urgente
        try
        {
            while(true)
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
     * Ferma l'animazione, se questa è iniziata;
     * altrimento non fa nulla.
     */
    public void stop()
    {

        if(thread != null)
        {
            thread.interrupt();
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

