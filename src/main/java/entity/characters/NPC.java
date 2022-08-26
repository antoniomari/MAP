package entity.characters;


import general.ActionSequence;
import general.GameException;
import general.GameManager;
import general.XmlLoader;

import java.util.HashMap;
import java.util.Map;

public class NPC extends GameCharacter
{
    /** Dizionario STATO-> PATH SPEAK SCENARIO ASSOCIATO. */
    private Map<String, String> speakScenarioMap;
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

    public void loadSpeakScenarios(Map<String, String> speakScenarioMap)
    {
        this.speakScenarioMap = speakScenarioMap;
    }


    /*
    public void setSpeakScenario(ActionSequence speakScenario)
    {
        this.speakScenario = speakScenario;

        GameManager.continueScenario();
    }

     */


    @Override
    public void setState(String state)
    {
        super.setState(state);

        String scenarioPath = speakScenarioMap.get(state);
        if(scenarioPath != null)
            speakScenario = XmlLoader.loadScenario(scenarioPath);

        // TODO: aggiustare
        GameManager.continueScenario();
    }

    public void setSpeakSentence(String sentence)
    {
        this.speakScenario = new ActionSequence("Parla", ActionSequence.Mode.SEQUENCE);
        speakScenario.append(() -> this.speak(sentence));

        GameManager.continueScenario();
    }

    public void speak()
    {
        if (speakScenario != null)
            GameManager.startScenario(speakScenario);
        else
            throw new GameException("speakScenario non impostato");
    }
}
