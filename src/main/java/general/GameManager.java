package general;

import GUI.MainFrame;
import GUI.gamestate.GameState;
import entity.GamePiece;
import entity.characters.PlayingCharacter;
import entity.rooms.Room;

import java.util.*;

public class GameManager
{
    public static final int BLOCK_SIZE = 24;

    private static MainFrame mainFrame;

    private static final Map<String, Room> rooms = new HashMap<>();
    private static final Map<String, GamePiece> pieces = new HashMap<>();

    private static final Stack<ActionSequence> scenarioStack = new Stack<>();

    static
    {
        // load Schwartz
        pieces.put(PlayingCharacter.getPlayerName(), PlayingCharacter.getPlayer());
    }

    public static void setMainFrame(MainFrame m)
    {
        mainFrame = m;
    }

    public static MainFrame getMainFrame()
    {
        return mainFrame;
    }

    public static void addPiece(GamePiece p)
    {
        pieces.put(p.getName(), p);
    }

    public static void removePiece(GamePiece p)
    {
        Objects.requireNonNull(p);

        pieces.remove(p.getName());
    }

    public static void addRoom(Room room)
    {
        rooms.put(room.getName(), room);
    }

    public static Room getRoom(String roomName)
    {
        return rooms.get(roomName);
    }


    public static Set<String> getRoomNames() {
        return rooms.keySet();
    }

    /**
     * Inizia l'esecuzione di uno scenario, eseguendo la sua prima azione.
     *
     * Se lo scenario si era già concluso (cioè sta venendo riutilizzato),
     * allora viene ri-iniziato.
     *
     * @param scenario scenario da eseguire
     */
    public static synchronized void startScenario(ActionSequence scenario)
    {
        scenarioStack.push(scenario);

        LogOutputManager.logOutput("Stack scenari: " + scenarioStack, LogOutputManager.SCENARIO_STACK_COLOR);

        // se non è la prima volta che si esegue lo scenario
        if(scenario.isConcluded())
            scenario.rewind();


        scenario.runAction();
    }

    /**
     * Continua l'esecuzione dell'ultimo scenario
     * presente nella pila {@link GameManager#scenarioStack}.
     *
     * Se questo è ora finito, lo rimuove e riesegue l'operazione
     * sul nuovo top, procedendo finché lo {@link GameManager#scenarioStack}
     * non è vuoto
     */
    public static synchronized void continueScenario()
    {
        if(scenarioStack.isEmpty())
        {
            GameState.changeState(GameState.State.PLAYING);
            return;
        }

        ActionSequence top = scenarioStack.peek();
            if(top.isConcluded())
            {
                scenarioStack.remove(top);
                LogOutputManager.logOutput("Stack scenari: " + scenarioStack,
                                            LogOutputManager.SCENARIO_STACK_COLOR);

                // continua lo scenario precedente nello stack
                continueScenario();
            }
            else
                top.runAction();
    }

    public static GamePiece getPiece(String name)
    {
        return pieces.get(name);
    }

}
