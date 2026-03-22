package state.states;

import main.GamePanel;
import state.State;
import state.StateManager;
import utilities.FontManager;
import utilities.SaveManager;
import utilities.SoundManager;
import utilities.Utility;

import java.awt.*;

public class WinState extends State
{
    private int colorTimer = 0;

    public WinState(StateManager stateManager)
    {
        super(stateManager);
        init();
    }

    @Override
    public void init()
    {
        Utility.setScanlineCount(3);
    }

    @Override
    public void update()
    {
        colorTimer++;
    }

    @Override
    public void draw(Graphics2D g2)
    {
        int w = GamePanel.WIDTH;
        int h = GamePanel.HEIGHT;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, w, h);

        g2.setColor(getCyclingColor());
        g2.setFont(FontManager.LCD_TITLE);
        Utility.drawCentered(g2, "6 AM", h / 2 - 30);
        g2.setFont(FontManager.LCD_CLOCK);
        Utility.drawCentered(g2, "YOU SURVIVED THE NIGHT!", h / 2 + 20);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "Press ENTER to continue", h / 2 + 50);
    }

    private Color getCyclingColor()
    {
        float hue = (colorTimer * 0.95f) % 1.0f;
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }

    @Override
    public void onEnter()
    {
        SoundManager.SIX_AM.play();
    }

    @Override
    public void keyPressed(int key)
    {
        if(stateManager.getNightManager().isFinalNight())
            stateManager.setState(StateManager.TITLE_STATE); // OR CREDITS LATER
        else
        {
            stateManager.getNightManager().advanceNight();
            SaveManager.save(stateManager.getNightManager().getNightNumber());
            stateManager.setState(StateManager.INTRO_STATE);
        }
    }

    @Override
    public void keyReleased(int key) {}

    @Override public void mouseMoved(int x, int y) {}
    @Override public void mouseClicked(int x, int y) {}
}