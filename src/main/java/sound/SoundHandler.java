package sound;

import general.GameException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class SoundHandler
{
    private static MusicThread musicThread;

    public static class MusicThread extends Thread
    {
        private AudioInputStream audioStream;
        private Clip clip;
        private String mode;

        public MusicThread(AudioInputStream audioStream, Clip clip, String mode)
        {
            this.mode = mode;
            this.audioStream = audioStream;
            this.clip = clip;
        }

        @Override
        public void run()
        {
            try
            {
                clip.open(audioStream);
                if(mode.equals("music"))
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                else if (mode.equals("sound"))
                    clip.start();
                else
                    throw new GameException("Modalità audio non esistente");

            }
            catch(IOException | LineUnavailableException e)
            {
                // do nothing TODO: aggiustare
            }
        }
    }

    public static void playWav(String wavPath, String mode)
    {
        try
        {
            File file = new File(wavPath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();

            musicThread = new MusicThread(audioStream, clip, mode);
            musicThread.start();
        }
        catch(UnsupportedAudioFileException | IOException | LineUnavailableException e)
        {
            // TODO: fai qualcosa
        }
    }
}
