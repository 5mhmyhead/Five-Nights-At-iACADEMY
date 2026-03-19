package state.states;

import main.GamePanel;
import state.State;
import state.StateManager;
import utilities.FontManager;
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
    public void draw(Graphics2D g2)
    {
        int w = GamePanel.WIDTH;
        int h = GamePanel.HEIGHT;

        // PLACEHOLDER LOSE SCREEN
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, w, h);

        g2.setColor(Color.RED);
        g2.setFont(FontManager.LCD_LARGE);
        Utility.drawCentered(g2, "GAME OVER", h / 2 - 20);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "Press R to try again", h / 2 + 20);
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
    @Override public void mouseReleased(int x, int y) {}
}