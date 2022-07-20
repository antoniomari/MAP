package GUI.gamestate;

import GUI.*;
import entity.characters.PlayingCharacter;
import entity.items.Item;
import entity.rooms.BlockPosition;
import general.GameManager;
import general.LogOutputManager;

import java.awt.*;
import java.awt.event.KeyEvent;
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
