package components.nights;

// THIS CLASS HOLDS THE AI LEVELS OF THE ANIMATRONICS FOR ONE NIGHT
// AI LEVELS ARE PASSED MATCHING THE ARRAY IN GAME STATE
public class NightConfig
{
    public final int night;
    public final int[] aiLevels;

    public NightConfig(int night, int... aiLevels)
    {
        this.night = night;
        this.aiLevels = aiLevels;
    }
}
