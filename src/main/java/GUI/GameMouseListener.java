package GUI;

import GUI.gamestate.GameState;
import general.GameManager;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameMouseListener implements MouseListener
{
    private final Button button;
    private final Runnable pressAction;
    private final Runnable releaseAction;

    private Runnable enterAction = () -> {};
    private Runnable exitAction = () -> {};

    private final GameState.State targetState;

    private boolean okFlag;

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
                        System.out.println("tasto destro? " + SwingUtilities.isRightMouseButton(e));
                        return SwingUtilities.isRightMouseButton(e);
                    }
                };

        abstract boolean checkButton(MouseEvent e);
    }

    public GameMouseListener(Button button, Runnable pressAction, Runnable releaseAction)
    {
        this(button, pressAction, releaseAction, GameState.State.PLAYING);
    }

    public GameMouseListener(Button button, Runnable pressAction, Runnable releaseAction, GameState.State targetState)
    {
        this.button = button;

        if(pressAction == null)
            this.pressAction = () -> {};
        else
            this.pressAction = pressAction;

        if(releaseAction == null)
            this.releaseAction = () -> {};
        else
            this.releaseAction = releaseAction;

        okFlag = false;
        this.targetState = targetState;
    }

    public void setMouseEnteredAction(Runnable enterAction)
    {
        this.enterAction = enterAction;
    }

    public void setMouseExitedAction(Runnable exitAction)
    {
        this.exitAction = exitAction;
    }


    @Override
    public void mouseClicked(MouseEvent e)
    {

    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if (GameState.getState() == targetState &&  button.checkButton(e))
        {
            pressAction.run();
            okFlag = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (GameState.getState() == targetState && button.checkButton(e))
        {
            if(okFlag)
                releaseAction.run();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        if(GameState.getState() == targetState)
        {
            enterAction.run();
        }
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        if (GameState.getState() == targetState)
            okFlag = false;

        if(GameState.getState() == targetState)
        {
            exitAction.run();
        }
    }
}
