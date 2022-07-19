package general;

import scenarios.ActionSequence;
import entity.GamePiece;
import entity.rooms.Room;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class GameManager
{
    private static final Map<String, Room> rooms = new HashMap<>();
    private static final Map<String, GamePiece> pieces = new HashMap<>();

    private static Stack<ActionSequence> scenarioStack = new Stack<>();


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

    public static void startScenario(ActionSequence scenario)
    {
        scenarioStack.push(scenario);

        LogOutputManager.logOutput("Stack scenari: " + scenarioStack, LogOutputManager.SCENARIO_STACK_COLOR);

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
                scenarioStack.pop();
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
