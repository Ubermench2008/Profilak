package ru.nsu.resortbooking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileControllerTest {

    @TempDir
    Path tempDir;
    private FileController fileController;

    @BeforeEach
    void setUp() {
        fileController = new FileController();
        try {
            var field = FileController.class.getDeclaredField("storageLocation");
            field.setAccessible(true);
            field.set(fileController, tempDir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void serveExistingFile() throws Exception {
        String fileName = "test.txt";
        Files.write(tempDir.resolve(fileName), "content".getBytes());

        ResponseEntity<Resource> response = fileController.serveFile(fileName);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isReadable());
    }

    @Test
    void serveNonExistingFile() {
        ResponseEntity<Resource> response = fileController.serveFile("nonexistent.txt");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}