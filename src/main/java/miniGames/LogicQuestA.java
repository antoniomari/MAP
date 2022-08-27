package miniGames;

// TODO: possibilità di implementare il factory design pattern

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LogicQuestA
{
    private JFrame mainFrame;

    // wrapper finale per ottimizzare la visualizzazione
    // e gerarchia dei pannelli
    private JScrollPane scrollWrapper;
    private JPanel mainWrapper;
    private JPanel container;

    // titolo del primo enigma
    private JLabel description;

    // componeti per la gestione dell'immagine
    private JPanel imagePannel;
    private JLabel image;
    private ImageIcon icon;

    // Pannello per il layout dei buttoni
    private JPanel optionPanel;
    private JButton buttons[] = { new JButton("7"), new JButton("2"),
            new JButton("5"), new JButton("1")  };

    // componenti Swing per comunicazione con utente
    private JDialog infoWindow;
    private JLabel infoText;
    private static final String VICTORY = "Primo Circuito settato correttamente.";
    private static final String LOST = "Ophs! Hai fuso il circuito.";

    private Font font;

    // costruttore
    public LogicQuestA() {
        mainFrame = new JFrame("PIANO ALU PROVA DEI CIRCUITI LOGICI");
        mainWrapper = new JPanel(new BorderLayout());

        // creazione pannelli principali
        container = new JPanel(new GridLayout(2,1,0,30));
        optionPanel = new JPanel(new GridLayout(2, 2,50,50));
        imagePannel = new JPanel(new GridLayout(1,1));

        // creazione titolo del minigioco e imagine gioco
        description = new JLabel("Circuito logico type A", SwingConstants.CENTER);
        icon = new ImageIcon("src/main/resources/img/ImageMiniGames/geometryEquation.png");
        image = new JLabel("" ,icon, JLabel.CENTER);

        font = new Font("Agency FB", Font.BOLD , 40);

        infoWindow = new JDialog();
        infoText = new JLabel("", SwingConstants.CENTER);

        setup();
        setupListener();
        addDetails();
    }

    final void setup() {
        description.setForeground(Color.RED);
        description.setBackground(Color.BLUE);
        description.setFont(font);
        image.setBackground(Color.BLUE);
        imagePannel.add(image);
        imagePannel.setToolTipText("risolvi l'equazione geometrica");
        imagePannel.setBackground(Color.BLUE);
        container.add(imagePannel);

        for (JButton button : buttons)
        {
            button.setBackground(Color.BLUE);
            button.setForeground(Color.RED);
            button.setFont(font);
            button.setToolTipText("Clic sinistro per rispondere");
            optionPanel.add(button);
        };

        // composizione dei componenti swing a matriosca
        optionPanel.setBackground(Color.BLUE);
        container.add(optionPanel);
        container.setBackground(Color.BLUE);
        mainWrapper.setBackground(Color.BLUE);
        mainWrapper.add(description, BorderLayout.NORTH);
        mainWrapper.add(container, BorderLayout.CENTER);
        scrollWrapper = new JScrollPane(mainWrapper);
        mainFrame.add(scrollWrapper, BorderLayout.CENTER);
    }

    final void addDetails() {
        mainFrame.setSize(600, 700);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setResizable(false);
        imagePannel.setBackground(Color.BLUE);
        optionPanel.setBackground(Color.DARK_GRAY);
        mainFrame.setBackground(Color.BLUE);
        mainFrame.getContentPane().setBackground(Color.BLUE);
        mainFrame.setVisible(true);
    }

    private void setupListener() {

        for (JButton button : buttons)
        {// gestione dei listener tramite espressioni lambda
            button.addActionListener((e) -> {
                if (e.getActionCommand().equals("1")) {
                    showInfoResult(VICTORY);
                    SwingUtilities.invokeLater(() -> new LogicQuestB());
                    mainFrame.dispose();
                }
                else {
                    showInfoResult(LOST);
                }
            });
        }

        // listener sulla finestra della jdialog
        // per la comunicazione con l'utente
        infoWindow.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent arg0)
            {
                System.out.println("Window closing");
                mainFrame.dispose();
            }
        });
    }

    private void showInfoResult(String msg) {
        infoText.setText(msg);
        infoWindow.add(infoText);
        infoWindow.setSize(450, 100);
        infoWindow.setLocationRelativeTo(null);
        infoWindow.setVisible(true);
        infoWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mainFrame.setVisible(false);
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new LogicQuestA());
    }
}

