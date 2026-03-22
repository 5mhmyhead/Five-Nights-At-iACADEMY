package components.animatronics;

import components.GameContext;
import components.JumpscarePlayer;
import components.cameras.CameraSystem;
import main.GamePanel;
import utilities.FontManager;
import utilities.Utility;

import java.awt.*;
import java.awt.image.BufferedImage;

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

    // SPRITES
    private final JumpscarePlayer jumpscare;
    private final JumpscarePlayer warningJumpscare;    // JUMPSCARE WHEN PLAYER STAYS IN CAM FOR TOO LONG
    private float warningAlpha = 0f;

    private final BufferedImage warningImage;
    private final BufferedImage[] cameraSprites;

    public Jirsten()
    {
        currentCamera = 3;
        location = Location.CAMERA;

        // LOAD SPRITES
        jumpscare = new JumpscarePlayer("/jumpscares/jirsten", 8);
        warningJumpscare = new JumpscarePlayer("/jumpscares/jirsten/warning", 4);

        warningImage = Utility.loadImage("/animatronics/jirsten/warning.png");
        cameraSprites = new BufferedImage[7];
        for(int i = 0; i < 7; i++)
            cameraSprites[i] = Utility.loadImage("/animatronics/jirsten/camera" + (i + 1) + ".png");
    }

    @Override
    public void update(GameContext ctx)
    {
        if(handleJumpscare(warningJumpscare, ctx)) return;
        if(handleJumpscare(jumpscare, ctx)) return;

        handleMovement(ctx);
        handleStare(ctx);
        handleStay(ctx);
    }

    private boolean handleJumpscare(JumpscarePlayer jp, GameContext ctx)
    {
        if(!jp.isPlaying())
            return false;

        jp.update();

        if(jp.isFinished())
            handleJumpscareFinished(ctx);

        return true;
    }

    private void handleJumpscareFinished(GameContext ctx)
    {
        ctx.cameras.breakCamera(ctx.cameras.getCurrentCamera());
        ctx.cameras.unlockInput();
        currentCamera = pickRandomCamera(ctx.cameras.getCurrentCamera());
        stareTimer = 0;
        moveTimer = 0;
        warningAlpha = 0f;
    }

    private void handleMovement(GameContext ctx)
    {
        int watchedCamera = ctx.cameras.getCurrentCamera();
        boolean playerWatching = ctx.cameras.isMonitorUp() && watchedCamera == currentCamera;

        // PAUSE MOVE TIMER WHILE PLAYER IS WATCHING JIRSTEN
        if (!playerWatching)
        {
            moveTimer++;
            if (moveTimer >= MOVE_INTERVAL)
            {
                moveTimer = 0;
                stareTimer = 0;

                if (shouldMove()) currentCamera = pickRandomCamera(watchedCamera);
            }
        }
    }

    private void handleStay(GameContext ctx)
    {
        if(ctx.cameras.wasCameraSwitched())
        {
            stayTimer = 0;
            warningAlpha = 0f;
            return;
        }

        if(ctx.cameras.isMonitorUp()
            && ctx.cameras.isCameraViewable(ctx.cameras.getCurrentCamera())
            && ctx.cameras.getCurrentCamera() != 3)
        {
            stayTimer++;

            // TWO SECONDS BEFORE JIRSTEN STARTS FADING IN
            int fadeTimer = stayTimer - 60;
            int fadeLimit = getStayLimit() - 60;

            if(fadeTimer > 0)
                warningAlpha = Math.min(1f, (float) fadeTimer / fadeLimit);
            else
                warningAlpha = 0f;

            if(stayTimer >= getStayLimit())
            {
                stayTimer = 0;
                warningAlpha = 0f;
                if(ctx.cameras.isCameraViewable(currentCamera))
                {
                    warningJumpscare.play();
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

            // BREAK CAMERA IF STARED AT TOO LONG
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

        do
        {
            next = (int)(Math.random() * 7);
        }
        while(next == currentCamera || next == watchedCamera);

        return next;
    }

    // DRAW FADING IN VERSION OF JIRSTEN WHEN STARING AT THE CAMERA FOR TOO LONG
    public void drawWarning(Graphics2D g2, CameraSystem cameras)
    {
        if(warningAlpha <= 0 || warningImage == null) return;
        if(!cameras.isCameraViewable(cameras.getCurrentCamera())) return;

        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, warningAlpha);
        Composite oldComposite = g2.getComposite();
        g2.setComposite(ac);
        g2.drawImage(warningImage, 0, 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
        g2.setComposite(oldComposite);
    }

    @Override
    public void drawOnCamera(Graphics2D g2, int swayX)
    {
        if(currentCamera >= 0
                && currentCamera < cameraSprites.length
                && cameraSprites[currentCamera] != null)
        {
            g2.drawImage(cameraSprites[currentCamera], swayX, 0,
                    GamePanel.WIDTH, GamePanel.HEIGHT, null);
        }
        else
        {
            g2.setColor(new Color(180, 100, 100));
            g2.fillRect(30, 80, 30, 30);

            g2.setFont(FontManager.LCD_SMALL);
            g2.drawString("JIRSTEN", 75, 100);
        }
    }

    @Override
    public boolean jumpscareIsPlaying()
    {
        return jumpscare.isPlaying() || warningJumpscare.isPlaying();
    }

    @Override
    public void drawJumpscare(Graphics2D g2)
    {
        jumpscare.draw(g2);
        warningJumpscare.draw(g2);
    }

    // CALCULATES THE STAY LIMIT DEPENDING ON THE AI LEVEL OF JIRSTEN
    // 15 SECONDS - 6 SECONDS RANGE
    private int getStayLimit()
    {
        int maxLimit = 450;
        int minLimit = 180;
        return maxLimit - (int)((maxLimit - minLimit) * (aiLevel - 1) / 19.0);
    }
}
