package state.states;

import components.nights.NightConfig;
import main.GamePanel;
import state.State;
import state.StateManager;
import utilities.*;

import java.awt.*;
import java.awt.event.KeyEvent;

public class WinState extends State
{
    private int colorTimer = 0;
    private Fireworks fireworks;

    // TIMER BEFORE CHEER PLAYS
    private static final int CHEER_TIMER = 80;
    private int cheerTimer = CHEER_TIMER;

    private boolean fadingOut = false;
    private int fadeOutTimer = 0;
    private int pendingState = -1;

    private int displayNightNumber = 0;

    public WinState(StateManager stateManager)
    {
        super(stateManager);
        init();
    }

    @Override
    public void init()
    {
        SoundManager.SIX_AM.setVolume(0.6);
        SoundManager.SIX_AM.play();

        displayNightNumber = stateManager.getNightManager().getNightNumber();
        Utility.setScanlineCount(3);
        fireworks = new Fireworks();
    }

    @Override
    public void update()
    {
        colorTimer++;
        fireworks.update();

        if(fadingOut)
        {
            fadeOutTimer--;
            if(fadeOutTimer <= 0)
                stateManager.setState(pendingState);
            return;
        }

        if(cheerTimer > 0)
        {
            cheerTimer--;
            if(cheerTimer == 0)
                SoundManager.CHEERING.play();
        }
    }

    @Override
    public void draw(Graphics2D g2)
    {
        int w = GamePanel.WIDTH;
        int h = GamePanel.HEIGHT;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, w, h);

        drawContent(g2);
        fireworks.draw(g2);

        // FADE OUT OVERLAY
        if(fadingOut)
        {
            float progress = 1.0f - (float) fadeOutTimer / 60;
            g2.setColor(new Color(0, 0, 0, (int)(progress * 255)));
            g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        }
    }

    private void drawContent(Graphics2D g2)
    {
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        Composite old = g2.getComposite();
        g2.setComposite(ac);

        drawRainbowText(g2, "6 AM", GamePanel.HEIGHT / 2 - 30, FontManager.LCD_TITLE);
        drawRainbowText(g2, "YOU SURVIVED THE NIGHT!", GamePanel.HEIGHT / 2 + 20, FontManager.LCD_CLOCK);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_MEDIUM);
        Utility.drawCentered(g2, "Night " + displayNightNumber, GamePanel.HEIGHT / 2 + 50);

        g2.setColor(Color.DARK_GRAY);
        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCentered(g2, "Press ENTER to continue", GamePanel.HEIGHT / 2 + 340);

        g2.setComposite(old);
        Utility.drawCRTScanlines(g2, 4, 2, 100);
    }

    private void drawRainbowText(Graphics2D g2, String text, int y, Font font)
    {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int totalWidth = fm.stringWidth(text);
        int charX = (GamePanel.WIDTH - totalWidth) / 2;

        for(int i = 0; i < text.length(); i++)
        {
            // EACH LETTER HAS ITS OWN HUE OFFSET
            float hue = ((colorTimer * 0.02f) + (i * 0.1f)) % 1.0f;

            g2.setColor(Color.getHSBColor(hue, 1.0f, 1.0f));
            g2.drawString(String.valueOf(text.charAt(i)), charX, y);

            charX += fm.charWidth(text.charAt(i));
        }
    }

    private boolean isMidMode()
    {
        if(!stateManager.getNightManager().isCustomNight()) return false;
        NightConfig config = stateManager.getNightManager().getConfig();
        for(int level : config.aiLevels())
            if(level < 15) return false;
        return true;
    }

    private boolean isMaxMode()
    {
        if(!stateManager.getNightManager().isCustomNight()) return false;
        NightConfig config = stateManager.getNightManager().getConfig();
        for(int level : config.aiLevels())
            if(level != 20) return false;
        return true;
    }

    @Override
    public void onEnter() {}

    @Override
    public void keyPressed(int key)
    {
        if(key != KeyEvent.VK_ENTER) return;
        if(fadingOut) return;

        fadingOut = true;
        fadeOutTimer = 60;

        // STORE WHICH STATE TO GO TO AFTER FADE
        if(stateManager.getNightManager().isCustomNight())
        {
            // STAR TWO FOR BEATING 6/15
            // STAR THREE FOR BEATING MAX MODE
            int stars = SaveManager.loadStars();

            if(isMaxMode())
                stars = 3; // STAR 3
            else if(isMidMode())
                stars = Math.min(3, Math.max(stars, 2)); // STAR 2

            SaveManager.save(SaveManager.loadNight(), stars, true);
            pendingState = StateManager.TITLE_STATE;
        }
        else if(stateManager.getNightManager().isFinalNight())
        {
            // ADD ONE STAR WHEN BEATING NIGHT 5
            int stars = Math.max(SaveManager.loadStars(), 1);
            SaveManager.save(5, stars, true);
            pendingState = StateManager.TITLE_STATE;
        }
        else
        {
            stateManager.getNightManager().advanceNight();
            SaveManager.save(stateManager.getNightManager().getNightNumber(),
                    SaveManager.loadStars(), SaveManager.isCustomNightUnlocked());
            pendingState = StateManager.INTRO_STATE;
        }
    }

    @Override
    public void keyReleased(int key) {}

    @Override public void mouseMoved(int x, int y) {}
    @Override public void mouseClicked(int x, int y) {}
}