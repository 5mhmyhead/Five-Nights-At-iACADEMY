package state.states;

import components.Clock;
import components.GameContext;
import components.animatronics.Animatronic;
import components.animatronics.Lanze;
import components.cameras.CameraSystem;
import components.nights.NightConfig;
import components.office.OfficeView;
import state.State;
import state.StateManager;

import java.awt.*;

public class GameState extends State
{
    private GameContext ctx;                // A CONTEXT CLASS THAT HOLDS ALL THE MECHANICS OF THE GAME
    private Animatronic[] animatronics;    // THE ANIMATRONICS IN THE GAME

    public GameState(StateManager stateManager)
    {
        super(stateManager);
        init();
    }

    @Override
    public void init()
    {
        ctx = new GameContext(
            new CameraSystem(),
            new OfficeView(),
            new Clock()
        );

        animatronics = new Animatronic[]
        {
            new Lanze(),
        };

        // APPLY AI LEVELS DEPENDING ON THE NIGHT
        NightConfig config = stateManager.getNightManager().getConfig();
        for(int i = 0; i < animatronics.length; i++)
            animatronics[i].setAiLevel(config.aiLevels[i]);
    }

    @Override
    public void update()
    {
        // NIGHT ENDS WHEN CLOCK HITS 6 AM
        if(ctx.clock.isNightOver()) stateManager.setState(StateManager.WIN_STATE);

        // DRAW THE GAME UI
        ctx.cameras.update();
        ctx.office.update();
        ctx.clock.update();

        // UPDATE ANIMATRONICS
        for(Animatronic a : animatronics)
            a.update(ctx);
    }

    @Override
    public void draw(Graphics2D g2)
    {
        ctx.office.draw(g2);
        ctx.cameras.draw(g2, ctx.getHoverState());
        ctx.clock.draw(g2, ctx.getClockColor());
    }

    @Override public void keyPressed(int key)
    {
        ctx.cameras.keyPressed(key);
    }

    @Override
    public void mouseMoved(int x, int y)
    {
        // LET THE PLAYER ACCESS THE CAMERAS ONLY WHEN THEY ARE IN MAIN VIEW
        if(!ctx.office.isPlayerAtDoor()) ctx.cameras.mouseMoved(x, y);
        if(!ctx.cameras.isMonitorUp()) ctx.office.mouseMoved(x, y);
    }

    @Override public void mouseClicked(int x, int y)
    {
        ctx.cameras.mouseClicked(x, y);
    }

    @Override public void keyReleased(int key) {}
}