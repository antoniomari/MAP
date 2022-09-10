package animation;

import general.GameException;
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
    /** Valore di default per i millisecondi di attesa alla fine dell'animazione. */
    protected static int DEFAULT_END_MILLISECONDS = 0;
    /** JLabel su cui eseguire l'animazione. */
    protected JLabel label;

    /** Lista di fotogrammi dell'animazione. */
    private final List<Image> frames;
    /** Icone relative ai fotogrammi (devono essere correttamente riscalate). */
    protected List<Icon> frameIcons;

    /** Indice (nella lista) del fotogramma visualizzato correntemente. */
    private int currentIndex = 0;

    /** Millisecondi da attendere alla fine dell'animazione. */
    protected int millisecondWaitEnd = DEFAULT_END_MILLISECONDS;

    /** Thread dedicato all'esecuzione dell'animazione. */
    protected AnimationThread thread;

    /**
     * Classe che rappresenta un thread di esecuzione dell'animazione.
     *
     *
     */
    protected class AnimationThread extends Thread
    {

        /**
         * Il metodo run è un template-method che include le chiamate ai metodi
         * astratti {@link Animation#execute()} e {@link Animation#terminate()}.
         */
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
                throw new GameException("Errore nel thread di animazione");
            }

            // template
            terminate();

            thread = null;
        }
    }

    /**
     * Esecuzione dell'animazione, chiamato unicamente nel metodo
     * run della classe AnimationThread.
     */
    protected abstract void execute();

    /**
     * Codice da eseguire al termine dell'animazione, chiamato
     * unicamente nel metodo run della classe AnimationThread.
     */
    protected abstract void terminate();


    /**
     * Crea un'animazione.
     *
     * Nota: il fattore di riscalamento per i frames viene calcolato
     * automaticamente
     *
     * @param labelToAnimate JLabel da animare
     * @param frames lista dei fotogrammi da utilizzare nell'animazione
     */
    protected Animation(JLabel labelToAnimate, List<Image> frames)
    {
        Objects.requireNonNull(labelToAnimate);
        Objects.requireNonNull(frames);

        this.label = labelToAnimate;
        this.frames = frames;
        resizeFrames();
        this.currentIndex = 0;
    }

    /**
     * Crea un'animazione in cui i frame vengono riscalati secondo un fattore
     * di riscalamento fornito.
     *
     * @param labelToAnimate JLabel da animare
     * @param frames lista dei fotogrammi da utilizzare nell'animazione
     * @param rescalingFactor fattore di riscalamento per i frames
     */
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
     * Inizializza frameIcons creando icone riscalate, adattandosi
     * automaticamente alle dimensioni della label.
     */
    private void resizeFrames()
    {
        // calcola rescaling factor
        frameIcons = new ArrayList<>(frames.size());
        double rescalingFactor = (double) label.getIcon().getIconWidth() / frames.get(0).getWidth(null);

        for(Image i : frames)
            frameIcons.add(SpriteManager.rescaledImageIcon(i, rescalingFactor));
    }

    /**
     * Inizializza frameIcons creando icone riscalate, utilizzando
     * un fattore di riscalamento dato.
     *
     * @param rescalingFactor fattore di riscalamento
     */
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
        if (currentIndex < frameIcons.size())
            return frameIcons.get(currentIndex++);
        else
        {
            currentIndex = 0;
            return frameIcons.get(frameIcons.size() - 1);

        }
    }

    /**
     * Esegue l'animazione, creando un AnimationThread dedicato.
     */
    public void start()
    {
        thread = new Animation.AnimationThread();
        thread.start();
    }

}
