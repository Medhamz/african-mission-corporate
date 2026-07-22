package com.africanmission.service;

import com.africanmission.model.Country;
import com.africanmission.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public List<Country> getActiveCountries() {
        return countryRepository.findByIsActiveTrue();
    }

    public Optional<Country> getCountryById(Long id) {
        return countryRepository.findById(id);
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
    public Country toggleActive(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pays non trouvé"));
        country.setIsActive(!country.getIsActive());
        return countryRepository.save(country);
    }

    public List<Country> getCountriesByType(String type) {
        return countryRepository.findByType(type);
    }
}