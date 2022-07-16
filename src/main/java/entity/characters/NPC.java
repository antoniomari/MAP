package entity.characters;

import animation.MovingAnimation;
import entity.rooms.BlockPosition;
import events.CharacterEvent;
import events.EventHandler;
import general.GameException;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NPC extends GameCharacter
{

    MovingAnimation movementAnimation;
    public NPC(String name, String spritePath)
    {
        super(name, spritePath);
    }

    public void setMovementAnimation(MovingAnimation m)
    {
        this.movementAnimation = m;
    }

    public MovingAnimation getMovementAnimation()
    {
        return movementAnimation;
    }

    public void move(BlockPosition finalPos, String type)
    {
        if(this.getPosition() == null)
            throw new GameException("Personaggio non posizionato");

        if(type.equals("absolute"))
            updatePosition(finalPos);
        else if(type.equals("relative"))
            updatePosition(getPosition().relativePosition(finalPos.getX(), finalPos.getY()));
        else
            throw new IllegalArgumentException("Valore type non valido");
    }


    public void speak()
    {
        String toPrint =  getName() + ": " + randomSentence();
        EventHandler.sendEvent(new CharacterEvent(this, toPrint, CharacterEvent.Type.NPC_SPEAKS));
    }

    public String randomSentence()
    {
        String[] sentences = {"Ciao, solo l'amico di spicoli", "Oggi ho preso 29 e lode", "Fa freddo"};
        List<String> sentencesList = Arrays.asList(sentences);

        Random rand = new Random();

        return sentencesList.get(rand.nextInt(sentencesList.size()));

    }

    public boolean hasAnimation()
    {
        return movementAnimation != null;
    }
}
