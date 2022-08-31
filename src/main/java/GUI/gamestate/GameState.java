package GUI.gamestate;

import GUI.AbsPosition;
import GUI.GameKeyListener;
import GUI.GameScreenManager;
import GUI.MainFrame;
import entity.characters.PlayingCharacter;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
import general.ActionSequence;
import general.GameManager;
import general.LogOutputManager;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

public class GameState
{
    private static State currentState;
    private static MainFrame mainFrame;
    private static final String[] ROOM_LOCKED_SENTENCES = {"Non si vuole aprire", "L'entrata è chiusa",
                                                    "Accipicchia, non passo!", "Infami, hanno chiuso!",
                                                    "La porta è bloccata", "AIUTO! NON SI APRE!",
                                                    "Tecnicooo! La porta è bloccata", "Mi sa che da qui non si va"};

    /** Azione per il movimento del giocatore. */
    private static final Runnable leftMouseClick = () ->
            {
                // se non hai alcun elemento selezionato allora prova a muoverti
                if (mainFrame.getInventoryPanel().getSelectedItem() == null)
                {
                    BlockPosition destinationPosition = GameScreenManager.calculateBlocks(
                            new AbsPosition(mainFrame.getMousePosition().x ,mainFrame.getMousePosition().y)).relativePosition(-2, 0); //nota: -2, 0 per le dim personaggi

                    if(mainFrame.getCurrentRoom().getFloor().isWalkable(destinationPosition))
                    {
                        PlayingCharacter.getPlayer().move(mainFrame.getCurrentRoom().getFloor().getNearestPlacement(destinationPosition, PlayingCharacter.getPlayer()), "absolute", 0);
                    }
                }
            };

    public enum State
    {
        INIT,
        PLAYING,
        MOVING,
        TEXT_BAR,
        SCENARIO_SOUND
    }

    static
    {
        currentState = State.INIT;
    }

    public static void setMainFrame(MainFrame frame)
    {
        mainFrame = frame;

        GameKeyListener escListener = new GameKeyListener(
                KeyEvent.VK_ESCAPE,
                () -> mainFrame.showMenu(!mainFrame.isMenuDisplaying()), null);
        mainFrame.addKeyListener(escListener);

        initGameListeners();
    }

    private static <T>  T randomChoice(T[] array)
    {
        Random random = new Random();

        //numero da 0 a len-1
        return array[random.nextInt(array.length + 1) - 1];
    }

    private static void arrowMovement(Room.Cardinal cardinal)
    {
        Room currentRoom = mainFrame.getCurrentRoom();
        Room adjacent = currentRoom.getAdjacentRoom(cardinal);
        PlayingCharacter schwartz = PlayingCharacter.getPlayer();

        if (adjacent != null)
        {
            GameState.changeState(State.MOVING);
            ActionSequence scenario = new ActionSequence("vai a" + cardinal.toString(),
                                                        ActionSequence.Mode.SEQUENCE);

            BlockPosition entrancePos = currentRoom.getFloor()
                    .getNearestPlacement(
                            currentRoom.getArrowPosition(cardinal).relativePosition(-2,0), schwartz);

            scenario.append(() -> schwartz.move(entrancePos, "absolute", 200));


            if(currentRoom.isAdjacentLocked(cardinal))
            {
                scenario.append(() -> schwartz.playEmoji("fumo"));
                scenario.append(() -> schwartz.speak(randomChoice(ROOM_LOCKED_SENTENCES)));
            }

            else
                scenario.append(() -> mainFrame.setCurrentRoom(adjacent));

            GameManager.startScenario(scenario);
        }
    }

    public static void initGameListeners()
    {

        KeyListener leftArrowListener = new GameKeyListener(KeyEvent.VK_LEFT,
            () -> arrowMovement(Room.Cardinal.WEST), null, State.PLAYING);

        KeyListener rightArrowListener = new GameKeyListener(KeyEvent.VK_RIGHT,
            () -> arrowMovement(Room.Cardinal.EAST), null, State.PLAYING);

        KeyListener upArrowListener = new GameKeyListener(KeyEvent.VK_UP,
            () -> arrowMovement(Room.Cardinal.NORTH), null, State.PLAYING);

        KeyListener downArrowListener = new GameKeyListener(KeyEvent.VK_DOWN,
            () -> arrowMovement(Room.Cardinal.SOUTH), null, State.PLAYING);

        mainFrame.addKeyListener(leftArrowListener);
        mainFrame.addKeyListener(rightArrowListener);
        mainFrame.addKeyListener(upArrowListener);
        mainFrame.addKeyListener(downArrowListener);

        GameKeyListener closeBarListener = new GameKeyListener(KeyEvent.VK_SPACE,
                                                                mainFrame.getTextBarPanel()::hideTextBar,
                                                                null, State.TEXT_BAR);
        mainFrame.addKeyListener(closeBarListener);
    }

    public static synchronized void changeState(State newState)
    {
        Objects.requireNonNull(newState);

        currentState = newState;
        LogOutputManager.logOutput("[" + LocalDateTime.now() + "] Nuovo stato: " + currentState, LogOutputManager.GAMESTATE_COLOR);

    }

    public static synchronized State getState()
    {
        return currentState;
    }

}
