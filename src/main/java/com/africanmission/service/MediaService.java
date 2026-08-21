package com.africanmission.service;

import com.africanmission.model.Media;
import com.africanmission.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;
    private static final String UPLOAD_DIR = "uploads/";

    public Media uploadFile(MultipartFile file, String altText) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier téléchargé est vide.");
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
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
        Files.copy(file.getInputStream(), filePath);

        Media media = new Media();
        media.setFilename(!originalFilename.isBlank() ? originalFilename : uniqueFilename);
        media.setFilePath("/" + UPLOAD_DIR + uniqueFilename); // Path relatif accessible en HTTP
        media.setFileType(file.getContentType());
        media.setFileSize(file.getSize());
        media.setAltText(altText != null ? altText : originalFilename);
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
        // Supprimer le fichier physique
        String cleanPath = media.getFilePath().startsWith("/") ? media.getFilePath().substring(1) : media.getFilePath();
        Path filePath = Paths.get(cleanPath);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        mediaRepository.delete(media);
    }

    public void softDelete(Long id) {
        Media media = getById(id);
        media.setIsActive(false);
        mediaRepository.save(media);
    }
}