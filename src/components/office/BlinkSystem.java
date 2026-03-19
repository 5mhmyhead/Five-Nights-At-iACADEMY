package components.office;


import main.GamePanel;

import java.awt.*;

// THE PLAYER CAN CLOSE THEIR EYES IN THE OFFICE VIEW, WHENEVER AN ANIMATRONIC MIGHT ATTACK THEM
public class BlinkSystem
{
    private boolean eyesClosed = false;

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

    public void init()
    {

    }

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

    public void draw(Graphics2D g2, boolean showHints)
    {
        if(showHints) drawHoverZone(g2);
        if(eyesClosed || closeTimer > 0) drawBlink(g2);
    }

    private void drawBlink(Graphics2D g2)
    {
        // TEMPORARY BLINK ANIMATION
        float progress;

        if(eyesClosed)
            progress = 1.0f - (float) closeTimer / BLINK_DURATION;
        else
            progress = (float) closeTimer / BLINK_DURATION;

        int eyelidHeight = (int)(progress * GamePanel.HEIGHT / 2);

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, GamePanel.WIDTH, eyelidHeight);

        g2.setPaint(null);
        g2.setColor(Color.BLACK);
        g2.fillRect(0, GamePanel.HEIGHT - eyelidHeight, GamePanel.WIDTH, eyelidHeight);

        g2.setPaint(null);
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

        // DRAWING THE EYE
        int eyeW = (int)(zoneW * 0.10);
        int eyeH = (int)(zoneH * 0.30);

        g2.drawArc(centerX - eyeW / 2, centerY - eyeH, eyeW, eyeH * 2, 0, 180);
        g2.drawArc(centerX - eyeW / 2, centerY - eyeH, eyeW, eyeH * 2, 0, -180);

        // LASHES AND PUPIL
        g2.drawLine(centerX - 16, centerY - eyeH / 2 - 3, centerX - 18, centerY - eyeH / 2 - 6);
        g2.drawLine(centerX - 8, centerY - eyeH / 2 - 5, centerX - 9, centerY - eyeH / 2 - 8);
        g2.drawLine(centerX, centerY - eyeH / 2 - 5, centerX, centerY - eyeH / 2 - 10);
        g2.drawLine(centerX + 8, centerY - eyeH / 2 - 5, centerX + 9, centerY - eyeH / 2 - 8);
        g2.drawLine(centerX + 16, centerY - eyeH / 2 - 3, centerX + 18, centerY - eyeH / 2 - 6);

        int pupilR = eyeH / 2;
        g2.fillOval(centerX - pupilR, centerY - pupilR, pupilR * 2, pupilR * 2);

        g2.setStroke(new BasicStroke(1));
    }

    // FORCES EYES OPEN
    public void forceDown()
    {
        eyesClosed = false;
    }

    public int getCloseTimer() { return closeTimer; }
    public boolean areEyesClosed() { return eyesClosed; }
}
