package entity.items;


import entity.GamePiece;
import general.ActionSequence;
import entity.characters.PlayingCharacter;
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
    /** Stato in cui il target si trova alla fine dell'azione useWith. */
    private String targetPieceFinalState;

    /**
     * Crea un PickupableItem
     *
     * @param name nome da dare al PickupableItem
     * @param description descrizione da dare al PickupableItem
     * @param canUse {@code true} se è inizialmente possibile eseguire
     *                           l'interazione personalizzata "usa",
     *                           {@code false} altrimenti
     */
    public PickupableItem(String name, String description, boolean canUse)
    {
        super(name, description, canUse);
    }

    /**
     * Rimuove this dalla stanza in cui è contenuto e lo aggiunge all'inventario
     * del personaggio giocante.
     */
    public void pickup()
    {
        removeFromRoom();
        PlayingCharacter.getPlayer().addToInventory(this);
    }

    public void setKeepOnUseWith(boolean b)
    {
        this.keepOnUseWith = b;
    }

    public void setUseWithAction(ActionSequence useWithScenario)
    {
        this.useWithScenario = useWithScenario;
    }

    /**
     * Imposta le informazioni sul targetPiece dell'interazione useWith.
     *
     * @param pieceName nome del targetPiece
     * @param initState stato in cui dev'essere il targetPiece per il
     *                  successo dell'interazione
     * @param finalState stato in cui si troverà il targetPiece dopo
     *                   l'esecuzione dell'interazione
     */
    public void setTargetPiece(String pieceName, String initState, String finalState)
    {
        targetPieceName = pieceName;
        targetPieceInitState = initState;
        targetPieceFinalState = finalState;
    }

    /**
     * Esegue l'interazione useWith, controllando se {@code gamePiece} è il target
     * e se esso è nello stato corretto.
     *
     * Alla fine dell'interazione this viene eventualmente rimosso dall'inventario e
     * {@code gamePiece} viene portato allo stato finale previsto dall'interazione.
     *
     * @param gamePiece GamePiece con cui si prova a eseguire l'interazione.
     */
    public void useWith(GamePiece gamePiece)
    {
        if(gamePiece.equals(GameManager.getPiece(targetPieceName)) && gamePiece.getState().equals(targetPieceInitState))
        {
            GameManager.startScenario(useWithScenario);

            if(!keepOnUseWith)
                PlayingCharacter.getPlayer().removeFromInventory(this);

            gamePiece.setState(targetPieceFinalState);
        }
    }
}
