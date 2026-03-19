package components.cameras;

import components.animatronics.Animatronic;
import main.GamePanel;
import utilities.FontManager;
import utilities.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;

public class CameraSystem
{
    // CAMERA SYSTEM HOLDS AN ARRAY OF CAMERAS
    // AND AN ARRAY TO CHECK IF THEY ARE BROKEN
    private final Camera[] cameras;
    private boolean[] brokenCameras;

    private int currentCamera = 0;
    private boolean monitorUp = false;
    private boolean wasInHoverZone = false;

    // REBOOTING SYSTEM
    private static final int REBOOT_DURATION = 450;
    private boolean rebooting = false;
    private int rebootTimer = 0;

    // HOVER ZONE DIMENSIONS
    private static final int HOVER_ZONE_Y = GamePanel.HEIGHT - 75;
    private static final int HOVER_ZONE_X_MIN = (int)(GamePanel.WIDTH * 0.05);
    private static final int HOVER_ZONE_X_MAX = (int)(GamePanel.WIDTH * 0.45);

    // STATIC WHEN FLIPPING CAMERAS
    private int staticTimer = 0;
    private static final int STATIC_DURATION = 20;

    // X AND Y COORDINATES OF EACH BUTTON STORED IN AN ARRAY
    private static final int[] BUTTON_X = { 285, 160, 245, 160, 285, 185, 85 };
    private static final int[] BUTTON_Y = { 570, 520, 490, 470, 420, 320, 340 };
    private static final int BUTTON_W = 60;
    private static final int BUTTON_H = 30;

    // REBOOT BUTTON LAYOUT
    private static final int REBOOT_X = 1040;
    private static final int REBOOT_Y = 645;
    private static final int REBOOT_W = 200;
    private static final int REBOOT_H = 40;

    // MUSIC BOX IN CAMERA 1
    private final MusicBox musicBox = new MusicBox();
    private final ControlledShock shockButton = new ControlledShock();

    public CameraSystem()
    {
        cameras = new Camera[]
        {
            new Camera("CAM 1 - THE HIVE",  "/cameras/theHive.png"),
            new Camera("CAM 2 - ANIMATION STUDIO", "/cameras/animationStudio.png"),
            new Camera("CAM 3 - HALLS", "/cameras/halls.png"),
            new Camera("CAM 4 - STUDENT LOUNGE", "/cameras/studentLounge.png"),
            new Camera("CAM 5 - LEARNING ROOM", "/cameras/learningRoom.png"),
            new Camera("CAM 6 - EMERGENCY EXIT", "/cameras/emergencyExit.png"),
            new Camera("CAM 7 - LIBRARY", "/cameras/library.png"),
        };

        brokenCameras = new boolean[cameras.length];
    }

    public void update()
    {
        if(rebooting)
        {
            rebootTimer--;
            if(rebootTimer <= 0)
            {
                rebooting = false;
                brokenCameras = new boolean[cameras.length];
                shockButton.addCharge();
            }
        }

        // UPDATE THE SHOCK BUTTON
        shockButton.update();

        // UPDATE CAMERA SWAY ANIMATION
        cameras[currentCamera].update();

        // UPDATE THE TIMER STATIC
        if(staticTimer > 0) staticTimer--;
    }

    public void mouseMoved(int mouseX, int mouseY)
    {
        boolean inHoverZone =
                   mouseX >= HOVER_ZONE_X_MIN
                && mouseX <= HOVER_ZONE_X_MAX
                && mouseY >= HOVER_ZONE_Y;

        if(inHoverZone && !wasInHoverZone)
        {
            monitorUp = !monitorUp;
            if(monitorUp) staticTimer = STATIC_DURATION;
        }

        wasInHoverZone = inHoverZone;
    }

    public void mouseClicked(int mouseX, int mouseY)
    {
        if(!monitorUp) return;

        // CHECKS IF THE MOUSE CURSOR IS INSIDE THE BUTTON ZONE
        // CAMERA BUTTONS
        for(int i = 0; i < cameras.length; i++)
        {
            if(mouseX >= BUTTON_X[i] && mouseX <= BUTTON_X[i] + BUTTON_W
            && mouseY >= BUTTON_Y[i] && mouseY <= BUTTON_Y[i] + BUTTON_H)
            {
                currentCamera = i;
                if(!rebooting) staticTimer = STATIC_DURATION; // ADD STATIC EFFECT
                return;
            }
        }

        // REBOOT BUTTON
        if(!rebooting
            && mouseX >= REBOOT_X && mouseX <= REBOOT_X + REBOOT_W
            && mouseY >= REBOOT_Y && mouseY <= REBOOT_Y + REBOOT_H)
        {
            rebooting = true;
            rebootTimer = REBOOT_DURATION;
        }

        // FORWARDS MOUSE CLICKS TO MUSIC BOX
        if(currentCamera == 0) musicBox.mousePressed(mouseX, mouseY);
        shockButton.mouseClicked(mouseX, mouseY);
    }

    public void mouseReleased(int mouseX, int mouseY)
    {
        musicBox.mouseReleased();
    }

    public void keyPressed(int key)
    {
        if(!monitorUp) return;

        if(key == KeyEvent.VK_A)
        {
            currentCamera = (currentCamera - 1 + cameras.length) % cameras.length;
            if(!rebooting) staticTimer = STATIC_DURATION;
        }
        if(key == KeyEvent.VK_D)
        {
            currentCamera = (currentCamera + 1) % cameras.length;
            if(!rebooting) staticTimer = STATIC_DURATION;
        }
    }

    public void draw(Graphics2D g2, boolean showHover, Animatronic[] animatronics)
    {
        if(monitorUp)
        {
            drawCameraFeed(g2, animatronics);
            drawCameraMap(g2);
            drawCameraButtons(g2);
            drawRebootButton(g2);

            if(currentCamera == 0) musicBox.draw(g2);
            shockButton.draw(g2);

            drawCameraLabel(g2);
            drawStatic(g2);
        }

        if(showHover) drawHoverZone(g2);
    }

    private void drawCameraFeed(Graphics2D g2, Animatronic[] animatronics)
    {
        if(rebooting) { drawRebootingScreen(g2); return; }
        if(brokenCameras[currentCamera]) { drawBrokenScreen(g2); return; }

        drawCameraImage(g2);
        drawCameraBorder(g2);
        drawAnimatronics(g2, animatronics);
    }

    private void drawAnimatronics(Graphics2D g2, Animatronic[] animatronics)
    {
        int swayX = cameras[currentCamera].getSwayX();

        for(Animatronic a : animatronics)
            if(a.getAiLevel() > 0
                    && a.getLocation() == Animatronic.Location.CAMERA
                    && a.getCurrentCamera() == currentCamera)
                a.drawOnCamera(g2, swayX);
    }

    // TODO: THESE ARE PLACEHOLDERS, MAKE TRUE BROKEN AND REBOOT SCREEN
    private void drawRebootingScreen(Graphics2D g2)
    {
        g2.setColor(new Color(40, 35, 0));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g2.setColor(new Color(150, 115, 0));
        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "[ REBOOTING... ]", GamePanel.HEIGHT / 2);
        Utility.drawCentered(g2, (rebootTimer / 30 + 1) + " seconds remaining", GamePanel.HEIGHT / 2 + 30);

        drawCameraBorder(g2);
    }

    private void drawBrokenScreen(Graphics2D g2)
    {
        g2.setColor(new Color(40, 0, 0));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g2.setColor(new Color(150, 0, 0));
        g2.setFont(FontManager.LCD_MEDIUM);
        Utility.drawCentered(g2, "[ NO SIGNAL ]", GamePanel.HEIGHT / 2);

        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "Waiting for something to happen?", GamePanel.HEIGHT / 2 + 30);

        Utility.drawStatic(g2, 1, 1, new Color(180, 0, 0));
        drawCameraBorder(g2);
    }

    private void drawCameraImage(Graphics2D g2)
    {
        cameras[currentCamera].draw(g2);
    }

    private void drawCameraBorder(Graphics2D g2)
    {
        g2.setColor(getTextColor());
        g2.drawRect(15, 15, GamePanel.WIDTH - 30, GamePanel.HEIGHT - 30);
    }

    private void drawCameraMap(Graphics2D g2)
    {
        g2.setColor(getTextColor());

        // HALLWAY ROOMS
        g2.drawRect(65,  330, 100, 75);
        g2.drawRect(175, 360, 100, 45);
        g2.drawRect(230, 415,  45, 130);
        g2.drawRect(230, 555, 125, 75);

        // CONNECTORS
        g2.drawRect(165, 370, 10, 15);
        g2.drawRect(215, 430, 15, 10);
        g2.drawRect(275, 430, 10, 10);
        g2.drawRect(220, 480, 10, 10);
        g2.drawRect(220, 530, 10, 10);
        g2.drawRect(245, 545, 15, 10);
        g2.drawRect(245, 405, 15, 10);

        // CAMERA ROOMS
        g2.drawRect(205, 330, 60, 30);
        g2.drawRect(290, 410, 30, 50);
        g2.drawRect(185, 410, 30, 50);
        g2.drawRect(185, 490, 30, 50);

        // YOU INDICATOR
        g2.setFont(FontManager.LCD_SMALL);
        g2.drawString("YOU", 150, 440);
        g2.fillRect(195, 430, 10, 10);
    }

    private void drawCameraButtons(Graphics2D g2)
    {
        for(int i = 0; i < cameras.length; i++)
        {
            boolean selected = (i == currentCamera);

            g2.setColor(selected ? getSelectedColor() : getIdleColor());
            g2.fillRect(BUTTON_X[i], BUTTON_Y[i], BUTTON_W, BUTTON_H);

            g2.setColor(getTextColor());
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(BUTTON_X[i], BUTTON_Y[i], BUTTON_W, BUTTON_H);
            g2.setStroke(new BasicStroke(1));

            g2.setFont(FontManager.LCD_SMALL);
            String label  = "CAM " + (i + 1);
            int labelX = BUTTON_X[i] + (BUTTON_W - g2.getFontMetrics().stringWidth(label)) / 2;
            int labelY = BUTTON_Y[i] + BUTTON_H / 2 + 4;
            g2.setColor(getTextColor());
            g2.drawString(label, labelX, labelY);
        }
    }

    private void drawRebootButton(Graphics2D g2)
    {
        Color bg = rebooting ? new Color(80, 80, 80, 180) : new Color(168, 180, 0, 180);
        Color text = new Color(225, 255, 180);
        String label = rebooting ? "REBOOTING..." : "REBOOT CAMERAS";

        g2.setColor(bg);
        g2.fillRoundRect(REBOOT_X, REBOOT_Y, REBOOT_W, REBOOT_H, 8, 8);

        g2.setColor(text);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(REBOOT_X, REBOOT_Y, REBOOT_W, REBOOT_H, 8, 8);
        g2.setStroke(new BasicStroke(1));

        g2.setFont(FontManager.LCD_SMALL);
        int labelX = REBOOT_X + (REBOOT_W - g2.getFontMetrics().stringWidth(label)) / 2;
        int labelY = REBOOT_Y + REBOOT_H / 2 + 5;
        g2.drawString(label, labelX, labelY);
    }

    // DRAWS NAME OF CAMERA ON TOP OF CAMERA MAP
    private void drawCameraLabel(Graphics2D g2)
    {
        g2.setColor(getTextColor());
        g2.setFont(FontManager.LCD_MEDIUM);
        g2.drawString(cameras[currentCamera].getName(), 65, 290);
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

    // CAMERA FUNCTIONS FOR JIRSTEN
    public void breakCamera(int index)
    {
        if(index >= 0 && index < brokenCameras.length)
            brokenCameras[index] = true;
    }

    public boolean isCameraViewable(int cameraIndex)
    {
        // CAMERA IS CONSIDERED VIEWABLE ONLY IF IT IS NOT BROKEN AND NOT REBOOTING
        return !brokenCameras[cameraIndex]
                && !rebooting;
    }

    private void drawStatic(Graphics2D g2) {
        Utility.drawStatic(g2, staticTimer, STATIC_DURATION, new Color(255, 255, 255));
    }

    // THE CAMERA UI CHANGES DEPENDING ON THE STATE OF THE CAMERA
    public Color getTextColor()
    {
        if(rebooting) return new Color(225, 255, 180);
        if(brokenCameras[currentCamera]) return new Color(255, 180, 180);

        return new Color(180, 255, 180);
    }

    public Color getIdleColor()
    {
        if(rebooting) return new Color(80, 80, 0);
        if(brokenCameras[currentCamera]) return new Color(80, 0, 0);

        return new Color(0, 80, 0);
    }

    public Color getSelectedColor()
    {
        if(rebooting) return new Color(180, 180, 0);
        if(brokenCameras[currentCamera]) return new Color(180, 0, 0);

        return new Color(0, 180, 0);
    }

    public MusicBox getMusicBox() { return musicBox; }
    public ControlledShock getShockButton() { return shockButton; }

    public boolean isMonitorUp() { return monitorUp; }
    public int getCurrentCamera() { return currentCamera; }
}
