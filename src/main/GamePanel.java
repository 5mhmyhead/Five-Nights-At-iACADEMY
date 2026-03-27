package main;

import state.StateManager;
import utilities.FontManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseMotionListener, MouseListener
{
    // GLOBAL SCREEN SETTINGS
    public static final int WIDTH  = 1280;
    public static final int HEIGHT = 720;

    private int scaledOffsetX = 0;
    private int scaledOffsetY = 0;
    private float scaleFactor = 1f;

    // THREAD AND FPS SETTINGS
    public static final int FPS = 30;

    private Thread gameThread;
    private boolean running;

    // CLASS THAT HANDLES THE CHANGING OF STATES IN THE GAME
    private StateManager stateManager;

    GamePanel()
    {
        super();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.requestFocus();
        init();
    }

    @Override
    public void addNotify()
    {
        super.addNotify();
        addMouseMotionListener(this);
        addMouseListener(this);

        if(gameThread == null)
        {
            gameThread = new Thread(this);
            this.addKeyListener(this);
            gameThread.start();
        }
    }

    private void init()
    {
        FontManager.loadFonts();
        new javafx.embed.swing.JFXPanel();
        stateManager = new StateManager();
        running = true;
    }

    @Override
    public void run()
    {
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;

        long lastUpdate = System.nanoTime();
        long currentTime;

        while(running)
        {
            currentTime = System.nanoTime();

            delta += (currentTime - lastUpdate) / drawInterval;
            lastUpdate = currentTime;

            if(delta >= 1)
            {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() { stateManager.update(); }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // FILL ENTIRE PANEL WITH BLACK FIRST
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // CALCULATE SCALE TO FIT 1280x720 INSIDE CURRENT WINDOW SIZE
        float scaleX = (float) getWidth()  / WIDTH;
        float scaleY = (float) getHeight() / HEIGHT;
        scaleFactor  = Math.min(scaleX, scaleY);

        // CENTER THE GAME AREA
        scaledOffsetX = (int)((getWidth()  - WIDTH  * scaleFactor) / 2);
        scaledOffsetY = (int)((getHeight() - HEIGHT * scaleFactor) / 2);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // APPLY TRANSFORM
        g2.translate(scaledOffsetX, scaledOffsetY);
        g2.scale(scaleFactor, scaleFactor);

        // ADD ANTIALIASING
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        stateManager.draw(g2);
        g2.dispose();
    }

    @Override public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_F11)
            Main.toggleFullscreen();

        stateManager.keyPressed(e.getKeyCode());
    }

    @Override public void keyReleased(KeyEvent e)
    {
        if(e != null)
            stateManager.keyReleased(e.getKeyCode());
    }

    @Override public void mousePressed(MouseEvent e)
    {
        stateManager.mouseClicked(toGameX(e.getX()), toGameY(e.getY()));
    }

    @Override public void mouseMoved(MouseEvent e)
    {
        stateManager.mouseMoved(toGameX(e.getX()), toGameY(e.getY()));
    }

    private int toGameX(int screenX) { return (int)((screenX - scaledOffsetX) / scaleFactor); }
    private int toGameY(int screenY) { return (int)((screenY - scaledOffsetY) / scaleFactor); }

    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
