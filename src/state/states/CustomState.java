package state.states;

import components.nights.NightConfig;
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
        Utility.drawCentered(g2, "ENTER to start  |  ESC to go back", GamePanel.HEIGHT - 25);

        g2.setColor(Color.DARK_GRAY);
        g2.setFont(FontManager.LCD_MEDIUM);
        Utility.drawCentered(g2, "W and S to tune AI level | A and D to choose animatronic", GamePanel.HEIGHT - 75);
        Utility.drawCentered(g2, "Q and E to tune all animatronics", GamePanel.HEIGHT - 50);

        // SUBTLE STATIC AND SCANLINES
        Utility.drawStatic(g2, 1, 10, new Color(255, 255, 255));
        Utility.drawAmbientScanlines(g2, new Color(255, 255, 255, 20), 1);
        Utility.drawCRTScanlines(g2, 4, 2, 80);
    }

    private void drawAnimatronics(Graphics2D g2)
    {
        g2.setFont(FontManager.LCD_SMALL);

        g2.drawString("Dave", 200, 140);
        g2.drawImage(dave, 200, 150, 150, 150, null);

        g2.drawString("Earl", GamePanel.WIDTH / 2 - 75, 140);
        g2.drawImage(earl, GamePanel.WIDTH / 2 - 75, 150, 150, 150, null);

        g2.drawString("Tyrone", GamePanel.WIDTH - 350, 140);
        g2.drawImage(tyrone, GamePanel.WIDTH - 350, 150, 150, 150, null);

        g2.drawString("Cristian", 200, 390);
        g2.drawImage(cristian, 200, 400, 150, 150, null);

        g2.drawString("Jirsten", GamePanel.WIDTH / 2 - 75, 390);
        g2.drawImage(jirsten, GamePanel.WIDTH / 2 - 75, 400, 150, 150, null);

        g2.drawString("Lanze", GamePanel.WIDTH - 350, 390);
        g2.drawImage(lanze, GamePanel.WIDTH - 350, 400, 150, 150, null);
    }

    private void drawSelection(Graphics2D g2, int i)
    {
        int[] rowX = { 200, 565, 930, 200, 565, 930 };
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

    private void startCustomNight()
    {
        // SET CUSTOM CONFIG IN NIGHT MANAGER
        int[] levelsCopy = Arrays.copyOf(aiLevels, aiLevels.length);
        stateManager.getNightManager().setCustomConfig(new NightConfig(0, levelsCopy));
        stateManager.forceUnloadGameState(); // CLEAR ANY PRELOADED GAME STATE
        stateManager.setState(StateManager.INTRO_STATE);
    }

    @Override public void keyReleased(int key) {}
    @Override public void mouseMoved(int x, int y) {}
    @Override public void mouseClicked(int x, int y) {}
}
