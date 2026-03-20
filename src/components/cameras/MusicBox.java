package components.cameras;

import utilities.FontManager;
import utilities.SoundManager;

import java.awt.*;

public class MusicBox
{
    private boolean clicked = false;
    private boolean winding = false;
    private boolean spedUp = false;

    private static final int X = 830;
    private static final int Y = 645;
    private static final int W = 200;
    private static final int H = 40;

    // TODO: TUNE COLORS OF MUSIC BOX BUTTON
    private static final Color COLOR_ACTIVE = new Color(0, 100, 180, 180);
    private static final Color COLOR_IDLE = new Color(0, 60, 120, 180);
    private static final Color COLOR_TEXT = new Color(180, 220, 255);

    public void update()
    {
        clicked = false;
    }

    public void mouseClicked(int mouseX, int mouseY)
    {
        // WHILE WINDING, PLAYER CANNOT CLICK
        if(winding) return;

        if(mouseX >= X && mouseX <= X + W && mouseY >= Y && mouseY <= Y + H)
        {
            winding = true;
            clicked = true;
        }
    }

    public void draw(Graphics2D g2)
    {
        g2.setColor(clicked ? COLOR_ACTIVE : COLOR_IDLE);
        g2.fillRoundRect(X, Y, W, H, 8, 8);

        g2.setColor(COLOR_TEXT);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(X, Y, W, H, 8, 8);
        g2.setStroke(new BasicStroke(1));

        g2.setFont(FontManager.LCD_SMALL);
        String label = winding ? "WINDING..." : "WIND MUSIC BOX";
        int labelX = X + (W - g2.getFontMetrics().stringWidth(label)) / 2;
        int labelY = Y + H / 2 + 5;
        g2.drawString(label, labelX, labelY);
    }

    public void setSpedUp(boolean boosted)
    {
        if(this.spedUp == boosted) return;

        this.spedUp = boosted;

        // SWAP TRACKS
        SoundManager.MUSIC_BOX.stop();
        SoundManager.MUSIC_BOX_SPED_UP.stop();

        if(boosted) SoundManager.MUSIC_BOX_SPED_UP.loop();
        else SoundManager.MUSIC_BOX.loop();
    }

    public void startWinding() { winding = true; }
    public void stopWinding()  { winding = false; }

    public boolean isWinding()   { return winding; }
    public boolean wasClicked()  { return clicked; }
}
