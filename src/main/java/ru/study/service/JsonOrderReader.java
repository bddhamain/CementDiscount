//package ru.study.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.type.TypeReference;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.List;
//
//public class JsonOrderReader {
//    private final Path file;
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    public JsonOrderReader(Path file) {
//        this.file = file;
//    }
//
//    public List<String> loadLines() throws IOException {
//        // reads JSON array of objects, returns each as a JSON string
//        return mapper.readValue(
//                file.toFile(),
//                new TypeReference<List<String>>() {}
//        );
//    }
//}