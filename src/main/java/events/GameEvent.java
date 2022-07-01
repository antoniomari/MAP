package events;

import java.time.LocalDateTime;
import java.util.Objects;

public class GameEvent
{
    private LocalDateTime eventTime;
    private String toPrint;

    public GameEvent(String toPrint)
    {
        Objects.requireNonNull(toPrint);

        eventTime = LocalDateTime.now();
        this.toPrint = toPrint;
    }

    public String getEventString()
    {
        return eventTime.toString() + " -> " + toPrint;
    }
}
