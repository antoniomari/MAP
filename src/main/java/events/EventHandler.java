package events;


import events.executors.AnimationExecutor;


public class EventHandler
{

    public static void printEvent(GameEvent ge)
    {
        System.out.println(ge.getEventString());

        if(ge instanceof ItemInteractionEvent)
            if (((ItemInteractionEvent) ge).hasAnimation())
                AnimationExecutor.executeAnimation(((ItemInteractionEvent) ge).getAnimation());

        if(ge instanceof InventoryEvent)
            executeInventoryEvent((InventoryEvent) ge);

        if(ge instanceof RoomEvent)
            executeRoomEvent((RoomEvent) ge);
    }

    public static void executeInventoryEvent(InventoryEvent e)
    {
        if()
        InventoryEvent.executeAdd(e.getItemInvolved());
    }

    public static void executeRoomEvent(RoomEvent e)
    {
        // demanda all'esecutore corrispondente l'aggiornamento della stanza
    }

}
