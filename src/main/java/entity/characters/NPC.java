package entity.characters;


import general.ActionSequence;
import general.GameException;
import general.GameManager;
import general.xml.XmlParser;

import java.util.Map;

public class NPC extends GameCharacter
{
    /** Dizionario STATO-> PATH SPEAK SCENARIO ASSOCIATO. */
    private Map<String, String> speakScenarioMap;
    private Map<String, String> speakSentenceMap;
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

    public void loadSpeakScenarios(Map<String, String> speakScenarioMap, Map<String, String> speakSentenceMap)
    {
        this.speakScenarioMap = speakScenarioMap;
        this.speakSentenceMap = speakSentenceMap;
    }


    @Override
    public void setState(String state, boolean continueScenario)
    {
        this.state = state;

        String scenarioPath = speakScenarioMap.get(state);

        if(scenarioPath != null)
            speakScenario = XmlParser.loadScenario(scenarioPath);
        else
        {
            String sentence = speakSentenceMap.get(state);

            if(sentence != null)
            {
                // TODO: aggiustare replica codice
                speakScenario = new ActionSequence("Parla", ActionSequence.Mode.SEQUENCE);
                speakScenario.append(() -> speak(sentence));
            }
        }

        // TODO: aggiustare
        if(continueScenario)
            GameManager.continueScenario();
    }


    // TODO :Aggiustare chiamatre
    public void speak()
    {
        if (speakScenario != null)
            GameManager.startScenario(speakScenario);
        else if(speakScenarioMap.containsKey(state))
        {
            speakScenario = XmlParser.loadScenario(speakScenarioMap.get(state));
            GameManager.startScenario(speakScenario);
        }
        else if(speakSentenceMap.containsKey(state))
        {
            this.speakScenario = new ActionSequence("Parla", ActionSequence.Mode.SEQUENCE);
            speakScenario.append(() -> this.speak(speakSentenceMap.get(state)));

            GameManager.startScenario(speakScenario);
        }
        else
            throw new GameException("speakScenario non impostato");
    }
}
