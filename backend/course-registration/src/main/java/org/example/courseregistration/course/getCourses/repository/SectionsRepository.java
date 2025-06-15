package org.example.courseregistration.course.getCourses.repository;

import org.example.courseregistration.course.getCourses.dto.CourseSectionDto;
import org.example.courseregistration.entity.Sections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SectionsRepository extends JpaRepository<Sections, Long> {

  @Query("SELECT new org.example.courseregistration.course.getCourses.dto.CourseSectionDto(" +
      "s.id, c.idNo, c.name, c.unit, c.major, s.section, s.time, s.capacity, s.classroom, p.name) " +
      "FROM Sections s JOIN s.course c JOIN s.professor p " +
      "ORDER BY c.idNo, s.section")
  List<CourseSectionDto> findAllCourseSections();
}