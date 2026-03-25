package main;

import utilities.Utility;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class Main
{
    public static JFrame window;
    BufferedImage icon = Utility.loadImage("/icons/icon.png");

    public static void main(String[] args) { new Main().startGame(); }

    public void startGame()
    {
        window = new JFrame("FIVE NIGHTS AT iACADEMY");

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setIconImage(icon);
        window.add(new GamePanel());
        window.setResizable(false);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}