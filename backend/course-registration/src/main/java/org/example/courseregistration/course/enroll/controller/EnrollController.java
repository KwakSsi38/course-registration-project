package org.example.courseregistration.course.enroll.controller;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.course.enroll.dto.DeleteRequestDto;
import org.example.courseregistration.course.enroll.dto.EnrollResponseDto; // EnrollResponseDto 임포트
import org.example.courseregistration.entity.Students;
import org.example.courseregistration.course.enroll.repository.EnrollStudentsRepository;
import org.example.courseregistration.course.enroll.service.EnrollService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.example.courseregistration.config.jwt.JwtTokenProvider;

@RestController
@RequestMapping("/api/subject")
@RequiredArgsConstructor
public class EnrollController {

  private final EnrollService enrollService;
  private final EnrollStudentsRepository studentsRepository;
  private final JwtTokenProvider jwtTokenProvider;

  /**
   * 수강 신청 API
   * 
   * @param sectionId 수강 신청할 섹션 ID
   * @param request   HTTP 요청 (인증 토큰 추출용)
   * @return 수강 신청 결과 DTO
   */
  @PostMapping("/apply")
  public ResponseEntity<EnrollResponseDto> apply( // 반환 타입을 EnrollResponseDto로 변경
      @RequestBody Long sectionId, // 섹션 ID를 직접 @RequestBody로 받음
      HttpServletRequest request) {

    String bearerToken = request.getHeader("Authorization");
    if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new EnrollResponseDto("인증 토큰이 없습니다.", null));
    }
    String token = bearerToken.substring(7);
    String studentId = jwtTokenProvider.getStudentId(token);

    Students student = studentsRepository.findById(studentId)
        .orElseThrow(() -> new RuntimeException("해당 학생을 찾을 수 없습니다."));

    if (!"재학".equals(student.getStatus())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new EnrollResponseDto("재학생만 수강신청이 가능합니다.", null));
    }

    // EnrollService에서 반환된 EnrollResponseDto를 그대로 반환
    EnrollResponseDto responseDto = enrollService.apply(studentId, sectionId);
    return ResponseEntity.ok(responseDto);
  }

  // 수강 취소 API
  @DeleteMapping("/cancel")
  public ResponseEntity<String> cancel(
      @RequestBody DeleteRequestDto cancleRequest,
      HttpServletRequest request) {

    String bearerToken = request.getHeader("Authorization");
    if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 토큰이 없습니다.");
    }
    String token = bearerToken.substring(7);
    String studentId = jwtTokenProvider.getStudentId(token);

    Students student = studentsRepository.findById(studentId)
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