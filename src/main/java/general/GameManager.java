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
    public static final int BLOCK_SIZE = 24;

    private static MainFrame mainFrame;

    private static final Map<String, Room> rooms = new HashMap<>();
    private static final Map<String, GamePiece> pieces = new HashMap<>();

    private static final Stack<ActionSequence> scenarioStack = new Stack<>();

    private static GameState currentState;

    public enum GameState
    {
        /** Inizializz*/
        INIT,
        PLAYING,
        ANIMATION,
        TEXT_BAR,
        SCENARIO_SOUND,
        TEST,
        TEST_RESULT
    }

    static
    {
        // load Schwartz
        pieces.put(PlayingCharacter.getPlayerName(), PlayingCharacter.getPlayer());
        currentState = GameState.INIT;
    }

    public static synchronized void changeState(GameState newState)
    {
        Objects.requireNonNull(newState);

        currentState = newState;
        LogOutputManager.logOutput("[" + LocalDateTime.now() + "] Nuovo stato: " + currentState, LogOutputManager.GAMESTATE_COLOR);
    }

    public static synchronized GameState getState()
    {
        return currentState;
    }


    public static MainFrame getMainFrame()
    {
        return mainFrame;
    }

    public static void addPiece(GamePiece p)
    {
        pieces.put(p.getName(), p);
    }

    public static void removePiece(GamePiece p)
    {
        Objects.requireNonNull(p);

        pieces.remove(p.getName());
    }

    public static void addRoom(Room room)
    {
        rooms.put(room.getName(), room);
    }

    public static Room getRoom(String roomName)
    {
        return rooms.get(roomName);
    }


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

    public static GamePiece getPiece(String name)
    {
        return pieces.get(name);
    }

    public static void initGameListeners()
    {

        KeyListener leftArrowListener = new GameKeyListener(KeyEvent.VK_LEFT,
                () -> arrowMovement(Room.Cardinal.WEST), null, GameState.PLAYING);

        KeyListener rightArrowListener = new GameKeyListener(KeyEvent.VK_RIGHT,
                () -> arrowMovement(Room.Cardinal.EAST), null, GameState.PLAYING);

        KeyListener upArrowListener = new GameKeyListener(KeyEvent.VK_UP,
                () -> arrowMovement(Room.Cardinal.NORTH), null, GameState.PLAYING);

        KeyListener downArrowListener = new GameKeyListener(KeyEvent.VK_DOWN,
                () -> arrowMovement(Room.Cardinal.SOUTH), null, GameState.PLAYING);

        mainFrame.addKeyListener(leftArrowListener);
        mainFrame.addKeyListener(rightArrowListener);
        mainFrame.addKeyListener(upArrowListener);
        mainFrame.addKeyListener(downArrowListener);

        GameKeyListener closeBarListener = new GameKeyListener(
                KeyEvent.VK_SPACE,
                () ->
                {
                    mainFrame.getTextBarPanel().hideTextBar();
                    GameManager.continueScenario();
                },
                null, GameState.TEXT_BAR);
        mainFrame.addKeyListener(closeBarListener);


        GameKeyListener quitTestListener = new GameKeyListener(KeyEvent.VK_ESCAPE, MiniGame::quitCurrentTest,
                null, GameState.TEST);
        mainFrame.addKeyListener(quitTestListener);
    }

    private static final String[] ROOM_LOCKED_SENTENCES = {"Non si vuole aprire", "L'entrata è chiusa",
            "Accipicchia, non passo!", "Infami, hanno chiuso!",
            "La porta è bloccata", "AIUTO! NON SI APRE!",
            "Tecnicooo! La porta è bloccata", "Mi sa che da qui non si va"};


    public static void setMainFrame(MainFrame frame)
    {
        mainFrame = frame;

        mainFrame.addKeyListener(new GameKeyListener(
                KeyEvent.VK_ESCAPE,
                () -> mainFrame.showMenu(!mainFrame.isMenuDisplaying()),
                null,
                GameState.PLAYING));

        initGameListeners();
    }

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


