package sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class SoundHandler
{
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException
    {
        Scanner scanner = new Scanner(System.in);

        File file = new File("src/main/resources/audio/musica/Bagno1.wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);

        String tasto = scanner.next();
        tasto = tasto.toUpperCase();

        while (!tasto.equals("Q")) {
            System.out.println("P = Play, S = Stop, R = Reset, Q = Quit");
            System.out.print("Clicca un tasto: ");

            tasto = scanner.next();
            tasto = tasto.toUpperCase();

            switch (tasto){
                case ("P"): clip.start();
                break;
                case ("S"): clip.stop();
                break;
                case ("R"): clip.setMicrosecondPosition(0);
                break;
                case ("Q"): clip.close();
                break;
                default: System.out.println("Tasto non valido.");
            }
        }
        clip.start();
        clip.loop(0);
    }
}
