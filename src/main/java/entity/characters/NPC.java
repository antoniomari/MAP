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
    /** Dizionario STATO-> sentence da pronunciare associata. */
    private Map<String, String> speakSentenceMap;
    /** Scenario da eseguire all'interazione "speak". */
    ActionSequence speakScenario;

    /**
     * Crea un NPC avente uno sprite-sheet e un json.
     *
     * @param name nome da assegnare all'NPC
     * @param spriteSheetPath path dello sprite-sheet dell'NPC
     * @param jsonPath path del json collegato allo sprite-sheet
     */
    public NPC(String name, String spriteSheetPath, String jsonPath)
    {
        super(name, spriteSheetPath, jsonPath);
    }

    /**
     * Crea un NPC avente un unico sprite.
     *
     * @param name nome da assegnare all'NPC
     * @param spritePath path dello sprite dell'NPC
     */
    public NPC(String name, String spritePath)
    {
        super(name, spritePath);
    }

    public void loadSpeakScenarios(Map<String, String> speakScenarioMap, Map<String, String> speakSentenceMap)
    {
        this.speakScenarioMap = speakScenarioMap;
        this.speakSentenceMap = speakSentenceMap;
    }


    /**
     * Imposta lo stato di this.
     *
     * Carica quindi lo scenario o la frase da pronunciare
     * per l'interazione "speak" associato al nuovo stato.
     *
     * @param state nuovo stato di this
     */
    @Override
    public void setState(String state)
    {
        super.setState(state);

        // imposta speak scenario (o speak sentence) in base a state
        String scenarioPath = speakScenarioMap.get(state);

        if(scenarioPath != null)
            speakScenario = XmlParser.loadScenario(scenarioPath);
        else
        {
            String sentence = speakSentenceMap.get(state);

            if(sentence != null)
            {
                speakScenario = new ActionSequence("Parla");
                speakScenario.append(() -> speak(sentence));
            }
        }
    }


    /**
     * Esegue l'interazione speak, eseguendo lo scenario
     * o pronunciando la frase associata allo stato in cui
     * this si trova attualmente.
     */
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
            this.speakScenario = new ActionSequence("Parla");
            speakScenario.append(() -> this.speak(speakSentenceMap.get(state)));
            GameManager.startScenario(speakScenario);
        }
        else
            throw new GameException("speakScenario non impostato");
    }
}
