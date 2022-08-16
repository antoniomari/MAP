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
import general.GameException;
import general.GameManager;
import graphics.SpriteManager;
import org.netbeans.lib.awtextra.AbsoluteConstraints;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;


public class GameScreenPanel extends JLayeredPane
{
    /*
        Layers per contenere tutti gli elementi di gioco nel pannello
     */
    /** Utilizzato per inserire oggetti da rimuovere in seguito, causa di bug */
    public static final Integer GARBAGE_LAYER = 0;
    /** Utilizzato per contenere l'immagine di sfondo della stanza. */
    public static final Integer BACKGROUND_LAYER = 1;
    /** Utilizzato per inserire Item nella stanza. */
    public static final Integer ITEM_LAYER = 2;
    /** Utilizzato per inserire GameCharacter nella stanza. */
    public static final Integer CHARACTER_LAYER = 3;
    /** Utilizzato per gli effetti animati. */
    public static final Integer EFFECT_LAYER = 4;
    /** Utilizzato per visualizzare la barra di testo. */
    public static final Integer TEXT_BAR_LEVEL = 5;
    // TODO: aggiungere algoritmo per posizionare correttamente secondo la prospettiva sia gli oggetti che i personaggi
    // tale algoritmo deve far sì che tutto ciò che sta sopra a te sia dietro e cosa sta sotto di te
    // sia davanti

    /** Stanza in cui si trova il Player. */
    private Room currentRoom;
    /**
     * Dizionario che contiene i GamePiece presenti nella stanza (currentRoom)
     * e le JLabel a essi associate.
     */
    private Map<GamePiece, JLabel> pieceLabelMap;

    /** Fattore di riscalamento per le icone (lo stesso del MainFrame). */
    private double rescalingFactor;
    /** JLabel per l'immagine di background della stanza. */
    private JLabel backgroundLabel;

    /**
     * Inizializza il pannello, impostando {@code initialRoom}
     * come stanza iniziale.
     *
     * @param initialRoom stanza iniziale
     */
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

    /**
     * Restituisce le coordinate, misurate in pixel,
     * dell'angolo in alto a sinistra del background, ovvero della stanza.
     *
     * @return coordinate, mìsurate in pixel, dell'angolo in alto a sinistra della stanza.
     */
    public AbsPosition getRoomBorders()
    {
        int x = backgroundLabel.getX();
        int y = backgroundLabel.getY();

        return new AbsPosition(x, y);
    }

    /**
     * Esegue il cambio stanza, posizionando il Player all'entrata
     * opportuna se si è entrati da una porta, alla posizione di default
     * della stanza altrimenti.
     *
     * @param newRoom stanza in cui entrare
     * @param screenWidth larghezza dello schermo, utilizzata per posizionare la stanza
     */
    public void changeRoom(Room newRoom, int screenWidth)
    {
        // punto cardinale dell'entrata di newRoom
        String cardinal;
        // controlla se newRoom è collegata con la vecchia stanza
        if(newRoom.equals(currentRoom.getNorth()))
        {
            cardinal = "south";
        }
        else if (newRoom.equals(currentRoom.getSouth()))
        {
            cardinal = "north";
        }
        else if (newRoom.equals(currentRoom.getWest()))
        {
            cardinal = "east";
        }
        else if (newRoom.equals(currentRoom.getEast()))
        {
            cardinal = "west";
        }
        else
        {
            // se le due stanze non sono direttamente collegate
            cardinal = null;
        }

        // rimuovi giocatore dalla room
        PlayingCharacter.getPlayer().removeFromRoom();

        // rimuovi oggetti e personaggi
        removeAllPiecesFromScreen();

        // aggiorna currentRoom
        this.currentRoom = newRoom;

        // setta nuovo background
        backgroundLabel.setIcon(SpriteManager.rescaledImageIcon(newRoom.getBackgroundImage(), rescalingFactor));

        // posiziona opportunamente il background TODO: aggiustare
        int xOffset = (screenWidth - backgroundLabel.getIcon().getIconWidth()) / 2;
        backgroundLabel.setBounds(getInsets().left + xOffset,
                getInsets().top,
                backgroundLabel.getIcon().getIconWidth(),
                backgroundLabel.getIcon().getIconHeight());

        // aggiungi componenti della nuova stanza sullo schermo
        for(GamePiece piece: newRoom.getPiecesPresent())
            addPieceOnScreen(piece, newRoom.getPiecePosition(piece));

        // aggiungi il personaggio giocante nella stanza
        if(cardinal == null)
            PlayingCharacter.getPlayer().addInRoom(newRoom, newRoom.getInitialPlayerPosition());
        else
            PlayingCharacter.getPlayer() // TODO: aggiustare qua il piazzamento
                    .addInRoom(newRoom, newRoom.getFloor().getNearestPlacement(
                            newRoom.getArrowPosition(cardinal).relativePosition(-2,0),
                            PlayingCharacter.getPlayer()));
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

    /**
     * Rimuovi ogni GamePiece dallo schermo.
     */
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

        // TODO: controllare se si può aggiustare con il metodo precedente
    }

    // TODO: aggiustare work in porgress
    public void effectAnimation(GamePiece piece, String spritesheetPath, String jsonPath, String whatAnimation, BlockPosition pos, int finalWait)
    {
        // TODO: aggiustare l'hard coding di "esplosione"
        if(whatAnimation.equals("Esplosione"))
        {
            JLabel effectLabel = new JLabel(pieceLabelMap.get(piece).getIcon());
            add(effectLabel, EFFECT_LAYER);

            GameScreenManager.updateLabelPosition(effectLabel, pos);

            StillAnimation effectAnimation = StillAnimation.createCustomAnimation(spritesheetPath, jsonPath, effectLabel);
            effectAnimation.setFinalDelay(finalWait);

            effectAnimation.setActionOnEnd(() ->
                    {
                        setLayer(effectLabel, GameScreenPanel.GARBAGE_LAYER);
                        effectLabel.setIcon(null);
                        this.remove(effectLabel);
                    });

            effectAnimation.start();
        }
    }

    /**
     * Aggiunge l'immagine di un GamePiece nella stanza correntemente visualizzata.
     *
     * @param piece pezzo da visualizzare
     * @param pos posizione in cui visualizzare {@code piece}
     */
    public void addPieceOnScreen(GamePiece piece , BlockPosition pos)
    {
        if (piece instanceof Item)
            addGameItem((Item) piece, pos);
        else if (piece instanceof GameCharacter)
            addGameCharacter((GameCharacter) piece, pos);
        else
            throw new GameException("GamePiece " + piece + " non valido");

    }

    /**
     * Sposta un GamePiece dalla posizione {@code initialPos} alla posizione {@code finalPos}.
     *
     * @param piece GamePiece da spostare sullo schermo
     * @param initialPos posizione iniziale del GamePiece, misurata in blocchi
     * @param finalPos posizione finale del GamePiece, misurata in blocchi
     * @param millisecondWaitEnd millisecondi da attendere dopo lo spostamento (gioco bloccato nello stato MOVING)
     * @param withAnimation {@code true} per eseguire l'animazione di movimento, {@code false} altrimenti
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

        // aggiorna posizione del pezzo
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
        // TODO: controllare se c'è codice duplicato
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

        // TODO: aggiustare per evitare copia
        GameMouseListener interactionListener = new GameMouseListener(MouseEvent.BUTTON1,
                null,
                () ->
                {
                    InventoryPanel inventoryPanel = retrieveParentFrame().getInventoryPanel();
                    PickupableItem selectedItem = inventoryPanel.getSelectedItem();
                    if(selectedItem != null)
                        selectedItem.useWith(ch);
                });
        characterLabel.addMouseListener(interactionListener);

        if(ch instanceof NPC)
        {
            // crea listener per il tasto destro, che deve visualizzare il corretto menu contestuale
            GameMouseListener popMenuListener = new GameMouseListener(MouseEvent.BUTTON3,
                    null, () -> PopMenuManager.showMenu(ch, characterLabel, 0, 0));
            characterLabel.addMouseListener(popMenuListener);
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
     * @param anim animazione di movimento, {@code null} se il GamePiece non dev'essere animato
     * @throws IllegalArgumentException se it non è presente nella stanza
     */
    private void updatePiecePosition(GamePiece piece, BlockPosition finalPos, MovingAnimation anim)
    {
        JLabel pieceLabel = pieceLabelMap.get(piece);
        // se è un oggetto può andare sulle pareti, se è un personaggio no
        boolean canGoOnWall = piece instanceof Item || piece instanceof NPC;
        // TODO: controllare NPC

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
        int xBlocks = finalPos.getX();
        int yBlocks = finalPos.getY();

        // determinare se lo sprite entra nella stanza
        int roomWidth = currentRoom.getBWidth();

        int spriteWidth = label.getIcon().getIconWidth() / (int)(GameManager.BLOCK_SIZE * rescalingFactor);
        int spriteHeight = label.getIcon().getIconHeight() / (int)(GameManager.BLOCK_SIZE * rescalingFactor);

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
        else
        {
            throw new GameException("Label non posizionabile in " + finalPos);
        }
    }
}
