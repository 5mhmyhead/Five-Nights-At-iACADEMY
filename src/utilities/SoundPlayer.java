package utilities;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.util.Objects;

public class SoundPlayer
{
    private MediaPlayer mediaPlayer;
    private boolean isLooping;

    private double masterVolume = 1.0;
    private double localVolume = 1.0;

    public SoundPlayer(String path)
    {
        try
        {
            String uri = Objects.requireNonNull(
                    SoundPlayer.class.getResource(path)).toExternalForm();
            Media media = new Media(uri);
            mediaPlayer = new MediaPlayer(media);
        }
        catch(Exception e)
        {
            System.out.println("Sound not found: " + path);
        }
    }

    public void play()
    {
        if(mediaPlayer == null) return;
        mediaPlayer.stop();
        mediaPlayer.seek(Duration.ZERO);
        mediaPlayer.setCycleCount(1);
        mediaPlayer.play();
        isLooping = false;
    }

    public void loop()
    {
        if(mediaPlayer == null) return;
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
        isLooping = true;
    }

    public void stop()
    {
        if(mediaPlayer == null) return;
        mediaPlayer.stop();
    }

    public void setMasterVolume(double master)
    {
        masterVolume = master;
        applyVolume();
    }

    public void setVolume(double volume)
    {
        // VOLUME IS 0.0 TO 1.0 IN JAVAFX
        localVolume = volume;
        applyVolume();
    }

    private void applyVolume()
    {
        if (mediaPlayer == null) return;
        mediaPlayer.setVolume(localVolume * masterVolume);
    }

    public void mute()
    {
        if(mediaPlayer == null) return;
        mediaPlayer.setMute(true);
    }

    public void unmute()
    {
        if(mediaPlayer == null) return;
        mediaPlayer.setMute(false);
    }
}