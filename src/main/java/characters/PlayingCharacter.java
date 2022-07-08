package characters;

import events.EventHandler;
import events.GameEvent;
import items.PickupableItem;

import java.util.ArrayList;
import java.util.List;

public class PlayingCharacter extends GameCharacter
{
    List<PickupableItem> inventory;
    public final static PlayingCharacter SPICOLO = new PlayingCharacter("Spicolo il terribile");

    public PlayingCharacter(String name)
    {
        super(name);
        this.inventory = new ArrayList<>();
    }

    public void addToInventory(PickupableItem i)
    {
        inventory.add(i);
    }

    public void removeFromInventory(PickupableItem i)
    {
        inventory.remove(i);
    }

    public List<PickupableItem> getInventory()
    {
        return inventory;
    }
}
