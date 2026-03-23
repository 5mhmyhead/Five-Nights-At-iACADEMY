package components.animatronics;

import components.GameContext;
import main.GamePanel;
import state.StateManager;
import utilities.FontManager;

import java.awt.*;

// OFFICE ANIMATRONIC, PATIENCE WILL GO DOWN WHEN LOOKING AT CAMERAS OR MAIN VIEW
// WILL ONLY REGAIN PATIENCE WHEN LOOKING AWAY AT THE DOOR

// EVERY 30-40 SECONDS, CRISTIAN EYES TURN RED
// PLAYER MUST STARE AT HIM FOR 2 SECONDS TO RESET HIS PATIENCE
// RED EYE ONLY HAPPENS WHEN CRISTIAN AI LEVEL IS HIGHER THAN 10
public class Cristian extends Animatronic
{
    private enum CristianState { IDLE, IMPATIENT, AGGRESSIVE, CRITICAL, RED_EYE, RED_EYE_CRITICAL }
    private CristianState state = CristianState.IDLE;

    // PATIENCE GOES DOWN BY 1 EVERY SUCCESSFUL MOVEMENT OPPORTUNITY
    // WHEN PATIENCE REACHES 0, THE PLAYER HAS 3 SECONDS OF BUFFER TIME TO REACT
    private static final int MAX_PATIENCE = 100;
    private static final int CRITICAL_COUNTDOWN = 150;
    private static final int MOVE_INTERVAL = 7;

    private int patience = 100;
    private int criticalTimer = 0;
    private int moveTimer = 0;

    // RED EYES MECHANIC, PATIENCE LOWERS TWICE AS FAST IN THIS STATE
    // CRISTIAN JUMPSCARES THE PLAYER WHEN THEY MOVE THEIR HEAD, FLIP THE CAMERA, OR CLOSE THEIR EYES
    private static final int FREEZE_DURATION = 210;
    private static final int STARE_REQUIRED = 60;
    private static final int RED_EYE_GRACE = 45;

    private static final int RED_EYE_MAX = 2100; // 70 SECONDS
    private static final int RED_EYE_MIN = 1800; // 60 SECONDS

    private int redEyeTrigger;
    private int redEyeGraceTimer = 0;

    private int redEyeTimer = 0;
    private int stareTimer = 0;
    private int freezeTimer = 0;
    private boolean warningShown = false;

    public Cristian()
    {
        location = Location.MAIN;
        redEyeTrigger = RED_EYE_MIN + (int)(Math.random() * (RED_EYE_MAX - RED_EYE_MIN));
    }

    @Override
    public void update(GameContext ctx)
    {
        handleRedEye(ctx);
        handleMovement(ctx);
        handlePatience(ctx);
    }

    // RED EYE MECHANICS
    private void handleRedEye(GameContext ctx)
    {
        if(freezeTimer > 0) { freezeTimer--; return; }

        if(state != CristianState.RED_EYE && state != CristianState.RED_EYE_CRITICAL)
        {
            tickRedEyeTrigger(ctx);
            return;
        }

        handleRedEyeStare(ctx);
    }

    private void tickRedEyeTrigger(GameContext ctx)
    {
        if(aiLevel <= 8) return;

        redEyeTimer++;
        if(redEyeTimer >= redEyeTrigger)
            activateRedEye(ctx);
    }

    private void activateRedEye(GameContext ctx)
    {
        warningShown = ctx.cameras.isMonitorUp();
        redEyeGraceTimer = RED_EYE_GRACE;
        state = CristianState.RED_EYE;
        redEyeTimer = 0;
        stareTimer = 0;

        // ROLLS FOR THE NEXT RED EYE CALL
        redEyeTrigger = RED_EYE_MIN + (int)(Math.random() * (RED_EYE_MAX - RED_EYE_MIN));
    }

    private void handleRedEyeStare(GameContext ctx)
    {
        // PLAYER WILL NOT LOSE DURING THE GRACE PERIOD
        if(redEyeGraceTimer > 0)
        {
            redEyeGraceTimer--;
            return;
        }

        boolean playerInOffice = !ctx.cameras.isMonitorUp() && !ctx.office.isPlayerAtDoor();
        boolean playerInCameras = ctx.cameras.isMonitorUp();

        // RED EYE WARNING IN THE CAMERAS
        if(playerInCameras && !warningShown) warningShown = true;

        if(playerInOffice && !ctx.blink.areEyesClosed())
        {
            stareTimer++;
            if(stareTimer >= STARE_REQUIRED) onRedEyeSuccess();
        }
        else if(stareTimer > 0)
        {
            // IF PLAYER LOOKS AWAY MID STARE THEN PLAYER LOSES
            ctx.stateManager.setKiller("Cristian");
            ctx.stateManager.setState(StateManager.LOSE_STATE);
        }
    }

    private void onRedEyeSuccess()
    {
        freezeTimer = FREEZE_DURATION;
        patience = MAX_PATIENCE;
        state = CristianState.IDLE;
        stareTimer = 0;
        warningShown = false;
    }

    private void handleMovement(GameContext ctx)
    {
        // PATIENCE IS FROZEN AFTER RED EYES
        if(freezeTimer > 0) return;

        moveTimer++;
        if(moveTimer >= MOVE_INTERVAL)
        {
            moveTimer = 0;
            if(shouldMove()) patience -= isRedEyeActive() ? 2 : 1;
        }

        if(patience <= 0 && state != CristianState.CRITICAL
                && state != CristianState.RED_EYE_CRITICAL)
        {
            if(state == CristianState.RED_EYE)
                state = CristianState.RED_EYE_CRITICAL;
            else
                state = CristianState.CRITICAL;

            criticalTimer = CRITICAL_COUNTDOWN;
        }
        else if(state != CristianState.CRITICAL
                && state != CristianState.RED_EYE
                && state != CristianState.RED_EYE_CRITICAL)
        {
            if(patience <= 33)
                state = CristianState.AGGRESSIVE;
            else if(patience <= 66)
                state = CristianState.IMPATIENT;
            else
                state = CristianState.IDLE;
        }
    }

    private void handlePatience(GameContext ctx)
    {
        if(ctx.office.isPlayerAtDoor())
        {
            // PATIENCE RECOVERS WHILE LOOKING AT DOOR
            patience++;
            if(patience >= MAX_PATIENCE)
                patience = MAX_PATIENCE;

            // EXIT CRITICAL IF PATIENCE RECOVERED ABOVE 0
            if(state == CristianState.CRITICAL && patience > 0)
                state = CristianState.AGGRESSIVE;
        }
        else if(state == CristianState.CRITICAL || state == CristianState.RED_EYE_CRITICAL)
        {
            criticalTimer--;
            if(criticalTimer <= 0)
            {
                ctx.stateManager.setKiller("Cristian");
                ctx.stateManager.setState(StateManager.LOSE_STATE);
            }
        }
    }

    @Override
    public void drawOnOffice(Graphics2D g2)
    {
        Color eyeColor = (state == CristianState.RED_EYE)
                ? new Color(255, 0, 0)
                : new Color(255, 56, 56);

        g2.setColor(eyeColor);
        g2.fillRect(30, 30, 30, 30);

        g2.setFont(FontManager.LCD_SMALL);
        g2.drawString("CRISTIAN [" + state + "]", 75, 50);

        if(state == CristianState.RED_EYE)
        {
            g2.setColor(new Color(255, 0, 0, 180));
            g2.setFont(FontManager.LCD_CLOCK);
            int x = (GamePanel.WIDTH - g2.getFontMetrics().stringWidth("LOOK AT ME")) / 2;
            g2.drawString("LOOK AT ME", x, GamePanel.HEIGHT / 2);
        }

        drawPatienceBar(g2);
    }

    @Override
    public void drawOnCamera(Graphics2D g2, int swayX)
    {
        if(!shouldShowCameraWarning()) return;

        if((System.currentTimeMillis() / 100) % 2 == 0)
        {
            g2.setColor(new Color(255, 0, 0, 200));
            g2.setFont(FontManager.LCD_CLOCK);
            int x = (GamePanel.WIDTH - g2.getFontMetrics().stringWidth("LOOK AT ME")) / 2;
            g2.drawString("LOOK AT ME", x, GamePanel.HEIGHT / 2);
        }
    }

    private void drawPatienceBar(Graphics2D g2)
    {
        int barX = 30;
        int barY = 75;
        int barW = 200;
        int barH = 10;

        g2.setColor(new Color(20, 20, 20, 180));
        g2.fillRoundRect(barX, barY, barW, barH, 4, 4);

        int fillW = (int)(barW * (patience / 100.0));
        g2.setColor(new Color(255, 100, 100, 220));
        g2.fillRoundRect(barX, barY, fillW, barH, 4, 4);

        g2.setColor(new Color(255, 180, 180));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(barX, barY, barW, barH, 4, 4);
        g2.setStroke(new BasicStroke(1));
    }

    public boolean shouldShowCameraWarning()
    {
        return (state == CristianState.RED_EYE || state == CristianState.RED_EYE_CRITICAL) && warningShown;
    }

    public boolean isRedEyeActive()
    {
        return state == CristianState.RED_EYE || state == CristianState.RED_EYE_CRITICAL;
    }

    public int getPatience() { return patience; }
}
