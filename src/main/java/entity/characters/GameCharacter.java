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

    public enum Emoji
    {

    }

    public GameCharacter(String name, String spritePath)
    {
        super(name, spritePath);
    }


    public GameCharacter(String name, String spritesheetPath, String jsonPath)
    {
        super(name, SpriteManager.loadSpriteSheet(spritesheetPath), jsonPath);
        spritesheet = SpriteManager.loadSpriteSheet(spritesheetPath);
        this.jsonPath = jsonPath;
        initMovingFrames();
        initAnimateFrames();
    }


    private void initMovingFrames()
    {
        // prova con leftMoving
        leftMovingFrames = SpriteManager.getKeywordOrderedFrames(spritesheet, jsonPath, "leftMoving");
        rightMovingFrames = SpriteManager.getKeywordOrderedFrames(spritesheet, jsonPath, "rightMoving");

        if(leftMovingFrames.isEmpty())
        {
            leftMovingFrames = SpriteManager.getKeywordOrderedFrames(spritesheet, jsonPath,  getName() + "leftMoving");
        }

        if(rightMovingFrames.isEmpty())
        {
            rightMovingFrames = SpriteManager.getKeywordOrderedFrames(spritesheet, jsonPath, getName() + "rightMoving");
        }

        leftMovingFrames.add(0, getSprite());
        rightMovingFrames.add(0, getSprite());
    }

    //TODO: eliminare copia
    private void initAnimateFrames()
    {
        animateFrames = SpriteManager.getKeywordOrderedFrames(spritesheet, jsonPath, "animate");

        if(animateFrames.isEmpty())
        {
            animateFrames = SpriteManager.getKeywordOrderedFrames(spritesheet, jsonPath, getName() + "animate");
        }
        animateFrames.add(0, getSprite());
    }

    /*
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

     */

    public void speak(String sentence)
    {
        String toPrint =  getName() + ": " + sentence;
        EventHandler.sendEvent(new CharacterEvent(this, toPrint, CharacterEvent.Type.NPC_SPEAKS));
    }

    public void playEmoji(String emojiName)
    {
        EventHandler.sendEvent(new CharacterEvent(this, emojiName, CharacterEvent.Type.EMOJI));
    }

}
