package events.executors;

import gui.GameScreenPanel;
import gui.InventoryPanel;
import gui.MainFrame;

import java.util.Objects;

/**
 * Un esecutore ha il compito di mantenere aggiornata
 * la GUI con i cambiamenti che avvengono nelle entity
 * (classi di dominio) del gioco.
 */
public class Executor
{
    protected static MainFrame mainFrame;
    protected static GameScreenPanel gameScreenPanel;
    protected static InventoryPanel inventoryPanel;
    
    public static void setMainFrame(MainFrame frame)
    {
        Objects.requireNonNull(frame);

        mainFrame = frame;
        gameScreenPanel = frame.getGameScreenPanel();
        inventoryPanel = frame.getInventoryPanel();
    }
}
