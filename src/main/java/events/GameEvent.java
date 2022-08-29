package events;

import entity.characters.GameCharacter;
import entity.items.Item;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
import java.time.LocalDateTime;
import java.util.Objects;

public class GameEvent
{
    protected LocalDateTime eventTime;
    protected String toPrint;
    protected Item itemInvolved;
    protected GameCharacter characterInvolved;
    protected Room roomInvolved;
    protected BlockPosition pos;
    protected BlockPosition finalPos;


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

    public Item getItemInvolved()
    {
        return itemInvolved;
    }

    //public GameCharacter getCharacterInvolved()
    //{

    //    return characterInvolved;
    //}
}
