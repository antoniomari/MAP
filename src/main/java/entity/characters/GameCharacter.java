package entity.characters;

import entity.GamePiece;
import entity.rooms.BlockPosition;
import events.CharacterEvent;
import events.EventHandler;
import general.GameException;
import graphics.SpriteManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
        spritesheet = SpriteManager.loadSpriteSheet(spritesheetPath);
        this.jsonPath = jsonPath;
        initMovingFrames();
    }


    private void initMovingFrames()
    {
        // TODO: generalizzare
        for(int i = 1; i <= 4; i++)
        {
            movingFrames.add(SpriteManager.loadSpriteByName(spritesheet, jsonPath, "moving" + i));
        }
        movingFrames.add(sprite);
    }


    public String randomSentence()
    {
        String[] sentences = {"Ciao, solo l'amico di spicoli", "Oggi ho preso 29 e lode", "Fa freddo"};
        List<String> sentencesList = Arrays.asList(sentences);

        Random rand = new Random();

        return sentencesList.get(rand.nextInt(sentencesList.size()));

    }

    public void speak()
    {
        speak(randomSentence());

    }

    public void speak(String sentence)
    {
        String toPrint =  getName() + ": " + sentence;
        EventHandler.sendEvent(new CharacterEvent(this, toPrint, CharacterEvent.Type.NPC_SPEAKS));
    }

}
