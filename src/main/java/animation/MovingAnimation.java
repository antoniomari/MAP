package animation;

import GUI.AbsPosition;
import GUI.GameScreenManager;
import GUI.GameScreenPanel;
import GUI.gamestate.GameState;
import entity.GamePiece;
import entity.rooms.BlockPosition;
import general.GameManager;
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
    private int delayMilliseconds;
    private boolean initialDelay;
    private final AbsPosition initialCoord;
    private final AbsPosition finalCoord;
    private List<AbsPosition> positionsList;

    private static final int FPS = 144;
    private final double speed;


    public MovingAnimation(JLabel label, BlockPosition initialPos, BlockPosition finalPos, int millisecondWaitEnd, boolean initialDelay, List<Image> frames)
    {
        // TODO: aggiustare speed
        this(label, initialPos, finalPos, millisecondWaitEnd, initialDelay,0.5, frames);
    }

    public MovingAnimation(JLabel label, BlockPosition initialPos,
                           BlockPosition finalPos,  int millisecondWaitEnd,
                           boolean initialDelay, double speed,
                           List<Image> frames)
    {
        super(label, frames);

        this.initialDelay = initialDelay;
        // initialCoord: coordinate iniziali dell'angolo in basso a sinistra
        this.initialCoord = GameScreenManager.calculateCoordinates(initialPos);
        // coordinate finali dell'angolo in basso a sinistra
        this.finalCoord = GameScreenManager.calculateCoordinates(finalPos);

        this.speed = speed; // ok
        this.millisecondWaitEnd = millisecondWaitEnd; // ok

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
        int numFrames = (int) (FPS * distance / (1000 * speed));

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


    protected void execute()
    {
        boolean delay = initialDelay;

        for(AbsPosition c : positionsList)
        {
            try
            {
                if(positionsList.indexOf(c) % 10 == 0)
                {
                    label.setIcon(getNextIcon());
                }

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

            label.setIcon(frameIcons.get(frameIcons.size() -1));
        }
    }

    @Override
    protected void terminate()
    {
        GameManager.continueScenario();
    }

}

