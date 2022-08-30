package GUI.miniGames;

// TODO: possibilità di implementare il factory design pattern

import general.GameException;
import general.GameManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicQuest
{

    //TODO: modificare size
    private static final Font FONT = new Font("Agency FB", Font.BOLD , 40);

    private static String[] QUEST_1_BUTTONS_TEXT = {"1", "2", "5", "7"};
    private static String[] QUEST_2_BUTTONS_TEXT = {"3, 2, 1", "5, 4, 8", "3, 2, 5", "3, 6, 1"};
    private static String[] QUEST_3_BUTTONS_TEXT = {"31", "22", "12", "8"};

    private static Map<Integer, Character> numberTypeMap;

    private final JDialog questDialog;

    private final JPanel mainWrapper;
    private final JPanel container;

    // titolo del primo enigma
    private JLabel description;

    // componeti per la gestione dell'immagine
    private final JPanel imagePanel;
    private JLabel image;

    // Pannello per il layout dei buttoni
    private final JPanel optionPanel;

    // TODO: parametrizzare
    private JButton[] buttons;
    private String winButtonText;

    // componenti Swing per comunicazione con utente
    private final JDialog infoWindow;
    private final JLabel infoText;
    private String victoryText;
    private String lostText;


    private int questNumber;

    static
    {
        numberTypeMap = new HashMap<>();
        numberTypeMap.put(1, 'A');
        numberTypeMap.put(2, 'B');
        numberTypeMap.put(3, 'C');
    }

    // costruttore
    private LogicQuest(int questNumber) {

        this.questNumber = questNumber;
        questDialog = new JDialog(GameManager.getMainFrame(), "PIANO ALU PROVA DEI CIRCUITI LOGICI");
        mainWrapper = new JPanel(new BorderLayout());

        // creazione pannelli principali
        container = new JPanel(new GridLayout(2,1,0,30));
        optionPanel = new JPanel(new GridLayout(2, 2,50,50));
        imagePanel = new JPanel(new GridLayout(1,1));

        // creazione titolo del minigioco
        description = new JLabel("Circuito logico type " + numberTypeMap.get(questNumber), SwingConstants.CENTER);

        infoWindow = new JDialog(questDialog);
        infoWindow.setModal(true);
        infoText = new JLabel("", SwingConstants.CENTER);

    }

    public static void executeTest()
    {
        SwingUtilities.invokeLater(() -> LogicQuest.createLogicQuest(1));
        GameManager.continueScenario();
    }

    private void initButtons(String[] buttonsText)
    {
        String winButton = buttonsText[0];

        buttons = new JButton[4];

        List<Integer> list = new ArrayList<Integer>();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        java.util.Collections.shuffle(list);

        buttons[list.get(0)] = new JButton(winButton);
        buttons[list.get(1)] = new JButton(buttonsText[1]);
        buttons[list.get(2)] = new JButton(buttonsText[2]);
        buttons[list.get(3)] = new JButton(buttonsText[3]);

        winButtonText = winButton;
    }

    public static LogicQuest createLogicQuest(int number)
    {
        if(number < 1 || number > 3)
        {
            throw new GameException("Logic quest number non valido");
        }

        LogicQuest quest = new LogicQuest(number);

        if(number == 1)
        {
            quest.initButtons(QUEST_1_BUTTONS_TEXT);
            quest.victoryText = "Primo Circuito settato correttamente.";
            quest.lostText = "Ops! Hai fuso il circuito.";
            quest.setIcon("src/main/resources/img/ImageMiniGames/geometryEquation.png");

        }
        else if(number == 2)
        {
            quest.initButtons(QUEST_2_BUTTONS_TEXT);
            quest.victoryText = "Secondo Circuito settato correttamente.";
            quest.lostText = "Ops! Hai fuso il circuito.";
            quest.setIcon("src/main/resources/img/ImageMiniGames/sweetEquation.png");

        }
        else // number = 3
        {
            quest.initButtons(QUEST_3_BUTTONS_TEXT);
            quest.victoryText = "Complimenti hai settato tutti i circuiti logici!";
            quest.lostText = "Peccato c'eri quasi, ma hai fuso il circuito.";
            quest.setIcon("src/main/resources/img/ImageMiniGames/fruitEquation.png");
        }


        quest.setup();
        quest.setupListener();
        quest.addDetails();

        return quest;
    }


    private void setIcon(String iconPath)
    {
        ImageIcon icon = new ImageIcon(iconPath);
        image = new JLabel(icon, JLabel.CENTER);
    }


    private void setup() {
        description.setForeground(Color.RED);
        description.setBackground(Color.BLUE);
        description.setFont(FONT);
        image.setBackground(Color.BLUE);
        imagePanel.add(image);
        imagePanel.setToolTipText("Risolvi l'equazione");
        imagePanel.setBackground(Color.BLUE);
        container.add(imagePanel);

        for (JButton button : buttons)
        {
            button.setBackground(Color.BLUE);
            button.setForeground(Color.RED);
            button.setFont(FONT);
            button.setToolTipText("Click sinistro per rispondere");
            optionPanel.add(button);
        };

        // composizione dei componenti swing a matriosca
        optionPanel.setBackground(Color.BLUE);
        container.add(optionPanel);
        container.setBackground(Color.BLUE);
        mainWrapper.setBackground(Color.BLUE);
        mainWrapper.add(description, BorderLayout.NORTH);
        mainWrapper.add(container, BorderLayout.CENTER);
        // wrapper finale per ottimizzare la visualizzazione
        // e gerarchia dei pannelli
        JScrollPane scrollWrapper = new JScrollPane(mainWrapper);
        questDialog.add(scrollWrapper, BorderLayout.CENTER);
    }

    final void addDetails() {
        // TODO: modificare size
        questDialog.setSize(600, 700);
        questDialog.setModal(true);
        questDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        questDialog.setResizable(false);

        // imposta come "figlio"
        // questDialog.setLocationRelativeTo(GameManager.getMainFrame());
        imagePanel.setBackground(Color.BLUE);
        optionPanel.setBackground(Color.DARK_GRAY);
        questDialog.setBackground(Color.BLUE);
        questDialog.getContentPane().setBackground(Color.BLUE);
        questDialog.setVisible(true);
    }

    private void setupListener() {

        for (JButton button : buttons)
        {// gestione dei listener tramite espressioni lambda
            button.addActionListener((e) -> {
                if (e.getActionCommand().equals(winButtonText)) {
                    showInfoResult(victoryText);

                    if(questNumber < 3)
                        SwingUtilities.invokeLater(() -> createLogicQuest(questNumber + 1));

                    questDialog.dispose();
                }
                else {
                    showInfoResult(lostText);
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
                // System.out.println("Window closing");
                questDialog.dispose();
            }
        });
    }

    private void showInfoResult(String msg)
    {
        infoText.setText(msg);
        infoWindow.add(infoText);
        infoWindow.setSize(450, 100);
        infoWindow.setLocationRelativeTo(questDialog);
        infoWindow.setVisible(true);
        infoWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        questDialog.setVisible(false);
    }
}

