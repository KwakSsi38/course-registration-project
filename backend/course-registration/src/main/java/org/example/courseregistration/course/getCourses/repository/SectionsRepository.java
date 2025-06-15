package org.example.courseregistration.course.getCourses.repository;

import org.example.courseregistration.course.getCourses.dto.CourseSectionDto;
import org.example.courseregistration.entity.Sections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionsRepository extends JpaRepository<Sections, Long> {

  // ✅ findAllCourseSections 메서드 수정: 여석 계산 로직 및 JOIN 조건 변경
  @Query("SELECT new org.example.courseregistration.course.getCourses.dto.CourseSectionDto(" +
      "s.id, c.idNo, c.name, c.unit, c.major, s.section, s.time, s.capacity, s.classroom, p.name, " +
      "s.capacity - COALESCE(COUNT(e.id), 0)) " + // ✅ COALESCE 추가: 수강 신청이 없으면 0으로 처리
      "FROM Sections s JOIN s.course c JOIN s.professor p " +
      "LEFT JOIN Enroll e ON e.course.idNo = s.course.idNo " + // ✅ JOIN 조건: Enroll의 course.idNo와 Sections의 course.idNo
                                                               // 매칭
      "AND e.year = s.year AND e.semester = s.semester AND e.section = s.section " + // ✅ JOIN 조건: 연도, 학기, 분반 매칭
      "WHERE s.year = 2025 AND s.semester = 1 " + // 고정된 연도/학기 조건
      "GROUP BY s.id, c.idNo, c.name, c.unit, c.major, s.section, s.time, s.capacity, s.classroom, p.name " +
      "ORDER BY c.idNo, s.section")
  List<CourseSectionDto> findAllCourseSections();

  // ✅ findCourseSectionsByCondition 메서드 수정: 여석 계산 로직 및 JOIN 조건 변경
  @Query("SELECT new org.example.courseregistration.course.getCourses.dto.CourseSectionDto(" +
      "s.id, c.idNo, c.name, c.unit, c.major, s.section, s.time, s.capacity, s.classroom, p.name, " +
      "s.capacity - COALESCE(COUNT(e.id), 0)) " + // ✅ COALESCE 추가
      "FROM Sections s JOIN s.course c JOIN s.professor p " +
      "LEFT JOIN Enroll e ON e.course.idNo = s.course.idNo " + // ✅ JOIN 조건 변경
      "AND e.year = s.year AND e.semester = s.semester AND e.section = s.section " + // ✅ JOIN 조건 변경
      "WHERE s.year = 2025 AND s.semester = 1 " + // 고정된 연도/학기 조건
      "AND (:courseName IS NULL OR c.name LIKE CONCAT('%', :courseName, '%')) " +
      "AND (:courseIdNo IS NULL OR c.idNo LIKE CONCAT('%', :courseIdNo, '%')) " +
      "AND (:major IS NULL OR c.major LIKE CONCAT('%', :major, '%')) " +
      "AND (:professorName IS NULL OR p.name LIKE CONCAT('%', :professorName, '%')) " +
      "AND (:credit IS NULL OR c.unit = :credit) " +
      "GROUP BY s.id, c.idNo, c.name, c.unit, c.major, s.section, s.time, s.capacity, s.classroom, p.name " +
      "ORDER BY c.idNo, s.section")
  List<CourseSectionDto> findCourseSectionsByCondition(
      @Param("courseName") String courseName,
      @Param("courseIdNo") String courseIdNo,
      @Param("major") String major,
      @Param("professorName") String professorName,
      @Param("credit") Integer credit);
}