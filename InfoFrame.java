package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

/**
 * InfoFrame class.
 * <p/>
 * Simulation loop and graphics initialisation done here.
 *
 * @author Ian Weeks (09/07/2015).
 */
public class InfoFrame extends Canvas
{
    private Parser parser;
    private BufferStrategy bufferStrategy;
    private boolean isPause = false, runSim = true;

    /**
     * InfoFrame constructor.
     * <p/>
     * Window creation and customisation.
     */
    public InfoFrame()
    {
        //Window title, close operation, dimension setup.
        JFrame jFrame = new JFrame("Vessel Travel Simulation");

        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel jPanel = (JPanel) jFrame.getContentPane();
        jPanel.setPreferredSize(new Dimension(1000, 800));
        jFrame.setIgnoreRepaint(true);

        //Panel size setup.
        this.setBounds(0, 0, 1000, 800);
        jPanel.add(InfoFrame.this);
        this.setBounds(0, 0, 1000, 400);
        jFrame.setLocation(0, 0);
        jFrame.pack();
        jFrame.setVisible(true);

        //Double buffer for canvas.
        this.createBufferStrategy(2);
        bufferStrategy = this.getBufferStrategy();

        //Handle key strokes.
        this.addKeyListener(new KeyListener()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                switch (e.getKeyCode())
                {
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0);
                        break;
                    case KeyEvent.VK_UP:
                        parser.increaseParseSpeed();
                        break;
                    case KeyEvent.VK_DOWN:
                        parser.decreaseParseSpeed();
                        break;
                    case KeyEvent.VK_P:
                        isPause = !isPause;
                        break;
                    case KeyEvent.VK_E:
                        runSim = !runSim;
                        break;
                    default:
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
            }

            @Override
            public void keyTyped(KeyEvent e)
            {
            }
        });
        //CsvData220315.csv
        parser = new Parser("CsvData160315_Edit.csv");


        //Start the simulation loop.
        startLoop();
    }

    /**
     * Game loop set to 15 fps refresh rate.
     */
    private void loop()
    {
        long lastTime;

        while (runSim)
        {
            //Data is recorded at 15 Hz so 15 frames per second is ~ to real time.
            int targetFPS = 15;
            long optTime = 1000000000 / targetFPS;
            if (!isPause)
            {
                lastTime = System.nanoTime();

                update();
                render();

                try
                {
                    long sleepTimer = ((lastTime - System.nanoTime() + optTime) / 1000000);
                    if (sleepTimer > 0)
                    {
                        Thread.sleep((lastTime - System.nanoTime() + optTime) / 1000000);
                    } else
                    {
                        /*
                         * Was getting an error of negative numbers
                         * on first frame. This was my fix. It works...
                         */
                        Thread.sleep(15);
                    }

                } catch (Exception e)
                {
                    System.out.println("Error in thread sleep: " + e);
                }
            }
        }
    }

    /**
     * Update class state.
     */
    public void update()
    {
        //Update parser.
        parser.update();
    }

    /**
     * Render images and text to the screen.
     */
    public void render()
    {
        //Setup graphics object
        Graphics2D g2d = (Graphics2D) bufferStrategy.getDrawGraphics();

        //Refresh screen
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, 1000, 800);

        //Render parser.
        parser.render(g2d);

        g2d.dispose();
        bufferStrategy.show();
    }

    /**
     * Start the game loop.
     */
    public void startLoop()
    {
        //Loop started in separate thread for performance.
        Thread theLoop = new Thread()
        {
            @Override
            public void run()
            {
                loop();
            }
        };
        theLoop.start();
    }
}
