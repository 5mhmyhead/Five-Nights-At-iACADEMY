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

    public static void saveSettings(float masterVolume, boolean reduceJumpscare, boolean lowPerformance)
    {
        try
        {
            Files.createDirectories(Paths.get(SAVE_DIR));
            try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_DIR + "/settings.dat")))
            {
                writer.println(masterVolume);
                writer.println(reduceJumpscare);
                writer.println(lowPerformance);
            }
        }
        catch (IOException e)
        {
            System.out.println("Failed to save settings: " + e.getMessage());
        }
    }

    public static float[] loadSettings()
    {
        // RETURNS, VOLUME, REDUCE, LOW PERF
        try
        {
            File file = new File(SAVE_DIR + "/settings.dat");
            if (!file.exists()) return new float[]{ 1.0f, 0f, 0f };

            try (BufferedReader reader = new BufferedReader(new FileReader(file)))
            {
                float vol = Float.parseFloat(reader.readLine().trim());
                float rj = Boolean.parseBoolean(reader.readLine().trim()) ? 1f : 0f;
                float lp = Boolean.parseBoolean(reader.readLine().trim()) ? 1f : 0f;
                return new float[]{ vol, rj, lp };
            }
        }
        catch (IOException | NumberFormatException e)
        {
            return new float[]{ 1.0f, 0f, 0f };
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
