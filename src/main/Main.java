package main;

import javax.swing.*;

public class Main
{
    public static JFrame window;
    public static void main(String[] args) { new Main().startGame(); }

    public void startGame()
    {
        window = new JFrame("FIVE NIGHTS AT iACADEMY");

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(new GamePanel());
        window.setResizable(false);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}