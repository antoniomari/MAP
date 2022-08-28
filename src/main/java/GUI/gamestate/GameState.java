package GUI.gamestate;

import GUI.AbsPosition;
import GUI.GameKeyListener;
import GUI.GameMouseListener;
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
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.Objects;

public class GameState
{
    private static State currentState;
    private static MainFrame mainFrame;

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

        initListeners();
    }

    public static void initListeners()
    {
        // GameMouseListener dropListener = new GameMouseListener(MouseEvent.BUTTON1, null, leftMouseClick, State.PLAYING);
        // mainFrame.getGameScreenPanel().addMouseListener(dropListener);


        KeyListener leftArrowListener = new GameKeyListener(KeyEvent.VK_LEFT,
            () ->
            {
                Room west = mainFrame.getCurrentRoom().getWest();

                if (west != null)
                {
                    ActionSequence scenario = new ActionSequence("vai a ovest", ActionSequence.Mode.SEQUENCE);
                    scenario.append(() -> PlayingCharacter.getPlayer().move(mainFrame.getCurrentRoom().getFloor().getNearestPlacement(mainFrame.getCurrentRoom().getArrowPosition("west").relativePosition(-2,0), PlayingCharacter.getPlayer()), "absolute", 0));
                    scenario.append(() -> mainFrame.setCurrentRoom(west));

                    GameManager.startScenario(scenario);
                }
            }, null, State.PLAYING);

        KeyListener rightArrowListener = new GameKeyListener(KeyEvent.VK_RIGHT,
            () ->
            {
                Room east = mainFrame.getCurrentRoom().getEast();

                if (east != null)
                {
                    ActionSequence scenario = new ActionSequence("vai a est", ActionSequence.Mode.SEQUENCE);
                    scenario.append(() -> PlayingCharacter.getPlayer().move(mainFrame.getCurrentRoom().getFloor().getNearestPlacement(mainFrame.getCurrentRoom().getArrowPosition("east").relativePosition(-2,0), PlayingCharacter.getPlayer()), "absolute", 0));
                    scenario.append(() -> mainFrame.setCurrentRoom(east));

                    GameManager.startScenario(scenario);
                }
            }, null, State.PLAYING);

        KeyListener upArrowListener = new GameKeyListener(KeyEvent.VK_UP,
            () ->
            {
                Room north = mainFrame.getCurrentRoom().getNorth();

                if (north != null)
                {
                    ActionSequence scenario = new ActionSequence("vai a nord", ActionSequence.Mode.SEQUENCE);
                    scenario.append(() -> PlayingCharacter.getPlayer().move(mainFrame.getCurrentRoom().getFloor().getNearestPlacement(mainFrame.getCurrentRoom().getArrowPosition("north").relativePosition(-2,0), PlayingCharacter.getPlayer()), "absolute", 0));
                    scenario.append(() -> mainFrame.setCurrentRoom(north));

                    GameManager.startScenario(scenario);
                }
            }, null, State.PLAYING);

        KeyListener downArrowListener = new GameKeyListener(KeyEvent.VK_DOWN,
            () ->
            {
                Room south = mainFrame.getCurrentRoom().getSouth();

                if (south != null)
                {
                    ActionSequence scenario = new ActionSequence("vai a sud", ActionSequence.Mode.SEQUENCE);
                    scenario.append(() -> PlayingCharacter.getPlayer().move(mainFrame.getCurrentRoom().getFloor().getNearestPlacement(mainFrame.getCurrentRoom().getArrowPosition("south").relativePosition(-2,0), PlayingCharacter.getPlayer()), "absolute", 0));
                    scenario.append(() -> mainFrame.setCurrentRoom(south));

                    GameManager.startScenario(scenario);
                }
            }, null, State.PLAYING);

        mainFrame.addKeyListener(leftArrowListener);
        mainFrame.addKeyListener(rightArrowListener);
        mainFrame.addKeyListener(upArrowListener);
        mainFrame.addKeyListener(downArrowListener);

        GameKeyListener closeBarListener = new GameKeyListener(KeyEvent.VK_SPACE,
                                                                mainFrame.getTextBarPanel()::hideTextBar,
                                                                null, State.TEXT_BAR);
        mainFrame.addKeyListener(closeBarListener);

        // listener per iniziare il gioco
        GameKeyListener startGameListener = new GameKeyListener(KeyEvent.VK_SPACE, mainFrame::play, null,
                State.INIT);
        mainFrame.addKeyListener(startGameListener);
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
