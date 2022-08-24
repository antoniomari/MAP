package animation;

import GUI.gamestate.GameState;
import graphics.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Classe astratta che rappresenta un'animazione.
 *
 * Ogni animazione agisce su una JLabel e coinvolge un certo
 * numero di fotogrammi (eventualmente 1).
 */
public abstract class Animation
{
    protected static int DEFAULT_END_MILLISECONDS = 0;
    /** JLabel su cui eseguire l'animazione. */
    protected JLabel label;

    protected int numFrames;

    /** Lista di fotogrammi dell'animazione. */
    private final List<Image> frames;
    /** Icone relative ai fotogrammi (devono essere correttamente riscalate). */
    protected List<Icon> frameIcons;

    /** Indice (nella lista) del fotogramma visualizzato correntemente. */
    private int currentIndex = 0;

    /** Millisecondi da attendere alla fine dell'animazione
     * (prima di tornare nel GameState Playing)
     */
    protected int millisecondWaitEnd = DEFAULT_END_MILLISECONDS;

    /**
     * Thread di esecuzione dell'animazione.
     *
     * Il metodo run è un template-method che include le chiamate ai metodi
     * astratti {@link Animation#execute()} e {@link Animation#terminate()}.
     *
     * All'inizio imposta lo stato di gioco Moving, viene eseguita l'animazione,
     * vengono attesi {@link Animation#millisecondWaitEnd} e infine viene terminata.
     */
    private class AnimationThread extends Thread
    {

        @Override
        public void run()
        {
            // template
            execute();

            try
            {
                Thread.sleep(millisecondWaitEnd);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            // template
            terminate();
        }
    }

    /**
     * Crea un'animazione.
     *
     * @param labelToAnimate JLabel da animare
     * @param frames lista dei fotogrammi da utilizzare nell'animazione
     */
    protected Animation(JLabel labelToAnimate, List<Image> frames)
    {
        this.label = labelToAnimate;
        this.frames = frames;
        resizeFrames();
        this.currentIndex = 0;
    }

    protected Animation(JLabel labelToAnimate, List<Image> frames, double rescalingFactor)
    {
        this.label = labelToAnimate;
        this.frames = frames;
        resizeFrames(rescalingFactor);
        this.currentIndex = 0;
    }

    /**
     * Imposta tempo di attesa alla fine dell'esecuzione dell'animazione
     *
     * @param milliseconds millisecondi da attendere
     */
    public void setFinalDelay(int milliseconds)
    {
        if(milliseconds < 0)
            throw new IllegalArgumentException("tempo negativo");

        this.millisecondWaitEnd = milliseconds;
    }

    public Icon getFirstFrameIcon()
    {
        return frameIcons.get(0);
    }



    /**
     * Imposta frameIcons creando icone riscalate per adattarsi
     * alle dimensioni della label
     */
    private void resizeFrames()
    {
        // calcola rescaling factor
        frameIcons = new ArrayList<>(frames.size());
        double rescalingFactor = (double) label.getIcon().getIconWidth() / frames.get(0).getWidth(null);

        for(Image i : frames)
            frameIcons.add(SpriteManager.rescaledImageIcon(i, rescalingFactor));
    }

    private void resizeFrames(double rescalingFactor)
    {
        frameIcons = new ArrayList<>(frames.size());

        for(Image i : frames)
            frameIcons.add(SpriteManager.rescaledImageIcon(i, rescalingFactor));
    }


    /**
     * Restituisce l'icona successiva a quella attualmente
     * in uso nell'animazione.
     *
     * La successiva all'ultima è la prima della lista {@link Animation#frameIcons}.
     * Utilizza e modifica {@link Animation#currentIndex}
     *
     * @return icona successiva a quella attualmente in uso nell'animazione
     */
    protected Icon getNextIcon()
    {
        if (currentIndex < frameIcons.size() - 1)
            return frameIcons.get(currentIndex++);
        else
        {
            currentIndex = 0;
            return frameIcons.get(frameIcons.size() - 1);
        }
    }

    /**
     * Esegue l'animazione, creando un thread dedicato.
     */
    public void start()
    {
        new Animation.AnimationThread().start();
    }


    /**
     * Esecuzione dell'animazione, chiamato unicamente nel metodo
     * run dell'AnimationThread.
     */
    protected abstract void execute();

    /**
     * Codice da eseguire al termine dell'animazione, chiamato
     * unicamente nel metodo run dell'AnimationThread.
     */
    protected abstract void terminate();



}
