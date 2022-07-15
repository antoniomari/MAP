package animation;

import GUI.AbsPosition;
import GUI.GameScreenManager;
import GUI.GameScreenPanel;
import entity.GamePiece;
import entity.rooms.BlockPosition;
import graphics.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MovingAnimation
{
    private JLabel label;
    private int delayMilliseconds;
    private final boolean initialDelay;
    private AbsPosition initialCoord;
    private AbsPosition finalCoord;
    private int numFrames;
    private List<AbsPosition> positionsList;

    private BlockPosition relativeF;

    private static final int FPS = 144;

    private int xOffset;
    private int yOffset;

    private class AnimationThread extends Thread // TODO trovare un modo per evitare che la stessa animazione venga eseguita contemporaneamente
    {
        @Override
        public void run()
        {
            // imposta a moving gamestate

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

                    label.setBounds(xOffset + c.getX(), yOffset + c.getY(), label.getIcon().getIconWidth(),
                            label.getIcon().getIconHeight());
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            // infine reimposta a playing state
        }
    }


    public MovingAnimation(GamePiece p, BlockPosition relativeFinal, boolean initialDelay)
    {
        this.relativeF = relativeFinal;
        this.initialDelay = initialDelay;
    }


    public MovingAnimation(JLabel label, boolean initialDelay)
    {
        this.label = label;
        this.initialDelay = initialDelay;

        this.initialCoord = new AbsPosition(label.getX(), label.getY());
    }

    public void setInitialCoord(AbsPosition initialC)
    {
        initialCoord = initialC;
    }


    public void setFinalCoord(AbsPosition finalC)
    {
        finalCoord = finalC;
        initCoordList();
    }

    public void setInsets(Insets i)
    {
        xOffset = i.left;
        yOffset = i.top;
    }

    public void initCoordList()
    {
        int initialX = initialCoord.getX();
        int finalX = finalCoord.getX();
        int initialY = initialCoord.getY();
        int finalY = finalCoord.getY();


        double distance = Math.sqrt(Math.pow(finalX - initialX, 2) + Math.pow(finalY - initialY, 2));
        delayMilliseconds = (int) Math.round(1000.0 / FPS);
        numFrames = FPS * (int) distance / 1000;

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

    public MovingAnimation compile(JLabel label, double rescalingFactor)
    {
        this.label = label;
        this.initialCoord = new AbsPosition(label.getX(), label.getY());
        this.finalCoord = GameScreenManager.calculateCoordinates(relativeF, rescalingFactor);

        initCoordList();

        return this;
    }



    public int getTimeToWait()
    {
        return delayMilliseconds;
    }

    public void setLabel(JLabel label)
    {
        this.label = label;
    }

    public void start()
    {
        new AnimationThread().start();
    }
}

