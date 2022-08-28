package entity.items;


import entity.GamePiece;
import general.ActionSequence;
import entity.characters.PlayingCharacter;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
import general.GameManager;

public class PickupableItem extends Item
{
    /** Flag che indica se l'oggetto dev'essere mantenuto nell'inventario una volta utilizzato. */
    private boolean keepOnUseWith;

    /** Scenario da eseguire quando l'oggetto viene utilizzato con l'oggetto target. */
    private ActionSequence useWithScenario;

    /** Nome del GamePiece target dell'azione useWith di this. */
    private String targetPieceName;
    /** Stato in cui il target si deve trovare per poter eseguire lo scenario dell'azione useWith. */
    private String targetPieceInitState;

    /** Stato in cui il target si trova alla fine dell'azione useWith */
    @Deprecated
    // TODO: controllare la deprecaggine
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

    public void setUseWithAction(ActionSequence useWithScenario)
    {
        this.useWithScenario = useWithScenario;
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
            GameManager.startScenario(useWithScenario);

            if(!keepOnUseWith)
                PlayingCharacter.getPlayer().removeFromInventory(this);

            // imposta stato alla fine
            // TODO: capire qua
            gamePiece.setState(targetPieceFinalState, false);
        }

    }

    // TODO: useWith(Item)
}
