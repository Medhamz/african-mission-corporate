package com.africanmission.service;

import com.africanmission.model.Media;
import com.africanmission.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    // Utilise un dossier 'uploads' relatif à l'exécution
    private static final String UPLOAD_DIR = "uploads";

    public Media uploadFile(MultipartFile file, String altText) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier téléchargé est vide.");
        }

        // Définition et création sécurisée du répertoire
        File uploadFolder = new File(UPLOAD_DIR);
        if (!uploadFolder.exists()) {
            boolean created = uploadFolder.mkdirs();
            if (!created && !uploadFolder.exists()) {
                throw new IOException("Impossible de créer le répertoire: " + uploadFolder.getAbsolutePath());
            }
        }

        Path uploadPath = uploadFolder.toPath().toAbsolutePath().normalize();

        // Nettoyage du nom pour éviter le Path Traversal
        String rawFilename = file.getOriginalFilename();
        String originalFilename = rawFilename != null ? StringUtils.cleanPath(rawFilename) : "";

        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Copie du flux
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        Media media = new Media();
        media.setFilename(!originalFilename.isBlank() ? originalFilename : uniqueFilename);

        // URL accessible côté client (ex: /uploads/uuid.png)
        media.setFilePath("/uploads/" + uniqueFilename);

        media.setFileType(file.getContentType());
        media.setFileSize(file.getSize());
        media.setAltText(altText != null && !altText.isBlank() ? altText : originalFilename);
        media.setIsActive(true);

        return mediaRepository.save(media);
    }

    public List<Media> getAllActive() {
        return mediaRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    public List<Media> getAllActiveImages() {
        return getAllActive().stream()
                .filter(m -> m.getFileType() != null && m.getFileType().startsWith("image/"))
                .collect(Collectors.toList());
    }

    public Media getById(Long id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Média non trouvé"));
    }

    public void delete(Long id) throws IOException {
        Media media = getById(id);

        if (media.getFilePath() != null) {
            String filename = media.getFilePath().replace("/uploads/", "");
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).toAbsolutePath().normalize();
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        }

        mediaRepository.delete(media);
    }

    public void softDelete(Long id) {
        Media media = getById(id);
        media.setIsActive(false);
        mediaRepository.save(media);
    }
}