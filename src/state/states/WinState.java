package state.states;

import main.GamePanel;
import state.State;
import state.StateManager;
import utilities.FontManager;
import utilities.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;

public class WinState extends State
{
    public WinState(StateManager stateManager)
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

        // PLACEHOLDER WIN SCREEN
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);

        g.setColor(Color.YELLOW);
        g.setFont(FontManager.LCD_LARGE);
        Utility.drawCentered(g, "6 AM - YOU SURVIVED!", h / 2 - 20);

        g.setColor(Color.WHITE);
        g.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g, "Press ENTER to continue", h / 2 + 20);
    }

    @Override
    public void keyPressed(int key)
    {
        if(key == KeyEvent.VK_ENTER)
            stateManager.setState(StateManager.TITLE_STATE);
    }

    @Override
    public void keyReleased(int key) {}

    @Override public void mouseMoved(int x, int y) {}
    @Override public void mouseClicked(int x, int y) {}
}