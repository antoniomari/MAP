package GUI.gamestate;

import GUI.*;
import entity.characters.PlayingCharacter;
import entity.rooms.BlockPosition;

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

        SPACE_LISTENER = new GameKeyListener(KeyEvent.VK_SPACE, mainFrame.getTextBarPanel()::hideTextBar, null);

        DROP_LISTENER = new GameMouseListener(MouseEvent.BUTTON1,
                () ->
                {
                    if (mainFrame.getInventoryPanel().getSelectedItem() != null)
                    {
                        BlockPosition bp = GameScreenManager.calculateBlocks(new AbsPosition(mainFrame.getMousePosition().x ,mainFrame.getMousePosition().y)).relativePosition(-1, 1);
                        mainFrame.getInventoryPanel().getSelectedItem().drop(mainFrame.getCurrentRoom(), bp);
                    }
                    else
                    {
                        BlockPosition bp = GameScreenManager.calculateBlocks(
                                new AbsPosition(mainFrame.getMousePosition().x ,mainFrame.getMousePosition().y)).relativePosition(-2, 0);

                        bp = mainFrame.getCurrentRoom().getFloor().getNearestPlacement(bp, PlayingCharacter.getPlayer().getBWidth(), PlayingCharacter.getPlayer().getBHeight());
                        if(bp != null)
                            PlayingCharacter.getPlayer().updatePosition(bp);
                    }
                }

                , null);
        mainFrame.getGameScreenPanel().addMouseListener(DROP_LISTENER);

        mainFrame.addKeyListener(new GameKeyListener(KeyEvent.VK_SPACE, mainFrame.getTextBarPanel()::hideTextBar, () -> changeState(State.PLAYING), State.TEXT_BAR));

    }

    public static void changeState(State newState)
    {
        Objects.requireNonNull(newState);

        CURRENT_STATE = newState;
        System.out.println("Nuovo stato: " + CURRENT_STATE);
    }

    public static State getState()
    {
        return CURRENT_STATE;
    }
    // stato PLAYING
    // stato MOVING
    // stato visualizzazione barra di testo


}
