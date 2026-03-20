package utilities;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.util.Objects;

public class SoundPlayer
{
    private MediaPlayer mediaPlayer;
    private boolean isLooping;

    public SoundPlayer(String path)
    {
        try
        {
            String uri = Objects.requireNonNull(
                    SoundPlayer.class.getResource(path)).toExternalForm();
            Media media = new Media(uri);
            mediaPlayer = new MediaPlayer(media);
            System.out.println("Sound loaded: " + path);
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

    public void pause()
    {
        if(mediaPlayer == null) return;
        mediaPlayer.pause();
    }

    public void resume()
    {
        if(mediaPlayer == null) return;
        mediaPlayer.play();
    }

    public void setVolume(double volume)
    {
        // VOLUME IS 0.0 TO 1.0 IN JAVAFX
        if(mediaPlayer == null) return;
        mediaPlayer.setVolume(volume);
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

    public boolean isPlaying()
    {
        return mediaPlayer != null &&
                mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public boolean isFinished()
    {
        return mediaPlayer != null &&
                mediaPlayer.getCurrentTime().equals(mediaPlayer.getTotalDuration());
    }

    public boolean isLooping() { return isLooping; }
}