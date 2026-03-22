package state.states;

import main.GamePanel;
import state.State;
import state.StateManager;
import utilities.FontManager;
import utilities.SaveManager;
import utilities.SoundManager;
import utilities.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class TitleState extends State
{
    private int selectedOption = 0; // 0 = NEW GAME, 1 = CONTINUE
    private boolean hasSave = false;

    private final BufferedImage[] animatronicFrames;
    private int currentFrame = 0;
    private int glitchTimer = 0;
    private boolean glitching = false;

    private int scanlineUpdateTimer = 0;

    public TitleState(StateManager stateManager)
    {
        super(stateManager);

        animatronicFrames = new BufferedImage[8];
        for(int i = 0; i < 8; i++)
            animatronicFrames[i] = Utility.loadImage("/menu/frame" + (i + 1) + ".png");

        init();
    }

    @Override
    public void init()
    {
        SoundManager.MAIN_MENU.loop();
        glitchTimer = randomGlitchInterval();

        hasSave = SaveManager.hasSave();
        selectedOption = 0;
    }

    private int randomGlitchInterval()
    {
        // STAY AT REST FOR 3-8 SECONDS BETWEEN GLITCHES
        return 90 + (int)(Math.random() * 150);
    }

    private int randomGlitchFrame()
    {
        // PICK ANY FRAME EXCEPT 0 (RESTING STATE)
        return 1 + (int)(Math.random() * 7);
    }

    @Override
    public void update()
    {
        scanlineUpdateTimer++;
        if(scanlineUpdateTimer % 5 == 0) // UPDATE SLOWER THAN NORMAL
            Utility.updateScanlines();

        updateAnimatronic();
    }

    private void updateAnimatronic()
    {
        glitchTimer--;

        if(!glitching)
        {
            currentFrame = 0; // STAY AT RESTING STATE

            if(glitchTimer <= 0)
            {
                // START GLITCH
                glitching = true;
                // 2-8 FRAMES
                glitchTimer = 2 + (int) (Math.random() * 8);
                currentFrame = randomGlitchFrame();
            }
        }
        else
        {
            // DURING GLITCH, RAPIDLY SWITCH FRAMES
            if(glitchTimer % 2 == 0)
                currentFrame = randomGlitchFrame();

            if(glitchTimer <= 0)
            {
                // END GLITCH
                glitching = false;
                currentFrame = 0;
                glitchTimer = randomGlitchInterval();
            }
        }
    }

    @Override
    public void draw(Graphics2D g2)
    {
        int w = GamePanel.WIDTH;
        int h = GamePanel.HEIGHT;

        // PLACEHOLDER TITLE SCREEN
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, w, h);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_TITLE);
        g2.drawString("FIVE NIGHTS", 75, 150);
        g2.drawString("AT iACADEMY", 75, 210);

        // ANIMATRONIC AND MENU
        drawAnimatronic(g2);
        drawMenu(g2);
        drawRightGradient(g2);

        // SUBTLE STATIC AND SCANLINES
        Utility.drawStatic(g2, 1, 10, new Color(255, 255, 255));
        Utility.drawAmbientScanlines(g2, new Color(255, 255, 255, 40), 2);
    }

    private void drawRightGradient(Graphics2D g2)
    {
        int gradientWidth = 500;
        int offsetX = 100;

        GradientPaint gradient = new GradientPaint(
                GamePanel.WIDTH - gradientWidth - offsetX, 0, new Color(0, 0, 0, 0),   // TRANSPARENT
                GamePanel.WIDTH - offsetX, 0, new Color(0, 0, 0, 255)  // BLACK
        );

        g2.setPaint(gradient);
        g2.fillRect(GamePanel.WIDTH - gradientWidth - offsetX, 0,
                gradientWidth + offsetX, GamePanel.HEIGHT);
        g2.setPaint(null);
    }

    private void drawAnimatronic(Graphics2D g2)
    {
        if(animatronicFrames[currentFrame] == null) return;
        g2.drawImage(animatronicFrames[currentFrame], 50, 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
    }

    private void drawMenu(Graphics2D g2)
    {
        g2.setFont(FontManager.LCD_CLOCK);

        // DRAW CURSOR
        int selectedY = selectedOption == 0 ? 500 : 550;
        g2.drawString(">", 75, selectedY);

        // NEW GAME
        g2.setColor(selectedOption == 0 ? new Color(255, 255, 255) : Color.DARK_GRAY);
        g2.drawString("NEW GAME", 125, 500);

        // CONTINUE
        if(hasSave)
        {
            g2.setColor(selectedOption == 1 ? new Color(255, 255, 255) : Color.DARK_GRAY);
            g2.drawString("CONTINUE", 125, 550);

            g2.setFont(FontManager.LCD_SMALL);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("Night " + SaveManager.load(), 125, 570);
        }
    }

    @Override
    public void keyPressed(int key)
    {
        if(key == KeyEvent.VK_UP
        || key == KeyEvent.VK_DOWN
        || key == KeyEvent.VK_W
        || key == KeyEvent.VK_S)
        {
            if(hasSave) // ONLY NAVIGATE IF CONTINUE EXISTS
                selectedOption = selectedOption == 0 ? 1 : 0;
        }

        if(key == KeyEvent.VK_ENTER)
        {
            if(selectedOption == 1 && hasSave)
            {
                // CONTINUE LOADS SAVED NIGHT
                int savedNight = SaveManager.load();
                stateManager.getNightManager().loadNight(savedNight);
            }
            else
            {
                // NEW GAME RESETS TO NIGHT 1
                SaveManager.deleteSave();
                stateManager.getNightManager().loadNight(1);
            }

            stateManager.setState(StateManager.INTRO_STATE);
        }
    }

    @Override public void keyReleased(int key) {}

    @Override public void mouseMoved(int x, int y) {}
    @Override public void mouseClicked(int x, int y) {}
}
