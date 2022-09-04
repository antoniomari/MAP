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

public class DoorLike extends Item implements Openable
{
    private boolean isLocked;
    private boolean isOpen;

    private ActionSequence openScenario;
    private Map<String, String> openScenarioMap;

    private final ActionSequence successOpenScenario;
    private final ActionSequence closeScenario;


    // PATH SPRITESHEET (png + json)
    private final static String SPRITESHEET_PATH = "/img/tileset/porte.png";
    private final static String JSON_PATH = "/img/tileset/porte.json";

    // SPRITESHEET OGGETTI
    private final static BufferedImage SPRITESHEET = SpriteManager.loadSpriteSheet(SPRITESHEET_PATH);

    // ANIMAZIONI
    private List<Image> openFrames;
    private List<Image> closeFrames;


    public DoorLike(String name, String description)
    {
        super(name, description, SPRITESHEET, JSON_PATH);

        // inizializza frames di animazione
        initFrames();


        // crea scenario di apertura
        //crea scenario sequenziale animazione apertura + scenario effetto
        successOpenScenario = new ActionSequence("apertura + effetto",
                ActionSequence.Mode.SEQUENCE);
        successOpenScenario.append(
                () ->
                {
                    EventHandler.sendEvent(
                            new ItemInteractionEvent(this, "Si è aperta", getOpenFrames()));
                });
        successOpenScenario.append(
                () ->
                {
                    getLocationRoom().setAdjacentLocked(Room.Cardinal.NORTH, false);
                    GameManager.continueScenario();
                });

        closeScenario = new ActionSequence("chiusura + effetto", ActionSequence.Mode.SEQUENCE);
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

    public void loadOpenScenarios(Map<String, String> openScenarioMap)
    {
        this.openScenarioMap = openScenarioMap;
    }


    private void initFrames()
    {
        Image closed = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, getName() + "Closed");

        openFrames = SpriteManager.getKeywordOrderedFrames(SPRITESHEET, JSON_PATH, getName() + "Open");
        openFrames.add(0, closed);

        closeFrames = new ArrayList<>();

        for(int i = openFrames.size(); i > 0; i--)
            closeFrames.add(openFrames.get(i - 1));
    }

    public void setInitialState(boolean isOpen, boolean isLocked)
    {
        this.isOpen = isOpen;
        this.isLocked = isLocked;
    }

    @Override
    public void open()
    {
        if(isLocked)
            EventHandler.sendEvent(new ItemInteractionEvent(this, "Non si apre, è bloccata"));
        else
        {
            if(!isOpen)
            {
                if(state.equals("canOpen"))
                {
                    // cambia stato in open
                    isOpen = true;
                    // esegui scenario completo
                    GameManager.startScenario(successOpenScenario);
                }
                else if(openScenario != null)
                    GameManager.startScenario(openScenario);
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
