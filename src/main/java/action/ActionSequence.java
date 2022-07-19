package action;

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

    public static ActionSequence loadScenario(String scenarioPath)
    {
        Document document = openXml(scenarioPath);
        return parseScenario(document.getDocumentElement());
    }

    private static ActionSequence parseScenario(Element scenarioElement)
    {
        System.out.println("Parsando scenario");
        // ricava tipo scnenario
        String modeString = scenarioElement.getElementsByTagName("mode").item(0).getTextContent();

        Mode mode = null;

        for(Mode m : Mode.values())
            if(m.toString().equals(modeString.toUpperCase()))
                mode = m;

        if(mode == null)
            throw new GameException("Tag xml \"mode\" xml non valido");

        ActionSequence scenarioSequence = new ActionSequence(mode);

        // ottieni lista di azioni
        NodeList actionList = scenarioElement.getElementsByTagName("azione");

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

            System.out.println("preso metodo " + methodName );

            if (methodName.equals("move"))
                scenarioSequence.append(parseMove(eAction));
            if (methodName.equals("speak"))
                scenarioSequence.append(parseSpeak(eAction));
            if (methodName.equals("add"))
                scenarioSequence.append(parseAdd(eAction));
            if(methodName.equals("updateSprite"))
                scenarioSequence.append(parseUpdateSprite(eAction));
        }

        // prendi scenario eventuale alla fine
        NodeList exeScenarioList = scenarioElement.getElementsByTagName("executeScenario");

        if(exeScenarioList.getLength() != 0)
        {
            String nextScenarioPath = exeScenarioList.item(0).getTextContent();
            scenarioSequence.append(() -> GameManager.startScenario(ActionSequence.loadScenario(nextScenarioPath)));
        }

        return scenarioSequence;
    }


    private static Runnable parseMove(Element eActionNode)
    {

        String subjectName = eActionNode.getElementsByTagName("subject").item(0).getTextContent();
        GameCharacter subject = (GameCharacter) GameManager.getPiece(subjectName);

        System.out.println("Prendo " + subjectName);

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

        // restituisci runnable corrispondente
        return () -> subject.speak(sentenceNewLined);
    }

    private static Runnable parseAdd(Element eActionNode)
    {
        // non serve la classe, sappiamo che è una Room
        // String subjectClassString = eActionNode.getElementsByTagName("subjectClass").item(0).getTextContent();
        String subject = eActionNode.getElementsByTagName("subject").item(0).getTextContent();
        Room subjectRoom = GameManager.getRoom(subject);

        // Carica il GamePiece da aggiungere
        String pieceName = eActionNode.getElementsByTagName("what").item(0).getTextContent();

        GamePiece piece = loadPiece(pieceName);

        if(piece == null)
            throw new GameException("Oggetto dell'add non trovato");

        int x = Integer.parseInt(eActionNode.getElementsByTagName("x").item(0).getTextContent());
        int y = Integer.parseInt(eActionNode.getElementsByTagName("y").item(0).getTextContent());


        return () -> piece.addInRoom(subjectRoom, new BlockPosition(x, y));

    }

    private static Runnable parseUpdateSprite(Element eActionNode)
    {
        // non serve la classe, sappiamo che è una Room
        // String subjectClassString = eActionNode.getElementsByTagName("subjectClass").item(0).getTextContent();
        String subject = eActionNode.getElementsByTagName("subject").item(0).getTextContent();
        GamePiece piece = GameManager.getPiece(subject);

        // Carica il GamePiece da aggiungere
        String spriteName = eActionNode.getElementsByTagName("spriteName").item(0).getTextContent();

        if(piece == null)
            throw new GameException("Piece non trovato");

        return () -> piece.updateSprite(spriteName);
    }

    private static Document openXml(String path)
    {
        try
        {
            File xmlFile = new File(path);
            // parser per xml
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // per ottenere documento
            DocumentBuilder db = dbf.newDocumentBuilder();
            // documento
            Document xml = db.parse(xmlFile);
            // elabora documento
            xml.getDocumentElement().normalize();

            return xml;
        }
        catch (ParserConfigurationException | IOException | SAXException e)
        {
            System.out.println(e.getStackTrace());
            throw new GameException("Errore nel caricamento dell'xml");
        }

    }

    private static GamePiece loadPiece(String name)
    {
        if(GameManager.getPiece(name) != null)
            return GameManager.getPiece(name);

        Document characterXml = openXml("src/main/resources/scenari/personaggi.xml");
        Document itemXml = openXml("src/main/resources/scenari/oggetti.xml");


        NodeList characterNodeList = characterXml.getElementsByTagName("personaggio");
        for(int i = 0; i < characterNodeList.getLength(); i++)
        {
            Node characterNode = characterNodeList.item(i);
            String elementName = characterNode.getAttributes().getNamedItem("nome").getNodeValue();
            if (elementName.equals(name))
            {
                Element characterElement = (Element) characterNode;
                String spritesheetPath = characterElement.getElementsByTagName("spritesheet").item(0).getTextContent();
                String jsonPath = characterElement.getElementsByTagName("json").item(0).getTextContent();

                return new NPC(name, spritesheetPath, jsonPath);
            }

        }


        NodeList itemNodeList = itemXml.getElementsByTagName("oggetto");
        for(int i = 0; i < itemNodeList.getLength(); i++)
        {
            Node itemNode = itemNodeList.item(i);
            String elementName = itemNode.getAttributes().getNamedItem("nome").getNodeValue();


            if (elementName.equals(name))
            {
                Element pieceElement = (Element) itemNode;
                String className = pieceElement.getElementsByTagName("classe").item(0).getTextContent();
                String description = pieceElement.getElementsByTagName("descrizione").item(0).getTextContent();
                boolean canUse = Boolean.parseBoolean(pieceElement
                        .getElementsByTagName("canUse")
                        .item(0).getTextContent());

                // TODO: caricare l'opportuna classe IMPORTANTEEEEE!"!!!!!!1
                Item item;
                if(className.equals("Item"))
                    item = new Item(name, description, canUse);
                else if (className.equals("PickupableItem"))
                    item = new PickupableItem(name, description);
                else if (className.equals("Door"))
                    item = new Door(name, description);
                else
                    throw new GameException("Classe oggetto [" + className + "] ancora non supportata");
                // TODO : rimpiazzare if-else


                Node onUseNode = pieceElement.getElementsByTagName("onUse").item(0);

                if (onUseNode != null)
                {
                    String scenarioPath = ((Element) onUseNode).getElementsByTagName("effetto").item(0).getTextContent();
                    item.setUseAction(loadScenario(scenarioPath));

                    item.setUseActionName(((Element) onUseNode).getElementsByTagName("actionName").item(0).getTextContent());
                }

                Node onOpenNode = pieceElement.getElementsByTagName("onOpen").item(0);

                if (onOpenNode != null)
                {
                    String scenarioPath = ((Element) onOpenNode).getElementsByTagName("effetto").item(0).getTextContent();
                    ((Openable) item).setOpenEffect(loadScenario(scenarioPath));
                }

                return item;
            }
        }

        return null;

    }

    public static Room loadRoom(String roomPath)
    {
        Document roomXml = openXml(roomPath);

        // creazione stanza
        Element roomElement = roomXml.getDocumentElement();
        String name = roomElement.getAttributes().getNamedItem("nome").getNodeValue();
        String pngPath = roomXml.getElementsByTagName("png").item(0).getTextContent();
        String jsonPath = roomXml.getElementsByTagName("json").item(0).getTextContent();

        return new Room(name, pngPath, jsonPath);
    }

    public static ActionSequence loadRoomInit(String roomPath)
    {
        Document roomXml = openXml(roomPath);
        Node scenarioNode = roomXml.getElementsByTagName("scenario").item(0);

        return parseScenario((Element) scenarioNode);
    }



}
