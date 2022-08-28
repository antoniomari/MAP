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

    private ActionSequence onOpen = ActionSequence.voidScenario();
    //private ActionSequence onClose;

    //private ActionSequence onUnlock;
    //private ActionSequence onLock;

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
        return openFrames;
    }

    @Override
    public List<Image> getCloseFrames()
    {
        return closeFrames;
    }

    @Override
    public void setOpenEffect(ActionSequence effect)
    {
        this.onOpen = effect;
    }

    @Override
    public void setCloseEffect(ActionSequence effect)
    {
        // this.onClose = effect;
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
