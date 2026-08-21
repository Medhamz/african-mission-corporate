package com.africanmission.repository;

import com.africanmission.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    // Récupère les médias actifs triés du plus récent au plus ancien
    List<Media> findByIsActiveTrueOrderByCreatedAtDesc();

    // Recherche un média par son nom de fichier
    Optional<Media> findByFilename(String filename);

    // Filtre les images actives par type MIME
    List<Media> findByIsActiveTrueAndFileTypeStartingWith(String fileType);
}