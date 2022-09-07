package GUI;

import GUI.gamestate.GameState;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;

/**
 * Implementazione di listener per tasti della tastiera,
 * le cui istanze sono personalizzabili: è possibile
 * impostare azioni (runnable) associate a uno specifico tasto,
 * le quali vengono attivate solo in un particolare stato di gioco
 * {@link GameState.State}
 */
public class GameKeyListener implements KeyListener
{
    /** Codice del tasto da ascoltare. */
    private final int keyCode;
    /** Flag che indica se il tasto è premuto. */
    private boolean pressed;
    /** Azione da eseguire alla pressione del tasto. */
    private final Runnable pressAction;
    /** Azione da eseguire al rilascio del tasto. */
    private final Runnable releaseAction;
    /** Stato in cui il gioco deve trovarsi per l'esecuzione delle azioni. */
    private final GameState.State targetState;

    /**
     * Crea un GameKeyListener.
     *
     * @param keyCode codice del tasto target del listener
     * @param pressAction azione da eseguire alla pressione del tasto,
     *                    quando il gioco si trova nello stato
     *                    {@code targetState} o {@code null} per non
     *                    impostare alcuna azione
     * @param releaseAction azione da eseguire al rilascio del tasto,
     *                      quando il gioco si trova nello stato
     *                      {@code targetState} o {@code null} per non
     *                      impostare alcuna azione
     * @param targetState stato di gioco {@link GameState.State} in cui
     *                    è attivo il GameKeyListener da creare
     */
    public GameKeyListener(int keyCode, Runnable pressAction, Runnable releaseAction, GameState.State targetState)
    {
        Objects.requireNonNull(targetState);

        this.keyCode = keyCode;

        if(pressAction == null)
            this.pressAction = () -> {};
        else
            this.pressAction = pressAction;

        if(releaseAction == null)
            this.releaseAction = () -> {};
        else
            this.releaseAction = releaseAction;

        this.targetState = targetState;
        this.pressed = false;
    }

    /**
     * Ignorato, gli eventi relativi alla pressione del tasto sono
     * presenti in {@link GameKeyListener#keyPressed(KeyEvent)} e
     * {@link GameKeyListener#keyReleased(KeyEvent)}.
     *
     * @param e evento
     */
    @Override
    public void keyTyped(KeyEvent e)
    {
        // vuoto
    }

    /**
     * Esegue {@link GameKeyListener#pressAction} alla pressione
     * del tasto {@link GameKeyListener#keyCode} se il gioco
     * si trova nello stato {@link GameKeyListener#targetState}.
     *
     * @param e evento
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
        if (GameState.getState() == targetState &&  e.getKeyCode() == keyCode)
        {
            if(!pressed)
            {
                pressAction.run();
                pressed = true;
            }
        }
    }

    /**
     * Esegue {@link GameKeyListener#releaseAction} al rilascio
     * del tasto {@link GameKeyListener#keyCode} se il gioco
     * si trova nello stato {@link GameKeyListener#targetState}.
     *
     * @param e evento
     */
    @Override
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == keyCode)
        {
            releaseAction.run();
            pressed = false;
        }
    }
}
