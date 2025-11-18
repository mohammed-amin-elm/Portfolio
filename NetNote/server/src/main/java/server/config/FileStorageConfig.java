package server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration class for file storage settings.
 * Handles the setup of the file upload directory.
 */
@Configuration
public class FileStorageConfig {

    @Value("${netnote.file.upload-dir}")
    private String uploadDir;

    /**
     * Creates and provides a {@code Path} bean for the upload directory.
     * Ensures that the directory exists, creating it if necessary.
     *
     * @return the path to the upload directory.
     * @throws RuntimeException if the directory cannot be created.
     */
    @Bean
    public Path uploadPath() {
        Path path = Paths.get(uploadDir);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            System.out.println("Could not create directory: " + e.getMessage());
            throw new RuntimeException("Could not create directory...");
        }
        return path;
    }

    /**
     * Retrieves the absolute, normalized path of the upload directory.
     *
     * @return the absolute and normalized path to the upload directory.
     */
    public Path getUploadDir() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }
}
