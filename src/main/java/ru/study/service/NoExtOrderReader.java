package ru.study.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class NoExtOrderReader {
    private final Path file;

    public NoExtOrderReader(Path file) {
        this.file = file;
    }

    public List<String> loadLines() throws IOException {
        // Just read the lines as plain text!
        return Files.readAllLines(file);
    }
}