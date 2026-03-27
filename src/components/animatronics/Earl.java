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

// LIKE BONNIE, MOVES IN THE CAMERAS ON THE LEFT OF THE PLAYER
// WHEN THEY ARE AT THE OFFICE, GO TO THE CORRESPONDING VIEW AND BLINK
// EARL MOVES SLOWER THAN TYRONE, BUT VISITS LESS CAMERAS
// PATH: CAMERA 7 -> 6 -> 5
public class Earl extends Animatronic
{
    public enum EarlState { MOVING, BOOST, DOOR }
    private EarlState state = EarlState.MOVING;

    private static final int[] PATH = { 6, 5, 4 };
    private int pathIndex = 0;

    private int moveTimer = 0;
    private int doorTimer = 0;

    private static final int MOVE_INTERVAL = 450; // 15 SECONDS
    private static final int DOOR_COUNTDOWN = 150; // 5 SECONDS

    // PLAYER HAS TO HOLD FOR AT LEAST 1 SECOND BEFORE EARL GOES AWAY
    private static final int BLINK_HOLD_REQUIRED = 30;
    private int blinkHoldTimer = 0;

    // PLAYER CAN STARE FOR AT LEAST 2 SECONDS TO REMOVE MUSIC BOX BOOST
    private static final int STARE_CANCEL_REQUIRED = 60;
    private int stareCancelTimer = 0;
    private int boostFrames = 0;

    // SPRITES
    private final JumpscarePlayer jumpscare;
    private final BufferedImage doorImage;

    private final BufferedImage camera5;
    private final BufferedImage camera6;
    private final BufferedImage camera7;

    public Earl()
    {
        currentCamera = 6;
        location = Location.CAMERA;

        // LOAD SPRITES
        jumpscare = new JumpscarePlayer("/jumpscares/earl", 7);
        doorImage = Utility.loadImage("/animatronics/earl/door.png");
        camera5 = Utility.loadImage("/animatronics/earl/camera5.png");
        camera6 = Utility.loadImage("/animatronics/earl/camera6.png");
        camera7 = Utility.loadImage("/animatronics/earl/camera7.png");
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
                ctx.stateManager.setKiller("Earl");
                ctx.stateManager.setState(StateManager.LOSE_STATE);
            }
            return;
        }

        switch(state)
        {
            case MOVING, BOOST -> handleMoving(ctx);
            case DOOR -> handleDoor(ctx);
        }
    }

    public void applyMusicBoxBoost(int frames)
    {
        if(state == EarlState.MOVING || state == EarlState.BOOST)
        {
            state = EarlState.BOOST;
            boostFrames = frames;
        }
    }

    private void handleMoving(GameContext ctx)
    {
        boolean playerWatching = ctx.cameras.isMonitorUp()
                && ctx.cameras.getCurrentCamera() == currentCamera
                && ctx.cameras.isCameraViewable(currentCamera);

        // IF THE PLAYER IS WATCHING FOR AT LEAST 2 SECONDS
        if(playerWatching && state == EarlState.BOOST)
        {
            stareCancelTimer++;
            // THE MUSIC BOX BOOST IS CANCELLED
            if(stareCancelTimer >= STARE_CANCEL_REQUIRED)
            {
                stareCancelTimer = 0;
                state = EarlState.MOVING;
                boostFrames = 0;
            }
        }
        else
        {
            stareCancelTimer = 0;
        }

        // TICK DOWN BOOST FRAMES
        if(state == EarlState.BOOST)
        {
            boostFrames--;
            if(boostFrames <= 0)
                state = EarlState.MOVING;
        }

        // ADD MOVE TICK IF MUSIC BOX WAS WOUND
        // ADD ANOTHER MOVE TICK IF CAMERA IS BROKEN, REBOOTING, OR UNRESPONSIVE
        moveTimer++;
        if(state == EarlState.BOOST)moveTimer++;
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
            // WHEN REACHING CAM 5, MOVE TO DOOR
            location = Location.DOOR;
            state = EarlState.DOOR;
            doorTimer = 0;
            SoundManager.KNOCK_EARL.play();
            SoundManager.KNOCK_EARL.setVolume(0.1);
        }
        else
        {
            currentCamera = PATH[pathIndex];
        }
    }

    private void handleDoor(GameContext ctx)
    {
        boolean playerDefending = ctx.office.isPlayerAtDoor()
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
        currentCamera = 6;
        location = Location.CAMERA;
        state = EarlState.MOVING;
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
            g2.setColor(new Color(74, 196, 87));
            g2.fillRect(30, 30, 30, 30);

            g2.setFont(FontManager.LCD_SMALL);
            g2.drawString("EARL", 75, 50);
        }
    }

    @Override
    public void drawOnDoor(Graphics2D g2)
    {
        if(!jumpscareIsPlaying())
        {
            if(doorImage != null)
                g2.drawImage(doorImage, 0, 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
            else
                drawPlaceholder(g2);
        }
    }

    private BufferedImage getSpriteForCamera()
    {
        return switch(currentCamera)
        {
            case 4 -> camera5;
            case 5 -> camera6;
            case 6 -> camera7;
            default -> null;
        };
    }

    public void drawPlaceholder(Graphics2D g2)
    {
        g2.setColor(new Color(67, 191, 48, 180));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_LARGE);
        Utility.drawCentered(g2, "EARL IS AT THE DOOR", GamePanel.HEIGHT / 2 - 20);

        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "BLINK! " + (doorTimer / 30 + 1) + "s", GamePanel.HEIGHT / 2 + 20);
    }

    @Override
    public void drawJumpscare(Graphics2D g2) { jumpscare.draw(g2); }

    @Override
    public boolean jumpscareIsPlaying()
    {
        return jumpscare.isPlaying();
    }
}
