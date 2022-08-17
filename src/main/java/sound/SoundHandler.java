package sound;

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

        public MusicThread(AudioInputStream audioStream, Clip clip)
        {
            this.audioStream = audioStream;
            this.clip = clip;
        }

        @Override
        public void run()
        {
            try
            {
                clip.open(audioStream);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                // clip.start();

            }
            catch(IOException | LineUnavailableException e)
            {
                // do nothing TODO: aggiustare
            }
        }
    }

    public static void playWav(String wavPath)
    {
        try
        {
            File file = new File(wavPath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();

            musicThread = new MusicThread(audioStream, clip);
            musicThread.start();
        }
        catch(UnsupportedAudioFileException | IOException | LineUnavailableException e)
        {
            // TODO: fai qualcosa
        }
    }
}
