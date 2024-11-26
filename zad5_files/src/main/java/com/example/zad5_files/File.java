package com.example.zad5_files;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class File {
    public static List<String> getFileLines(String path) throws IOException {
        return Files.readAllLines(Paths.get(path));
    }

    public static void writeFileLines(String path, List<String> lines) throws IOException {
        Files.write(Paths.get(path), lines, StandardOpenOption.APPEND);
    }

    public static void clearFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
        Files.createFile(path);
    }
}
