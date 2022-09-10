package events;

import entity.characters.GameCharacter;
import entity.items.Item;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Classe che rappresenta un evento di gioco.
 */
public abstract class GameEvent
{
    protected LocalDateTime eventTime;
    protected String toPrint;

    public GameEvent(String toPrint)
    {
        Objects.requireNonNull(toPrint);

        eventTime = LocalDateTime.now();
        this.toPrint = toPrint;
    }

    public String getEventString()
    {
        return eventTime.toString();
    }

}
