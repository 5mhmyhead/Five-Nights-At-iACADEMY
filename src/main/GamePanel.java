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
        stateManager = new StateManager();
        running = true;
    }

    @Override
    public void run()
    {
        init();

        double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;

        long lastUpdate = System.nanoTime();
        long currentTime;

        while(running)
        {
            currentTime = System.nanoTime();

            delta += (currentTime - lastUpdate) / drawInterval;
            lastUpdate = currentTime;

            // ONLY UPDATE AND DRAW ONCE FULL FRAME TIME HAS ACCUMULATED
            if(delta >= 1)
            {
                update();
                repaint();
                delta--;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        // ANTIALIASING
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // DRAWS THE GAME
        stateManager.draw(g2);
        g2.dispose();
    }

    private void update() { stateManager.update(); }

    @Override public void keyPressed(KeyEvent e)
    {
        if(e != null)
            stateManager.keyPressed(e.getKeyCode());
    }

    @Override public void keyReleased(KeyEvent e)
    {
        if(e != null)
            stateManager.keyReleased(e.getKeyCode());
    }

    @Override public void mousePressed(MouseEvent e)
    {
        stateManager.mouseClicked(e.getX(), e.getY());
    }

    @Override public void mouseMoved(MouseEvent e)
    {
        stateManager.mouseMoved(e.getX(), e.getY());
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
