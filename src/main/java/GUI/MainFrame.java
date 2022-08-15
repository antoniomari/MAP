package GUI;

import GUI.gamestate.GameState;
import general.ActionSequence;
import database.DBManager;
import events.executors.AnimationExecutor;
import events.executors.InventoryUpdateExecutor;
import events.executors.RoomUpdateExecutor;
import general.GameManager;
import graphics.SpriteManager;
import entity.rooms.Room;
import general.XmlLoader;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    /* Dimensioni in blocchi di stanza di default, utilizzate
        per effettuare calcoli per ricavare le dimensioni dell'inventoryPanel
        e del textBarPanel, in modo tale che siano ottimizzati per stanze di tali
        dimensioni, essendo la maggioranza
    */
    private static final int DEFAULT_WIDTH_BLOCKS = 32;
    private static final int DEFAULT_HEIGHT_BLOCKS = 16;

    /** Path cursore personalizzato. */
    private static final String CURSOR_PATH = "src/main/resources/img/HUD/cursoreneonnero.png";


    protected Room currentRoom;
    private Icon backgroundImg;


    private final int screenWidth;
    private final int screenHeight;
    private int gameWidth;
    private int gameHeight;
    private double rescalingFactor;

    private GameScreenPanel gameScreenPanel;
    private JPanel mainPanel;
    private JLayeredPane menuPanel;
    private JPanel gamePanel;
    private InventoryPanel inventoryPanel;
    private TextBarPanel textBarPanel;

    // LISTENER FOR KEYS
   //private final GameKeyListener ESC_LISTENER;



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

    public GameScreenPanel getGameScreenPanel()
    {
        return gameScreenPanel;
    }


    public Room getCurrentRoom()
    {
        return currentRoom;
    }

    public void setCurrentRoom(Room newRoom)
    {
        this.currentRoom = newRoom;
        rescalingFactor = calculateScalingFactor(currentRoom);

        // solo per stanze più grandi TODO: abilitare
        gameScreenPanel.setScalingFactor(rescalingFactor);

        gameScreenPanel.changeRoom(newRoom, screenWidth);

        gameWidth = (int)(newRoom.getBWidth() * rescalingFactor * GameManager.BLOCK_SIZE);
        gameHeight = (int)(newRoom.getBHeight() * rescalingFactor * GameManager.BLOCK_SIZE);
    }


    private double calculateScalingFactor(int roomBWidth, int roomBHeight)
    {
        double widthRescalingFactor;
        double heightRescalingFactor;

        widthRescalingFactor = (double) screenWidth / (roomBWidth * GameManager.BLOCK_SIZE);
        widthRescalingFactor = Math.floor(widthRescalingFactor * GameManager.BLOCK_SIZE) / GameManager.BLOCK_SIZE;

        heightRescalingFactor = (double) screenHeight / (roomBHeight * GameManager.BLOCK_SIZE);
        heightRescalingFactor = Math.floor(heightRescalingFactor * GameManager.BLOCK_SIZE) / GameManager.BLOCK_SIZE;

        return Math.min(widthRescalingFactor, heightRescalingFactor);
    }

    private double calculateScalingFactor(Room room)
    {
        int roomWidthBlocks = room.getBWidth();
        int roomHeightBlocks = room.getBHeight();

        return calculateScalingFactor(roomWidthBlocks, roomHeightBlocks);

        //return widthRescalingFactor;
    }


    public MainFrame(Room initialRoom)
    {
        currentRoom = initialRoom;

        // Calcolo delle dimensioni dello schermo
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int) screenSize.getWidth();
        screenHeight = (int) screenSize.getHeight();

        // inizializzazione immagine di sfondo
        setupBackground();
        // inizializzazione componenti
        initComponents();
        // inizializzazione cursore
        initCursor();
        // attiva schermo intero
        fullScreenOn();

        GameScreenManager.setActivePanel(gameScreenPanel);


        // imposta gli esecutori su di te TODO: migliorare codice
        AnimationExecutor.setMainFrame(this);
        InventoryUpdateExecutor.setMainFrame(this);
        RoomUpdateExecutor.setMainFrame(this);

        GameState.setMainFrame(this);

        setupPlayground();
    }


    private void initCursor()
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage(CURSOR_PATH);
        Cursor c = toolkit.createCustomCursor(image , new Point(getX(), getY()), "img");
        setCursor(c);
    }

    private void setupBackground()
    {
        Image roomImage = currentRoom.getBackgroundImage();
        int roomWidthBlocks = currentRoom.getBWidth();
        int roomHeightBlocks = currentRoom.getBHeight();

        // per calcolare lo scaling factor serve ottenere il rapporto tra
        // la larghezza dello schermo e quella dell'immagine della stanza;
        // in seguito si aggiusta in modo tale che la grandezza di ogni blocco
        // sia pari al più grande intero minore della grandezza dei blocchi
        // fullscreen
        rescalingFactor = calculateScalingFactor(currentRoom);
        // CREA L'IMMAGINE DI SFONDO CON LE CORRETTE DIMENSIONI PER ADATTARSI ALLO SCHERMO
        gameWidth = (int)(roomWidthBlocks * rescalingFactor * GameManager.BLOCK_SIZE);
        gameHeight = (int)(roomHeightBlocks * rescalingFactor * GameManager.BLOCK_SIZE);

        System.out.println("GameWidth, GameHeight" + gameWidth + " " + gameHeight);
        System.out.println("ScreenWidth, ScreenHeight" + screenWidth + " " + screenHeight);

        backgroundImg = SpriteManager.rescaledImageIcon(roomImage, rescalingFactor);

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
        gamePanel = new JPanel();



        // Chiudi l'app alla chiusura della finestra
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Schwartz");

        // Imposta dimensioni finestra pari a quelle dello schermo
        setPreferredSize(new Dimension(screenWidth, screenHeight));


        initGameScreenPanel();
        initTextBarPanel();
        initInventoryPanel();
        initGamePanel();
        initMenuPanel();



        // -----------------------------------------------------
        //                  SETUP mainPanel
        // -----------------------------------------------------

        //Imposta layout (Card layout per poter visualizzare più schermate e scegliere quale
        // visualizzare)
        CardLayout cl = new CardLayout();
        mainPanel.setLayout(cl);

        // aggiungi CARDS al mainPanel, con le rispettive etichette
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GIOCO");

        // mostra la schermata di gioco
        cl.show(mainPanel, "GIOCO");

        mainPanel.setPreferredSize(new Dimension(screenWidth, screenHeight));

        // -----------------------------------------------------
        //                  SETUP MainFrame
        // -----------------------------------------------------
        add(mainPanel, BorderLayout.CENTER);
        //addKeyListener(ESC_LISTENER);
        pack();

    }


    public void setupPlayground()
    {
        DBManager.setupInventory();

        // TODO: attenzione alla current room
        // ActionSequence a = XmlLoader.loadRoomInit("src/main/resources/scenari/demoRoom.xml");
        ActionSequence a = XmlLoader.loadRoomInit("src/main/resources/scenari/piano terra/PT-B.xml");
        GameManager.startScenario(a);

    }

    private void initGameScreenPanel()
    {
        gameScreenPanel = new GameScreenPanel(currentRoom);

        gameScreenPanel.setScalingFactor(rescalingFactor);

        // Crea nuova label per visualizzare l'immagine di sfondo
        // COMPONENTI SWING

        JLabel backgroundLabel = new JLabel(backgroundImg);

        // Imposta dimensioni pannello pari a quelle dello schermo
        gameScreenPanel.setPreferredSize(new Dimension(screenWidth, gameHeight)); // screenWidth / 2));

        // Aggiungi background al layer 0
        gameScreenPanel.add(backgroundLabel, GameScreenPanel.BACKGROUND_LAYER);
        gameScreenPanel.setBackgroundLabel(backgroundLabel);

        /*
        backgroundLabel.setBounds(gameScreenPanel.getRoomBorders().getX(),
                gameScreenPanel.getRoomBorders().getY(),
                backgroundImg.getIconWidth(),
                backgroundImg.getIconHeight());

         */
    }

    private void initTextBarPanel()
    {
        textBarPanel = new TextBarPanel(defaultScalingFactor());
        int x_offset = (int)(3 * 48 * defaultScalingFactor()); // TODO : aggiustare questi
        int y_offset = (int)(7 * defaultScalingFactor());

        textBarPanel.setBounds(gameScreenPanel.getInsets().left + x_offset, gameScreenPanel.getInsets().top + y_offset,
                (int) textBarPanel.getPreferredSize().getWidth(), (int) textBarPanel.getPreferredSize().getHeight());
        gameScreenPanel.add(textBarPanel, GameScreenPanel.TEXT_BAR_LEVEL);

        // addKeyListener(new GameKeyListener(KeyEvent.VK_SPACE, textBarPanel::hideTextBar, null));
    }

    private double defaultScalingFactor()
    {
        return calculateScalingFactor(DEFAULT_WIDTH_BLOCKS, DEFAULT_HEIGHT_BLOCKS);

    }
    private int defaultGameHeight()
    {
        return (int) (DEFAULT_HEIGHT_BLOCKS * GameManager.BLOCK_SIZE * defaultScalingFactor());
    }

    private void initInventoryPanel()
    {

        inventoryPanel = new InventoryPanel(screenHeight - defaultGameHeight());
    }

    private void initGamePanel()
    {
        gamePanel.setLayout(null);
        gamePanel.setPreferredSize(new Dimension(screenWidth, screenHeight));
        gamePanel.setBackground(Color.BLACK);

        Insets insets = gamePanel.getInsets();

        int xBorder = (screenWidth - (int) inventoryPanel.getPreferredSize().getWidth()) / 2;

        inventoryPanel.setBounds(insets.left + xBorder, insets.top + defaultGameHeight(),
                (int) inventoryPanel.getPreferredSize().getWidth(), (int) inventoryPanel.getPreferredSize().getHeight());

        gamePanel.add(inventoryPanel);

        xBorder = (screenWidth - gameWidth) / 2;


        // imposta posizione dello schermo di gioco
        gameScreenPanel.setBounds(insets.left, insets.top, gameScreenPanel.getPreferredSize().width,
                gameScreenPanel.getPreferredSize().height);

        gameScreenPanel.getBackgroundLabel().setBounds(gameScreenPanel.getRoomBorders().getX() + xBorder,
                gameScreenPanel.getRoomBorders().getY(),
                backgroundImg.getIconWidth(),
                backgroundImg.getIconHeight());

        System.out.println("Sfondo img" + gameScreenPanel.getBackgroundLabel().getIcon().getIconWidth());


        gamePanel.add(gameScreenPanel);


    }

    public void initMenuPanel()
    {
        menuPanel = new JLayeredPane();

        // Creazione bottoni per menuPanel
        JButton okButton = new JButton("Ok");
        JButton exitButton = new JButton("Esci");
        exitButton.addActionListener((e) -> System.exit(0));

        menuPanel.setPreferredSize(new Dimension(screenWidth, screenHeight));
        menuPanel.setOpaque(true);
        menuPanel.setBackground(Color.BLACK);


        JLabel backLabel = new JLabel(SpriteManager.rescaledImageIcon(SpriteManager.loadSpriteSheet("/img/lab1 blur.png"), rescalingFactor));

        int xBorder = (screenWidth - backLabel.getIcon().getIconWidth()) / 2;

        backLabel.setBounds(menuPanel.getInsets().left + xBorder, menuPanel.getInsets().top, backLabel.getIcon().getIconWidth(), backLabel.getIcon().getIconHeight());

        okButton.setBounds(menuPanel.getInsets().left + 100, menuPanel.getInsets().top + 100, 50, 50);
        exitButton.setBounds(menuPanel.getInsets().left + 200, menuPanel.getInsets().top + 200, 50, 50);


        JPanel buttonPanel = new JPanel(new BorderLayout());
        JLabel impostLabel = new JLabel(SpriteManager.rescaledImageIcon(SpriteManager.loadSpriteSheet("/img/impostazioni.png"), rescalingFactor / 3));


        buttonPanel.add(impostLabel, BorderLayout.CENTER);

        buttonPanel.setPreferredSize(new Dimension(screenWidth, screenHeight));  // TODO : capire dimensionamenti
        buttonPanel.setBounds(menuPanel.getInsets().left, menuPanel.getInsets().top, screenWidth / 2, screenHeight / 2);
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(new Color(0, 0, 0, 0));

        menuPanel.add(buttonPanel, Integer.valueOf(2));

        menuPanel.add(backLabel, Integer.valueOf(0));

        // Aggiungi bottoni al menuPanel
        menuPanel.add(okButton, Integer.valueOf(3));
        menuPanel.add(exitButton, Integer.valueOf(3));

        menuPanel.setPreferredSize(new Dimension(screenWidth, screenHeight));
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
    public static void main(String[] args)
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


        // Room cucina = DBManager.loadRoom("Cucina"); todo: riabilitare
        Room demoRoom = XmlLoader.loadRoom("src/main/resources/scenari/piano terra/PT-B.xml");
        // Room demoRoom = XmlLoader.loadRoom("src/main/resources/scenari/demoRoom.xml");

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame(demoRoom).setVisible(true));
    }
}
