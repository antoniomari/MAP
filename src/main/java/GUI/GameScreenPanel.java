package GUI;

import characters.GameCharacter;
import graphics.SpriteManager;
import items.Item;
import rooms.Coordinates;
import rooms.Room;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

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

    public void addItemCurrentRoom(Item item , Coordinates coord)
    {
        Coordinates blockCoord = GameScreenManager.calculateBlocks(coord, rescalingFactor);
        addGameItem(item, blockCoord.getX(), blockCoord.getY());
    }

    public void addCharacterCurrentRoom(GameCharacter ch, Coordinates coord)
    {
        Coordinates blockCoord = GameScreenManager.calculateBlocks(coord, rescalingFactor);
        addGameCharacter(ch, blockCoord.getX(), blockCoord.getY());
    }

    public void moveCharacter(GameCharacter ch, Coordinates coord)
    {
        Coordinates blockCoord = GameScreenManager.calculateBlocks(coord, rescalingFactor);
        updateCharacterPosition(ch, blockCoord.getX(), blockCoord.getY());
    }


    /**
     * Aggiunge al gameScreenPanel un Item, il quale verrà posizionato
     * nel blocco desiderato.
     *
     * Crea una JLabel associata all'oggetto e la aggiunge nel gameScreenPanel
     * per poter stampare lo sprite dell'oggetto sullo schermo.
     *
     * @param it oggetto da aggiungere
     * @param xBlocks numero di blocchi a sinsitra di quello desiderato
     * @param yBlocks numero di blocchi sopra rispetto a quello desiderato
     */
    public void addGameItem(Item it, final int xBlocks, final int yBlocks)
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

        updateItemPosition(it, xBlocks, yBlocks);
    }

    public void addGameCharacter(GameCharacter ch, final int xBlocks, final int yBlocks)
    {
        // recupera lo sprite della giusta dimensione
        Icon rescaledSprite = SpriteManager.rescaledImageIcon(ch.getSprite(), rescalingFactor);

        // crea la label corrispondente all'Item
        JLabel characterLabel = new JLabel(rescaledSprite);

        // metti la coppia Item JLabel nel dizionario
        characterLabelMap.put(ch, characterLabel);

        // aggiungi la label nell'ITEM_LAYER  TODO pensare al layer
        add(characterLabel, ITEM_LAYER);

        updateCharacterPosition(ch, xBlocks, yBlocks);
    }

    /**
     * Aggiorna la posizione di un oggetto nella stanza.
     *
     * @param it oggetto da riposizionare
     * @param xBlocks blocco x
     * @param yBlocks blocco y
     * @throws IllegalArgumentException se it non è presente nella stanza
     */
    public void updateItemPosition(Item it, int xBlocks, int yBlocks)
    {
        GameScreenManager.updateSpritePosition(it, xBlocks, yBlocks, currentRoom, itemLabelMap,
                this, rescalingFactor);
    }

    /**
     * Aggiorna la posizione di un personaggio nella stanza.
     *
     * @param ch personaggio da riposizionare
     * @param xBlocks blocco x
     * @param yBlocks blocco y
     * @throws IllegalArgumentException se ch non è presente nella stanza
     */
    public void updateCharacterPosition(GameCharacter ch, int xBlocks, int yBlocks)
    {
        GameScreenManager.updateSpritePosition(ch, xBlocks, yBlocks, currentRoom, characterLabelMap,
                this, rescalingFactor);

    }
}
