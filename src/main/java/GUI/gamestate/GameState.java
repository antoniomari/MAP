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
import java.util.Objects;

public class GameState
{
    private static State CURRENT_STATE;
    private static MainFrame mainFrame;
    private static GameKeyListener ESC_LISTENER;
    private static GameMouseListener DROP_LISTENER;

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
                        PlayingCharacter.getPlayer().updatePosition(destinationPosition);
                    }


                            // TODO: ripristinare getNearestPlacement
                    // bp = mainFrame.getCurrentRoom().getFloor().getNearestPlacement(bp, PlayingCharacter.getPlayer().getBWidth(), PlayingCharacter.getPlayer().getBHeight());

                    //if(bp != null)
                    //    PlayingCharacter.getPlayer().updatePosition(bp);
                }
            };

    public enum State
    {
        PLAYING,
        MOVING,
        TEXT_BAR
    }

    static
    {
        CURRENT_STATE = State.PLAYING;
    }

    public static void setMainFrame(MainFrame frame)
    {
        mainFrame = frame;

        ESC_LISTENER = new GameKeyListener(KeyEvent.VK_ESCAPE, () -> mainFrame.showMenu(true), null);
        mainFrame.addKeyListener(ESC_LISTENER);

        initListeners();
    }

    public static void initListeners()
    {
        DROP_LISTENER = new GameMouseListener(MouseEvent.BUTTON1, null, leftMouseClick, State.PLAYING);
        mainFrame.getGameScreenPanel().addMouseListener(DROP_LISTENER);


        KeyListener leftArrowListener = new GameKeyListener(KeyEvent.VK_LEFT,
            () ->
            {
                Room west = mainFrame.getCurrentRoom().getWest();

                if (west != null)
                {
                    ActionSequence scenario = new ActionSequence("vai a ovest", ActionSequence.Mode.SEQUENCE);
                    scenario.append(() -> PlayingCharacter.getPlayer().updatePosition(mainFrame.getCurrentRoom().getArrowPosition("west"), 100));
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
                    scenario.append(() -> PlayingCharacter.getPlayer().updatePosition(mainFrame.getCurrentRoom().getArrowPosition("east"), 100));
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
                    scenario.append(() -> PlayingCharacter.getPlayer().updatePosition(mainFrame.getCurrentRoom().getArrowPosition("north"), 100));
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
                    scenario.append(() -> PlayingCharacter.getPlayer().updatePosition(mainFrame.getCurrentRoom().getArrowPosition("south"), 100));
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
    }

    public static void changeState(State newState)
    {
        Objects.requireNonNull(newState);

        CURRENT_STATE = newState;
        LogOutputManager.logOutput("Nuovo stato: " + CURRENT_STATE, LogOutputManager.GAMESTATE_COLOR);
    }

    public static State getState()
    {
        return CURRENT_STATE;
    }
    // stato PLAYING
    // stato MOVING
    // stato visualizzazione barra di testo


}
