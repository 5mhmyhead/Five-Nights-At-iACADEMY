package components.animatronics;

import components.GameContext;
import main.GamePanel;
import state.StateManager;
import utilities.FontManager;
import utilities.Utility;

import java.awt.*;

// BIG BAD FREDDY, MOVES SLOWLY AND FREEZES WHEN STARED AT
// CAN ONLY BE SHOCKED 3 TIMES TO MAKE HIM GO BACK TO HIS STARTING CAMERA
public class Dave extends Animatronic
{
    private enum DaveState { GRACE, MOVING, DOOR, JUMPSCARE }
    private DaveState state = DaveState.GRACE;

    private static final int[] PATH = { 6, 0, 5, 1, 4, 2, 3 };
    private int pathIndex = 0;

    private static final int GRACE_DURATION = 300;
    private static final int MOVE_INTERVAL = 250;
    private static final int DOOR_COUNTDOWN = 120;

    private int graceTimer = 0;
    private int moveTimer = 0;
    private int doorTimer = 0;

    public Dave()
    {
        currentCamera = 6;
        location = Location.CAMERA;
    }

    @Override
    public void update(GameContext ctx)
    {
        handleShock(ctx);

        switch(state)
        {
            case GRACE -> handleGrace();
            case MOVING -> handleMoving(ctx);
            case DOOR -> handleDoor();
            case JUMPSCARE -> ctx.stateManager.setState(StateManager.LOSE_STATE);
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
            state = DaveState.JUMPSCARE;
    }

    private void handleShock(GameContext ctx)
    {
        if(ctx.wasShockPressed()) reset();
    }

    private void handleMoving(GameContext ctx)
    {
        int watchedCamera = ctx.cameras.getCurrentCamera();
        boolean playerWatching = ctx.cameras.isMonitorUp() && (watchedCamera == currentCamera);
        boolean cameraViewable = ctx.cameras.isCameraViewable(currentCamera);

        if(playerWatching && cameraViewable) return;

        int interval = cameraViewable ? MOVE_INTERVAL : MOVE_INTERVAL / 2;

        moveTimer++;
        if(moveTimer >= interval)
        {
            moveTimer = 0;
            if(shouldMove()) advancePath();
        }
    }

    private void advancePath()
    {
        pathIndex++;

        if(pathIndex >= PATH.length)
        {
            location = Location.DOOR;
            state = DaveState.DOOR;
            doorTimer = DOOR_COUNTDOWN;
        }
        else
        {
            currentCamera = PATH[pathIndex];
        }
    }

    // WHEN SHOCKED, DAVE RESETS BACK TO CAM 6
    private void reset()
    {
        currentCamera = 6;
        location = Location.CAMERA;
        state = DaveState.MOVING;
        pathIndex = 0;
        moveTimer = 0;
        doorTimer = 0;

        System.out.println("DAVE GOT SHOCKED!!!");
    }

    @Override
    public void drawOnCamera(Graphics2D g2, int swayX)
    {
        g2.setColor(new Color(91, 139, 241));
        g2.fillRect(200, 80, 30, 30);

        g2.setFont(FontManager.LCD_SMALL);
        g2.drawString("DAVE", 245, 100);
    }

    @Override
    public void drawOnDoor(Graphics2D g2)
    {
        g2.setColor(new Color(38, 135, 255, 180));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_LARGE);
        Utility.drawCentered(g2, "DAVE IS AT THE MAIN", GamePanel.HEIGHT / 2 - 20);

        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "SHOCK HIM! " + (doorTimer / 30 + 1) + "s", GamePanel.HEIGHT / 2 + 20);
    }

    public boolean isAtDoor() { return state == DaveState.DOOR; }
    public int getDoorTimer() { return doorTimer; }
}
