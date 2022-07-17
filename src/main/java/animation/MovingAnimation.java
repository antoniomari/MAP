package animation;

import GUI.AbsPosition;
import GUI.GameScreenManager;
import GUI.GameScreenPanel;
import GUI.gamestate.GameState;
import entity.GamePiece;
import entity.rooms.BlockPosition;
import graphics.SpriteManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MovingAnimation extends Animation
{
    private JLabel label;
    private int delayMilliseconds;
    private final boolean initialDelay;
    private final AbsPosition initialCoord;
    private final AbsPosition finalCoord;
    private int numFrames;
    private List<AbsPosition> positionsList;

    private static final int FPS = 144;
    private final double speed;
    private final int millisecondWaitEnd;

    private List<Image> frames;
    private List<Icon> frameIcons;
    private int currentIndex;

    private static final Map<JLabel, Queue<MovingAnimation>> animationQueueMap = new HashMap<>();
    //private static final Queue<MovingAnimation> animationQueue = new ConcurrentLinkedQueue<>();


    private class AnimationThread extends Thread // TODO trovare un modo per evitare che la stessa animazione venga eseguita contemporaneamente
    {

        @Override
        public void run()
        {
            if(GameState.getState() != GameState.State.MOVING)
                GameState.changeState(GameState.State.MOVING);

            boolean delay = initialDelay;

            for(AbsPosition c : positionsList)
            {
                try
                {
                    if(delay)
                        Thread.sleep(delayMilliseconds);
                    else
                        delay = true;

                    GameScreenManager.updateLabelPosition(label, c);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            // rimuovi animazione corrente dalla coda
            Queue<MovingAnimation> animationQueue = animationQueueMap.get(label);
            animationQueue.remove();


            // TODO: improtante, syncronized sul game state
            if(animationQueue.isEmpty())
                GameState.changeState(GameState.State.PLAYING);
            else
            {
                try
                {
                    // aspetta quanto richiesto prima di iniziare la successiva
                    Thread.sleep(millisecondWaitEnd);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                MovingAnimation next = animationQueue.peek();
                next.executeAnimation();
            }

        }
    }


    public MovingAnimation(JLabel label, BlockPosition initialPos, BlockPosition finalPos, int millisecondWaitEnd, boolean initialDelay, List<Image> frames)
    {
        this(label, initialPos, finalPos, millisecondWaitEnd, initialDelay,1.0, frames);
    }

    public MovingAnimation(JLabel label, BlockPosition initialPos, BlockPosition finalPos,  int millisecondWaitEnd, boolean initialDelay, double speed, List<Image> frames)
    {
        this.label = label;
        this.initialDelay = initialDelay;

        // initialCoord: coordinate iniziali dell'angolo in basso a sinistra
        this.initialCoord = GameScreenManager.calculateCoordinates(initialPos);

        // coordinate finali dell'angolo in basso a sinistra
        this.finalCoord = GameScreenManager.calculateCoordinates(finalPos);


        this.speed = speed;
        this.millisecondWaitEnd = millisecondWaitEnd;

        this.frames = frames;
        this.currentIndex = 0;

        // calcola rescaling factor
        frameIcons = new ArrayList<>(frames.size());
        double rescalingFactor = (double) label.getIcon().getIconWidth() / frames.get(0).getWidth(null);

        for(Image i : frames)
            frameIcons.add(SpriteManager.rescaledImageIcon(i, rescalingFactor));


        initCoordList();
    }

    private Icon getNextIcon()
    {
        if (currentIndex < frameIcons.size() - 1)
            return frameIcons.get(currentIndex++);
        else
        {
            currentIndex = 0;
            return frameIcons.get(frameIcons.size() - 1);

        }
    }

    private void initCoordList()
    {
        int initialX = initialCoord.getX();
        int finalX = finalCoord.getX();
        int initialY = initialCoord.getY();
        int finalY = finalCoord.getY();


        double distance = Math.sqrt(Math.pow(finalX - initialX, 2) + Math.pow(finalY - initialY, 2));
        delayMilliseconds = (int) Math.round(1000.0 / FPS);
        numFrames = (int)(FPS * distance / (1000 * speed));

        double deltaX = (double)(finalX- initialX) / numFrames;
        double deltaY = (double)(finalY - initialY) / numFrames;

        positionsList = new ArrayList<>();

        int xIncrement;
        int yIncrement;

        for(int i = 1; i < numFrames; i++)
        {
            xIncrement = initialX + (int) Math.round(i * deltaX);
            yIncrement = initialY + (int) Math.round(i * deltaY);

            positionsList.add(new AbsPosition(xIncrement, yIncrement));
        }
        positionsList.add(finalCoord);

    }

    @Override
    public String toString()
    {
        return "Animazione{" +
                "initialCoord=" + initialCoord +
                ", finalCoord=" + finalCoord +
                '}';
    }

    public void start()
    {
        Queue<MovingAnimation> workingQueue;

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

    private void executeAnimation()
    {
        new AnimationThread().start();
    }

    @Override
    public void addFrame(Image im)
    {
        // TODO: implementare
    }
}

