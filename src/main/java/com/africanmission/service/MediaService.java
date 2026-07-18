package com.africanmission.service;

import com.africanmission.model.Media;
import com.africanmission.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;
    private static final String UPLOAD_DIR = "uploads/";

    public Media uploadFile(MultipartFile file, String altText) throws IOException {
        // Créer le dossier si inexistant
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer un nom unique
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // Sauvegarder le fichier
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        // Enregistrer en base
        Media media = new Media();
        media.setFilename(originalFilename != null ? originalFilename : uniqueFilename);
        media.setFilePath(UPLOAD_DIR + uniqueFilename);
        media.setFileType(file.getContentType());
        media.setFileSize(file.getSize());
        media.setAltText(altText);
        media.setIsActive(true);

        return mediaRepository.save(media);
    }

    public List<Media> getAllActive() {
        return mediaRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    public Media getById(Long id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Média non trouvé"));
    }

    public void delete(Long id) throws IOException {
        Media media = getById(id);
        Path filePath = Paths.get(media.getFilePath());
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