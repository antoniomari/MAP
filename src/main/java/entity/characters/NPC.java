package entity.characters;


import general.ActionSequence;
import general.GameManager;

public class NPC extends GameCharacter
{
    ActionSequence speakScenario;

    public NPC(String name, String spritesheetPath, String jsonPath)
    {
        super(name, spritesheetPath, jsonPath);
        // TODO: ottimizzare
    }

    public NPC(String name, String spritePath)
    {
        super(name, spritePath);
    }

    public void setSpeakScenario(ActionSequence speakScenario)
    {
        this.speakScenario = speakScenario;
    }

    @Override
    public void speak()
    {
        GameManager.startScenario(speakScenario);
    }
}
