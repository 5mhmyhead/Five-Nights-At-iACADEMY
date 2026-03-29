package state.states;

import components.nights.ChallengeConfig;
import components.nights.NightConfig;
import components.nights.PresetConfig;
import main.GamePanel;
import state.State;
import state.StateManager;
import utilities.FontManager;
import utilities.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class CustomState extends State
{
    private static final String[] NAMES = { "DAVE", "EARL", "TYRONE", "CRISTIAN", "JIRSTEN", "LANZE" };
    private final int[] aiLevels = { 0, 0, 0, 0, 0, 0 };
    private int selectedRow = 0;

    private boolean startHovered = false;

    // CHALLENGES
    private boolean noFootsteps = false;
    private boolean zeroShocks = false;
    private boolean longReboot = false;
    private boolean hallucinations = false;
    private boolean halvedPatience = false;
    private boolean superboost = false;

    // PRESETS
    private int selectedPreset = 0;

    private static final PresetConfig[] PRESETS =
    {
        new PresetConfig("NONE",
            new int[]{ 0, 0, 0, 0, 0, 0 },
            new ChallengeConfig(false, false, false, false, false, false)),

        new PresetConfig("NIGHT STALKER",
            new int[]{ 20, 0, 0, 10, 15, 10 },
            new ChallengeConfig(true, false, true, true, false, false)),

        new PresetConfig("CLOSED EYES",
            new int[]{ 0, 20, 20, 0, 0, 15 },
            new ChallengeConfig(true, false, false, false, true, true)),

        new PresetConfig("IMPATIENCE",
            new int[]{ 10, 5, 5, 20, 0, 20 },
            new ChallengeConfig(false, false, false, false, true, false)),

        new PresetConfig("MAYHEM",
            new int[]{ 18, 15, 15, 15, 18, 15 },
            new ChallengeConfig(false, false, true, true, false, true)),

        new PresetConfig("6/20 MODE",
            new int[]{ 20, 20, 20, 20, 20, 20 },
            new ChallengeConfig(false, false, false, false, false, false)),

        new PresetConfig("ULTIMATE 6/20",
            new int[]{ 20, 20, 20, 20, 20, 20 },
            new ChallengeConfig(true, true, true, true, true, true)),
    };

    private final BufferedImage dave;
    private final BufferedImage earl;
    private final BufferedImage tyrone;
    private final BufferedImage cristian;
    private final BufferedImage jirsten;
    private final BufferedImage lanze;

    public CustomState(StateManager stateManager)
    {
        super(stateManager);

        dave = Utility.loadImage("/custom/dave.png");
        earl = Utility.loadImage("/custom/earl.png");
        tyrone = Utility.loadImage("/custom/tyrone.png");
        cristian = Utility.loadImage("/custom/cristian.png");
        jirsten = Utility.loadImage("/custom/jirsten.png");
        lanze = Utility.loadImage("/custom/lanze.png");

        init();
    }

    @Override
    public void init() { selectedRow = 0; }

    @Override
    public void update() {}

    @Override
    public void onEnter() {}

    @Override
    public void draw(Graphics2D g2)
    {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        // TITLE
        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_TITLE);
        Utility.drawCentered(g2, "CUSTOM NIGHT", 100);

        // ANIMATRONIC ROWS
        drawAnimatronics(g2);

        for(int i = 0; i < NAMES.length; i++)
            drawSelection(g2, i);

        // START PROMPT
        g2.setFont(FontManager.LCD_SMALL);
        g2.drawString("ENTER to start  |  ESC to go back", 100, GamePanel.HEIGHT - 25);

        g2.setColor(startHovered ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        g2.fillRect(1000, 600, 200, 40);
        g2.setColor(Color.WHITE);
        g2.drawRect(1000, 600, 200, 40);

        g2.setFont(FontManager.LCD_MEDIUM);
        g2.drawString("START", 1067, 629);

        g2.setColor(Color.DARK_GRAY);
        g2.setFont(FontManager.LCD_MEDIUM);
        g2.drawString("W and S to tune AI level | A and D to choose animatronic", 100, GamePanel.HEIGHT - 75);
        g2.drawString("Q and E to tune all animatronics", 100, GamePanel.HEIGHT - 50);

        // CHALLENGES AND PRESETS
        drawChallenges(g2);
        drawPresets(g2);

        // SUBTLE STATIC AND SCANLINES
        Utility.drawStatic(g2, 1, 10, new Color(255, 255, 255));
        Utility.drawAmbientScanlines(g2, new Color(255, 255, 255, 20), 1);
        Utility.drawCRTScanlines(g2, 4, 2, 80);
    }

    private void drawAnimatronics(Graphics2D g2)
    {
        int[] rowX    = { 100, 400, 700, 100, 400, 700 };
        int[] rowY    = { 150, 150, 150, 400, 400, 400 };
        int[] labelY  = { 140, 140, 140, 390, 390, 390 };

        BufferedImage[] images = { dave, earl, tyrone, cristian, jirsten, lanze };
        String[] names = { "Dave", "Earl", "Tyrone", "Cristian", "Jirsten", "Lanze" };

        g2.setFont(FontManager.LCD_SMALL);
        for (int i = 0; i < images.length; i++)
        {
            g2.drawString(names[i], rowX[i], labelY[i]);
            g2.drawImage(images[i], rowX[i], rowY[i], 150, 150, null);
        }
    }

    private void drawSelection(Graphics2D g2, int i)
    {
        int[] rowX = { 100, 400, 700, 100, 400, 700 };
        int[] columnY = { 350, 350, 350, 600, 600, 600 };
        boolean sel = (i == selectedRow);

        // HIGHLIGHT SELECTED ROW
        if(sel)
        {
            g2.setColor(new Color(255, 255, 255, 20));
            g2.fillRect(rowX[i] - 25, columnY[i] - 235, 200, 250);
        }

        // ARROWS
        g2.setFont(FontManager.LCD_LARGE);

        g2.setColor(sel ? Color.WHITE : Color.DARK_GRAY);
        g2.drawString("<", rowX[i], columnY[i]);

        g2.drawString(">", rowX[i] + 60, columnY[i]);

        // AI LEVEL
        g2.setColor(Color.WHITE);
        String level = String.format("%2d", aiLevels[i]);
        g2.drawString(level, rowX[i] + 20, columnY[i]);
    }

    private void drawChallenges(Graphics2D g2)
    {
        g2.setFont(FontManager.LCD_LARGE);
        g2.setColor(Color.WHITE);
        g2.drawString("CHALLENGES", 1000, 175);

        drawCheckbox(g2, "NO FOOTSTEPS", noFootsteps, 1000, 225);
        drawCheckbox(g2, "ZERO SHOCK START", zeroShocks, 1000, 275);
        drawCheckbox(g2, "20s REBOOTS", longReboot, 1000, 325);
        drawCheckbox(g2, "JIRSTEN'S PHANTOM", hallucinations, 1000, 375);
        drawCheckbox(g2, "HALVED PATIENCE", halvedPatience, 1000, 425);
        drawCheckbox(g2, "SPEEDY LULLABY", superboost, 1000, 475);
    }

    private void drawCheckbox(Graphics2D g2, String label, boolean checked, int x, int y)
    {
        // BOX
        g2.setColor(checked ? Color.WHITE : Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y - 15, 15, 15);

        // CHECKMARK
        if (checked)
        {
            g2.drawLine(x + 2, y - 8, x + 6, y - 4);
            g2.drawLine(x + 6, y - 4, x + 13, y - 13);
        }

        g2.setFont(FontManager.LCD_SMALL);
        g2.setColor(checked ? Color.WHITE : Color.LIGHT_GRAY);
        g2.drawString(label, x + 22, y);
        g2.setStroke(new BasicStroke(1));
    }

    private void drawPresets(Graphics2D g2)
    {
        int centerX = 1100; // CENTERED OVER CHALLENGE CHECKBOXES
        int y = 530;

        g2.setFont(FontManager.LCD_LARGE);
        g2.setColor(Color.WHITE);
        g2.drawString("PRESETS", 1000, y);

        g2.setFont(FontManager.LCD_LARGE);
        g2.drawString("<", 1000, y + 40);

        String presetName = selectedPreset >= 0 ? PRESETS[selectedPreset].name() : "NONE";
        g2.setFont(FontManager.LCD_SMALL);
        g2.setColor(selectedPreset != 0 ? Color.WHITE : Color.DARK_GRAY);
        int nameX = 1030;
        g2.drawString(presetName, nameX, y + 35);

        g2.setColor(Color.WHITE);
        g2.setFont(FontManager.LCD_LARGE);
        g2.drawString(">", 1170, y + 40);
    }

    @Override
    public void keyPressed(int key)
    {
        switch(key)
        {
            case KeyEvent.VK_UP, KeyEvent.VK_W ->
                    aiLevels[selectedRow] = Math.min(20, aiLevels[selectedRow] + 1);

            case KeyEvent.VK_DOWN, KeyEvent.VK_S ->
                    aiLevels[selectedRow] = Math.max(0, aiLevels[selectedRow] - 1);

            case KeyEvent.VK_LEFT, KeyEvent.VK_A ->
                    selectedRow = (selectedRow - 1 + NAMES.length) % NAMES.length;

            case KeyEvent.VK_RIGHT, KeyEvent.VK_D ->
                    selectedRow = (selectedRow + 1) % NAMES.length;

            case KeyEvent.VK_Q ->
            {
                for(int i = 0; i < aiLevels.length; i++)
                    aiLevels[i] = Math.max(0, aiLevels[i] - 1);
            }

            case KeyEvent.VK_E ->
            {
                for(int i = 0; i < aiLevels.length; i++)
                    aiLevels[i] = Math.min(20, aiLevels[i] + 1);
            }

            case KeyEvent.VK_ENTER -> startCustomNight();
            case KeyEvent.VK_ESCAPE -> stateManager.setState(StateManager.TITLE_STATE);
        }
    }

    private void applyPreset(PresetConfig preset)
    {
        for (int i = 0; i < aiLevels.length; i++)
            aiLevels[i] = preset.aiLevels()[i];

        noFootsteps = preset.challenges().noFootsteps();
        zeroShocks = preset.challenges().zeroShocks();
        longReboot = preset.challenges().longReboot();
        hallucinations = preset.challenges().hallucinations();
        halvedPatience = preset.challenges().halvedPatience();
        superboost = preset.challenges().superboost();
    }

    private void startCustomNight()
    {
        // SET CUSTOM CONFIG IN NIGHT MANAGER
        int[] levelsCopy = Arrays.copyOf(aiLevels, aiLevels.length);
        stateManager.getNightManager().setCustomConfig(new NightConfig(0, levelsCopy));

        // APPLY CHALLENGES
        stateManager.getNightManager().setChallengeConfig(
                new ChallengeConfig(noFootsteps, zeroShocks, longReboot,
                        hallucinations, halvedPatience, superboost));

        stateManager.forceUnloadGameState(); // CLEAR ANY PRELOADED GAME STATE
        stateManager.setState(StateManager.INTRO_STATE);
    }

    @Override
    public void mouseMoved(int x, int y)
    {
        // HIGHLIGHT ANIMATRONIC CARD ON HOVER
        int[] rowX   = { 100, 400, 700, 100, 400, 700 };
        int[] columnY = { 350, 350, 350, 600, 600, 600 };

        startHovered = y >= 650 && y <= 700 && x >= 20 && x <= 170;

        for (int i = 0; i < NAMES.length; i++)
        {
            if (x >= rowX[i] - 25 && x <= rowX[i] + 175
                    && y >= columnY[i] - 235 && y <= columnY[i] + 15)
            {
                selectedRow = i;
                return;
            }
        }
    }

    @Override
    public void mouseClicked(int x, int y)
    {
        int[] rowX    = { 100, 400, 700, 100, 400, 700 };
        int[] columnY = { 350, 350, 350, 600, 600, 600 };

        for (int i = 0; i < NAMES.length; i++)
        {
            // LEFT ARROW
            if (x >= rowX[i] - 10 && x <= rowX[i] + 20
                    && y >= columnY[i] - 25 && y <= columnY[i] + 10)
            {
                selectedRow = i;
                aiLevels[i] = Math.max(0, aiLevels[i] - 1);
                return;
            }

            // RIGHT ARROW
            if (x >= rowX[i] + 55 && x <= rowX[i] + 90
                    && y >= columnY[i] - 25 && y <= columnY[i] + 10)
            {
                selectedRow = i;
                aiLevels[i] = Math.min(20, aiLevels[i] + 1);
                return;
            }
        }

        // START BUTTON
        if (y >= 600 && y <= 640 && x >= 1000 && x <= 1200)
            startCustomNight();

        // PRESET BUTTONS
        if (y >= 545 && y <= 575)
        {
            // LEFT ARROW
            if (x >= 1000 && x <= 1025)
            {
                selectedPreset = selectedPreset <= 0
                        ? PRESETS.length - 1
                        : selectedPreset - 1;
                applyPreset(PRESETS[selectedPreset]);
                return;
            }

            // RIGHT ARROW
            if (x >= 1170 && x <= 1200)
            {
                selectedPreset = (selectedPreset + 1) % PRESETS.length;
                applyPreset(PRESETS[selectedPreset]);
                return;
            }
        }

        // CHALLENGE CHECKBOXES
        if (x >= 1000 && x <= 1200)
        {
            if (y >= 210 && y <= 240) noFootsteps = !noFootsteps;
            if (y >= 260 && y <= 290) zeroShocks = !zeroShocks;
            if (y >= 310 && y <= 340) longReboot = !longReboot;
            if (y >= 360 && y <= 390) hallucinations = !hallucinations;
            if (y >= 410 && y <= 440) halvedPatience = !halvedPatience;
            if (y >= 460 && y <= 490) superboost = !superboost;
            return;
        }
    }

    @Override public void keyReleased(int key) {}
}
