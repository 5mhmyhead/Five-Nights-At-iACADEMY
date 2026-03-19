package state.states;

import main.GamePanel;
import state.State;
import state.StateManager;
import utilities.FontManager;
import utilities.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;

public class TitleState extends State
{
    public TitleState(StateManager stateManager)
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

        // PLACEHOLDER TITLE SCREEN
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, w, h);

        g2.setColor(Color.RED);
        g2.setFont(FontManager.LCD_LARGE);
        Utility.drawCentered(g2, "FIVE NIGHTS AT iACADEMY", h / 2 - 20);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "Press ENTER to start", h / 2 + 20);

        // SHOW NIGHT NUMBER
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "Night " + stateManager.getNightManager().getNightNumber(), h - 40);
    }

    @Override
    public void keyPressed(int key)
    {
        if(key == KeyEvent.VK_ENTER)
            stateManager.setState(StateManager.INTRO_STATE);
    }

    @Override public void keyReleased(int key) {}

    @Override public void mouseMoved(int x, int y) {}
    @Override public void mouseClicked(int x, int y) {}
    @Override public void mouseReleased(int x, int y) {}
}
