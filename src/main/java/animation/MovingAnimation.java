package animation;

import GUI.AbsPosition;
import GUI.GameScreenManager;
import GUI.GameScreenPanel;
import GUI.gamestate.GameState;
import entity.GamePiece;
import entity.rooms.BlockPosition;
import graphics.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MovingAnimation
{
    private JLabel label;
    private int delayMilliseconds;
    private final boolean initialDelay;
    private AbsPosition initialCoord;
    private AbsPosition finalCoord;
    private int numFrames;
    private List<AbsPosition> positionsList;

    private static final int FPS = 144;
    private final double speed;

    private static final Queue<MovingAnimation> animationQueue = new ConcurrentLinkedQueue<>();


    private class AnimationThread extends Thread // TODO trovare un modo per evitare che la stessa animazione venga eseguita contemporaneamente
    {

        @Override
        public void run()
        {

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

                    // System.out.println("X e Y: " + c);

                    GameScreenManager.updateLabelPosition(label, c);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            // rimuovi animazione corrente dalla coda
            animationQueue.remove();

            if(animationQueue.isEmpty())
                GameState.changeState(GameState.State.PLAYING);
            else
                animationQueue.poll().start();
        }
    }


    public MovingAnimation(JLabel label, BlockPosition initialPos, BlockPosition finalPos,  boolean initialDelay)
    {
        this(label, initialPos, finalPos, initialDelay, 1.0);
    }

    public MovingAnimation(JLabel label, BlockPosition initialPos, BlockPosition finalPos,  boolean initialDelay, double speed)
    {
        this.label = label;
        this.initialDelay = initialDelay;

        // initialCoord: coordinate iniziali dell'angolo in basso a sinistra
        this.initialCoord = GameScreenManager.calculateCoordinates(initialPos);

        // coordinate finali dell'angolo in basso a sinistra
        this.finalCoord = GameScreenManager.calculateCoordinates(finalPos);

        this.speed = speed;

        initCoordList();
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

        //System.out.println("Initial: " + initialCoord);

        //System.out.println("Final: " + finalCoord);
        //System.out.println("dX = " + deltaX);
        //System.out.println("dY = " + deltaY);


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

    public void start()
    {
        if(animationQueue.isEmpty())
        {
            animationQueue.add(this);
            new AnimationThread().start();
        }
        else
            animationQueue.add(this);

    }
}

