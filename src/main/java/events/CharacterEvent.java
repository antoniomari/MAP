package events;

import entity.characters.GameCharacter;
import entity.rooms.BlockPosition;

public class CharacterEvent extends GameEvent
{
    private Type type;
    private String sentence;

    public enum Type
    {
        NPC_SPEAKS
                {
                    @Override
                    public String toString()
                    {
                        return "ha parlato";
                    }
                },
        EMOJI
                {
                    @Override
                    public String toString()
                    {
                        return "emoji";
                    }
                }

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


    public GameCharacter getCharacterInvolved()
    {
        return characterInvolved;
    }


    public String getEventString()
    {
        return eventTime.toString() + " -> " + "[" + characterInvolved + "] " + type;
    }

    public String getSentence()
    {
        return sentence;
    }
}
