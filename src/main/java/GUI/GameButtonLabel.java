package GUI;

import graphics.SpriteManager;

import javax.swing.*;

public class GameButtonLabel extends JLabel
{
    private Icon buttonIcon;
    private Icon hoverIcon;

    public GameButtonLabel(String buttonIconPath, String hoverIconPath, double scalingFactor)
    {
        super();
        this.buttonIcon = SpriteManager.rescaledImageIcon(SpriteManager.loadSpriteSheet(buttonIconPath),
                                scalingFactor);

        this.hoverIcon = SpriteManager.rescaledImageIcon(SpriteManager.loadSpriteSheet(hoverIconPath),
                scalingFactor);

        setIcon(buttonIcon);
    }

    public void changeIcon(boolean mouseHover)
    {
        if(mouseHover)
            setIcon(hoverIcon);
        else
            setIcon(buttonIcon);
    }
}
