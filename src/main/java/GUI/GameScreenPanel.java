package GUI;

import animation.MovingAnimation;
import characters.GameCharacter;
import characters.PlayingCharacter;
import graphics.SpriteManager;
import items.Item;
import rooms.BlockPosition;
import rooms.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameScreenPanel extends JLayeredPane
{
    /**
     * Dizionario che contiene gli oggetti presenti nella stanza (currentRoom)
     * e le JLabel associate al gameScreenPanel
     */
    private final Map<Item, JLabel> itemLabelMap;
    private final Map<GameCharacter, JLabel> characterLabelMap;
    private Room currentRoom;
    private double rescalingFactor;

    public final static Integer GARBAGE_LAYER = 0; // Utilizzato per la rimozione degli oggetti, causa di bug
    public final static Integer BACKGROUND_LAYER = 1;
    public final static Integer ITEM_LAYER = 2;
    public final static Integer TEXT_BAR_LEVEL = 3;

    public GameScreenPanel(Room initialRoom)
    {
        super();
        itemLabelMap = new HashMap<>();
        characterLabelMap = new HashMap<>();
        currentRoom = initialRoom;
    }

    public void setScalingFactor(double scalingFactor)
    {
        this.rescalingFactor = scalingFactor;
    }

    public JLabel getLabelAssociated(Item item)
    {
        return itemLabelMap.get(item);
    }

    public void removeItemCurrentRoom(Item item)
    {
        JLabel labelToRemove = itemLabelMap.get(item);

        //Sposta etichetta nel layer per la rimozione
        setLayer(labelToRemove, GameScreenPanel.GARBAGE_LAYER);
        labelToRemove.setIcon(null);

        // rimuovere la JLabel dal gameScreenPanel
        remove(labelToRemove);

        // elimina la voce dal dizionario
        itemLabelMap.remove(item);
    }

    public void addItemCurrentRoom(Item item , BlockPosition pos)
    {
        addGameItem(item,pos);
    }

    public void addCharacterCurrentRoom(GameCharacter ch, BlockPosition pos)
    {
        addGameCharacter(ch, pos);
    }

    public void moveCharacter(GameCharacter ch, BlockPosition finalPos, boolean withAnimation)
    {

        MovingAnimation animation;
        // crea animazione
        if(withAnimation)
            animation = createMoveAnimation(ch);
        else
            animation = null;

        updateCharacterPosition(ch, finalPos, animation);
    }

    // todo: gestire opportunamente final coords
    public MovingAnimation createMoveAnimation(GameCharacter ch)
    {
        MovingAnimation moveAnimation = new MovingAnimation(characterLabelMap.get(ch),  true);
        moveAnimation.setInsets(getInsets());

        return moveAnimation;
    }

    /**
     * Aggiunge al gameScreenPanel un Item, il quale verrà posizionato
     * nel blocco desiderato.
     *
     * Crea una JLabel associata all'oggetto e la aggiunge nel gameScreenPanel
     * per poter stampare lo sprite dell'oggetto sullo schermo.
     *
     * @param it oggetto da aggiungere
     * @param pos blocco in cui piazzare l'oggetto
     */
    public void addGameItem(Item it, BlockPosition pos)
    {
        // recupera lo sprite della giusta dimensione
        Icon rescaledSprite = it.getScaledIconSprite(rescalingFactor);

        // crea la label corrispondente all'Item
        JLabel itemLabel = new JLabel(rescaledSprite);

        // crea listener per il tasto destro, che deve visualizzare il corretto menu contestuale
        GameMouseListener popMenuListener = new GameMouseListener(MouseEvent.BUTTON3,
                null, () -> PopMenuManager.showMenu(it, itemLabel, 0, 0));
        itemLabel.addMouseListener(popMenuListener);

        // metti la coppia Item JLabel nel dizionario
        itemLabelMap.put(it, itemLabel);

        // aggiungi la label nell'ITEM_LAYER
        add(itemLabel, ITEM_LAYER);

        updateItemPosition(it, pos);
    }

    public void addGameCharacter(GameCharacter ch, BlockPosition pos)
    {
        // recupera lo sprite della giusta dimensione
        Icon rescaledSprite = SpriteManager.rescaledImageIcon(ch.getSprite(), rescalingFactor);

        // crea la label corrispondente all'Item
        JLabel characterLabel = new JLabel(rescaledSprite);

        // metti la coppia Item JLabel nel dizionario
        characterLabelMap.put(ch, characterLabel);

        // aggiungi la label nell'ITEM_LAYER  TODO pensare al layer
        add(characterLabel, ITEM_LAYER);

        updateCharacterPosition(ch, pos, null);
    }

    /**
     * Aggiorna la posizione di un oggetto nella stanza.
     *
     * @param it oggetto da riposizionare
     * @param finalPos posizione d'arrivo dell'oggetto
     * @throws IllegalArgumentException se it non è presente nella stanza
     */
    private void updateItemPosition(Item it, BlockPosition finalPos)
    {
        Objects.requireNonNull(it);
        Objects.requireNonNull(finalPos);

        // controlla che it sia presente effettivamente nella stanza
        if(!itemLabelMap.containsKey(it))
            throw new IllegalArgumentException("Item non presente nella stanza");

        updateSpritePosition(itemLabelMap.get(it), finalPos, null, true);
    }

    /**
     * Aggiorna la posizione di un personaggio nella stanza.
     *
     * @param ch personaggio da riposizionare
     * @param finalPos posizione d'arrivo del personaggio
     * @param anim animazione da eseguire, può essere null
     * @throws IllegalArgumentException se ch non è presente nella stanza
     */
    private void updateCharacterPosition(GameCharacter ch, BlockPosition finalPos, MovingAnimation anim)
    {
        Objects.requireNonNull(ch);
        Objects.requireNonNull(finalPos);

        // controlla che ch sia presente effettivamente nella stanza
        if(!characterLabelMap.containsKey(ch))
            throw new IllegalArgumentException("Item non presente nella stanza");

        updateSpritePosition(characterLabelMap.get(ch), finalPos, anim, false);

    }


    /**
     * Aggiorna la posizione di una label a finalPos, se possibile
     *
     * @param label
     * @param finalPos
     * @param anim
     * @param canGoOnWall
     */
    private void updateSpritePosition(JLabel label, BlockPosition finalPos, MovingAnimation anim, boolean canGoOnWall)
    {

        int BLOCK_SIZE = 24;

        int xBlocks = finalPos.getX();
        int yBlocks = finalPos.getY();


        // determinare se lo sprite entra nella stanza
        int roomWidth = currentRoom.getBWidth();

        int spriteWidth = label.getIcon().getIconWidth() / (int)(BLOCK_SIZE * rescalingFactor);
        int spriteHeight = label.getIcon().getIconHeight() / (int)(BLOCK_SIZE * rescalingFactor);

        int rightBlock = xBlocks + spriteWidth - 1;
        int topBlock = yBlocks - spriteHeight + 1;

        // controlla se lo sprite entra per intero nella schermata
        boolean canMove = rightBlock < roomWidth && topBlock >= 0;

        if(!canGoOnWall)
            canMove = canMove && currentRoom.getFloor().isWalkable(xBlocks, yBlocks);

        if (canMove)
        {
            // update finalPosition
            finalPos = new BlockPosition(xBlocks, topBlock);

            if(anim == null)
            {
                GameScreenManager.updateLabelPosition(label, finalPos, rescalingFactor);
            }
            else
            {
                anim.setFinalCoord(GameScreenManager.calculateCoordinates(finalPos, rescalingFactor));
                anim.start();
                // TODO: stoppare
            }
        }
    }

}
