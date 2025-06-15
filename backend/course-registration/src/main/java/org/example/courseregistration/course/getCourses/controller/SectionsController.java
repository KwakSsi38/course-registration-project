package org.example.courseregistration.course.getCourses.controller;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.course.getCourses.dto.CourseSectionDto;
import org.example.courseregistration.course.getCourses.dto.CourseSearchCondition; // CourseSearchCondition DTO 추가
import org.example.courseregistration.course.getCourses.service.SectionsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionsController {

  private final SectionsService sectionsService;

  // 과목 리스트 조회 및 검색 API 통합
  // 예시 요청: /api/sections/list
  // 예시 요청: /api/sections/list?courseName=자료구조
  // 예시 요청: /api/sections/list?credit=3
  @GetMapping("/list")
  public List<CourseSectionDto> getSections(
      @RequestParam(required = false) String courseName,
      @RequestParam(required = false) String courseIdNo,
      @RequestParam(required = false) String major,
      @RequestParam(required = false) String professorName,
      @RequestParam(required = false) Integer credit) { // 학점 Integer로

    // 검색 조건 DTO를 생성하여 Service로 전달
    CourseSearchCondition condition = new CourseSearchCondition(
        courseName, courseIdNo, major, professorName, credit);

    // 모든 검색 파라미터가 null이면 전체 목록을 반환
    // 그렇지 않으면 검색 조건에 맞는 목록을 반환
    if (courseName == null && courseIdNo == null && major == null && professorName == null && credit == null) {
      return sectionsService.getAllCourseSections();
    } else {
      return sectionsService.searchCourseSections(condition);
    }
  }
}