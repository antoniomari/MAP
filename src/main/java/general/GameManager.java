package general;

import action.ActionSequence;
import entity.GamePiece;
import entity.characters.GameCharacter;
import entity.items.Item;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

        System.out.println("Stack" + scenarioStack);

        if(scenario.getMode() == ActionSequence.Mode.SEQUENCE)
        {
            System.out.println("Runno singola");
            scenario.runAction();
        }
        else
        {
            System.out.println("Runno tutte");
            scenario.runAll();
            scenarioStack.remove(scenario);
        }

    }

    public static synchronized void continueScenario()
    {
        if(scenarioStack.isEmpty())
            return;
        ActionSequence top = scenarioStack.peek();
        System.out.println("top=" + top);
        System.out.println("Number element" + scenarioStack.size());
        if(top.getMode() == ActionSequence.Mode.SEQUENCE)
        {
            System.out.println("Continuazione" + top);
            if(top.isConcluded())
                scenarioStack.pop();
            else
                top.runAction();
        }

    }

    public static GamePiece getPiece(String name)
    {
        return pieces.get(name);
    }

}
