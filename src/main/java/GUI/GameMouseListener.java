package GUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameMouseListener implements MouseListener
{
    private final int button;
    private final Runnable clickAction;
    private final Runnable pressAction;


    public GameMouseListener(int button, Runnable clickAction, Runnable pressAction)
    {
        this.button = button;

        if(pressAction == null)
            this.pressAction = () -> {};
        else
            this.pressAction = pressAction;

        if(clickAction == null)
            this.clickAction = () -> {};
        else
            this.clickAction = clickAction;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getButton() == button )
        {
            clickAction.run();
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if (e.getButton() == button )
        {
            pressAction.run();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {

    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        // TODO
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        // TODO
    }
}
