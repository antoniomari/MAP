package events;

import entity.GamePiece;
import entity.characters.GameCharacter;
import events.executors.*;
import entity.items.PickupableItem;
import general.GameManager;
import general.LogOutputManager;
import sound.SoundHandler;

/**
 * Classe che si occupa della gestione degli eventi, smistando
 * ogni GameEvent a seconda del tipo effettivo e delegando l'esecuzione
 * alle opportune classi Executor.
 */
public class EventHandler
{

    /**
     * Invia un evento, indirizzandolo agli esecutori che si occuperanno
     * di aggiornare la gui in modo coerente alle informazioni inviate
     * dall'evento.
     *
     * @param ge evento di gioco da inoltrare
     */
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
        GameCharacter ch = e.getCharacterInvolved();

        if(e.getType() == CharacterEvent.Type.SPEAK)
        {
            TextBarUpdateExecutor.executeDisplay(e.getContent());
            AnimationExecutor.executeSpeakAnimation(ch, ch.getSpeakFrames(), 300, GameManager.GameState.TEXT_BAR);
        }
        else if(e.getType() == CharacterEvent.Type.EMOJI)
        {
            final String EMOJI_SPRITESHEET_PATH = "/img/animazioni/emoji.png";
            final String EMOJI_JSON_PATH = "/img/animazioni/emoji.json";

            SoundHandler.playWav(SoundHandler.EMOJI_SOUND_PATH, SoundHandler.Mode.SOUND);

            AnimationExecutor.executeEffectAnimation(e.getCharacterInvolved(), EMOJI_SPRITESHEET_PATH, EMOJI_JSON_PATH,
                    e.getContent(), e.getCharacterInvolved().getPosition().relativePosition(1, - e.getCharacterInvolved().getBHeight()),
                    500);
        }
    }

    public static void executeGamePieceEvent(GamePieceEvent e)
    {
        GamePiece piece = e.getPieceInvolved();
        if(e.getType() == GamePieceEvent.Type.EFFECT_ANIMATION)
        {
            AnimationExecutor.executeEffectAnimation(piece,
                    e.getAnimationSpritesheet(),
                    e.getAnimationJson(),
                    e.getAnimationName(),
                    piece.getPosition(),
                    500);
        }

        if(e.getType() == GamePieceEvent.Type.PERPETUAL_EFFECT_ANIMATION)
        {
            AnimationExecutor.executePerpetualEffectAnimation(piece,
                    e.getAnimationSpritesheet(),
                    e.getAnimationJson(),
                    e.getAnimationName(),
                    piece.getPosition(),
                    500);
        }

        if(e.getType() == GamePieceEvent.Type.MOVE)
        {
            PieceUpdateExecutor.executeMove(piece, e.getOldPosition(), e.getNewPosition(), e.getMillisecondWaitEnd());
            // lavora sulla currentRoom TODO: migliorare quest'aspetto
        }

        if(e.getType() == GamePieceEvent.Type.UPDATE_SPRITE)
            PieceUpdateExecutor.executeUpdateSprite(piece);

        if(e.getType() == GamePieceEvent.Type.PIECE_ANIMATION)
            AnimationExecutor.executeAnimation(e.getPieceInvolved(), e.getFrames());
    }

}
