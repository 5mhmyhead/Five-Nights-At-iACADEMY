package components.animatronics;

import components.GameContext;
import main.GamePanel;
import state.StateManager;
import utilities.FontManager;
import utilities.Utility;

import java.awt.*;

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

    private static final int MOVE_INTERVAL = 300; // 10 SECONDS
    private static final int DOOR_COUNTDOWN = 150; // 5 SECONDS

    // PLAYER HAS TO HOLD FOR AT LEAST 1 SECOND BEFORE EARL GOES AWAY
    private static final int BLINK_HOLD_REQUIRED = 30;
    private int blinkHoldTimer = 0;

    // PLAYER CAN STARE FOR AT LEAST 2 SECONDS TO REMOVE MUSIC BOX BOOST
    private static final int STARE_CANCEL_REQUIRED = 60;
    private int stareCancelTimer = 0;
    private int boostFrames = 0;

    public Earl()
    {
        currentCamera = 6;
        location = Location.CAMERA;
    }

    @Override
    public void update(GameContext ctx)
    {
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

        String speedMode = "NORMAL";
        if(state == EarlState.BOOST && !ctx.cameras.isCameraViewable(currentCamera))
            speedMode = "BOOSTED + BROKEN";
        else if(state == EarlState.BOOST)
            speedMode = "BOOSTED";
        else if(!ctx.cameras.isCameraViewable(currentCamera))
            speedMode = "BROKEN";

        System.out.println(getClass().getSimpleName() + " speed: " + speedMode +
                " | moveTimer: " + moveTimer + "/" + MOVE_INTERVAL);

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
            doorTimer++;
            if(doorTimer >= DOOR_COUNTDOWN)
                ctx.stateManager.setState(StateManager.LOSE_STATE);
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
        g2.setColor(new Color(74, 196, 87));
        g2.fillRect(30, 30, 30, 30);

        g2.setFont(FontManager.LCD_SMALL);
        g2.drawString("EARL", 75, 50);
    }

    @Override
    public void drawOnDoor(Graphics2D g2)
    {
        g2.setColor(new Color(67, 191, 48, 180));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_LARGE);
        Utility.drawCentered(g2, "EARL IS AT THE DOOR", GamePanel.HEIGHT / 2 - 20);

        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "BLINK! " + (doorTimer / 30 + 1) + "s", GamePanel.HEIGHT / 2 + 20);
    }
}
