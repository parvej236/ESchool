package com.parvej.backend.homeslide;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeSlideServiceImpl implements HomeSlideService {

    private final HomeSlideRepository repo;

    @Override
    public List<HomeSlide> getAll() {
        return repo.findAllByOrderByDisplayOrderAsc();
    }

    @Override
    public List<HomeSlide> getActiveSlides() {
        return repo.findByActiveTrueOrderByDisplayOrderAsc();
    }

    @Override
    public HomeSlide getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Slide not found with id: " + id));
    }

    @Override
    public HomeSlide create(HomeSlideDTO dto, String imageUrl) {
        if (dto.getTitle() == null || dto.getTitle().isBlank())
            throw new RuntimeException("English title is required");
        if (imageUrl == null || imageUrl.isBlank())
            throw new RuntimeException("Image is required");

        HomeSlide slide = new HomeSlide();
        slide.setTitle(dto.getTitle());
        slide.setDescription(dto.getDescription());
        slide.setTitleBn(dto.getTitleBn());
        slide.setDescriptionBn(dto.getDescriptionBn());
        slide.setLinkUrl(dto.getLinkUrl());
        slide.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        slide.setActive(dto.getActive() != null ? dto.getActive() : true);
        slide.setImageUrl(imageUrl);
        return repo.save(slide);
    }

    @Override
    public HomeSlide update(Long id, HomeSlideDTO dto, String imageUrl) {
        HomeSlide slide = getById(id);

        if (dto.getTitle() != null && !dto.getTitle().isBlank())
            slide.setTitle(dto.getTitle());
        if (dto.getDescription() != null)
            slide.setDescription(dto.getDescription());
        if (dto.getTitleBn() != null)
            slide.setTitleBn(dto.getTitleBn());
        if (dto.getDescriptionBn() != null)
            slide.setDescriptionBn(dto.getDescriptionBn());
        if (dto.getLinkUrl() != null)
            slide.setLinkUrl(dto.getLinkUrl());
        if (dto.getDisplayOrder() != null)
            slide.setDisplayOrder(dto.getDisplayOrder());
        if (dto.getActive() != null)
            slide.setActive(dto.getActive());
        if (imageUrl != null)
            slide.setImageUrl(imageUrl);

        return repo.save(slide);
    }
    @Override
    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new RuntimeException("Slide not found with id: " + id);
        repo.deleteById(id);
    }

}
