/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import com.sun.tools.javac.Main;
import database.DBManager;
import items.Door;
import rooms.Room;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOError;
import java.io.IOException;

/**
 *
 */
public class MainFrame extends javax.swing.JFrame {

    private final Room currentRoom;
    private Icon backgroundImg;

    private final int screenWidth;
    private final int screenHeight;


    // COMPONENTI SWING
    private javax.swing.JLabel backgroundLabel;
    private javax.swing.JPanel gamePanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel menuPanel;


    public MainFrame(Room initialRoom)
    {
        currentRoom = initialRoom;

        // Calcolo delle dimensioni dello schermo
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int) screenSize.getWidth();
        screenHeight = (int) screenSize.getHeight();

        // inizializzazione immagine di sfondo
        setupBackground();
        // inizializzazione compoonenti
        initComponents();
        // attiva schermo intero
        fullScreenOn();
    }

    private void setupBackground()
    {
        Image roomImage;
        try
        {
            roomImage = ImageIO.read(getClass().getResource(currentRoom.getBackgroundPath()));
            int roomWidth = roomImage.getWidth(null);
            int roomHeight = roomImage.getHeight(null);
            double proportion = (double) screenWidth / roomWidth;

            backgroundImg = new ImageIcon(roomImage.getScaledInstance(screenWidth, (int)(roomHeight * proportion), Image.SCALE_SMOOTH));
        }
        catch (IOException e)
        {
            // Errore caricamento background
            throw new IOError(e);
        }
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
        mainPanel = new javax.swing.JPanel();
        menuPanel = new javax.swing.JPanel();
        gamePanel = new javax.swing.JPanel();

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

        // Crea nuova label per visualizzare l'immagine di sfondo
        backgroundLabel = new javax.swing.JLabel(backgroundImg);

        // Aggiungi la label al gamePanel come centrale
        gamePanel.add(backgroundLabel, BorderLayout.CENTER);

        // Imposta dimensioni pannello pari a quelle dello schermo
        gamePanel.setPreferredSize(new java.awt.Dimension(screenWidth, screenHeight));

        // -----------------------------------------------------
        //                  SETUP menuPanel
        // -----------------------------------------------------

        // Creazione bottoni per menuPanel
        JButton okButton = new JButton("Ok");
        JButton exitButton = new JButton("Esci");

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
        pack();
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
