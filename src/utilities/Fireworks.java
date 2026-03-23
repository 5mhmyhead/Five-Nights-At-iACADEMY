package utilities;

import main.GamePanel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class Fireworks
{
    private static final int FIREWORK_COUNT  = 7;
    private static final int PARTICLES_EACH  = 50;
    private static final int TOTAL_PARTICLES = FIREWORK_COUNT * PARTICLES_EACH;
    private static final Random random = new Random();

    private final float[] x, y, velX, velY, alpha;
    private final Color[] colors;
    private final int[] fireworkGroup;

    // FIREWORK LAUNCH POSITIONS AND TIMERS
    private final float[] launchX;
    private final int[] launchTimer;
    private final Color[] fireworkColor;

    public Fireworks()
    {
        x = new float[TOTAL_PARTICLES];
        y = new float[TOTAL_PARTICLES];
        velX = new float[TOTAL_PARTICLES];
        velY = new float[TOTAL_PARTICLES];
        alpha = new float[TOTAL_PARTICLES];
        colors = new Color[TOTAL_PARTICLES];
        fireworkGroup = new int[TOTAL_PARTICLES];

        launchX = new float[FIREWORK_COUNT];
        launchTimer = new int[FIREWORK_COUNT];
        fireworkColor = new Color[FIREWORK_COUNT];

        // STAGGER LAUNCH TIMERS
        for(int i = 0; i < FIREWORK_COUNT; i++)
        {
            launchX[i] = 100 + random.nextFloat() * (GamePanel.WIDTH - 200);
            launchTimer[i] = i * 20 + random.nextInt(30);
            fireworkColor[i] = Color.getHSBColor(random.nextFloat(), 0.8f, 1.0f);
        }

        // INITIALIZE ALL PARTICLES AS INACTIVE
        for(int i = 0; i < TOTAL_PARTICLES; i++)
            alpha[i] = 0f;
    }

    private void launch(int fireworkIndex)
    {
        int base = fireworkIndex * PARTICLES_EACH;
        float fx = launchX[fireworkIndex];
        float fy = GamePanel.HEIGHT * 0.2f + random.nextFloat() * GamePanel.HEIGHT * 0.4f;
        Color col = fireworkColor[fireworkIndex];

        for(int i = 0; i < PARTICLES_EACH; i++)
        {
            double angle = (2 * Math.PI / PARTICLES_EACH) * i;
            float speed = 2f + random.nextFloat() * 3f;

            x[base + i] = fx;
            y[base + i] = fy;
            velX[base + i] = (float)(Math.cos(angle) * speed);
            velY[base + i] = (float)(Math.sin(angle) * speed);
            alpha[base + i] = 1.0f;
            colors[base + i] = col;
            fireworkGroup[base + i] = fireworkIndex;
        }

        // RESET LAUNCH TIMER AND PICK NEW POSITION AND COLOR
        launchTimer[fireworkIndex] = 60 + random.nextInt(60);
        launchX[fireworkIndex] = 100 + random.nextFloat() * (GamePanel.WIDTH - 200);
        fireworkColor[fireworkIndex] = Color.getHSBColor(random.nextFloat(), 0.8f, 1.0f);
    }

    public void update()
    {
        // TICK LAUNCH TIMERS
        for(int i = 0; i < FIREWORK_COUNT; i++)
        {
            launchTimer[i]--;
            if(launchTimer[i] <= 0)
                launch(i);
        }

        // UPDATE PARTICLES
        for(int i = 0; i < TOTAL_PARTICLES; i++)
        {
            if(alpha[i] <= 0) continue;

            x[i] += velX[i];
            y[i] += velY[i];
            velY[i] += 0.05f; // GRAVITY
            velX[i] *= 0.98f; // DRAG
            velY[i] *= 0.98f;
            alpha[i] -= 0.015f; // FADE OUT
        }
    }

    public void draw(Graphics2D g2)
    {
        for(int i = 0; i < TOTAL_PARTICLES; i++)
        {
            if(alpha[i] <= 0) continue;

            AlphaComposite ac = AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, Math.min(1.0f, alpha[i]));
            Composite old = g2.getComposite();
            g2.setComposite(ac);
            g2.setColor(colors[i]);

            int px = (int)x[i];
            int py = (int)y[i];
            int s  = 2; // SIZE OF TRIANGLE

            // POINTING UPWARD
            int[] xPoints = { px,     px - s, px + s };
            int[] yPoints = { py - s, py + s, py + s };

            // ROTATE TRIANGLE TO FACE DIRECTION OF TRAVEL
            double angle = Math.atan2(velY[i], velX[i]);
            AffineTransform old2 = g2.getTransform();
            g2.rotate(angle, px, py);
            g2.fillPolygon(xPoints, yPoints, 3);
            g2.setTransform(old2);

            g2.setComposite(old);
        }
    }
}
