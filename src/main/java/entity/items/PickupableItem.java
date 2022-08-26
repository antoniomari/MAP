package entity.items;


import entity.GamePiece;
import general.ActionSequence;
import entity.characters.PlayingCharacter;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
import general.GameManager;

public class PickupableItem extends Item
{
    private boolean keepOnUseWith;
    private ActionSequence usewithAction;
    private String targetPieceName;
    private String targetPieceInitState;
    private String targetPieceFinalState;

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

    public void setKeepOnUseWith(boolean b)
    {
        this.keepOnUseWith = b;
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

    public void setTargetPiece(String pieceName, String initState, String finalState)
    {
        targetPieceName = pieceName;
        targetPieceInitState = initState;
        targetPieceFinalState = finalState;
    }


    public void useWith(GamePiece gamePiece)
    {
        if(gamePiece.equals(GameManager.getPiece(targetPieceName)) && gamePiece.getState().equals(targetPieceInitState))
        {
            GameManager.startScenario(usewithAction);

            if(!keepOnUseWith)
                PlayingCharacter.getPlayer().removeFromInventory(this);

            // imposta stato alla fine
            gamePiece.setState(targetPieceFinalState);
        }

    }

    // TODO: useWith(Item)
}
