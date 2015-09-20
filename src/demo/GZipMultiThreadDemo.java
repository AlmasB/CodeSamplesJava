package com.almasb.java.framework.demo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;

public class GZipMultiThreadDemo {
    public static final int THREAD_COUNT = 4;
    private static int filesToBeCompressed = -1;
    private static Lock lock = new ReentrantLock();
    private final static Condition c = lock.newCondition();

    public static void main(String[] args) {

        String[] fileNames = new String[] { "ball.png" };

        Queue<File> pool = new ConcurrentLinkedQueue<File>();
        GZipThread[] threads = new GZipThread[THREAD_COUNT];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new GZipThread(pool, c);
            threads[i].start();
        }
        AtomicInteger totalFiles = computeNumOfcompressFile(fileNames, pool);
        filesToBeCompressed = totalFiles.get();

        //
        for (int i = 0; i < threads.length; i++)
            threads[i].interrupt();
    }

    public static int getNumberOfFilesToBeCompressed() {
        return filesToBeCompressed;
    }

    private static AtomicInteger computeNumOfcompressFile(String[] args,
            Queue<File> pool) {
        AtomicInteger totalFiles = new AtomicInteger(0);
        for (int i = 0; i < args.length; i++) {
            File f = new File(args[i]);
            if (f.exists()) {
                if (f.isDirectory()) {
                    handleDir(totalFiles, pool, f);
                }
                else {
                    handleFile(totalFiles, pool, f);
                }
            }
        }
        return totalFiles;
    }

    private static void handleFile(AtomicInteger totalFiles, Queue<File> pool,
            File f) {
        totalFiles.addAndGet(1);
        pool.add(f);
        lockedNotify();
    }

    private static void handleDir(AtomicInteger totalFiles, Queue<File> pool,
            File f) {
        File[] files = f.listFiles();
        for (int j = 0; j < files.length; j++) {
            if (!files[j].isDirectory()) {//
                handleFile(totalFiles, pool, f);
            }
        }
    }

    private static void lockedNotify() {
        try {
            lock.lock();
            c.signalAll();
        }
        finally {
            lock.unlock();
        }
    }
}

class GZipThread extends Thread {

    private Queue<File> pool;
    private static AtomicInteger filesCompressed = new AtomicInteger(0);
    private Condition c;

    public GZipThread(Queue<File> pool, Condition c) {
        this.pool = pool;
        this.c = c;
    }

    private static void incrementFilesCompressed() {
        filesCompressed.addAndGet(1);
    }

    @Override
    public void run() {
        run:
            while (filesCompressed.get() != GZipMultiThreadDemo.getNumberOfFilesToBeCompressed()) {
                while (pool.isEmpty()) {
                    if (isEndOfThread())
                        break run;
                    awaitThread();
                }
                try {
                    File input = (File) pool.remove();
                    incrementFilesCompressed();
                    //
                    if (!input.getName().equals(".gz")) {
                        compress(input);
                    }
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
    }

    private void awaitThread() {
        try {
            c.await();
        }
        catch (Exception e) {
        }
    }

    private boolean isEndOfThread() {
        if (filesCompressed.get() == GZipMultiThreadDemo.getNumberOfFilesToBeCompressed()) {
            System.out.println("Thread ending");
            return true;
        }
        return false;
    }

    private void compress(File input) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(input);
            in = new BufferedInputStream(in);
            File output = new File(input.getParent(), input.getName() + ".gz");
            if (!output.exists()) {//
                out = new FileOutputStream(output);
                out = new GZIPOutputStream(out);
                out = new BufferedOutputStream(out);
                int b;
                while ((b = in.read()) != -1)
                    out.write(b);
                out.flush();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            close(out);
            close(in);
        }
    }

    private void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
