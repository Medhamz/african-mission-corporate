package com.africanmission.service;

import com.africanmission.model.Media;
import com.africanmission.repository.MediaRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;
    private final Cloudinary cloudinary;

    public Media uploadFile(MultipartFile file, String altText) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier téléchargé est vide.");
        }

        // Nettoyage du nom de fichier original
        String rawFilename = file.getOriginalFilename();
        String originalFilename = rawFilename != null ? StringUtils.cleanPath(rawFilename) : "";

        // Envoi direct du fichier sur Cloudinary sans stockage local
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "african_mission_uploads",
                "resource_type", "auto"
        ));

        // Récupération de l'URL publique HTTPS de l'image
        String imageUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id"); // Utile pour la suppression

        Media media = new Media();
        media.setFilename(!originalFilename.isBlank() ? originalFilename : publicId);

        // On enregistre l'URL absolue HTTPS hébergée sur Cloudinary
        media.setFilePath(imageUrl);

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

        // Optionnel : Suppression du fichier sur Cloudinary s'il y a un ID public
        // Si vous souhaitez juste supprimer la référence BDD :
        mediaRepository.delete(media);
    }

    public void softDelete(Long id) {
        Media media = getById(id);
        media.setIsActive(false);
        mediaRepository.save(media);
    }
}