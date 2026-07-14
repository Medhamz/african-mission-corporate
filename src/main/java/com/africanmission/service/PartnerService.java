package com.africanmission.service;

import com.africanmission.model.Partner;
import com.africanmission.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;

    public List<Partner> getAllActivePartners() {
        return partnerRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    // ✅ Méthode ajoutée pour récupérer TOUS les partenaires (actifs et inactifs)
    public List<Partner> getAllPartners() {
        return partnerRepository.findAll();
    }

    public Partner savePartner(Partner partner) {
        return partnerRepository.save(partner);
    }

    public void deletePartner(Long id) {
        partnerRepository.deleteById(id);
    }

    public Partner getPartnerById(Long id) {
        return partnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partenaire non trouvé"));
    }
}