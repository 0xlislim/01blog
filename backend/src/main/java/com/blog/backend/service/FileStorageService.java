package com.blog.backend.service;

import com.blog.backend.exception.InvalidFileException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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

        // Validate actual file content using magic bytes
        String detectedType = detectFileTypeFromContent(file);
        if (detectedType == null) {
            throw new InvalidFileException("Could not verify file type from content. The file may be corrupted or have an incorrect extension.");
        }

        // Ensure the detected type matches the claimed content type category
        boolean claimedImage = isImageType(contentType);
        boolean detectedImage = isImageType(detectedType);
        boolean claimedVideo = isVideoType(contentType);
        boolean detectedVideo = isVideoType(detectedType);

        if ((claimedImage && !detectedImage) || (claimedVideo && !detectedVideo)) {
            throw new InvalidFileException("File content does not match the declared file type. Please upload a valid " +
                (claimedImage ? "image" : "video") + " file.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains("..")) {
            throw new InvalidFileException("Invalid filename: " + originalFilename);
        }
    }

    private String detectFileTypeFromContent(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[12];
            int bytesRead = is.read(header);
            if (bytesRead < 4) {
                return null;
            }

            // JPEG: FF D8 FF
            if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF) {
                return "image/jpeg";
            }

            // PNG: 89 50 4E 47 0D 0A 1A 0A
            if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50 &&
                header[2] == (byte) 0x4E && header[3] == (byte) 0x47) {
                return "image/png";
            }

            // GIF: 47 49 46 38
            if (header[0] == (byte) 0x47 && header[1] == (byte) 0x49 &&
                header[2] == (byte) 0x46 && header[3] == (byte) 0x38) {
                return "image/gif";
            }

            // WebP: 52 49 46 46 ... 57 45 42 50
            if (header[0] == (byte) 0x52 && header[1] == (byte) 0x49 &&
                header[2] == (byte) 0x46 && header[3] == (byte) 0x46 &&
                bytesRead >= 12 &&
                header[8] == (byte) 0x57 && header[9] == (byte) 0x45 &&
                header[10] == (byte) 0x42 && header[11] == (byte) 0x50) {
                return "image/webp";
            }

            // MP4: ftyp at bytes 4-7
            if (bytesRead >= 8 &&
                header[4] == (byte) 0x66 && header[5] == (byte) 0x74 &&
                header[6] == (byte) 0x79 && header[7] == (byte) 0x70) {
                return "video/mp4";
            }

            // WebM: 1A 45 DF A3
            if (header[0] == (byte) 0x1A && header[1] == (byte) 0x45 &&
                header[2] == (byte) 0xDF && header[3] == (byte) 0xA3) {
                return "video/webm";
            }

            // QuickTime/MOV: also uses ftyp but with 'qt' brand
            // MOV files can also start with 'moov', 'mdat', 'wide', 'free', 'skip'
            if (bytesRead >= 8) {
                String fourCC = new String(new byte[]{header[4], header[5], header[6], header[7]});
                if (fourCC.equals("moov") || fourCC.equals("mdat") || fourCC.equals("wide") ||
                    fourCC.equals("free") || fourCC.equals("skip")) {
                    return "video/quicktime";
                }
            }

            return null;
        } catch (IOException e) {
            return null;
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
