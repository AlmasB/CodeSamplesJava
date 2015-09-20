package com.almasb.java.io;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javafx.scene.image.Image;

import javax.imageio.ImageIO;

import com.almasb.common.util.Out;

/**
 * This class is used to load resources from various places including file
 * system and Internet
 *
 * At the moment the methods working with file system assume that file is
 * to be read/written in the project's res/ folder
 *
 * All places where {@code fileName} is required use names as if you are in
 * default resources folder
 *
 * The methods were tested on Windows and Linux
 *
 * {@link #loadFileNames(File)} and {@link #loadFileNamesJar(String)} are not
 * for general use yet
 *
 * @author Almas
 * @version 1.9
 *
 *          v 1.1 - ability to use resources within program instead of just
 *          downloading to disk v 1.2 - autoload of application resources from
 *          default resources folder v 1.3 - fixed a bug in
 *          {@link #loadResourceAsStream(String)} when absolute path was used
 *          instead of relative in res/ Added object loader
 *          v 1.4 - added object writer
 *          v 1.5 - jar running query, minor fixes
 *          v 1.6 - load resource as byte[], better exception reporting
 *          v 1.7 - use of Optional, thorough file search if not found in
 *          expected folder, throw exceptions to outside world to make them think
 *          about exception handling
 *          v 1.8 - merged ByteWriter and PlainTextWriter, fixed bugs
 *          v 1.9 - writeJavaObject is now crossplatform, all bugs related to
 *          unclosed streams when exceptions occurred have been fixed
 */
public final class ResourceManager {

    /**
     * Do not instantiate
     */
    private ResourceManager() {}

    /**
     * Default buffer size of read/write operations
     */
    private static final int BUFFER_SIZE = 8192;

    /**
     * Running directory
     */
    private static final String RUN_DIR = System.getProperty("user.dir") + "/";

    /**
     * Default resources folder
     */
    private static final String RES_DIR = "res/";

    /**
     * @return {@code true} if the application is running from jar (java -jar)
     *         {@code false} if the application is running normally (java)
     */
    private static boolean runningFromJar = false;

    /**
     * Holds file names of this application's resources To be precise all file
     * names in {@link #RES_DIR}
     */
    /*package-private*/ static final ArrayList<String> resourceFileNames = new ArrayList<String>();

    static {
        if (!loadFileNames(new File(RUN_DIR + RES_DIR))) {
            loadFileNamesJar(RES_DIR);
            runningFromJar = true;
        }

        Out.i(resourceFileNames.size() + " resource file(-s) found in " + RES_DIR);
    }

    /**
     * @return {@code true} if the application is running from jar (java -jar)
     *         {@code false} if the application is running normally (java)
     */
    public static boolean isRunningFromJar() {
        return runningFromJar;
    }

    /**
     * Loads file names from a folder/directory
     *
     * If it contains other folders they'll be searched too
     *
     * @param folder
     *            folder files of which need to be retrieved
     * @return {@code true} if file names were found {@code false} if I/O error
     *         occurred or running from jar
     */
    private static boolean loadFileNames(File folder) {
        // TODO: check out file filters FileFilter filter =
        // new FileNameExtensionFilter("Resource files", "jpg", "jpeg",
        // ".png", ".txt");
        // if needed
        File[] allFiles = folder.listFiles();
        if (allFiles == null) {
            return false;
        }

        for (File aFile : allFiles) {
            if (aFile.isDirectory()) {
                loadFileNames(aFile);
            }
            else {
                //String name = aFile.toString().replace("\\", "/");
                //resourceFileNames.add(name.substring(name.indexOf("res/") + 4));
                String name = aFile.toString();
                resourceFileNames.add(name.substring(name.indexOf("res" + File.separator) + 4));
            }
        }
        return true;
    }

    /**
     * Loads file names from a folder/directory when running within a jar
     *
     * If it contains other folders they'll be searched too
     *
     * @param folderName
     *            folder files of which need to be retrieved
     */
    private static void loadFileNamesJar(String folderName) {
        CodeSource src = Resources.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jar = src.getLocation();
            try (InputStream is = jar.openStream();
                    ZipInputStream zip = new ZipInputStream(is)) {
                ZipEntry ze = null;
                while ((ze = zip.getNextEntry()) != null) {
                    String entryName = ze.getName();
                    if (entryName.startsWith(folderName)) {
                        resourceFileNames.add(entryName.substring(entryName.indexOf(folderName) + folderName.length()));
                    }
                }
            }
            catch (IOException e) {
                Out.e("ResourceManager.loadJarFileNames()", "Coudn't load file names from jar", e);
            }
        }
        else {
            Out.e("ResourceManager.loadJarFileNames()", "no code source", null);
        }
    }

    /**
     * Creates URL out of given String link
     *
     * @param link
     *            String representation of a URL
     * @return created URL or {@code null} if MalformedURLException was thrown
     */
    public static URL createRemoteURL(String link) {
        URL url = null;
        try {
            url = new URL(link);
            return url;
        }
        catch (MalformedURLException e) {
            Out.e("ResourceManager.createRemoteURL()", "No legal protocol was found in a specification string or the string could not be parsed", e);
        }
        Out.i("ResourceManager.createRemoteURL()", "Couldn't create remote URL");
        return url;
    }

    /**
     * Get local URL of a file
     *
     * At the moment only returns URL to res/ folder
     *
     * TODO: generalise and better doc
     *
     * @param fileName
     *                  file name
     * @return
     *          url to file
     */
    public static URL getLocalURL(String fileName) {
        URL url = null;
        try {
            if (fileName.contains("/")) {
                fileName = fileName.substring(0, fileName.lastIndexOf("/")+1);
            }
            else {
                fileName = "";
            }

            if (!runningFromJar) {
                url = new URL("FILE:///" + RUN_DIR.replace("\\", "/") + RES_DIR.replace("\\", "/") + fileName);
            }
            else {
                url = ResourceManager.class.getClassLoader().getResource(RES_DIR + fileName);
            }


            return url;
        }
        catch (MalformedURLException e) {
            Out.e("ResourceManager.createLocalURL()", "No legal protocol was found in a specification string or the string could not be parsed", e);
        }
        Out.i("ResourceManager.createLocalURL()", "Couldn't create local URL");
        return url;
    }

    /**
     * Loads resource as InputStream from file
     *
     * The file will be searched for in different commonly used
     * folders. However, this is not an encouragement to resources lying
     * around. A typical way to carry resources is to use "res/" folder
     * in root of the project and in root of the runnable jar
     *
     * The order in which the file will be searched for:
     *
     * 1. As relative path under "res/" folder
     * OR if running within jar under "res/" folder in its jar
     * 2. As relative path under running directory
     * OR if running within jar under running directory in its jar
     * 3. As absolute path
     * 4. As relative path under "src/" folder
     *
     * Note: Caller must close the returned InputStream to avoid resource leak
     *
     * @param fileName
     *            name of the resource
     * @return
     *          Optional containing InputStream from the resource
     *          or Optional.empty() if resource can't be found / error occurred
     */
    @SuppressWarnings("resource")
    public static Optional<InputStream> loadResourceAsStream(String fileName) {
        InputStream is = null;

        if (!runningFromJar) {
            try {
                is = new FileInputStream(RUN_DIR + RES_DIR + fileName);
            }
            catch (FileNotFoundException e) {
                try {
                    is = new FileInputStream(RUN_DIR + fileName);
                }
                catch (FileNotFoundException e1) {} // ignore for now, show 1 exception at the end
            }
        }
        else {  // running from jar
            is = ResourceManager.class.getClassLoader().getResourceAsStream(RES_DIR + fileName);
            if (is == null)
                is = ResourceManager.class.getClassLoader().getResourceAsStream(fileName);
        }

        // if stream is still null try other ways
        if (is == null) {
            is = ResourceManager.class.getResourceAsStream("/" + fileName);

            if (is == null)
                is = ResourceManager.class.getResourceAsStream("/" + RES_DIR + fileName);

            if (is == null) {
                try {
                    is = new FileInputStream(fileName);
                }
                catch (FileNotFoundException e) {
                    // we have tried many common folders and absolute path
                    // and failed so show error
                    Out.e("ResourceManager.loadResourceAsStream()", "Couldn't load file: " + fileName, e);
                }
            }
        }

        return Optional.ofNullable(is);
    }

    /**
     * Loads resource as byte[]
     *
     * @param fileName
     * @return
     *          resource as bytes
     * @throws ResourceLoadException
     */
    public static byte[] loadResourceAsByteArray(String fileName) throws IOException {
        try (InputStream is = loadResourceAsStream(fileName).orElseThrow(ResourceLoadException::new)) {
            return getBytes(is);
        }
    }

    /**
     * Downloads an Internet resource as InputStream which then can be used as a
     * program resource or saved to disk as a file
     *
     * Caller must close this InputStream to avoid resource leak
     *
     * @param link
     *            the url of the resource as string
     * @return InputStream from the resource or {@code null} if url is
     *         {@code null}
     */
    private static InputStream downloadResourceAsStream(String link) {
        URL url = createRemoteURL(link);
        if (url != null) {
            InputStream is = null;
            try {
                is = url.openStream();
                return is;
            }
            catch (IOException e) {
                Out.e(e);
            }
        }
        Out.i("ResourceManager.downloadResourceAsStream", "Couldn't download resource");
        return null;
    }

    /**
     * Loads an image from file
     *
     * @param fileName
     *            name of the file
     * @return loaded image
     * @throws IOException
     * @throws ResourceLoadException
     */
    public static BufferedImage loadImage(String fileName) throws IOException, ResourceLoadException {
        try (InputStream is = loadResourceAsStream(fileName).orElseThrow(ResourceLoadException::new)) {
            return ImageIO.read(is);
        }
    }

    /**
     * Loads an image from file
     *
     * @param fileName
     *            name of the file
     * @return loaded image
     * @throws ResourceLoadException
     */
    public static Image loadFXImage(String fileName) throws IOException {
        try (InputStream is = loadResourceAsStream(fileName).orElseThrow(ResourceLoadException::new)) {
            return new Image(is);
        }
    }

    /**
     * Loads an audio from file located under {@value #RES_DIR}
     * whether in .jar or running from eclipse
     *
     * @param fileName
     *            name of the file
     * @return loaded audioclip or {@code null} if resource wasn't found
     */
    public static AudioClip loadAudio(String fileName) throws Exception {
        URL url = null;
        if (runningFromJar) {
            url = ResourceManager.class.getClassLoader().getResource(RES_DIR + fileName);
        }
        else {
            File file = new File(RES_DIR + fileName);
            URI uri = file.toURI();
            url = uri.toURL();
        }

        if (url != null) {
            return Applet.newAudioClip(url);
        }

        throw new ResourceLoadException("ResourceManager.loadAudio() Couldn't load audio: " + fileName);
    }

    /**
     * Loads text from file
     *
     * @param fileName
     *            name of the file
     * @param ignoreEmtpyLines
     *            whether to ignore empty lines or not
     * @param ignoreLines
     *            lines starting with these char sequences will be ignored e.g.
     *            can use for comments where needed.
     *
     *            Note: DO NOT USE "" or all lines will be filtered
     * @return loaded text or empty list if error occurred
     */
    public static List<String> loadText(String fileName, boolean ignoreEmptyLines, String... ignoreLines) {
        final List<String> lines = new ArrayList<String>();

        loadResourceAsStream(fileName).ifPresent(stream -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                reading:
                    while ((line = br.readLine()) != null) {
                        if (ignoreEmptyLines && line.isEmpty())
                            continue reading;

                        for (String ignore : ignoreLines)
                            if (line.startsWith(ignore))
                                continue reading;

                        lines.add(line);
                    }

                stream.close();
            }
            catch (IOException e) {
                Out.e(e);
            }
        });

        return lines;
    }

    /**
     * Loads text from file in a list as it is (w/o any filtering) Lines in a
     * file are represented by lines in the list
     *
     * @param fileName
     *            name of the file
     * @return loaded text as a list or {@code null} if there was an error
     */
    public static List<String> loadText(String fileName) {
        return loadText(fileName, false);
    }

    /**
     * Loads a file with given fileName as a java object
     *
     * Can be typecasted into appropriate type provided user had written the
     * file using {@code ObjectOutputStream}
     *
     * @param fileName
     *            name of the file
     * @return a java object or {@code null} if any error occurs
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws ResourceLoadException
     */
    public static Object loadJavaObject(String fileName) throws IOException, ClassNotFoundException, ResourceLoadException {
        try (InputStream is = loadResourceAsStream(fileName).orElseThrow(ResourceLoadException::new);
                ObjectInputStream ois = new ObjectInputStream(is)) {
            return ois.readObject();
        }
    }

    /**
     * Writes a java object to a file
     *
     * @param fileName
     *                  name of file
     * @param obj
     *              object to write
     * @throws IOException
     */
    public static void writeJavaObject(String fileName, Object obj) throws IOException {
        Path file = getPathTo(fileName);
        createDirsTo(file);

        try (OutputStream os = Files.newOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeObject(obj);
        }
    }

    /**
     * Downloads a resource from Internet via {@code downloadResourceAsStream()}
     * Saves the resource to disk with given fileName
     *
     * @param link
     *            the Internet link of the resource
     * @param fileName
     *            name of the file to save as
     */
    private static void downloadResource(String link, String fileName) {
        InputStream is = downloadResourceAsStream(link);
        if (is != null) {
            try {
                write(is, fileName);
                is.close();
            }
            catch (IOException e) {
                Out.e(e);
            }
        }
        else {
            Out.i("ResourceManager.downloadResource", "Couldn't download resource " + link);
        }
    }

    /**
     * Downloads an html page from the Internet using given link and saves it as
     * {@code fileName}
     *
     * If specified {@code fileName} doesn't exist, it'll attempt to create it
     * and its parent directories
     *
     * @param link
     *            the Internet link of the page
     * @param fileName
     *            name of the file to save as
     */
    public static void downloadAsHtml(String link, String fileName) {
        if (link.startsWith("www.")) {
            link = "http://" + link;
        }

        if (!fileName.endsWith(".html") || !fileName.endsWith(".htm")) {
            fileName += ".html";
        }
        downloadResource(link, fileName);
    }

    /**
     * Downloads a resource from the Internet using given link and saves it as
     * {@code fileName} in plain text format
     *
     * If specified {@code fileName} doesn't exist, it'll attempt to create it
     * and its parent directories
     *
     * @param link
     *            the Internet link of the page
     * @param fileName
     *            name of the file to save as
     */
    public static void downloadAsText(String link, String fileName) {
        if (!fileName.endsWith(".txt") || !fileName.endsWith(".text")) {
            fileName += ".txt";
        }
        downloadResource(link, fileName);
    }

    /**
     * Downloads a resource from the Internet using given link and saves it as
     * {@code fileName}
     *
     * If specified {@code fileName} doesn't exist, it'll attempt to create it
     * and its parent directories
     *
     * @param link
     *            the Internet link of the page
     * @param fileName
     *            name of the file to save as
     */
    public static void downloadAsFile(String link, String fileName) {
        downloadResource(link, fileName);
    }

    /**
     * User-Agent of the Mozilla Firefox 23 browser running on Windows 7 x64 Can
     * be used with downloadResourceWithCustomRequest() as userAgent parameter
     */
    public static final String USER_AGENT_FIREFOX_23_WIN_7 = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0";

    /**
     * Downloads a resource from the Internet using given link and custom
     * request headers and saves it as {@code fileName}
     *
     * If specified {@code fileName} doesn't exist, it'll attempt to create it
     * and its parent directories
     *
     * @param link
     *            the Internet link of the page
     * @param fileName
     *            name of the file to save as
     * @param referer
     *            specifies referer for this request
     * @param userAgent
     *            specifies user agent for this request
     */
    public static void downloadResourceWithCustomRequest(String link,
            String fileName, String referer, String userAgent) {
        URL url = createRemoteURL(link);
        if (url != null) {
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                if (referer != null) {
                    connection.setRequestProperty("Referer", referer);
                }
                if (userAgent != null) {
                    connection.setRequestProperty("User-Agent", userAgent);
                }
                InputStream is = connection.getInputStream();
                write(is, fileName);
                is.close();
            }
            catch (IOException e) {
                Out.e(e);
            }
        }
    }

    /**
     * Return java.nio Path
     *
     * @param fileName
     *                  name of the file or folder
     * @return
     * @throws InvalidPathException
     */
    public static Path getPathTo(String fileName) throws InvalidPathException {
        return Paths.get(fileName);
    }

    /**
     * Attempts to create parent directories of file/folder with given fileName
     *
     * @param fileName
     * @throws IOException
     */
    public static void createDirsTo(String fileName) throws IOException {
        createDirsTo(getPathTo(fileName));
    }

    /**
     * Attempts to create parent directories of file/folder
     *
     * @param file
     * @throws IOException
     */
    public static void createDirsTo(Path file) throws IOException {
        Files.createDirectories(file.getParent());
    }

    /**
     * Writes bytes from specified InputStream to a specified file with given
     * name
     *
     * Will create any directories necessary for the file
     * Will NOT close the provided InputStream
     *
     * @param is
     *            InputStream from which to read
     * @param fileName
     *            name of the file to which to write
     * @throws IOException
     */
    public static void write(InputStream is, String fileName) throws IOException {
        Path file = getPathTo(fileName);
        createDirsTo(file);

        try (OutputStream os = Files.newOutputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = is.read(buffer)) != -1)
                os.write(buffer, 0, length);
        }
    }

    /**
     * Writes bytes to given fileName
     *
     * Will create any directories necessary for the file
     * Overwrites any existing file
     *
     * @param data
     * @param fileName
     * @throws IOException
     */
    public static void write(byte[] data, String fileName) throws IOException {
        Path file = getPathTo(fileName);
        createDirsTo(file);
        Files.write(file, data);
    }

    /**
     * Writes list items to file with specified file name
     *
     * @param fileName
     *            name of the file
     * @param list
     *            items to write to file
     * @throws IOException
     */
    public static void write(String fileName, List<String> list) throws IOException {
        write(fileName, list, "");
    }

    /**
     * Writes list items to file with specified file name and includes header as
     * file's first line
     *
     * If parent directories (folders) don't exist, it will attempt to create
     * them
     *
     * @param fileName
     *            name of the file
     * @param list
     *            items to write to file
     * @param header
     *            first line in the file followed by an empty new line
     * @throws IOException
     */
    public static void write(String fileName, List<String> list, String header) throws IOException {
        Path file = getPathTo(fileName);
        createDirsTo(file);

        if (!header.isEmpty()) {
            ArrayList<String> head = new ArrayList<String>();
            head.add(header);
            head.add("");

            Files.write(file, head);
            Files.write(file, list, StandardOpenOption.APPEND);
        }
        else {
            Files.write(file, list);
        }
    }

    /**
     * Converts a finite input stream to byte[]
     *
     *
     * @param is
     * @return
     *          byte[] data of an input stream or empty array
     *          if an exception was thrown
     */
    public static byte[] getBytes(InputStream is) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            return out.toByteArray();
        }
        catch (IOException e) {
            Out.e("ResourceManager.getBytes()", "Couldn't convert input stream to byte[]", e);
        }

        return new byte[] {};
    }

    public static class ResourceLoadException extends IOException {
        private static final long serialVersionUID = -5948047007368864024L;

        public ResourceLoadException() {
            this("Resource load failed");
        }

        public ResourceLoadException(String info) {
            super(info);
        }
    }
}
