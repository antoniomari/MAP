package GUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameMouseListener implements MouseListener
{
    private final int button;
    private final Runnable pressAction;
    private final Runnable releaseAction;

    private boolean okFlag;


    public GameMouseListener(int button, Runnable pressAction, Runnable releaseAction)
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
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {

    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if (e.getButton() == button)
        {
            pressAction.run();
            okFlag = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (e.getButton() == button)
        {
            if(okFlag)
                releaseAction.run();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        okFlag = false;
    }
}
