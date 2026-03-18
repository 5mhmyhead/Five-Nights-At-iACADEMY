package state.states;

import main.GamePanel;
import state.State;
import state.StateManager;
import utilities.FontManager;
import utilities.Utility;

import java.awt.*;

// THE INTRO SCREEN THAT POPS UP BEFORE EVERY NIGHT
public class IntroState extends State
{
    private static final int DISPLAY_DURATION = 90;
    private int timer = 0;

    public IntroState(StateManager stateManager)
    {
        super(stateManager);
        init();
    }

    @Override
    public void init()
    {
        timer = 0;
    }

    @Override
    public void update()
    {
        timer++;
        if(timer >= DISPLAY_DURATION)
            stateManager.setState(StateManager.GAME_STATE);
    }

    @Override
    public void draw(Graphics2D g2)
    {
        int w = GamePanel.WIDTH;
        int h = GamePanel.HEIGHT;

        // DRAW INTRO SCREEN
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, w, h);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_LARGE);
        Utility.drawCentered(g2, getNightName(), h / 2 - 40);

        g2.setFont(FontManager.LCD_CLOCK);
        Utility.drawCentered(g2, "12:00 AM", h / 2 + 40);
    }

    private String getNightName()
    {
        return "Night " + stateManager.getNightManager().getNightNumber();
    }

    @Override public void keyPressed(int key) {}
    @Override public void keyReleased(int key) {}
    @Override public void mouseMoved(int x, int y) {}
    @Override public void mouseClicked(int x, int y) {}
}
