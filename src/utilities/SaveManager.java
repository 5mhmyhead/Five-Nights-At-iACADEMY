package utilities;

import java.io.*;
import java.nio.file.*;

public class SaveManager
{
    private static final String SAVE_DIR  = System.getProperty("user.home") + "/NightsAtIAcademy";
    private static final String SAVE_FILE = SAVE_DIR + "/save.dat";

    public static void save(int nightNumber)
    {
        try
        {
            Files.createDirectories(Paths.get(SAVE_DIR));
            try(PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE)))
            {
                writer.println(nightNumber);
            }
        }
        catch(IOException e)
        {
            System.out.println("Failed to save: " + e.getMessage());
        }
    }

    public static int load()
    {
        try
        {
            File file = new File(SAVE_FILE);
            if(!file.exists()) return 1; // NO SAVE — START AT NIGHT 1

            try(BufferedReader reader = new BufferedReader(new FileReader(file)))
            {
                return Integer.parseInt(reader.readLine().trim());
            }
        }
        catch(IOException | NumberFormatException e)
        {
            System.out.println("Failed to load save: " + e.getMessage());
            return 1;
        }
    }

    public static boolean hasSave()
    {
        int savedNight = load();
        return savedNight > 1; // ONLY SHOW CONTINUE IF PAST NIGHT 1
    }

    public static void deleteSave()
    {
        new File(SAVE_FILE).delete();
    }
}
