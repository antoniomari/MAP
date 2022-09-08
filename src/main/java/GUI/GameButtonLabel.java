package GUI;

import general.GameManager;
import graphics.SpriteManager;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.util.function.Predicate;

public class GameButtonLabel extends JLabel
{
    private Icon buttonIcon;
    private Icon hoverIcon;
    private GameMouseListener gameMouseListener;

    public GameButtonLabel(String buttonIconPath, String hoverIconPath, double scalingFactor)
    {
        super();
        this.buttonIcon = SpriteManager.rescaledImageIcon(SpriteManager.loadSpriteSheet(buttonIconPath),
                                scalingFactor);

        this.hoverIcon = SpriteManager.rescaledImageIcon(SpriteManager.loadSpriteSheet(hoverIconPath),
                scalingFactor);

        setIcon(buttonIcon);
    }

    public void setConditionToEnable(Predicate<Void> condition)
    {

    }

    @Override
    public void addMouseListener(MouseListener ml)
    {
        super.addMouseListener(ml);

        if(ml instanceof GameMouseListener)
        {
            gameMouseListener = (GameMouseListener) ml;
        }

    }


    public void disableButtonLabel()
    {
        setIcon(hoverIcon);
        removeMouseListener(gameMouseListener);
    }


    public void changeIcon(boolean mouseHover)
    {
        if(mouseHover)
            setIcon(hoverIcon);
        else
            setIcon(buttonIcon);
    }
}
