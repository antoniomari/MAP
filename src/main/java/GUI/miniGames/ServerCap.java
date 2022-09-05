package GUI.miniGames;

import general.Util;
import graphics.SpriteManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServerCap
{
    private static BufferedImage bfi;
    // dizionario contenente per ogni imagine captcha la stringa di soluzione associata
    private static Map<String, String> captchaMatch;

    // contiene il path dell'imagine captcha scelta in fase di
    // inizializzazione e permette di risalire al valore associato
    // nel dizionario
    private static String imgKeyPath;

    public ServerCap()
    {
        captchaMatch = initMacth();
        setIcon(captchaMatch.keySet());
    }

    private void setImgKeyPath(String key)
    {
        imgKeyPath = key;
    }

    private void setIcon(Set<String> path)
    {
        String[] pathString =  path.toArray(new String[0]);
        setImgKeyPath(Util.randomChoice(pathString));

        bfi = SpriteManager.loadSpriteSheet(imgKeyPath);
    }

    private static Map<String, String> initMacth()
    {
        Map<String, String> solution = new HashMap<>();

        String[] imgPath = {
                "/img/captchaImg/captcha1.png",
                "/img/captchaImg/captcha2.png",
                "/img/captchaImg/captcha3.png",
                "/img/captchaImg/captcha4.png",
                "/img/captchaImg/captcha5.png",
                "/img/captchaImg/captcha6.png",
                "/img/captchaImg/captcha7.png",
                "/img/captchaImg/captcha8.png",
                "/img/captchaImg/captcha9.png"
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

    private String checkAnswer(String answer)
    {
        String result;

        if (answer.equalsIgnoreCase(captchaMatch.get(imgKeyPath)))
        {
            result = "passed";
        }
        else
            result ="failed";

        return result;
    }


    public static void main(String args[]) throws IOException
    {
        ServerCap serverCap = new ServerCap();

        // fissiamo il numero di porta per la connessione
        int port = 1234;

        // creiamo l'oggetto SocketServer
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server avviato sulla porta "+ port);

            // questo ciclo che permane vero è il listenr del server posto sulla porta
            while (true) {

                try (Socket clientsocket = serverSocket.accept();
                      BufferedOutputStream bufferedOutputStream = new
                              BufferedOutputStream(clientsocket.getOutputStream());) {

                    System.out.println("connesso al client captcha");
                    // ImageIO it's a particular class that allow to decode and to encode image
                    ImageIO.write(bfi, "png", bufferedOutputStream);

                    BufferedReader bufferReader = new BufferedReader(new
                                                  InputStreamReader(clientsocket.getInputStream()));

                    String response = serverCap.checkAnswer(bufferReader.readLine());

                    PrintWriter printWriter = new PrintWriter(clientsocket.getOutputStream());
                    printWriter.println(response);

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
