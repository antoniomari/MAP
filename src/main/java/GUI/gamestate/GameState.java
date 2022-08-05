package GUI.gamestate;

import GUI.AbsPosition;
import GUI.GameKeyListener;
import GUI.GameMouseListener;
import GUI.GameScreenManager;
import GUI.MainFrame;
import entity.characters.PlayingCharacter;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
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
    private static GameKeyListener SPACE_LISTENER;
    private static GameMouseListener DROP_LISTENER;

    private static Runnable leftMouseClick = () ->
            {
                // se hai un elemento selezionato e click sinistro su oggetto prova "useWith"
                if (mainFrame.getInventoryPanel().getSelectedItem() == null)
                {
                    BlockPosition bp = GameScreenManager.calculateBlocks(
                            new AbsPosition(mainFrame.getMousePosition().x ,mainFrame.getMousePosition().y)).relativePosition(-2, 0);

                    bp = mainFrame.getCurrentRoom().getFloor().getNearestPlacement(bp, PlayingCharacter.getPlayer().getBWidth(), PlayingCharacter.getPlayer().getBHeight());
                    if(bp != null)
                        PlayingCharacter.getPlayer().updatePosition(bp);
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

        // TODO: migliora
        mainFrame.addKeyListener(new GameKeyListener(KeyEvent.VK_SPACE, mainFrame.getTextBarPanel()::hideTextBar, null, State.TEXT_BAR));
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
                    mainFrame.setCurrentRoom(west);
                }
            }, null);

        KeyListener rightArrowListener = new GameKeyListener(KeyEvent.VK_RIGHT,
            () ->
            {
                Room east = mainFrame.getCurrentRoom().getEast();

                if (east != null)
                {
                    mainFrame.setCurrentRoom(east);
                }
            }, null);

        KeyListener upArrowListener = new GameKeyListener(KeyEvent.VK_UP,
            () ->
            {
                Room north = mainFrame.getCurrentRoom().getNorth();

                if (north != null)
                {
                    mainFrame.setCurrentRoom(north);
                }
            }, null);

        KeyListener downArrowListener = new GameKeyListener(KeyEvent.VK_DOWN,
            () ->
            {
                Room south = mainFrame.getCurrentRoom().getSouth();

                if (south != null)
                {
                    mainFrame.setCurrentRoom(south);
                }
            }, null);

        mainFrame.addKeyListener(leftArrowListener);
        mainFrame.addKeyListener(rightArrowListener);
        mainFrame.addKeyListener(upArrowListener);
        mainFrame.addKeyListener(downArrowListener);
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
