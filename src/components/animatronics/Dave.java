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

// BIG BAD FREDDY, MOVES SLOWLY AND FREEZES WHEN STARED AT
// CAN ONLY BE SHOCKED 3 TIMES TO MAKE HIM GO BACK TO HIS STARTING CAMERA
public class Dave extends Animatronic
{
    private enum DaveState { GRACE, MOVING, DOOR }
    private DaveState state = DaveState.GRACE;

    private static final int MAX_JUMPS = 5; // MOVES 5 CAMERAS BEFORE GOING TO THE DOOR
    private int moveCount = 0;

    private static final int GRACE_DURATION = 300;
    private static final int MOVE_INTERVAL = 270;
    private static final int DOOR_COUNTDOWN = 120;

    private int graceTimer = 0;
    private int moveTimer = 0;
    private int doorTimer = 0;

    // DAVE CANNOT MOVE FOR 20 FRAMES AFTER PLAYER LEAVES THE CAMERA
    private static final int WATCH_PAUSE_DURATION = 20;
    private int watchPauseTimer = 0;
    private boolean wasWatched  = false;

    // SPRITES
    // DAVE TAKES HIS TIME PLAYING HIS JUMPSCARE ANIMATION
    private final JumpscarePlayer jumpscare;
    private int jumpscareDelay;
    private boolean delayStarted = false;

    private final BufferedImage[] cameraSprites;
    private final BufferedImage doorImage;

    public Dave()
    {
        currentCamera = 6;
        location = Location.CAMERA;

        // ROLLS A RANDOM NUMBER TO MATCH JUMPSCARE DELAY
        jumpscareDelay = (int)(Math.random() * 10 + 1) * GamePanel.FPS;

        // LOAD SPRITES
        jumpscare = new JumpscarePlayer("/jumpscares/dave", 7);
        doorImage = Utility.loadImage("/animatronics/dave/door.png");

        cameraSprites = new BufferedImage[7];
        for(int i = 0; i < 7; i++)
            cameraSprites[i] = Utility.loadImage("/animatronics/dave/camera" + (i + 1) + ".png");
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
                ctx.stateManager.setKiller("Dave");
                ctx.stateManager.setState(StateManager.LOSE_STATE);
            }
            return;
        }

        // IF DAVE REACHES THE DOOR
        // JUMPSCARE WITH A DELAY
        if(delayStarted)
        {
            jumpscareDelay--;
            if(jumpscareDelay <= 0)
            {
                // TRIGGER CAMERA OR BLINK TRANSITION FIRST
                if(!ctx.cameras.isTransitioning() && ctx.cameras.isMonitorUp())
                    ctx.cameras.forceMonitorDown();

                if(ctx.blink.areEyesClosed())
                    ctx.blink.forceOpen();

                // WAIT FOR TRANSITIONS TO FINISH BEFORE JUMPSCARE
                if(!ctx.cameras.isMonitorUp() && !ctx.cameras.isTransitioning() && !ctx.blink.areEyesClosed())
                    jumpscare.play();
            }

            return;
        }

        handleShock(ctx);

        switch(state)
        {
            case GRACE -> handleGrace();
            case MOVING -> handleMoving(ctx);
            case DOOR -> handleDoor();
        }
    }

    private void handleGrace()
    {
        graceTimer++;
        if(graceTimer >= GRACE_DURATION)
            state = DaveState.MOVING;
    }

    private void handleDoor()
    {
        doorTimer--;
        if(doorTimer <= 0)
            delayStarted = true;
    }

    private void handleShock(GameContext ctx)
    {
        if(ctx.wasShockPressed()) reset();
    }

    private void handleMoving(GameContext ctx)
    {
        int watchedCamera = ctx.cameras.getCurrentCamera();
        boolean playerWatching = ctx.cameras.isMonitorUp()
                && watchedCamera == currentCamera
                && ctx.cameras.isCameraViewable(currentCamera);

        // WHEN PLAYER STOPS WATCHING, START PAUSE TIMER
        if(wasWatched && !playerWatching)
            watchPauseTimer = WATCH_PAUSE_DURATION;

        wasWatched = playerWatching;

        // PAUSE MOVE TIMER WHILE BEING WATCHED OR 1 SECOND AFTER WATCHING
        if(playerWatching || watchPauseTimer > 0)
        {
            if(watchPauseTimer > 0) watchPauseTimer--;
            return;
        }

        moveTimer++;
        if(!ctx.cameras.isCameraViewable(currentCamera)) moveTimer++;

        if(moveTimer >= MOVE_INTERVAL)
        {
            moveTimer = 0;
            if(shouldMove()) advancePath(ctx);
        }
    }

    private void advancePath(GameContext ctx)
    {
        moveCount++;
        if(moveCount >= MAX_JUMPS)
        {
            location = Location.DOOR;
            state = DaveState.DOOR;
            doorTimer = DOOR_COUNTDOWN;
            SoundManager.KNOCK_DAVE.play();
            SoundManager.KNOCK_DAVE.setVolume(0.1);
            return;
        }

        int watchedCamera = ctx.cameras.getCurrentCamera();
        int next;

        do
        {
            next = (int)(Math.random() * 7);
        }
        while(next == currentCamera || next == watchedCamera);

        // SOUND CUES FOR DAVE MOVING
        if (next < currentCamera)
        {
            SoundManager.DAVE_LEFT_TO_RIGHT.setVolume(0.2);
            SoundManager.DAVE_LEFT_TO_RIGHT.play();
        }
        else
        {
            SoundManager.DAVE_RIGHT_TO_LEFT.setVolume(0.2);
            SoundManager.DAVE_RIGHT_TO_LEFT.play();
        }

        currentCamera = next;
    }

    // WHEN SHOCKED, DAVE RESETS BACK TO CAM 6
    private void reset()
    {
        currentCamera = 6;
        location = Location.CAMERA;
        state = DaveState.MOVING;
        moveTimer = 0;
        doorTimer = 0;
        moveCount = 0;
        watchPauseTimer = 0;
        wasWatched = false;
    }

    @Override
    public void drawOnCamera(Graphics2D g2, int swayX)
    {
        if(currentCamera >= 0
            && currentCamera < cameraSprites.length
            && cameraSprites[currentCamera] != null)
        {
            g2.drawImage(cameraSprites[currentCamera], swayX  - 16, 0,
                GamePanel.WIDTH  + 16 * 2, GamePanel.HEIGHT, null);
        }
        else
        {
            g2.setColor(new Color(91, 139, 241));
            g2.fillRect(200, 80, 30, 30);

            g2.setFont(FontManager.LCD_SMALL);
            g2.drawString("DAVE", 245, 100);
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

    public void drawPlaceholder(Graphics2D g2)
    {
        g2.setColor(new Color(38, 135, 255, 180));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_LARGE);
        Utility.drawCentered(g2, "DAVE IS AT THE MAIN", GamePanel.HEIGHT / 2 - 20);

        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "SHOCK HIM! " + (doorTimer / 30 + 1) + "s", GamePanel.HEIGHT / 2 + 20);
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
