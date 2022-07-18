package entity.items;

import entity.GamePiece;
import events.EventHandler;
import events.ItemInteractionEvent;
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

    // flag utlizzabile
    private boolean usable;



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
        this.usable = canUse;
    }

    public void use()
    {

    }

    public boolean canUse()
    {
        return usable;
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
