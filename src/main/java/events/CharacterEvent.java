package events;

import characters.GameCharacter;
import items.PickupableItem;
import rooms.Coordinates;

public class CharacterEvent extends GameEvent
{
    private Type type;
    private Coordinates position;

    public enum Type
    {
        MOVE {
            public String toString()
            {
                return "si è spostato";
            }
        },
    }

    public CharacterEvent(GameCharacter ch, Coordinates pos, Type type)
    {
        super(ch, type.toString());
        this.type = type;
        this.position = pos;
    }

    public Type getType()
    {
        return this.type;
    }

    public Coordinates getPosition()
    {
        return this.position;
    }
}
