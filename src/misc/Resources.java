package com.almasb.java.io;

import java.applet.AudioClip;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import com.almasb.common.graphics.Dimension2D;
import com.almasb.common.util.Out;

/**
 * Stores all resources of the application
 *
 * Note: all cached resources can only be loaded automatically from "res/"
 * folder, for other folders you have to use ResourceManager and load manually
 *
 * @author Almas
 * @version 1.4
 *
 *          v 1.1 - text cache support
 *          v 1.2 - no longer need to load resources manually,
 *          static init block takes care of that, i.e. all resources
 *          will be loaded just before the 1st request to cache
 *          v 1.3 - refactor to support android/java model
 *          for autoload from res/ you need to copy over the extract (drawable, raw) from R.class
 *          from android (R.class has to be in the root package of your project)
 *          Use double underscore __ instead of /
 *          for directories and single underscore _ for spaces in names
 *
 *          v 1.4 - can retrieve resources by name and by id
 */
public final class Resources {

    /**
     * Stores cached image resources
     */
    private static HashMap<Integer, BufferedImage> cachedImages = new HashMap<Integer, BufferedImage>();
    private static HashMap<String, BufferedImage> cachedImagesString = new HashMap<String, BufferedImage>();

    /**
     * Stores cached audio resources
     */
    private static HashMap<Integer, AudioClip> cachedAudioClips = new HashMap<Integer, AudioClip>();
    private static HashMap<String, AudioClip> cachedAudioClipsString = new HashMap<String, AudioClip>();

    /**
     * Stores cached text files
     */
    private static HashMap<String, List<String>> cachedTextFiles = new HashMap<String, List<String>>();

    /**
     * Do not instantiate
     */
    private Resources() {}

    /**
     * Must call this before accessing any resources
     *
     * @param drawableR
     * @param rawR
     */
    @SuppressWarnings("rawtypes")
    public static void init(Class drawableR, Class rawR) {
        Field[] drawableFields = drawableR.getDeclaredFields();
        for (Field f : drawableFields) {
            try {
                int resID = f.getInt(drawableR);
                String resName = f.getName() + ".png";
                BufferedImage image = ResourceManager.loadImage(resName);
                cachedImages.put(resID, image);
                cachedImagesString.put(resName, image);
            }
            catch (IllegalAccessException | IllegalArgumentException | IOException e) {
                Out.e("Resources.init()", "com.almasb.java.io.Resources Failed to load drawable: " + f.getName(), e);
            }
        }

        Field[] rawFields = rawR.getDeclaredFields();
        for (Field f : rawFields) {
            try {
                int resID = f.getInt(rawR);
                String resName = f.getName() + ".wav";
                AudioClip clip = ResourceManager.loadAudio(resName);
                cachedAudioClips.put(resID, clip);
                cachedAudioClipsString.put(resName, clip);
            }
            catch (Exception e) {
                Out.e("Resources.init()", "com.almasb.java.io.Resources Failed to load raw: " + f.getName(), e);
            }
        }

        ResourceManager.resourceFileNames.stream()
        .filter(fileName -> fileName.endsWith(".txt"))
        .forEach(fileName -> cachedTextFiles.put(fileName, ResourceManager.loadText(fileName)));

        Out.i("Resources loaded: " + getSize());
    }

    /**
     * Retrieve image from cache
     *
     * @param resID
     *              id of the image
     * @return the image
     */
    public static BufferedImage getImage(int resID) {
        return cachedImages.get(resID);
    }

    /**
     * Retrieve image from cache
     *
     * @param fileName
     *            name of the image file
     * @return the image
     */
    public static BufferedImage getImage(String resName) {
        return cachedImagesString.get(resName);
    }

    /**
     * Retrieve audio from cache
     *
     * @param resID
     *            id of the audio file
     * @return the audio
     */
    public static AudioClip getAudio(int resID) {
        return cachedAudioClips.get(resID);
    }

    /**
     * Retrieve audio from cache
     *
     * @param fileName
     *            name of the audio file
     * @return the audio
     */
    public static AudioClip getAudio(String resName) {
        return cachedAudioClipsString.get(resName);
    }

    /**
     * Retrieve text from cache
     *
     * Lines of text are represented by String in a list
     *
     * @param fileName
     *            name of the file
     * @return lines of text in a list
     */
    public static List<String> getText(String fileName) {
        return cachedTextFiles.get(fileName);
    }

    /**
     *
     * @return
     *          number of resource files currently held in cache
     */
    public static int getSize() {
        return cachedImages.size() + cachedAudioClips.size() + cachedTextFiles.size();
    }

    public static Dimension2D getImageDimension(int resID) {
        BufferedImage bmp = cachedImages.get(resID);
        if (bmp != null)
            return new Dimension2D(bmp.getWidth(), bmp.getHeight());

        return new Dimension2D(0, 0);
    }
}
