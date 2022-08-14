package entity.items;


import entity.GamePiece;
import general.ActionSequence;
import entity.characters.PlayingCharacter;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
import general.GameManager;

public class PickupableItem extends Item
{
    private ActionSequence usewithAction;
    private String targetPieceName;

    // costruttore che inizializza l'oggetto come presente nell'inventario
    public PickupableItem(String name, String description, boolean canUse)
    {
        super(name, description, canUse);
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

    public void setUseWithAction(ActionSequence usewithAction)
    {
        this.usewithAction = usewithAction;
    }

    public void setTargetPiece(String pieceName)
    {
        targetPieceName = pieceName;
    }


    public void useWith(GamePiece gamePiece)
    {
        // System.out.println("entrato in useWith");
        if(gamePiece.equals(GameManager.getPiece(targetPieceName)))
            GameManager.startScenario(usewithAction);
    }

    // TODO: useWith(Item)
}
