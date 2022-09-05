package GUI.miniGames;

import GUI.gamestate.GameState;
import general.GameManager;
import general.LogOutputManager;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

/**
 *  La classe serve a simulare un captcha per il riconoscimento dell'interazione con
 *  un umano. In particolare tale classe implementerà un client che richiede ad un
 *  server la ricezione di un test captcha.
 */
public class ClientCap extends MiniGame
{
    private static final Font FONT = new Font("Agency FB", Font.BOLD , 40);
    private static final String WIN = "Captcha risolto sei umano!";
    private static final String LOSE = "Accesso negato ai Robot del laboratorio!";

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

    private boolean captchaResult = false;

    // costruttore
    private ClientCap()
    {
        super(WIN, LOSE, "");

        startConnection();

        scenarioOnWinPath = "src/main/resources/scenari/piano ALU/provaAscensore.xml";
        setCloseOnFail(true);

        // creazione della jdialog con connesione al pannello del mainframde del gioco
        captchaPanel = new JPanel(/*GameManager.getMainFrame(), "CAPTCHA TEXT"*/);
        mainWrapper = new JPanel(new BorderLayout());

        // creazione pannelli principali
        imagePanel = new JPanel(new GridLayout(1,1));
        interactionPanel = new JPanel(new GridLayout(2,1));

        // componeti per interazione con l'utente
        istruction = new JLabel("Digitare la parola visualizzata nell'immagine.", SwingConstants.LEFT);
        captAnswer = new JTextField();
        // captAnswer.setFocusable(false);

        // creazione titolo del minigioco
        description = new JLabel("Controllo di sicurezza \n  ", SwingConstants.CENTER);

        // TODO: da spostare nel factory method
        setup();
        //setupListener();
        addDetails();

        add(captchaPanel, TEST_LAYER);
        captchaPanel.setBounds(getInsets().left, getInsets().top,
                (int) captchaPanel.getPreferredSize().getWidth(),
                (int) captchaPanel.getPreferredSize().getHeight());

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

        // (new Color 16, 44, 84) blue navy dark color
        imagePanel.setBackground((new Color (16, 44, 84)));
        captchaPanel.setBackground((new Color( 16, 44, 84)));
        captchaPanel.setBackground((new Color (16, 44, 84)));
        captchaPanel.setVisible(true);
    }

/*
    // inizializzazione dei listener per gli eventi generati dal captcha
    private void setupListener()
    {
        captAnswer.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    captAnswer.getText();
                    System.out.println(captAnswer.getText());

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
*/
    // verifica del input inserito per il captcha
    private boolean checkAnswer(String answer)
    {
        boolean passed;

       if (answer.equalsIgnoreCase("passed"))
       {
           passed = true;
       }
       else
           passed = false;

        return passed;
    }

    public void setImage(JLabel img) {
        this.image = img;
    }

    // executor del test capcha
    public static void executeTest()
    {
        LogOutputManager.logOutput("Iniziando Test CAPTCHA: ", LogOutputManager.GAMESTATE_COLOR);
        GameState.changeState(GameState.State.TEST);

        MiniGame.setCurrentTest( new ClientCap());
    }

    public void startConnection()
    {
        try (Socket socketDelServer = new Socket("localhost", 1234);
              BufferedInputStream bufferedInputStream = new
                             BufferedInputStream(socketDelServer.getInputStream())) {
           Thread.sleep(2000);
            BufferedImage bufferedImage = ImageIO.read(bufferedInputStream);

            setImage(new JLabel(new ImageIcon(bufferedImage)));

            captAnswer.addKeyListener(new KeyAdapter()
            {
                @Override
                public void keyPressed(KeyEvent e)
                {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    {
                        try
                        {
                            // manda la stringa scritta dall'utente al server per il controllo
                            PrintWriter printWriter = new PrintWriter(socketDelServer.getOutputStream(), true);
                            printWriter.println(captAnswer.getText());

                            BufferedReader bufferedReader = new BufferedReader(new
                                                          InputStreamReader(socketDelServer.getInputStream()));

                            GameManager.getMainFrame().requestFocus();

                            // gestione della risposta data dall'utente
                            if (checkAnswer(bufferedReader.readLine()))
                            {
                                showResult(WIN);
                            }
                            else
                            {
                                showResult(LOSE);
                            }
                            printWriter.close();
                            bufferedReader.close();
                            // captAnswer.setText("");
                        }catch (IOException ioe)
                        {
                            ioe.printStackTrace();
                        }
                    }
                }
            });

            //jLabelText.setText("image received");

        } catch (ConnectException ce) {
            System.err.println("Non riesco a connettermi al server " + ce.getMessage());
        } catch (IOException ioe) {
            System.err.println("Problemi... " + ioe.getMessage());
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

/*
        try
        {
            ServerSocket serverSocket = new ServerSocket(1234);

            Socket socket = serverSocket.accept();

            InputStream inputStream = socket.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            BufferedImage bufferedImage = ImageIO.read(bufferedInputStream);

            bufferedInputStream.close();
            socket.close();

            JLabel jLabelPic = new JLabel(new ImageIcon(bufferedImage));
            jLabelText.setText("image received");

            jFrame.add(jLabelPic, BorderLayout.CENTER);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

 */
