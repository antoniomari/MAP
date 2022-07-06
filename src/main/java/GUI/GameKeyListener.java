package GUI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameKeyListener implements KeyListener
{
    private final int keyCode;
    private boolean pressed;
    private final Runnable pressAction;
    private final Runnable releaseAction;

    public GameKeyListener(int keyCode, Runnable pressAction, Runnable releaseAction)
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

        this.pressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        // vuoto
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == keyCode)
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
