package org.example.courseregistration.course.timeTable.controller;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.course.timeTable.dto.TimeTableDto;
import org.example.courseregistration.course.timeTable.service.CourseTimeTableService;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.example.courseregistration.config.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimeTableController {

  private final CourseTimeTableService timeTableService;
  private final JwtTokenProvider jwtTokenProvider;

  // 시간표 조회 API
  @GetMapping("/view")
  public ResponseEntity<List<TimeTableDto>> getTimeTable(HttpServletRequest request) {

    String bearerToken = request.getHeader("Authorization");

    if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String token = bearerToken.substring(7);
    String studentId = jwtTokenProvider.getStudentId(token);

    List<TimeTableDto> response = timeTableService.getTimeTable(studentId);
    return ResponseEntity.ok(response);
  }
}