package GUI;

import graphics.SpriteManager;
import rooms.Coordinates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class InventoryPanel extends JLayeredPane
{
    // Dimensioni //
    private final static int CAPACITY = 30;  // capacità inventario
    private final static int BAR_SIZE = 10;  // numero di oggetti nella barra
    private final static int MAX_BAR = CAPACITY / BAR_SIZE;  // numero di barre
    private final static int ORIGINAL_ITEM_SIZE = 48;  // dimensione lato sprite oggetti (quadrato)

    // Path immagini e json //
    private final static String BAR_PATH = "/img/inventario/Barra oggetti inventario.png";
    private final static String BUTTON_SPRITESHEET_PATH = "/img/inventario/bottoni.png";
    private final static String BUTTON_JSON_PATH = "/img/inventario/bottoni.json";

    // Immagini png (da caricare) //
    private final static BufferedImage BAR_IMAGE;
    private final static BufferedImage BUTTON_SPRITESHEET;

    // Livelli del LayeredPane inventario //
    private final static Integer BAR_LEVEL = 1;
    private final static Integer ITEM_LEVEL = 2;




    /*
    private final static int NO_SELECTION = -1;
        TODO: ciao
    private int selectedItem = NO_SELECTION;

     */

    /** Lista delle etichette associate a ciascun elemento della barra dell'inventario */
    private List<JLabel> itemLabelList;
    /** Lista delle icone degli oggetti presenti nell'inventario */
    private List<Icon> itemIconList;

    /** Barra correntemente visualizzata */
    private int currentBar;

    // Label per barra e bottoni
    private JLabel barLabel;
    private JLabel upButtonLabel;
    private JLabel downButtonLabel;

    /** Fattore di riscalamento per la visualizzazione */
    private double scalingFactor;


    static
    {
        // Caricamento immagini barra e bottoni
        BAR_IMAGE = SpriteManager.loadSpriteSheet(BAR_PATH);
        BUTTON_SPRITESHEET = SpriteManager.loadSpriteSheet(BUTTON_SPRITESHEET_PATH);
    }


    public InventoryPanel(int preferredHeight)
    {
        super();


        scalingFactor = (double) preferredHeight / ORIGINAL_ITEM_SIZE;

        initBar();
        initButtons();

        int totalWidth = barLabel.getIcon().getIconWidth()
                + upButtonLabel.getIcon().getIconWidth()
                + downButtonLabel.getIcon().getIconWidth();



        int inventoryWidth = (int) (totalWidth * scalingFactor);
        int inventoryHeight = (int) (ORIGINAL_ITEM_SIZE * scalingFactor);

        // imposta dimensione del pannello
        setPreferredSize(new Dimension(inventoryWidth, inventoryHeight));

        // setup icone oggetti
        itemIconList = new ArrayList<>(CAPACITY);

        initLabelList();

        // visualizza barra 1
        currentBar = 1;
        displayBar(currentBar);
    }


    private void initBar()
    {
        Insets inventoryInsets = this.getInsets();

        // setup background, immagine della barra dell'inventario
        barLabel = new JLabel(SpriteManager.rescaledImageIcon(BAR_IMAGE, scalingFactor));
        // imposta bordi background
        int backgroundWidth = barLabel.getIcon().getIconWidth();
        int backgroundHeight = barLabel.getIcon().getIconHeight();

        barLabel.setBounds(inventoryInsets.left + (int)(ORIGINAL_ITEM_SIZE * scalingFactor * 2), inventoryInsets.top,
                backgroundWidth, backgroundHeight);

        add(barLabel, BAR_LEVEL);
    }

    private void initButtons()
    {
        Insets inventoryInsets = this.getInsets();

        // aggiungi bottoni per cambiare visualizzazione barra
        upButtonLabel = new JLabel(
                SpriteManager.rescaledImageIcon(SpriteManager.loadSpriteByName(
                        BUTTON_SPRITESHEET, BUTTON_JSON_PATH, "up"), scalingFactor));

        upButtonLabel.setBounds(inventoryInsets.left, inventoryInsets.top,
                upButtonLabel.getIcon().getIconWidth(), upButtonLabel.getIcon().getIconHeight());

        downButtonLabel = new JLabel(
                SpriteManager.rescaledImageIcon(SpriteManager.loadSpriteByName(
                        BUTTON_SPRITESHEET, BUTTON_JSON_PATH, "down"), scalingFactor));

        downButtonLabel.setBounds(inventoryInsets.left + (int)(ORIGINAL_ITEM_SIZE * scalingFactor), inventoryInsets.top,
                downButtonLabel.getIcon().getIconWidth(), downButtonLabel.getIcon().getIconHeight());

        add(upButtonLabel, BAR_LEVEL);
        add(downButtonLabel, BAR_LEVEL);

        GameMouseListener upListener = new GameMouseListener(MouseEvent.BUTTON1, this::visualizePreviousBar, null);
        GameMouseListener downListener = new GameMouseListener(MouseEvent.BUTTON1, this::visualizeNextBar, null);

        upButtonLabel.addMouseListener(upListener);
        downButtonLabel.addMouseListener(downListener);
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
            Coordinates coord = calculateOffset(i);
            tempLabel.setBounds(inventoryInsets.left + coord.getX(),
                    inventoryInsets.top +  coord.getY(),
                    (int)(ORIGINAL_ITEM_SIZE * scalingFactor), (int)(ORIGINAL_ITEM_SIZE * scalingFactor));
            // aggiungi label alla lista
            itemLabelList.add(tempLabel);
            // aggiungi label al pannello
            add(tempLabel, ITEM_LEVEL);

            // aggiungi MouseListener per la selezione TODO: urgente, capire come fare qua
            // GameMouseListener mouseListener = new GameMouseListener(MouseEvent.BUTTON1, () -> selectItem(tempLabel), null)
        }
    }


    /**
     * Calcola la posizione della i-esima label nella barra dell'inventario,
     * con i = 0, ..., BAR_SIZE-1.
     *
     * @param i indice della label
     * @return coordinate di posizionamento della i-esima label
     */
    public Coordinates calculateOffset(int i)
    {
        return new Coordinates((int)((i + 2) * ORIGINAL_ITEM_SIZE * scalingFactor + 1), 1);
    }

    /**
     * Aggiunge l'icona di un oggetto nell'inventario
     *
     * @param image icona dell'item aggiunto
     */
    public void addItem(Image image)
    {
        if(itemIconList.size() < CAPACITY)
            itemIconList.add(SpriteManager.rescaledImageIcon(image, scalingFactor));

        //temp TODO: aggiustare logica del display bar
        displayBar(1);
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
        int end = Math.min(start + 10, itemIconList.size());

        // caso barra completamente vuota
        List<Icon> sublist;

        if(start >= itemIconList.size())
            sublist = new ArrayList<>(); // lista vuota
        else
            sublist = itemIconList.subList(start, end);

        // binding di ogni elemento della sottolista nella label
        for(int j = 0; j < sublist.size(); j++)
            itemLabelList.get(j).setIcon(sublist.get(j));

        // le rimanenti icone devono essere null
        for(int j = sublist.size(); j < BAR_SIZE; j++)
            itemLabelList.get(j).setIcon(null);
    }

    public void visualizeNextBar()
    {
        if(currentBar < MAX_BAR)
            displayBar(++currentBar);

        // altrimenti non puoi andare avanti
    }

    public void visualizePreviousBar()
    {
        if(currentBar > 1)
            displayBar(--currentBar);

        // altrimenti non puoi andare indietro
    }

}
