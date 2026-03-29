package utilities;

public class GlobalSettings
{
    private static boolean lowPerformance = false;
    private static boolean reduceJumpscare = false;

    public static void setLowPerformance(boolean enabled)
    {
        lowPerformance = enabled;
    }

    public static boolean isLowPerformance()
    {
        return lowPerformance;
    }

    public static void setReduceJumpscare(boolean enabled)
    {
        reduceJumpscare = enabled;
    }

    public static boolean isReduceJumpscare()
    {
        return reduceJumpscare;
    }
}
