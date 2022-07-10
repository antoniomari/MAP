package characters;

import graphics.SpriteManager;

import java.awt.image.BufferedImage;

public class GameCharacter
{
    private String name;
    protected String spritePath;
    protected BufferedImage sprite;

    public GameCharacter(String name, String spritePath)
    {
        this.name = name;
        this.spritePath = spritePath;
        this.sprite = SpriteManager.loadSpriteSheet(spritePath);
    }

    public String getName()
    {
        return this.name;
    }

    public BufferedImage getSprite()
    {
        return this.sprite;
    }
}
