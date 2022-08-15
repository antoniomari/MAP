package general;

import entity.GamePiece;
import entity.characters.PlayingCharacter;
import entity.rooms.Room;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class GameManager
{
    public static final int BLOCK_SIZE = 24;

    private static final Map<String, Room> rooms = new HashMap<>();
    private static final Map<String, GamePiece> pieces = new HashMap<>();

    private static final Stack<ActionSequence> scenarioStack = new Stack<>();

    static
    {
        // load Schwartz
        pieces.put(PlayingCharacter.getPlayerName(), PlayingCharacter.getPlayer());
    }

    public static void addPiece(GamePiece p)
    {
        pieces.put(p.getName(), p);
    }

    public static void addRoom(Room room)
    {
        rooms.put(room.getName(), room);
    }

    public static Room getRoom(String roomName)
    {
        return rooms.get(roomName);
    }

    public static synchronized void startScenario(ActionSequence scenario)
    {
        scenarioStack.push(scenario);

        LogOutputManager.logOutput("Stack scenari: " + scenarioStack, LogOutputManager.SCENARIO_STACK_COLOR);

        // se non è la prima volta che si esegue lo scenario
        if(scenario.isConcluded())
            scenario.rewind();

        if(scenario.getMode() == ActionSequence.Mode.SEQUENCE)
        {
            scenario.runAction();
        }
        else
        {
            scenario.runAll();
            scenarioStack.remove(scenario);

            LogOutputManager.logOutput("Stack scenari: " + scenarioStack, LogOutputManager.SCENARIO_STACK_COLOR);
        }
    }

    public static synchronized void continueScenario()
    {
        if(scenarioStack.isEmpty())
            return;

        ActionSequence top = scenarioStack.peek();
        if(top.getMode() == ActionSequence.Mode.SEQUENCE)
        {
            if(top.isConcluded())
            {
                scenarioStack.remove(top);
                LogOutputManager.logOutput("Stack scenari: " + scenarioStack,
                                            LogOutputManager.SCENARIO_STACK_COLOR);
            }
            else
                top.runAction();
        }
    }

    public static GamePiece getPiece(String name)
    {
        return pieces.get(name);
    }

}
