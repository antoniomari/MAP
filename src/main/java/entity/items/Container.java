package entity.items;

import entity.characters.PlayingCharacter;
import general.ActionSequence;
import graphics.SpriteManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Container extends Item implements Openable
{
    List<PickupableItem> containedPickups;

    private static final String JSON_PATH = "/img/tileset/container.json";
    private static final String SPRITESHEET_PATH = "/img/tileset/container.png";
    private static final BufferedImage SPRITESHEET;

    static
    {
        SPRITESHEET = SpriteManager.loadSpriteSheet(SPRITESHEET_PATH);
    }

    public Container(String name, String description)
    {
        super(name, description, SPRITESHEET, JSON_PATH);
        containedPickups = new ArrayList<>();
    }

    public void addPickup(PickupableItem it)
    {
        containedPickups.add(it);
    }

    //@Override
    public void open()
    {
        for(PickupableItem p : containedPickups)
        {
            PlayingCharacter.getPlayer().addToInventory(p);
        }

        for (PickupableItem p : containedPickups)
        {
            containedPickups.remove(p);
        }
    }

    //@Override
    public void close()
    {
        // non serve a niente
    }

    @Override
    public List<Image> getOpenFrames()
    {
        return null;
    }

    @Override
    public List<Image> getCloseFrames()
    {
        return null;
    }

    //@Override
    public boolean isOpen()
    {
        return false;
    }

    @Override
    public void setOpenEffect(ActionSequence effect)
    {

    }

    @Override
    public void setCloseEffect(ActionSequence effect)
    {

    }

    // TODO : finire impleemntaziopne openable
}
