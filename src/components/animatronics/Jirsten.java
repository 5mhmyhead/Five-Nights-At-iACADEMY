package components.animatronics;

import components.GameContext;
import components.JumpscarePlayer;
import utilities.FontManager;

import java.awt.*;

// BALLOON BOY, WHEN STARED AT FOR TOO LONG, BREAKS THE CAMERA
// CAMERAS CAN BE REBOOTED, BUT ANIMATRONICS WILL MOVE FASTER
// STARING AT A CAMERA FOR LONGER THAN 10 SECONDS BREAKS A RANDOM CAMERA
// STARTS AT CAMERA 4, CAN'T BREAK HER STARTING CAMERA
public class Jirsten extends Animatronic
{
    private static final int MOVE_INTERVAL = 150; // MOVES EVERY 5 SECONDS
    private static final int STARE_LIMIT = 50;

    private int moveTimer = 0;
    private int stareTimer = 0;
    private int stayTimer = 0;

    private final JumpscarePlayer jumpscare;

    public Jirsten()
    {
        currentCamera = 3;
        location = Location.CAMERA;

        jumpscare = new JumpscarePlayer("/jumpscares/jirsten", 8);
    }

    @Override
    public void update(GameContext ctx)
    {
        if(jumpscare.isPlaying())
        {
            jumpscare.update();

            if(jumpscare.isFinished())
            {
                // BREAK THE CAMERA AFTER JUMPSCARE ENDS
                ctx.cameras.breakCamera(ctx.cameras.getCurrentCamera());
                ctx.cameras.unlockInput(); // UNLOCK AFTER CAMERA BREAKS

                currentCamera = pickRandomCamera(ctx.cameras.getCurrentCamera());
                stareTimer = 0;
                moveTimer = 0;
            }
            return; // BLOCK ALL OTHER UPDATES WHILE JUMPSCARE PLAYS
        }

        handleMovement(ctx);
        handleStare(ctx);
        handleStay(ctx);
    }

    private void handleMovement(GameContext ctx)
    {
        int watchedCamera = ctx.cameras.getCurrentCamera();
        boolean playerWatching = ctx.cameras.isMonitorUp() && watchedCamera == currentCamera;

        // PAUSE MOVE TIMER WHILE PLAYER IS WATCHING JIRSTEN
        if (!playerWatching) {
            moveTimer++;
            if (moveTimer >= MOVE_INTERVAL) {
                moveTimer = 0;
                stareTimer = 0;

                if (shouldMove()) {
                    currentCamera = pickRandomCamera(watchedCamera);
                    System.out.println("JIRSTEN MOVED TO CAM " + (currentCamera + 1));
                } else {
                    System.out.println("JIRSTEN FAILED MOVEMENT OPPORTUNITY");
                }
            }
        }
    }

    private void handleStay(GameContext ctx)
    {
        if(ctx.cameras.wasCameraSwitched())
        {
            stayTimer = 0;
            return;
        }

        if(ctx.cameras.isMonitorUp())
        {
            stayTimer++;

            if(stayTimer >= getStayLimit())
            {
                stayTimer = 0;
                // ONLY JUMPSCARE IF CAMERA IS VIEWABLE
                if(ctx.cameras.isCameraViewable(currentCamera))
                {
                    jumpscare.play();
                    ctx.cameras.lockInput();
                }
                else
                {
                    ctx.cameras.breakCamera(ctx.cameras.getCurrentCamera());
                }
            }
        }
    }

    private void handleStare(GameContext ctx)
    {
        int watchedCamera = ctx.cameras.getCurrentCamera();
        boolean playerWatching = ctx.cameras.isMonitorUp() && (watchedCamera == currentCamera);

        if(playerWatching)
        {
            stareTimer++;

            // BREAK CAMERA IF STARED AT TOO LONG (NOT ON CAM 4)
            if(stareTimer >= STARE_LIMIT && currentCamera != 3)
            {
                stareTimer = 0;
                // ONLY JUMPSCARE IF CAMERA IS VIEWABLE
                if(ctx.cameras.isCameraViewable(currentCamera))
                {
                    jumpscare.play();
                    ctx.cameras.lockInput();
                }
            }
        }
        else
        {
            // SEND JIRSTEN BACK TO CAM 4 IF PLAYER LOOKED AWAY IN TIME
            if(stareTimer > 0 && currentCamera != 3)
            {
                currentCamera = 3;
                moveTimer = 0;
            }

            stareTimer = 0;
        }
    }

    private int pickRandomCamera(int watchedCamera)
    {
        int next;
        do {
            next = (int)(Math.random() * 7);
        } while(next == currentCamera || next == watchedCamera);

        return next;
    }

    @Override
    public void drawOnCamera(Graphics2D g2, int swayX)
    {
        g2.setColor(new Color(180, 100, 100));
        g2.fillRect(30, 80, 30, 30);

        g2.setFont(FontManager.LCD_SMALL);
        g2.drawString("JIRSTEN", 75, 100);
    }

    @Override
    public boolean jumpscareIsPlaying() { return jumpscare.isPlaying(); }

    @Override
    public void drawJumpscare(Graphics2D g2) { jumpscare.draw(g2); }

    // CALCULATES THE STAY LIMIT DEPENDING ON THE AI LEVEL OF JIRSTEN
    // 15 SECONDS - 6 SECONDS RANGE
    private int getStayLimit()
    {
        int maxLimit = 450;
        int minLimit = 180;
        return maxLimit - (int)((maxLimit - minLimit) * (aiLevel - 1) / 19.0);
    }
}
