package entity.items;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Container extends Item //implements Openable
{
    List<PickupableItem> containedPickups;

    public Container(String name, String description, BufferedImage spriteSheet, String jsonPath)
    {
        super(name, description, spriteSheet, jsonPath);
        containedPickups = new ArrayList<>();
    }

    public void addPickup(PickupableItem it)
    {
        containedPickups.add(it);
    }

    //@Override
    public void open()
    {
        // fai vedere quello che c'è dentro
    }

    //@Override
    public void close()
    {
        // non serve a niente
    }

    //@Override
    public boolean isOpen()
    {
        return false;
    }
}
