package action;

import entity.characters.GameCharacter;
import entity.characters.NPC;
import entity.items.Item;
import entity.items.PickupableItem;
import entity.rooms.BlockPosition;
import general.GameManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActionSequence
{
    private final List<Runnable> actionList;
    private int index;


    public ActionSequence()
    {
        actionList = new ArrayList<>();
        index = 0;
    }

    public void append(Runnable action)
    {
        actionList.add(action);
    }

    public void runAction()
    {
        if(!isConcluded())
        {
            actionList.get(index++).run();
        }

    }

    public void runAll()
    {
        for(Runnable r : actionList)
        {
            r.run();
        }
    }

    public boolean isConcluded()
    {
        return index == actionList.size();
    }

    public static ActionSequence loadScenario(String scenarioPath)
    {

        try
        {
            // TODO: aggiustare commenti
            // carica file
            File file = new File(scenarioPath);
            // parser per xml
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // per ottenere documento
            DocumentBuilder db = dbf.newDocumentBuilder();
            // documento
            Document document = db.parse(file);
            // elabora documento
            document.getDocumentElement().normalize();

            ActionSequence scenarioSequence = new ActionSequence();

            // ottieni lista di azioni
            NodeList actionList = document.getElementsByTagName("azione");

            // cicla sulle azioni
            for (int i = 0; i < actionList.getLength(); i++)
            {
                Node actionToExecute = actionList.item(i);
                Element eAction = (Element) actionToExecute;
                // prendi nome metodo
                String methodName = eAction
                        .getElementsByTagName("method")
                        .item(0)
                        .getTextContent();

                System.out.println("preso metodo");

                if (methodName.equals("move"))
                    scenarioSequence.append(parseMove(eAction));
                if (methodName.equals("speak"))
                    scenarioSequence.append(parseSpeak(eAction));
                if (methodName.equals("add"))
                    scenarioSequence.append(parseAdd(eAction));

            }

            return scenarioSequence;
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
    }


    private static Runnable parseMove(Element eActionNode)
    {

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

    private static Runnable parseSpeak(Element eActionNode)
    {
        String subjectName = eActionNode.getElementsByTagName("subject").item(0).getTextContent();
        GameCharacter subject = (GameCharacter) GameManager.getPiece(subjectName);

        // ricava stringa da stampare
        String sentence = eActionNode.getElementsByTagName("sentence").item(0).getTextContent();

        // formatta stringa
        String sentenceNewLined = sentence.strip().replaceAll("\\s\\(\\*\\)\\s", "\n");

        // esegui comando
        return () -> subject.speak(sentenceNewLined);
    }

    private static Runnable parseAdd(Element eActionNode)
    {
        String subjectClassString = eActionNode.getElementsByTagName("subjectClass").item(0).getTextContent();
        String subject = eActionNode.getElementsByTagName("subject").item(0).getTextContent();

        String objectName = eActionNode.getElementsByTagName("what").item(0).getTextContent();
        int x = Integer.parseInt(eActionNode.getElementsByTagName("x").item(0).getTextContent());
        int y = Integer.parseInt(eActionNode.getElementsByTagName("y").item(0).getTextContent());

        // crea oggetto:
        PickupableItem item = new PickupableItem(objectName, "CIAO CIAO");

        return () -> item.addInRoom(GameManager.getRoom(subject), new BlockPosition(x, y));

    }

}
