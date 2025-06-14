// ✅ 경로: src/main/java/org/example/courseregistration/controller/TimeTableController.java
package org.example.courseregistration.controller;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.TimeTableDto;
import org.example.courseregistration.service.CourseTimeTableService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimeTableController {

    private final CourseTimeTableService timeTableService;

    // ✅ 시간표 조회 API
    @GetMapping("/view")
    public List<TimeTableDto> getTimeTable(@RequestParam String studentId,
                                           @RequestParam int year,
                                           @RequestParam int semester) {
        return timeTableService.getTimeTable(studentId, year, semester);
    }
}