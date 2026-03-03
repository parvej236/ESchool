package com.parvej.backend.classInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassInfoRepo extends JpaRepository<ClassInfo, Integer> {
    List<ClassInfo> findByActiveTrue();
    List<ClassInfo> findByActiveFalse();
}


