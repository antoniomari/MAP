package animation;

import GUI.AbsPosition;
import GUI.GameScreenManager;
import GUI.gamestate.GameState;
import entity.rooms.BlockPosition;
import general.GameManager;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Classe che rappresenta un'animazione di movimento,
 * che sposta gradualmente una JLabel dalla posizione
 * iniziale a quella finale. Viene alternata la
 * visualizzazione dei fotogrammi.
 */
public class MovingAnimation extends Animation
{
    private int delayMilliseconds;
    private boolean initialDelay;
    private final double blockDistance;
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

        int initialX = initialPos.getX();
        int finalX = finalPos.getX();
        int initialY = initialPos.getY();
        int finalY = finalPos.getY();
        blockDistance = Math.sqrt(Math.pow(finalX - initialX, 2) + Math.pow(finalY - initialY, 2));

        // initialCoord: coordinate iniziali dell'angolo in basso a sinistra
        this.initialCoord = GameScreenManager.calculateCoordinates(initialPos);
        // coordinate finali dell'angolo in basso a sinistra
        this.finalCoord = GameScreenManager.calculateCoordinates(finalPos);

        this.speed = speed * 0.05; // ok
        setFinalDelay(millisecondWaitEnd);

        initCoordList();
    }


    private void initCoordList()
    {
        int initialX = initialCoord.getX();
        int finalX = finalCoord.getX();
        int initialY = initialCoord.getY();
        int finalY = finalCoord.getY();


        delayMilliseconds = (int) Math.round(1000.0 / FPS);
        numFrames = (int) (FPS * blockDistance / (1000 * speed));

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

        if(GameState.getState() != GameState.State.MOVING)
            GameState.changeState(GameState.State.MOVING);

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

            GameState.changeState(GameState.State.PLAYING);
        }
    }

    @Override
    protected void terminate()
    {
        GameManager.continueScenario();
    }

}

