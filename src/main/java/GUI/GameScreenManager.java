package GUI;

import entity.rooms.BlockPosition;

import javax.swing.*;
import java.awt.*;
import java.nio.file.attribute.AttributeView;
import java.util.Objects;


public class GameScreenManager
{

    private static final int BLOCK_SIZE = 24;
    private static GameScreenPanel active_panel;
    private static double rescalingFactor;

    private GameScreenManager()
    {
        // costruttore privato per non permettere l'istanziazione
    }

    public static void setActivePanel(GameScreenPanel gsp)
    {
        Objects.requireNonNull(gsp);

        active_panel = gsp;
        rescalingFactor = active_panel.getScalingFactor();

    }

    // TODO: calcolare massimi xBlock e yBlock per la stanza

    /**
     * Restituisce la coordinata del pixel in alto a sinistra del blocco
     * @param pos
     * @return
     */
    public static AbsPosition calculateCoordinates(BlockPosition pos)
    {
        int xBlocks = pos.getX();
        int yBlocks = pos.getY();

        if(xBlocks < 0 || yBlocks < 0)
            throw new IllegalArgumentException();

        Insets insets = active_panel.getInsets();

        int xOffset = (int) Math.round(insets.left + xBlocks * BLOCK_SIZE * rescalingFactor);
        int yOffset = (int) Math.round(insets.top + yBlocks * BLOCK_SIZE * rescalingFactor);

        return new AbsPosition(xOffset, yOffset);
    }


    /**
     * Restituisce la posizione del blocco il cui angolo in basso a sinistra è il più vicino
     * alla posizione assoluta passata.
     *
     * @param absPos posizione assoluta della quale calcolare il blocco più vicino
     * @return posizione del blocco corrispondente
     */
    public static BlockPosition calculateBlocks(AbsPosition absPos)
    {

        // scegli il blocco più vicino
        int x = (int) Math.round((double) absPos.getX() / (BLOCK_SIZE * rescalingFactor));
        int y = (int) Math.round((double) absPos.getY() / (BLOCK_SIZE * rescalingFactor)) - 1;

        //System.out.println("CALCOLATO X : " + x);
        //System.out.println("CALCOLATO Y: " + y);

        //check
        //System.out.println( (x % (int)(BLOCK_SIZE * rescalingFactor) == 0)
        //                    + " "
        //                    + (y % (int)(BLOCK_SIZE * rescalingFactor) == 0) );

        // todo: controllare
        return new BlockPosition(Math.max(x, 0), y);
    }


    /**
     *
     * @param label
     * @param pos angolo in basso a sinistra
     */
    public static void updateLabelPosition(JLabel label, BlockPosition pos)
    {

        updateLabelPosition(label, calculateCoordinates(pos));

        // calcola angolo in alto a sinistra
        //BlockPosition leftUpCornerPos = pos.relativePosition(0, - bHeight + 1);

        //AbsPosition c = calculateCoordinates(leftUpCornerPos);
        //c = new AbsPosition(c.getX(), c.getY());

        //label.setBounds(insets.left + c.getX(), insets.top + c.getY(),
        //        label.getIcon().getIconWidth(), label.getIcon().getIconHeight());
    }

    /**
     *
     * @param label label su cui lavorare
     * @param pos posizione assoluta dell'angolo in basso a sinistra
     */
    public static void updateLabelPosition(JLabel label, AbsPosition pos)
    {
        // offset y
        int offsetHeight= label.getIcon().getIconHeight() - (int)(BLOCK_SIZE * rescalingFactor);


        // pos è angolo in basso a sinistra, dobbiamo calcolarci l'angolo in alto a sinistra
        Insets insets = active_panel.getInsets();

        AbsPosition leftUpCornerPos = new AbsPosition(pos.getX(), pos.getY() - offsetHeight);
        label.setBounds(insets.left + leftUpCornerPos.getX(), insets.top + leftUpCornerPos.getY(),
                        label.getIcon().getIconWidth(), label.getIcon().getIconHeight());
    }
}
