package org.example.courseregistration.mypage.service;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.mypage.dto.TimeTableResponse;
import org.example.courseregistration.mypage.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeTableService {
    private final EnrollmentRepository enrollmentRepository;

    public List<TimeTableResponse> getTimeTable(String studentId, int year, int semester) {
        return enrollmentRepository.findEnrollmentDetails(studentId, year, semester);
    }
}
