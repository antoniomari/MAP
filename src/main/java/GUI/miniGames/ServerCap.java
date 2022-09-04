package GUI.miniGames;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerCap
{
   private static ImageIcon imageIcon = new ImageIcon("src/com/company/captchaImg/captcha2.png");

    public static BufferedImage loadSpriteSheet(String spriteSheetPath)
    {
        try
        {
            // spriteSheetPath = "src/main/resources" + spriteSheetPath;
            return ImageIO.read((new File(spriteSheetPath)).toURI().toURL());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Impossibile caricare " + spriteSheetPath);
        }
    }

    public static void main(String args[]) throws IOException
    {
        ServerCap serverCap = new ServerCap();
        BufferedImage bfi = loadSpriteSheet("src/com/company/captchaImg/captcha2.png");

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

                    // ImageIO it's a particular class that allow to decode and to encode image
                    ImageIO.write(bfi, "png", bufferedOutputStream);

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
