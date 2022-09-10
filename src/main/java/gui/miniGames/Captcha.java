package gui.miniGames;

import general.FontManager;
import general.GameManager;
import general.LogOutputManager;
import general.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  La classe serve a simulare un captcha per il riconoscimento dell'interazione con
 *  un umano.
 */
public class Captcha extends MiniGame
{
    private static final Font FONT = FontManager.CAPTCHA_DESCRIPTION_FONT.deriveFont((float) 40);
    private static final String WIN = "Captcha risolto sei umano!";
    private static final String LOSE = "Accesso negato ai Robot del laboratorio!";

    // dizionario contenente per ogni imagine captcha la stringa di soluzione associata
    private static Map<String, String> captchaMatch;

    // La captchaPanel rappresenta la finestra contenente il captcha
    // e il mainWrapper contiene tutti i componenti swing quali imagine
    // e barra di testo interattiva per l'utente.
    private final JPanel captchaPanel;
    private final JPanel mainWrapper;

    // titolo della finestra captcha
    private final JLabel description;

    // componeti swing per la gestione dell'immagine
    private final JPanel imagePanel;
    private JLabel image;

    // componenti swing per l'interaizione dell'utente con la jdialog
    private final JPanel interactionPanel;
    private final JLabel istruction;
    private JTextField captAnswer;

    // componenti Swing per comunicazione con utente
    /*
    private final JDialog infoWindow;
    private final JLabel infoText;

     */

    // contiene il path dell'imagine captcha scelta in fase di
    // inizializzazione e permette di risalire al valore associato
    // nel dizionario
    private String imgKeyPath;

    public static void executeTest()
    {
        LogOutputManager.logOutput("Iniziando Test CAPTCHA: ", LogOutputManager.GAMESTATE_COLOR);
        GameManager.changeState(GameManager.GameState.TEST);

        MiniGame.setCurrentTest( new Captcha());
    }

    // costruttore
    private Captcha()
    {
        super(WIN, LOSE, "");

        scenarioOnWinPath = "src/main/resources/scenari/piano ALU/dopoCaptcha.xml";
        setCloseOnFail(true);


        // creazione della jdialog con connesione al pannello del mainframde del gioco
        captchaPanel = new JPanel(/*GameManager.getMainFrame(), "CAPTCHA TEXT"*/);
        mainWrapper = new JPanel(new BorderLayout());

        // creazione pannelli principali
        imagePanel = new JPanel(new GridLayout(1,1));
        interactionPanel = new JPanel(new GridLayout(2,1));

        // componeti per interazione con l'utente
        istruction = new JLabel("Digitare la parola nell'immagine.", SwingConstants.LEFT);
        captAnswer = new JTextField();
        // captAnswer.setFocusable(false);

        // creazione titolo del minigioco
        description = new JLabel("Controllo di sicurezza \n  ", SwingConstants.CENTER);


        // creazione e caricamento delle coppie del dizionario e settaggio dell'immagine del captcha
        captchaMatch = initMatch();
        setIcon(captchaMatch.keySet());

        // TODO: da spostare nel factory method
        setup();
        setupListener();
        addDetails();

        setMainPanel(captchaPanel);
        int xOffset = (int) getPreferredSize().getWidth() / 4;
        int yOffset = 0;
        captchaPanel.setBounds(getInsets().left + xOffset, getInsets().top + yOffset, (int) getPreferredSize().getWidth() / 2, (int)getPreferredSize().getHeight());
    }


    /*
    public static Captcha createCaptcha()
    {
        Captcha captcha = new Captcha();

        captcha.setup();
        captcha.setupListener();
        captcha.addDetails();

        return captcha;
    }

     */

    private void setImgKeyPath(String key) {
        imgKeyPath = key;
    }

    private void setIcon(Set<String> path)
    {
        String[] pathString =  path.toArray(new String[0]);
        setImgKeyPath(Util.randomChoice(pathString));


        ImageIcon icon = new ImageIcon(imgKeyPath);
        // Rimpiazzare set immagine

        image = new JLabel(icon, JLabel.CENTER);
    }

    private static Map<String, String> initMatch()
    {
        Map<String, String> solution = new HashMap<>();

        String[] imgPath = {
                "src/main/resources/img/captchaImg/captcha1.png",
                "src/main/resources/img/captchaImg/captcha2.png",
                "src/main/resources/img/captchaImg/captcha3.png",
                "src/main/resources/img/captchaImg/captcha4.png",
                "src/main/resources/img/captchaImg/captcha5.png",
                "src/main/resources/img/captchaImg/captcha6.png",
                "src/main/resources/img/captchaImg/captcha7.png",
                "src/main/resources/img/captchaImg/captcha8.png",
                "src/main/resources/img/captchaImg/captcha9.png"
        };

        String accessToken[] = {
                "gimpy", "5s4ug", "rj28q", "flirc",
                "fh2de", "unrexc", "inquiry", "tegunt"
        };

        for ( int i = 0; i < imgPath.length -1; i++) {
            solution.put(imgPath[i], accessToken[i]);
        }

        return solution;
    }

    private void setup()
    {
        description.setForeground(Color.RED);
        description.setFont(FONT);
        imagePanel.add(image);
        imagePanel.setToolTipText("Risolvi il captcha.");
        istruction.setBackground(new Color(156,156,156));
        istruction.setFont(FONT);

        // new Color(3, 187,133) blue-verde-smeraldo
        // new Color(0, 143,57) verde really nice!
        // 255, 165,0 orange
        // new Color(173, 255,47) giallo limone cool!!
        captAnswer.setForeground(new Color(173, 255,47));
        captAnswer.setBackground(new Color(156,156,156));
        captAnswer.setFont(FONT);
        istruction.setToolTipText("digita i caratteri osservati e premi invio nella barra sottostante.");
        captAnswer.setToolTipText("premi invio dopo aver digitato i caratteri.");
        interactionPanel.setBackground(new Color(156,156,156));
        interactionPanel.add(istruction);
        interactionPanel.add(captAnswer);

        // composizione dei componenti swing a matriosca
        mainWrapper.setBackground(new Color(156,156,156));
        mainWrapper.add(description, BorderLayout.NORTH);
        mainWrapper.add(imagePanel, BorderLayout.CENTER );
        mainWrapper.add(interactionPanel, BorderLayout.SOUTH);

        // wrapper finale per ottimizzare la visualizzazione
        // e gerarchia dei pannelli
        //JScrollPane scrollWrapper = new JScrollPane(mainWrapper);
        captchaPanel.add(mainWrapper, BorderLayout.CENTER);
    }

    final void addDetails()
    {
        captchaPanel.setPreferredSize(new Dimension(700, 700));
        // TODO: da reimpostare setModal per frezzare le sottostanti finestre aperte
        // captchaPanel.setModal(true);

        /*
        captchaPanel.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        captchaPanel.setResizable(false);

         */

        // imposta come "figlio"
        // captchaPanel.setLocationRelativeTo(GameManager.getMainFrame());
        // (new Color 16, 44, 84) blue navy dark color
        imagePanel.setBackground((new Color (16, 44, 84)));
        captchaPanel.setBackground((new Color( 16, 44, 84)));
        captchaPanel.setBackground((new Color (16, 44, 84)));
        captchaPanel.setVisible(true);
    }

    private void setupListener()
    {
        captAnswer.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {

                    GameManager.getMainFrame().requestFocus();

                    // gestione della risposta data dall'utente
                    if (checkAnswer(captAnswer.getText()))
                    {
                        showResult(WIN);
                    }
                    else
                        showResult(LOSE);

                    // captAnswer.setText("");
                }
            }
        });
    }

    private boolean checkAnswer(String answer)
    {
        boolean passed;

        passed = answer.equalsIgnoreCase(captchaMatch.get(imgKeyPath));

        return passed;
    }
}

