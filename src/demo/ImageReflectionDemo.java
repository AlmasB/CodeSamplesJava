package com.almasb.java.framework.demo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class ImageReflectionDemo extends JComponent {
    @Override
    public void paintComponent(Graphics g) {
        try {
            BufferedImage image = ImageIO.read(new File("ball.png"));
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            int gap = 20;
            float opacity = 0.4f;
            float fadeHeight = 0.3f;

            g2d.translate((width - imageWidth) / 2, height / 2 - imageHeight);
            g2d.drawRenderedImage(image, null);
            g2d.translate(0, 2 * imageHeight + gap);
            g2d.scale(1, -1);

            BufferedImage reflection = new BufferedImage(imageWidth,
                    imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D rg = reflection.createGraphics();
            rg.drawRenderedImage(image, null);
            rg.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
            rg.setPaint(new GradientPaint(0, imageHeight * fadeHeight,
                    new Color(0.0f, 0.0f, 0.0f, 0.0f), 0, imageHeight,
                    new Color(0.0f, 0.0f, 0.0f, opacity)));

            rg.fillRect(0, 0, imageWidth, imageHeight);
            rg.dispose();
            g2d.drawRenderedImage(reflection, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Reflection");
        ImageReflectionDemo r = new ImageReflectionDemo();
        frame.add(r);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
