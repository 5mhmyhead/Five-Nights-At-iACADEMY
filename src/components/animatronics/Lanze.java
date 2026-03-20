package components.animatronics;

import components.GameContext;
import components.JumpscarePlayer;
import components.cameras.MusicBox;
import state.StateManager;
import utilities.FontManager;
import utilities.SoundManager;
import utilities.Utility;

import java.awt.*;
import java.awt.image.BufferedImage;

// FNAF 2 PUPPET, HAS A PATIENCE METER THAT SLOWLY DECREASES OVER TIME
public class Lanze extends Animatronic
{
    private enum LanzeState { IDLE, IMPATIENT, AGGRESSIVE, CRITICAL }
    private LanzeState state = LanzeState.IDLE;

    // PATIENCE GOES DOWN BY 1 EVERY SUCCESSFUL MOVEMENT OPPORTUNITY
    // WHEN PATIENCE REACHES 0, THE PLAYER HAS 3 SECONDS OF BUFFER TIME TO REACT
    private static final int MAX_PATIENCE = 100;
    private static final int CRITICAL_COUNTDOWN = 120;

    private int patience = 100;
    private int criticalTimer = 0;

    private static final int MOVE_INTERVAL = 5;
    private int moveTimer = 0;

    // MULTIPLIER FOR MUSIC BOX BOOST
    private static final int BOOST_MULTIPLIER = 3;

    // SPRITES
    private final BufferedImage imageIdle;
    private final BufferedImage imageImpatient;
    private final BufferedImage imageAggressive;
    private final BufferedImage imageCritical;

    // LANZE SPRITE DOESN'T UPDATE IF PLAYER LOOKS AT HIM
    private BufferedImage displayedImage;

    // LANZE WAITS FOR THE PLAYER TO DROP THE MONITOR
    private final JumpscarePlayer jumpscare;
    private boolean jumpscareWaiting = false;

    public Lanze()
    {
        currentCamera = 0;
        location = Location.CAMERA;

        imageIdle = Utility.loadImage("/animatronics/lanzeIdle.png");
        imageImpatient = Utility.loadImage("/animatronics/lanzeImpatient.png");
        imageAggressive = Utility.loadImage("/animatronics/lanzeAggressive.png");
        imageCritical = Utility.loadImage("/animatronics/lanzeCritical.png");

        // SPRITE STARTS AT IDLE
        displayedImage = imageIdle;
        // LOAD JUMPSCARE
        jumpscare = new JumpscarePlayer("/jumpscares/lanze", 8);
    }

    @Override
    public void update(GameContext ctx)
    {
        // IF JUMPSCARE IS PLAYING, ONLY UPDATE IT
        if(jumpscare.isPlaying())
        {
            jumpscare.update();
            if(jumpscare.isFinished())
                ctx.stateManager.setState(StateManager.LOSE_STATE);
            return;
        }

        // TRIGGER JUMPSCARE ONCE MONITOR IS DOWN
        if(!(!jumpscareWaiting || ctx.cameras.isMonitorUp()))
        {
            jumpscareWaiting = false;
            jumpscare.play();
            return;
        }

        handleMovement();
        handlePatience(ctx);
        updateDisplayedImage(ctx);
    }

    private void handleMovement()
    {
        moveTimer++;
        if(moveTimer >= MOVE_INTERVAL)
        {
            moveTimer = 0;
            if(shouldMove()) patience--;
        }

        if(patience <= 0 && state != LanzeState.CRITICAL)
        {
            // TURNS CRITICAL WHEN REACHING 0 PATIENCE
            state = LanzeState.CRITICAL;
            criticalTimer = CRITICAL_COUNTDOWN;
        }
        else if(state != LanzeState.CRITICAL)
        {
            // UPDATE STATE OF LANZE
            if(patience <= 33)
                state = LanzeState.AGGRESSIVE;
            else if(patience <= 66)
                state = LanzeState.IMPATIENT;
            else
                state = LanzeState.IDLE;
        }
    }

    private void handlePatience(GameContext ctx)
    {
        MusicBox musicBox = ctx.cameras.getMusicBox();

        if(musicBox.isWinding())
        {
            patience++;
            if(patience >= MAX_PATIENCE)
            {
                patience = MAX_PATIENCE;
                musicBox.stopWinding(); // DONE WINDING
            }

            if(state == LanzeState.CRITICAL && patience > 0)
                state = LanzeState.AGGRESSIVE;

            // NOTIFY SPEED BOOST ON THE FRAME WINDING STARTS
            if(musicBox.wasClicked())
            {
                // EARL AND TYRONE MOVE FASTER WHEN THE MUSIC BOX IS WOUND
                // MUSIC BOX BOOST IS LANZE PATIENCE MULTIPLIED BY THREE
                // THE LOWER LANZE PATIENCE IS, THE LOWER THE BOOST
                ctx.cameras.getMusicBox().applyBoost(patience);
                int boostFrames = patience * BOOST_MULTIPLIER;

                for(Animatronic a : ctx.animatronics)
                {
                    if(a instanceof Earl earl) earl.applyMusicBoxBoost(boostFrames);
                    if(a instanceof Tyrone tyrone) tyrone.applyMusicBoxBoost(boostFrames);
                }
            }
        }
        else if(state == LanzeState.CRITICAL)
        {
            criticalTimer--;
            if(criticalTimer <= 0)
                jumpscareWaiting = true;
        }
    }

    @Override
    public void drawOnCamera(Graphics2D g2, int swayX)
    {
        if(displayedImage != null)
        {
            double scale = 1.5;
            g2.drawImage(displayedImage, 200 + swayX, 75, (int)(577 * scale), (int)(433 * scale), null);
        }
        else
        {
            g2.setColor(new Color(129, 100, 180));
            g2.fillRect(30, 30, 30, 30);

            g2.setFont(FontManager.LCD_SMALL);
            g2.drawString("LANZE [state: " + state + "]", 75, 50);
        }

        drawPatienceBar(g2);
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

    private void drawPatienceBar(Graphics2D g2)
    {
        int barX = 830;
        int barY = 630;
        int barW = 200;
        int barH = 10;

        // BACKGROUND
        g2.setColor(new Color(20, 20, 20, 180));
        g2.fillRoundRect(barX, barY, barW, barH, 4, 4);

        // FILL — SCALES WITH PATIENCE (0-100)
        int fillW = (int)(barW * (patience / 100.0));
        g2.setColor(new Color(100, 180, 255, 220));
        g2.fillRoundRect(barX, barY, fillW, barH, 4, 4);

        // BORDER
        g2.setColor(new Color(180, 220, 255));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(barX, barY, barW, barH, 4, 4);
        g2.setStroke(new BasicStroke(1));
    }

    private BufferedImage getCurrentImage()
    {
        return switch(state)
        {
            case IDLE -> imageIdle;
            case IMPATIENT -> imageImpatient;
            case AGGRESSIVE -> imageAggressive;
            case CRITICAL -> imageCritical;
        };
    }

    public void updateDisplayedImage(GameContext ctx)
    {
        boolean playerWatching = ctx.cameras.isMonitorUp()
                && ctx.cameras.getCurrentCamera() == currentCamera;

        if(!playerWatching) displayedImage = getCurrentImage();
    }
}
