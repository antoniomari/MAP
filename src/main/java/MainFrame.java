/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import database.DBManager;
import items.Item;
import rooms.Coordinates;
import rooms.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainFrame extends javax.swing.JFrame {

    private final Room currentRoom;
    private Icon backgroundImg;

    private final int screenWidth;
    private final int screenHeight;
    private double rescalingFactor;


    // COMPONENTI SWING
    private javax.swing.JLabel backgroundLabel;
    private javax.swing.JLayeredPane gamePanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel menuPanel;

    // LISTENER FOR KEYS
    private final MyKeyListener ESC_LISTENER;

    public class MyKeyListener implements KeyListener
    {
        private final int keyCode;
        private boolean pressed;
        private final Runnable pressAction;
        private final Runnable releaseAction;

        public MyKeyListener(int keyCode, Runnable pressAction, Runnable releaseAction)
        {
            this.keyCode = keyCode;

            if(pressAction == null)
                this.pressAction = () -> {};
            else
                this.pressAction = pressAction;

            if(releaseAction == null)
                this.releaseAction = () -> {};
            else
                this.releaseAction = releaseAction;

            this.pressed = false;
        }

        @Override
        public void keyTyped(KeyEvent e)
        {
            // vuoto
        }

        @Override
        public void keyPressed(KeyEvent e)
        {
            if (e.getKeyCode() == keyCode)
            {
                if(!pressed)
                {
                    pressAction.run();
                    pressed = true;
                }

            }
        }

        @Override
        public void keyReleased(KeyEvent e)
        {
            if (e.getKeyCode() == keyCode)
            {
                releaseAction.run();
                pressed = false;
            }
        }
    }

    public MainFrame(Room initialRoom)
    {
        currentRoom = initialRoom;

        // Calcolo delle dimensioni dello schermo
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int) screenSize.getWidth();
        screenHeight = (int) screenSize.getHeight();

        this.ESC_LISTENER = new MyKeyListener(KeyEvent.VK_ESCAPE, () -> showMenu(true), null);

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
        backgroundImg = new ImageIcon(roomImage.getScaledInstance(screenWidth, (int)(roomHeight * rescalingFactor), Image.SCALE_SMOOTH));


    }

    private void fullScreenOn()
    {
        GraphicsDevice device = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getScreenDevices()[0];
        device.setFullScreenWindow(this);
    }

    private Icon rescaledImageIcon(Image im)
    {
        int newWidth = (int) (rescalingFactor * im.getWidth(null));
        int newHeight = (int)(rescalingFactor * im.getHeight(null));
        Image newSprite = im.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
         return new ImageIcon(newSprite);
    }

    // inizializzazione componenti JFrame
    private void initComponents()
    {
        //Creazione componenti
        mainPanel = new javax.swing.JPanel();
        menuPanel = new javax.swing.JPanel();
        gamePanel = new javax.swing.JLayeredPane();

        // Chiudi l'app alla chiusura della finestra
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Schwartz");

        // Imposta cursore visualizzato
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR)); //TODO: custom cursor

        // Imposta dimensioni finestra pari a quelle dello schermo
        setPreferredSize(new java.awt.Dimension(screenWidth, screenHeight));

        // -----------------------------------------------------
        //                  SETUP gamePanel
        // -----------------------------------------------------

        Item barile1 = new Item("Barile", "Un barile scemo come Basile");
        JLabel barile1Label = new JLabel(rescaledImageIcon(barile1.getSprite()));

        // Crea nuova label per visualizzare l'immagine di sfondo
        backgroundLabel = new javax.swing.JLabel(backgroundImg);


        // Imposta dimensioni pannello pari a quelle dello schermo
        gamePanel.setPreferredSize(new java.awt.Dimension(screenWidth, screenHeight));

        Insets gamePanelInsets = gamePanel.getInsets();

        backgroundLabel.setBounds(gamePanelInsets.left, gamePanelInsets.top, backgroundImg.getIconWidth(), backgroundImg.getIconHeight());
        paintItem(barile1, barile1Label, 9, 5);

        // Aggiungi background al layer 0
        gamePanel.add(backgroundLabel, Integer.valueOf(1));
        gamePanel.add(barile1Label, Integer.valueOf(2));



        // -----------------------------------------------------
        //                  SETUP menuPanel
        // -----------------------------------------------------

        // Creazione bottoni per menuPanel
        JButton okButton = new JButton("Ok");
        JButton exitButton = new JButton("Esci");
        exitButton.addActionListener((e) -> System.exit(0));
        Item barile = new Item("Barile", "Un barile scemo come Basile");
        JLabel barileLabel = new JLabel(rescaledImageIcon(barile.getSprite()));

        // Imposta layout
        menuPanel.setLayout(new FlowLayout());

        // Aggiungi bottoni al menuPanel
        menuPanel.add(okButton);
        menuPanel.add(exitButton);
        menuPanel.add(barileLabel);

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

    private void paintItem(Item it, JLabel label, int xBlocks, int yBlocks)
    {
        Icon resizedSprite = rescaledImageIcon(it.getSprite());
        Insets gamePanelInsets = gamePanel.getInsets();

        Coordinates coord = calculateCoordinates(xBlocks, yBlocks);
        int x = coord.getX();
        int y = coord.getY();

        label.setBounds(gamePanelInsets.left + x, gamePanelInsets.top + y, resizedSprite.getIconWidth(), resizedSprite.getIconHeight());

    }

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
