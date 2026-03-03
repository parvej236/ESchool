package com.parvej.backend.homepage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeSlideRepo extends JpaRepository<HomeSlide, Integer> {
    List<HomeSlide> findByActiveTrueOrderByDisplayOrderAsc();
    List<HomeSlide> findAllByOrderByDisplayOrderAsc();
}

