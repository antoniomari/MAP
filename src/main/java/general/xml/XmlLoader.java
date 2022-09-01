package general.xml;

import entity.GamePiece;
import entity.characters.GameCharacter;
import entity.characters.NPC;
import entity.characters.PlayingCharacter;
import entity.items.*;
import entity.rooms.Room;
import general.ActionSequence;
import general.GameException;
import general.GameManager;
import general.LogOutputManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.SequenceInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XmlLoader
{
    private static final String CHARACTER_XML_PATH = "src/main/resources/scenari/personaggi.xml";
    private static final String ITEM_XML_PATH = "src/main/resources/scenari/oggetti.xml";

    private static final Map<String, Element> pieceElementMap;

    static
    {
        pieceElementMap = new HashMap<>();

        Document characterXml = XmlParser.openXml(CHARACTER_XML_PATH);
        Document itemXml = XmlParser.openXml(ITEM_XML_PATH);


        List<Element> characterElementList = XmlParser.getTagsList(characterXml, "personaggio");
        List<Element> itemElementList = XmlParser.getTagsList(itemXml, "oggetto");

        // TODO: capire funzionamento flatMap
        List<Element> pieceElementList = Stream.of(characterElementList, itemElementList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        for(Element pieceElement : pieceElementList)
        {
            String name = XmlParser.getXmlAttribute(pieceElement, "nome");
            if(!pieceElementMap.containsKey(name))
                pieceElementMap.put(name, pieceElement);
            else
            {
                throw new GameException("GamePiece " + name + " duplicato");
            }
        }
    }

    /**
     * Restituisce il GamePiece sulla base del nome: se non è già stato caricato
     * in memoria allora lo cerca in "personaggi.xml" e in "oggetti.xml"
     *
     * @param name nome del GamePiece cercato
     * @return GamePiece cercato
     * @throws GameException se non esiste un GamePiece di nome {@code name}
     */
    public static GamePiece loadPiece(String name)
    {
        // 1: cerca in gameManager (caso in cui è già stato caricato in memoria)
        GamePiece piece = GameManager.getPiece(name);
        if(piece != null)
            return piece;

        Element pieceElement = pieceElementMap.get(name);

        if(pieceElement != null)
        {
            if(pieceElement.getNodeName().equals("oggetto"))
                piece = loadItem(pieceElement);
            else
                piece = loadCharacter(pieceElement);

            return piece;
        }
        else
        {
            // piece non presente, lancia eccezione
            throw new GameException("GamePiece " + name + " inesistente");
        }
    }

    /**
     * Carica un GameCharacter dal rispettivo file (personaggi.xml).
     *
     * Cerca il GameCharacter (elemento xml con tag {@literal <personaggio>} il cui nome
     * è {@code name}. Nella costruzione dell'oggetto utilizza il contenuto dei seguenti tag:
     * <ul>
     *     <li>{@literal <spritesheet>} path dello spritesheet del personaggio</li>
     *
     *    <li>{@literal <json>} path del json associato allo spritesheet del personaggio</li>
     * </ul>
     *
     * @param characterElement elemento XML del GameCharacter da caricare
     * @return il GameCharacter caricato, {@code null} se non esiste un GameCharacter
     * il cui nome sia {@code name}
     */
    private static GameCharacter loadCharacter(Element characterElement)
    {

        String name = XmlParser.getXmlAttribute(characterElement, "nome");

        String spritesheetPath = XmlParser.getTagValue(characterElement, "spritesheet");
        Optional<String> jsonPath = XmlParser.getOptionalTagValue(characterElement, "json");

        GameCharacter loaded;

        if(jsonPath.isPresent())
        {
            if(name.equals(PlayingCharacter.getPlayerName()))
                loaded = new GameCharacter(name, spritesheetPath, jsonPath.get());
            else
                loaded = new NPC(name, spritesheetPath, jsonPath.get());
        }
        else
        {
            loaded = new NPC(name, spritesheetPath);
        }

        // carica animazioni TODO
        // carica speakScenarios
        Element scenariosNode = (Element) characterElement.getElementsByTagName("speakScenarios").item(0);
        Map<String, String> scenarioPathMap = new HashMap<>();
        Map<String, String> sentenceSpeakMap = new HashMap<>();

        if(scenariosNode != null)
        {
            List<Element> scenarioPathList = XmlParser.getTagsList(scenariosNode, "scenario");

            for(Element scenarioElement : scenarioPathList)
            {
                String state = XmlParser.getXmlAttribute(scenarioElement, "state");
                scenarioPathMap.put(state, scenarioElement.getTextContent());
            }

            // leggi le sentence e crea scenari da esse
            List<Element> sentenceElementList = XmlParser.getTagsList(scenariosNode, "sentence");

            for(Element sentenceElement : sentenceElementList)
            {
                String state = XmlParser.getXmlAttribute(sentenceElement, "state");
                sentenceSpeakMap.put(state, sentenceElement.getTextContent());
            }
        }

        if(loaded instanceof NPC)
            ((NPC) loaded).loadSpeakScenarios(scenarioPathMap, sentenceSpeakMap);

        return loaded;
    }

    // TODO : documentazion e modularizzazione
    private static Item loadItem(Element itemElement)
    {

        String name = XmlParser.getXmlAttribute(itemElement, "nome");
        String className = XmlParser.getTagValue(itemElement, "classe");
        String description = XmlParser.getTagValue(itemElement, "descrizione");
        boolean canUse = Boolean.parseBoolean(XmlParser.getTagValue(itemElement, "canUse"));

        // TODO: caricare l'opportuna classe IMPORTANTEEEEE!"!!!!!!1
        Item itemToLoad;
        if(className.equals("Item"))
            itemToLoad = new Item(name, description, canUse);
        else if (className.equals("PickupableItem"))
            itemToLoad = new PickupableItem(name, description, canUse);
        else if (className.equals("DoorLike"))
            itemToLoad = new DoorLike(name, description);
        else if (className.equals("Container"))
            itemToLoad = new Container(name, description);
        else
            throw new GameException("Classe oggetto [" + className + "] ancora non supportata");
        // TODO : rimpiazzare if-else

        // caricamento animazione
        Optional<String> animationSpritesheetPath = XmlParser.getOptionalTagValue(itemElement, "animationSpritesheet");
        Optional<String> animationJsonPath = XmlParser.getOptionalTagValue(itemElement, "animationJson");

        // TODO: controlli??
        if(animationSpritesheetPath.isPresent())
        {
            itemToLoad.initAnimateFrames(animationSpritesheetPath.get(), animationJsonPath.get());
        }

        // carica animazione perpetua
        Optional<String> perpetualAnimationPath = XmlParser.getOptionalTagValue(itemElement,"animazionePerpetuaPng");
        Optional<String> perpetualAnimationJson = XmlParser.getOptionalTagValue(itemElement, "animazionePerpetuaJson");

        if(perpetualAnimationJson.isPresent())
        {
            itemToLoad.initPerpetualAnimationFrame(perpetualAnimationPath.get(), perpetualAnimationJson.get());
        }

        loadOnUse(itemElement, itemToLoad);

        loadOnOpen(itemElement, itemToLoad);

        loadOnUseWith(itemElement, itemToLoad);

        return itemToLoad;
    }

    private static void loadOnUse(Element itemElement, Item itemToLoad)
    {
        // setup onUse action
        Element onUseElement = (Element) itemElement.getElementsByTagName("onUse").item(0);
        Map<String, String> scenarioPathMap = new HashMap<>();

        if (onUseElement != null)
        {
            // imposta nome azione
            itemToLoad.setUseActionName(XmlParser.getTagValue(onUseElement, "actionName"));


            Optional<String> scenarioPath = XmlParser.getOptionalTagValue(onUseElement, "effetto");

            if(scenarioPath.isPresent())
            {
                // imposta useAction
                itemToLoad.setUseAction(XmlParser.loadScenario(scenarioPath.get()));
            }
            else
            {
                List<Element> effetti = XmlParser.getTagsList(onUseElement, "useScenario");

                for(Element effettoElement : effetti)
                {
                    String state = XmlParser.getXmlAttribute(effettoElement, "state");
                    scenarioPathMap.put(state, effettoElement.getTextContent());
                }
            }
        }
        itemToLoad.loadUseScenarios(scenarioPathMap);
    }

    private static void loadOnOpen(Element itemElement, Item itemToLoad)
    {
        // inferenza: se trovo questo tag allora è un doorLike TODO: aggiustare
        Element onOpenElement = (Element) itemElement.getElementsByTagName("onOpen").item(0);

        if (onOpenElement != null)
        {
            String scenarioPath = XmlParser.getTagValue(onOpenElement, "effetto");
            // imposta openEffect TODO: rinominare in setOpenAction
            ((Openable) itemToLoad).setOpenEffect(XmlParser.loadScenario(scenarioPath));

            if(itemToLoad.getClass() == DoorLike.class)
            {
                boolean isOpen = Boolean.parseBoolean(XmlParser.getTagValue(itemElement, "isOpen"));
                boolean isLocked = Boolean.parseBoolean(XmlParser.getTagValue(itemElement, "isLocked"));
                ((DoorLike) itemToLoad).setInitialState(isOpen, isLocked);
            }

        }
    }

    private static void loadOnUseWith(Element itemElement, Item itemToLoad)
    {
        Element onUseWithElement = (Element) itemElement.getElementsByTagName("onUseWith").item(0);

        // TODO : completare
        if (onUseWithElement != null)
        {
            String targetName = XmlParser.getTagValue(onUseWithElement, "target");
            Optional<String> targetInitState = XmlParser.getOptionalTagValue(onUseWithElement, "targetInitState");
            Optional<String> targetFinalState = XmlParser.getOptionalTagValue(onUseWithElement, "targetFinalState");

            Optional<String> keepOptional = XmlParser.getOptionalTagValue(onUseWithElement, "keep");
            if (keepOptional.isPresent())
            {
                boolean keep = Boolean.parseBoolean(keepOptional.get());
                ((PickupableItem) itemToLoad).setKeepOnUseWith(keep);
            }

            ((PickupableItem) itemToLoad).setTargetPiece(targetName,
                    targetInitState.orElse("init"),
                    targetFinalState.orElse("init"));

            /*
            Optional<String> methodName = XmlParser.getOptionalTagValue(onUseWithElement, "method");
            if(methodName.isPresent())
            {
                Method method;
                GamePiece target;
                try
                {
                    target = GameManager.getPiece(targetName);
                    method = target.getClass().getMethod(methodName.get());
                }
                catch(NoSuchMethodException e)
                {
                    throw new GameException("metodo non trovato");
                }

                // TODO: generalizzare
                ActionSequence useWithScenario = new ActionSequence("useWithScenario", ActionSequence.Mode.INSTANT);
                useWithScenario.append(() ->
                {
                    try
                    {
                        method.invoke(target);
                    } catch (IllegalAccessException | InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }
                });

                ((PickupableItem) itemToLoad).setUseWithAction(useWithScenario);
            }
            else
            {

             */
            String scenarioPath = XmlParser.getTagValue(onUseWithElement, "scenario");
            ActionSequence useWithScenario = XmlParser.loadScenario(scenarioPath);

            ((PickupableItem) itemToLoad).setUseWithAction(useWithScenario);


        }
    }


    /**
     * Crea una Room descritta nel file xml corrispondente.
     *
     * @param roomPath path del file xml contenente i dati della Room
     *                 (root tag: {@literal <stanza>})
     * @return stanza corrispondente
     */
    public static Room loadRoom(String roomPath)
    {
        Document roomXml = XmlParser.openXml(roomPath);

        // creazione stanza
        Element roomElement = roomXml.getDocumentElement();
        String name = XmlParser.getXmlAttribute(roomElement, "nome");

        if(GameManager.getRoom(name) != null) // se la room è stata già caricata TODO: documentare
            return GameManager.getRoom(name);
        else
        {
            String pngPath = XmlParser.getTagValue(roomElement, "png");
            String jsonPath = XmlParser.getTagValue(roomElement, "json");
            String musicPath = XmlParser.getTagValue(roomElement, "musica");

            LogOutputManager.logOutput("Caricando stanza " + name, LogOutputManager.XML_COLOR);

            Room room = new Room(name, pngPath, jsonPath, musicPath);
            room.setXmlPath(roomPath);

            return room;
        }
    }

    /**
     * Carica lo scenario d'inizializzazione della stanza, contenuto
     * nel file xml della stessa.
     *
     * @param roomPath path del file xml contenente i dati della Room
     * @return ActionSequence che comprende le azioni d'inizializzazione stanza
     */
    public static ActionSequence loadRoomInit(String roomPath)
    {
        Document roomXml = XmlParser.openXml(roomPath);
        Node scenarioNode = roomXml.getElementsByTagName("scenario").item(0);

        return XmlParser.parseScenario("init Room " + roomPath, (Element) scenarioNode);
    }


    public static ActionSequence loadRoomInitDB(String roomPath)
    {
        Document roomXml = XmlParser.openXml(roomPath);
        Node scenarioNode = roomXml.getElementsByTagName("scenario").item(0);

        return XmlParser.parseInitRoomDB("init Room " + roomPath, (Element) scenarioNode);
    }
}
