package components.nights;

public class NightManager
{
    private int currentNight = 0;
    private static final NightConfig[] NIGHTS =
    {
        // ORDER IS DAVE, EARL, TYRONE, CRISTIAN, JIRSTEN, LANZE
        new NightConfig(1, 5, 8, 8, 0, 0, 3),
        new NightConfig(2, 8, 10, 10, 0, 5, 5),
        new NightConfig(3, 10, 12, 12, 10, 10, 10),
        new NightConfig(4, 12, 15, 15, 12, 15, 12),
        new NightConfig(5, 15, 18, 18, 15, 20, 15),
    };

    public NightConfig getConfig() { return NIGHTS[currentNight]; }
    public int getNightNumber() { return NIGHTS[currentNight].night; }
    public boolean isFinalNight() { return currentNight == NIGHTS.length - 1; }

    public void advanceNight()
    {
        if(!isFinalNight()) currentNight++;
    }
}
