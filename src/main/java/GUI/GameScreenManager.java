package GUI;

import characters.GameCharacter;
import items.Item;
import rooms.Coordinates;
import rooms.Room;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
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
    public static Coordinates calculateCoordinates(int xBlocks, int yBlocks, double rescalingFactor)
    {
        if(xBlocks < 0 || yBlocks < 0)
            throw new IllegalArgumentException();

        Insets insets = active_panel.getInsets();

        int xOffset = (int) Math.round(insets.left + xBlocks * BLOCK_SIZE * rescalingFactor);
        int yOffset = (int) Math.round(insets.top + yBlocks * BLOCK_SIZE * rescalingFactor);

        return new Coordinates(xOffset, yOffset);
    }


    public static Coordinates calculateBlocks(Coordinates coord, double rescalingFactor)
    {
        int xBlocks = (int)(coord.getX() / (BLOCK_SIZE * rescalingFactor));
        int yBlocks = (int)(coord.getY() / (BLOCK_SIZE * rescalingFactor));

        return new Coordinates(xBlocks, yBlocks);
    }

    // todo: completare e raffinare logica
    // xBlocks e yBlocks sono il blocco in basso a sinistra
    public static void updateSpritePosition(GameCharacter ch, int xBlocks, int yBlocks, Room currentRoom,
                                      Map<GameCharacter, JLabel> characterLabelMap, GameScreenPanel gameScreenPanel,
                                      double rescalingFactor)
    {
        Objects.requireNonNull(ch);

        // controlla che it è presente effettivamente nella stanza
        if(!characterLabelMap.containsKey(ch))
        {
            // TODO: ricontrollare eccezione lanciata
            throw new IllegalArgumentException("Personaggio non presente nella stanza");
        }

        // determinare se lo sprite entra nella stanza
        int roomWidth = currentRoom.getBWidth();
        int roomHeight = currentRoom.getBHeight();

        int spriteWidth = ch.getSprite().getWidth() / BLOCK_SIZE;
        int spriteHeight = ch.getSprite().getHeight() / BLOCK_SIZE;

        int rightBlock = xBlocks + spriteWidth - 1;
        int topBlock = yBlocks - spriteHeight + 1;

        // controlla se lo sprite entra per intero nella schermata
        boolean canMove = rightBlock < roomWidth && topBlock >= 0;

        if (currentRoom.getFloor().isWalkable(xBlocks, yBlocks) && canMove)
        {
            // System.out.println("ok");
            // System.out.println("Moving to " + xBlocks + ", " + yBlocks);
            // System.out.println("Top-left: " + topBlock + ", " + xBlocks);
            // System.out.println("Top-right: " + topBlock + ", " + rightBlock);
            // System.out.println("bottom-left: " + yBlocks + ", " + xBlocks);
            // System.out.println("bottom-right: " + yBlocks + ", " + rightBlock);
            // System.out.println("Sprite width: " + spriteWidth);
            // System.out.println("Sprite height: " + spriteHeight);
            Icon rescaledSprite = characterLabelMap.get(ch).getIcon();
            Insets insets = gameScreenPanel.getInsets();
            Coordinates coord = GameScreenManager.calculateCoordinates(xBlocks,
                                        topBlock, rescalingFactor);

            JLabel characterLabel = characterLabelMap.get(ch);
            characterLabel.setBounds(insets.left + coord.getX(), insets.top + coord.getY(),
                    rescaledSprite.getIconWidth(), rescaledSprite.getIconHeight());
        }

    }



    // todo: completare e raffinare logica
    // xBlocks e yBlocks sono il blocco in basso a sinistra
    public static void updateSpritePosition(Item it, int xBlocks, int yBlocks, Room currentRoom,
                                            Map<Item, JLabel> itemLabelMap, GameScreenPanel gameScreenPanel,
                                            double rescalingFactor)
    {
        Objects.requireNonNull(it);


        // controlla che it è presente effettivamente nella stanza
        if(!itemLabelMap.containsKey(it))
        {
            // TODO: ricontrollare eccezione lanciata
            throw new IllegalArgumentException("Item non presente nella stanza");
        }

        // determinare se lo sprite entra nella stanza
        int roomWidth = currentRoom.getBWidth();
        int roomHeight = currentRoom.getBHeight();

        int spriteWidth = it.getSprite().getWidth(null) / BLOCK_SIZE;
        int spriteHeight = it.getSprite().getHeight(null) / BLOCK_SIZE;

        int rightBlock = xBlocks + spriteWidth - 1;
        int topBlock = yBlocks - spriteHeight + 1;

        boolean canMove = rightBlock < roomWidth && topBlock >= 0;;

        if (canMove)
        {
            // System.out.println("ok");
            // System.out.println("Moving to " + xBlocks + ", " + yBlocks);
            // System.out.println("Top-left: " + topBlock + ", " + xBlocks);
            // System.out.println("Top-right: " + topBlock + ", " + rightBlock);
            // System.out.println("bottom-left: " + yBlocks + ", " + xBlocks);
            // System.out.println("bottom-right: " + yBlocks + ", " + rightBlock);
            // System.out.println("Sprite width: " + spriteWidth);
            // System.out.println("Sprite height: " + spriteHeight);
            Icon rescaledSprite = it.getScaledIconSprite(rescalingFactor);
            Insets insets = gameScreenPanel.getInsets();
            Coordinates coord = GameScreenManager.calculateCoordinates(xBlocks,
                    topBlock, rescalingFactor);

            JLabel itemLabel = itemLabelMap.get(it);
            itemLabel.setBounds(insets.left + coord.getX(), insets.top + coord.getY(),
                    rescaledSprite.getIconWidth(), rescaledSprite.getIconHeight());
        }

    }
}
