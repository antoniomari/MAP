package animation;

import GUI.AbsPosition;
import GUI.GameScreenManager;
import GUI.gamestate.GameState;
import graphics.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Animation
{
    protected JLabel label;

    private int numFrames;
    private final List<Image> frames;
    protected List<Icon> frameIcons;

    protected static final Map<JLabel, Queue<Animation>> animationQueueMap = new HashMap<>();

    protected int millisecondWaitEnd;

    private class AnimationThread extends Thread // TODO trovare un modo per evitare che la stessa animazione venga eseguita contemporaneamente
    {

        @Override
        public void run()
        {
            if(GameState.getState() != GameState.State.MOVING)
                GameState.changeState(GameState.State.MOVING);

            // specifico per ogni classe
            execute();

            // rimuovi animazione corrente dalla coda
            Queue<Animation> animationQueue = animationQueueMap.get(label);
            animationQueue.remove();


            // TODO: importante, syncronized sul game state
            if(animationQueue.isEmpty())
                GameState.changeState(GameState.State.PLAYING);
            else
            {


                // TODO: ricontrollare IMPORTANTE !!!
                //Animation next = animationQueue.peek();
                //next.executeAnimation();
            }

            try
            {
                Thread.sleep(millisecondWaitEnd);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            // template
            terminate();

        }
    }

    private int currentIndex = 0;

    protected Animation(JLabel labelToAnimate, List<Image> frames)
    {

        this.label = labelToAnimate;
        this.frames = frames;
        resizeFrames();
        this.currentIndex = 0;
        this.millisecondWaitEnd = 0;
    }

    protected void resizeFrames()
    {
        // calcola rescaling factor
        frameIcons = new ArrayList<>(frames.size());
        double rescalingFactor = (double) label.getIcon().getIconWidth() / frames.get(0).getWidth(null);

        for(Image i : frames)
            frameIcons.add(SpriteManager.rescaledImageIcon(i, rescalingFactor));
    }

    protected Icon getNextIcon()
    {
        if (currentIndex < frameIcons.size() - 1)
            return frameIcons.get(currentIndex++);
        else
        {
            currentIndex = 0;
            return frameIcons.get(frameIcons.size() - 1);

        }
    }

    public void start()
    {
        Queue<Animation> workingQueue;

        if(!animationQueueMap.containsKey(label))
        {
            workingQueue = new ConcurrentLinkedQueue<>();
            animationQueueMap.put(label, workingQueue);
        }
        else
        {
            workingQueue = animationQueueMap.get(label);
        }

        if(workingQueue.isEmpty())
        {
            workingQueue.add(this);
            executeAnimation();
        }
        else
        {
            workingQueue.add(this);
        }
    }

    protected void executeAnimation()
    {
        new Animation.AnimationThread().start();
    }

    protected abstract void execute();

    protected abstract void terminate();



}
