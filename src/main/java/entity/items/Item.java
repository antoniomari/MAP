package entity.items;

import general.ActionSequence;
import entity.GamePiece;
import events.EventHandler;
import events.ItemInteractionEvent;
import general.GameManager;
import general.xml.XmlParser;
import graphics.SpriteManager;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Classe che rappresenta un oggetto del gioco.
 */
public class Item extends GamePiece implements Observable
{
    /** Path dello sprite-sheet degli oggetti. */
    private final static String OBJECT_SPRITESHEET_PATH = "/img/tileset/oggetti.png";
    /** Path del json associato allo sprite-sheet. */
    private final static String JSON_PATH = "/img/tileset/oggetti.json";
    /** Sprite-sheet degli oggetti (caricato staticamente). */
    private static final BufferedImage SPRITESHEET;

    /** Descrizione dell'oggetto. Caricata dall'XML dell'oggetto. */
    private final String description;
    /** Flag che indica se è abilitata l'azione specifica dell'oggetto. */
    private boolean canUse;

    /** Nome dell'interazione "usa" personalizzata dell'oggetto. */
    private String useActionName = "Usa";
    /** Scenario eseguito al momento dell'interazione "usa". */
    private ActionSequence useScenario;
    /** Dizionario stato->scenario da eseguire (interazione "usa") associato allo stato. */
    private Map<String, String> useScenarioMap;

    // CARICAMENTO SPRITESHEET IN MEMORIA
    static
    {
        SPRITESHEET = SpriteManager.loadImage(OBJECT_SPRITESHEET_PATH);
    }

    /**
     * Crea un Item.
     *
     * @param name nome da dare all'Item
     * @param description descrizione da dare all'Item
     * @param canUse {@code true} se è inizialmente possibile eseguire
     *                           l'interazione personalizzata "usa",
     *                           {@code false} altrimenti
     */
    public Item(String name, String description, boolean canUse)
    {
        this(name, description, SPRITESHEET, JSON_PATH, canUse);
    }

    /**
     * Costruttore protetto per essere richiamato dalle sottoclassi le quali
     * fanno riferimento a uno sprite-sheet e un json diverso da quello degli
     * oggetti {@link Item#OBJECT_SPRITESHEET_PATH}.
     *
     * @param name nome da dare all'Item
     * @param description descrizione da dare all'Item
     * @param spriteSheet path dello sprite-sheet a cui fare riferimento
     * @param jsonPath path del json collegato allo sprite-sheet
     * @param canUse {@code true} se è inizialmente possibile eseguire
     *                           l'interazione personalizzata "usa",
     *                           {@code false} altrimenti
     */
    protected Item(String name, String description, BufferedImage spriteSheet, String jsonPath, boolean canUse)
    {
        super(name, spriteSheet, jsonPath);
        this.description = description;
        this.canUse = canUse;
    }

    /**
     * Carica dizionario degli scenari collegati all'interazione "usa".
     *
     * Viene invocato al caricamento da "oggetti.xml".
     *
     * @param useScenarioMap dizionario degli scenari collegati all'interazione
     *                       "usa"
     */
    public void loadUseScenarios(Map<String, String> useScenarioMap)
    {
        this.useScenarioMap = useScenarioMap;
    }

    /**
     * Imposta lo stato dell'Item, aggiornando opportunamente
     * lo scenario da eseguire all'interazione "usa" personalizzata
     * sulla base nel nuovo stato.
     *
     * @param state nuovo stato
     */
    @Override
    public void setState(String state)
    {
        super.setState(state);

        // imposta useScenario in base allo state
        String scenarioPath = useScenarioMap.get(state);
        if(scenarioPath != null)
            useScenario = XmlParser.loadScenario(scenarioPath);
    }

    /**
     * Effettua l'interazione personalizzata "usa", se questo è possibile,
     * eseguendo lo scenario impostato per l'avvenimento sulla base dello
     * stato in cui si trova this.
     */
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
