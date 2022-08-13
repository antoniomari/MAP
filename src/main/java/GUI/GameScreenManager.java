package GUI;

import entity.rooms.BlockPosition;

import javax.swing.*;
import java.util.Objects;


public class GameScreenManager
{

    private static final int BLOCK_SIZE = 24;
    private static GameScreenPanel active_panel;

    private GameScreenManager()
    {
        // costruttore privato per non permettere l'istanziazione
    }

    public static void setActivePanel(GameScreenPanel gsp)
    {
        Objects.requireNonNull(gsp);

        active_panel = gsp;
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

        AbsPosition roomBorders = active_panel.getRoomBorders();


        int xOffset = (int) Math.round(roomBorders.getX() + xBlocks * BLOCK_SIZE * active_panel.getScalingFactor());
        int yOffset = (int) Math.round(roomBorders.getY() + yBlocks * BLOCK_SIZE * active_panel.getScalingFactor());


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
        AbsPosition roomBorders = active_panel.getRoomBorders();
        // ricalcola absPos in base a offset
        absPos = new AbsPosition(absPos.getX() - roomBorders.getX(), absPos.getY() - roomBorders.getY());

        // scegli il blocco più vicino
        int x = (int) Math.round((double) absPos.getX() / (BLOCK_SIZE * active_panel.getScalingFactor()));
        int y = (int) Math.round((double) absPos.getY() / (BLOCK_SIZE * active_panel.getScalingFactor())) - 1;

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
    }

    /**
     *
     * @param label label su cui lavorare
     * @param pos posizione assoluta dell'angolo in basso a sinistra
     */
    public static void updateLabelPosition(JLabel label, AbsPosition pos)
    {
        int offsetHeight = label.getIcon().getIconHeight() - (int)(BLOCK_SIZE * active_panel.getScalingFactor());

        // pos è angolo in basso a sinistra, dobbiamo calcolarci l'angolo in alto a sinistra
        AbsPosition leftUpCornerPos = new AbsPosition(pos.getX(), pos.getY() - offsetHeight);

        label.setBounds(leftUpCornerPos.getX(),
                        leftUpCornerPos.getY(),
                        label.getIcon().getIconWidth(),
                        label.getIcon().getIconHeight());
    }
}
