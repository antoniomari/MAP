package events;

import items.Item;

import java.time.LocalDateTime;
import java.util.Objects;

public class GameEvent
{
    private LocalDateTime eventTime;
    private String toPrint;
    private Item itemInvolved;

    public GameEvent(Item item, String toPrint)
    {
        Objects.requireNonNull(toPrint);

        eventTime = LocalDateTime.now();
        this.toPrint = toPrint;
        this.itemInvolved = item;
    }

    public String getEventString()
    {
        return eventTime.toString() + " -> " + " [" + itemInvolved.getName() + "] "+ toPrint;
    }
}
