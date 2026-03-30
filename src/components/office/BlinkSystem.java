package components.office;


import main.GamePanel;

import java.awt.*;

// THE PLAYER CAN CLOSE THEIR EYES IN THE OFFICE VIEW, WHENEVER AN ANIMATRONIC MIGHT ATTACK THEM
public class BlinkSystem
{
    private boolean eyesClosed = false;
    private boolean keyBlinking = false;

    // BLINK ANIMATION
    private static final int BLINK_DURATION = 3;
    private int closeTimer = 0;

    // HOVER ZONE DIMENSIONS
    private static final int HOVER_ZONE_Y = GamePanel.HEIGHT - 75;
    private static final int HOVER_ZONE_X_MIN  = (int)(GamePanel.WIDTH * 0.55);
    private static final int HOVER_ZONE_X_MAX  = (int)(GamePanel.WIDTH * 0.95);

    public BlinkSystem()
    {
        init();
    }
    public void init() {}

    public void update()
    {
        if(closeTimer > 0) closeTimer--;
    }

    public void mouseMoved(int mouseX, int mouseY)
    {
        boolean inHoverZone = mouseX >= HOVER_ZONE_X_MIN
                && mouseX <= HOVER_ZONE_X_MAX
                && mouseY >= HOVER_ZONE_Y;

        if(inHoverZone && !eyesClosed)
        {
            eyesClosed = true;
            closeTimer = BLINK_DURATION;
        }
        else if(!inHoverZone && eyesClosed)
        {
            eyesClosed = false;
            closeTimer = BLINK_DURATION;
        }
    }

    public void draw(Graphics2D g2, boolean showHints, boolean isTransitioning)
    {
        if(showHints && !isTransitioning) drawHoverZone(g2);
        if(eyesClosed || closeTimer > 0) drawBlink(g2);
    }

    private void drawBlink(Graphics2D g2)
    {
        float progress;

        if(eyesClosed)
            progress = 1.0f - (float) closeTimer / BLINK_DURATION;
        else
            progress = (float) closeTimer / BLINK_DURATION;

        int eyelidHeight = (int)(progress * GamePanel.HEIGHT / 2 + 100);
        int featherSize  = Math.min(60, eyelidHeight); // CANT FEATHER MORE THAN EYELID HEIGHT

        // TOP EYELID
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, GamePanel.WIDTH, Math.max(0, eyelidHeight - featherSize));

        // TOP FEATHER
        for(int i = 0; i < featherSize; i++)
        {
            float alpha = (float)(featherSize - i) / featherSize;
            g2.setColor(new Color(0, 0, 0, (int)(alpha * 255)));
            g2.fillRect(0, eyelidHeight - featherSize + i, GamePanel.WIDTH, 1);
        }

        // BOTTOM EYELID
        g2.setColor(Color.BLACK);
        g2.fillRect(0, GamePanel.HEIGHT - Math.max(0, eyelidHeight - featherSize),
                GamePanel.WIDTH, Math.max(0, eyelidHeight - featherSize));

        // BOTTOM FEATHER
        for(int i = 0; i < featherSize; i++)
        {
            float alpha = (float) i / featherSize;
            g2.setColor(new Color(0, 0, 0, (int)(alpha * 255)));
            g2.fillRect(0, GamePanel.HEIGHT - eyelidHeight + i, GamePanel.WIDTH, 1);
        }
    }

    private void drawHoverZone(Graphics2D g2)
    {
        int zoneW = HOVER_ZONE_X_MAX - HOVER_ZONE_X_MIN;
        int zoneH = GamePanel.HEIGHT - HOVER_ZONE_Y - 35;
        int centerX = HOVER_ZONE_X_MIN + zoneW / 2;
        int centerY = HOVER_ZONE_Y + zoneH / 2;

        // FILL
        g2.setColor(new Color(255, 225, 225, 31));
        g2.fillRoundRect(HOVER_ZONE_X_MIN, HOVER_ZONE_Y, zoneW, zoneH, 10, 10);

        // BORDER
        g2.setColor(new Color(255, 225, 225, 140));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(HOVER_ZONE_X_MIN, HOVER_ZONE_Y, zoneW, zoneH, 10, 10);

        // CLOSED EYE
        int eyeW = (int)(zoneW * 0.10);
        int eyeH = (int)(zoneH * 0.50);
        int iconOffsetY = 7;

        g2.setStroke(new BasicStroke(2));
        g2.drawArc(centerX - eyeW / 2, centerY - eyeH / 2 - iconOffsetY, eyeW, eyeH, 180, 180);

        // EYELASHES
        int lashLength = 7;

        g2.drawLine(centerX - eyeW / 2 + 5, centerY + eyeH / 4 - iconOffsetY, centerX - eyeW / 2, centerY + eyeH / 4 + lashLength - iconOffsetY);
        g2.drawLine(centerX - eyeW / 4, centerY + eyeH / 2 - iconOffsetY, centerX - eyeW / 4 - 2, centerY + eyeH / 2 + lashLength - iconOffsetY);
        g2.drawLine(centerX, centerY + eyeH / 2 - iconOffsetY, centerX, centerY + eyeH / 2 + lashLength + 2 - iconOffsetY);
        g2.drawLine(centerX + eyeW / 4, centerY + eyeH / 2 - iconOffsetY, centerX + eyeW / 4 + 2, centerY + eyeH / 2 + lashLength - iconOffsetY);
        g2.drawLine(centerX + eyeW / 2 - 5, centerY + eyeH / 4 - iconOffsetY, centerX + eyeW / 2, centerY + eyeH / 4 + lashLength - iconOffsetY);

        g2.setStroke(new BasicStroke(1));
    }

    public void setKeyBlink(boolean blink)
    {
        if (blink && !eyesClosed)
        {
            eyesClosed = true;
            closeTimer = BLINK_DURATION;
        }
        else if (!blink && eyesClosed && keyBlinking)
        {
            eyesClosed = false;
            closeTimer = BLINK_DURATION;
        }

        keyBlinking = blink;
    }
    // FORCES EYES OPEN
    public void forceOpen()
    {
        if(eyesClosed)
        {
            eyesClosed = false;
            closeTimer = BLINK_DURATION;
        }
    }

    public int getCloseTimer() { return closeTimer; }
    public boolean areEyesClosed() { return eyesClosed; }
}
