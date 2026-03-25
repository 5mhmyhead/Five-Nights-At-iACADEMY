package state.states;

import components.Clock;
import components.GameContext;
import components.animatronics.*;
import components.cameras.CameraSystem;
import components.nights.NightConfig;
import components.office.BlinkSystem;
import components.office.OfficeView;
import state.State;
import state.StateManager;
import utilities.SoundManager;
import utilities.Utility;

import java.awt.*;

public class GameState extends State
{
    private GameContext ctx;               // A CONTEXT CLASS THAT HOLDS ALL THE MECHANICS OF THE GAME
    private Animatronic[] animatronics;    // THE ANIMATRONICS IN THE GAME
    private int[] previousCameras;         // PREVIOUS ANIMATRONICS CAMERAS TO CHECK DISRUPTION

    // NIGHT OVER FADE
    private boolean nightOver = false;
    private int fadeOutTimer  = 0;
    private static final int FADE_OUT_DURATION = 75; // 2.5 SECONDS

    public GameState(StateManager stateManager)
    {
        super(stateManager);
        init();
    }

    @Override
    public void init()
    {
        initAnimatronics();
        initContext();
        applyNightConfig();
    }

    private void initAnimatronics()
    {
        animatronics = new Animatronic[]
        {
            new Dave(),
            new Earl(),
            new Tyrone(),
            new Cristian(),
            new Jirsten(),
            new Lanze(),
        };

        previousCameras = new int[animatronics.length];
        for(int i = 0; i < animatronics.length; i++)
            previousCameras[i] = animatronics[i].getCurrentCamera();
    }

    private void initContext()
    {
        ctx = new GameContext(
            stateManager,
            animatronics,
            new CameraSystem(),
            new OfficeView(),
            new BlinkSystem(),
            new Clock()
        );
    }

    private void applyNightConfig()
    {
        // APPLY AI LEVELS DEPENDING ON THE NIGHT
        NightConfig config = stateManager.getNightManager().getConfig();
        for(int i = 0; i < animatronics.length; i++)
            animatronics[i].setAiLevel(config.aiLevels()[i]);

        System.out.println("Dave AI: " + config.aiLevels()[0]);
        System.out.println("Earl AI: " + config.aiLevels()[1]);
        System.out.println("Tyrone AI: " + config.aiLevels()[2]);
        System.out.println("Cristian AI: " + config.aiLevels()[3]);
        System.out.println("Jirsten AI: " + config.aiLevels()[4]);
        System.out.println("Lanze AI: " + config.aiLevels()[5]);
    }

    @Override
    public void update()
    {
        if(handleActiveJumpscare()) return;
        if(handleNightOver()) return;

        updateAnimatronics();
        updateSystems();
    }

    private boolean handleActiveJumpscare()
    {
        for(Animatronic a : animatronics)
        {
            if(a.jumpscareIsPlaying())
            {
                a.update(ctx);
                ctx.cameras.update();
                ctx.blink.update();
                return true;
            }
        }
        return false;
    }

    private boolean handleNightOver()
    {
        if(ctx.clock.isNightOver() && !nightOver)
        {
            nightOver = true;
            fadeOutTimer = FADE_OUT_DURATION;
        }

        if(nightOver)
        {
            fadeOutTimer--;
            if(fadeOutTimer <= 0)
                stateManager.setState(StateManager.WIN_STATE);
            return true;
        }

        return false;
    }

    private void updateAnimatronics()
    {
        for(int i = 0; i < animatronics.length; i++)
        {
            Animatronic a = animatronics[i];
            if(a.getAiLevel() <= 0) continue;

            int prevCam = previousCameras[i];
            a.update(ctx);
            int newCam = a.getCurrentCamera();

            // TRIGGER DISRUPTION IF ANIMATRONIC ENTERED OR LEFT WATCHED CAMERA
            if(prevCam != newCam && ctx.cameras.isMonitorUp())
            {
                int watchedCam = ctx.cameras.getCurrentCamera();
                if(prevCam == watchedCam || newCam == watchedCam)
                    ctx.cameras.triggerDisruption();
            }

            previousCameras[i] = newCam;
        }
    }

    private void updateSystems()
    {
        ctx.cameras.update();
        ctx.office.update();
        ctx.blink.update();
        ctx.clock.update();
    }

    @Override
    public void draw(Graphics2D g2)
    {
        drawOffice(g2);
        drawCameraSystem(g2);
        drawHUD(g2);
        drawJumpscares(g2);
        drawNightOverFade(g2);
    }

    private void drawOffice(Graphics2D g2)
    {
        ctx.office.draw(g2);

        boolean monitorGoingDown = ctx.cameras.isTransitioningDown();

        if(ctx.cameras.isMonitorUp() && !monitorGoingDown) return;

        if(!ctx.office.isPlayerAtDoor() && !ctx.office.isTransitioning())
            drawOfficeAnimatronics(g2);
        else if(ctx.office.isPlayerAtDoor() && !ctx.office.isTransitioning())
            drawDoorAnimatronics(g2);
    }

    private void drawOfficeAnimatronics(Graphics2D g2)
    {
        for(Animatronic a : animatronics)
            if(a.getAiLevel() > 0 && a.getLocation() == Animatronic.Location.MAIN)
                a.drawOnOffice(g2);
    }

    private void drawDoorAnimatronics(Graphics2D g2)
    {
        for(Animatronic a : animatronics)
            if(a.getAiLevel() > 0 && a.getLocation() == Animatronic.Location.DOOR)
                a.drawOnDoor(g2);
    }

    private void drawCameraSystem(Graphics2D g2)
    {
        ctx.cameras.draw(g2, ctx.isInMainView(), animatronics);
    }

    private void drawHUD(Graphics2D g2)
    {
        // CRISTIAN CAMERA WARNING
        ctx.clock.draw(g2, ctx.getClockColor(), ctx.getNightNumber());

        if (!ctx.isInCameras())
            ctx.office.drawHoverZoneOverlay(g2);

        ctx.blink.draw(g2, !ctx.isInCameras() && !ctx.cameras.isTransitioning(), ctx.office.isTransitioning());

        // DRAW CRISTIAN OVERLAY IN CAMERAS, AT DOOR, AND WHEN EYES CLOSED
        for (Animatronic a : animatronics)
        {
            if (!(a instanceof Cristian cristian) || cristian.getAiLevel() <= 0) continue;

            if (cristian.isRedEyeActive() || cristian.shouldShowWarning())
                cristian.drawRedEyeOverlay(g2, ctx);
        }
    }

    private void drawJumpscares(Graphics2D g2)
    {
        for(Animatronic a : animatronics)
            if(a.jumpscareIsPlaying() && !(a instanceof Jirsten))
                a.drawJumpscare(g2);
    }

    private void drawNightOverFade(Graphics2D g2)
    {
        if(!nightOver) return;
        SoundManager.RISER.loop();

        float progress = 1.0f - (float) fadeOutTimer / FADE_OUT_DURATION;
        int scanlineCount = (int)(4 + progress * 15);

        Utility.drawAmbientScanlines(g2, new Color(255, 255, 255), scanlineCount);
        Utility.drawStatic(g2, (int)(progress * FADE_OUT_DURATION),
                FADE_OUT_DURATION, new Color(255, 255, 255));
    }

    @Override
    public void onEnter()
    {
        SoundManager.MAIN_MENU.stop();
        SoundManager.AMBIENCE.loop();
        SoundManager.MUSIC_BOX.loop();
        SoundManager.MUSIC_BOX.mute();
        SoundManager.MUSIC_BOX_SPED_UP.loop();
        SoundManager.MUSIC_BOX_SPED_UP.mute();
    }

    @Override public void keyPressed(int key)
    {
        if(nightOver) return;
        ctx.cameras.keyPressed(key);
    }

    @Override public void mouseClicked(int x, int y)
    {
        if(nightOver) return;
        ctx.cameras.mouseClicked(x, y);
    }

    @Override
    public void mouseMoved(int x, int y)
    {
        if(nightOver) return;
        // LET THE PLAYER ACCESS THE CAMERAS ONLY WHEN THEY ARE IN MAIN VIEW
        if(ctx.isInMainView() && ctx.blink.getCloseTimer() == 0)
            ctx.cameras.mouseMoved(x, y);

        if(!ctx.isInCameras() && ctx.blink.getCloseTimer() == 0)
            ctx.office.mouseMoved(x, y);

        if(!ctx.isInCameras() && !ctx.office.isTransitioning())
            ctx.blink.mouseMoved(x, y);
    }

    @Override public void keyReleased(int key) {}
}