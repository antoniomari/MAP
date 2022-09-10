package gui;

import general.GameManager;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

/**
 * Implementazione di listener per tasti del mouse,
 * le cui istanze sono personalizzabili: è possibile
 * impostare azioni (runnable) associate a uno specifico tasto,
 * le quali vengono attivate solo in un particolare stato di gioco
 * {@link GameManager.GameState}
 */
public class GameMouseListener implements MouseListener
{
    /** Tasto da ascoltare. */
    private final Button button;
    /** Azione da eseguire alla pressione del tasto. */
    private Runnable pressAction = () -> {};
    /** Azione da eseguire al rilascio del tasto. */
    private Runnable releaseAction = () -> {};
    /** Azione da eseguire all'entrata del mouse su un componente. */
    private Runnable enterAction = () -> {};
    /** Azione da eseguire all'uscita del mouse da un componente. */
    private Runnable exitAction = () -> {};
    /** Stato in cui il gioco deve trovarsi per l'esecuzione delle azioni. */
    private final GameManager.GameState targetState;

    private boolean okFlag;


    /**
     * Enumerazione per il controllo sui bottoni
     */
    public enum Button
    {
        LEFT
                {
                    @Override
                    boolean checkButton(MouseEvent e)
                 {

                     return SwingUtilities.isLeftMouseButton(e);
                 }
                },
        RIGHT
                {
                    @Override
                    boolean checkButton(MouseEvent e)
                    {
                        return SwingUtilities.isRightMouseButton(e);
                    }
                };

        /**
         * Restituisce {@code true} se l'evento {@code e} riguarda
         * il tasto rappresentato dal valore dell'enumerazione.
         *
         * @param e evento di cui controllare il tasto
         * @return {@code true} se l'evento {@code e} riguarda
         * il tasto rappresentato dal valore dell'enumerazione;
         * {@code false} altrimenti.
         */
        abstract boolean checkButton(MouseEvent e);
    }

    /**
     * Crea un GameMouseListener attivo nello stato {@link GameManager.GameState#PLAYING}
     *
     * @param button tasto target del listener
     * @param pressAction azione da eseguire alla pressione del tasto,
     *                    quando il gioco si trova nello stato
     *                    {@link GameManager.GameState#PLAYING} o {@code null} per non
     *                    impostare alcuna azione
     * @param releaseAction azione da eseguire al rilascio del tasto,
     *                      quando il gioco si trova nello stato
     *                      {@link GameManager.GameState#PLAYING} o {@code null} per non
     *                      impostare alcuna azione
     */
    public GameMouseListener(Button button, Runnable pressAction, Runnable releaseAction)
    {
        this(button, pressAction, releaseAction, GameManager.GameState.PLAYING);
    }

    /**
     * Crea un GameMouseListener.
     *
     * @param button tasto target del listener
     * @param pressAction azione da eseguire alla pressione del tasto,
     *                    quando il gioco si trova nello stato
     *                    {@code targetState} o {@code null} per non
     *                    impostare alcuna azione
     * @param releaseAction azione da eseguire al rilascio del tasto,
     *                      quando il gioco si trova nello stato
     *                      {@code targetState} o {@code null} per non
     *                      impostare alcuna azione
     * @param targetState stato di gioco {@link GameManager.GameState} in cui
     *                    è attivo il GameKeyListener da creare
     */
    public GameMouseListener(Button button, Runnable pressAction, Runnable releaseAction, GameManager.GameState targetState)
    {
        Objects.requireNonNull(button);
        Objects.requireNonNull(targetState);

        this.button = button;

        if(pressAction != null)
            this.pressAction = pressAction;

        if(releaseAction != null)
            this.releaseAction = releaseAction;

        okFlag = false;
        this.targetState = targetState;
    }

    /**
     * Imposta azione da eseguire all'entrata del mouse sul componente.
     *
     * @param enterAction azione da eseguire
     */
    public void setMouseEnteredAction(Runnable enterAction)
    {
        Objects.requireNonNull(enterAction);
        this.enterAction = enterAction;
    }

    /**
     * Imposta azione da eseguire all'uscita del mouse da un componente.
     *
     * @param exitAction azione da eseguire
     */
    public void setMouseExitedAction(Runnable exitAction)
    {
        Objects.requireNonNull(exitAction);
        this.exitAction = exitAction;
    }

    /**
     * Ignorato, per la pressione e il rilascio del tasto vengono
     * utilizzati i metodi {@link GameMouseListener#mousePressed(MouseEvent)}
     * e {@link GameMouseListener#mouseReleased(MouseEvent)}.
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {

    }

    /**
     *  Attiva l'azione alla pressione del tasto (identificato da
     *  {@link GameMouseListener#button}) su un componente
     *  se il gioco è nello stato {@link GameMouseListener#targetState}.
     */
    @Override
    public void mousePressed(MouseEvent e)
    {
        if (GameManager.getState() == targetState &&  button.checkButton(e))
        {
            pressAction.run();
            okFlag = true;
        }
    }

    /**
     *  Attiva l'azione al rilascio del tasto da un componente
     *  se il gioco è nello stato {@link GameMouseListener#targetState}.
     */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (GameManager.getState() == targetState && button.checkButton(e))
        {
            if(okFlag)
                releaseAction.run();
        }
    }

    /**
     *  Attiva l'azione all'entrata dal mouse su un componente
     *  se il gioco è nello stato {@link GameMouseListener#targetState}.
     */
    @Override
    public void mouseEntered(MouseEvent e)
    {
        if(GameManager.getState() == targetState)
        {
            enterAction.run();
        }
    }

    /**
     *  Attiva l'azione all'uscita dal mouse da un componente
     *  se il gioco è nello stato {@link GameMouseListener#targetState}.
     */
    @Override
    public void mouseExited(MouseEvent e)
    {
        if (GameManager.getState() == targetState)
            okFlag = false;

        if(GameManager.getState() == targetState)
        {
            exitAction.run();
        }
    }
}
