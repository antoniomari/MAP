package GUI.miniGames;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class ClientCap
{
    public static void main(String args[])
    {
        JFrame jFrame = new JFrame("Client");
        jFrame.setSize(600, 600);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel jLabelText = new JLabel("Waiting for image from server...");
        jFrame.add(jLabelText, BorderLayout.SOUTH);

        jFrame.setVisible(true);

        try (Socket socketDelServer = new Socket("localhost", 1234);
              BufferedInputStream bufferedInputStream = new
                             BufferedInputStream(socketDelServer.getInputStream())) {
           Thread.sleep(2000);
            BufferedImage bufferedImage = ImageIO.read(bufferedInputStream);

            JLabel jLabelPic = new JLabel(new ImageIcon(bufferedImage));
            jLabelText.setText("image received");

            jFrame.add(jLabelPic, BorderLayout.CENTER);

            //System.out.println(br.readLine());
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
