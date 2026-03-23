package utilities;

// MANAGES ALL SOUND CLIPS FOR THE GAME
public class SoundManager
{
    // AMBIENCE
    public static final SoundPlayer AMBIENCE = new SoundPlayer("/sounds/ambience.wav");

    // MUSIC BOX
    public static final SoundPlayer MUSIC_BOX = new SoundPlayer("/sounds/musicBox.wav");
    public static final SoundPlayer MUSIC_BOX_SPED_UP = new SoundPlayer("/sounds/musicBoxSpedUp.wav");

    // CAMERA UI
    public static final SoundPlayer MONITOR = new SoundPlayer("/sounds/monitor.wav");
    public static final SoundPlayer CAMERA_SWITCH = new SoundPlayer("/sounds/cameraSwitch.wav");
    public static final SoundPlayer CAMERA_REBOOTING = new SoundPlayer("/sounds/cameraRebooting.wav");
    public static final SoundPlayer CAMERA_BROKEN = new SoundPlayer("/sounds/cameraBroken.wav");
    public static final SoundPlayer SHOCK = new SoundPlayer("/sounds/shock.wav");

    // SOUND CUES
    public static final SoundPlayer KNOCK_EARL = new SoundPlayer("/sounds/knockingEarl.wav");
    public static final SoundPlayer KNOCK_DAVE = new SoundPlayer("/sounds/knockingDave.wav");

    public static final SoundPlayer FOOTSTEPS = new SoundPlayer("/sounds/footsteps.wav");
    public static final SoundPlayer JUMPSCARE = new SoundPlayer("/sounds/jumpscare.wav");

    // MISC
    public static final SoundPlayer MAIN_MENU = new SoundPlayer("/sounds/mainMenu.wav");
    public static final SoundPlayer NIGHT_START = new SoundPlayer("/sounds/nightStart.wav");

    public static final SoundPlayer CHEERING = new SoundPlayer("/sounds/cheering.wav");
    public static final SoundPlayer SIX_AM = new SoundPlayer("/sounds/sixAM.wav");
    public static final SoundPlayer RISER = new SoundPlayer("/sounds/riser.wav");
    public static final SoundPlayer HEARTBEAT = new SoundPlayer("/sounds/heartbeat.wav");

    // ARRAY FOR BULK OPERATIONS
    private static final SoundPlayer[] soundPlayers = new SoundPlayer[]
    {
        AMBIENCE, CAMERA_SWITCH, MONITOR, MAIN_MENU, NIGHT_START, HEARTBEAT, KNOCK_EARL, KNOCK_DAVE, FOOTSTEPS,
        MUSIC_BOX, MUSIC_BOX_SPED_UP, SHOCK, CAMERA_REBOOTING, CAMERA_BROKEN, SIX_AM, RISER, CHEERING
    };

    public static void stopAll()
    {
        for(SoundPlayer soundPlayer : soundPlayers)
            soundPlayer.stop();
    }
}