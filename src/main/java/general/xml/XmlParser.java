package general.xml;

import GUI.miniGames.LogicQuest;
import GUI.miniGames.TestMist;
import entity.GamePiece;
import entity.characters.GameCharacter;
import entity.characters.NPC;
import entity.characters.PlayingCharacter;
import entity.items.*;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
import events.executors.TextBarUpdateExecutor;
import general.ActionSequence;
import general.GameException;
import general.GameManager;
import general.LogOutputManager;
import graphics.SpriteManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sound.SoundHandler;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XmlParser
{

    public static ActionSequence loadScenario(String scenarioPath)
    {
        Objects.requireNonNull(scenarioPath);

        // STAMPA DI LOG
        LogOutputManager.logOutput("Parsing scenario " + scenarioPath, LogOutputManager.XML_COLOR);

        Document document = openXml(scenarioPath);
        return parseScenario(scenarioPath, document.getDocumentElement());
    }

    static String getTagValue(Element xmlElement, String tagName)
    {
        Optional<String> value = getOptionalTagValue(xmlElement, tagName);

        if(value.isEmpty())
            throw new GameException("tag [" + tagName + "] non presente in xml");
        else
            return value.get();
    }

    static Optional<String> getOptionalTagValue(Element xmlElement, String tagName)
    {
        NodeList elements = xmlElement.getElementsByTagName(tagName);

        if(elements.getLength() > 1)
            throw new GameException("tag [" + tagName + "] replicato in xml");

        if(elements.item(0) == null)
            return Optional.empty();
        else
            return Optional.of(elements.item(0).getTextContent());
    }


    static List<Element> getTagsList(Document xmlDocument, String tagName)
    {
        NodeList nodes = xmlDocument.getElementsByTagName(tagName);

        List<Element> elementList = new ArrayList<>(nodes.getLength());

        for(int i = 0; i < nodes.getLength(); i++)
            elementList.add((Element) nodes.item(i));

        return elementList;
    }

    static List<Element> getTagsList(Element xmlElement, String tagName)
    {
        NodeList nodes = xmlElement.getElementsByTagName(tagName);

        List<Element> elementList = new ArrayList<>(nodes.getLength());

        for(int i = 0; i < nodes.getLength(); i++)
            elementList.add((Element) nodes.item(i));

        return elementList;
    }

    /**
     * Restituisce l'attributo {@code attributeName} dell'elemento xml {@code xmlElement}.
     *
     * @param xmlElement elemento xml
     * @param attributeName nome dell'attributo di cui è cercato il valore
     * @return il valore dell'attributo corrispondente
     * @throws GameException se {@code xmlElement} non presenta l'attributo {@code attributeName}
     */
    static String getXmlAttribute(Element xmlElement, String attributeName)
    {
        Node valueNode = xmlElement.getAttributes().getNamedItem(attributeName);

        if(valueNode == null)
            throw new GameException("Attributo [" + attributeName + "] non presente nell'xml");
        else
            return valueNode.getNodeValue();
    }

    static ActionSequence parseScenario(String scenarioName, Element scenarioElement)
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

        ActionSequence scenarioSequence = new ActionSequence(scenarioName, mode);

        // ottieni lista di azioni
        NodeList actionList = scenarioElement.getElementsByTagName("azione");

        // cicla sulle azioni
        for (int i = 0; i < actionList.getLength(); i++)
        {
            Element eAction =  (Element) actionList.item(i);
            scenarioSequence.append(parseAction(eAction));
        }

        // prendi scenario eventuale alla fine
        Optional<String> nextScenario = getOptionalTagValue(scenarioElement, "executeScenario");

        if(nextScenario.isPresent())
        {
            String nextScenarioPath = nextScenario.get();
            scenarioSequence.append(() -> GameManager.startScenario(loadScenario(nextScenarioPath)));
        }

        return scenarioSequence;
    }

    private static Runnable parseAction(Element actionElement)
    {
        // prendi nome metodo
        String methodName = getTagValue(actionElement, "method");

        LogOutputManager.logOutput("Azione " + methodName, LogOutputManager.XML_COLOR);

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
            case "addPickup":
                actionParsed = parseAddPickup(actionElement);
                break;
            case "setEast":
                actionParsed = parseSetEast(actionElement);
                break;
            case "setWest":
                actionParsed = parseSetWest(actionElement);
                break;
            case "setNorth":
                actionParsed = parseSetNorth(actionElement);
                break;
            case "setSouth":
                actionParsed = parseSetSouth(actionElement);
                break;
            case "setSpeakScenario":
                actionParsed = parseSetSpeakScenario(actionElement);
                break;
            case "animate":
                actionParsed = parseAnimate(actionElement);
                break;
            case "animateReverse":
                actionParsed = parseAnimateReverse(actionElement);
                break;
            case "removeFromRoom":
                actionParsed = parseRemoveFromRoom(actionElement);
                break;
            case "addToInventory":
                actionParsed = parseAddToInventory(actionElement);
                break;
            case "playMusic":
                actionParsed = parsePlayMusic(actionElement);
                break;
            case "playScenarioSound":
                actionParsed = parsePlayScenarioSound(actionElement);
                break;
            case "setScenarioOnEnter":
                actionParsed = parseSetScenarioOnEnter(actionElement);
                break;
            case "loadFloor":
                actionParsed = parseLoadFloor(actionElement);
                break;
            case "itemSpeak":
                actionParsed = parseItemSpeak(actionElement);
                break;
            case "playEmoji":
                actionParsed = parsePlayEmoji(actionElement);
                break;
            case "setState":
                actionParsed = parseSetState(actionElement);
                break;
            case "executeTest":
                actionParsed = parseExecuteTest(actionElement);
                break;
            case "setCanUse":
                actionParsed = parseSetCanUse(actionElement);
                break;
            case "teleport":
                actionParsed = parseTeleport(actionElement);
                break;
            case "addRoomEffect":
                actionParsed = parseAddRoomEffect(actionElement);
                break;
            default:
                throw new GameException("XML contiene metodo " + methodName + " non valido");
        }

        return actionParsed;
    }


    /**
     * Esegue il parsing di un elemento azione xml (root tag: {@literal  <action>})
     * che contiene il valore {@code move} per il tag {@literal  <method>}
     *
     * I tag richiesti per il parsing di questo comando sono:
     * <ul>
     *    <li>{@literal <subject>} il nome del soggetto dell'azione (GamePiece)</li>
     *
     *    <li>{@literal <x>} ascissa di blocco in cui subject deve muoversi</li>
     *
     *    <li>{@literal <y>} ordinata di blocco in cui subject deve muoversi</li>
     *
     *    <li>{@literal <how>} "absolute" se (x, y) sono coordinate assolute;
     *    "relative" se sono relative alla posizione attuale di suject</li>
     *
     *    <li>{@literal <finalWait>} numero di millisecondi da aspettare
     *    dopo l'esecuzione dell'animazione di movimento</li>
     * </ul>
     * @param eAction elemento corrispondente all'azione xml
     * @return Runnable associata al comando move
     */
    private static Runnable parseMove(Element eAction)
    {
        String subjectName = getTagValue(eAction, "subject");

        // ricava coordinate finali
        int x = Integer.parseInt(getTagValue(eAction, "x"));
        int y = Integer.parseInt(getTagValue(eAction, "y"));

        // ricava modalità
        String type = getTagValue(eAction, "how");

        // ricava millis
        int millisecondEndWait = Integer.parseInt(eAction
                .getElementsByTagName("finalWait")
                .item(0).getTextContent());

        // ritorna runnable
        return () -> (GameManager.getPiece(subjectName)).move(new BlockPosition(x, y), type, millisecondEndWait);
    }


    /**
     * Esegue il parsing di un elemento azione xml (root tag: {@literal  <action>})
     * che contiene il valore {@code speak} per il tag {@literal  <method>}
     *
     * I tag richiesti per il parsing di questo comando sono:
     * <ul>
     *    <li>{@literal <subject>} il nome del soggetto dell'azione (GameCharacter)</li>
     *
     *    <li>{@literal <sentence>} frase pronunciata (a capo con "(*)")</li>
     * </ul>
     * @param eAction elemento corrispondente all'azione xml
     * @return Runnable associata al comando move
     */
    private static Runnable parseSpeak(Element eAction)
    {
        String subjectName = getTagValue(eAction, "subject");
        // ricava stringa da stampare
        String sentence = getTagValue(eAction, "sentence");
        // formatta stringa (spazi)
        String sentenceNewLined = sentence.strip().replaceAll("\\s\\(\\*\\)\\s", "\n");
        // restituisci runnable corrispondente
        return () -> ((GameCharacter) GameManager.getPiece(subjectName)).speak(sentenceNewLined);
    }

    private static Runnable parseItemSpeak(Element eAction)
    {
        String subjectName = getTagValue(eAction, "subject");
        // ricava stringa da stampare
        String sentence = getTagValue(eAction, "sentence");
        // formatta stringa (spazi)
        String sentenceNewLined = sentence.strip().replaceAll("\\s\\(\\*\\)\\s", "\n");
        String toPrint = subjectName + ": " + sentenceNewLined;
        // restituisci runnable corrispondente
        return () -> TextBarUpdateExecutor.executeDisplay(toPrint);
    }

    /**
     * Esegue il parsing di un elemento azione xml (root tag: {@literal  <action>})
     * che contiene il valore {@code add} per il tag {@literal  <method>}
     *
     * I tag richiesti per il parsing di questo comando sono:
     * <ul>
     *     <li>{@literal <subject>} il nome della stanza in cui caricare il GamePiece</li>
     *
     *    <li>{@literal <what>} il nome del del GamePiece da caricare nella stanza,
     *    che viene caricato tramite il rispettivo file xml</li>
     *
     *    <li>{@literal x} ascissa dove dev'essere collocato l'angolo in basso a sinistra
     *    dello sprite di {@literal <what>}</li>
     *
     *    <li>{@literal y} ordinata dove dev'essere collocato l'angolo in basso a sinistra
     *    dello sprite di {@literal <what>}</li>
     * </ul>
     *
     * @param eAction elemento corrispondente all'azione xml
     * @return Runnable associata al comando move
     * @throws GameException se {@literal <what>} non è stato trovato nel
     * corrispettivo xml
     */
    private static Runnable parseAdd(Element eAction)
    {
        // non serve la classe, sappiamo che è una Room
        String subject = getTagValue(eAction, "subject");

        // Carica il GamePiece da aggiungere
        String pieceName = getTagValue(eAction, "what");
        GamePiece piece = XmlLoader.loadPiece(pieceName);

        int x = Integer.parseInt(getTagValue(eAction, "x"));
        int y = Integer.parseInt(getTagValue(eAction, "y"));

        return () -> piece.addInRoom(GameManager.getRoom(subject), new BlockPosition(x, y));

    }

    private static Runnable parseAnimate(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");

        return () -> GameManager.getPiece(subject).animate();
    }

    private static Runnable parseAnimateReverse(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");

        return () -> GameManager.getPiece(subject).animateReverse();
    }

    private static Runnable parseAddPickup(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");

        String pickupName = getTagValue(eAction, "what");
        PickupableItem pickup = (PickupableItem) GameManager.getPiece(pickupName);

        return () -> ((Container) GameManager.getPiece(subject)).addPickup(pickup);
    }

    private static Runnable parseSetEast(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");

        String roomName = getTagValue(eAction, "what");
        if(GameManager.getRoom(roomName) == null)
        {
            Room room = loadRoom(roomName);
            ActionSequence roomScenario = loadRoomInit(roomName);

            return () -> {GameManager.getRoom(subject).setEast(room); GameManager.startScenario(roomScenario);};
        }
        else
        {
            Room room = GameManager.getRoom(roomName);
            return () -> GameManager.getRoom(subject).setEast(room);
        }
    }

    private static Runnable parseSetWest(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");

        String roomName = getTagValue(eAction, "what");
        if(GameManager.getRoom(roomName) == null)
        {
            Room room = loadRoom(roomName);
            ActionSequence roomScenario = loadRoomInit(roomName);

            return () -> {GameManager.getRoom(subject).setWest(room); GameManager.startScenario(roomScenario);};
        }
        else
        {
            Room room = GameManager.getRoom(roomName);
            return () -> GameManager.getRoom(subject).setWest(room);
        }
    }

    private static Runnable parseSetNorth(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");

        String roomName = getTagValue(eAction, "what");
        if(GameManager.getRoom(roomName) == null)
        {
            Room room = loadRoom(roomName);
            ActionSequence roomScenario = loadRoomInit(roomName);

            return () -> {GameManager.getRoom(subject).setNorth(room); GameManager.startScenario(roomScenario);};
        }
        else
        {
            Room room = GameManager.getRoom(roomName);
            return () -> GameManager.getRoom(subject).setNorth(room);
        }
    }

    private static Runnable parseSetSouth(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");

        String roomName = getTagValue(eAction, "what");

        if(GameManager.getRoom(roomName) == null)
        {
            Room room = loadRoom(roomName);
            ActionSequence roomScenario = loadRoomInit(roomName);

            return () -> {GameManager.getRoom(subject).setSouth(room); GameManager.startScenario(roomScenario);};
        }
        else
        {
            Room room = GameManager.getRoom(roomName);
            return () -> GameManager.getRoom(subject).setSouth(room);
        }
    }


    private static Runnable parseSetSpeakScenario(Element eAction)
    {
        /*
        String subject = getTagValue(eAction, "subject");

        String scenarioPath = getTagValue(eAction, "what");

        Document document = openXml(scenarioPath);
        return () -> ((NPC) GameManager.getPiece(subject))
                .setSpeakScenario(parseScenario(scenarioPath, document.getDocumentElement())); */

        throw new GameException("Deprecato");
    }

    private static Runnable parseRemoveFromRoom(Element eAction)
    {
        String subject = getTagValue(eAction,"subject");

        return () -> GameManager.getPiece(subject).removeFromRoom();
    }

    private static Runnable parseAddToInventory(Element eAction)
    {
        String itemName = getTagValue(eAction, "what");
        PickupableItem it = (PickupableItem) XmlLoader.loadPiece(itemName);
        // TODO: controllare correttezza
        return () -> PlayingCharacter.getPlayer().addToInventory(it);
    }

    /*
    private static Runnable parseSetSpeakSentence(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        String sentence = getTagValue(eAction, "sentence");
        String sentenceNewLined = sentence.strip().replaceAll("\\s\\(\\*\\)\\s", "\n");


        return () -> ((NPC) GameManager.getPiece(subject)).setSpeakSentence(sentenceNewLined);
    }

     */

    private static Runnable parsePlayMusic(Element eAction)
    {
        String musicPath = getTagValue(eAction, "what");

        return () -> SoundHandler.playWav(musicPath, SoundHandler.Mode.MUSIC);
    }

    private static Runnable parsePlayScenarioSound(Element eAction)
    {
        String soundPath = getTagValue(eAction, "what");

        return () -> SoundHandler.playWav(soundPath, SoundHandler.Mode.SCENARIO_SOUND);
    }

    private static Runnable parseSetScenarioOnEnter(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        String scenarioPath = getTagValue(eAction, "what");

        return () -> (GameManager.getRoom(subject)).setScenarioOnEnter(scenarioPath);
    }

    private static Runnable parseLoadFloor(Element eAction)
    {
        String floorName = getTagValue(eAction, "floor");

        final String MIST_PATH = "src/main/resources/scenari/piano MIST/MIST-A.xml";
        final String ALU_PATH = "src/main/resources/scenari/piano ALU/ALU-A.xml";
        final String BUG_PATH = "src/main/resources/scenari/piano BUG/BUG-A.xml";
        final String SERVER_PATH = "src/main/resources/scenari/piano SERVER/ServerRoom.xml";
        final String EBERT_PATH = "src/main/resources/scenari/piano EBERT/ebert-a.xml";
        final String FLASH_PATH = "src/main/resources/scenari/piano FLASH/FlashRoom.xml";

        String floorPath;

        switch (floorName)
        {
            case "MIST":
                floorPath = MIST_PATH;
                break;
            case "ALU":
                floorPath = ALU_PATH;
                break;
            case "BUG":
                floorPath = BUG_PATH;
                break;
            case "SERVER":
                floorPath = SERVER_PATH;
                break;
            case "EBERT":
                floorPath = EBERT_PATH;
                break;
            case "FLASH":
                floorPath = FLASH_PATH;
                break;
            default:
                throw new GameException("Piano non valido");
        }

        return () ->
        {
            GameManager.getMainFrame().setCurrentRoom(loadRoom(floorPath));
            GameManager.startScenario(loadRoomInit(floorPath));
        };
    }

    private static Runnable parsePlayEmoji(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        String emojiName = getTagValue(eAction, "what");

        return () -> ((GameCharacter) GameManager.getPiece(subject)).playEmoji(emojiName);
    }

    private static Runnable parseSetState(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        String state = getTagValue(eAction, "state");

        return () -> GameManager.getPiece(subject).setState(state, true);
    }

    private static Runnable parseExecuteTest(Element eAction)
    {
        String what = getTagValue(eAction, "what");

        if(what.equals("ALU"))
            return () -> SwingUtilities.invokeLater(() -> LogicQuest.createLogicQuest(1));
        else if(what.equals("MIST"))
            return () -> SwingUtilities.invokeLater(TestMist::new);
        else
            throw new GameException("Nome del test non valido");
    }

    private static Runnable parseSetCanUse(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        boolean canUse = Boolean.parseBoolean(getTagValue(eAction, "canUse"));

        return () -> ((Item) GameManager.getPiece(subject)).setCanUse(canUse);
    }

    private static Runnable parseTeleport(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        String roomName = getTagValue(eAction, "where");

        return () -> GameManager.getMainFrame().setCurrentRoom(GameManager.getRoom(roomName));
    }

    private static Runnable parseAddRoomEffect(Element eAction)
    {
        String effectPath = getTagValue(eAction, "what");

        return () -> GameManager.getMainFrame().getGameScreenPanel()
                    .addCurrentRoomEffect(SpriteManager.loadSpriteSheet(effectPath));
    }

    /**
     * Esegue il parsing di un elemento azione xml (root tag: {@literal  <action>})
     * che contiene il valore {@code updateSprite} per il tag {@literal  <method>}.
     *
     * I tag richiesti per il parsing di questo comando sono:
     * <ul>
     *     <li>{@literal <subject>} il nome del soggetto dell'azione (GamePiece)</li>
     *
     *    <li>{@literal <spriteName>} il nome ddello sprite da caricare (presente nel json)</li>
     * </ul>
     *
     * @param eAction elemento corrispondente all'azione xml
     * @return Runnable associata al comando updateSprite
     * @throws GameException se {@literal <subject>} non è stato trovato nel GameManager
     */
    private static Runnable parseUpdateSprite(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        GamePiece piece = GameManager.getPiece(subject);

        if(piece == null)
            throw new GameException("Piece non trovato");

        String spriteName = getTagValue(eAction, "spriteName");

        return () -> piece.updateSprite(spriteName);
    }

    /**
     * Esegue il parsing di un elemento azione xml (root tag: {@literal  <action>})
     * che contiene il valore {@code effectAnimation} per il tag {@literal  <method>}.
     *
     * I tag richiesti per il parsing di questo comando sono:
     * <ul>
     *     <li>{@literal <subject>} il nome del soggetto dell'azione (GamePiece)</li>
     *
     *    <li>{@literal <animationName>} il nome dell'animazione da eseguire</li>
     * </ul>
     *
     * @param eAction elemento corrispondente all'azione xml
     * @return Runnable associata al comando updateSprite
     */
    private static Runnable parseEffectAnimation(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");

        // recupera il nome dell'animazione
        String animationName = getTagValue(eAction, "animationName");
        int finalWait = Integer.parseInt(getTagValue(eAction, "finalWait"));

        return () -> GameManager.getPiece(subject).executeEffectAnimation(animationName, finalWait);
    }

    /**
     * Restituisce un Document relativo a un file xml.
     *
     * @param path path del file xml da aprire
     * @return Document del file
     * @throws GameException se si verifica un problema nell'aprire e caricare il file
     */
    static Document openXml(String path)
    {
        try
        {
            File xmlFile = new File(path);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse xml -> ottieni documento
            Document xml = db.parse(xmlFile);
            // normalizza documento
            xml.getDocumentElement().normalize();

            return xml;
        }
        catch (ParserConfigurationException | IOException | SAXException e)
        {
            LogOutputManager.logOutput(e.getStackTrace().toString(), LogOutputManager.EXCEPTION_COLOR);
            throw new GameException("Errore nel caricamento dell'xml");
        }
    }

    /**
     * Crea una Room descritta nel file xml corrispondente.
     *
     * @param roomPath path del file xml contenente i dati della Room
     *                 (root tag: {@literal <stanza>})
     * @return stanza corrispondente
     */
    @Deprecated
    public static Room loadRoom(String roomPath)
    {
        return XmlLoader.loadRoom(roomPath);
    }

    /**
     * Carica lo scenario d'inizializzazione della stanza, contenuto
     * nel file xml della stessa.
     *
     * @param roomPath path del file xml contenente i dati della Room
     * @return ActionSequence che comprende le azioni d'inizializzazione stanza
     */
    @Deprecated
    public static ActionSequence loadRoomInit(String roomPath)
    {
        return XmlLoader.loadRoomInit(roomPath);
    }

}
