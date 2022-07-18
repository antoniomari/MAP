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

public class GameManager
{
    private static final Map<String, Room> rooms = new HashMap<>();
    private static final Map<String, GamePiece> pieces = new HashMap<>();

    private static ActionSequence currentAnimatedScenario;


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


    /*

    // TODO: completare
    public static Room loadPlayableScenario(String path)
    {
        try
        {
            // TODO: aggiustare commenti
            // carica file
            File file = new File(path);
            // parser per xml
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // per ottenere documento
            DocumentBuilder db = dbf.newDocumentBuilder();
            // documento
            Document document = db.parse(file);
            // elabora documento
            document.getDocumentElement().normalize();

            // crea stanza
            String name = document.getAttributes().item(0).getTextContent();
            String pngPath = document.getElementsByTagName("png").item(0).getTextContent();
            String jsonPath = document.getElementsByTagName("json").item(0).getTextContent();

            Room room = new Room(name, pngPath, jsonPath);

            Node itemsNode = document.getElementsByTagName("oggetti").item(0);
            NodeList itemList = ((Element) itemsNode).getElementsByTagName("oggetto");

            for (int i = 0; i < itemList.getLength(); i++)
            {
                // carica oggetto

                //Node actionToExecute = actionList.item(i);
                //Element eAction = (Element) actionToExecute;
                // prendi nome metodo
                //String methodName = eAction
                //        .getElementsByTagName("method")
                //        .item(0)
                //        .getTextContent();
            }

            Node charactersNode = document.getElementsByTagName("personaggi").item(0);
            NodeList characterList = ((Element) charactersNode).getElementsByTagName("personaggio");

            for (int i = 0; i < characterList.getLength(); i++)
            {
                // carica personaggio
            }

            return room;
        } catch (Exception e)
        {
            System.out.println(e);
            return null;
        }
    }


    // TODO: completare
    private static Item loadItem(String name)
    {

        /*
        String subjectName = eActionNode.getElementsByTagName("subject").item(0).getTextContent();
        GameCharacter subject = (GameCharacter) GameManager.getPiece(subjectName);

        // costruisci block position
        int x = Integer.parseInt(eActionNode.getElementsByTagName("x").item(0).getTextContent());
        int y = Integer.parseInt(eActionNode.getElementsByTagName("y").item(0).getTextContent());

        // ricava modalità
        String type = eActionNode.getElementsByTagName("how").item(0).getTextContent();

        // ricava millis
        int millisecondEndWait = Integer.parseInt(eActionNode
                .getElementsByTagName("finalWait")
                .item(0).getTextContent());

        // esegui comando
        return () -> subject.move(new BlockPosition(x, y), type, millisecondEndWait);


    }

    // TODO : completare
    private static GameCharacter loadCharacter(String name)
    {
        /*
        String subjectName = eActionNode.getElementsByTagName("subject").item(0).getTextContent();
        GameCharacter subject = (GameCharacter) GameManager.getPiece(subjectName);

        // ricava modalità
        String sentence = eActionNode.getElementsByTagName("sentence").item(0).getTextContent();

        // esegui comando
        return () -> subject.speak(sentence);


    }

    */
}
