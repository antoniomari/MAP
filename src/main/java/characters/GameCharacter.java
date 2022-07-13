package characters;

import events.CharacterEvent;
import events.EventHandler;
import graphics.SpriteManager;
import rooms.BlockPosition;

import java.awt.image.BufferedImage;

public class GameCharacter
{
    private String name;
    protected String spritePath;
    protected BufferedImage sprite;
    protected BlockPosition position;

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


    public void setPosition(BlockPosition newPosition)
    {
        this.position = newPosition;
        EventHandler.sendEvent(new CharacterEvent(this, newPosition, CharacterEvent.Type.MOVE));
    }

    public BlockPosition getPosition()
    {
        return position;
    }
}
