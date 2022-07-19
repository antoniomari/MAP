package GUI.gamestate;

import GUI.*;
import entity.characters.PlayingCharacter;
import entity.items.Item;
import entity.rooms.BlockPosition;
import general.GameManager;
import general.LogOutputManager;

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

        DROP_LISTENER = new GameMouseListener(MouseEvent.BUTTON1,
                () ->
                {
                    if (mainFrame.getInventoryPanel().getSelectedItem() != null)
                    {
                        //BlockPosition bp = GameScreenManager.calculateBlocks(new AbsPosition(mainFrame.getMousePosition().x ,mainFrame.getMousePosition().y)).relativePosition(-1, 1);
                        //mainFrame.getInventoryPanel().getSelectedItem().drop(mainFrame.getCurrentRoom(), bp);
                        // System.out.println("usato");
                        mainFrame.getInventoryPanel().getSelectedItem().useWith((Item)GameManager.getPiece("Barile"));
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

        // TODO: migliora
        mainFrame.addKeyListener(new GameKeyListener(KeyEvent.VK_SPACE, mainFrame.getTextBarPanel()::hideTextBar, null, State.TEXT_BAR));

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
