package GUI;

import GUI.gamestate.GameState;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameKeyListener implements KeyListener
{
    private final int keyCode;
    private boolean pressed;
    private final Runnable pressAction;
    private final Runnable releaseAction;
    private final GameState.State targetState;

    public GameKeyListener(int keyCode, Runnable pressAction, Runnable releaseAction, GameState.State targetState)
    {
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

    public GameKeyListener(int keyCode, Runnable pressAction, Runnable releaseAction)
    {
        this(keyCode, pressAction, releaseAction, GameState.State.PLAYING);
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        // vuoto
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        System.out.println("PREMUTA: " + pressed);
        if (GameState.getState() == targetState &&  e.getKeyCode() == keyCode)
        {
            if(!pressed)
            {
                pressAction.run();
                pressed = true;
            }

        }
    }

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
