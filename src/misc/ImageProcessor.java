package com.almasb.java.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

import com.almasb.common.util.Out;

public class ImageProcessor {

    /**
     * Converts an image to a 2d int map, if {@code positiveImage} flag is on
     * then 1 is where tile of tileSize consists of at least 70% of the color {@code target}
     * the other tiles will be 0.
     *
     * Changing positiveImage flag flips 1s and 0s
     *
     * @param img
     *              the image to parse
     * @param target
     *               target color of the tile
     * @param tileSize
     *                  size of the tile (assuming it's a square)
     * @param positiveImage
     *                      writes 1s in a file where image is recognized if flag is true
     *                      otherwise 0s
     * @return
     *          the 2d int map with 1s where image is similar, 0s other tiles
     */
    public static int[][] parseToMap(BufferedImage img, Color target, int tileSize, boolean positiveImage) {
        int columns = img.getWidth() / tileSize;
        int rows = img.getHeight() / tileSize;

        int[][] map = new int[columns][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (isTileEqualToTarget(img, j*tileSize, i*tileSize, target.getRGB(), tileSize)) {
                    map[j][i] = positiveImage ? 1 : 0;
                }
                else {
                    map[j][i] = positiveImage ? 0 : 1;
                }
            }
        }

        write(map); // not necessary

        return map;
    }

    private static boolean isTileEqualToTarget(BufferedImage img, int x, int y, int rgb, int tileSize) {
        int similarity = 0;

        for (int i = y; i < y + tileSize; i++) {
            for (int j = x; j < x + tileSize; j++) {
                if (img.getRGB(j, i) == rgb)
                    similarity++;
            }
        }
        return similarity / (tileSize * tileSize * 1.0) > 0.7; // consider 70+% good enough
    }

    private static void write(int[][] map) {
        try (PrintWriter pw = new PrintWriter("processed_map.txt")) {
            for (int i = 0; i < map[0].length; i++) {
                for (int j = 0; j < map.length; j++) {
                    pw.write(map[j][i] + "");
                }
                pw.append('\n');
            }
        }
        catch (IOException e) {
            Out.e("ImageProcessor.write()", "Couldn't create file", e);
        }
    }

    public static byte[] imageToByteArray(BufferedImage img) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", baos);
            baos.flush();
        }
        catch (IOException e) {
            Out.e("ImageProcessor.imageToByteArray()", "Couldn't convert image to BAOS", e);
        }
        // baos close() does nothing, so no need to call

        return baos.toByteArray();
    }

    public static BufferedImage imageFromByteArray(byte[] data) {
        try {
            return ImageIO.read(new ByteArrayInputStream(data));
        }
        catch (IOException e) {
            Out.e("ImageProcessor.imageFromByteArray()", "Couldn't convert byte[] to image", e);
        }

        return null;
    }
}
