package state;

import state.states.GameState;
import state.states.LoseState;
import state.states.TitleState;
import state.states.WinState;

import java.awt.*;
import java.awt.event.KeyEvent;

// HANDLES SWITCHING BETWEEN AND MOVING THROUGH DIFFERENT STATES DURING GAMEPLAY
public class StateManager
{
    // GAME STATES
    private static final int NUMBER_OF_STATES = 4;

    public static final int TITLE_STATE = 0;
    public static final int GAME_STATE = 1;
    public static final int LOSE_STATE = 2;
    public static final int WIN_STATE = 3;

    private final State[] states;   // ARRAY HOLDING THE STATES
    private int currentState;       // ID REPRESENTING THE CURRENT STATE

    public StateManager()
    {
        states = new State[NUMBER_OF_STATES];
        currentState = TITLE_STATE;
        loadState(currentState);
    }

    // UPDATES THE CURRENT STATE
    public void update()
    {
        if(states[currentState] != null)
            states[currentState].update();
    }

    // DRAWS THE CURRENT STATE
    public void draw(Graphics2D g2d)
    {
        if(states[currentState] != null)
            states[currentState].draw(g2d);
    }

    // SET STATE DEPENDING ON INDEX GIVEN
    public void setState(int state)
    {
        unloadState(currentState);
        currentState = state;
        loadState(currentState);
    }

    // DEALLOCATES THE REMOVED STATE
    private void unloadState(int state) { states[state] = null; }

    // LOADS STATE DEPENDING ON INDEX
    private void loadState(int state)
    {
        if(state == TITLE_STATE) states[state] = new TitleState(this);
        if(state == GAME_STATE) states[state] = new GameState(this);
        if(state == LOSE_STATE) states[state] = new LoseState(this);
        if(state == WIN_STATE) states[state] = new WinState(this);
    }

    // SENDS THE KEY PRESS TO THE CURRENT STATE
    public void keyPressed(int key)
    {
        // GLOBAL DEBUG KEYS TO MOVE BETWEEN STATES
        if(key == KeyEvent.VK_F1) { setState(TITLE_STATE); return; }
        if(key == KeyEvent.VK_F2) { setState(GAME_STATE);  return; }
        if(key == KeyEvent.VK_F3) { setState(LOSE_STATE);  return; }
        if(key == KeyEvent.VK_F4) { setState(WIN_STATE);   return; }

        if(states[currentState] != null) states[currentState].keyPressed(key);
    }

    // PASSES KEYBOARD ACTIONS TO ACTIVE STATE
    public void keyReleased(int key) { if(states[currentState] != null) states[currentState].keyReleased(key); }

    // PASSES MOUSE ACTIONS TO ACTIVE STATE
    public void mouseMoved(int x, int y) { if(states[currentState] != null) states[currentState].mouseMoved(x, y); }
    public void mouseClicked(int x, int y) { if(states[currentState] != null) states[currentState].mouseClicked(x, y); }
}