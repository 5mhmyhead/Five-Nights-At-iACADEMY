package components;

import components.animatronics.Animatronic;
import components.cameras.CameraSystem;
import components.office.BlinkSystem;
import components.office.OfficeView;
import state.StateManager;

import java.awt.*;

// GAME CONTEXT HOLDS ALL THE ELEMENTS OF THE GAME UI
// ENABLES ANIMATRONICS TO ONLY REFER TO THIS CONTEXT WHEN MANAGING THEIR STATES
public class GameContext
{
    public final StateManager stateManager;
    public Animatronic[] animatronics;

    // GAME UI ELEMENTS
    public final CameraSystem cameras;
    public final OfficeView office;
    public final BlinkSystem blink;
    public final Clock clock;

    public GameContext(StateManager stateManager, Animatronic[] animatronics, CameraSystem cameras, OfficeView office, BlinkSystem blink, Clock clock)
    {
        this.stateManager = stateManager;
        this.animatronics = animatronics;
        this.cameras = cameras;
        this.office = office;
        this.blink = blink;
        this.clock = clock;
    }

    public Color getClockColor()
    {
        // CLOCK CHANGES COLOR DEPENDING ON STATE
        return isInCameras() ? cameras.getTextColor() : Color.WHITE;
    }

    // FUNCTIONS THAT RETURN SPECIFIC STATES OF THE PLAYER
    public boolean isInMainView()
    {
        return !office.isPlayerAtDoor()
            && !office.isTransitioning()
            && !blink.areEyesClosed();
    }

    public boolean isInDoorView()
    {
        return office.isPlayerAtDoor()
            && !office.isTransitioning()
            && !blink.areEyesClosed();
    }

    public boolean isInCameras()
    {
        return cameras.isMonitorUp()
            && !office.isTransitioning()
            && !blink.areEyesClosed();
    }

    public boolean wasShockPressed()
    {
        return cameras.getShockButton().wasShockPressed();
    }

    public int getNightNumber()
    {
        return stateManager.getNightManager().getNightNumber();
    }
}
