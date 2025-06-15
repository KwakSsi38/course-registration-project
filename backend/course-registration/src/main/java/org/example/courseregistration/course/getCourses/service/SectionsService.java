package org.example.courseregistration.course.getCourses.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.course.getCourses.dto.CourseSectionDto;
import org.example.courseregistration.course.getCourses.dto.CourseSearchCondition;
import org.example.courseregistration.course.getCourses.repository.SectionsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionsService {

  private final SectionsRepository sectionsRepository;

  public List<CourseSectionDto> getAllCourseSections() {
    return sectionsRepository.findAllCourseSections();
  }

  public List<CourseSectionDto> searchCourseSections(CourseSearchCondition condition) {
    // ✅ 검색어에 와일드카드 문자 '%'를 직접 추가합니다.
    String courseName = condition.getCourseName();
    if (courseName != null && !courseName.isEmpty()) {
      courseName = "%" + courseName + "%";
    }

    String courseIdNo = condition.getCourseIdNo();
    if (courseIdNo != null && !courseIdNo.isEmpty()) {
      courseIdNo = "%" + courseIdNo + "%";
    }

    String major = condition.getMajor();
    if (major != null && !major.isEmpty()) {
      major = "%" + major + "%";
    }

    String professorName = condition.getProfessorName();
    if (professorName != null && !professorName.isEmpty()) {
      professorName = "%" + professorName + "%";
    }

    // credit은 LIKE 검색이 아니므로 와일드카드를 붙이지 않습니다.
    Integer credit = condition.getCredit();

    return sectionsRepository.findCourseSectionsByCondition(
        courseName,
        courseIdNo,
        major,
        professorName,
        credit);
  }
}