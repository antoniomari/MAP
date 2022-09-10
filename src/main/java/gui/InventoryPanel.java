package gui;

import animation.StillAnimation;
import entity.characters.PlayingCharacter;
import general.Pair;
import graphics.SpriteManager;
import entity.items.PickupableItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Classe che rappresenta il pannello che costituisce la barra dell'inventario.
 */
public class InventoryPanel extends JLayeredPane
{
    // Dimensioni (nota: utilizziamo la costante pubblica della classe PlayingCharacter) //
    /** Numero di oggetti nella barra */
    private final static int BAR_SIZE = 8;
    /** Capacità dell'inventario. */
    private final static int INVENTORY_SIZE = PlayingCharacter.INVENTORY_SIZE;
    /** Numero di barre */
    private final static int MAX_BAR = INVENTORY_SIZE / BAR_SIZE;
    /** Dimensione lato sprite oggetti (quadrato) */
    private final static int ORIGINAL_ITEM_SIZE = 48;

    // Path immagini e json //
    private final static String BAR_PATH = "/img/inventario/Barra oggetti inventario.png";
    private final static String TEXT_BOARD_PATH = "/img/inventario/barra nome oggetto.png";
    private final static String BUTTON_SPRITESHEET_PATH = "/img/inventario/bottoni.png";
    private final static String BUTTON_JSON_PATH = "/img/inventario/bottoni.json";
    private final static String SELECTION_ITEM_PATH = "/img/inventario/oggetto selezionato.png";

    // Immagini png (da caricare) //
    private final static BufferedImage BAR_IMAGE;
    private final static BufferedImage SELECTION_IMAGE;
    private final static BufferedImage BUTTON_SPRITESHEET;
    private final static BufferedImage TEXT_BOARD_IMAGE;

    // Livelli del LayeredPane inventario //
    private final static Integer BAR_LEVEL = 1;
    private final static Integer SELECTION_LEVEL = 2;
    private final static Integer ITEM_LEVEL = 3;

    // Costante per la deselezione (dare come parametro a select(i)) //
    private final static int NO_ITEM = -1;

    // Animazioni
    private StillAnimation UP_BUTTON_PRESS;
    private StillAnimation DOWN_BUTTON_PRESS;


    //**************************************************
    //            VARIABILI DI ISTANZA
    //**************************************************

    // Componenti swing //
    private JLabel barLabel; // barra inventario
    private JLabel upButtonLabel;
    private JLabel downButtonLabel;
    private JLabel selectionLabel; // label per stampare l'immagine dell'item selezionato
    private JLabel textBoardLabel; // label per la casella di testo sulla destra
    private JLabel textLabel; // area di testo per la casella sulla destra
    /** Lista delle etichette associate a ciascun elemento della barra dell'inventario */
    private List<JLabel> itemLabelList;

    /** Barra correntemente visualizzata */
    private int currentBar;
    private PickupableItem selectedItem;

    /** Icona corrispondente a SELECTION_IMAGE, resa variabile in quanto è riscalata ed è necessario
     * salvare un riferimento esterno ad esa per poter correttamente selezionare e deselezionare.
     */
    private Icon selectionIcon;

    /** Fattore di riscalamento per tutte le icone */
    private double scalingFactor;
    /** calcolato tramite (int) (ORIGINAL_ITEM_SIZE * scalingFactor) */
    private int scaledItemSize;

    /** lista che contiene riferimenti a tutti gli oggetti dell'inventario e alle rispettive
     * icone, riscalate secondo il fattore di riscalamento. */
    private List<Pair<PickupableItem, Icon>> inventoryItemIconList;


    static
    {
        // Caricamento immagini barra, bottoni e cella di selezione
        BAR_IMAGE = SpriteManager.loadImage(BAR_PATH);
        BUTTON_SPRITESHEET = SpriteManager.loadImage(BUTTON_SPRITESHEET_PATH);
        SELECTION_IMAGE = SpriteManager.loadImage(SELECTION_ITEM_PATH);
        TEXT_BOARD_IMAGE = SpriteManager.loadImage(TEXT_BOARD_PATH);
    }

    @Override
    public String getToolTipText(MouseEvent event)
    {
        return super.getToolTipText(event);
    }

    public InventoryPanel(int preferredHeight)
    {
        super();

        // lo scalingFactor è il rapporto tra l'altezza del menu e quella delle icone originali
        scalingFactor = (double) preferredHeight / ORIGINAL_ITEM_SIZE;

        int inventoryWidth = BAR_IMAGE.getWidth() + 2 * ORIGINAL_ITEM_SIZE + TEXT_BOARD_IMAGE.getWidth();
        int scaledWidth = (int) (scalingFactor * inventoryWidth);
        if (scaledWidth > MainFrame.SCREEN_WIDTH)
        {
            scalingFactor = (double) MainFrame.SCREEN_WIDTH/inventoryWidth;
        }
        scaledItemSize = (int) (ORIGINAL_ITEM_SIZE * scalingFactor);

        initItemIconList();  // inizializza sulla base dell'inventario corrente del giocatore
        initSelection();  // inizializza label e icona di selezione (all'inizio deselezionato)
        initBar();  // inizializza barra dell'inventario
        initButtons();  // inizializza bottoni
        initTextBoard();
        setupDimensions();  // calcola larghezza e altezza di this e impostale
        initLabelList();  // inizializza le label per la visualizzazione icone oggetti
        initAnimation();  // inizializza le animazioni

        // visualizza barra 1
        currentBar = 1;
        refreshBar();
    }

    private void initItemIconList()
    {
        // inizializza riferimento all'inventario del personaggio giocante
        List<PickupableItem> characterInventory = PlayingCharacter.getPlayer().getInventory();

        // utilizziamo LinkedHashMap per mantenere l'ordine di inserimento
        inventoryItemIconList = new ArrayList<>(PlayingCharacter.INVENTORY_SIZE);

        for(PickupableItem item : characterInventory)
        {
            inventoryItemIconList.add(new Pair<>(item, item.getScaledIconSprite(scalingFactor)));
        }
    }

    private void initSelection()
    {
        // inizialmente nessun oggetto è selezionato
        selectedItem = null;

        selectionLabel = new JLabel();
        selectionIcon = SpriteManager.rescaledImageIcon(SELECTION_IMAGE, scalingFactor);
        add(selectionLabel, SELECTION_LEVEL);
    }


    private void initBar()
    {
        Insets inventoryInsets = this.getInsets();

        // setup background, immagine della barra dell'inventario
        barLabel = new JLabel(SpriteManager.rescaledImageIcon(BAR_IMAGE, scalingFactor));
        // imposta bordi background
        int backgroundWidth = barLabel.getIcon().getIconWidth();
        int backgroundHeight = barLabel.getIcon().getIconHeight();

        barLabel.setBounds(inventoryInsets.left +  scaledItemSize * 2, inventoryInsets.top,
                backgroundWidth, backgroundHeight);

        add(barLabel, BAR_LEVEL);
    }


    private void initButtons()
    {
        Insets inventoryInsets = this.getInsets();

        // bottone "up" per visualizzare barra dell'inventario precedente
        upButtonLabel = new JLabel(
                SpriteManager.rescaledImageIcon(SpriteManager.loadSpriteByName(
                        BUTTON_SPRITESHEET, BUTTON_JSON_PATH, "up"), scalingFactor));

        upButtonLabel.setBounds(inventoryInsets.left, inventoryInsets.top,
                upButtonLabel.getIcon().getIconWidth(), upButtonLabel.getIcon().getIconHeight());

        // bottone "down" per visualizzare  barra dell'inventario successiva
        downButtonLabel = new JLabel(
                SpriteManager.rescaledImageIcon(SpriteManager.loadSpriteByName(
                        BUTTON_SPRITESHEET, BUTTON_JSON_PATH, "down"), scalingFactor));

        downButtonLabel.setBounds(inventoryInsets.left + scaledItemSize, inventoryInsets.top,
                downButtonLabel.getIcon().getIconWidth(), downButtonLabel.getIcon().getIconHeight());

        // aggiungi MouseListener per il tasto sinistro
        GameMouseListener upListener = new GameMouseListener(
                            GameMouseListener.Button.LEFT, () -> {UP_BUTTON_PRESS.start();visualizePreviousBar();}, null);
        GameMouseListener downListener = new GameMouseListener(
                            GameMouseListener.Button.LEFT, () -> {DOWN_BUTTON_PRESS.start(); visualizeNextBar(); }, null);
        // registra listener
        upButtonLabel.addMouseListener(upListener);
        downButtonLabel.addMouseListener(downListener);

        // aggiungi bottoni all'InventoryPanel
        add(upButtonLabel, BAR_LEVEL);
        add(downButtonLabel, BAR_LEVEL);
    }

    private void initTextBoard()
    {
        Font dialogFont;
        try
        {
            dialogFont = Font.createFont(Font.TRUETYPE_FONT, new File(TextBarPanel.FONT_PATH));
        }
        catch (IOException | FontFormatException e)
        {
            // errore nel caricamento del font
            throw new IOError(e);
        }

        Icon textBoardIcon = SpriteManager.rescaledImageIcon(TEXT_BOARD_IMAGE, scalingFactor);

        textBoardLabel = new JLabel(textBoardIcon);
        Insets inventoryInsets = getInsets();
        textBoardLabel.setBounds(inventoryInsets.left + scaledItemSize * (2 + BAR_SIZE),
                                inventoryInsets.top, textBoardIcon.getIconWidth(),
                                textBoardIcon.getIconHeight());
        add(textBoardLabel, BAR_LEVEL);


        textLabel= new JLabel();
        textLabel.setFont(dialogFont.deriveFont((float)(15 * scalingFactor)));
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);

        int horizontalBorder = (int) (20 * scalingFactor);
        int verticalBorder = (int) (10 * scalingFactor);
        textLabel.setBounds(inventoryInsets.left + scaledItemSize * (2 + BAR_SIZE) + horizontalBorder,
                inventoryInsets.top + verticalBorder, textBoardIcon.getIconWidth() - 2 * horizontalBorder,
                textBoardIcon.getIconHeight() - 2 * verticalBorder);

        add(textLabel, SELECTION_LEVEL);
    }

    private void setupDimensions()
    {
        // calcolo larghezza pannello
        int originalInventoryWidth = ORIGINAL_ITEM_SIZE * (BAR_SIZE + 2 + 4); // + 2 bottoni
                                                                    // e +4 (dimensione textBoard)
        int inventoryWidth = (int) (originalInventoryWidth * scalingFactor);

        // imposta dimensione del pannello
        setPreferredSize(new Dimension(inventoryWidth, scaledItemSize));
    }


    private void initLabelList()
    {
        Insets inventoryInsets = getInsets();

        // crea array di label
        itemLabelList = new ArrayList<>(BAR_SIZE);

        for(int i = 0; i < BAR_SIZE; i++)
        {
            // crea label
            JLabel tempLabel = new JLabel();

            // posiziona label
            AbsPosition coord = calculateOffset(i);
            tempLabel.setBounds(inventoryInsets.left + coord.getX(),
                                inventoryInsets.top +  coord.getY(),
                                    scaledItemSize, scaledItemSize);
            // aggiungi label alla lista
            itemLabelList.add(tempLabel);
            // aggiungi label al pannello
            add(tempLabel, ITEM_LEVEL);

            // aggiungi MouseListener per la selezione (tasto sinistro)
            GameMouseListener selectionListener = new GameMouseListener(
                        GameMouseListener.Button.LEFT,
                    () ->
                    {
                        selectItem(itemLabelList.indexOf(tempLabel));
                        if(selectedItem != null)
                            showTextOnBoard(getSelectedItem().getName());
                    }, null);

            GameMouseListener observeListener = new GameMouseListener(
                    GameMouseListener.Button.RIGHT,
                    () ->
                    {
                        if(selectedItem != null)  // TODO : aggiustare in modo tale che esca descrizione click solo item
                        {
                            java.awt.Container parent = getParent();
                            while(!(parent instanceof MainFrame))
                                parent = parent.getParent();
                            ((MainFrame)parent).getTextBarPanel().showTextBar(selectedItem.getDescription());
                        }
                    },
                    null);

            tempLabel.addMouseListener(selectionListener);
            tempLabel.addMouseListener(observeListener);
        }
    }

    private void initAnimation()
    {

        // Creazione animazioni
        List<Image> upFrames = new ArrayList<>();
        upFrames.add(SpriteManager.loadSpriteByName(BUTTON_SPRITESHEET, BUTTON_JSON_PATH, "upPressed"));
        upFrames.add(SpriteManager.loadSpriteByName(BUTTON_SPRITESHEET, BUTTON_JSON_PATH, "up"));

        UP_BUTTON_PRESS = new StillAnimation(upButtonLabel, upFrames, 100, false, StillAnimation.DEFAULT_DELAY_MILLISECONDS);

        List<Image> downFrames = new ArrayList<>();
        downFrames.add(SpriteManager.loadSpriteByName(BUTTON_SPRITESHEET, BUTTON_JSON_PATH, "downPressed"));
        downFrames.add(SpriteManager.loadSpriteByName(BUTTON_SPRITESHEET, BUTTON_JSON_PATH, "down"));

        DOWN_BUTTON_PRESS = new StillAnimation(downButtonLabel, downFrames, 100, false, StillAnimation.DEFAULT_DELAY_MILLISECONDS);
    }

    /**
     * Seleziona l'item corrispondente alla label in posizione i.
     *
     * @param i indice della label: da 0 a BAR_SIZE-1 (da sinistra verso destra),
     *          oppure NO_ITEM per indicare che non si sta selezionando alcun item.
     */
    private void selectItem(int i)
    {
        // calcola indice dell'item in inventoryItemIconList
        int itemIndex = (currentBar -1) * 10 +  i;

        // deseleziona icona
        selectionLabel.setIcon(null);

        // CASO indice invalido (NO_ITEM è un indice invalido)
        if (itemIndex <= -1 || itemIndex >= inventoryItemIconList.size())
        {
            selectedItem = null;
            hideTextOnBoard();
        }
        else // indice valido
        {
            // recupera l'elemento selezionato
            selectedItem = inventoryItemIconList.get(itemIndex).getObject1();

            // aggiorna posizione selection label
            AbsPosition coord = calculateOffset(i);
            selectionLabel.setBounds(getInsets().left + coord.getX(), getInsets().top + coord.getY(),
                    selectionIcon.getIconWidth(), selectionIcon.getIconHeight());

            // reimposta icona
            selectionLabel.setIcon(selectionIcon);
        }
    }

    public PickupableItem getSelectedItem()
    {
        return selectedItem;
    }

    /**
     * Calcola la posizione della i-esima label nella barra dell'inventario,
     * con i = 0, ..., BAR_SIZE-1.
     *
     * @param i indice della label
     * @return coordinate di posizionamento della i-esima label
     */
    private AbsPosition calculateOffset(int i)
    {
        return new AbsPosition((i + 2) * scaledItemSize + 1, 1);
    }

    /**
     * Aggiunge l'icona di un oggetto nell'inventario
     *
     * @param item item aggiunto
     */
    public void addItem(PickupableItem item)
    {
        if(inventoryItemIconList.size() < PlayingCharacter.INVENTORY_SIZE)
            inventoryItemIconList.add(new Pair<>(item, item.getScaledIconSprite(scalingFactor)));

        int lastIndex = inventoryItemIconList.size() - 1;
        displayBar(lastIndex / BAR_SIZE + 1);
    }


    /**
     * Rimuovi oggetto dall'inventario.
     *
     * @param item oggetto da rimuovere. Se non è presente nell'inventario,
     *             allora non succede nulla
     */
    public void dropFromInventory(PickupableItem item)
    {
        Objects.requireNonNull(item);

        // cerca oggetto nell'inventario e rimuovilo
        for(Pair<PickupableItem, Icon> p : inventoryItemIconList)
            if(p.getObject1() == item)
            {
                inventoryItemIconList.remove(p);

                // se era l'oggetto selezionato, allora deseleziona e ricarica la barra
                if(selectedItem == item)
                {
                    selectItem(NO_ITEM);
                    refreshBar();
                }

                return;
            }
    }

    /**
     * Ricarica la barra dell'inventario.
     *
     * Quando il contenuto della barra viene modificato,
     * permette di visualizzare immediatamente la modifica.
     *
     */
    public void refreshBar()
    {
        displayBar(currentBar);
    }

    /**
     * Esegue il display della barra del menù richiesta
     * @param i numero della barra richiesta
     */
    public void displayBar(int i)
    {
        // controllo correttezza indice
        if(i < 1)
            throw new IllegalArgumentException("i < 1");

        if(i > MAX_BAR)
            throw new IllegalArgumentException("i > maximum value");

        // selezionare la sottolista
        int start = (i-1) * 10;
        int end = Math.min(start + 10, inventoryItemIconList.size());

        // caso barra completamente vuota
        List<Pair<PickupableItem, Icon>> sublist;

        if(start >= inventoryItemIconList.size())
            sublist = new ArrayList<>(); // lista vuota
        else
            sublist = inventoryItemIconList.subList(start, end);

        // binding di ogni elemento della sottolista nella label
        for(int j = 0; j < sublist.size(); j++)
            itemLabelList.get(j).setIcon(sublist.get(j).getObject2());

        // le rimanenti icone devono essere null
        for(int j = sublist.size(); j < BAR_SIZE; j++)
            itemLabelList.get(j).setIcon(null);
    }

    /**
     * Visualizza la barra dell'inventario
     * successiva rispetto a quella attualmente
     * visualizzata.
     */
    public void visualizeNextBar()
    {
        if(currentBar < MAX_BAR)
        {
            selectItem(NO_ITEM);
            displayBar(++currentBar);
        }
        // altrimenti non puoi andare avanti
    }

    /**
     * Visualizza la barra dell'inventario
     * precedente rispetto a quella attualmente
     * visualizzata.
     */
    public void visualizePreviousBar()
    {
        if(currentBar > 1)
        {
            selectItem(NO_ITEM);
            displayBar(--currentBar);
        }
        // altrimenti non puoi andare indietro
    }

    /**
     * Mostra testo sulla textBoard.
     *
     * @param text testo da mostrare
     */
    public void showTextOnBoard(String text)
    {
        textLabel.setText(text);
    }

    /**
     * Nascondi il testo dalla textBoard.
     */
    public void hideTextOnBoard()
    {
        textLabel.setText(null);
    }
}
