package events;

import entity.characters.GameCharacter;

/**
 * Evento collegato direttamente a un
 * GameCharacter, che rappresenta un'azione
 * propria della classe GameCharacter.
 */
public class CharacterEvent extends GameEvent
{
    /** GameCharacter coinvolto nell'evento. */
    private final GameCharacter characterInvolved;
    /** Tipo dell'evento. */
    private final Type type;
    /** Contenuto di testo (frase o nome emoji). */
    private String content;

    public enum Type
    {
        SPEAK
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


    /**
     * Crea un CharacterEvent.
     *
     * @param ch GameCharacter soggetto dell'evento
     * @param content frase pronunciata (caso SPEAK) o nome dell'emoji (caso EMOJI)
     * @param type tipo dell'evento
     */
    public CharacterEvent(GameCharacter ch, String content, Type type)
    {
        super(type.toString());
        this.type = type;
        this.content = content;
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

    public String getContent()
    {
        return content;
    }
}
