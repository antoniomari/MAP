package general;

public class LogOutputManager
{
    public static final String EVENT_COLOR = "\u001B[33m";
    public static final String GAMESTATE_COLOR = "\u001B[34m";
    public static final String EXCEPTION_COLOR = "\u001B[31m";
    public static final String SCENARIO_STACK_COLOR = "\u001B[35m";
    public static final String XML_COLOR = "\u001B[36m";
    private static final String RESET_COLOR = "\u001B[0m";

    public static void logOutput(String text, String color)
    {
        System.out.println(color + text + RESET_COLOR);
    }
}
