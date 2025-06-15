// ✅ 경로: src/main/java/org/example/courseregistration/controller/EnrollController.java

package org.example.courseregistration.course.enroll.controller;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.course.enroll.dto.DeleteRequestDto;
import org.example.courseregistration.course.enroll.dto.EnrollRequestDto;
import org.example.courseregistration.entity.Students;
import org.example.courseregistration.course.enroll.repository.EnrollStudentsRepository;
import org.example.courseregistration.course.enroll.service.EnrollService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.example.courseregistration.config.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/subject")
@RequiredArgsConstructor
public class EnrollController {

  private final EnrollService enrollService;
  private final EnrollStudentsRepository studentsRepository;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping("/apply")
  public ResponseEntity<String> apply(
      @RequestBody String sectionNo,
      HttpServletRequest request) {

    String bearerToken = request.getHeader("Authorization");
    if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    String token = bearerToken.substring(7);
    String studentId = jwtTokenProvider.getStudentId(token);

    Students student = studentsRepository.findById(
        studentId)
        .orElseThrow(() -> new RuntimeException("해당 학생을 찾을 수 없습니다."));
    if (!"재학".equals(student.getStatus())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body("재학생만 수강신청이 가능합니다.");
    }

    String result = enrollService.apply(studentId, sectionNo);
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/cancel")
  public ResponseEntity<String> cancel(
      @RequestBody DeleteRequestDto cancleRequest,
      HttpServletRequest request) {

    String bearerToken = request.getHeader("Authorization");
    if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    String token = bearerToken.substring(7);
    String studentId = jwtTokenProvider.getStudentId(token);

    Students student = studentsRepository.findById(
        studentId)
        .orElseThrow(() -> new RuntimeException("해당 학생을 찾을 수 없습니다."));
    if (!"재학".equals(student.getStatus())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body("재학생만 수강취소가 가능합니다.");
    }

    String courseIdNo = cancleRequest.getCourseIdNo();
    String sectionNo = cancleRequest.getSectionNo();

    String result = enrollService.cancel(studentId, courseIdNo, sectionNo);
    return ResponseEntity.ok(result);
  }
}