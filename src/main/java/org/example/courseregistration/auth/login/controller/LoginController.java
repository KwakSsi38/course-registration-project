package org.example.courseregistration.auth.login.controller;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.auth.login.dto.*;
import org.example.courseregistration.auth.login.repository.StudentsRepository;
import org.example.courseregistration.auth.login.service.LoginService;
import org.example.courseregistration.config.jwt.JwtTokenProvider;
import org.example.courseregistration.entity.Students;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class LoginController {
    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;
    private final StudentsRepository studentsRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = loginService.login(request);
        LoginResponse response = new LoginResponse(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody VerifyPwdRequest request) {
        String token = authHeader.replace("Bearer", "");
        String studentId = jwtTokenProvider.getStudentId(token);

        Students student = studentsRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        if (!request.getPassword().equals(student.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호 불일치");
        }

        // 새 토큰 발급: "비밀번호 검증된 사용자" 권한 부여
        List<String> roles = Arrays.asList("PASSWORD_VERIFIED");
        String verifiedToken = jwtTokenProvider.createToken(student.getId(), roles );
        return ResponseEntity.ok(Map.of("verifiedToken", verifiedToken));
    }
}
