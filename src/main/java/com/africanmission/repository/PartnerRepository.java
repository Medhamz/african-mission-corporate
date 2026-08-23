package com.africanmission.repository;

import com.africanmission.model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    // Récupère les partenaires actifs pour la partie publique (triés)
    List<Partner> findByIsActiveTrueOrderByDisplayOrderAsc();

    // Récupère tous les partenaires pour l'administration (triés)
    List<Partner> findAllByOrderByDisplayOrderAsc();

    // Recherche par nom pour la barre de recherche globale admin
    List<Partner> findByNameContainingIgnoreCase(String query);
}