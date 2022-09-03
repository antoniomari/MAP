package GUI.miniGames;

// TODO: possibilità di implementare il factory design pattern

import GUI.gamestate.GameState;
import general.GameException;
import general.GameManager;
import general.LogOutputManager;
import graphics.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicQuest extends MiniGame
{

    //TODO: modificare size
    private static final Font FONT = new Font("Agency FB", Font.BOLD , 40);

    private static String[] QUEST_1_BUTTONS_TEXT = {"1", "2", "5", "7"};
    private static String[] QUEST_2_BUTTONS_TEXT = {"3, 2, 1", "5, 4, 8", "3, 2, 5", "3, 6, 1"};
    private static String[] QUEST_3_BUTTONS_TEXT = {"31", "22", "12", "8"};

    private static Map<Integer, Character> numberTypeMap;

   // private final JPanel this;

    private JPanel mainWrapper;
    private JPanel container;

    // titolo del primo enigma
    private JLabel description;

    // componeti per la gestione dell'immagine
    private JPanel imagePanel;
    private JLabel image;

    // Pannello per il layout dei buttoni
    private JPanel optionPanel;

    // TODO: parametrizzare
    private JButton[] buttons;
    private String winButtonText;

    private int questNumber;

    static
    {
        numberTypeMap = new HashMap<>();
        numberTypeMap.put(1, 'A');
        numberTypeMap.put(2, 'B');
        numberTypeMap.put(3, 'C');
    }

    // costruttore
    private LogicQuest(int questNumber, String winText, String loseText, String[] buttonTexts,
                       String iconPath)
    {
        super(winText, loseText, "");

        scenarioOnWinPath = "src/main/resources/scenari/piano ALU/winLogicQuest.xml";

        initContent(questNumber, buttonTexts, iconPath);
    }

    private void initContent(int questNumber, String[] buttonTexts, String iconPath)
    {
        this.questNumber = questNumber;
        mainWrapper = new JPanel(new BorderLayout());

        // creazione pannelli principali
        container = new JPanel(new GridLayout(2,1,0,30));
        optionPanel = new JPanel(new GridLayout(2, 2,50,50));
        imagePanel = new JPanel(new GridLayout(1,1));

        // creazione titolo del minigioco
        description = new JLabel("Circuito logico type " + numberTypeMap.get(questNumber), SwingConstants.CENTER);

        initButtons(buttonTexts);
        setIcon(iconPath);

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
        int xOffset = (int) getPreferredSize().getWidth() / 4;
        int yOffset = 0;
        mainWrapper.setBounds(getInsets().left + xOffset, getInsets().top, (int) getPreferredSize().getWidth() / 2, (int)getPreferredSize().getHeight());
        add(mainWrapper, TEST_LAYER);


        /*

        if(questNumber == 1)
        {
            quest = new LogicQuest(questNumber, "Primo Circuito settato correttamente.",
                    "Ops! Hai fuso il circuito.");
            quest.initButtons(QUEST_1_BUTTONS_TEXT);
            quest.setIcon("src/main/resources/img/ImageMiniGames/geometryEquation.png");

        }
        else if(number == 2)
        {
            quest = new LogicQuest(questNumber, "Secondo Circuito settato correttamente.",
                    "Ops! Hai fuso il circuito.");
            quest.initButtons(QUEST_2_BUTTONS_TEXT);
            quest.setIcon("src/main/resources/img/ImageMiniGames/sweetEquation.png");

        }
        else // number = 3
        {
            quest = new LogicQuest(number, "Complimenti hai settato tutti i circuiti logici!",
                    "Peccato c'eri quasi, ma hai fuso il circuito.");
            quest.initButtons(QUEST_3_BUTTONS_TEXT);
            quest.setIcon("src/main/resources/img/ImageMiniGames/fruitEquation.png");
        }

         */
        setupListener();
        // TODO: modificare size
        imagePanel.setBackground(Color.BLUE);
        optionPanel.setBackground(Color.DARK_GRAY);
        this.setBackground(Color.BLUE);

        setVisible(true);
    }


    public static void executeTest()
    {
        LogOutputManager.logOutput("Iniziando Test ALU: ", LogOutputManager.GAMESTATE_COLOR);
        GameState.changeState(GameState.State.TEST);

        MiniGame lastTest = MiniGame.getLastQuitTest();

        if(lastTest instanceof LogicQuest)
            MiniGame.setCurrentTest(lastTest);
        else
        {
            MiniGame.setCurrentTest(createLogicQuest(1));
        }
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
        buttons[list.get(0)].setFocusable(false);
        buttons[list.get(1)] = new JButton(buttonsText[1]);
        buttons[list.get(1)].setFocusable(false);
        buttons[list.get(2)] = new JButton(buttonsText[2]);
        buttons[list.get(2)].setFocusable(false);
        buttons[list.get(3)] = new JButton(buttonsText[3]);
        buttons[list.get(3)].setFocusable(false);

        winButtonText = winButton;
    }


    public static LogicQuest createLogicQuest(int number)
    {
        if(number < 1 || number > 3)
        {
            throw new GameException("Logic quest number non valido");
        }

        LogicQuest quest; //= new LogicQuest(number);

        if(number == 1)
        {
            quest = new LogicQuest(number, "Primo Circuito settato correttamente.",
                    "Ops! Hai fuso il circuito.", QUEST_1_BUTTONS_TEXT,
                    "src/main/resources/img/ImageMiniGames/geometryEquation.png");
        }
        else if(number == 2)
        {
            quest = new LogicQuest(number, "Secondo Circuito settato correttamente.",
                    "Ops! Hai fuso il circuito.", QUEST_2_BUTTONS_TEXT,
                    "src/main/resources/img/ImageMiniGames/sweetEquation.png");
        }
        else // number = 3
        {
            quest = new LogicQuest(number, "Complimenti hai settato tutti i circuiti logici!",
                    "Peccato c'eri quasi, ma hai fuso il circuito.", QUEST_3_BUTTONS_TEXT,
                    "src/main/resources/img/ImageMiniGames/fruitEquation.png");
        }
        return quest;
    }



    private void setIcon(String iconPath)
    {
        ImageIcon icon = new ImageIcon(iconPath);
        image = new JLabel(icon, JLabel.CENTER);
    }

    private void setupListener()
    {

        for (JButton button : buttons)
            button.addActionListener((e) ->
            {
                if (e.getActionCommand().equals(winButtonText))
                    showResult(victory);
                else
                    showResult(lost);
            });

    }

}

