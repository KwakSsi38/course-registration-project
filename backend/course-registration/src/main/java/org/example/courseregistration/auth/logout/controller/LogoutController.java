package org.example.courseregistration.auth.logout.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class LogoutController {
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // 서버에서 별도 토큰 무효화 로직 없음
        return ResponseEntity.ok().build();
    }
}
