package general;

import entity.GamePiece;
import entity.rooms.Room;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameManager
{
    private static Map<String, Room> rooms = new HashMap<>();
    private static Map<String, GamePiece> pieces = new HashMap<>();

    public static void addPiece(GamePiece p)
    {
        pieces.put(p.getName(), p);

        System.out.println(pieces);
    }

    public static GamePiece getPiece(String name)
    {
        return pieces.get(name);
    }
}
