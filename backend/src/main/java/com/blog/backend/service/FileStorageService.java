package com.blog.backend.service;

import com.blog.backend.exception.InvalidFileException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private Path fileStorageLocation;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${file.allowed-image-types:image/jpeg,image/png,image/gif,image/webp}")
    private String allowedImageTypes;

    @Value("${file.allowed-video-types:video/mp4,video/webm,video/quicktime}")
    private String allowedVideoTypes;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory: " + this.fileStorageLocation, ex);
        }
    }

    public String storeFile(MultipartFile file) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFilename, ex);
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new InvalidFileException("File not found: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new InvalidFileException("File not found: " + filename);
        }
    }

    public void deleteFile(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file: " + filename, ex);
        }
    }

    public String getMediaType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new InvalidFileException("Could not determine file type");
        }

        if (isImageType(contentType)) {
            return "IMAGE";
        } else if (isVideoType(contentType)) {
            return "VIDEO";
        } else {
            throw new InvalidFileException("Unsupported file type: " + contentType);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is empty or null");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new InvalidFileException("Could not determine file type");
        }

        if (!isImageType(contentType) && !isVideoType(contentType)) {
            throw new InvalidFileException("File type not allowed. Supported types: images (jpeg, png, gif, webp) and videos (mp4, webm, mov)");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains("..")) {
            throw new InvalidFileException("Invalid filename: " + originalFilename);
        }
    }

    private boolean isImageType(String contentType) {
        List<String> allowed = Arrays.asList(allowedImageTypes.split(","));
        return allowed.contains(contentType.toLowerCase());
    }

    private boolean isVideoType(String contentType) {
        List<String> allowed = Arrays.asList(allowedVideoTypes.split(","));
        return allowed.contains(contentType.toLowerCase());
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    public String getContentType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return switch (extension) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".webp" -> "image/webp";
            case ".mp4" -> "video/mp4";
            case ".webm" -> "video/webm";
            case ".mov" -> "video/quicktime";
            default -> "application/octet-stream";
        };
    }
}
