package components;

import components.cameras.CameraSystem;
import components.office.OfficeView;
import state.StateManager;

import java.awt.*;

// GAME CONTEXT HOLDS ALL THE ELEMENTS OF THE GAME UI
// ENABLES ANIMATRONICS TO ONLY REFER TO THIS CONTEXT WHEN MANAGING THEIR STATES
public class GameContext
{
    public final StateManager stateManager;

    // GAME UI ELEMENTS
    public final CameraSystem cameras;
    public final OfficeView office;
    public final Clock clock;

    public GameContext(StateManager stateManager, CameraSystem cameras, OfficeView office, Clock clock)
    {
        this.stateManager = stateManager;
        this.cameras = cameras;
        this.office = office;
        this.clock = clock;
    }

    public Color getClockColor()
    {
        // CLOCK CHANGES COLOR DEPENDING ON STATE
        return cameras.isMonitorUp() ? new Color(180, 255, 180) : Color.WHITE;
    }

    public boolean getHoverState()
    {
        return !office.isPlayerAtDoor() && !office.isTransitioning();
    }

    public int getNightNumber()
    {
        return stateManager.getNightManager().getNightNumber();
    }
}
