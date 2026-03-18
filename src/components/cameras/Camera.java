package components.cameras;

import main.GamePanel;
import utilities.Utility;

import java.awt.*;
import java.awt.image.BufferedImage;

// CLASS THAT HOLDS A SINGLE CAMERA FOR THE CAMERA SYSTEM
public class Camera
{
    private final String name;
    private BufferedImage image;

    // CAMERAS SWAY TO ADD REALISM
    private double swayTimer = 0;
    private static final double SWAY_SPEED = 0.03;
    private static final double SWAY_AMPLITUDE = 16;

    public Camera(String name, String imagePath)
    {
        this.name = name;
        this.image = Utility.loadImage(imagePath);
    }

    public void update()
    {
        swayTimer += SWAY_SPEED;
    }

    public void draw(Graphics2D g2)
    {
        if(image == null) return;

        double scale = Math.max(
                (double) GamePanel.WIDTH  / image.getWidth(),
                (double) GamePanel.HEIGHT / image.getHeight());

        int drawW = (int)(image.getWidth() * scale);
        int drawH = (int)(image.getHeight() * scale);
        int drawX = (GamePanel.WIDTH - drawW) / 2 + getSwayX();
        int drawY = (GamePanel.HEIGHT - drawH) / 2;

        g2.drawImage(image, drawX, drawY, drawW, drawH, null);
    }

    public int getSwayX()
    {
        return (int)(Math.sin(swayTimer) * SWAY_AMPLITUDE);
    }

    public String getName() { return name; }
    public BufferedImage getImage() { return image; }
}
