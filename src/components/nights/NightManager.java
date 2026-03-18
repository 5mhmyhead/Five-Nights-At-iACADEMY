package components.nights;

public class NightManager
{
    private int currentNight = 0;
    private static final NightConfig[] NIGHTS =
    {
        new NightConfig(1,  3),
        new NightConfig(2, 10),
        new NightConfig(3, 20),
    };

    public NightConfig getConfig() { return NIGHTS[currentNight]; }
    public int getNightNumber() { return NIGHTS[currentNight].night; }
    public boolean isFinalNight() { return currentNight == NIGHTS.length - 1; }

    public void advanceNight()
    {
        if(!isFinalNight()) currentNight++;
    }
}
