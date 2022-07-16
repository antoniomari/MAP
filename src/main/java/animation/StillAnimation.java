package animation;

import graphics.SpriteManager;
import entity.items.Item;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// animazione statica
public class StillAnimation extends Animation
{
    private List<Image> frames;
    private List<Icon> framesIcon;
    private JLabel label;
    private int delayMilliseconds;
    private Item item;
    private final boolean initialDelay;

    private class AnimationThread extends Thread // TODO trovare un modo per evitare che la stessa animazione venga eseguita contemporaneamente
    {
        @Override
        public void run()
        {
            boolean delay = initialDelay;

            for(Icon frame : framesIcon)
            {
                try
                {
                    if(delay)
                        Thread.sleep(delayMilliseconds);
                    else
                        delay = true;

                    label.setIcon(frame);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public StillAnimation(int delayMilliseconds, boolean initialDelay)
    {
        frames = new ArrayList<>();
        framesIcon = new ArrayList<>();
        this.delayMilliseconds = delayMilliseconds;
        this.initialDelay = initialDelay;
    }

    public void compile(JLabel label, double rescalingFactor)
    {
        this.label = label;
        rescaleFrames(rescalingFactor);
    }


    public void addFrame(Image frame)
    {
        Objects.requireNonNull(frame);
        frames.add(frame);
    }

    private void rescaleFrames(double rescalingFactor)
    {
        framesIcon = new ArrayList<>();

        for(Image frame : frames)
        {
            framesIcon.add(SpriteManager.rescaledImageIcon(frame, rescalingFactor));
        }
    }

    public void start()
    {
        new AnimationThread().start();
    }
}
