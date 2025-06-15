// ✅ 경로: src/main/java/org/example/courseregistration/service/SectionsService.java

package org.example.courseregistration.sub.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.sub.dto.CourseSectionDto;
import org.example.courseregistration.sub.repository.SectionsRepository;
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