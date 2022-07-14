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

    protected int bWidth;
    protected int bHeight;

    public GameCharacter(String name, String spritePath)
    {
        int BLOCK_SIZE = 24;

        this.name = name;
        this.spritePath = spritePath;
        this.sprite = SpriteManager.loadSpriteSheet(spritePath);
        this.bWidth = sprite.getWidth() / BLOCK_SIZE;
        this.bHeight = sprite.getHeight() / BLOCK_SIZE;
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

    public int getBWidth()
    {
        return bWidth;
    }

    public int getBHeight()
    {
        return bHeight;
    }

}
