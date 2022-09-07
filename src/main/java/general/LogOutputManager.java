package general;

import java.io.*;
import java.nio.BufferOverflowException;

public class LogOutputManager
{
    public static final String EVENT_COLOR = "\u001B[33m";
    public static final String GAMESTATE_COLOR = "\u001B[34m";
    public static final String EXCEPTION_COLOR = "\u001B[31m";
    public static final String SCENARIO_STACK_COLOR = "\u001B[35m";
    public static final String XML_COLOR = "\u001B[36m";
    private static final String RESET_COLOR = "\u001B[0m";

    private static final PrintWriter logFileStream;
    private static final PrintStream errFileStream;

    static
    {
        // reindirizza il system.err

        try
        {
            File logFile = new File("./log.txt");
            logFileStream = new PrintWriter(new FileWriter(logFile));
            errFileStream = new PrintStream(new FileOutputStream(logFile));
            System.setErr(errFileStream);
        }
        catch (IOException e)
        {
            throw new GameException("Errore nella creazione file di log");
        }
    }

    public static void logOutput(String text, String color)
    {
        logFileStream.println(text);
    }

    public static void logOutput(String text)
    {
        logFileStream.println(text);
    }

    public static void closeLogFile()
    {
        errFileStream.close();
        logFileStream.close();
    }
}
