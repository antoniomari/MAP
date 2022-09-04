package entity.items;

import general.ActionSequence;
import entity.GamePiece;
import events.EventHandler;
import events.ItemInteractionEvent;
import general.GameManager;
import general.xml.XmlParser;
import graphics.SpriteManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class Item extends GamePiece implements Observable
{
    /** Descrizione dell'oggetto. Caricata dall'XML dell'oggetto. */
    private final String description;

    // PATH TILESET OGGETTI
    private final static String OBJECT_SPRITESHEET_PATH = "/img/tileset/oggetti.png";
    // PATH JSON RELATIVO AL TILESET
    private final static String JSON_PATH = "/img/tileset/oggetti.json";

    // SPRITESHEET OGGETTI
    private static final BufferedImage SPRITESHEET;

    /** Flag che indica se è abilitata l'azione specifica dell'oggetto. */
    private boolean canUse;

    // modalità: è utilizzabile una volta//infinite volte
    private int usability = USE_INFTY;

    // default per il nome dell'azione (viene modificato con il setter)
    private String useActionName = "Usa";
    private ActionSequence useScenario;

    private Map<String, String> useScenarioMap;


    public static final int USE_ONCE = 1;
    public static final int USE_INFTY = 2;


    // CARICAMENTO SPRITESHEET IN MEMORIA
    static
    {
        SPRITESHEET = SpriteManager.loadSpriteSheet(OBJECT_SPRITESHEET_PATH);
    }

    public void loadUseScenarios(Map<String, String> useScenarioMap)
    {
        this.useScenarioMap = useScenarioMap;
    }

    @Override
    public void setState(String state)
    {
        super.setState(state);

        // imposta useScenario in base allo state
        String scenarioPath = useScenarioMap.get(state);
        if(scenarioPath != null)
            useScenario = XmlParser.loadScenario(scenarioPath);
    }

    public Item(String name, String description)
    {
        this(name, description, false);
    }

    public Item(String name, String description, boolean canUse)
    {
        this(name, description, SPRITESHEET, JSON_PATH, canUse);
    }

    protected Item(String name, String description, BufferedImage spriteSheet, String jsonPath)
    {
        this(name, description, spriteSheet, jsonPath, false);
    }

    protected Item(String name, String description, BufferedImage spriteSheet, String jsonPath, boolean canUse)
    {
        super(name, spriteSheet, jsonPath);
        this.description = description;
        this.canUse = canUse;
    }

    public void setUsability(int usability)
    {
        if(usability == USE_ONCE)
            this.usability = USE_ONCE;
        else if (usability == USE_INFTY)
            this.usability = USE_INFTY;
        else
            throw new IllegalArgumentException("usability non valida");
    }

    public void setUseAction(ActionSequence useScenario)
    {
        this.useScenario = useScenario;
    }

    public void use()
    {
        if(canUse)
        {
            if(useScenario != null)
                GameManager.startScenario(useScenario);

            else if(useScenarioMap.containsKey(state))
            {
                useScenario = XmlParser.loadScenario(useScenarioMap.get(state));
                GameManager.startScenario(useScenario);
            }
            if(usability == USE_ONCE)
                canUse = false;
        }

    }

    public String getUseActionName()
    {
        return useActionName;
    }

    public void setUseActionName(String s)
    {
        this.useActionName = s;
    }

    public void setCanUse(boolean canUse)
    {
        this.canUse = canUse;
    }

    public boolean canUse()
    {
        return canUse;
    }

    public void observe()
    {
        EventHandler.sendEvent(new ItemInteractionEvent(this, ItemInteractionEvent.Type.OBSERVE));
    }

    public String getDescription()
    {
        return description;
    }

}
