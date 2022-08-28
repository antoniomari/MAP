package general.xml;

import entity.GamePiece;
import entity.characters.GameCharacter;
import entity.characters.NPC;
import entity.characters.PlayingCharacter;
import entity.items.*;
import general.ActionSequence;
import general.GameException;
import general.GameManager;
import general.xml.XmlLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PieceLoader
{
    /**
     * Restituisce il GamePiece sulla base del nome: se non è già stato caricato
     * in memoria allora lo cerca in "personaggi.xml" e in "oggetti.xml"
     *
     * @param name nome del GamePiece cercato
     * @return GamePiece cercato
     * @throws GameException se non esiste un GamePiece di nome {@code name}
     */
    private static GamePiece loadPiece(String name)
    {
        // 1: cerca in gameManager (caso in cui è già stato caricato in memoria)
        GamePiece piece = GameManager.getPiece(name);
        if(piece != null)
            return piece;

        // cerca tra i personaggi (file xml)
        piece = loadCharacter(name);
        if(piece != null)
            return piece;

        // cerca tra gli oggetti (file xml)
        piece = loadItem(name);
        if(piece != null)
            return piece;

        // piece non presente, lancia eccezione
        throw new GameException("GamePiece inesistente");
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
     * @param name nome del GameCharacter da caricare
     * @return il GameCharacter caricato, {@code null} se non esiste un GameCharacter
     * il cui nome sia {@code name}
     */
    private static GameCharacter loadCharacter(String name)
    {
        Document characterXml = XmlLoader.openXml("src/main/resources/scenari/personaggi.xml");

        List<Element> characterNodeList = XmlLoader.getTagsList((Element) characterXml, "personaggio");

        // per ogni personaggio controlla l'atttibuto "nome"
        //for(int i = 0; i < characterNodeList.getLength(); i++)
        for(Element characterElement : characterNodeList)
        {
            String elementName = XmlLoader.getXmlAttribute(characterElement, "nome");

            if (elementName.equals(name))
            {
                String spritesheetPath = XmlLoader.getTagValue(characterElement, "spritesheet");
                Optional<String> jsonPath = XmlLoader.getOptionalTagValue(characterElement, "json");

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

                // carica speakScenarios
                Element scenariosNode = (Element) characterElement.getElementsByTagName("speakScenarios").item(0);
                Map<String, String> scenarioPathMap = new HashMap<>();

                if(scenariosNode != null)
                {
                    NodeList scenarioPathList = scenariosNode.getElementsByTagName("scenario");


                    for(int j = 0; j < scenarioPathList.getLength(); j++)
                    {
                        Element element = (Element) scenarioPathList.item(j);
                        String state = XmlLoader.getXmlAttribute(element, "state");
                        scenarioPathMap.put(state, element.getTextContent());
                    }
                }


                if(loaded instanceof NPC)
                    ((NPC) loaded).loadSpeakScenarios(scenarioPathMap);

                return loaded;

            }
        }
        // personaggio non presente
        return null;
    }

    // TODO : documentazion e modularizzazione
    private static Item loadItem(String name)
    {
        Document itemXml = XmlLoader.openXml("src/main/resources/scenari/oggetti.xml");

        List<Element> itemNodeList = XmlLoader.getTagsList((Element) itemXml, "oggetto");

        // per ogni oggetto cerca
        //for(int i = 0; i < itemNodeList.getLength(); i++)
        for(Element itemElement : itemNodeList)
        {
            String elementName = XmlLoader.getXmlAttribute(itemElement, "nome");

            if (elementName.equals(name))
            {
                String className = XmlLoader.getTagValue(itemElement, "classe");
                String description = XmlLoader.getTagValue(itemElement, "descrizione");
                boolean canUse = Boolean.parseBoolean(XmlLoader.getTagValue(itemElement, "canUse"));

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
                else if (className.equals("TriggerableItem"))
                    itemToLoad = new TriggerableItem(name, description, false);
                else
                    throw new GameException("Classe oggetto [" + className + "] ancora non supportata");
                // TODO : rimpiazzare if-else

                // caricamento animazione
                Optional<String> animationSpritesheetPath = XmlLoader.getOptionalTagValue(itemElement, "animationSpritesheet");
                Optional<String> animationJsonPath = XmlLoader.getOptionalTagValue(itemElement, "animationJson");

                // TODO: controlli??
                if(animationSpritesheetPath.isPresent())
                {
                    itemToLoad.initAnimateFrames(animationSpritesheetPath.get(), animationJsonPath.get());
                }


                loadOnUse(itemElement, itemToLoad);

                /*
                // setup onTrigger action
                Element onTriggerElement = (Element) itemElement.getElementsByTagName("onTrigger").item(0);
                // TODO: aggiungere directlyTriggered
                if(onTriggerElement != null)
                {
                    String scenarioPath = XmlLoader.getTagValue(onTriggerElement, "effetto");
                    // imposta trigger action
                    ((Triggerable) itemToLoad).setTriggerScenario(XmlLoader.loadScenario(scenarioPath));
                    // imposta nome azione
                }

                */

                loadOnOpen(itemElement, itemToLoad);

                loadOnUseWith(itemElement, itemToLoad);


                return itemToLoad;
            }
        }

        // non trovato
        return null;
    }

    private static void loadOnUse(Element itemElement, Item itemToLoad)
    {
        // setup onUse action
        Element onUseElement = (Element) itemElement.getElementsByTagName("onUse").item(0);

        if (onUseElement != null)
        {
            /*
            String scenarioPath = XmlLoader.getTagValue(onUseElement, "effetto");
            // imposta useAction
            itemToLoad.setUseAction(XmlLoader.loadScenario(scenarioPath));

            // imposta nome azione
            itemToLoad.setUseActionName(XmlLoader.getTagValue(onUseElement, "actionName"));

             */

            List<Element> effetti = XmlLoader.getTagsList(onUseElement, "effetto");
        }



    }

    private static void loadOnOpen(Element itemElement, Item itemToLoad)
    {
        // inferenza: se trovo questo tag allora è un doorLike TODO: aggiustare
        Element onOpenElement = (Element) itemElement.getElementsByTagName("onOpen").item(0);

        if (onOpenElement != null)
        {
            String scenarioPath = XmlLoader.getTagValue(onOpenElement, "effetto");
            // imposta openEffect TODO: rinominare in setOpenAction
            ((Openable) itemToLoad).setOpenEffect(XmlLoader.loadScenario(scenarioPath));

            if(itemToLoad.getClass() == DoorLike.class)
            {
                boolean isOpen = Boolean.parseBoolean(XmlLoader.getTagValue(itemElement, "isOpen"));
                boolean isLocked = Boolean.parseBoolean(XmlLoader.getTagValue(itemElement, "isLocked"));
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
            String targetName = XmlLoader.getTagValue(onUseWithElement, "target");
            Optional<String> targetInitState = XmlLoader.getOptionalTagValue(onUseWithElement, "targetInitState");
            Optional<String> targetFinalState = XmlLoader.getOptionalTagValue(onUseWithElement, "targetFinalState");

            Optional<String> keepOptional = XmlLoader.getOptionalTagValue(onUseWithElement, "keep");
            if (keepOptional.isPresent())
            {
                boolean keep = Boolean.parseBoolean(keepOptional.get());
                ((PickupableItem) itemToLoad).setKeepOnUseWith(keep);
            }

            ((PickupableItem) itemToLoad).setTargetPiece(targetName,
                    targetInitState.orElse("init"),
                    targetFinalState.orElse("init"));

            Optional<String> methodName = XmlLoader.getOptionalTagValue(onUseWithElement, "method");
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
                String scenarioPath = XmlLoader.getTagValue(onUseWithElement, "scenario");
                ActionSequence useWithScenario = XmlLoader.loadScenario(scenarioPath);

                ((PickupableItem) itemToLoad).setUseWithAction(useWithScenario);
            }

        }
    }
}
