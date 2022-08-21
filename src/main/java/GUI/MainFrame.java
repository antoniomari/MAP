package GUI;

import GUI.gamestate.GameState;
import entity.characters.PlayingCharacter;
import events.executors.Executor;
import general.ActionSequence;
import database.DBManager;
import general.GameManager;
import graphics.SpriteManager;
import entity.rooms.Room;
import general.XmlLoader;
import sound.SoundHandler;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    /*
        Dimensioni in blocchi di stanza di default, utilizzate
        per effettuare calcoli per ricavare le dimensioni dell'inventoryPanel
        e del textBarPanel, in modo tale che siano ottimizzati per stanze di tali
        dimensioni, essendo la maggioranza.
    */
    private static final int DEFAULT_WIDTH_BLOCKS = 32;
    private static final int DEFAULT_HEIGHT_BLOCKS = 16;
    /*
        Scaling factor e altezza in pixel corrispondenti
        alla stanza di default, variano da schermo a schermo
        e vengono calcolati dopo aver recuperato SCREEN_WIDTH
        e SCREEN_HEIGHT
     */
    private static final double DEFAULT_SCALING_FACTOR;
    private static final int DEFAULT_GAME_HEIGHT;

    private static final String STARTING_MUSIC_PATH = "src/main/resources/audio/musica/Menù iniziale.wav";

    /** Path cursore personalizzato. */
    private static final String CURSOR_PATH = "src/main/resources/img/HUD/cursoreneonnero.png";

    /** Stanza in cui il giocatore si trova attualmente. */
    private Room currentRoom;
    /** Immagine di background (riscalata) corrispondente alla currentRoom*/
    private Icon backgroundImg;

    /** Dimensione in pixel della larghezza dello schermo. */
    private static final int SCREEN_WIDTH;
    /** Dimensione in pixel dell'altezza dello schermo. */
    private static final int SCREEN_HEIGHT;

    /** Dimensione in pixel della larghezza della schermata di gioco. */
    private int gameWidth;
    /** Dimensione in pixel dell'altezza della schermata di gioco. */
    private int gameHeight;

    /** Fattore di riscalamento per ogni icona nella schermata di gioco
     * e per la barra di testo.
     */
    private double rescalingFactor;

    /*
        COMPONENTI SWING DEL FRAME
     */
    /** Pannello contenente la schermata di gioco. */
    private GameScreenPanel gameScreenPanel;
    /** Panello contenente la barra di visualizzazione di testo (contenuta nel gameScreen). */
    private TextBarPanel textBarPanel;
    /** Pannello contenente la barra dell'inventario. */
    private InventoryPanel inventoryPanel;


    /** Pannello contenente schermata di gioco {@link MainFrame#gameScreenPanel},
     * barra dell'inventario {@link MainFrame#inventoryPanel}
     * e barra di visualizzazione di testo {@link MainFrame#textBarPanel}. */
    private JPanel gamePanel;
    /** Pannello contenente il menu di pausa. */
    private JLayeredPane menuPanel;
    /** Pannello contenente il menu d'inizio. */
    private JLayeredPane startingMenuPanel;

    /** Pannello padre di tutti gli altri.
     * Usa CardLayout per alternare la visualizzazione di
     * {@link MainFrame#gamePanel} e {@link MainFrame#menuPanel}
     * */
    private JPanel mainPanel;

    /** Schermata mostrata correntemente. Valori possibili: MENU, GAME. */
    private String currentDisplaying;

    static
    {
        // Calcolo delle dimensioni dello schermo
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH = (int) screenSize.getWidth();
        SCREEN_HEIGHT = (int) screenSize.getHeight();

        // calcolo del fattore di riscalamento di default e dell'altezza
        DEFAULT_SCALING_FACTOR = calculateScalingFactor(DEFAULT_WIDTH_BLOCKS, DEFAULT_HEIGHT_BLOCKS);
        DEFAULT_GAME_HEIGHT =  (int) (DEFAULT_HEIGHT_BLOCKS * GameManager.BLOCK_SIZE * DEFAULT_SCALING_FACTOR);
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

    /**
     * Calcola il fattore di riscalamento per una stanza di dimensioni
     * {@code roomBWidth} blocchi di larghezza e {@code roomBHeight} blocchi di altezza.
     *
     * Utilizza le costanti {@link MainFrame#SCREEN_WIDTH} e {@link MainFrame#SCREEN_HEIGHT}
     * per poter riscalare le stanze in modo tale che il lato di un blocco sia il più grande
     * multiplo intero di pixel che permetta all'immagine della stanza di entrare nello schermo
     * mantenendo le proporzioni.
     *
     * @param roomBWidth larghezza della stanza, misurata in numero di blocchi
     * @param roomBHeight altezza della stanza, misurata in numero di blocchi
     * @return scalingFactor per una stanza di tali dimensioni
     */
    private static double calculateScalingFactor(int roomBWidth, int roomBHeight)
    {
        double widthRescalingFactor = (double) SCREEN_WIDTH / (roomBWidth * GameManager.BLOCK_SIZE);
        widthRescalingFactor = Math.floor(widthRescalingFactor * GameManager.BLOCK_SIZE) / GameManager.BLOCK_SIZE;

        // se la DEFAULT_GAME_HEIGHT non è stata ancora inizializzata allora usa la SCREEN_HEIGHT
        // questo perchè il metodo viene innanzitutto chiamato nell'inizializzazione della DEFAULT_GAME_HEIGHT,
        // per cui serve utilizzare la SCREEN_HEIGH; in seguito per tutte le altre chiamate si utilizzerà
        // regolarmente la DEFAULT_GAME_HEIGHT.
        int height = DEFAULT_GAME_HEIGHT == 0 ? SCREEN_HEIGHT : DEFAULT_GAME_HEIGHT;

        double heightRescalingFactor = (double) height / (roomBHeight * GameManager.BLOCK_SIZE);
        heightRescalingFactor = Math.floor(heightRescalingFactor * GameManager.BLOCK_SIZE) / GameManager.BLOCK_SIZE;

        return Math.min(widthRescalingFactor, heightRescalingFactor);
    }

    /**
     * Imposta la currentRoom del gioco in esecuzione.
     *
     * Vengono aggiornati {@link MainFrame#rescalingFactor},
     * {@link MainFrame#gameWidth} e {@link MainFrame#gameHeight}
     * sulla base delle dimensioni della stanza {@code newRoom}.
     *
     * Nota: viene richiamato il metodo {@link GameScreenPanel#changeRoom(Room, int)}
     * affinché vengano correttamente impostati tutti i GamePiece nella stanza.
     *
     * Viene inoltre riprodotta la musica della stanza newRoom.
     *
     * @param newRoom stanza da impostare come {@link MainFrame#currentRoom}
     */
    public void setCurrentRoom(Room newRoom)
    {
        this.currentRoom = newRoom;
        rescalingFactor = calculateScalingFactor(currentRoom.getBWidth(), currentRoom.getBHeight());

        // solo per stanze più grandi TODO: abilitare
        gameScreenPanel.setScalingFactor(rescalingFactor);

        gameScreenPanel.changeRoom(newRoom, SCREEN_WIDTH);

        gameWidth = (int)(newRoom.getBWidth() * rescalingFactor * GameManager.BLOCK_SIZE);
        gameHeight = (int)(newRoom.getBHeight() * rescalingFactor * GameManager.BLOCK_SIZE);

        // imposta musica
        SoundHandler.playWav(currentRoom.getMusicPath(), SoundHandler.Mode.MUSIC);
    }

    /**
     * Inizializza il frame, ponendolo a schermo intero, e lo registra
     * presso {@link Executor} e {@link GameState}.
     *
     * Registra inoltre il {@link MainFrame#gameScreenPanel} presso
     * {@link GameScreenManager}.
     *
     * @param initialRoom stanza iniziale da impostare come {@link MainFrame#currentRoom}
     */
    public MainFrame(Room initialRoom)
    {
        // Chiudi l'app alla chiusura della finestra
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Schwartz");
        // Imposta dimensioni finestra pari a quelle dello schermo
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        // imposta stanza iniziale
        currentRoom = initialRoom;

        // inizializzazione immagine di sfondo
        setupBackground();
        // inizializzazione componenti
        initComponents();
        // inizializzazione cursore
        initCursor();
        // attiva schermo intero
        fullScreenOn();

        // registrati presso gli Executor (eventi) e presso GameState
        Executor.setMainFrame(this);
        GameState.setMainFrame(this);
        // registra il gameScreenPanel presso GameScreenManager
        GameScreenManager.setActivePanel(gameScreenPanel);
        GameManager.setMainFrame(this);
    }

    // Inizializzazione cursore personalizzato
    private void initCursor()
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage(CURSOR_PATH);
        Cursor c = toolkit.createCustomCursor(image , new Point(getX(), getY()), "img");
        setCursor(c);
    }

    // Inizializzazione immagine di sfondo e gameWidth, gameHeight
    private void setupBackground()
    {
        Image roomImage = currentRoom.getBackgroundImage();
        int roomWidthBlocks = currentRoom.getBWidth();
        int roomHeightBlocks = currentRoom.getBHeight();

        // TODO: rivedere commenti interni
        // per calcolare lo scaling factor serve ottenere il rapporto tra
        // la larghezza dello schermo e quella dell'immagine della stanza;
        // in seguito si aggiusta in modo tale che la grandezza di ogni blocco
        // sia pari al più grande intero minore della grandezza dei blocchi
        // fullscreen
        rescalingFactor = calculateScalingFactor(currentRoom.getBWidth(), currentRoom.getBHeight());
        // CREA L'IMMAGINE DI SFONDO CON LE CORRETTE DIMENSIONI PER ADATTARSI ALLO SCHERMO
        gameWidth = (int)(roomWidthBlocks * rescalingFactor * GameManager.BLOCK_SIZE);
        gameHeight = (int)(roomHeightBlocks * rescalingFactor * GameManager.BLOCK_SIZE);

        backgroundImg = SpriteManager.rescaledImageIcon(roomImage, rescalingFactor);

    }

    // imposta schermo intero
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

        initGameScreenPanel();
        initTextBarPanel();
        initInventoryPanel();
        initGamePanel();
        initMenuPanel();
        initStartingMenuPanel();

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
        mainPanel.add(startingMenuPanel, "INIZIO");

        // mostra la schermata di gioco
        cl.show(mainPanel, "INIZIO");
        currentDisplaying = "INIZIO";
        // imposta musica
        SoundHandler.playWav(STARTING_MUSIC_PATH, SoundHandler.Mode.MUSIC);

        mainPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        // -----------------------------------------------------
        //                  SETUP MainFrame
        // -----------------------------------------------------
        add(mainPanel, BorderLayout.CENTER);
        //addKeyListener(ESC_LISTENER);
        pack();

    }

    // setup scena iniziale di gioco TODO: deprecare
    // TODO: posticipare correttamente caricamento, non deve avvenire all'inizio dell'esecuzione
    // ma solo all'inizio del gioco
    private void setupPlayground()
    {
        SoundHandler.playWav(currentRoom.getMusicPath(), SoundHandler.Mode.MUSIC);

        ActionSequence a = XmlLoader.loadRoomInit("src/main/resources/scenari/piano terra/PT-B.xml");
        GameManager.startScenario(a);
    }

    // inizializza gameScreenPanel
    private void initGameScreenPanel()
    {
        gameScreenPanel = new GameScreenPanel(currentRoom);
        gameScreenPanel.setScalingFactor(rescalingFactor);

        // Crea nuova label per visualizzare l'immagine di sfondo
        JLabel backgroundLabel = new JLabel(backgroundImg);

        // Imposta dimensioni pannello sfruttando la larghezza dello schermo
        gameScreenPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, gameHeight)); // SCREEN_WIDTH / 2));

        // Aggiungi background al layer 0
        gameScreenPanel.add(backgroundLabel, GameScreenPanel.BACKGROUND_LAYER);
        gameScreenPanel.setBackgroundLabel(backgroundLabel);
    }

    // inizializza textBarPanel
    private void initTextBarPanel()
    {
        textBarPanel = new TextBarPanel(DEFAULT_SCALING_FACTOR);

        // nota: questi numeri sono per centrare sullo schermo la textBar
        int x_offset = (int)(6 * GameManager.BLOCK_SIZE * DEFAULT_SCALING_FACTOR); // TODO : aggiustare questi
        int y_offset = (int)(7 * DEFAULT_SCALING_FACTOR);

        // impostazione posizione del textBarPanel
        textBarPanel.setBounds(gameScreenPanel.getInsets().left + x_offset, gameScreenPanel.getInsets().top + y_offset,
                (int) textBarPanel.getPreferredSize().getWidth(), (int) textBarPanel.getPreferredSize().getHeight());
        // aggiungi a gameScreenPanel
        gameScreenPanel.add(textBarPanel, GameScreenPanel.TEXT_BAR_LEVEL);
    }

    // inizializza inventoryPanel
    private void initInventoryPanel()
    {
        inventoryPanel = new InventoryPanel(SCREEN_HEIGHT - DEFAULT_GAME_HEIGHT);
    }

    // inizializza gamePanel
    private void initGamePanel()
    {
        gamePanel.setLayout(null);
        gamePanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        gamePanel.setBackground(Color.BLACK);

        Insets insets = gamePanel.getInsets();

        // imposta posizione dell'inventoryPanel nel gamePanel e aggiungilo
        int xBorder = (SCREEN_WIDTH - (int) inventoryPanel.getPreferredSize().getWidth()) / 2;
        inventoryPanel.setBounds(insets.left + xBorder, insets.top + DEFAULT_GAME_HEIGHT,
                (int) inventoryPanel.getPreferredSize().getWidth(), (int) inventoryPanel.getPreferredSize().getHeight());
        gamePanel.add(inventoryPanel);

        // imposta posizione del gameScreenPanel nel gamePanel e aggiungilo
        xBorder = (SCREEN_WIDTH - gameWidth) / 2;
        gameScreenPanel.setBounds(insets.left, insets.top, gameScreenPanel.getPreferredSize().width,
                gameScreenPanel.getPreferredSize().height);
        gameScreenPanel.getBackgroundLabel().setBounds(gameScreenPanel.getRoomBorders().getX() + xBorder,
                gameScreenPanel.getRoomBorders().getY(),
                backgroundImg.getIconWidth(),
                backgroundImg.getIconHeight());
        gamePanel.add(gameScreenPanel);
    }

    // inizializza menuPanel
    public void initMenuPanel()
    {
        menuPanel = new JLayeredPane();

        // Creazione bottoni per menuPanel TODO: modificare sta cacata
        JButton okButton = new JButton("Ok");
        JButton exitButton = new JButton("Esci");
        exitButton.addActionListener((e) -> System.exit(0));

        menuPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        menuPanel.setOpaque(true);
        menuPanel.setBackground(Color.BLACK);


        String MENU_BACK_PATH = "/img/computersPause.png";

        Image menuBackImage = SpriteManager.loadSpriteSheet(MENU_BACK_PATH);
        double rescalingBackgroundFactor = ((double) SCREEN_WIDTH/menuBackImage.getWidth(null));

        JLabel backLabel = new JLabel(SpriteManager.rescaledImageIcon(SpriteManager.loadSpriteSheet(MENU_BACK_PATH),
                                                                                        rescalingBackgroundFactor ));

        int xBorder = (SCREEN_WIDTH - backLabel.getIcon().getIconWidth()) / 2;

        backLabel.setBounds(menuPanel.getInsets().left + xBorder, menuPanel.getInsets().top, backLabel.getIcon().getIconWidth(), backLabel.getIcon().getIconHeight());

        okButton.setBounds(menuPanel.getInsets().left + 100, menuPanel.getInsets().top + 100, 50, 50);
        exitButton.setBounds(menuPanel.getInsets().left + 200, menuPanel.getInsets().top + 200, 50, 50);


        JPanel buttonPanel = new JPanel(new BorderLayout());
        JLabel impostLabel = new JLabel(SpriteManager.rescaledImageIcon(SpriteManager.loadSpriteSheet("/img/impostazioni.png"), rescalingFactor / 3));


        buttonPanel.add(impostLabel, BorderLayout.CENTER);

        buttonPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));  // TODO : capire dimensionamenti
        buttonPanel.setBounds(menuPanel.getInsets().left, menuPanel.getInsets().top, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(new Color(0, 0, 0, 0));

        menuPanel.add(buttonPanel, Integer.valueOf(2));

        menuPanel.add(backLabel, Integer.valueOf(0));

        // Aggiungi bottoni al menuPanel
        menuPanel.add(okButton, Integer.valueOf(3));
        menuPanel.add(exitButton, Integer.valueOf(3));

        menuPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
    }

    // inizializza menuPanel
    public void initStartingMenuPanel()
    {
        startingMenuPanel = new JLayeredPane();

        startingMenuPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        startingMenuPanel.setOpaque(true);
        startingMenuPanel.setBackground(Color.BLACK);


        String MENU_BACK_PATH = "/img/schermataIniziale.png";

        Image menuBackImage = SpriteManager.loadSpriteSheet(MENU_BACK_PATH);
        double rescalingBackgroundFactor = ((double) SCREEN_WIDTH/menuBackImage.getWidth(null));

        JLabel backLabel = new JLabel(SpriteManager.rescaledImageIcon(SpriteManager.loadSpriteSheet(MENU_BACK_PATH),
                rescalingBackgroundFactor ));

        int xBorder = (SCREEN_WIDTH - backLabel.getIcon().getIconWidth()) / 2;

        backLabel.setBounds(startingMenuPanel.getInsets().left + xBorder, startingMenuPanel.getInsets().top,
                backLabel.getIcon().getIconWidth(), backLabel.getIcon().getIconHeight());

        startingMenuPanel.add(backLabel, Integer.valueOf(0));

        startingMenuPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
    }

    /**
     * Visualizza menu di gioco oppure chiude il menu di gioco.
     *
     * @param b {@code true} per visualizzare il menu di gioco,
     *                      {@code false} per chiudere il menu di gioco
     */
    public void showMenu(boolean b)
    {
        CardLayout cl = (CardLayout) mainPanel.getLayout();

        if(b)
        {
            cl.show(mainPanel, "MENU");
            currentDisplaying = "MENU";
        }
        else
        {
            cl.show(mainPanel, "GIOCO");
            currentDisplaying = "GIOCO";
        }

    }

    public void play()
    {
        if(GameState.getState() != GameState.State.INIT)
        {
            return;
        }

        CardLayout cl = (CardLayout) mainPanel.getLayout();

        cl.show(mainPanel, "GIOCO");
        currentDisplaying = "GIOCO";

        GameState.changeState(GameState.State.PLAYING);
        setupPlayground();
    }

    /**
     * Ritorna true se il MENU è correntemente visualizzato.
     *
     * @return true se il MENU è correntemente visualizzato, false altrimenti.
     */
    public boolean isMenuDisplaying()
    {
        return currentDisplaying.equals("MENU");
    }

    /**
     * Punto d'inizio dell'applicazione.
     *
     * @param args argomenti da linea di comando
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

        Room demoRoom = XmlLoader.loadRoom("src/main/resources/scenari/piano terra/PT-B.xml");

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame(demoRoom).setVisible(true));
    }
}
