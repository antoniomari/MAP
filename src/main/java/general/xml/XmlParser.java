package general.xml;

import GUI.miniGames.Captcha;
import GUI.miniGames.ClientCap;
import GUI.miniGames.LogicQuest;
import GUI.miniGames.TestMist;
import entity.GamePiece;
import entity.characters.GameCharacter;
import entity.characters.PlayingCharacter;
import entity.items.Item;
import entity.items.Openable;
import entity.items.PickupableItem;
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
import restClient.RecipeRestClient;
import sound.SoundHandler;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class XmlParser
{

    /**
     * Costruisce un'ActionSequence risultata dal parsing di un file ".xml"
     *
     * @param scenarioPath path del file ".xml"
     * @return ActionSequence corrispondente al contenuto del file ".xml"
     */
    public static ActionSequence loadScenario(String scenarioPath)
    {
        Objects.requireNonNull(scenarioPath);

        // STAMPA DI LOG
        LogOutputManager.logOutput("Parsing scenario " + scenarioPath, LogOutputManager.XML_COLOR);

        Document document = openXml(scenarioPath);
        return parseScenario(scenarioPath, document.getDocumentElement());
    }

    /**
     * Restituisce il valore testuale di un tag dell'elemento (xml) passato.
     *
     * @param xmlElement elemento (xml) di cui ricercare il valore del tag
     * @param tagName nome del tag
     *
     * @return valore testuale del tag {@code tagName} nell'elemento {@code xmlElement}
     * @throws GameException se {@code tagName} non è presente in {@code xmlElement},
     * oppure se è duplicato.
     */
    static String getTagValue(Element xmlElement, String tagName)
    {
        Optional<String> value = getOptionalTagValue(xmlElement, tagName);

        if(!value.isPresent())
            throw new GameException("tag [" + tagName + "] non presente in xml");
        else
            return value.get();
    }

    /**
     * Restituisce un Optional che contiene il valore testuale di un tag, eventualmente
     * presente nell'elemento (xml) passato.
     *
     * @param xmlElement elemento (xml) di cui ricercare il valore del tag
     * @param tagName nome del tag
     *
     * @return Optional che contiene il valore testuale del tag {@code tagName}, eventualmente
     * presente in {@code xmlElement}
     * @throws GameException se {@code tagName} è duplicato in {@code xmlElement}
     */
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

    /**
     * Restituisce una lista (eventualmente vuota) contenente gli elementi xml
     * col tag {@code tagName} presenti all'interno dell'elemento (xml) specificato.
     *
     * @param xmlElement elemento xml all'interno del quale cercare gli elementi
     *                   col tag {@code tagName}
     * @param tagName nome del tag
     *
     * @return una lista contenente gli elementi xml col tag {@code tagName}
     * presenti all'interno dell'elemento (xml) specificato.
     */
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

    /**
     * Esegue il parsing di un elemento xml corrispondente a uno scenario d'inizializzazione
     * di una stanza, del quale vengono ignorate tutte le azioni tranne:
     * <ul>
     *     <li>{@code setNorth}</li>
     *     <li>{@code setWest}</li>
     *     <li>{@code setEast}</li>
     *     <li>{@code setSouth}</li>
     * </ul>
     * @param scenarioName nome da assegnare allo scenario (solo fini di stampa documentativa)
     * @param roomInitScenarioElement elemento xml il cui root tag è "scenario", corrispondente
     *                                allo scenario di iniziliazzazione della stanza
     * @return ActionSequence corrispondente allo scenario d'inizializzazione della stanza per
     * l'utilizzo nel caricamento da db
     */
    static ActionSequence parseInitRoomDB(String scenarioName, Element roomInitScenarioElement)
    {
        // ottieni lista di azioni
        List<Element> actionList = XmlParser.getTagsList(roomInitScenarioElement, "azione");

        ActionSequence scenarioSequence = new ActionSequence(scenarioName);

        Set<String> affectedMethodNames = new HashSet<>(4);
        affectedMethodNames.add("setNorth");
        affectedMethodNames.add("setWest");
        affectedMethodNames.add("setSouth");
        affectedMethodNames.add("setEast");

        for(Element action : actionList)
        {
            if(affectedMethodNames.contains(getTagValue(action, "method")))
                scenarioSequence.append(parseActionLoadFromDB(action));
        }

        return scenarioSequence;
    }

    /**
     * Effettua il parsing di un'azione tra quelle consentite per il db
     * nello scenario d'inizializzazione di una stanza
     * (vedi {@link XmlParser#parseInitRoomDB(String, Element)}).
     *
     * @param actionElement elemento xml dell'azione di cui effettuare
     *                      il parsing
     * @return Runnable corrispondente all'azione di cui è effettuato
     * il parsing
     */
    private static Runnable parseActionLoadFromDB(Element actionElement)
    {
        // prendi nome metodo
        String methodName = getTagValue(actionElement, "method");
        LogOutputManager.logOutput("Azione da DB " + methodName, LogOutputManager.XML_COLOR);
        Runnable actionParsed;

        switch(methodName)
        {
            case "setNorth":
                actionParsed = parseSetNorthDB(actionElement);
                break;
            case "setWest":
                actionParsed = parseSetWestDB(actionElement);
                break;
            case "setEast":
                actionParsed = parseSetEastDB(actionElement);
                break;
            case "setSouth":
                actionParsed = parseSetSouthDB(actionElement);
                break;
            default:
                throw new GameException("metodo non valido");
        }

        return actionParsed;
    }

    /**
     * Effettua il parsing di un'azione d'impostazione stanza adiacente
     * per uno scenario di caricamento dal db.
     *
     * Nota: non viene eseguito lo scenario di inizializzazione della stanza
     * adiacente, diversamente da quanto avviene nel parsing di un'azione d'impostazione
     * stanza adiacente per uno scenario non destinato al caricamento dal db.
     *
     * Sintassi azione:
     * <ul>
     *     <li>Tag "subject": nome della Room soggetto dell'azione</li>
     *     <li>Tag "what": nome della Room oggetto dell'azione</li>
     *     <li>Tag "method": uno tra "setNorth", "setWest", "setEast", "setSouth"</li>
     * </ul>
     *
     * @param cardinal direzione della Room oggetto dal punto di vista
     *                 della Room oggetto.
     * @param eAction elemento xml dell'azione di cui effettuare il parsing
     * @return Runnable corrispondente all'azione
     */
    private static Runnable parseSetAdjacentRoomDB(Room.Cardinal cardinal, Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        String roomName = getTagValue(eAction, "what");

        if(GameManager.getRoom(roomName) == null)
        {
            Room room = XmlLoader.loadRoom(roomName);

            return () ->
            {
                GameManager.getRoom(subject).setAdjacentRoom(cardinal, room);
                GameManager.continueScenario();
            };
        }
        else
        {
            Room room = GameManager.getRoom(roomName);
            return () ->
            {
                GameManager.getRoom(subject).setAdjacentRoom(cardinal, room);
                GameManager.continueScenario();
            };
        }
    }

    private static Runnable parseSetEastDB(Element eAction)
    {
        return parseSetAdjacentRoomDB(Room.Cardinal.EAST, eAction);
    }

    private static Runnable parseSetWestDB(Element eAction)
    {
        return parseSetAdjacentRoomDB(Room.Cardinal.WEST, eAction);
    }

    private static Runnable parseSetNorthDB(Element eAction)
    {
        return parseSetAdjacentRoomDB(Room.Cardinal.NORTH, eAction);
    }

    private static Runnable parseSetSouthDB(Element eAction)
    {
        return parseSetAdjacentRoomDB(Room.Cardinal.SOUTH, eAction);
    }

    /**
     * Effettua il parsing di uno scenario partendo dall'elemento xml il cui
     * root tag è "scenario".
     *
     * Se {@code scenarioElement} non contiene elementi con il tag "azione" allora
     * viene creato uno scenario vuoto tramite {@link ActionSequence#voidScenario()};
     * inoltre, se lo scenario contiene un elemento col tag "executeScenario" allora viene
     * caricato lo scenario con il path corrispondente al valore dell'elemento e
     * la sua esecuzione viene posta come ultima azione dello scenario di cui si sta
     * effettuando il parsing.
     *
     * @param scenarioName nome da assegnare allo scenario (solo fini documentativi)
     * @param scenarioElement elemento xml corrispondente allo scenario di cui si vuole
     *                        effettuare il parsing
     * @return ActionSequence corrispondente allo scenario
     */
    static ActionSequence parseScenario(String scenarioName, Element scenarioElement)
    {

        ActionSequence scenarioSequence = new ActionSequence(scenarioName);

        // ottieni lista di azioni
        List<Element> actionList = XmlParser.getTagsList(scenarioElement, "azione");

        for(Element action : actionList)
        {
            scenarioSequence.append(parseAction(action));
        }


        // prendi scenario eventuale alla fine
        Optional<String> nextScenario = getOptionalTagValue(scenarioElement, "executeScenario");

        if(nextScenario.isPresent())
        {
            String nextScenarioPath = nextScenario.get();
            scenarioSequence.append(() -> GameManager.startScenario(loadScenario(nextScenarioPath)));
        }

        if(scenarioSequence.length() == 0)
            scenarioSequence = ActionSequence.voidScenario();

        return scenarioSequence;
    }

    /**
     * Effettua il parsing di un elemento xml corrispondente a un'azione.
     *
     * Ogni elemento di questo tipo deve contenere al suo interno un elemento
     * col tag "method" e con valore corrispondente a una delle azioni consentite
     * dal gioco.
     *
     * @param actionElement elemento xml corrispondente a un'azione
     * @return Runnable corrispondente all'azione di cui si effettua il parsing
     */
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
            case "effectAnimation":
                actionParsed = parseEffectAnimation(actionElement);
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
            case "lockEntrance":
                actionParsed = parseLockEntrance(actionElement);
                break;
            case "open":
                actionParsed = parseOpen(actionElement);
                break;
            case "makeSchwartzRobot":
                actionParsed = parseMakeSchwartzRobot(actionElement);
                break;
            case "removeFromInventory":
                actionParsed = parseRemoveFromInventory(actionElement);
                break;
            case "describeRandomCocktail":
                actionParsed = parseDescribeRandomCocktail(actionElement);
                break;
            default:
                throw new GameException("XML contiene metodo " + methodName + " non valido");
        }

        return actionParsed;
    }

    // TODO: continuare da qua

    /**
     * Esegue il parsing di un elemento azione xml il cui "method" è "move"
     *
     * I tag richiesti per questo comando sono:
     * <ul>
     *    <li>"subject": nome del soggetto dell'azione (GamePiece)</li>
     *    <li>"x": ascissa di blocco in cui subject deve muoversi</li>
     *
     *    <li>"y": ordinata di blocco in cui subject deve muoversi</li>
     *
     *    <li>"how": "absolute" se (x, y) sono coordinate assolute;
     *    "relative" se sono relative alla posizione attuale di subject</li>
     *
     *    <li>"finalWait" numero di millisecondi da aspettare
     *    dopo l'esecuzione dell'animazione di movimento</li>
     * </ul>
     * @param eAction elemento corrispondente all'azione xml
     * @return Runnable associata all'azione
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

        // lo scenario viene mandato avanti dal thread dell'animazione creata
        return () -> (GameManager.getPiece(subjectName)).move(new BlockPosition(x, y), type, millisecondEndWait);
    }


    /**
     * Esegue il parsing di un elemento azione xml il cui "method" è "move"
     *
     * I tag richiesti per questo comando sono:
     * <ul>
     *    <li>"subject": nome del soggetto dell'azione (GameCharacter)</li>
     *    <li>"sentence": frase pronunciata (al massimo una newline, espressa con "(*)")</li>
     * </ul>
     * @param eAction elemento corrispondente all'azione xml
     * @return Runnable associata all'azione
     */
    private static Runnable parseSpeak(Element eAction)
    {
        String subjectName = getTagValue(eAction, "subject");
        // ricava stringa da stampare
        String sentence = getTagValue(eAction, "sentence");
        // formatta stringa (spazi)
        String sentenceNewLined = formatForTextBar(sentence);

        // lo scenario viene mandato avanti dall'input utente (chiusura textBar)
        return () -> ((GameCharacter) GameManager.getPiece(subjectName)).speak(sentenceNewLined);
    }

    /**
     * Formatta una stringa che dev'essere visualizzata sulla textBar.
     * <ul>
     *     <li>Vengono eliminati gli spazi ai bordi</li>
     *     <li>la sequenza di caratteri "(*)" viene rimpiazzata con \n</li>
     * </ul>
     * @param s stringa da formattare
     * @return stringa risultato, formattata per essere visualizzata sulla textBar
     */
    private static String formatForTextBar(String s)
    {
        return s.trim().replaceAll("\\s\\(\\*\\)\\s", "\n");
    }

    private static Runnable parseItemSpeak(Element eAction)
    {
        String subjectName = getTagValue(eAction, "subject");
        // ricava stringa da stampare
        String sentence = getTagValue(eAction, "sentence");
        // formatta stringa (spazi)
        String sentenceNewLined = sentence.trim().replaceAll("\\s\\(\\*\\)\\s", "\n");
        String toPrint = subjectName + ": " + sentenceNewLined;

        // lo scenario viene mandato avanti dall'input utente (chiusura textBar)
        // TODO: fix code
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

        // scenario viene mandato avanti qui
        return () ->
        {
            piece.removeFromRoom();
            piece.addInRoom(GameManager.getRoom(subject), new BlockPosition(x, y));
            GameManager.continueScenario();
            // TODO: controllare correttezza nel caso in cui venga aggiunto schwartz
        };

    }

    private static Runnable parseAnimate(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");

        // scenario portato avanti dall'animazione
        return () -> GameManager.getPiece(subject).animate();
    }

    private static Runnable parseAnimateReverse(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");

        // scenario portato avanti dall'animazione
        return () -> GameManager.getPiece(subject).animateReverse();
    }

    /*
    private static Runnable parseAddPickup(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");

        String pickupName = getTagValue(eAction, "what");
        PickupableItem pickup = (PickupableItem) GameManager.getPiece(pickupName);

        // scenario portato avanti qui
        return () ->
        {
            ((Container) GameManager.getPiece(subject)).addPickup(pickup);
            GameManager.continueScenario();
        };
    }

     */

    private static Runnable parseSetAdjacentRoom(Element eAction, Room.Cardinal cardinal)
    {
        String subject = getTagValue(eAction, "subject");

        String roomName = getTagValue(eAction, "what");
        if(GameManager.getRoom(roomName) == null)
        {
            Room room = XmlLoader.loadRoom(roomName);
            ActionSequence roomScenario = XmlLoader.loadRoomInit(roomName);

            return () ->
            {
                GameManager.getRoom(subject).setAdjacentRoom(cardinal, room);
                GameManager.startScenario(roomScenario);
            };
        }
        else
        {
            Room room = GameManager.getRoom(roomName);
            return () ->
            {
                GameManager.getRoom(subject).setAdjacentRoom(cardinal, room);
                GameManager.continueScenario();
            };
        }
    }

    private static Runnable parseSetEast(Element eAction)
    {
        return parseSetAdjacentRoom(eAction, Room.Cardinal.EAST);
    }

    private static Runnable parseSetWest(Element eAction)
    {
        return parseSetAdjacentRoom(eAction, Room.Cardinal.WEST);
    }

    private static Runnable parseSetNorth(Element eAction)
    {
        return parseSetAdjacentRoom(eAction, Room.Cardinal.NORTH);
    }

    private static Runnable parseSetSouth(Element eAction)
    {
        return parseSetAdjacentRoom(eAction, Room.Cardinal.SOUTH);
    }


    private static Runnable parseRemoveFromRoom(Element eAction)
    {
        String subject = getTagValue(eAction,"subject");

        // lo scenario viene mandato avanti qui
        return () ->
        {
            GameManager.getPiece(subject).removeFromRoom();
            GameManager.continueScenario();
        };
    }

    private static Runnable parseAddToInventory(Element eAction)
    {
        String itemName = getTagValue(eAction, "what");
        PickupableItem it = (PickupableItem) XmlLoader.loadPiece(itemName);

        // lo scenario viene mandato avanti qui
        return () ->
        {
            PlayingCharacter.getPlayer().addToInventory(it);
            GameManager.continueScenario();
        };

    }


    private static Runnable parsePlayMusic(Element eAction)
    {
        String musicPath = getTagValue(eAction, "what");

        // lo scenario viene mandato avanti qui
        return () ->
        {
            SoundHandler.playWav(musicPath, SoundHandler.Mode.MUSIC);
            GameManager.continueScenario();
        };
    }

    private static Runnable parsePlayScenarioSound(Element eAction)
    {
        String soundPath = getTagValue(eAction, "what");

        // lo scenario viene mandato avanti nel soundhandler
        return () -> SoundHandler.playWav(soundPath, SoundHandler.Mode.SCENARIO_SOUND);
    }

    private static Runnable parseSetScenarioOnEnter(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        String scenarioPath = getTagValue(eAction, "what");

        // lo scenario viene mandato avanti qui
        return () ->
        {
            (GameManager.getRoom(subject)).setScenarioOnEnter(scenarioPath);
            GameManager.continueScenario();
        };
    }

    private static Runnable parseLoadFloor(Element eAction)
    {
        String floorName = getTagValue(eAction, "floor");

        final String MIST_PATH = "src/main/resources/scenari/piano MIST/MIST-A.xml";
        final String MIST_NAME = "Mist-A";
        final String ALU_PATH = "src/main/resources/scenari/piano ALU/ALU-A.xml";
        final String ALU_NAME = "ALUA";
        final String BUG_PATH = "src/main/resources/scenari/piano BUG/BUG-A.xml";
        final String BUG_NAME = "Bug-A";
        final String SERVER_PATH = "src/main/resources/scenari/piano SERVER/ServerRoom.xml";
        final String SERVER_NAME = "ServerRoom";
        final String EBERT_PATH = "src/main/resources/scenari/piano EBERT/ebert-a.xml";
        final String EBERT_NAME = "Ebert-A";
        final String FLASH_PATH = "src/main/resources/scenari/piano FLASH/FlashRoom.xml";
        final String FLASH_NAME = "FlashRoom";
        final String FINALE_PATH = "src/main/resources/scenari/piano FLASH/FlashRoomFinale.xml";
        final String FINALE_NAME = "FlashRoomFinale";

        String floorPath;
        String startingRoomName;

        switch (floorName)
        {
            case "MIST":
                floorPath = MIST_PATH;
                startingRoomName = MIST_NAME;
                break;
            case "ALU":
                floorPath = ALU_PATH;
                startingRoomName = ALU_NAME;
                break;
            case "BUG":
                floorPath = BUG_PATH;
                startingRoomName = BUG_NAME;
                break;
            case "SERVER":
                floorPath = SERVER_PATH;
                startingRoomName = SERVER_NAME;
                break;
            case "EBERT":
                floorPath = EBERT_PATH;
                startingRoomName = EBERT_NAME;
                break;
            case "FLASH":
                floorPath = FLASH_PATH;
                startingRoomName = FLASH_NAME;
                break;
            case "FINALE":
                floorPath = FINALE_PATH;
                startingRoomName = FINALE_NAME;
                break;
            default:
                throw new GameException("Piano non valido");
        }

        return () ->
        {
            if(GameManager.getRoom(startingRoomName) == null)
            {
                XmlLoader.loadRoom(floorPath);
                GameManager.startScenario(XmlLoader.loadRoomInit(floorPath));
            }
            else
            {
                GameManager.continueScenario();
            }
        };
    }

    private static Runnable parsePlayEmoji(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        String emojiName = getTagValue(eAction, "what");

        // lo scenario viene mandato avanti dall'animazione
        return () ->
        {
            ((GameCharacter) GameManager.getPiece(subject)).playEmoji(emojiName);
        };
    }

    private static Runnable parseSetState(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        String state = getTagValue(eAction, "state");

        // lo scenario viene mandato avanti qui
        return () ->
        {
            GameManager.getPiece(subject).setState(state);
            GameManager.continueScenario();
        };
    }

    private static Runnable parseExecuteTest(Element eAction)
    {
        String what = getTagValue(eAction, "what");

        // lo scenario viene mandato avanti dall'input di chiusura del JDialog del test inserito dall'utente
        switch (what)
        {
            case "ALU":
                int number = Integer.parseInt(getTagValue(eAction, "number"));
                return () -> LogicQuest.executeTest(number);
            case "MIST":
                return TestMist::executeTest;
            case "CAPTCHA":
                return Captcha::executeTest;
            default:
                throw new GameException("Nome del test non valido");
        }
    }

    private static Runnable parseSetCanUse(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        boolean canUse = Boolean.parseBoolean(getTagValue(eAction, "canUse"));

        // lo scenario viene mandato avanti qui
        return () ->
        {
            ((Item) GameManager.getPiece(subject)).setCanUse(canUse);
            GameManager.continueScenario();
        };
    }

    private static Runnable parseTeleport(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        String roomName = getTagValue(eAction, "where");

        // lo scenario viene mandato avanti in setCurrentRoom, dallo scenarioOnEnter
        return () ->
        {
            GameManager.getMainFrame().setCurrentRoom(GameManager.getRoom(roomName));
        };
    }

    private static Runnable parseAddRoomEffect(Element eAction)
    {
        String effectPath = getTagValue(eAction, "what");

        // lo scenario viene mandato avanti qui
        return () ->
        {
            GameManager.getMainFrame()
                .getGameScreenPanel()
                .addCurrentRoomEffect(SpriteManager.loadSpriteSheet(effectPath));

            GameManager.continueScenario();
        };
    }

    private static Runnable parseLockEntrance(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        Room.Cardinal cardinal = Room.Cardinal.valueOf(getTagValue(eAction, "cardinal").toUpperCase());
        boolean lock = Boolean.parseBoolean(getTagValue(eAction, "lock"));

        // lo scenario viene mandato avanti qui
        return () ->
        {
            GameManager.getRoom(subject).setAdjacentLocked(cardinal, lock);
            GameManager.continueScenario();
        };
    }

    private static Runnable parseOpen(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");

        return () -> ((Openable) GameManager.getPiece(subject)).open();
    }

    private static Runnable parseMakeSchwartzRobot(Element eAction)
    {
        return () ->
        {
            PlayingCharacter.makePlayerFinalForm();
            GameManager.continueScenario();
        };
    }

    private static Runnable parseRemoveFromInventory(Element eAction)
    {
        String what = getTagValue(eAction, "what");
        return () ->
        {
            PlayingCharacter.getPlayer().removeFromInventory(
                    (PickupableItem) GameManager.getPiece(what));
            GameManager.continueScenario();
        };
    }

    private static Runnable parseDescribeRandomCocktail(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");

        return () ->
        {
            GameCharacter speakingChar = (GameCharacter) GameManager.getPiece(subject);

            RecipeRestClient.Recipe cocktailRecipe = RecipeRestClient.generateRecipe();

            ActionSequence describeScenario = new ActionSequence("descrizione cocktail");
            describeScenario.append(() -> speakingChar.speak("For " + cocktailRecipe.getCategory() + " cocktail:"));

            for(String ingredient : cocktailRecipe.getIngredients())
                describeScenario.append(() -> speakingChar.speak(ingredient));

            GameManager.startScenario(describeScenario);
        };
    }

    /*
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
     *
    private static Runnable parseUpdateSprite(Element eAction)
    {
        String subject = getTagValue(eAction, "subject");
        GamePiece piece = GameManager.getPiece(subject);

        if(piece == null)
            throw new GameException("Piece non trovato");

        String spriteName = getTagValue(eAction, "spriteName");

        // lo scenario viene mandato avanti qui
        return () ->
        {
            piece.updateSprite(spriteName);
            GameManager.continueScenario();
        };

    }

     */

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
        boolean isPerpetual = Boolean.parseBoolean(getTagValue(eAction, "isPerpetual"));

        // lo scenario viene mandato avanti dall'animazione (o qui se essa è perpetua)f
        return () ->
        {
            GameManager.getPiece(subject).executeEffectAnimation(animationName, finalWait, isPerpetual);

            if(isPerpetual)
                GameManager.continueScenario();
        };

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
            throw new GameException("Errore nel caricamento dell'xml");
        }
    }

    /*

    /**
     * Crea una Room descritta nel file xml corrispondente.
     *
     * @param roomPath path del file xml contenente i dati della Room
     *                 (root tag: {@literal <stanza>})
     * @return stanza corrispondente

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

    @Deprecated
    public static ActionSequence loadRoomInit(String roomPath)
    {
        return XmlLoader.loadRoomInit(roomPath);
    }


     */

}
