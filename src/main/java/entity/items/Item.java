package entity.items;

import general.ActionSequence;
import entity.GamePiece;
import events.EventHandler;
import events.ItemInteractionEvent;
import general.GameManager;
import graphics.SpriteManager;

import java.awt.image.BufferedImage;

public class Item extends GamePiece implements Observable
{
    // private final String name;
    private final String description;

    // PATH TILESET OGGETTI
    private final static String OBJECT_SPRITESHEET_PATH = "/img/tileset/oggetti.png";
    // PATH JSON RELATIVO AL TILESET
    private final static String JSON_PATH = "/img/tileset/oggetti.json";

    // SPRITESHEET OGGETTI
    private static final BufferedImage SPRITESHEET;

    // flag utilizzabile
    private boolean canUse;

    // modalità: è utilizzabile una volta//infinite volte
    private int usability;

    // default per il nome dell'azione (viene modificato con il setter)
    private String useActionName = "Usa";
    private ActionSequence useAction;


    public static final int USE_ONCE = 1;
    public static final int USE_INFTY = 2;


    // CARICAMENTO SPRITESHEET IN MEMORIA
    static
    {
        SPRITESHEET = SpriteManager.loadSpriteSheet(OBJECT_SPRITESHEET_PATH);
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
        this.usability = USE_ONCE;
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

    public void setUseAction(ActionSequence useEffect)
    {
        this.useAction = useEffect;
    }

    public void use()
    {
        if(canUse)
        {
            GameManager.startScenario(useAction);

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
