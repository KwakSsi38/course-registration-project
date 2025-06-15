// ✅ 경로: src/main/java/org/example/courseregistration/repository/SectionsRepository.java

package org.example.courseregistration.sub.repository;

import org.example.courseregistration.sub.dto.CourseSectionDto;
import org.example.courseregistration.sub.entity.Sections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SectionsRepository extends JpaRepository<Sections, Long> {

    @Query("SELECT new org.example.courseregistration.dto.CourseSectionDto(" +
            "s.id, c.idNo, c.id, c.unit, c.major, s.section, s.time, s.capacity, s.classroom) " +
            "FROM Sections s JOIN s.course c " +
            "ORDER BY c.idNo, s.section")
    List<CourseSectionDto> findAllCourseSections();
}