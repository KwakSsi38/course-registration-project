// ✅ 경로: src/main/java/org/example/courseregistration/controller/SectionsController.java

package org.example.courseregistration.controller;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.CourseSectionDto;
import org.example.courseregistration.service.SectionsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionsController {

    private final SectionsService sectionsService;

    // ✅ 과목 리스트 조회 API
    @GetMapping("/list")
    public List<CourseSectionDto> getAllSections() {
        return sectionsService.getAllCourseSections();
    }
}