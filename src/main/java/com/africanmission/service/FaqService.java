package com.africanmission.service;

import com.africanmission.model.Faq;
import com.africanmission.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    public List<Faq> getAllFaqs() {
        return faqRepository.findAll();
    }

    public List<Faq> getActiveFaqs() {
        return faqRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    public List<Faq> getFaqsByCategory(String category) {
        return faqRepository.findByCategoryAndIsActiveTrue(category);
    }

    public List<String> getAllCategories() {
        return faqRepository.findDistinctCategoryByIsActiveTrue();
    }

    public Faq getFaqById(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ non trouvée"));
    }

    public Faq save(Faq faq) {
        return faqRepository.save(faq);
    }

    public void delete(Long id) {
        faqRepository.deleteById(id);
    }
}