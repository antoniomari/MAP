package sound;

import general.GameException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SoundHandler
{
    public static final String PICKUP_SOUND_PATH = "src/main/resources/audio/effetti/pickupItem.wav";
    private static Clip currentMusic;
    private static String currentMusicPath;

    public static void playWav(String wavPath, String mode)
    {
        Objects.requireNonNull(wavPath);
        Objects.requireNonNull(mode);

        switch (mode)
        {
            case "music":
                insertCD(wavPath);
                break;
            case "sound":
                playSound(wavPath);
                break;
            case "scenarioSound":
                // TODO: implementare
                break;
            default:
                throw new GameException("Modalità audio non esistente");
        }
    }


    /**
     * Riproduce in loop audio di sottofondo. Se uno è attualmente
     * in riproduzione, lo sostituisce.
     *
     * @param wavPath path dell'audio da riprodurre
     */
    private static void insertCD(String wavPath)
    {

        // se il path è lo stesso continua la riproduzione attuale
        if(currentMusicPath!= null && currentMusicPath.equals(wavPath))
            return;

        if(currentMusic != null)
        {
            currentMusic.stop();
        }


        currentMusicPath = wavPath;
        currentMusic = openClip(wavPath);
        currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }

    private static void playSound(String wavPath)
    {
        Clip soundClip = openClip(wavPath);
        soundClip.start();
    }

    private static Clip openClip(String wavPath)
    {
        System.out.println("Sto aprendo: " + wavPath);
        try
        {
            File file = new File(wavPath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();

            clip.open(audioStream);
            return clip;
        }
        catch(IOException | LineUnavailableException | UnsupportedAudioFileException e)
        {
            e.printStackTrace();
            throw new GameException("Errore nell'apertura audio [" + wavPath + "]");
        }

    }

}
