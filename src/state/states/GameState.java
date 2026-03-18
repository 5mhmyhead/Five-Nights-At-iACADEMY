package state.states;

import main.GamePanel;
import state.State;
import state.StateManager;
import utilities.FontManager;
import utilities.Utility;

import java.awt.*;

public class GameState extends State
{
    public GameState(StateManager stateManager)
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

        // PLACEHOLDER GAME SCREEN
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);

        g.setColor(new Color(180, 0, 0));
        g.setFont(FontManager.LCD_LARGE);
        Utility.drawCentered(g, "CURRENTLY IN GAME", h / 2 - 20);

        g.setColor(Color.WHITE);
        g.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g, "This is the main office", h / 2 + 20);
    }

    @Override public void keyPressed(int key) {}
    @Override public void mouseMoved(int x, int y) {}
    @Override public void mouseClicked(int x, int y) {}
    @Override public void keyReleased(int key) {}
}