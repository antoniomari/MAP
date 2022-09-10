package entity.characters;

import entity.GamePiece;
import events.CharacterEvent;
import events.EventHandler;
import graphics.SpriteManager;
import org.json.JSONException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class GameCharacter extends GamePiece
{
    /** Sprite-sheet contenente tutti gli sprite di this. */
    private BufferedImage spriteSheet;
    /** Path del json collegato allo sprite-sheet. */
    private String jsonPath;
    /** Frame "speaking" per l'animazione collegata all'azione "speak". */
    private Image speakFrame;

    /**
     * Crea un GameCharacter avente un unico sprite.
     *
     * @param name nome da assegnare al GameCharacter
     * @param spritePath path dello sprite del GameCharacter
     */
    public GameCharacter(String name, String spritePath)
    {
        super(name, spritePath);
        defaultInitSpeakFrame();
    }


    /**
     * Crea un GameCharacter avente uno sprite-sheet e un json.
     *
     * @param name nome da assegnare al GameCharacter
     * @param spriteSheetPath path dello sprite-sheet del GameCharacter
     * @param jsonPath path del json collegato allo sprite-sheet
     */
    public GameCharacter(String name, String spriteSheetPath, String jsonPath)
    {
        super(name, SpriteManager.loadImage(spriteSheetPath), jsonPath);
        spriteSheet = SpriteManager.loadImage(spriteSheetPath);
        this.jsonPath = jsonPath;
        initAnimateFrames();
        initMovingFrames();
        initSpeakFrame();
    }


    /**
     * Inizializza i frame di movimento.
     *
     * Sia per "left" che per "right" (di seguito si porta solo come esempio "left):
     * <ol>
     *     <li>Cerca oggetti json "leftMoving1", "leftMoving2", ...</li>
     *     <li>Se questi non sono trovati cerca oggetti json "[name]leftMoving1", "[name]leftMoving2", ...,
     *     dove name è il nome di this</li>
     *     <li>infine aggiunge come primo frame lo sprite principale di this</li>
     * </ol>
     */
    private void initMovingFrames()
    {
        // prova con leftMoving
        leftMovingFrames = SpriteManager.getKeywordOrderedFrames(spriteSheet, jsonPath, "leftMoving");
        rightMovingFrames = SpriteManager.getKeywordOrderedFrames(spriteSheet, jsonPath, "rightMoving");

        if(leftMovingFrames.isEmpty())
        {
            leftMovingFrames = SpriteManager.getKeywordOrderedFrames(spriteSheet, jsonPath,  getName() + "leftMoving");
        }

        if(rightMovingFrames.isEmpty())
        {
            rightMovingFrames = SpriteManager.getKeywordOrderedFrames(spriteSheet, jsonPath, getName() + "rightMoving");
        }

        leftMovingFrames.add(0, getSprite());
        rightMovingFrames.add(0, getSprite());
    }

    /**
     * Inizializza i frame di animazione personalizzata sulla base del contenuto
     * dello sprite-sheet e del json.
     */
    private void initAnimateFrames()
    {
        animateFrames = SpriteManager.getKeywordOrderedFrames(spriteSheet, jsonPath, "animate");

        if(animateFrames.isEmpty())
            animateFrames = SpriteManager.getKeywordOrderedFrames(spriteSheet, jsonPath, getName() + "animate");

        animateFrames.add(0, getSprite());
    }

    /**
     * Inizializzazione {@link GameCharacter#speakFrame}.
     *
     * Cerca nel json relativo allo sprite-sheet l'oggetto "speaking";
     * se questo non è presente cerca l'oggetto "[name]speaking", dove
     * [name] è il nome di this; se neanche questo è presente allora
     * esegue l'inizializzazione di default {@link GameCharacter#defaultInitSpeakFrame()}.
     */
    private void initSpeakFrame()
    {

        try
        {
            speakFrame = SpriteManager.loadSpriteByName(spriteSheet, jsonPath, "speaking");
        }
        catch(JSONException e)
        {
            try
            {
                speakFrame = SpriteManager.loadSpriteByName(spriteSheet, jsonPath, getName() + "speaking");
            }
            catch(JSONException eg)
            {
                defaultInitSpeakFrame();
            }
        }
    }

    /**
     * Inizializzazione di default per il frame
     * per l'azione speak (uguale allo sprite principale).
     */
    private void defaultInitSpeakFrame()
    {
        speakFrame = sprite;
    }

    /**
     * Restituisce i frame per costruire l'animazione perpetua di speaking.
     *
     * @return frame per l'animazione perpetua di speaking
     */
    public List<Image> getSpeakFrames()
    {
        List<Image> speakFrames = new ArrayList<>();
        speakFrames.add(speakFrame);
        speakFrames.add(speakFrame);
        speakFrames.add(sprite);

        return speakFrames;
    }

    /**
     * Esegue l'interazione "speak".
     *
     * @param sentence frase da far pronunciare a this
     */
    public void speak(String sentence)
    {
        String toPrint =  getName() + ": " + sentence;
        EventHandler.sendEvent(new CharacterEvent(this, toPrint, CharacterEvent.Type.NPC_SPEAKS));
    }

    /**
     * Esegue l'effetto animato di un'emoji sopra la testa del GameCharacter.
     *
     * @param emojiName nome dell'emoji. Valori possibili:
     *                  <ul>
     *                      <li>interrogativo</li>
     *                      <li>esclamativo</li>
     *                      <li>notaMusicale</li>
     *                      <li>cuore</li>
     *                      <li>goccia</li>
     *                      <li>fumo</li>
     *                      <li>puntini</li>
     *                      <li>provocazione</li>
     *                      <li>enfasi</li>
     *                      <li>dormi</li>
     *                  </ul>
     */
    public void playEmoji(String emojiName)
    {
        EventHandler.sendEvent(new CharacterEvent(this, emojiName, CharacterEvent.Type.EMOJI));
    }

}
