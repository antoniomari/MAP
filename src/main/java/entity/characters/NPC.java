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

    public void setSpeakSentence(String sentence)
    {
        this.speakScenario = new ActionSequence("Parla", ActionSequence.Mode.SEQUENCE);
        speakScenario.append(() -> this.speak(sentence));
    }

    @Override
    public void speak()
    {
        if (speakScenario != null) {
            GameManager.startScenario(speakScenario);
        }
    }
}
