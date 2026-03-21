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

import java.awt.*;

public class GameState extends State
{
    private GameContext ctx;               // A CONTEXT CLASS THAT HOLDS ALL THE MECHANICS OF THE GAME
    private Animatronic[] animatronics;    // THE ANIMATRONICS IN THE GAME

    public GameState(StateManager stateManager)
    {
        super(stateManager);
        init();
    }

    @Override
    public void init()
    {
        SoundManager.AMBIENCE.loop();

        SoundManager.MUSIC_BOX.loop();
        SoundManager.MUSIC_BOX.mute();

        SoundManager.MUSIC_BOX_SPED_UP.loop();
        SoundManager.MUSIC_BOX_SPED_UP.mute();

        animatronics = new Animatronic[]
        {
            new Dave(),
            new Earl(),
            new Tyrone(),
            new Cristian(),
            new Jirsten(),
            new Lanze(),
        };

        ctx = new GameContext(
            stateManager,
            animatronics,
            new CameraSystem(),
            new OfficeView(),
            new BlinkSystem(),
            new Clock()
        );

        // APPLY AI LEVELS DEPENDING ON THE NIGHT
        NightConfig config = stateManager.getNightManager().getConfig();
        for(int i = 0; i < animatronics.length; i++)
            animatronics[i].setAiLevel(config.aiLevels[i]);
    }

    @Override
    public void update()
    {
        // CHECK FOR ACTIVE JUMPSCARE FIRST
        for(Animatronic a : animatronics)
            if(a.jumpscareIsPlaying())
            {
                a.update(ctx); // ONLY UPDATE THE ANIMATRONIC WITH ACTIVE JUMPSCARE
                return;        // BLOCK EVERYTHING ELSE
            }

        // NIGHT ENDS WHEN CLOCK HITS 6 AM
        if(ctx.clock.isNightOver()) stateManager.setState(StateManager.WIN_STATE);

        // UPDATE ANIMATRONICS
        for(Animatronic a : animatronics)
            if(a.getAiLevel() > 0) a.update(ctx);

        // DRAW THE GAME UI
        ctx.cameras.update();
        ctx.office.update();
        ctx.blink.update();
        ctx.clock.update();
    }

    @Override
    public void draw(Graphics2D g2)
    {
        ctx.office.draw(g2);

        // DRAW ANIMATRONICS
        if(!ctx.cameras.isMonitorUp())
        {
            if(!ctx.office.isPlayerAtDoor())
            {
                // OFFICE ANIMATRONICS
                for(Animatronic a : animatronics)
                    if(a.getAiLevel() > 0 && a.getLocation() == Animatronic.Location.MAIN)
                        a.drawOnOffice(g2);
            }
            else
            {
                // DOOR ANIMATRONICS
                for(Animatronic a : animatronics)
                    if(a.getAiLevel() > 0 && a.getLocation() == Animatronic.Location.DOOR)
                        a.drawOnDoor(g2);
            }
        }

        ctx.cameras.draw(g2, ctx.isInMainView(), animatronics);

        // CRISTIAN CAMERA WARNING
        if(ctx.cameras.isMonitorUp())
            for(Animatronic a : animatronics)
                if(a instanceof Cristian cristian && cristian.getAiLevel() > 0)
                    cristian.drawOnCamera(g2, 0);

        ctx.clock.draw(g2, ctx.getClockColor(), ctx.getNightNumber());
        ctx.blink.draw(g2, !ctx.isInCameras(), ctx.office.isTransitioning());

        // JUMPSCARE WILL PLAY IF A PLAYER LOSES
        for(Animatronic a : animatronics)
            if(a.jumpscareIsPlaying() && !(a instanceof Jirsten))
                a.drawJumpscare(g2);
    }

    @Override public void keyPressed(int key)
    {
        ctx.cameras.keyPressed(key);
    }

    @Override
    public void mouseMoved(int x, int y)
    {
        // LET THE PLAYER ACCESS THE CAMERAS ONLY WHEN THEY ARE IN MAIN VIEW
        if(ctx.isInMainView() && ctx.blink.getCloseTimer() == 0) ctx.cameras.mouseMoved(x, y);
        if(!ctx.isInCameras() && ctx.blink.getCloseTimer() == 0) ctx.office.mouseMoved(x, y);
        if(!ctx.isInCameras() && !ctx.office.isTransitioning()) ctx.blink.mouseMoved(x, y);
    }

    @Override public void mouseClicked(int x, int y)
    {
        ctx.cameras.mouseClicked(x, y);
    }

    @Override public void keyReleased(int key) {}
}