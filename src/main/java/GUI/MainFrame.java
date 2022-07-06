package GUI;


import database.DBManager;
import items.Door;
import items.Item;
import rooms.Coordinates;
import rooms.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MainFrame extends javax.swing.JFrame {

    private final Room currentRoom;
    private Icon backgroundImg;

    private final int screenWidth;
    private final int screenHeight;
    private int gameWidth;
    private int gameHeight;
    private static double rescalingFactor;
    private final static JLabel outputLabel = new JLabel("ciao");

    // COMPONENTI SWING
    private JLabel backgroundLabel;
    private JLayeredPane gameScreenPanel;
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel gamePanel;
    private JPanel inventoryPanel;

    // LISTENER FOR KEYS
    private final GameKeyListener ESC_LISTENER;


    /**
     * Dizionario che contiene gli oggetti presenti nella stanza
     * e le JLabel associate al gameScreenPanel
     */
    private static Map<Item, JLabel> itemLabelMap;

    // LAYER GAMEPANEL (sono richiesti Integer e non int)
    private final static Integer BACKGROUND_LAYER = 1;
    private final static Integer ITEM_LAYER = 2;


    public static JLabel getLabelAssociated(Item item)
    {
        return itemLabelMap.get(item);
    }

    public static double getScalingFactor()
    {
        return rescalingFactor;
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

        // inizializzazione immagine di sfondo
        setupBackground();
        // inizializzazione componenti
        initComponents();
        // inizializzazione cursore
        initCursor();
        // attiva schermo intero
        fullScreenOn();

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

    /*
    private Icon rescaledImageIcon(Image im)
    {
        int newWidth = (int) (rescalingFactor * im.getWidth(null));
        int newHeight = (int)(rescalingFactor * im.getHeight(null));
        Image newSprite = im.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
         return new ImageIcon(newSprite);
    }

     */

    // inizializzazione componenti JFrame
    private void initComponents()
    {
        //Creazione componenti
        mainPanel = new JPanel();
        menuPanel = new JPanel();
        gamePanel = new JPanel();
        inventoryPanel = new JPanel();
        gameScreenPanel = new JLayeredPane();



        // Chiudi l'app alla chiusura della finestra
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Schwartz");

        // Imposta dimensioni finestra pari a quelle dello schermo
        setPreferredSize(new java.awt.Dimension(screenWidth, screenHeight));

        // -----------------------------------------------------
        //                  SETUP gameScreenPanel
        // -----------------------------------------------------

        Item barile1 = new Item("Barile", "Un barile scemo come Basile");
        Door door = new Door("Porta", "Una porta spicolosa.");

        // Crea nuova label per visualizzare l'immagine di sfondo
        backgroundLabel = new javax.swing.JLabel(backgroundImg);


        // Imposta dimensioni pannello pari a quelle dello schermo
        gameScreenPanel.setPreferredSize(new java.awt.Dimension(gameWidth, gameHeight));

        Insets gameScreenPanelInsets = gameScreenPanel.getInsets();

        backgroundLabel.setBounds(gameScreenPanelInsets.left, gameScreenPanelInsets.top, backgroundImg.getIconWidth(), backgroundImg.getIconHeight());

        addGameItem(barile1, 9, 5);
        addGameItem(door, 7, 2);

        // Aggiungi background al layer 0
        gameScreenPanel.add(backgroundLabel, BACKGROUND_LAYER);

        // -----------------------------------------------------
        //                  SETUP inventoryPanel
        // -----------------------------------------------------
        inventoryPanel.setLayout(new FlowLayout());
        inventoryPanel.add(outputLabel);

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

    public static void output(String s)
    {
        outputLabel.setFont(new Font("Calibri", Font.BOLD, 40));
        outputLabel.setText(s);
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
        itemLabel.addMouseListener(new GameMouseListener(MouseEvent.BUTTON1,
                                   () -> System.out.println("a"), () -> System.out.println("b")));
        GameMouseListener popMenuListener = new GameMouseListener(MouseEvent.BUTTON3,
                           () -> PopMenuManager.showMenu(it, itemLabel, 0, 0), null);
        itemLabel.addMouseListener(popMenuListener);

        // metti la coppia Item JLabel nel dizionario
        itemLabelMap.put(it, itemLabel);

        // aggiungi la label nell'ITEM_LAYER
        gameScreenPanel.add(itemLabel, ITEM_LAYER);

        updateItemPosition(it, xBlocks, yBlocks);
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
        Coordinates coord = calculateCoordinates(xBlocks, yBlocks);

        JLabel itemLabel = itemLabelMap.get(it);
        itemLabel.setBounds(insets.left + coord.getX(), insets.top + coord.getY(),
                            rescaledSprite.getIconWidth(), rescaledSprite.getIconHeight());
    }
    // TODO: aggiornare stato stanza, generare evento di gioco

    // TODO: calcolare massimi xBlock e yBlock per la stanza
    private Coordinates calculateCoordinates(int xBlocks, int yBlocks)
    {
        if(xBlocks < 0 || yBlocks < 0)
            throw new IllegalArgumentException();

        final int BLOCK_SIZE = 48;
        int xOffset = (int)(xBlocks * BLOCK_SIZE * rescalingFactor);
        int yOffset = (int) (yBlocks * BLOCK_SIZE * rescalingFactor);

        return new Coordinates(xOffset, yOffset);
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
    public static void main(String args[]) throws Exception
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */


        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        DBManager.setupInventory();

        Room cucina = DBManager.loadRoom("Cucina");

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame(cucina).setVisible(true));
    }

}
