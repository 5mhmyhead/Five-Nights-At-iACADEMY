package components.nights;

// THIS RECORD CLASS HOLDS THE AI LEVELS OF THE ANIMATRONICS FOR ONE NIGHT
// AI LEVELS ARE PASSED MATCHING THE ARRAY IN GAME STATE
public record NightConfig(int night, int... aiLevels) {}
