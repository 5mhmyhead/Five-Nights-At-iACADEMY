package components.animatronics;

import components.GameContext;
import main.GamePanel;
import state.StateManager;
import utilities.FontManager;
import utilities.Utility;

import java.awt.*;

// LIKE CHIKA, MOVES IN THE CAMERAS ON THE RIGHT OF THE PLAYER
// WHEN THEY ARE AT THE OFFICE, GO TO THE CORRESPONDING VIEW AND BLINK
// TYRONE MOVES FASTER THAN EARL
// PATH: CAMERA 4 -> 2 -> 3 -> 5
public class Tyrone extends Animatronic
{
    public enum TyroneState { MOVING, MAIN, JUMPSCARE }
    private TyroneState state = TyroneState.MOVING;

    private static final int[] PATH = { 3, 1, 2, 4 };
    private int pathIndex = 0;

    private int moveTimer = 0;
    private int doorTimer = 0;

    private static final int MOVE_INTERVAL = 200; // 10 SECONDS
    private static final int DOOR_COUNTDOWN = 150; // 5 SECONDS

    public Tyrone()
    {
        currentCamera = 3;
        location = Location.CAMERA;
    }

    @Override
    public void update(GameContext ctx)
    {
        switch(state)
        {
            case MOVING -> handleMoving(ctx);
            case MAIN -> handleDoor(ctx);
            case JUMPSCARE -> ctx.stateManager.setState(StateManager.LOSE_STATE);
        }
    }

    private void handleMoving(GameContext ctx)
    {
        boolean playerWatching = ctx.cameras.isMonitorUp()
                && ctx.cameras.getCurrentCamera() == currentCamera
                && ctx.cameras.isCameraViewable(currentCamera);

        // TYRONE MOVES TWICE AS FAST WHEN BEING WATCHED
        // MOVES NORMALLY IN HIS STARTING CAM
        if(playerWatching && !(currentCamera == 3)) moveTimer++;
        moveTimer++;

        if(moveTimer >= MOVE_INTERVAL)
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
            // WHEN REACHING CAM 5, MOVE TO MAIN
            location = Location.MAIN;
            state = TyroneState.MAIN;
            doorTimer = 0;
        }
        else
        {
            currentCamera = PATH[pathIndex];
        }
    }

    private void handleDoor(GameContext ctx)
    {
        boolean playerDefending = !ctx.office.isPlayerAtDoor()
                && ctx.blink.areEyesClosed();

        if(playerDefending) reset();
        else
        {
            doorTimer++;
            if(doorTimer >= DOOR_COUNTDOWN)
                ctx.stateManager.setState(StateManager.LOSE_STATE);
        }
    }

    private void reset()
    {
        currentCamera = 3;
        location = Location.CAMERA;
        state = TyroneState.MOVING;
        pathIndex = 0;
        moveTimer = 0;
        doorTimer = 0;
    }

    @Override
    public void drawOnCamera(Graphics2D g2, int swayX)
    {
        g2.setColor(new Color(196, 180, 74));
        g2.fillRect(200, 30, 30, 30);

        g2.setFont(FontManager.LCD_SMALL);
        g2.drawString("TYRONE", 245, 50);
    }

    @Override
    public void drawOnOffice(Graphics2D g2)
    {
        g2.setColor(new Color(191, 150, 48, 180));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_LARGE);
        Utility.drawCentered(g2, "TYRONE IS HERE", GamePanel.HEIGHT / 2 - 20);

        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "BLINK! " + (doorTimer / 30 + 1) + "s", GamePanel.HEIGHT / 2 + 20);
    }
}
