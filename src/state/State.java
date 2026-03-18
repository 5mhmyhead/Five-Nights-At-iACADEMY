package state;

import java.awt.*;

// ABSTRACT CLASS THAT THE DIFFERENT STATES WILL EXTEND FROM
public abstract class State
{
    protected StateManager stateManager;
    public State(StateManager stateManager) { this.stateManager = stateManager; }

    public abstract void init();
    public abstract void update();
    public abstract void draw(Graphics2D g2);

    // ABSTRACT CLASSES FOR KEYBOARD AND MOUSE INPUT
    public abstract void keyPressed(int key);
    public abstract void keyReleased(int key);

    public abstract void mouseMoved(int x, int y);
    public abstract void mouseClicked(int x, int y);
}