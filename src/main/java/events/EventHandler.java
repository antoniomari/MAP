package events;

import entity.GamePiece;
import entity.rooms.Room;
import events.executors.*;
import entity.items.PickupableItem;
import general.LogOutputManager;

public class EventHandler
{

    public static void sendEvent(GameEvent ge)
    {
        // stampa per logger
        LogOutputManager.logOutput(ge.getEventString(), LogOutputManager.EVENT_COLOR);

        if(ge instanceof ItemInteractionEvent)
            executeItemInteractionEvent((ItemInteractionEvent) ge);

        if(ge instanceof InventoryEvent)
            executeInventoryEvent((InventoryEvent) ge);

        if(ge instanceof RoomEvent)
            executeRoomEvent((RoomEvent) ge);

        if(ge instanceof CharacterEvent)
            executeCharacterEvent((CharacterEvent) ge);

        if(ge instanceof GamePieceEvent)
            executeGamePieceEvent((GamePieceEvent) ge);
    }

    public static void executeItemInteractionEvent(ItemInteractionEvent e)
    {
        if (e.hasAnimation())
            AnimationExecutor.executeAnimation(e.getItemInvolved(), e.getFrames());
        if (e.getType() == ItemInteractionEvent.Type.OBSERVE)
            TextBarUpdateExecutor.executeDisplay(e.getItemInvolved().getDescription());
        if(e.getType() == ItemInteractionEvent.Type.UPDATE_SPRITE)
            PieceUpdateExecutor.executeUpdateSprite(e.getItemInvolved());
        if(e.getType() == ItemInteractionEvent.Type.EFFECT_ANIMATION)  // TODO: modificare in gamePiece da item
            AnimationExecutor.executeEffectAnimation(e.getItemInvolved(), e.getSpritesheetPath(), e.getJsonPath(), e.getAnimationName(), e.getItemInvolved().getPosition(), e.getFinalWait());

    }

    public static void executeInventoryEvent(InventoryEvent e)
    {
        if(e.getType() == InventoryEvent.Type.ADD_ITEM)
            InventoryUpdateExecutor.executeAdd((PickupableItem) e.getItemInvolved()); //TODO: vedere se si può migliorare castr
        if(e.getType() == InventoryEvent.Type.USE_ITEM)
            InventoryUpdateExecutor.executeDrop((PickupableItem) e.getItemInvolved());

    }

    public static void executeRoomEvent(RoomEvent e)
    {
        if(e.getType() == RoomEvent.Type.REMOVE_PIECE_FROM_ROOM)
        {
            RoomUpdateExecutor.executeRemoveItem(e.getItemInvolved()); // lavora sulla currentRoom TODO: migliorare quest'aspetto
        }
        else if(e.getType() == RoomEvent.Type.ADD_PIECE_IN_ROOM)
        {
            if(e.getItemInvolved() != null)
                RoomUpdateExecutor.executeAddItem(e.getItemInvolved(), e.getCoordinates());
            else
                RoomUpdateExecutor.executeAddCharacter(e.characterInvolved, e.getCoordinates());
        }
    }

    public static void executeCharacterEvent(CharacterEvent e)
    {
        if(e.getType() == CharacterEvent.Type.MOVE)
        {
            CharacterUpdateExecutor.executeMove(e.getCharacterInvolved(), e.getOldPosition(), e.getPosition(), e.getMillisecondWaitEnd());
            // lavora sulla currentRoom TODO: migliorare quest'aspetto
        }
        else if(e.getType() == CharacterEvent.Type.NPC_SPEAKS)
        {
            TextBarUpdateExecutor.executeDisplay(e.getSentence());
        }
    }

    public static void executeGamePieceEvent(GamePieceEvent e)
    {
        if(e.getType() == GamePieceEvent.Type.MOVE)
        {
            CharacterUpdateExecutor.executeMove(e.getPieceInvolved(), e.getOldPosition(), e.getNewPosition(), e.getMillisecondWaitEnd());
            // lavora sulla currentRoom TODO: migliorare quest'aspetto
        }

    }

}
