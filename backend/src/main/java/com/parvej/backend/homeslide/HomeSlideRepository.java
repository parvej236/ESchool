package com.parvej.backend.homeslide;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeSlideRepository extends JpaRepository<HomeSlide, Long> {
    List<HomeSlide> findAllByOrderByDisplayOrderAsc();
    List<HomeSlide> findByActiveTrueOrderByDisplayOrderAsc();
}

