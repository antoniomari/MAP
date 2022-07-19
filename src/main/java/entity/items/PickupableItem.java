package entity.items;


import scenarios.ActionSequence;
import entity.characters.PlayingCharacter;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
import general.GameManager;

public class PickupableItem extends Item
{
    private ActionSequence usewithAction;
    private Item targetItem;

    // costruttore che inizializza l'oggetto come presente nell'inventario
    public PickupableItem(String name, String description)
    {
        super(name, description);
    }


    public void pickup()
    {
        // generato evento togliStanza
        removeFromRoom(); // setta a null la stanza

        PlayingCharacter.getPlayer().addToInventory(this);

    }

    public void drop(Room room, BlockPosition pos)
    {
        //aggiungi alla stanza
        addInRoom(room, pos);

        // rimuovi dall'inventario
        PlayingCharacter.getPlayer().removeFromInventory(this);
    }

    public void setUsewithAction(ActionSequence usewithAction)
    {
        this.usewithAction = usewithAction;
    }

    public void setTargetItem(Item item)
    {
        this.targetItem = item;
    }

    public void useWith(Item item)
    {
        System.out.println("entrato in useWith");
        if(item.equals(targetItem))
            GameManager.startScenario(usewithAction);
    }

    // TODO: useWith(Item)
}
