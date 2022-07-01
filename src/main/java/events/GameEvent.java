package events;

import characters.GameCharacter;
import items.Item;

import java.time.LocalDateTime;
import java.util.Objects;

public class GameEvent
{
    private LocalDateTime eventTime;
    private String toPrint;
    private Item itemInvolved;
    private GameCharacter characterInvolved;

    public GameEvent(Item item, String toPrint)
    {
        Objects.requireNonNull(toPrint);

        eventTime = LocalDateTime.now();
        this.toPrint = toPrint;
        this.itemInvolved = item;
    }

    public GameEvent(GameCharacter ch, String toPrint)
    {
        Objects.requireNonNull(toPrint);

        eventTime = LocalDateTime.now();
        this.toPrint = toPrint;
        this.itemInvolved = null;
        this.characterInvolved = ch;
    }

    public String getEventString()
    {
        if (itemInvolved == null)
        {
            return eventTime.toString() + " -> " + " [" + characterInvolved.getName() + "] "+ toPrint;
        }

        return eventTime.toString() + " -> " + " [" + itemInvolved.getName() + "] "+ toPrint;
    }
}
