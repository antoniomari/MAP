package events.executors;

import GUI.gamestate.GameState;

public class TextBarUpdateExecutor extends Executor
{
    public static void executeDisplay(String text)
    {
        GameState.changeState(GameState.State.TEXT_BAR);
        mainFrame.getTextBarPanel().showTextBar(text);
    }
}
