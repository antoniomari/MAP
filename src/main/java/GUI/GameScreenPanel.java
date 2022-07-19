package GUI;

import animation.MovingAnimation;
import animation.StillAnimation;
import entity.GamePiece;
import entity.characters.GameCharacter;
import entity.characters.NPC;
import entity.items.Item;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
import general.GameManager;

import javax.swing.*;
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
    private final Map<GamePiece, MovingAnimation> activeMovingAnimation;
    private Room currentRoom;
    private double rescalingFactor;

    public static final Integer GARBAGE_LAYER = 0; // Utilizzato per la rimozione degli oggetti, causa di bug
    public static final Integer BACKGROUND_LAYER = 1;
    public static final Integer ITEM_LAYER = 2;
    public static final Integer CHARACTER_LAYER = 3;
    public static final Integer EFFECT_LAYER = 4;
    public static final Integer TEXT_BAR_LEVEL = 5;

    public GameScreenPanel(Room initialRoom)
    {
        super();
        itemLabelMap = new HashMap<>();
        characterLabelMap = new HashMap<>();
        activeMovingAnimation = new HashMap<>();
        currentRoom = initialRoom;
    }

    public void setScalingFactor(double scalingFactor)
    {
        this.rescalingFactor = scalingFactor;
    }

    public double getScalingFactor()
    {
        return rescalingFactor;
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

    public void effectAnimation(String whatAnimation, BlockPosition pos)
    {
        if(whatAnimation.equals("Esplosione"))
        {
            JLabel barileLabel = itemLabelMap.get((Item)GameManager.getPiece("Barile"));
            JLabel effectLabel = new JLabel(barileLabel.getIcon());
            add(effectLabel, EFFECT_LAYER);
            GameScreenManager.updateLabelPosition(effectLabel, pos);
            StillAnimation effectAnimation = StillAnimation.createExplosionAnimation(effectLabel, pos);
            effectAnimation.setActionOnEnd(() -> this.remove(effectLabel));
            effectAnimation.start();
        }

    }

    public void addItemCurrentRoom(Item item , BlockPosition pos)
    {
        addGameItem(item,pos);
    }

    public void addCharacterCurrentRoom(GameCharacter ch, BlockPosition pos)
    {
        addGameCharacter(ch, pos);
    }

    public void moveCharacter(GameCharacter ch, BlockPosition initialPos, BlockPosition finalPos, int millisecondWaitEnd, boolean withAnimation)
    {

        MovingAnimation animation;
        // crea animazione
        if(withAnimation)
            animation = createMoveAnimation(ch, initialPos, finalPos, millisecondWaitEnd);
        else
            animation = null;

        updateCharacterPosition(ch, finalPos, animation);
    }

    public void updateSprite(GamePiece piece)
    {
        Icon newIcon = piece.getScaledIconSprite(rescalingFactor);
        if(piece instanceof Item)
            itemLabelMap.get(piece).setIcon(newIcon);
        else if (piece instanceof GameCharacter)
            characterLabelMap.get(piece).setIcon(newIcon);
    }


    public MovingAnimation createMoveAnimation(GameCharacter ch, BlockPosition initialPos, BlockPosition finalPos, int millisecondWaitEnd)
    {
        return new MovingAnimation(characterLabelMap.get(ch),
                                    initialPos, finalPos, millisecondWaitEnd, true, ch.getMovingFrames());
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
        Icon rescaledSprite = ch.getScaledIconSprite(rescalingFactor);

        // crea la label corrispondente all'Item
        JLabel characterLabel = new JLabel(rescaledSprite);

        if(ch instanceof NPC)
        {
            // crea listener per il tasto destro, che deve visualizzare il corretto menu contestuale
            GameMouseListener popMenuListener = new GameMouseListener(MouseEvent.BUTTON3,
                    null, () -> PopMenuManager.showMenu(ch, characterLabel, 0, 0));
            characterLabel.addMouseListener(popMenuListener);
        }

        // metti la coppia Item JLabel nel dizionario
        characterLabelMap.put(ch, characterLabel);

        // aggiungi la label
        add(characterLabel, CHARACTER_LAYER);

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
     * @param finalPos blocco in basso a sinistra della posizione di arrivo del personaggio
     * @param anim animazione da eseguire, può essere null
     * @throws IllegalArgumentException se ch non è presente nella stanza
     */
    private void updateCharacterPosition(GameCharacter ch, BlockPosition finalPos, MovingAnimation anim)
    {
        Objects.requireNonNull(ch);
        Objects.requireNonNull(finalPos);


        // controlla che ch sia presente effettivamente nella stanza
        if(!characterLabelMap.containsKey(ch))
            throw new IllegalArgumentException("Personaggio non presente nella stanza");

        updateSpritePosition(characterLabelMap.get(ch), finalPos, anim, false);

    }


    /**
     * Aggiorna la posizione di una label a finalPos, se possibile
     *
     * @param label
     * @param finalPos posizione del blocco in basso a sinistra del GamePiece corrispondente alla label
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

        //TODO: bordo superiore, può salire di 1 il cursore
        //TODO: bordi laterali, si controlla la posizione e si dà quella più vicina disponibile


        // controlla se lo sprite entra per intero nella schermata
        boolean canMove = rightBlock < roomWidth && topBlock >= 0;

        if(!canGoOnWall)
            canMove = canMove && currentRoom.getFloor().isWalkable(xBlocks, yBlocks);

        if (canMove)
        {
            if(anim == null)
            {
                GameScreenManager.updateLabelPosition(label, finalPos);
            }
            else
            {
                anim.start();
            }
        }
    }

}
