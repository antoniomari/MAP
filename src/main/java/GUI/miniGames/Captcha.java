package GUI.miniGames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  La classe serve a simulare un captcha per il riconoscimento dell'interazione con
 *  un umano.
 */
public class Captcha extends MiniGame
{
    private static final Font FONT = new Font("Agency FB", Font.BOLD , 40);
    private final String WIN = "Captcha risolto sei umano!";
    private final String LOSE = "Accesso negato ai Robot del laboratorio!";

    // dizionario contenente per ogni imagine captcha la stringa di soluzione associata
    private static Map<String, String> captchaMatch;

    // La captchaDialog rappresenta la finestra contenente il captcha
    // e il mainWrapper contiene tutti i componenti swing quali imagine
    // e barra di testo interattiva per l'utente.
    private final JDialog captchaDialog;
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
    private final JDialog infoWindow;
    private final JLabel infoText;

    // contiene il path dell'imagine captcha scelta in fase di
    // inizializzazione e permette di risalire al valore associato
    // nel dizionario
    private String imgKeyPath;

    public static void executeTestCaptcha()
    {

    }

    // costruttore
    private Captcha() {

        // creazione della jdialog con connesione al pannello del mainframde del gioco
        captchaDialog = new JDialog(/*GameManager.getMainFrame(), "CAPTCHA TEXT"*/);
        mainWrapper = new JPanel(new BorderLayout());

        // creazione pannelli principali
        imagePanel = new JPanel(new GridLayout(1,1));
        interactionPanel = new JPanel(new GridLayout(2,1));

        // componeti per interazione con l'utente
        istruction = new JLabel("Digitare la parola visualizzata nell'immagine.", SwingConstants.LEFT);
        captAnswer = new JTextField();

        // creazione titolo del minigioco
        description = new JLabel("Controllo di sicurezza \n  ", SwingConstants.CENTER);

        // creazione componenti interazione con utente in risposta
        // alle azioni compiute dall'utente servono a dare risposta
        // di quello che sta accadendo
        infoWindow = new JDialog(captchaDialog);
        infoWindow.setModal(true);
        infoText = new JLabel("", SwingConstants.CENTER);

        // creazione e caricamento del coppie del dizionario e settagio dell'imagine del captcha
        captchaMatch = initMacth();
        setIcon(captchaMatch.keySet());

        // TODO: da spostare nel factory method
        setup();
        setupListener();
        addDetails();
    }


    public static Captcha createCaptcha()
    {
        Captcha captcha = new Captcha();

        captcha.setup();
        captcha.setupListener();
        captcha.addDetails();

        return captcha;
    }

    private void setImgKeyPath(String key) {
        imgKeyPath = key;
    }

    private void setIcon(Set<String> path)
    {
        String[] pathString =  path.toArray(new String[0]);

        // int num = (int) (Math.random()*10)
        // int max = 9;
        // int min = 0;
        // avvolte da eccezione capire perchè
        // (int) (Math.random()*(max-min)) + min]
        setImgKeyPath(pathString[(int) (Math.random()*10)]);

        ImageIcon icon = new ImageIcon(imgKeyPath);
        // Rimpiazzare set immagine

        image = new JLabel(icon, JLabel.CENTER);
        System.out.println(image.getIcon().toString());
    }

    private static Map<String, String> initMacth()
    {
        Map<String, String> solution = new HashMap<>();

        String imgPath[] = {
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
        JScrollPane scrollWrapper = new JScrollPane(mainWrapper);
        captchaDialog.add(scrollWrapper, BorderLayout.CENTER);
    }

    final void addDetails()
    {
        captchaDialog.setSize(700, 700);
        // TODO: da reimpostare setModal per frezzare le sottostanti finestre aperte
        // captchaDialog.setModal(true);
        captchaDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        captchaDialog.setResizable(false);

        // imposta come "figlio"
        // captchaDialog.setLocationRelativeTo(GameManager.getMainFrame());
        // (new Color 16, 44, 84) blue navy dark color
        imagePanel.setBackground((new Color (16, 44, 84)));
        captchaDialog.setBackground((new Color( 16, 44, 84)));
        captchaDialog.getContentPane().setBackground((new Color (16, 44, 84)));
        captchaDialog.setVisible(true);
    }

    private void setupListener()
    {
        captAnswer.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    System.out.println(captAnswer.getText());

                    // gestione della risposta data dall'utente
                    if (checkAnswer(captAnswer.getText()))
                    {
                        showInfoResult(WIN);
                    }
                    else
                        showInfoResult(LOSE);

                    // captAnswer.setText("");
                }
            }
        });

        // listener sulla finestra della jdialog
        // per la comunicazione con l'utente
        infoWindow.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent arg0)
            {
                // System.out.println("Window closing");
                captchaDialog.dispose();
            }
        });
    }

    private boolean checkAnswer(String answer)
    {
        boolean passed;

        passed = answer.equalsIgnoreCase(captchaMatch.get(imgKeyPath));

        return passed;
    }

    private void showInfoResult(String msg)
    {
        infoText.setText(msg);
        infoWindow.add(infoText);
        infoWindow.setSize(450, 100);
        infoWindow.setLocationRelativeTo(captchaDialog);
        infoWindow.setVisible(true);
        infoWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        captchaDialog.setVisible(false);
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new Captcha());
    }
}

