package state.states;

import main.GamePanel;
import main.Main;
import state.State;
import state.StateManager;
import utilities.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TitleState extends State
{
    private static final int NEW_GAME = 0;
    private static final int CONTINUE = 1;
    private static final int CUSTOM_NIGHT = 2;

    private int selectedOption = 0; // 0 = NEW GAME, 1 = CONTINUE, 2 = CUSTOM NIGHT
    private boolean hasSave = false;

    // THREE SECONDS TO DELETE SAVE
    private int deleteHoldTimer = 0;
    private static final int DELETE_HOLD_DURATION = 90;

    // GAME OPTIONS
    private boolean showingOptions = false;
    private boolean showingControls = false;
    private int optionsSelected = 0;

    private static final int OPT_FULLSCREEN = 0;
    private static final int OPT_MASTER_VOLUME = 1;
    private static final int OPT_REDUCE_JUMPSCARE = 2;
    private static final int OPT_LOW_PERFORMANCE = 3;
    private static final int OPT_CONTROLS = 4;
    private static final int OPT_BACK = 5;
    private static final int OPT_COUNT = 6;

    private boolean fullscreen = false;
    private boolean reduceJumpscare = false;
    private boolean lowPerformance = false;
    private float masterVolume = 1.0f;

    // ANIMATRONIC ROTATION
    private static final String[] NAMES = { "dave", "earl", "tyrone", "jirsten", "lanze", "cristian" };
    ArrayList<String> ANIMATRONICS = new ArrayList<>(Arrays.asList(NAMES));

    private final BufferedImage[][] animatronicFrames; // [character][frame]
    private int currentCharacter = 0;
    private int currentFrame = 0;

    // GLITCH STATE
    private int glitchTimer = 0;
    private boolean glitching = false;

    // CHARACTER SWAP TIMER
    private int characterTimer = 0;
    private boolean pendingSwap = false;

    private int scanlineUpdateTimer = 0;

    // CUSTOM NIGHT AND STARS
    private boolean customNightUnlocked = false;
    private int stars = 0;

    public TitleState(StateManager stateManager)
    {
        super(stateManager);

        Collections.shuffle(ANIMATRONICS);

        animatronicFrames = new BufferedImage[ANIMATRONICS.size()][8];
        for (int c = 0; c < ANIMATRONICS.size(); c++)
            for (int f = 0; f < 8; f++)
                animatronicFrames[c][f] = Utility.loadImage(
                        "/menu/" + ANIMATRONICS.get(c) + "/frame" + (f + 1) + ".png"
                );

        init();
    }

    @Override
    public void init()
    {
        SoundManager.MAIN_MENU.loop();
        glitchTimer = randomGlitchInterval();
        characterTimer = randomCharacterInterval();
        pendingSwap = false;
        currentCharacter = 0;
        currentFrame = 0;

        hasSave = SaveManager.hasSave();
        customNightUnlocked = SaveManager.isCustomNightUnlocked();
        stars = SaveManager.loadStars();
        selectedOption = 0;

        // LOAD AND APPLY SAVED SETTINGS
        float[] settings = SaveManager.loadSettings();
        masterVolume = settings[0];
        reduceJumpscare = settings[1] == 1f;
        lowPerformance = settings[2] == 1f;

        SoundManager.setMasterVolume(masterVolume);
        GlobalSettings.setReduceJumpscare(reduceJumpscare);
        GlobalSettings.setLowPerformance(lowPerformance);
    }

    private int randomGlitchInterval()
    {
        // 3-8 SECONDS BETWEEN GLITCHES
        return 60 + (int)(Math.random() * 120);
    }

    private int randomGlitchFrame()
    {
        // ANY FRAME EXCEPT 0
        return 1 + (int)(Math.random() * 7);
    }

    private int randomCharacterInterval()
    {
        // 4-7 SECONDS BETWEEN CHARACTER SWAPS
        return 120 + (int)(Math.random() * 90);
    }

    @Override
    public void update()
    {
        if (deleteHoldTimer > 0)
        {
            deleteHoldTimer--;
            if (deleteHoldTimer <= 0)
            {
                SaveManager.deleteSave();
                hasSave = false;
                customNightUnlocked = false;
                stars = 0;
                selectedOption = NEW_GAME;
            }
        }

        scanlineUpdateTimer++;
        if(scanlineUpdateTimer % 5 == 0) // UPDATE SLOWER THAN NORMAL
            Utility.updateScanlines();

        updateAnimatronic();
    }

    private void updateAnimatronic()
    {
        glitchTimer--;

        // TICK CHARACTER TIMER ONLY WHILE RESTING
        if (!glitching)
        {
            characterTimer--;
            if (characterTimer <= 0)
                pendingSwap = true; // FLAG, SWAP ON NEXT GLITCH END
        }

        if (!glitching)
        {
            currentFrame = 0;

            if (glitchTimer <= 0)
            {
                glitching = true;
                glitchTimer = 2 + (int)(Math.random() * 8);
                currentFrame = randomGlitchFrame();
            }
        }
        else
        {
            if (glitchTimer % 2 == 0)
                currentFrame = randomGlitchFrame();

            if (glitchTimer <= 0)
            {
                glitching = false;
                currentFrame = 0;
                glitchTimer = randomGlitchInterval();

                // SWAP CHARACTER IF FLAGGED
                if (pendingSwap)
                {
                    currentCharacter = (currentCharacter + 1) % ANIMATRONICS.size();
                    characterTimer = randomCharacterInterval();
                    pendingSwap = false;
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g2)
    {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, 1280, 720);

        drawAnimatronic(g2);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_TITLE);
        g2.drawString("FIVE", 75, 150);
        g2.drawString("NIGHTS", 75, 210);
        g2.drawString("AT", 75, 270);
        g2.drawString("iACADEMY", 75, 330);

        g2.setFont(FontManager.LCD_SMALL);
        g2.drawString("Made by DWYANE SIDO, ver 1.1", 25, 700);

        g2.setColor(Color.DARK_GRAY);
        g2.drawString("DEBUG KEYS f1 - f5 to move between states", 25, 30);
        g2.drawString("Press ESC for options", 25, 50);

        drawMenu(g2);
        drawStars(g2);
        drawRightGradient(g2);

        // DRAW OPTIONS
        if (showingOptions) drawOptionsOverlay(g2);

        // SUBTLE STATIC AND SCANLINES
        Utility.drawStatic(g2, 1, 10, new Color(255, 255, 255));
        Utility.drawAmbientScanlines(g2, new Color(255, 255, 255, 20), 2);
        Utility.drawCRTScanlines(g2, 4, 2, 100);
    }

    private void drawRightGradient(Graphics2D g2)
    {
        int gradientWidth = 500;
        int offsetX = 150;

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
        BufferedImage frame = animatronicFrames[currentCharacter][currentFrame];
        if (frame == null) return;
        g2.drawImage(frame, 0, 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
    }

    private void drawMenu(Graphics2D g2)
    {
        g2.setFont(FontManager.LCD_CLOCK);

        int selectedY = switch (selectedOption)
        {
            case 0 -> 500;
            case 1 -> 550;
            case 2 -> 620;
            default -> 0;
        };

        // DRAW CURSOR
        g2.setColor(Color.WHITE);
        g2.drawString(">", 75, selectedY);

        // NEW GAME
        g2.setColor(selectedOption == NEW_GAME ? new Color(255, 255, 255) : Color.DARK_GRAY);
        g2.drawString("NEW GAME", 125, 500);

        // CONTINUE
        if(hasSave)
        {
            g2.setColor(selectedOption == CONTINUE ? new Color(255, 255, 255) : Color.DARK_GRAY);
            g2.drawString("CONTINUE", 125, 550);

            g2.setFont(FontManager.LCD_SMALL);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("Night " + SaveManager.loadNight(), 125, 570);
        }

        // CUSTOM NIGHT
        if(customNightUnlocked)
        {
            g2.setFont(FontManager.LCD_CLOCK);
            g2.setColor(selectedOption == CUSTOM_NIGHT ? Color.WHITE : Color.DARK_GRAY);
            g2.drawString("CUSTOM NIGHT", 125, 620);
        }


        if (deleteHoldTimer > 0)
        {
            float progress = 1f - (float) deleteHoldTimer / DELETE_HOLD_DURATION;
            int barW = 200;
            int barX = 125;
            int barY = 440;

            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(barX, barY, barW, 6);

            g2.setColor(Color.WHITE);
            g2.fillRect(barX, barY, (int)(barW * progress), 6);

            g2.setFont(FontManager.LCD_SMALL);
            g2.drawString("DELETING SAVE...", barX, barY - 5);

            g2.setColor(Color.DARK_GRAY);
            g2.drawString("Press DELETE again to cancel", barX, barY - 25);
        }
        else
        {
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(FontManager.LCD_SMALL);
            g2.drawString("Press DELETE to erase ALL SAVE DATA", 25, 680);
        }
    }

    private void drawStars(Graphics2D g2)
    {
        if(stars <= 0) return;
        for(int i = 0; i < stars; i++)
            drawStar(g2, 90 + i * 45);
    }

    private void drawStar(Graphics2D g2, int centerX)
    {
        g2.setColor(Color.WHITE);

        int points = 5;
        int[] xPoints = new int[points * 2];
        int[] yPoints = new int[points * 2];

        double outerRadius = 20;
        double innerRadius = 20 * 0.4;

        for(int i = 0; i < points * 2; i++)
        {
            double angle = Math.PI / points * i - Math.PI / 2;
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;

            xPoints[i] = (int)(centerX + Math.cos(angle) * radius);
            yPoints[i] = (int)(370 + Math.sin(angle) * radius);
        }

        g2.fillPolygon(xPoints, yPoints, points * 2);
    }

    // OPTION FUNCTIONS
    private void drawOptionsOverlay(Graphics2D g2)
    {
        // DARKEN BACKGROUND
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        // WINDOW
        int winW = 600;
        int winH = 480;
        int winX = (GamePanel.WIDTH  - winW) / 2;
        int winY = (GamePanel.HEIGHT - winH) / 2;

        g2.setColor(new Color(10, 10, 10, 230));
        g2.fillRoundRect(winX, winY, winW, winH, 12, 12);

        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(winX, winY, winW, winH, 12, 12);
        g2.setStroke(new BasicStroke(1));

        if (showingControls) drawControlsScreen(g2, winX, winY, winW, winH);
        else drawOptionsScreen(g2, winX, winY, winW, winH);
    }

    private void drawOptionsScreen(Graphics2D g2, int winX, int winY, int winW, int winH)
    {
        // TITLE
        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_LARGE);
        Utility.drawCenteredInWindow(g2, "OPTIONS", winX, winY + 50, winW);

        int startY = winY + 110;
        int gap = 55;

        drawOverlayOption(g2, OPT_FULLSCREEN, "FULLSCREEN", fullscreen? "ON" : "OFF", winX, startY, winW);
        drawOverlayOption(g2, OPT_MASTER_VOLUME, "MASTER VOLUME", Math.round(masterVolume * 100) + "%", winX, startY + gap, winW);
        drawOverlayOption(g2, OPT_REDUCE_JUMPSCARE, "REDUCE JUMPSCARE SOUND", reduceJumpscare  ? "ON" : "OFF", winX, startY + gap * 2, winW);
        drawOverlayOption(g2, OPT_LOW_PERFORMANCE, "LOW PERFORMANCE", lowPerformance   ? "ON" : "OFF",  winX, startY + gap * 3, winW);
        drawOverlayOption(g2, OPT_CONTROLS, "CONTROLS", ">", winX, startY + gap * 4, winW);
        drawOverlayOption(g2, OPT_BACK, "BACK", "", winX, startY + gap * 5, winW);

        g2.setFont(FontManager.LCD_SMALL);
        g2.setColor(Color.DARK_GRAY);
        Utility.drawCenteredInWindow(g2, "W/S navigate  |  A/D change  |  ENTER confirm", winX, winY + winH - 20, winW);
    }

    private void drawOverlayOption(Graphics2D g2, int index, String label, String value, int winX, int y, int winW)
    {
        boolean selected = optionsSelected == index;

        g2.setFont(FontManager.LCD_MEDIUM);
        g2.setColor(selected ? Color.WHITE : Color.DARK_GRAY);
        g2.drawString(selected ? "> " + label : "  " + label, winX + 40, y);

        if (!value.isEmpty())
        {
            g2.setColor(Color.WHITE);
            int valX = winX + winW - 40 - g2.getFontMetrics().stringWidth(value);
            g2.drawString(value, valX, y);
        }
    }

    private void drawControlsScreen(Graphics2D g2, int winX, int winY, int winW, int winH)
    {
        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_LARGE);
        Utility.drawCenteredInWindow(g2, "CONTROLS", winX, winY + 50, winW);

        int y = winY + 100;
        int gap = 40;

        g2.setFont(FontManager.LCD_SMALL);
        drawControl(g2, "FLIP CAMERAS","Q","HOVER BOTTOM LEFT", winX + 40, y);
        y += gap;

        drawControl(g2, "SWITCH CAMERA","A / D","CLICK CAMERA MAP", winX + 40, y);
        y += gap;

        drawControl(g2, "LOOK AT DOOR","SHIFT","HOVER RIGHT EDGE", winX + 40, y);
        y += gap;

        drawControl(g2, "CLOSE EYES","E","HOVER BOTTOM RIGHT", winX + 40, y);
        y += gap;

        drawControl(g2, "WIND MUSIC BOX","W","BLUE BUTTON ON CAMERA ONE", winX + 40, y);
        y += gap;

        drawControl(g2, "SHOCK DAVE","S","YELLOW BUTTON, THREE SHOCKS", winX + 40, y);
        y += gap;

        drawControl(g2, "REBOOT CAMERAS","R","ORANGE BUTTON", winX + 40, y);
        y += gap;

        drawControl(g2, "FULLSCREEN","F11","", winX + 40, y);
        y += gap;

        drawControl(g2, "OPTIONS","ESC","", winX + 40, y);

        g2.setColor(Color.DARK_GRAY);
        g2.setFont(FontManager.LCD_SMALL);
        Utility.drawCenteredInWindow(g2, "ESC to go back", winX, winY + winH - 20, winW);
    }

    private void drawControl(Graphics2D g2, String action, String key, String extra, int x, int y)
    {
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString(action, x, y);

        g2.setColor(Color.WHITE);
        g2.drawString(key, x + 200, y);

        g2.setColor(Color.DARK_GRAY);
        g2.drawString(extra, x + 280, y);
    }

    @Override
    public void onEnter() {}

    @Override
    public void keyPressed(int key)
    {
        if (showingOptions)
        {
            handleOptionsInput(key);
            return;
        }

        if (key == KeyEvent.VK_ESCAPE)
        {
            showingOptions = true;
            return;
        }

        if (key == KeyEvent.VK_DELETE)
        {
            if(deleteHoldTimer > 0)
                deleteHoldTimer = 0;
            else
                deleteHoldTimer = DELETE_HOLD_DURATION;
        }

        // BUILD AVAILABLE OPTIONS LIST
        java.util.List<Integer> options = new java.util.ArrayList<>();
        options.add(NEW_GAME);

        if(hasSave) options.add(CONTINUE);
        if(customNightUnlocked) options.add(CUSTOM_NIGHT);

        int currentIndex = options.indexOf(selectedOption);

        if(key == KeyEvent.VK_UP || key == KeyEvent.VK_W)
            selectedOption = options.get((currentIndex - 1 + options.size()) % options.size());

        if(key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S)
            selectedOption = options.get((currentIndex + 1) % options.size());

        if(key == KeyEvent.VK_ENTER)
        {
            if(selectedOption == CUSTOM_NIGHT && customNightUnlocked)
            {
                stateManager.setState(StateManager.CUSTOM_STATE);
                return;
            }
            else if(selectedOption == CONTINUE && hasSave)
            {
                // CONTINUE LOADS SAVED NIGHT
                stateManager.getNightManager().continueGame(SaveManager.loadNight());
            }
            else
            {
                // KEEP STARS AND CUSTOM NIGHT, ONLY RESET NIGHT PROGRESS
                SaveManager.save(1, SaveManager.loadStars(), SaveManager.isCustomNightUnlocked());
                stateManager.getNightManager().startNewGame();
            }

            stateManager.setState(StateManager.INTRO_STATE);
        }
    }

    private void handleOptionsInput(int key)
    {
        if (showingControls)
        {
            if (key == KeyEvent.VK_ESCAPE) showingControls = false;
            return;
        }

        if (key == KeyEvent.VK_ESCAPE) { showingOptions = false; return; }
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) optionsSelected = (optionsSelected - 1 + OPT_COUNT) % OPT_COUNT;
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) optionsSelected = (optionsSelected + 1) % OPT_COUNT;
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) adjustOptionValue(-1);
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) adjustOptionValue(1);
        if (key == KeyEvent.VK_ENTER) confirmOption();
    }

    private void adjustOptionValue(int direction)
    {
        switch (optionsSelected)
        {
            case OPT_FULLSCREEN ->
            {
                Main.toggleFullscreen();
                fullscreen = !fullscreen;
            }

            case OPT_MASTER_VOLUME ->
            {
                masterVolume = Math.min(1f, Math.max(0f, masterVolume + direction * 0.1f));
                SoundManager.setMasterVolume(masterVolume);
            }

            case OPT_REDUCE_JUMPSCARE ->
            {
                reduceJumpscare = !reduceJumpscare;
                GlobalSettings.setReduceJumpscare(reduceJumpscare);
            }

            case OPT_LOW_PERFORMANCE ->
            {
                lowPerformance = !lowPerformance;
                GlobalSettings.setLowPerformance(lowPerformance);
            }
        }

        // SAVE AFTER EVERY CHANGE
        SaveManager.saveSettings(masterVolume, reduceJumpscare, lowPerformance);
    }

    // FOR ENTER BUTTONG
    private void confirmOption()
    {
        switch (optionsSelected)
        {
            case OPT_FULLSCREEN ->
            {
                Main.toggleFullscreen();
                fullscreen = !fullscreen;
            }

            case OPT_REDUCE_JUMPSCARE ->
            {
                reduceJumpscare = !reduceJumpscare;
                GlobalSettings.setReduceJumpscare(reduceJumpscare);
            }

            case OPT_LOW_PERFORMANCE ->
            {
                lowPerformance = !lowPerformance;
                GlobalSettings.setLowPerformance(lowPerformance);
            }

            case OPT_CONTROLS -> showingControls = true;
            case OPT_BACK -> showingOptions  = false;
        }

        SaveManager.saveSettings(masterVolume, reduceJumpscare, lowPerformance);
    }

    @Override
    public void mouseClicked(int x, int y)
    {
        if (showingOptions) return;

        if (x < 75 || x > 425) return;

        if (isHovering(y, 500)) selectedOption = NEW_GAME;
        else if (hasSave && isHovering(y, 550)) selectedOption = CONTINUE;
        else if (customNightUnlocked && isHovering(y, 620)) selectedOption = CUSTOM_NIGHT;
        else return; // CLICKED OUTSIDE ANY OPTION

        // CONFIRM SELECTION
        keyPressed(KeyEvent.VK_ENTER);
    }

    @Override
    public void mouseMoved(int x, int y)
    {
        if (showingOptions) return;

        if (x < 75 || x > 425) return;

        if (isHovering(y, 500))
            selectedOption = NEW_GAME;
        else if (hasSave && isHovering(y, 550))
            selectedOption = CONTINUE;
        else if (customNightUnlocked && isHovering(y, 620))
            selectedOption = CUSTOM_NIGHT;
    }

    private boolean isHovering(int mouseY, int optionY)
    {
        return mouseY >= optionY - 30 && mouseY <= optionY + 10;
    }


    @Override public void keyReleased(int key) {}
}
