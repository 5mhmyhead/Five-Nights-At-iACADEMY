package components.office;


import main.GamePanel;

import java.awt.*;

// THE PLAYER CAN CLOSE THEIR EYES IN THE OFFICE VIEW, WHENEVER AN ANIMATRONIC MIGHT ATTACK THEM
public class BlinkSystem
{
    private boolean eyesClosed = false;
    private boolean wasInHoverZone = false;

    // BLINK ANIMATION
    private static final int BLINK_DURATION = 16;
    private boolean transitioning = false;
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
        if(closeTimer == 0) transitioning = false;
    }

    public void mouseMoved(int mouseX, int mouseY)
    {
        boolean inHoverZone = mouseX >= HOVER_ZONE_X_MIN
                && mouseX <= HOVER_ZONE_X_MAX
                && mouseY >= HOVER_ZONE_Y;

        if(inHoverZone && !wasInHoverZone && !transitioning)
        {
            eyesClosed = !eyesClosed;
            closeTimer = BLINK_DURATION;
            transitioning = true;
        }

        wasInHoverZone = inHoverZone;
    }

    public void draw(Graphics2D g2, boolean showHints)
    {
        if(eyesClosed || closeTimer > 0) drawBlink(g2);
        if(showHints) drawHoverZone(g2);
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
        int arrowW = (int)(zoneW * 0.20);
        int arrowH = 10;
        int startX = HOVER_ZONE_X_MIN + (zoneW - arrowW) / 2;
        int midX = HOVER_ZONE_X_MIN + zoneW / 2;
        int endX = startX + arrowW;

        // FILL
        g2.setColor(new Color(255, 255, 255, 30));
        g2.fillRoundRect(HOVER_ZONE_X_MIN, HOVER_ZONE_Y, zoneW, zoneH, 10, 10);

        // BORDER AND ARROWS
        g2.setColor(new Color(255, 255, 255, 140));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(HOVER_ZONE_X_MIN, HOVER_ZONE_Y, zoneW, zoneH, 10, 10);

        int topY = HOVER_ZONE_Y + 10;
        int botY = topY + arrowH + 2;

        g2.drawPolyline(new int[]{ startX, midX, endX }, new int[]{ topY, topY + arrowH, topY }, 3);
        g2.drawPolyline(new int[]{ startX, midX, endX }, new int[]{ botY, botY + arrowH, botY }, 3);

        g2.setStroke(new BasicStroke(1));
    }

    // FORCES EYES OPEN
    public void forceDown()
    {
        eyesClosed = false;
        wasInHoverZone = false;
    }

    public boolean areEyesClosed() { return eyesClosed; }
    public boolean isTransitioning() { return transitioning; }
}
