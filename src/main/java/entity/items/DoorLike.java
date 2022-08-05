package entity.items;

import general.GameManager;
import general.ActionSequence;
import events.EventHandler;
import events.ItemInteractionEvent;
import graphics.SpriteManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

public class DoorLike extends Item implements Openable, Lockable
{
    private boolean isLocked;
    private boolean isOpen;

    private ActionSequence onOpen;
    private ActionSequence onClose;

    private ActionSequence onUnlock;
    private ActionSequence onLock;

    // PATH SPRITESHEET (png + json)
    private final static String SPRITESHEET_PATH = "/img/tileset/porteMIST.png";
    private final static String JSON_PATH = "/img/tileset/porteMIST.json";

    // SPRITESHEET OGGETTI
    private final static BufferedImage SPRITESHEET = SpriteManager.loadSpriteSheet(SPRITESHEET_PATH);

    // ANIMAZIONI
    private static final List<Image> OPEN_FRAMES;
    private static final List<Image> CLOSE_FRAMES;

    static
    {
        // inizializzazione frames
        Image closed = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "closed");
        Image open1 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open1");
        Image open2 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open2");
        Image open3 = SpriteManager.loadSpriteByName(SPRITESHEET, JSON_PATH, "open3");

        OPEN_FRAMES = SpriteManager.getKeywordOrderedFrames(SPRITESHEET, JSON_PATH, "open");

        CLOSE_FRAMES = new ArrayList<>();
        CLOSE_FRAMES.add(open3);
        CLOSE_FRAMES.add(open2);
        CLOSE_FRAMES.add(open1);
        CLOSE_FRAMES.add(closed);

    }

    public DoorLike(String name, String description)
    {
        super(name, description, SPRITESHEET, JSON_PATH);
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
                // cambia stato in open
                isOpen = true;

                //crea scenario sequenziale animazione apertura + scenario effetto
                ActionSequence scenarioWithOpenAnimation = new ActionSequence("apertura + effetto",
                                                                            ActionSequence.Mode.SEQUENCE);
                scenarioWithOpenAnimation.append(() -> EventHandler.sendEvent(
                                new ItemInteractionEvent(this, "Si è aperta", getOpenFrames())));
                scenarioWithOpenAnimation.append(() -> GameManager.startScenario(onOpen));

                // esegui scenario completo
                GameManager.startScenario(scenarioWithOpenAnimation);
            }
        }
    }

    @Override
    public void close()
    {
        if(isOpen())
        {
            EventHandler.sendEvent(new ItemInteractionEvent(this, "Si è chiusa", getCloseFrames()));
            // cambia stato in close
            isOpen = false;
        }
    }

    @Override
    public List<Image> getOpenFrames()
    {
        return OPEN_FRAMES;
    }

    @Override
    public List<Image> getCloseFrames()
    {
        return CLOSE_FRAMES;
    }

    @Override
    public void setOpenEffect(ActionSequence effect)
    {
        this.onOpen = effect;
    }

    @Override
    public void setCloseEffect(ActionSequence effect)
    {
        this.onClose = effect;
    }

    @Override
    public boolean isOpen()
    {
        return isOpen;
    }


    public void lock()
    {
        isLocked = true;
    }

    public void unlock()
    {
        isLocked = false;
    }
}
