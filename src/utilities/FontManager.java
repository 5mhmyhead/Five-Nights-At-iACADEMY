package utilities;

import java.awt.*;

// CLASS THAT HANDLES FONT LOADING
public class FontManager
{
    public static Font LCD_SMALL;
    public static Font LCD_MEDIUM;
    public static Font LCD_LARGE;
    public static Font LCD_CLOCK;
    public static Font LCD_TITLE;
    public static Font LCD_WARNING;

    public static void loadFonts()
    {
        LCD_SMALL = Utility.loadFont("/fonts/lcdSolid.ttf", 15f).deriveFont(Font.BOLD);
        LCD_MEDIUM = Utility.loadFont("/fonts/lcdSolid.ttf", 20f).deriveFont(Font.BOLD);
        LCD_LARGE = Utility.loadFont("/fonts/lcdSolid.ttf", 25f).deriveFont(Font.BOLD);
        LCD_CLOCK = Utility.loadFont("/fonts/lcdSolid.ttf", 40f).deriveFont(Font.BOLD);
        LCD_TITLE = Utility.loadFont("/fonts/lcdSolid.ttf", 72f).deriveFont(Font.BOLD);
        LCD_WARNING = Utility.loadFont("/fonts/lcdSolid.ttf", 128f).deriveFont(Font.BOLD);
    }
}