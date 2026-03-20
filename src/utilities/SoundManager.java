package utilities;

// MANAGES ALL SOUND CLIPS FOR THE GAME
public class SoundManager
{
    // AMBIENCE
    public static final SoundPlayer AMBIENCE = new SoundPlayer("/sounds/ambience.wav");

    // MUSIC BOX
    public static final SoundPlayer MUSIC_BOX = new SoundPlayer("/sounds/musicBox.wav");
    public static final SoundPlayer MUSIC_BOX_SPED_UP = new SoundPlayer("/sounds/musicBoxSpedUp.wav");

    // UI SOUNDS
    public static final SoundPlayer CAMERA_SWITCH = new SoundPlayer("/sounds/cameraSwitch.wav");
    public static final SoundPlayer CAMERA_STATIC = new SoundPlayer("/sounds/cameraStatic.wav");
    public static final SoundPlayer MONITOR_UP = new SoundPlayer("/sounds/monitorUp.wav");
    public static final SoundPlayer MONITOR_DOWN = new SoundPlayer("/sounds/monitorDown.wav");

    // GAMEPLAY SOUNDS
    public static final SoundPlayer SHOCK = new SoundPlayer("/sounds/shock.wav");
    public static final SoundPlayer CAMERA_REBOOT = new SoundPlayer("/sounds/cameraReboot.wav");
    public static final SoundPlayer CAMERA_BREAK = new SoundPlayer("/sounds/cameraBreak.wav");

    public static final SoundPlayer SIX_AM = new SoundPlayer("/sounds/sixAm.wav");
    public static final SoundPlayer JUMPSCARE = new SoundPlayer("/sounds/jumpscare.wav");

    // ARRAY FOR BULK OPERATIONS
    private static final SoundPlayer[] soundPlayers = new SoundPlayer[]
    {
        AMBIENCE, CAMERA_SWITCH, CAMERA_STATIC, MONITOR_UP, MONITOR_DOWN,
        MUSIC_BOX, MUSIC_BOX_SPED_UP, SHOCK, CAMERA_REBOOT, CAMERA_BREAK, SIX_AM, JUMPSCARE
    };

    public static void stopAll()
    {
        for(SoundPlayer soundPlayer : soundPlayers)
            soundPlayer.stop();
    }

    public static SoundPlayer getPlaying()
    {
        for(SoundPlayer soundPlayer : soundPlayers)
            if(soundPlayer.isPlaying()) return soundPlayer;
        return null;
    }
}