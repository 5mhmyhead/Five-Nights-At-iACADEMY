package components.cameras;

import utilities.FontManager;
import utilities.SoundManager;

import java.awt.*;

// ADDS A SHOCK BUTTON IN THE CAMERA UI
// USED TO DETER DAVE FROM THE OFFICE
public class ControlledShock
{
    private static final int MAX_CHARGES = 3;
    private int charges = MAX_CHARGES;
    private boolean shockPressed = false;

    // COOLDOWN AFTER SHOCKING
    private static final int COOLDOWN_DURATION = 90;
    private int cooldownTimer = 0;

    // SHOCK BUTTON LAYOUT
    private static final int X = 1040;
    private static final int Y = 595;
    private static final int W = 200;
    private static final int H = 40;

    private static final Color COLOR_ACTIVE  = new Color(180, 140, 0, 180);
    private static final Color COLOR_EMPTY   = new Color(60,  60,  60, 180);
    private static final Color COLOR_TEXT    = new Color(255, 240, 150);

    public void update()
    {
        shockPressed = false;
        if(cooldownTimer > 0) cooldownTimer--;
    }

    public void mouseClicked(int mouseX, int mouseY)
    {
        if(charges <= 0) return;
        if(cooldownTimer > 0) return;

        if(mouseX >= X && mouseX <= X + W && mouseY >= Y && mouseY <= Y + H)
        {
            SoundManager.SHOCK.setVolume(0.5);
            SoundManager.SHOCK.play();
            shockPressed  = true;
            charges--;
            cooldownTimer = COOLDOWN_DURATION;
        }
    }

    // ADDS A CHARGE WHENEVER YOU REBOOT THE CAMERAS
    public void addCharge()
    {
        if(charges < MAX_CHARGES) charges++;
    }

    public void draw(Graphics2D g2)
    {
        boolean canShock = charges > 0 && cooldownTimer <= 0;

        g2.setColor(canShock ? COLOR_ACTIVE : COLOR_EMPTY);
        g2.fillRoundRect(X, Y, W, H, 8, 8);

        g2.setColor(COLOR_TEXT);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(X, Y, W, H, 8, 8);
        g2.setStroke(new BasicStroke(1));

        g2.setFont(FontManager.LCD_SMALL);

        String label;
        if(cooldownTimer > 0)
            label = "RECALIBRATING...";
        else
            label = "SHOCK [" + charges + "/" + MAX_CHARGES + "]";

        int labelX = X + (W - g2.getFontMetrics().stringWidth(label)) / 2;
        int labelY = Y + H / 2 + 5;
        g2.drawString(label, labelX, labelY);
    }

    public boolean wasShockPressed() { return shockPressed; }
    public int getCharges() { return charges; }
}
