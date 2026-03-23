package utilities;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class Utility
{
    private static int[] scanlinePositions;
    private static int SCANLINE_COUNT = 4;

    // DRAW STRING TO THE CENTER OF SCREEN
    public static void drawCentered(Graphics2D g, String text, int y)
    {
        int x = (GamePanel.WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    // LOAD IMAGE FROM FILE
    public static BufferedImage loadImage(String path)
    {
        try
        {
            return ImageIO.read(Objects.requireNonNull(
                    Utility.class.getResourceAsStream(path)));
        }
        catch(IOException | NullPointerException e)
        {
            System.out.println("Image not found: " + path);
            return null;
        }
    }

    // LOAD FONT FROM FILE
    public static Font loadFont(String path, float size)
    {
        try
        {
            Font font = Font.createFont(Font.TRUETYPE_FONT,
                    Objects.requireNonNull(Utility.class.getResourceAsStream(path)));

            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font.deriveFont(size);
        }
        catch(FontFormatException | IOException | NullPointerException e)
        {
            System.out.println("Font not found: " + path);
            return new Font("Monospaced", Font.PLAIN, (int) size);
        }
    }

    // APPLIES MOTION BLUR
    public static BufferedImage applyMotionBlur(BufferedImage image)
    {
        if(image == null) return null;

        // HORIZONTAL MOTION BLUR KERNEL
        int blurLength = 20;
        float[] kernel = new float[blurLength];
        Arrays.fill(kernel, 1.0f / blurLength);

        Kernel k  = new Kernel(blurLength, 1, kernel);
        ConvolveOp op = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, null);
    }

    // FUNCTION THAT DRAWS STATIC ON THE SCREEN
    public static void drawStatic(Graphics2D g2, int staticTimer, int staticDuration, Color baseColor)
    {
        float minAlpha = 0.20f;
        float maxAlpha = 0.90f;

        float fadeRange = maxAlpha - minAlpha;
        float alpha = minAlpha;

        if(staticTimer > 0)
            alpha = minAlpha + ((float) staticTimer / staticDuration) * fadeRange;

        for(int y = 0; y < GamePanel.HEIGHT; y += 3)
        {
            for(int x = 0; x < GamePanel.WIDTH; x += 3)
            {
                if(Math.random() > 0.5)
                {
                    int brightness = (int)(Math.random() * 255);
                    g2.setColor(new Color(
                        (int)(baseColor.getRed() * brightness / 255.0),
                        (int)(baseColor.getGreen() * brightness / 255.0),
                        (int)(baseColor.getBlue() * brightness / 255.0),
                        (int)(alpha * 150)));

                    g2.fillRect(x, y, 2, 2);
                }
            }
        }
    }

    // DRAW SCANLINES WHENEVER THE PLAYER PULLS UP THE CAMERAS
    public static void drawScanlines(Graphics2D g2, Color baseColor)
    {
        if(scanlinePositions == null) return;

        g2.setColor(new Color(
            baseColor.getRed(),
            baseColor.getGreen(),
            baseColor.getBlue()
        ));

        for(int pos : scanlinePositions)
        {
            int height = 20 + (int)(Math.random() * 100);
            g2.fillRect(0, pos, GamePanel.WIDTH, height);
        }
    }

    // FOR SCANLINES OUTSIDE OF CAMERAS
    public static void drawAmbientScanlines(Graphics2D g2, Color baseColor, int count)
    {
        // GENERATE FRESH POSITIONS FOR THIS DRAW
        int sectionHeight = GamePanel.HEIGHT / count;

        g2.setColor(new Color(
            baseColor.getRed(),
            baseColor.getGreen(),
            baseColor.getBlue(),
            baseColor.getAlpha()
        ));

        for(int i = 0; i < count; i++)
        {
            int pos = i * sectionHeight + (int)(Math.random() * sectionHeight) - 30;
            int height = 20 + (int)(Math.random() * 120);
            g2.fillRect(0, pos, GamePanel.WIDTH, height);
        }
    }

    public static void updateScanlines()
    {
        scanlinePositions = new int[SCANLINE_COUNT];
        for(int i = 0; i < SCANLINE_COUNT; i++)
            scanlinePositions[i] = (int)(Math.random() * GamePanel.HEIGHT);
    }

    public static void setScanlineCount(int count)
    {
        SCANLINE_COUNT = count;
    }

    public static void drawCRTScanlines(Graphics2D g2, int spacing, int lineHeight, int alpha)
    {
        g2.setColor(new Color(0, 0, 0, alpha));
        for(int y = 0; y < GamePanel.HEIGHT; y += spacing)
            g2.fillRect(0, y, GamePanel.WIDTH, lineHeight);
    }
}