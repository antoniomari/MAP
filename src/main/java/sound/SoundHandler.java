package sound;

import GUI.gamestate.GameState;
import general.GameException;
import general.GameManager;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SoundHandler
{
    public static final String PICKUP_SOUND_PATH = "src/main/resources/audio/effetti/pickupItem.wav";
    public static final String SCROLL_BAR_PATH = "src/main/resources/audio/effetti/scroll.wav";
    public static final String EMOJI_SOUND_PATH = "src/main/resources/audio/effetti/emojiSound.wav";
    private static Clip currentMusic;
    private static String currentMusicPath;

    public enum Mode
    {
        MUSIC, SOUND, SCENARIO_SOUND
    }

    // Todo: bufferizzazione
    public static void playWav(String wavPath, Mode mode)
    {
        Objects.requireNonNull(wavPath);
        Objects.requireNonNull(mode);

        switch (mode)
        {
            case MUSIC:
                insertCD(wavPath);
                break;
            case SOUND:
                playSound(wavPath);
                break;
            case SCENARIO_SOUND:
                playScenarioSound(wavPath);
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

    private static void playScenarioSound(String wavPath)
    {
        Clip scenarioSoundClip = openClip(wavPath);

        scenarioSoundClip.addLineListener(event ->
        {
            if (event.getType() == LineEvent.Type.STOP)
            {
                scenarioSoundClip.close();
                GameManager.continueScenario();
            }
        });

        scenarioSoundClip.start();
        GameState.changeState(GameState.State.SCENARIO_SOUND);
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
