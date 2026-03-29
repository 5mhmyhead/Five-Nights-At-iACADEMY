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

// OFFICE ANIMATRONIC, PATIENCE WILL GO DOWN WHEN LOOKING AT CAMERAS OR MAIN VIEW
// WILL ONLY REGAIN PATIENCE WHEN LOOKING AWAY AT THE DOOR

// EVERY 30-40 SECONDS, CRISTIAN EYES TURN RED
// PLAYER MUST STARE AT HIM FOR 2 SECONDS TO RESET HIS PATIENCE
// RED EYE ONLY HAPPENS WHEN CRISTIAN AI LEVEL IS HIGHER THAN 10
public class Cristian extends Animatronic
{
    private enum CristianState { IDLE, IMPATIENT, AGGRESSIVE, CRITICAL, RED_EYE, RED_EYE_CRITICAL }
    private enum FlickerState  { NONE, ACTIVE }

    private static final int CRITICAL_COUNTDOWN = 150;
    private static final int MOVE_INTERVAL = 9;
    private static int maxPatience = 100;

    private CristianState state = CristianState.IDLE;
    private int patience = maxPatience;
    private int criticalTimer = 0;
    private int moveTimer = 0;

    private static final int FREEZE_DURATION = 150;
    private static final int STARE_REQUIRED = 60;
    private static final int RED_EYE_GRACE = 45;
    private static final int RED_EYE_MIN = 1800; // 60 SECONDS
    private static final int RED_EYE_MAX = 2100; // 70 SECONDS

    //private int redEyeTrigger = 60;
    private int redEyeTrigger = RED_EYE_MIN + (int)(Math.random() * (RED_EYE_MAX - RED_EYE_MIN));
    private int redEyeTimer = 0;
    private int redEyeGraceTimer = 0;
    private int stareTimer = 0;
    private int freezeTimer = 0;
    private boolean warningShown = false;
    private boolean waitingForRedEye = false;

    private static final int FLASH_INTERVAL_MIN = 15;
    private static final int FLASH_INTERVAL_MAX = 45;

    private final BufferedImage[] flashFrames = new BufferedImage[4];
    private int currentFlashFrame = -1;
    private int flashTimer = 0;
    private int nextFlash = 20;

    private static final int TEXT_COUNT = 2;
    private static final int TEXT_VISIBLE_DURATION = 4;
    private static final int TEXT_POPUP_MIN = 8;
    private static final int TEXT_POPUP_MAX = 20;

    private final int[] textX = new int[TEXT_COUNT];
    private final int[] textY = new int[TEXT_COUNT];
    private final float[] textSize = new float[TEXT_COUNT];
    private final float[] textAlpha = new float[TEXT_COUNT];

    private boolean textVisible = false;
    private int textVisibleTimer = 0;
    private int nextTextPopup = 0;

    private static final int[] FLICKER_ALPHAS  = { 0, 190, 60, 255, 190, 60, 0 };
    private static final int FRAMES_PER_STEP = 3;

    private FlickerState flickerState = FlickerState.NONE;
    private int flickerStep = 0;
    private int flickerFrameTimer = 0;
    private Runnable onFlickerMidpoint; // CALLED WHEN ALPHA HITS 255

    private final BufferedImage imageIdle;
    private final BufferedImage imageImpatient;
    private final BufferedImage imageAggressive;
    private final BufferedImage imageCritical;
    private final BufferedImage imageLookAtMe;
    private BufferedImage displayedImage;

    private final JumpscarePlayer jumpscare;
    private boolean jumpscareWaiting = false;

    public Cristian()
    {
        location = Location.MAIN;

        imageIdle = Utility.loadImage("/animatronics/cristian/cristianIdle.png");
        imageImpatient = Utility.loadImage("/animatronics/cristian/cristianImpatient.png");
        imageAggressive = Utility.loadImage("/animatronics/cristian/cristianAggressive.png");
        imageCritical = Utility.loadImage("/animatronics/cristian/cristianCritical.png");
        imageLookAtMe = Utility.loadImage("/animatronics/cristian/cristianLookAtMe.png");
        displayedImage = imageIdle;

        for (int i = 0; i < flashFrames.length; i++)
            flashFrames[i] = Utility.loadImage("/animatronics/cristian/cristianFlash" + (i + 1) + ".png");

        jumpscare = new JumpscarePlayer("/jumpscares/cristian", 8);
        shuffleTextPositions();
    }

    @Override
    public void update(GameContext ctx)
    {
        if (jumpscare.isPlaying())
        {
            jumpscare.update();
            if (jumpscare.isFinished())
            {
                ctx.stateManager.setKiller("Cristian");
                ctx.stateManager.setState(StateManager.LOSE_STATE);
            }
            return;
        }

        // TRIGGER JUMPSCARE ONCE MONITOR IS DOWN OR BLINK IS DOWN
        if(!(!jumpscareWaiting || ctx.cameras.isMonitorUp() || ctx.blink.areEyesClosed()))
        {
            jumpscareWaiting = false;
            jumpscare.play();
            return;
        }

        updateFlicker();
        handleTransition(ctx);
        handleRedEye(ctx);
        handleMovement(ctx);
        handlePatience(ctx);
        updateDisplayedImage(ctx);
    }

    private void handleTransition(GameContext ctx)
    {
        if(waitingForRedEye && playerFacingOffice(ctx))
        {
            Runnable midpoint = () ->
            {
                state = CristianState.RED_EYE;
                redEyeGraceTimer = RED_EYE_GRACE;
            };

            // FLICKER ONLY IF PLAYER IS WATCHING THE OFFICE
            if (playerFacingOffice(ctx)) startFlicker(midpoint);
            else midpoint.run();

            waitingForRedEye = false;
        }
    }

    private void handleRedEye(GameContext ctx)
    {
        if (freezeTimer > 0) { freezeTimer--; return; }

        if (!isRedEyeActive())
        {
            tickRedEyeTrigger(ctx);

            if (waitingForRedEye && !warningShown)
                warningShown = true;

            return;
        }

        handleRedEyeStare(ctx);
    }

    private void tickRedEyeTrigger(GameContext ctx)
    {
        if (aiLevel <= 8) return;

        redEyeTimer++;
        if (redEyeTimer >= redEyeTrigger)
            activateRedEye(ctx);
    }

    private void activateRedEye(GameContext ctx)
    {
        redEyeTimer = 0;
        stareTimer = 0;
        redEyeTrigger  = RED_EYE_MIN + (int)(Math.random() * (RED_EYE_MAX - RED_EYE_MIN));
        waitingForRedEye = true;

        SoundManager.ITS_ME.setVolume(0.5);
        SoundManager.ITS_ME.loop();
    }

    private void handleRedEyeStare(GameContext ctx)
    {
        // PLAYER WILL NOT LOSE DURING THE GRACE PERIOD
        if (redEyeGraceTimer > 0) { redEyeGraceTimer--; return; }

        // STARE STARTS ONCE PLAYER IS LOOKING AT CRISTIAN
        if (playerFacingOffice(ctx))
        {
            stareTimer++;
            if (stareTimer >= STARE_REQUIRED) onRedEyeSuccess(ctx);
        }
        else if (stareTimer > 0)
        {
            if (!ctx.cameras.isTransitioning() && ctx.cameras.isMonitorUp())
                ctx.cameras.forceMonitorDown();

            if (ctx.blink.areEyesClosed())
                ctx.blink.forceOpen();

            SoundManager.LOOK_AT_ME.stop();
            jumpscareWaiting = true;
        }
    }

    private void onRedEyeSuccess(GameContext ctx)
    {
        SoundManager.ITS_ME.stop();
        freezeTimer = FREEZE_DURATION;
        patience = maxPatience;
        stareTimer = 0;
        warningShown = false;

        Runnable midpoint = () ->
        {
            state = CristianState.IDLE;
            displayedImage = imageIdle;
        };

        // FLICKER ONLY IF PLAYER IS WATCHING THE OFFICE
        if (playerFacingOffice(ctx)) startFlicker(midpoint);
        else midpoint.run();
    }

    private void handleMovement(GameContext ctx)
    {
        if (freezeTimer > 0) return; // PATIENCE IS FROZEN AFTER RED EYE SUCCESS

        moveTimer++;
        if (moveTimer >= MOVE_INTERVAL)
        {
            moveTimer = 0;
            if (shouldMove()) patience -= isRedEyeActive() ? 2 : 1;
        }

        if (patience <= 0 && state != CristianState.CRITICAL && state != CristianState.RED_EYE_CRITICAL)
        {
            state = (state == CristianState.RED_EYE) ? CristianState.RED_EYE_CRITICAL : CristianState.CRITICAL;
            criticalTimer = CRITICAL_COUNTDOWN;
        }
        else if (state != CristianState.CRITICAL && !isRedEyeActive())
        {
            if (patience <= 33)
                state = CristianState.AGGRESSIVE;
            else if (patience <= 66)
                state = CristianState.IMPATIENT;
            else
                state = CristianState.IDLE;
        }
    }

    private void handlePatience(GameContext ctx)
    {
        if (ctx.office.isPlayerAtDoor())
        {
            // PATIENCE RECOVERS WHILE LOOKING AT THE DOOR
            patience = Math.min(patience + 1, maxPatience);
            if (state == CristianState.CRITICAL && patience > 0)
                state = CristianState.AGGRESSIVE;
        }
        else if (state == CristianState.CRITICAL || state == CristianState.RED_EYE_CRITICAL)
        {
            criticalTimer--;
            if (criticalTimer <= 0)
            {
                if (!ctx.cameras.isTransitioning() && ctx.cameras.isMonitorUp())
                    ctx.cameras.forceMonitorDown();

                if (ctx.blink.areEyesClosed())
                    ctx.blink.forceOpen();

                SoundManager.LOOK_AT_ME.stop();
                jumpscareWaiting = true;
            }
        }
    }

    @Override
    public void drawOnOffice(Graphics2D g2)
    {
        if(jumpscare.isPlaying()) return;

        if (displayedImage != null)
            g2.drawImage(displayedImage, 0, 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
        else
        {
            g2.setColor(new Color(243, 33, 82));
            g2.fillRect(30, 30, 30, 30);

            g2.setFont(FontManager.LCD_SMALL);
            g2.drawString("CRISTIAN [state: " + state + "]", 75, 50);
        }

        drawFlicker(g2);
    }

    public void drawRedEyeOverlay(Graphics2D g2, GameContext ctx)
    {
        if (jumpscare.isPlaying()) return; // DON'T DRAW OVERLAY DURING JUMPSCARE
        updateRedEyeEffect();

        if(!playerFacingOffice(ctx))
        {
            Utility.drawAmbientScanlines(g2, new Color(255, 255, 255), 2);
            Utility.drawStatic(g2, 8, 8, new Color(255, 255, 255));
        }
        else
        {
            Utility.drawAmbientScanlines(g2, new Color(255, 255, 255, 20), 2);
            Utility.drawStatic(g2, 8, 10, new Color(255, 255, 255));
        }

        // FLASH FRAMES
        if(!playerFacingOffice(ctx))
        {
            if (currentFlashFrame >= 0 && flashFrames[currentFlashFrame] != null)
                g2.drawImage(flashFrames[currentFlashFrame], 0, 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
        }

        // LOOK AT ME TEXT
        if (textVisible)
        {
            for (int i = 0; i < TEXT_COUNT; i++)
            {
                g2.setFont(FontManager.LCD_CLOCK.deriveFont(textSize[i]));
                g2.setColor(new Color(255, 255, 255, (int) textAlpha[i]));
                g2.drawString("LOOK AT ME", textX[i], textY[i]);
            }
        }
    }

    @Override
    public void drawJumpscare(Graphics2D g2) { jumpscare.draw(g2); }

    private void updateRedEyeEffect()
    {
        // FLASH FRAME TIMING
        if (currentFlashFrame >= 0)
        {
            flashTimer--;
            if (flashTimer <= 0) currentFlashFrame = -1;
        }
        else
        {
            nextFlash--;
            if (nextFlash <= 0)
            {
                currentFlashFrame = (int)(Math.random() * flashFrames.length);
                flashTimer = 2 + (int)(Math.random() * 4);
                nextFlash = FLASH_INTERVAL_MIN + (int)(Math.random() * (FLASH_INTERVAL_MAX - FLASH_INTERVAL_MIN));
            }
        }

        // TEXT POPUP TIMING
        if (textVisible)
        {
            textVisibleTimer--;
            if (textVisibleTimer <= 0)
            {
                textVisible = false;
                nextTextPopup = TEXT_POPUP_MIN + (int)(Math.random() * (TEXT_POPUP_MAX - TEXT_POPUP_MIN));
            }
        }
        else
        {
            nextTextPopup--;
            if (nextTextPopup <= 0)
            {
                textVisible = true;
                textVisibleTimer = TEXT_VISIBLE_DURATION;
                shuffleTextPositions();
            }
        }
    }

    private void shuffleTextPositions()
    {
        for (int i = 0; i < TEXT_COUNT; i++)
        {
            textX[i] = 50 + (int)(Math.random() * (GamePanel.WIDTH  - 200));
            textY[i] = 80 + (int)(Math.random() * (GamePanel.HEIGHT - 100));
            textSize[i] = 40f + (float)(Math.random() * 80f);
            textAlpha[i] = 80  + (int)(Math.random() * 175);
        }
    }

    private void startFlicker(Runnable onMidpoint)
    {
        flickerState = FlickerState.ACTIVE;
        flickerStep = 0;
        flickerFrameTimer = 0;
        onFlickerMidpoint = onMidpoint;
    }

    private void updateFlicker()
    {
        if (flickerState == FlickerState.NONE) return;

        flickerFrameTimer++;
        if (flickerFrameTimer < FRAMES_PER_STEP) return;
        flickerFrameTimer = 0;
        flickerStep++;

        // MIDPOINT
        if (flickerStep == 3 && onFlickerMidpoint != null)
        {
            onFlickerMidpoint.run();
            onFlickerMidpoint = null;
        }

        if (flickerStep >= FLICKER_ALPHAS.length)
        {
            flickerStep  = 0;
            flickerState = FlickerState.NONE;
        }
    }

    private void drawFlicker(Graphics2D g2)
    {
        if (flickerState == FlickerState.NONE) return;
        int alpha = FLICKER_ALPHAS[flickerStep];
        if (alpha == 0) return;
        g2.setColor(new Color(0, 0, 0, alpha));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
    }

    public void updateDisplayedImage(GameContext ctx)
    {
        // ALWAYS UPDATE SPRITE DURING FLICKER — MIDPOINT CALLBACK NEEDS TO SHOW THROUGH
        if (flickerState == FlickerState.ACTIVE || isRedEyeActive())
        {
            displayedImage = getCurrentImage();
            return;
        }

        if (!playerFacingOffice(ctx))
            displayedImage = getCurrentImage();
    }

    private BufferedImage getCurrentImage()
    {
        return switch (state)
        {
            case IDLE -> imageIdle;
            case IMPATIENT -> imageImpatient;
            case AGGRESSIVE -> imageAggressive;
            case CRITICAL -> imageCritical;
            case RED_EYE, RED_EYE_CRITICAL -> imageLookAtMe;
        };
    }

    private boolean playerFacingOffice(GameContext ctx)
    {
        return !ctx.cameras.isMonitorUp()
                && !ctx.office.isPlayerAtDoor()
                && !ctx.blink.areEyesClosed();
    }

    public boolean shouldShowWarning()
    {
        return (isRedEyeActive() || waitingForRedEye) && warningShown;
    }

    public boolean isRedEyeActive()
    {
        return state == CristianState.RED_EYE || state == CristianState.RED_EYE_CRITICAL;
    }

    public void setMaxPatience(int max)
    {
        maxPatience = max;
        patience = max;
    }

    @Override
    public boolean jumpscareIsPlaying() { return jumpscare.isPlaying(); }
    public int getPatience() { return patience; }
}
