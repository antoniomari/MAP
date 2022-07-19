package scenarios;

import entity.GamePiece;
import entity.characters.GameCharacter;
import entity.characters.NPC;
import entity.items.Door;
import entity.items.Item;
import entity.items.Openable;
import entity.items.PickupableItem;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
import general.GameException;
import general.GameManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActionSequence
{
    private final List<Runnable> actionList;
    private int index;
    private final Mode mode;

    public enum Mode
    {
        INSTANT, SEQUENCE
    }


    public ActionSequence(Mode mode)
    {
        Objects.requireNonNull(mode);

        actionList = new ArrayList<>();
        index = 0;
        this.mode = mode;
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder("[");
        for(Runnable r : actionList)
            s.append(r.toString() + "\t");
        s.append("]");

        return s.toString();
    }

    public Mode getMode()
    {
        return mode;
    }

    public void append(Runnable action)
    {
        actionList.add(action);
    }

    public void runAction()
    {
        if(!isConcluded())
        {
            System.out.println("Runno " + actionList.get(index));
            actionList.get(index++).run();
        }

    }

    public void runAll()
    {
        for(Runnable r : actionList)
        {
            System.out.println("Ranno " + r);
            r.run();
        }
    }

    public boolean isConcluded()
    {
        System.out.println("Indice " + index);
        System.out.println("Dim " + actionList.size());
        return index == actionList.size();
    }
}
