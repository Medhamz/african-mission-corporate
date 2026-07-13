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

    public Partner savePartner(Partner partner) {
        return partnerRepository.save(partner);
    }

    public void deletePartner(Long id) {
        partnerRepository.deleteById(id);
    }
}