package components.animatronics;

import components.GameContext;
import utilities.FontManager;

import java.awt.*;

// BALLOON BOY, WHEN STARED AT FOR TOO LONG, BREAKS THE CAMERA
// CAMERAS CAN BE REBOOTED, BUT ANIMATRONICS WILL MOVE FASTER
// STARTS AT CAMERA 4, CAN'T BREAK HER STARTING CAMERA
public class Jirsten extends Animatronic
{
    private static final int MOVE_INTERVAL = 150; // MOVES EVERY 5 SECONDS
    private static final int STARE_LIMIT = 50;

    private int moveTimer  = 0;
    private int stareTimer = 0;

    public Jirsten()
    {
        currentCamera = 3;
        location = Location.CAMERA;
    }

    @Override
    public void update(GameContext ctx)
    {
        handleMovement(ctx);
        handleStare(ctx);
    }

    private void handleMovement(GameContext ctx)
    {
        int watchedCamera = ctx.cameras.getCurrentCamera();
        boolean playerWatching = ctx.cameras.isMonitorUp() && watchedCamera == currentCamera;

        // PAUSE MOVE TIMER WHILE PLAYER IS WATCHING JIRSTEN
        if(!playerWatching)
        {
            moveTimer++;
            if(moveTimer >= MOVE_INTERVAL)
            {
                moveTimer  = 0;
                stareTimer = 0;

                if(shouldMove())
                {
                    currentCamera = pickRandomCamera(watchedCamera);
                    System.out.println("JIRSTEN MOVED TO CAM " + (currentCamera + 1));
                }
                else
                {
                    System.out.println("JIRSTEN FAILED MOVEMENT OPPORTUNITY");
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
                ctx.cameras.breakCamera(currentCamera);
                stareTimer = 0;
                currentCamera = pickRandomCamera(watchedCamera);
                moveTimer = 0;
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
}
