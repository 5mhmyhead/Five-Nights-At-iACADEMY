package components.animatronics;

import components.GameContext;
import state.StateManager;
import utilities.FontManager;

import java.awt.*;

// OFFICE ANIMATRONIC, PATIENCE WILL GO DOWN WHEN LOOKING AT CAMERAS OR MAIN VIEW
// WILL ONLY REGAIN PATIENCE WHEN LOOKING AWAY AT THE DOOR
public class Cristian extends Animatronic
{
    private enum CristianState { IDLE, IMPATIENT, AGGRESSIVE, CRITICAL }
    private CristianState state = CristianState.IDLE;

    // PATIENCE GOES DOWN BY 1 EVERY SUCCESSFUL MOVEMENT OPPORTUNITY
    // WHEN PATIENCE REACHES 0, THE PLAYER HAS 3 SECONDS OF BUFFER TIME TO REACT
    private static final int MAX_PATIENCE = 100;
    private static final int CRITICAL_COUNTDOWN = 90;

    private int patience = 100;
    private int criticalTimer = 0;

    private static final int MOVE_INTERVAL = 7;
    private int moveTimer = 0;

    public Cristian()
    {
        location = Location.MAIN;
    }

    @Override
    public void update(GameContext ctx)
    {
        handleMovement(ctx);
        handlePatience(ctx);
    }

    private void handleMovement(GameContext ctx)
    {
        moveTimer++;
        if (moveTimer >= MOVE_INTERVAL)
        {
            moveTimer = 0;
            if (shouldMove()) patience--;
        }

        if (patience <= 0 && state != CristianState.CRITICAL)
        {
            // TURNS CRITICAL WHEN REACHING 0 PATIENCE
            state = CristianState.CRITICAL;
            criticalTimer = CRITICAL_COUNTDOWN;
        }
        else if (state != CristianState.CRITICAL)
        {
            // UPDATE STATE OF CRISTIAN
            if (patience <= 33)
                state = CristianState.AGGRESSIVE;
            else if (patience <= 66)
                state = CristianState.IMPATIENT;
            else
                state = CristianState.IDLE;
        }
    }

    private void handlePatience(GameContext ctx)
    {
        if(ctx.office.isPlayerAtDoor())
        {
            // PATIENCE RECOVERS WHILE LOOKING AT DOOR
            patience++;
            if(patience >= MAX_PATIENCE)
                patience = MAX_PATIENCE;
        }
        else
        {
            if(state == CristianState.CRITICAL)
            {
                criticalTimer--;
                if(criticalTimer <= 0)
                    ctx.stateManager.setState(StateManager.LOSE_STATE);
            }
        }
    }

    @Override
    public void drawOnOffice(Graphics2D g2)
    {
        g2.setColor(new Color(255, 56, 56));
        g2.fillRect(30, 30, 30, 30);

        g2.setFont(FontManager.LCD_SMALL);
        g2.drawString("CRISTIAN[state: " + state + "]", 75, 50);

        drawPatienceBar(g2);
    }

    private void drawPatienceBar(Graphics2D g2)
    {
        int barX = 30;
        int barY = 75;
        int barW = 200;
        int barH = 10;

        // BACKGROUND
        g2.setColor(new Color(20, 20, 20, 180));
        g2.fillRoundRect(barX, barY, barW, barH, 4, 4);

        // FILL — SCALES WITH PATIENCE (0-100)
        int fillW = (int)(barW * (patience / 100.0));
        g2.setColor(new Color(255, 100, 100, 220));
        g2.fillRoundRect(barX, barY, fillW, barH, 4, 4);

        // BORDER
        g2.setColor(new Color(255, 180, 180));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(barX, barY, barW, barH, 4, 4);
        g2.setStroke(new BasicStroke(1));
    }

    public int getPatience() { return patience; }
    public boolean isCritical() { return state == CristianState.CRITICAL; }
    public int getCriticalTimer() { return criticalTimer; }
}
