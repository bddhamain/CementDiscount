package ru.study.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvOrderReader {
    private final Path file;

    public CsvOrderReader(Path file) {
        this.file = file;
    }

    public List<String> loadLines() throws IOException {
        return Files.readAllLines(file);
    }
}
