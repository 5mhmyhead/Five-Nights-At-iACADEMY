package components.nights;

public class NightManager
{
    private int currentNight = 0;
    private boolean isCustomNight = false;
    private NightConfig customConfig = null;

    private static final NightConfig[] NIGHTS =
    {
        // ORDER IS DAVE, EARL, TYRONE, CRISTIAN, JIRSTEN, LANZE
        new NightConfig(1, 4, 8, 6, 0, 0, 4),
        new NightConfig(2, 6, 10, 8, 0, 5, 6),
        new NightConfig(3, 8, 12, 10, 6, 8, 8),
        new NightConfig(4, 10, 14, 12, 9, 11, 10),
        new NightConfig(5, 12, 16, 14, 12, 14, 12),
    };

    public void setCustomConfig(NightConfig config)
    {
        customConfig  = config;
        isCustomNight = true;
    }

    public void clearCustomNight()
    {
        customConfig  = null;
        isCustomNight = false;
    }

    public void loadNight(int nightNumber)
    {
        currentNight = Math.max(0, Math.min(nightNumber - 1, NIGHTS.length - 1));
    }

    public NightConfig getConfig()
    {
        return isCustomNight ? customConfig : NIGHTS[currentNight];
    }

    public int getNightNumber()
    {
        return isCustomNight ? 6 : NIGHTS[currentNight].night();
    }

    public boolean isFinalNight()
    {
        return currentNight == NIGHTS.length - 1;
    }

    public void advanceNight()
    {
        if(!isFinalNight()) currentNight++;
    }

    public boolean isCustomNight()
    {
        return isCustomNight;
    }
}
