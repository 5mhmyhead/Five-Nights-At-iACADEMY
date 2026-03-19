package state.states;

import main.GamePanel;
import state.State;
import state.StateManager;
import utilities.FontManager;
import utilities.Utility;

import java.awt.*;

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
    public void draw(Graphics2D g2)
    {
        int w = GamePanel.WIDTH;
        int h = GamePanel.HEIGHT;

        // PLACEHOLDER WIN SCREEN
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, w, h);

        g2.setColor(Color.YELLOW);
        g2.setFont(FontManager.LCD_LARGE);
        Utility.drawCentered(g2, "6 AM - YOU SURVIVED!", h / 2 - 20);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "Press ENTER to continue", h / 2 + 20);
    }

    @Override
    public void keyPressed(int key)
    {
        if(stateManager.getNightManager().isFinalNight())
            stateManager.setState(StateManager.TITLE_STATE); // OR CREDITS LATER
        else
        {
            stateManager.getNightManager().advanceNight();
            stateManager.setState(StateManager.INTRO_STATE);
        }
    }

    @Override
    public void keyReleased(int key) {}

    @Override public void mouseMoved(int x, int y) {}
    @Override public void mouseClicked(int x, int y) {}
    @Override public void mouseReleased(int x, int y) {}
}