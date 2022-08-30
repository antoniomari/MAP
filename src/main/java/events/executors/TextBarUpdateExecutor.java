package events.executors;

import entity.characters.GameCharacter;

public class TextBarUpdateExecutor extends Executor
{
    public static void executeDisplay(String text)
    {
        mainFrame.getTextBarPanel().showTextBar(text);
    }
}
