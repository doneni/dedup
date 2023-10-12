package com.dedup;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception
    {
        char[] file1 = getFileChars(new String("resource/jabberwocky.txt"));
        char[] file2 = getFileChars(new String("resource/agatha.txt"));
        System.out.println("Hello World");

    }

    static char[] getFileChars(String filePath)
    {
        try {
            Path path = Paths.get(filePath);

            byte[] fileBytes = Files.readAllBytes(path);
            char[] fileChars = new String(fileBytes, StandardCharsets.UTF_8).toCharArray();

            return fileChars;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}