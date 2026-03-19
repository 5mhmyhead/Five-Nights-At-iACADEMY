package components.office;

import main.GamePanel;
import utilities.Utility;

import java.awt.*;
import java.awt.image.BufferedImage;

public class OfficeView
{
    // TWO VIEWS IN THE OFFICE
    private final MainView mainView;
    private final DoorView doorView;

    // IF PLAYER IS AT MAIN VIEW
    private boolean playerAtDoor = false;
    private boolean wasInHoverZone = false;

    // LEFT AND RIGHT HOVER ZONES THAT SWITCHES OFFICE VIEWS
    private static final int MAIN_HOVER_X_MIN = 0;
    private static final int MAIN_HOVER_X_MAX = 60;

    private static final int DOOR_HOVER_X_MIN = GamePanel.WIDTH - 60;
    private static final int DOOR_HOVER_X_MAX = GamePanel.WIDTH;

    private static final int HOVER_ZONE_Y_MIN = (int)(GamePanel.HEIGHT * 0.20);
    private static final int HOVER_ZONE_Y_MAX = (int)(GamePanel.HEIGHT * 0.80);

    // TRANSITION BETWEEN THE MAIN AND MAIN VIEWS
    private static final int TRANSITION_LENGTH = 6;
    private final BufferedImage[] transitionFrames;

    private int transitionIndex = 0;
    private boolean transitioning = false;
    private boolean transitionForward = true;

    private static final int FRAMES_PER_IMAGE = 1;  // VARIABLE TO TUNE TRANSITION ANIMATION
    private int frameHoldCounter = 0;

    public OfficeView()
    {
        mainView = new MainView();
        doorView = new DoorView();

        // LOAD TRANSITION FRAMES
        transitionFrames = new BufferedImage[TRANSITION_LENGTH];
        for(int i = 0; i < TRANSITION_LENGTH; i++)
            transitionFrames[i] = Utility.applyMotionBlur(Utility.loadImage("/office/transitions/transition" + (i + 1) + ".png"));
    }

    public void update()
    {
        if(!transitioning) return;

        frameHoldCounter++;
        if(frameHoldCounter < FRAMES_PER_IMAGE) return;
        frameHoldCounter = 0;

        if(transitionForward)
        {
            transitionIndex++;
            if(transitionIndex >= TRANSITION_LENGTH)
            {
                transitioning = false;
                playerAtDoor = true;
                transitionIndex = 0;
            }
        }
        else
        {
            transitionIndex--;
            if(transitionIndex < 0)
            {
                transitioning = false;
                playerAtDoor = false;
                transitionIndex = 0;
            }
        }
    }

    public void mouseMoved(int mouseX, int mouseY)
    {
        if(transitioning) return;

        // HOVER ZONE CHANGES DEPENDING ON IF THE PLAYER IS AT THE MAIN OR NOT
        int zoneXMin = playerAtDoor ? DOOR_HOVER_X_MIN : MAIN_HOVER_X_MIN;
        int zoneXMax = playerAtDoor ? DOOR_HOVER_X_MAX : MAIN_HOVER_X_MAX;

        boolean inHoverZone =
                   mouseX >= zoneXMin
                && mouseX <= zoneXMax
                && mouseY >= HOVER_ZONE_Y_MIN
                && mouseY <= HOVER_ZONE_Y_MAX;

        if(inHoverZone && !wasInHoverZone)
        {
            transitioning = true;
            transitionForward = !playerAtDoor;
            transitionIndex = transitionForward ? 0 : TRANSITION_LENGTH - 1;
        }

        wasInHoverZone = inHoverZone;
    }

    public void draw(Graphics2D g2)
    {
        if(transitioning)
        {
            drawTransitionFrame(g2);
            return;
        }

        if(playerAtDoor)
            doorView.draw(g2);
        else
            mainView.draw(g2);

        drawHoverZone(g2);
    }

    private void drawTransitionFrame(Graphics2D g2)
    {
        // CURRENT FRAME IS LOADED DEPENDING ON THE INDEX
        BufferedImage frame = transitionFrames[transitionIndex];

        if(frame != null)
            g2.drawImage(frame, 0, 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
        else
        {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        }
    }

    private void drawHoverZone(Graphics2D g2)
    {
        int zoneXMin = playerAtDoor ? DOOR_HOVER_X_MIN : MAIN_HOVER_X_MIN + 15;
        int zoneXMax = playerAtDoor ? DOOR_HOVER_X_MAX : MAIN_HOVER_X_MAX + 15;

        int zoneW = zoneXMax - zoneXMin - 15;
        int zoneH = HOVER_ZONE_Y_MAX - HOVER_ZONE_Y_MIN;
        int midY = HOVER_ZONE_Y_MIN + zoneH / 2;

        // FILL THE HOVER ZONE
        g2.setColor(new Color(255, 255, 255, 40));
        g2.fillRoundRect(zoneXMin, HOVER_ZONE_Y_MIN, zoneW, zoneH, 10, 10);

        // DRAW THE BORDER
        g2.setColor(new Color(255, 255, 255, 140));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(zoneXMin, HOVER_ZONE_Y_MIN, zoneW, zoneH, 10, 10);

        // DRAW THE ARROWS
        drawArrows(g2, zoneXMin, zoneH, zoneW, midY);
        g2.setStroke(new BasicStroke(1));
    }

    private void drawArrows(Graphics2D g2, int zoneXMin, int zoneH, int zoneW, int midY)
    {
        int arrowW = (int)(zoneW * 0.20);
        int arrowH = (int)(zoneH * 0.20);

        int centerX = zoneXMin + zoneW / 2;
        int startY = midY - arrowH / 2;
        int endY = midY + arrowH / 2;

        // DRAWS TWO ARROWS STACKED, IN DIFFERENT OFFSETS TO CENTER
        int[] offsets = { -6, 6 };

        for(int offset : offsets)
        {
            int tipX = centerX + offset;
            int baseX = tipX - arrowW / 2;
            int endX = tipX + arrowW / 2;

            int[] xPoints;
            int[] yPoints = { startY, midY, endY };

            if(playerAtDoor) xPoints = new int[]{ baseX, tipX + arrowW / 2, baseX };
            else xPoints = new int[]{ endX, tipX - arrowW / 2, endX };

            g2.drawPolyline(xPoints, yPoints, 3);
        }
    }

    // CALLED BY THE GAME STATE WHEN MASK OR CAMERA COMES UP
    public void forceNormalView()
    {
        playerAtDoor = false;
        wasInHoverZone = false;
    }

    public boolean isPlayerAtDoor() { return playerAtDoor; }
    public boolean isTransitioning() { return transitioning; }
}
