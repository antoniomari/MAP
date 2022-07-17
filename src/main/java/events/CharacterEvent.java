package events;

import entity.characters.GameCharacter;
import entity.rooms.BlockPosition;

public class CharacterEvent extends GameEvent
{
    private Type type;
    private BlockPosition oldPosition;
    private BlockPosition position;
    private String sentence;
    private int millisecondWaitEnd;

    public enum Type
    {
        MOVE {
            public String toString()
            {
                return "si è spostato";
            }
        },
        ADDED_IN_ROOM
                {
                    @Override
                    public String toString()
                    {
                        return "aggiunto in stanza";
                    }
                },
        NPC_SPEAKS
                {
                    @Override
                    public String toString()
                    {
                        return "ha parlato";
                    }
                }

    }

    public CharacterEvent(GameCharacter ch, BlockPosition pos, Type type)
    {
        super(type.toString());
        this.type = type;
        this.position = pos;
        this.characterInvolved = ch;
    }

    public CharacterEvent(GameCharacter ch, BlockPosition oldPos, BlockPosition newPos, int millisecondWaitEnd, Type type)
    {
        super(type.toString());
        this.type = type;
        this.oldPosition = oldPos;
        this.position = newPos;
        this.characterInvolved = ch;
        this.millisecondWaitEnd = millisecondWaitEnd;
    }

    public CharacterEvent(GameCharacter ch, String sentence, Type type)
    {
        super(type.toString());
        this.type = type;
        this.sentence = sentence;
        this.characterInvolved = ch;
    }

    public Type getType()
    {
        return this.type;
    }

    public BlockPosition getOldPosition()
    {
        return oldPosition;
    }

    public BlockPosition getPosition()
    {
        return this.position;
    }

    public int getMillisecondWaitEnd()
    {
        return  millisecondWaitEnd;
    }


    public String getEventString()
    {
        if(type == Type.MOVE || type == Type.ADDED_IN_ROOM)
        return eventTime.toString() + " -> " + " [" + getCharacterInvolved().getName() + "] "+ type.toString()
                + " in posizione " + position.toString();
        else
            return eventTime.toString() + " -> " + "[" + characterInvolved + "] " + type;
    }

    public String getSentence()
    {
        return sentence;
    }
}
