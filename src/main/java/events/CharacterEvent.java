package events;

import characters.GameCharacter;
import items.PickupableItem;
import rooms.BlockPosition;

public class CharacterEvent extends GameEvent
{
    private Type type;
    private BlockPosition position;

    public enum Type
    {
        MOVE {
            public String toString()
            {
                return "si è spostato";
            }
        },
    }

    public CharacterEvent(GameCharacter ch, BlockPosition pos, Type type)
    {
        super(ch, type.toString());
        this.type = type;
        this.position = pos;
    }

    public Type getType()
    {
        return this.type;
    }

    public BlockPosition getPosition()
    {
        return this.position;
    }

    public String getEventString()
    {
        return eventTime.toString() + " -> " + " [" + getCharacterInvolved().getName() + "] "+ type.toString()
                + " in posizione " + position.toString();
    }
}
