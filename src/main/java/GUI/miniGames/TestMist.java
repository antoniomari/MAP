package GUI.miniGames;

/**
 *  Mini gioco del test psicologico versione finale da integrare sul progetto di MAP
 *  serve definire un meccanismo di avvio del mini gioco
 *
 */
// TODO: possibilità di applicare factory design pattern

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TestMist
{
    private JFrame mainFrame;

    //JLabel e ImageIcon per creazione background
    private JLabel backgroundLabel;
    private ImageIcon backgroundImageIcon;
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
    private JPanel answerPanel[] = new JPanel[ANSWERS];
    private JCheckBox answerS[] = new JCheckBox[ANSWERS];
    private JCheckBox answerN[] = new JCheckBox[ANSWERS];

    private Font fontQuestion;
    private Font fontTitle;
    private Font fontDescription;

    private JButton checkTest;

    // Utilizzati per la comunicazione di messaggi con l'utente
    private JDialog infoPlayer;
    private JLabel dialogLabel;

    // questWrapper e una JLabel con funzione di wrapper attorno ad un elemento questions
    // e altri due swing components rispettivamente una answerS e un answerN che sono
    // JCheckBox i tre componenti assieme formano una singola Label del array questWrapper
    private JLabel questWrapper[] = new JLabel[ANSWERS];
    private JLabel questions[] = { new JLabel("1. Il Blackjack è un gioco di carte?"),
            new JLabel("2. Babbo Natale consegna i regali a Pasqua?"),
            new JLabel("3. Il legno più duro del diamante? "),
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
            new JLabel("14. Le balene sono delle specie di pesci?"),
            new JLabel("15. La notte è più buia del giorno?"),
            new JLabel("16. La guerra è meglio della pace?"),
            new JLabel("17. Un secondo è più breve di un minuto?"),
            new JLabel("18. C'è un numero che è il più grande?"),
            new JLabel("19. Si necessita di una patente per avere dei figli?"),
            new JLabel("20. La gravidanza è contagiosa?"),
    };

    private final boolean CORRECT_ANSWER[] = { true, false, false, true, false,
            true, true, false, false, true,
            false, false, true, false, true,
            false, true, false, false, false };

    private static final int ANSWERS = 20;
    private static final String VICTORY = "Test passato con successo!";
    private static final String LOST = "Test errato! Sei psicologicamente instabile.";
    private static final String ERROR = "ERRORE:  " +
            "controlla di aver dato una sola risposta per ogni domanda.";


    public TestMist() {
        mainFrame = new JFrame();

        backgroundImageIcon = new ImageIcon("src/main/resources/img/ImageMiniGames/sfondofoglio.jpg");
        backgroundLabel = new JLabel(backgroundImageIcon);

        headingPanel = new JPanel(new GridLayout(2,1));;
        title = new JLabel("Minimum intelligence signal text", SwingConstants.CENTER);
        description = new JLabel("Test psicologico intelletivo somministrato dalla Dott.ssa Gastani Frinzi.",
                SwingConstants.CENTER);

        testPanel = new JPanel(new BorderLayout());
        questPanel = new JPanel(new GridLayout(20,1));

        checkTest = new JButton("Verifica");

        fontQuestion = new Font("Baskerville Old Face", Font.ITALIC,18);
        fontDescription = new Font("Bookman Old Style", Font.PLAIN, 20);
        fontTitle = new Font("Castellar", Font.BOLD | Font.ITALIC, 34);

        infoPlayer = new JDialog();
        dialogLabel = new JLabel("", SwingConstants.CENTER);
        scrollPane = new JScrollPane(backgroundLabel);

        // TODO: considerare implementazione design pattern factory
        // queste 3 funzioni simulano una factory methods e servono
        // per fornire un oggetto che sia in un determinato stato prima
        // del suo utilizzo
        setup();
        setupListener();
        setDetails();
    }

    private void setup() {
        // settaggio dei jcheckBox per risposte si e no
        for (int i = 0; i < ANSWERS; i++) {
            answerPanel[i] = new JPanel(new GridLayout(1,2));
            answerS[i] = new JCheckBox("SI");
            answerS[i].setOpaque(false);
            answerS[i].setFont(fontQuestion);
            answerN[i] = new JCheckBox("NO");
            answerN[i].setOpaque(false);
            answerN[i].setFont(fontQuestion);
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
            questions[j].setFont(fontQuestion);
            questWrapper[j].add(questions[j]);
            questWrapper[j].add(answerPanel[j]);
            questWrapper[j].setOpaque(false);
            questPanel.add(questWrapper[j]);
        }

        // composizioni dei componenti swing sul layer centrale
        questPanel.setOpaque(false);
        testPanel.add(questPanel, BorderLayout.CENTER);
        testPanel.setOpaque(false);
        scrollPane.setOpaque(false);

        // composizione dei componenti swing sul layer in alto
        title.setFont(fontTitle);
        description.setFont(fontDescription);
        headingPanel.add(title);
        headingPanel.add(description);
        headingPanel.setOpaque(false);
        testPanel.add(headingPanel, BorderLayout.NORTH);

        // composizione del testPanel all'interno del background
        // fornito dall'imagine contenuta nella JLabel
        backgroundLabel.setLayout(new BorderLayout());
        backgroundLabel.add(testPanel, BorderLayout.CENTER);
        checkTest.setFont(fontDescription);
        backgroundLabel.add(checkTest, BorderLayout.SOUTH);

        // aggiunta di tutti i componenti nel mainframe
        mainFrame.add(scrollPane, BorderLayout.CENTER);
    }

    private void setDetails() {
        checkTest.setBackground(new Color(225, 198,153));
        //mainFrame.pack();
        mainFrame.setSize(1000, 600);
        mainFrame.setMaximumSize(new Dimension(backgroundImageIcon.getIconWidth(),
                backgroundImageIcon.getIconHeight()));
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    private void setupListener() {
        // Listener per il bottone di verifica test
        checkTest.addActionListener((ae) -> {
            checkTestCompleted();;
        });

        // listener sulla finestra della jdialog
        // per la comunicazione con l'utente
        infoPlayer.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent arg0)
            {
                System.out.println("Window closing");
                mainFrame.dispose();
            }
        });
    }

    private void checkTestCompleted() {
        // elaborazione del test
        boolean completed = false;
        int counter = 1;

        // controlla le risposte date ad ogni domanda del test
        for (int i = 0; i < ANSWERS; i++) {

            // caso in cui siano marcati sia il SI che il NO
            if (answerS[i].isSelected() && answerN[i].isSelected()) {
                completed = false;
                //System.out.println("Sono marcati sia il si che il no");
                showResult(ERROR);
                break;
                // caso in cui non si sia selezionato nè il SI nè il NO
            } else if (!answerS[i].isSelected()  && !answerN[i].isSelected()) {
                completed = false;
                //System.out.println("Non sono marcati nè il si nè il no");
                showResult(ERROR);
                break;
            } else {
                // controllo con un array contenente le rix esatte
                counter++;
            }

            if (counter == ANSWERS) {
                completed = true;
                //System.out.println("si è completato il test");
                checkTestResult();
            }
        }
    }

    private void checkTestResult() {
        boolean checkSingleAnswer = true;

        for (int i = 0; i < ANSWERS; i++) {
            //System.out.println(answerN[i].isSelected() + " - " + CORRECT_ANSWER[i]);
            if (answerN[i].isSelected()==CORRECT_ANSWER[i]) {
                checkSingleAnswer = false;
                break;
            }
        }
        if (checkSingleAnswer) {
            //System.out.println("Test corretto");
            showResult(VICTORY);
        } else
        {
            //System.out.println("Test errato");
            showResult(LOST);
        }
    }

    private void showResult(String msg) {
        dialogLabel.setText(msg);
        infoPlayer.add(dialogLabel);
        infoPlayer.setSize(450, 100);
        infoPlayer.setLocationRelativeTo(null);
        infoPlayer.setVisible(true);
        infoPlayer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mainFrame.setVisible(false);
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new TestMist());
    }
}

