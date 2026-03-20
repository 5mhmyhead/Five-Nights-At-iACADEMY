package utilities;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.util.Objects;

public class SoundPlayer
{
    private final Object synchronizationLock = new Object();
    private Clip clip;
    private boolean isLooping;

    public SoundPlayer(String path)
    {
        try
        {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(SoundPlayer.class.getResource(path)));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        }
        catch(Exception e)
        {
            System.out.println("Sound not found: " + path);
        }
    }

    public void play()
    {
        try
        {
            if(clip != null)
            {
                new Thread(() ->
                {
                    synchronized(synchronizationLock)
                    {
                        clip.stop();
                        clip.setFramePosition(0);
                        clip.start();
                    }
                }).start();
                isLooping = false;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void stop()
    {
        if(clip == null) return;
        clip.stop();
    }

    public void loop()
    {
        try
        {
            if(clip != null)
            {
                new Thread(() ->
                {
                    synchronized(synchronizationLock)
                    {
                        clip.stop();
                        clip.setFramePosition(0);
                        clip.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                }).start();
                isLooping = true;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setVolume(int relativeVolume)
    {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(relativeVolume);
    }

    public boolean isPlaying()  { return clip != null && clip.isActive(); }
    public boolean isFinished() { return clip != null && clip.getMicrosecondPosition() == clip.getMicrosecondLength(); }
    public boolean isLooping()  { return isLooping; }
}
