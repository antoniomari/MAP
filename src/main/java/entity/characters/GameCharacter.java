package entity.characters;

import entity.GamePiece;
import events.AnimationEvent;
import events.CharacterEvent;
import events.EventHandler;
import graphics.SpriteManager;

import java.awt.image.BufferedImage;
import java.util.*;

public class GameCharacter extends GamePiece
{
    private String jsonPath;
    private BufferedImage spritesheet;

    public GameCharacter(String name, String spritePath)
    {
        super(name, spritePath);
    }


    public GameCharacter(String name, String spritesheetPath, String jsonPath)
    {
        super(name, SpriteManager.loadSpriteSheet(spritesheetPath), jsonPath);
        movingFrames = new ArrayList<>();
        animateFrames = new ArrayList<>(); // TODO: aggiustare
        spritesheet = SpriteManager.loadSpriteSheet(spritesheetPath);
        this.jsonPath = jsonPath;
        initMovingFrames();
        initAnimateFrames();
    }


    private void initMovingFrames()
    {
        movingFrames = SpriteManager.getKeywordOrderedFrames(spritesheet, jsonPath, "moving");

        if(movingFrames.isEmpty())
        {
            movingFrames = SpriteManager.getKeywordOrderedFrames(spritesheet, jsonPath,  getName() + "moving");
        }
        movingFrames.add(0, getSprite());
    }

    private void initAnimateFrames()
    {
        animateFrames = SpriteManager.getKeywordOrderedFrames(spritesheet, jsonPath, "animate");

        if(animateFrames.isEmpty())
        {
            animateFrames = SpriteManager.getKeywordOrderedFrames(spritesheet, jsonPath, getName() + "animate");
        }
        animateFrames.add(0, getSprite());
    }

    public void animate()
    {
        EventHandler.sendEvent(new AnimationEvent(this, animateFrames));
    }

    public void animateReverse()
    {
        Collections.reverse(animateFrames);
        EventHandler.sendEvent(new AnimationEvent(this, animateFrames));
        Collections.reverse(animateFrames);
    }


    public String randomSentence()
    {
        String[] sentences = {"Ciao, solo l'amico di spicoli", "Oggi ho preso 29 e lode", "Fa freddo"};
        List<String> sentencesList = Arrays.asList(sentences);

        Random rand = new Random();

        return sentencesList.get(rand.nextInt(sentencesList.size()));

    }

    //public void speak()
    //{
    //    speak(randomSentence());
    //}

    public void speak(String sentence)
    {
        String toPrint =  getName() + ": " + sentence;
        EventHandler.sendEvent(new CharacterEvent(this, toPrint, CharacterEvent.Type.NPC_SPEAKS));
    }

}
