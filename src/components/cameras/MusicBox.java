package components.cameras;

import utilities.FontManager;

import java.awt.*;

public class MusicBox
{
    private boolean held = false;

    private static final int X = 830;
    private static final int Y = 645;
    private static final int W = 200;
    private static final int H = 40;

    private static final Color COLOR_ACTIVE = new Color(0, 100, 180, 180);
    private static final Color COLOR_IDLE = new Color(0, 60, 120, 180);
    private static final Color COLOR_TEXT = new Color(180, 220, 255);

    public void mousePressed(int mouseX, int mouseY)
    {
        if(mouseX >= X && mouseX <= X + W && mouseY >= Y && mouseY <= Y + H)
            held = true;
    }

    public void mouseReleased() { held = false; }

    public void draw(Graphics2D g2)
    {
        g2.setColor(held ? COLOR_ACTIVE : COLOR_IDLE);
        g2.fillRoundRect(X, Y, W, H, 8, 8);

        g2.setColor(COLOR_TEXT);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(X, Y, W, H, 8, 8);
        g2.setStroke(new BasicStroke(1));

        g2.setFont(FontManager.LCD_SMALL);
        String label = held ? "PLAYING..." : "WIND MUSIC BOX";
        int labelX = X + (W - g2.getFontMetrics().stringWidth(label)) / 2;
        int labelY = Y + H / 2 + 5;
        g2.drawString(label, labelX, labelY);
    }

    public boolean isHeld() { return held; }
}
