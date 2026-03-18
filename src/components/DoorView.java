package components;

import main.GamePanel;
import utilities.FontManager;
import utilities.Utility;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DoorView
{
    private final BufferedImage doorImage;
    public DoorView() { doorImage = Utility.loadImage("/office/door.png"); }

    public void draw(Graphics2D g2)
    {
        if(doorImage != null)
            g2.drawImage(doorImage, 0, 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
        else
            drawPlaceholder(g2);
    }

    private void drawPlaceholder(Graphics2D g2)
    {
        g2.setColor(Color.black);
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g2.setColor(Color.GREEN);
        g2.setFont(FontManager.LCD_LARGE);
        Utility.drawCentered(g2, "CURRENTLY IN GAME", GamePanel.HEIGHT / 2 - 20);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "you are in DOOR VIEW", GamePanel.HEIGHT / 2 + 20);
    }
}
