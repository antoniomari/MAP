package events.executors;

import GUI.GameScreenPanel;
import GUI.MainFrame;

public class Executor
{
    protected static MainFrame mainFrame;
    protected static GameScreenPanel gameScreenPanel;
    
    public static void setMainFrame(MainFrame frame)
    {
        mainFrame = frame;
        gameScreenPanel = frame.getGameScreenPanel();
    }
}
