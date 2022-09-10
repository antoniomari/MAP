package gui.miniGames;

/**
 *  Mini gioco del test psicologico versione finale da integrare sul progetto di MAP
 *  serve definire un meccanismo di avvio del mini gioco
 *
 */
// TODO: possibilità di applicare factory design pattern

import general.FontManager;
import general.GameManager;
import general.LogOutputManager;
import graphics.SpriteManager;

import javax.swing.*;
import java.awt.*;

public class TestMist extends MiniGame
{


    //JLabel e ImageIcon per creazione background
    private JLabel backgroundLabel;
    private JScrollPane scrollPane;

    //JLabel per il titolo e l'intestazione del test
    private JLabel title;
    private JLabel description;

    //JPanel per contenere titolo e intestazione
    private JPanel headingPanel;

    // Questo container è dedicato a contenere tutti gli altri
    // componenti JPanel presenti e a suo tempo è incapsulato in
    // uno JScrollPane per permettere la visulizzazione del
    // contenuto in modo ottimale
    private JPanel testPanel;

    // conterrà l'array contenente le domande del test
    private JPanel questPanel;

    //JPanel per contenere i due array paralleli di checkbox
    private final JPanel[] answerPanel = new JPanel[ANSWERS];
    private final JCheckBox[] answerS = new JCheckBox[ANSWERS];
    private final JCheckBox[] answerN = new JCheckBox[ANSWERS];


    private Font QUESTION_FONT;
    private Font TITLE_FONT;
    private Font DESCRIPTION_FONT;

    private JButton checkTest;


    // questWrapper e una JLabel con funzione di wrapper attorno ad un elemento questions
    // e altri due swing components rispettivamente una answerS e un answerN che sono
    // JCheckBox i tre componenti assieme formano una singola Label del array questWrapper
    private final JLabel[] questWrapper = new JLabel[ANSWERS];
    private final JLabel[] questions = { new JLabel("1. Il Blackjack è un gioco di carte?"),
            new JLabel("2. Babbo Natale consegna i regali a Pasqua?"),
            new JLabel("3. Il legno è più duro del diamante? "),
            new JLabel("4. Usualmente, necessitiamo della luce per vedere?"),
            new JLabel("5. L'aria è un solido?"),
            new JLabel("6. La via Lattea è una galassia?"),
            new JLabel("7. Tutti i mammiferi necessitano di ossigeno per vivere?"),
            new JLabel("8. Ci sono più di 400 giorni in un anno?"),
            new JLabel("9. 1 per 5 è uguale a 500?"),
            new JLabel("10. Napoleone è morto?"),
            new JLabel("11. Questa frase è in Spagnolo?"),
            new JLabel("12. Il nostro sole è l'unica stella nello spazio?"),
            new JLabel("13. A volte le persone mentono?"),
            new JLabel("14. Le balene sono pesci?"),
            new JLabel("15. La notte è più buia del giorno?"),
            new JLabel("16. La guerra è meglio della pace?"),
            new JLabel("17. Un secondo è più breve di un minuto?"),
            new JLabel("18. C'è un numero che è il più grande?"),
            new JLabel("19. Si necessita di una patente per avere dei figli?"),
            new JLabel("20. La gravidanza è contagiosa?"),
    };

    private final boolean[] CORRECT_ANSWER = { true, false, false, true, false,
            true, true, false, false, true,
            false, false, true, false, true,
            false, true, false, false, false };

    private static final int ANSWERS = 20;
    private static final String VICTORY = "Test passato con successo!";
    private static final String LOST = "Test errato! Sei psicologicamente instabile.";
    private static final String ERROR = "ERRORE:  " +
            "controlla di aver dato una ed una sola risposta per ogni domanda.";


    public TestMist()
    {
        super(VICTORY, LOST, ERROR);
        scenarioOnWinPath = "src/main/resources/scenari/piano MIST/fineTest.xml";
        initContent();
    }

    public void initContent()
    {
        JLayeredPane testPanelWrapper = new JLayeredPane();
        scrollPane = new JScrollPane(testPanelWrapper);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        testPanel = new JPanel();
        testPanel.setLayout(new BorderLayout());

        testPanel.setPreferredSize(new Dimension(getPreferredSize().width, getPreferredSize().height * 2));
        testPanel.setBounds(testPanelWrapper.getInsets().left, testPanelWrapper.getInsets().top,
                testPanel.getPreferredSize().width, testPanel.getPreferredSize().height);


        Image backgroundImage = SpriteManager.loadImage("/img/ImageMiniGames/sfondofoglio.jpg");
        backgroundLabel = new JLabel(SpriteManager.rescaledImageIcon(backgroundImage, testPanel.getPreferredSize().width, testPanel.getPreferredSize().height));

        backgroundLabel.setBounds(testPanelWrapper.getInsets().left, testPanelWrapper.getInsets().top,
                testPanel.getPreferredSize().width, testPanel.getPreferredSize().height);


        testPanelWrapper.setPreferredSize(new Dimension(testPanel.getPreferredSize().width, testPanel.getPreferredSize().height));
        testPanelWrapper.add(testPanel,  Integer.valueOf(2));
        testPanelWrapper.add(backgroundLabel, Integer.valueOf(1));


        scrollPane.setBounds(getInsets().left, getInsets().top, (int) getPreferredSize().getWidth(), (int)getPreferredSize().getHeight());
        setMainPanel(scrollPane);



        headingPanel = new JPanel(new GridLayout(2,1));;
        title = new JLabel("Minimum intelligence signal text", SwingConstants.CENTER);
        description = new JLabel("Test psicologico intelletivo somministrato dalla Dott.ssa Gastani Frinzi.",
                SwingConstants.CENTER);

        // resize font
        QUESTION_FONT = FontManager.MIST_QUESTION_FONT.deriveFont((float)(getPreferredSize().getWidth() / 40));
        TITLE_FONT = FontManager.MIST_TITLE_FONT.deriveFont((float)(getPreferredSize().getWidth() / 25));
        DESCRIPTION_FONT = FontManager.MIST_DESCRIPTION_FONT.deriveFont((float)(getPreferredSize().getWidth() / 50));

        questPanel = new JPanel(new GridLayout(20,1));

        checkTest = new JButton("Verifica");
        checkTest.setFocusable(false);
        checkTest.setBackground(new Color(225, 198,153));

        setup();
        setupListener();
        setVisible(true);
    }

    public static void executeTest()
    {
        LogOutputManager.logOutput("Iniziando Test MIST: ", LogOutputManager.GAMESTATE_COLOR);
        GameManager.changeState(GameManager.GameState.TEST);

        MiniGame lastTest = MiniGame.getLastQuitTest();

        if(lastTest instanceof TestMist)
            MiniGame.setCurrentTest(lastTest);
        else
        {
            MiniGame.setCurrentTest(new TestMist());
        }
    }

    private void setup()
    {
        // settaggio dei jcheckBox per risposte si e no
        for (int i = 0; i < ANSWERS; i++) {
            answerPanel[i] = new JPanel(new GridLayout(1,2));
            answerS[i] = new JCheckBox("SI");
            answerS[i].setFocusable(false);
            answerS[i].setOpaque(false);
            answerS[i].setFont(QUESTION_FONT);
            answerN[i] = new JCheckBox("NO");
            answerN[i].setFocusable(false);
            answerN[i].setOpaque(false);
            answerN[i].setFont(QUESTION_FONT);
            answerPanel[i].add(answerS[i]);
            answerPanel[i].add(answerN[i]);
            answerPanel[i].setOpaque(false);
        }

        // settaggio delle singole label che formeranno una domanda con
        // i 2 checkbox associati il tutto racchiuso in questPanel
        // un JPanell dedicato al wrapping di tali componenti
        for (int j = 0; j < ANSWERS; j++) {
            questWrapper[j] = new JLabel();
            questWrapper[j].setLayout(new FlowLayout());
            //questions[j].setLayout(new BorderLayout());
            //questions[j].add(answerPanel[j],BorderLayout.EAST);
            questions[j].setOpaque(false);
            questions[j].setFont(QUESTION_FONT);
            questWrapper[j].add(questions[j]);
            questWrapper[j].add(answerPanel[j]);
            questWrapper[j].setOpaque(false);
            questPanel.add(questWrapper[j]);
        }

        // composizioni dei componenti swing sul layer centrale
        questPanel.setOpaque(false);
        testPanel.setOpaque(false);
        scrollPane.setOpaque(false);

        // composizione dei componenti swing sul layer in alto
        title.setFont(TITLE_FONT);
        description.setFont(DESCRIPTION_FONT);
        headingPanel.add(title);
        headingPanel.add(description);
        headingPanel.setOpaque(false);
        checkTest.setFont(DESCRIPTION_FONT);

        testPanel.add(questPanel, BorderLayout.CENTER);
        testPanel.add(headingPanel, BorderLayout.NORTH);
        testPanel.add(checkTest, BorderLayout.SOUTH);
    }


    private void setupListener() {

        checkTest.addActionListener((ae) -> checkTestCompleted());
    }

    protected void checkTestCompleted() {
        // elaborazione del test
        boolean completed = false;
        int counter = 1;

        // controlla le risposte date ad ogni domanda del test
        for (int i = 0; i < ANSWERS; i++) {

            // caso in cui siano marcati sia il SI che il NO
            if (answerS[i].isSelected() && answerN[i].isSelected()) {
                completed = false;
                showResult(ERROR);
                break;
                // caso in cui non si sia selezionato nè il SI nè il NO
            } else if (!answerS[i].isSelected()  && !answerN[i].isSelected()) {
                completed = false;
                showResult(ERROR);
                break;
            } else {
                // controllo con un array contenente le rix esatte
                counter++;
            }

            if (counter == ANSWERS) {
                completed = true;
                if(checkTestResult())
                {

                    showResult(VICTORY);
                }

                else
                    showResult(LOST);

            }
        }
    }

    protected boolean checkTestResult() {
        boolean checkSingleAnswer = true;

        for (int i = 0; i < ANSWERS; i++)
        {
            if (answerN[i].isSelected() == CORRECT_ANSWER[i])
            {
                checkSingleAnswer = false;
                break;
            }
        }

        return checkSingleAnswer;
    }
}
