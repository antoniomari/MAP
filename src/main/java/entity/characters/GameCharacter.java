package entity.characters;

import entity.GamePiece;

import java.awt.image.BufferedImage;

public class GameCharacter extends GamePiece
{

    public GameCharacter(String name, String spritePath)
    {
        super(name, spritePath);
    }

    public GameCharacter(String name, BufferedImage spritesheet, String jsonPath)
    {
        super(name, spritesheet, jsonPath);
    }

}
