package com.almasb.java.framework.demo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class BufferStrategyDemo extends JFrame {

    private int x = 0, y = 0;

    private boolean dx = false, dy = false;

    private BufferStrategy strategy;

    public BufferStrategyDemo() {

        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        createBufferStrategy(2);
        strategy = this.getBufferStrategy();


        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {


                    // Prepare for rendering the next frame
                    x += dx ? -1 : 1;
                    y += dy ? -1 : 1;


                    if (x > 1280 - 60)
                        dx = true;

                    if (x < 0)
                        dx = false;

                    if (y < 0)
                        dy = false;

                    if (y > 720-60)
                        dy = true;


                    // Render single frame
                    do {
                        // The following loop ensures that the contents of the drawing buffer
                        // are consistent in case the underlying surface was recreated
                        do {
                            // Get a new graphics context every time through the loop
                            // to make sure the strategy is validated
                            Graphics graphics = strategy.getDrawGraphics();

                            // Render to graphics
                            // ...

                            Graphics2D g2d = (Graphics2D) graphics;

                            g2d.setColor(Color.WHITE);
                            g2d.fillRect(0, 0, 1280, 720);

                            g2d.setColor(Color.RED);
                            g2d.drawRect(x, y, 60, 60);

                            // Dispose the graphics
                            graphics.dispose();

                            // Repeat the rendering if the drawing buffer contents
                            // were restored
                        } while (strategy.contentsRestored());

                        // Display the buffer
                        strategy.show();

                        // Repeat the rendering if the drawing buffer was lost
                    } while (strategy.contentsLost());





                    //repaint();

                    try {
                        Thread.sleep(16);
                    }
                    catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, 1280, 720);

        g2d.setColor(Color.RED);
        g2d.drawRect(x, y, 60, 60);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }
}
