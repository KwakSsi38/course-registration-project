package org.example.courseregistration.auth.login.service;


import lombok.RequiredArgsConstructor;
import org.example.courseregistration.auth.login.dto.LoginRequest;
import org.example.courseregistration.auth.login.repository.StudentsRepository;
// import org.example.courseregistration.config.jwt.JwtTokenProvider;
import org.example.courseregistration.config.jwt.JwtTokenProvider;
import org.example.courseregistration.entity.Students;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final StudentsRepository studentsRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public String login(LoginRequest loginRequest) {
        String studentId = loginRequest.getStudentId();
        String password = loginRequest.getPassword();

        Students student = studentsRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학번입니다."));

        if (!password.equals(student.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        String token = jwtTokenProvider.createToken(student.getId(), List.of() );

        return token;
    }

}
