package components.animatronics;

import components.GameContext;

import java.awt.*;

public abstract class Animatronic
{
    // STATES OF THE ANIMATRONIC
    public enum Location { CAMERA, MAIN, VENT }

    protected int currentCamera;
    protected Location location = Location.CAMERA;
    protected int aiLevel = 0;

    // THE ANIMATRONIC LOOKS AT GAME CONTEXT FOR SPECIFIC PLAYER ACTIONS OR STATES
    public abstract void update(GameContext ctx);

    // DRAW METHODS THAT OVERRIDE ONLY THE ONES NEEDED
    public void drawOnCamera(Graphics2D g2, int swayX) {}
    public void drawOnOffice(Graphics2D g2) {}
    public void drawOnDoor(Graphics2D g2)   {}

    // AI LEVEL IS BASED ON THE ORIGINAL GAME
    // IF ROLL IS LESS THAN OR EQUAL TO AI LEVEL, THEN THE ANIMATRONIC MOVES
    protected boolean shouldMove()
    {
        return (int)(Math.random() * 20) + 1 <= aiLevel;
    }

    public void setAiLevel(int level)
    {
        aiLevel = level;
    }

    public int getCurrentCamera() { return currentCamera; }
    public Location getLocation() { return location; }
}