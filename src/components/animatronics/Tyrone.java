package components.animatronics;

import components.GameContext;
import components.JumpscarePlayer;
import main.GamePanel;
import state.StateManager;
import utilities.FontManager;
import utilities.SoundManager;
import utilities.Utility;

import java.awt.*;
import java.awt.image.BufferedImage;

// LIKE CHIKA, MOVES IN THE CAMERAS ON THE RIGHT OF THE PLAYER
// WHEN THEY ARE AT THE OFFICE, GO TO THE CORRESPONDING VIEW AND BLINK
// TYRONE MOVES FASTER THAN EARL, BUT VISITS MORE CAMERAS
// PATH: CAMERA 4 -> 1 -> 2 -> 3 -> 5
public class Tyrone extends Animatronic
{
    public enum TyroneState { MOVING, BOOST, MAIN }
    private TyroneState state = TyroneState.MOVING;

    private static final int[] PATH = { 3, 0, 1, 2, 4 };
    private int pathIndex = 0;

    private int moveTimer = 0;
    private int doorTimer = 0;

    private static final int MOVE_INTERVAL = 180; // 6 SECONDS
    private static final int DOOR_COUNTDOWN = 150; // 5 SECONDS

    // PLAYER HAS TO HOLD FOR AT LEAST 1 SECOND BEFORE TYRONE GOES AWAY
    private static final int BLINK_HOLD_REQUIRED = 30;
    private int blinkHoldTimer = 0;

    // PLAYER CAN STARE FOR AT LEAST 2 SECONDS TO REMOVE MUSIC BOX BOOST
    private static final int STARE_CANCEL_REQUIRED = 60;
    private int stareCancelTimer = 0;
    private int boostFrames = 0;

    // SPRITES
    private final JumpscarePlayer jumpscare;
    private final BufferedImage mainImage;

    private final BufferedImage camera1;
    private final BufferedImage camera2;
    private final BufferedImage camera3;
    private final BufferedImage camera4;
    private final BufferedImage camera5;

    public Tyrone()
    {
        currentCamera = 3;
        location = Location.CAMERA;

        // LOAD SPRITES
        jumpscare = new JumpscarePlayer("/jumpscares/tyrone", 7);
        mainImage = Utility.loadImage("/animatronics/tyrone/main.png");

        camera1 = Utility.loadImage("/animatronics/tyrone/camera1.png");
        camera2 = Utility.loadImage("/animatronics/tyrone/camera2.png");
        camera3 = Utility.loadImage("/animatronics/tyrone/camera3.png");
        camera4 = Utility.loadImage("/animatronics/tyrone/camera4.png");
        camera5 = Utility.loadImage("/animatronics/tyrone/camera5.png");
    }

    @Override
    public void update(GameContext ctx)
    {
        // IF JUMPSCARE IS PLAYING, ONLY UPDATE IT
        if(jumpscare.isPlaying())
        {
            jumpscare.update();
            if(jumpscare.isFinished())
            {
                ctx.stateManager.setKiller("Tyrone");
                ctx.stateManager.setState(StateManager.LOSE_STATE);
            }
            return;
        }

        switch(state)
        {
            case MOVING, BOOST -> handleMoving(ctx);
            case MAIN -> handleMain(ctx);
        }
    }

    public void applyMusicBoxBoost(int frames)
    {
        if(state == TyroneState.MOVING || state == TyroneState.BOOST)
        {
            state = TyroneState.BOOST;
            boostFrames = frames;
        }
    }

    private void handleMoving(GameContext ctx)
    {
        boolean playerWatching = ctx.cameras.isMonitorUp()
                && ctx.cameras.getCurrentCamera() == currentCamera
                && ctx.cameras.isCameraViewable(currentCamera);

        // IF THE PLAYER IS WATCHING FOR AT LEAST 2 SECONDS
        if(playerWatching && state == TyroneState.BOOST)
        {
            stareCancelTimer++;
            // THE MUSIC BOX BOOST IS CANCELLED
            if(stareCancelTimer >= STARE_CANCEL_REQUIRED)
            {
                stareCancelTimer = 0;
                state = TyroneState.MOVING;
                boostFrames = 0;
            }
        }
        else
        {
            stareCancelTimer = 0;
        }

        // TICK DOWN BOOST FRAMES
        if(state == TyroneState.BOOST)
        {
            boostFrames--;
            if(boostFrames <= 0)
                state = TyroneState.MOVING;
        }

        // ADD MOVE TICK IF MUSIC BOX WAS WOUND
        // ADD ANOTHER MOVE TICK IF CAMERA IS BROKEN, REBOOTING, OR UNRESPONSIVE
        moveTimer++;
        if(state == TyroneState.BOOST)moveTimer++;
        if(!ctx.cameras.isCameraViewable(currentCamera)) moveTimer++;

        if(moveTimer >= MOVE_INTERVAL)
        {
            moveTimer = 0;
            if(shouldMove() && !playerWatching) advancePath();
        }
    }

    private void advancePath()
    {
        pathIndex++;

        if(pathIndex >= PATH.length)
        {
            // WHEN REACHING CAM 5, MOVE TO MAIN
            location = Location.MAIN;
            state = TyroneState.MAIN;
            doorTimer = 0;
            SoundManager.FOOTSTEPS.play();
            SoundManager.FOOTSTEPS.setVolume(0.2);
        }
        else
        {
            currentCamera = PATH[pathIndex];
        }
    }

    private void handleMain(GameContext ctx)
    {
        boolean playerDefending = !ctx.office.isPlayerAtDoor()
                && ctx.blink.areEyesClosed();

        doorTimer++;

        if(playerDefending)
        {
            blinkHoldTimer++;
            if(blinkHoldTimer >= BLINK_HOLD_REQUIRED)
            {
                blinkHoldTimer = 0;
                reset();
            }
        }
        else
        {
            blinkHoldTimer = 0; // RESET IF PLAYER STOPS BLINKING
            if(doorTimer >= DOOR_COUNTDOWN)
            {
                if(ctx.blink.areEyesClosed()) return;
                // TRIGGER CAMERA OR BLINK TRANSITION FIRST
                if(!ctx.cameras.isTransitioning() && ctx.cameras.isMonitorUp())
                    ctx.cameras.forceMonitorDown();

                if(ctx.blink.areEyesClosed())
                    ctx.blink.forceOpen();

                // WAIT FOR TRANSITIONS TO FINISH BEFORE JUMPSCARE
                if(!ctx.cameras.isMonitorUp() && !ctx.cameras.isTransitioning() && !ctx.blink.areEyesClosed())
                    jumpscare.play();
            }
        }
    }

    private void reset()
    {
        currentCamera = 3;
        location = Location.CAMERA;
        state = TyroneState.MOVING;
        boostFrames = 0;
        pathIndex = 0;
        moveTimer = 0;
        doorTimer = 0;
    }

    @Override
    public void drawOnCamera(Graphics2D g2, int swayX)
    {
        BufferedImage sprite = getSpriteForCamera();

        if(sprite != null)
            g2.drawImage(sprite, swayX - 16, 0, GamePanel.WIDTH + 16 * 2, GamePanel.HEIGHT, null);
        else
        {
            g2.setColor(new Color(196, 180, 74));
            g2.fillRect(200, 30, 30, 30);

            g2.setFont(FontManager.LCD_SMALL);
            g2.drawString("TYRONE", 245, 50);
        }
    }

    @Override
    public void drawOnOffice(Graphics2D g2)
    {
        if(!jumpscareIsPlaying())
        {
            if(mainImage != null)
                g2.drawImage(mainImage, 0, 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
            else
                drawPlaceholder(g2);
        }
    }

    private BufferedImage getSpriteForCamera()
    {
        return switch(currentCamera)
        {
            case 0 -> camera1;
            case 1 -> camera2;
            case 2 -> camera3;
            case 3 -> camera4;
            case 4 -> camera5;
            default -> null;
        };
    }

    private void drawPlaceholder(Graphics2D g2)
    {
        g2.setColor(new Color(191, 150, 48, 180));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_LARGE);
        Utility.drawCentered(g2, "TYRONE IS HERE", GamePanel.HEIGHT / 2 - 20);

        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "BLINK! " + (doorTimer / 30 + 1) + "s", GamePanel.HEIGHT / 2 + 20);
    }

    @Override
    public void drawJumpscare(Graphics2D g2)
    {
        jumpscare.draw(g2);
    }

    @Override
    public boolean jumpscareIsPlaying()
    {
        return jumpscare.isPlaying();
    }
}
