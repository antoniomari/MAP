package GUI.debug;

import java.text.Annotation;

public class MotionDebugger
{
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_RESET = "\u001B[0m";

    public static boolean ACTIVE = true;

    public static void print(String s)
    {
        if(ACTIVE)
            System.out.println(ANSI_CYAN + s + ANSI_RESET);
    }

}
