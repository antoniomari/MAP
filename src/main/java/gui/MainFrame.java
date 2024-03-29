package gui;

import animation.PerpetualAnimation;
import database.DBManager;
import entity.characters.PlayingCharacter;
import events.executors.Executor;
import general.ActionSequence;
import general.FontManager;
import general.GameManager;
import general.LogOutputManager;
import general.xml.XmlLoader;
import graphics.SpriteManager;
import entity.rooms.Room;
import sound.SoundHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta il Frame di gioco.
 */
public class MainFrame extends JFrame {

    /*
        Dimensioni in blocchi di stanza di default, utilizzate
        per effettuare calcoli per ricavare le dimensioni dell'inventoryPanel
        e del textBarPanel, in modo tale che siano ottimizzati per stanze di tali
        dimensioni, essendo la maggioranza.
    */
    private static final int DEFAULT_WIDTH_BLOCKS = 32;
    private static final int DEFAULT_HEIGHT_BLOCKS = 16;

    private static final String INITIAL_ROOM_PATH = "src/main/resources/scenari/piano terra/PT-B.xml";
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
    private static final String CURSOR_PATH = "/img/HUD/cursoreneonnero1.png";

    // PATH ICONE
    private static final String ICON16_PATH = "/img/HUD/gameIcon16.png";
    private static final String ICON32_PATH = "/img/HUD/gameIcon32.png";
    private static final String ICON64_PATH = "/img/HUD/gameIcon64.png";
    private static final String ICON128_PATH = "/img/HUD/gameIcon128.png";


    /** Stanza in cui il giocatore si trova attualmente. */
    private Room currentRoom;

    private String initialRoomPath;

    /** Immagine di background (riscalata) corrispondente alla currentRoom*/
    private Icon backgroundImg;

    /** Dimensione in pixel della larghezza dello schermo. */
    public static int SCREEN_WIDTH;
    /** Dimensione in pixel dell'altezza dello schermo. */
    public static int SCREEN_HEIGHT;

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
     * {@link MainFrame#gamePanel}, {@link MainFrame#menuPanel} e {@link MainFrame#startingMenuPanel}.
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
        int height = (DEFAULT_GAME_HEIGHT == 0) ? SCREEN_HEIGHT : DEFAULT_GAME_HEIGHT;

        double heightRescalingFactor = (double) height / (roomBHeight * GameManager.BLOCK_SIZE);

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

        gameScreenPanel.setScalingFactor(rescalingFactor);

        gameScreenPanel.changeRoom(newRoom, SCREEN_WIDTH);

        gameWidth = (int)(newRoom.getBWidth() * rescalingFactor * GameManager.BLOCK_SIZE);
        gameHeight = (int)(newRoom.getBHeight() * rescalingFactor * GameManager.BLOCK_SIZE);

        // imposta musica
        SoundHandler.playWav(currentRoom.getMusicPath(), SoundHandler.Mode.MUSIC);

        newRoom.startScenarioOnEnter();
    }

    /**
     * Inizializza il frame, ponendolo a schermo intero, e lo registra
     * presso {@link Executor} e {@link GameManager.GameState}.
     *
     * Registra inoltre il {@link MainFrame#gameScreenPanel} presso
     * {@link GameScreenManager}.
     *
     * @param initialRoomPath path della stanza iniziale da impostare come {@link MainFrame#currentRoom}
     */
    public MainFrame(String initialRoomPath)
    {
        // Chiudi l'app alla chiusura della finestra
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Schwartz");
        // Imposta dimensioni finestra pari a quelle dello schermo
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        // imposta stanza iniziale
        //currentRoom = initialRoom;
        this.initialRoomPath = initialRoomPath;

        // inizializzazione componenti
        initComponents();
        // inizializzazione cursore
        initCursor();
        // inizializzazione icona
        initIcons();
        // attiva schermo intero
        fullScreenOn();

        // registrati presso gli Executor (eventi) e presso GameState
        Executor.setMainFrame(this);
        // registra il gameScreenPanel presso GameScreenManager
        GameScreenManager.setActivePanel(gameScreenPanel);
        GameManager.setMainFrame(this);
    }

    // Inizializzazione cursore personalizzato
    private void initCursor()
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension cursorDimension = toolkit.getBestCursorSize(32,32);
        Image cursorImage = SpriteManager.loadImage(CURSOR_PATH).getScaledInstance(cursorDimension.width, cursorDimension.height, Image.SCALE_SMOOTH);
        Cursor c = toolkit.createCustomCursor(cursorImage , new Point(0,0), "img");
        setCursor(c);
    }

    private void initIcons()
    {
        List<Image> iconsList = new ArrayList<>();
        iconsList.add(SpriteManager.loadImage(ICON16_PATH));
        iconsList.add(SpriteManager.loadImage(ICON32_PATH));
        iconsList.add(SpriteManager.loadImage(ICON64_PATH));
        iconsList.add(SpriteManager.loadImage(ICON128_PATH));

        setIconImages(iconsList);
    }

    // Inizializzazione immagine di sfondo e gameWidth, gameHeight
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
        gameScreenPanel = new GameScreenPanel();
        mainPanel = new JPanel();
        gamePanel = new JPanel();
        textBarPanel = new TextBarPanel(DEFAULT_SCALING_FACTOR);
        inventoryPanel = new InventoryPanel(SCREEN_HEIGHT - DEFAULT_GAME_HEIGHT);

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

        pack();

    }

    // ma solo all'inizio del gioco
    private void setupPlayground()
    {
        ActionSequence a = XmlLoader.loadRoomInit("src/main/resources/scenari/piano terra/PT-B.xml");
        SoundHandler.playWav(currentRoom.getMusicPath(), SoundHandler.Mode.MUSIC);
        GameManager.startScenario(a);

    }

    // inizializza gameScreenPanel
    private void initGameScreenPanel()
    {
        gameScreenPanel.setInitialRoom(currentRoom);
        gameScreenPanel.setScalingFactor(rescalingFactor);

        // Crea nuova label per visualizzare l'immagine di sfondo
        JLabel backgroundLabel = new JLabel(backgroundImg);

        // Imposta dimensioni pannello sfruttando la larghezza dello schermo
        gameScreenPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, gameHeight));

        // Aggiungi background al layer 0
        gameScreenPanel.add(backgroundLabel, GameScreenPanel.BACKGROUND_LAYER);
        gameScreenPanel.setBackgroundLabel(backgroundLabel);
    }

    // inizializza textBarPanel
    private void initTextBarPanel()
    {
        // nota: questi numeri sono per centrare sullo schermo la textBar
        int x_offset = (int) (SCREEN_WIDTH - textBarPanel.getPreferredSize().getWidth()) / 2;
        int y_offset = (int)(7 * DEFAULT_SCALING_FACTOR);

        // impostazione posizione del textBarPanel
        textBarPanel.setBounds(gameScreenPanel.getInsets().left + x_offset, gameScreenPanel.getInsets().top + y_offset,
                (int) textBarPanel.getPreferredSize().getWidth(), (int) textBarPanel.getPreferredSize().getHeight());
        // aggiungi a gameScreenPanel
        gameScreenPanel.add(textBarPanel, GameScreenPanel.TEXT_BAR_LEVEL);
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
        menuPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        menuPanel.setOpaque(true);
        menuPanel.setBackground(Color.BLACK);


        final String MENU_BACK_PATH = "/img/computersPause.png";


        JLabel backLabel = new JLabel(SpriteManager.rescaledImageIcon(SpriteManager.loadImage(MENU_BACK_PATH),
                                                                                        SCREEN_WIDTH,SCREEN_HEIGHT ));

        final int xBorder = (SCREEN_WIDTH - backLabel.getIcon().getIconWidth()) / 2;
        final int LEFT = menuPanel.getInsets().left;
        final int TOP = menuPanel.getInsets().top;

        backLabel.setBounds(LEFT + xBorder, TOP, backLabel.getIcon().getIconWidth(), backLabel.getIcon().getIconHeight());

        GameButtonLabel continuaLabel = makeMenuButton("/img/Menu iniziale/continua.png",
            "/img/Menu iniziale/continua pressed.png", () -> showMenu(false));
        continuaLabel.setBounds(LEFT + SCREEN_WIDTH / 40, TOP + (SCREEN_HEIGHT * 8) / 24,
                continuaLabel.getIcon().getIconWidth(), continuaLabel.getIcon().getIconHeight());

        GameButtonLabel salvaLabel = makeMenuButton("/img/Menu iniziale/salva.png",
                "/img/Menu iniziale/salva pressed.png", this::save);
        salvaLabel.setBounds(LEFT + SCREEN_WIDTH / 40, TOP + (SCREEN_HEIGHT * 12) / 24,
                salvaLabel.getIcon().getIconWidth(), salvaLabel.getIcon().getIconHeight());

        GameButtonLabel impostazioniLabel = makeMenuButton("/img/Menu iniziale/impostazioni.png",
                "/img/Menu iniziale/impostazioni pressed.png", null);
        impostazioniLabel.setBounds(LEFT + SCREEN_WIDTH / 40, TOP + (SCREEN_HEIGHT * 16) / 24,
                impostazioniLabel.getIcon().getIconWidth(), impostazioniLabel.getIcon().getIconHeight());

        impostazioniLabel.disableButtonLabel();

        GameButtonLabel esciLabel = makeMenuButton("/img/Menu iniziale/esci.png",
                "/img/Menu iniziale/esci pressed.png", this::exit);
        esciLabel.setBounds(LEFT + SCREEN_WIDTH / 40, TOP + (SCREEN_HEIGHT * 20) / 24,
                esciLabel.getIcon().getIconWidth(), esciLabel.getIcon().getIconHeight());

        menuPanel.add(backLabel, Integer.valueOf(0));
        menuPanel.add(continuaLabel, Integer.valueOf(1));
        menuPanel.add(salvaLabel, Integer.valueOf(1));
        menuPanel.add(impostazioniLabel, Integer.valueOf(1));
        menuPanel.add(esciLabel, Integer.valueOf(1));


        menuPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
    }

    // inizializza menuPanel
    private void initStartingMenuPanel()
    {
        startingMenuPanel = new JLayeredPane();

        startingMenuPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        startingMenuPanel.setOpaque(true);
        startingMenuPanel.setBackground(Color.BLACK);

        String MENU_BACK_PATH = "/img/Menu iniziale/background.png";

        Image menuBackImage = SpriteManager.loadImage(MENU_BACK_PATH);
        JLabel backLabel = new JLabel(SpriteManager.rescaledImageIcon(SpriteManager.loadImage(MENU_BACK_PATH),
                SCREEN_WIDTH, SCREEN_HEIGHT ));

        final int LEFT = startingMenuPanel.getInsets().left;
        final int TOP = startingMenuPanel.getInsets().top;

        final int xBorder = (SCREEN_WIDTH - backLabel.getIcon().getIconWidth()) / 2;

        backLabel.setBounds(LEFT + xBorder, TOP,
                backLabel.getIcon().getIconWidth(), backLabel.getIcon().getIconHeight());

        // imposta titolo
        BufferedImage titleImage  = SpriteManager.loadSpriteByName(
                SpriteManager.loadImage("/img/Menu iniziale/titolo.png"),
                "/img/Menu iniziale/titolo.json", "1");
        final double titleRescalingFactor = (double) SCREEN_WIDTH / (1.5 * titleImage.getWidth());
        final int titleXOffset = (int) (SCREEN_WIDTH - titleImage.getWidth() * titleRescalingFactor) / 2;
        JLabel titleLabel = new JLabel(SpriteManager.rescaledImageIcon(titleImage, titleRescalingFactor));

        titleLabel.setBounds(LEFT + titleXOffset, TOP, titleLabel.getIcon().getIconWidth(), titleLabel.getIcon().getIconHeight());


        BufferedImage titleBackImage  = SpriteManager.loadImage("/img/Menu iniziale/muro titolo.png");
        JLabel titleBackLabel = new JLabel(SpriteManager.rescaledImageIcon(titleBackImage, titleRescalingFactor));

        titleBackLabel.setBounds(LEFT + titleXOffset, TOP, titleBackLabel.getIcon().getIconWidth(), titleBackLabel.getIcon().getIconHeight());


        PerpetualAnimation titleAnimation = PerpetualAnimation.createPerpetualAnimation(
                "/img/Menu iniziale/titolo.png",
                "/img/Menu iniziale/titolo.json",
                titleLabel);

        titleAnimation.setDelay(100);
        titleAnimation.setFinalDelay(1000);

        // bottoni-label
        GameButtonLabel nuovaPartitaLabel = makeMenuButton("/img/Menu iniziale/nuovapartita.png",
                "/img/Menu iniziale/nuovapartita pressed.png", () -> {play(); titleAnimation.stop();});
        nuovaPartitaLabel.setBounds(LEFT + SCREEN_WIDTH / 40, TOP + (SCREEN_HEIGHT * 8) / 24,
                    nuovaPartitaLabel.getIcon().getIconWidth(), nuovaPartitaLabel.getIcon().getIconHeight());

        GameButtonLabel continuaLabel = makeMenuButton("/img/Menu iniziale/continua.png",
                "/img/Menu iniziale/continua pressed.png", () -> {loadSavings(); titleAnimation.stop();});
        continuaLabel.setBounds(LEFT + SCREEN_WIDTH / 40, TOP + (SCREEN_HEIGHT * 12) / 24,
                    continuaLabel.getIcon().getIconWidth(), continuaLabel.getIcon().getIconHeight());
        // se non ci sono salvataggi disabilita
        if(!DBManager.existSavings())
            continuaLabel.disableButtonLabel();


        GameButtonLabel impostazioniLabel = makeMenuButton("/img/Menu iniziale/impostazioni.png",
                "/img/Menu iniziale/impostazioni pressed.png", null);
        impostazioniLabel.setBounds(LEFT + SCREEN_WIDTH / 40, TOP + (SCREEN_HEIGHT * 16) / 24,
                    impostazioniLabel.getIcon().getIconWidth(), impostazioniLabel.getIcon().getIconHeight());
        // disabilita, attualmente non funzionante
        impostazioniLabel.disableButtonLabel();


        GameButtonLabel esciLabel = makeMenuButton("/img/Menu iniziale/esci.png",
                "/img/Menu iniziale/esci pressed.png", this::exit);
        esciLabel.setBounds(LEFT + SCREEN_WIDTH / 40, TOP + (SCREEN_HEIGHT * 20) / 24,
                    esciLabel.getIcon().getIconWidth(), esciLabel.getIcon().getIconHeight());




        startingMenuPanel.add(backLabel, Integer.valueOf(0));
        startingMenuPanel.add(nuovaPartitaLabel, Integer.valueOf(1));
        startingMenuPanel.add(continuaLabel, Integer.valueOf(1));
        startingMenuPanel.add(impostazioniLabel, Integer.valueOf(1));
        startingMenuPanel.add(esciLabel, Integer.valueOf(1));

        startingMenuPanel.add(titleBackLabel, Integer.valueOf(1));
        startingMenuPanel.add(titleLabel, Integer.valueOf(2));

        //addKeyListener(new GameKeyListener(KeyEvent.VK_P, titleAnimation::start, null, GameState.State.INIT));
        titleAnimation.start();

        startingMenuPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
    }

    private GameButtonLabel makeMenuButton(String buttonImagePath, String buttonPressedImagePath, Runnable clickAction)
    {
        GameButtonLabel buttonLabel = new GameButtonLabel(buttonImagePath, buttonPressedImagePath,
                DEFAULT_SCALING_FACTOR / 3);

        GameMouseListener buttonListener = new GameMouseListener(
                                            GameMouseListener.Button.LEFT,
                () -> {clickAction.run(); buttonLabel.changeIcon(false);},
                null,
                GameManager.GameState.INIT);
        buttonListener.setMouseEnteredAction(
                () ->
                {
                    buttonLabel.changeIcon(true);
                    SoundHandler.playWav(SoundHandler.CLICK_SOUND_PATH,
                            SoundHandler.Mode.SOUND);
                });

        buttonListener.setMouseExitedAction(
                () -> buttonLabel.changeIcon(false));

        buttonLabel.addMouseListener(buttonListener);
        return buttonLabel;
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
            GameManager.changeState(GameManager.GameState.INIT);
        }
        else
        {
            cl.show(mainPanel, "GIOCO");
            currentDisplaying = "GIOCO";

            GameManager.changeState(GameManager.GameState.PLAYING);
        }

    }


    /** Chiamato per iniziare una nuova partita. */
    private void play()
    {
        if(GameManager.getState() != GameManager.GameState.INIT)
        {
            return;
        }

        currentRoom = XmlLoader.loadRoom(initialRoomPath);

        // inizializzazione immagine di sfondo
        setupBackground();
        // inizializzazione componenti
        initGameScreenPanel();
        initTextBarPanel();
        initGamePanel();

        CardLayout cl = (CardLayout) mainPanel.getLayout();

        cl.show(mainPanel, "GIOCO");
        currentDisplaying = "GIOCO";

        GameManager.changeState(GameManager.GameState.PLAYING);
        setupPlayground();
    }

    /**
     * Salva il gioco, chiudendo poi il menu di pausa.
     */
    private void save()
    {
        DBManager.save();
        showMenu(false);
    }

    /**
     * Chiude l'applicazione del gioco.
     */
    private void exit()
    {
        LogOutputManager.closeLogFile();
        System.exit(0);
    }

    /**
     * Carica il salvataggio presente nel db
     */
    private void loadSavings()
    {
        DBManager.loadGameData();

        currentRoom = PlayingCharacter.getPlayer().getLocationRoom();

        // inizializzazione immagine di sfondo
        setupBackground();
        // inizializzazione componenti
        initGameScreenPanel();
        initTextBarPanel();
        initGamePanel();

        gameScreenPanel.changeRoom(currentRoom, SCREEN_WIDTH);

        CardLayout cl = (CardLayout) mainPanel.getLayout();

        cl.show(mainPanel, "GIOCO");
        currentDisplaying = "GIOCO";

        GameManager.changeState(GameManager.GameState.PLAYING);
        SoundHandler.playWav(currentRoom.getMusicPath(), SoundHandler.Mode.MUSIC);
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
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
                if ("Nimbus".equals(info.getName()))
                {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        DBManager.createGameDB();
        FontManager.loadFonts();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame(INITIAL_ROOM_PATH).setVisible(true));
    }
}
