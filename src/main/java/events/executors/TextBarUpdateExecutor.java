package events.executors;

public class TextBarUpdateExecutor extends Executor
{
    public static void executeDisplay(String text)
    {
        mainFrame.getTextBarPanel().showTextBar(text);
    }
}
