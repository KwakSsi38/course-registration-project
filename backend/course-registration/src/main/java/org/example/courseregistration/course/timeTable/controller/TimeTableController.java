// ✅ 경로: src/main/java/org/example/courseregistration/controller/TimeTableController.java
package org.example.courseregistration.course.timeTable.controller;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.course.timeTable.dto.TimeTableDto;
import org.example.courseregistration.course.timeTable.service.CourseTimeTableService;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.example.courseregistration.auth.login.repository.StudentsRepository;
import org.example.courseregistration.config.jwt.JwtTokenProvider;
import org.example.courseregistration.entity.Students;
import org.example.courseregistration.mypage.dto.ChangePasswordRequest;
import org.example.courseregistration.mypage.dto.StudentInfoResponse;
import org.example.courseregistration.mypage.dto.TimeTableResponse;
import org.example.courseregistration.mypage.service.MyPageService;
import org.example.courseregistration.mypage.service.TimeTableService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimeTableController {

  private final CourseTimeTableService timeTableService;
  private final JwtTokenProvider jwtTokenProvider;

  // ✅ 시간표 조회 API
  @GetMapping("/view")
  public ResponseEntity<List<TimeTableDto>> getTimeTable(HttpServletRequest request) {
    // param을 받는 것이 아니라

    String bearerToken = request.getHeader("Authorization");

    if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String token = bearerToken.substring(7);
    String studentId = jwtTokenProvider.getStudentId(token);

    LocalDate now = LocalDate.now();
    int year = now.getYear();
    int semester = (now.getMonthValue() < 9 && now.getMonthValue() > 2) ? 1 : 2;

    List<TimeTableDto> response = timeTableService.getTimeTable(studentId, year, semester);
    return ResponseEntity.ok(response);
  }
}