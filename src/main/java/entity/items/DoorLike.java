package entity.items;

import entity.rooms.Room;
import general.GameException;
import general.GameManager;
import general.ActionSequence;
import events.EventHandler;
import events.ItemInteractionEvent;
import general.xml.XmlParser;
import graphics.SpriteManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class DoorLike extends Item implements Openable
{
    /** Path dello sprite-sheet per gli oggetti DoorLike. */
    private final static String SPRITESHEET_PATH = "/img/tileset/porte.png";
    /** Path del json associato allo sprite-sheet per gli oggetti DoorLike. */
    private final static String JSON_PATH = "/img/tileset/porte.json";
    /** Sprite-sheet per gli oggetti DoorLike. */
    private final static BufferedImage SPRITESHEET = SpriteManager.loadSpriteSheet(SPRITESHEET_PATH);

    /** Flag che indica se this è aperto. */
    private boolean isOpen;
    /** Scenario da eseguire all'interazione "open". */
    private ActionSequence openScenario;
    /**
     * Dizionario stato->pathScenario per associare lo scenario da eseguire all'interazione "open"
     * a seconda dello stato in cui si trova this.
     */
    private Map<String, String> openScenarioMap;
    /** Scenario di apertura eseguire all'interazione "open" quando lo stato di this è "canOpen". */
    private ActionSequence successOpenScenario;
    /** Scenario di chiusura, da eseguire all'interazione "close". */
    private ActionSequence closeScenario;
    /** Frames per l'animazione di apertura. */
    private List<Image> openFrames;
    /** Frames per l'animazione di chiusura. */
    private List<Image> closeFrames;


    /**
     * Crea un oggetto DoorLike.
     *
     * @param name nome da assegnare al DoorLike
     * @param description descrizione da assegnare al DoorLike
     */
    public DoorLike(String name, String description)
    {
        super(name, description, SPRITESHEET, JSON_PATH, false);

        // inizializza frames di animazione
        initFrames();
        initScenarios();
    }

    /**
     * Inizializzazione frames di animazione {@link DoorLike#openFrames}
     * e {@link DoorLike#closeFrames}.
     */
    private void initFrames()
    {
        Image closed = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, getName() + "Closed");

        openFrames = SpriteManager.getKeywordOrderedFrames(SPRITESHEET, JSON_PATH, getName() + "Open");
        openFrames.add(0, closed);

        closeFrames = new ArrayList<>();

        for(int i = openFrames.size(); i > 0; i--)
            closeFrames.add(openFrames.get(i - 1));
    }

    /**
     * Inizializzazione scenari di apertura e di chiusura.
     */
    private void initScenarios()
    {
        // crea scenario di apertura
        //crea scenario sequenziale animazione apertura + scenario effetto
        successOpenScenario = new ActionSequence("apertura + effetto");
        successOpenScenario.append(
                () -> EventHandler.sendEvent(
                                new ItemInteractionEvent(this, "Si è aperta", getOpenFrames())));
        successOpenScenario.append(
                () ->
                {
                    getLocationRoom().setAdjacentLocked(Room.Cardinal.NORTH, false);
                    GameManager.continueScenario();
                });

        closeScenario = new ActionSequence("chiusura + effetto");
        closeScenario.append(
                () ->
                {
                    EventHandler.sendEvent(
                            new ItemInteractionEvent(this, "Si è chiusa", getCloseFrames()));
                    GameManager.continueScenario();
                });
        closeScenario.append(
                () ->
                {
                    getLocationRoom().setAdjacentLocked(Room.Cardinal.NORTH, true);
                    GameManager.continueScenario();
                }
        );
    }

    @Override
    public void setState(String state)
    {
        super.setState(state);

        // imposta useScenario in base allo state
        String scenarioPath = openScenarioMap.get(state);
        if(scenarioPath != null)
            openScenario = XmlParser.loadScenario(scenarioPath);
    }

    @Override
    public void loadOpenScenarios(Map<String, String> openScenarioMap)
    {
        Objects.requireNonNull(openScenarioMap);
        this.openScenarioMap = openScenarioMap;
    }

    @Override
    public void open()
    {

        if(!isOpen)
        {
            // controlla caso apertura
            if(state.equals("canOpen"))
            {
                // cambia stato in open
                isOpen = true;
                // esegui scenario completo
                GameManager.startScenario(successOpenScenario);
            }
            // controlla scenario impostato
            else if(openScenario != null)
                GameManager.startScenario(openScenario);
            // controlla nel dizionario
            else if(openScenarioMap.containsKey(state))
            {
                openScenario = XmlParser.loadScenario(openScenarioMap.get(state));
                GameManager.startScenario(openScenario);
            }
            else
            {
                throw new GameException("Scenario non disponibile per l'apertura di " + getName());
            }
        }
    }

    @Override
    public void close()
    {
        if(isOpen())
        {
            GameManager.startScenario(closeScenario);
            // cambia stato in close
            isOpen = false;
        }
    }

    @Override
    public List<Image> getOpenFrames()
    {
        return openFrames;
    }

    @Override
    public List<Image> getCloseFrames()
    {
        return closeFrames;
    }

    @Override
    public boolean isOpen()
    {
        return isOpen;
    }
}
