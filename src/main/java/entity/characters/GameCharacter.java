package entity.characters;

import entity.GamePiece;
import graphics.SpriteManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GameCharacter extends GamePiece
{
    private List<Image> movingFrames;
    private String jsonPath;
    private BufferedImage spritesheet;

    public GameCharacter(String name, String spritePath)
    {
        super(name, spritePath);
        fakeInitMovingFrames();
    }

    private void fakeInitMovingFrames()
    {
        movingFrames = new ArrayList<>();
        movingFrames.add(sprite);
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


    public List<Image> getMovingFrames()
    {
        return movingFrames;
    }

}
