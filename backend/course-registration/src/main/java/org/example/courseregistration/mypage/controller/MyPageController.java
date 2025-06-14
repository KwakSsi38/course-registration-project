package org.example.courseregistration.mypage.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/my-page")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;
    private final JwtTokenProvider jwtTokenProvider;
    private final StudentsRepository studentsRepository;
    private final TimeTableService timeTableService;

    // [ 정보 조회 ]
    @GetMapping
    public ResponseEntity<StudentInfoResponse> getMyPage(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        StudentInfoResponse response = myPageService.getStudentInfo(token);

        return ResponseEntity.ok(response);
    }

    // [ 비밀번호 변경 ]
    @PatchMapping("/edit")
    public ResponseEntity<?> editPassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordRequest request) {

        String token = authHeader.replace("Bearer ", "");

        // 비밀번호 검증된 사용자만 변경 가능
        if (!jwtTokenProvider.hasRole(token, "PASSWORD_VERIFIED")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("비밀번호 인증이 필요합니다.");
        }

        String studentId = jwtTokenProvider.getStudentId(token);

        Students student = studentsRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        student.setPassword(request.getNewPassword());
        studentsRepository.save(student);

        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    // [ 학기 시간표 조회 ]
    @GetMapping("/timetable")
    public ResponseEntity<List<TimeTableResponse>> getMyTimeTable(
            @RequestParam("year") int year,
            @RequestParam("semester") int semester,
            HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = bearerToken.substring(7);
        String studentId = jwtTokenProvider.getStudentId(token);

        List<TimeTableResponse> response = timeTableService.getTimeTable(studentId, year, semester);
        return ResponseEntity.ok(response);
    }

}






