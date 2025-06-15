package org.example.courseregistration.course.getCourses.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.course.getCourses.dto.CourseSectionDto;
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
}