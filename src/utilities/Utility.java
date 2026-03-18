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
}