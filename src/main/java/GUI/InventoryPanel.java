package GUI;

import graphics.SpriteManager;
import rooms.Coordinates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryPanel extends JLayeredPane
{
    /** Capacità dell'inventario */
    private final static int CAPACITY = 30;

    /** Numero di oggetti visualizzati nella barra */
    private final static int BAR_SIZE = 10;

    /** Numero massimo di barre, */
    private final static int MAX_BAR = CAPACITY / BAR_SIZE;

    /** Dimensione label degli oggetti nell'inventario */
    private final static int LABEL_SIZE = 48;

    /** Path dell'immagine di background della barra dell'inventario */
    private final static String BACKGROUND_PATH = "/img/inventario/Barra oggetti inventario.png";

    /** Immagine di background della barra dell'inventario */
    private final static BufferedImage BACKGROUND_IMAGE;

    private final static String BUTTON_SPRITESHEET_PATH = "/img/inventario/bottoni.png";
    private final static String BUTTON_JSON_PATH = "/img/inventario/bottoni.json";
    private final static BufferedImage BUTTON_SPRITESHEET;

    /** Lista delle etichette associate a ciascun elemento nella barra dell'inventario */
    private List<JLabel> itemLabelList;
    /** Lista delle icone degli oggetti presenti nell'inventario */
    private List<Icon> itemIconList;

    private final static Integer BAR_LEVEL = 1;
    private final static Integer ITEM_LEVEL = 2;

    /** Barra correntemente visualizzata */
    private int currentBar;


    static
    {
        // Caricamento immagine background barra dell'inventario
        BACKGROUND_IMAGE = SpriteManager.loadSpriteSheet(BACKGROUND_PATH);
        BUTTON_SPRITESHEET = SpriteManager.loadSpriteSheet(BUTTON_SPRITESHEET_PATH);
    }


    public InventoryPanel()
    {
        super();

        Insets inventoryInsets = this.getInsets();

        // setup background, immagine della barra dell'inventario
        JLabel backgroundLabel = new JLabel(new ImageIcon(BACKGROUND_IMAGE));
        // imposta bordi background
        int backgroundWidth = backgroundLabel.getIcon().getIconWidth();
        int backgroundHeight = backgroundLabel.getIcon().getIconHeight();

        backgroundLabel.setBounds(inventoryInsets.left + LABEL_SIZE * 2, inventoryInsets.top,
                                            backgroundWidth, backgroundHeight);

        add(backgroundLabel, BAR_LEVEL);

        // aggiungi bottoni per cambiare visualizzazione barra
        JLabel upButtonLabel = new JLabel(
                        new ImageIcon(SpriteManager.loadSpriteByName(
                                BUTTON_SPRITESHEET, BUTTON_JSON_PATH, "up")));

        upButtonLabel.setBounds(inventoryInsets.left, inventoryInsets.top,
                                upButtonLabel.getIcon().getIconWidth(), upButtonLabel.getIcon().getIconHeight());

        JLabel downButtonLabel = new JLabel(
                new ImageIcon(SpriteManager.loadSpriteByName(
                        BUTTON_SPRITESHEET, BUTTON_JSON_PATH, "down")));

        downButtonLabel.setBounds(inventoryInsets.left + LABEL_SIZE, inventoryInsets.top,
                downButtonLabel.getIcon().getIconWidth(), downButtonLabel.getIcon().getIconHeight());

        add(upButtonLabel, BAR_LEVEL);
        add(downButtonLabel, BAR_LEVEL);

        GameMouseListener upListener = new GameMouseListener(MouseEvent.BUTTON1, this::visualizePreviousBar, null);
        GameMouseListener downListener = new GameMouseListener(MouseEvent.BUTTON1, this::visualizeNextBar, null);

        upButtonLabel.addMouseListener(upListener);
        downButtonLabel.addMouseListener(downListener);

        // imposta dimensione del pannello
        setPreferredSize(new Dimension(backgroundWidth + 2 * LABEL_SIZE, backgroundHeight));

        // setup icone oggetti
        itemIconList = new ArrayList<>(CAPACITY);

        // crea 10 label e posizionale
        itemLabelList = new ArrayList<>(BAR_SIZE);

        for(int i = 0; i < BAR_SIZE; i++)
        {
            // crea label
            JLabel tempLabel = new JLabel();

            // posiziona label
            Coordinates coord = calculateOffset(i);
            tempLabel.setBounds(inventoryInsets.left + coord.getX(),
                                inventoryInsets.top +  coord.getY(),
                                    LABEL_SIZE, LABEL_SIZE);
            // aggiungi label alla lista
            itemLabelList.add(tempLabel);
            // aggiungi label al pannello
            add(tempLabel, ITEM_LEVEL);
        }

        // visualizza barra 1
        currentBar = 1;
        displayBar(currentBar);
    }

    public void initListeners()
    {
        GameKeyListener upArrowListener = new GameKeyListener(KeyEvent.VK_UP, this::visualizePreviousBar, null);
        GameKeyListener downArrowListener = new GameKeyListener(KeyEvent.VK_DOWN, this::visualizeNextBar, null);

        Container parent = getParent();
        while(!(parent instanceof MainFrame))
        {
            System.out.println(parent.getClass());
            parent = parent.getParent();
        }
        parent.addKeyListener(upArrowListener);
        parent.addKeyListener(downArrowListener);
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
        return new Coordinates((i + 2) * LABEL_SIZE + 1, 1);
    }

    /**
     * Aggiunge l'icona di un oggetto nell'inventario
     *
     * @param icon icona dell'item aggiunto
     */
    public void addItem(Icon icon)
    {
        if(itemIconList.size() < CAPACITY)
            itemIconList.add(icon);

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
