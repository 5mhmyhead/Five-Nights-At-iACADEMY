package state.states;

import main.GamePanel;
import state.State;
import state.StateManager;
import utilities.FontManager;
import utilities.SoundManager;
import utilities.Utility;

import java.awt.*;

// THE INTRO SCREEN THAT POPS UP BEFORE EVERY NIGHT
public class IntroState extends State
{
    private static final int DISPLAY_DURATION = 150;
    private static final int GLITCH_DURATION = 90;
    private static final int SCANLINE_FADE_DURATION = 60;

    private int timer = 0;

    public IntroState(StateManager stateManager)
    {
        super(stateManager);
        init();
    }

    @Override
    public void init()
    {
        timer = 0;

        // PRELOAD GAME STATE IN BACKGROUND WHILE INTRO PLAYS
        new Thread(() -> stateManager.preloadGameState()).start();
    }

    @Override
    public void update()
    {
        timer++;
        if(timer >= DISPLAY_DURATION)
            stateManager.setState(StateManager.GAME_STATE);

        // UPDATE SCANLINES WHILE FADING IN
        if(timer <= SCANLINE_FADE_DURATION)
            Utility.updateScanlines();
    }

    @Override
    public void draw(Graphics2D g2)
    {
        drawBackground(g2);
        drawText(g2);
        drawScanlinesFadeIn(g2);
        drawStaticFadeIn(g2);
        Utility.drawCRTScanlines(g2, 4, 2, 100);
    }

    private void drawBackground(Graphics2D g2)
    {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
    }

    private void drawText(Graphics2D g2)
    {
        if(timer <= GLITCH_DURATION)
        {
            drawGlitchedText(g2, getNightName(), GamePanel.HEIGHT / 2 - 30, FontManager.LCD_LARGE);
            drawGlitchedText(g2, "12:00 AM",  GamePanel.HEIGHT / 2 + 30, FontManager.LCD_CLOCK);
        }
        else
        {
            // STABLE TEXT AFTER GLITCH PHASE
            g2.setColor(Color.WHITE);
            g2.setFont(FontManager.LCD_LARGE);
            Utility.drawCentered(g2, getNightName(), GamePanel.HEIGHT / 2 - 30);

            g2.setFont(FontManager.LCD_CLOCK);
            Utility.drawCentered(g2, "12:00 AM", GamePanel.HEIGHT / 2 + 30);
        }
    }

    private void drawGlitchedText(Graphics2D g2, String text, int y, Font font)
    {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        int totalWidth = fm.stringWidth(text);
        int charX = (GamePanel.WIDTH - totalWidth) / 2;

        for(int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            g2.setColor(Color.WHITE);

            // EACH CHARACTER HAS A RANDOM CHANCE TO BE INVISIBLE
            // CHANCE DECREASES AS TIMER APPROACHES DURATION
            float glitchChance = 0.6f * (1.0f - (float) timer / GLITCH_DURATION);
            if(Math.random() > glitchChance) g2.drawString(String.valueOf(c), charX, y);

            charX += fm.charWidth(c);
        }
    }

    private void drawScanlinesFadeIn(Graphics2D g2)
    {
        if(timer > SCANLINE_FADE_DURATION) return;

        // SCANLINES REDUCE FROM HIGH COUNT TO 0
        float progress = (float) timer / SCANLINE_FADE_DURATION;
        int scanlineCount = (int)(10 * (1.0f - progress));

        if(scanlineCount > 0)
            Utility.drawAmbientScanlines(g2, new Color(255, 255, 255), scanlineCount);
    }

    private void drawStaticFadeIn(Graphics2D g2)
    {
        if(timer > SCANLINE_FADE_DURATION) return;

        // STATIC REDUCES AS TIMER INCREASES
        int remainingStatic = SCANLINE_FADE_DURATION - timer;
        Utility.drawStatic(g2, remainingStatic, SCANLINE_FADE_DURATION, new Color(180, 255, 180));
    }

    private String getNightName()
    {
        return "Night " + stateManager.getNightManager().getNightNumber();
    }

    @Override
    public void onEnter()
    {
        SoundManager.MAIN_MENU.stop();

        SoundManager.NIGHT_START.setVolume(0.3);
        SoundManager.NIGHT_START.play();
    }

    @Override public void keyPressed(int key) {}
    @Override public void keyReleased(int key) {}

    @Override public void mouseMoved(int x, int y) {}
    @Override public void mouseClicked(int x, int y) {}
}
