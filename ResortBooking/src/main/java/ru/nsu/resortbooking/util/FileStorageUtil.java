package ru.nsu.resortbooking.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.*;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileStorageUtil {
    private final Path storageLocation;

    public FileStorageUtil(
            @Value("${file.storage.location}") String storageLocation
    ) throws IOException {
        this.storageLocation = Paths.get(storageLocation)
                .toAbsolutePath().normalize();
        Files.createDirectories(this.storageLocation);
    }

    public String storeFile(MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            Path target = storageLocation.resolve(fileName);
            Files.copy(
                    file.getInputStream(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл " + fileName, e);
        }
    }
}
