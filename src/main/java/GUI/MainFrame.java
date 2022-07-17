package GUI;

import GUI.gamestate.GameState;
import action.ActionExecutor;
import action.ActionSequence;
import entity.characters.NPC;
import entity.characters.PlayingCharacter;
import database.DBManager;
import entity.items.Door;
import entity.items.PickupableItem;
import events.executors.AnimationExecutor;
import events.executors.InventoryUpdateExecutor;
import events.executors.RoomUpdateExecutor;
import general.GameManager;
import graphics.SpriteManager;
import entity.rooms.BlockPosition;
import entity.rooms.Room;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final String CURSOR_PATH = "src/main/resources/img/HUD/cursoreneonnero.png";
    private static final int BLOCK_SIZE = 24;

    protected final Room currentRoom;
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


    public MainFrame(Room initialRoom)
    {
        currentRoom = initialRoom;

        // Calcolo delle dimensioni dello schermo
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int) screenSize.getWidth();
        screenHeight = (int) screenSize.getHeight();

        //this.ESC_LISTENER = new GameKeyListener(KeyEvent.VK_ESCAPE, () -> showMenu(true), null);

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
        rescalingFactor = (double) screenWidth / (roomWidthBlocks * BLOCK_SIZE);
        rescalingFactor = Math.floor(rescalingFactor * BLOCK_SIZE) / BLOCK_SIZE;

        // CREA L'IMMAGINE DI SFONDO CON LE CORRETTE DIMENSIONI PER ADATTARSI ALLO SCHERMO
        gameWidth = (int)(roomWidthBlocks * rescalingFactor * BLOCK_SIZE);
        gameHeight = (int)(roomHeightBlocks * rescalingFactor * BLOCK_SIZE);
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
        // dopo inventoryPanel = new InventoryPanel();



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

        PickupableItem barile1 = new PickupableItem(
                "Barile", "Un barile scemo come Basile");
        barile1.addInRoom(currentRoom, new BlockPosition(18, 12));


        Door door = new Door("Porta", "Una porta spicolosa.");
        door.addInRoom(currentRoom, new BlockPosition(14, 7));

        PlayingCharacter.getPlayer().addInRoom(currentRoom, new BlockPosition(12, 10));

        // aggiungi nicolas
        NPC nicolas = new NPC("Nicolas", "/img/personaggi/Nicolas spritesheet.png",
                                                    "/img/personaggi/nicolas.json");
        nicolas.addInRoom(currentRoom, new BlockPosition(17, 10));

        ActionSequence a = ActionSequence.loadScenario("src/main/resources/scenari/scenario1.xml");

        GameManager.startAnimatedScenario(a);

    }

    private void initGameScreenPanel()
    {
        gameScreenPanel = new GameScreenPanel(currentRoom);
        gameScreenPanel.setScalingFactor(rescalingFactor);

        // Crea nuova label per visualizzare l'immagine di sfondo
        // COMPONENTI SWING
        JLabel backgroundLabel = new JLabel(backgroundImg);

        // Imposta dimensioni pannello pari a quelle dello schermo
        gameScreenPanel.setPreferredSize(new Dimension(gameWidth, gameHeight));
        Insets gameScreenPanelInsets = gameScreenPanel.getInsets();

        backgroundLabel.setBounds(gameScreenPanelInsets.left,
                                gameScreenPanelInsets.top,
                                backgroundImg.getIconWidth(),
                                backgroundImg.getIconHeight());

        // Aggiungi background al layer 0
        gameScreenPanel.add(backgroundLabel, GameScreenPanel.BACKGROUND_LAYER);
    }

    private void initTextBarPanel()
    {
        textBarPanel = new TextBarPanel(rescalingFactor);
        int x_offset = (int)(3 * 48 * rescalingFactor); // TODO : aggiustare questi
        int y_offset = (int)(7 * rescalingFactor);

        textBarPanel.setBounds(gameScreenPanel.getInsets().left + x_offset, gameScreenPanel.getInsets().top + y_offset,
                (int) textBarPanel.getPreferredSize().getWidth(), (int) textBarPanel.getPreferredSize().getHeight());
        gameScreenPanel.add(textBarPanel, GameScreenPanel.TEXT_BAR_LEVEL);

        // addKeyListener(new GameKeyListener(KeyEvent.VK_SPACE, textBarPanel::hideTextBar, null));
    }

    private void initInventoryPanel()
    {
        inventoryPanel = new InventoryPanel(screenHeight - gameHeight);
    }

    private void initGamePanel()
    {
        gamePanel.setLayout(null);
        gamePanel.setPreferredSize(new Dimension(screenWidth, screenHeight));
        gamePanel.setBackground(Color.BLACK);

        Insets insets = gamePanel.getInsets();
        int xBorder = (screenWidth - gameWidth) / 2;


        // imposta posizione dello schermo di gioco
        gameScreenPanel.setBounds(insets.left + xBorder, insets.top, gameWidth, gameHeight);

        gamePanel.add(gameScreenPanel);

        xBorder = (screenWidth - (int) inventoryPanel.getPreferredSize().getWidth()) / 2;

        inventoryPanel.setBounds(insets.left + xBorder, insets.top + gameHeight,
                (int) inventoryPanel.getPreferredSize().getWidth(), (int) inventoryPanel.getPreferredSize().getHeight());

        gamePanel.add(inventoryPanel, BorderLayout.NORTH);
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
        Room cucina = new Room("Cucina", "/img/LabPav.png", "/img/lab1.json");

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame(cucina).setVisible(true));
    }
}
