package GUI;

import animation.MovingAnimation;
import animation.StillAnimation;
import com.sun.tools.javac.Main;

import java.util.*;

import entity.GamePiece;
import entity.characters.GameCharacter;
import entity.characters.NPC;
import entity.items.Item;
import entity.items.PickupableItem;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
import general.GameManager;
import graphics.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class GameScreenPanel extends JLayeredPane
{
    /**
     * Dizionario che contiene gli oggetti presenti nella stanza (currentRoom)
     * e le JLabel associate al gameScreenPanel
     */
    private Map<Item, JLabel> itemLabelMap;
    private Map<GameCharacter, JLabel> characterLabelMap;
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
        currentRoom = initialRoom;
    }

    public void changeRoom(Room newRoom)
    {
        this.currentRoom = newRoom;

        List<Component> componentsToRemove = new ArrayList<>();

        Component[] comps = getComponentsInLayer(BACKGROUND_LAYER);

        for (Component comp : comps)
        {
            remove(comp);
        }

        comps = getComponentsInLayer(ITEM_LAYER);

        for (Component comp : comps)
        {
            remove(comp);
        }

        comps = getComponentsInLayer(CHARACTER_LAYER);

        for (Component comp : comps)
        {
            remove(comp);
        }

        // setta nuovo background
        JLabel backgroundLabel = new JLabel(SpriteManager.rescaledImageIcon(newRoom.getBackgroundImage(), rescalingFactor));

        backgroundLabel.setBounds(getInsets().left,
                getInsets().top,
                backgroundLabel.getIcon().getIconWidth(),
                backgroundLabel.getIcon().getIconHeight());

        // Aggiungi background al layer 0
        add(backgroundLabel, BACKGROUND_LAYER);

        // setta oggetti nella stanza
        itemLabelMap = new HashMap<>();
        characterLabelMap = new HashMap<>();

        List<GamePiece> pieces = newRoom.getPiecesPresent();

        for(GamePiece piece: pieces)
        {
            if(piece instanceof Item)
            {
                addGameItem((Item) piece, newRoom.getPiecePosition(piece));
            }
            else
            {
                addGameCharacter((GameCharacter) piece, newRoom.getPiecePosition(piece));
            }
        }
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

    private MainFrame retrieveParentFrame()
    {
        Container parent = getParent();

        while(!(parent instanceof MainFrame))
        {
            parent = parent.getParent();
        }

        return (MainFrame) parent;
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

    public void effectAnimation(GamePiece piece, String spritesheetPath, String jsonPath, String whatAnimation, BlockPosition pos, int finalWait)
    {
        if(whatAnimation.equals("Esplosione"))
        {
            // TODO: generalizzazione anche su gameCharacter
            JLabel pieceLabel = itemLabelMap.get(piece);
            JLabel effectLabel = new JLabel(pieceLabel.getIcon());
            add(effectLabel, EFFECT_LAYER);
            GameScreenManager.updateLabelPosition(effectLabel, pos);
            StillAnimation effectAnimation = StillAnimation.createExplosionAnimation(spritesheetPath, jsonPath, effectLabel, finalWait);
            effectAnimation.setActionOnEnd(() ->
                    {
                        setLayer(effectLabel, GameScreenPanel.GARBAGE_LAYER);
                        effectLabel.setIcon(null);
                        this.remove(effectLabel);
                    });

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

    public void movePiece(GamePiece piece, BlockPosition initialPos, BlockPosition finalPos, int millisecondWaitEnd, boolean withAnimation)
    {

        MovingAnimation animation;
        // crea animazione
        if(withAnimation)
            animation = createMoveAnimation(piece, initialPos, finalPos, millisecondWaitEnd);
        else
            animation = null;

        updatePiecePosition(piece, finalPos, animation);
    }

    public void updateSprite(GamePiece piece)
    {
        Icon newIcon = piece.getScaledIconSprite(rescalingFactor);
        if(piece instanceof Item)
            itemLabelMap.get(piece).setIcon(newIcon);
        else if (piece instanceof GameCharacter)
            characterLabelMap.get(piece).setIcon(newIcon);
    }


    public MovingAnimation createMoveAnimation(GamePiece piece, BlockPosition initialPos, BlockPosition finalPos, int millisecondWaitEnd)
    {
        JLabel labelToAnimate;

        if(piece instanceof Item)
            labelToAnimate = itemLabelMap.get(piece);
        else
            labelToAnimate = characterLabelMap.get(piece);

        return new MovingAnimation(labelToAnimate,
                                    initialPos, finalPos, millisecondWaitEnd, true, piece.getMovingFrames());
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

        // crea listener per il tasto sinistro
        GameMouseListener interactionListener = new GameMouseListener(MouseEvent.BUTTON1,
                null,
                () ->
                {
                    InventoryPanel inventoryPanel = retrieveParentFrame().getInventoryPanel();
                    PickupableItem selectedItem = inventoryPanel.getSelectedItem();
                    if(selectedItem != null)
                        selectedItem.useWith(it);
                });
        itemLabel.addMouseListener(interactionListener);

        // metti la coppia Item JLabel nel dizionario
        itemLabelMap.put(it, itemLabel);

        // aggiungi la label nell'ITEM_LAYER
        add(itemLabel, ITEM_LAYER);

        updatePiecePosition(it, pos, null);
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

        if(ch.getName().equals("Schwartz"))
        {
            // listener per il tasto sinistro, per usare gli oggetti
            // crea listener per il tasto sinistro
            GameMouseListener interactionListener = new GameMouseListener(MouseEvent.BUTTON1,
                    null,
                    () ->
                    {
                        InventoryPanel inventoryPanel = retrieveParentFrame().getInventoryPanel();
                        PickupableItem selectedItem = inventoryPanel.getSelectedItem();
                        if(selectedItem != null)
                            selectedItem.use();
                    });
            characterLabel.addMouseListener(interactionListener);
        }

        // metti la coppia Item JLabel nel dizionario
        characterLabelMap.put(ch, characterLabel);

        // aggiungi la label
        add(characterLabel, CHARACTER_LAYER);

        updatePiecePosition(ch, pos, null);
    }

    /**
     * Aggiorna la posizione di un oggetto nella stanza.
     *
     * @param piece GamePiece da posizionare
     * @param finalPos posizione d'arrivo dell'oggetto
     * @throws IllegalArgumentException se it non è presente nella stanza
     */
    private void updatePiecePosition(GamePiece piece, BlockPosition finalPos, MovingAnimation anim)
    {
        Objects.requireNonNull(piece);
        Objects.requireNonNull(finalPos);

        JLabel pieceLabel = null;
        boolean canGoOnWall = true;

        // controlla che ch sia presente effettivamente nella stanza
        if(characterLabelMap.containsKey(piece))
        {
            pieceLabel = characterLabelMap.get(piece);
            canGoOnWall = false;
        }
        if(itemLabelMap.containsKey(piece))
        {
            pieceLabel = itemLabelMap.get(piece);
        }

        if(pieceLabel == null)
            throw new IllegalArgumentException("GamePiece non presente nella stanza");

        updateSpritePosition(pieceLabel, finalPos, anim, canGoOnWall);
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
