package GUI;

import GUI.gamestate.GameState;
import entity.rooms.BlockPosition;
import entity.rooms.RoomFloor;
import general.GameManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class GameScreenManager
{

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


        int xOffset = (int) Math.round(roomBorders.getX() + xBlocks * GameManager.BLOCK_SIZE * active_panel.getScalingFactor());
        int yOffset = (int) Math.round(roomBorders.getY() + yBlocks * GameManager.BLOCK_SIZE * active_panel.getScalingFactor());


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
        int x = (int) Math.round((double) absPos.getX() / (GameManager.BLOCK_SIZE * active_panel.getScalingFactor()));
        int y = (int) Math.round((double) absPos.getY() / (GameManager.BLOCK_SIZE * active_panel.getScalingFactor())) - 1;

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
        int offsetHeight = label.getIcon().getIconHeight() - (int)(GameManager.BLOCK_SIZE * active_panel.getScalingFactor());

        // pos è angolo in basso a sinistra, dobbiamo calcolarci l'angolo in alto a sinistra
        AbsPosition leftUpCornerPos = new AbsPosition(pos.getX(), pos.getY() - offsetHeight);

        label.setBounds(leftUpCornerPos.getX(),
                        leftUpCornerPos.getY(),
                        label.getIcon().getIconWidth(),
                        label.getIcon().getIconHeight());
    }

    public static List<BlockPosition> calculatePath(BlockPosition initialPos, BlockPosition finalPos)
    {
        return calculatePathNPC(initialPos, finalPos);
        /*


        List<BlockPosition> positions = new ArrayList<>();

        BlockPosition nextStop = findNextStop(initialPos, finalPos);

        positions.add(nextStop);

        /*
        while(nextStop != finalPos)
        {
            positions.add(nextStop);
            nextStop = findNextStop(nextStop, finalPos);
        }



        if(nextStop != finalPos)
            positions.add(finalPos);
        return positions;
         */
    }


    private static BlockPosition findNextStop(BlockPosition initialPos, BlockPosition finalPos)
    {
        List<BlockPosition> intermediatePositions = new ArrayList<>(100);

        AbsPosition initialAbsPos = GameScreenManager.calculateCoordinates(initialPos);
        AbsPosition finalAbsPos = GameScreenManager.calculateCoordinates(finalPos);

        int deltaX = (finalAbsPos.getX() - initialAbsPos.getY()) / 100;
        int deltaY = (finalAbsPos.getY() - initialAbsPos.getY()) / 100;


        // suddivisione in 100 punti
        for(int i = 0; i < 100; i++)
        {
            intermediatePositions.add(GameScreenManager.calculateBlocks(new AbsPosition(initialAbsPos.getX() + i * deltaX, initialAbsPos.getY() + i * deltaY)));
            if(!active_panel.getCurrentRoom().getFloor().isWalkable(intermediatePositions.get(i)))
            {
                System.out.println("non puoi camminare su " + intermediatePositions.get(intermediatePositions.size() - 1));
                System.out.println("ti fermi su " + intermediatePositions.get(intermediatePositions.size() - 2));

                return intermediatePositions.get(intermediatePositions.size() - 2);
            }
        }

        return finalPos;
    }

    private static void goOn(RoomFloor rf, BlockPosition initialPos, BlockPosition finalPos)
    {
        // positiva se final a destra di initial, negativa altrimenti
        int rightDistance = finalPos.getX() - initialPos.getX();
        // positiva se final sotto initial, negativa altrimenti
        int downDistance = finalPos.getY() - initialPos.getY();

        // true se l'asse x ha movimento libero verso la direzione target
        boolean xFree;
        // true se l'asse y ha movimento libero verso la direzione target
        boolean yFree;

        // CASO vai a destra
        if(rightDistance > 0)
        {
            xFree = rf.isWalkable(initialPos.relativePosition(1, 0));
        }

        // CASO: vai sotto
        if(downDistance > 0)
        {
            yFree = rf.isWalkable(initialPos.relativePosition(0, 1));
        }

    }



    public static List<BlockPosition> calculatePathNPC(BlockPosition initialPos, BlockPosition finalPos)
    {
        List<BlockPosition> positions = new ArrayList<>();
        positions.add(finalPos);

        return positions;
    }



}
