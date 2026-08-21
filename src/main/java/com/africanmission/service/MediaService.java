package com.africanmission.service;

import com.africanmission.model.Media;
import com.africanmission.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
    private static final String UPLOAD_DIR = "uploads";

    public Media uploadFile(MultipartFile file, String altText) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier téléchargé est vide.");
        }

        // Résolution du chemin absolu pour garantir la création du répertoire
        Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Nettoyage du nom pour éviter le Path Traversal
        String rawFilename = file.getOriginalFilename();
        String originalFilename = rawFilename != null ? StringUtils.cleanPath(rawFilename) : "";

        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Copie sécurisée du flux avec remplacement en cas de conflit
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        Media media = new Media();
        media.setFilename(!originalFilename.isBlank() ? originalFilename : uniqueFilename);

        // Construction correcte du chemin HTTP sans double slash (/uploads/filename.ext)
        media.setFilePath("/" + UPLOAD_DIR + "/" + uniqueFilename);

        media.setFileType(file.getContentType());
        media.setFileSize(file.getSize());
        media.setAltText(altText != null && !altText.isBlank() ? altText : originalFilename);
        media.setIsActive(true);

        return mediaRepository.save(media);
    }

    public List<Media> getAllActive() {
        return mediaRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    // Récupération filtrée pour la Galerie (Images uniquement)
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

        // Suppression du fichier physique en chemin relatif
        if (media.getFilePath() != null) {
            String cleanPath = media.getFilePath().startsWith("/") ? media.getFilePath().substring(1) : media.getFilePath();
            Path filePath = Paths.get(cleanPath).toAbsolutePath().normalize();
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