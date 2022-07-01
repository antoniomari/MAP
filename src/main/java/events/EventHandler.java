package events;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EventHandler
{

    public static void printEvent(GameEvent ge)
    {
        System.out.println(ge.getEventString());
    }
}
