package GUI;


import characters.GameCharacter;
import characters.PlayingCharacter;
import database.DBManager;
import events.executors.AnimationExecutor;
import events.executors.InventoryUpdateExecutor;
import events.executors.RoomUpdateExecutor;
import graphics.SpriteManager;
import items.Door;
import items.Item;
import items.PickupableItem;
import rooms.Coordinates;
import rooms.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainFrame extends JFrame {

    private final Room currentRoom;
    private Icon backgroundImg;

    private final int screenWidth;
    private final int screenHeight;
    private int gameWidth;
    private int gameHeight;
    private double rescalingFactor;

    // COMPONENTI SWING
    private JLabel backgroundLabel;
    private JLayeredPane gameScreenPanel;
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel gamePanel;
    private InventoryPanel inventoryPanel;
    private TextBarPanel textBarPanel;

    // LISTENER FOR KEYS
    private final GameKeyListener ESC_LISTENER;


    /**
     * Dizionario che contiene gli oggetti presenti nella stanza (currentRoom)
     * e le JLabel associate al gameScreenPanel
     */
    private final Map<Item, JLabel> itemLabelMap;
    private final Map<GameCharacter, JLabel> characterLabelMap;

    // LAYER GAMEPANEL (sono richiesti Integer e non int)
    private final static Integer GARBAGE_LAYER = 0; // Utilizzato per la rimozione degli oggetti, causa di bug
    private final static Integer BACKGROUND_LAYER = 1;
    private final static Integer ITEM_LAYER = 2;
    private final static Integer TEXT_BAR_LEVEL = 3;


    public JLabel getLabelAssociated(Item item)
    {
        return itemLabelMap.get(item);
    }


    public void removeItemCurrentRoom(Item item)
    {
        JLabel labelToRemove = itemLabelMap.get(item);

        //Sposta etichetta nel layer per la rimozione
        gameScreenPanel.setLayer(labelToRemove, GARBAGE_LAYER);
        labelToRemove.setIcon(null);

        // rimuovere la JLabel dal gameScreenPanel
        gameScreenPanel.remove(labelToRemove);

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


    public double getScalingFactor()
    {
        return rescalingFactor;
    }

    public InventoryPanel getInventoryPanel()
    {
        return inventoryPanel;
    }

    public TextBarPanel getTextBarPanel()
    {
        return textBarPanel;
    }

    public MainFrame(Room initialRoom)
    {
        currentRoom = initialRoom;

        // Calcolo delle dimensioni dello schermo
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int) screenSize.getWidth();
        screenHeight = (int) screenSize.getHeight();

        this.ESC_LISTENER = new GameKeyListener(KeyEvent.VK_ESCAPE, () -> showMenu(true), null);
        itemLabelMap = new HashMap<>();
        characterLabelMap = new HashMap<>();

        // inizializzazione immagine di sfondo
        setupBackground();
        // inizializzazione componenti
        initComponents();
        // inizializzazione cursore
        initCursor();
        // attiva schermo intero
        fullScreenOn();

        // imposta gli esecutori su di te TODO: migliorare codice
        AnimationExecutor.setMainFrame(this);
        InventoryUpdateExecutor.setMainFrame(this);
        RoomUpdateExecutor.setMainFrame(this);
    }


    private void initCursor()
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage("src/main/resources/img/HUD/cursoreneonnero.png");
        Cursor c = toolkit.createCustomCursor(image , new Point(getX(), getY()), "img");
        setCursor(c);
    }

    private void setupBackground()
    {
        Image roomImage = currentRoom.getBackgroundImage();
        int roomWidth = roomImage.getWidth(null);
        int roomHeight = roomImage.getHeight(null);
        rescalingFactor = (double) screenWidth / roomWidth;

        // CREA L'IMMAGINE DI SFONDO CON LE CORRETTE DIMENSIONI PER ADATTARSI ALLO SCHERMO
        gameWidth = screenWidth;
        gameHeight = (int)(roomHeight * rescalingFactor);
        backgroundImg = new ImageIcon(roomImage.getScaledInstance(gameWidth, gameHeight, Image.SCALE_SMOOTH));
    }

    private void fullScreenOn()
    {
        GraphicsDevice device = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getScreenDevices()[0];
        device.setFullScreenWindow(this);
    }


    // inizializzazione componenti JFrame
    private void initComponents()
    {
        //Creazione componenti
        mainPanel = new JPanel();
        menuPanel = new JPanel();
        gamePanel = new JPanel();
        // dopo inventoryPanel = new InventoryPanel();



        // Chiudi l'app alla chiusura della finestra
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Schwartz");

        // Imposta dimensioni finestra pari a quelle dello schermo
        setPreferredSize(new java.awt.Dimension(screenWidth, screenHeight));

        // -----------------------------------------------------
        //                  SETUP gameScreenPanel
        // -----------------------------------------------------

       initGameScreenPanel();


        // -----------------------------------------------------
        //                  SETUP textBarPanel
        // -----------------------------------------------------
        textBarPanel = new TextBarPanel(rescalingFactor);
        int x_offset = (int)(3 * 48 * rescalingFactor); // TODO : aggiustare questi
        int y_offset = (int)(7 * rescalingFactor);

        textBarPanel.setBounds(gameScreenPanel.getInsets().left + x_offset, gameScreenPanel.getInsets().top + y_offset,
                (int) textBarPanel.getPreferredSize().getWidth(), (int) textBarPanel.getPreferredSize().getHeight());
        gameScreenPanel.add(textBarPanel, TEXT_BAR_LEVEL);

        addKeyListener(new GameKeyListener(KeyEvent.VK_SPACE, textBarPanel::hideTextBar, null));

        // -----------------------------------------------------
        //                  SETUP inventoryPanel
        // -----------------------------------------------------
        initInventoryPanel();

        // -----------------------------------------------------
        //                  SETUP gamePanel
        // -----------------------------------------------------
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setPreferredSize(new java.awt.Dimension(screenWidth, screenHeight));
        gamePanel.add(gameScreenPanel, BorderLayout.CENTER);
        gamePanel.add(inventoryPanel, BorderLayout.SOUTH);

        // -----------------------------------------------------
        //                  SETUP menuPanel
        // -----------------------------------------------------

        // Creazione bottoni per menuPanel
        JButton okButton = new JButton("Ok");
        JButton exitButton = new JButton("Esci");
        exitButton.addActionListener((e) -> System.exit(0));

        // Imposta layout
        menuPanel.setLayout(new FlowLayout());

        // Aggiungi bottoni al menuPanel
        menuPanel.add(okButton);
        menuPanel.add(exitButton);

        menuPanel.setPreferredSize(new java.awt.Dimension(screenWidth, screenHeight));

        // -----------------------------------------------------
        //                  SETUP mainPanel
        // -----------------------------------------------------

        //Imposta layout (Card layout per poter visualizzare più schermate e scegliere quale
        // visualizzare)
        CardLayout cl = new java.awt.CardLayout();
        mainPanel.setLayout(cl);

        // aggiungi CARDS al mainPanel, con le rispettive etichette
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GIOCO");

        // mostra la schermata di gioco
        cl.show(mainPanel, "GIOCO");

        mainPanel.setPreferredSize(new java.awt.Dimension(screenWidth, screenHeight));


        // -----------------------------------------------------
        //                  SETUP MainFrame
        // -----------------------------------------------------
        add(mainPanel, BorderLayout.CENTER);
        addKeyListener(ESC_LISTENER);
        pack();

    }


    public void setupPlayground()
    {
        PickupableItem barile1 = new PickupableItem("Barile", "Un barile scemo come Basile", currentRoom);
        barile1.setLocationRoom(currentRoom);
        currentRoom.addItem(barile1, GameScreenManager.calculateCoordinates(9, 5, rescalingFactor));
        Door door = new Door("Porta", "Una porta spicolosa.");
        addGameCharacter(PlayingCharacter.getPlayer(), 6, 5);

        addGameItem(barile1, 9, 5);
        addGameItem(door, 7, 2);

    }

    public void initGameScreenPanel()
    {
        gameScreenPanel = new JLayeredPane();
        // Crea nuova label per visualizzare l'immagine di sfondo
        backgroundLabel = new javax.swing.JLabel(backgroundImg);

        setupPlayground();

        // Imposta dimensioni pannello pari a quelle dello schermo
        gameScreenPanel.setPreferredSize(new java.awt.Dimension(gameWidth, gameHeight));

        Insets gameScreenPanelInsets = gameScreenPanel.getInsets();

        backgroundLabel.setBounds(gameScreenPanelInsets.left, gameScreenPanelInsets.top, backgroundImg.getIconWidth(), backgroundImg.getIconHeight());

        // Aggiungi background al layer 0
        gameScreenPanel.add(backgroundLabel, BACKGROUND_LAYER);
    }

    public void initInventoryPanel()
    {
        inventoryPanel = new InventoryPanel(screenHeight - gameHeight);

        GameMouseListener dropItemListener = new GameMouseListener(MouseEvent.BUTTON1,
                () ->
                {
                    if (inventoryPanel.getSelectedItem() != null)
                    {
                        inventoryPanel.getSelectedItem().drop(currentRoom, new Coordinates(getMousePosition().x, getMousePosition().y));
                    }
                    else
                    {
                        PlayingCharacter.getPlayer().setPosition(new Coordinates(getMousePosition().x, getMousePosition().y));
                    }
                }

                , null);
        gameScreenPanel.addMouseListener(dropItemListener);
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
    private void addGameItem(Item it, final int xBlocks, final int yBlocks)
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
        gameScreenPanel.add(itemLabel, ITEM_LAYER);

        updateItemPosition(it, xBlocks, yBlocks);
    }

    private void addGameCharacter(GameCharacter ch, final int xBlocks, final int yBlocks)
    {
        // recupera lo sprite della giusta dimensione
        Icon rescaledSprite = SpriteManager.rescaledImageIcon(ch.getSprite(), rescalingFactor);

        // crea la label corrispondente all'Item
        JLabel characterLabel = new JLabel(rescaledSprite);

        // metti la coppia Item JLabel nel dizionario
        characterLabelMap.put(ch, characterLabel);

        // aggiungi la label nell'ITEM_LAYER  TODO pensare al layer
        gameScreenPanel.add(characterLabel, ITEM_LAYER);

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
    private void updateItemPosition(Item it, int xBlocks, int yBlocks)
    {
        Objects.requireNonNull(it);

        // controlla che it è presente effettivamente nella stanza
        if(!itemLabelMap.containsKey(it))
        {
            // TODO: ricontrollare eccezione lanciata
            throw new IllegalArgumentException("Item non presente nella stanza");
        }
        Icon rescaledSprite = it.getScaledIconSprite(rescalingFactor);
        Insets insets = gameScreenPanel.getInsets();
        Coordinates coord = GameScreenManager.calculateCoordinates(xBlocks, yBlocks, rescalingFactor);

        JLabel itemLabel = itemLabelMap.get(it);
        itemLabel.setBounds(insets.left + coord.getX(), insets.top + coord.getY(),
                rescaledSprite.getIconWidth(), rescaledSprite.getIconHeight());
    }

    /**
     * Aggiorna la posizione di un personaggio nella stanza.
     *
     * @param ch personaggio da riposizionare
     * @param xBlocks blocco x
     * @param yBlocks blocco y
     * @throws IllegalArgumentException se ch non è presente nella stanza
     */
    private void updateCharacterPosition(GameCharacter ch, int xBlocks, int yBlocks)
    {
        Objects.requireNonNull(ch);

        // controlla che it è presente effettivamente nella stanza
        if(!characterLabelMap.containsKey(ch))
        {
            // TODO: ricontrollare eccezione lanciata
            throw new IllegalArgumentException("Pergonaggio non presente nella stanza");
        }
        Icon rescaledSprite = characterLabelMap.get(ch).getIcon();
        Insets insets = gameScreenPanel.getInsets();
        Coordinates coord = GameScreenManager.calculateCoordinates(xBlocks, yBlocks, rescalingFactor);

        JLabel characterLabel = characterLabelMap.get(ch);
        characterLabel.setBounds(insets.left + coord.getX(), insets.top + coord.getY(),
                rescaledSprite.getIconWidth(), rescaledSprite.getIconHeight());
    }


    public void showMenu(boolean b)
    {
        CardLayout cl = (CardLayout) mainPanel.getLayout();

        if(b)
            cl.show(mainPanel, "MENU");
        else
            cl.show(mainPanel, "GIOCO");

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        DBManager.setupInventory();

        Room cucina = DBManager.loadRoom("Cucina");

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame(cucina).setVisible(true));
    }
}
