package org.example.courseregistration.mypage.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.auth.login.repository.StudentsRepository;
import org.example.courseregistration.config.jwt.JwtTokenProvider;
import org.example.courseregistration.entity.Students;
import org.example.courseregistration.mypage.dto.StudentInfoResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final StudentsRepository studentsRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public StudentInfoResponse getStudentInfo(String token) {
        String studentId = jwtTokenProvider.getStudentId(token);

        Students student = studentsRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        return new StudentInfoResponse(
                student.getId(),
                student.getName(),
                student.getGrade().toString(),
                student.getSemester().toString(),
                student.getMajor().toString(),
                student.getDualMajor().toString(),
                student.getStatus().toString(),
                student.getPhone(),
                student.getEmail(),
                student.getAddress(),
                student.getBank()
        );
    }
}
