package utilities;

import java.io.*;
import java.nio.file.*;

public class SaveManager
{
    private static final String SAVE_DIR  = System.getProperty("user.home") + "/NightsAtIAcademy";
    private static final String SAVE_FILE = SAVE_DIR + "/save.dat";

    public static void save(int nightNumber, int stars, boolean customNightUnlocked)
    {
        try
        {
            Files.createDirectories(Paths.get(SAVE_DIR));
            try(PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE)))
            {
                writer.println(nightNumber);
                writer.println(stars);
                writer.println(customNightUnlocked);
            }
        }
        catch(IOException e)
        {
            System.out.println("Failed to save: " + e.getMessage());
        }
    }

    public static int loadNight()
    {
        try
        {
            File file = new File(SAVE_FILE);
            if(!file.exists()) return 1;
            try(BufferedReader reader = new BufferedReader(new FileReader(file)))
            {
                return Integer.parseInt(reader.readLine().trim());
            }
        }
        catch(IOException | NumberFormatException e) { return 1; }
    }

    public static int loadStars()
    {
        try
        {
            File file = new File(SAVE_FILE);
            if(!file.exists()) return 0;
            try(BufferedReader reader = new BufferedReader(new FileReader(file)))
            {
                reader.readLine(); // SKIP NIGHT NUMBER
                return Integer.parseInt(reader.readLine().trim());
            }
        }
        catch(IOException | NumberFormatException e) { return 0; }
    }

    public static boolean isCustomNightUnlocked()
    {
        try
        {
            File file = new File(SAVE_FILE);
            if(!file.exists()) return false;
            try(BufferedReader reader = new BufferedReader(new FileReader(file)))
            {
                reader.readLine(); // SKIP NIGHT NUMBER
                reader.readLine(); // SKIP STARS
                return Boolean.parseBoolean(reader.readLine().trim());
            }
        }
        catch(IOException e) { return false; }
    }

    public static boolean hasSave()
    {
        return loadNight() > 1;
    }

    public static void deleteSave()
    {
        new File(SAVE_FILE).delete();
    }
}
