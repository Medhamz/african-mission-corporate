package com.africanmission.service;

import com.africanmission.model.KioskSlide;
import com.africanmission.repository.KioskSlideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KioskService {

    private final KioskSlideRepository kioskSlideRepository;

    @Transactional(readOnly = true)
    public List<KioskSlide> getAllSlides() {
        return kioskSlideRepository.findAllByOrderBySlideOrderAsc();
    }

    @Transactional(readOnly = true)
    public List<KioskSlide> getActiveSlides() {
        // CORRECTION : isActive() au lieu de getIsActive()
        return kioskSlideRepository.findByIsActiveTrueOrderBySlideOrderAsc();
    }

    @Transactional(readOnly = true)
    public KioskSlide getSlideById(Long id) {
        return kioskSlideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Slide non trouvé"));
    }

    @Transactional
    public KioskSlide saveSlide(KioskSlide slide) {
        return kioskSlideRepository.save(slide);
    }

    @Transactional
    public void deleteSlide(Long id) {
        kioskSlideRepository.deleteById(id);
    }

    @Transactional
    public KioskSlide toggleActive(Long id) {
        KioskSlide slide = getSlideById(id);
        slide.setIsActive(!slide.getIsActive());
        return kioskSlideRepository.save(slide);
    }

    @Transactional
    public void reorderSlides(List<Long> slideIds) {
        for (int i = 0; i < slideIds.size(); i++) {
            KioskSlide slide = getSlideById(slideIds.get(i));
            slide.setSlideOrder(i + 1);
            kioskSlideRepository.save(slide);
        }
    }
}