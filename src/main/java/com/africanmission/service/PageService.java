package com.africanmission.service;

import com.africanmission.model.Page;
import com.africanmission.model.Project;
import com.africanmission.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;

    public List<Page> getAllPages() {
        return pageRepository.findAll();
    }

    public Page getBySlug(String slug) {
        return pageRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Page non trouvée"));
    }

    public Page getById(Long id) {
        return pageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Page non trouvée"));
    }

    public Page save(Page page) {
        return pageRepository.save(page);
    }

    public void delete(Long id) {
        pageRepository.deleteById(id);
    }

    public Page createDefaultPage(String slug, String title, String content) {
        Page page = new Page();
        page.setSlug(slug);
        page.setTitle(title);
        page.setContent(content);
        page.setIsActive(true);
        return pageRepository.save(page);
    }

    public List<Page> searchByTitle(String query) {
        return pageRepository.findByTitleContainingIgnoreCase(query);
    }
}