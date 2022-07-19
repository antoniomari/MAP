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
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class XmlLoader
{
    public static ActionSequence loadScenario(String scenarioPath)
    {
        Document document = openXml(scenarioPath);
        return parseScenario(document.getDocumentElement());
    }

    private static String getTagValue(Element xmlElement, String tagName)
    {
        Optional<String> value = getOptionalTagValue(xmlElement, tagName);

        if(value.isEmpty())
            throw new GameException("tag [" + tagName + "] non presente in xml");
        else
            return value.get();
    }

    private static Optional<String> getOptionalTagValue(Element xmlElement, String tagName)
    {
        NodeList elements = xmlElement.getElementsByTagName(tagName);

        if(elements.getLength() > 1)
            throw new GameException("tag [" + tagName + "] replicato in xml");

        if(elements.item(0) == null)
            return Optional.empty();
        else
            return Optional.of(elements.item(0).getTextContent());
    }

    private static ActionSequence parseScenario(Element scenarioElement)
    {
        // ricava tipo scenario
        String modeString = getTagValue(scenarioElement, "mode");
        ActionSequence.Mode mode = null;

        // controlla se il valore del tag "mode" corrisponde a un tipo scenario
        for(ActionSequence.Mode m : ActionSequence.Mode.values())
            if(m.toString().equals(modeString.toUpperCase()))
                mode = m;

        // se mode non corrisponde allora lancia eccezione
        if(mode == null)
            throw new GameException("Tag xml \"mode\" xml non valido");

        ActionSequence scenarioSequence = new ActionSequence(mode);

        // ottieni lista di azioni
        NodeList actionList = scenarioElement.getElementsByTagName("azione");

        // cicla sulle azioni
        for (int i = 0; i < actionList.getLength(); i++)
        {
            Element eAction =  (Element) actionList.item(i);
            scenarioSequence.append(parseAction(eAction));
        }

        // prendi scenario eventuale alla fine
        Optional<String> scenarioName = getOptionalTagValue(scenarioElement, "executeScenario");

        if(scenarioName.isPresent())
        {
            String nextScenarioPath = scenarioName.get();
            scenarioSequence.append(() -> GameManager.startScenario(loadScenario(nextScenarioPath)));
        }

        return scenarioSequence;
    }

    private static Runnable parseAction(Element actionElement)
    {
        // prendi nome metodo
        String methodName = getTagValue(actionElement, "method");

        System.out.println("preso metodo " + methodName );

        Runnable actionParsed;

        // TODO : modificare con le reflection
        switch (methodName)
        {
            case "move":
                actionParsed = parseMove(actionElement);
                break;
            case "speak":
                actionParsed = parseSpeak(actionElement);
                break;
            case "add":
                actionParsed = parseAdd(actionElement);
                break;
            case "updateSprite":
                actionParsed = parseUpdateSprite(actionElement);
                break;
            case "effectAnimation":
                actionParsed = parseEffectAnimation(actionElement);
                break;
            default:
                throw new GameException("XML contiene metodo " + methodName + " non valido");
        }

        return actionParsed;
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

    private static Runnable parseEffectAnimation(Element eActionNode)
    {
        // non serve la classe, sappiamo che è una Room
        // String subjectClassString = eActionNode.getElementsByTagName("subjectClass").item(0).getTextContent();
        String subject = eActionNode.getElementsByTagName("subject").item(0).getTextContent();
        GamePiece piece = GameManager.getPiece(subject);

        // Carica il GamePiece da aggiungere
        String animationNAme = eActionNode.getElementsByTagName("animationName").item(0).getTextContent();

        if(piece == null)
            throw new GameException("Piece non trovato");

        return () -> piece.executeEffectAnimation(animationNAme);
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

                Node onUseWithNode = pieceElement.getElementsByTagName("onUseWith").item(0);

                if (onUseWithNode != null)
                {
                    String targetName = ((Element) onUseWithNode).getElementsByTagName("target").item(0).getTextContent();
                    Item target = (Item) GameManager.getPiece(targetName);
                    String methodName = ((Element) onUseWithNode).getElementsByTagName("method").item(0).getTextContent();
                    Method method;
                    try
                    {
                        method = target.getClass().getMethod(methodName);
                    }
                    catch(NoSuchMethodException e)
                    {
                        throw new GameException("metodo non trovato");
                    }

                    // TODO: generalizzare
                    ActionSequence useWithScenario = new ActionSequence(ActionSequence.Mode.INSTANT);
                    useWithScenario.append(() ->
                    {
                        try
                        {
                            System.out.println("Metodo" + method.getName());
                            System.out.println("Target" + targetName);
                            method.invoke(target);
                        } catch (IllegalAccessException | InvocationTargetException e)
                        {
                            e.printStackTrace();
                        }
                    });

                    ((PickupableItem) item).setTargetItem(target);
                    ((PickupableItem) item).setUsewithAction(useWithScenario);
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
