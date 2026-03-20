package components;

import main.GamePanel;
import utilities.SoundManager;
import utilities.Utility;

import java.awt.*;
import java.awt.image.BufferedImage;

public class JumpscarePlayer
{
    private final BufferedImage[] frames;
    private int currentFrame = 0;
    private boolean playing = false;
    private boolean finished = false;

    // FRAMES PER IMAGE
    private static final int FRAMES_PER_IMAGE = 3;
    private int frameHoldTimer = 0;

    public JumpscarePlayer(String folderPath, int frameCount)
    {
        frames = new BufferedImage[frameCount];
        for(int i = 0; i < frameCount; i++)
            frames[i] = Utility.loadImage(folderPath + "/frame" + (i + 1) + ".png");
    }

    public void play()
    {
        // PLAYS THE JUMPSCARE SOUND
        SoundManager.JUMPSCARE.setVolume(0.3);
        SoundManager.JUMPSCARE.play();

        currentFrame = 0;
        frameHoldTimer = 0;
        playing = true;
        finished = false;
    }

    public void update()
    {
        if(!playing) return;

        frameHoldTimer++;
        if(frameHoldTimer >= FRAMES_PER_IMAGE)
        {
            frameHoldTimer = 0;
            currentFrame++;

            if(currentFrame >= frames.length)
            {
                playing = false;
                finished = true;
            }
        }
    }

    public void draw(Graphics2D g2)
    {
        if(!playing) return;

        BufferedImage frame = frames[currentFrame];
        if(frame != null)
            g2.drawImage(frame, 0, 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
    }

    public boolean isPlaying()  { return playing; }
    public boolean isFinished() { return finished; }
}
