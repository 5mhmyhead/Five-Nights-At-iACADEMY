package state;

import components.nights.NightManager;
import state.states.*;

import java.awt.*;
import java.awt.event.KeyEvent;

// HANDLES SWITCHING BETWEEN AND MOVING THROUGH DIFFERENT STATES DURING GAMEPLAY
public class StateManager
{
    // MANAGES THE FIVE NIGHTS AND ITS AI LEVELS OF THE ANIMATRONICS
    private final NightManager nightManager = new NightManager();

    // GAME STATES
    private static final int NUMBER_OF_STATES = 5;

    public static final int TITLE_STATE = 0;
    public static final int INTRO_STATE = 1;
    public static final int GAME_STATE = 2;
    public static final int LOSE_STATE = 3;
    public static final int WIN_STATE = 4;

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
    public void draw(Graphics2D g2)
    {
        if(states[currentState] != null)
            states[currentState].draw(g2);
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
        if(state == INTRO_STATE) states[state] = new IntroState(this);
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
    public void mouseReleased(int x, int y) { if(states[currentState] != null) states[currentState].mouseReleased(x, y); }

    public NightManager getNightManager() { return nightManager; }
}