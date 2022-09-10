package animation;

import general.GameException;
import general.ScenarioMethod;
import gui.AbsPosition;
import gui.GameScreenManager;
import entity.rooms.BlockPosition;
import general.GameManager;
import jdk.jfr.internal.OldObjectSample;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Classe che rappresenta un'animazione di movimento,
 * che sposta gradualmente una JLabel dalla posizione
 * iniziale a quella finale. Viene alternata la
 * visualizzazione dei fotogrammi (loop finché l'animazione non termina).
 */
public class MovingAnimation extends Animation
{
    /** Numero di aggiornamenti della JLabel al secondo (cambi di posizione). */
    private static final int FPS = 60;

    /** Millisecondi di delay tra cambi di posizione, CALCOLATI AUTOMATICAMENTE. */
    private int delayMilliseconds;
    /** Distanza (misurata in blocchi) tra la posiziona iniziale e la finale. */
    private final double blockDistance;
    /** Corrispettivo della posizione iniziale, misurato in pixel. */
    private final AbsPosition initialCoord;
    /** Corrispettivo della posizione finale, misurato in pixel. */
    private final AbsPosition finalCoord;
    /** Lista che contiene tutte le posizioni da assumere */
    private List<AbsPosition> positionsList;
    /** Velocità a cui l'animazione prosegue. */
    private final double speed;
    /**
     * Indice per restituire l'icona successiva nelle chiamate a {@link MovingAnimation#getNextIcon()}.
     * Inizializzato ad {@code 1} perché in posizione 0 la lista di frames richiesta deve contenere
     * il frame da impostare una volta finita l'esecuzione dell'animazione. */
    private int currentIndex = 1;


    /**
     * Crea una MovingAnimation.
     *
     * @param label JLabel da animare
     * @param initialPos posizione iniziale della JLabel per il calcolo della traiettoria
     * @param finalPos posizione finale della JLabel per il calcolo della traiettoria
     * @param millisecondWaitEnd millisecondi da attendere alla fine dell'animazione
     * @param frames lista d'immagini che costituiscono i fotogrammi dell'animazione
     */
    public MovingAnimation(JLabel label, BlockPosition initialPos,
                           BlockPosition finalPos, int millisecondWaitEnd,
                           List<Image> frames)
    {
        // in futuro: dare la possibilità di personalizzare la velocità delle animazioni di movimento
        this(label, initialPos, finalPos, millisecondWaitEnd,0.5, frames);
    }

    // costruttore che comprende la speed,
    private MovingAnimation(JLabel label, BlockPosition initialPos,
                            BlockPosition finalPos,  int millisecondWaitEnd,
                            double speed, List<Image> frames)
    {
        super(label, frames);

        Objects.requireNonNull(initialPos);
        Objects.requireNonNull(finalPos);

        // calcolo della distanza in blocchi
       blockDistance = calculateBlockDistance(initialPos, finalPos);

        // initialCoord: AbsPosition iniziale dell'angolo in basso a sinistra
        this.initialCoord = GameScreenManager.calculateCoordinates(initialPos);
        // AbsPosition finale dell'angolo in basso a sinistra
        this.finalCoord = GameScreenManager.calculateCoordinates(finalPos);

        // valore 0.03 scelto per tentativi, affinché l'animazione risulti di una velocità gradevole
        this.speed = speed * 0.03;
        setFinalDelay(millisecondWaitEnd);
        // inizializzazione lista di coordinate intermedie
        initCoordList();
    }

    /**
     * Calcola la distanza in blocchi (frazionaria) tra due posizioni in blocchi.
     *
     * @param initialPos posizione iniziale
     * @param finalPos posizione finale
     * @return distanza in blocchi tra {@code initialPos} e {@code finalPos}
     */
    private double calculateBlockDistance(BlockPosition initialPos, BlockPosition finalPos)
    {
        int initialX = initialPos.getX();
        int finalX = finalPos.getX();
        int initialY = initialPos.getY();
        int finalY = finalPos.getY();
        return Math.sqrt(Math.pow(finalX - initialX, 2) + Math.pow(finalY - initialY, 2));
    }


    /**
     * Inizializza la lista di coordinate intermedie dell'animazione,
     * impostando conseguentemente il valore di {@link MovingAnimation#delayMilliseconds}.
     */
    private void initCoordList()
    {
        int initialX = initialCoord.getX();
        int finalX = finalCoord.getX();
        int initialY = initialCoord.getY();
        int finalY = finalCoord.getY();


        delayMilliseconds = (int) Math.round(1000.0 / FPS);
        int numFrames = (int) (FPS * blockDistance / (1000 * speed));

        System.out.println("Num frame animazione: " + numFrames);

        double deltaX = (double)(finalX - initialX) / numFrames;
        double deltaY = (double)(finalY - initialY) / numFrames;

        positionsList = new ArrayList<>();

        int xIncrement;
        int yIncrement;

        for(int i = 1; i < numFrames; i++)
        {
            xIncrement = initialX + (int) Math.round(i * deltaX);
            yIncrement = initialY + (int) Math.round(i * deltaY);

            positionsList.add(new AbsPosition(xIncrement, yIncrement));
        }
        positionsList.add(finalCoord);

    }

    @Override
    public String toString()
    {
        return "Animazione{" +
                "initialCoord=" + initialCoord +
                ", finalCoord=" + finalCoord +
                '}';
    }

    /**
     * Restituisce l'icona successiva a quella attualmente
     * in uso nell'animazione.
     *
     * La successiva all'ultima è la seconda della lista {@link Animation#frameIcons},
     * perché la prima è riservata all'icona da impostare alla fine dell'animazione.
     * Utilizza e modifica {@link MovingAnimation#currentIndex}
     *
     * @return icona successiva a quella attualmente in uso nell'animazione
     */
    @Override
    protected Icon getNextIcon()
    {
        if (currentIndex < frameIcons.size())
            return frameIcons.get(currentIndex++);
        else
        {
            currentIndex = 1;
            return frameIcons.get(frameIcons.size() - 1);
        }
    }


    protected void execute()
    {

        // imposta lo stato ANIMATION
        if(GameManager.getState() != GameManager.GameState.ANIMATION)
            GameManager.changeState(GameManager.GameState.ANIMATION);

        try
        {
        // itera sulle posizioni intermedie, aggiornando l'icona ogni 10 passi e attendendo
        // il tempo di delay dopo ogni passo
            for(AbsPosition c : positionsList)
            {

                if(positionsList.indexOf(c) % 10 == 1)
                    label.setIcon(getNextIcon());

                Thread.sleep(delayMilliseconds);

                GameScreenManager.updateLabelPosition(label, c);
            }
        }
        catch (InterruptedException e)
        {
            label.setIcon(frameIcons.get(0));
            throw new GameException("MovingAnimation interrotta");
        }

        // imposta icona finale
        label.setIcon(frameIcons.get(0));
    }

    @Override
    @ScenarioMethod
    protected void terminate()
    {
        GameManager.continueScenario();
    }

}
