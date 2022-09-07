package general;

import jdk.jpackage.internal.Log;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FontManager
{
    private final static String MIST_QUESTION_PATH = "src/main/resources/font/AmericanTypewriter.ttf";
    private final static String MIST_TITLE_PATH = "src/main/resources/font/FontMaledetto.ttf";
    private final static String MIST_DESCRIPTION_PATH = "src/main/resources/font/Bookman Old Style Regular.ttf";

    private final static String LOGIC_DESCRIPTION_PATH = "src/main/resources/font/FbiOld.ttf";
    private final static String CAPTCHA_DESCRIPTION_PATH = "src/main/resources/font/AgencyFb.TTF";

    public static Font MIST_QUESTION_FONT;
    public static Font MIST_TITLE_FONT;
    public static Font MIST_DESCRIPTION_FONT;
    public static Font LOGIC_DESCRIPTION_FONT;
    public static Font CAPTCHA_DESCRIPTION_FONT;

    public static void loadFonts()
    {
        LogOutputManager.logOutput("[##### Caricamento Font #####]", LogOutputManager.EVENT_COLOR);
        try
        {
            MIST_QUESTION_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(MIST_QUESTION_PATH));
            LogOutputManager.logOutput("[FONT] Caricato MIST_QUESTION");
            MIST_TITLE_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(MIST_TITLE_PATH));
            LogOutputManager.logOutput("[FONT] Caricato MIST_TITLE");
            MIST_DESCRIPTION_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(MIST_DESCRIPTION_PATH));
            LogOutputManager.logOutput("[FONT] Caricato MIST_DESCRIPTION");

            LOGIC_DESCRIPTION_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(LOGIC_DESCRIPTION_PATH));
            LogOutputManager.logOutput("[FONT] Caricato LOGIC_DESCRIPTION");

            CAPTCHA_DESCRIPTION_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(CAPTCHA_DESCRIPTION_PATH));
            LogOutputManager.logOutput("[FONT] Caricato CAPTCHA_DESCRIPTION");

        } catch (IOException | FontFormatException e)
        {
            // errore nel caricamento del font
            LogOutputManager.logOutput("Errore caricamento font", LogOutputManager.EXCEPTION_COLOR);
            LogOutputManager.logOutput("Causato da: " + e.getMessage(), LogOutputManager.EXCEPTION_COLOR);
            LogOutputManager.logOutput("Dettagli: " + e.getLocalizedMessage(), LogOutputManager.EXCEPTION_COLOR);

            // utilizza i font di default
            throw new GameException("Errore caricamento font " + MIST_QUESTION_PATH);

        }
    }
}
