package general;

import gui.GameKeyListener;
import gui.MainFrame;
import gui.miniGames.MiniGame;
import entity.GamePiece;
import entity.characters.PlayingCharacter;
import entity.rooms.BlockPosition;
import entity.rooms.Room;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import java.util.*;

public class GameManager
{
    /** Dimensione dei blocchi (in pixel, facendo riferimento alle immagini originali). */
    public static final int BLOCK_SIZE = 24;

    /** Frame del gioco. */
    private static MainFrame mainFrame;

    /** Dizionario delle stanze Nome->Room. */
    private static final Map<String, Room> rooms = new HashMap<>();
    /** Dizionario dei GamePiece Nome->GamePiece. */
    private static final Map<String, GamePiece> pieces = new HashMap<>();
    /** Stack per l'esecuzione degli scenari. */
    private static final Stack<ActionSequence> scenarioStack = new Stack<>();
    /** Stato di gioco. */
    private static GameState currentState;

    /**
     * Frasi da pronunciare quando una porta è bloccata
     * (utilizzate in {@link GameManager#arrowMovement(Room.Cardinal)}
     */
    private static final String[] ROOM_LOCKED_SENTENCES = {
            "Non si vuole aprire", "L'entrata è chiusa",
            "Accipicchia, non passo!", "Infami, hanno chiuso!",
            "La porta è bloccata", "AIUTO! NON SI APRE!",
            "Tecnicooo! La porta è bloccata", "Mi sa che da qui non si va"};

    /**
     * Stati di gioco possibili
     */
    public enum GameState
    {
        /** Menu (principale o di pausa). */
        INIT,
        /** Scena di gioco in cui il giocatore può interagire liberamente con la stanza. */
        PLAYING,
        /** Animazione in corso */
        ANIMATION,
        /** Display della barra di testo. */
        TEXT_BAR,
        /** Riproduzione di un suono durante uno scenario. */
        SCENARIO_SOUND,
        /** Esecuzione di un test. */
        TEST,
        /** Visualizzazione del risultato di un test. */
        TEST_RESULT
    }

    static
    {
        // load Schwartz
        pieces.put(PlayingCharacter.getPlayerName(), PlayingCharacter.getPlayer());

        // poni il gioco in stato INIT
        currentState = GameState.INIT;
    }

    /**
     * Modifica lo stato di gioco.
     *
     * @param newState nuovo stato
     */
    public static synchronized void changeState(GameState newState)
    {
        Objects.requireNonNull(newState);

        currentState = newState;
        LogOutputManager.logOutput("[" + LocalDateTime.now() + "] Nuovo stato: " + currentState, LogOutputManager.GAMESTATE_COLOR);
    }

    /**
     * Restituisce lo stato attuale del gioco.
     *
     * @return stato attuale del gioco
     */
    public static synchronized GameState getState()
    {
        return currentState;
    }

    /**
     * Registra il frame di gioco presso il GameManager.
     *
     * Inizializza conseguentemente i listener di gioco.
     *
     * @param frame frame di gioco
     */
    public static void setMainFrame(MainFrame frame)
    {
        mainFrame = frame;

        initGameListeners();
    }

    public static MainFrame getMainFrame()
    {
        return mainFrame;
    }

    /**
     * Registra un GamePiece presso il GameManager.
     *
     * @param p GamePiece da registrare
     */
    public static void addPiece(GamePiece p)
    {
        pieces.put(p.getName(), p);
    }

    /**
     * Rimuovi un GamePiece dal GameManager.
     *
     * @param p GamePiece da rimuovere
     */
    public static void removePiece(GamePiece p)
    {
        Objects.requireNonNull(p);

        pieces.remove(p.getName());
    }

    /**
     * Registra una Room presso il GameManager.
     *
     * @param room Room da registrare
     */
    public static void addRoom(Room room)
    {
        rooms.put(room.getName(), room);
    }

    /**
     * Ottieni una Room tramite il suo nome
     *
     * @param roomName nome della Room da cercare
     * @return Room corrispondente
     */
    public static Room getRoom(String roomName)
    {
        return rooms.get(roomName);
    }

    /**
     * Ottieni un GamePiece tramite il suo nome
     *
     * @param name nome del GamePiece da cercare
     * @return GamePiece corrispondente
     */
    public static GamePiece getPiece(String name)
    {
        return pieces.get(name);
    }


    /**
     * Ottieni l'insieme dei nomi delle Room registrate
     * presso il GameManager.
     *
     * @return insieme dei nomi delle Room registrate
     */
    public static Set<String> getRoomNames()
    {
        return rooms.keySet();
    }

    /**
     * Inizia l'esecuzione di uno scenario, eseguendo la sua prima azione.
     *
     * Se lo scenario si era già concluso (cioè sta venendo riutilizzato),
     * allora viene ri-iniziato.
     *
     * @param scenario scenario da eseguire
     */
    public static synchronized void startScenario(ActionSequence scenario)
    {
        scenarioStack.push(scenario);

        LogOutputManager.logOutput("Stack scenari: " + scenarioStack, LogOutputManager.SCENARIO_STACK_COLOR);

        // se non è la prima volta che si esegue lo scenario
        if(scenario.isConcluded())
            scenario.rewind();


        scenario.runAction();
    }

    /**
     * Continua l'esecuzione dell'ultimo scenario
     * presente nella pila {@link GameManager#scenarioStack}.
     *
     * Se questo è ora finito, lo rimuove e riesegue l'operazione
     * sul nuovo top, procedendo finché lo {@link GameManager#scenarioStack}
     * non è vuoto
     */
    public static synchronized void continueScenario()
    {
        if(scenarioStack.isEmpty())
        {
            changeState(GameState.PLAYING);
            return;
        }

        ActionSequence top = scenarioStack.peek();
            if(top.isConcluded())
            {
                scenarioStack.remove(top);
                LogOutputManager.logOutput("Stack scenari: " + scenarioStack,
                                            LogOutputManager.SCENARIO_STACK_COLOR);

                // continua lo scenario precedente nello stack
                continueScenario();
            }
            else
                top.runAction();
    }

    /**
     * Inizializza listener
     */
    public static void initGameListeners()
    {
        // LISTENER TASTO ESC -> MOSTRA MENU DI PAUSA
        mainFrame.addKeyListener(new GameKeyListener(
                KeyEvent.VK_ESCAPE,
                () -> mainFrame.showMenu(!mainFrame.isMenuDisplaying()),
                null,
                GameState.PLAYING));

        // LISTENER TASTO FRECCIA SINISTRA -> PROVA AD ANDARE ALLA ROOM A OVEST
        KeyListener leftArrowListener = new GameKeyListener(KeyEvent.VK_LEFT,
                () -> arrowMovement(Room.Cardinal.WEST), null, GameState.PLAYING);
        // LISTENER TASTO FRECCIA DESTRA -> PROVA AD ANDARE ALLA ROOM A EST
        KeyListener rightArrowListener = new GameKeyListener(KeyEvent.VK_RIGHT,
                () -> arrowMovement(Room.Cardinal.EAST), null, GameState.PLAYING);
        // LISTENER TASTO FRECCIA IN ALTO -> PROVA AD ANDARE ALLA ROOM A NORD
        KeyListener upArrowListener = new GameKeyListener(KeyEvent.VK_UP,
                () -> arrowMovement(Room.Cardinal.NORTH), null, GameState.PLAYING);
        // LISTENER TASTO FRECCIA IN BASSO -> PROVA AD ANDARE ALLA ROOM A SUD
        KeyListener downArrowListener = new GameKeyListener(KeyEvent.VK_DOWN,
                () -> arrowMovement(Room.Cardinal.SOUTH), null, GameState.PLAYING);

        mainFrame.addKeyListener(leftArrowListener);
        mainFrame.addKeyListener(rightArrowListener);
        mainFrame.addKeyListener(upArrowListener);
        mainFrame.addKeyListener(downArrowListener);

        // LISTENER TASTO BARRA SPAZIATRICE -> CHIUDI TEXT BAR
        GameKeyListener closeBarListener = new GameKeyListener(
                KeyEvent.VK_SPACE,
                () ->
                {
                    mainFrame.getTextBarPanel().hideTextBar();
                    GameManager.continueScenario();
                },
                null, GameState.TEXT_BAR);
        mainFrame.addKeyListener(closeBarListener);

        // LISTENER TASTO ESC -> CHIUDI TEST DURANTE L'ESECUZIONE
        GameKeyListener quitTestListener = new GameKeyListener(KeyEvent.VK_ESCAPE, MiniGame::quitCurrentTest,
                null, GameState.TEST);
        mainFrame.addKeyListener(quitTestListener);
    }

    /**
     * Prova a muoverti nella stanza verso la direzione specificata.
     *
     * Se esiste una stanza adiacente in tale direzione, il personaggio giocante
     * si muoverà verso l'entrata corrispondente e se questa non è bloccata allora
     * cambierà stanza, altrimenti riprodurrà l'emoji "fumo" e pronuncerà una frase.
     *
     * @param cardinal direzione verso cui provare a muoversi
     */
    private static void arrowMovement(Room.Cardinal cardinal)
    {
        Room currentRoom = mainFrame.getCurrentRoom();
        Room adjacent = currentRoom.getAdjacentRoom(cardinal);
        PlayingCharacter schwartz = PlayingCharacter.getPlayer();

        if (adjacent != null)
        {
            changeState(GameState.ANIMATION);
            ActionSequence scenario = new ActionSequence("vai a " + cardinal.toString());

            BlockPosition entrancePos = currentRoom.getFloor()
                    .getNearestPlacement(
                            currentRoom.getArrowPosition(cardinal).relativePosition(-2,0), schwartz);

            scenario.append(() -> schwartz.move(entrancePos, "absolute", 0));


            if(currentRoom.isAdjacentLocked(cardinal))
            {
                scenario.append(() -> schwartz.playEmoji("fumo"));
                scenario.append(() -> schwartz.speak(Util.randomChoice(ROOM_LOCKED_SENTENCES)));
            }

            else
                scenario.append(() -> mainFrame.setCurrentRoom(adjacent));

            GameManager.startScenario(scenario);
        }
    }
}


