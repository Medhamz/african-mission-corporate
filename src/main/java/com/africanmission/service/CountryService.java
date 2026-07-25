package com.africanmission.service;

import com.africanmission.model.Country;
import com.africanmission.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    @Transactional(readOnly = true)
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Country> getActiveCountries() {
        // CORRECTION : isActive() au lieu de getIsActive()
        return countryRepository.findByIsActiveTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Country getCountryById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pays non trouvé"));
    }

    @Transactional
    public Country saveCountry(Country country) {
        return countryRepository.save(country);
    }

    @Transactional
    public void deleteCountry(Long id) {
        countryRepository.deleteById(id);
    }

    @Transactional
    public void toggleActive(Long id) {
        Country country = getCountryById(id);
        country.setIsActive(!country.getIsActive());
        countryRepository.save(country);
    }
}