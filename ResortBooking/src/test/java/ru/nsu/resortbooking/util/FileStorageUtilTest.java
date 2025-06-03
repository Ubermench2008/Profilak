package ru.nsu.resortbooking.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileStorageUtilTest {

    @Test
    void storeFileShouldHandleIOException(@TempDir Path tempDir) throws IOException {
        String storagePath = tempDir.toString();
        FileStorageUtil fileStorageUtil = new FileStorageUtil(storagePath);

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockFile.getInputStream()).thenThrow(new IOException("Test exception"));

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> fileStorageUtil.storeFile(mockFile)
        );

        assertTrue(exception.getMessage().contains("Не удалось сохранить файл"));
    }

    @Test
    void storeFileSuccessfully(@TempDir Path tempDir) throws IOException {
        FileStorageUtil util = new FileStorageUtil(tempDir.toString());
        MultipartFile file = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                "content".getBytes()
        );

        String storedFileName = util.storeFile(file);

        assertNotNull(storedFileName);
        assertTrue(storedFileName.contains("test.txt"));
        assertTrue(tempDir.resolve(storedFileName).toFile().exists());
    }
}