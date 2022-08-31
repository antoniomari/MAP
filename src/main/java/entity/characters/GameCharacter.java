package entity.characters;

import entity.GamePiece;
import events.AnimationEvent;
import events.CharacterEvent;
import events.EventHandler;
import graphics.SpriteManager;
import org.json.JSONException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class GameCharacter extends GamePiece
{
    private String jsonPath;
    private BufferedImage spritesheet;

    private Image speakFrame;

    public enum Emoji
    {
        ESCLAMATIVO, INTERROGATIVO, NOTA_MUSICALE,
        CUORE, PROVOCAZIONE, GOCCIA, FUMO, PUNTINI, ENFASI, DORMI
    }

    public GameCharacter(String name, String spritePath)
    {
        super(name, spritePath);
        fakeInitSpeakFrame();
    }


    public GameCharacter(String name, String spritesheetPath, String jsonPath)
    {
        super(name, SpriteManager.loadSpriteSheet(spritesheetPath), jsonPath);
        spritesheet = SpriteManager.loadSpriteSheet(spritesheetPath);
        this.jsonPath = jsonPath;
        initAnimateFrames();
        initMovingFrames();
        initSpeakFrame();
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
            animateFrames = SpriteManager.getKeywordOrderedFrames(spritesheet, jsonPath, getName() + "animate");

        animateFrames.add(0, getSprite());
    }

    private void initSpeakFrame()
    {
        try
        {
            speakFrame = SpriteManager.loadSpriteByName(spritesheet, jsonPath, "speaking");
        }
        catch(JSONException e)
        {
            fakeInitSpeakFrame();
        }

    }

    private void fakeInitSpeakFrame()
    {
        speakFrame = sprite;
    }

    public List<Image> getSpeakFrames()
    {
        List<Image> speakFrames = new ArrayList<>();
        speakFrames.add(speakFrame);
        speakFrames.add(speakFrame);
        speakFrames.add(sprite);

        return speakFrames;
    }

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
