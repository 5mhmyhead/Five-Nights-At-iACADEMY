package components;

import main.GamePanel;
import utilities.FontManager;

import java.awt.*;

public class Clock
{
    // 45 SECONDS TIMES 30 FPS EQUALS 1500 FRAMES PER HOUR
    // 4:30 PER NIGHT
    private static final int FRAMES_PER_HOUR = 1350;
    private static final String[] HOURS = { "12 AM", "1 AM", "2 AM", "3 AM", "4 AM", "5 AM" };

    private int frameTick;
    private int currentHour;
    private boolean nightOver;

    public Clock()
    {
        frameTick = 0;
        currentHour = 0;
        nightOver = false;
    }

    public void update()
    {
        frameTick++;

        if(frameTick == FRAMES_PER_HOUR)
        {
            frameTick = 0;
            currentHour++;

            if(currentHour == HOURS.length)
                nightOver = true;
        }
    }

    public void draw(Graphics2D g2, Color color, int nightNum)
    {
        if(nightOver) return;

        g2.setColor(color);
        g2.setFont(FontManager.LCD_CLOCK);

        // ENSURE THAT TEXTS STAY RIGHT ALIGNED
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(HOURS[currentHour]);
        int x = GamePanel.WIDTH - textWidth - 40;

        g2.drawString(HOURS[currentHour], x, 70);

        // DRAW NIGHT NUMBER
        g2.setFont(FontManager.LCD_MEDIUM);
        g2.drawString("Night " + nightNum, GamePanel.WIDTH - 130, 105);
    }

    public boolean isNightOver()
    {
        return nightOver;
    }
}
