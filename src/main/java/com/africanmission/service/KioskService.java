package com.africanmission.service;

import com.africanmission.model.KioskSlide;
import com.africanmission.repository.KioskSlideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KioskService {

    private final KioskSlideRepository kioskSlideRepository;

    public List<KioskSlide> getAllSlides() {
        return kioskSlideRepository.findAllByOrderBySlideOrderAsc();
    }

    public List<KioskSlide> getActiveSlides() {
        return kioskSlideRepository.findByIsActiveTrueOrderBySlideOrderAsc();
    }

    public KioskSlide getSlideById(Long id) {
        return kioskSlideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Slide non trouvé"));
    }

    public KioskSlide saveSlide(KioskSlide slide) {
        return kioskSlideRepository.save(slide);
    }

    public void deleteSlide(Long id) {
        kioskSlideRepository.deleteById(id);
    }

    public KioskSlide toggleActive(Long id) {
        KioskSlide slide = getSlideById(id);
        slide.setIsActive(!slide.getIsActive());
        return kioskSlideRepository.save(slide);
    }

    public void reorderSlides(List<Long> slideIds) {
        for (int i = 0; i < slideIds.size(); i++) {
            KioskSlide slide = getSlideById(slideIds.get(i));
            slide.setSlideOrder(i + 1);
            kioskSlideRepository.save(slide);
        }
    }
}