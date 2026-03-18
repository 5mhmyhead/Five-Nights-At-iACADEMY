package state.states;

import components.OfficeView;
import main.GamePanel;
import state.State;
import state.StateManager;
import utilities.FontManager;
import utilities.Utility;

import java.awt.*;

public class GameState extends State
{
    private OfficeView office;

    public GameState(StateManager stateManager)
    {
        super(stateManager);
        init();
    }

    @Override
    public void init()
    {
        office = new OfficeView();
    }

    @Override
    public void update()
    {
        office.update();
    }

    @Override
    public void draw(Graphics2D g2)
    {
        office.draw(g2);
    }

    @Override public void keyPressed(int key) {}

    @Override
    public void mouseMoved(int x, int y)
    {
        office.mouseMoved(x, y);
    }

    @Override public void mouseClicked(int x, int y) {}
    @Override public void keyReleased(int key) {}
}