package GUI;

import animation.MovingAnimation;
import animation.StillAnimation;

import java.util.*;

import entity.GamePiece;
import entity.characters.GameCharacter;
import entity.characters.NPC;
import entity.characters.PlayingCharacter;
import entity.items.Item;
import entity.items.PickupableItem;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
import graphics.SpriteManager;
import org.netbeans.lib.awtextra.AbsoluteConstraints;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;


public class GameScreenPanel extends JLayeredPane
{
    /**
     * Dizionario che contiene i GamePiece presenti nella stanza (currentRoom)
     * e le JLabel a essi associate.
     */
    private Map<GamePiece, JLabel> pieceLabelMap;
    private Room currentRoom;
    private double rescalingFactor;
    private JLabel backgroundLabel;

    public static final Integer GARBAGE_LAYER = 0; // Utilizzato per la rimozione degli oggetti, causa di bug
    public static final Integer BACKGROUND_LAYER = 1;
    // TODO: aggiungere algoritmo per posizionare correttamente secondo la prospettiva sia gli oggetti che i personaggi
    public static final Integer ITEM_LAYER = 2;
    public static final Integer CHARACTER_LAYER = 3;
    public static final Integer EFFECT_LAYER = 4;
    public static final Integer TEXT_BAR_LEVEL = 5;

    public GameScreenPanel(Room initialRoom)
    {
        super();
        pieceLabelMap = new HashMap<>();
        currentRoom = initialRoom;
    }


    public void setBackgroundLabel(JLabel backgroundLabel)
    {
        this.backgroundLabel = backgroundLabel;
    }
    public JLabel getBackgroundLabel()
    {
        return backgroundLabel;
    }

    public AbsPosition getRoomBorders()
    {
        int x = getInsets().left;
        int y = getInsets().top;

        return new AbsPosition(x, y);
    }

    public void changeRoom(Room newRoom)
    {
        // rimuovi giocatore dalla room
        PlayingCharacter.getPlayer().removeFromRoom();

        // rimuovi oggetti e personaggi
        removeAllPiecesFromScreen();

        // setta nuovo background
        JLabel backgroundLabel = (JLabel) getComponentsInLayer(BACKGROUND_LAYER)[0];
        backgroundLabel.setIcon(SpriteManager.rescaledImageIcon(newRoom.getBackgroundImage(), rescalingFactor));


        backgroundLabel.setBounds(getRoomBorders().getX(),
                getRoomBorders().getY(),
                backgroundLabel.getIcon().getIconWidth(),
                backgroundLabel.getIcon().getIconHeight());

        // aggiungi componenti della nuova stanza sullo schermo
        for(GamePiece piece: newRoom.getPiecesPresent())
            addPieceOnScreen(piece, newRoom.getPiecePosition(piece));

        // aggiungi il personaggio giocante nella stanza
        PlayingCharacter.getPlayer().addInRoom(newRoom, newRoom.getInitialPlayerPosition());

        // aggiorna currentRoom
        this.currentRoom = newRoom;
    }

    /**
     * Imposta il fattore di riscalamento.
     *
     * @param scalingFactor fattore di riscalamento
     */
    public void setScalingFactor(double scalingFactor)
    {
        this.rescalingFactor = scalingFactor;
    }

    /**
     * Restituisce il fattore di riscalamento.
     *
     * @return fattore di riscalamento
     */
    public double getScalingFactor()
    {
        return rescalingFactor;
    }

    /**
     * Ritorna la JLabel associata a un GamePiece presente sullo schermo.
     *
     * @param piece GamePiece di cui si richiede la label
     * @return label associata
     */
    public JLabel getLabelAssociated(GamePiece piece)
    {
        return pieceLabelMap.get(piece);
    }

    /**
     * Recupera il MainFrame antenato di questo GameScreenPanel.
     *
     * @return MainFrame antenato
     * @throws NullPointerException se tra i Component antenati non è presente alcun MainFrame
     */
    private MainFrame retrieveParentFrame()
    {
        Container parent = getParent();

        while(!(parent instanceof MainFrame))
            parent = parent.getParent();

        return (MainFrame) parent;
    }

    /**
     * NOTA: non usare per cicli
     * @param piece
     */
    public void removePieceFromScreen(GamePiece piece)
    {
        JLabel labelToRemove = pieceLabelMap.get(piece);

        //Sposta etichetta nel layer per la rimozione
        setLayer(labelToRemove, GARBAGE_LAYER);
        labelToRemove.setIcon(null);

        // rimuovere la JLabel dal gameScreenPanel
        remove(labelToRemove);

        // elimina la voce dal dizionario
        pieceLabelMap.remove(piece);
    }

    private void removeAllPiecesFromScreen()
    {
        List<JLabel> labelsToDelete = new ArrayList<>(pieceLabelMap.values());

        for(JLabel label : labelsToDelete)
        {
            setLayer(label, GARBAGE_LAYER);
            label.setIcon(null);
            remove(label);
        }

        pieceLabelMap = new HashMap<>();

        // TODO : continaure subito
    }



    public void effectAnimation(GamePiece piece, String spritesheetPath, String jsonPath, String whatAnimation, BlockPosition pos, int finalWait)
    {
        if(whatAnimation.equals("Esplosione"))
        {
            JLabel effectLabel = new JLabel(pieceLabelMap.get(piece).getIcon());
            add(effectLabel, EFFECT_LAYER);

            GameScreenManager.updateLabelPosition(effectLabel, pos);

            StillAnimation effectAnimation = StillAnimation.createCustomAnimation(spritesheetPath, jsonPath, effectLabel, finalWait);

            effectAnimation.setActionOnEnd(() ->
                    {
                        setLayer(effectLabel, GameScreenPanel.GARBAGE_LAYER);
                        effectLabel.setIcon(null);
                        this.remove(effectLabel);
                    });

            effectAnimation.start();
        }
    }

    public void addPieceOnScreen(GamePiece piece , BlockPosition pos)
    {
        if (piece instanceof Item)
            addGameItem((Item) piece, pos);
        else if (piece instanceof GameCharacter)
            addGameCharacter((GameCharacter) piece, pos);
    }

    /**
     *
     * @param piece
     * @param initialPos
     * @param finalPos
     * @param millisecondWaitEnd
     * @param withAnimation
     */
    public void movePiece(GamePiece piece, BlockPosition initialPos,
                          BlockPosition finalPos, int millisecondWaitEnd, boolean withAnimation)
    {
        Objects.requireNonNull(piece);
        Objects.requireNonNull(initialPos);
        Objects.requireNonNull(finalPos);

        MovingAnimation animation;
        // crea animazione
        if(withAnimation)
            animation = createMoveAnimation(piece, initialPos, finalPos, millisecondWaitEnd);
        else
            animation = null;

        updatePiecePosition(piece, finalPos, animation);
    }

    /**
     * Aggiorna la visualizzazione dello sprite del GamePiece specificato.
     *
     * Da utilizzare ad esempio nel caso in cui viene modificato lo sprite
     * del GamePiece, in questo modo si potrà effettivamente visualizzarlo sullo
     * schermo.
     *
     * @param piece GamePiece di cui aggiornare lo sprite
     */
    public void updateSprite(GamePiece piece)
    {
        Objects.requireNonNull(piece);

        Icon newIcon = piece.getScaledIconSprite(rescalingFactor);
        pieceLabelMap.get(piece).setIcon(newIcon);
    }

    /**
     * Crea l'animazione di movimento di un GamePiece, la quale parte da {@code initialPos},
     * termina in {@code finalPos} e alla fine attende {@code millisecondWaitEnd} millisecondi prima
     * che il gioco torni nello stato {@link GUI.gamestate.GameState.State#PLAYING}.
     *
     * @param piece GamePiece da animare
     * @param initialPos posizione di partenza dell'animazione
     * @param finalPos posizione di arrivo dell'animazione
     * @param millisecondWaitEnd millisecondi da aspettare alla fine dell'animazione
     * @return l'animazione che soddisfa le condizioni richieste
     */
    private MovingAnimation createMoveAnimation(GamePiece piece, BlockPosition initialPos,
                                                BlockPosition finalPos, int millisecondWaitEnd)
    {
        JLabel labelToAnimate = pieceLabelMap.get(piece);

        return new MovingAnimation(labelToAnimate,
                                    initialPos, finalPos, millisecondWaitEnd, true, piece.getMovingFrames());
    }


    /**
     * Aggiunge a questo GameScreenPanel una JLabel di un Item, la quale verrà posizionata
     * in modo tale che il suo blocco in basso a sinistra occupi la posizione {@code pos}.
     *
     * @param it Item del quale aggiungere la label
     * @param pos posizione del blocco in basso a sinistra dell'Item
     */
    private void addGameItem(Item it, BlockPosition pos)
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
        pieceLabelMap.put(it, itemLabel);

        // aggiungi la label nell'ITEM_LAYER
        add(itemLabel, ITEM_LAYER);

        updatePiecePosition(it, pos, null);
    }

    /**
     * Aggiunge a questo GameScreenPanel una JLabel di un GameCharacter, la quale verrà posizionata
     * in modo tale che il suo blocco in basso a sinistra occupi la posizione {@code pos}.
     *
     * @param ch GameCharacter del quale aggiungere la label
     * @param pos posizione del blocco in basso a sinistra del GameCharacter
     */
    private void addGameCharacter(GameCharacter ch, BlockPosition pos)
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

        if(ch instanceof PlayingCharacter)
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
        pieceLabelMap.put(ch, characterLabel);
        // aggiungi la label
        add(characterLabel, CHARACTER_LAYER);

        updatePiecePosition(ch, pos, null);
    }

    /**
     * Aggiorna la posizione della label associata a un GamePiece nella stanza.
     *
     * @param piece GamePiece da riposizionare
     * @param finalPos posizione d'arrivo dell'oggetto
     * @throws IllegalArgumentException se it non è presente nella stanza
     */
    private void updatePiecePosition(GamePiece piece, BlockPosition finalPos, MovingAnimation anim)
    {
        JLabel pieceLabel = pieceLabelMap.get(piece);
        // se è un oggetto può andare sulle pareti, se è un personaggio no
        boolean canGoOnWall = piece instanceof Item;

        if(pieceLabel == null)
            throw new IllegalArgumentException("GamePiece non presente nella stanza");

        updateSpritePosition(pieceLabel, finalPos, anim, canGoOnWall);
    }

    /**
     * Aggiorna la posizione di una label a finalPos, se possibile
     *
     * @param label JLabel di cui aggiornare la posizione
     * @param finalPos posizione del blocco in basso a sinistra a cui {@code label} deve arrivare
     * @param anim animazione di movimento, se questa dev'essere eseguita, {@code null} altrimenti
     * @param canGoOnWall flag che indica se il GamePiece può essere posizionato sulle pareti
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
            if(anim == null)
                GameScreenManager.updateLabelPosition(label, finalPos);
            else
                anim.start();
        }
    }
}
