package general;

import action.ActionSequence;
import entity.GamePiece;
import entity.rooms.Room;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameManager
{
    private static Map<String, Room> rooms = new HashMap<>();
    private static Map<String, GamePiece> pieces = new HashMap<>();

    private static ActionSequence currentAnimatedScenario;

    private static boolean canContinue = false;

    public static void addPiece(GamePiece p)
    {
        pieces.put(p.getName(), p);

        System.out.println(pieces);
    }

    public static void startAnimatedScenario(ActionSequence scenario)
    {
        currentAnimatedScenario = scenario;
        currentAnimatedScenario.runAction();
    }
    public static synchronized void continueScenario()
    {
       if(!currentAnimatedScenario.isConcluded())
           currentAnimatedScenario.runAction();
    }

    public static GamePiece getPiece(String name)
    {
        return pieces.get(name);
    }
}
