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
    private String tip1 = "";
    private String tip2 = "";

    private static final int STATIC_DURATION = 20;
    private int staticTimer = STATIC_DURATION;

    private int scanlineUpdateTimer = 0;

    public LoseState(StateManager stateManager)
    {
        super(stateManager);
        init();
    }

    @Override
    public void init()
    {
        staticTimer = STATIC_DURATION;

        String killer = stateManager.getKiller();
        tip1 = getTip1For(killer);
        tip2 = getTip2For(killer);
    }

    @Override
    public void update()
    {
        if(staticTimer > 0) staticTimer--;

        if(scanlineUpdateTimer % 5 == 0)
            Utility.updateScanlines();
        scanlineUpdateTimer++;
    }

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
        Utility.drawCentered(g2, "Press R to try again", h / 2);

        g2.setColor(Color.DARK_GRAY);
        Utility.drawCentered(g2, tip1, h / 2 + 50);
        Utility.drawCentered(g2, tip2, h / 2 + 70);

        Utility.drawStatic(g2, staticTimer, STATIC_DURATION, new Color(255, 255, 255));
        Utility.drawAmbientScanlines(g2, new Color(255, 0, 0, 26), 2);
        Utility.drawCRTScanlines(g2, 4, 2, 100);
    }

    private String getTip1For(String killer)
    {
        return switch(killer)
        {
            case "Dave" -> "Dave won't move if you keep looking at him. ";
            case "Earl" -> "Earl has a predictable path, use it to your advantage. ";
            case "Tyrone" -> "Tyrone moves fast, but visits a lot of cameras.";
            case "Cristian" -> "Cristian doesn't like being watched, but sometimes he wishes for attention.";
            case "Lanze" -> "Winding Lanze's music box too early will anger Earl and Tyrone.";
            default -> "Waiting for something to happen?";
        };
    }

    private String getTip2For(String killer)
    {
        return switch(killer)
        {
            case "Dave" -> "You only have a few shocks after all.";
            case "Earl" -> "Blink when he reaches your door.";
            case "Tyrone" -> " Blink when he reaches your main office.";
            case "Cristian" -> "You'll know when he wants to be watched.";
            case "Lanze" -> "Don't wind too often.";
            default -> "";
        };
    }

    @Override
    public void onEnter() {}

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