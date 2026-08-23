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

    public List<Partner> getAllPartners() {
        return partnerRepository.findAllByOrderByDisplayOrderAsc();
    }

    public Partner savePartner(Partner partner) {
        if (partner.getDisplayOrder() == null) {
            partner.setDisplayOrder(0);
        }
        if (partner.getIsActive() == null) {
            partner.setIsActive(true);
        }
        return partnerRepository.save(partner);
    }

    public void deletePartner(Long id) {
        partnerRepository.deleteById(id);
    }

    public Partner getPartnerById(Long id) {
        return partnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partenaire non trouvé avec l'ID : " + id));
    }

    public Partner togglePartnerStatus(Long id) {
        Partner partner = getPartnerById(id);
        partner.setIsActive(!partner.getIsActive());
        return partnerRepository.save(partner);
    }

    public List<Partner> searchByName(String query) {
        return partnerRepository.findByNameContainingIgnoreCase(query);
    }
}