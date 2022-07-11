package characters;

import events.CharacterEvent;
import events.EventHandler;
import items.PickupableItem;
import rooms.Coordinates;
import java.util.ArrayList;
import java.util.List;

public class PlayingCharacter extends GameCharacter
{
    public static final int INVENTORY_SIZE = 30;
    private Coordinates position;

    List<PickupableItem> inventory;
    private static PlayingCharacter player;

    private PlayingCharacter(String name, String spritePath)
    {
        super(name, spritePath);
        this.inventory = new ArrayList<>();
    }

    public static PlayingCharacter getPlayer()
    {
        if(player == null)
            player = new PlayingCharacter("Schwartz", "/img/personaggi/schwartz.png");

        return player;
    }

    public void setPosition(Coordinates newPosition)
    {
        this.position = newPosition;
        EventHandler.sendEvent(new CharacterEvent(this, newPosition, CharacterEvent.Type.MOVE));
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

    public void flushInventory()
    {
        for(PickupableItem i : inventory)
        {
            inventory.remove(i);
        }
    }
}
