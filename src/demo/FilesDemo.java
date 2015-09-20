package com.almasb.java.framework.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FilesDemo {

    public static void listCurrentDirFilter() throws Exception {
        Files.list(Paths.get("./"))
        .filter(file -> file.getFileName().toString().startsWith("ba")
                && file.getFileName().toString().endsWith("g"))
                .forEach(file -> System.out.println(file.getFileName()));
    }

    public static void main(String[] args) throws Exception {

        Path myFile = Paths.get("test/directory/Main.java");
        Files.createDirectories(myFile.getParent());
        //Files.createFile(myFile);

        //                OutputStream os = Files.newOutputStream(myFile);
        //                PrintWriter pw = new PrintWriter(os);
        //
        //                pw.println("Hello yo! :D");
        //
        //                pw.flush();
        //                pw.close();
        //
        //                os.close();



        //        BufferedWriter bw = Files.newBufferedWriter(myFile, StandardOpenOption.CREATE);
        //        bw.write("username=test\n");
        //        bw.write("password=testpass\n");
        //        bw.write("course=ci101\n");
        //        bw.close();

                Path p = Paths.get("res");
                Path resolved = p.resolve("words.txt");

                System.out.println(p.resolve("words.txt"));

                System.out.println(p.relativize(resolved));


        //        Files.walk(Paths.get("src")).forEach(file -> {
        //
        //            if (Files.isDirectory(file)) return;
        //
        //            //System.out.println("fileName: " + file.getFileName());
        //            //System.out.println("absPath: " + file.toAbsolutePath().toString());
        //            //System.out.println("toString: " + file.toString());
        //            //System.out.println("relative: " + file.subpath(1, file.getNameCount()));
        //
        //            System.out.println(Paths.get("src").relativize(file));
        //        });
        //
        //        Path myFile = FileSystems.getDefault().getPath("test/directory/Main.java");
        //        Files.createDirectories(myFile.getParent());
        //        Files.createFile(myFile);
        //
        //        OutputStream os = Files.newOutputStream(myFile);
        //        PrintWriter pw = new PrintWriter(os);
        //
        //        pw.println("Hello yo! :D");
        //
        //        pw.flush();
        //        pw.close();
        //
        //        os.close();

        BufferedReader br = Files.newBufferedReader(myFile);
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

    }
}
