package entity.characters;

import animation.MovingAnimation;
import events.CharacterEvent;
import events.EventHandler;
import events.ItemInteractionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.RecursiveTask;

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


    public void speak()
    {
        EventHandler.sendEvent(new CharacterEvent(this, randomSentence(), CharacterEvent.Type.NPC_SPEAKS));

        // TODO : le animazioni devono effettivamente cambiare la posizione del personaggio
        //EventHandler.sendEvent(new CharacterEvent(this, getPosition(), CharacterEvent.Type.MOVE));
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
