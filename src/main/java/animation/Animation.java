package animation;

import graphics.SpriteManager;
import items.Item;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// animazione statica
public class Animation extends Thread// TODO: implementare iterable??
{
    private List<Image> frames;
    private List<Icon> framesIcon;
    private JLabel label;
    private int delayMilliseconds;
    private Item item;

    public Animation(Item item, int delayMilliseconds)
    {
        frames = new ArrayList<>();
        framesIcon = new ArrayList<>();
        this.delayMilliseconds = delayMilliseconds;
        this.item = item;
    }

    public void addFrame(Image frame)
    {
        Objects.requireNonNull(frame);
        frames.add(frame);

    }

    public void rescaleFrames(double rescalingFactor)
    {
        framesIcon = new ArrayList<>();

        for(Image frame : frames)
        {
            framesIcon.add(SpriteManager.rescaledImageIcon(frame, rescalingFactor));
        }
    }


    /*
    private static Icon rescaledImageIcon(Image im, double rescalingFactor)
    {
        int newWidth = (int) (rescalingFactor * im.getWidth(null));
        int newHeight = (int)(rescalingFactor * im.getHeight(null));
        Image newSprite = im.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(newSprite);
    }

     */

    public Item getItem()
    {
        return item;
    }

    // nota: aggiustare
    public List<Icon> getFrames()
    {
        return framesIcon;
    }

    public int getTimeToWait()
    {
        return delayMilliseconds;
    }

    public void setLabel(JLabel label)
    {
        this.label = label;
    }

    @Override
    public void run()
    {

        for(Icon frame : framesIcon)
        {
            try
            {
                Thread.sleep(delayMilliseconds);
                label.setIcon(frame);


            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
