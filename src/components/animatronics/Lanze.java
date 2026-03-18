package components.animatronics;

import components.GameContext;
import utilities.FontManager;

import java.awt.*;

public class Lanze extends Animatronic
{
    private static final int MOVE_INTERVAL = 300;
    private int moveTimer = 0;

    public Lanze()
    {
        currentCamera = 0;
        location = Location.CAMERA;
    }

    @Override
    public void update(GameContext ctx)
    {
        handleMovement();
    }

    private void handleMovement()
    {
        moveTimer++;

        if(moveTimer >= MOVE_INTERVAL)
        {
            moveTimer = 0;

            if(shouldMove())
                System.out.println("LANZE MOVED (AI LEVEL: " + aiLevel + ")");
            else
                System.out.println("LANZE FAILED MOVE ROLL (AI LEVEL: " + aiLevel + ")");
        }
    }

    @Override
    public void drawOnCamera(Graphics2D g2, int swayX)
    {
        g2.setColor(new Color(129, 100, 180));
        g2.fillRect(30, 30, 30, 30);

        g2.setFont(FontManager.LCD_SMALL);
        g2.drawString("LANZE", 75, 50);
    }
}
