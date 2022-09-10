package events.executors;

/**
 * Esecutore per aggiornare la visualizzazione
 * della barra di testo.
 */
public class TextBarUpdateExecutor extends Executor
{
    public static void executeDisplay(String text)
    {
        mainFrame.getTextBarPanel().showTextBar(text);
    }
}
