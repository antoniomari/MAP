package events;

import events.executors.AnimationExecutor;
import events.executors.CharacterUpdateExecutor;
import events.executors.InventoryUpdateExecutor;
import events.executors.RoomUpdateExecutor;
import items.PickupableItem;

public class EventHandler
{
    public static void sendEvent(GameEvent ge)
    {
        System.out.println(ge.getEventString());

        if(ge instanceof ItemInteractionEvent)
            executeItemInteractionEvent((ItemInteractionEvent) ge);

        if(ge instanceof InventoryEvent)
            executeInventoryEvent((InventoryEvent) ge);

        if(ge instanceof RoomEvent)
            executeRoomEvent((RoomEvent) ge);

        if(ge instanceof CharacterEvent)
            executeCharacterEvent((CharacterEvent) ge);
    }

    public static void executeItemInteractionEvent(ItemInteractionEvent e)
    {
        if (e.hasAnimation())
            AnimationExecutor.executeAnimation(e.getAnimation());
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
        if(e.getType() == RoomEvent.Type.REMOVE_ITEM_FROM_ROOM)
        {
            RoomUpdateExecutor.executeRemoveItem(e.getItemInvolved()); // lavora sulla currentRoom TODO: migliorare quest'aspetto
        }
        else if(e.getType() == RoomEvent.Type.ADD_ITEM_IN_ROOM)
        {
            RoomUpdateExecutor.executeAddItem(e.getItemInvolved(), e.getCoordinates());
        }
    }

    public static void executeCharacterEvent(CharacterEvent e)
    {
        if(e.getType() == CharacterEvent.Type.MOVE)
        {
            CharacterUpdateExecutor.executeMove(e.getCharacterInvolved(), e.getPosition()); // lavora sulla currentRoom TODO: migliorare quest'aspetto
        }
    }
}
