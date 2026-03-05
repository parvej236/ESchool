package com.parvej.backend.homeslide;

import java.util.List;

public interface HomeSlideService {
    List<HomeSlide> getAll();
    List<HomeSlide> getActiveSlides();
    HomeSlide       getById(Long id);
    HomeSlide       create(HomeSlideDTO dto, String imageUrl);
    HomeSlide       update(Long id, HomeSlideDTO dto, String imageUrl);
    void            delete(Long id);
}
