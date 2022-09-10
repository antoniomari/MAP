package sound;

import general.GameException;
import general.GameManager;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SoundHandler
{
    // PATH DI SUONI BUFFERIZZATI
    public static final String PICKUP_SOUND_PATH = "src/main/resources/audio/effetti/pickupItem.wav";
    public static final String SCROLL_BAR_PATH = "src/main/resources/audio/effetti/scroll.wav";
    public static final String EMOJI_SOUND_PATH = "src/main/resources/audio/effetti/emojiSound.wav";
    public static final String CLICK_SOUND_PATH = "src/main/resources/audio/effetti/bottone selezione mouse.wav";

    /** Clip utilizzata per la riproduzione della musica di sottofondo delle stanze. */
    private static final Clip currentMusic;
    /** Path della musica di sottofondo delle stanze attualmente in esecuzione. */
    private static String currentMusicPath;

    /** Dizionario path->clip per recuperare le clip relative ai suoni bufferizzati. */
    private static final Map<String, Clip> commonSoundsMap;
    /** Clip utilizzata per la riproduzione di suoni riprodotti tramite scenari. */
    private static final Clip scenarioSound;

    /**
     * Modalità di riproduzione del file .wav
     */
    public enum Mode
    {
        /** Modalità utilizzata per le musiche di sottofondo delle stanze. */
        MUSIC,
        /** Modalità utilizzata per i suoni generati da input del giocatore. */
        SOUND,
        /** Modalità utilizzata per i suoni riprodotti tramite scenari. */
        SCENARIO_SOUND
    }

    static
    {
        // apri clip per musica e sound
        try
        {
            currentMusic = AudioSystem.getClip();

            commonSoundsMap = new HashMap<>();
            commonSoundsMap.put(CLICK_SOUND_PATH, AudioSystem.getClip());
            commonSoundsMap.put(SCROLL_BAR_PATH, AudioSystem.getClip());
            commonSoundsMap.put(PICKUP_SOUND_PATH, AudioSystem.getClip());
            commonSoundsMap.put(EMOJI_SOUND_PATH, AudioSystem.getClip());

            for(String key : commonSoundsMap.keySet())
                openWav(key, commonSoundsMap.get(key));

            scenarioSound = AudioSystem.getClip();
            scenarioSound.addLineListener(event ->
            {
                if (event.getType() == LineEvent.Type.STOP)
                    GameManager.continueScenario();
            });
        }
        catch(LineUnavailableException e)
        {
            e.printStackTrace();
            throw new GameException("Errore nell'apertura clip audio");
        }

    }


    /**
     * Riproduce un file wav.
     *
     * @param wavPath path del file wav da riprodurre
     * @param mode modalità con cui riprodurre il file wav
     */
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
     * in riproduzione (diverso da quello che si vuole riprodurre),
     * lo sostituisce.
     *
     * @param wavPath path dell'audio da riprodurre
     */
    private static void insertCD(String wavPath)
    {

        // se il path è lo stesso continua la riproduzione attuale
        if(currentMusicPath != null && currentMusicPath.equals(wavPath))
            return;


        if(currentMusic.isRunning())
            currentMusic.stop();
        currentMusicPath = wavPath;
        currentMusic.close();

        openWav(wavPath, currentMusic);
        currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Riproduce l'audio aperto tramite un file wav,
     * il cui path dev'essere stato registrato tra
     * i path degli audio bufferizzati.
     *
     * @param wavPath path del file .wav da riprodurre
     */
    private static void playSound(String wavPath)
    {
        // caso suono bufferizzato
        if(commonSoundsMap.containsKey(wavPath))
        {
            Clip commonSoundClip = commonSoundsMap.get(wavPath);

            if(commonSoundClip.isRunning())
            {
                commonSoundClip.stop();
            }

            commonSoundClip.flush();
            commonSoundClip.setFramePosition(0);

            commonSoundClip.start();
        }
        else
        {
            throw new GameException("Suono non presente tra quelli registrati");
        }
    }

    /**
     * Riproduce l'audio aperto tramite file wav
     * utilizzando la clip {@link SoundHandler#scenarioSound}.
     *
     * Chiude inizialmente la clip, per poi riaprirla con il nuovo
     * file wav.
     *
     * @param wavPath path del file .wav da riprodurre
     */
    private static void playScenarioSound(String wavPath)
    {
        scenarioSound.close();
        openWav(wavPath, scenarioSound);

        scenarioSound.start();
        GameManager.changeState(GameManager.GameState.SCENARIO_SOUND);
    }

    /**
     * Apre file wav sulla clip specificata.
     *
     * @param wavPath path del file .wav da aprire
     * @param targetClip clip su cui caricare l'audio
     */
    private static void openWav(String wavPath, Clip targetClip)
    {
        System.out.println("Sto aprendo: " + wavPath);
        try
        {
            File file = new File(wavPath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            targetClip.open(audioStream);
        }
        catch(IOException | LineUnavailableException | UnsupportedAudioFileException e)
        {
            e.printStackTrace();
            throw new GameException("Errore nell'apertura audio [" + wavPath + "]");
        }

    }
}
