package state.states;

import main.GamePanel;
import state.State;
import state.StateManager;
import utilities.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;

public class LoseState extends State
{
    public LoseState(StateManager stateManager)
    {
        super(stateManager);
        init();
    }

    @Override
    public void init() {}

    @Override
    public void update() {}

    @Override
    public void draw(Graphics2D g)
    {
        int w = GamePanel.WIDTH;
        int h = GamePanel.HEIGHT;

        // PLACEHOLDER LOSE SCREEN
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);

        g.setColor(new Color(200, 0, 0));
        g.setFont(new Font("Serif", Font.BOLD, 28));
        Utility.drawCentered(g, "GAME OVER", h / 2 - 20);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Serif", Font.PLAIN, 12));
        Utility.drawCentered(g, "Press R to try again", h / 2 + 20);
    }

    @Override
    public void keyPressed(int key)
    {
        if(key == KeyEvent.VK_R)
            stateManager.setState(StateManager.TITLE_STATE);
    }

    @Override
    public void keyReleased(int key) {}

    @Override public void mouseMoved(int x, int y) {}
    @Override public void mouseClicked(int x, int y) {}
}