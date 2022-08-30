package events;

import animation.Animation;
import java.util.List;
import events.executors.*;
import entity.items.PickupableItem;
import general.LogOutputManager;
import sound.SoundHandler;

import java.awt.*;
import java.util.ArrayList;

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

        if(ge instanceof AnimationEvent)
            executeAnimationEvent((AnimationEvent) ge);
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
        {
            InventoryUpdateExecutor.executeAdd((PickupableItem) e.getItemInvolved()); //TODO: vedere se si può migliorare castr

            // TODO: aggiustare codice
            SoundHandler.playWav(SoundHandler.PICKUP_SOUND_PATH, SoundHandler.Mode.SOUND);
        }
        if(e.getType() == InventoryEvent.Type.USE_ITEM)
            InventoryUpdateExecutor.executeDrop((PickupableItem) e.getItemInvolved());

    }

    public static void executeRoomEvent(RoomEvent e)
    {
        if(e.getType() == RoomEvent.Type.REMOVE_PIECE_FROM_ROOM)
        {
            RoomUpdateExecutor.executeRemovePiece(e.getPieceInvolved());
        }
        else if(e.getType() == RoomEvent.Type.ADD_PIECE_IN_ROOM)
        {
            RoomUpdateExecutor.executeAddPiece(e.getRoomInvolved(), e.getPieceInvolved(), e.getCoordinates());
        }
    }

    public static void executeCharacterEvent(CharacterEvent e)
    {
        /*
        if(e.getType() == CharacterEvent.Type.MOVE)
        {
            CharacterUpdateExecutor.executeMove(e.getCharacterInvolved(), e.getOldPosition(), e.getPosition(), e.getMillisecondWaitEnd());
            // lavora sulla currentRoom TODO: migliorare quest'aspetto
        }

         */
        if(e.getType() == CharacterEvent.Type.NPC_SPEAKS)
        {
            //e.getCharacterInvolved().updateSprite("speaking");
            TextBarUpdateExecutor.executeDisplay(e.getSentence());
        }
        else if(e.getType() == CharacterEvent.Type.EMOJI)
        {
            final String EMOJI_SPRITESHEET_PATH = "/img/animazioni/emoji.png";
            final String EMOJI_JSON_PATH = "/img/animazioni/emoji.json";

            SoundHandler.playWav(SoundHandler.EMOJI_SOUND_PATH, SoundHandler.Mode.SOUND);

            AnimationExecutor.executeEffectAnimation(e.getCharacterInvolved(), EMOJI_SPRITESHEET_PATH, EMOJI_JSON_PATH,
                    e.getSentence(), e.getCharacterInvolved().getPosition().relativePosition(1, - e.getCharacterInvolved().getBHeight()),
                    500);
            // TODO: generalizzare, fuori e.getSentence, dentro nome animazione
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

    public static void executeAnimationEvent(AnimationEvent e)
    {
        AnimationExecutor.executeAnimation(e.getPieceInvolved(), e.getFrames());
    }

}
